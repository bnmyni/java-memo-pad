(function ($) {
    $.rightMenu = function (box, options) {


        var menuId = "RightMenu";
        box = $(box);
        var $menuBox = [];

        box.bind("contextmenu", function () {
            return false
        });


        var witchs = {
            right:3,
            middle:2,
            left:1
        }


        box.mousedown(function (e) {
            e.preventDefault();
            e.stopPropagation();
            if (e.which == witchs[options.which]) {
                buildMenu(e);
                $menuBox = $("#" + menuId);
                setMenuEvent();
                $menuBox.data("source", box);
            }
        });


        function setMenuEvent() {
            $menuBox.bind("contextmenu", function () {
                return false
            });


            $(document).bind("click", _documentClick );


            var $menuLi = $menuBox.find("li").hover(function () {
                $(this).addClass("over");
            }, function () {
                $(this).removeClass("over");
            });
            $menuLi.each(function (i) {
                var menu = options.menus[i];
                if (menu.click) {
                    if(!menu.eventData) menu.eventData = {};


                    menu.eventData.source =  box;
                    $(this).click(menu.eventData, menu.click);
                    $(this).click(menu.eventData, function(){
                            $menuBox.hide();
                    });
                }
            });
        }


        function buildMenu(e) {
            var $menuBox = $("#" + menuId);
            if ($menuBox.length) {
                $menuBox.remove();
                $(document).unbind("click", _documentClick);
            }


            $menuBox = $("<div/>").attr("id", menuId);
            var $ul = $("<ul/>").appendTo($menuBox);
            for (var i in options.menus) {
                var menu = options.menus[i];
                $("<li/>").appendTo($ul).html(menu.name);
            }


            var pointX = e.pageX + 15;
            var pointY = e.pageY + 15;
            $menuBox.css({left:pointX, top:pointY});


            $("body").append($menuBox);
        }


        function _documentClick(e) {
            var isHide1 = $(e.target).parents("#" + menuId).length;
            var isHide2 = $(e.target).parents().andSelf().filter(box).length
            if (!isHide1 && !isHide2) {
                $menuBox.hide();
                $(document).unbind("click", _documentClick);
            }
        }


    };


    $.fn.extend({
        rightMenu:function (options) {
            var _options = {which:3, menus:[]};
            options = $.extend(_options, options);
            this.each(function () {
                new $.rightMenu(this, options);
            });
            return this;
        }
    });
})(jQuery); 