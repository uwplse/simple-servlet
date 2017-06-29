package edu.washington.cse.instrumentation.analysis.transformers;

import soot.SootMethod;

public interface ConditionalTransformer {
	public boolean transformMethod(SootMethod m);
}
