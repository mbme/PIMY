"use strict";

define([
    '../pimy',
    'jquery',
    'lodash'
], function (app, $, _) {
    var constants = {
        BUTTON_TIMEOUT: 1000
    };
    app.directive('fixed', function ($log) {
        return {
            restrict: 'A',
            link: function (scope, elem, attrs) {
                if (attrs.right && attrs.left) {
                    throw "Can't use right & left attributes together";
                }
                if (attrs.top && attrs.bottom) {
                    throw "Can't use top & bottom attributes together";
                }

                var fixedElem = $(elem).addClass('fixed');
                var parent = fixedElem.parent();

                var css = {};

                var pOffset = parent.offset();
                var pWidth = parent.width();
                if (attrs.right) {
                    css.left = pWidth - _.parseInt(attrs.right);
                }
                if (attrs.left) {
                    css.left = pOffset.left + _.parseInt(attrs.left);
                }
                if (attrs.top) {
                    //sometimes top offset is below zero,
                    // so we should use 0 instead
                    css.top = (pOffset.top < 0 ? 0 : pOffset.top) + _.parseInt(attrs.top);
                }
                if (attrs.bottom) {
                    css.bottom = _.parseInt(attrs.bottom);
                }

                if (attrs.parentWidth) {
                    css.width = pWidth;
                }

                fixedElem.css(css).hide();

                var mousePos = { y: -1, x: -1 };
                var buttonBarTimer;

                /**
                 *  @from EpicEditor
                 * Here we check if the mouse has moves more than 5px
                 * in any direction before triggering the mousemove code
                 * we do this for 2 reasons:
                 * 1. On Mac OS X lion when you scroll and it does
                 * the iOS like "jump" when it hits the
                 * top/bottom of the page it'll fire off a
                 * mousemove of a few pixels depending on how hard you scroll
                 * 2. We give a slight buffer to the user in case he
                 * barely touches his touchpad or mouse and not trigger the UI
                 */
                var mouseMoveListener = function (e) {
                    if (Math.abs(mousePos.y - e.pageY) >= 5 || Math.abs(mousePos.x - e.pageX) >= 5) {
                        fixedElem.show();
                        if (buttonBarTimer) {
                            clearTimeout(buttonBarTimer);
                        }

                        buttonBarTimer = window.setTimeout(function () {
                            fixedElem.hide();
                        }, constants.BUTTON_TIMEOUT);
                    }

                    mousePos = { y: e.pageY, x: e.pageX };
                };

                parent.on('mousemove', mouseMoveListener);

                var mouseOverListener = function () {
                    if (buttonBarTimer) {
                        clearTimeout(buttonBarTimer);
                    }
                };
                fixedElem.on('mouseover', mouseOverListener);

                scope.$on('$destroy', function () {
                    $log.debug('destroying "fixed"');
                    parent.off('mousemove', null, mouseMoveListener);
                    fixedElem.off('mouseover', null, mouseOverListener);
                });
            }
        };
    });

    return app;
});