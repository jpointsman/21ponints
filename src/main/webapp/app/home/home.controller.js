(function() {
    'use strict';

    angular
        .module('healthpointsApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'Points', 'Preferences', 'LoginService', '$state'];

    function HomeController ($scope, Principal, Points, Preferences, LoginService, $state) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.register = register;
        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });

        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }
        function register () {
            $state.go('register');
        }

        Preferences.user(function(data){
            vm.preferences = data;
        });

        Points.thisWeek(function(data){
            vm.pointsThisWeek = data;
            vm.pointsPersentag = (data.points / 21) * 100;
        });
        //Points.thisWeek({}, onSuccess, onError);
        //function onSuccess(data, headers) {
        //    vm.pointsThiWeek = data;
        //    vm.pointsPersentage = (data.points / 21) * 100;
        //}
        //function onError(error) {
        //    AlertService.error(error.data.message);
        //}
    }
})();
