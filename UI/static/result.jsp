<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<%@page import="java.util.*" %>
<%@page import="cis455.project.amazon.Item" %>
<%@page import="cis455.project.youtube.YoutubeItem" %>
<%@page import="cis455.project.ui.UIGlobal" %>
<%@page import="cis455.project.ui.SearchResult" %>



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
  <%
    SearchResult searchResult = (SearchResult)request.getAttribute(UIGlobal.attrSearchResult);
    String query = searchResult.getQuery();
    String wikipediaUrl = searchResult.getWikipediaUrl();
    String paraWikipedia = request.getParameter(UIGlobal.paraWiki);
    String[] titles = searchResult.getPageTitles();
    String[] urls = searchResult.getPageUrls();
    int records = titles.length;
    int count = searchResult.getCount();
    int pages = searchResult.getPages();
    int pageCurrent = searchResult.getPageCurrent();
    List amazonItems = searchResult.getAmazonItems();
    YoutubeItem youtubeItems = searchResult.getYoutubeItems();
    String urlNext = searchResult.getNeighborPageUrl(true);
    String urlPrev = searchResult.getNeighborPageUrl(false);
  %>

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
            <% if( wikipediaUrl != null && paraWikipedia.equals("1")) { %>
                <fieldset>
                    <legend>
                        <img src="img/button1.png"/>
                    </legend>
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <a href='<%= wikipediaUrl%>' >
                                <font size="4" color="#26B0FF"> 
                                    <%= query + "- Wikipedia, the free encyclopedia"%><br>
                                </font>
                            </a>
                        </div>
                    </div>
                </fieldset>
            <% } %>

            <% for (int i = 0; i < records; i++) { %>
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <a href='<%= urls[i]%>'><%= titles[i]%><br></a>
                    </div>
                    <div class="panel-body">
                        <%= urls[i]%>
                    </div>
                </div>
            <%}%>
           
        <div class="row">
            <ul class="pager">
                <% if (urlNext != null) { %>
                    <li class="next"><a href='<%= urlNext%>'>Next &rarr;</a></li>
                <% } %>
                <% if (urlPrev != null) { %>
                    <li class="previous"><a href='<%= urlPrev%>'>&larr; Previous </a></li>
                <% } %>
            </ul>
        </div>
            
        </div>

        <div class="ad-display" border="1">
            <fieldset>

            <% for (int i = 0; i < amazonItems.size(); i ++) { %>
                <%
                Item item = ((Item)amazonItems.get(i));
                String title = item.getTitle();
                String img = item.getImg();
                String price = item.getPrice();
                String url = item.getUrl();
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
            <% } %>

            <%for (int i = 0; i < youtubeItems.item_Num; i++){ %>
                <%
                String y_title = youtubeItems.title[i];
                String y_img = youtubeItems.img[i];
                String y_url = youtubeItems.url[i];
                String y_embed_url = youtubeItems.embed_url[i];
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
            <% } %>

        </fieldset>
        </div>
    </div>
    <div><p>Query String: <%=query%></p></div>
    <div><p>Page String: <%=pageCurrent%></p></div>
	</div>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <script src="js/google_script.js"></script>
  </body>
</html>