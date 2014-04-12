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
                $scope.setError("Loading segments failed: " + result.content);
            });
    };

    $scope.createSegment = function(segment) {
        $scope.resetError();
        $http.put('../job/segment/' + segment).
            success(function(result){
                $scope.getAllSegments();
            }).
            error(function(result) {
                $scope.setError("Creating segment failed: " + result);
            });
    };

    $scope.deleteSegment = function(segmentId) {
        $scope.resetError();
        $http.delete('../job/segment/' + segmentId).
            success(function(result) {
                $scope.getAllSegments();
            }).
            error(function(result) {
                $scope.setError("Deleting segment " + segmentId + " failed: " + result);
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
