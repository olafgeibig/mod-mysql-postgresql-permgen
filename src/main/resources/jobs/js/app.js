'use strict';

var AngularSpringApp = {};

var App = angular.module('AngularSpringApp', ['AngularSpringApp.filters', 'AngularSpringApp.services', 'AngularSpringApp.directives', 'ngRoute', 'ui.bootstrap']);

// Declare app level module which depends on filters, and services
App.config(['$routeProvider', function ($routeProvider) {

    $routeProvider.when('/', {
        templateUrl: 'html/main.html'
//        controller: MainController
    });

    $routeProvider.when('/push/tags', {
        templateUrl: 'html/push/tags.html',
        controller: PushController
    });

    $routeProvider.otherwise({redirectTo: '/'});
}]);
