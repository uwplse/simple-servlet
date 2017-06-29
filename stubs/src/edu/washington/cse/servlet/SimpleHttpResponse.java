package edu.washington.cse.servlet;

import static edu.washington.cse.servlet.Util.nondetBool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class SimpleHttpResponse implements HttpServletResponse {
	
	String contentType;
	String characterEncoding;
	
	Locale locale;
	
	int bufferSize, contentLength, status;
	
	Cookie[] respCookie = new Cookie[1];
	
	private final Map<String, String> headers = new HashMap<>();

	@Override
	public void flushBuffer() throws IOException {
	}

	@Override
	public int getBufferSize() {
		return bufferSize;
	}

	@Override
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}
	
	ServletOutputStream sos = new ServletOutputStream() {
		@Override
		public void write(final int arg0) throws IOException {
		}
	};
	
	PrintWriter pw = new PrintWriter(sos);
	
	

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if(nondetBool()) {
			throw new IllegalStateException();
		}
		return sos;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if(nondetBool()) {
			throw new IllegalStateException();
		}
		return pw;
	}

	@Override
	public boolean isCommitted() {
		return nondetBool();
	}

	@Override
	public void reset() {
	}

	@Override
	public void resetBuffer() {
	}

	@Override
	public void setBufferSize(final int bs) {
		bufferSize = bs;
	}

	@Override
	public void setCharacterEncoding(final String ce) {
		this.characterEncoding = ce;
	}

	@Override
	public void setContentLength(final int cl) {
		this.contentLength = cl;
	}

	@Override
	public void setContentType(final String ct) {
		this.contentType = ct;
	}

	@Override
	public void setLocale(final Locale locale) {
		this.locale = locale;
	}

	@Override
	public void addCookie(final Cookie cookie) {
		respCookie[0] = cookie;
	}

	@Override
	public void addDateHeader(final String key, final long value) {
		this.headers.put(key, String.valueOf(value));
	}

	@Override
	public void addHeader(final String key, final String value) {
		this.headers.put(key, String.valueOf(value));
	}

	@Override
	public void addIntHeader(final String key, final int value) {
		this.headers.put(key, String.valueOf(value));
	}

	@Override
	public boolean containsHeader(final String key) {
		return headers.containsKey(key);
	}

	@Override
	public String encodeRedirectURL(final String in) {
		return new String(in);
	}

	@Override
	public String encodeRedirectUrl(final String in) {
		return new String(in);
	}

	@Override
	public String encodeURL(final String in) {
		return new String(in);
	}

	@Override
	public String encodeUrl(final String in) {
		return new String(in);
	}

	@Override
	public String getHeader(final String key) {
		return headers.get(key);
	}

	@Override
	public Collection<String> getHeaderNames() {
		return headers.keySet();
	}

	@Override
	public Collection<String> getHeaders(final String arg0) {
		return headers.values();
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public void sendError(final int arg0) throws IOException {

	}

	@Override
	public void sendError(final int arg0, final String arg1) throws IOException {
	}

	@Override
	public void sendRedirect(final String arg0) throws IOException {
	}

	@Override
	public void setDateHeader(final String key, final long value) {
		this.headers.put(key, String.valueOf(value));
	}

	@Override
	public void setHeader(final String key, final String value) {
		this.headers.put(key, value);
	}

	@Override
	public void setIntHeader(final String key, final int value) {
		this.headers.put(key, String.valueOf(value));
	}

	@Override
	public void setStatus(final int stat) {
		this.status = stat;
	}

	@Override
	public void setStatus(final int stat, final String arg1) {
		this.status = stat;
	}

}
