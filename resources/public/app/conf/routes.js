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
                reloadOnSearch: false,
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

    return app;
});