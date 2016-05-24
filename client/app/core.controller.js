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
        vm.dateInvalidError = false;
        vm.dateMissingError = false;
        vm.ninoMissingError = false;
        vm.ninoInvalidError = false;
        vm.ninoNotFoundError = false;
        vm.serverError = '';

        vm.formatAmount = function() {
            return accounting.formatMoney(vm.model.threshold.amount, { symbol: CURRENCY_SYMBOL, precision: 0});
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
            return moment(vm.getFullToDate(), DATE_VALIDATE_FORMAT, true).format("DD/MM/YYYY");
        }

        vm.formatFromDate = function() {
                    return moment(vm.getFullFromDate(), DATE_VALIDATE_FORMAT, true).format("DD/MM/YYYY");
        }

        vm.scrollTo = function(anchor){
            $anchorScroll(anchor);
        };

        vm.submit = function() {

            if (validateForm()) {

                restService.checkApplication(vm.model.nino, vm.getFullFromDate(), vm.getFullToDate())
                    .then(function(data) {
                        vm.model.applicant = data.applicant;
                        vm.model.total;
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
            $location.path('/income-proving-tool');
        };

        function clearErrors() {
            vm.ninoNotFoundError = false;
            vm.ninoInvalidError = false;
            vm.restError = false;
            vm.ninoMissingError = false;
            vm.dateMissingError = false;
            vm.dateInvalidError = false;
            vm.serverError = '';
            vm.validateError = false;
        }

        function validateForm(){
            var validated = true;
           /* clearErrors();

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
                vm.queryForm.applicationRaisedDateDay.$setValidity(false);
                vm.queryForm.applicationRaisedDateMonth.$setValidity(false);
                vm.queryForm.applicationRaisedDateYear.$setValidity(false);
                vm.dateMissingError = true;
                validated = false;
            } else  if (!moment(vm.getFullDate(), DATE_VALIDATE_FORMAT, true).isValid()){
                vm.dateInvalidError = true;
                validated = false;
            }

            if (vm.model.dependants !== null && !(/^\d{0,2}$/.test(vm.model.dependants))){
                vm.dependantsInvalidError = true;
                validated = false;
            }

            vm.model.nino = vm.model.nino.toUpperCase();*/
            return validated;
        }
    }

})();