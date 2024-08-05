var Common = {
    SUCC: "success",
    ERROR: "error",
    NO_LOGIN: "no_login",
    NO_AUTH: "no_auth",
    NO_DATA: "no_data",
    FAIL: "failure",
    REDIRECT: "redirect",
    post: function (url, param, callBack) {
        $.post(url, param, function (json) {
            if (json.code == Common.NO_LOGIN) {
                if (json.data) {
                    Common.go(json.data);
                } else {
                    Common.go("/login");
                }
            } else if (json.code == Common.NO_AUTH) {
                Msg.warning("您没有些操作权限，请找主管授权！");
            } else if (json.code == Common.REDIRECT) {
                Common.go(json.url);
            } else {
                if ($.isFunction(callBack)) {
                    callBack(json);
                } else {
                    if (json.code == Common.SUCC) {
                        Msg.success(json.message);
                    } else {
                        Msg.danger(json.message);
                    }
                }
            }
        }, "json");
    },
    /**
     *
     * Option{url, param, success, error, obj}
     *
     */

    post2: function (Option) {
        $.post(Option.url, Option.param, function (json) {
            if (json.code == Common.NO_LOGIN) {
                Common.go("/login");
            } else if (json.code == Common.NO_AUTH) {
                Msg.warning("您没有些操作权限，请找主管授权！");
            } else if (json.code == Common.REDIRECT) {
                Common.go(json.url);
            } else {
                if (json.code == Common.SUCC) {
                    if ($.isFunction(Option.success)) {
                        Option.success(json);
                    } else {
                        Msg.success(json.message);
                    }
                } else {
                    if ($.isFunction(Option.error)) {
                        Option.error(json);
                    } else {
                        if (json.subCode == 100 && Option.obj) {
                            MsgBox.warning(Option.obj, json.message + "&nbsp;&nbsp;<a style=\"color: white;text-decoration:underline;\" href='/login.html'>登录</a>");
                        } else {
                            Msg.danger(json.message);
                        }
                    }
                }
            }
        }, "json");
    },
    //同步
    ajax: function (url, param, callBack) {
        $.ajax({
        	url : url,
        	async:false,
       		type : "POST",
       		data : param,
       		dataType : "json",
       		success : function(json,status){
       			if (json.code == Common.NO_LOGIN) {
                    if (json.data) {
                        Common.go(json.data);
                    } else {
                        Common.go("/login");
                    }
                } else if (json.code == Common.NO_AUTH) {
                    Msg.warning("您没有些操作权限，请找主管授权！");
                } else if (json.code == Common.REDIRECT) {
                    Common.go(json.url);
                } else {
                    if ($.isFunction(callBack)) {
                        callBack(json);
                    } else {
                        if (json.code == Common.SUCC) {
                            Msg.success(json.message);
                        } else {
                            Msg.danger(json.message);
                        }
                    }
                }
       	  	}
        });
    },
    back: function (btn) {
        $(btn).unbind("click").bind("click", function () {
            history.go(-1);
        });
    },
    backHistory: function () {
        history.go(-1);
    },
    go: function (url) {
        window.location.href = url;
    },
    enter: function (target, fn) {
        $(target).unbind("keydown").keydown(function (event) {
            if (event.keyCode == 13) {
                fn();
            }
        });
    },
    refresh: function () {
        window.location.reload();
    },
    hasAttr: function (obj, field) {
        return typeof($(obj).attr(field)) != "undefined";
    },
    attrEquals: function (obj, field, val) {
        return typeof($(obj).attr(field)) != "undefined" && $(obj).attr(field) == val;
    },
    random: function (maxleng) {
        parseInt(Math.random() * maxleng);
    },
    trim: function (str) {
        if (str != undefined) {
            str = str.replace(/(^\s*)|(\s*$)/g, "");
            return str;
        }
    },
    isNumber: function (str) {
        return Common.Rules.number.test(str);
    },
    isDigits: function (str) {
        return Common.Rules.digits.test(str);
    },
    ajaxForm: function (form, listURL, fn) {
        $(form).ajaxForm(function (json) {
            if ($.isFunction(fn)) {
                fn(json);
            } else {
                if (json.code == Common.SUCC) {
                    if (listURL) {
                        Common.go(listURL);
                    } else {
                        Msg.success(json.message, function () {
                            if (json.data) {
                                Common.go(json.data);
                            } else {
                                Common.refresh();
                            }
                        });
                    }
                } else {
                    Msg.danger(json.message);
                }
            }
        });
        //$(form).validate({
        //    success: function () {
        //        Common.post(this.$form.attr("action"), this.$form.serializeArray(), function (json) {
        //            if ($.isFunction(fn)) {
        //                fn(json);
        //            } else {
        //                if (json.code == Common.SUCC) {
        //                    Msg.success(json.message, function () {
        //                        if (json.data){
        //                            Common.go(json.data);
        //                        } else {
        //                            Common.go(listURL);
        //                        }
        //                    });
        //                } else {
        //                    Msg.danger(json.message);
        //                }
        //            }
        //        });
        //        return false;
        //    }
        //});
        $(form).find(".btn-save").unbind("click").bind("click", function () {
            $(form).submit();
        });
    },
    reset: function (form) {
        $(form).find("input[type!=button]").val("");
        $(form).find(".sui-dropdown .dropdown-inner a span").text("请选择");
        $(form).submit();
    },
    trim: function (v) {
        if (!v) return v;
        return v.replace(/^\s+/g, '').replace(/\s+$/g, '')
    },
    docOffset: function () {
        var scrollTop = document.documentElement.scrollTop || window.pageYOffset || document.body.scrollTop;
        var scrollLeft = document.documentElement.scrollLeft || window.pageXOffset || document.body.scrollLeft;
        return {top: scrollTop, left: scrollLeft};
    },
    docOffsetTop: function () {
        return document.documentElement.scrollTop || window.pageYOffset || document.body.scrollTop;
    },
    docOffsetLeft: function () {
        return document.documentElement.scrollLeft || window.pageXOffset || document.body.scrollLeft;
    },
    /*===================下载文件
     * options:{
     * url:'',  //下载地址
     * data:{name:value}, //要发送的数据
     * method:'post'
     * }
     */
    downLoadFile : function (options) {
        var config = $.extend(true, { method: 'post' }, options);
        var $iframe = $('<iframe id="down-file-iframe" />');
        var $form = $('<form target="down-file-iframe" method="' + config.method + '" />');
        $form.attr('action', config.url);
        for (var key in config.data) {
            $form.append('<input type="hidden" name="' + key + '" value="' + config.data[key] + '" />');
        }
        $iframe.append($form);
        $(document.body).append($iframe);
        $form[0].submit();
        $iframe.remove();
    }
};

Common.Rules = {
    number: /^\d+(\\.\d*)?$/,
    digits: /^\d+$/,
    mobile: /^0?1[3|4|5|7|8][0-9]\d{8,9}$/,
    tel: /^[+]{0,1}(\d){1,3}[ ]?([-]?((\d)|[ ]){1,11})+$/,
    email: /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
    zip: /^[1-9][0-9]{5}$/,
    date: /^[1|2]\d{3}-[0-2][0-9]-[0-3][0-9]$/,
    url: /(http|ftp|https):\/\/([\w-]+\.)?[\w-]+\.(com|net|cn|org|me|io|info)/
};

function Messager(content, msgType, times) {
    this.content = content;
    this.dom = $("<div class='messager' style='display: none;'>"
        + "<div class='messager-content'>" + this.content + "</div>"
        + "<div class='messager-actions'>"
        + "<button type='button' class='close action'>×</button> "
        + "</div>"
        + "</div>");
    this.parent = $("body");
    this.parent.append(this.dom);
    if (msgType == 1) {
        this.dom.addClass("messager-success");
    } else if (msgType == 2) {
        this.dom.addClass("messager-info")
    } else if (msgType == 3) {
        this.dom.addClass("messager-warning")
    } else if (msgType == 4) {
        this.dom.addClass("messager-danger")
    } else {
        this.dom.addClass("messager-info")
    }
    var w = (($(window).width() - 100) / 2) + "px";
    this.dom.css("left", w);
    this.dom.show();
    this.id = new Date().getTime();
    this.dom.attr("id", this.id)
    this.dom.find("button.close").unbind("click").bind("click", function () {
        $(this).parents(".messager").remove();
    });
};

var Msg = {
    success: function (content, fn) {
        var m = new Messager(content, 1);
        Msg.close(m, fn);
    },
    info: function (content, fn) {
        var m = new Messager(content, 2);
        Msg.close(m, fn);
    },
    warning: function (content, fn) {
        var m = new Messager(content, 3);
        Msg.close(m, fn);
    },
    danger: function (content, fn) {
        var m = new Messager(content, 4);
        Msg.close(m, fn);
    },
    close: function (m, fn) {
        if ($.isFunction(fn)) {
            $("body").find("#" + m.id).find("button.close").bind("click", function () {
                fn();
            });
        }
        setTimeout(function () {
            $("body").find("#" + m.id).remove();
            if ($.isFunction(fn)) {
                fn();
            }
        }, 2000);
    }
};

var Pagination = {
    init: function (func, pClass) {
        var form = (pClass || "") + " form.pagination-form";
        $(form).find(".sui-pagination ul>li>a").unbind("click").bind("click", function () {
            $(form).find("input[name='curNo']").val($(this).text());
            Pagination.query(form, func);
        });
        $(form).find(".sui-pagination ul>li.prev>a").unbind("click").bind("click", function () {
            var no = parseInt($(form).find("input[name='curNo']").val());
            if (no > 1) {
                $(form).find("input[name='curNo']").val(no - 1);
            }
            Pagination.query(form, func);
        });
        $(form).find(".sui-pagination ul>li.first>a").unbind("click").bind("click", function () {
            $(form).find("input[name='curNo']").val(1);
            Pagination.query(form, func);
        });
        $(form).find(".sui-pagination ul>li.next>a").unbind("click").bind("click", function () {
            var no = parseInt($(form).find("input[name='curNo']").val());
            var total = parseInt($(form).find("input[name='totalPage']").val());
            if (no < total) {
                $(form).find("input[name='curNo']").val(no + 1);
            }
            Pagination.query(form, func);
        });
        $(form).find(".sui-pagination ul>li.last>a").unbind("click").bind("click", function () {
            var total = parseInt($(form).find("input[name='totalPage']").val());
            $(form).find("input[name='curNo']").val(total);
            Pagination.query(form, func);
        });
        $(form).find("input.page-confirm").unbind("click").bind("click", function () {
            var v = $(form).find("input[name=curNo]").val();
            var v2 = $(form).find("input[name=perSize]").val();
            if (!Common.isDigits(v2)) {
                Msg.warning("请输入整数每页条数");
                return;
            }
            if (Common.isDigits(v)) {
                var total = parseInt($(form).find("input[name='totalPage']").val());
                if (parseInt(v) > total) {
                    Msg.warning("输入的页面码大于总页码");
                } else {
                    $(form).find("input[name='curNo']").val(v);
                    Pagination.query(form, func);
                }
            } else {
                Msg.warning("请输入整数页码");
            }
        });
        $(form).find(".sui-pagination ul>li.disabled>a").unbind("click");
    },
    query: function (form, func) {
        if ($.isFunction(func)) {
            func($(form).serializeArray());
        } else {
            $(form).submit();
        }
    }
};

var Web_Pagination = {
    init: function (func, pClass) {
        var form = (pClass || "") + " form.pagination-form";
        $(form).find(".sui-pagination ul>li>a").unbind("click").bind("click", function () {
            $(form).find("input[name='curNo']").val($(this).text());
            Web_Pagination.query($(this).parents(" form.pagination-form"), func);
        });
        $(form).find(".sui-pagination ul>li.prev>a").unbind("click").bind("click", function () {
            var _form = $(this).parents(" form.pagination-form");
            var no = parseInt($(_form).find("input[name='curNo']").val());
            if (no > 1) {
                $(_form).find("input[name='curNo']").val(no - 1);
            }
            Web_Pagination.query(_form, func);
        });
        $(form).find(".sui-pagination ul>li.first>a").unbind("click").bind("click", function () {
            var _form = $(this).parents(" form.pagination-form");
            $(_form).find("input[name='curNo']").val(1);
            Web_Pagination.query(_form, func);
        });
        $(form).find(".sui-pagination ul>li.next>a").unbind("click").bind("click", function () {
            var _form = $(this).parents(" form.pagination-form");
            var no = parseInt($(_form).find("input[name='curNo']").val());
            var total = parseInt($(_form).find("input[name='totalPage']").val());
            if (no < total) {
                $(_form).find("input[name='curNo']").val(no + 1);
            }
            Web_Pagination.query(_form, func);
        });
        $(form).find(".sui-pagination ul>li.last>a").unbind("click").bind("click", function () {
            var _form = $(this).parents(" form.pagination-form");
            var total = parseInt($(_form).find("input[name='totalPage']").val());
            $(_form).find("input[name='curNo']").val(total);
            Web_Pagination.query(_form, func);
        });
        $(form).find("input.page-confirm").unbind("click").bind("click", function () {
            var _form = $(this).parents(" form.pagination-form");
            var v = $(_form).find("input.page-num").val();
            if (Common.isNumber(v)) {
                var total = parseInt($(_form).find("input[name='totalPage']").val());
                if (parseInt(v) > total) {
                    Msg.warning("输入的页面码大于总页码");
                } else {
                    $(_form).find("input[name='curNo']").val(v);
                    Web_Pagination.query(_form, func);
                }
            }
        });
        $(form).find(".sui-pagination ul>li.disabled>a").unbind("click");
    },
    query: function (form, func) {
        if ($.isFunction(func)) {
            func($(form).serializeArray());
        } else {
            $(form).submit();
        }
    }
};

var SortQuery = {
    init: function (form) {
        $("a.btn-sort").unbind("click").bind("click", function () {
            var sort = "";
            var _thisName = $(this).attr("sort-name");
            $("a.btn-sort").each(function () {
                var i = $(this).children("i");
                if ($(this).attr("sort-name") == _thisName) {
                    if (i.hasClass("icon-caret-up")) {
                        sort += $(this).attr("sort-name") + "_" + "desc";
                    } else if (i.hasClass("icon-caret-down")) {
                    } else {
                        sort += $(this).attr("sort-name") + "_" + "asc";
                    }
                } else {
                    if (i.hasClass("icon-caret-up")) {
                        sort += $(this).attr("sort-name") + "_" + "asc";
                    } else if (i.hasClass("icon-caret-down")) {
                        sort += $(this).attr("sort-name") + "_" + "desc";
                    }
                }
                sort += " ";
            });
            $("input[name=sort]").val(sort);
            $(form).submit();
        });
    }
}

var DialogSel = function (Option) {
    var $this = $(this);
    $this.Options = Option || {
            targetId: '', title: '', url: '', form: '',
            selFn: function (data) {
            }, showFn: function () {
            }, okFn: function (data) {
            }, clearFn: function () {
            }
        };
    this.setUrl = function (url) {
        $this.Options.url = url;
    }
    this.getOptions = function () {
        return $this.Options;
    }
    this.getId = function () {
        return $this.Options.targetId;
    }
    $this.Options.params = Option.params || {};
    $($this.Options.targetId).on('show', function () {
        $('.sui-modal .modal-header .modal-title').html($this.Options.title);
    });
    $($this.Options.targetId).on('shown', function () {
        $('form.sel-form .btn-cancel').unbind("click").bind("click", function () {
            $($this.Options.targetId).modal('hide');
        });
        $('form.sel-form .btn-query').click(function () {
            $this.Options.params = $("form.sel-form").serializeArray();
            DialogSel.query($this);
        });
        $('form.sel-form .btn-clear').click(function () {
            if ($.isFunction($this.Options.clearFn)) {
                $this.Options.clearFn();
            }
            $($this.Options.targetId).modal('hide')
        });
        bindEvents($this);
        if ($.isFunction($this.Options.showFn)) {
            $this.Options.showFn();
        }
        ;
    });
};

DialogSel.query = function (sel) {
    Common.post(sel.Options.url, sel.Options.params, function (json) {
        $('.sui-modal .modal-body').find("table").remove();
        $('.sui-modal .modal-body').find(".pagination-form").remove();
        $('.sui-modal .modal-body').append(json.data);
        bindEvents(sel);
    });
};
function bindEvents(sel) {
    Pagination2.init(function (args) {
        sel.Options = args;
        DialogSel.query(sel);
    }, sel.Options);
    if (sel.Options.tdEvent) {
        $(sel.Options.targetId).find(".sel-table").find("tbody tr td").unbind("dbclick").bind("dblclick", function () {
            sel.Options.tdEvent($(this));
            $(sel.Options.targetId).modal('hide')
        });
    } else {
        $(sel.Options.targetId).find(".sel-table").find("tbody tr").unbind("dbclick").bind("dblclick", function () {
            if ($.isFunction(sel.Options.selFn)) {
                sel.Options.selFn($(this).find("a.btn-sel").attr("data"));
            }
            $(sel.Options.targetId).modal('hide')
        });
    }
    $(sel.Options.targetId).find(".sel-table").find("tbody tr td a.btn-sel").unbind("click").bind("click", function () {
        if ($.isFunction(sel.Options.selFn)) {
            sel.Options.selFn($(this).attr("data"));
        }
        $(sel.Options.targetId).modal('hide')
    });
    $("input[name=ck-id]").each(function () {
        $(this).prop("checked", false);
    });
    $(sel.Options.targetId).find("input[name='ck-all']").prop("checked", false).unbind("change").bind("change", function () {
        if (this.checked) {
            $("input[name=ck-id]").each(function () {
                $(this).prop("checked", true);
            });
        } else {
            $("input[name=ck-id]").each(function () {
                $(this).prop("checked", false);
            });
        }
    });
    $(sel.Options.targetId).find("input[name='ck-id']").unbind("change").bind("change", function () {
        if ($("input[name=ck-id]:checked").length == $("input[name=ck-id]").length) {
            $(sel.Options.targetId).find("input[name='ck-all']").prop("checked", true);
        } else {
            $(sel.Options.targetId).find("input[name='ck-all']").prop("checked", false);
        }
    });

    $(sel.Options.targetId).find("input.btn-ok").unbind("click").bind("click", function () {
        var result = new Array();
        $("input[name=ck-id]:checked").each(function () {
            result.push($.parseJSON($(this).attr("data")));
        });
        if ($.isFunction(sel.Options.okFn)) {
            sel.Options.okFn(result);
        }
        $(sel.Options.targetId).modal('hide')
    });
};

var Pagination2 = {
    init: function (func, Options) {
        var form = (Options.targetId || "") + " form.pagination-form";
        $(form).find(".sui-pagination ul>li>a").unbind("click").bind("click", function () {
            $(form).find("input[name='curNo']").val($(this).text());
            Pagination2.query(form, Options, func);
        });
        $(form).find(".sui-pagination ul>li.prev>a").unbind("click").bind("click", function () {
            var no = parseInt($(form).find("input[name='curNo']").val());
            if (no > 1) {
                $(form).find("input[name='curNo']").val(no - 1);
            }
            Pagination2.query(form, Options, func);
        });
        $(form).find(".sui-pagination ul>li.first>a").unbind("click").bind("click", function () {
            $(form).find("input[name='curNo']").val(1);
            Pagination2.query(form, Options, func);
        });
        $(form).find(".sui-pagination ul>li.next>a").unbind("click").bind("click", function () {
            var no = parseInt($(form).find("input[name='curNo']").val());
            var total = parseInt($(form).find("input[name='totalPage']").val());
            if (no < total) {
                $(form).find("input[name='curNo']").val(no + 1);
            }
            Pagination2.query(form, Options, func);
        });
        $(form).find(".sui-pagination ul>li.last>a").unbind("click").bind("click", function () {
            var total = parseInt($(form).find("input[name='totalPage']").val());
            $(form).find("input[name='curNo']").val(total);
            Pagination2.query(form, Options, func);
        });
        $(form).find(".sui-pagination ul>li.disabled>a").unbind("click");
    },
    query: function (form, Options, func) {
        if ($.isFunction(func)) {
            Options.params = $(form).serializeArray();
            func(Options);
        } else {
            $(form).submit();
        }
    }
};

//Option:{getId:a, width: w, content: c, callback: fn}
var Dialog = {
    show: function (Option) {
        var getid = Option.id || "#mydialog";
        var mask = Option.mask || "1";
        var width = Option.width || "80%";
        var detail = "";
        var title = Option.title || "对话框";
        var content = Option.content || "";
        var masklayout = $('<div class="dialog-mask"></div>');
        var hiddenCancel = !!Option.hiddenCancel;

        if (mask == "1") {
            $("body").append(masklayout);
        }
        detail = '<div class="dialog-win" style="position:fixed;width:' + width + ';z-index:11;">';
        if (getid != null) {
            detail = detail + $(getid).html();
        }
        detail = detail + '</div>';

        var win = $(detail);
        win.find(".dialog").addClass("open");
        $("body").append(win);
        var x = parseInt($(window).width() - win.outerWidth()) / 2;
        var y = parseInt($(window).height() - win.outerHeight()) / 2;
        if (y <= 10) {
            y = "10"
        }
        win.css({"left": x, "top": y});
        win.find(".dialog-head strong").text(title);
        win.find(".dialog-body").html(content);
        win.find(".dialog-close,.close").each(function () {
            $(this).click(function () {
                win.remove();
                $('.dialog-mask').remove();
            });
        });
        win.find(".dialog-ok").each(function () {
            $(this).click(function () {
                win.remove();
                $('.dialog-mask').remove();
                if ($.isFunction(Option.callback)) {
                    Option.callback();
                }
            });
        });
        if (hiddenCancel) {
            win.find(".dialog-close").hide();
        }
        masklayout.click(function () {
            win.remove();
            $(this).remove();
        });
    }
};

var Confirm = {
    show: function (Option) {
        var getid = Option.id || "#mydialog";
        var mask = Option.mask || "1";
        var width = Option.width || "80%";
        var detail = "";
        var title = Option.title || "对话框";
        var content = Option.content || "";
        var masklayout = $('<div class="dialog-mask"></div>');

        if (mask == "1") {
            $("body").append(masklayout);
        }
        detail = '<div class="dialog-win" style="position:fixed;width:' + width + ';z-index:11;">';
        if (getid != null) {
            detail = detail + $(getid).html();
        }
        detail = detail + '</div>';

        var win = $(detail);
        win.find(".dialog").addClass("open");
        $("body").append(win);
        var x = parseInt($(window).width() - win.outerWidth()) / 2;
        var y = parseInt($(window).height() - win.outerHeight()) / 2;
        if (y <= 10) {
            y = "10"
        }
        win.css({"left": x, "top": y});
        win.find(".dialog-head strong").text(title);
        win.find(".dialog-body").html(content);
        win.find(".dialog-close,.close").each(function () {
            $(this).click(function () {
                win.remove();
                $('.dialog-mask').remove();
            });
        });
        win.find(".dialog-ok").each(function () {
            $(this).click(function () {
                if ($.isFunction(Option.callback)) {
                    if (Option.callback(win)) {
                        win.remove();
                        $('.dialog-mask').remove();
                    }
                }
            });
        });
        masklayout.click(function () {
            win.remove();
            $(this).remove();
        });
    }
};

var Admin = {
    backList: function (btn, url) {
        $(btn).unbind("click").bind("click", function () {
            Common.go(url);
        });
    },
    menuClick: function (id) {
        Common.post("/admin/menu/switchMenu", {id: id}, function (json) {
            if (json.code = Common.SUCC) {
                Common.go(json.data);
            }
        });
    },
    dateSet: function () {
        $(".btn-today").unbind("click").bind("click", function () {
            var now = new Date();
            var endDate = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59, 59);
            $("input[name=createdFrom]").val(formatDate(now, "yyyy-MM-dd 00:00"));
            $("input[name=createdEnd]").val(formatDate(endDate, "yyyy-MM-dd 23:59"));
        });
        $(".btn-week").unbind("click").bind("click", function () {
            var now = new Date();
            var endDate = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59, 59);
            var lastWeek = addDate(endDate, -6);
            $("input[name=createdFrom]").val(formatDate(lastWeek, "yyyy-MM-dd 00:00"));
            $("input[name=createdEnd]").val(formatDate(endDate, "yyyy-MM-dd HH:mm"));
        });
        $(".btn-month").unbind("click").bind("click", function () {
            var now = new Date();
            var endDate = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59, 59);
            var lastMonth = addDate(endDate, -29);
            $("input[name=createdFrom]").val(formatDate(lastMonth, "yyyy-MM-dd 00:00"));
            $("input[name=createdEnd]").val(formatDate(endDate, "yyyy-MM-dd HH:mm"));
        });
    }
};

function formatDate(date, format) {
    if (!date) return;
    if (!format) format = "yyyy-MM-dd";
    switch(typeof date) {
        case "string":
            date = new Date(date.replace(/-/, "/"));
            break;
        case "number":
            date = new Date(date);
            break;
    }
    if (!date instanceof Date) return;
    var dict = {
        "yyyy": date.getFullYear(),
        "M": date.getMonth() + 1,
        "d": date.getDate(),
        "H": date.getHours(),
        "m": date.getMinutes(),
        "s": date.getSeconds(),
        "MM": ("" + (date.getMonth() + 101)).substr(1),
        "dd": ("" + (date.getDate() + 100)).substr(1),
        "HH": ("" + (date.getHours() + 100)).substr(1),
        "mm": ("" + (date.getMinutes() + 100)).substr(1),
        "ss": ("" + (date.getSeconds() + 100)).substr(1)
    };
    return format.replace(/(yyyy|MM?|dd?|HH?|ss?|mm?)/g, function() {
        return dict[arguments[0]];
    });
}

function addDate(date,days){
    var a = new Date(date)
    a = a.valueOf()
    a = a + days * 24 * 60 * 60 * 1000
    a = new Date(a)
    return a;
}

/*
 *名称:图片上传本地预览插件 v1.1
 *作者:周祥
 *时间:2013年11月26日
 *介绍:基于JQUERY扩展,图片上传预览插件 目前兼容浏览器(IE 谷歌 火狐) 不支持safari
 *插件网站:http://keleyi.com/keleyi/phtml/image/16.htm
 *参数说明: Img:图片ID;Width:预览宽度;Height:预览高度;ImgType:支持文件类型;Callback:选择文件显示图片后回调方法;
 *使用方法:
 <div>
 <img id="ImgPr" width="120" height="120" /></div>
 <input type="file" id="up" />
 把需要进行预览的IMG标签外 套一个DIV 然后给上传控件ID给予uploadPreview事件
 $("#up").uploadPreview({ Img: "ImgPr", Width: 120, Height: 120, ImgType: ["gif", "jpeg", "jpg", "bmp", "png"], Callback: function () { }});
 */
jQuery.fn.extend({
    uploadPreview: function (opts) {
        var _self = this,
            _this = $(this);
        opts = jQuery.extend({
            Img: "ImgPr",
            Width: 100,
            Height: 100,
            ImgType: ["gif", "jpeg", "jpg", "bmp", "png"],
            Callback: function () {}
        }, opts || {});
        _self.getObjectURL = function (file) {
            var url = null;
            if (window.createObjectURL != undefined) {
                url = window.createObjectURL(file)
            } else if (window.URL != undefined) {
                url = window.URL.createObjectURL(file)
            } else if (window.webkitURL != undefined) {
                url = window.webkitURL.createObjectURL(file)
            }
            return url
        };
        _this.change(function () {
            if (this.value) {
                if (!RegExp("\.(" + opts.ImgType.join("|") + ")$", "i").test(this.value.toLowerCase())) {
                    alert("选择文件错误,图片类型必须是" + opts.ImgType.join("，") + "中的一种");
                    this.value = "";
                    return false
                }
                if ( /msie/.test(navigator.userAgent.toLowerCase())) {
                    try {
                        $("#" + opts.Img).attr('src', _self.getObjectURL(this.files[0]))
                    } catch (e) {
                        var src = "";
                        var obj = $("#" + opts.Img);
                        var div = obj.parent("div")[0];
                        _self.select();
                        if (top != self) {
                            window.parent.document.body.focus()
                        } else {
                            _self.blur()
                        }
                        src = document.selection.createRange().text;
                        document.selection.empty();
                        obj.hide();
                        obj.parent("div").css({
                            'filter': 'progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale)',
                            'width': opts.Width + 'px',
                            'height': opts.Height + 'px'
                        });
                        div.filters.item("DXImageTransform.Microsoft.AlphaImageLoader").src = src
                    }
                } else {
                    $("#" + opts.Img).attr('src', _self.getObjectURL(this.files[0]))
                }
                opts.Callback()
            }
        })
    }
});

var dateFunc = {
    init:function(btn, type, start, end){
        $(btn).unbind("click").bind("click", function () {
            var now = new Date()
            var startDate;
            switch (type){
                case "today":
                    start.val(formatDate(now));
                    end.val(formatDate(now));
                    break;
                case "lastWeek":
                    startDate = addDate(now, - 6);
                    start.val(formatDate(startDate));
                    end.val(formatDate(now));
                    break;
                case "lastMonth":
                    startDate = addDate(now, - 29);
                    start.val(formatDate(startDate));
                    end.val(formatDate(now));
                    break;
            }
        });
    },

    coerceImg: function(str) {
        if (str == '质量问题') {
            Msg.danger(str + ' 需要上传图片');
            return true;
        } else if (str == '做工粗糙/有瑕疵') {
            Msg.danger(str + ' 需要上传图片');
            return true;
        } else if (str == '颜色/图案/款式与商品描述不符') {
            Msg.danger(str + ' 需要上传图片');
            return true;
        } else if (str == '大小/尺寸与商品描述不符') {
            Msg.danger(str + ' 需要上传图片');
            return true;
        } else if (str == '材质与商品描述不符') {
            Msg.danger(str + ' 需要上传图片');
            return true;
        } else if (str == '少件（含缺少配件）') {
            Msg.danger(str + ' 需要上传图片');
            return true;
        } else if (str == '卖家发错货') {
            Msg.danger(str + ' 需要上传图片');
            return true;
        } else if (str == '收到商品时有破损/污渍/变形') {
            Msg.danger(str + ' 需要上传图片');
            return true;
        }
        return false;
    }
};