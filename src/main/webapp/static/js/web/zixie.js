/*index页menu*/
function gopage(n) 
{
  var tag = document.getElementById("menu").getElementsByTagName("li");
  var taglength = tag.length;

  for (i=1;i<=taglength;i++)
  {
    document.getElementById("m"+i).className="";
    document.getElementById("tt_c"+i).style.display='none';
  }
    document.getElementById("m"+n).className="on";
    document.getElementById("tt_c"+n).style.display='';
}
/*index页商品产品分类*/
function getNames(obj,name,tij)
{
    var p = document.getElementById(obj);
    var plist = p.getElementsByTagName(tij);
    var rlist = new Array();
    for(i=0;i<plist.length;i++)
    {
        if(plist[i].getAttribute("name") == name)
    {
     rlist[rlist.length] = plist[i];
        }
    }
    return rlist;
}
function butong_net(obj,name)
{
    var p = obj.parentNode.getElementsByTagName("td");
    var p1 = getNames(name,"f","div");
    for(i=0;i<p1.length;i++)
    {
    if(obj==p[i])
    {
    p[i].className = "s";
    p1[i].className = "dis";
    }
    else
    {
    p[i].className = "";
    p1[i].className = "undis";
    }
    }
}
/*登录页错误信息*/
function l_error () {
    document.getElementById("error").style.display="block";
}
/*判断注册协议选中*/
function asd(){
    var reg=document.getElementsByName("chk_agree");
    if(reg[0].checked){
        document.getElementById("submit").disabled=false;
        $("#submit").addClass("ttreg_tijiBtn01");
        $("#submit").removeClass("ttreg_tijiBtn02");
    }
    else {
    document.getElementById("submit").disabled=true;
        $("#submit").addClass("ttreg_tijiBtn02");
        $("#submit").removeClass("ttreg_tijiBtn01");
    }
}

/*找回密码*/
function xiyibu(){
  document.getElementById('diyi').style.display = "none";
  document.getElementById('dier').style.display = "block";
}
function xiyibu02(){
  document.getElementById('dier').style.display = "none";
  document.getElementById('disan').style.display = "block";
}
/*user update*/
function dj_update(){
    document.getElementById("name_text").style.display="none";
    document.getElementById("update_btn").style.display="none";
    document.getElementById("name_update").style.display="block";
}
function dj_update02(){
    document.getElementById("tel_text").style.display="none";
    document.getElementById("tel_btn").style.display="none";
    document.getElementById("tel_update").style.display="block";
}
function dj_update03(){
    document.getElementById("mail_text").style.display="none";
    document.getElementById("mail_btn").style.display="none";
    document.getElementById("mail_update").style.display="block";
}
function dj_update04(){
    document.getElementById("address_text").style.display="none";
    document.getElementById("address_btn").style.display="none";
    document.getElementById("address_update").style.display="block";
}
function dj_update05(){
    document.getElementById("email_text").style.display="none";
    document.getElementById("email_btn").style.display="none";
    document.getElementById("email_update").style.display="block";
}

function del(){
    if(!confirm("确认要删除商品吗？")){
        window.event.returnValue = false;
    }
}
function xiajiaWx(){
    if(!confirm("确认要下架商品吗？")){
        window.event.returnValue = false;
    }
}
function sahngjiaWx(){
    if(!confirm("确认要上架商品吗？")){
        window.event.returnValue = false;
    }
}
function delDingDan(){
    if(!confirm("确认要删除订单吗？")){
        window.event.returnValue = false;
    }
}
/*详情页：单选颜色和尺寸*/
function chgclass(o){
    var u=document.getElementById('color');
    var c= u.childNodes;
    for(var i=0; i< c.length;i++){
        if(c[i].tagName && c[i].tagName.toLowerCase() == 'span'){
            if(c[i]==o)
            {
                c[i].className="div_2";
            }else{
                c[i].className="div_1";
            }
        }
    }
}
function chgclassS(o){
    var u=document.getElementById('size');
    var c= u.childNodes;
    for(var i=0; i< c.length;i++){
        if(c[i].tagName && c[i].tagName.toLowerCase() == 'span'){
            if(c[i]==o)
            {
                c[i].className="div_2";
            }else{
                c[i].className="div_1";
            }
        }
    }
}
///*商品规格 start*/
//function chgclassGuige(o){
//    var u = document.getElementById('shangpinguige');
//    var c = u.childNodes;
//    for(var i=0;i<c.length;i++){
//        if(c[i].tagName && c[i].tagName.toLowerCase()=='a'){
//            if(c[i]==o){
//                c[i].className = 'div_color21';
//            }else{
//                c[i].className = 'div_color2';
//            }
//        }
//    }
//}
/*商品规格 end*/
function chgclassG(o){
    var u=document.getElementById('guige');
    var c= u.childNodes;
    for(var i=0; i< c.length;i++){
        if(c[i].tagName && c[i].tagName.toLowerCase() == 'span'){
            if(c[i]==o)
            {
                c[i].className="div_2";
            }else if(c[i].className == 'div_2'){
                c[i].className="div_1";
            }
        }
    }
}
/*添加适配车型 start*/
function AddNewCar()
{
    //添加一行
    var i =shipeiCarTable.rows.length;
    var nameId ="txtName"+i;
    var deleteId = "btnDelete"+i;
    var newTr = shipeiCarTable.insertRow();
    //添加列
    var nameTd = newTr.insertCell();
    var deleteTd = newTr.insertCell();
    //设置列内容和属性
    nameTd.innerHTML = '<input id="'+ nameId +'" name="color" type="text" width="190px" />';
    deleteTd.innerHTML = '<input id="'+ deleteId +'" type="button" value="删除" onclick="DelCarRow()" />';
}
function DelCarRow()
{
    document.getElementById("shipeiCarTable").deleteRow(0);
}
/*添加适配车型 end*/
/*添加颜色 start*/
function AddNewColor()
{
    //添加一行
    var newTr = categoryTable.insertRow();
    //添加列
    var nameTd = newTr.insertCell();
    var deleteTd = newTr.insertCell();
    //设置列内容和属性
    nameTd.innerHTML = '<input name="color" maxlength="10" value="" type="text" width="190px" />';

    deleteTd.innerHTML = '<input type="button" value="删除" class="del-guige" onclick="delColorRow(this);" />';
}
function delColorRow(obj)
{
    document.getElementById("categoryTable").deleteRow(obj.parentElement.parentElement.rowIndex);
    $(".div_contentlist label").trigger("change");
}
/*添加颜色 end*/
/*添加尺寸 start*/
function AddNewSize()
{
    //添加一行
    var newTr = sizeTable.insertRow();
    //添加列
    var nameTd = newTr.insertCell();
    var deleteTd = newTr.insertCell();
    //设置列内容和属性
    nameTd.innerHTML = '<input name="size" maxlength="10" value="" type="text" width="190px" />';
    deleteTd.innerHTML = '<input type="button" value="删除" class="del-guige" onclick="delSizeRow(this);" />';
}
function delSizeRow(obj)
{
    document.getElementById("sizeTable").deleteRow(obj.parentElement.parentElement.rowIndex);
    $(".div_contentlist label").trigger("change");
}
/*添加商品规格 start*/
var guiGeIndex = 1;
function AddNewGuiGe()
{
    guiGeIndex++;
    //添加一行
    var newTr = guigeTable.insertRow();
    //添加列
    var nameTd = newTr.insertCell();
    //var uploadTd = newTr.insertCell();
    var deleteTd = newTr.insertCell();
    //设置列内容和属性
    nameTd.innerHTML = '<input name="specValue" maxlength="10" value="" type="text" width="190px" />';
    //uploadTd.innerHTML = '<div class="" style="width:100px;">' +
    //    '<div id="uploader_m' + guiGeIndex + '" class="uploader_d1 wu-example" style="border:0;">' +
    //    '<div id="imglist'+guiGeIndex+'" class="uploader-list">' +
    //    '<input type="hidden" name="specImg" value="">' +
    //    '</div>' +
    //    '<div class="btns">' +
    //    '<div id="filePicker_m'+guiGeIndex+'" class="filePicker_d1 " title="上传图片">上传图片</div></div>' +
    //    '<div class="showImgUploadYs" id="showImg'+guiGeIndex+'"></div>' +
    //    '</div></div>';
    deleteTd.innerHTML = '<input type="button" value="删除" class="del-guige" onclick="delGuigeRow(this);" />';
    //uploadImage(guiGeIndex);
}
function delGuigeRow(obj)
{
    document.getElementById("guigeTable").deleteRow(obj.parentElement.parentElement.rowIndex);
    $(".div_contentlist label").trigger("change");
}
/*添加商品规格 end*/
/*收藏本站*/
function AddFavorite(sURL, sTitle) {
    sURL = encodeURI(sURL);
    try{
        window.external.addFavorite(sURL, sTitle);
    }catch(e) {
        try{
            window.sidebar.addPanel(sTitle, sURL, "");
        }catch (e) {
            alert("加入收藏失败，请使用Ctrl+D进行添加,或手动在浏览器里进行设置.");
        }
    }
}
/*加入购物车成功淡入淡出*/
$(document).ready(function(){
    $("#add_GWC").click(function(){
        $("#cg_GWC").css("display","block");
        $("#cg_GWC").fadeOut(5000);
    });
});
/*货源商城排序*/
/* 1 start */
function xiaoliang_PX(){
    var imgObj = document.getElementById("paixu_pic");
    var Flag=(imgObj.getAttribute("src",2)=="/static/images/web/zdxq_orderD.jpg")
    imgObj.src=Flag?"/static/images/web/zdxq_orderU.jpg":"/static/images/web/zdxq_orderD.jpg";
}
function shijian_PX(){
    var lichObj= document.getElementById("licheng_pic");
    var FlagL=(lichObj.getAttribute("src",2)=="/static/images/web/zdxq_orderD.jpg")
    lichObj.src=FlagL?"/static/images/web/zdxq_orderU.jpg":"/static/images/web/zdxq_orderD.jpg";
}
function jiage_PX(){
    var lichObj= document.getElementById("time_pic");
    var FlagL=(lichObj.getAttribute("src",2)=="/static/images/web/zdxq_orderD.jpg")
    lichObj.src=FlagL?"/static/images/web/zdxq_orderU.jpg":"/static/images/web/zdxq_orderD.jpg";
}
/* 1 end */
/* 2 start */
function xiaoliang_PX02(){
    var imgObj = document.getElementById("paixu_pic02");
    var Flag=(imgObj.getAttribute("src",2)=="/static/images/web/zdxq_orderD.jpg")
    imgObj.src=Flag?"/static/images/web/zdxq_orderU.jpg":"/static/images/web/zdxq_orderD.jpg";
}
function shijian_PX02(){
    var lichObj= document.getElementById("licheng_pic02");
    var FlagL=(lichObj.getAttribute("src",2)=="/static/images/web/zdxq_orderD.jpg")
    lichObj.src=FlagL?"/static/images/web/zdxq_orderU.jpg":"/static/images/web/zdxq_orderD.jpg";
}
function jiage_PX02(){
    var lichObj= document.getElementById("time_pic02");
    var FlagL=(lichObj.getAttribute("src",2)=="/static/images/web/zdxq_orderD.jpg")
    lichObj.src=FlagL?"/static/images/web/zdxq_orderU.jpg":"/static/images/web/zdxq_orderD.jpg";
}
/* 2 end */
/* 3 start */
function xiaoliang_PX03(){
    var imgObj = document.getElementById("paixu_pic03");
    var Flag=(imgObj.getAttribute("src",2)=="/static/images/web/zdxq_orderD.jpg")
    imgObj.src=Flag?"/static/images/web/zdxq_orderU.jpg":"/static/images/web/zdxq_orderD.jpg";
}
function shijian_PX03(){
    var lichObj= document.getElementById("licheng_pic03");
    var FlagL=(lichObj.getAttribute("src",2)=="/static/images/web/zdxq_orderD.jpg")
    lichObj.src=FlagL?"/static/images/web/zdxq_orderU.jpg":"/static/images/web/zdxq_orderD.jpg";
}
function jiage_PX03(){
    var lichObj= document.getElementById("time_pic03");
    var FlagL=(lichObj.getAttribute("src",2)=="/static/images/web/zdxq_orderD.jpg")
    lichObj.src=FlagL?"/static/images/web/zdxq_orderU.jpg":"/static/images/web/zdxq_orderD.jpg";
}
/* 3 end */
/* 4 start */
function xiaoliang_PX04(){
    var imgObj = document.getElementById("paixu_pic04");
    var Flag=(imgObj.getAttribute("src",2)=="/static/images/web/zdxq_orderD.jpg")
    imgObj.src=Flag?"/static/images/web/zdxq_orderU.jpg":"/static/images/web/zdxq_orderD.jpg";
}
function shijian_PX04(){
    var lichObj= document.getElementById("licheng_pic04");
    var FlagL=(lichObj.getAttribute("src",2)=="/static/images/web/zdxq_orderD.jpg")
    lichObj.src=FlagL?"/static/images/web/zdxq_orderU.jpg":"/static/images/web/zdxq_orderD.jpg";
}
function jiage_PX04(){
    var lichObj= document.getElementById("time_pic04");
    var FlagL=(lichObj.getAttribute("src",2)=="/static/images/web/zdxq_orderD.jpg")
    lichObj.src=FlagL?"/static/images/web/zdxq_orderU.jpg":"/static/images/web/zdxq_orderD.jpg";
}
/* 4 end */
/* 5 start */
function xiaoliang_PX05(){
    var imgObj = document.getElementById("paixu_pic05");
    var Flag=(imgObj.getAttribute("src",2)=="/static/images/web/zdxq_orderD.jpg")
    imgObj.src=Flag?"/static/images/web/zdxq_orderU.jpg":"/static/images/web/zdxq_orderD.jpg";
}
function shijian_PX05(){
    var lichObj= document.getElementById("licheng_pic05");
    var FlagL=(lichObj.getAttribute("src",2)=="/static/images/web/zdxq_orderD.jpg")
    lichObj.src=FlagL?"/static/images/web/zdxq_orderU.jpg":"/static/images/web/zdxq_orderD.jpg";
}
function jiage_PX05(){
    var lichObj= document.getElementById("time_pic05");
    var FlagL=(lichObj.getAttribute("src",2)=="/static/images/web/zdxq_orderD.jpg")
    lichObj.src=FlagL?"/static/images/web/zdxq_orderU.jpg":"/static/images/web/zdxq_orderD.jpg";
}
/* 5 end */
/* 6 start */
function xiaoliang_PX06(){
    var imgObj = document.getElementById("paixu_pic06");
    var Flag=(imgObj.getAttribute("src",2)=="/static/images/web/zdxq_orderD.jpg")
    imgObj.src=Flag?"/static/images/web/zdxq_orderU.jpg":"/static/images/web/zdxq_orderD.jpg";
}
function shijian_PX06(){
    var lichObj= document.getElementById("licheng_pic06");
    var FlagL=(lichObj.getAttribute("src",2)=="/static/images/web/zdxq_orderD.jpg")
    lichObj.src=FlagL?"/static/images/web/zdxq_orderU.jpg":"/static/images/web/zdxq_orderD.jpg";
}
function jiage_PX06(){
    var lichObj= document.getElementById("time_pic06");
    var FlagL=(lichObj.getAttribute("src",2)=="/static/images/web/zdxq_orderD.jpg")
    lichObj.src=FlagL?"/static/images/web/zdxq_orderU.jpg":"/static/images/web/zdxq_orderD.jpg";
}
/* 6 end */
/*$(document).ready(function(){
    $("#u01 li p").click(function(){
        $("#u01 li").children('table').eq(1).slideToggle("slow");
        changeIcon($("#u01 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u02 li p").click(function(){
        $("#u02 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u02 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u03 li p").click(function(){
        $("#u03 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u03 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u04 li p").click(function(){
        $("#u04 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u04 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u05 li p").click(function(){
        $("#u05 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u05 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u06 li p").click(function(){
        $("#u06 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u06 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u07 li p").click(function(){
        $("#u07 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u07 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u08 li p").click(function(){
        $("#u08 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u08 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u09 li p").click(function(){
        $("#u09 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u09 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u10 li p").click(function(){
        $("#u10 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u10 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u11 li p").click(function(){
        $("#u11 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u11 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u12 li p").click(function(){
        $("#u12 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u12 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u13 li p").click(function(){
        $("#u13 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u13 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u14 li p").click(function(){
        $("#u14 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u14 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u15 li p").click(function(){
        $("#u15 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u15 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u16 li p").click(function(){
        $("#u16 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u16 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u17 li p").click(function(){
        $("#u17 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u17 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u18 li p").click(function(){
        $("#u18 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u18 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u19 li p").click(function(){
        $("#u19 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u19 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u20 li p").click(function(){
        $("#u20 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u20 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u21 li p").click(function(){
        $("#u21 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u21 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u22 li p").click(function(){
        $("#u22 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u22 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u23 li p").click(function(){
        $("#u23 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u23 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u24 li p").click(function(){
        $("#u24 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u24 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u25 li p").click(function(){
        $("#u25 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u25 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u26 li p").click(function(){
        $("#u26 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u26 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u27 li p").click(function(){
        $("#u27 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u27 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u28 li p").click(function(){
        $("#u28 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u28 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u29 li p").click(function(){
        $("#u29 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u29 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u30 li p").click(function(){
        $("#u30 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u30 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u31 li p").click(function(){
        $("#u31 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u31 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u32 li p").click(function(){
        $("#u32 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u32 li p span.dian"));
    });
});
$(document).ready(function(){
    $("#u33 li p").click(function(){
        $("#u33 li").children('table').eq(1).slideToggle("500");
        changeIcon($("#u33 li p span.dian"));
    });
});
function changeIcon(mainNode) {
    if (mainNode) {
        if (mainNode.css("background-image").indexOf("spcx_jianTou01.png") >= 0) {
            mainNode.css("background-image","url('/static/images/web/spcx_jianTou02.png')");
        } else {
            mainNode.css("background-image","url('/static/images/web/spcx_jianTou01.png')");
        }
    }
}*/

/*购物车页滚动到底，浮动面板还原*/
$(window).scroll(function() {
    var x = $(document).scrollTop() + $(window).height() - $("#ncp_gwJieSuan").height();
    if (x + 361 > $(document).height()) {
        $("#ncp_gwJieSuan").addClass("tfoot_gxMian02");
        $("#ncp_gwJieSuan").removeClass("tfoot_gxMian");
    } else {
        $("#ncp_gwJieSuan").addClass("tfoot_gxMian");
        $("#ncp_gwJieSuan").removeClass("tfoot_gxMian02");
    }
});

/*品牌分类*/
$(function(){
    window.onload = function()
    {
        /*1 start*/
        var $li = $('#pinPai_flTab li');
        var $ul = $('#pinPai_con ul');

        $li.click(function(){
            var $this = $(this);
            var $t = $this.index();
            $li.removeClass();
            $this.addClass('current');
            $ul.css('display','none');
            $ul.eq($t).css('display','block');
        })
        /*1 end*/

        /*2 start*/
        var $li02 = $('#pinPai_flTab02 li');
        var $ul02 = $('#pinPai_con02 ul');

        $li02.click(function(){
            var $this = $(this);
            var $t = $this.index();
            $li02.removeClass();
            $this.addClass('current');
            $ul02.css('display','none');
            $ul02.eq($t).css('display','block');
        })
        /*2 end*/

        /*3 start*/
        var $li03 = $('#pinPai_flTab03 li');
        var $ul03 = $('#pinPai_con03 ul');

        $li03.click(function(){
            var $this = $(this);
            var $t = $this.index();
            $li03.removeClass();
            $this.addClass('current');
            $ul03.css('display','none');
            $ul03.eq($t).css('display','block');
        })
        /*3 end*/

        /*4 start*/
        var $li04 = $('#pinPai_flTab04 li');
        var $ul04 = $('#pinPai_con04 ul');

        $li04.click(function(){
            var $this = $(this);
            var $t = $this.index();
            $li04.removeClass();
            $this.addClass('current');
            $ul04.css('display','none');
            $ul04.eq($t).css('display','block');
        })
        /*4 end*/
    }
});
/*安全座椅*/
$(function(){
    /*对账单*/
    $("#dingdanmingxi tr th:first,#dingdanmingxi02 tr th:first,#dingdanmingxi03 tr th:first").css("text-align","center");
});
/*图片预览*/
function bigImg(obj){
    $('.winright img').attr("src",obj.src);
    var windowW = $(window).width();
    var windowH = $(window).height();
    var rheight=(obj.height*420)/obj.width;
    var w= (windowW - 420)/2;
    if(rheight>windowH){
        var h=10;
    }else{
        var h=(windowH-rheight)/2-30;
    }
    var myAlert = document.getElementById("sqsImgBig");
    myAlert.style.display = "block";
    myAlert.style.position = "fixed";
    myAlert.style.top = h+"px";
    myAlert.style.left = w+"px";
    var bgObj = document.getElementById("bgDiv");
    bgObj.style.display="block";
    bgObj.style.position="fixed";
    bgObj.style.top="0";
    bgObj.style.left="0";
    bgObj.style.background="#777";
    bgObj.style.filter="alpha(opacity:40)";
    bgObj.style.zoom="1";
    bgObj.style.opacity="0.6";
    bgObj.style.width="100%";
    bgObj.style.height="100%";
}

/*新需求前端--备注*/
$(document).ready(function(){
    $('#zongzi_dingdan td .add_ghsbeizhu').click(function(){
        var eNode=$(this).children("p");
        eNode.slideDown();
    });
    $('.hd_jddwei').mouseleave(function(){
        $('.header_la').hide();
    });
    $(" .add_beizhuQbtn").click(function(){
        $(this).parent().hide();
    });
});

//function changeIcon(mainNode) {
//    if (mainNode) {
//        if (mainNode.css("background-image").indexOf("/static/images/add/add_downIcon.png") >= 0) {
//            mainNode.css("background-image","url('/static/images/add/add_upIcon.png')");
//        } else {
//            mainNode.css("background-image","url('/static/images/add/add_downIcon.png')");
//        }
//    }
//}
/*关于我们 start*/
$(document).ready(function(){
    //页面中的DOM已经装载完成时，执行的代码
    $(".zx_ulmain > a").click(function(){
        var ulNode = $(this).next("ul");
        ulNode.slideToggle();
        zxchangeIcon($(this));
    });
});
function zxchangeIcon(mainNode) {
    if (mainNode) {
        if (mainNode.css("background-image").indexOf("/static/images/zuixin/zx_downS.png") >= 0) {
            mainNode.css("background-image","url('/static/images/zuixin/zx_right.png')");
        } else {
            mainNode.css("background-image","url('/static/images/zuixin/zx_downS.png')");
        }
    }
}
/*关于我们 end*/