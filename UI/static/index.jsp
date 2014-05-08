<%-- <%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%> --%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>MiniGoogle</title>
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link href="css/google_styles.css" rel="stylesheet">
  </head>
  <body>
  <div>
    <div height="0px">&nbsp;</div>
    <div class="header-wrap">
    	<div class="header">
    		<ul class="nav clearfix">
    			<li class="item screen">
    				<a href="#">item1</a>
    			</li>
    			<li class="item screen">
    				<a href="#">item2</a>
    			</li>
    			<li class="item screen">
    				<a href="#">item3</a>
    			</li>
    		</ul>
    	</div>
    </div>
    
    
	<div id="page">
	<fieldset>
		<legend>
			<img src="img/button1.png"/>
		</legend>
        <form id="searchFormStyle" method="post" action="UIServlet">
    		<fieldset>
                <input type="text"  x-webkit-speech placeholder="Search Keyword" name="query" id="s" />
                <input type="submit" name="submit" id="submitButton" value="Search"/>
                <input type="hidden" id="hiddenbox" name="category" value="web" />

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