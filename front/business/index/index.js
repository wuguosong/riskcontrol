/**
 * Created by gaohe on 2016/06/09.
 */
bewg.config(
        ['$stateProvider', '$urlRouterProvider',
            function ($stateProvider, $urlRouterProvider) {

                $stateProvider
                    .state('index', {
                        // abstract: true,
                        url: '/index',
                        views: {
                            'index': {
                                templateUrl: 'html.html',
                                controller: 'homeCtrl'
                            }
                        },
                    })
                    .state('index.index', {
                        // abstract: true,
                        url: '/',
                        views: {
                            'business': {
                                templateUrl: '/business/index/common.html',
                                controller: 'indexCtrl'
                            }
                        },
                    })
            }
        ]
    );
