(function() {
    'use strict';

    angular
        .module('healthpointsApp')
        .controller('WeightDetailController', WeightDetailController);

    WeightDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Weight', 'User'];

    function WeightDetailController($scope, $rootScope, $stateParams, entity, Weight, User) {
        var vm = this;

        vm.weight = entity;

        var unsubscribe = $rootScope.$on('healthpointsApp:weightUpdate', function(event, result) {
            vm.weight = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
