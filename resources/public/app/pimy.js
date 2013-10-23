"use strict";

var app = angular.module("pimy", ["ngRoute", "EditRecord", "restangular"]);

app.config(function ($locationProvider, $provide, $routeProvider, RestangularProvider) {
    $routeProvider
        .when('/', {
            template: '',
            menuItem: ''
        })
        .when('/records', {
            template: '<h1>records</h1>',
            menuItem: 'records'
        })
        .when('/records/new', {
            templateUrl: '/public/app/EditRecord/editor.tpl.html',
            controller: 'EditorCtrl',
            menuItem: 'new_record'
        })
        .otherwise({
            template: "<div class='text-center error'>NOT FOUND</div>",
            menuItem: ''
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

    RestangularProvider.setBaseUrl("/api");
});


app.controller('MenuCtrl', function ($scope, $rootScope) {
    $rootScope.$on("$routeChangeStart", function (event, next) {
        $scope.menu = next.menuItem;
    });
});

var mbHrefName = 'mbhref';
app.directive(mbHrefName, function () {
    return {
        restrict: 'A',
        controller: function ($element, $attrs, $location, $scope) {
            $element.on('click', function () {
                $scope.$apply(function () {
                    $location.url($attrs[mbHrefName]);
                });
            });
        }
    };
});