package edu.washington.cse.instrumentation.analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.yaml.snakeyaml.Yaml;

import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SourceLocator;
import soot.SourceLocator.FoundFile;
import soot.options.Options;
import edu.washington.cse.instrumentation.analysis.transformers.ConditionalTransformer;
import edu.washington.cse.instrumentation.analysis.transformers.DefaultMethodSynthesisTransformer;
import edu.washington.cse.instrumentation.analysis.transformers.DispatchInlineTransformer;
import edu.washington.cse.instrumentation.analysis.transformers.JSPSimplificationTransformer;
import edu.washington.cse.instrumentation.analysis.transformers.JasperTagPoolTransformer;
import edu.washington.cse.instrumentation.analysis.transformers.JspDispatchInlineTransformer;

public class InstrumentServlet {
	private static List<String> expandBlob(final String path) {
		if(path.endsWith("/*")) {
			final File dir = new File(path.substring(0, path.length() - 2));
			final ArrayList<String> toReturn = new ArrayList<>();
			for(final File f : dir.listFiles()) {
				toReturn.add(f.toString());
			}
			return toReturn;
		} else {
			return Collections.singletonList(path);
		}
	}
	
	private static List<String> collectClasses(final String path, final Set<String> classes) throws IOException {
		if(path.endsWith(".jar")) {
			try(final ZipFile zf = new ZipFile(path)) {
				final Enumeration<? extends ZipEntry> it = zf.entries();
				while(it.hasMoreElements()) {
					final ZipEntry ze = it.nextElement();
					if(ze.getName().endsWith(".class")) {
						final String className = ze.getName().replace("/", ".").replace(".class", "");
						classes.add(className);
					}
				}
			}
			return Collections.singletonList(path);
		} else if(path.endsWith("/*")) {
			final List<String> expanded = expandBlob(path);
			for(final String expandedPath : expanded) {
				collectClasses(expandedPath, classes);
			}
			return expanded;
		} else {
			final File f = new File(path);
			if(f.isDirectory()) {
				final Path p = f.toPath();
				Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
						if(file.getFileName().toString().endsWith(".class") && attrs.isRegularFile()) {
							classes.add(p.relativize(file).toString().replace("/", ".").replace(".class", ""));
						}
						return FileVisitResult.CONTINUE;
					}
				});
				return Collections.singletonList(path);
			}
		}
		return Collections.emptyList();
	}
	
	public static void main(final String[] args) throws IOException {
		final Options o = Options.v();
		o.set_allow_phantom_refs(true);
		o.set_whole_program(false);
		o.set_output_format(Options.output_format_class);
		o.set_asm_backend(true);
		
		final String servletClasspath = args[0];
		final String appClasspath = args[1];
		final String routingFile = args[2];
		final String outputDir = args[3];
		final String digestList = args[4];
		
		o.set_output_dir(outputDir);
		final Set<String> toInstrument = new HashSet<>();
		final ArrayList<String> classPath = new ArrayList<>();
		for(final String s : appClasspath.split(File.pathSeparator)) {
			classPath.addAll(collectClasses(s, toInstrument));
		}
		for(final String s : servletClasspath.split(File.pathSeparator)) {
			classPath.addAll(expandBlob(s));
		}
		final StringBuilder sb = new StringBuilder(classPath.get(0));
		for(int i = 1; i < classPath.size(); i++) {
			sb.append(":").append(classPath.get(i));
		}
		o.set_soot_classpath(sb.toString());
		o.classes().addAll(toInstrument);
		o.set_prepend_classpath(true);
		Scene.v().addBasicClass("edu.washington.cse.servlet.jsp.SimplePageContext", SootClass.BODIES);
		Scene.v().loadNecessaryClasses();
		
		final Yaml y = new Yaml();
		final Map<String, Object> routing;
		try(final FileInputStream fis = new FileInputStream(new File(routingFile))) {
			@SuppressWarnings("unchecked")
			final Map<String, Object> tmp = (Map<String, Object>) y.load(fis);
			routing = tmp;
		}
		final JspUrlRouter router = new JspUrlRouter(routing);
		
		final ConditionalTransformer[] transformers = new ConditionalTransformer[]{
			new JasperTagPoolTransformer(),
			new JSPSimplificationTransformer(),
			new DefaultMethodSynthesisTransformer(),
			new JspDispatchInlineTransformer(),
			new DispatchInlineTransformer(router),
		};
		
		final Set<SootClass> toWrite = new HashSet<>();
		for(final String className : toInstrument) {
			instrumentClass(transformers, toWrite, className);
		}
		instrumentClass(transformers, toWrite, "edu.washington.cse.servlet.jsp.SimplePageContext");
		
		Scene.v().getApplicationClasses().clear();
		Scene.v().getApplicationClasses().addAll(toWrite);
		
		final HashSet<String> inputFiles = new HashSet<>();
		for(final SootClass cls : toWrite) {
			final String path = cls.getName().replace(".", "/").concat(".class");
			final FoundFile f = SourceLocator.v().lookupInClassPath(path);
			inputFiles.add(f.getFilePath());
		}
		try(PrintStream ps = new PrintStream(new File(digestList))) {
			for(final String inF : inputFiles) {
				ps.println(inF);
			}
		}
		
		PackManager.v().runBodyPacks();
		PackManager.v().writeOutput();
	}

	public static void instrumentClass(final ConditionalTransformer[] transformers, final Set<SootClass> toWrite, final String className) {
		final SootClass cls = Scene.v().getSootClass(className);
		for(final SootMethod m : new ArrayList<>(cls.getMethods())) {
			if(!m.isConcrete()) {
				continue;
			}
			m.retrieveActiveBody();
			boolean instrumented = false;
			for(final ConditionalTransformer ct : transformers) {
				instrumented = ct.transformMethod(m) || instrumented;
			}
			if(instrumented) {
				toWrite.add(cls);
			}
		}
	}
}
