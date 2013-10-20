"use strict";

var app = angular.module("pimy", ["ngRoute", "EditRecord"]);

app.config(function ($locationProvider, $provide, $routeProvider) {
    $routeProvider.when('/records/new', {
        templateUrl: '/public/app/EditRecord/editor.tpl.html',
        controller: 'EditorCtrl'
    });

    //to exclude # from links
    $locationProvider.html5Mode(true);

    //improved logger with interpolation
    $provide.decorator('$log', function ($delegate) {
        var groupFinder = /(.)?\{\}/g;

        //creates logger with string interpolation
        var logInterpolation = function (log) {
            return function () {
                if (arguments.length === 0) {
                    throw 'Zero log arguments for ' + log;
                }
                if (arguments.length === 1) {
                    log(arguments[0]);
                    return;
                }
                var paramsCount = arguments.length - 1;

                var paramPos = 1;
                var args = [].slice.call(arguments);
                log(arguments[0].replace(groupFinder, function (str, firstChar) {
                    if (firstChar === '\\') {
                        return '{}';
                    }
                    if (paramPos > paramsCount) {
                        throw "To many params in template " + args[0];
                    }

                    var result = args[paramPos];
                    if (_.isObject(result)) {
                        result = JSON.stringify(result);
                    }

                    paramPos += 1;

                    return firstChar ? firstChar + result : result;
                }));
            };
        };

        $delegate.log = logInterpolation($delegate.log);
        $delegate.debug = $delegate.log;
        $delegate.info = logInterpolation($delegate.info);
        $delegate.warn = logInterpolation($delegate.warn);

        return $delegate;
    });
});


app.factory('menuService', function ($rootScope) {
    var menu = {
        new_record: false,
        records: false
    };

    $rootScope.$on('$routeChangeStart', function () {
        _.each(menu, function (value, key) {
            if (_.isFunction(value)) {
                return;
            }
            menu[key] = false;
        });
    });

    menu.activate = function (name) {
        _.each(menu, function (value, key) {
            if (_.isFunction(value)) {
                return;
            }
            if (key === name) {
                menu[key] = true;
            }
        });
    };
    return menu;
});

app.controller('MenuCtrl', function ($scope, $rootScope) {
    $rootScope.$watch('menuItem', function (val) {
        $scope.menu = val;
    });
});

