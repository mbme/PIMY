"use strict";

define([
    'angular'
], function (angular) {
    var viewer = angular.module("RecordsViewer", []);

    viewer.controller('RecordsViewerCtrl', function ($rootScope, $scope, $log, $timeout, $element, ModalService, Restangular) {
        $scope.isNew = function () {
            return !$scope.record || $scope.record.id < 0;
        };

        $scope.isEditMode = function () {
            return $rootScope.pimy_options.viewer_edit_mode;
        };

        var initialized = false;

        $scope.isInitialized = function () {
            return initialized;
        };

        $scope.delete = function () {
            var recId = $scope.record.id;
            $log.debug('Deleting record {}', recId);
            ModalService.dialog({
                title: 'Delete',
                text: 'Are you sure you want to delete record?'
            }).then(function (result) {
                    if (result === 'btn:ok') {
                        Restangular.one('records', recId).remove().then(
                            function () {
                                $log.info('Removed record {}', recId);
                                $rootScope.$emit('pagination:refresh');
                            },
                            function (response) {
                                $log.error(
                                    'There was an error while removing record {}: {}\n{}',
                                    recId, response.status, response.body
                                );
                            }
                        );
                    }
                });
        };

        var cleanUp = $rootScope.$on('rec-viewer:update', function (event, record) {
            $log.debug('previewing record {}', record.id);
            $scope.record = record;
            $scope.text = record.text;

            initialized = true;

            //DOM will be updated after next $digest, so we should trigger
            //scrollable update event after it too
            $timeout(function () {
                $rootScope.$emit('scrollable:update', $element, true);
            });
        });

        $scope.$on('$destroy', function () {
            $log.debug('destroying record viewer');
            cleanUp();
        });

        $rootScope.$emit('rec-viewer:ready');
    });

    return viewer;
});