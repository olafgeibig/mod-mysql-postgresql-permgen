'use strict';

/**
 * TagController
 * @constructor
 */
var TaggingController = function($scope, $http, $modal, $log) {

    $scope.getAllTags = function() {
        $scope.resetError();
        $http.get('/job/tag/').
            success(function(result) {
                $scope.tags = result.content.tags;
            }).
            error(function(result) {
                $scope.setError("Loading tags failed: " + result.content);
            });
    };

    $scope.getAllSegments = function() {
        $scope.resetError();
        $http.get('/job/segment/').
            success(function(result) {
                $scope.segments = result.content.segments;
            }).
            error(function(result) {
                $scope.setError("Loading segments failed: " + result.content);
            });
    };

    $scope.runQuery = function(query) {
        $scope.resetError();
        $http.post('/job/query/exec', query).
            success(function(result) {
                $scope.queryResult = result;
            }).
            error(function(result) {
                $scope.setError("Executing query failed: " + result);
            });
    };

    $scope.resetError = function() {
        $scope.error = false;
        $scope.errorMessage = '';
    };

    $scope.setError = function(message) {
        $scope.error = true;
        $scope.errorMessage = message;
    };

    $scope.getAllTags();
};
