package edu.washington.cse.servlet.jsp;

import static edu.washington.cse.servlet.Util.nondetInt;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

public class SimpleJspWriter extends JspWriter {

	protected SimpleJspWriter(final int bufferSize, final boolean autoFlush) {
		super(bufferSize, autoFlush);
	}

	@Override
	public void clear() throws IOException {
	}

	@Override
	public void clearBuffer() throws IOException {
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public int getRemaining() {
		return nondetInt();
	}

	@Override
	public void newLine() throws IOException {
	}

	@Override
	public void print(final boolean arg0) throws IOException {
	}

	@Override
	public void print(final char arg0) throws IOException {
	}

	@Override
	public void print(final int arg0) throws IOException {
	}

	@Override
	public void print(final long arg0) throws IOException {
	}

	@Override
	public void print(final float arg0) throws IOException {
	}

	@Override
	public void print(final double arg0) throws IOException {
	}

	@Override
	public void print(final char[] arg0) throws IOException {
	}

	@Override
	public void print(final String arg0) throws IOException {
	}

	@Override
	public void print(final Object arg0) throws IOException {
	}

	@Override
	public void println() throws IOException {
	}

	@Override
	public void println(final boolean arg0) throws IOException {
	}

	@Override
	public void println(final char arg0) throws IOException {
	}

	@Override
	public void println(final int arg0) throws IOException {
	}

	@Override
	public void println(final long arg0) throws IOException {
	}

	@Override
	public void println(final float arg0) throws IOException {
	}

	@Override
	public void println(final double arg0) throws IOException {
	}

	@Override
	public void println(final char[] arg0) throws IOException {
	}

	@Override
	public void println(final String arg0) throws IOException {
	}

	@Override
	public void println(final Object arg0) throws IOException {
	}

	@Override
	public void write(final char[] arg0, final int arg1, final int arg2) throws IOException {
	}

}
