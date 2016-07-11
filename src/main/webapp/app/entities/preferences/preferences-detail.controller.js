(function() {
    'use strict';

    angular
        .module('healthpointsApp')
        .controller('PreferencesDetailController', PreferencesDetailController);

    PreferencesDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Preferences', 'User'];

    function PreferencesDetailController($scope, $rootScope, $stateParams, entity, Preferences, User) {
        var vm = this;

        vm.preferences = entity;

        var unsubscribe = $rootScope.$on('healthpointsApp:preferencesUpdate', function(event, result) {
            vm.preferences = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
