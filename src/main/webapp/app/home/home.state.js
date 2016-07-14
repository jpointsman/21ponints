(function () {
    'use strict';

    angular
        .module('healthpointsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('home', {
                parent: 'app',
                url: '/',
                data: {
                    authorities: []
                },
                views: {
                    'content@': {
                        templateUrl: 'app/home/home.html',
                        controller: 'HomeController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('home');
                        $translatePartialLoader.addPart('points');
                        $translatePartialLoader.addPart('bloodPressure');
                        return $translate.refresh();
                    }]
                }
            })
            .state('points.add', {
                parent: 'home',
                url: 'add/points',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/points/points-dialog.html',
                        controller: 'PointsDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    date: null,
                                    exercise: null,
                                    meals: null,
                                    alcohol: null,
                                    note: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function () {
                        $state.go('home', null, {reload: true});
                    }, function () {
                        $state.go('home');
                    });
                }]
            })
            .state('blood-pressure.add', {
                parent: 'home',
                url: 'add/blood-pressure',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/blood-pressure/blood-pressure-dialog.html',
                        controller: 'BloodPressureDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    timestamp: null,
                                    systolic: null,
                                    diastolic: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function() {
                        $state.go('home', null, { reload: true });
                    }, function() {
                        $state.go('home');
                    });
                }]
            });
    }
})();
