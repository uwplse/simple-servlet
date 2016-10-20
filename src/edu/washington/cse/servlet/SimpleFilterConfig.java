package edu.washington.cse.servlet;

import javax.servlet.FilterConfig;

public class SimpleFilterConfig extends AbstractConfig implements FilterConfig {
	public SimpleFilterConfig(final SimpleContext cont) {
		super(cont);
	}

	@Override
	public String getFilterName() {
		return new String();
	}
}
