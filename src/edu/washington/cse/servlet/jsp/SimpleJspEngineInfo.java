package edu.washington.cse.servlet.jsp;

import javax.servlet.jsp.JspEngineInfo;

public class SimpleJspEngineInfo extends JspEngineInfo {
	@Override
	public String getSpecificationVersion() {
		return new String("2.0");
	}

}
