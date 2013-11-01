"use strict";

var app = angular.module("pimy",
    ["ngRoute", "restangular",
        "RecordsEditor", "RecordsViewer", "RecordsList"]);

app.config(function ($locationProvider, $routeProvider, RestangularProvider) {
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

app.service('PimyScrollables', function ($log) {
    this.scrollables = [];

    this.add = function (elem) {
        $(elem).tinyscrollbar();
        this.scrollables.push(elem);
        $log.debug('Added 1 item to scrollables');
    };

    this.remove = function (elem) {
        var pos = _.indexOf(this.scrollables, elem);
        if (pos > -1) {
            this.scrollables.splice(pos, 1);
            $log.debug('Removed 1 item from scrollables');
        } else {
            $log.warn("Scrollables: can't find item which should be removed");
        }
    };
    //call update on each registered scrollable
    this.updateScrollables = function () {
        $log.debug('Updating {} scrollables', this.scrollables.length);
        _.each(this.scrollables, function (item) {
            $(item).tinyscrollbar_update();
        });
    };

    this.updateSingleScrollable = function (item) {
        $log.debug('Updating single scrollable');
        $(item).tinyscrollbar_update();
    };

    //subscribe to window resize event
    var self = this;
    $(window).resize(function () {
        self.updateScrollables();
    });
});

//directive to make divs have custom scrollbar
app.directive('pimyscroll', function (PimyScrollables) {
    return {
        restrict: 'E',
        templateUrl: '/public/app/pimyscroll.tpl.html',
        transclude: true,
        replace: true,
        link: function (scope, elem) {
            PimyScrollables.add(elem);
            scope.$on('update-scrollable', function () {
                PimyScrollables.updateSingleScrollable(elem);
            });
            scope.$on('$destroy', function () {
                PimyScrollables.remove(elem);
            });
        }
    };
});

//bootstrap icon
app.directive('icon', function () {
    return {
        restrict: 'E',
        template: '<i class="glyphicon glyphicon-{{ type }}"></i>',
        replace: true,
        scope: {
            type: '@'
        }
    };
});

app.directive('pimyFixed', function () {
    return {
        restrict: 'A',
        scope: {
            right: '@',
            top: '@'
        },
        link: function (scope, elem) {
            var fixedElem = $(elem);
            var parent = fixedElem.parent();

            fixedElem.css({
                position: 'fixed',
                'z-index': 1000,
                left: parent.width() - (scope.right || 10),
                top: scope.top || 10
            }).hide();


            var mousePos = { y: -1, x: -1 };
            var buttonBarTimer;

            parent.on('mousemove', function (e) {
                //@from EpicEditor
                // Here we check if the mouse has moves more than 5px in any direction before triggering the mousemove code
                // we do this for 2 reasons:
                // 1. On Mac OS X lion when you scroll and it does the iOS like "jump" when it hits the top/bottom of the page it'll fire off
                //    a mousemove of a few pixels depending on how hard you scroll
                // 2. We give a slight buffer to the user in case he barely touches his touchpad or mouse and not trigger the UI
                if (Math.abs(mousePos.y - e.pageY) >= 5 || Math.abs(mousePos.x - e.pageX) >= 5) {
                    fixedElem.show();
                    if (buttonBarTimer) {
                        clearTimeout(buttonBarTimer);
                    }

                    buttonBarTimer = window.setTimeout(function () {
                        fixedElem.hide();
                    }, 1000);
                }

                mousePos = { y: e.pageY, x: e.pageX };
            });

            fixedElem.on('mouseover', function () {
                if (buttonBarTimer) {
                    clearTimeout(buttonBarTimer);
                }
            });
        }
    };
});
