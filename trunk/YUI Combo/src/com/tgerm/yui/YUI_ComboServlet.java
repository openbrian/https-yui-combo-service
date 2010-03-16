package com.tgerm.yui;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Http Servlet to wrap the YUI Combo Service(http://yui.yahooapis.com/combo?)
 * in a secure https shell.
 * 
 * @author abhinav {@link abhinav@tgerm.com}
 * 
 */
@SuppressWarnings("serial")
public class YUI_ComboServlet extends HttpServlet {

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doHead(req, resp);
		YUIComboHelper.handleYuiComboRequest(req, resp);
	}

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doOptions(req, resp);
		YUIComboHelper.handleYuiComboRequest(req, resp);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		YUIComboHelper.handleYuiComboRequest(req, resp);
	}

}
