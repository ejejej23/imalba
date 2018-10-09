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
function deleteBoard(num) {
	if(confirm("게시물을 삭제 하시겠습니까 ?")) {
		var url="<%=cp%>/partTime/delete.do?page=${page}&num="+num;
		location.href=url;
	}
}

$(function(){
	$("#apply").click(function(){
		  var uid="${sessionScope.member.userId}";
		  
		  if(!uid){
			  alert("로그인이 필요합니다");
			  return;
		  }
		  
		  if(confirm("지원 하시겠습니까 ?")) {

			var query="page=${page}&num=${dto.ptNum}&comId=${dto.comId}";
		  	var url="<%=cp%>/applyList/apply.do"
		  
			  $.ajax({
				 type:"post"
				 ,url:url
				 ,data:query
				 ,dataType:"json"
				 ,success:function(data){
					 var state=data.state;
					 if(state=="loginFail"){
						 location.href="<%=cp%>/member/login.do";
						 return;
					 }
					 if(state=="done"){
						 $("#msg").text(" 이미 지원하셨습니다");
						 return;
					 }
					 $("#msg").text(" 지원 완료!");
				 }
			  	 ,error:function(e){
			  		 console.log(e.responseText);
			  	 }
			  });
		  }
	});
});

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
			<table style="width: 100%; margin: 20px auto 0px; border-spacing: 0px; border-collapse: collapse;">
			<tr height="35" style="border-top: 1px solid #cccccc; border-bottom: 1px solid #cccccc;">
			    <td colspan="2" align="center">
				   ${dto.subject }
			    </td>
			</tr>
			
			<tr height="35" style="border-bottom: 1px solid #e7e7e7; background: #e7e7e7;">
			    <td width="50%" align="left" style="padding-left: 5px;">
			       회사명 : ${dto.comName } 
			    </td>
			    <td width="50%" align="right" style="padding-right: 5px;">
			        ${dto.created } | 조회 ${dto.hitCount }
			    </td>
			</tr>
			
			<tr height="35" style="border-bottom: 1px solid #e7e7e7; background: #e7e7e7;">
			    <td colspan="2" align="left" style="padding-left: 5px;">
			       담당자 연락처 : 
			       ${dto.comTel } | ${dto.comEmail }
			    </td>
			</tr>
			
			<tr height="35" style="border-bottom: 1px solid #e7e7e7; background: #e7e7e7;">
			    <td colspan="2" align="left" style="padding-left: 5px;">
			       주소 : 
			       ${dto.comZip } | ${dto.comAddress1 } &nbsp; ${dto.comAddress2 }
			    </td>
			</tr>
			
			
			
			
			
			
			
			<tr>
				<td colspan="2" align="left" style="padding:10px 5px; text-align: center;">
					<c:if test="${dto.jobImage!=null }">
						<img src="<%=cp%>/uploads/photo/${dto.jobImage}" style="max-width: 100%; height:auto; resize:both;">
					</c:if>
				</td>
			</tr>
			<tr style="border-bottom: 1px solid #cccccc;">
			  <td colspan="2" align="left" style="padding: 50px 5px;" valign="top" height="200">
			      ${dto.content }
			   </td>
			</tr>
			
			<tr height="35" style="border-bottom: 1px solid #cccccc;">
			    <td colspan="2" align="left" style="padding-left: 5px;">
			       <div style=" display: inline-block;">
			       		<span> 모집기간 : ${dto.applyStart } - ${dto.applyEnd }</span>
			      		<span id="msg" style="color: red; font-weight: bold;"></span>
			       </div>
			       		
			       <c:if test="${sessionScope.member.userRoll == 1 && apply=='ok'}">
		           		<div style="float: right; display: inline-block;"><button id="apply" class="btn" >지원하기</button></div>
		           </c:if>
			    </td>
			</tr>

			<tr height="35" style="border-bottom: 1px solid #cccccc;">
			    <td colspan="2" align="left" style="padding-left: 5px;">
			       이전글 :
			       <a href="article.do?${query }&num=${preReadDto.ptNum}">${preReadDto.subject }</a>
			    </td>
			</tr>
			
			<tr height="35" style="border-bottom: 1px solid #cccccc;">
			    <td colspan="2" align="left" style="padding-left: 5px;">
			       다음글 :
			       <a href="article.do?${query }&num=${nextReadDto.ptNum}">${nextReadDto.subject }</a>
			    </td>
			</tr>
			</table>
			
			<table style="width: 100%; margin: 0px auto 20px; border-spacing: 0px;">
			<tr height="45">
			    <td width="300" align="left">
			    	<c:if test="${sessionScope.member.userName == dto.comName }">
			          <button type="button" class="btn" onclick="javascript:location.href='<%=cp%>/partTime/update.do?${query }&num=${dto.ptNum }';">수정</button>
			        </c:if>
			        <c:if test="${sessionScope.member.userId == dto.comId || sessionScope.member.userId == 'admin'}">
			          <button type="button" class="btn" onclick="deleteBoard('${dto.ptNum}');">삭제</button>
			        </c:if>
			    </td>
			
			    <td align="right">
			        <button type="button" class="btn" onclick="javascript:location.href='<%=cp%>/partTime/list.do?${query}';">리스트</button>
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