(function() {
    'use strict';

    angular
        .module('healthpointsApp')
        .controller('PreferencesDialogController', PreferencesDialogController);

    PreferencesDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Preferences', 'User'];

    function PreferencesDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Preferences, User) {
        var vm = this;

        vm.preferences = entity;
        vm.clear = clear;
        vm.save = save;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.preferences.id !== null) {
                Preferences.update(vm.preferences, onSaveSuccess, onSaveError);
            } else {
                Preferences.save(vm.preferences, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('healthpointsApp:preferencesUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
