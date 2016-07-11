(function() {
    'use strict';

    angular
        .module('healthpointsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('points', {
            parent: 'entity',
            url: '/points',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'healthpointsApp.points.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/points/points.html',
                    controller: 'PointsController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('points');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('points-detail', {
            parent: 'entity',
            url: '/points/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'healthpointsApp.points.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/points/points-detail.html',
                    controller: 'PointsDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('points');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Points', function($stateParams, Points) {
                    return Points.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('points.new', {
            parent: 'points',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
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
                }).result.then(function() {
                    $state.go('points', null, { reload: true });
                }, function() {
                    $state.go('points');
                });
            }]
        })
        .state('points.edit', {
            parent: 'points',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/points/points-dialog.html',
                    controller: 'PointsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Points', function(Points) {
                            return Points.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('points', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('points.delete', {
            parent: 'points',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/points/points-delete-dialog.html',
                    controller: 'PointsDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Points', function(Points) {
                            return Points.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('points', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
