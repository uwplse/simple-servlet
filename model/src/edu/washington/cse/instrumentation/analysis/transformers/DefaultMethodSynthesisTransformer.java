package edu.washington.cse.instrumentation.analysis.transformers;

import java.util.HashSet;
import java.util.Set;

import edu.washington.cse.instrumentation.analysis.synthesis.JSPMethodSynthesizer;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

public class DefaultMethodSynthesisTransformer implements ConditionalTransformer {
	
	private final Set<SootClass> visited = new HashSet<>();
	
	@Override
	public boolean transformMethod(final SootMethod m) {
		final SootClass cls = m.getDeclaringClass();
		if(!visited.add(cls)) {
			return false;
		}
		if(isSubclass(cls, "org.apache.jasper.runtime.HttpJspBase")) {
			if(cls.getName().equals("org.apache.jasper.runtime.HttpJspBase")) {
				return false;
			}
			System.out.println("Transforming jasper bodies: " + cls);
			JSPMethodSynthesizer.synthesizeBodies(cls);
			return true;
		} else if(isSubclass(cls, "javax.servlet.http.HttpServlet")) {
			if(cls.getName().equals("javax.servlet.http.HttpServlet")) {
				return false;
			}
			System.out.println("instrumenting: " + cls);
			HttpServletTransformer.synthesizeBody(cls);
			return true;
		}
		return false;
	}
	
	private boolean isSubclass(final SootClass sub, final String superClass) {
		assert superClass.equals("org.apache.jasper.runtime.HttpJspBase") || 
			superClass.equals("javax.servlet.http.HttpServlet") : superClass;
		assert Scene.v().containsClass(superClass) : superClass;
		sub.checkLevel(SootClass.HIERARCHY);
		
		SootClass it = sub;
		while(it.hasSuperclass()) {
			it = it.getSuperclass();
			if(it.getName().equals(superClass)) {
				return true;
			}
		}
		assert it.getName().equals("java.lang.Object") || it.isPhantom() : it;
		return false;
	}
}
