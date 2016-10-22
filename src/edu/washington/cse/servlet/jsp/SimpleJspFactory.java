package edu.washington.cse.servlet.jsp;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.JspEngineInfo;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;


public class SimpleJspFactory extends JspFactory {
	@Override
	public JspEngineInfo getEngineInfo() {
		return new SimpleJspEngineInfo();
	}

	@Override
	public PageContext getPageContext(final Servlet servlet, final ServletRequest request, final ServletResponse response,
			final String errorPage, final boolean needSession, final int bufferSize, final boolean autoflush) {
		final PageContext context = new SimplePageContext(servlet, request, response);
		return context;
	}

	@Override
	public void releasePageContext(final PageContext context) { }
}
