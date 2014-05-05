<%-- <%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%> --%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Bootstrap 101 Template</title>

    <!-- Bootstrap -->
    <link href="/UI/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- Google Powered -->
    <link href="/UI/css/google_styles.css" rel="stylesheet">
    
    <!-- UICloud -->
    <!-- <link href="/HelloWorld/css/uicloud.css" rel="stylesheet"> -->

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>
  <body>
  <div>
    <div height="0px">&nbsp;</div>
    <div class="header-wrap">
    	<div class="header">
    		<ul class="nav clearfix">
    			<!-- <li class="item">
    				<a href="#">
    					<img src="">
    				</a>
    			</li> -->
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
    
    <form id="searchFormStyle" method="post" action="QueryCheckServlet">
		<fieldset>
            
           <!--  <input type="text"  placeholder="Search Keyword" name="query" id="s" />
			<input type="submit" name="submit" id="submitButton" value="Search"/> -->
            <input type="text"  x-webkit-speech placeholder="Search Keyword" name="query" id="s" />
            <!-- <input type="text" x-webkit-speech  name="testbox"/> -->
            <input type="submit" name="submit" id="submitButton" value="Search"/>
            <input type="hidden" id="hiddenbox" name="category" value="web" />

                        
            <!-- <ul class="icons">
                <li class="web" title="Web Search" data-searchType="web">Web</li>
                <li class="images" title="Image Search" data-searchType="images">Images</li>
                <li class="news" title="News Search" data-searchType="news">News</li>
                <li class="videos" title="Video Search" data-searchType="video">Videos</li>
            </ul> -->

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

    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="js/bootstrap.min.js"></script>
    <script src="js/google_script.js"></script>
  </body>
</html>