(function() {
    'use strict';

    angular
        .module('healthpointsApp')
        .controller('PointsDetailController', PointsDetailController);

    PointsDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Points', 'User'];

    function PointsDetailController($scope, $rootScope, $stateParams, entity, Points, User) {
        var vm = this;

        vm.points = entity;

        var unsubscribe = $rootScope.$on('healthpointsApp:pointsUpdate', function(event, result) {
            vm.points = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
