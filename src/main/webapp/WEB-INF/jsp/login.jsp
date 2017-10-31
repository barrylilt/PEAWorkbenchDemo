<%@page import="org.springframework.security.core.context.SecurityContextHolder"%>
<%@page import="com.saama.workbench.util.PEAUtils"%>
<%@page import="com.saama.workbench.util.AppConstants"%>
<%@page import="com.saama.workbench.util.PropertiesUtil"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <title>Login</title>
<link rel="icon" href="images/SaamaLogo_sm.png" type="image/jpg">
    <!-- Bootstrap -->
    <link href="http://maxcdn.bootstrapcdn.com/bootswatch/3.3.4/flatly/bootstrap.min.css" rel="stylesheet">
    <link href="http://cdn.jsdelivr.net/flat-ui/2.2.2/css/flat-ui.min.css" rel="stylesheet">
    
    <style>
		input:-webkit-autofill {
		    -webkit-box-shadow: 0 0 0px 1000px white inset;
		}
		input:-webkit-autofill,
		input:-webkit-autofill:hover,
		input:-webkit-autofill:focus,
		input:-webkit-autofill:active {
		    transition: background-color 5000s ease-in-out 0s;
		}
       html, body, .container {
            height: 100%;
            width: 100%;
        }
        .container {
            display: table;
            vertical-align: inherit;
        }
        .col-center-block {
            float: none;
            display: block;
            margin-left: auto;
            margin-right: auto;
        }
        .vertical-center-row {
            display: table-cell;
            vertical-align: middle;
        }
        
        .vs-screen-bg {
            background-color: #808B96;
        } 
		.login-help{
			color:red;
			font-size:14px;
			padding-left:1%;
		}
    </style>
   
</head>
<body class="vs-screen-bg">
<%if (PEAUtils.convertToBoolean(PropertiesUtil.getProperty(AppConstants.SAML_ENABLED))) { %>
		<%if (SecurityContextHolder.getContext().getAuthentication() == null) { %> 
			<script>window.location.href = "login";</script>
		<%} else {%>
			<div id="inset_form"></div>
			<script>
				document.getElementById('inset_form').innerHTML = '<form action="login" name="form" method="post" style="display:none;"></form>';
				document.forms['form'].submit();
			</script>
		<%} %>
	
	<%} else { %>
    <div class="container">
        <div class="row vertical-center-row">
            <div class="col-xs-12 col-sm-8 col-md-6 col-center-block">
                <div class="row">
                    <div class="col-sm-1 col-md-2">
                        
                    </div>
                    <div class="col-sm-8 col-md-7">
                        <form class="login-form" action="login" method="POST">
                        <div class="text-center">
                            <div class="vs-big-font"><img src="images/saama_logo.png" style="width: 100px;height: 33px;"></div>
                            <h6>PEA Workbench</h6>
                        </div>
                            <div class="form-group">
                                <input type="text" class="form-control login-field" name="user" ng-model="user" value="" placeholder="Username">
                                <label class="login-field-icon fui-user" for="login-name"></label>
                            </div>

                            <div class="form-group">
                                <input type="password" class="form-control login-field" name="password" ng-model="password" value="" placeholder="Password">
                                <label class="login-field-icon fui-lock" for="login-pass"></label>
                            </div>

                            <button type="submit" class="btn btn-primary btn-lg btn-block">Login</button>
                            <span class="login-help">
								${sessionScope.invalidUser}
							</span>	
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
 <%}%>   

	<%-- <div class="log-form">
		<h2><img src="images/saama_logo.jpg" class="pull-left" style="width: 50px;height: 50px;margin-top: 0px;position:relative;" alt="logo">
		<label style="position: absolute;top: 16px;margin-left: 12px;">Login to Workbench</label></h2>
		<form action="login" method="POST">
			<input type="text" title="username" name="user" placeholder="Username" /> 
			<input type="password" title="username" name="password" placeholder="Password" />
			<button type="submit" class="login btn" name="login">Login</button>
		<span class="login-help">
			${sessionScope.invalidUser}
		</span>	
		</form>
	</div> --%>
	
	
</body>
</html>