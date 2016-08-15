(function() {
    'use strict';

    angular
        .module('app.core')
        .controller('coreController', coreController);

    coreController.$inject = ['$rootScope','$location','restService','$anchorScroll', '$log'];
    /* @ngInject */
    function coreController($rootScope, $location, restService, $anchorScroll, $log) {
        var vm = this;

        var NINO_REGEX = /^[a-zA-Z]{2}[0-9]{6}[a-dA-D]{1}$/;
        var CURRENCY_SYMBOL = '£';
        var DATE_DISPLAY_FORMAT = 'DD/MM/YYYY';
        var DATE_VALIDATE_FORMAT = 'YYYY-M-D';
        var INVALID_NINO_NUMBER = '0001';

        initialise();

        vm.formatMoney = function(moneyToFormat) {
            return accounting.formatMoney(moneyToFormat, { symbol: CURRENCY_SYMBOL, precision: 2});
        };

        vm.getFullDate = function (d, m, y) {
            var month = m.length > 1 ? m : '0' + m;
            var day = d.length > 1 ? d : '0' + d
            var result = y + '-' + month + '-' + day;
            return result;
        };

        vm.getFullFromDate = function() {
            return vm.getFullDate(vm.model.fromDateDay, vm.model.fromDateMonth, vm.model.fromDateYear);
        };

        vm.getFullToDate = function() {
            return vm.getFullDate(vm.model.toDateDay, vm.model.toDateMonth, vm.model.toDateYear);
        };

        vm.formatToDate = function() {
                  return vm.formatDate(vm.getFullToDate());
        }

        vm.formatFromDate = function() {
                  return vm.formatDate(vm.getFullFromDate());
        }

        vm.formatDate = function(dateToFormat) {
                  return moment(dateToFormat, DATE_VALIDATE_FORMAT, true).format("DD/MM/YYYY");
        }

        vm.scrollTo = function(anchor){
            $anchorScroll(anchor);
        };

        vm.submit = function() {

            if (validateForm()) {

                restService.checkApplication(vm.model.nino, vm.getFullFromDate(), vm.getFullToDate())
                    .then(function(data) {
                        vm.model.applicant = data.individual;
                        vm.model.incomes = data.incomes;
                        vm.model.total = data.total;
                        $location.path('/income-proving-result');
                    }).catch(function(error) {
                        $log.debug("received a non success result: " + error.status + " : " + error.statusText)
                        if (error.status === 404) {
                            $location.path('/income-proving-no-records');
                        } else {
                            vm.serverError = 'Unable to process your request, please try again.';
                            vm.serverErrorDetail = error.data.message;
                        }
                   });
             } else {
                vm.validateError = true;
             }
        };

        vm.newSearch = function() {
            initialise()
            $location.path('/income-proving-query');
        };

        function initialise() {
            vm.model = {
                nino: '',
                fromDateDay: '',
                fromDateMonth: '',
                fromDateYear: '',
                toDateDay: '',
                toDateMonth: '',
                toDateYear: '',

                total: '',
                applicant: ''
            };

            vm.validateError = false;
            vm.fromDateInvalidError = false;
            vm.fromDateMissingError = false;
            vm.toDateInvalidError = false;
            vm.toDateMissingError = false;

            vm.ninoMissingError = false;
            vm.ninoInvalidError = false;
            vm.ninoNotFoundError = false;

            vm.serverError = '';
            vm.serverErrorDetail = '';
        }

        function clearErrors() {
            vm.ninoNotFoundError = false;
            vm.ninoInvalidError = false;
            vm.ninoMissingError = false;

            vm.fromDateMissingError = false;
            vm.fromDateInvalidError = false;

            vm.toDateMissingError = false;
            vm.toDateInvalidError = false;

            vm.serverError = '';
            vm.serverErrorDetail = '';
            vm.validateError = false;
        }

        function validateForm(){
            var validated = true;
            clearErrors();

            vm.model.nino =  vm.model.nino.replace(/ /g,'');

            if (vm.model.nino === '') {
                vm.queryForm.nino.$setValidity(false);
                vm.ninoMissingError = true;
                validated =  false;
            } else {

                if (!NINO_REGEX.test(vm.model.nino)) {
                    vm.ninoInvalidError = true;
                    vm.queryForm.nino.$setValidity(false);
                    validated = false;
                }
            }

            if (vm.model.fromDateDay === null ||
                vm.model.fromDateMonth === null ||
                vm.model.fromDateYear === null  ) {
                vm.queryForm.fromDateDay.$setValidity(false);
                vm.queryForm.fromDateMonth.$setValidity(false);
                vm.queryForm.fromDateYear.$setValidity(false);
                vm.fromDateMissingError = true;
                validated = false;
            } else  if (!moment(vm.getFullFromDate(), DATE_VALIDATE_FORMAT, true).isValid()){
                vm.fromDateInvalidError = true;
                validated = false;
            }

            if (vm.model.toDateDay === null ||
                vm.model.toDateMonth === null ||
                vm.model.toDateYear === null  ) {
                vm.queryForm.toDateDay.$setValidity(false);
                vm.queryForm.toDateMonth.$setValidity(false);
                vm.queryForm.toDateYear.$setValidity(false);
                vm.toDateMissingError = true;
                validated = false;
            } else  if (!moment(vm.getFullToDate(), DATE_VALIDATE_FORMAT, true).isValid()){
                vm.toDateInvalidError = true;
                validated = false;
            }

            vm.model.nino = vm.model.nino.toUpperCase();
            return validated;
        }
    }

})();