"use strict";

define([
    'angular',
    'restangular'
], function (angular) {
    var utils = angular.module('RecordUtils', ['restangular']);

    utils.service('RecordsService', function (Restangular) {
        return Restangular.all('records');
    });

    return angular;
});