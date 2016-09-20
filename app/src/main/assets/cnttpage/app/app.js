'use strict';

// Declare app level module which depends on views, and components
angular.module('myApp', [
        'ngRoute',
        'ngResource',
        'myApp.view1',
        'myApp.view2',
        'myApp.version'
    ]).
    config(['$routeProvider', function($routeProvider) {
        $routeProvider.
            when('/login', {templateUrl: 'partials/login.html',   controller: LoginCtrl}).
            when('/loggedin', {templateUrl: 'partials/user-admin.html', controller: UserCtrl}).
            otherwise({redirectTo: '/login'})
        ;

    }], ['$locationProvider', function($locationProvider) {
        $locationProvider.html5Mode = true;
    }]).
    factory("User", function($resource) {
        return $resource("users/:userId.json", {}, {
        query: {method: "GET", params: {userId: "users"}, isArray: true}
        });
    })
;
