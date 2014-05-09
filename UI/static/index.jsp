<%-- <%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%> --%>
<%@page import="cis455.project.ui.UIGlobal" %>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Avernus Search Engine</title>
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link href="css/google_styles.css" rel="stylesheet">
  </head>
  <body class="welcome">
  <%
    String mode = (String)request.getParameter(UIGlobal.paraMode);
  %>

  <div>
    <div height="0px">&nbsp;</div>
    <div class="header-wrap">
    	<div class="header">
    		<ul class="nav clearfix">
                <li class="item screen">
                    <a href="index.jsp?mode=web">Web</a>
                </li>
                <li class="item screen">
                    <a href="index.jsp?mode=image">Image</a>
                </li>
    		</ul>
    	</div>
    </div>
    
    
	<div id="page">
	<fieldset>
        <div align="center">
            <img src="img/logo6.png"/>
        </div>
        <form id="searchFormStyle" method="post" action='<%= UIGlobal.urlSearchSubmit()%>'>
    		<fieldset>
                <input type="text"  x-webkit-speech placeholder="Search Keyword" name="query" id="s" />
                <input type="submit" name="submit" id="submitButton" value="Search"/>
                <input type="hidden" id="hiddenbox" name="category" value='<%= mode%>' />

                <div class="row" padding-top="10px">
                    &nbsp;
                </div>

                <div class="row" align="center">
                    <div class="checkbox-inline" height="45px">
                        <label padding-left="100px">
                            <input type="checkbox" value="amazon" name="amazon" align="center" checked>
                            <img src="img/amazon.jpg" height="45px" width="auto"/>
                        </label>
                    </div>
                    <div class="checkbox-inline" height="45px">
                        <label>
                            <input type="checkbox" value="youtube" name="youtube" align="center" checked>
                            <img src="img/youtube.jpg" height="45px" width="auto"/>
                        </label>
                    </div>
                    <div class="checkbox-inline" height="45px">
                    <label>
                        <input type="checkbox" value="spellcheck" name="spellcheck" align="center" checked>
                        <img src="img/spellcheck.jpg" height="45px" width="auto"/>
                    </label>
                </div>
                </div>

            </fieldset>
        </form>
	</fieldset>    

	</div>
    <div class="footer-wrap">
        <div class = "footer">
            <p>Footer</p>
        </div>
    </div>
	
	</div>
    <!-- Scripts -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <script src="js/google_script.js"></script>
  </body>
</html>