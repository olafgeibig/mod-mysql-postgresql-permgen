'use strict';

/**
 * TagController
 * @constructor
 */
var TaggingController = function($scope, $http, $modal, $log, $timeout) {

    $scope.query = { query: '', dryRun: true, tag: ''};
    $scope.jobId = null;

    $scope.getMetadata = function() {
        $http.get('../job/metadata/').
            success(function(result) {
                $scope.metadata = result;
            });
    };

    $scope.getAllTags = function() {
        $scope.resetError();
        $http.get('../job/tag/').
            success(function(result) {
                $scope.tags = result.content.tags;
            }).
            error(function(result) {
                $scope.setError("Loading tags failed: " + result.content);
            });
    };

    $scope.getAllSegments = function() {
        $scope.resetError();
        $http.get('../job/segment/').
            success(function(result) {
                $scope.segments = result.content.segments;
            }).
            error(function(result) {
                $scope.setError("Loading segments failed: " + result.content);
            });
    };

    $scope.runTagging = function(query) {
        $scope.resetError();
        $http.post('../job/tagging/run', query).
            success(function(result) {
                $scope.queryResult = null;
                $scope.sampleResult = null;
                $scope.jobId = result;
                poll();
            }).
            error(function(result) {
                $scope.setError("Tagging failed: " + result.message);
                $scope.jobId = null;
            });
    };

    $scope.runSampling = function(query) {
        $scope.resetError();
        $http.post('../job/tagging/sample', query).
            success(function(result) {
                $scope.sampleResult = result;
                $scope.queryResult = null;
            }).
            error(function(result) {
                $scope.setError("Executing query failed: " + result.message);
            });
    };

    var poll = function() {
        if(!!$scope.jobId) {
            $timeout(function() {
                if(!!$scope.jobId) {
                    $http.get('../job/tagging/progress/' + $scope.jobId).
                        success(function(result) {
                            $scope.jobStatus = result;
                        }).
                        error(function(result) {
                            $scope.jobId = null;
//                            $scope.setError("Tagging failed: " + result.message);
                        });
                    poll();
                };
            }, 1000);
            if(!!$scope.jobStatus) {
                switch($scope.jobStatus.status) {

                    case "SUCCEEDED":
                        $http.get('../job/tagging/result/' + $scope.jobId).
                            success(function(result) {
                                $scope.queryResult = result;
                                $scope.sampleResult = null;
                                $scope.jobId = null;
                                $scope.jobStatus = null;
                            }).
                            error(function(result) {
                                $scope.setError("Fetching tagging result failed: " + result.message);
                                $scope.queryResult = null;
                                $scope.sampleResult = null;
                                $scope.jobId = null;
                                $scope.jobStatus = null;
                            });
                        break;

                    case "FAILED":
                        $http.get('../job/tagging/result/' + $scope.jobId).
                            error(function(result) {
                                $scope.setError("Fetching tagging result failed: " + result.message);
                                $scope.queryResult = null;
                                $scope.sampleResult = null;
                                $scope.jobId = null;
                                $scope.jobStatus = null;
                            });
                        break;

                };
            };
        };
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
    $scope.getMetadata();
};
