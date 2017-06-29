package edu.washington.cse.instrumentation.analysis.transformers;

import soot.Body;
import soot.PatchingChain;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Constant;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

public class JSPSimplificationTransformer implements ConditionalTransformer {
	@Override
	public boolean transformMethod(final SootMethod m) {
		final Body b = m.getActiveBody();
		if(!b.getMethod().getSubSignature().equals("void _jspService(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)")) {
			return false;
		}
		final PatchingChain<Unit> units = b.getUnits();
		Unit u = units.getFirst();
		boolean instrumented = false;
		while(u != null) {
			final Stmt s = (Stmt) u;
			u = units.getSuccOf(u);
			if(s.containsInvokeExpr()) {
				final InvokeExpr ie = s.getInvokeExpr();
				if(ie.getMethodRef().getSignature().startsWith("<javax.servlet.jsp.JspWriter: void write") &&
						ie.getMethodRef().parameterTypes().size() == 1 && ie.getArg(0) instanceof Constant) {
					units.remove(s);
					instrumented = true;
				}
			}
		}
		return instrumented;
	}

}
