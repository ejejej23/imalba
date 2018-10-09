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

		// 이미지를 저장할 경로
		// 이미지는 반드시 웹경로에 저장해야 img 태그로 볼 수 있음.
		String root = session.getServletContext().getRealPath("/");// 웹경로 루트의 실제 위치
		pathname = root + "uploads" + File.separator + "photo";
		File f = new File(pathname);
		if (!f.exists()) {// 저장경로의 폴더가 없으면 폴더 만들기
			f.mkdirs();
		}

		// uri에 따른 작업 구분
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
		// 로그인 폼
		String path = "/WEB-INF/views/member/login.jsp";
		forward(req, resp, path);
	}

	private void loginMSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 개인회원 로그인 처리
		HttpSession session = req.getSession();
		String cp = req.getContextPath();
		MemberDAO dao = new MemberDAO();

		String userId = req.getParameter("userId");
		String userPwd = req.getParameter("userPwd");

		MemberDTO dto = dao.readMember(userId);
		if (dto != null) {
			if (userPwd.equals(dto.getPassword()) && dto.getEnabled() == 1) {
				// 로그인 성공
				// 세션의 유지기간 설정 : 20분으로 설정함
				session.setMaxInactiveInterval(20 * 60);

				// 세션에 저장할 내용
				SessionInfo info = new SessionInfo();
				info.setUserId(dto.getUserId());
				info.setUserName(dto.getUserName());

				if (dto.getUserId().equals("admin")) {
					info.setUserRoll(0);// 관리자
				} else {
					info.setUserRoll(1);// 회원
				}

				session.setAttribute("member", info);

				// 메인화면으로 리다이렉트
				resp.sendRedirect(cp);
				return;
			}
		}

		// 로그인 실패
		String msg = "아이디 또는 패스워드가 일치하지 않습니다.";
		req.setAttribute("messageM", msg);

		String path = "/WEB-INF/views/member/login.jsp";
		forward(req, resp, path);

	}

	private void loginCSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 기업회원 로그인 처리
		HttpSession session = req.getSession();
		String cp = req.getContextPath();
		CompanyDAO dao = new CompanyDAO();

		String comId = req.getParameter("comId");
		String comPwd = req.getParameter("comPwd");

		CompanyDTO dto = dao.readCompany(comId);

		if (dto != null) {
			if (comPwd.equals(dto.getPassword()) && dto.getEnabled() == 1) {
				// 로그인 성공
				// 세션의 유지기간 설정 : 20분으로 설정함
				session.setMaxInactiveInterval(20 * 60);

				// 세션에 저장할 내용
				SessionInfo info = new SessionInfo();
				info.setUserId(dto.getComId());
				info.setUserName(dto.getComName());
				if (dto.getComId().equals("admin")) {
					info.setUserRoll(0);// 관리자
				} else {
					info.setUserRoll(2);// 업체
				}

				session.setAttribute("member", info);

				// 메인화면으로 리다이렉트
				resp.sendRedirect(cp);
				return;
			}
		}

		// 로그인 실패
		String msg = "아이디 또는 패스워드가 일치하지 않습니다.";
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
		// 기업회원가입

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
			// 회원가입 실패
			String msg = "회원가입에 실패했습니다";

			req.setAttribute("mode", "created");
			req.setAttribute("message", msg);

			forward(req, resp, "/WEB-INF/views/member/memberC.jsp");
			return;
		}

		// 회원가입 성공
		String msg = "<b>" + dto.getComName() + "</b>님 회원(기업)이 된것을 축하합니다!<br> 다시한번 로그인해주세요<br>";

		req.setAttribute("message", msg);
		forward(req, resp, "/WEB-INF/views/member/complete.jsp");
	}

	private void joinSubmitM(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 개인회원가입
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
			// 회원가입 실패
			String msg = "회원가입에 실패했습니다";

			req.setAttribute("mode", "created");
			req.setAttribute("message", msg);

			forward(req, resp, "/WEB-INF/views/member/memberM.jsp");
			return;
		}

		// 회원가입 성공
		String msg = "<b>" + dto.getUserName() + "</b>님 회원(개인)이 된것을 축하합니다!<br> 다시한번 로그인해주세요<br>";

		req.setAttribute("message", msg);
		forward(req, resp, "/WEB-INF/views/member/complete.jsp");
	}

	private void pwdForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 패스워드 확인 폼
		HttpSession session = req.getSession();
		String cp = req.getContextPath();

		SessionInfo info = (SessionInfo) session.getAttribute("member");
		if (info == null) {
			// 로그아웃상태이면
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}

		String mode = req.getParameter("mode");
		String roll = req.getParameter("roll");
		if (mode.equals("update"))
			req.setAttribute("title", "회원 정보 수정");
		else
			req.setAttribute("title", "회원 탈퇴");

		req.setAttribute("mode", mode);
		req.setAttribute("roll", roll);
		forward(req, resp, "/WEB-INF/views/member/pwd.jsp");
	}

	private void pwdSubmitC(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 패스워드 확인
		HttpSession session = req.getSession();
		String cp = req.getContextPath();
		CompanyDAO dao = new CompanyDAO();

		SessionInfo info = (SessionInfo) session.getAttribute("member");
		if (info == null) { // 로그아웃 된 경우
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}

		// DB에서 해당 회원 정보 가져오기
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
				req.setAttribute("title", "회원 정보 수정");
			else
				req.setAttribute("title", "회원 탈퇴");

			req.setAttribute("mode", mode);
			req.setAttribute("message", "<span style='color:red;'>패스워드가 일치하지 않습니다.</span>");
			forward(req, resp, "/WEB-INF/views/member/pwd.jsp");
			return;
		}

		if (mode.equals("delete")) {
			// 회원탈퇴
			dao.deleteCompany(info.getUserId());

			session.removeAttribute("member");
			session.invalidate();

			StringBuffer sb = new StringBuffer();
			sb.append("<b>" + dto.getComName() + "</b>님 회원탈퇴가 정상처리되었습니다.<br>");
			sb.append("그동안 이용해 주셔서 감사합니다.<br>");

			req.setAttribute("title", "회원 탈퇴");
			req.setAttribute("message", sb.toString());

			forward(req, resp, "/WEB-INF/views/member/complete.jsp");

			return;
		}

		// 회원정보수정
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

		// 회원수정폼으로 이동
		req.setAttribute("title", "회원 정보 수정");
		req.setAttribute("dto", dto);
		req.setAttribute("mode", "update");

		forward(req, resp, "/WEB-INF/views/member/memberC.jsp");
	}

	private void pwdSubmitM(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 패스워드 확인
		HttpSession session = req.getSession();
		String cp = req.getContextPath();
		MemberDAO dao = new MemberDAO();

		SessionInfo info = (SessionInfo) session.getAttribute("member");
		if (info == null) { // 로그아웃 된 경우
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}

		// DB에서 해당 회원 정보 가져오기
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
				req.setAttribute("title", "회원 정보 수정");
			else
				req.setAttribute("title", "회원 탈퇴");

			req.setAttribute("mode", mode);
			req.setAttribute("message", "<span style='color:red;'>패스워드가 일치하지 않습니다.</span>");
			forward(req, resp, "/WEB-INF/views/member/pwd.jsp");
			return;
		}

		dto.setBirth(dto.getBirth().substring(0, 10));

		if (mode.equals("delete")) {
			// 회원탈퇴
			dao.deleteMember(info.getUserId());

			session.removeAttribute("member");
			session.invalidate();

			StringBuffer sb = new StringBuffer();
			sb.append("<b>" + dto.getUserName() + "</b>님 회원탈퇴가 정상처리되었습니다.<br>");
			sb.append("그동안 이용해 주셔서 감사합니다.<br>");

			req.setAttribute("title", "회원 탈퇴");
			req.setAttribute("message", sb.toString());

			forward(req, resp, "/WEB-INF/views/member/complete.jsp");

			return;
		}

		// 회원정보수정
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

		// 회원수정폼으로 이동
		req.setAttribute("title", "회원 정보 수정");
		req.setAttribute("dto", dto);
		req.setAttribute("mode", "update");

		forward(req, resp, "/WEB-INF/views/member/memberM.jsp");
	}

	private void updateSubmitC(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 회원정보 수정 완료
		HttpSession session = req.getSession();
		String cp = req.getContextPath();
		CompanyDAO dao = new CompanyDAO();
		CompanyDTO dto = new CompanyDTO();

		SessionInfo info = (SessionInfo) session.getAttribute("member");
		if (info == null) { // 로그아웃 된 경우
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
		sb.append("<b>" + dto.getComName() + "</b>님 회원 정보가 수정 되었습니다.<br>");
		sb.append("메인화면으로 이동 하시기 바랍니다.<br>");

		req.setAttribute("title", "회원 정보 수정");
		req.setAttribute("message", sb.toString());

		forward(req, resp, "/WEB-INF/views/member/complete.jsp");
	}

	private void updateSubmitM(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 회원정보 수정 완료
		HttpSession session = req.getSession();
		String cp = req.getContextPath();
		MemberDAO dao = new MemberDAO();

		SessionInfo info = (SessionInfo) session.getAttribute("member");
		if (info == null) { // 로그아웃 된 경우
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
		sb.append("<b>" + dto.getUserName() + "</b>님 회원 정보가 수정 되었습니다.<br>");
		sb.append("메인화면으로 이동 하시기 바랍니다.<br>");

		req.setAttribute("title", "회원 정보 수정");
		req.setAttribute("message", sb.toString());

		forward(req, resp, "/WEB-INF/views/member/complete.jsp");
	}

	private void logout(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 로그아웃
		HttpSession session = req.getSession();
		String cp = req.getContextPath();

		// 세션에 저장된 정보를 지운다.
		session.removeAttribute("member");

		// 세션에 저장된 모든 정보를 지우고 세션을 초기화 한다.
		session.invalidate();

		// 루트로 리다이렉트
		resp.sendRedirect(cp);
	}

}
