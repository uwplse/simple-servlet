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
	private final boolean aggressiveInline;

	public DispatchInlineTransformer(final JspUrlRouter router, final boolean quiet, final boolean aggressiveInline) {
		final Map<Local, Set<String>> deferredResolution = new HashMap<>();

		this.resolver = new DispatchResolutionTransformer(router, deferredResolution, quiet);
		this.inliner = new AggressiveDispatchInlineTransformer(router, deferredResolution, quiet);
		this.aggressiveInline = aggressiveInline;
	}
	
	public DispatchInlineTransformer(final JspUrlRouter router, final boolean aggressiveInline) {
		this(router, false, aggressiveInline);
	}

	@Override
	public boolean transformMethod(final SootMethod m) {
		boolean instrumented = this.resolver.transformMethod(m);
		if(aggressiveInline) {
			instrumented = this.inliner.transformMethod(m) || instrumented;
		}
		return instrumented;
	}
}
