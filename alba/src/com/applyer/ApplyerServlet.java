package com.applyer;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.member.SessionInfo;
import com.util.MyServlet;
import com.util.MyUtil;

@WebServlet("/applyerList/*")
public class ApplyerServlet extends MyServlet {
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
			list(req, resp);
		} else if (uri.indexOf("article.do") != -1) {
			article(req, resp);
		} else if (uri.indexOf("delete.do") != -1) {
			delete(req, resp);
		}
	}

	private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();
		ApplyerDAO dao = new ApplyerDAO();
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
		// 기업
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

		List<ApplyerDTO> list = null;

		int listNum, n = 0;
		// 기업
		if (searchValue.length() == 0) {
			list = dao.listApplyer(start, end, info.getUserId());
		} else {
			// 검색
			list = dao.listApplyer(start, end, searchKey, searchValue, info.getUserId());
		}

		String temp1, temp2;
		String tempA;
		int a, b;
		for (ApplyerDTO dto : list) {
			listNum = dataCount - (start + n - 1);
			dto.setListNum(listNum);
			temp1 = dto.getApplyDate();
			temp2 = temp1.substring(0, 10);
			dto.setApplyDate(temp2);

			// 나이계산
			tempA = dto.getBirth();
			if (tempA != null) {
				a = Integer.parseInt(tempA.substring(0, 4));// 생년월일에서 년도 가져오기
				b = Calendar.getInstance().get(Calendar.YEAR);// 현재 년도 구하기

				dto.setAge(b - a + 1);
			}

			n++;
		}
		
		String query = "";
		if (searchValue.length() != 0) {
			query = "searchKey=" + searchKey + "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8");
		}

		// 페이징
		String listurl = cp + "/applyer/list.do";
		if (query.length() != 0) {
			listurl += "?" + query;
		}
		
		String paging = util.paging(current_page, total_page, listurl);

		req.setAttribute("page", current_page);
		req.setAttribute("total_page", total_page);
		req.setAttribute("dataCount", dataCount);
		req.setAttribute("list", list);
		req.setAttribute("paging", paging);

		forward(req, resp, "/WEB-INF/views/applyerList/list.jsp");
	}

	private void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}

	private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}
}
