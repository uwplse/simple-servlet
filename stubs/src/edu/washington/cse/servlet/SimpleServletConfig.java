package edu.washington.cse.servlet;

import javax.servlet.ServletConfig;

public class SimpleServletConfig extends AbstractConfig implements ServletConfig {

	public SimpleServletConfig(final SimpleContext cont) {
		super(cont);
	}

	@Override
	public String getServletName() {
		return new String();
	}

}
