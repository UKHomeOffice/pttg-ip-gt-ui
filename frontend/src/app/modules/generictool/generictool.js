/* jshint node: true */

'use strict';

var generictoolModule = angular.module('hod.generictool', ['ui.router']);


generictoolModule.factory('GenerictoolService', ['IOService', '$state', function (IOService, $state) {
  var lastAPIresponse = {};
  var details = {
    nino: '',
    toDate: '',
    fromDate: ''
  };

  this.submit = function (nino, fromDate, toDate) {
    IOService.get('individual/' + nino + '/income', {fromDate: fromDate, toDate: toDate}, {timeout: 5000 }).then(function (res) {
      // console.log(res);
      lastAPIresponse = res;
      $state.go('generictoolResults');
    }, function (res) {
      console.log(res);
      lastAPIresponse = res;
      $state.go('generictoolResults');
    });
  };

  this.getLastAPIresponse = function () {
    return lastAPIresponse;
  };

  this.getDetails = function () {
    return details;
  };

  this.reset = function () {
    details.nino = '';
    details.toDate = '';
    details.fromDate = '';
  };

  return this;
}]);


// #### ROUTES #### //
generictoolModule.config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {
  // define a route for the details of the form
  $stateProvider.state({
    name: 'generictool',
    url: '/generictool',
    title: 'Generic tool: Query',
    views: {
      'content@': {
        templateUrl: 'modules/generictool/generictool.html',
        controller: 'GenerictoolDetailsCtrl'
      },
    },
  });
}]);

// fill in the details of the form
generictoolModule.controller(
'GenerictoolDetailsCtrl', ['$rootScope', '$scope', '$state', '$stateParams', 'GenerictoolService', 'IOService', '$window', '$timeout',
function ($rootScope, $scope, $state, $stateParams, GenerictoolService, IOService, $window) {
  $scope.details = GenerictoolService.getDetails();

  $scope.conf = {
    nino: {
      validate: function (val) {

        if (val) {
          var v = val.replace(/[^a-zA-Z0-9]/g, '');
          if (/^[a-zA-Z]{2}[0-9]{6}[a-dA-D]{1}$/.test(v)) {
            return true;
          }
        }
        return { summary: 'The National Insurance Number is invalid', msg: 'Enter a valid National Insurance Number'};
      }
    },
    dependants: {
      required: false
    },
    toDate: {
      max: moment().format('YYYY-MM-DD'),
    }
  };

  $scope.detailsSubmit = function (isValid) {
    $scope.details.nino = ($scope.details.nino.replace(/[^a-zA-Z0-9]/g, '')).toUpperCase();
    if (isValid) {
      GenerictoolService.submit($scope.details.nino, $scope.details.fromDate, $scope.details.toDate);
    }
  };
}]);
