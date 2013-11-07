"use strict";

define([
    '../pimy',
    'text!./pagination.tpl.html',
    'angular'
], function (app, paginationTpl, angular) {
    var ITEMS_PER_PAGE = 10;

    app.service('PaginationService', function ($log, $location, $rootScope) {
        var items = {
            offset: 0,
            limit: ITEMS_PER_PAGE,
            total: 1000
        };

        //load initial value from search params
        items.offset = $location.search().offset || 0;

        //updates pagination attributes in url
        var updateSearch = function () {
            $location.search('offset', items.offset);
            $location.search('limit', items.limit);
        };

        var loader = null;
        this.installPagination = function ($scope) {
            //wrapper for applying function passed from parent scope
            loader = function (offset, limit) {
                return $scope.loader({
                    offset: offset,
                    limit: limit
                });
            };

            //uninstall pagination on scope destroy
            $scope.$on('$destroy', function () {
                loader = null;
                $log.debug('Successfully uninstalled pagination');
            });
            $log.debug('Initializing pagination {}', items);
            updatePagination();
            $log.debug('Successfully installed pagination');
        };

        //run loading results using passed loader function
        var loadResults = function () {
            if (loader === null) {
                $log.warn('Currently there is no registered loader');
                return;
            }
            items.total = loader(items.offset, items.limit);
        };

        var updatePagination = function () {
            updateSearch();
            loadResults();
            $rootScope.$emit('pagination:update');
        };

        //load previous page
        this.prev = function () {
            items.offset = items.offset >= items.limit ? items.offset - items.limit : 0;
            $log.debug('Loading prev page {}', items);
            updatePagination();
        };

        //load next page
        this.next = function () {
            items.offset = items.offset + items.limit >= items.total ? items.total - items.limit
                : items.offset + items.limit;
            $log.debug('Loading next page {}', items);
            updatePagination();
        };

        /**
         * returns clone of current pagination state.
         * */
        this.getState = function () {
            return angular.copy(items);
        };
    });

    app.directive('pagination', function () {
        return {
            restrict: 'E',
            scope: {
                'loader': '&'
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

                $rootScope.$on('pagination:update', function () {
                    var items = PaginationService.getState();
                    page.total = Math.ceil(items.total / items.limit) || 1;
                    page.current = Math.floor((items.offset + 1) / items.limit) + 1;
                });

                PaginationService.installPagination($scope);
            }
        };
    });

    return app;
});