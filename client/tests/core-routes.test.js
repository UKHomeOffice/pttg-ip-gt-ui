describe('Testing routes', function() {
beforeEach(module('app.core'));

var location, route, rootScope;

beforeEach(inject(
    function( _$location_, _$route_, _$rootScope_ ) {
        location = _$location_;
        route = _$route_;
        rootScope = _$rootScope_;
}));


describe('Starting route', function() {
    beforeEach(inject(
        function($httpBackend) {
            $httpBackend.expectGET('views/income-proving-query.html')
            .respond(200);
        }));

    it('should load the query page on entry', function() {
        location.path('/');
        rootScope.$digest();
        expect(route.current.templateUrl).toBe('views/income-proving-query.html')
    });
});

describe('Default (unrecognised) route', function() {
    beforeEach(inject(
        function($httpBackend) {
            $httpBackend.expectGET('views/404.html')
            .respond(200);
        }));

     it('should load 404 (not found) for unregistered urls', function() {
            location.path('/unknown');
            rootScope.$digest();
            expect(route.current.templateUrl).toBe('views/404.html')
        });
});

describe('Result route', function() {
    beforeEach(inject(
        function($httpBackend) {
            $httpBackend.expectGET('views/income-proving-result.html')
            .respond(200);
        }));

     it('should load results page', function() {
            location.path('/income-proving-result');
            rootScope.$digest();
            expect(route.current.templateUrl).toBe('views/income-proving-result.html')
        });
});

describe('No records route', function() {
    beforeEach(inject(
        function($httpBackend) {
            $httpBackend.expectGET('views/income-proving-no-records.html')
            .respond(200);
        }));

     it('should load no records page', function() {
            location.path('/income-proving-no-records');
            rootScope.$digest();
            expect(route.current.templateUrl).toBe('views/income-proving-no-records.html')
        });
});


});