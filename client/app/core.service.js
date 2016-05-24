(function () {
    'use strict';

    angular
        .module('app.core')
        .factory('restService', restService);

    restService.$inject = ['$http', '$q'];
    /* @ngInject */
    function restService($http, $q) {
        return {
            checkApplication : checkApplication
        };
        function checkApplication(nino, fromDate, toDate) {
            var url = 'incomeproving/v1/individual/'+nino+'/income';
            return $http.get(url, {
                                      params: { fromDate: fromDate, toDate: toDate }
                                  })

                .then(
                    function success(response) { return response.data },
                    function error(response) { throw response }
                );

        }
    }
})();