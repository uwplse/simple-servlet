package edu.washington.cse.instrumentation.analysis.transformers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import soot.Local;
import soot.SootMethod;
import edu.washington.cse.instrumentation.analysis.JspUrlRouter;

public class DispatchInlineTransformer implements ConditionalTransformer {
	private final DispatchResolutionTransformer resolver;
	private final AggressiveDispatchInlineTransformer inliner;

	public DispatchInlineTransformer(final JspUrlRouter router, final boolean quiet) {
		final Map<Local, Set<String>> deferredResolution = new HashMap<>();

		this.resolver = new DispatchResolutionTransformer(router, deferredResolution, quiet);
		this.inliner = new AggressiveDispatchInlineTransformer(router, deferredResolution, quiet);
	}
	
	public DispatchInlineTransformer(final JspUrlRouter router) {
		this(router, false);
	}

	@Override
	public boolean transformMethod(final SootMethod m) {
		boolean instrumented = this.resolver.transformMethod(m);
		instrumented = this.inliner.transformMethod(m) || instrumented;
		return instrumented;
	}
}
