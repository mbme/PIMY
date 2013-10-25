"use strict";

var app = angular.module("pimy", ["ngRoute", "RecordEditor", "restangular"]);

app.config(function ($locationProvider, $routeProvider, RestangularProvider) {
    $routeProvider
        .when('/', {
            pimy_menu_item: '',
            pimy_left_view: '',
            pimy_right_view: ''
        })
        .when('/records', {
            pimy_menu_item: 'records',
            pimy_left_view: '',
            pimy_right_view: ''
        })
        .when('/records/new', {
            pimy_menu_item: 'new_record',
            pimy_left_view: '/record/editor',
            pimy_right_view: '/record/viewer'
        })
        .otherwise({
            template: '<h1>NOT FOUND</h1>'
        });

    //to exclude # from links
    $locationProvider.html5Mode(true);

    RestangularProvider.setBaseUrl("/api");
});

//improved logger with interpolation
app.config(function ($provide) {
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


app.controller('MenuCtrl', function ($scope, $rootScope) {
    $rootScope.$on("$routeChangeStart", function (event, next) {
        $scope.menu = next.pimy_menu_item;
    });
});

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

var mbHrefName = 'pimyhref';
//opens specified address after click on element; attribute only
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