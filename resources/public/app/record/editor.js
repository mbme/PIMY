"use strict";

define([
    'angular',
    'lodash',
    'codemirror',
    'codemirror.markdown',
    'codemirror.active-line',
    '../common/fixed'
], function (angular, _, CodeMirror) {
    var editor = angular.module("RecordsEditor", []);

    var nthIndexOf = function (str, substr, n) {
        var times = 0,
            index = null;

        while (times < n && index !== -1) {
            index = str.indexOf(substr, index + 1);
            times++;
        }

        return index;
    };

    editor.directive('codeMirror', function ($timeout) {
        return {
            restrict: 'E',
            scope: {
                record: '=',
                updateInterval: '@'
            },
            link: function (scope, elem) {
                var updateInterval = scope.updateInterval || 1500;

                var cm = CodeMirror(elem[0], {
                    mode: 'markdown',
                    lineWrapping: true,
                    lineNumbers: false,
                    styleActiveLine: false
                });

                var initialized = false;

                var unWatchRecord = scope.$watch('record', function () {
                    var rec = scope.record;
                    if (!rec) {
                        return;
                    }
                    var val = rec.title + '\n\n' + rec.tags.join(', ') + '\n\n' + rec.text;
                    cm.setValue(val);

                    initialized = true;
                    unWatchRecord();
                }, true);

                var needsUpdate = false;
                cm.on('change', function () {
                    if (!initialized || needsUpdate) {
                        return;
                    }
                    needsUpdate = true;
                    $timeout(function () {
                        var text = cm.getValue();

                        var header = text.split('\n', 4);
                        if (header.length !== 4) {
                            //todo implement validation notification
                            console.log('WRONG TEXT');
                            console.log(header);
                            return;
                        }
                        scope.record.title = header[0].trim();
                        scope.record.tags = [];
                        _.each(header[2].split(','), function (val) {
                            var res = val.trim();
                            if (res) { // skip empty tags
                                scope.record.tags.push(res);
                            }
                        });

                        scope.record.text = text.substring(nthIndexOf(text, '\n', 4) + 1);

                        needsUpdate = false;
                    }, updateInterval);
                });
            }
        };
    });

    editor.controller('RecordsEditorCtrl',
        function ($scope, $rootScope, $log, $routeParams, Restangular, $location) {
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
                    title: '',
                    text: '',
                    tags: []
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