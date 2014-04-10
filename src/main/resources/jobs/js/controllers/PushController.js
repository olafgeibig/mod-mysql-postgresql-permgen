'use strict';

/**
 * PushController
 * @constructor
 */
var PushController = function($scope, $http, $modal, $log) {

    $scope.getAllTags = function() {
        $scope.resetError();
        $http.get('/jobs/tag').
            success(function(result) {
                $scope.tags = result.content.tags;
            }).
            error(function(result) {
                $scope.setError("Loading tags failed: " + result.content);
            });
    };

    $scope.createTag = function(tag) {
        $scope.resetError();
        $http.put('/jobs/tag/' + tag).
            success(function(result){
                $scope.getAllTags();
            }).
            error(function(result) {
                $scope.setError("Creating tag failed: " + result);
            });
    };

    $scope.deleteTag = function(tag) {
        $scope.resetError();
        $http.delete('/jobs/tag/' + tag).
            success(function(result) {
                $scope.getAllTags();
            }).
            error(function(result) {
                $scope.setError("Deleting tag " + tag + " failed: " + result);
            });
    };

    $scope.open = function (tagToBeDeleted) {
        var modalInstance = $modal.open({
            templateUrl: 'DeleteModal.html',
            controller: ModalInstanceCtrl,
            resolve: {
                tag: function () {
                    return tagToBeDeleted;
                }
            }
        });

        modalInstance.result.then(function (tag) {
            $log.info('Modal closed, delete tag ' + tag + ' here at: ' + new Date());
            $scope.deleteTag(tag)
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
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

var ModalInstanceCtrl = function ($scope, $modalInstance, tag) {
    $scope.tag = tag;

    $scope.ok = function () {
        $modalInstance.close($scope.tag);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
};
