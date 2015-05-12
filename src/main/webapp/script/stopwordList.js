var app = angular.module('stopwordList', ['ngResource','ngGrid', 'ui.bootstrap']);
// Create a controller with name stopwordList to bind to the html page.
app.controller('stopwordList', function ($scope, $http, $rootScope) {
    $scope.userEmail = 'anonymous';
    $scope.init = function(email) {
        $scope.userEmail = email;
    }
    // Makes the REST request to get the data to populate the grid.
    $scope.refreshGrid = function (page) {
        $http({
            url: '/jersey/stopwordList',
            method: 'GET',
            params: {
                page: page,
                sortFields: $scope.sortInfo.fields[0],
                sortDirections: $scope.sortInfo.directions[0],
                userId: $scope.userEmail,
            }
        }).success(function (data) {
            $scope.stopwordList = data;
        });
    };
 
    // Do something when the grid is sorted.
    // The grid throws the ngGridEventSorted that gets picked up here and assigns the sortInfo to the scope.
    // This will allow to watch the sortInfo in the scope for changed and refresh the grid.
    $scope.$on('ngGridEventSorted', function (event, sortInfo) {
        $scope.sortInfo = sortInfo;
    });
 
    // Watch the sortInfo variable. If changes are detected than we need to refresh the grid.
    // This also works for the first page access, since we assign the initial sorting in the initialize section.
    $scope.$watch('sortInfo', function () {
        $scope.refreshGrid($scope.stopwordList.currentPage);
    }, true);

    // Picks the event broadcasted when a person is saved or deleted to refresh the grid elements with the most
    // updated information.
    $scope.$on('refreshGrid', function () {
        $scope.refreshGrid($scope.stopwordList.currentPage);
    });

    // Initialize required information: sorting, the first page to show and the grid options.
    $scope.sortInfo = {fields: ['id'], directions: ['asc']};
    $scope.stopwordList = {currentPage : 1};
    $scope.gridOptions = {
        data: 'stopwordList.list',
        useExternalSorting: true,
        sortInfo: $scope.sortInfo,
        columnDefs: [
                    { field: 'id', displayName: 'Word' },
                    { field: 'value', displayName: 'Date' },
                    { field: '', width: 30, cellTemplate: '<span class="glyphicon glyphicon-remove remove" ng-click="deleteRow(row)"></span>' }
                ],
    };
    // Broadcast an event when an element in the grid is deleted. No real deletion is performed at this point.
    $scope.deleteRow = function (row) {
        $rootScope.$broadcast('deleteEntry', row.entity.id);
    };
});

// Create a controller with name entryFormController to bind to the form section.
app.controller('entryFormController', function ($scope, $rootScope, entryService) {
    $scope.userEmail = 'anonymous';
    $scope.init = function(email) {
        $scope.userEmail = email;
    }
    // Clears the form. Either by clicking the 'Clear' button in the form, or when a successful save is performed.
    $scope.clearForm = function () {
        $scope.entry = null;
        // Resets the form validation state.
        $scope.entryForm.$setPristine();
        // Broadcast the event to also clear the grid selection.
        $rootScope.$broadcast('clear');
    };

    // Calls the rest method to save a entry.
    $scope.updateEntry = function () {
        $scope.entry.userId = $scope.userEmail;
        entryService.save($scope.entry).$promise.then(
            function () {
                // Broadcast the event to refresh the grid.
                $rootScope.$broadcast('refreshGrid');
                // Broadcast the event to display a save message.
                $rootScope.$broadcast('entrySaved');
                $scope.clearForm();
            },
            function () {
                // Broadcast the event for a server error.
                $rootScope.$broadcast('error');
            });
    };

    // Picks up the event broadcast when the entry is selected from the grid and perform the entry load by calling
    // the appropriate rest service.
    $scope.$on('entrySelected', function (event, id) {
        $scope.entry = entryService.get({id: id, userId: userEmail});
    });

    // Picks us the event broadcast when the entry is deleted from the grid and perform the actual entry delete by
    // calling the appropriate rest service.
    $scope.$on('deleteEntry', function (event, id) {
        entryService.delete({id: id, userId: $scope.userEmail}).$promise.then(
            function () {
                // Broadcast the event to refresh the grid.
                $rootScope.$broadcast('refreshGrid');
                // Broadcast the event to display a delete message.
                $rootScope.$broadcast('entryDeleted');
                $scope.clearForm();
            },
            function () {
                // Broadcast the event for a server error.
                $rootScope.$broadcast('error');
            });
    });
});

// Create a controller with name alertMessagesController to bind to the feedback messages section.
app.controller('alertMessagesController', function ($scope) {
    // Picks up the event to display a saved message.
    $scope.$on('entrySaved', function () {
        $scope.alerts = [
            { type: 'success', msg: 'Record saved successfully!' }
        ];
    });

    // Picks up the event to display a deleted message.
    $scope.$on('entryDeleted', function () {
        $scope.alerts = [
            { type: 'success', msg: 'Record deleted successfully!' }
        ];
    });

    // Picks up the event to display a server error message.
    $scope.$on('error', function () {
        $scope.alerts = [
            { type: 'danger', msg: 'There was a problem in the server!' }
        ];
    });

    $scope.closeAlert = function (index) {
        $scope.alerts.splice(index, 1);
    };
});

// Service that provides stopword list operations
app.factory('entryService', function ($resource) {
    return $resource('/jersey/stopwordList/:id');
});

app.controller('readList', function ($scope, $http, $rootScope) {
    $scope.userEmail = 'anonymous';
    $scope.init = function(email) {
        $scope.userEmail = email;
    }
    // Makes the REST request to get the data to populate the grid.
    $scope.refreshGrid = function (page) {
        $http({
            url: '/jersey/readList',
            method: 'GET',
            params: {
                page: page,
                sortFields: $scope.sortInfo.fields[0],
                sortDirections: $scope.sortInfo.directions[0],
                userId: $scope.userEmail,
            }
        }).success(function (data) {
            $scope.readList = data;
        });
    };

    // Do something when the grid is sorted.
    // The grid throws the ngGridEventSorted that gets picked up here and assigns the sortInfo to the scope.
    // This will allow to watch the sortInfo in the scope for changed and refresh the grid.
    $scope.$on('ngGridEventSorted', function (event, sortInfo) {
        $scope.sortInfo = sortInfo;
    });

    // Watch the sortInfo variable. If changes are detected than we need to refresh the grid.
    // This also works for the first page access, since we assign the initial sorting in the initialize section.
    $scope.$watch('sortInfo', function () {
        $scope.refreshGrid($scope.readList.currentPage);
    }, true);

    // Picks the event broadcasted when a person is saved or deleted to refresh the grid elements with the most
    // updated information.
    $scope.$on('refreshGrid', function () {
        $scope.refreshGrid($scope.readList.currentPage);
    });

    // Initialize required information: sorting, the first page to show and the grid options.
    $scope.sortInfo = {fields: ['id'], directions: ['asc']};
    $scope.readList = {currentPage : 1};
    $scope.gridOptions = {
        data: 'readList.list',
        useExternalSorting: true,
        sortInfo: $scope.sortInfo,
        columnDefs: [
                    { field: 'id', displayName: 'Article' },
                    { field: 'section', displayName: 'Category' },
                    { field: 'value', displayName: 'Date' },
                ],
    };
});
