(function() {
    'use strict';

    angular
        .module('app.core')
        .controller('coreController', coreController);

    coreController.$inject = ['$rootScope','$location','restService','$anchorScroll'];
    /* @ngInject */
    function coreController($rootScope, $location, restService, $anchorScroll) {
        var vm = this;

        var NINO_REGEX = /^[a-zA-Z]{2}[0-9]{6}[a-dA-D]{1}$/;
        var CURRENCY_SYMBOL = '£';
        var DATE_DISPLAY_FORMAT = 'DD/MM/YYYY';
        var DATE_VALIDATE_FORMAT = 'YYYY-M-D';
        var INVALID_NINO_NUMBER = '0001';

        /* has it*/

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

        vm.formatMoney = function(moneyToFormat) {
            return accounting.formatMoney(moneyToFormat, { symbol: CURRENCY_SYMBOL, precision: 2});
        };

        vm.getFullFromDate = function() {
                var month = vm.model.fromDateMonth > 9 ? vm.model.fromDateMonth : '0' + vm.model.fromDateMonth;
                var day = vm.model.fromDateDay > 9 ? vm.model.fromDateDay : '0' + vm.model.fromDateDay
                return vm.model.fromDateYear+'-'+month+'-'+day;
            return vm.model.fromDateYear+'-'+vm.model.fromDateMonth+'-'+vm.model.fromDateDay;
        };

        vm.getFullToDate = function() {
                    var month = vm.model.toDateMonth > 9 ? vm.model.toDateMonth : '0' + vm.model.toDateMonth;
                    var day = vm.model.toDateDay > 9 ? vm.model.toDateDay : '0' + vm.model.toDateDay
                    return vm.model.toDateYear+'-'+month+'-'+day;
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
                        if (error.status === 400 && error.data.error.code === INVALID_NINO_NUMBER){
                            vm.ninoInvalidError = true;
                            vm.restError = true;
                        } else if (error.status === 404) {
                            $location.path('/income-proving-no-records');
                        } else {
                            vm.serverError = 'Unable to process your request, please try again.';
                        }
                   });
             } else {
                vm.validateError = true;
             }
        };

        vm.newSearch = function() {
            $location.path('/income-proving-query');
        };

        function clearErrors() {
            vm.ninoNotFoundError = false;
            vm.ninoInvalidError = false;
            vm.restError = false;
            vm.ninoMissingError = false;
            vm.fromDateMissingError = false;
            vm.fromDateInvalidError = false;
            vm.toDateMissingError = false;
            vm.toDateInvalidError = false;
            vm.serverError = '';
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