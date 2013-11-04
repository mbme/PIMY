"use strict";

define([
    '../pimy'
], function (app) {
    //main controller to manage 2 views
    app.controller('SectionsCtrl', function ($scope, $rootScope) {
        var prepareUrl = function (addr) {
            if (!addr) {
                return '';
            }
            return '/public/app' + addr + '.tpl.html';
        };
        $rootScope.$on("$routeChangeStart", function (event, next) {
            $scope.pimy_left_view = prepareUrl(next.pimy_left_view);
            $scope.pimy_right_view = prepareUrl(next.pimy_right_view);
        });
    });

    //updating menu on route changes
    app.controller('MenuCtrl', function ($scope, $rootScope) {
        $rootScope.$on("$routeChangeStart", function (event, next) {
            $scope.menu = next.pimy_menu_item;
        });
    });

    return app;
});