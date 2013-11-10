"use strict";

define([
    'angular',
    './utils'
], function (angular) {
    var list = angular.module("RecordsList", ["RecordUtils"]);

    list.controller('RecordsListCtrl', function ($rootScope, $scope, $log, RecordsService, $q) {
        //marks record as active (selected)
        var setActive = function (record) {
            //if record is already selected then do nothing
            if ($scope.activeId === record.id) {
                return;
            }

            $scope.activeId = record.id;
            $rootScope.$emit('rec-viewer:update', record);
        };
        $scope.setActive = setActive;

        $scope.loader = function (offset, limit) {
            var deferred = $q.defer();

            RecordsService.getList({
                offset: offset,
                limit: limit
            }).then(function (data) {
                    var records = data.items;
                    $log.debug('Loaded {} records, {} total', records.length, data.total);
                    $scope.records = records;

                    //select first item
                    if (records.length > 0) {
                        setActive(records[0]);
                    }

                    deferred.resolve(data.total);
                }, function (response) {
                    $log.error('There was an error while saving: {}\n{}', response.status, response.body);
                    deferred.reject();
                });

            return deferred.promise;
        };
    });

    list.directive('sentinel', function () {
        return {
            restrict: 'A',
            link: function (scope) {
                scope.$watch('$last', function (val) {
                    if (val) {
                        scope.$emit('scrollable:update');
                    }
                });
            }
        };
    });

    return list;
});