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
    <title>NYTimes Reader</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Add some nice styling and functionality.  We'll just use Twitter Bootstrap -->
    <link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap-theme.min.css">
    <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="https://code.jquery.com/jquery.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/masonry/3.3.1/masonry.pkgd.min.js"></script>
    <script src="script/autosuggest.js"></script>
    <script src="script/title_suggestions.js"></script>

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
                    jQuery(this).parent().next(".content").slideToggle(500);});
                // Load the data into auto suggestion box.
                var array = [];
                var links = [];
                var sections = [];
                var elements = document.getElementById("accountSectionsHTML").getElementsByTagName("a");
                for (var i = 0; i < elements.length; i++) {
                    array.push(elements[i].textContent);
                    links.push(elements[i].getAttribute("href"));
                    sections.push(elements[i].parentElement.parentElement.previousElementSibling.getAttribute("section"));
                }
                var oTextbox = new AutoSuggestControl(document.getElementById("txt1"),
                                                new TitleSuggestions(array, links, sections, userId));
            });
         }
    </script>
    <script>
         function loadRankingHTMLPage(userId) {
             $("#accountRankingHTML").load("jersey/account/rankingHtml",{id:userId},
               function() {
                 if (typeof(Storage) != "undefined") {
                     if (sessionStorage.sectionPreference) {
                       upSection(sessionStorage.sectionPreference);
                     }
                  }
               });
         }
    </script>
    <script type="text/javascript">
         function sendText(elementId,userId,section) {
             var titleAndSummary=elementId.firstChild.data + " " + elementId.parentNode.lastChild.textContent;
             $.post("jersey/account/record", {id:userId, title:titleAndSummary, section: section});     }
    </script>
    <script type="text/javascript">
        function up(elmnt) {
            var section = elmnt.parentElement.getAttribute("section");
            upSection(section);
            if (typeof(Storage) != "undefined") {
              // set a client side storage to record the preference.
              sessionStorage.sectionPreference = section;
            }
        }
    </script>
    <script type="text/javascript">
        function upSection(section) {
            var selected = $("div").filter(function() {return $(this).attr("section") == section});
            var $grid = $('.grid').masonry({
              columnWidth: 250,
              itemSelector: '.grid-item'
            });
            // make jQuery object
            copied = selected.clone();
            $grid.masonry( 'remove', selected).masonry();
            $grid.prepend(copied).masonry('prepended', copied);
        }
    </script>
</head>
<body>
    <p>
      <img src="pics/ireader.png" height="24" width="24" hspace="50">
      <input type="text" id="txt1" size="80" placeholder="Type to search articles"/>
      <shiro:authenticated>Visit your <a href="<c:url value="/account"/>">account page</a>.</shiro:authenticated>
      <shiro:notAuthenticated><a href="<c:url value="/account"/>">Register</a></shiro:notAuthenticated>

       (<shiro:user> <a href="<c:url value="/logout"/>">Log out</a></shiro:user>
        <shiro:guest><a href="<c:url value="/loginOrReg.jsp"/>">Log in</a></shiro:guest>)
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
                    <div id="accountRankingHTML"></div>
                    <script>loadRankingHTMLPage("anonymous");</script>
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
                <div id="accountRankingHTML"></div>
                <script>loadRankingHTMLPage("${account.email}");</script>
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
