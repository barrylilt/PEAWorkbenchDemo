<!DOCTYPE html>
<html lang="en">
  <!--  head starts here -->
	<head>
		<meta charset="utf-8">
		<title>PEA Workbench </title>
		<link rel="icon" href="images/SaamaLogo_sm.png" type="image/jpg">
		<!-- fonts added here -->
		<!-- <link href='http://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800' rel='stylesheet' type='text/css'>
	
		scripts added here
		<script src="js/lib/jquery/jquery-2.1.4.min.js"></script>
		<script src="js/lib/jquery/jquery-ui.js"></script>
		<script src="js/lib/dataTable/jquery.dataTables.min.js"></script>
		<script src="js/lib/bootstrap/bootstrap.3.3.5.min.js"></script>
		<script src="js/lib/bootstrap/run_prettify.min.js"></script>
		<script src="js/lib/bootstrap/bootstrap-dialog.min.js"></script>
		<script src="js/lib/underscore-min.1.8.3.js"></script>
		
		<script src="js/tabs.js"></script> -->
		
</head>
<!--  head ends here -->
<body>
    <input type="hidden" id="contextPath" value="<%=request.getContextPath()%>">
	<div id="appTable">
		<div id="mid-container">
			    <jsp:include page="home.jsp"/>
		</div>
		<div class="ajaxLoader">
			<img src="images/ajax-loader.gif" alt="Loader. . .">
			
		</div>
	</div>
</body>
</html>
