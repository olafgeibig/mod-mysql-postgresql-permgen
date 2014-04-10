'use strict';

/**
 * MainController
 * @constructor
 */
var MainController = function($scope, $http) {
    $scope.meh = function() {
        $http.get('push/tagDevices').success(function(result){
            $scope.tags = result;
        });
    };
};