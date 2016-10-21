package edu.washington.cse.servlet;

import static edu.washington.cse.servlet.Util.nondetInt;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.http.HttpSession;

import edu.washington.cse.servlet.exceptions.UnsupportedModelException;

public abstract class SimpleContext implements ServletContext {
	private final Map<Integer, SimpleSession> sessions = new HashMap<>();
	public final Servlet[] servlets;
	
	public SimpleContext() {
		initProperites.setProperty("foo", "bar");
		contextAttributes.put("Foo", "bar");
		this.servlets = new Servlet[1];
	}

	@Override
	public Dynamic addFilter(final String arg0, final String arg1) {
		throw new UnsupportedModelException();
	}

	@Override
	public Dynamic addFilter(final String arg0, final Filter arg1) {
		throw new UnsupportedModelException();
	}

	@Override
	public Dynamic addFilter(final String arg0, final Class<? extends Filter> arg1) {
		throw new UnsupportedModelException();
	}

	@Override
	public void addListener(final String arg0) {
		throw new UnsupportedModelException();
	}

	@Override
	public <T extends EventListener> void addListener(final T arg0) {
		throw new UnsupportedModelException();
	}

	@Override
	public void addListener(final Class<? extends EventListener> arg0) {
		throw new UnsupportedModelException();
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(final String arg0,
			final String arg1) {
		throw new UnsupportedModelException();
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(final String arg0,
			final Servlet arg1) {
		throw new UnsupportedModelException();
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(final String arg0,
			final Class<? extends Servlet> arg1) {
		throw new UnsupportedModelException();
	}

	@Override
	public <T extends Filter> T createFilter(final Class<T> arg0)
			throws ServletException {
		throw new UnsupportedModelException();
	}

	@Override
	public <T extends EventListener> T createListener(final Class<T> arg0)
			throws ServletException {
		throw new UnsupportedModelException();
	}

	@Override
	public <T extends Servlet> T createServlet(final Class<T> arg0)
			throws ServletException {
		throw new UnsupportedModelException();
	}

	@Override
	public void declareRoles(final String... arg0) {
		throw new UnsupportedModelException();
	}
	
	private final Map<String, Object> contextAttributes = new HashMap<>();

	@Override
	public Object getAttribute(final String arg0) {
		return contextAttributes.get(arg0);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return new SimpleEnumeration<String>(contextAttributes.keySet());
	}

	@Override
	public ClassLoader getClassLoader() {
		throw new UnsupportedModelException();
	}
	
	@Override
	public ServletContext getContext(final String arg0) {
		return null;
	}
	
	@Override
	public String getContextPath() {
		return new String("");
	}

	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		throw new UnsupportedModelException();
	}

	@Override
	public int getEffectiveMajorVersion() {
		return 2;
	}

	@Override
	public int getEffectiveMinorVersion() {
		return 5;
	}

	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		throw new UnsupportedModelException();
	}

	@Override
	public FilterRegistration getFilterRegistration(final String arg0) {
		throw new UnsupportedModelException();
	}

	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		throw new UnsupportedModelException();
	}
	
	private final Properties initProperites = new Properties();

	@Override
	public String getInitParameter(final String arg0) {
		return initProperites.getProperty(arg0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration<String> getInitParameterNames() {
		return (Enumeration<String>) initProperites.propertyNames();
	}

	@Override
	public JspConfigDescriptor getJspConfigDescriptor() {
		throw new UnsupportedModelException();
	}

	@Override
	public int getMajorVersion() {
		return 2;
	}

	@Override
	public String getMimeType(final String arg0) {
		return null;
	}

	@Override
	public int getMinorVersion() {
		return 5;
	}

	@Override
	public RequestDispatcher getNamedDispatcher(final String arg0) {
		return new RequestDispatcher() {
			
			@Override
			public void include(final ServletRequest req, final ServletResponse resp)
					throws ServletException, IOException {
				final Servlet serv = servlets[(int) System.currentTimeMillis()];
				serv.service(req, resp);
			}
			
			@Override
			public void forward(final ServletRequest req, final ServletResponse resp)
					throws ServletException, IOException {
				final Servlet serv = servlets[(int) System.currentTimeMillis()];
				serv.service(req, resp);
			}
		};
	}

	@Override
	public String getRealPath(final String arg0) {
		return new String();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(final String arg0) {
		return new RequestDispatcher() {
			
			@Override
			public void include(final ServletRequest req, final ServletResponse resp)
					throws ServletException, IOException {
				final Servlet serv = servlets[(int) System.currentTimeMillis()];
				serv.service(req, resp);
			}
			
			@Override
			public void forward(final ServletRequest req, final ServletResponse resp)
					throws ServletException, IOException {
				final Servlet serv = servlets[(int) System.currentTimeMillis()];
				serv.service(req, resp);
			}
		};
	}

	@Override
	public URL getResource(final String arg0) throws MalformedURLException {
		return new URL(arg0);
	}

	@Override
	public InputStream getResourceAsStream(final String arg0) {
		return new InputStream() {
			@Override
			public int read() throws IOException {
				return 0;
			}
		};
	}

	@Override
	public Set<String> getResourcePaths(final String arg0) {
		return Collections.emptySet();
	}

	@Override
	public String getServerInfo() {
		return new String();
	}

	@Override
	public Servlet getServlet(final String arg0) throws ServletException {
		return null;
	}

	@Override
	public String getServletContextName() {
		return new String();
	}

	@Override
	public Enumeration<String> getServletNames() {
		return Collections.emptyEnumeration();
	}

	@Override
	public ServletRegistration getServletRegistration(final String arg0) {
		throw new UnsupportedModelException();
	}

	@Override
	public Map<String, ? extends ServletRegistration> getServletRegistrations() {
		throw new UnsupportedModelException();
	}

	@Override
	public Enumeration<Servlet> getServlets() {
		return Collections.emptyEnumeration();
	}

	@Override
	public SessionCookieConfig getSessionCookieConfig() {
		throw new UnsupportedModelException();
	}

	@Override
	public void log(final String arg0) {
	}

	@Override
	public void log(final Exception arg0, final String arg1) {
	}

	@Override
	public void log(final String arg0, final Throwable arg1) {
	}

	@Override
	public void removeAttribute(final String key) {
		notifyContextAttributeRemoved(key, contextAttributes.remove(key));
	}

	@Override
	public void setAttribute(final String key, final Object value) {
		final Object rem = contextAttributes.put(key, value);
		if(rem != null) {
			notifyContextAttributeReplaced(key, rem);
		}
		notifyContextAttributeAdded(key, value);
	}

	@Override
	public boolean setInitParameter(final String prop, final String val) {
		initProperites.setProperty(prop, val);
		return System.currentTimeMillis() == 0;
	}

	@Override
	public void setSessionTrackingModes(final Set<SessionTrackingMode> arg0)
			throws IllegalStateException, IllegalArgumentException {
		throw new UnsupportedModelException();
	}

	public HttpSession getSession(final int id) {
		return sessions.get(id);
	}

	public HttpSession getSession(final int id, final boolean create) {
		if(!sessions.containsKey(id)) {
			final SimpleSession sess = new SimpleSession(this);
			notifySessionCreated(sess);
			sessions.put(id, sess);
		}
		return sessions.get(id);
	}
	
	public void destroySession() {
		final SimpleSession sess = sessions.get(nondetInt());
		notifySessionDestroyed(sess);
	}
	
	public void actSession() {
		final SimpleSession sess = sessions.get(nondetInt());
		notifySessionDidActivate(sess);
	}
	
	public void passSession() {
		final SimpleSession sess = sessions.get(nondetInt());
		notifySessionWillPassivate(sess);
	}
	
	public abstract void notifySessionDestroyed(SimpleSession sess);
	public abstract void notifySessionCreated(final SimpleSession sess);
	
	public abstract void notifySessionWillPassivate(SimpleSession sess);
	public abstract void notifySessionDidActivate(final SimpleSession sess);
	
	
	public abstract void notifyContextAttributeAdded(String key, Object value);
	public abstract void notifyContextAttributeReplaced(String key, Object value);
	public abstract void notifyContextAttributeRemoved(String key, Object value);

	public abstract void notifySessionAttributeRemoved(final SimpleSession simpleSession, final String v, final Object r);
	public abstract void notifySessionAttributeReplaced(final SimpleSession simpleSession, final String k, final Object n);
	public abstract void notifySessionAttributeAdded(final SimpleSession simpleSession, final String k, final Object v);
	
	public abstract void notifyRequestInitialized(final SimpleHttpRequest req);
	public abstract void notifyRequestDestroyed(final SimpleHttpRequest req);

	public abstract void notifyRequestAttributeRemoved(final SimpleHttpRequest simpleHttpRequest, final String k, final Object remove);
	public abstract void notifyRequestAttributeAdded(final SimpleHttpRequest simpleHttpRequest, final String k, final Object v);
	public abstract void notifyRequestAttributeReplaced(final SimpleHttpRequest simpleHttpRequest, final String k, final Object r);
	
	public abstract void notifyContextInitialized();
	public abstract void notifyContextDestroyed();
}
