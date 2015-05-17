<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  ~ Copyright (c) 2013 Les Hazlewood and contributors
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<jsp:include page="include.jsp"/>
<!DOCTYPE html>
<html>
<head>
    <title>Apache Shiro Tutorial Webapp</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Add some nice styling and functionality.  We'll just use Twitter Bootstrap -->
    <link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap-theme.min.css">
    <style>
    div.img {
        margin: 5px;
        padding: 5px;
        border: 1px solid #0000ff;
        height: auto;
        width: auto;
        float: left;
        text-align: center;
    }

    div.img img {
        display: inline;
        margin: 5px;
        border: 1px solid #ffffff;
    }

    div.img a:hover img {
        border:1px solid #0000ff;
    }

    div.desc {
        text-align: left;
        font-weight: normal;
        width: 150px;
        margin: 5px;
    }
    #columns {
    	column-width: 320px;
    	column-gap: 15px;
      width: 90%;
    	max-width: 1100px;
    	margin: 50px auto;
    }

    div#columns figure {
    	background: #fefefe;
    	border: 2px solid #fcfcfc;
    	box-shadow: 0 1px 2px rgba(34, 25, 25, 0.4);
    	margin: 0 2px 15px;
    	padding: 15px;
    	padding-bottom: 10px;
    	transition: opacity .4s ease-in-out;
      display: inline-block;
      column-break-inside: avoid;
    }

    div#columns figure img {
    	width: 100%; height: auto;
    	border-bottom: 1px solid #ccc;
    	padding-bottom: 15px;
    	margin-bottom: 5px;
    }

    div#columns figure figcaption {
      font-size: .9rem;
    	color: #444;
      line-height: 1.5;
    }

    div#columns small {
      font-size: 1rem;
      float: right;
      color: #aaa;
    }

    div#columns small a {
      color: #666;
      text-decoration: none;
      transition: .4s color;
    }</style>
    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="https://code.jquery.com/jquery.js"></script>
    <script>
         function loadSectionsHTMLPage(userId) {
            $("#accountSectionsHTML").load("jersey/account/sectionsHtml",{id:userId}, function() {
                jQuery(".content").hide();
                //toggle the component with class msg_body
                jQuery(".heading").click(function(){
                    var section = jQuery(this).text();
                    if (section.length > 3 && (section.charAt(section.length - 1) == ']')) {
                       var ch = section.charAt(section.length - 2);
                       if (ch == "+") {
                         jQuery(this).text(section.substring(0,section.length - 2) + "-]");
                       } else if (ch == "-") {
                         jQuery(this).text(section.substring(0,section.length - 2) + "+]");
                       }
                    }
                    jQuery(this).next(".content").slideToggle(500);});
            });
         }
    </script>
    <script>
         function loadRankingHTMLPage(userId) {
             $("#accountRankingHTML").load("jersey/account/rankingHtml",{id:userId});
         }
    </script>
    <script type="text/javascript">
         function sendText(elementId,userId,section) {
             var titleAndSummary=elementId.firstChild.data + " " + elementId.parentNode.lastChild.textContent;
             $.post("jersey/account/record", {id:userId, title:titleAndSummary, section: section});     }
    </script>
</head>
<body>
    <p>
      <shiro:authenticated>Visit your <a href="<c:url value="/account"/>">account page</a>.</shiro:authenticated>
      <shiro:notAuthenticated>If you want to access the authenticated-only <a href="<c:url value="/account"/>">account page</a>,
        you will need to log-in first.</shiro:notAuthenticated>

      ( <shiro:user> <a href="<c:url value="/logout"/>">Log out</a></shiro:user>
        <shiro:guest><a href="<c:url value="/loginOrReg.jsp"/>">Log in</a> <b>You are NOT logged in</b> </shiro:guest> )
    </p>

    <p>
        <shiro:guest>
                <div style="column-count:1;-moz-column-count:1; /* Firefox */
                               -webkit-column-count:1; /* Safari and Chrome */">
                    <div style="column-count:4;-moz-column-count:4; /* Firefox */
                                        -webkit-column-count:4; /* Safari and Chrome */">
                        <div id="accountSectionsHTML"></div>
                        <script> loadSectionsHTMLPage("anonymous");</script>
                    </div>
                    <div style="column-count:4;-moz-column-count:4; /* Firefox */
                                        -webkit-column-count:4; /* Safari and Chrome */">
                        <div id="accountRankingHTML"></div>
                        <script> loadRankingHTMLPage("anonymous");</script>
                    </div>
                </div>
        </shiro:guest>
        <shiro:user>
            <%
                //This should never be done in a normal page and should exist in a proper MVC controller of some sort, but for this
                //tutorial, we'll just pull out Stormpath Account data from Shiro's PrincipalCollection to reference in the
                //<c:out/> tag next:
                request.setAttribute("account", org.apache.shiro.SecurityUtils.getSubject().getPrincipals().oneByType(java.util.Map.class));
            %>
            <div style="column-count:1;-moz-column-count:1; /* Firefox */
               -webkit-column-count:1; /* Safari and Chrome */">
                <div style="column-count:4;-moz-column-count:4; /* Firefox */
                                    -webkit-column-count:4; /* Safari and Chrome */">
                    <div id="accountSectionsHTML"></div>
                    <script> loadSectionsHTMLPage("${account.email}");</script>
                </div>
                <div style="column-count:4;-moz-column-count:4; /* Firefox */
                                    -webkit-column-count:4; /* Safari and Chrome */">
                    <div id="accountRankingHTML"></div>
                    <script> loadRankingHTMLPage("${account.email}");</script>
                </div>
            </div>
        </shiro:user>

    </p>


    <script src="//netdna.bootstrapcdn.com/bootstrap/3.0.2/js/bootstrap.min.js"></script>
    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
</body>
</html>
