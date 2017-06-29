package edu.washington.cse.instrumentation.analysis.synthesis;

import java.util.Arrays;
import java.util.List;

import soot.Local;
import soot.Modifier;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.VoidType;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;

public class ServletServiceSynthesizer {

	public static void addServiceAdapter(final SootClass declaringClass, final String callee) {
		final RefType thisType = declaringClass.getType();
		final Jimple jimple = Jimple.v();
		final RefType requestType = RefType.v("javax.servlet.ServletRequest");
		final RefType responseType = RefType.v("javax.servlet.ServletResponse");
		final SootMethod serviceMethod = new SootMethod("service", Arrays.<Type>asList(
				requestType,
				responseType
			), VoidType.v(), Modifier.PUBLIC,
			ServletServiceSynthesizer.getServiceExceptions());
		final JimpleBody jb = jimple.newBody(serviceMethod);
		final PatchingChain<Unit> units = jb.getUnits();
		final Local thisLocal = jimple.newLocal("this", thisType);
		final Local reqLocal = jimple.newLocal("r1", requestType);
		final Local respLocal = jimple.newLocal("r2", responseType);			
		final RefType httpRequestType = RefType.v("javax.servlet.http.HttpServletRequest");
		final RefType httpResponseType = RefType.v("javax.servlet.http.HttpServletResponse");
		
		final Local t1 = jimple.newLocal("t1", httpRequestType);
		final Local t2 = jimple.newLocal("t2", httpResponseType);
		
		jb.getLocals().addAll(Arrays.asList(thisLocal, reqLocal, respLocal, t1, t2));
		
		units.add(jimple.newNopStmt());
		units.add(jimple.newIdentityStmt(thisLocal, jimple.newThisRef(thisType)));
		units.add(jimple.newIdentityStmt(reqLocal, jimple.newParameterRef(requestType, 0)));
		units.add(jimple.newIdentityStmt(respLocal, jimple.newParameterRef(responseType, 1)));
		
		units.add(jimple.newAssignStmt(t1, jimple.newCastExpr(reqLocal, httpRequestType)));
		units.add(jimple.newAssignStmt(t2, jimple.newCastExpr(respLocal, httpResponseType)));
		
		final SootMethodRef serviceMethodRef = Scene.v().makeMethodRef(declaringClass, callee, Arrays.<Type>asList(
			httpRequestType, httpResponseType
		), VoidType.v(), false);
		units.add(jimple.newInvokeStmt(
			jimple.newVirtualInvokeExpr(thisLocal, serviceMethodRef, t1, t2)
		));
		units.add(jimple.newReturnVoidStmt());
		
		serviceMethod.setActiveBody(jb);
		declaringClass.addMethod(serviceMethod);
	}

	static List<SootClass> getServiceExceptions() {
		return Arrays.asList(Scene.v().getSootClass("javax.servlet.ServletException"), Scene.v().getSootClass("java.io.IOException"));
	}

}
