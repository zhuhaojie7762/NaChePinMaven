$(function(){
    //全选/全不选
    $("#CheckedAll").click(function(){
        $('[name=items]:checkbox').attr("checked", this.checked );
    });
});
$(document).ready(function(){
    $("#weiwancheng_Tab tr td div span.tianxiedhsj").click(function(){
        $(this).next(".hd_qddwei").show(300);
    });
    $(".xiala_close").click(function(){
        $(this).parent().parent().hide(180);
       /* $(this).parent().hide(180);*/
    });

});

$(document).ready(function(){
    $('#weiwancheng_Tab tr td .beiZhu_wai,#zongzi_dingdan tr td .beiZhu_wai').click(function(){
        var eNode=$(this).children("p");
        eNode.slideDown();
    });
    $('.hd_jddwei').mouseleave(function(){
        $('.header_la').hide();
    });
    $(".dingbot").click(function(){
        $('.header_la').hide();
    });
    $("#expressesForm").on("click", "#pis_xlclose", function () {
        $(this).parent().parent().hide();
    });

});
/*待拣货订单tab*/
window.onload = function() {
    var oDiv = document.getElementById("daijianhuo_ddTab");
    var oLi = oDiv.getElementsByTagName("div")[0].getElementsByTagName("li");
    var aCon = oDiv.getElementsByTagName("div")[1].getElementsByTagName("div");
    var timer = null;
    for (var i = 0; i < oLi.length; i++) {
        oLi[i].index = i;
        oLi[i].onclick = function() {
            show(this.index);
        }
    }
    function show(a) {
        index = a;
        var alpha = 0;
        for (var j = 0; j < oLi.length; j++) {
            oLi[j].className = "";
            aCon[j].className = "";
            aCon[j].style.opacity = 0;
            aCon[j].style.filter = "alpha(opacity=0)";
        }
        oLi[index].className = "cur";
        clearInterval(timer);
        timer = setInterval(function() {
                alpha += 2;
                alpha > 100 && (alpha = 100);
                aCon[index].style.opacity = alpha / 100;
                aCon[index].style.filter = "alpha(opacity=" + alpha + ")";
                alpha == 100 && clearInterval(timer);
            },
            5)
    }
}
/*实际重量*/
$(function(){
    $(".btn-realWeight").click(function(){
        $(this).next(".shijizlCon").show(500);
    });
    $(".btn-realWeight-cancel").click(function(){
      $(this).parent().hide(300);
    });
});
$(function(){
    $(".tuihuism").click(function(){
       $(".tuihuishouming").show(500);
    });
    $(".quxiaoBtn").click(function(){
        $(".tuihuishouming").hide(200);
    });
    /*供货商已结算账单*/
    $(".tabTwo ul li:first").click(function(){
        $(this).removeClass("tabTwoBg").addClass("tabTwoNoBg");
        $(".tabTwo ul li:nth-child(2)").removeClass("tabTwoNoBg").addClass("tabTwoBg");
        $("#twoTabCon").show();
        $("#duizjilu").hide();
    });
    $(".tabTwo ul li:nth-child(2)").click(function(){
        $(this).removeClass("tabTwoBg").addClass("tabTwoNoBg");
        $(".tabTwo ul li:first").removeClass("tabTwoNoBg").addClass("tabTwoBg");
        $("#duizjilu").show();
        $("#twoTabCon").hide();
    });
});
//图片预览
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