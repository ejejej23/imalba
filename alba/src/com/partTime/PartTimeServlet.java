package com.partTime;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.member.SessionInfo;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.util.FileManager;
import com.util.MyServlet;
import com.util.MyUtil;

@WebServlet("/partTime/*")
public class PartTimeServlet extends MyServlet {
	private static final long serialVersionUID = 1L;
	private String pathname;

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

		// �̹����� ������ ���
		// �̹����� �ݵ�� ����ο� �����ؾ� img �±׷� �� �� ����.
		String root = session.getServletContext().getRealPath("/");// ����� ��Ʈ�� ���� ��ġ
		pathname = root + "uploads" + File.separator + "photo";
		File f = new File(pathname);
		if (!f.exists()) {// �������� ������ ������ ���� �����
			f.mkdirs();
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
		}
	}

	private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");

		String cp = req.getContextPath();
		PartTimeDAO dao = new PartTimeDAO();
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

		List<PartTimeDTO> list;

		int listNum, n = 0;
		if (searchValue.length() == 0) {
			list = dao.listPartTime(start, end);
		} else {
			// �˻�
			list = dao.listPartTime(start, end, searchKey, searchValue);
		}

		String temp, temp2;
		for (PartTimeDTO dto : list) {
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
		String listurl = cp + "/partTime/list.do";
		String articleUrl = cp + "/partTime/article.do?page=" + current_page;

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

		forward(req, resp, "/WEB-INF/views/partTime/list.jsp");
	}

	private void createdForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("mode", "created");
		forward(req, resp, "/WEB-INF/views/partTime/created.jsp");
	}

	private void createdSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String cp = req.getContextPath();
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		PartTimeDAO dao = new PartTimeDAO();
		PartTimeDTO dto = new PartTimeDTO();

		String encType = "utf-8";
		int maxSize = 5 * 1024 * 1024;

		// <form enctype="multipart/form-data"...> ��� �����ؾ� ���� ���ε� ����,
		// request��ü�� �Ķ���͸� �Ѱܹ��� �� ����.
		MultipartRequest mreq = new MultipartRequest(req, pathname, maxSize, encType, new DefaultFileRenamePolicy());
		if (mreq.getFile("upload") != null) {

			String saveFilename = mreq.getFilesystemName("upload");
			saveFilename = FileManager.doFilerename(pathname, saveFilename);

			dto.setJobImage(saveFilename);
		}
		// �ۼ��� ��ü �̸�
		dto.setComId(info.getUserId());
		dto.setComName(info.getUserName());
		dto.setSubject(mreq.getParameter("subject"));
		dto.setContent(mreq.getParameter("content"));
		dto.setApplyStart(mreq.getParameter("applyStart"));
		dto.setApplyEnd(mreq.getParameter("applyEnd"));

		dao.insertPartTime(dto);

		resp.sendRedirect(cp + "/partTime/list.do");
	}

	private void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String cp = req.getContextPath();
		req.setCharacterEncoding("utf-8");

		PartTimeDAO dao = new PartTimeDAO();
		PartTimeDTO dto = null;
		String query = "";

		int ptNum = Integer.parseInt(req.getParameter("num"));
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
		dao.updateHitCount(ptNum);

		// �Խù� ��������
		dto = dao.readPartTime(ptNum);
		if (dto == null) {
			resp.sendRedirect(cp + "/partTime/list.do?" + query);
			return;
		}
		dto.setApplyStart(dto.getApplyStart().substring(0, 10));
		dto.setApplyEnd(dto.getApplyEnd().substring(0, 10));
		dto.setContent(dto.getContent().replaceAll("\n", "<br>"));

		// ������,������
		PartTimeDTO preReadDto = dao.preReadPartTime(ptNum, searchKey, searchValue);
		PartTimeDTO nextReadDto = dao.nextReadPartTime(ptNum, searchKey, searchValue);

		String apply = "no";
		// �����ϱ� ������ ����

		Date currentTime = new Date();
		Date applyS = null;
		Date applyE = null;

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			applyS = format.parse(dto.getApplyStart());
			applyE = format.parse(dto.getApplyEnd());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		long compare1 = applyS.getTime() - currentTime.getTime();
		long compare2 = applyE.getTime() - currentTime.getTime();

		if (compare1 <= 0 && compare2 >= 0) {
			apply = "ok";
		}

		// �������Ҷ� �ѱ� ������ ����
		req.setAttribute("dto", dto);
		req.setAttribute("preReadDto", preReadDto);
		req.setAttribute("nextReadDto", nextReadDto);
		req.setAttribute("query", query);
		req.setAttribute("page", page);
		req.setAttribute("apply", apply);

		forward(req, resp, "/WEB-INF/views/partTime/article.jsp");
	}

	private void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String cp = req.getContextPath();
		req.setCharacterEncoding("utf-8");

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		PartTimeDAO dao = new PartTimeDAO();
		PartTimeDTO dto = null;
		String query = "";

		// num, page �Ķ���� �ޱ�
		int num = Integer.parseInt(req.getParameter("num"));
		String page = req.getParameter("page");
		query = "page=" + page;

		// num�� �ش��ϴ� �ڷ� ��������
		dto = dao.readPartTime(num);

		// ������ ������ list�� �����̷�Ʈ
		// ������ ������ list.do�� �����̷�Ʈ
		if (dto == null) {
			resp.sendRedirect(cp + "/partTime/list.do?" + query);
			return;
		}
		dto.setApplyStart(dto.getApplyStart().substring(0, 10));
		dto.setApplyEnd(dto.getApplyEnd().substring(0, 10));

		// �ۼ��ڸ� ��������
		if (!info.getUserId().equals(dto.getComId())) {
			resp.sendRedirect(cp + "/partTime/list.do?" + query);
			return;
		}

		// jsp�� �ѱ� ��
		req.setAttribute("mode", "update");
		req.setAttribute("dto", dto);
		req.setAttribute("page", page);

		forward(req, resp, "/WEB-INF/views/partTime/created.jsp");

	}

	private void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String cp = req.getContextPath();
		req.setCharacterEncoding("utf-8");

		PartTimeDAO dao = new PartTimeDAO();
		PartTimeDTO dto = new PartTimeDTO();

		String encType = "utf-8";
		int maxSize = 5 * 1024 * 1024;

		MultipartRequest mreq = new MultipartRequest(req, pathname, maxSize, encType, new DefaultFileRenamePolicy());
		dto.setPtNum(Integer.parseInt(mreq.getParameter("num")));
		dto.setSubject(mreq.getParameter("subject"));
		dto.setContent(mreq.getParameter("content"));
		dto.setApplyStart(mreq.getParameter("applyStart"));
		dto.setApplyEnd(mreq.getParameter("applyEnd"));

		String imageFilename = mreq.getParameter("jobImage");

		if (mreq.getFile("upload") != null) {
			// ���� ���� �����
			FileManager.doFiledelete(pathname, imageFilename);

			// ������ ����� ���ϸ�
			String saveFilename = mreq.getOriginalFileName("upload");
			saveFilename = FileManager.doFilerename(pathname, saveFilename);
			dto.setJobImage(saveFilename);

		} else {
			// ������ �������� ���� ���
			dto.setJobImage(imageFilename);
		}
		dao.updatePartTime(dto);

		String page = mreq.getParameter("page");
		resp.sendRedirect(cp + "/partTime/article.do?page=" + page + "&num=" + dto.getPtNum());

	}

	private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String cp = req.getContextPath();
		PartTimeDAO dao = new PartTimeDAO();
		req.setCharacterEncoding("utf-8");

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String query = null;

		// num, page �Ķ���� �ޱ�
		int num = Integer.parseInt(req.getParameter("num"));
		String page = req.getParameter("page");
		query = "page=" + page;

		// num�� �ش��ϴ� �ڷ� ��������
		PartTimeDTO dto = null;
		dto = dao.readPartTime(num);
		// ������ ������ list.do�� �����̷�Ʈ
		if (dto == null) {
			resp.sendRedirect(cp + "/partTime/list.do?" + query);
			return;
		}

		// ������, �ۼ��ڸ� ��������
		if (!info.getUserId().equals(dto.getComId()) && !info.getUserId().equals("admin")) {
			resp.sendRedirect(cp + "/partTime/list.do?" + query);
			return;
		}

		FileManager.doFiledelete(pathname, dto.getJobImage());
		dao.deletePartTime(num);
		
		resp.sendRedirect(cp+"/partTime/list.do?page="+page);
	}
}
