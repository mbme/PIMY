"use strict";

define([
    'require',
    'angular',

    './pimy',
    './conf/routes',
    './conf/logger',
    './conf/controllers',

    './common/directives',
    './common/scroll',

    './record/editor',
    './record/viewer',
    './record/list'
], function (require, angular) {
    require(['domReady!'], function (document) {
        /* everything is loaded...go! */
        angular.bootstrap(document, ['pimy']);
        angular.resumeBootstrap();
    });
});