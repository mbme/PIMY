"use strict";

define([
    '../pimy',
    'jquery',
    'lodash',
    'jq-tinyscrollbar'
], function (app, $, _) {

    app.service('PimyScrollables', function ($log, $rootScope) {
        var scrollables = [];
        var self = this;

        this.add = function (elem) {
            $(elem).tinyscrollbar();
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

        //call update on each registered scrollable
        this.updateScrollables = function () {
            $log.debug('Updating {} scrollables', scrollables.length);
            _.each(scrollables, function (item) {
                $(item).tinyscrollbar_update();
            });
        };

        this.updateSingleScrollable = function ($item) {
            $log.debug('Updating single scrollable');
            $item.tinyscrollbar_update();
        };

        $rootScope.$on('scrollable:update', function (event, elem) {
            if (elem) {
                var scrollable = elem.closest('.pimyscroll');
                if (scrollable.length === 0) {
                    $log.warn("Can't find scrollable for {}.'{}'", elem.get(0).tagName, elem.attr('class'));
                    return;
                }

                self.updateSingleScrollable(scrollable);
            } else {
                self.updateScrollables();
            }
        });

        //also subscribe to window resize event
        $(window).resize(function () {
            self.updateScrollables();
        });
    });

    //directive to make divs have custom scrollbar
    app.directive('scroll', function (PimyScrollables) {
        return {
            restrict: 'E',
            templateUrl: '/public/app/common/scroll.tpl.html',
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