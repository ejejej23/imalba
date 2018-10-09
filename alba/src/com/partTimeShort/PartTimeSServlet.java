package com.partTimeShort;

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

@WebServlet("/partTimeShort/*")
public class PartTimeSServlet extends MyServlet {
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
		} else if (uri.indexOf("created.do") != -1) {
			createdForm(req, resp);
		} else if (uri.indexOf("created_ok.do") != -1) {
			createdSubmit(req, resp);
		} else if (uri.indexOf("article.do") != -1) {
			article(req, resp);
		} else if (uri.indexOf("update.do") != -1) {
			updateForm(req, resp);
		} else if (uri.indexOf("update_ok.do") != -1) {
			updateSubmit(req, resp);
		} else if (uri.indexOf("delete.do") != -1) {
			delete(req, resp);
		} else if (uri.indexOf("doneChk.do") != -1) {
			doneChk(req, resp);
		} else if (uri.indexOf("insertReply.do") != -1) {
			insertReply(req, resp);
		} else if (uri.indexOf("listReply.do") != -1) {
			listReply(req, resp);
		} else if (uri.indexOf("deleteReply.do") != -1) {
			deleteReply(req, resp);
		}
	}

	private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");

		String cp = req.getContextPath();
		PartTimeSDAO dao = new PartTimeSDAO();
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

		int dataCount;

		if (searchValue.length() == 0) {
			dataCount = dao.dataCount();
		} else {
			// �˻����� ������ ����
			dataCount = dao.dataCount(searchKey, searchValue);
		}

		int rows = 6;
		int total_page = util.pageCount(rows, dataCount);
		if (current_page > total_page)
			current_page = total_page;

		int start = (current_page - 1) * rows + 1;
		int end = current_page * rows;

		List<PartTimeSDTO> list;

		int listNum, n = 0;
		if (searchValue.length() == 0) {
			list = dao.listPartTimeShort(start, end);
		} else {
			// �˻�
			list = dao.listPartTimeShort(start, end, searchKey, searchValue);
		}

		String temp, temp2;
		for (PartTimeSDTO dto : list) {
			listNum = dataCount - (start + n - 1);
			dto.setListNum(listNum);
			temp = dto.getCreated();
			temp2 = temp.substring(0, 10);
			dto.setCreated(temp2);
			n++;
		}

		String query = "";
		if (searchValue.length() != 0) {
			query = "searchKey=" + searchKey + "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8");
		}

		// ����¡
		String listurl = cp + "/partTimeShort/list.do";
		String articleUrl = cp + "/partTimeShort/article.do?page=" + current_page;

		if (query.length() != 0) {
			listurl += "?" + query;
			articleUrl += "&" + query;
		}

		String paging = util.paging(current_page, total_page, listurl);

		req.setAttribute("page", current_page);
		req.setAttribute("total_page", total_page);
		req.setAttribute("dataCount", dataCount);
		req.setAttribute("list", list);
		req.setAttribute("articleUrl", articleUrl);
		req.setAttribute("paging", paging);

		forward(req, resp, "/WEB-INF/views/partTimeShort/list.jsp");
	}

	private void createdForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("mode", "created");
		forward(req, resp, "/WEB-INF/views/partTimeShort/created.jsp");
	}

	private void createdSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String cp = req.getContextPath();
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		PartTimeSDAO dao = new PartTimeSDAO();
		PartTimeSDTO dto = new PartTimeSDTO();

		dto.setComId(info.getUserId());// �ۼ�ȸ�� ���̵�
		dto.setSubject(req.getParameter("subject"));
		dto.setContent(req.getParameter("content"));

		dao.insertPartTimeShort(dto);

		resp.sendRedirect(cp + "/partTimeShort/list.do");
	}

	private void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String cp = req.getContextPath();
		req.setCharacterEncoding("utf-8");

		PartTimeSDAO dao = new PartTimeSDAO();
		PartTimeSDTO dto = null;
		String query = "";

		int num = Integer.parseInt(req.getParameter("num"));
		String page = req.getParameter("page");
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");

		if (searchKey == null) {
			searchKey = "subject";
			searchValue = "";
		}

		searchValue = URLDecoder.decode(searchValue, "UTF-8");
		query = "page=" + page;
		if (searchValue.length() != 0) {
			query += "&searchKey=" + searchKey + "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8");
		}

		// �Խù� ��ȸ�� ����
		dao.updateHitCount(num);

		// �Խù� ��������
		dto = dao.readPartTimeShort(num);
		if (dto == null) {
			resp.sendRedirect(cp + "/partTimeShort/list.do?" + query);
			return;
		}
		dto.setCreated(dto.getCreated().substring(0, 10));
		dto.setContent(dto.getContent().replaceAll("\n", "<br>"));

		// �������Ҷ� �ѱ� ������ ����
		req.setAttribute("dto", dto);
		req.setAttribute("query", query);
		req.setAttribute("page", page);

		forward(req, resp, "/WEB-INF/views/partTimeShort/article.jsp");
	}

	private void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		req.setCharacterEncoding("utf-8");
		String query = null;
		String cp = req.getContextPath();

		int num = Integer.parseInt(req.getParameter("num"));
		String page = req.getParameter("page");
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");

		if (searchKey == null) {
			searchKey = "subject";
			searchValue = "";
		}
		searchValue = URLDecoder.decode(searchValue, "UTF-8");
		query = "page=" + page;
		if (searchValue.length() != 0) {
			query += "&searchKey=" + searchKey + "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8");
		}

		PartTimeSDAO dao = new PartTimeSDAO();
		PartTimeSDTO dto = dao.readPartTimeShort(num);
		if (dto == null) {
			resp.sendRedirect(cp + "/partTimeShort/list.do?" + query);
			return;
		}

		// �ۼ��ڸ� ���������ϰ� ó�� : �ۼ��ڰ� �ƴϸ� ����Ʈ�� ���ư�
		if (!info.getUserId().equals(dto.getComId())) {
			resp.sendRedirect(cp + "/partTimeShort/list.do?" + query);
			return;
		}

		req.setAttribute("mode", "update");
		req.setAttribute("dto", dto);
		req.setAttribute("page", page);
		req.setAttribute("searchKey", searchKey);
		req.setAttribute("searchValue", searchValue);

		forward(req, resp, "/WEB-INF/views/partTimeShort/created.jsp");

	}

	private void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		PartTimeSDAO dao = new PartTimeSDAO();
		PartTimeSDTO dto = new PartTimeSDTO();

		dto.setComId(info.getUserId());
		dto.setPtsNum(Integer.parseInt(req.getParameter("num")));
		dto.setSubject(req.getParameter("subject"));
		dto.setContent(req.getParameter("content"));

		dao.updatePartTimeS(dto);

		String cp = req.getContextPath();
		String page = req.getParameter("page");
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");
		String query = "page" + page;
		if (searchValue.length() != 0) {
			query += "&searchKey=" + searchKey + "&searchValue" + URLEncoder.encode(searchValue, "UTF-8");
		}

		resp.sendRedirect(cp + "/partTimeShort/article.do?" + query + "&num=" + dto.getPtsNum());

	}

	private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String cp = req.getContextPath();
		PartTimeSDAO dao = new PartTimeSDAO();
		req.setCharacterEncoding("utf-8");

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String query = null;

		// num, page �Ķ���� �ޱ�
		int num = Integer.parseInt(req.getParameter("num"));
		String page = req.getParameter("page");
		query = "page=" + page;

		// num�� �ش��ϴ� �ڷ� ��������
		PartTimeSDTO dto = null;
		dto = dao.readPartTimeShort(num);
		// ������ ������ list.do�� �����̷�Ʈ
		if (dto == null) {
			resp.sendRedirect(cp + "/partTimeShort/list.do?" + query);
			return;
		}

		// ������, �ۼ��ڸ� ��������
		if (!info.getUserId().equals(dto.getComId()) && !info.getUserId().equals("admin")) {
			resp.sendRedirect(cp + "/partTimeShort/list.do?" + query);
			return;
		}

		//�ش� ���� ��� ����
		dao.deletePartTimeShortReply(num);
		
		//�ۻ���
		dao.deletePartTimeShort(num);

		resp.sendRedirect(cp + "/partTimeShort/list.do?page=" + page);
	}

	private void doneChk(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PartTimeSDAO dao = new PartTimeSDAO();

		int num = Integer.parseInt(req.getParameter("num"));
		int done = Integer.parseInt(req.getParameter("done"));

		if (done == 0) {
			// �����ϱ�
			dao.updateDone(num, 1);
		} else {
			// �������
			dao.updateDone(num, 0);
		}

		String cp = req.getContextPath();
		String page = req.getParameter("page");
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");
		String query = "page" + page;
		if (searchKey == null) {
			searchKey = "subject";
			searchValue = "";
		}
		searchValue = URLDecoder.decode(searchValue, "UTF-8");
		query = "page=" + page;
		if (searchValue.length() != 0) {
			query += "&searchKey=" + searchKey + "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8");
		}

		resp.sendRedirect(cp + "/partTimeShort/article.do?" + query + "&num=" + num);
	}

	protected void insertReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// �������� : AJAX-JSON
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		PartTimeSDAO dao = new PartTimeSDAO();

		String header = req.getHeader("AJAX");
		if (header != null && header.equals("true") && info == null) {
			// ������Ȳ : AJAX �α��� �ȵ� ������ ������ �� �Ÿ�.
			resp.sendError(403);
			return;
		}

		String state = "false";
		ReplyDTO dto = new ReplyDTO();
		dto.setNum(Integer.parseInt(req.getParameter("num")));// �۹�ȣ
		dto.setUserId(info.getUserId());// �������̵�
		dto.setContent(req.getParameter("content"));// ��۳���

		dao.insertReply(dto);
		state = "true";

		JSONObject job = new JSONObject();
		job.put("state", state);

		resp.setContentType("text/html; charset=utf-8");
		PrintWriter out = resp.getWriter();
		out.println(job.toString());
	}

	protected void listReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ��� ����Ʈ: AJAX-TEXT
		PartTimeSDAO dao = new PartTimeSDAO();
		MyUtil util = new MyUtil();

		int num = Integer.parseInt(req.getParameter("num"));
		String pageNo = req.getParameter("pageNo");
		int current_page = 1;
		if (pageNo != null)
			current_page = Integer.parseInt(pageNo);

		// ��ü������ ����
		int dataCount = dao.dataCountReply(num);

		// ��ü ������ ��
		int rows = 5;
		int total_page = util.pageCount(rows, dataCount);
		if (current_page > total_page)
			current_page = total_page;

		int start = (current_page - 1) * rows + 1;
		int end = current_page * rows;

		// �Խù� ��������
		List<ReplyDTO> list = dao.listReply(num, start, end);
		for (ReplyDTO dto : list) {
			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
		}

		// ����¡ ó��
		String paging = util.paging(current_page, total_page);

		// ��Ʈ����Ʈ�� �߰�
		req.setAttribute("pageNo", current_page);
		req.setAttribute("total_page", total_page);
		req.setAttribute("dataCount", dataCount);
		req.setAttribute("list", list);
		req.setAttribute("paging", paging);

		forward(req, resp, "/WEB-INF/views/partTimeShort/listReply.jsp");
	}

	protected void deleteReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ���û��� : AJAX-JSON
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		PartTimeSDAO dao = new PartTimeSDAO();

		String header = req.getHeader("AJAX");
		if (header != null && header.equals("true") && info == null) {
			// ������Ȳ : AJAX �α��� �ȵ� ������ ������ �� �Ÿ�.
			resp.sendError(403);
			return;
		}

		String state = "false";
		int num = Integer.parseInt(req.getParameter("replyNum"));

		dao.deleteReply(num);
		state = "true";

		JSONObject job = new JSONObject();
		job.put("state", state);

		resp.setContentType("text/html; charset=utf-8");
		PrintWriter out = resp.getWriter();
		out.println(job.toString());
	}

}
