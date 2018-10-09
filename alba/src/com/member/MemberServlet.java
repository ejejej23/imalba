package com.member;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.util.FileManager;
import com.util.MyServlet;

import net.sf.json.JSONObject;

@WebServlet("/member/*")
public class MemberServlet extends MyServlet {
	private static final long serialVersionUID = 1L;

	private String pathname;

	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		String uri = req.getRequestURI();

		HttpSession session = req.getSession();

		// �̹����� ������ ���
		// �̹����� �ݵ�� ����ο� �����ؾ� img �±׷� �� �� ����.
		String root = session.getServletContext().getRealPath("/");// ����� ��Ʈ�� ���� ��ġ
		pathname = root + "uploads" + File.separator + "photo";
		File f = new File(pathname);
		if (!f.exists()) {// �������� ������ ������ ���� �����
			f.mkdirs();
		}

		// uri�� ���� �۾� ����
		if (uri.indexOf("login.do") != -1) {
			loginForm(req, resp);
		} else if (uri.indexOf("loginC_ok.do") != -1) {
			loginCSubmit(req, resp);
		} else if (uri.indexOf("loginM_ok.do") != -1) {
			loginMSubmit(req, resp);
		} else if (uri.indexOf("memberC.do") != -1) {
			joinFormC(req, resp);
		} else if (uri.indexOf("memberM.do") != -1) {
			joinFormM(req, resp);
		} else if (uri.indexOf("memberC_ok.do") != -1) {
			joinSubmitC(req, resp);
		} else if (uri.indexOf("memberM_ok.do") != -1) {
			joinSubmitM(req, resp);
		} else if (uri.indexOf("updateC_ok.do") != -1) {
			updateSubmitC(req, resp);
		} else if (uri.indexOf("updateM_ok.do") != -1) {
			updateSubmitM(req, resp);
		} else if (uri.indexOf("logout.do") != -1) {
			logout(req, resp);
		} else if (uri.indexOf("userIdCheck.do") != -1) {
			userIdCheck(req, resp);
		} else if (uri.indexOf("comIdCheck.do") != -1) {
			comIdCheck(req, resp);
		} else if (uri.indexOf("pwd.do") != -1) {
			pwdForm(req, resp);
		} else if (uri.indexOf("pwd_okC.do") != -1) {
			pwdSubmitC(req, resp);
		} else if (uri.indexOf("pwd_okM.do") != -1) {
			pwdSubmitM(req, resp);
		}

	}

	private void userIdCheck(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MemberDAO dao = new MemberDAO();
		String userId = req.getParameter("userId");

		MemberDTO dto = dao.readMember(userId);
		String passed = "true";

		if (dto != null) {
			passed = "false";
		}

		JSONObject job = new JSONObject();
		job.put("passed", passed);

		resp.setContentType("text/html; charset=utf-8;");
		PrintWriter out = resp.getWriter();

		out.print(job.toString());
	}

	private void comIdCheck(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		CompanyDAO dao = new CompanyDAO();
		String comId = req.getParameter("comId");

		CompanyDTO dto = dao.readCompany(comId);
		String passed = "true";

		if (dto != null) {
			passed = "false";
		}

		JSONObject job = new JSONObject();
		job.put("passed", passed);

		resp.setContentType("text/html; charset=utf-8;");
		PrintWriter out = resp.getWriter();

		out.print(job.toString());
	}

	private void loginForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// �α��� ��
		String path = "/WEB-INF/views/member/login.jsp";
		forward(req, resp, path);
	}

	private void loginMSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ����ȸ�� �α��� ó��
		HttpSession session = req.getSession();
		String cp = req.getContextPath();
		MemberDAO dao = new MemberDAO();

		String userId = req.getParameter("userId");
		String userPwd = req.getParameter("userPwd");

		MemberDTO dto = dao.readMember(userId);
		if (dto != null) {
			if (userPwd.equals(dto.getPassword()) && dto.getEnabled() == 1) {
				// �α��� ����
				// ������ �����Ⱓ ���� : 20������ ������
				session.setMaxInactiveInterval(20 * 60);

				// ���ǿ� ������ ����
				SessionInfo info = new SessionInfo();
				info.setUserId(dto.getUserId());
				info.setUserName(dto.getUserName());

				if (dto.getUserId().equals("admin")) {
					info.setUserRoll(0);// ������
				} else {
					info.setUserRoll(1);// ȸ��
				}

				session.setAttribute("member", info);

				// ����ȭ������ �����̷�Ʈ
				resp.sendRedirect(cp);
				return;
			}
		}

		// �α��� ����
		String msg = "���̵� �Ǵ� �н����尡 ��ġ���� �ʽ��ϴ�.";
		req.setAttribute("messageM", msg);

		String path = "/WEB-INF/views/member/login.jsp";
		forward(req, resp, path);

	}

	private void loginCSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ���ȸ�� �α��� ó��
		HttpSession session = req.getSession();
		String cp = req.getContextPath();
		CompanyDAO dao = new CompanyDAO();

		String comId = req.getParameter("comId");
		String comPwd = req.getParameter("comPwd");

		CompanyDTO dto = dao.readCompany(comId);

		if (dto != null) {
			if (comPwd.equals(dto.getPassword()) && dto.getEnabled() == 1) {
				// �α��� ����
				// ������ �����Ⱓ ���� : 20������ ������
				session.setMaxInactiveInterval(20 * 60);

				// ���ǿ� ������ ����
				SessionInfo info = new SessionInfo();
				info.setUserId(dto.getComId());
				info.setUserName(dto.getComName());
				if (dto.getComId().equals("admin")) {
					info.setUserRoll(0);// ������
				} else {
					info.setUserRoll(2);// ��ü
				}

				session.setAttribute("member", info);

				// ����ȭ������ �����̷�Ʈ
				resp.sendRedirect(cp);
				return;
			}
		}

		// �α��� ����
		String msg = "���̵� �Ǵ� �н����尡 ��ġ���� �ʽ��ϴ�.";
		req.setAttribute("messageC", msg);

		String path = "/WEB-INF/views/member/login.jsp";
		forward(req, resp, path);

	}

	private void joinFormC(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("mode", "created");
		forward(req, resp, "/WEB-INF/views/member/memberC.jsp");
	}

	private void joinFormM(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("mode", "created");
		forward(req, resp, "/WEB-INF/views/member/memberM.jsp");
	}

	private void joinSubmitC(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ���ȸ������

		CompanyDAO dao = new CompanyDAO();
		CompanyDTO dto = new CompanyDTO();

		String encType = "utf-8";
		int maxSize = 5 * 1024 * 1024;

		MultipartRequest mreq = new MultipartRequest(req, pathname, maxSize, encType, new DefaultFileRenamePolicy());

		if (mreq.getFile("upload") != null) {

			String saveFilename = mreq.getFilesystemName("upload");
			saveFilename = FileManager.doFilerename(pathname, saveFilename);

			dto.setLogoImage(saveFilename);
		}

		dto.setComId(mreq.getParameter("comId"));
		dto.setPassword(mreq.getParameter("comPwd"));
		dto.setComName(mreq.getParameter("comName"));
		dto.setZip(mreq.getParameter("zip"));
		dto.setAddress1(mreq.getParameter("addr1"));
		dto.setAddress2(mreq.getParameter("addr2"));

		String email1 = mreq.getParameter("email1");
		String email2 = mreq.getParameter("email2");
		if (email1 != null && email1.length() != 0 && email2 != null && email2.length() != 0) {
			dto.setEmail(email1 + "@" + email2);
		}

		String tel1 = mreq.getParameter("tel1");
		String tel2 = mreq.getParameter("tel2");
		String tel3 = mreq.getParameter("tel3");
		if (tel1 != null && tel2 != null && tel3 != null && tel1.length() != 0 && tel2.length() != 0
				&& tel3.length() != 0) {
			dto.setPhoneNum(tel1 + "-" + tel2 + "-" + tel3);
		}

		int result = dao.insertCompany(dto);
		if (result == 0) {
			// ȸ������ ����
			String msg = "ȸ�����Կ� �����߽��ϴ�";

			req.setAttribute("mode", "created");
			req.setAttribute("message", msg);

			forward(req, resp, "/WEB-INF/views/member/memberC.jsp");
			return;
		}

		// ȸ������ ����
		String msg = "<b>" + dto.getComName() + "</b>�� ȸ��(���)�� �Ȱ��� �����մϴ�!<br> �ٽ��ѹ� �α������ּ���<br>";

		req.setAttribute("message", msg);
		forward(req, resp, "/WEB-INF/views/member/complete.jsp");
	}

	private void joinSubmitM(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ����ȸ������
		MemberDAO dao = new MemberDAO();
		MemberDTO dto = new MemberDTO();

		dto.setUserId(req.getParameter("userId"));
		dto.setPassword(req.getParameter("userPwd"));
		dto.setUserName(req.getParameter("userName"));
		dto.setBirth(req.getParameter("birth"));
		dto.setZip(req.getParameter("zip"));
		dto.setAddress1(req.getParameter("address1"));
		dto.setAddress2(req.getParameter("address2"));

		String email1 = req.getParameter("email1");
		String email2 = req.getParameter("email2");
		if (email1 != null && email1.length() != 0 && email2 != null && email2.length() != 0) {
			dto.setEmail(email1 + "@" + email2);
		}

		String tel1 = req.getParameter("tel1");
		String tel2 = req.getParameter("tel2");
		String tel3 = req.getParameter("tel3");
		if (tel1 != null && tel2 != null && tel3 != null && tel1.length() != 0 && tel2.length() != 0
				&& tel3.length() != 0) {
			dto.setPhoneNum(tel1 + "-" + tel2 + "-" + tel3);
		}

		int result = dao.insertMember(dto);
		if (result == 0) {
			// ȸ������ ����
			String msg = "ȸ�����Կ� �����߽��ϴ�";

			req.setAttribute("mode", "created");
			req.setAttribute("message", msg);

			forward(req, resp, "/WEB-INF/views/member/memberM.jsp");
			return;
		}

		// ȸ������ ����
		String msg = "<b>" + dto.getUserName() + "</b>�� ȸ��(����)�� �Ȱ��� �����մϴ�!<br> �ٽ��ѹ� �α������ּ���<br>";

		req.setAttribute("message", msg);
		forward(req, resp, "/WEB-INF/views/member/complete.jsp");
	}

	private void pwdForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// �н����� Ȯ�� ��
		HttpSession session = req.getSession();
		String cp = req.getContextPath();

		SessionInfo info = (SessionInfo) session.getAttribute("member");
		if (info == null) {
			// �α׾ƿ������̸�
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}

		String mode = req.getParameter("mode");
		String roll = req.getParameter("roll");
		if (mode.equals("update"))
			req.setAttribute("title", "ȸ�� ���� ����");
		else
			req.setAttribute("title", "ȸ�� Ż��");

		req.setAttribute("mode", mode);
		req.setAttribute("roll", roll);
		forward(req, resp, "/WEB-INF/views/member/pwd.jsp");
	}

	private void pwdSubmitC(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// �н����� Ȯ��
		HttpSession session = req.getSession();
		String cp = req.getContextPath();
		CompanyDAO dao = new CompanyDAO();

		SessionInfo info = (SessionInfo) session.getAttribute("member");
		if (info == null) { // �α׾ƿ� �� ���
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}

		// DB���� �ش� ȸ�� ���� ��������
		CompanyDTO dto = dao.readCompany(info.getUserId());

		if (dto == null) {
			session.invalidate();
			resp.sendRedirect(cp);
			return;
		}

		String comPwd = req.getParameter("userPwd");
		String mode = req.getParameter("mode");
		if (!dto.getPassword().equals(comPwd)) {
			if (mode.equals("update"))
				req.setAttribute("title", "ȸ�� ���� ����");
			else
				req.setAttribute("title", "ȸ�� Ż��");

			req.setAttribute("mode", mode);
			req.setAttribute("message", "<span style='color:red;'>�н����尡 ��ġ���� �ʽ��ϴ�.</span>");
			forward(req, resp, "/WEB-INF/views/member/pwd.jsp");
			return;
		}

		if (mode.equals("delete")) {
			// ȸ��Ż��
			dao.deleteCompany(info.getUserId());

			session.removeAttribute("member");
			session.invalidate();

			StringBuffer sb = new StringBuffer();
			sb.append("<b>" + dto.getComName() + "</b>�� ȸ��Ż�� ����ó���Ǿ����ϴ�.<br>");
			sb.append("�׵��� �̿��� �ּż� �����մϴ�.<br>");

			req.setAttribute("title", "ȸ�� Ż��");
			req.setAttribute("message", sb.toString());

			forward(req, resp, "/WEB-INF/views/member/complete.jsp");

			return;
		}

		// ȸ����������
		if (dto.getEmail() != null) {
			String[] s = dto.getEmail().split("@");
			if (s.length == 2) {
				dto.setEmail1(s[0]);
				dto.setEmail2(s[1]);
			}
		}

		if (dto.getPhoneNum() != null) {
			String[] s = dto.getPhoneNum().split("-");
			if (s.length == 3) {
				dto.setTel1(s[0]);
				dto.setTel2(s[1]);
				dto.setTel3(s[2]);
			}
		}

		// ȸ������������ �̵�
		req.setAttribute("title", "ȸ�� ���� ����");
		req.setAttribute("dto", dto);
		req.setAttribute("mode", "update");

		forward(req, resp, "/WEB-INF/views/member/memberC.jsp");
	}

	private void pwdSubmitM(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// �н����� Ȯ��
		HttpSession session = req.getSession();
		String cp = req.getContextPath();
		MemberDAO dao = new MemberDAO();

		SessionInfo info = (SessionInfo) session.getAttribute("member");
		if (info == null) { // �α׾ƿ� �� ���
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}

		// DB���� �ش� ȸ�� ���� ��������
		MemberDTO dto = dao.readMember(info.getUserId());
		if (dto == null) {
			session.invalidate();
			resp.sendRedirect(cp);
			return;
		}

		String userPwd = req.getParameter("userPwd");
		String mode = req.getParameter("mode");
		if (!dto.getPassword().equals(userPwd)) {
			if (mode.equals("update"))
				req.setAttribute("title", "ȸ�� ���� ����");
			else
				req.setAttribute("title", "ȸ�� Ż��");

			req.setAttribute("mode", mode);
			req.setAttribute("message", "<span style='color:red;'>�н����尡 ��ġ���� �ʽ��ϴ�.</span>");
			forward(req, resp, "/WEB-INF/views/member/pwd.jsp");
			return;
		}

		dto.setBirth(dto.getBirth().substring(0, 10));

		if (mode.equals("delete")) {
			// ȸ��Ż��
			dao.deleteMember(info.getUserId());

			session.removeAttribute("member");
			session.invalidate();

			StringBuffer sb = new StringBuffer();
			sb.append("<b>" + dto.getUserName() + "</b>�� ȸ��Ż�� ����ó���Ǿ����ϴ�.<br>");
			sb.append("�׵��� �̿��� �ּż� �����մϴ�.<br>");

			req.setAttribute("title", "ȸ�� Ż��");
			req.setAttribute("message", sb.toString());

			forward(req, resp, "/WEB-INF/views/member/complete.jsp");

			return;
		}

		// ȸ����������
		if (dto.getEmail() != null) {
			String[] s = dto.getEmail().split("@");
			if (s.length == 2) {
				dto.setEmail1(s[0]);
				dto.setEmail2(s[1]);
			}
		}

		if (dto.getPhoneNum() != null) {
			String[] s = dto.getPhoneNum().split("-");
			if (s.length == 3) {
				dto.setTel1(s[0]);
				dto.setTel2(s[1]);
				dto.setTel3(s[2]);
			}
		}

		// ȸ������������ �̵�
		req.setAttribute("title", "ȸ�� ���� ����");
		req.setAttribute("dto", dto);
		req.setAttribute("mode", "update");

		forward(req, resp, "/WEB-INF/views/member/memberM.jsp");
	}

	private void updateSubmitC(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ȸ������ ���� �Ϸ�
		HttpSession session = req.getSession();
		String cp = req.getContextPath();
		CompanyDAO dao = new CompanyDAO();
		CompanyDTO dto = new CompanyDTO();

		SessionInfo info = (SessionInfo) session.getAttribute("member");
		if (info == null) { // �α׾ƿ� �� ���
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}
		
		String encType = "utf-8";
		int maxSize = 5 * 1024 * 1024;

		MultipartRequest mreq = new MultipartRequest(req, pathname, maxSize, encType, new DefaultFileRenamePolicy());

		if (mreq.getFile("upload") != null) {

			String saveFilename = mreq.getFilesystemName("upload");
			saveFilename = FileManager.doFilerename(pathname, saveFilename);

			dto.setLogoImage(saveFilename);
		}

		dto.setComId(mreq.getParameter("comId"));
		dto.setPassword(mreq.getParameter("comPwd"));
		dto.setComName(mreq.getParameter("comName"));
		dto.setZip(mreq.getParameter("zip"));
		dto.setAddress1(mreq.getParameter("addr1"));
		dto.setAddress2(mreq.getParameter("addr2"));

		String email1 = mreq.getParameter("email1");
		String email2 = mreq.getParameter("email2");
		if (email1 != null && email1.length() != 0 && email2 != null && email2.length() != 0) {
			dto.setEmail(email1 + "@" + email2);
		}

		String tel1 = mreq.getParameter("tel1");
		String tel2 = mreq.getParameter("tel2");
		String tel3 = mreq.getParameter("tel3");
		if (tel1 != null && tel2 != null && tel3 != null && tel1.length() != 0 && tel2.length() != 0
				&& tel3.length() != 0) {
			dto.setPhoneNum(tel1 + "-" + tel2 + "-" + tel3);
		}
		
		dao.updateCompany(dto);

		StringBuffer sb = new StringBuffer();
		sb.append("<b>" + dto.getComName() + "</b>�� ȸ�� ������ ���� �Ǿ����ϴ�.<br>");
		sb.append("����ȭ������ �̵� �Ͻñ� �ٶ��ϴ�.<br>");

		req.setAttribute("title", "ȸ�� ���� ����");
		req.setAttribute("message", sb.toString());

		forward(req, resp, "/WEB-INF/views/member/complete.jsp");
	}

	private void updateSubmitM(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ȸ������ ���� �Ϸ�
		HttpSession session = req.getSession();
		String cp = req.getContextPath();
		MemberDAO dao = new MemberDAO();

		SessionInfo info = (SessionInfo) session.getAttribute("member");
		if (info == null) { // �α׾ƿ� �� ���
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}

		MemberDTO dto = new MemberDTO();

		dto.setUserId(req.getParameter("userId"));
		dto.setPassword(req.getParameter("userPwd"));
		dto.setUserName(req.getParameter("userName"));
		dto.setBirth(req.getParameter("birth"));
		String email1 = req.getParameter("email1");
		String email2 = req.getParameter("email2");
		if (email1 != null && email1.length() != 0 && email2 != null && email2.length() != 0) {
			dto.setEmail(email1 + "@" + email2);
		}
		String tel1 = req.getParameter("tel1");
		String tel2 = req.getParameter("tel2");
		String tel3 = req.getParameter("tel3");
		if (tel1 != null && tel1.length() != 0 && tel2 != null && tel2.length() != 0 && tel3 != null
				&& tel3.length() != 0) {
			dto.setPhoneNum(tel1 + "-" + tel2 + "-" + tel3);
		}
		dto.setZip(req.getParameter("zip"));
		dto.setAddress1(req.getParameter("addr1"));
		dto.setAddress2(req.getParameter("addr2"));

		dao.updateMember(dto);

		StringBuffer sb = new StringBuffer();
		sb.append("<b>" + dto.getUserName() + "</b>�� ȸ�� ������ ���� �Ǿ����ϴ�.<br>");
		sb.append("����ȭ������ �̵� �Ͻñ� �ٶ��ϴ�.<br>");

		req.setAttribute("title", "ȸ�� ���� ����");
		req.setAttribute("message", sb.toString());

		forward(req, resp, "/WEB-INF/views/member/complete.jsp");
	}

	private void logout(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// �α׾ƿ�
		HttpSession session = req.getSession();
		String cp = req.getContextPath();

		// ���ǿ� ����� ������ �����.
		session.removeAttribute("member");

		// ���ǿ� ����� ��� ������ ����� ������ �ʱ�ȭ �Ѵ�.
		session.invalidate();

		// ��Ʈ�� �����̷�Ʈ
		resp.sendRedirect(cp);
	}

}
