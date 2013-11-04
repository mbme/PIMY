"use strict";

define([
    '../pimy'
], function (app) {

    app.config(function ($routeProvider) {
        $routeProvider
            .when('/', {
                redirectTo: '/records'
            })
            .when('/records', {
                pimy_menu_item: 'records',
                pimy_left_view: '/record/list',
                pimy_right_view: '/record/viewer'
            })
            .when('/records/new', {
                pimy_menu_item: 'new_record',
                pimy_left_view: '/record/editor',
                pimy_right_view: '/record/viewer'
            })
            .otherwise({
                template: '<h1>NOT FOUND</h1>'
            });
    });

    app.config(function ($locationProvider) {
        //to exclude # from links
        $locationProvider.html5Mode(true);
    });

    app.config(function (RestangularProvider) {
        RestangularProvider.setBaseUrl("/api");
    });

    return app;
});