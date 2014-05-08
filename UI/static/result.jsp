<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<%@page import="java.util.*" %>
<%@page import="cis455.project.amazon.Item" %>
<%@page import="cis455.project.youtube.YoutubeItem" %>

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
    
    <div class="row">
        <div class="col-md-12">&nbsp;</div>
    </div>
    <div class="row">
        <div class="col-md-12">&nbsp;</div>
    </div>

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
                    
                </div>
            </div>
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
                    </div>
                </fieldset>
            <% } %>

            <% 
            String[] titles_arr = (String[])request.getAttribute("titles");
            String[] urls_arr = (String[])request.getAttribute("urls");
            int result_num = titles_arr.length;
            %>
            <%for (int resultIndex = 0; resultIndex < result_num; resultIndex++){ %>
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
            <%for (int resultIndex = 0; resultIndex < list.size(); resultIndex++){ %>
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

            <%for (int resultIndex = 0; resultIndex < ((YoutubeItem)request.getAttribute("youtube_items")).item_Num; resultIndex++){ %>
            <%
                String y_title = ((YoutubeItem)request.getAttribute("youtube_items")).title[resultIndex];
                String y_img = ((YoutubeItem)request.getAttribute("youtube_items")).img[resultIndex];
                String y_url = ((YoutubeItem)request.getAttribute("youtube_items")).url[resultIndex];
                String y_embed_url = ((YoutubeItem)request.getAttribute("youtube_items")).embed_url[resultIndex];
            %>
               
                <ul class="list-group" >
                    <li class="nav nav-pills nav-justified">
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
        String page_string = (String)request.getAttribute("page");
    %>
    <div><p>Query String: <%=query_string%></p></div>
    <div><p>Page String: <%=page_string%></p></div>


	
	</div>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <script src="js/google_script.js"></script>
  </body>
</html>