(function () {
    'use strict';

    var core = angular.module('app.core');
    core.config(routeConfig);
    routeConfig.$inject = ['$routeProvider', '$locationProvider'];

    /* @ngInject */

    function routeConfig($routeProvider, $locationProvider) {
        $locationProvider.html5Mode(false);
        $routeProvider
            .when('/', {
                redirectTo: '/income-proving-tool'
            })
            .when('/income-proving-tool', {
                templateUrl : 'views/income-proving-tool.html'
            })
            .when('/income-proving-result', {
                templateUrl : 'views/income-proving-result.html'
            })
            .when('/income-proving-no-records', {
                templateUrl : 'views/income-proving-no-records.html'
            })
            .otherwise({
                templateUrl: 'views/404.html'
            });
    }
})();