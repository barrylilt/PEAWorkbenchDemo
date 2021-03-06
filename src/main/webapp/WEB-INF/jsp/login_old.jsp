<%@page import="org.springframework.security.core.context.SecurityContextHolder"%>
<%@page import="com.saama.workbench.util.PEAUtils"%>
<%@page import="com.saama.workbench.util.AppConstants"%>
<%@page import="com.saama.workbench.util.PropertiesUtil"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 

"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Login</title>
<link rel="icon" href="images/SaamaLogo_sm.png" type="image/jpg">
<style type="text/css">

/* @import url(http://fonts.googleapis.com/css?family=Open+Sans+Condensed:300,700|Open+Sans:400,300,600); */
* {
  box-sizing: border-box;
}
body {
  font-family: 'open sans', helvetica, arial, sans;
  background-color: #F7F7F7;
  -webkit-background-size: cover;
  -moz-background-size: cover;
  -o-background-size: cover;
  background-size: cover;
}
.log-form {
  width: 40%;
  min-width: 320px;
  max-width: 475px;
  background: #fff;
  position: absolute;
  top: 40%;
  left: 50%;
  -webkit-transform: translate(-50%, -50%);
  -moz-transform: translate(-50%, -50%);
  -o-transform: translate(-50%, -50%);
  -ms-transform: translate(-50%, -50%);
  transform: translate(-50%, -50%);
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.25);
  border: 1px solid #286192;
}

@media (max-width: 40em) {
  .log-form {
    width: 95%;
    position: relative;
    margin: 2.5% auto 0 auto;
    left: 0%;
    -webkit-transform: translate(0%, 0%);
    -moz-transform: translate(0%, 0%);
    -o-transform: translate(0%, 0%);
    -ms-transform: translate(0%, 0%);
    transform: translate(0%, 0%);
    border: 1px solid #286192;
  }
}
.log-form form {
  display: block;
  width: 100%;
  padding: 2em;
  
}
.log-form h2 {
  color: black;
  font-family: 'open sans condensed';
  font-size: 1em;
  display: block;
  background: #fff;
  width: 100%;
  text-transform: uppercase;
  padding: -.25em 1em .75em 1.5em;
  box-shadow: inset 0px 1px 1px rgba(255, 255, 255, 0.05);
  
  margin: 0;
  font-weight: 200;
}
.log-form input {
  display: block;
  margin: auto auto;
  width: 100%;
  margin-bottom: 2em;
  padding: .5em 0;
  border: none;
  border-bottom: 1px solid #eaeaea;
  padding-bottom: 1.25em;
  color: #757575;
}
.log-form input:focus {
  outline: none;
}
.log-form .btn {
  display: inline-block;
  background: #337ab7;
  border: 1px solid #235580;
  padding: .5em 2em;
  color: white;
  float:right;
  box-shadow: inset 0px 1px 0px rgba(255, 255, 255, 0.2);
  margin-bottom: 30px;
}
.log-form .btn:hover {
  background: #286192;
}
.log-form .btn:active {
  background: #286192;
  box-shadow: inset 0px 1px 1px rgba(0, 0, 0, 0.1);
}
.log-form .btn:focus {
  outline: none;
}
.login-help{
color:red;
font-size:14px;
padding-left:1%;
}
</style>
</head>
<body>

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

	<div class="log-form">
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
	</div>
	
	<%}%>

	</body>
</html>
<%-- <%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 

"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Login</title>
<link rel="icon" href="images/unilever-logo.jpg" type="image/jpg">
</head>
<body onload="formsubmit">
	<div id="inset_form"></div>
	<script>
		document.getElementById('inset_form').innerHTML = '<form action="login" name="form" method="post" style="display:none;"></form>';
		document.forms['form'].submit();
	</script>
	</body>
</html> --%>