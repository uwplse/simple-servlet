package edu.washington.cse.servlet;

import static edu.washington.cse.servlet.Util.nondetBool;
import static edu.washington.cse.servlet.Util.nondetInt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import edu.washington.cse.servlet.exceptions.UnsupportedModelException;

public class SimpleHttpRequest implements HttpServletRequest {

	private final StringBuffer stringBuffer;
	private final Map<String, Object> attributes = new HashMap<>();
	private final Map<String, String[]> query = new HashMap<>();
	private final SimpleContext context;
	
	public SimpleHttpRequest(final SimpleContext context) {
		stringBuffer = new StringBuffer();
		this.context = context;
		this.query.put("foo", new String[]{"bar"});
	}

	@Override
	public AsyncContext getAsyncContext() {
		throw new UnsupportedModelException();
	}

	@Override
	public Object getAttribute(final String arg0) {
		return attributes.get(arg0);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return new SimpleEnumeration<>(attributes.keySet());
	}

	@Override
	public String getCharacterEncoding() {
		return new String();
	}

	@Override
	public int getContentLength() {
		return nondetInt();
	}

	@Override
	public String getContentType() {
		return new String();
	}

	@Override
	public DispatcherType getDispatcherType() {
		throw new UnsupportedModelException();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		if(nondetBool()) {
			return new ServletInputStream() {
				@Override
				public int read() throws IOException {
					return 0;
				}
			};
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public String getLocalAddr() {
		return new String();
	}

	@Override
	public String getLocalName() {
		return new String();
	}

	@Override
	public int getLocalPort() {
		return nondetInt();
	}

	@Override
	public Locale getLocale() {
		return new Locale(getHeader("foo"));
	}

	@Override
	public Enumeration<Locale> getLocales() {
		return new SimpleEnumeration<>(Collections.singleton(getLocale()));
	}

	@Override
	public String getParameter(final String arg0) {
		return query.get(arg0)[0];
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return new HashMap<>(query);
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return new SimpleEnumeration<>(query.keySet());
	}

	@Override
	public String[] getParameterValues(final String p) {
		final String[] toCopy = query.get(p);
		return Arrays.copyOf(toCopy, toCopy.length);
	}

	@Override
	public String getProtocol() {
		return new String();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		if(nondetBool()) {
			return new BufferedReader(new Reader() {
				@Override
				public int read(final char[] arg0, final int arg1, final int arg2) throws IOException {
					return 0;
				}
				
				@Override
				public void close() throws IOException {
				}
			});
		} else {
			throw new IllegalStateException();
		}
		
	}

	@Override
	public String getRealPath(final String arg0) {
		return new String();
	}

	@Override
	public String getRemoteAddr() {
		return new String();
	}

	@Override
	public String getRemoteHost() {
		return new String();
	}

	@Override
	public int getRemotePort() {
		return nondetInt();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(final String url) {
		return context.getRequestDispatcher(url);
	}

	@Override
	public String getScheme() {
		return new String();
	}

	@Override
	public String getServerName() {
		return new String();
	}

	@Override
	public int getServerPort() {
		return nondetInt();
	}

	@Override
	public ServletContext getServletContext() {
		return context;
	}

	@Override
	public boolean isAsyncStarted() {
		throw new UnsupportedModelException();
	}

	@Override
	public boolean isAsyncSupported() {
		throw new UnsupportedModelException();
	}

	@Override
	public boolean isSecure() {
		return nondetBool();
	}

	@Override
	public void removeAttribute(final String k) {
		context.reqAttrList[nondetInt()].attributeReplaced(new ServletRequestAttributeEvent(context, this, k, attributes.remove(k)));
	}

	@Override
	public void setAttribute(final String k, final Object v) {
		final Object r = attributes.put(k, v);
		context.reqAttrList[nondetInt()].attributeAdded(new ServletRequestAttributeEvent(context, this, k, v));
		if(r != null) {
			context.reqAttrList[nondetInt()].attributeReplaced(new ServletRequestAttributeEvent(context, this, k, r));	
		}
	}

	@Override
	public void setCharacterEncoding(final String arg0) throws UnsupportedEncodingException {
		if(nondetBool()) {
			throw new UnsupportedEncodingException();
		}
	}

	@Override
	public AsyncContext startAsync() {
		throw new UnsupportedModelException();
	}

	@Override
	public AsyncContext startAsync(final ServletRequest arg0, final ServletResponse arg1) {
		throw new UnsupportedModelException();
	}

	@Override
	public boolean authenticate(final HttpServletResponse arg0) throws IOException,
			ServletException {
		throw new UnsupportedModelException();
	}

	@Override
	public String getAuthType() {
		return new String();
	}

	@Override
	public String getContextPath() {
		return new String();
	}

	@Override
	public Cookie[] getCookies() {
		return new Cookie[]{
			new Cookie("foo", "bar")
		};
	}

	@Override
	public long getDateHeader(final String arg0) {
		return System.currentTimeMillis();
	}

	@Override
	public String getHeader(final String arg0) {
		return new String();
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		return Collections.emptyEnumeration();
	}

	@Override
	public Enumeration<String> getHeaders(final String arg0) {
		return Collections.emptyEnumeration();
	}

	@Override
	public int getIntHeader(final String arg0) {
		return nondetInt();
	}

	@Override
	public String getMethod() {
		return new String();
	}

	@Override
	public Part getPart(final String arg0) throws IOException, IllegalStateException,
			ServletException {
		throw new UnsupportedModelException();
	}

	@Override
	public Collection<Part> getParts() throws IOException, IllegalStateException,
			ServletException {
		throw new UnsupportedModelException();
	}

	@Override
	public String getPathInfo() {
		return new String();
	}

	@Override
	public String getPathTranslated() {
		return new String();
	}

	@Override
	public String getQueryString() {
		return new String();
	}

	@Override
	public String getRemoteUser() {
		return new String();
	}

	@Override
	public String getRequestURI() {
		return new String();
	}

	@Override
	public StringBuffer getRequestURL() {
		return stringBuffer;
	}

	@Override
	public String getRequestedSessionId() {
		return new String();
	}

	@Override
	public String getServletPath() {
		return new String();
	}

	@Override
	public HttpSession getSession() {
		return context.getSession(nondetInt());
	}

	@Override
	public HttpSession getSession(final boolean create) {
		return context.getSession(nondetInt(), create);
	}

	@Override
	public Principal getUserPrincipal() {
		if(nondetBool()) {
			return new Principal() {
				
				@Override
				public String getName() {
					return new String();
				}
			};
		} else {
			return null;
		}
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return nondetBool();
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return nondetBool();
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		return nondetBool();
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return nondetBool();
	}

	@Override
	public boolean isUserInRole(final String arg0) {
		throw new UnsupportedModelException();
	}

	@Override
	public void login(final String arg0, final String arg1) throws ServletException {
		throw new UnsupportedModelException();
	}

	@Override
	public void logout() throws ServletException {
		throw new UnsupportedModelException();
	}

}
