"use strict";

define([
    '../pimy',
    'jquery'
], function (app, $) {

    app.directive('fixed', function () {
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
                    css.left = pWidth - attrs.right;
                }
                if (attrs.left) {
                    css.left = pOffset.left + attrs.left;
                }
                if (attrs.top) {
                    css.top = pOffset.top + attrs.top;
                }
                if (attrs.bottom) {
                    css.bottom = attrs.bottom;
                }

                css.width = pWidth;
                if (attrs['parent-width']) {
                }

                console.log(css);
                fixedElem.css(css).hide();


                var mousePos = { y: -1, x: -1 };
                var buttonBarTimer;

                parent.on('mousemove', function (e) {
                    //@from EpicEditor
                    // Here we check if the mouse has moves more than 5px
                    // in any direction before triggering the mousemove code
                    // we do this for 2 reasons:
                    // 1. On Mac OS X lion when you scroll and it does
                    //    the iOS like "jump" when it hits the
                    //    top/bottom of the page it'll fire off a
                    //    mousemove of a few pixels depending on how hard you scroll
                    // 2. We give a slight buffer to the user in case he
                    //    barely touches his touchpad or mouse and not trigger the UI
                    if (Math.abs(mousePos.y - e.pageY) >= 5 || Math.abs(mousePos.x - e.pageX) >= 5) {
                        fixedElem.show();
                        if (buttonBarTimer) {
                            clearTimeout(buttonBarTimer);
                        }

                        buttonBarTimer = window.setTimeout(function () {
                            fixedElem.hide();
                        }, 1000);
                    }

                    mousePos = { y: e.pageY, x: e.pageX };
                });

                fixedElem.on('mouseover', function () {
                    if (buttonBarTimer) {
                        clearTimeout(buttonBarTimer);
                    }
                });
            }
        };
    });

    return app;
});