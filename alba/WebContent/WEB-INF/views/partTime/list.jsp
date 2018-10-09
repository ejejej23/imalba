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
            <h3><span style="font-family: Webdings">2</span> 알바 게시판 </h3>
        </div>
        
        <div>

			<table style="width: 100%; margin: 20px auto 0px; border-spacing: 0px;">
			   <tr height="35">
			      <td align="left" width="50%">
			          ${dataCount}개(${page}/${total_page} 페이지)
			      </td>
			      <td align="right">
			          &nbsp;
			      </td>
			   </tr>
			</table>
			
			<table style="width: 100%; margin: 0px auto; border-spacing: 0px; border-collapse: collapse;">
			  
			  <c:forEach var="dto" items="${list }" varStatus="status">
				<c:if test="${status.index==0}">
					<tr>
				</c:if>
				<c:if test="${status.index!=0 && status.index%3==0}">
					<c:out value="</tr><tr>" escapeXml="false" />
				</c:if>
				<td width="210" height="250" align="center">
					<div class="imgLayout">
						<img src="<%=cp%>/uploads/photo/${dto.logoImage}" style="cursor:pointer;"
							width="180" height="180" border="0" onclick="javascript:location.href='${articleUrl}&num=${dto.ptNum}'"><br>
							<span style="color: white; background: yellowgreen; height:20px; width:170px; display: inline-block;">${dto.comName }</span><br>
							 <span class="subject"
							onclick="javascript:location.href='${articleUrl}&num=${dto.ptNum}'">${dto.subject}</span>
					</div>
				</td>

			</c:forEach>
			<c:set var="n" value="${list.size()}"></c:set>
			<c:if test="${n>0 && n%3!=0}">
				<c:forEach var="i" begin="${n%3+1}" end="3" step="1">
					<td width="210">
						<div class="imgLayout">&nbsp;</div>
					</td>
				</c:forEach>
			</c:if>

			<c:if test="${n != 0}">
				<c:out value="</tr>" escapeXml="false" />
			</c:if>
			  
			  
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
			          <button type="button" class="btn" onclick="javascript:location.href='<%=cp%>/partTime/list.do';">새로고침</button>
			      </td>
			      <td align="center">
			          <form name="searchForm" action="" method="post">
			              <select name="searchKey" class="selectField">
			                  <option value="subject">제목</option>
			                  <option value="comName">작성자</option>
			                  <option value="content">내용</option>
			                  <option value="created">등록일</option>
			            </select>
			            <input type="text" name="searchValue" class="boxTF">
			            <button type="button" class="btn" onclick="searchList()">검색</button>
			        </form>
			      </td>
			      <c:if test="${sessionScope.member.userRoll ==2 || sessionScope.member.userRoll ==0}">
			      <td align="right" width="100">
			          <button type="button" class="btn" onclick="javascript:location.href='<%=cp%>/partTime/created.do?page=${page }';">글쓰기</button>
			      </td>
			      </c:if>
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