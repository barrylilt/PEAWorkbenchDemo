
<!-- Bootstrap 3.3.5 -->
<link rel="stylesheet" href="bootstrap/css/bootstrap.min.css">
<!-- Font Awesome -->
<link rel="stylesheet" href="dist/css/font-awesome.min.css">

<!-- Open Sans -->
<link rel="stylesheet" type="text/css" href="//fonts.googleapis.com/css?family=Open+Sans" />

<!-- Montserrat -->
<link rel="stylesheet" type="text/css" href="//fonts.googleapis.com/css?family=Montserrat" />

<!-- Ionicons -->
<link rel="stylesheet" href="dist/css/ionicons.min.css">
<!-- Select2 -->
<link rel="stylesheet" href="plugins/select2/select2.min.css">
<!-- Theme style -->
<link rel="stylesheet" href="dist/css/AdminLTE.min.css">
<!-- AdminLTE Skins. Choose a skin from the css/skins
         folder instead of downloading all of them to reduce the load. -->
<link rel="stylesheet" href="dist/css/skins/_all-skins.min.css">
<!-- iCheck -->
<link rel="stylesheet" href="plugins/iCheck/flat/blue.css">
<!-- Morris chart -->
<link rel="stylesheet" href="plugins/morris/morris.css">
<!-- jvectormap -->
<!-- <link rel="stylesheet" href="plugins/jvectormap/jquery-jvectormap-1.2.2.css"> -->
<!-- Date Picker -->
<link rel="stylesheet" href="plugins/datepicker/datepicker3.css">
<!-- Daterange picker -->
<link rel="stylesheet"
	href="plugins/daterangepicker/daterangepicker-bs3.css">
<!-- bootstrap wysihtml5 - text editor -->
<link rel="stylesheet"
	href="plugins/bootstrap-wysihtml5/bootstrap3-wysihtml5.min.css">
<!-- DataTables -->
<link rel="stylesheet"
	href="plugins/datatables/dataTables.bootstrap.css">
<link rel="stylesheet" type="text/css" href="dist/css/bootstrap-dialog.min.css" />

<link rel="stylesheet" type="text/css" href="dist/css/main.css" />
<link rel="stylesheet" type="text/css" href="dist/css/multiple-select.css"/>
<link rel="stylesheet" href="dist/css/animate.css">

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
      <![endif]-->


<!-- jQuery 2.1.4 -->
<script src="plugins/jQuery/jQuery-2.1.4.min.js"></script>
<!-- jQuery UI 1.11.4 -->
<script src="plugins/jQueryUI/jquery-ui.min.js"></script>
<!-- Resolve conflict in jQuery UI tooltip with Bootstrap tooltip -->
<script>
	$.widget.bridge('uibutton', $.ui.button);
</script>
<!-- Bootstrap 3.3.5 -->
<script src="bootstrap/js/bootstrap.js"></script>
<script src="plugins/bootstrap-confirmation/bootstrap-confirmation.js"></script>
<!-- Sparkline -->
<script src="plugins/sparkline/jquery.sparkline.min.js"></script>
<!-- jvectormap -->
<script src="plugins/jvectormap/jquery-jvectormap-1.2.2.min.js"></script>
<script src="plugins/jvectormap/jquery-jvectormap-world-mill-en.js"></script>
<!-- jQuery Knob Chart -->
<script src="plugins/knob/jquery.knob.js"></script>
<!-- daterangepicker -->
<script src="plugins/moment/moment.min.js"></script>
<script src="plugins/daterangepicker/daterangepicker.js"></script>
<!-- datepicker -->
<script src="plugins/datepicker/bootstrap-datepicker.js"></script>
<!-- dialog -->
<script src="plugins/bootstrap-dialog/bootstrap-dialog.min.js"></script>
<!-- Bootstrap WYSIHTML5 -->
<script
	src="plugins/bootstrap-wysihtml5/bootstrap3-wysihtml5.all.min.js"></script>
<!-- Slimscroll -->
<script src="plugins/slimScroll/jquery.slimscroll.min.js"></script>
<!-- FastClick -->
<script src="plugins/fastclick/fastclick.min.js"></script>
<!-- DataTables -->
<script src="plugins/datatables/jquery.dataTables.min.js"></script>
<script src="plugins/datatables/dataTables.fixedColumns.min.js"></script>
<!-- <script src="//cdn.datatables.net/1.10.12/js/jquery.dataTables.js" ></script> -->

<script src="plugins/datatables/dataTables.bootstrap.min.js"></script>
<!-- Select2 -->
<script src="plugins/select2/select2.full.min.js"></script>
<!-- High chart -->
<script src="plugins/graph/highstock.js"></script>
<script src="plugins/graph/highcharts-more.js"></script>
<!-- angular js file -->
<!-- <script src="dist/js/angular/angular.js"></script> -->
<script src="dist/js/angular/angular.min.js"></script>
<script src="dist/js/angular/angular-route.min.js"></script>
<script src="dist/js/angular/angular-animate.js"></script>
<script src="plugins/bootstrap-notify/bootstrap-notify.js"></script>
<script src="plugins/jQuery/jquery.highlight.js"></script>
<script src="plugins/jQuery/jquery.steps.js"></script>
<script src="plugins/datatables/dataTables.searchHighlight.min.js"></script>
<!-- <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/angular-datatables/0.5.4/plugins/scroller/angular-datatables.scroller.min.js"></script> -->

<script src="dist/js/angular/ui-bootstrap-tpls-1.3.2.js"></script>
<script src="dist/js/app.js"></script>
<script src="dist/js/demo.js"></script>
<script src="dist/js/angularApp.js"></script>
<script src="dist/js/angular/angular-datatables.js"></script>

<script src="dist/js/multiple-select.js"></script>

<link rel="stylesheet" href="dist/css/jquery/redmond/jquery-ui.min.css" />
<link rel="stylesheet" href="dist/css/basicprimitives/primitives.latest.css">
<script type="text/javascript" src="dist/js/basicprimitives/primitives.min.js"></script>
<script type="text/javascript" src="dist/js/jquery/jquery-ui-1.10.2.custom.js"></script>


<!-- Controller  -->
<script src="scripts/controllers/peaWorkbenchControllers.js"></script>
<script src="scripts/controllers/manageDataControllers.js"></script>
<script src="scripts/controllers/viewDataControllers.js"></script>
<script src="scripts/controllers/manageDatasetsControllers.js"></script>
<script>
	$.fn.dataTable.ext.errMode = 'none';
	$.fn.forceNumericOnly = function() {
			function chkSingleDot(value) {
				return (value.indexOf('.') < 0);
			}
		    return this.each(function() {
		        $(this).keydown(function(e) {
		            var key = e.charCode || e.keyCode || 0;
		            // allow backspace, tab, delete, enter, arrows, numbers and keypad numbers ONLY
		            // home, end, period, and numpad decimal
		            return (
		                key == 8 || //Backspace
		                key == 9 || //Tab
		                key == 13 || //Enter
		                key == 46 || //Delete
		                key == 110 || // 
		                (key == 190 && chkSingleDot(this.value))|| // Dot
		                (key >= 35 && key <= 40) || // Arrows
		                (key >= 48 && key <= 57) || // Numbers
		                (key >= 96 && key <= 105)); // Keypad Numbers
		        });
		    });
		};
</script>
