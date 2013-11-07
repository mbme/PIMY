"use strict";

define([
    '../pimy',
    'text!./pagination.tpl.html'
], function (app, paginationTpl) {
    var ITEMS_PER_PAGE = 10;

    app.service('PaginationService', function ($log, $location, $rootScope) {
        var items = {
            offset: 0,
            limit: ITEMS_PER_PAGE,
            total: 0
        };
        $rootScope.pagination = items;

        $rootScope.$on('pagination:update', function (evt, total) {
            items.total = total;
            items.offset = $location.search().offset;
        });

        var updateSearch = function () {
            $location.search('offset', items.offset);
        };

        this.prev = function () {
            items.offset = items.offset >= items.limit ? items.offset - items.limit : 0;
            updateSearch();
            $log.debug('Loading prev page {}', items);
        };

        this.next = function () {
            items.offset = items.offset + items.limit >= items.total ? items.total - items.limit
                : items.offset + items.limit;
            updateSearch();
            $log.debug('Loading next page {}', items);
        };
    });

    app.directive('pagination', function () {
        return {
            restrict: 'E',
            scope: {
                loadItems: '&'
            },
            template: paginationTpl,
            replace: true,
            controller: function ($rootScope, $scope, PaginationService) {
                var page = {
                    current: 0,
                    total: 0,
                    hasPrev: function () {
                        return this.current > 1;
                    },
                    hasNext: function () {
                        return this.current < this.total;
                    },
                    prev: function () {
                        if (this.hasPrev()) {
                            PaginationService.prev();
                        }
                    },
                    next: function () {
                        if (this.hasNext()) {
                            PaginationService.next();
                        }
                    }
                };
                $scope.page = page;

                var handler = function (items) {
                    page.total = Math.ceil(items.total / items.limit) || 1;
                    page.current = Math.floor((items.offset + 1) / items.limit) + 1;
                };
                $rootScope.$watch('pagination', handler, true);
            }
        };
    });

    return app;
});