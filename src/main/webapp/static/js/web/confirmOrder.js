/* 下单页面用 */
$(function () {
    //解析1688地址
    $("#parse1688").click(function () {
        var parseAddr = $("#ziDongFenXi").val();
        if (parseAddr.length == 0) {
            Msg.warning("亲，您要分析的地址没有填写！");
            return false;
        }
        Common.post2({
            url: "/trade/parse1688",
            param: { parseAddr: parseAddr },
            success: function (json) {
                autoAddress(json);
                calcPostFee();
                endInfo();
            }
        });
    });
    //解析淘宝地址
    $("#parseTaoBao").click(function () {
        var parseAddr = $("#ziDongFenXi").val();
        if (parseAddr.length == 0) {
            Msg.warning("亲，您要分析的地址没有填写！");
            return false;
        }
        Common.post2({
            url: "/trade/parseTaoBao",
            param: { parseAddr: parseAddr },
            success: function (json) {
                autoAddress(json);
                calcPostFee();
                endInfo();
            }
        });
    });
    //解析淘宝地址
    $("#parseTaoBao2").click(function () {
        var parseAddr = $("#ziDongFenXi").val();
        if (parseAddr.length == 0) {
            Msg.warning("亲，您要分析的地址没有填写！");
            return false;
        }
        Common.post2({
            url: "/trade/parseTaoBao2",
            param: { parseAddr: parseAddr },
            success: function (json) {
                autoAddress(json);
                calcPostFee();
                endInfo();
            }
        });
    });
    //地址分析后赋值
    var autoAddress = function(json) {
        var info = json.data;
        $("#provinceId").val(info.provinceId);
        $("input[name=province]").trigger("change");
        $("#cityId").val(info.cityId);
        $("input[name=city]").trigger("change");
        $("#regionId").val(info.regionId);
        for (p in info) {
            if (p == "provinceId")
                continue;
            if (p == "cityId")
                continue;
            if (p == "regionId")
                continue;
            if(p == "province") {
                $("#" + p).text(info[p]);
            } else if(p == "city"){
                $("#" + p).text(info[p]);
            } else if (p == "region") {
                $("#" + p).text(info[p]);
            } else {
                $("#" + p).val(info[p]);
            }
        }
    }

    //省改变时，运费改变
    $("input[name=province]").change(function () {
        calcPostFee();
        var id = $(this).val();
        Common.post2({
            url: '/cities',
            param: {id: id, display: true},
            success: function(json){
                $(".cities").html(json.data);
                endInfo();
            }
        });
        $("input[name=city]").val("");
        $("input[name=city]").siblings("span").text("请选择市");
        $("input[name=county]").val("");
        $("input[name=county]").siblings("span").text("请选择区");
        $(".counties").html("");
    });
    //市改变时，运费改变
    $("input[name=city]").change(function(){
        var id = $(this).val();
        Common.post2({
            url: '/counties',
            param: {id: id, display: true},
            success: function(json){
                $(".counties").html(json.data);
                endInfo();
            }
        });
        $("input[name=county]").val("");
        $("input[name=county]").siblings("span").text("请选择区");
        calcPostFee();
    });

    //区改变时，最下方买家信息改变
    $("#autoRegion").on("click", "a", function () {
        var regionInfo = $(this)[0].firstChild.data;
        $("#region").text(regionInfo);
        endInfo();
    });

    //地址改变时，最下方买家信息改变
    $("#address").change(function() {
        endInfo();
    });

    //姓名改变时，最下方买家信息改变
    $("#name").change(function() {
        endInfo();
    });

    //电话号码改变时，最下方买家信息改变
    $("#mobileNumber").change(function() {
        endInfo();
    });

    //使用优惠券选中状态改变时，金额改变
    $("input[name=couponFlag]").click(function(){
        calcTotalFee();
    });

    //优惠券改变时，金额改变
    $(".btn-coupon").click(function() {
        var money = $(this).attr("data-money");
        var type = $(this).attr("data-type");
        var condition = $(this).attr("data-condition");
        var id = $(this).attr("data-id");
        if (money > 0) {
            $("#lab-condition").text(condition);
            if(type == 'PAYMENT'){
                $("#lab-type").text("抵货款");
            }else{
                $("#lab-type").text("抵运费");
            }
            $("#lab-money").text(money);
            $("#lab-coupon").show();
        }else{
            $("#lab-coupon").hide();
        }
        $("input[name=couponMoney]").val(money);
        $("input[name=couponMoney]").attr("data-type", type);
        $("input[name=couponId]").val(id);
        calcTotalFee();
    });

    //快递公司修改时，运费改变
    $(".peiSongTanChuang").on("click", ".express-company", function () {
        var id = $(this).attr("data-id");
        var name = $(this).attr("data-name");
        var fee = $(this).attr("data-fee");
        var cutMoney = $(this).attr("data-cut");
        var discount = $(this).attr("data-discount");

        $(".default-name").text(name);
        $("input[name=defaultExpress]").val(id);
        $(".default-post-fee").text(fee);
        $("input[name=postFee]").val(fee);
        $("input[name=cutMoney]").val(cutMoney);
        $("input[name=discount]").val(discount);
        $("input[name=errMessage]").val("");
        calcTotalFee();
        Common.post2({
            url: '/trade/changeDefaultExpress',
            param: {expressCompanyId: id},
            success: function (json) {
            }
        });
    });

    //运费计算
    function calcPostFee(){
        var provinceId = $("input[name=province]").val();
        var areaId = $("input[name=city]").val();
        var weight = $("input[name=province]").attr("data-weight");
        var defaultExpress = $("input[name=defaultExpress]").val();
        $("input[name=errMessage]").val("");
        Common.post2({
            url: "/trade/calcPostFee",
            param: {
                provinceId: provinceId,
                areaId: areaId,
                weight: weight,
                expressCompanyId: defaultExpress
            },
            success: function (json) {
                if(json.data == -1) {
                    $("input[name=postFee]").val(0);
                    $("input[name=errMessage]").val("此地区该快递不送达，请选用其他快递!");
                }else if(json.data == -2){
                    $("input[name=postFee]").val(0);
                    $("input[name=errMessage]").val("货物过重，此快递公司不接收，请选用其他快递!");
                }else{
                    $("input[name=postFee]").val(json.data);
                }
                calcTotalFee();
                if(json.data == -1){
                    Msg.warning("此地区该快递不送达，请选用其他快递!");
                }else if(json.data == -2){
                    Msg.warning("货物过重，此快递公司不接收，请选用其他快递!");
                }
            }
        });
        Common.post2({
            url: "/trade/allPostFee",
            param: {
                provinceId: provinceId,
                areaId: areaId,
                weight: weight
            },
            success: function (json) {
                $("#peiSongTc").html("");
                $("#peiSongTc").html(json.data);
            }
        });
    };

    //显示费用计算
    function calcTotalFee() {
        var couponMoney = parseFloat($("input[name=couponMoney]").val());
        var type = $("input[name=couponMoney]").attr("data-type");
        var oldPostFee = parseFloat($("input[name=postFee]").val());
        var totalPayment = parseFloat($("input[name=totalPayment]").val());
        var cutMoney = $("input[name=cutMoney]").val();
        var discount = $("input[name=discount]").val();
        $(".post-fee2").text(oldPostFee.toFixed(2));
        var newPostFee = 0;

        if (discount > 0) {
            newPostFee = oldPostFee * discount / 100;
        } else {
            newPostFee = oldPostFee - (cutMoney / 100);
            if (newPostFee < 0) {
                newPostFee = 0;
            }
        }
        var platformDiscount = oldPostFee - newPostFee;
        $(".default-post-fee").text(newPostFee.toFixed(2));

        if ($("input[name=couponFlag]").is(":checked") && couponMoney > 0) {
            if (type == "POST_FEE") {
                if ( newPostFee < couponMoney) {
                    couponMoney = newPostFee;
                    newPostFee = 0;
                }else{
                    newPostFee = newPostFee - couponMoney;
                }

                $("#postFee_discount").text((couponMoney + platformDiscount).toFixed(2));
                $("#good_discount").text("0.00");
            } else {
                if ( totalPayment < couponMoney ) {
                    totalPayment = 0;
                    couponMoney = totalPayment;
                }else{
                    totalPayment = totalPayment - couponMoney;
                }
                $("#postFee_discount").text(platformDiscount.toFixed(2));
                $("#good_discount").text(couponMoney.toFixed(2));
            }

        }else{
            $("#good_discount").text("0.00");
            $("#postFee_discount").text(platformDiscount.toFixed(2));
            //$("#total_discount").text((platformDiscount).toFixed(2));
            couponMoney = 0;
        }
        var packFeeDiscount = parseFloat($("#packFee_discount").text());
        $("#total_discount").text((platformDiscount + couponMoney + packFeeDiscount).toFixed(2));

        var elem = totalPayment + newPostFee;

        $(".goodsMoney").text(totalPayment.toFixed(2));
        $(".real-post-fee").text(newPostFee.toFixed(2));
        $(".totalFee").text(elem.toFixed(2));
    };

    //显示客户信息
    function endInfo() {
        $("#orderMsg").text("寄送至：" + $("#province")[0].firstChild.data + " " + $("#city")[0].firstChild.data + " " + $("#region")[0].firstChild.data + " " + $("#address").val() + " " + $("#name").val() + " " + $("#mobileNumber").val())
    }

    //提交表单
    $("#confirmOrderForm").submit(function(){
        var btn = $(".tiJiao_BtnYs");
        if($("textarea[name=buyerMessage]").val().length > 200) {
            MsgBox.warning(btn, "留言请小于200字符");
            return false;
        }
        if(!$("input[name=receiveName]").val()) {
            MsgBox.warning(btn, "请填写收货人姓名");
            return false;
        }
        var phone = $("input[name=receivePhone]").val();
        if(!phone || !Common.Rules.mobile.test(phone)) {
            MsgBox.warning(btn, "请填写正确收货人电话");
            return false;
        }
        var provinceId = $("input[name=province]").val();
        var cityId = $("input[name=city]").val();
        var countyId = $("input[name=county]").val();
        if(!provinceId || provinceId == "0" || !cityId || cityId == "0" || !countyId || countyId == "0") {
            MsgBox.warning(btn, "请选择省市区");
            return false;
        }
        if(!$("textarea[name=receiveAddress]").val()) {
            MsgBox.warning(btn, "请填明细地址");
            return false;
        }
        var postCode = $("input[name=postCode]").val();
        if(postCode.length != 6) {
            MsgBox.warning(btn, "请填正确的邮编");
            return false;
        }
        if($(".express-company:checked").length == 0){
            MsgBox.warning(btn, "请选择快递公司");
            return false;
        }
        var errMessage = $("input[name=errMessage]").val();
        if(errMessage.length > 0){
            MsgBox.warning(btn, errMessage);
            return false;
        }
        return true;
    });

    //优惠券隐藏/显示
    $("#youhuiceng").click(function(){
        var ulNode=$(this).next("div.addTabQie");
        ulNode.slideToggle();
        toggleIcon($(this));
    });

    function toggleIcon(mainNode) {
        if (mainNode) {
            if (mainNode.css("background-image").indexOf("/static/images/add/add_downIcon.png") >= 0) {
                mainNode.css("background-image","url('/static/images/add/add_upIcon.png')");
            } else {
                mainNode.css("background-image","url('/static/images/add/add_downIcon.png')");
            }
        }
    }

    /*自动解析示例*/
    $("#fenxi01").click(function(){
        $("#ziDongFenXi").attr("placeholder","姓名，电话，天台县坦头镇天台金恒德中国汽车用品城C1幢501室，310017");
    });
    $("#fenxi02").click(function(){
        $("#ziDongFenXi").attr("placeholder","姓名，电话，天台县坦头镇天台金恒德中国汽车用品城C1幢501室 (310017)");
    });
    $("#fenxi03").click(function(){
        $("#ziDongFenXi").attr("placeholder","浙江省天台县坦头镇天台金恒德中国汽车用品城C1幢501室，邮编，姓名，手机号");
    });
});