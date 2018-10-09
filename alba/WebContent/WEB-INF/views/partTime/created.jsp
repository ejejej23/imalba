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
    function sendOk() {
        var f = document.boardForm;

    	var str = f.subject.value;
        if(!str) {
            alert("제목을 입력하세요. ");
            f.subject.focus();
            return;
        }

    	str = f.content.value;
        if(!str) {
            alert("내용을 입력하세요. ");
            f.content.focus();
            return;
        }
        
        str = f.applyStart.value;
    	str = str.trim();
        if(!str || !isValidDateFormat(str)) {
            alert("모집 시작일을 입력하세요[YYYY-MM-DD]. ");
            f.applyStart.focus();
            return;
        }
        
        str = f.applyEnd.value;
    	str = str.trim();
        if(!str || !isValidDateFormat(str)) {
            alert("모집 마감일을 입력하세요[YYYY-MM-DD]. ");
            f.applyEnd.focus();
            return;
        }
        
        //모집 시작일이 모집 마감일보다 선행되어야 함.
        var dateStart = $("#applyStart").val();
        var dateEnd = $("#applyEnd").val();
        
        var y = parseInt(dateStart.substr(0,4));
        var m = parseInt(dateStart.substr(5,2));
        var d = parseInt(dateStart.substr(8));
        var tempS = new Date(y,m-1,d);
        
        y = parseInt(dateEnd.substr(0,4));
        m = parseInt(dateEnd.substr(5,2));
        d = parseInt(dateEnd.substr(8));
        var tempE = new Date(y,m-1,d);

        var diff = (tempE-tempS);
        
        if(diff<=0){
        	alert("모집 시작일이 모집 마감일 이전이어야 합니다.");
            f.applyEnd.focus();
            return;
        }
        
    	var mode="${mode}";
    	if(mode=="created")
    		f.action="<%=cp%>/partTime/created_ok.do";
    	else if(mode=="update")
    		f.action="<%=cp%>/partTime/update_ok.do";

        f.submit();
    }
    
    $(function(){
    	$("#applyStart").datepicker({
    		showMonthAfterYear:true
    		,minDate:0
    	});
    	
    	$("#applyEnd").datepicker({
    		showMonthAfterYear:true
    		,minDate:0
    	});
    });
    
    <c:if test="${mode=='update'}">
    function viewerImage(){

    	var url="<%=cp%>/uploads/photo/${dto.jobImage}"
    	var img="<img src='"+url+"' width='570' height='450'>"
    	$("#photoLayout").html(img);
    	
    	$("#photoDialog").dialog({
    		title:"이미지"
    		,width:600
    		,height:520
    		,modal:true
    	});
    }
    </c:if>
</script>
</head>
<body>

<div class="header">
    <jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</div>
	
<div class="container">
    <div class="body-container" style="width: 700px;">
        <div class="body-title">
            <h3><span style="font-family: Webdings">2</span> 게시판 </h3>
        </div>
        
        <div>
			<form name="boardForm" method="post" enctype="multipart/form-data">
			  <table style="width: 100%; margin: 20px auto 0px; border-spacing: 0px; border-collapse: collapse;">
			  <tr align="left" height="40" style="border-top: 1px solid #cccccc; border-bottom: 1px solid #cccccc;"> 
			      <td width="100" bgcolor="#eeeeee" style="text-align: center;">제&nbsp;&nbsp;&nbsp;&nbsp;목</td>
			      <td style="padding-left:10px;"> 
			          <input type="text" name="subject" maxlength="100" class="boxTF" style="width: 95%;" value="${dto.subject }">
			      </td>
			  </tr>

			  <tr align="left" height="40" style="border-bottom: 1px solid #cccccc;"> 
			      <td width="100" bgcolor="#eeeeee" style="text-align: center;">작성자</td>
			      <td style="padding-left:10px;"> 
			          ${sessionScope.member.userName }
			      </td>
			  </tr>
			  
			  <tr align="left" height="40" style="border-bottom: 1px solid #cccccc;"> 
			      <td width="100" bgcolor="#eeeeee" style="text-align: center;">모집 시작일</td>
			      <td style="padding-left:10px;"> 
			          <input type="text" name="applyStart" id="applyStart" maxlength="100" class="boxTF" style="width: 95%;" value="${dto.applyStart }">
			      </td>
			  </tr>
			  
			  <tr align="left" height="40" style="border-bottom: 1px solid #cccccc;"> 
			      <td width="100" bgcolor="#eeeeee" style="text-align: center;">모집 마감일</td>
			      <td style="padding-left:10px;"> 
			          <input type="text" name="applyEnd" id="applyEnd" maxlength="100" class="boxTF" style="width: 95%;" value="${dto.applyEnd }">
			      </td>
			  </tr>
			
			  <tr align="left" style="border-bottom: 1px solid #cccccc;"> 
			      <td width="100" bgcolor="#eeeeee" style="text-align: center; padding-top:5px;" valign="top">내&nbsp;&nbsp;&nbsp;&nbsp;용</td>
			      <td valign="top" style="padding:5px 0px 5px 10px;"> 
			          <textarea name="content" rows="12" class="boxTA" style="width: 95%;">${dto.content }</textarea>
			      </td>
			  </tr>
			  
			  <tr align="left" height="40" style="border-bottom: 1px solid #cccccc;">
			      <td width="100" bgcolor="#eeeeee" style="text-align: center;">첨&nbsp;&nbsp;&nbsp;&nbsp;부</td>
			      <td style="padding-left:10px;"> 
			          <input type="file" name="upload" class="boxTF" size="53" style="height: 25px;">
			       </td>
			  </tr> 
			  
			  <c:if test="${mode=='update' }">
				  <tr align="left" height="40" style="border-bottom: 1px solid #cccccc;">
					  <td width="100" bgcolor="#eeeeee" style="text-align: center;">등록된 이미지</td>
					  <td style="padding-left:10px;"> 
						   <img src="<%=cp%>/uploads/photo/${dto.jobImage}"
					      		width="30" height="30" onclick="viewerImage();" style="cursor: pointer;">
					  </td>
				  </tr>
			  </c:if>
			  
			  </table>
			
			  <table style="width: 100%; margin: 0px auto; border-spacing: 0px;">
			     <tr height="45"> 
			      <td align="center" >
			      	<c:if test="${mode=='update' }">
			      		<input type="hidden" name="num" value="${dto.ptNum }">
			      		<input type="hidden" name="page" value="${page }">
			      		<input type="hidden" name="jobImage" value="${dto.jobImage }">
			      	</c:if>
			      
			        <button type="button" class="btn" onclick="sendOk();">${mode=='update'?'수정완료':'등록하기'}</button>
			        <button type="reset" class="btn">다시입력</button>
			        <button type="button" class="btn" onclick="javascript:location.href='<%=cp%>/partTime/list.do';">${mode=='update'?'수정취소':'등록취소'}</button>

				  </td>
			    </tr>
			  </table>

					<div id="photoDialog">
						<div id="photoLayout"></div>
					</div>

				</form>
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