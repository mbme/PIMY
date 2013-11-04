"use strict";

define([
    'angular',
    'angular-route',
    'restangular'
], function (angular) {
    return angular.module("pimy", [
        "ngRoute",
        "restangular",

        "RecordsEditor",
        "RecordsViewer",
        "RecordsList"
    ]);
});

