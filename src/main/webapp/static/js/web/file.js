function checkFileSize(target){

    //检测上传文件的大小        
    var isIE = /msie/i.test(navigator.userAgent) && !window.opera;
    var fileSize = 0;
    if (isIE && !target.files){
        var filePath = target.value;
        var fileSystem = new ActiveXObject("Scripting.FileSystemObject");
        var file = fileSystem.GetFile (filePath);
        fileSize = file.Size;
    } else {
        fileSize = target.files[0].size;
    }

    if(fileSize>(1024*1024*50)){
        target.outerHTML = target.outerHTML;
        alert("文件大小不能超过50M");
    }else{
        target.previousSibling.value=getFileName(target.value);
    }
}

function getFileName(path){
    var pos=path.lastIndexOf("\\");
    return path.substring(pos+1);
}

function checkImageSize(target, type){

    //检测上传文件的大小
    var isIE = /msie/i.test(navigator.userAgent) && !window.opera;
    var fileSize = 0;
    if (isIE && !target.files){
        var filePath = target.value;
        var fileSystem = new ActiveXObject("Scripting.FileSystemObject");
        var file = fileSystem.GetFile (filePath);
        fileSize = file.Size;
    } else {
        fileSize = target.files[0].size;
    }

    if(fileSize>(1 * 1024 * 1024)){
        target.outerHTML = target.outerHTML;
        alert("文件大小不能超过1M");
    }else{
        previewImage(target, type);
    }
}