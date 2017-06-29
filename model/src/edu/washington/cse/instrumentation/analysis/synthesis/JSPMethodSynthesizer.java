package edu.washington.cse.instrumentation.analysis.synthesis;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import soot.Local;
import soot.Modifier;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.VoidType;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;

public class JSPMethodSynthesizer {

	public static void synthesizeBodies(final SootClass declaringClass) {
		assert declaringClass.getSuperclass().getName().equals("org.apache.jasper.runtime.HttpJspBase") : declaringClass;
		// inline jsp implementations
		final RefType servletConfigType = RefType.v("javax.servlet.ServletConfig");
		final SootClass servletExceptionClass = Scene.v().getSootClass("javax.servlet.ServletException");
		
		final SootField configField = new SootField("_config", servletConfigType, Modifier.PRIVATE);
		declaringClass.addField(configField);
		final SootFieldRef configRef = configField.makeRef();
		
		final RefType thisType = RefType.v(declaringClass.getName());
		final Jimple jimple = Jimple.v();
		{
			final SootMethod initMethod = new SootMethod("init", Collections.<Type>singletonList(servletConfigType),
					VoidType.v(), Modifier.PUBLIC, Collections.<SootClass>singletonList(servletExceptionClass));
			final JimpleBody jb = jimple.newBody(initMethod);
			final PatchingChain<Unit> units = jb.getUnits();
			final Local thisLocal = jimple.newLocal("this", thisType);
			final Local configLocal = jimple.newLocal("configArg", servletConfigType);
			jb.getLocals().add(thisLocal);
			jb.getLocals().add(configLocal);
			
			units.add(jimple.newNopStmt());
			units.add(jimple.newIdentityStmt(thisLocal, jimple.newThisRef(thisType)));
			units.add(jimple.newIdentityStmt(configLocal, jimple.newParameterRef(servletConfigType, 0)));
			units.add(jimple.newAssignStmt(jimple.newInstanceFieldRef(thisLocal, configRef), configLocal));
			units.add(jimple.newReturnVoidStmt());
			
			declaringClass.addMethod(initMethod);
			initMethod.setActiveBody(jb);
		}
		
		{
			final SootMethod getConfigMethod = new SootMethod("getServletConfig", Collections.<Type>emptyList(),
					servletConfigType, Modifier.PUBLIC);
			final JimpleBody jb = jimple.newBody(getConfigMethod);
			final PatchingChain<Unit> units = jb.getUnits();
			final Local thisLocal = jimple.newLocal("this", thisType);
			final Local configLocal = jimple.newLocal("configArg", servletConfigType);
			jb.getLocals().add(thisLocal);
			jb.getLocals().add(configLocal);
			
			units.add(jimple.newNopStmt());
			units.add(jimple.newIdentityStmt(thisLocal, jimple.newThisRef(thisType)));
			units.add(jimple.newAssignStmt(configLocal, jimple.newInstanceFieldRef(thisLocal, configRef)));
			units.add(jimple.newReturnStmt(configLocal));
			declaringClass.addMethod(getConfigMethod);
			getConfigMethod.setActiveBody(jb);
		}
		
		{
			final SootMethod destroyMethod = new SootMethod("destroy", Collections.<Type>emptyList(), VoidType.v(), Modifier.PUBLIC);
			final JimpleBody jb = jimple.newBody(destroyMethod);
			final PatchingChain<Unit> units = jb.getUnits();
			final Local thisLocal = jimple.newLocal("this", thisType);
			jb.getLocals().add(thisLocal);
			
			units.add(jimple.newNopStmt());
			units.add(jimple.newIdentityStmt(thisLocal, jimple.newThisRef(thisType)));
			
			units.add(jimple.newInvokeStmt(
				jimple.newVirtualInvokeExpr(thisLocal, Scene.v().makeMethodRef(declaringClass, "jspDestroy", Collections.<Type>emptyList(), VoidType.v(), false))
			));
			units.add(jimple.newInvokeStmt(
				jimple.newVirtualInvokeExpr(thisLocal, Scene.v().makeMethodRef(declaringClass, "_jspDestroy", Collections.<Type>emptyList(), VoidType.v(), false))
			));
			units.add(jimple.newReturnVoidStmt());
			declaringClass.addMethod(destroyMethod);
			destroyMethod.setActiveBody(jb);
		}
		
		ServletServiceSynthesizer.addServiceAdapter(declaringClass, "_jspService");
		
		{
			final RefType responseType = RefType.v("javax.servlet.http.HttpServletResponse");
			final RefType requestType = RefType.v("javax.servlet.http.HttpServletRequest");
			final List<Type> serviceParams = Arrays.<Type>asList(requestType,responseType);
			final SootMethod httpServiceMethod = new SootMethod("service",
				serviceParams, VoidType.v(),
				Modifier.PUBLIC,
				ServletServiceSynthesizer.getServiceExceptions());
			final JimpleBody b = jimple.newBody(httpServiceMethod);
			final Local thisLocal = jimple.newLocal("r0", thisType);
			final Local reqLocal = jimple.newLocal("req", requestType);
			final Local respLocal = jimple.newLocal("resp", requestType);
			b.getLocals().addAll(Arrays.asList(thisLocal, reqLocal, respLocal));
			final PatchingChain<Unit> units = b.getUnits();
			units.add(jimple.newNopStmt());
			units.add(jimple.newIdentityStmt(thisLocal, jimple.newThisRef(thisType)));
			units.add(jimple.newIdentityStmt(reqLocal, jimple.newParameterRef(requestType, 0)));
			units.add(jimple.newIdentityStmt(respLocal, jimple.newParameterRef(responseType, 1)));
			
			units.add(
				jimple.newInvokeStmt(
					jimple.newVirtualInvokeExpr(thisLocal, Scene.v().makeMethodRef(declaringClass, "_jspService", serviceParams, VoidType.v(), false), reqLocal, respLocal)
				)
			);
			units.add(jimple.newReturnVoidStmt());
			
			httpServiceMethod.setActiveBody(b);
			declaringClass.addMethod(httpServiceMethod);
		}
	}

}
