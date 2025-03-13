<%--
Home page for the web app
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Gloss-E Java Web Start Application Page</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  </head>
  <body>
    <h1>Gloss-E Web Start Application</h1>

    <script src="http://www.java.com/js/deployJava.js"></script>
    <script>
        var url = "${pageContext.request.scheme}://${header.host}${pageContext.request.contextPath}/gloss-e.jnlp";
        deployJava.createWebStartLaunchButton( url, '1.8.0' );
    </script>
  </body>
</html>
