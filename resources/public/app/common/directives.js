"use strict";

define([
    '../pimy',
    'marked',
    'humaneDate',
    'lodash'
], function (app, marked, humaneDate, _) {

    //opens specified address after click on element; attribute only
    var addrName = 'addr';
    app.directive(addrName, function ($location) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var clickListener = function () {
                    scope.$apply(function () {
                        $location.url(attrs[addrName]);
                    });
                };
                element.on('click', clickListener);

                scope.$on('$destroy', function () {
                    element.off('click', null, clickListener);
                });
            }
        };
    });

    //ionicons
    app.directive('icon', function () {
        return {
            restrict: 'E',
            template: '<i class="icon ion-{{ type }}"></i>',
            replace: true,
            scope: {
                type: '@'
            }
        };
    });

    app.directive('tag', function () {
        return {
            restrict: 'E',
            template: '<span class="tag">#{{ name }}</span>',
            replace: true,
            scope: {
                name: '@'
            }
        };
    });

    marked.setOptions({
        breaks: true,
        smartypants: true
    });

    //markdown compiler
    app.directive('markdown', function ($sce) {
        return {
            restrict: 'E',
            template: '<div class="text" ng-bind-html="md"></div>',
            scope: {
                data: '='
            },
            link: function (scope) {
                scope.$watch('data', function (newVal) {
                    if (!newVal) {
                        return;
                    }

                    scope.md = $sce.trustAsHtml(marked(newVal));
                });
            }
        };
    });

    //directive with possibility to show dates in relative format
    var timerUpdateInterval = 20*1000;
    app.directive('time', function ($interval) {
        return {
            restrict: 'E',
            scope: {
                datetime: '@'
            },
            link: function (scope, elem, attrs) {
                //if does not has attribute human-readable
                if (_.isUndefined(attrs.humanReadable)) {
                    return;
                }

                var update = function () {
                    var date = new Date(parseInt(scope.datetime, 10));
                    elem.text(humaneDate(date));
                };

                update();

                var promise = $interval(update, timerUpdateInterval);

                scope.$on('$destroy', function () {
                    $interval.cancel(promise);
                });
            }
        };
    });

    return app;
});