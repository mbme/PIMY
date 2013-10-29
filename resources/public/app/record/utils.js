"use strict";

var utils = angular.module('RecordUtils', ['restangular']);

utils.service('RecordsService', function (Restangular) {
    return Restangular.all('records');
});