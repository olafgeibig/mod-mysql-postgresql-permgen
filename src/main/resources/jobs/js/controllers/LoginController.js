'use strict';

/**
 * LoginController
 * @constructor
 */
var LoginController = function($scope, $http, $modal, authService, credentialsService, $timeout, $log, $location) {

    var isShowing = false;

    $scope.$on('event:auth-loginRequired', function() {
        if (isShowing) {
            return;
        }

        // If we're in the process of hiding the modal, we need to wait for
        // all CSS animations to complete before showing the modal again.
        // Otherwise, we might end up with an invisible modal, making the whole
        // view rather unusable. I've been unable to control the transitions
        // between "showing", "shown", "hiding", and "hidden" tightly using
        // JQuery notifications without collecting more and more modal backdrops
        // in the DOM, so the dirty solution here is to simply wait a second
        // before showing the log-in dialog.
        isShowing = true;
        $timeout(function() {
            $scope.openLogin();
            isShowing = false;
        }, 1000);
    });

    $scope.$on('event:auth-loginConfirmed', function() {
        $scope.credentials.password = '';
    });

    $scope.credentials = { userName: '', password: '' };

    $scope.isLoggedIn = function() {
        return credentialsService.isLoggedIn();
    }

    $scope.getUser = function() {
        return credentialsService.getUser();
    }

    $scope.logOut = function() {
        credentialsService.clearCredentials();
        $location.path('/');
    }

    $scope.secureCheck = function() {
        $http.get('./job/foo').
            success(function(result) {
                $scope.secured = 'secured';
            }).
            error(function(result) {
                $scope.secured = 'not secured';
            });
    }
    //$scope.secureCheck();

    $scope.openLogin = function () {
        $log.info('Modal opened at: ' + new Date());
        var modalInstance = $modal.open({
            templateUrl: 'LoginModal.html',
            controller: LoginModalCtrl,
            resolve: {
                credentials: function () {
                    return $scope.credentials;
                }
            }
        });

        modalInstance.result.then(function (credentials) {
            //$log.info('Modal closed at: ' + new Date());
            credentialsService.setCredentials($scope.credentials.userName, $scope.credentials.password);
            authService.loginConfirmed();
        }, function () {
            //$log.info('Modal dismissed at: ' + new Date());
            authService.loginCancelled()
        });
    };
};


var LoginModalCtrl = function ($scope, $modalInstance, credentials) {
    $scope.credentials = credentials;

    $scope.ok = function () {
        $modalInstance.close($scope.credential);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
};
