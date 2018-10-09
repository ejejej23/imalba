<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%
	String cp = request.getContextPath();
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>나는알바</title>

<link rel="stylesheet" href="<%=cp%>/resource/css/style.css"
	type="text/css">
<link rel="stylesheet" href="<%=cp%>/resource/css/layout.css"
	type="text/css">
<link rel="stylesheet"
	href="<%=cp%>/resource/jquery/css/smoothness/jquery-ui.min.css"
	type="text/css">
<link rel="shortcut icon" href="<%=cp%>/resource/images/faviconNa.ico">

<style type="text/css">
.lbl {
	position: absolute;
	margin-left: 15px;
	margin-top: 17px;
	color: #999999;
	font-size: 11pt;
}

.loginTF {
	width: 340px;
	height: 35px;
	padding: 5px;
	padding-left: 15px;
	border: 1px solid #999999;
	color: #333333;
	margin-top: 5px;
	margin-bottom: 5px;
	font-size: 14px;
	border-radius: 4px;
}
.logins{
	padding: 20px;
	align-content: center;
	align-self: center;
	text-align: center;
}
</style>

<script type="text/javascript"
	src="<%=cp%>/resource/jquery/js/jquery-1.12.4.min.js"></script>
<script type="text/javascript" src="<%=cp%>/resource/js/util.js"></script>
<script type="text/javascript">
function bgLabel(ob, id) {
    if(!ob.value) {
	    document.getElementById(id).style.display="";
    } else {
	    document.getElementById(id).style.display="none";
    }
}

function sendLoginC() {
    var f = document.loginCForm;

	var str = f.comId.value;
    if(!str) {
        alert("아이디를 입력하세요. ");
        f.comId.focus();
        return;
    }

    str = f.comPwd.value;
    if(!str) {
        alert("패스워드를 입력하세요. ");
        f.comPwd.focus();
        return;
    }

    f.action = "<%=cp%>/member/loginC_ok.do";
		f.submit();
	}

function sendLoginM() {
    var f = document.loginMForm;

	var str = f.userId.value;
    if(!str) {
        alert("아이디를 입력하세요. ");
        f.userId.focus();
        return;
    }

    str = f.userPwd.value;
    if(!str) {
        alert("패스워드를 입력하세요. ");
        f.userPwd.focus();
        return;
    }

    f.action = "<%=cp%>/member/loginM_ok.do";
		f.submit();
	}
	
</script>

</head>
<body>

	<div class="header">
		<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
	</div>

	<div class="container" >
		<div style="text-align: center;">

			<div class="logins" style="margin: 80px auto 70px; width: 360px;">
				<div style="text-align: center;">
					<span style="font-weight: bold; font-size: 27px; color: #424951;">기업회원
						로그인</span>
				</div>

				<form name="loginCForm" method="post" action="">
					<table
						style="margin: 15px auto; width: 360px; border-spacing: 0px;">
						<tr align="center" height="60">
							<td><label for="comId" id="lblComId" class="lbl">아이디</label>
								<input type="text" name="comId" id="comId" class="loginTF"
								maxlength="15" tabindex="1"
								onfocus="document.getElementById('lblComId').style.display='none';"
								onblur="bgLabel(this, 'lblComId');"></td>
						</tr>
						<tr align="center" height="60">
							<td><label for="comPwd" id="lblComPwd" class="lbl">패스워드</label>
								<input type="password" name="comPwd" id="comPwd"
								class="loginTF" maxlength="20" tabindex="2"
								onfocus="document.getElementById('lblComPwd').style.display='none';"
								onblur="bgLabel(this, 'lblComPwd');"></td>
						</tr>
						<tr align="center" height="65">
							<td>
								<button type="button" onclick="sendLoginC();" style="background: green;" class="btnConfirm">로그인</button>
							</td>
						</tr>

						<tr align="center" height="45">
							<td><a href="<%=cp%>/">아이디찾기</a>&nbsp;&nbsp;&nbsp; <a
								href="<%=cp%>/">패스워드찾기</a>&nbsp;&nbsp;&nbsp; <a
								href="<%=cp%>/member/memberC.do">기업회원가입</a></td>
						</tr>

						<tr align="center" height="40">
							<td><span style="color: blue;">${messageC}</span></td>
						</tr>

					</table>


				</form>
			</div>

			<div class="logins" style="margin: 80px auto 70px; width: 360px;">
				<div style="text-align: center;">
					<span style="font-weight: bold; font-size: 27px; color: #424951;">회원
						로그인</span>
				</div>

				<form name="loginMForm" method="post" action="">
					<table
						style="margin: 15px auto; width: 360px; border-spacing: 0px;">
						<tr align="center" height="60">
							<td><label for="userId" id="lblUserId" class="lbl">아이디</label>
								<input type="text" name="userId" id="userId" class="loginTF"
								maxlength="15" tabindex="3"
								onfocus="document.getElementById('lblUserId').style.display='none';"
								onblur="bgLabel(this, 'lblUserId');"></td>
						</tr>
						<tr align="center" height="60">
							<td><label for="userPwd" id="lblUserPwd" class="lbl">패스워드</label>
								<input type="password" name="userPwd" id="userPwd"
								class="loginTF" maxlength="20" tabindex="4"
								onfocus="document.getElementById('lblUserPwd').style.display='none';"
								onblur="bgLabel(this, 'lblUserPwd');"></td>
						</tr>
						<tr align="center" height="65">
							<td>
								<button type="button" onclick="sendLoginM();" style="background: limegreen;"  class="btnConfirm">로그인</button>
							</td>
						</tr>

						<tr align="center" height="45">
							<td><a href="<%=cp%>/">아이디찾기</a>&nbsp;&nbsp;&nbsp; <a
								href="<%=cp%>/">패스워드찾기</a>&nbsp;&nbsp;&nbsp; <a
								href="<%=cp%>/member/memberM.do">개인회원가입</a></td>
						</tr>

						<tr align="center" height="40">
							<td><span style="color: blue;">${messageM}</span></td>
						</tr>

					</table>


				</form>
			</div>
		</div>
		
		
		
	</div>

	<div class="footer">
		<jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
	</div>

	<script type="text/javascript"
		src="<%=cp%>/resource/jquery/js/jquery-ui.min.js"></script>
	<script type="text/javascript"
		src="<%=cp%>/resource/jquery/js/jquery.ui.datepicker-ko.js"></script>
</body>
</html>