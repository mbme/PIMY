"use strict";

define([
    'angular',
    'angular-route',
    'restangular'
], function (angular) {
    var app = angular.module("pimy", [
        "ngRoute",
        "restangular",

        "RecordsEditor",
        "RecordsViewer",
        "RecordsList"
    ]);

    app.config(function ($locationProvider) {
        //to exclude # from links
        $locationProvider.html5Mode(true);
    });

    app.config(function (RestangularProvider) {
        RestangularProvider.setBaseUrl("/api");

        RestangularProvider.setResponseInterceptor(function (data, operation, what, url, response) {
            //if we retrieve list of something then also
            // try to retrieve special header with total count of items
            if (operation === 'getList') {
                return {
                    items: data,
                    total: response.headers('X-Total-Count')
                };
            }
            return data;
        });
    });
    return  app;
});

