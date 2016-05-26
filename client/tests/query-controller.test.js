describe('coreController test suite', function(){

   var location;
   var coreController;
   var scope;
   var restService;
   var form;

   beforeEach(function() {module("app.core") });

   beforeEach(function () {
       restService = jasmine.createSpyObj('restService', [
         'checkApplication'
       ]);

       module(function ($provide) {
         $provide.value('restService', restService);
       });
     });

   beforeEach(module('templates'));

   beforeEach(inject(function($rootScope, $controller, $location, $anchorScroll, $q, $templateCache, $compile) {

            RestServicePromise = $q.defer();

            restService.checkApplication.and.returnValue({ $promise: RestServicePromise.promise });

            RestServicePromise.resolve('MOCK DATA');



       scope=$rootScope.$new();

       location = $location;



       coreController = $controller('coreController as vm', { '$scope' : scope, '$location' : location, 'restService' : restService, '$anchorScroll' : $anchorScroll } );

       templateHtml = $templateCache.get('client/views/income-proving-query.html')


        formElem = angular.element("<div>" + templateHtml + "</div>")
           $compile(formElem)(scope)
           form = scope.form

           scope.$apply()


   }));

  /* it('happy path', function(){
       //spyOn(restService, 'checkApplication');

       restService.checkApplication("AA", "2000-1-1", "2000-1-1")

       expect(coreController).toBe.defined
       expect(coreController.validateError).toBeFalsy();
       expect(restService.checkApplication).toHaveBeenCalled();
   });*/

   it('validate', function(){

          coreController.model = {
                      nino: 'AA121212A',
                      fromDateDay: '1',
                      fromDateMonth: '1',
                      fromDateYear: '2000',
                      toDateDay: '1',
                      toDateMonth: '1',
                      toDateYear: '2001',

                      total: '',
                      applicant: ''
                  };


                  coreController.submit();

                   expect(coreController.validateError).toBeTruthy();
                   expect(restService.checkApplication).toHaveBeenCalled();


    });

});


