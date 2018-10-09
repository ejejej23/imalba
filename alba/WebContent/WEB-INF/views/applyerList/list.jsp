<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
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

<link rel="stylesheet" href="<%=cp%>/resource/css/style.css" type="text/css">
<link rel="stylesheet" href="<%=cp%>/resource/css/layout.css" type="text/css">
<link rel="stylesheet" href="<%=cp%>/resource/jquery/css/smoothness/jquery-ui.min.css" type="text/css">
<link rel="shortcut icon" href="<%=cp%>/resource/images/faviconNa.ico">

<script type="text/javascript" src="<%=cp%>/resource/js/util.js"></script>
<script type="text/javascript" src="<%=cp%>/resource/jquery/js/jquery-1.12.4.min.js"></script>
<script type="text/javascript">
	function searchList() {
		var f=document.searchForm;
		f.submit();
	}
</script>
</head>
<body>

<div class="header">
    <jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</div>
	
<div class="container">
    <div class="body-container" style="width: 700px;">
        <div class="body-title">
            <h3><span style="font-family: Webdings">2</span> 지원자 리스트 </h3>
        </div>
        
        <div>
			<table style="width: 100%; margin: 20px auto 0px; border-spacing: 0px;">
			   <tr height="35">
			      <td align="left" width="50%">
			          ${dataCount }개(${page }/${total_page } 페이지)
			      </td>
			      <td align="right">
			          &nbsp;
			      </td>
			   </tr>
			</table>
			
			<table style="width: 100%; margin: 0px auto; border-spacing: 0px; border-collapse: collapse;">
				  <tr align="center" bgcolor="#eeeeee" height="35" style="border-top: 1px solid #cccccc; border-bottom: 1px solid #cccccc;"> 
				      <th width="60" style="color: #787878;">번호</th>
				      <th style="color: #787878;">공고이름</th>
				      <th width="100" style="color: #787878;">지원자 이름</th>
				      <th width="80" style="color: #787878;">나이</th>
				      <th width="100" style="color: #787878;">연락처</th>
				      <th width="80" style="color: #787878;">지원일</th>
				  </tr>
				 
				 <c:forEach var="dto" items="${list }">
				  <tr align="center" bgcolor="#ffffff" height="35" style="border-bottom: 1px solid #cccccc;"> 
				      <td>${dto.listNum }</td>
				      <td align="left" style="padding-left: 10px;">
				           <a href="#">${dto.subject }</a>
				      </td>
				      <td>${dto.userName }</td>
				      <td>${dto.age }</td>
				      <td>${dto.userTel }</td>
				      <td>${dto.applyDate }</td>
				  </tr>
				</c:forEach>
				</table>
				 
				<table style="width: 100%; margin: 0px auto; border-spacing: 0px;">
				   <tr height="35">
					<td align="center">
				        ${paging }
					</td>
				   </tr>
				</table>
			 
			<table style="width: 100%; margin: 10px auto; border-spacing: 0px;">
			   <tr height="40">
			      <td align="left" width="100">
			          <button type="button" class="btn" onclick="javascript:location.href='<%=cp%>/applyerList/list.do';">새로고침</button>
			      </td>
			      <td align="center">
			          <form name="searchForm" action="" method="post">
			              <select name="searchKey" class="selectField">
			                  <option value="subject">제목</option>
			                  <option value="content">내용</option>
			                  <option value="created">등록일</option>
			            </select>
			            <input type="text" name="searchValue" class="boxTF">
			            <button type="button" class="btn" onclick="searchList()">검색</button>
			        </form>
			      </td>
			      
			   </tr>
			</table>
        </div>

    </div>
</div>

<div class="footer">
    <jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
</div>

<script type="text/javascript" src="<%=cp%>/resource/jquery/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="<%=cp%>/resource/jquery/js/jquery.ui.datepicker-ko.js"></script>
</body>
</html>