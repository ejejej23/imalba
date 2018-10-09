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

		// �α��� ����
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

		// �˻�����
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");
		if (searchKey == null) {
			searchKey = "subject";
			searchValue = "";
		}
		// �˻������� get���� �Ѿ�� ��쿡�� ���ڵ��Ѵ�
		if (req.getMethod().equalsIgnoreCase("GET")) {
			searchValue = URLDecoder.decode(searchValue, "UTF-8");
		}

		int dataCount = 0;
		// ���
		if (searchValue.length() == 0) {
			dataCount = dao.dataCount(info.getUserId());
		} else {
			// �˻����� ������ ����
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
		// ���
		if (searchValue.length() == 0) {
			list = dao.listApplyer(start, end, info.getUserId());
		} else {
			// �˻�
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

			// ���̰��
			tempA = dto.getBirth();
			if (tempA != null) {
				a = Integer.parseInt(tempA.substring(0, 4));// ������Ͽ��� �⵵ ��������
				b = Calendar.getInstance().get(Calendar.YEAR);// ���� �⵵ ���ϱ�

				dto.setAge(b - a + 1);
			}

			n++;
		}
		
		String query = "";
		if (searchValue.length() != 0) {
			query = "searchKey=" + searchKey + "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8");
		}

		// ����¡
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
