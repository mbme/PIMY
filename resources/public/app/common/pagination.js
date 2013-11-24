"use strict";

define([
    '../pimy',
    'text!./pagination.tpl.html',
    'angular',
    'lodash'
], function (app, paginationTpl, angular, _) {
    var parseInt = function (val, def) {
        var res = _.parseInt(val) || def;

        return res > 0 ? res : def;
    };

    app.service('PaginationService', function ($log, $location, $rootScope) {
        var state = {
            offset: 0,
            limit: 20,
            total: -1,
            getCurrentPage: function () {
                var res = Math.floor(this.offset / this.limit) + 1;

                //we're calculating current page based on offset,
                // so we should check if offset was not too big
                var total = this.getTotalPages();
                return res > total ? total : res;
            },
            setCurrentPage: function (page) {
                var currentPage = page;
                if (currentPage < 1) {
                    currentPage = 1;
                }

                //if not initialized yet we should
                // allow to load any page
                if (this.total !== -1) {
                    var totalPages = this.getTotalPages();
                    if (currentPage > totalPages) {
                        currentPage = totalPages;
                    }
                }

                this.offset = (currentPage - 1) * this.limit;
            },
            nextPage: function () {
                this.setCurrentPage(this.getCurrentPage() + 1);
            },
            prevPage: function () {
                this.setCurrentPage(this.getCurrentPage() - 1);
            },
            getTotalPages: function () {
                return Math.ceil(this.total / this.limit) || 1;
            }
        };

        state.setCurrentPage(parseInt($location.search().page, 1));

        var loader = null;
        //run loading results using passed loader function
        var loadResults = function () {
            if (loader === null) {
                $log.warn('Currently there is no registered loader');
                return;
            }
            loader(state.offset, state.limit).then(function (res, loadedStr) {
                var firstLoad = state.total === -1;
                var loaded = parseInt(loadedStr, 0);

                state.total = parseInt(res, 0);

                //if there were some results, but pagination was
                // wrong and we loaded 0 results then trying to
                // load results with correct pagination
                if (firstLoad && loaded === 0) {
                    state.setCurrentPage(state.getCurrentPage());
                    loadResults();
                } else {
                    //updates pagination attribute in url
                    $location.search('page', state.getCurrentPage());

                    $rootScope.$emit('pagination:update');
                }
            });
        };

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
            $log.debug('Initializing pagination {}', state);
            loadResults();
            $log.debug('Successfully installed pagination');
        };

        //load previous page
        this.prevPage = function () {
            $log.debug('Loading prev page {}', state);
            state.prevPage();
            loadResults();
        };

        //load next page
        this.nextPage = function () {
            $log.debug('Loading next page {}', state);
            state.nextPage();
            loadResults();
        };

        //load custom page
        this.loadPage = function (page) {
            var pageNumber = parseInt(page, 1);
            $log.debug('Loading page {}\n{}', pageNumber, state);
            state.setCurrentPage(pageNumber);
            loadResults();
        };

        this.getState = function () {
            var res = _.pick(state, ['offset', 'limit', 'total']);
            res.totalPages = state.getTotalPages();
            res.currentPage = state.getCurrentPage();
            return  res;
        };

        $rootScope.$on('pagination:cleanup', function () {
            $log.debug('cleaning up pagination url params');
            $location.search('page', null);
        });

        var self = this;
        $rootScope.$on('pagination:refresh', function () {
            $log.debug('Refreshing list');
            if (state.total - 1 === state.offset) {
                self.prevPage();
            } else {
                loadResults();
            }
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
            link: function (scope, element) {
                element.parent().addClass('pagination-container');
                scope.hasPrevPage = function () {
                    return this.state && this.state.currentPage > 1;
                };
                scope.hasNextPage = function () {
                    return this.state && this.state.currentPage < this.state.totalPages;
                };
                scope.prevPage = function () {
                    if (this.hasPrevPage()) {
                        PaginationService.prevPage();
                    }
                };
                scope.nextPage = function () {
                    if (this.hasNextPage()) {
                        PaginationService.nextPage();
                    }
                };

                scope.currentPage = "";
                scope.keyPressHandler = function (event) {
                    if (event.keyCode === 13) {
                        event.target.blur();
                        PaginationService.loadPage(scope.currentPage);
                    }
                };

                var cleanUp = $rootScope.$on('pagination:update', function () {
                    scope.state = PaginationService.getState();
                    scope.currentPage = scope.state.currentPage;
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