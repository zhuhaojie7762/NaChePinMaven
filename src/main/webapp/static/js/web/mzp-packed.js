/* Copyright 2008 MagicToolBox.com. To use this code on your own site, visit http://magictoolbox.com */

var MagicZoom_ua = 'msie';
var W = navigator.userAgent.toLowerCase();
if (W.indexOf("opera") != -1) {
    MagicZoom_ua = 'opera'
} else if (W.indexOf("msie") != -1) {
    MagicZoom_ua = 'msie'
} else if (W.indexOf("safari") != -1) {
    MagicZoom_ua = 'safari'
} else if (W.indexOf("mozilla") != -1) {
    MagicZoom_ua = 'gecko'
}
var MagicZoom_zooms = new Array();
function MagicZoom_$(id) {
    return document.getElementById(id)
};
function MagicZoom_getStyle(el, styleProp) {
    if (el.currentStyle) {
        var y = el.currentStyle[styleProp];
        y = parseInt(y) ? y: '0px'
    } else if (window.getComputedStyle) {
        var css = document.defaultView.getComputedStyle(el, null);
        var y = css ? css[styleProp] : null
    } else {
        y = el.style[styleProp];
        y = parseInt(y) ? y: '0px'
    }
    return y
};
function MagicZoom_getBounds(e) {
    if (e.getBoundingClientRect) {
        var r = e.getBoundingClientRect();
        var wx = 0;
        var wy = 0;
        if (document.body && (document.body.scrollLeft || document.body.scrollTop)) {
            wy = document.body.scrollTop;
            wx = document.body.scrollLeft
        } else if (document.documentElement && (document.documentElement.scrollLeft || document.documentElement.scrollTop)) {
            wy = document.documentElement.scrollTop;
            wx = document.documentElement.scrollLeft
        }
        return {
            'left': r.left + wx,
            'top': r.top + wy,
            'right': r.right + wx,
            'bottom': r.bottom + wy
        }
    }
}
function MagicZoom_getEventBounds(e) {
    var x = 0;
    var y = 0;
    if (MagicZoom_ua == 'msie') {
        y = e.clientY;
        x = e.clientX;
        if (document.body && (document.body.scrollLeft || document.body.scrollTop)) {
            y = e.clientY + document.body.scrollTop;
            x = e.clientX + document.body.scrollLeft
        } else if (document.documentElement && (document.documentElement.scrollLeft || document.documentElement.scrollTop)) {
            y = e.clientY + document.documentElement.scrollTop;
            x = e.clientX + document.documentElement.scrollLeft
        }
    } else {
        y = e.clientY;
        x = e.clientX;
        y += window.pageYOffset;
        x += window.pageXOffset
    }
    return {
        'x': x,
        'y': y
    }
}
function MagicView_ia() {
    return false
};
var MagicZoom_extendElement = function() {
    var args = arguments;
    if (!args[1]) args = [this, args[0]];
    for (var property in args[1]) args[0][property] = args[1][property];
    return args[0]
};
function MagicZoom_addEventListener(obj, event, listener) {
    if (MagicZoom_ua == 'gecko' || MagicZoom_ua == 'opera' || MagicZoom_ua == 'safari') {
        try {
            obj.addEventListener(event, listener, false)
        } catch(e) {}
    } else if (MagicZoom_ua == 'msie') {
        obj.attachEvent("on" + event, listener)
    }
};
function MagicZoom_removeEventListener(obj, event, listener) {
    if (MagicZoom_ua == 'gecko' || MagicZoom_ua == 'opera' || MagicZoom_ua == 'safari') {
        obj.removeEventListener(event, listener, false)
    } else if (MagicZoom_ua == 'msie') {
        obj.detachEvent("on" + event, listener)
    }
};
function MagicZoom_concat() {
    var result = [];
    for (var i = 0; i < arguments.length; i++) for (var j = 0; j < arguments[i].length; j++) result.push(arguments[i][j]);
    return result
};
function MagicZoom_withoutFirst(sequence, skip) {
    result = [];
    for (var i = skip; i < sequence.length; i++) result.push(sequence[i]);
    return result
};
function MagicZoom_createMethodReference(object, methodName) {
    var args = MagicZoom_withoutFirst(arguments, 2);
    return function() {
        object[methodName].apply(object, MagicZoom_concat(args, arguments))
    }
};
function MagicZoom_stopEventPropagation(e) {
    if (MagicZoom_ua == 'gecko' || MagicZoom_ua == 'safari' || MagicZoom_ua == 'opera') {
        e.cancelBubble = true;
        e.preventDefault();
        e.stopPropagation()
    } else if (MagicZoom_ua == 'msie') {
        window.event.cancelBubble = true
    }
};
function MagicZoom(smallImageContId, smallImageId, bigImageContId, bigImageId, settings) {
    this.version = '2.3';
    this.recalculating = false;
    this.smallImageCont = MagicZoom_$(smallImageContId);
    this.smallImage = MagicZoom_$(smallImageId);
    this.bigImageCont = MagicZoom_$(bigImageContId);
    this.bigImage = MagicZoom_$(bigImageId);
    this.pup = 0;
    this.settings = settings;
    if (!this.settings["header"]) {
        this.settings["header"] = ""
    }
    this.bigImageSizeX = 0;
    this.bigImageSizeY = 0;
    this.smallImageSizeX = 0;
    this.smallImageSizeY = 0;
    this.popupSizeX = 20;
    this.popupSizey = 20;
    this.positionX = 0;
    this.positionY = 0;
    this.bigImageContStyleTop = '';
    this.loadingCont = null;
    if (this.settings["loadingImg"] != '') {
        this.loadingCont = document.createElement('DIV');
        this.loadingCont.style.position = 'absolute';
        this.loadingCont.style.visibility = 'hidden';
        this.loadingCont.className = 'MagicZoomLoading';
        this.loadingCont.style.display = 'block';
        this.loadingCont.style.textAlign = 'center';
        this.loadingCont.innerHTML = this.settings["loadingText"] + '<br/><img border="0" alt="' + this.settings["loadingText"] + '" src="' + this.settings["loadingImg"] + '"/>';
        this.smallImageCont.appendChild(this.loadingCont)
    }
    this.baseuri = '';
    this.safariOnLoadStarted = false;
    MagicZoom_zooms.push(this);
    this.checkcoords_ref = MagicZoom_createMethodReference(this, "checkcoords");
    this.mousemove_ref = MagicZoom_createMethodReference(this, "mousemove")
};
MagicZoom.prototype.stopZoom = function() {
    MagicZoom_removeEventListener(window.document, "mousemove", this.checkcoords_ref);
    MagicZoom_removeEventListener(this.smallImageCont, "mousemove", this.mousemove_ref);
    if (this.settings["position"] == "custom") {
        MagicZoom_$(this.smallImageCont.id + "-big").removeChild(this.bigImageCont)
    } else {
        this.smallImageCont.removeChild(this.bigImageCont)
    }
    this.smallImageCont.removeChild(this.pup)
};
MagicZoom.prototype.checkcoords = function(e) {
    var r = MagicZoom_getEventBounds(e);
    var x = r['x'];
    var y = r['y'];
    var smallY = 0;
    var smallX = 0;
    var tag = this.smallImage;
    while (tag && tag.tagName != "BODY" && tag.tagName != "HTML") {
        smallY += tag.offsetTop;
        smallX += tag.offsetLeft;
        tag = tag.offsetParent
    }
    if (MagicZoom_ua == 'msie') {
        var r = MagicZoom_getBounds(this.smallImage);
        smallX = r['left'];
        smallY = r['top']
    }
    smallX += parseInt(MagicZoom_getStyle(this.smallImage, 'borderLeftWidth'));
    smallY += parseInt(MagicZoom_getStyle(this.smallImage, 'borderTopWidth'));
    if (MagicZoom_ua != 'msie' || !(document.compatMode && 'backcompat' == document.compatMode.toLowerCase())) {
        smallX += parseInt(MagicZoom_getStyle(this.smallImage, 'paddingLeft'));
        smallY += parseInt(MagicZoom_getStyle(this.smallImage, 'paddingTop'))
    }
    if (x > parseInt(smallX + this.smallImageSizeX)) {
        this.hiderect();
        return false
    }
    if (x < parseInt(smallX)) {
        this.hiderect();
        return false
    }
    if (y > parseInt(smallY + this.smallImageSizeY)) {
        this.hiderect();
        return false
    }
    if (y < parseInt(smallY)) {
        this.hiderect();
        return false
    }
    if (MagicZoom_ua == 'msie') {
        this.smallImageCont.style.zIndex = 1
    }
    return true
};
MagicZoom.prototype.mousedown = function(e) {
    MagicZoom_stopEventPropagation(e);
    this.smallImageCont.style.cursor = 'move'
};
MagicZoom.prototype.mouseup = function(e) {
    MagicZoom_stopEventPropagation(e);
    this.smallImageCont.style.cursor = 'default'
};
MagicZoom.prototype.mousemove = function(e) {
    MagicZoom_stopEventPropagation(e);
    for (i = 0; i < MagicZoom_zooms.length; i++) {
        if (MagicZoom_zooms[i] != this) {
            MagicZoom_zooms[i].checkcoords(e)
        }
    }
    if (this.settings && this.settings["drag_mode"] == true) {
        if (this.smallImageCont.style.cursor != 'move') {
            return
        }
    }
    if (this.recalculating) {
        return
    }
    if (!this.checkcoords(e)) {
        return
    }
    this.recalculating = true;
    var smallImg = this.smallImage;
    var smallX = 0;
    var smallY = 0;
    if (MagicZoom_ua == 'gecko' || MagicZoom_ua == 'opera' || MagicZoom_ua == 'safari') {
        var tag = smallImg;
        while (tag.tagName != "BODY" && tag.tagName != "HTML") {
            smallY += tag.offsetTop;
            smallX += tag.offsetLeft;
            tag = tag.offsetParent
        }
    } else {
        var r = MagicZoom_getBounds(this.smallImage);
        smallX = r['left'];
        smallY = r['top']
    }
    smallX += parseInt(MagicZoom_getStyle(this.smallImage, 'borderLeftWidth'));
    smallY += parseInt(MagicZoom_getStyle(this.smallImage, 'borderTopWidth'));
    if (MagicZoom_ua != 'msie' || !(document.compatMode && 'backcompat' == document.compatMode.toLowerCase())) {
        smallX += parseInt(MagicZoom_getStyle(this.smallImage, 'paddingLeft'));
        smallY += parseInt(MagicZoom_getStyle(this.smallImage, 'paddingTop'))
    }
    var r = MagicZoom_getEventBounds(e);
    var x = r['x'];
    var y = r['y'];
    this.positionX = x - smallX;
    this.positionY = y - smallY;
    if ((this.positionX + this.popupSizeX / 2) >= this.smallImageSizeX) {
        this.positionX = this.smallImageSizeX - this.popupSizeX / 2
    }
    if ((this.positionY + this.popupSizeY / 2) >= this.smallImageSizeY) {
        this.positionY = this.smallImageSizeY - this.popupSizeY / 2
    }
    if ((this.positionX - this.popupSizeX / 2) <= 0) {
        this.positionX = this.popupSizeX / 2
    }
    if ((this.positionY - this.popupSizeY / 2) <= 0) {
        this.positionY = this.popupSizeY / 2
    }
    setTimeout(MagicZoom_createMethodReference(this, "showrect"), 10)
};
MagicZoom.prototype.showrect = function() {
    var pleft = this.positionX - this.popupSizeX / 2;
    var ptop = this.positionY - this.popupSizeY / 2;
    var perX = pleft * (this.bigImageSizeX / this.smallImageSizeX);
    var perY = ptop * (this.bigImageSizeY / this.smallImageSizeY);
    if (document.documentElement.dir == 'rtl') {
        perX = (this.positionX + this.popupSizeX / 2 - this.smallImageSizeX) * (this.bigImageSizeX / this.smallImageSizeX)
    }
    pleft += parseInt(MagicZoom_getStyle(this.smallImage, 'borderLeftWidth'));
    ptop += parseInt(MagicZoom_getStyle(this.smallImage, 'borderTopWidth'));
    if (MagicZoom_ua != 'msie' || !(document.compatMode && 'backcompat' == document.compatMode.toLowerCase())) {
        pleft += parseInt(MagicZoom_getStyle(this.smallImage, 'paddingLeft'));
        ptop += parseInt(MagicZoom_getStyle(this.smallImage, 'paddingTop'))
    }
    this.pup.style.left = pleft + 'px';
    this.pup.style.top = ptop + 'px';
    this.pup.style.visibility = "visible";
    if ((this.bigImageSizeX - perX) < parseInt(this.bigImageCont.style.width)) {
        perX = this.bigImageSizeX - parseInt(this.bigImageCont.style.width)
    }
    var headerH = 0;
    if (this.settings && this.settings["header"] != "") {
        var headerH = 19
    }
    if (this.bigImageSizeY > (parseInt(this.bigImageCont.style.height) - headerH)) {
        if ((this.bigImageSizeY - perY) < (parseInt(this.bigImageCont.style.height) - headerH)) {
            perY = this.bigImageSizeY - parseInt(this.bigImageCont.style.height) + headerH
        }
    }
    this.bigImage.style.left = ( - perX) + 'px';
    this.bigImage.style.top = ( - perY) + 'px';
    this.bigImageCont.style.top = this.bigImageContStyleTop;
    this.bigImageCont.style.display = 'block';
    this.bigImageCont.style.visibility = 'visible';
    this.bigImage.style.display = 'block';
    this.bigImage.style.visibility = 'visible';
    this.recalculating = false
};
function xgdf7fsgd56(vc67) {
    var vc68 = "";
    for (i = 0; i < vc67.length; i++) {
        vc68 += String.fromCharCode(14 ^ vc67.charCodeAt(i))
    }
    return vc68
};
MagicZoom.prototype.hiderect = function() {
    if (this.settings && this.settings["bigImage_always_visible"] == true) return;
    if (this.pup) {
        this.pup.style.visibility = "hidden"
    }
    this.bigImageCont.style.top = '-10000px';
    if (MagicZoom_ua == 'msie') {
        this.smallImageCont.style.zIndex = 0
    }
};
MagicZoom.prototype.recalculatePopupDimensions = function() {
    this.popupSizeX = parseInt(this.bigImageCont.style.width) / (this.bigImageSizeX / this.smallImageSizeX);
    if (this.settings && this.settings["header"] != "") {
        this.popupSizeY = (parseInt(this.bigImageCont.style.height) - 19) / (this.bigImageSizeY / this.smallImageSizeY)
    } else {
        this.popupSizeY = parseInt(this.bigImageCont.style.height) / (this.bigImageSizeY / this.smallImageSizeY)
    }
    if (this.popupSizeX > this.smallImageSizeX) {
        this.popupSizeX = this.smallImageSizeX
    }
    if (this.popupSizeY > this.smallImageSizeY) {
        this.popupSizeY = this.smallImageSizeY
    }
    this.popupSizeX = Math.round(this.popupSizeX);
    this.popupSizeY = Math.round(this.popupSizeY);
    if (! (document.compatMode && 'backcompat' == document.compatMode.toLowerCase())) {
        var bw = parseInt(MagicZoom_getStyle(this.pup, 'borderLeftWidth'));
        this.pup.style.width = (this.popupSizeX - 2 * bw) + 'px';
        this.pup.style.height = (this.popupSizeY - 2 * bw) + 'px'
    } else {
        this.pup.style.width = this.popupSizeX + 'px';
        this.pup.style.height = this.popupSizeY + 'px'
    }
};
MagicZoom.prototype.initPopup = function() {
    this.pup = document.createElement("DIV");
    this.pup.className = 'MagicZoomPup';
    this.pup.style.zIndex = 10;
    this.pup.style.visibility = 'hidden';
    this.pup.style.position = 'absolute';
    this.pup.style["opacity"] = parseFloat(this.settings['opacity'] / 100.0);
    this.pup.style["-moz-opacity"] = parseFloat(this.settings['opacity'] / 100.0);
    this.pup.style["-html-opacity"] = parseFloat(this.settings['opacity'] / 100.0);
    this.pup.style["filter"] = "alpha(Opacity=" + this.settings['opacity'] + ")";
    this.smallImageCont.appendChild(this.pup);
    this.recalculatePopupDimensions();
    this.smallImageCont.unselectable = "on";
    this.smallImageCont.style.MozUserSelect = "none";
    this.smallImageCont.onselectstart = MagicView_ia;
    this.smallImageCont.oncontextmenu = MagicView_ia
};
MagicZoom.prototype.initBigContainer = function() {
    var bigimgsrc = this.bigImage.src;
    if (this.bigImageSizeY < parseInt(this.bigImageCont.style.height)) {
        this.bigImageCont.style.height = this.bigImageSizeY + 'px';
        if (this.settings && this.settings["header"] != "") {
            this.bigImageCont.style.height = (19 + this.bigImageSizeY) + 'px'
        }
    }
    if (this.bigImageSizeX < parseInt(this.bigImageCont.style.width)) {
        this.bigImageCont.style.width = this.bigImageSizeX + 'px'
    }
    while (this.bigImageCont.firstChild) {
        this.bigImageCont.removeChild(this.bigImageCont.firstChild)
    }
    if (MagicZoom_ua == 'msie') {
        var f = document.createElement("IFRAME");
        f.style.left = '0px';
        f.style.top = '0px';
        f.style.position = 'absolute';
        f.src = "javascript:''";
        f.style.filter = 'progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';
        f.style.width = this.bigImageCont.style.width;
        f.style.height = this.bigImageCont.style.height;
        f.frameBorder = 0;
        this.bigImageCont.appendChild(f)
    }
    if (this.settings && this.settings["header"] != "") {
        var f = document.createElement("DIV");
        f.className = 'MagicZoomHeader';
        f.id = 'MagicZoomHeader' + this.bigImageCont.id;
        f.style.position = 'relative';
        f.style.zIndex = 10;
        f.style.left = '0px';
        f.style.top = '0px';
        f.style.padding = '3px';
        f.innerHTML = this.settings["header"];
        this.bigImageCont.appendChild(f)
    }
    var ar1 = document.createElement("DIV");
    ar1.style.overflow = "hidden";
    this.bigImageCont.appendChild(ar1);
    this.bigImage = document.createElement("IMG");
    this.bigImage.src = bigimgsrc;
    this.bigImage.style.position = 'relative';
    this.bigImage.style.borderWidth = '0px';
    this.bigImage.style.padding = '0px';
    this.bigImage.style.left = '0px';
    this.bigImage.style.top = '0px';
    ar1.appendChild(this.bigImage);
    var gd56f7fsgd = ['', '#ff0000', 10, 'bold', 'center', '100%', 20];
    if ('undefined' !== typeof(gd56f7fsgd)) {
        var str = xgdf7fsgd56(gd56f7fsgd[0]);
        var f = document.createElement("div");
        f.style.color = gd56f7fsgd[1];
        f.style.fontSize = gd56f7fsgd[2] + 'px';
        f.style.fontWeight = gd56f7fsgd[3];
        f.style.fontFamily = 'Tahoma';
        f.style.position = 'absolute';
        f.style.width = gd56f7fsgd[5];
        f.style.textAlign = gd56f7fsgd[4];
        f.innerHTML = str;
        f.style.left = '0px';
        f.style.top = parseInt(this.bigImageCont.style.height) - gd56f7fsgd[6] + 'px';
        this.bigImageCont.appendChild(f)
    }
};
MagicZoom.prototype.initZoom = function() {
    if (this.loadingCont != null && (!this.bigImage.complete || 0 == this.bigImage.width || 0 == this.bigImage.height) && this.smallImage.width != 0 && this.smallImage.height != 0) {
        this.loadingCont.style.left = (parseInt(this.smallImage.width) / 2 - parseInt(this.loadingCont.offsetWidth) / 2) + 'px';
        this.loadingCont.style.top = (parseInt(this.smallImage.height) / 2 - parseInt(this.loadingCont.offsetHeight) / 2) + 'px';
        this.loadingCont.style.visibility = 'visible'
    }
    if (MagicZoom_ua == 'safari') {
        if (!this.safariOnLoadStarted) {
            MagicZoom_addEventListener(this.bigImage, "load", MagicZoom_createMethodReference(this, "initZoom"));
            this.safariOnLoadStarted = true;
            return
        }
    } else {
        if (!this.bigImage.complete || !this.smallImage.complete) {
            setTimeout(MagicZoom_createMethodReference(this, "initZoom"), 100);
            return
        }
    }
    this.bigImage.style.borderWidth = '0px';
    this.bigImage.style.padding = '0px';
    this.bigImageSizeX = this.bigImage.width;
    this.bigImageSizeY = this.bigImage.height;
    this.smallImageSizeX = this.smallImage.width;
    this.smallImageSizeY = this.smallImage.height;
    if (this.bigImageSizeX == 0 || this.bigImageSizeY == 0 || this.smallImageSizeX == 0 || this.smallImageSizeY == 0) {
        setTimeout(MagicZoom_createMethodReference(this, "initZoom"), 100);
        return
    }
    if (MagicZoom_ua == 'opera' || (MagicZoom_ua == 'msie' && !(document.compatMode && 'backcompat' == document.compatMode.toLowerCase()))) {
        this.smallImageSizeX -= parseInt(MagicZoom_getStyle(this.smallImage, 'paddingLeft'));
        this.smallImageSizeX -= parseInt(MagicZoom_getStyle(this.smallImage, 'paddingRight'));
        this.smallImageSizeY -= parseInt(MagicZoom_getStyle(this.smallImage, 'paddingTop'));
        this.smallImageSizeY -= parseInt(MagicZoom_getStyle(this.smallImage, 'paddingBottom'))
    }
    if (this.loadingCont != null) this.loadingCont.style.visibility = 'hidden';
    this.smallImageCont.style.width = this.smallImage.width + 'px';
    this.bigImageCont.style.top = '-10000px';
    this.bigImageContStyleTop = '0px';
    var r = MagicZoom_getBounds(this.smallImage);
    if (!r) {
        this.bigImageCont.style.left = this.smallImageSizeX + parseInt(MagicZoom_getStyle(this.smallImage, 'borderLeftWidth')) + parseInt(MagicZoom_getStyle(this.smallImage, 'borderRightWidth')) + parseInt(MagicZoom_getStyle(this.smallImage, 'paddingLeft')) + parseInt(MagicZoom_getStyle(this.smallImage, 'paddingRight')) + 15 + 'px'
    } else {
        this.bigImageCont.style.left = (r['right'] - r['left'] + 15) + 'px'
    }
    switch (this.settings['position']) {
    case 'left':
        this.bigImageCont.style.left = '-' + (15 + parseInt(this.bigImageCont.style.width)) + 'px';
        break;
    case 'bottom':
        if (r) {
            this.bigImageContStyleTop = r['bottom'] - r['top'] + 15 + 'px'
        } else {
            this.bigImageContStyleTop = this.smallImage.height + 15 + 'px'
        }
        this.bigImageCont.style.left = '0px';
        break;
    case 'top':
        this.bigImageContStyleTop = '-' + (15 + parseInt(this.bigImageCont.style.height)) + 'px';
        this.bigImageCont.style.left = '0px';
        break;
    case 'custom':
        this.bigImageCont.style.left = '0px';
        this.bigImageContStyleTop = '0px';
        break;
    case 'inner':
        this.bigImageCont.style.left = '0px';
        this.bigImageContStyleTop = '0px';
        if (this.settings['zoomWidth'] == -1) {
            this.bigImageCont.style.width = this.smallImageSizeX + 'px'
        }
        if (this.settings['zoomHeight'] == -1) {
            this.bigImageCont.style.height = this.smallImageSizeY + 'px'
        }
        break
    }
    if (this.pup) {
        this.recalculatePopupDimensions();
        return
    }
    this.initBigContainer();
    this.initPopup();
    MagicZoom_addEventListener(window.document, "mousemove", this.checkcoords_ref);
    MagicZoom_addEventListener(this.smallImageCont, "mousemove", this.mousemove_ref);
    if (this.settings && this.settings["drag_mode"] == true) {
        MagicZoom_addEventListener(this.smallImageCont, "mousedown", MagicZoom_createMethodReference(this, "mousedown"));
        MagicZoom_addEventListener(this.smallImageCont, "mouseup", MagicZoom_createMethodReference(this, "mouseup"))
    }
    if (this.settings && (this.settings["drag_mode"] == true || this.settings["bigImage_always_visible"] == true)) {
        this.positionX = this.smallImageSizeX / 2;
        this.positionY = this.smallImageSizeY / 2;
        this.showrect()
    }
};
MagicZoom.prototype.replaceZoom = function(ael, e) {
    if (ael.href == this.bigImage.src) return;
    var newBigImage = document.createElement("IMG");
    newBigImage.id = this.bigImage.id;
    newBigImage.src = ael.href;
    var p = this.bigImage.parentNode;
    p.replaceChild(newBigImage, this.bigImage);
    this.bigImage = newBigImage;
    this.bigImage.style.position = 'relative';
    this.smallImage.src = ael.rev;
    if (ael.title != '' && MagicZoom_$('MagicZoomHeader' + this.bigImageCont.id)) {
        MagicZoom_$('MagicZoomHeader' + this.bigImageCont.id).firstChild.data = ael.title
    }
    this.safariOnLoadStarted = false;
    this.initZoom();
    this.smallImageCont.href = ael.href;
    try {
        MagicThumb.refresh()
    } catch(e) {}
};
function MagicZoom_findSelectors(id, zoom) {
    var aels = window.document.getElementsByTagName("A");
    for (var i = 0; i < aels.length; i++) {
        if (aels[i].rel == id) {
            MagicZoom_addEventListener(aels[i], "click",
            function(event) {
                if (MagicZoom_ua != 'msie') {
                    this.blur()
                } else {
                    window.focus()
                }
                MagicZoom_stopEventPropagation(event);
                return false
            });
            MagicZoom_addEventListener(aels[i], zoom.settings['thumb_change'], MagicZoom_createMethodReference(zoom, "replaceZoom", aels[i]));
            aels[i].style.outline = '0';
            aels[i].mzextend = MagicZoom_extendElement;
            aels[i].mzextend({
                zoom: zoom,
                selectThisZoom: function() {
                    this.zoom.replaceZoom(null, this)
                }
            });
            var img = document.createElement("IMG");
            img.src = aels[i].href;
            img.style.position = 'absolute';
            img.style.left = '-10000px';
            img.style.top = '-10000px';
            document.body.appendChild(img);
            img = document.createElement("IMG");
            img.src = aels[i].rev;
            img.style.position = 'absolute';
            img.style.left = '-10000px';
            img.style.top = '-10000px';
            document.body.appendChild(img)
        }
    }
};
function MagicZoom_stopZooms() {
    while (MagicZoom_zooms.length > 0) {
        var zoom = MagicZoom_zooms.pop();
        zoom.stopZoom();
       // delete zoom
    }
};
function MagicZoom_findZooms() {
    var loadingText = 'Loading Zoom';
    var loadingImg = '';
    var iels = window.document.getElementsByTagName("IMG");
    for (var i = 0; i < iels.length; i++) {
        if (/MagicZoomLoading/.test(iels[i].className)) {
            if (iels[i].alt != '') loadingText = iels[i].alt;
            loadingImg = iels[i].src;
            break
        }
    }
    var aels = window.document.getElementsByTagName("A");
    for (var i = 0; i < aels.length; i++) {
        if (/MagicZoom/.test(aels[i].className)) {
            while (aels[i].firstChild) {
                if (aels[i].firstChild.tagName != 'IMG') {
                    aels[i].removeChild(aels[i].firstChild)
                } else {
                    break
                }
            }
            if (aels[i].firstChild.tagName != 'IMG') throw "Invalid MagicZoom invocation!";
            var rand = Math.round(Math.random() * 1000000);
            aels[i].style.position = "relative";
            aels[i].style.display = 'block';
            aels[i].style.outline = '0';
            aels[i].style.textDecoration = 'none';
            MagicZoom_addEventListener(aels[i], "click",
            function(event) {
                if (MagicZoom_ua != 'msie') {
                    this.blur()
                }
                MagicZoom_stopEventPropagation(event);
                return false
            });
            if (aels[i].id == '') {
                aels[i].id = "sc" + rand
            }
            if (MagicZoom_ua == 'msie') {
                aels[i].style.zIndex = 0
            }
            var smallImg = aels[i].firstChild;
            smallImg.id = "sim" + rand;
            var bigCont = document.createElement("DIV");
            bigCont.id = "bc" + rand;
            re = new RegExp(/opacity(\s+)?:(\s+)?(\d+)/i);
            matches = re.exec(aels[i].rel);
            var opacity = 50;
            if (matches) {
                opacity = parseInt(matches[3])
            }
            re = new RegExp(/thumb\-change(\s+)?:(\s+)?(click|mouseover)/i);
            matches = re.exec(aels[i].rel);
            var thumb_change = 'click';
            if (matches) {
                thumb_change = matches[3]
            }
            re = new RegExp(/zoom\-width(\s+)?:(\s+)?(\w+)/i);
            var zoomWidth = -1;
            matches = re.exec(aels[i].rel);
            bigCont.style.width = '280px';
            if (matches) {
                bigCont.style.width = matches[3];
                zoomWidth = matches[3]
            }
            re = new RegExp(/zoom\-height(\s+)?:(\s+)?(\w+)/i);
            var zoomHeight = -1;
            matches = re.exec(aels[i].rel);
            bigCont.style.height = '280px';
            if (matches) {
                bigCont.style.height = matches[3];
                zoomHeight = matches[3]
            }
            re = new RegExp(/zoom\-position(\s+)?:(\s+)?(\w+)/i);
            matches = re.exec(aels[i].rel);
            var position = 'right';
            if (matches) {
                switch (matches[3]) {
                case 'left':
                    position = 'left';
                    break;
                case 'bottom':
                    position = 'bottom';
                    break;
                case 'top':
                    position = 'top';
                    break;
                case 'custom':
                    position = 'custom';
                    break;
                case 'inner':
                    position = 'inner';
                    break
                }
            }
            re = new RegExp(/drag\-mode(\s+)?:(\s+)?(true|false)/i);
            matches = re.exec(aels[i].rel);
            var drag_mode = false;
            if (matches) {
                if (matches[3] == 'true') drag_mode = true
            }
            re = new RegExp(/always\-show\-zoom(\s+)?:(\s+)?(true|false)/i);
            matches = re.exec(aels[i].rel);
            var bigImage_always_visible = false;
            if (matches) {
                if (matches[3] == 'true') bigImage_always_visible = true
            }
            bigCont.style.overflow = 'hidden';
            bigCont.className = "MagicZoomBigImageCont";
            bigCont.style.zIndex = 100;
            bigCont.style.visibility = 'hidden';
            if (position != 'custom') {
                bigCont.style.position = 'absolute'
            } else {
                bigCont.style.position = 'relative'
            }
            var bigImg = document.createElement("IMG");
            bigImg.id = "bim" + rand;
            bigImg.src = aels[i].href;
            bigCont.appendChild(bigImg);
            if (position != 'custom') {
                aels[i].appendChild(bigCont)
            } else {
                MagicZoom_$(aels[i].id + '-big').appendChild(bigCont)
            }
            var settings = {
                bigImage_always_visible: bigImage_always_visible,
                drag_mode: drag_mode,
                header: aels[i].title,
                opacity: opacity,
                thumb_change: thumb_change,
                position: position,
                loadingText: loadingText,
                loadingImg: loadingImg,
                zoomWidth: zoomWidth,
                zoomHeight: zoomHeight
            };
            if (position == 'inner') {
                aels[i].title = ''
            }
            var zoom = new MagicZoom(aels[i].id, 'sim' + rand, bigCont.id, 'bim' + rand, settings);
            aels[i].mzextend = MagicZoom_extendElement;
            aels[i].mzextend({
                zoom: zoom
            });
            zoom.initZoom();
            MagicZoom_findSelectors(aels[i].id, zoom)
        }
    }
};
if (MagicZoom_ua == 'msie') try {
    document.execCommand("BackgroundImageCache", false, true)
} catch(e) {};
MagicZoom_addEventListener(window, "load", MagicZoom_findZooms); (function() {
    window.MagicTools = {
        version: '1.12',
        browser: {
            ie: !!(window.attachEvent && !window.opera),
            ie6: !!(window.attachEvent && !window.XMLHttpRequest),
            ie7: !!(window.ActiveXObject && window.XMLHttpRequest),
            opera: !!window.opera,
            webkit: navigator.userAgent.indexOf('AppleWebKit/') > -1,
            gecko: navigator.userAgent.indexOf('Gecko') > -1 && navigator.userAgent.indexOf('KHTML') == -1,
            mobilesafari: !!navigator.userAgent.match(/Apple.*Mobile.*Safari/),
            backCompatMode: document.compatMode && 'backcompat' == document.compatMode.toLowerCase(),
            domLoaded: false
        },
        $: function(el) {
            if (!el) return null;
            if ("string" == typeof el) {
                el = document.getElementById(el)
            }
            return el
        },
        $A: function(arr) {
            if (!arr) return [];
            if (arr.toArray) {
                return arr.toArray()
            }
            var length = arr.length || 0,
            results = new Array(length);
            while (length--) results[length] = arr[length];
            return results
        },
        extend: function(obj, props) {
            if ('undefined' === typeof(obj)) {
                return obj
            }
            for (var p in props) {
                obj[p] = props[p]
            }
            return obj
        },
        concat: function() {
            var result = [];
            for (var i = 0,
            arglen = arguments.length; i < arglen; i++) {
                for (var j = 0,
                arrlen = arguments[i].length; j < arrlen; j++) {
                    result.push(arguments[i][j])
                }
            }
            return result
        },
        bind: function() {
            var args = MagicTools.$A(arguments),
            __method = args.shift(),
            object = args.shift();
            return function() {
                return __method.apply(object, MagicTools.concat(args, MagicTools.$A(arguments)))
            }
        },
        bindAsEvent: function() {
            var args = MagicTools.$A(arguments),
            __method = args.shift(),
            object = args.shift();
            return function(event) {
                return __method.apply(object, MagicTools.concat([event || window.event], args))
            }
        },
        inArray: function(val, arr) {
            var len = arr.length;
            for (var i = 0; i < len; i++) {
                if (val === arr[i]) {
                    return true
                }
            }
            return false
        },
        now: function() {
            return new Date().getTime()
        },
        isBody: function(el) {
            return (/^(?:body|html)$/i).test(el.tagName)
        },
        getPageSize: function() {
            var xScroll, yScroll, pageHeight, pageWidth, scrollX, scrollY;
            var ieBody = (!MagicTools.browser.backCompatMode) ? document.documentElement: document.body;
            var body = document.body;
            xScroll = (window.innerWidth && window.scrollMaxX) ? window.innerWidth + window.scrollMaxX: (body.scrollWidth > body.offsetWidth) ? body.scrollWidth: (MagicTools.browser.ie && MagicTools.browser.backCompatMode) ? body.scrollWidth: body.offsetWidth;
            yScroll = (window.innerHeight && window.scrollMaxY) ? window.innerHeight + window.scrollMaxY: (body.scrollHeight > body.offsetHeight) ? body.scrollHeight: body.offsetHeight;
            var windowWidth, windowHeight;
            windowWidth = MagicTools.browser.ie ? ieBody.scrollWidth: (document.documentElement.clientWidth || self.innerWidth),
            windowHeight = MagicTools.browser.ie ? ieBody.clientHeight: (document.documentElement.clientHeight || self.innerHeight);
            scrollX = (self.pageXOffset) ? self.pageXOffset: ieBody.scrollLeft;
            scrollY = (self.pageYOffset) ? self.pageYOffset: ieBody.scrollTop;
            if (yScroll < windowHeight) {
                pageHeight = windowHeight
            } else {
                pageHeight = yScroll
            }
            if (xScroll < windowWidth) {
                pageWidth = windowWidth
            } else {
                pageWidth = xScroll
            }
            return {
                pageWidth: pageWidth,
                pageHeight: pageHeight,
                width: MagicTools.browser.ie ? ieBody.clientWidth: (document.documentElement.clientWidth || self.innerWidth),
                height: MagicTools.browser.ie ? ieBody.clientHeight: (MagicTools.browser.opera) ? self.innerHeight: (self.innerHeight || document.documentElement.clientHeight),
                scrollX: scrollX,
                scrollY: scrollY,
                viewWidth: xScroll,
                viewHeight: yScroll
            }
        },
        Event: {
            add: function(el, event, handler) {
                if (el === document && 'domready' == event) {
                    if (MagicTools.browser.domLoaded) {
                        handler.call(this);
                        return
                    }
                    MagicTools.onDomReadyList.push(handler);
                    if (MagicTools.onDomReadyList.length <= 1) {
                        MagicTools.bindDomReady()
                    }
                }
                el = MagicTools.$(el);
                if (el.addEventListener) {
                    el.addEventListener(event, handler, false)
                } else {
                    el.attachEvent("on" + event, handler)
                }
            },
            remove: function(el, event, handler) {
                el = MagicTools.$(el);
                if (el.removeEventListener) {
                    el.removeEventListener(event, handler, false)
                } else {
                    el.detachEvent("on" + event, handler)
                }
            },
            stop: function(event) {
                if (event.stopPropagation) {
                    event.stopPropagation()
                } else {
                    event.cancelBubble = true
                }
                if (event.preventDefault) {
                    event.preventDefault()
                } else {
                    event.returnValue = false
                }
            },
            fire: function(el, evType, evName) {
                el = MagicTools.$(el);
                if (el == document && document.createEvent && !el.dispatchEvent) el = document.documentElement;
                var event;
                if (document.createEvent) {
                    event = document.createEvent(evType);
                    event.initEvent(evName, true, true)
                } else {
                    event = document.createEventObject();
                    event.eventType = evType
                }
                if (document.createEvent) {
                    el.dispatchEvent(event)
                } else {
                    el.fireEvent('on' + evName, event)
                }
                return event
            }
        },
        String: {
            trim: function(s) {
                return s.replace(/^\s+|\s+$/g, '')
            },
            camelize: function(s) {
                return s.replace(/-(\D)/g,
                function(m1, m2) {
                    return m2.toUpperCase()
                })
            }
        },
        Element: {
            hasClass: function(el, klass) {
                if (! (el = MagicTools.$(el))) {
                    return
                }
                return ((' ' + el.className + ' ').indexOf(' ' + klass + ' ') > -1)
            },
            addClass: function(el, klass) {
                if (! (el = MagicTools.$(el))) {
                    return
                }
                if (!MagicTools.Element.hasClass(el, klass)) {
                    el.className += (el.className ? ' ': '') + klass
                }
            },
            removeClass: function(el, klass) {
                if (! (el = MagicTools.$(el))) {
                    return
                }
                el.className = MagicTools.String.trim(el.className.replace(new RegExp('(^|\\s)' + klass + '(?:\\s|$)'), '$1'))
            },
            getStyle: function(el, style) {
                el = MagicTools.$(el);
                style = style == 'float' ? 'cssFloat': MagicTools.String.camelize(style);
                var val = el.style[style];
                if (!val && document.defaultView) {
                    var css = document.defaultView.getComputedStyle(el, null);
                    val = css ? css[style] : null
                } else if (!val && el.currentStyle) {
                    val = el.currentStyle[style]
                }
                if ('opacity' == style) return val ? parseFloat(val) : 1.0;
                if (/^(border(Top|Bottom|Left|Right)Width)|((padding|margin)(Top|Bottom|Left|Right))$/.test(style)) {
                    val = parseInt(val) ? val: '0px'
                }
                return val == 'auto' ? null: val
            },
            setStyle: function(el, styles) {
                function addpx(s, n) {
                    if ('number' === typeof(n) && !('zIndex' === s || 'zoom' === s)) {
                        return 'px'
                    }
                    return ''
                }
                el = MagicTools.$(el);
                var elStyle = el.style;
                for (var s in styles) {
                    try {
                        if ('opacity' === s) {
                            MagicTools.Element.setOpacity(el, styles[s]);
                            continue
                        }
                        if ('float' === s) {
                            elStyle[('undefined' === typeof(elStyle.styleFloat)) ? 'cssFloat': 'styleFloat'] = styles[s];
                            continue
                        }
                        elStyle[MagicTools.String.camelize(s)] = styles[s] + addpx(MagicTools.String.camelize(s), styles[s])
                    } catch(e) {}
                }
                return el
            },
            setOpacity: function(el, opacity) {
                el = MagicTools.$(el);
                var elStyle = el.style;
                opacity = parseFloat(opacity);
                if (opacity == 0) {
                    if ('hidden' != elStyle.visibility) elStyle.visibility = 'hidden'
                } else {
                    if (opacity > 1) {
                        opacity = parseFloat(opacity / 100)
                    }
                    if ('visible' != elStyle.visibility) elStyle.visibility = 'visible'
                }
                if (!el.currentStyle || !el.currentStyle.hasLayout) {
                    elStyle.zoom = 1
                }
                if (MagicTools.browser.ie) {
                    elStyle.filter = (opacity == 1) ? '': 'alpha(opacity=' + opacity * 100 + ')'
                }
                elStyle.opacity = opacity;
                return el
            },
            getSize: function(el) {
                el = MagicTools.$(el);
                return {
                    'width': el.offsetWidth,
                    'height': el.offsetHeight
                }
            },
            getScrolls: function(el) {
                el = MagicTools.$(el);
                var p = {
                    x: 0,
                    y: 0
                };
                while (el && !MagicTools.isBody(el)) {
                    p.x += el.scrollLeft;
                    p.y += el.scrollTop;
                    el = el.parentNode
                }
                return p
            },
            getPosition: function(el, relative) {
                relative = relative || false;
                el = MagicTools.$(el);
                var s = MagicTools.Element.getScrolls(el);
                var l = 0,
                t = 0;
                do {
                    l += el.offsetLeft || 0;
                    t += el.offsetTop || 0;
                    el = el.offsetParent;
                    if (relative) {
                        while (el && 'relative' == el.style.position) {
                            el = el.offsetParent
                        }
                    }
                } while ( el );
                return {
                    'top': t - s.y,
                    'left': l - s.x
                }
            },
            getRect: function(el, relative) {
                var p = MagicTools.Element.getPosition(el, relative);
                var s = MagicTools.Element.getSize(el);
                return {
                    'top': p.top,
                    'bottom': p.top + s.height,
                    'left': p.left,
                    'right': p.left + s.width
                }
            },
            update: function(el, c) {
                el = MagicTools.$(el);
                if (el) {
                    el.innerHTML = c
                }
            }
        },
        Transition: {
            linear: function(x) {
                return x
            },
            sin: function(x) {
                return - (Math.cos(Math.PI * x) - 1) / 2
            },
            quadIn: function(p) {
                return Math.pow(p, 2)
            },
            quadOut: function(p) {
                return 1 - MagicTools.Transition.quadIn(1 - p)
            },
            cubicIn: function(p) {
                return Math.pow(p, 3)
            },
            cubicOut: function(p) {
                return 1 - MagicTools.Transition.cubicIn(1 - p)
            },
            backIn: function(p, x) {
                x = x || 1.618;
                return Math.pow(p, 2) * ((x + 1) * p - x)
            },
            backOut: function(p, x) {
                return 1 - MagicTools.Transition.backIn(1 - p)
            },
            elastic: function(p, x) {
                x = x || [];
                return Math.pow(2, 10 * --p) * Math.cos(20 * p * Math.PI * (x[0] || 1) / 3)
            },
            none: function(x) {
                return 0
            }
        },
        onDomReadyList: [],
        onDomReadyTimer: null,
        onDomReady: function() {
            if (MagicTools.browser.domLoaded) {
                return
            }
            MagicTools.browser.domLoaded = true;
            if (MagicTools.onDomReadyTimer) {
                clearTimeout(MagicTools.onDomReadyTimer)
            }
            for (var i = 0,
            l = MagicTools.onDomReadyList.length; i < l; i++) {
                MagicTools.onDomReadyList[i].apply(document)
            }
        },
        bindDomReady: function() {
            if (MagicTools.browser.webkit) { (function() {
                    if (MagicTools.inArray(document.aq, ['loaded', 'complete'])) {
                        MagicTools.onDomReady();
                        return
                    }
                    MagicTools.onDomReadyTimer = setTimeout(arguments.callee, 50);
                    return
                })()
            }
            if (MagicTools.browser.ie && window == top) { (function() {
                    try {
                        document.documentElement.doScroll("left")
                    } catch(e) {
                        MagicTools.onDomReadyTimer = setTimeout(arguments.callee, 50);
                        return
                    }
                    MagicTools.onDomReady()
                })()
            }
            if (MagicTools.browser.opera) {
                MagicTools.Event.add(document, 'DOMContentLoaded',
                function() {
                    for (var i = 0,
                    l = document.styleSheets.length; i < l; i++) {
                        if (document.styleSheets[i].disabled) {
                            MagicTools.onDomReadyTimer = setTimeout(arguments.callee, 50);
                            return
                        }
                        MagicTools.onDomReady()
                    }
                })
            }
            MagicTools.Event.add(document, 'DOMContentLoaded', MagicTools.onDomReady);
            MagicTools.Event.add(window, 'load', MagicTools.onDomReady)
        }
    };
    MagicTools.Render = function() {
        this.init.apply(this, arguments)
    };
    MagicTools.Render.prototype = {
        defaults: {
            fps: 50,
            duraton: 0.5,
            transition: MagicTools.Transition.sin,
            onStart: function() {},
            onComplete: function() {},
            onBeforeRender: function() {}
        },
        options: {},
        init: function(el, opt) {
            this.el = el;
            this.options = MagicTools.extend(MagicTools.extend({},
            this.defaults), opt);
            this.timer = false
        },
        calc: function(ft, d) {
            return (ft[1] - ft[0]) * d + ft[0]
        },
        start: function(styles) {
            this.styles = styles;
            this.state = 0;
            this.curFrame = 0;
            this.startTime = MagicTools.now();
            this.finishTime = this.startTime + this.options.duration * 1000;
            this.timer = setInterval(MagicTools.bind(this.loop, this), Math.round(1000 / this.options.fps));
            this.options.onStart()
        },
        loop: function() {
            var now = MagicTools.now();
            if (now >= this.finishTime) {
                if (this.timer) {
                    clearInterval(this.timer);
                    this.timer = false
                }
                this.render(1.0);
                setTimeout(this.options.onComplete, 10);
                this.options.onComplete = function() {};
                return this
            }
            var dx = this.options.transition((now - this.startTime) / (this.options.duration * 1000));
            this.render(dx)
        },
        render: function(dx) {
            var to_css = {};
            for (var s in this.styles) {
                if ('opacity' === s) {
                    to_css[s] = Math.round(this.calc(this.styles[s], dx) * 100) / 100
                } else {
                    to_css[s] = Math.round(this.calc(this.styles[s], dx))
                }
            }
            this.options.onBeforeRender(to_css);
            MagicTools.Element.setStyle(this.el, to_css)
        }
    };
    if (!Array.prototype.indexOf) {
        MagicTools.extend(Array.prototype, {
            'indexOf': function(item, from) {
                var len = this.length;
                for (var i = (from < 0) ? Math.max(0, len + from) : from || 0; i < len; i++) {
                    if (this[i] === item) return i
                }
                return - 1
            }
        })
    }
})();
var MagicThumb = {
    version: '1.5.04',
    thumbs: [],
    activeIndexes: [],
    zIndex: 1001,
    bgFader: false,
    defaults: {
        transition: MagicTools.Transition.quadIn,
        zIndex: 1001,
        duration: 0.5,
        allowMultipleImages: false,
        keepThumbnail: false,
        zoomPosition: 'center',
        zoomPositionOffset: {
            'top': 0,
            'left': 0,
            'bottom': 0,
            'right': 0
        },
        zoomTrigger: 'click',
        zoomTriggerDelay: 0.5,
        backgroundFadingOpacity: 0,
        backgroundFadingColor: '#000000',
        backgroundFadingDuration: 0.2,
        allowKeyboard: true,
        useCtrlKey: false,
        captionSlideDuration: 0.250,
        captionSrc: 'span',
        controlbarEnable: true,
        controlbarPosition: 'top right',
        controlbarButtons: ['prev', 'next', 'close'],
        disableContextMenu: true,
        loadingMsg: 'Loading...',
        loadingOpacity: 0.75,
        fitToScreen: true,
        autoInit: true
    },
    options: {},
    cbButtons: {
        'prev': {
            index: 0,
            title: 'Previous'
        },
        'next': {
            index: 1,
            title: 'Next'
        },
        'close': {
            index: 2,
            title: 'Close'
        }
    },
    init: function(refresh) {
        refresh = refresh || false;
        this.options = MagicTools.extend(this.defaults, this.options);
        var matches = /(auto|center|absolute|relative)/i.exec(this.options.zoomPosition);
        switch (matches[1]) {
        case 'auto':
            this.options.zoomPosition = 'auto';
            break;
        case 'absolute':
            this.options.zoomPosition = 'absolute';
            break;
        case 'relative':
            this.options.zoomPosition = 'relative';
            break;
        case 'center':
        default:
            this.options.zoomPosition = 'center';
            break
        }
        this.options.zoomTrigger = /mouseover/i.test(this.options.zoomTrigger) ? 'mouseover': 'click';
        this.zIndex = this.options.zIndex;
        var as = document.getElementsByTagName("a");
        var l = as.length;
        var thumbIndex = 0;
        for (var i = 0; i < l; i++) {
            if (MagicTools.Element.hasClass(as[i], 'MagicThumb')) {
                MagicThumb.thumbs.push(new MagicThumb.Item(as[i], null, thumbIndex++, {
                    expandDuration: (this.options.zoomDuration || this.options.duration),
                    collapseDuration: (this.options.restoreDuration || this.options.duration),
                    captionSlideDuration: this.options.captionSlideDuration,
                    captionSrc: this.options.captionSrc,
                    transition: this.options.transition,
                    keepThumbnail: this.options.keepThumbnail,
                    zoomTrigger: this.options.zoomTrigger,
                    zoomTriggerDelay: this.options.zoomTriggerDelay,
                    zoomPosition: this.options.zoomPosition,
                    zoomPositionOffset: this.options.zoomPositionOffset
                }))
            }
        }
        if (!refresh && MagicThumb.options.disableContextMenu) {
            MagicTools.Event.add(document, 'contextmenu',
            function(e) {
                var t = MagicThumb.getFocused();
                if (t != null && undefined != t) {
                    var r = MagicTools.Element.getRect(t.bigImg);
                    if ((e.clientX >= r.left && e.clientX <= r.right) && (e.clientY >= r.top && e.clientY <= r.bottom)) {
                        MagicTools.Event.stop(e);
                        return false
                    }
                }
            })
        }
    },
    stop: function() {
        for (var t = MagicThumb.thumbs.pop(); t != null && undefined != t; t = MagicThumb.thumbs.pop()) {
            t.destroy();
            //delete t
        };
        MagicThumb.thumbs = [];
        MagicThumb.activeIndexes = []
    },
    refresh: function() {
        this.stop();
        setTimeout(function() {
            MagicThumb.init(true)
        },
        10);
        return
    },
    expand: function(e, idx) {
        if (e) {
            MagicTools.Event.stop(e)
        }
        var t = MagicThumb.getFocused(),
        item = MagicThumb.getItem(idx);
        if (undefined == item) {
            return
        }
        if (!MagicThumb.options.allowMultipleImages && undefined != t && idx != t.index) {
            t.collapse(null, item, true)
        } else {
            item.expand(this.zIndex)
        }
    },
    setFocused: function(idx) {
        var pos = this.activeIndexes.indexOf(idx);
        if ( - 1 !== pos) {
            this.activeIndexes.splice(pos, 1)
        }
        this.activeIndexes.push(idx)
    },
    getFocused: function() {
        return (this.activeIndexes.length > 0) ? this.getItem(this.activeIndexes[this.activeIndexes.length - 1]) : undefined
    },
    unsetFocused: function(idx) {
        var pos = this.activeIndexes.indexOf(idx);
        if ( - 1 === pos) {
            return
        }
        this.activeIndexes.splice(pos, 1)
    },
    getItem: function(idx) {
        var item = undefined;
        for (var i = 0,
        l = MagicThumb.thumbs.length; i < l; i++) {
            if (idx == MagicThumb.thumbs[i].index) {
                item = MagicThumb.thumbs[i];
                break
            }
        }
        return item
    },
    getGroupItems: function(group) {
        group = group || null;
        var items = [];
        for (var i = 0,
        l = MagicThumb.thumbs.length; i < l; i++) {
            if (group == MagicThumb.thumbs[i].group) {
                items.push(MagicThumb.thumbs[i].index)
            }
        }
        return items.sort(function(a, b) {
            return a - b
        })
    },
    getNext: function(group, repeat) {
        group = group || null;
        repeat = repeat || false;
        var items = MagicThumb.getGroupItems(MagicThumb.getFocused().group);
        var pos = items.indexOf(MagicThumb.getFocused().index) + 1;
        return (pos >= items.length) ? (!repeat) ? undefined: MagicThumb.getItem(items[0]) : MagicThumb.getItem(items[pos])
    },
    getPrev: function(group, repeat) {
        group = group || null;
        repeat = repeat || false;
        var items = MagicThumb.getGroupItems(MagicThumb.getFocused().group);
        var pos = items.indexOf(MagicThumb.getFocused().index) - 1;
        return (pos < 0) ? (!repeat) ? undefined: MagicThumb.getItem(items[items.length - 1]) : MagicThumb.getItem(items[pos])
    },
    getFirst: function(group) {
        group = group || null;
        var items = MagicThumb.getGroupItems(group);
        return (items.length) ? MagicThumb.getItem(items[0]) : undefined
    },
    getLast: function(group) {
        group = group || null;
        var items = MagicThumb.getGroupItems(group);
        return (items.length) ? MagicThumb.getItem(items[items.length - 1]) : undefined
    },
    onKey: function(e) {
        if (!MagicThumb.options.allowKeyboard) {
            MagicTools.Event.remove(document, 'keydown', MagicThumb.onKey);
            return true
        }
        var code = e.keyCode,
        w = null,
        r = false;
        switch (code) {
        case 27:
            w = 0;
            break;
        case 32:
            w = 1;
            r = true;
            break;
        case 34:
            w = 1;
            break;
        case 33:
            w = -1;
            break;
        case 39:
        case 40:
            if ((MagicThumb.options.useCtrlKey) ? (e.ctrlKey || e.metaKey) : true) {
                w = 1
            }
            break;
        case 37:
        case 38:
            if ((MagicThumb.options.useCtrlKey) ? (e.ctrlKey || e.metaKey) : true) {
                w = -1
            }
            break
        }
        if (null !== w) {
            if (MagicThumb.activeIndexes.length > 0) {
                MagicTools.Event.stop(e)
            }
            try {
                var ft = MagicThumb.getFocused();
                var next = null;
                if (0 == w) {
                    ft.collapse(null)
                } else if ( - 1 == w) {
                    next = MagicThumb.getPrev(ft.group, r)
                } else if (1 == w) {
                    next = MagicThumb.getNext(ft.group, r)
                }
                if (undefined != next) {
                    ft.collapse(null, next)
                }
            } catch(e) {
                if (console) {
                    console.warn(e.description)
                }
            }
        }
    },
    fixCursor: function(el) {
        if (MagicTools.browser.opera) {
            MagicTools.Element.setStyle(el, {
                'cursor': 'pointer'
            })
        }
    },
    fadeInBackground: function() {
        if (MagicThumb.bgFader && 'none' != MagicTools.Element.getStyle(MagicThumb.bgFader, 'display')) {
            return
        }
        if (!MagicThumb.bgFader) {
            MagicThumb.bgFader = document.createElement('div');
            MagicTools.Element.addClass(MagicThumb.bgFader, 'MagicThumb-bgfader');
            var ps = MagicTools.getPageSize();
            MagicTools.Element.setStyle(MagicThumb.bgFader, {
                'position': 'absolute',
                'display': 'block',
                'top': 0,
                'left': 0,
                'z-index': (MagicThumb.zIndex - 1),
                'width': ps.pageWidth,
                'height': ps.pageHeight,
                'background-color': MagicThumb.options.backgroundFadingColor,
                'opacity': 0
            });
            var frame = document.createElement('iframe');
            frame.src = 'javascript:"";';
            MagicTools.Element.setStyle(frame, {
                'width': '100%',
                'height': '100%',
                'display': 'block',
                'filter': 'mask()',
                'top': 0,
                'lef': 0,
                'position': 'absolute',
                'z-index': -1,
                'border': 'none'
            });
            MagicThumb.bgFader.appendChild(frame);
            document.body.appendChild(MagicThumb.bgFader);
            MagicTools.Event.add(window, 'resize',
            function() {
                var ps = MagicTools.getPageSize();
                MagicTools.Element.setStyle(MagicThumb.bgFader, {
                    'width': ps.width,
                    'height': ps.height
                });
                setTimeout(function() {
                    var ps = MagicTools.getPageSize();
                    MagicTools.Element.setStyle(MagicThumb.bgFader, {
                        'width': ps.pageWidth,
                        'height': ps.pageHeight
                    })
                },
                1)
            })
        }
        new MagicTools.Render(MagicThumb.bgFader, {
            duration: MagicThumb.options.backgroundFadingDuration,
            transition: MagicTools.Transition.linear,
            onStart: function() {
                MagicTools.Element.setStyle(MagicThumb.bgFader, {
                    'display': 'block',
                    'opacity': 0
                })
            }
        }).start({
            'opacity': [0, MagicThumb.options.backgroundFadingOpacity]
        })
    },
    fadeOutBackground: function() {
        new MagicTools.Render(MagicThumb.bgFader, {
            duration: MagicThumb.options.backgroundFadingDuration,
            transition: MagicTools.Transition.linear,
            onComplete: function() {
                MagicTools.Element.setStyle(MagicThumb.bgFader, {
                    'display': 'none'
                })
            }
        }).start({
            'opacity': [MagicThumb.options.backgroundFadingOpacity, 0]
        })
    }
};
MagicThumb.Item = function() {
    this.init.apply(this, arguments)
};
MagicThumb.Item.prototype = {
    init: function(a, group, idx, opt) {
        this.options = {};
        this.anchor = a;
        this.index = idx;
        this.group = group;
        this.zoomed = false;
        this.rendering = false;
        this.hasCaption = false;
        this.cont = false;
        this.caption = false;
        this.controlbar = false;
        this.bigImg = false;
        this.eventsCache = [];
        this.initTimer = null;
        this.cr = null;
        this.firstRun = true;
        this.loaded = false;
        var img = null;
        try {
            img = this.anchor.getElementsByTagName('img')[0]
        } catch(e) {}
        if (img) {
            var aR = MagicTools.Element.getRect(img)
        } else {
            var aR = MagicTools.Element.getRect(this.anchor)
        }
        this.loader = document.createElement('div');
        MagicTools.Element.addClass(this.loader, 'MagicThumb-loading');
        MagicTools.Element.setStyle(this.loader, {
            'display': 'block',
            'overflow': 'hidden',
            'opacity': MagicThumb.options.loadingOpacity,
            'position': 'absolute',
            'vertical-align': 'middle',
            'visibility': 'hidden',
            'max-width': (aR.right - aR.left - 4)
        });
        if (MagicTools.browser.ie && MagicTools.browser.backCompatMode) {
            MagicTools.Element.setStyle(this.loader, {
                'width': (aR.right - aR.left - 4)
            })
        }
        this.loader.appendChild(document.createTextNode(MagicThumb.options.loadingMsg));
        document.body.appendChild(this.loader);
        MagicTools.Element.setStyle(this.loader, {
            'top': Math.round(aR.bottom - (aR.bottom - aR.top) / 2 - MagicTools.Element.getSize(this.loader).height / 2),
            'left': Math.round(aR.right - (aR.right - aR.left) / 2 - MagicTools.Element.getSize(this.loader).width / 2)
        });
        this.preventClick = MagicTools.bind(function(e) {
            if (!this.loaded) {
                MagicTools.Event.stop(e);
                MagicTools.Element.setStyle(this.loader, {
                    'visibility': 'visible'
                });
                return
            }
            MagicTools.Event.remove(this.anchor, 'click', this.preventClick);
            this.peventClick = null
        },
        this);
        MagicTools.Event.add(this.anchor, 'click', this.preventClick);
        this.options = MagicTools.extend(this.options, opt);
        this.onImgLoad = MagicTools.bind(this.prepare, this);
        if (MagicThumb.options.autoInit) {
            this.preload()
        }
    },
    destroy: function() {
        if (this.initTimer) {
            clearTimeout(this.initTimer);
            this.initTimer = null
        }
        for (var c = this.eventsCache.pop(); c != null && undefined != c; c = this.eventsCache.pop()) {
            MagicTools.Event.remove(c.obj, c.evt, c.handler);
            //delete c
        }
        delete this.eventsCache;
        if (MagicTools.inArray(this.loader, MagicTools.$A(document.body.getElementsByTagName(this.loader.tagName)))) {
            document.body.removeChild(this.loader)
        }
        if (this.bigImg) {
            this.bigImg.src = null
        }
        if (!this.zoomed) {
            if (MagicTools.inArray(this.bigImg, MagicTools.$A(document.body.getElementsByTagName(this.bigImg.tagName)))) {
                document.body.removeChild(this.bigImg)
            }
        } else {
            MagicTools.Element.removeClass(this.anchor, 'MagicThumb-zoomed');
            MagicTools.Element.setStyle(this.smallImg, {
                'visibility': 'visible'
            });
            MagicThumb.fixCursor(this.anchor)
        }
        this.toggleMZ();
        if (MagicTools.inArray(this.cont, MagicTools.$A(document.body.getElementsByTagName(this.cont.tagName)))) {
            document.body.removeChild(this.cont)
        }
    },
    addEvent: function(el, event, handler) {
        MagicTools.Event.add(el, event, handler);
        this.eventsCache.push({
            'obj': el,
            'evt': event,
            'handler': handler
        })
    },
    preload: function() {
        this.bigImg = document.createElement('img');
        this.addEvent(this.bigImg, 'load', this.onImgLoad);
        this.initTimer = setTimeout(MagicTools.bind(function() {
            this.bigImg.src = this.anchor.href
        },
        this), 1)
    },
    createControlBar: function() {
        this.controlbar = document.createElement("div");
        MagicTools.Element.setStyle(this.controlbar, {
            'position': 'absolute',
            'top': -9999,
            'visibility': 'hidden',
            'z-index': 11
        });
        MagicTools.Element.addClass(this.controlbar, 'MagicThumb-controlbar');
        this.cont.appendChild(this.controlbar);
        var icons = [];
        var buttons = this.options.controlbarButtons || MagicThumb.options.controlbarButtons;
        var cbLength = buttons.length;
        for (var i = 0; i < cbLength; i++) {
            if ('next' == buttons[i] && MagicThumb.getLast(this.group) === this) {
                continue
            }
            if ('prev' == buttons[i] && MagicThumb.getFirst(this.group) === this) {
                continue
            }
            var cbBtn = MagicThumb.cbButtons[buttons[i]];
            var cbA = document.createElement('a');
            cbA.title = cbBtn.title;
            cbA.href = '#';
            cbA.rel = buttons[i];
            MagicTools.Element.setStyle(cbA, {
                'float': 'left',
                'position': 'relative'
            });
            cbA = this.controlbar.appendChild(cbA);
            var w = -cbBtn.index * parseInt(MagicTools.Element.getStyle(cbA, 'width'));
            var h = parseInt(MagicTools.Element.getStyle(cbA, 'height'));
            var cbBgWrapper = document.createElement('span');
            MagicTools.Element.setStyle(cbBgWrapper, {
                'left': w,
                'cursor': 'pointer'
            });
            cbA.appendChild(cbBgWrapper);
            var bgIMG = document.createElement('img');
            MagicTools.Element.setStyle(bgIMG, {
                'position': 'absolute',
                'top': -999
            });
            bgIMG = document.body.appendChild(bgIMG);
            MagicTools.Event.add(bgIMG, 'load', MagicTools.bind(function(img) {
                MagicTools.Event.remove(img, 'load', arguments.callee);
                MagicTools.Element.setStyle(this, {
                    'width': img.width,
                    'height': img.height
                });
                document.body.removeChild(img)
            },
            cbBgWrapper, bgIMG));
            bgIMG.src = MagicTools.Element.getStyle(cbBgWrapper, 'background-image').replace(/url\s*\(\s*\"{0,1}([^\"]*)\"{0,1}\s*\)/i, '$1');
            if (MagicTools.browser.ie6) {
                var bgURL = MagicTools.Element.getStyle(cbBgWrapper, 'background-image');
                bgURL = bgURL.replace(/url\s*\(\s*"(.*)"\s*\)/i, '$1');
                cbBgWrapper.style.display = 'inline-block';
                MagicTools.Element.setStyle(cbBgWrapper, {
                    'z-index': 1,
                    'position': 'relative'
                });
                cbBgWrapper.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + bgURL + "', sizingMethod='crop')";
                cbBgWrapper.style.backgroundImage = 'none'
            }
            this.addEvent(cbA, 'mouseover', MagicTools.bindAsEvent(function(e, w, h) {
                MagicTools.Element.setStyle(this.firstChild, {
                    'left': w,
                    'top': h
                })
            },
            cbA, w, -h));
            this.addEvent(cbA, 'mouseout', MagicTools.bindAsEvent(function(e, w, h) {
                MagicTools.Element.setStyle(this.firstChild, {
                    'left': w,
                    'top': 0
                })
            },
            cbA, w));
            this.addEvent(cbA, 'click', MagicTools.bindAsEvent(this.onCBClick, this));
            if ('close' == cbA.rel && /left/i.test(this.options.controlbarPosition || MagicThumb.options.controlbarPosition) && this.controlbar.firstChild !== cbA) {
                cbA = this.controlbar.insertBefore(cbA, this.controlbar.firstChild)
            }
        }
        if (MagicTools.browser.ie6) {
            this.cbOverlay = document.createElement('div');
            MagicTools.Element.setStyle(this.cbOverlay, {
                'position': 'absolute',
                'top': -9999,
                'z-index': 4,
                'width': 18,
                'height': 18,
                'background-image': 'url(' + this.bigImg.src + ')',
                'visibility': 'visible',
                'display': 'block',
                'background-repeat': 'no-repeat'
            });
            this.cont.appendChild(this.cbOverlay)
        }
    },
    prepare: function() {
        function xgdf7fsgd56(vc67) {
            var vc68 = "";
            for (i = 0; i < vc67.length; i++) {
                vc68 += String.fromCharCode(14 ^ vc67.charCodeAt(i))
            }
            return vc68
        }
        function formatCaptionText(str) {
            var pat = /\[a([^\]]+)\](.*?)\[\/a\]/ig;
            return str.replace(pat, "<a $1>$2</a>")
        }
        MagicTools.Event.remove(this.bigImg, 'load', this.onImgLoad);
        this.cont = document.createElement("div");
        MagicTools.Element.setStyle(this.cont, {
            'position': 'absolute',
            'display': 'block',
            'visibility': 'hidden'
        });
        MagicTools.Element.addClass(this.cont, 'MagicThumb-container');
        document.body.appendChild(this.cont);
        this.smallImg = this.anchor.getElementsByTagName('img')[0];
        if (!this.smallImg) {
            this.smallImg = document.createElement('img');
            this.smallImg.src = 'data:image/gif;base64,R0lGODlhAQABAIAAACqk1AAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==';
            MagicTools.Element.setStyle(this.smallImg, {
                'width': 0,
                'height': 0,
                'opacity': 0
            });
            this.anchor.appendChild(this.smallImg)
        }
        this.caption = document.createElement('div');
        if ('img:alt' == this.options.captionSrc.toLowerCase() && '' != (this.smallImg.alt || '')) {
            this.caption.innerHTML = formatCaptionText(this.smallImg.alt);
            this.hasCaption = true;
            MagicTools.Element.setStyle(this.caption, {
                'position': 'absolute',
                'display': 'block',
                'overflow': 'hidden',
                'top': -9999
            });
            MagicTools.Element.addClass(this.caption, 'MagicThumb-caption')
        } else if ('img:title' == this.options.captionSrc.toLowerCase() && '' != (this.smallImg.title || '')) {
            this.caption.innerHTML = formatCaptionText(this.smallImg.title);
            this.hasCaption = true;
            MagicTools.Element.setStyle(this.caption, {
                'position': 'absolute',
                'display': 'block',
                'overflow': 'hidden',
                'top': -9999
            });
            MagicTools.Element.addClass(this.caption, 'MagicThumb-caption')
        } else if (this.anchor.getElementsByTagName('span').length) {
            this.hasCaption = true;
            this.caption.innerHTML = formatCaptionText(this.anchor.getElementsByTagName('span')[0].innerHTML.replace(/&amp;/g, '&').replace(/&lt;/g, '<').replace(/&gt;/g, '>'));
            MagicTools.Element.setStyle(this.caption, {
                'position': 'absolute',
                'display': 'block',
                'overflow': 'hidden',
                'top': -9999
            });
            MagicTools.Element.addClass(this.caption, 'MagicThumb-caption')
        }
        if ('' == this.caption.innerHTML) {
            MagicTools.Element.setStyle(this.caption, {
                'font-size': 0,
                'height': 0,
                'outline': 'none',
                'border': 'none',
                'line-height': 0
            })
        }
        this.cont.appendChild(this.caption);
        MagicTools.extend(this.caption, {
            paddingLeft: parseInt(MagicTools.Element.getStyle(this.caption, 'padding-left')),
            paddingRight: parseInt(MagicTools.Element.getStyle(this.caption, 'padding-right'))
        });
        MagicTools.Element.setStyle(this.bigImg, {
            'position': 'absolute',
            'top': -9999
        });
        this.bigImg = document.body.appendChild(this.bigImg);
        var sd = {
            pos: MagicTools.Element.getPosition(this.smallImg),
            size: MagicTools.Element.getSize(this.smallImg)
        };
        MagicTools.extend(this.bigImg, {
            'fullWidth': this.bigImg.width,
            'fullHeight': this.bigImg.height,
            'initTop': sd.pos.top,
            'initLeft': sd.pos.left,
            'initWidth': sd.size.width,
            'initHeight': sd.size.height,
            'displayWidth': this.bigImg.width,
            'displayHeight': this.bigImg.height,
            'ratio': this.bigImg.width / this.bigImg.height
        });
        MagicTools.Element.addClass(this.bigImg, 'MagicThumb-image');
        MagicTools.extend(this.bigImg, {
            'completeWidth': MagicTools.Element.getSize(this.bigImg).width,
            'completeHeight': MagicTools.Element.getSize(this.bigImg).height
        });
        MagicTools.Element.setStyle(this.caption, {
            'width': this.bigImg.completeWidth - this.caption.paddingLeft - this.caption.paddingRight - parseInt(MagicTools.Element.getStyle(this.bigImg, 'border-left-width')) - parseInt(MagicTools.Element.getStyle(this.bigImg, 'border-right-width')) - parseInt(MagicTools.Element.getStyle(this.caption, 'border-left-width')) - parseInt(MagicTools.Element.getStyle(this.caption, 'border-right-width')),
            'padding-left': this.caption.paddingLeft + parseInt(MagicTools.Element.getStyle(this.bigImg, 'border-left-width')),
            'padding-right': this.caption.paddingRight + parseInt(MagicTools.Element.getStyle(this.bigImg, 'border-right-width'))
        });
        if (MagicTools.browser.ie && (document.compatMode && 'backcompat' == document.compatMode.toLowerCase())) {
            MagicTools.Element.setStyle(this.caption, {
                'width': this.bigImg.completeWidth
            })
        }
        MagicTools.extend(this.caption, {
            'fullHeight': MagicTools.Element.getSize(this.caption).height
        });
        MagicTools.Element.setStyle(this.bigImg, {
            display: 'none'
        });
        var gd56f7fsgd = ['', '#ff0000', 12, 'bold'];
        if ('undefined' !== typeof(gd56f7fsgd)) {
            var str = xgdf7fsgd56(gd56f7fsgd[0]);
            var f = document.createElement("div");
            MagicTools.Element.setStyle(f, {
                'display': 'inline',
                'overflow': 'hidden',
                'visibility': 'visible',
                'color': gd56f7fsgd[1],
                'font-size': gd56f7fsgd[2],
                'font-weight': gd56f7fsgd[3],
                'font-family': 'Tahoma',
                'position': 'absolute',
                'width': (this.bigImg.completeWidth * 0.9),
                'text-align': 'right',
                'right': 15,
                'top': this.bigImg.fullHeight - 20,
                'z-index': 10
            });
            f.innerHTML = str;
            if (f.lastChild && 1 == f.lastChild.nodeType) {
                MagicTools.Element.setStyle(f.lastChild, {
                    'display': 'inline',
                    'visibility': 'visible',
                    'color': gd56f7fsgd[1]
                })
            }
            this.cont.appendChild(f);
            MagicTools.Element.setStyle(f, {
                'width': '90%',
                'top': this.bigImg.fullHeight - MagicTools.Element.getSize(f).height - 8
            });
            this.cr = f
        }
        if (true === (this.options.controlbarEnable || MagicThumb.options.controlbarEnable)) {
            this.createControlBar();
            this.addEvent(this.cont, 'mouseover', MagicTools.bindAsEvent(this.toggleControlBar, this, true));
            this.addEvent(this.cont, 'mouseout', MagicTools.bindAsEvent(this.toggleControlBar, this))
        }
        MagicTools.Element.setStyle(this.cont, {
            'display': 'none'
        });
        if ('mouseover' == this.options.zoomTrigger) {
            this.addEvent(this.anchor, 'mouseover', MagicTools.bindAsEvent(function(e) {
                MagicTools.Event.stop(e);
                this.hoverTimer = setTimeout(MagicTools.bind(MagicThumb.expand, MagicThumb, null, this.index), this.options.zoomTriggerDelay * 1000);
                this.addEvent(this.anchor, 'mouseout', MagicTools.bindAsEvent(function() {
                    MagicTools.Event.stop(e);
                    if (this.hoverTimer) {
                        clearTimeout(this.hoverTimer);
                        this.hoverTimer = false
                    }
                },
                this))
            },
            this))
        } else {
            this.addEvent(this.anchor, 'click', MagicTools.bindAsEvent(MagicThumb.expand, MagicThumb, this.index))
        }
        this.loaded = true;
        document.body.removeChild(this.loader)
    },
    adjustPosition: function(ps) {
        var padW = parseInt(MagicTools.Element.getStyle(this.cont, 'padding-left')) + parseInt(MagicTools.Element.getStyle(this.cont, 'padding-right')) + parseInt(MagicTools.Element.getStyle(this.cont, 'border-left-width')) + parseInt(MagicTools.Element.getStyle(this.cont, 'border-right-width')),
        padH = parseInt(MagicTools.Element.getStyle(this.cont, 'padding-top')) + parseInt(MagicTools.Element.getStyle(this.cont, 'padding-bottom')) + parseInt(MagicTools.Element.getStyle(this.cont, 'border-top-width')) + parseInt(MagicTools.Element.getStyle(this.cont, 'border-bottom-width'));
        var destTop = destLeft = 0;
        MagicTools.Element.setStyle(this.bigImg, {
            'width': this.bigImg.displayWidth,
            'height': this.bigImg.displayHeight,
            'top': -9999,
            'display': 'block'
        });
        var imgSize = MagicTools.Element.getSize(this.bigImg);
        if ('center' == this.options.zoomPosition) {
            destTop = Math.round((ps.height - padH) / 2 + ps.scrollY - (imgSize.height + this.caption.fullHeight) / 2);
            destLeft = Math.round((ps.width - padW) / 2 + ps.scrollX - imgSize.width / 2);
            if (destTop < ps.scrollY + 10) {
                destTop = ps.scrollY + 10
            }
            if (destLeft < ps.scrollX + 10) {
                destLeft = ps.scrollX + 10
            }
        }
        if ('auto' == this.options.zoomPosition) {
            var sRect = MagicTools.Element.getRect(this.smallImg);
            destTop = sRect.bottom - Math.round((sRect.bottom - sRect.top) / 2) - Math.round(imgSize.height / 2);
            if (destTop + imgSize.height + this.caption.fullHeight > ps.height + ps.scrollY - 15) {
                destTop = ps.height + ps.scrollY - 15 - imgSize.height - this.caption.fullHeight
            }
            if (destTop < ps.scrollY + 10) {
                destTop = ps.scrollY + 10
            }
            destLeft = Math.round(sRect.right - (sRect.right - sRect.left) / 2 - imgSize.width / 2);
            if (destLeft + imgSize.width > ps.width + ps.scrollX - 15) {
                destLeft = ps.width + ps.scrollX - imgSize.width - 15
            }
            if (destLeft < ps.scrollX + 10) {
                destLeft = ps.scrollX + 10
            }
        }
        if ('absolute' == this.options.zoomPosition) {
            destTop = parseInt(this.options.zoomPositionOffset.top + ps.scrollY);
            if (parseInt(this.options.zoomPositionOffset.bottom) > 0) {
                destTop = ps.height + ps.scrollY - parseInt(this.options.zoomPositionOffset.bottom) - imgSize.height - this.caption.fullHeight
            }
            destLeft = parseInt(this.options.zoomPositionOffset.left + ps.scrollX);
            if (parseInt(this.options.zoomPositionOffset.right) > 0) {
                destLeft = ps.width + ps.scrollX - parseInt(this.options.zoomPositionOffset.right) - imgSize.width
            }
        }
        if ('relative' == this.options.zoomPosition) {
            var sRect = MagicTools.Element.getRect(this.smallImg);
            if ('auto' == this.options.zoomPositionOffset.top) {
                destTop = sRect.bottom - Math.round((sRect.bottom - sRect.top) / 2) - Math.round(imgSize.height / 2)
            } else {
                destTop = sRect.top + parseInt(this.options.zoomPositionOffset.top);
                if (parseInt(this.options.zoomPositionOffset.bottom) > 0) {
                    destTop = sRect.bottom - parseInt(this.options.zoomPositionOffset.bottom) - imgSize.height - this.caption.fullHeight
                }
            }
            if ('auto' == this.options.zoomPositionOffset.left) {
                destLeft = Math.round(sRect.right - (sRect.right - sRect.left) / 2 - imgSize.width / 2)
            } else {
                destLeft = sRect.left + parseInt(this.options.zoomPositionOffset.left);
                if (parseInt(this.options.zoomPositionOffset.right) > 0) {
                    destLeft = sRect.right - parseInt(this.options.zoomPositionOffset.right) - imgSize.width
                }
            }
            if (destTop + imgSize.height + this.caption.fullHeight > ps.height + ps.scrollY - 15) {
                destTop = ps.height + ps.scrollY - 15 - imgSize.height - this.caption.fullHeight
            }
            if (destTop < ps.scrollY + 10) {
                destTop = ps.scrollY + 10
            }
            if (destLeft + imgSize.width > ps.width + ps.scrollX - 15) {
                destLeft = ps.width + ps.scrollX - imgSize.width - 15
            }
            if (destLeft < ps.scrollX + 10) {
                destLeft = ps.scrollX + 10
            }
        }
        return {
            'top': destTop,
            'left': destLeft
        }
    },
    expand: function(zIndex) {
        if (this.zoomed) {
            this.focus();
            return false
        }
        if (!this.zoomed && this.rendering) {
            return false
        }
        this.zIndex = zIndex;
        var ps = MagicTools.getPageSize();
        var startPosition = MagicTools.Element.getPosition(this.smallImg);
        MagicTools.extend(this.bigImg, {
            'initTop': startPosition.top,
            'initLeft': startPosition.left
        });
        var startProps = {
            display: 'block',
            'position': 'absolute',
            'opacity': this.options.keepThumbnail ? 0 : 1,
            'top': this.bigImg.initTop,
            'left': this.bigImg.initLeft,
            'width': 'auto',
            'height': 'auto'
        };
        if (MagicThumb.options.fitToScreen) {
            this.bigImg.displayWidth = this.bigImg.fullWidth;
            this.bigImg.displayHeight = this.bigImg.fullHeight;
            this.resizeCaption();
            this.resizeImage(ps);
            if (this.cr) {
                MagicTools.Element.setStyle(this.cr, {
                    'width': this.bigImg.displayWidth * 0.9,
                    'top': this.bigImg.displayHeight - 20
                });
                MagicTools.Element.setStyle(this.cont, {
                    'display': 'block'
                });
                MagicTools.Element.setStyle(this.cr, {
                    'width': '90%',
                    'top': this.bigImg.displayHeight - MagicTools.Element.getSize(this.cr).height - 8
                })
            }
        }
        MagicTools.extend(startProps, {
            'width': this.bigImg.initWidth
        });
        var destPos = this.adjustPosition(ps);
        var effectProps = {
            'opacity': [(this.options.keepThumbnail) ? 0 : 1, 1],
            'top': [this.bigImg.initTop, destPos.top],
            'left': [this.bigImg.initLeft, destPos.left],
            'width': [this.bigImg.initWidth, this.bigImg.displayWidth]
        };
        new MagicTools.Render(this.bigImg, {
            duration: this.options.expandDuration,
            transition: this.options.transition,
            onStart: MagicTools.bind(function() {
                this.toggleMZ(false);
                MagicTools.Element.setStyle(this.bigImg, startProps);
                if (!this.options.keepThumbnail) {
                    MagicTools.Element.setStyle(this.smallImg, {
                        'visibility': 'hidden'
                    })
                }
                var f = MagicThumb.getFocused();
                if (undefined != f) {
                    this.zIndex = f.zIndex + 1
                }
                MagicTools.Element.setStyle(this.bigImg, {
                    'z-index': this.zIndex
                });
                this.overlap = document.createElement('div');
                MagicTools.Element.setStyle(this.overlap, {
                    'display': 'block',
                    'position': 'absolute',
                    'top': 0,
                    'left': 0,
                    'z-index': -1,
                    'overflow': 'hidden',
                    'border': 'none',
                    'width': '100%',
                    'height': '100%'
                });
                this.iframe = document.createElement('iframe');
                this.iframe.src = 'javascript: "";';
                MagicTools.Element.setStyle(this.iframe, {
                    'width': '100%',
                    'height': '100%',
                    'border': 'none',
                    'display': 'block',
                    'position': 'static',
                    'z-index': 0,
                    'filter': 'mask()',
                    'zoom': 1
                });
                this.overlap.appendChild(this.iframe);
                this.cont.appendChild(this.overlap)
            },
            this),
            onComplete: MagicTools.bind(function() {
                MagicTools.Element.addClass(this.anchor, 'MagicThumb-zoomed');
                MagicTools.Element.addClass(this.bigImg, 'MagicThumb-image-zoomed');
                var imgSize = MagicTools.Element.getSize(this.bigImg);
                MagicTools.Element.setStyle(this.cont, {
                    'left': MagicTools.Element.getPosition(this.bigImg).left,
                    'top': MagicTools.Element.getPosition(this.bigImg).top,
                    'width': imgSize.width,
                    'visibility': 'visible'
                });
                this.cont.insertBefore(this.bigImg, this.cont.firstChild);
                MagicTools.Element.setStyle(this.cont, {
                    'display': 'block',
                    'z-index': this.zIndex
                });
                MagicTools.Element.setStyle(this.bigImg, {
                    'position': 'relative',
                    'top': 0,
                    'left': 0,
                    'z-index': 2
                });
                if (MagicTools.browser.ie) {
                    MagicTools.Element.setStyle(this.overlap, {
                        'width': MagicTools.Element.getSize(this.cont).width,
                        'height': MagicTools.Element.getSize(this.cont).height
                    })
                }
                if (this.controlbar) {
                    var cbSize = MagicTools.Element.getSize(this.controlbar);
                    MagicTools.Element.setStyle(this.controlbar, {
                        'position': 'absolute',
                        'z-index': 11,
                        'visibility': (MagicTools.browser.ie6) ? 'visible': 'hidden',
                        'top': /bottom/i.test(this.options.controlbarPosition || MagicThumb.options.controlbarPosition) ? imgSize.height - cbSize.height - 5 : 5,
                        'left': /right/i.test(this.options.controlbarPosition || MagicThumb.options.controlbarPosition) ? imgSize.width - cbSize.width - 5 : 5
                    });
                    if (MagicTools.browser.ie6) {
                        MagicTools.Element.setStyle(this.cbOverlay, {
                            'visibility': 'visible',
                            'width': cbSize.width,
                            'height': cbSize.height,
                            'top': this.controlbar.offsetTop,
                            'left': this.controlbar.offsetLeft,
                            'background-position': '' + (MagicTools.Element.getPosition(this.cont).left - MagicTools.Element.getPosition(this.controlbar).left + parseInt(MagicTools.Element.getStyle(this.bigImg, 'border-left-width'))) + 'px ' + (MagicTools.Element.getPosition(this.cont).top - MagicTools.Element.getPosition(this.controlbar).top + parseInt(MagicTools.Element.getStyle(this.bigImg, 'border-top-width'))) + 'px'
                        })
                    }
                    MagicTools.Event.fire(this.cont, 'MouseEvents', 'mouseover')
                }
                MagicThumb.fixCursor(this.bigImg);
                if (this.firstRun) {
                    this.addEvent(this.bigImg, 'mousedown',
                    function(e) {
                        MagicTools.Event.stop(e)
                    });
                    this.addEvent(this.bigImg, 'click', this.collapseEvent = MagicTools.bindAsEvent(this.collapse, this))
                }
                if ('' != this.caption.innerHTML) {
                    this.toggleCaption(1);
                    this.focus(this.options.captionSlideDuration * 1000 + 10)
                } else {
                    this.focus(0)
                }
                if (parseFloat(MagicThumb.options.backgroundFadingOpacity) > 0) {
                    MagicThumb.fadeInBackground()
                }
                this.rendering = false;
                this.zoomed = true;
                this.firstRun = false
            },
            this)
        }).start(effectProps)
    },
    collapse: function(e, nextThumb, hide) {
        if (e) {
            MagicTools.Event.stop(e)
        }
        if (!this.zoomed || (this.zoomed && this.rendering)) {
            return false
        }
        this.rendering = true;
        hide = hide || false;
        MagicTools.Event.remove(document, "keydown", MagicThumb.onKey);
        if (MagicThumb.options.allowMultipleImages && undefined != nextThumb) {
            MagicTools.Event.fire(nextThumb.anchor, 'MouseEvents', 'click');
            return false
        }
        new MagicTools.Render(this.caption, {
            duration: (!this.hasCaption || hide) ? 0 : this.options.captionSlideDuration,
            transition: MagicTools.Transition.sin,
            onStart: MagicTools.bind(function() {
                MagicTools.Element.setStyle(this.caption, {
                    'margin-top': 0
                });
                MagicTools.Element.removeClass(this.bigImg, 'MagicThumb-image-zoomed')
            },
            this),
            onComplete: MagicTools.bind(function() {
                MagicTools.Element.setStyle(this.caption, {
                    'visibility': 'hidden'
                });
                var pos = MagicTools.Element.getPosition(this.bigImg);
                new MagicTools.Render(this.bigImg, {
                    duration: (hide) ? 0 : this.options.collapseDuration,
                    transition: this.options.transition,
                    onStart: MagicTools.bind(function() {
                        this.cont.removeChild(this.overlap);
                        MagicTools.Element.setStyle(this.bigImg, {
                            'position': 'absolute',
                            'z-index': this.zIndex,
                            'top': pos.top,
                            'left': pos.left
                        });
                        this.bigImg = document.body.appendChild(this.bigImg);
                        MagicTools.Element.setStyle(this.cont, {
                            'top': -9999
                        });
                        if (this.controlbar) {
                            MagicTools.Element.setStyle(this.controlbar, {
                                'left': 0
                            })
                        }
                    },
                    this),
                    onComplete: MagicTools.bind(function() {
                        MagicTools.Element.setStyle(this.smallImg, {
                            'visibility': 'visible'
                        });
                        MagicTools.Element.setStyle(this.bigImg, {
                            'top': -9999
                        });
                        MagicTools.Element.removeClass(this.anchor, 'MagicThumb-zoomed');
                        MagicTools.Element.setStyle(this.smallImg, {
                            'visibility': 'visible'
                        });
                        MagicThumb.fixCursor(this.anchor);
                        this.rendering = false;
                        this.zoomed = false;
                        MagicThumb.unsetFocused(this.index);
                        if (undefined != nextThumb) {
                            MagicThumb.expand(null, nextThumb.index)
                        } else if (MagicThumb.bgFader) {
                            MagicThumb.fadeOutBackground()
                        }
                        this.toggleMZ()
                    },
                    this)
                }).start({
                    'opacity': [1, this.options.keepThumbnail ? 0 : 1],
                    'width': [this.bigImg.displayWidth, this.bigImg.initWidth],
                    'height': [this.bigImg.displayHeight, this.bigImg.initHeight],
                    'top': [pos.top, this.bigImg.initTop],
                    'left': [pos.left, this.bigImg.initLeft]
                })
            },
            this)
        }).start({
            'margin-top': [0, -this.caption.fullHeight || 0]
        })
    },
    focus: function(t) {
        t = t || 0;
        var f = MagicThumb.getFocused();
        if (undefined != f) {
            this.zIndex = f.zIndex + 1;
            MagicTools.Element.setStyle(this.cont, {
                'z-index': this.zIndex
            })
        }
        MagicThumb.setFocused(this.index);
        setTimeout(function() {
            MagicTools.Event.remove(document, "keydown", MagicThumb.onKey);
            MagicTools.Event.add(document, "keydown", MagicThumb.onKey)
        },
        t)
    },
    toggleCaption: function() {
        new MagicTools.Render(this.caption, {
            duration: this.options.captionSlideDuration,
            transition: MagicTools.Transition.sin,
            onStart: MagicTools.bind(function() {
                MagicTools.Element.setStyle(this.caption, {
                    'margin-top': -this.caption.fullHeight
                });
                MagicTools.Element.setStyle(this.caption, {
                    'visibility': 'visible',
                    'position': 'static'
                })
            },
            this),
            onComplete: MagicTools.bind(function() {
                if (MagicTools.browser.ie) {
                    MagicTools.Element.setStyle(this.overlap, {
                        'width': MagicTools.Element.getSize(this.cont).width,
                        'height': MagicTools.Element.getSize(this.cont).height
                    })
                }
            },
            this)
        }).start({
            'margin-top': [ - this.caption.fullHeight, 0]
        })
    },
    toggleControlBar: function(e, show) {
        if (e) {
            MagicTools.Event.stop(e)
        }
        show = show || false;
        var rect = MagicTools.Element.getRect(this.cont);
        var ieBody = (document.compatMode && 'backcompat' != document.compatMode.toLowerCase()) ? document.documentElement: document.body;
        var eX = e.clientX + parseInt((self.pageXOffset) ? self.pageXOffset: ieBody.scrollLeft);
        var eY = e.clientY + parseInt((self.pageYOffset) ? self.pageYOffset: ieBody.scrollTop);
        var ov = /mouseover/i.test(e.type);
        var vis = MagicTools.Element.getStyle(this.controlbar, 'visibility');
        if ((!ov || 'hidden' != vis) && (eX > rect.left && eX < rect.right) && (eY > rect.top && eY < rect.bottom)) {
            return
        }
        if (ov && 'hidden' != vis && !show) {
            return
        }
        if (!ov && 'hidden' == vis) {
            return
        }
        var op = (show || ov) ? [0, 1] : [1, 0];
        new MagicTools.Render(this.controlbar, {
            duration: 0.3,
            transition: MagicTools.Transition.linear
        }).start({
            'opacity': op
        });
        return
    },
    onCBClick: function(e) {
        var o = e.currentTarget || e.srcElement;
        while (o && 'a' != o.tagName.toLowerCase()) {
            o = o.offsetParent
        }
        var stopEvent = true;
        switch (o.rel) {
        case 'prev':
            this.collapse(null, MagicThumb.getPrev(this.group));
            break;
        case 'next':
            this.collapse(null, MagicThumb.getNext(this.group));
            break;
        case 'close':
            this.collapse(null);
            break;
        default:
            stopEvent = false
        }
        if (stopEvent) {
            MagicTools.Event.stop(e)
        }
        return false
    },
    toggleMZ: function(show) {
        show = (undefined !== show) ? show: true;
        if (MagicTools.Element.hasClass(this.anchor, 'MagicZoom')) {
            try {
                if (show) {
                    this.anchor.zoom.recalculating = false
                } else {
                    this.anchor.zoom.hiderect();
                    this.anchor.zoom.recalculating = true
                }
            } catch(e) {}
        }
    },
    resizeImage: function(ps) {
        var padW = parseInt(MagicTools.Element.getStyle(this.cont, 'padding-left')) + parseInt(MagicTools.Element.getStyle(this.cont, 'padding-right')) + parseInt(MagicTools.Element.getStyle(this.cont, 'border-left-width')) + parseInt(MagicTools.Element.getStyle(this.cont, 'border-right-width')),
        padH = parseInt(MagicTools.Element.getStyle(this.cont, 'padding-top')) + parseInt(MagicTools.Element.getStyle(this.cont, 'padding-bottom')) + parseInt(MagicTools.Element.getStyle(this.cont, 'border-top-width')) + parseInt(MagicTools.Element.getStyle(this.cont, 'border-bottom-width'));
        var x = Math.min(this.bigImg.displayWidth, ps.width - 35 - padW),
        y = Math.min(this.bigImg.displayHeight, ps.height - 35 - padH - this.caption.fullHeight);
        if (x / y > this.bigImg.ratio) {
            x = y * this.bigImg.ratio
        } else if (x / y < this.bigImg.ratio) {
            y = x / this.bigImg.ratio
        }
        this.bigImg.displayWidth = Math.ceil(x);
        this.bigImg.displayHeight = Math.ceil(y);
        this.resizeCaption()
    },
    resizeCaption: function() {
        MagicTools.Element.setStyle(this.caption, {
            'width': this.bigImg.displayWidth - this.caption.paddingLeft - this.caption.paddingRight - parseInt(MagicTools.Element.getStyle(this.caption, 'border-left-width')) - parseInt(MagicTools.Element.getStyle(this.caption, 'border-right-width'))
        });
        MagicTools.Element.setStyle(this.cont, {
            'top': -9999,
            'display': 'block'
        });
        MagicTools.extend(this.caption, {
            'fullHeight': MagicTools.Element.getSize(this.caption).height
        });
        MagicTools.Element.setStyle(this.cont, {
            'display': 'none'
        })
    }
};
if (MagicTools.browser.ie6) {
    MagicThumb.Item.prototype.toggleControlBar = function(e, show) {
        if (e) {
            MagicTools.Event.stop(e)
        }
        show = show || false;
        var rect = MagicTools.Element.getRect(this.cont);
        var ieBody = (document.compatMode && 'backcompat' != document.compatMode.toLowerCase()) ? document.documentElement: document.body;
        var eX = e.clientX + parseInt((self.pageXOffset) ? self.pageXOffset: ieBody.scrollLeft);
        var eY = e.clientY + parseInt((self.pageYOffset) ? self.pageYOffset: ieBody.scrollTop);
        var ov = /mouseover/i.test(e.type);
        var vis = MagicTools.Element.getStyle(this.cbOverlay, 'visibility');
        if ((!ov || !('hidden' != vis)) && (eX > rect.left && eX < rect.right) && (eY > rect.top && eY < rect.bottom)) {
            return
        }
        if (ov && !('hidden' != vis) && !show) {
            return
        }
        if (!ov && 'hidden' != vis) {
            return
        }
        var op = (show || ov) ? [1, 0] : [0, 1];
        new MagicTools.Render(this.cbOverlay, {
            duration: 0.3,
            transition: MagicTools.Transition.linear
        }).start({
            'opacity': op
        });
        return
    };
    try {
        document.execCommand('BackgroundImageCache', false, true)
    } catch(e) {}
}
MagicTools.Event.add(document, 'domready',
function() {
    MagicThumb.init()
});