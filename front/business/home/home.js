/**
 * Created by gaohe on 2016/06/09.
 */
bewg.config(
        ['$stateProvider', '$urlRouterProvider',
            function ($stateProvider, $urlRouterProvider) {
                $urlRouterProvider
                // .when('/c?id', '/contacts/:id')
                // .when('/user/:id', '/contacts/:id')
                    .otherwise('/index/');
                $stateProvider

                    .state('logout', {
                        // abstract: true,
                        url: '/logout',
                        views: {
                            'index': {
                                template: '<h1>我要登陆啊 </h1>',
                                controller: 'logoutCtrl'
                            }
                        },
                    })
            }
        ]
    );
