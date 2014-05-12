'use strict';

/**
 * SegmentController
 * @constructor
 */
var SegmentController = function($scope, $http, $modal, $log) {

    $scope.getAllSegments = function() {
        $scope.resetError();
        $http.get('../job/segment/').
            success(function(result) {
                $scope.segments = result.content.segments;
            }).
            error(function(result) {
                $scope.setError("Loading segments failed: " + result.message);
            });
    };

    $scope.createSegment = function(segment) {
        $scope.resetError();
        $http.post('../job/segment/', segment).
            success(function(result){
                $scope.getAllSegments();
            }).
            error(function(result) {
                $scope.setError("Creating segment failed: " + result.message);
            });
    };

    $scope.deleteSegment = function(segmentId) {
        $scope.resetError();
        $http.delete('../job/segment/' + segmentId).
            success(function(result) {
                $scope.getAllSegments();
            }).
            error(function(result) {
                $scope.setError("Deleting segment " + segmentId + " failed: " + result.message);
            });
    };

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

    $scope.open = function (segmentToBeDeleted) {
        var modalInstance = $modal.open({
            templateUrl: 'DeleteModal.html',
            controller: ModalInstanceCtrl,
            resolve: {
                segment: function () {
                    return segmentToBeDeleted;
                }
            }
        });

        modalInstance.result.then(function (segment) {
            $log.info('Modal closed, delete segment ' + segment.id + ' here at: ' + new Date());
            $scope.deleteSegment(segment.id)
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

    $scope.getAllSegments();
    $scope.getAllTags();
};

var ModalInstanceCtrl = function ($scope, $modalInstance, segment) {
    $scope.segment = segment;

    $scope.ok = function () {
        $modalInstance.close($scope.segment);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
};
