/* jshint node: true */

'use strict';

var generictoolModule = angular.module('hod.generictool');


// #### ROUTES #### //
generictoolModule.config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {

  // define a route for the results operation
  $stateProvider.state({
    name: 'generictoolResults',
    url: '/result',
    title: 'Financial Status : Result',
    parent: 'generictool',
    views: {
      'content@': {
        templateUrl: 'modules/generictool/generictoolResult.html',
        controller: 'GenerictoolResultCtrl'
      },
    },
  });
}]);


generictoolModule.controller('GenerictoolResultCtrl', ['$scope', '$state', '$filter', 'GenerictoolService', function ($scope, $state, $filter, GenerictoolService) {

  var res = GenerictoolService.getLastAPIresponse();
  $scope.details = GenerictoolService.getDetails();

  var pounds = function (num) {
    return $filter('currency')(num, '£', 2);
  };

  var displayDate = function (d) {
    return moment(d).format('DD/MM/YYYY');
  };

  if (!res.status) {
    $state.go('generictool');
    return;
  }

  $scope.details.displayToDate = displayDate($scope.details.toDate);
  $scope.details.displayFromDate = displayDate($scope.details.fromDate);
  $scope.individual = res.data.individual;
  var incomes = [];
  _.each(res.data.incomes, function (inc) {
    incomes.push({
      payDate: displayDate(inc.payDate),
      employer: inc.employer,
      income: pounds(inc.income)
    });
  });
  $scope.incomes = incomes;
  $scope.total = pounds(res.data.total);

  $scope.haveResult = (incomes.length > 0) ? true: false;
  if (res.status === 404) {
    $scope.heading = 'There is no record for ' + $scope.details.nino + ' with HMRC';
    $scope.reason = 'We couldn\'t perform the financial requirement check as no income information exists with HMRC for the National Insurance Number ' + $scope.details.nino + '.';
  } else if (res.status !== 200) {
    $scope.heading = 'You can’t use this service just now. The problem will be fixed as soon as possible';
    $scope.reason = 'Please try again later.';
  }

  $scope.newSearch = function () {
    GenerictoolService.reset();
    $state.go('generictool');
  };

  console.log(res);
}]);