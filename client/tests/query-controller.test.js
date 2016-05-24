describe('coreController', function(){
    var controller;

    beforeEach(function (){
        controller = angular.mocks.module('coreController');
    });

    it('should be created successfully', function () {
        expect(controller).to.be.defined;
    });

});