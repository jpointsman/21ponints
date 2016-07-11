(function() {
    'use strict';

    angular
        .module('healthpointsApp')
        .controller('BloodPressureDetailController', BloodPressureDetailController);

    BloodPressureDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'BloodPressure', 'User'];

    function BloodPressureDetailController($scope, $rootScope, $stateParams, entity, BloodPressure, User) {
        var vm = this;

        vm.bloodPressure = entity;

        var unsubscribe = $rootScope.$on('healthpointsApp:bloodPressureUpdate', function(event, result) {
            vm.bloodPressure = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
