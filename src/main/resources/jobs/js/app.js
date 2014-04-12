'use strict';

var AngularSpringApp = {};

var App = angular.module('AngularSpringApp', ['AngularSpringApp.filters', 'AngularSpringApp.services',
    'AngularSpringApp.directives', 'ngRoute', 'ui.bootstrap', 'ngCookies', 'http-auth-interceptor']);

// Declare app level module which depends on filters, and services
App.config(['$routeProvider', function ($routeProvider) {

    $routeProvider.when('/', {
        templateUrl: 'html/main.html',
        controller: MainController
    });

    $routeProvider.when('/push/tags', {
        templateUrl: 'html/push/tags.html',
        controller: TagController
    });

    $routeProvider.when('/push/segments', {
        templateUrl: 'html/push/segments.html',
        controller: SegmentController
    });

    $routeProvider.when('/push/tagging', {
        templateUrl: 'html/push/tagging.html',
        controller: TaggingController
    });

    $routeProvider.otherwise({redirectTo: '/'});
}]);

