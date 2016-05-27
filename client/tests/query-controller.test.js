describe('coreController', function(){

    var location, coreController, form, restService, scope, response, deferred;

    beforeEach(module("app.core"));

    beforeEach(inject(function($rootScope, $controller, $location, $templateCache, $compile,  $anchorScroll, $q) {
        scope=$rootScope.$new();
        location = $location;
        q = $q;

        restService = {
            checkApplication : function(nino, applicationRaisedDate, dependants) {
            }
        };

        coreController = $controller('coreController as vm', { '$scope' : scope, '$location' : location, 'restService' : restService, '$anchorScroll' : $anchorScroll } );
        configureTemplates($compile, $templateCache);
    }));

    spyOnSuccessful = function(){
        spyOn(restService, 'checkApplication').and.callFake(function() {
            deferred = q.defer();
            deferred.resolve(response);
            return deferred.promise;
        });
    }

    spyOnNinoFailure = function(){
        spyOn(restService, 'checkApplication').and.callFake(function() {
            deferred = q.defer();
            var error = {status:400, data:{error:{code: "0001"}}};
            deferred.reject(error);
            return deferred.promise;
        });
    }

    configureTemplates = function($compile, $templateCache){
        templateHtml = $templateCache.get('client/views/income-proving-query.html')
        formElem = angular.element("<div>" + templateHtml + "</div>")
        $compile(formElem)(scope)
        form = scope.form
        scope.$apply()
    }

    it('is expected to be defined', function(){
        expect(coreController).toBeDefined();
    });

    it('is expected to get the from date in ISO format', function(){
        coreController.model.fromDateDay='1';
        coreController.model.fromDateMonth='2';
        coreController.model.fromDateYear='2015';
        expect(coreController.getFullFromDate()).toEqual('2015-02-01')
    });

    it('is expected to format the from date to DD/MM/YYYY', function(){
        coreController.model.fromDateDay='1';
        coreController.model.fromDateMonth='2';
        coreController.model.fromDateYear='2015';
        expect(coreController.formatFromDate()).toEqual('01/02/2015')
    });

    it('is expected to get the to date in ISO format', function(){
        coreController.model.toDateDay='1';
        coreController.model.toDateMonth='2';
        coreController.model.toDateYear='2015';
        expect(coreController.getFullToDate()).toEqual('2015-02-01')
    });

    it('is expected to format the to date to DD/MM/YYYY', function(){
        coreController.model.toDateDay='1';
        coreController.model.toDateMonth='2';
        coreController.model.toDateYear='2015';
        expect(coreController.formatToDate()).toEqual('01/02/2015')
    });

    it('is expected the form submits the correct data to the service', function() {
        spyOnSuccessful();

        coreController.model.fromDateDay='1';
        coreController.model.fromDateMonth='2';
        coreController.model.fromDateYear='2015';
        coreController.model.toDateDay='1';
        coreController.model.toDateMonth='2';
        coreController.model.toDateYear='2015';
        coreController.model.nino='AA123456A';
        coreController.submit()
        expect(restService.checkApplication).toHaveBeenCalled();
    });


   it('does not call service on validation failure - invalid from date', function(){
        spyOnSuccessful();

        coreController.model.fromDateDay='1';
        coreController.model.fromDateMonth='2000';
        coreController.model.fromDateYear='2015';
        coreController.model.toDateDay='1';
        coreController.model.toDateMonth='2';
        coreController.model.toDateYear='2015';
        coreController.model.nino='AA123456A';
        coreController.submit()

        expect(coreController.validateError).toBeTruthy();
        expect(restService.checkApplication.calls.count()).toBe(0);
    });

    it('does call service on validation success', function(){
        spyOnSuccessful();

        coreController.model.fromDateDay='1';
        coreController.model.fromDateMonth='2';
        coreController.model.fromDateYear='2015';
        coreController.model.toDateDay='1';
        coreController.model.toDateMonth='2';
        coreController.model.toDateYear='2015';
        coreController.model.nino='AA123456A';
        coreController.submit()

        expect(coreController.validateError).toBeFalsy();
        expect(restService.checkApplication).toHaveBeenCalled();
    });

    it('formats money to a precision of 2 decimal places with a pound sign', function(){
       expect(coreController.formatMoney(500)).toBe("£500.00");
    });

    it('sets returned data from service on the model ', function(){
       spyOnSuccessful();
       response = {individual : {forename: "Jane", surname: "Brown", nino:"AA121212A"}, total: "400", incomes: [{payDate:"2015-01-01", employer: "WHSmiths", income: 500}] };

       coreController.model.fromDateDay='1';
       coreController.model.fromDateMonth='2';
       coreController.model.fromDateYear='2015';
       coreController.model.toDateDay='1';
       coreController.model.toDateMonth='2';
       coreController.model.toDateYear='2015';
       coreController.model.nino='AA123456A';

       coreController.submit()
       scope.$digest()

       expect(coreController.model.total).toBe("400");
       expect(coreController.model.applicant.forename).toBe("Jane");
       expect(coreController.model.applicant.nino).toBe("AA121212A");
       expect(coreController.model.incomes[0].employer).toBe("WHSmiths");
       expect(coreController.model.incomes[0].income).toBe(500);
       expect(coreController.model.incomes[0].payDate).toBe("2015-01-01");
       expect(restService.checkApplication.calls.count()).toBe(1);
    });

    it('handles invalid nino error from service', function(){
       spyOnNinoFailure();

       coreController.model.fromDateDay='1';
       coreController.model.fromDateMonth='2';
       coreController.model.fromDateYear='2015';
       coreController.model.toDateDay='1';
       coreController.model.toDateMonth='2';
       coreController.model.toDateYear='2015';
       coreController.model.nino='AA123456A';

       coreController.submit()
       scope.$digest();

       expect(coreController.ninoInvalidError).toBeTruthy();
       expect(coreController.restError).toBeTruthy();

       expect(restService.checkApplication.calls.count()).toBe(1);
    });

});


