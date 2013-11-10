"use strict";

define([
    '../pimy'
], function (app) {
    //main controller to manage 2 views
    app.controller('SectionsCtrl', function ($scope, $rootScope, $timeout) {
        var prepareUrl = function (addr) {
            if (!addr) {
                return '';
            }
            return '/public/app' + addr + '.tpl.html';
        };

        $rootScope.$on("$routeChangeStart", function (event, next, prev) {
            var pimyLeftView = prepareUrl(next.pimy_left_view);
            var leftChanged = !prev || next.pimy_left_view !== prev.pimy_left_view;
            if (leftChanged) {
                $scope.pimy_left_view = pimyLeftView;
            } else {
                $scope.pimy_left_view = null;
                $timeout(function () {
                    $scope.pimy_left_view = pimyLeftView;
                });
            }

            var pimyRightView = prepareUrl(next.pimy_right_view);
            var rightChanged = !prev || next.pimy_right_view !== prev.pimy_right_view;
            if (rightChanged) {
                $scope.pimy_right_view = pimyRightView;
            } else {
                $scope.pimy_right_view = null;
                $timeout(function () {
                    $scope.pimy_right_view = pimyRightView;
                });
            }

            $rootScope.pimy_options = next.pimy_options || {};
        });
    });

    //updating menu on route changes
    app.controller('MenuCtrl', function ($scope, $rootScope, $log) {
        var cleanUp = $rootScope.$on("$routeChangeStart", function (event, next) {
            $scope.menu = next.pimy_menu_item;
        });

        $scope.$on('$destroy', function () {
            $log.debug('destroying menu');
            cleanUp();
        });
    });

    return app;
});