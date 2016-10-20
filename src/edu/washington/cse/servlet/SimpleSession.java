package edu.washington.cse.servlet;

import static edu.washington.cse.servlet.Util.nondetBool;
import static edu.washington.cse.servlet.Util.nondetInt;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionContext;

@SuppressWarnings("deprecation")
public class SimpleSession implements HttpSession {
	private final SimpleContext simpCont;
	private final Map<String, Object> attr = new HashMap<>();
	private final String id;

	public SimpleSession(final SimpleContext simpCont) {
		this.simpCont = simpCont;
		this.id = new String();
	}
	

	@Override
	public Object getAttribute(final String arg0) {
		return this.attr.get(arg0);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return new SimpleEnumeration<>(attr.keySet());
	}

	@Override
	public long getCreationTime() {
		return System.currentTimeMillis();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public long getLastAccessedTime() {
		return System.currentTimeMillis();
	}

	@Override
	public int getMaxInactiveInterval() {
		return nondetInt();
	}

	@Override
	public ServletContext getServletContext() {
		return simpCont;
	}

	@Override
	public HttpSessionContext getSessionContext() {
		return new HttpSessionContext() {
			
			@Override
			public HttpSession getSession(final String arg0) {
				return null;
			}
			
			@Override
			public Enumeration<String> getIds() {
				return Collections.emptyEnumeration();
			}
		};
	}

	@Override
	public Object getValue(final String k) {
		return this.getAttribute(k);
	}

	@Override
	public String[] getValueNames() {
		return this.attr.keySet().toArray(new String[0]);
	}

	@Override
	public void invalidate() {
	}

	@Override
	public boolean isNew() {
		return nondetBool();
	}

	@Override
	public void putValue(final String k, final Object v) {
		this.setAttribute(k, v);
	}

	@Override
	public void removeAttribute(final String v) {
		final Object r = this.attr.remove(v);
		if(r instanceof HttpSessionBindingListener) {
			((HttpSessionBindingListener)r).valueUnbound(new HttpSessionBindingEvent(this, v, r));
		}
		simpCont.sessAttrList[nondetInt()].attributeRemoved(new HttpSessionBindingEvent(this, v, r));
	}

	@Override
	public void removeValue(final String k) {
		this.removeAttribute(k);
	}

	@Override
	public void setAttribute(final String k, final Object v) {
		if(v == null) { removeAttribute(k); return; }
		
		final Object n = attr.put(k, v);
		if(v instanceof HttpSessionBindingListener) {
			((HttpSessionBindingListener) v).valueBound(new HttpSessionBindingEvent(this, k, v));
		}
		simpCont.sessAttrList[nondetInt()].attributeAdded(new HttpSessionBindingEvent(this, k, v));
		if(n != null) {
			simpCont.sessAttrList[nondetInt()].attributeReplaced(new HttpSessionBindingEvent(this, k, n));	
		}
		if(n instanceof HttpSessionBindingEvent) {
			((HttpSessionBindingListener)n).valueUnbound(new HttpSessionBindingEvent(this, k, n));
		}
	}

	@Override
	public void setMaxInactiveInterval(final int arg0) { }

}
