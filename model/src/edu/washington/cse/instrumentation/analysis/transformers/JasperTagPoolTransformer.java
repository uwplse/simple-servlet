package edu.washington.cse.instrumentation.analysis.transformers;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import soot.Body;
import soot.Local;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.VoidType;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.NullConstant;
import soot.jimple.Stmt;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class JasperTagPoolTransformer implements ConditionalTransformer {
	private static final String GET_TAG_POOL_SIG = 
			"<org.apache.jasper.runtime.TagHandlerPool: org.apache.jasper.runtime.TagHandlerPool getTagHandlerPool(javax.servlet.ServletConfig)>";
	private static final String RELEASE_TAG_SIG = "<org.apache.jasper.runtime.TagHandlerPool: void release()>";
	private static final String REUSE_TAG_SIG = "<org.apache.jasper.runtime.TagHandlerPool: void reuse(javax.servlet.jsp.tagext.Tag)>";
	private static final String GET_TAG_SIG = "<org.apache.jasper.runtime.TagHandlerPool: javax.servlet.jsp.tagext.Tag get(java.lang.Class)>";

	@Override
	public boolean transformMethod(final SootMethod toTransform) {
		final Body b = toTransform.retrieveActiveBody();
		final PatchingChain<Unit> units = b.getUnits();
		final UnitGraph ug = new BriefUnitGraph(b);
		boolean instrumented = false;
		for(final Iterator<Unit> it = units.snapshotIterator(); it.hasNext(); ) {
			final Unit currUnit = it.next();
			final Stmt stmt = (Stmt) currUnit;
			if(!(currUnit instanceof AssignStmt)) {
				if(!stmt.containsInvokeExpr()) {
					continue;
				}
				final InvokeExpr ie = stmt.getInvokeExpr();
				final String calledSig = ie.getMethod().getSignature();
				if(calledSig.equals(GET_TAG_SIG)) {
					throw new RuntimeException("Could not adapt: " + b.getMethod());
				}
				if(calledSig.equals(REUSE_TAG_SIG) || 
						calledSig.equals(RELEASE_TAG_SIG)) {
					units.insertAfter(Jimple.v().newNopStmt(), currUnit);
					instrumented = true;
//					b.validate();
				}
				continue;
			}
			final AssignStmt invokeStmt = (AssignStmt) currUnit;
			if(!invokeStmt.containsInvokeExpr()) {
				continue;
			}
			final Value lhs = invokeStmt.getLeftOp(); 
			final SootMethod m = invokeStmt.getInvokeExpr().getMethod();
			if(!m.getSignature().equals(GET_TAG_SIG) && !m.getSignature().equals(GET_TAG_POOL_SIG)) {
				continue;
			}
			if(m.getSignature().equals(GET_TAG_POOL_SIG)) {
				invokeStmt.setRightOp(NullConstant.v());
				continue;
			}
			final List<Unit> succs = ug.getSuccsOf(currUnit);
			if(succs.size() != 1) {
				throw new RuntimeException("Could not adapt: " + currUnit + " in " + b.getMethod());
			}
			final Unit nextUnit = succs.get(0);
			if(!(nextUnit instanceof AssignStmt) || !(((AssignStmt)nextUnit).getRightOp() instanceof CastExpr)) {
				throw new RuntimeException("Could not adapt: " + currUnit + " in " + b.getMethod());
			}
			final CastExpr castExpr = (CastExpr) ((AssignStmt)nextUnit).getRightOp();
			if(castExpr.getOp() != lhs) {
				throw new RuntimeException("Could not adapt: " + currUnit + " in " + b.getMethod());
			}
			final Type castedType = castExpr.getCastType();
			if(!(castedType instanceof RefType)) {
				throw new RuntimeException("Could not adapt: " + currUnit + " in " + b.getMethod());
			}
			invokeStmt.getRightOpBox().setValue(Jimple.v().newNewExpr((RefType)castedType));
			final SootClass created = ((RefType)castedType).getSootClass();
			final Unit invokeConstructor = Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr((Local)lhs, 
					Scene.v().makeMethodRef(created, "<init>", Collections.<Type>emptyList(), VoidType.v(), false)));
			units.insertAfter(invokeConstructor, currUnit);
			instrumented = true;
//			b.validate();
		}
		return instrumented;
	}
}
