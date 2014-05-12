'use strict';

/**
 * MainController
 * @constructor
 */
var MainController = function($scope, $http) {
    $scope.getVersion = function() {
        $http.get('../version').success(function(result){
            $scope.version = result;
        });
    };
    $scope.getVersion();
};