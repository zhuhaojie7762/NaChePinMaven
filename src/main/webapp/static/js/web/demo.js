jQuery(function() {
    var $ = jQuery;
    if($('#uploader').length > 0) {
        var $wrap = $('#uploader'),

        // 图片容器
        $queue = $('<ul class="filelist"></ul>')
                .appendTo($wrap.find('.queueList')),

        // 状态栏，包括进度和控制按钮
            $statusBar = $wrap.find('.statusBar'),

        // 文件总体选择信息。
            $info = $statusBar.find('.info'),

        // 上传按钮
            $upload = $wrap.find('.uploadBtn'),

        // 没选择文件之前的内容。
            $placeHolder = $wrap.find('.placeholder'),

        // 总体进度条
            $progress = $statusBar.find('.progress').hide(),

        // 添加的文件数量
            fileCount = 0,

        // 添加的文件总大小
            fileSize = 0,

        // 优化retina, 在retina下这个值是2
            ratio = window.devicePixelRatio || 1,

        // 缩略图大小
            thumbnailWidth = 110 * ratio,
            thumbnailHeight = 110 * ratio,

        // 可能有pedding, ready, uploading, confirm, done.
            state = 'pedding',

        // 所有文件的进度信息，key为file id
            percentages = {},

            supportTransition = (function () {
                var s = document.createElement('p').style,
                    r = 'transition' in s ||
                        'WebkitTransition' in s ||
                        'MozTransition' in s ||
                        'msTransition' in s ||
                        'OTransition' in s;
                s = null;
                return r;
            })(),

        // WebUploader实例
            uploader;

        if (!WebUploader.Uploader.support()) {
            alert('Web Uploader 不支持您的浏览器！如果你使用的是IE浏览器，请尝试升级 flash 播放器');
            throw new Error('WebUploader does not support the browser you are using.');
        }

        // 实例化
        uploader = WebUploader.create({
            //// 选完文件后，是否自动上传。
            //auto: true,

            pick: {
                id: '#filePicker',
                label: '点击选择图片'
            },
            dnd: '#uploader .queueList',
            paste: document.body,

            accept: {
                title: 'Images',
                extensions: 'gif,jpg,jpeg,bmp,png',
                mimeTypes: 'image/*'
            },

            // swf文件路径
            swf: BASE_URL + '/js/Uploader.swf',

            disableGlobalDnd: true,

            chunked: true,
            server: 'http://v0.api.upyun.com/nachepin-storage',
            fileNumLimit: 5,
            fileSizeLimit: 5 * 1024 * 1024,    // 5 M
            fileSingleSizeLimit: 1 * 1024 * 1024,    // 1 M
            formData: {
                'policy': $("#imagePolicy").val(),
                'signature': $("#imageSignature").val()
            },
            compress: false
        });

        // 添加“添加文件”的按钮，
        uploader.addButton({
            id: '#filePicker2',
            label: '继续添加'
        });

        // 当有文件添加进来时执行，负责view的创建
        function addFile(file) {
            var $li = $('<li id="' + file.id + '">' +
                    '<p class="title">' + file.name + '</p>' +
                    '<p class="imgWrap"></p>' +
                    '<p class="progress"><span></span></p>' +
                    '</li>'),

                $btns = $('<div class="file-panel">' +
                    '<span class="cancel">删除</span>' +
                    '<span class="rotateRight">向右旋转</span>' +
                    '<span class="rotateLeft">向左旋转</span></div>').appendTo($li),
                $prgress = $li.find('p.progress span'),
                $wrap = $li.find('p.imgWrap'),
                $info = $('<p class="error"></p>'),

                showError = function (code) {
                    switch (code) {
                        case 'exceed_size':
                            text = '文件大小超出';
                            break;

                        case 'interrupt':
                            text = '上传暂停';
                            break;

                        default:
                            text = '上传失败，请重试';
                            break;
                    }

                    $info.text(text).appendTo($li);
                };

            if (file.getStatus() === 'invalid') {
                showError(file.statusText);
            } else {
                // @todo lazyload
                $wrap.text('预览中');
                uploader.makeThumb(file, function (error, src) {
                    if (error) {
                        $wrap.text('不能预览');
                        return;
                    }

                    var img = $('<img src="' + src + '">');
                    $wrap.empty().append(img);
                }, thumbnailWidth, thumbnailHeight);

                percentages[file.id] = [file.size, 0];
                file.rotation = 0;
            }

            file.on('statuschange', function (cur, prev) {
                if (prev === 'progress') {
                    $prgress.hide().width(0);
                } else if (prev === 'queued') {
                    $li.off('mouseenter mouseleave');
                    $btns.remove();
                }

                // 成功
                if (cur === 'error' || cur === 'invalid') {
                    console.log(file.statusText);
                    showError(file.statusText);
                    percentages[file.id][1] = 1;
                } else if (cur === 'interrupt') {
                    showError('interrupt');
                } else if (cur === 'queued') {
                    percentages[file.id][1] = 0;
                } else if (cur === 'progress') {
                    $info.remove();
                    $prgress.css('display', 'block');
                } else if (cur === 'complete') {
                    $li.append('<span class="success"></span>');
                }

                $li.removeClass('state-' + prev).addClass('state-' + cur);
            });

            $li.on('mouseenter', function () {
                $btns.stop().animate({height: 30});
            });

            $li.on('mouseleave', function () {
                $btns.stop().animate({height: 0});
            });

            $btns.on('click', 'span', function () {
                var index = $(this).index(),
                    deg;

                switch (index) {
                    case 0:
                        uploader.removeFile(file);
                        return;

                    case 1:
                        file.rotation += 90;
                        break;

                    case 2:
                        file.rotation -= 90;
                        break;
                }

                if (supportTransition) {
                    deg = 'rotate(' + file.rotation + 'deg)';
                    $wrap.css({
                        '-webkit-transform': deg,
                        '-mos-transform': deg,
                        '-o-transform': deg,
                        'transform': deg
                    });
                } else {
                    $wrap.css('filter', 'progid:DXImageTransform.Microsoft.BasicImage(rotation=' + (~~((file.rotation / 90) % 4 + 4) % 4) + ')');
                    // use jquery animate to rotation
                    // $({
                    //     rotation: rotation
                    // }).animate({
                    //     rotation: file.rotation
                    // }, {
                    //     easing: 'linear',
                    //     step: function( now ) {
                    //         now = now * Math.PI / 180;

                    //         var cos = Math.cos( now ),
                    //             sin = Math.sin( now );

                    //         $wrap.css( 'filter', "progid:DXImageTransform.Microsoft.Matrix(M11=" + cos + ",M12=" + (-sin) + ",M21=" + sin + ",M22=" + cos + ",SizingMethod='auto expand')");
                    //     }
                    // });
                }

            });

            $li.appendTo($queue);
        }

        // 负责view的销毁
        function removeFile(file) {
            var $li = $('#' + file.id);

            delete percentages[file.id];
            updateTotalProgress();
            $li.off().find('.file-panel').off().end().remove();
        }

        function updateTotalProgress() {
            var loaded = 0,
                total = 0,
                spans = $progress.children(),
                percent;

            $.each(percentages, function (k, v) {
                total += v[0];
                loaded += v[0] * v[1];
            });

            percent = total ? loaded / total : 0;

            spans.eq(0).text(Math.round(percent * 100) + '%');
            spans.eq(1).css('width', Math.round(percent * 100) + '%');
            updateStatus();
        }

        function updateStatus() {
            var text = '', stats;

            if (state === 'ready') {
                text = '选中' + fileCount + '张图片，共' +
                    WebUploader.formatSize(fileSize) + '。';
            } else if (state === 'confirm') {
                stats = uploader.getStats();
                if (stats.uploadFailNum) {
                    text = '已成功上传' + stats.successNum + '张照片，' +
                        stats.uploadFailNum + '张照片上传失败，<a class="retry" href="#">重新上传</a>失败图片或<a class="ignore" href="#">忽略</a>'
                }

            } else {
                stats = uploader.getStats();
                text = '共' + fileCount + '张（' +
                    WebUploader.formatSize(fileSize) +
                    '），已上传' + stats.successNum + '张';

                if (stats.uploadFailNum) {
                    text += '，失败' + stats.uploadFailNum + '张';
                }
            }

            $info.html(text);
        }

        function setState(val) {

            if (val === state) {
                return;
            }

            $upload.removeClass('state-' + state);
            $upload.addClass('state-' + val);

            var stats = uploader.getStats();
            switch (val) {
                case 'pedding':
                    $placeHolder.removeClass('element-invisible');
                    $queue.parent().removeClass('filled');
                    $queue.hide();
                    $statusBar.addClass('element-invisible');
                    uploader.refresh();
                    break;

                case 'ready':
                    $placeHolder.addClass('element-invisible');
                    $('#filePicker2').removeClass('element-invisible');
                    $queue.parent().addClass('filled');
                    $queue.show();
                    $statusBar.removeClass('element-invisible');
                    $upload.removeClass('disabled');
                    uploader.refresh();
                    break;

                case 'uploading':
                    $('#filePicker2').addClass('element-invisible');
                    $progress.show();
                    $upload.text('暂停上传');
                    break;

                case 'paused':
                    $progress.show();
                    $upload.text('继续上传');
                    break;

                case 'confirm':
                    $progress.hide();
                    $upload.text('开始上传').addClass('disabled');

                    stats = uploader.getStats();
                    if (stats.successNum && !stats.uploadFailNum) {
                        setState('finish');
                        return;
                    }
                    break;
                case 'finish':
                    stats = uploader.getStats();
                    if (stats.successNum) {
                        //alert('上传成功');
                        $('#filePicker2').removeClass('element-invisible');
                        MsgBox.success($queue, '上传成功');
                    } else {
                        // 没有成功的图片，重设
                        state = 'done';
                        location.reload();
                    }
                    break;
            }

            state = val;
            updateStatus();
        }

        uploader.onUploadProgress = function (file, percentage) {
            var $li = $('#' + file.id),
                $percent = $li.find('.progress span');

            $percent.css('width', percentage * 100 + '%');
            percentages[file.id][1] = percentage;
            updateTotalProgress();
        };

        uploader.onFileQueued = function (file) {
            fileCount++;
            fileSize += file.size;

            if (fileCount === 1) {
                $placeHolder.addClass('element-invisible');
                $statusBar.show();
            }

            addFile(file);
            setState('ready');
            updateTotalProgress();
        };

        uploader.onFileDequeued = function (file) {
            fileCount--;
            fileSize -= file.size;

            if (!fileCount) {
                setState('pedding');
            }

            removeFile(file);
            updateTotalProgress();

        };

        uploader.on('all', function (type) {
            var stats;
            switch (type) {
                case 'uploadFinished':
                    setState('confirm');
                    break;

                case 'startUpload':
                    setState('uploading');
                    break;

                case 'stopUpload':
                    setState('paused');
                    break;

            }
        });

        uploader.on('uploadSuccess', function (file, json) {
            var $file = $('#' + file.id);
            try {
                //var responseText = (ret._raw || ret),
                //    json = utils.str2json(responseText);
                if (json.code == "200") {
                    $file.append('<input type="hidden" name="headImgUrl" value="' + json.url + '" />');
                }
            } catch (e) {
                $file.find('.error').text(lang.errorServerUpload).show();
            }
        });

        uploader.onError = function (code) {
            var msg = "";
            if(code == "Q_EXCEED_NUM_LIMIT"){
                msg = "添加的文件数量过多";
            }else if(code == "Q_EXCEED_SIZE_LIMIT"){
                msg = "添加的文件过大";
            }else if(code == "Q_TYPE_DENIED") {
                msg = "添加的文件类型不正确";
            }else if(code == "F_DUPLICATE"){
                msg = "文件重复";
            }else{
                msg = code;
            }
            MsgBox.danger($queue, 'Error: ' + msg);
        };

        $upload.on('click', function () {
            if ($(this).hasClass('disabled')) {
                return false;
            }

            getImageParam();

            if (state === 'ready') {
                uploader.upload();
            } else if (state === 'paused') {
                uploader.upload();
            } else if (state === 'uploading') {
                uploader.stop();
            }
        });

        function getImageParam() {
//            $.ajax({
//                url: "/member/newGoods/getPolicy",
//                type: "post",
//                async: false,
//                data: {type: "image"},
//                success: function (json) {
//                    if (json.code == Common.SUCC) {
//                        $("#policy").val(json.data.policy);
//                        $("#signature").val(json.data.signature);
//                    } else {
//                        Msg.danger(json.message);
//                    }
//                }
//            });
        }


        $info.on('click', '.retry', function () {
            uploader.retry();
        });

        $info.on('click', '.ignore', function () {
            alert('todo');
        });

        $upload.addClass('state-' + state);
        updateTotalProgress();
    }

});

//以下是新增商品信息页
// 文件上传01
jQuery(function() {
    var $ = jQuery,
        $list = $('#thelist'),
        $btn = $('#ctlBtn'),
        state = 'pending',
        uploader;

    uploader = WebUploader.create({

        // 选完文件后，是否自动上传。
        auto: true,

        // 不压缩image
        resize: false,

        // swf文件路径
        swf: BASE_URL + '/js/Uploader.swf',

        // 文件接收服务端。
        server: 'http://v0.api.upyun.com/nachepin-storage',

        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        //zdj 修改
        pick: '#filePicker_d1',

        fileNumLimit: 1,
        fileSizeLimit: 100 * 1024 * 1024,    // 100 M
        fileSingleSizeLimit: 100 * 1024 * 1024,    // 100 M

        formData:{'policy':$("#filePolicy").val(),
            'signature':$("#fileSignature").val()}
    });


    // 当有文件添加进来的时候
    uploader.on( 'fileQueued', function( file ) {
        $list.find("label").hide();
        $list.find(".item").remove();
        $list.append( '<div id="' + file.id + '" class="item shangChuanJar">' +
            '<span class="info zhidingYS-info" >' + file.name + '</span>' +
            '<span class="state zhidingYS-state">等待上传...</span>' +
            '</div>' );
        $("#uploading1").val("上传中");
    });

    // 文件上传过程中创建进度条实时显示。
    uploader.on( 'uploadProgress', function( file, percentage ) {
        var $li = $( '#'+file.id ),
            $percent = $li.find('.progress .progress-bar');

        // 避免重复创建
        if ( !$percent.length ) {
            $percent = $('<div class="progress progress-striped active" >' +
                '<div class="progress-bar" role="progressbar" style="width: 0%">' +
                '</div>' +
                '</div>').appendTo( $li ).find('.progress-bar');
        }

        $li.find('span.state').text('上传中');

        $percent.css( 'width', percentage * 100 + '%' );
    });

    uploader.on( 'uploadSuccess', function( file, json ) {
        var $file = $('#' + file.id);
        $file.find('span.state').text('已上传');
        if (json.code == "200") {
            $file.siblings("input:hidden").val(json.url);
            $("#uploading1").val("");
            uploader.reset();
        }
    });

    uploader.on( 'uploadError', function( file ) {
    	var $file = $('#' + file.id);
    	$file.find('span.state').text('上传出错，请重新上传');
    	
        $file.siblings("input:hidden").val("");
        $("#uploading1").val("");
        uploader.reset();
    });

    uploader.onError = function( code ) {
        var msg = "";
        if(code == "Q_EXCEED_NUM_LIMIT"){
            msg = "添加的文件数量过多";
        }else if(code == "Q_EXCEED_SIZE_LIMIT"){
            msg = "添加的文件过大";
        }else if(code == "Q_TYPE_DENIED"){
            msg = "添加的文件类型不正确";
        }else{
            msg = code;
        }
        MsgBox.danger($list, 'Error: ' + msg);
    };

    uploader.on( 'uploadComplete', function( file ) {
        $( '#'+file.id ).find('.progress').fadeOut();
    });

    uploader.on( 'all', function( type ) {
        if ( type === 'startUpload' ) {
            state = 'uploading';
        } else if ( type === 'stopUpload' ) {
            state = 'paused';
        } else if ( type === 'uploadFinished' ) {
            state = 'done';
        }

        if ( state === 'uploading' ) {
            $btn.text('暂停上传');
        } else {
            $btn.text('开始上传');
        }
    });

    $btn.on( 'click', function() {
        if ( state === 'uploading' ) {
            uploader.stop();
        } else {
            uploader.upload();
        }
    });
});

// 文件上传02
jQuery(function() {
    var $ = jQuery,
        $list = $('#thelist02'),
        $btn = $('#ctlBtn'),
        state = 'pending',
        uploader;

    uploader = WebUploader.create({

        // 选完文件后，是否自动上传。
        auto: true,

        // 不压缩image
        resize: false,

        // swf文件路径
        swf: BASE_URL + '/js/Uploader.swf',

        // 文件接收服务端。
        server: 'http://v0.api.upyun.com/nachepin-storage',

        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        //zdj 修改
        pick: '#filePicker_d2',

        fileNumLimit: 1,
        fileSizeLimit: 100 * 1024 * 1024,    // 100 M
        fileSingleSizeLimit: 100 * 1024 * 1024,    // 100 M

        formData:{'policy':$("#filePolicy").val(),
            'signature':$("#fileSignature").val()}
    });


    // 当有文件添加进来的时候
    uploader.on( 'fileQueued', function( file ) {
        $list.find("label").hide();
        $list.find(".item").remove();
        $list.append( '<div id="' + file.id + '" class="item shangChuanJar">' +
            '<span class="info zhidingYS-info" >' + file.name + '</span>' +
            '<span class="state zhidingYS-state">等待上传...</span>' +
            '</div>' );
        $("#uploading2").val("上传中");
    });

    // 文件上传过程中创建进度条实时显示。
    uploader.on( 'uploadProgress', function( file, percentage ) {
        var $li = $( '#'+file.id ),
            $percent = $li.find('.progress .progress-bar');

        // 避免重复创建
        if ( !$percent.length ) {
            $percent = $('<div class="progress progress-striped active">' +
                '<div class="progress-bar" role="progressbar" style="width: 0%">' +
                '</div>' +
                '</div>').appendTo( $li ).find('.progress-bar');
        }

        $li.find('span.state').text('上传中');

        $percent.css( 'width', percentage * 100 + '%' );
    });

    uploader.on( 'uploadSuccess', function( file, json ) {
        var $file = $('#' + file.id);
        $file.find('span.state').text('已上传');
        if (json.code == "200") {
            $file.siblings("input:hidden").val(json.url);
            $("#uploading2").val("");
            uploader.reset();
        }
    });

    uploader.on( 'uploadError', function( file ) {
    	var $file = $('#' + file.id);
    	$file.find('span.state').text('上传出错，请重新上传');
    	
        $file.siblings("input:hidden").val("");
        $("#uploading2").val("");
        uploader.reset();
    });

    uploader.onError = function( code ) {
        var msg = "";
        if(code == "Q_EXCEED_NUM_LIMIT"){
            msg = "添加的文件数量过多";
        }else if(code == "Q_EXCEED_SIZE_LIMIT"){
            msg = "添加的文件过大";
        }else if(code == "Q_TYPE_DENIED"){
            msg = "添加的文件类型不正确";
        }else{
            msg = code;
        }
        MsgBox.danger($list, 'Error: ' + msg);
    };

    uploader.on( 'uploadComplete', function( file ) {
        $( '#'+file.id ).find('.progress').fadeOut();
    });

    uploader.on( 'all', function( type ) {
        if ( type === 'startUpload' ) {
            state = 'uploading';
        } else if ( type === 'stopUpload' ) {
            state = 'paused';
        } else if ( type === 'uploadFinished' ) {
            state = 'done';
        }

        if ( state === 'uploading' ) {
            $btn.text('暂停上传');
        } else {
            $btn.text('开始上传');
        }
    });

    $btn.on( 'click', function() {
        if ( state === 'uploading' ) {
            uploader.stop();
        } else {
            uploader.upload();
        }
    });
});

// 文件上传03
jQuery(function() {
    var $ = jQuery,
        $list = $('#thelist03'),
        $btn = $('#ctlBtn'),
        state = 'pending',
        uploader;

    uploader = WebUploader.create({

        // 选完文件后，是否自动上传。
        auto: true,

        // 不压缩image
        resize: false,

        // swf文件路径
        swf: BASE_URL + '/js/Uploader.swf',

        // 文件接收服务端。
        server: 'http://v0.api.upyun.com/nachepin-storage',

        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        //zdj 修改
        pick: '#filePicker_d3',

        fileNumLimit: 1,
        fileSizeLimit: 100 * 1024 * 1024,    // 100 M
        fileSingleSizeLimit: 100 * 1024 * 1024,    // 100 M

        formData:{'policy':$("#filePolicy").val(),
            'signature':$("#fileSignature").val()}
    });


    // 当有文件添加进来的时候
    uploader.on( 'fileQueued', function( file ) {
        $list.find("label").hide();
        $list.find(".item").remove();
        $list.append( '<div id="' + file.id + '" class="item shangChuanJar">' +
            '<span class="info zhidingYS-info" >' + file.name + '</span>' +
            '<span class="state zhidingYS-state">等待上传...</span>' +
            '</div>' );
        $("#uploading3").val("上传中");
    });

    // 文件上传过程中创建进度条实时显示。
    uploader.on( 'uploadProgress', function( file, percentage ) {
        var $li = $( '#'+file.id ),
            $percent = $li.find('.progress .progress-bar');

        // 避免重复创建
        if ( !$percent.length ) {
            $percent = $('<div class="progress progress-striped active">' +
                '<div class="progress-bar" role="progressbar" style="width: 0%">' +
                '</div>' +
                '</div>').appendTo( $li ).find('.progress-bar');
        }

        $li.find('span.state').text('上传中');

        $percent.css( 'width', percentage * 100 + '%' );
    });

    uploader.on( 'uploadSuccess', function( file, json ) {
        var $file = $('#' + file.id);
        $file.find('span.state').text('已上传');
        if (json.code == "200") {
            $file.siblings("input:hidden").val(json.url);
            uploader.reset();
            $("#uploading3").val("");
        }
    });

    uploader.on( 'uploadError', function( file ) {
    	var $file = $('#' + file.id);
    	$file.find('span.state').text('上传出错，请重新上传');
    	
        $file.siblings("input:hidden").val("");
        $("#uploading3").val("");
        uploader.reset();
    });

    uploader.onError = function( code ) {
        var msg = "";
        if(code == "Q_EXCEED_NUM_LIMIT"){
            msg = "添加的文件数量过多";
        }else if(code == "Q_EXCEED_SIZE_LIMIT"){
            msg = "添加的文件过大";
        }else if(code == "Q_TYPE_DENIED"){
            msg = "添加的文件类型不正确";
        }else{
            msg = code;
        }
        MsgBox.danger($list, 'Error: ' + msg);
    };

    uploader.on( 'uploadComplete', function( file ) {
        $( '#'+file.id ).find('.progress').fadeOut();
    });

    uploader.on( 'all', function( type ) {
        if ( type === 'startUpload' ) {
            state = 'uploading';
        } else if ( type === 'stopUpload' ) {
            state = 'paused';
        } else if ( type === 'uploadFinished' ) {
            state = 'done';
        }

        if ( state === 'uploading' ) {
            $btn.text('暂停上传');
        } else {
            $btn.text('开始上传');
        }
    });

    $btn.on( 'click', function() {
        if ( state === 'uploading' ) {
            uploader.stop();
        } else {
            uploader.upload();
        }
    });
});

// 文件上传04
jQuery(function() {
    var $ = jQuery,
        $list = $('#thelist04'),
        $btn = $('#ctlBtn'),
        state = 'pending',
        uploader;

    uploader = WebUploader.create({

        // 选完文件后，是否自动上传。
        auto: true,

        // 不压缩image
        resize: false,

        // swf文件路径
        swf: BASE_URL + '/js/Uploader.swf',

        // 文件接收服务端。
        server: 'http://v0.api.upyun.com/nachepin-storage',

        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        //zdj 修改
        pick: '#filePicker_d4',

        fileNumLimit: 1,
        fileSizeLimit: 100 * 1024 * 1024,    // 100 M
        fileSingleSizeLimit: 100 * 1024 * 1024,    // 100 M

        formData:{'policy':$("#filePolicy").val(),
            'signature':$("#fileSignature").val()}
    });


    // 当有文件添加进来的时候
    uploader.on( 'fileQueued', function( file ) {
        $list.find("label").hide();
        $list.find(".item").remove();
        $list.append( '<div id="' + file.id + '" class="item shangChuanJar">' +
            '<span class="info zhidingYS-info" >' + file.name + '</span>' +
            '<span class="state zhidingYS-state">等待上传...</span>' +
            '</div>' );
        $("#uploading4").val("上传中");
    });

    //function getFileParam(){
    //    $.ajax({
    //        url:"/member/newGoods/getPolicy",
    //        type:"post",
    //        async: false,
    //        data:{type:"file"},
    //        success:function(json){
    //            if(json.code == Common.SUCC){
    //                $("#policy").val(json.data.policy);
    //                $("#signature").val(json.data.signature);
    //            }else{
    //                Msg.danger(json.message);
    //            }
    //        }
    //    });
    //}

    // 文件上传过程中创建进度条实时显示。
    uploader.on( 'uploadProgress', function( file, percentage ) {
        var $li = $( '#'+file.id ),
            $percent = $li.find('.progress .progress-bar');

        // 避免重复创建
        if ( !$percent.length ) {
            $percent = $('<div class="progress progress-striped active">' +
                '<div class="progress-bar" role="progressbar" style="width: 0%">' +
                '</div>' +
                '</div>').appendTo( $li ).find('.progress-bar');
        }

        $li.find('span.state').text('上传中');

        $percent.css( 'width', percentage * 100 + '%' );
    });

    uploader.on( 'uploadSuccess', function( file, json ) {
        var $file = $('#' + file.id);
        $file.find('span.state').text('已上传');
        if (json.code == "200") {
            $file.siblings("input:hidden").val(json.url);
            $("#uploading4").val("");
            uploader.reset();
        }
    });

    uploader.on( 'uploadError', function( file ) {
    	var $file = $('#' + file.id);
    	$file.find('span.state').text('上传出错，请重新上传');
    	
        $file.siblings("input:hidden").val("");
        $("#uploading4").val("");
        uploader.reset();
    });

    uploader.onError = function( code ) {
        var msg = "";
        if(code == "Q_EXCEED_NUM_LIMIT"){
            msg = "添加的文件数量过多";
        }else if(code == "Q_EXCEED_SIZE_LIMIT"){
            msg = "添加的文件过大";
        }else if(code == "Q_TYPE_DENIED"){
            msg = "添加的文件类型不正确";
        }else{
            msg = code;
        }
        MsgBox.danger($list, 'Error: ' + msg);
    };

    uploader.on( 'uploadComplete', function( file ) {
        $( '#'+file.id ).find('.progress').fadeOut();
    });

    uploader.on( 'all', function( type ) {
        if ( type === 'startUpload' ) {
            state = 'uploading';
        } else if ( type === 'stopUpload' ) {
            state = 'paused';
        } else if ( type === 'uploadFinished' ) {
            state = 'done';
        }

        if ( state === 'uploading' ) {
            $btn.text('暂停上传');
        } else {
            $btn.text('开始上传');
        }
    });

    $btn.on( 'click', function() {
        if ( state === 'uploading' ) {
            uploader.stop();
        } else {
            uploader.upload();
        }
    });
});


function uploadImage(i) {
    var $ = jQuery,
        $list = $('#imglist' + i),
        $showImg = $('#showImg' + i),
        state = 'pending',
        uploader;

    uploader = WebUploader.create({
        // 选完文件后，是否自动上传。
        auto: true,
        // 不压缩image
        resize: false,

        accept: {
            title: 'Images',
            extensions: 'gif,jpg,jpeg,bmp,png',
            mimeTypes: 'image/*'
        },

        // swf文件路径
        swf: BASE_URL + '/js/Uploader.swf',

        // 文件接收服务端。
        server: 'http://v0.api.upyun.com/nachepin-storage',

        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        //zdj 修改
        pick: '#filePicker_m'+i,

        fileNumLimit: 1,
        fileSizeLimit: 50 * 1024 * 1024,    // 50 M
        fileSingleSizeLimit: 50 * 1024 * 1024,    // 50 M

        formData:{'policy':$("#imagePolicy").val(),
            'signature':$("#imageSignature").val()},
        compress: false
    });


    // 当有文件添加进来的时候
    uploader.on( 'fileQueued', function( file ) {
        $list.find("label").hide();
        $list.find(".item").remove();
        $list.append( '<div id="' + file.id + '" class="item">' +
            //'<h4 class="info zhidingYS-info">' + file.name + '</h4>' +
            //'<p class="state zhidingYS-state">等待上传...</p>' +
            '</div>' );
        $list.find('input.uploadState').val('上传中');
    });

 /*   // 文件上传过程中创建进度条实时显示。
    uploader.on( 'uploadProgress', function( file, percentage ) {
        var $li = $( '#'+file.id ),
            $percent = $li.find('.progress .progress-bar');

        // 避免重复创建
        if ( !$percent.length ) {
            $percent = $('<div class="progress progress-striped active">' +
                '<div class="progress-bar" role="progressbar" style="width: 0%">' +
                '</div>' +
                '</div>').appendTo( $li ).find('.progress-bar');
        }

        $li.find('span.state').text('上传中');

        $percent.css( 'width', percentage * 100 + '%' );
    });*/

    uploader.on( 'uploadSuccess', function( file, json ) {
        var $file = $('#' + file.id);
        //$file.find('span.state').text('已上传');
        if (json.code == "200") {
            //$file.siblings("input:hidden").val(json.url);
            var imgURL = $list.parents("td").find("input[name='goodsImg']");
            var dataValue = imgURL.attr("data-type");

            imgURL.parents("tbody").find("[data-type='"+dataValue+"']").val(json.url);
            uploader.reset();
            $list.find('input.uploadState').val('');

            uploader.makeThumb(file, function (error, src) {
                if (error) {
                    $showImg.text('不能预览');
                    return;
                }
                $showImg.find('img').remove();
                $showImg.append('<img src = "' + src + '" />');
            });
        }
    });

    uploader.on( 'uploadError', function( file ) {
        $( '#'+file.id ).find('span.state').text('上传出错');
    });

    uploader.onError = function( code ) {
        var msg = "";
        if(code == "Q_EXCEED_NUM_LIMIT"){
            msg = "添加的文件数量过多";
        }else if(code == "Q_EXCEED_SIZE_LIMIT"){
            msg = "添加的文件过大";
        }else if(code == "Q_TYPE_DENIED"){
            msg = "添加的文件类型不正确";
        }else{
            msg = code;
        }
        MsgBox.danger($list, 'Error: ' + msg);
    };

    uploader.on( 'uploadComplete', function( file ) {
        $( '#'+file.id ).find('.progress').fadeOut();
    });

    uploader.on( 'all', function( type ) {
        if ( type === 'startUpload' ) {
            state = 'uploading';
        } else if ( type === 'stopUpload' ) {
            state = 'paused';
        } else if ( type === 'uploadFinished' ) {
            state = 'done';
        }

    });
};
