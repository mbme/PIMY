"use strict";

// let Angular know that we're bootstrapping manually
// https://github.com/angular/angular.js/commit/603fe0d19608ffe1915d8bc23bf412912e7ee1ac
window.name = "NG_DEFER_BOOTSTRAP!";

require.config({
    baseUrl: '/public',
    paths: {
        'domReady': 'lib/require.domReady',
        'text': 'lib/require.text',

        'angular': 'lib/angular',
        'angular-route': 'lib/angular-route',
        'restangular': 'lib/restangular',

        'jquery': 'lib/jquery-2.0.3',
        'jq-tinyscrollbar': 'lib/jquery.tinyscrollbar',
        'lodash': 'lib/lodash'
    },

    shim: {
        'angular': {
            exports: 'angular',
            deps: ['jquery']
        },

        'angular-route': {
            deps: ['angular']
        },

        'restangular': {
            deps: ['angular', 'lodash']
        },

        'jq-tinyscrollbar': {
            deps: ['jquery']
        }
    },

    deps: ['app/bootstrap', 'text']
});