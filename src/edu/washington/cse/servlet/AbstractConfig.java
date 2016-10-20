package edu.washington.cse.servlet;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

public class AbstractConfig {

	private final SimpleContext context;
	private final Map<String, String> params = new HashMap<>();

	public AbstractConfig (final SimpleContext cont) {
		this.context = cont;
	}
	
	public String getInitParameter(final String key) {
		return params.get(key);
	}

	public Enumeration<String> getInitParameterNames() {
		return new SimpleEnumeration<String>(params.keySet());
	}

	public ServletContext getServletContext() {
		return context;
	}
}
