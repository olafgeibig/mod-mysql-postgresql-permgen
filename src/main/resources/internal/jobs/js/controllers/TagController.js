'use strict';

/**
 * TagController
 * @constructor
 */
var TagController = function($scope, $http, $modal, $log) {

    $scope.getAllTags = function() {
        $scope.resetError();
        $http.get('../job/tag/').
            success(function(result) {
                $scope.tags = result.content.tags;
            }).
            error(function(result) {
                $scope.setError("Loading tags failed: " + result.message);
            });
    };

    $scope.createTag = function(tag) {
        $scope.resetError();
        $http.put('../job/tag/' + tag).
            success(function(result){
                $scope.getAllTags();
            }).
            error(function(result) {
                $scope.setError("Creating tag failed: " + result.message);
            });
    };

    $scope.deleteTag = function(tag) {
        $scope.resetError();
        $http.delete('../job/tag/' + tag).
            success(function(result) {
                $scope.getAllTags();
            }).
            error(function(result) {
                $scope.setError("Deleting tag " + tag + " failed: " + result.message);
            });
    };

    $scope.open = function (tagToBeDeleted) {
        var modalInstance = $modal.open({
            templateUrl: 'DeleteTagModal.html',
            controller: TagModalCtrl,
            resolve: {
                tag: function () {
                    return tagToBeDeleted;
                }
            }
        });

        modalInstance.result.then(function (tag) {
            $scope.deleteTag(tag)
        }, function () {
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

var TagModalCtrl = function ($scope, $modalInstance, tag) {
    $scope.tag = tag;

    $scope.ok = function () {
        $modalInstance.close($scope.tag);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
};
