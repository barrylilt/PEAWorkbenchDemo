<!DOCTYPE html>
<%@page import="com.saama.workbench.util.AppConstants"%>
<html>
   <head>
      <meta charset="utf-8">
      <meta http-equiv="X-UA-Compatible" content="IE=edge">
      <title>PEA Workbench</title>
      <%@ include file="i18n.jsp"%>
      <%@ include file="plugins.jsp"%>
      <!-- Tell the browser to be responsive to screen width -->
      <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
   </head>
   <style type="text/css">
	.boxNew {
        border-radius: 50%;
        max-width: 100%;
        height: 105px;
        background-color: #45C3B8;
        color: white;
        background-repeat: no-repeat;
        background-position: center;
        margin: auto;
        width: 113px;
    }
    .correct {
        background-image: url(/PEAWorkbench/images/pending.png);
    }
    .remove {
        background-image: url(/PEAWorkbench/images/notPending.png);
    }
    .overlay {
        background: rgba(0, 0, 0, .75);
        text-align: center;
        opacity: 0;
        width: 100%;
        height: 100%;
        -webkit-transition: all 0.3s ease-in-out;
        -moz-transition: all 0.3s ease-in-out;
        -o-transition: all 0.3s ease-in-out;
        -ms-transition: all 0.3s ease-in-out;
        transition: all 0.3s ease-in-out;
        z-index: 50;
        background: rgba(255, 255, 255, 1) !important;
        color: black;
        width: 100%;
        height: 105px;
    }
    .boxNew:hover .overlay {
        opacity: 0.6;
    }
    .search {
        position: relative;
        top: 25%;
    }
    .users-list>li {
        width: 33%;
    }
    
    .box-default .table>tbody>tr>td {
        line-height: 2.278571;
    }
    
    #datepicker {
        width: 180px;
        display: inline-block;
        float: none;
        margin-left: 10px;
    }
    .nav-tabs-custom>.nav-tabs>li.active {
        border-top-color: #b5bbc8;
    }
     /* #OPSO .highcharts-container {
        width: 853px !important;
    }  */ 
	</style>
   <body class="hold-transition wysihtml5-supported sidebar-mini" ng-app="PEAWorkbench" >
      <div class="wrapper">
         <%@ include file="/header.html" %>
         <!-- Left side column. contains the logo and sidebar -->
         <div ng-controller="sideMenuController">
         	<%@ include file="/sideMenu.html"%>
         </div>
         <!-- Content Wrapper. Contains page content -->
         <div class="content-wrapper"   >
         	<div ng-view >
         	</div>
         	
         </div><!-- /.content-wrapper -->
           <%@ include file="/footer.html" %>
           <%@ include file="/settingsMenu.html"%>
      </div><!-- ./wrapper -->
      <%-- <div><!-- /setting-menu -->
           <%@ include file="/settingsMenu.html" %>
      </div><!-- ./wrapper --> --%>
     
      <input type="hidden" id="defaultDateFormat" 	value="${sessionScope.dateFormatUI}">
      <input type="hidden" id="i18nLang" 			value="${sessionScope.UserObject.language}">
      <input type="hidden" id="contextPath" 		value="<%=request.getContextPath()%>">
      <input type="hidden" id="theme" 				value="<%=(String)request.getAttribute("theme")%>">
      <input type="hidden" id="logoutURL" 			value="${sessionScope.logoutURL}">
      
      <span  id="sessionScope" style="display:none;" >${sessionScope.LDAPGROUP}</span>
      <span  id="userGroups" style="display:none;" >${sessionScope.LDAPGROUP}</span>
      <span  id="groupName" style="display:none;" ><%=AppConstants.GROUPNAME%></span>
      
   </body>
   <script>
   $("body").addClass($('#theme').val());
   </script>
</html>