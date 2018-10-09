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
		var url="<%=cp%>/partTimeShort/delete.do?page=${page}&num="+num;
		location.href=url;
	}
}

//마감상태 변화시 체크메시지
function doneChk(num,done){
	var msg = done == 0? "모집을 마감하시겠습니까?":"마감을 취소하시겠습니까?";
	
	if(confirm(msg)) {
		var url="<%=cp%>/partTimeShort/doneChk.do?${query }&page=${page}&num="+num+"&done="+done;
		location.href=url;
	}
}

//마감여부 표시
$(function(){
	var isVisible = ${dto.done}
	
	if(isVisible==0){
		$("#doneShow").hide();
	}else{
		$("#doneShow").show();
	}
});

$(function(){
	listPage(1);
});

function listPage(page){
	   var url = "<%=cp%>/partTimeShort/listReply.do";
	   var query="num=${dto.ptsNum}&pageNo="+page;
	   $.ajax({
	      type: "post",
	      url : url,
	      data : query,
	      success : function(data){
	         $("#listReply").html(data);
	      },
	      beforeSend : function(jqXHR){
	         jqXHR.setRequestHeader("AJAX", true);
	      },
	      error : function(e){
	         if(e.status==403){
	            location.href="<%=cp%>/member/login.do";
	            return;
	         }
	         console.log(e.responseText);
	      }
	   });
}

$(function(){
	$(".btnSendReply").click(function(){
		var userRoll = ${sessionScope.member.userRoll};
		
		if(userRoll == 2){
			alert("기업회원 계정은 댓글을 작성할 수 없습니다");
			return;
		}
		
		var num = "${dto.ptsNum}";
		var $tb = $(this).closest("table");
		var content = $tb.find("textarea").val().trim();
		
		if(!content){
			$tb.find("textarea").focus();
			return;
		}
		content = encodeURIComponent(content);
		
		var query = "num="+num+"&content="+content;
		var url = "<%=cp%>/partTimeShort/insertReply.do";
		
		$.ajax({
			type:"post"
			,url:url
			,data:query
			,dataType:"json"
			,success:function(data){
				$tb.find("textarea").val("");
	            
	            var state = data.state;
	            if(state =="true"){
	               listPage(1);
	            }

			}
			,beforeSend:function(jqXHR){
				jqXHR.setRequestHeader("AJAX",true);
			}
			,error:function(jqXHR){
				if(jqXHR.status==403){
					location.href="<%=cp%>/member/login.do";
					return;
				}
				console.log(jqXHR.responseText);
			}
		});
		
	});
});

$(function(){
	
	$("body").on("click",".deleteReply",function(){
		if(!confirm("댓글을 삭제하시겠습니까?"))
			return;
		
		var replyNum=$(this).attr("data-replyNum");
		var page = $(this).attr("data-pageNo");
		
		var url = "<%=cp%>/partTimeShort/deleteReply.do";
		
		$.ajax({
			type:"post"
			,url:url
			,data:{replyNum:replyNum}
			,dataType:"json"
			,success:function(data){
				listPage(page);
			}
			,beforeSend:function(jqXHR){
				jqXHR.setRequestHeader("AJAX",true);
			}
			,error:function(jqXHR){
				if(jqXHR.status==403){
					location.href="<%=cp%>/member/login.do";
					return;
				}
				console.log(jqXHR.responseText);
			}
		});
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
            <h3><span style="font-family: Webdings">2</span> [급구]단기알바 게시판 </h3>
        </div>
        
        <div>
			<table style="width: 100%; margin: 20px auto 0px; border-spacing: 0px; border-collapse: collapse;">
			<tr height="35" style="border-top: 1px solid #cccccc; border-bottom: 1px solid #cccccc;">
			    <td colspan="2" align="center">
				   ${dto.subject }&nbsp;&nbsp;
				   <span id="doneShow" style="display: none; color: white; background: green;">&nbsp;모집 완료&nbsp;</span>
			    </td>
			</tr>
			
			<tr height="35" style="border-bottom: 1px solid #cccccc;">
			    <td width="50%" align="left" style="padding-left: 5px;">
			       회사명 : ${dto.comName }
			    </td>
			    <td width="50%" align="right" style="padding-right: 5px;">
			        ${dto.created } | 조회 ${dto.hitCount }
			    </td>
			</tr>
			
			<tr style="border-bottom: 1px solid #cccccc;">
			  <td colspan="2" align="left" style="padding: 10px 5px;" valign="top" height="200">
			      ${dto.content }
			   </td>
			</tr>
			
			</table>
			
			<table style="width: 100%; margin: 0px auto 20px; border-spacing: 0px;">
			<tr height="45">
				<c:if test="${sessionScope.member.userId == dto.comId }">
				    <td width="300" align="left">
				    	  <button type="button" class="btn" onclick="doneChk('${dto.ptsNum}','${dto.done }');">${dto.done==0?'마감하기':'마감취소'}</button>
				          <button type="button" class="btn" onclick="javascript:location.href='<%=cp%>/partTimeShort/update.do?${query}&num=${dto.ptsNum }';">수정</button>
				          <button type="button" class="btn" onclick="deleteBoard('${dto.ptsNum}');">삭제</button>
				    </td>
				</c:if>
				
			    <td align="right">
			        <button type="button" class="btn" onclick="javascript:location.href='<%=cp%>/partTimeShort/list.do?${query }';">리스트</button>
			    </td>
			</tr>
			</table>
        </div>

		<div>
            <table style='width: 100%; margin: 15px auto 0px; border-spacing: 0px;'>
            <tr height='30'> 
	            <td align='left'>
	            	<span style='font-weight: bold;' >댓글쓰기</span><span> - 타인을 비방하거나 개인정보를 유출하는 글의 게시를 삼가 주세요.</span>
	            </td>
            </tr>
            <tr>
               <td style='padding:5px 5px 0px;'>
                    <textarea class='boxTA' style='width:99%; height: 70px;resize:none;'></textarea>
                </td>
            </tr>
            <tr>
               <td align='right'>
                    <button type='button' class='btn btnSendReply' style='padding:10px 20px;'>댓글 등록</button>
                </td>
            </tr>
            </table>
            
            <div id="listReply"></div>
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