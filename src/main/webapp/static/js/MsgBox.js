/**
 * @param Option{content, left, top, type, times, fn}
 * @constructor
 */
function MsgBox(Option) {
    this.content = Option.content;
    this.left = Option.left || 0;
    this.top = Option.top || 0;
    this.type = Option.type || MsgType.INFO;
    this.times = Option.times || 0;
    this.fn = Option.fn;
    this.dom = $("<div class='msg-box' style='display: none;'>"
        + "<div class='content'>" + this.content + "</div>"
        + "<div class='actions'>"
        + "<button type='button' class='close action'>×</button> "
        + "</div>"
        + "</div>");
    this.parent = $("body");
    this.parent.append(this.dom);
    if (this.type == MsgType.SUCCESS) {
        this.dom.addClass("success");
    } else if (this.type == MsgType.INFO) {
        this.dom.addClass("info")
    } else if (this.type == MsgType.WARNING) {
        this.dom.addClass("warning2")
    } else if (this.type == MsgType.DANGER) {
        this.dom.addClass("danger")
    } else {
        this.dom.addClass("info")
    }
    var w = 0;
    if (this.left) {
        var width = $(this.dom).outerWidth() + 7;
        var clientWidth = document.body.clientWidth;
        w = (this.left + width) > clientWidth ? (clientWidth - width) : this.left;

    } else {
        w = (($(window).width() - 100) / 2) + "px";
    }
    this.dom.css({"left": w, "top": this.top + "px"});
    this.dom.fadeIn();
    this.id = new Date().getTime();
    this.dom.attr("id", this.id);
    this.close = close;
    function close() {
        var box = $("body").find("#" + this.id);
        box.fadeOut(function () {
            box.remove();
        });
    }

    this.dom.find("button.close").unbind("click").bind("click", function () {
        var box = $(this).parents(".msg-box");
        box.fadeOut(function () {
            box.remove();
        });
    });
    if (this.times > 0) {
        MsgBox.delayClose(this);
    }
};

MsgBox.delayClose = function (m) {
    setTimeout(function () {
        var box = $("body").find("#" + m.id);
        box.fadeOut(function () {
            box.remove();
            if ($.isFunction(m.fn)) {
                m.fn();
            }
        });
    }, m.times);
};

MsgBox.info = function (Target, content, fn, times) {
    var targetOffset = $(Target).offset();
    var msgBox = new MsgBox({
        content: content,
        left: targetOffset.left - Common.docOffsetLeft(),
        top: targetOffset.top - Common.docOffsetTop() - $(Target).outerHeight(),
        type: MsgType.INFO,
        times: times || 2000,
        fn: fn
    });
    return msgBox;
};

MsgBox.success = function (Target, content, fn, times) {
    var targetOffset = $(Target).offset();
    var msgBox = new MsgBox({
        content: content,
        left: targetOffset.left - Common.docOffsetLeft(),
        top: targetOffset.top - Common.docOffsetTop() - $(Target).outerHeight(),
        type: MsgType.SUCCESS,
        times: times || 2000,
        fn: fn
    });
    return msgBox;
};

MsgBox.warning = function (Target, content, times, fn) {
    var targetOffset = $(Target).offset();
    var msgBox = new MsgBox({
        content: content,
        left: targetOffset.left - Common.docOffsetLeft(),
        top: targetOffset.top - Common.docOffsetTop() - $(Target).outerHeight(),
        type: MsgType.WARNING,
        times: times || 2000,
        fn: fn
    });
    return msgBox;
};

MsgBox.danger = function (Target, content, fn, times) {
    var targetOffset = $(Target).offset();
    var msgBox = new MsgBox({
        content: content,
        left: targetOffset.left - Common.docOffsetLeft(),
        top: targetOffset.top - Common.docOffsetTop() - $(Target).outerHeight(),
        type: MsgType.DANGER,
        times: times || 2000,
        fn: fn
    });
    return msgBox;
};

var MsgType = {
    INFO: 100,
    SUCCESS: 200,
    WARNING: 300,
    DANGER: 500,
};


/**
 * @param Option{target, content, left, top, type, times, fn}
 * @constructor
 */
function PopMsg(Option) {
    this.content = Option.content;
    this.left = Option.left || 0;
    this.top = Option.top || 0;
    this.type = Option.type || MsgType.INFO;
    this.times = Option.times || 0;
    this.fn = Option.fn;
    this.dom = $("<div class='pop-msg' style='display: none;'>"
        + "<div class='content'>" + this.content + "</div>"
        + "<div class='actions'>"
        + "<button type='button' class='close action'>×</button> "
        + "</div>"
        + "</div>");
    this.parent = $(Option.target);
    this.parent.after(this.dom);
    if (this.type == MsgType.SUCCESS) {
        this.dom.addClass("success");
    } else if (this.type == MsgType.INFO) {
        this.dom.addClass("info")
    } else if (this.type == MsgType.WARNING) {
        this.dom.addClass("warning2")
    } else if (this.type == MsgType.DANGER) {
        this.dom.addClass("danger")
    } else {
        this.dom.addClass("info")
    }
    var w = 0;
    if (this.left) {
        var width = $(this.dom).outerWidth() + 7;
        var clientWidth = document.body.clientWidth;
        w = (this.left + width) > clientWidth ? (clientWidth - width) : this.left;

    } else {
        w = (($(window).width() - 100) / 2) + "px";
    }
    this.dom.css({"left": w, "top": this.top + "px"});
    this.dom.fadeIn();
    this.id = new Date().getTime();
    this.dom.attr("id", this.id);
    this.close = close;
    function close() {
        var box = $("body").find("#" + this.id);
        box.fadeOut(function () {
            box.remove();
        });
    }

    this.dom.find("button.close").unbind("click").bind("click", function () {
        var box = $(this).parents(".msg-box");
        box.fadeOut(function () {
            box.remove();
        });
    });
    //if (this.times > 0) {
    //    PopMsg.delayClose(this);
    //}
};

PopMsg.delayClose = function (m) {
    setTimeout(function () {
        var box = $("body").find("#" + m.id);
        box.fadeOut(function () {
            box.remove();
            if ($.isFunction(m.fn)) {
                m.fn();
            }
        });
    }, m.times);
};

PopMsg.danger = function (Target, content, fn, times) {
    var targetOffset = $(Target).offset();
    var scrollTop = document.body.scrollTop;
    var scrollLeft = document.body.scrollLeft;
    var msgBox = new PopMsg({
        target: Target,
        content: content,
        left: targetOffset.left - scrollLeft,
        top: targetOffset.top - scrollTop - $(Target).outerHeight(),
        type: MsgType.DANGER,
        times: times || 2000,
        fn: fn
    });
    return msgBox;
};