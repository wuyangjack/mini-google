<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<%@page import="java.util.*" %>
<%@page import="amazonAPI.Item" %>
<%@page import="youtubeAPI.YoutubeItem" %>

<!DOCTYPE html>
<%! int resultIndex; 
    // String page_number = request.getAttribute("page");
    // String wiki_string = request.getAttribute("wiki");
%>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Bootstrap 101 Template</title>

    <!-- Bootstrap -->
    <link href="css/bootstrap.min.css" rel="stylesheet">
    
    <!-- Google Powered -->
    <link href="css/google_styles.css" rel="stylesheet">


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
    
    <div class="row">
        <div class="col-md-12">&nbsp;</div>
    </div>
    <div class="row">
        <div class="col-md-12">&nbsp;</div>
    </div>
    <!-- <form>
        <div width = "80%" align = "center">
        <input type="text" display="block" width="100%" height = "20px" padding="0 50px 0 50px">
        <input type="submit">
    </div>
    </form> -->
    <div class="row">
        <div class="col-md-1">&nbsp;</div>
        <form>
        <div class="col-lg-8">
            <div class="input-group">
                
                <span class="glyphicon glyphicon-search input-group-btn"></span>
                <input type="text" class="form-control">
                <span class="input-group-btn">
                    <input type="submit" name="submit" id="submitButton" value="Search"/>
                </span>
                
            </div><!-- /input-group -->
        </div><!-- /.col-lg-6 -->
        </form>
    </div>
	
    <div class="result">
        <div class="result-display">
            <%if(request.getAttribute("page").equals("1")){ %>
                <fieldset>
                    <legend>
                        <img src="img/button1.png"/>
                    </legend>
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <a href='<%= request.getAttribute("wiki")%>' ><font size="4" color="#26B0FF"> <%= request.getParameter("query") + "- Wikipedia, the free encyclopedia"%><br></font></a>
                        </div>
                        <!-- <div class="panel-body">
                            <%= request.getParameter("url" + resultIndex)%>
                        </div> -->
                    </div>
                </fieldset>
            <% } %>

            <% 
            String[] titles_arr = (String[])request.getAttribute("titles");
            String[] urls_arr = (String[])request.getAttribute("urls");
            int result_num = titles_arr.length;
            %>
            <%for (resultIndex = 0; resultIndex < result_num; resultIndex++){ %>
                <!-- <div>
                    <a href="http://www.google.com"> <%= request.getParameter("title" + resultIndex)%></a>
                    <p><%= request.getParameter("url" + resultIndex)%></p>
                </div> -->
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <a href='<%= urls_arr[resultIndex]%>'><%= titles_arr[resultIndex]%><br></a>
                    </div>
                    <div class="panel-body">
                        <%= urls_arr[resultIndex]%>
                    </div>
                </div>
            <%}%>
           
        <div class="row">
        <!-- <ul class="pagination">
            <li><a href="#">&laquo;</a></li>
            <li><a href="#">1</a></li>
            <li><a href="#">2</a></li>
            <li><a href="#">3</a></li>
            <li><a href="#">4</a></li>
            <li><a href="#">5</a></li>
            <li><a href="#">6</a></li>
            <li><a href="#">7</a></li>
            <li><a href="#">8</a></li>
            <li><a href="#">9</a></li>
            <li><a href="#">10</a></li>
            <li><a href="#">&raquo;</a></li>
        </ul> -->
            <ul class="pager">
                <li class="previous"><a href="#">&larr; Older</a></li>
                <li class="next"><a href="#">Newer &rarr;</a></li>
            </ul>
        </div>
            
        </div>
        <div class="ad-display" border="1">
            <fieldset>
            <%
                ArrayList list = (ArrayList)request.getAttribute("amazon_items");
            %>
            <%for (resultIndex = 0; resultIndex < list.size(); resultIndex++){ %>
            <%
               
                String title = ((Item)list.get(resultIndex)).getTitle();
                String img = ((Item)list.get(resultIndex)).getImg();
                String price = ((Item)list.get(resultIndex)).getPrice();
                String url = ((Item)list.get(resultIndex)).getUrl();
            %>
               
                <ul class="list-group">
                    <li class="list-group-item list-group-item-danger" >
                        <a href='<%= url%>'> <%=title%> </a>
                        <span class="badge"><%=price%></span>
                     </li>
                     <li align="center" >
                        <div> &nbsp;
                        </div>
                        <div height="160">
                            <img src='<%= img%>'></img>
                        </div>
                     </li>
                </ul>
            <%}%>

            <%for (resultIndex = 0; resultIndex < ((YoutubeItem)request.getAttribute("youtube_items")).item_Num; resultIndex++){ %>
            <%
                String y_title = ((YoutubeItem)request.getAttribute("youtube_items")).title[resultIndex];
                String y_img = ((YoutubeItem)request.getAttribute("youtube_items")).img[resultIndex];
                String y_url = ((YoutubeItem)request.getAttribute("youtube_items")).url[resultIndex];
                String y_embed_url = ((YoutubeItem)request.getAttribute("youtube_items")).embed_url[resultIndex];
            %>
               
                <ul class="list-group" >
                    <li class="nav nav-pills nav-justified">
                        <!-- <a href='<%= y_url%>' onClick="PopupCenter('<%= y_url%>', '<%=y_title%>', 100, 100)"> <%=y_title%> </a> -->
                        <a href='<%= y_url%>'>  <%=y_title%> </a>
                        
                     </li>
                     <li align="center">
                        <div height="160">
                            <iframe width="275" height="auto" src='<%= y_embed_url%>' >
                            </iframe>
                        </div>
                     </li>
                </ul>
            <%}%>


        </fieldset>
        </div>
    </div>
    <%
        String query_string = (String)request.getAttribute("query");
    %>
    <div><p>Query String: <%=query_string%></p></div>

	
	</div>

    <!-- <iframe  name="iframe"></iframe> -->
    
    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="js/bootstrap.min.js"></script>
    <script src="js/google_script.js"></script>
    <script>
    function PopupCenter(pageURL, title,w,h) {
    var left = (screen.width/2)-(w/2);
    var top = (screen.height/2)-(h/2);
    var targetWin = window.open (pageURL, title, 'toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no, width='+w+', height='+h+', top='+top+', left='+left);
    } 
    </script>
  </body>
</html> width='+w+', height='+h+', top='+top+', left='+left);
    } 
    </script>
  </body>
</html>