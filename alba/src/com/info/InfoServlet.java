package com.info;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.util.MyServlet;

@WebServlet("/info/*")
public class InfoServlet extends MyServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		String uri=req.getRequestURI();
		
		if(uri.indexOf("intro.do")!=-1) {
			forward(req, resp, "/WEB-INF/views/info/intro.jsp");
		}if(uri.indexOf("map.do")!=-1) {
			forward(req, resp, "/WEB-INF/views/info/map.jsp");
		}
	}
}
