package com.apply;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.member.SessionInfo;
import com.util.MyServlet;
import com.util.MyUtil;

import net.sf.json.JSONObject;

@WebServlet("/applyList/*")
public class ApplyServlet extends MyServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		String uri = req.getRequestURI();

		// 로그인 정보
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		if (info == null) {
			forward(req, resp, "/WEB-INF/views/member/login.jsp");
			return;
		}

		if (uri.indexOf("list.do") != -1) {
			listApply(req, resp);
		} else if (uri.indexOf("apply.do") != -1) {
			applyInsert(req, resp);
		} else if (uri.indexOf("delete.do") != -1) {
			delete(req, resp);
		}
	}

	private void listApply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();
		ApplyDAO dao = new ApplyDAO();
		MyUtil util = new MyUtil();

		String page = req.getParameter("page");
		int current_page = 1;
		if (page != null) {
			current_page = Integer.parseInt(page);
		}

		// 검색조건
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");
		if (searchKey == null) {
			searchKey = "subject";
			searchValue = "";
		}
		// 검색조건이 get으로 넘어온 경우에는 디코딩한다
		if (req.getMethod().equalsIgnoreCase("GET")) {
			searchValue = URLDecoder.decode(searchValue, "UTF-8");
		}

		int dataCount = 0;
		// 개인
		if (searchValue.length() == 0) {
			dataCount = dao.dataCount(info.getUserId());
		} else {
			// 검색조건 있을때 진행
			dataCount = dao.dataCount(searchKey, searchValue, info.getUserId());
		}
		int rows = 6;
		int total_page = util.pageCount(rows, dataCount);
		if (current_page > total_page)
			current_page = total_page;

		int start = (current_page - 1) * rows + 1;
		int end = current_page * rows;

		List<ApplyDTO> list = null;

		int listNum, n = 0;
		// 개인
		if (searchValue.length() == 0) {
			list = dao.listApplyM(start, end, info.getUserId());
		} else {
			// 검색
			list = dao.listApplyM(start, end, searchKey, searchValue, info.getUserId());
		}

		String temp, temp2;
		for (ApplyDTO dto : list) {
			listNum = dataCount - (start + n - 1);
			dto.setListNum(listNum);
			temp = dto.getApplyDate();
			temp2 = temp.substring(0, 10);
			dto.setApplyDate(temp2);
			n++;
		}

		String query = "";
		if (searchValue.length() != 0) {
			query = "searchKey=" + searchKey + "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8");
		}

		// 페이징
		String listurl = cp + "/applyList/list.do";

		if (query.length() != 0) {
			listurl += "?" + query;
		}

		String paging = util.paging(current_page, total_page, listurl);

		req.setAttribute("page", current_page);
		req.setAttribute("total_page", total_page);
		req.setAttribute("dataCount", dataCount);
		req.setAttribute("list", list);
		req.setAttribute("paging", paging);

		forward(req, resp, "/WEB-INF/views/applyList/list.jsp");
	}

	private void applyInsert(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		ApplyDAO dao = new ApplyDAO();

		String state = "false";
		if (info != null) {
			ApplyDTO dto = new ApplyDTO();
			dto.setUserId(info.getUserId());
			dto.setPtNum(Integer.parseInt(req.getParameter("num")));

			int result = dao.readMyApply(dto.getUserId(), dto.getPtNum());
			if (result != 0) {
				state = "done";
			} else {
				dao.insertApply(dto);
				state = "true";
			}
		} else {
			state = "loginFail";
		}

		JSONObject job = new JSONObject();
		job.put("state", state);

		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out = resp.getWriter();
		out.print(job.toString());
	}

	private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}
}
