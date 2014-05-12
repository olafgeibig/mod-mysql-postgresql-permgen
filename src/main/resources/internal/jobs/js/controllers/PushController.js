'use strict';

/**
 * PushController
 * @constructor
 */
var PushController = function($scope, $http, $modal, $log) {

    $scope.push = { audienceType:'all', deviceType:'all', dryRun:true };
    $scope.audienceTypes = ['tag','segment'];
    $scope.deviceTypes = ['all','ios','android'];


    $scope.setAudienceToAll = function() {
        $scope.resetError();
        $scope.segments = null;
        $scope.tags = null;
        //$scope.form.audienceName.$setValidity("required", false);
    };

    $scope.getAllTags = function() {
        $scope.resetError();
        $http.get('../job/tag/').
            success(function(result) {
                $scope.tags = result.content.tags;
                $scope.segments = null;
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
                $scope.tags = null;
            }).
            error(function(result) {
                $scope.setError("Loading segments failed: " + result.content);
            });
    };

    $scope.execPush = function(pushData) {
        $scope.resetError();
        $http.post('../job/pushNotification', pushData).
            success(function(result) {
                $scope.pushRequest = JSON.stringify(JSON.parse(result.request),null,2);
                $scope.pushResult = JSON.stringify(JSON.parse(result.response),null,2);
                $scope.pushError = null;
            }).
            error(function(result) {
                if(result.statusCode = 400) {
                    $scope.pushRequest = JSON.stringify(JSON.parse(result.request),null,2);
                    $scope.pushError = JSON.stringify(JSON.parse(result.content),null,2);
                } else {
                    $scope.setError("Executing push failed: " + result.statusMessage);
                }
                $scope.pushResult = null;
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

};
