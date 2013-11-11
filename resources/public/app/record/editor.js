"use strict";

define([
    'angular',
    '../common/fixed'
], function (angular) {
    var editor = angular.module("RecordsEditor", []);

    editor.controller('RecordsEditorCtrl',
        function ($scope, $rootScope, $log, $element, $routeParams, Restangular, $location) {
            var recordId = $routeParams.recordId;
            if (recordId) {
                Restangular.one('records', recordId).get().then(function (data) {
                    $log.debug('loaded record {}', data.id);
                    $scope.record = data;
                }, function (response) {
                    $log.error(
                        'There was an error while loading record {}: {}\n{}',
                        recordId, response.status, response.body
                    );
                });
            } else {
                $scope.record = {
                    id: -1,
                    title: 'title asdf',
                    text: 'some text asdf',
                    tags: ['asdf1', '123']
                };
            }

            var notifyViewer = function (rec) {
                if (!rec) {
                    return;
                }
                $rootScope.$emit('rec-viewer:update', rec);
            };

            $scope.$watch('record', function (rec) {
                notifyViewer(rec);
                $rootScope.$emit('scrollable:update', $element, true);
            }, true);

            var cleanUp = $rootScope.$on('rec-viewer:ready', function () {
                notifyViewer($scope.record);
            });

            $scope.$on('$destroy', function () {
                $log.debug('destroying record editor');
                cleanUp();
            });

            $scope.save = function () {
                var successHandler = function () {
                    $log.debug('successfully saved record {}', recordId);
                    $location.url('/records');
                };
                var failureHandler = function (response) {
                    $log.error(
                        'There was an error while saving record {}: {}\n{}',
                        recordId, response.status, response.body
                    );
                };
                if ($scope.record.id === -1) {
                    Restangular.all('records').post($scope.record)
                        .then(successHandler, failureHandler);
                } else {
                    $scope.record.put()
                        .then(successHandler, failureHandler);
                }
            };
        });

    return editor;
});