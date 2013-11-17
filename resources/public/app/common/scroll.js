"use strict";

define([
    '../pimy',
    'jquery',
    'lodash',
    'text!./scroll.tpl.html',
    'jq-tinyscrollbar'
], function (app, $, _, scrollTpl) {
    var constants = {
        SCROLLABLE_UPDATE_INTERVAL: 500
    };
    app.service('PimyScrollables', function ($log, $rootScope, $timeout) {
        var scrollables = [],
            self = this;

        this.add = function (elem) {
            elem.tinyscrollbar();
            scrollables.push(elem);
            $log.debug('Added item to scrollables');
        };

        this.remove = function (elem) {
            var pos = _.indexOf(scrollables, elem);
            if (pos > -1) {
                scrollables.splice(pos, 1);
                $log.debug('Removed 1 item from scrollables');
            } else {
                $log.warn("Scrollables: can't find item which should be removed");
            }
        };

        var updateAll = function () {
            $log.debug('Updating {} scrollables', scrollables.length);
            _.each(scrollables, function (item) {
                item.tinyscrollbar_update();
            });
        };

        var scrollablesUpdate = false;
        //call update on each registered scrollable
        this.updateScrollables = function (now) {
            if (now === true) {
                updateAll();
                return;
            }

            if (!scrollablesUpdate) {
                scrollablesUpdate = true;
                $timeout(function () {
                    updateAll();
                    scrollablesUpdate = false;
                }, constants.SCROLLABLE_UPDATE_INTERVAL, false);
            }
        };

        var updateOne = function ($item) {
            $log.debug('Updating single scrollable');
            $item.tinyscrollbar_update();
        };

        var singleScrollableUpdate = false;
        this.updateSingleScrollable = function ($item, now) {
            if (now === true) {
                updateOne($item);
                return;
            }

            if (!singleScrollableUpdate) {
                singleScrollableUpdate = true;
                $timeout(function () {
                    updateOne($item);
                    singleScrollableUpdate = false;
                }, constants.SCROLLABLE_UPDATE_INTERVAL, false);
            }
        };

        $rootScope.$on('scrollable:update', function (event, elem, now) {
            if (elem) {
                var scrollable = elem.closest('.pimyscroll');
                if (scrollable.length === 0) {
                    $log.warn("Can't find scrollable for {}.'{}'", elem.get(0).tagName, elem.attr('class'));
                    return;
                }

                self.updateSingleScrollable(scrollable, now);
            } else {
                self.updateScrollables(now);
            }
        });

        //also subscribe to window resize event
        $(window).resize(function () {
            self.updateScrollables();
        });
    });

    //directive to make divs to has custom scrollbar
    app.directive('scroll', function (PimyScrollables) {
        return {
            restrict: 'E',
            template: scrollTpl,
            transclude: true,
            replace: true,
            link: function (scope, elem) {
                PimyScrollables.add(elem);

                scope.$on('$destroy', function () {
                    PimyScrollables.remove(elem);
                });
            }
        };
    });
});