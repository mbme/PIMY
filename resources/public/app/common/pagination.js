"use strict";

define([
    '../pimy',
    'text!./pagination.tpl.html',
    'angular',
    'lodash'
], function (app, paginationTpl, angular, _) {

    app.service('PaginationService', function ($log, $location, $rootScope) {
        //load initial value from search params
        var items = {
            offset: _.parseInt($location.search().offset) || 0,
            limit: _.parseInt($location.search().limit) || 10,
            total: 0
        };

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
            loader(items.offset, items.limit).then(function (res) {
                items.total = _.parseInt(res) || 0;
                $rootScope.$emit('pagination:update');
            });
        };

        var updatePagination = function () {
            updateSearch();
            loadResults();
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

        $rootScope.$on('pagination:cleanup', function () {
            $log.debug('cleaning up pagination url params');
            $location.search('offset', null);
            $location.search('limit', null);
        });
    });

    app.directive('pagination', function ($rootScope, $log, PaginationService) {
        return {
            restrict: 'E',
            scope: {
                'loader': '&'
            },
            template: paginationTpl,
            replace: true,
            link: function (scope) {
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
                scope.page = page;

                var cleanUp = $rootScope.$on('pagination:update', function () {
                    var items = PaginationService.getState();
                    page.total = Math.ceil(items.total / items.limit) || 1;
                    page.current = Math.floor(items.offset / items.limit) + 1;

                    //we're calculating current page based on offset,
                    // so we should check if offset was not too big
                    page.current = page.current > page.total ? page.total : page.current;
                });

                scope.$on('$destroy', function () {
                    $log.debug('destroying pagination');
                    cleanUp();
                });

                PaginationService.installPagination(scope);
            }
        };
    });

    return app;
});