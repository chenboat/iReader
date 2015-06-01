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
<jsp:include page="../include.jsp"/>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html ng-app="stopwordList">
<head>
    <title>User account page</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        body{padding: 0 20px;}
    </style>
    <script src="../lib/angular.min.js"></script>
    <script src="../lib/angular-resource.min.js"></script>
    <script src="../lib/jquery.min.js"></script>
    <script src="../lib/ui-bootstrap-tpls.min.js"></script>
    <script src="../lib/ng-grid-2.0.11.min.js"></script>

    <script src="../script/stopwordList.js"></script>
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript">
       google.load("visualization", "1", {packages:["corechart","table"]});
       function drawChart(userId) {
         $.get("/jersey/readList/category", { userId: userId})
         .success(function (jsonData) {
             var data = new google.visualization.DataTable();
             data.addColumn('string', 'Category');
             data.addColumn('number', 'Count');
             for (var key in jsonData.map) {
                data.addRow([key,jsonData.map[key]]);
             }
             var options = {
                title: 'My Reading Activities'
             };
             var chart = new google.visualization.PieChart(document.getElementById('piechart'));
             chart.draw(data, options);
             var table = new google.visualization.Table(document.getElementById('category_table'));
             table.draw(data, {sortColumn : 1, sortAscending : false });
         });
       }
    </script>
    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="../lib/ng-grid.min.css"/>
    <link rel="stylesheet" type="text/css" href="../css/style.css"/>
</head>
<body>
  <div class="container">
    <p><a href="<c:url value="/home.jsp"/>">Return to the home page.</a> | <a href="<c:url value="/logout"/>">Log out.</a></p>
    <shiro:user>
        <%
            //This should never be done in a normal page and should exist in a proper MVC controller of some sort, but for this
            //tutorial, we'll just pull out Stormpath Account data from Shiro's PrincipalCollection to reference in the
            //<c:out/> tag next:
            request.setAttribute("account", org.apache.shiro.SecurityUtils.getSubject().getPrincipals().oneByType(java.util.Map.class));
        %>
        <p> ${account.email}</p>
        <div class="row">
            <div class="col-sm-6" id="piechart" style="width: 450px; height: 450px;"></div>
            <div class="col-sm-6" id="category_table"></div>
            <script type="text/javascript">drawChart("${account.email}");</script>
        </div>

        <div class="row" style="background-color:lavender;">
            <!-- Specify a Angular controller script that binds Javascript variables to the feedback messages.-->
            <div class="col-sm-12">
                <div class="message" ng-controller="alertMessagesController">
                    <alert ng-repeat="alert in alerts" type="alert.type" close="closeAlert($index)">{{alert.msg}}</alert>
                </div>
            </div>

            <!-- Specify a Angular controller script that binds Javascript variables to the grid.-->
            <div class="col-sm-12">
                <!-- Specify a Angular controller script that binds Javascript variables to the form.-->
                <div class="form" ng-controller="entryFormController" ng-init="init('${account.email}')">
                    <!-- Verify entry, if there is no id present, that we are Adding a Person -->
                    <div ng-if="entry.id == null">
                        <h5>Add Entry</h5>
                    </div>
                    <!-- Otherwise it's an Edit -->
                    <div ng-if="entry.id != null">
                        <h5>Edit Entry</h5>
                    </div>

                    <div>
                        <!-- Specify the function to be called on submit and disable HTML5 validation, since we're using Angular validation-->
                        <form name="entryForm" ng-submit="updateEntry()" novalidate>

                            <!-- Display an error if the input is invalid and is dirty (only when someone changes the value) -->
                            <div class="form-group" ng-class="{'has-error' : entryForm.id.$invalid && entryForm.id.$dirty}">
                                <label for="id">id:</label>
                                <!-- Display a check when the field is valid and was modified -->
                                <span ng-class="{'glyphicon glyphicon-ok' : entryForm.id.$valid && entryForm.id.$dirty}"></span>

                                <input id="id" name="id" type="text" class="form-control" maxlength="50"
                                       ng-model="entry.id"/>

                                <!-- Validation messages to be displayed on required, minlength and maxlength -->
                                <p class="help-block" ng-show="entryForm.id.$error.required">Add Id.</p>
                            </div>

                            <!-- Form buttons. The 'Save' button is only enabled when the form is valid. -->
                            <div class="buttons">
                                <button type="button" class="btn btn-primary" ng-click="clearForm()">Clear</button>
                                <button type="submit" class="btn btn-primary" ng-disabled="entryForm.$invalid">Save</button>
                            </div>
                        </form>
                    </div>
                </div>
                <div class="grid">
                    <!-- Specify a JavaScript controller script that binds Javascript variables to the HTML.-->
                    <div ng-controller="stopwordList" ng-init="init('${account.email}')">
                        <div>
                            <h5>Stopword List</h5>
                        </div>
                        <!-- Binds the grid component to be displayed. -->
                        <div class="gridStyle" ng-grid="gridOptions"></div>

                        <!--  Bind the pagination component to be displayed. -->
                        <pagination direction-links="true" boundary-links="true"
                                    total-items="stopwordList.totalResults"
                                    page="stopwordList.currentPage"
                                    items-per-page="stopwordList.pageSize"
                                    on-select-page="refreshGrid(page)">
                        </pagination>
                    </div>
                </div>
            </div>
        </div>

        <div class="row">
            <!-- Display the articles already read.-->
            <div class="grid">
                <!-- Specify a JavaScript controller script that binds Javascript variables to the HTML.-->
                <div ng-controller="readList" ng-init="init('${account.email}')">
                    <div>
                        <h5>Articles Read</h5>
                    </div>
                    <!-- Binds the grid component to be displayed. -->
                    <div class="gridStyle" ng-grid="gridOptions"></div>

                    <!--  Bind the pagination component to be displayed. -->
                    <pagination direction-links="true" boundary-links="true"
                                total-items="readList.totalResults"
                                page="readList.currentPage"
                                items-per-page="readList.pageSize"
                                on-select-page="refreshGrid(page)">
                    </pagination>
                </div>
            </div>
        </div>
    </shiro:user>
  </div>
</body>
</html>