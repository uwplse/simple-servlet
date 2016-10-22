package edu.washington.cse.servlet.jsp;

import static edu.washington.cse.servlet.Util.nondetBool;
import static edu.washington.cse.servlet.Util.nondetInt;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;

import edu.washington.cse.servlet.SimpleEnumeration;
import edu.washington.cse.servlet.SimpleHttpRequest;
import edu.washington.cse.servlet.SimpleHttpResponse;
import edu.washington.cse.servlet.exceptions.UnsupportedModelException;

public class SimplePageContext extends PageContext {

	private final HashMap<String, Object> attributes = new HashMap<>();
	
	private final SimpleHttpResponse response;
	private final SimpleHttpRequest request;
	private final Servlet servlet;
	private final ServletConfig config;
	private final ServletContext context;

	private final Exception exception;

	private final SimpleJspWriter simpleJspWriter;

	public SimplePageContext(final Servlet servlet, final ServletRequest request, final ServletResponse response) {
		this.servlet = servlet;
		this.request = (SimpleHttpRequest) request;
		this.response = (SimpleHttpResponse) response;
		this.config = servlet.getServletConfig();
		this.context = config.getServletContext();
		
		this.exception = (Exception) request.getAttribute("exception.key");
		simpleJspWriter = new SimpleJspWriter(nondetInt(), nondetBool());
	}

	@Override
	public void forward(final String url) throws ServletException, IOException {
		this.request.getRequestDispatcher(url).forward(request, response);
	}

	@Override
	public Exception getException() {
		return exception;
	}

	@Override
	public Object getPage() {
		return this.servlet;
	}

	@Override
	public ServletRequest getRequest() {
		return this.request;
	}

	@Override
	public ServletResponse getResponse() {
		return this.response;
	}

	@Override
	public ServletConfig getServletConfig() {
		return config; 
	}

	@Override
	public ServletContext getServletContext() {
		return this.context;
	}

	@Override
	public HttpSession getSession() {
		return ((HttpServletRequest)this.request).getSession();
	}

	@Override
	public void handlePageException(final Exception arg0) throws ServletException, IOException { 
		this.handlePageException((Throwable)arg0);
	}

	@Override
	public void handlePageException(final Throwable k) throws ServletException, IOException { 
		request.setAttribute("exc", k);
		request.getRequestDispatcher("error").forward(request, response);
	}

	@Override
	public void include(final String arg0) throws ServletException, IOException {
		this.request.getRequestDispatcher(arg0).include(request, response);
	}

	@Override
	public void include(final String arg0, final boolean arg1) throws ServletException, IOException {
		this.request.getRequestDispatcher(arg0).include(request, response);
	}

	@Override
	public void initialize(final Servlet arg0, final ServletRequest arg1, final ServletResponse arg2, final String arg3, final boolean arg4, final int arg5, final boolean arg6) throws IOException, IllegalStateException,
			IllegalArgumentException {
	}

	@Override
	public void release() {
	}

	@Override
	public Object findAttribute(final String k) {
		return this.getAttribute(k);
	}

	@Override
	public Object getAttribute(final String k) {
		return this.attributes.get(k);
	}

	@Override
	public Object getAttribute(final String k, final int arg1) {
		if(nondetBool()) {
			return this.attributes.get(k);
		} else if(nondetBool()) {
			return this.request.getAttribute(k);
		} else if(nondetBool()) {
			return this.getSession().getAttribute(k);
		} else {
			return this.context.getAttribute(k);
		}
	}

	@Override
	public Enumeration<String> getAttributeNamesInScope(final int scope) {
		if(nondetBool()) {
			return new SimpleEnumeration<String>(this.attributes.keySet());
		} else if(nondetBool()) {
			return this.request.getAttributeNames();
		} else if(nondetBool()) {
			return this.getSession().getAttributeNames();
		} else {
			return this.context.getAttributeNames();
		}
	}

	@Override
	public int getAttributesScope(final String arg0) {
		return nondetInt();
	}

	@Override
	public ExpressionEvaluator getExpressionEvaluator() {
		throw new UnsupportedModelException();
	}

	@Override
	public JspWriter getOut() {
		return simpleJspWriter;
	}

	@Override
	@Deprecated
	public VariableResolver getVariableResolver() {
		return null;
	}

	@Override
	public void removeAttribute(final String k) {
		this.attributes.remove(k);
	}

	@Override
	public void removeAttribute(final String k, final int arg1) {
		if(nondetBool()) {
			this.attributes.remove(k);
		} else if(nondetBool()) {
			this.request.removeAttribute(k);
		} else if(nondetBool()) {
			this.getSession().removeAttribute(k);
		} else {
			this.context.removeAttribute(k);
		}
	}

	@Override
	public void setAttribute(final String k, final Object v) {
		this.attributes.put(k, v);
	}

	@Override
	public void setAttribute(final String k, final Object v, final int arg2) {
		if(nondetBool()) {
			this.attributes.put(k, v);
		} else if(nondetBool()) {
			this.request.setAttribute(k, v);
		} else if(nondetBool()) {
			this.getSession().setAttribute(k, v);
		} else {
			this.context.setAttribute(k, v);
		}
	}

}
