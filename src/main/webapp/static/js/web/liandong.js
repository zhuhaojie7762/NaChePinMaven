/**
 * Created by Administrator on 14-12-01.
 * 模拟淘宝SKU添加组合
 * 页面注意事项：
 *      1、 .div_contentlist   这个类变化这里的js单击事件类名也要改
 *      2、 .Father_Title      这个类作用是取到所有标题的值，赋给表格，如有改变JS也应相应改动
 *      3、 .Father_Item       这个类作用是取类型组数，有多少类型就添加相应的类名：如: Father_Item1、Father_Item2、Father_Item3 ...
 */
$(function () {
    //SKU信息
    var json=new Array();   //保存库存等信息
    var jsonImg = new Array();  //保存图片
    var uploaderIndex = 0;

    $(".div_contentlist label").bind("change", function () {

        var imgIndex = 0;
        $('#process tbody tr').each(function(){
            json[$(this).index()]=new Array();
            json[$(this).index()][0]=$(this).children('td').eq(0).children('input').val();
            json[$(this).index()][1]=$(this).children('td').eq(1).children('input[name="colorPropValue"]').val();
            json[$(this).index()][2]=$(this).children('td').eq(2).children('input').val();
            json[$(this).index()][3]=$(this).children('td').eq(3).children('input[name="price"]').val();
            json[$(this).index()][4]=$(this).children('td').eq(4).children('input').val();
            json[$(this).index()][5]=$(this).children('td').eq(5).children('input').val();
            json[$(this).index()][6]=$(this).children('td').eq(6).children('input').val();
            json[$(this).index()][7]=$(this).children('td').eq(3).children('input[name="skuId"]').val();

            if($(this).children('td').eq(1).css("display") != "none"){
                jsonImg[imgIndex]=new Array();
                jsonImg[imgIndex][0]=$(this).children('td').eq(0).children('input[name="specPropValue"]').val();     //规格
                jsonImg[imgIndex][1]=$(this).children('td').eq(1).children('input[name="colorPropValue"]').val();    //颜色
                jsonImg[imgIndex][2]=$(this).children('td').eq(1).children('input[name="goodsImg"]').val();         //图片
                imgIndex++;
            }
        });
        var list = new Array();
        $('.addGuiGeTab input:text').each(function(){
            list[$(this).parent().parent().index()]=$(this).val();
        });
        for(var i=0;i<list.length-1;i++)
        {
            for(var j=i+1;j<list.length;j++)
            {
                if(list[i]==list[j]&&list[i]!="")
                {
                    alert('输入的值重复了，请重新输入')
                    $('.addGuiGeTab tr').eq(j).children('td').eq(0).children('input').eq(0).focus();
                    return;
                }
            }
        }

        list.length = 0;
        $('.addColorTab input:text').each(function(){
            list[$(this).parent().parent().index()]=$(this).val();
        });

        for(var i=0;i<list.length-1;i++)
        {
            for(var j=i+1;j<list.length;j++)
            {
                if(list[i]==list[j]&&list[i]!="")
                {
                    alert('输入的值重复了，请重新输入')
                    $('.addColorTab tr').eq(j).children('td').eq(0).children('input').eq(0).focus();
                    return;
                }
            }
        }

        list.length = 0;
        $('.addSizeTab input:text').each(function(){
            list[$(this).parent().parent().index()]=$(this).val();
        });
        for(var i=0;i<list.length-1;i++)
        {
            for(var j=i+1;j<list.length;j++)
            {
                if(list[i]==list[j]&&list[i]!="")
                {
                    alert('输入的值重复了，请重新输入')
                    $('.addSizeTab tr').eq(j).children('td').eq(0).children('input').eq(0).focus();
                    return;
                }
            }
        }

        step.Creat_Table();
    });
    var step = {
        //SKU信息组合
        Creat_Table: function () {
            step.hebingFunction();
            var SKUObj = $(".Father_Title");
            //var skuCount = SKUObj.length;//
            var arrayTile = new Array();//标题组数
            var arrayInfor = new Array();//盛放每组选中的CheckBox值的对象
            var arrayColumn = new Array();//指定列，用来合并哪些列
            var bCheck = true;//是否全选
            var columnIndex = 0;
            $.each(SKUObj, function (i, item){
                arrayColumn.push(columnIndex);
                columnIndex++;
                arrayTile.push(SKUObj.find("li").eq(i).html().replace("：", ""));
                var itemName = "Father_Item" + i;
                //选中的CHeckBox取值
                var order = new Array();
                //$("." + itemName + " input[type=checkbox]:checked").each(function (){
                //    order.push($(this).siblings("input[name='guiGe']").val());
                //});
                $("." + itemName + " input:text").each(function (){
                    order.push($(this).val());
                });
                arrayInfor.push(order);
                if (order.join() == ""){
                    bCheck = false;
                }
                //var skuValue = SKUObj.find("li").eq(index).html();
            });
            //开始创建Table表
            if (bCheck == true) {
                var RowsCount = 0;
                $("#createTable").html("");
                var table = $("<table id=\"process\" border=\"1\" cellpadding=\"1\" cellspacing=\"0\" style=\"width:100%;padding:5px;\"></table>");
                table.appendTo($("#createTable"));
                var thead = $("<thead></thead>");
                thead.appendTo(table);
                var trHead = $("<tr></tr>");
                trHead.appendTo(thead);
                //创建表头
                $.each(arrayTile, function (index, item) {
                    var td = $("<th width='36'>" + item + "</th>");
                    td.appendTo(trHead);
                });
                var itemColumHead = $("<th width='30'>批发价格(元)"+
                    "<br><div class='checkbox checkbox-success'>"+
                    "<input id='checkbox1' type='checkbox'>"+
                    "<label for='checkbox1'>"+
                    "全部相同"+
                    "</label>"+
                    "</div>"+
                    "</th><th width='30'>建议零售价(元)"+
                    "<br><div class='checkbox checkbox-success'>"+
                    "<input id='checkbox2' type='checkbox'>"+
                    "<label for='checkbox2'>"+
                    "全部相同"+
                    "</label>"+
                    "</div>"+
                    "</th><th width='30'>库存"+
                    "<br><div class='checkbox checkbox-success'>"+
                    "<input id='checkbox3' type='checkbox'>"+
                    "<label for='checkbox3'>"+
                    "全部相同"+
                    "</label>"+
                    "</div>"+
                    "</th><th width='30'>重量(千克)"+
                    "<br><div class='checkbox checkbox-success'>"+
                    "<input id='checkbox4' type='checkbox'>"+
                    "<label for='checkbox4'>"+
                    "全部相同"+
                    "</label>"+
                    "</div>"+
                    "</th>+<th width='120'>批量设置</th>");
                itemColumHead.appendTo(trHead);
                var tbody = $("<tbody></tbody>");
                tbody.appendTo(table);
                ////生成组合
                var zuheDate = step.doExchange(arrayInfor);
                if (zuheDate.length > 0) {
                    //创建行
                    $.each(zuheDate, function (index, item) {
                        var td_array = item.split(",");
                        var tr = $("<tr></tr>");
                        var d="";
                        var skuId = "";
                        var price="";
                        var retailPrice="";
                        var stock="";
                        var weight="";
                        var zhi;
                        tr.appendTo(tbody);
                        $.each(td_array, function (i, values) {
                            var itemName;
                            switch (i){
                                case 0:
                                    itemName = "specPropValue";
                                    break;
                                case 1:
                                    itemName = "colorPropValue";
                                    break;
                                case 2:
                                    itemName = "sizePropValue";
                                    break;
                            }
                            var td = $("<td>" + values + "<input name='" + itemName + "' type='hidden' value='" + values +"' /></td>");
                            td.appendTo(tr);
                            d=d+values;
                        });
                        for(var a=0;a<json.length;a++)
                        {
                            var s=json[a][0]+json[a][1]+json[a][2];
                            if(s==d)
                            {
                                zhi=a;
                                price=json[zhi][3];
                                retailPrice=json[zhi][4];
                                stock=json[zhi][5];
                                weight=json[zhi][6];
                                skuId=json[zhi][7];
                            }
                        }
                        //console.log(index+"::")
                        var td1 = $("<td ><input name=\"price\" size='1' width='30' class=\"l-text\" type=\"text\" value='"+price+"' onkeyup=\"if(isNaN(value))execCommand('undo')\" onafterpaste=\"if(isNaN(value))execCommand('undo')\" /><input type='hidden' name='skuId' value='"+skuId+"' /></td>");
                        td1.appendTo(tr);
                        var td2 = $("<td ><input name=\"retailPrice\" size='1' width='30' class=\"l-text\" type=\"text\" value='"+retailPrice+"' onkeyup=\"if(isNaN(value))execCommand('undo')\" onafterpaste=\"if(isNaN(value))execCommand('undo')\"></td>");
                        td2.appendTo(tr);
                        var td3 = $("<td ><input name=\"stock\" size='1' width='30' class=\"l-text\" type=\"text\" value='"+stock+"' onkeyup=\"this.value=this.value.replace(/\\D/g,'')\" onafterpaste=\"this.value=this.value.replace(/\\D/g,'')\"></td>");
                        td3.appendTo(tr);
                        var td4 = $("<td ><input name=\"weight\" size='1' width='30' class=\"l-text\" type=\"text\" value='"+weight+"' onkeyup=\"if(isNaN(value))execCommand('undo')\" onafterpaste=\"if(isNaN(value))execCommand('undo')\"></td>");
                        td4.appendTo(tr);
                        var td5 = $("<td><div class='tdss'><a href='javascript:void(0);' class='shezhis'>设置</a><div class='box'>"+
                            "<div class='box_'>批量设置<a href='javascript:void(0);' class='guans'></a></div>"+
                            "<ul>"+
                            "<li style='height: 40px;'><i>批发价格：</i><div class='checkbox checkbox-success'><input id='checkbox"+index+"11' type='checkbox'><label for='checkbox"+index+"11'>同规格批发价格相同</label></div></li>"+
                            "<li><div class='checkbox checkbox-success'><input id='checkbox"+index+"21' type='checkbox'><label for='checkbox"+index+"21'>同颜色批发价格相同</label></div></li>"+
                            "<li><div class='checkbox checkbox-success'><input id='checkbox"+index+"31' type='checkbox'><label for='checkbox"+index+"31'>同尺寸批发价格相同</label></div></li>"+
                            "<li style=' height: 40px; border-top:1px dotted #f0f0f0;'><i>建议零售价：</i><div class='checkbox checkbox-success'><input id='checkbox"+index+"12' type='checkbox'><label for='checkbox"+index+"12'>同规格建议零售价相同</label></div></li>"+
                            "<li><div class='checkbox checkbox-success'><input id='checkbox"+index+"22' type='checkbox'><label for='checkbox"+index+"22'>同颜色建议零售价相同</label></div></li>"+
                            "<li><div class='checkbox checkbox-success'><input id='checkbox"+index+"32' type='checkbox'><label for='checkbox"+index+"32'>同尺寸建议零售价相同</label></div></li>"+
                            "<li style='height: 40px; border-top:1px dotted #f0f0f0;'><i>库存：</i><div class='checkbox checkbox-success'><input id='checkbox"+index+"13' type='checkbox'><label for='checkbox"+index+"13'>同规格库存相同</label></div></li>"+
                            "<li><div class='checkbox checkbox-success'><input id='checkbox"+index+"23' type='checkbox'><label for='checkbox"+index+"23'>同颜色库存相同</label></div></li>"+
                            "<li><div class='checkbox checkbox-success'><input id='checkbox"+index+"33' type='checkbox'><label for='checkbox"+index+"33'>同尺寸库存相同</label></div></li>"+
                            "<li style='height: 40px; border-top:1px dotted #f0f0f0;'><i>重量：</i><div class='checkbox checkbox-success'><input id='checkbox"+index+"14' type='checkbox'><label for='checkbox"+index+"14'>同规格重量相同</label></div></li>"+
                            "<li><div class='checkbox checkbox-success'><input id='checkbox"+index+"24' type='checkbox'><label for='checkbox"+index+"24'>同颜色重量相同</label></div></li>"+
                            "<li><div class='checkbox checkbox-success'><input id='checkbox"+index+"34' type='checkbox'><label for='checkbox"+index+"34'>同尺寸重量相同</label></div></li>"+
                            "</ul>"+
                            "</div></div></td>");
                        td5.appendTo(tr);
                    });
                    function checkboxc(j){
                        $("#checkbox"+j).change(function(){
                            var ss1=$('#process tbody tr').eq(0).children('td').eq(j+2).children('input').val();
                            console.log(1111);
                            //console.log(($('#process tbody tr').length)-1);
                            if($(this).is(':checked')==true)
                            {
                                for(var i=1;i<$('#process tbody tr').length;i++)
                                {
                                    $('#process tbody tr').eq(i).children('td').eq(j+2).children('input').attr('readonly','readonly');
                                    $('#process tbody tr').eq(i).children('td').eq(j+2).children('input').val(ss1);
                                }
                            }
                            else{
                                for(var i=1;i<$('#process tbody tr').length;i++)
                                {
                                    $('#process tbody tr').eq(i).children('td').eq(j+2).children('input').removeAttr('readonly');
                                }
                            }
                        });
                        $('#process tbody tr').eq(0).children('td').eq(j+2).children('input').keyup(function(){
                            if($("#checkbox"+j).is(':checked')==true)
                            {
                                var ss1=$('#process tbody tr').eq(0).children('td').eq(j+2).children('input').val();
                                for(var i=1;i<$('#process tbody tr').length;i++)
                                {
                                    $('#process tbody tr').eq(i).children('td').eq(j+2).children('input').val(ss1);
                                }
                            }
                        });
                    }
                    checkboxc(1);
                    checkboxc(2);
                    checkboxc(3);
                    checkboxc(4);

                    $('.guans').click(function(){
                        $(this).parent().parent().hide();
                    });

                    $('.shezhis').click(function(){
                        if($(this).next().is(":hidden")){
                            $('.box').hide();
                            $(this).next().show();
                        }
                        else{
                            $('.box').hide();
                        }
                    });
                    function checkbox(i,a,b){
                        //console.log(i+""+a+""+b);
                        $('#checkbox'+i+""+a+""+b).on('change',function(){
                            if($(this).is(':checked')==true)
                            {
                                var vals1=$('#process tbody tr').eq(i).children('td').eq(a-1).children('input').eq(0).val();
                                //console.log(vals1);
                                var vals2=$('#process tbody tr').eq(i).children('td').eq(b+2).children('input').eq(0).val();
                                //console.log(vals2);
                                for(var j=0;j<$('#process tbody tr').length;j++)
                                {
                                    if($('#process tbody tr').eq(j).children('td').eq(a-1).children('input').eq(0).val()==vals1)
                                    {
                                        $('#process tbody tr').eq(j).children('td').eq(b+2).children('input').eq(0).val(vals2);
                                    }

                                }
                            }
                        });
                    }
                    for(var i=0;i<$('#process tbody tr').length;i++)
                    {
                        for(var a=1;a<4;a++)
                        {
                            for(var b=1;b<5;b++)
                            {
                                //console.log(i+""+a+""+b)
                                checkbox(i,a,b);
                            }
                        }
                    }
                    $('#process tbody tr td input').keyup(function(){
                        var indexs=$(this).parent().index();
                        var indexs1=$(this).parent().parent().index();
                        //console.log(indexs);
                        var thiss=$(this).parent().parent().children("td:last-child").children('.tdss').children('.box').children('ul').children('li');
                        for(var i=0;i<thiss.length;i++)
                        {


                            if(thiss.eq(i).children('div').children('input').is(':checked')==true)
                            {
                                var newold1=i%3;
                                var newold2=Math.floor(i/3)+3;

                                for(var j=0;j<$('#process tbody tr').length;j++)
                                {
                                    if($('#process tbody tr').eq(j).children('td').eq(newold1).children('input').eq(0).val()==$('#process tbody tr').eq(indexs1).children('td').eq(newold1).children('input').eq(0).val())
                                    {
                                        $('#process tbody tr').eq(j).children('td').eq(newold2).children('input').eq(0).val($('#process tbody tr').eq(indexs1).children('td').eq(newold2).children('input').eq(0).val());
                                    }

                                }
                            }
                        }
                    });
                }
                //结束创建Table表
                arrayColumn.pop();//删除数组中最后一项
                //合并单元格
                $(table).mergeCell({
                    // 目前只有cols这么一个配置项, 用数组表示列的索引,从0开始
                    cols: arrayColumn
                });
                step.createUpload($(table), 1);
            } else{
                //未全选中,清除表格
                document.getElementById('createTable').innerHTML="";
            }
        },//合并行
        hebingFunction: function () {
            $.fn.mergeCell = function (options) {
                return this.each(function () {
                    var cols = options.cols;
                    for (var i = cols.length - 1; cols[i] != undefined; i--) {
                        // fixbug console调试
                        // console.debug(cols[i]);
                        mergeCell($(this), cols[i]);
                    }
                    dispose($(this));
                });
            };
            function mergeCell($table, colIndex) {
                $table.data('col-content', ''); // 存放单元格内容
                $table.data('col-before_content', ''); // 存放前几列单元格内容
                $table.data('col-rowspan', 1); // 存放计算的rowspan值 默认为1
                $table.data('col-td', $()); // 存放发现的第一个与前一行比较结果不同td(jQuery封装过的), 默认一个"空"的jquery对象
                $table.data('trNum', $('tbody tr', $table).length); // 要处理表格的总行数, 用于最后一行做特殊处理时进行判断之用
                // 进行"扫面"处理 关键是定位col-td, 和其对应的rowspan
                $('tbody tr', $table).each(function (index) {
                    // td:eq中的colIndex即列索引
                    var $td = $('td:eq(' + colIndex + ')', this);
                    // 取出单元格的当前内容
                    var currentContent = $td.html();
                    var beforeContent = "";
                    if(colIndex > 0){
                        for(var col=0;col < colIndex; col++){
                            var $td2 = $('td:eq(' + col + ')', this);
                            beforeContent += $td2.html();
                        }
                    }
                    // 第一次时走此分支
                    if ($table.data('col-content') == '') {
                        $table.data('col-content', currentContent);
                        $table.data('col-before_content', beforeContent);
                        $table.data('col-td', $td);
                    } else {
                        // 上一行与当前行内容相同
                        if ($table.data('col-content') == currentContent && $table.data('col-before_content') == beforeContent) {
                            // 上一行与当前行内容相同则col-rowspan累加, 保存新值
                            var rowspan = $table.data('col-rowspan') + 1;
                            $table.data('col-rowspan', rowspan);
                            // 值得注意的是 如果用了$td.remove()就会对其他列的处理造成影响
                            $td.hide();
                            // 最后一行的情况比较特殊一点
                            // 比如最后2行 td中的内容是一样的, 那么到最后一行就应该把此时的col-td里保存的td设置rowspan
                            if (++index == $table.data('trNum'))
                                $table.data('col-td').attr('rowspan', $table.data('col-rowspan'));
                        } else { // 上一行与当前行内容不同
                            // col-rowspan默认为1, 如果统计出的col-rowspan没有变化, 不处理
                            if ($table.data('col-rowspan') != 1) {
                                $table.data('col-td').attr('rowspan', $table.data('col-rowspan'));
                            }
                            // 保存第一次出现不同内容的td, 和其内容, 重置col-rowspan
                            $table.data('col-td', $td);
                            $table.data('col-content', $td.html());
                            $table.data('col-before_content', beforeContent);
                            $table.data('col-rowspan', 1);
                        }
                    }
                });
            }
            // 同样是个private函数 清理内存之用
            function dispose($table) {
                $table.removeData();
            }
        },
        //组合数组
        doExchange: function (doubleArrays) {
            var len = doubleArrays.length;
            if (len >= 2) {
                var arr1 = doubleArrays[0];
                var arr2 = doubleArrays[1];
                var len1 = doubleArrays[0].length;
                var len2 = doubleArrays[1].length;
                var newlen = len1 * len2;
                var temp = new Array(newlen);
                var index = 0;
                for (var i = 0; i < len1; i++) {
                    for (var j = 0; j < len2; j++) {
                        temp[index] = arr1[i] + "," + arr2[j];
                        index++;
                    }
                }
                var newArray = new Array(len - 1);
                newArray[0] = temp;
                if (len > 2) {
                    var _count = 1;
                    for (var i = 2; i < len; i++) {
                        newArray[_count] = doubleArrays[i];
                        _count++;
                    }
                }
                //console.log(newArray);
                return step.doExchange(newArray);
            }
            else {
                return doubleArrays[0];
            }
        },
        createUpload: function($table, colIndex){
            var imgUrl = "";
            $('tbody tr', $table).each(function (index) {
                var $td = $('td:eq(' + colIndex + ')', this);

                if($td.css("display") != "none"){

                    uploaderIndex++;
                    var spec = $(this).children('td').eq(0).children('input[name="specPropValue"]').val();     //规格
                    var color = $(this).children('td').eq(1).children('input[name="colorPropValue"]').val();
                    imgUrl = "";
                    for(var a=0;a<jsonImg.length;a++) {
                        if(spec==jsonImg[a][0] && color == jsonImg[a][1]) {
                            imgUrl = jsonImg[a][2];
                            break;
                        }
                    }
                    var objUpload = '<div class="" style="width:100px;">' +
                        '<div id="uploader_m' + uploaderIndex + '" class="uploader_d1 wu-example" style="border:0;">' +
                        '<div id="imglist'+uploaderIndex+'" class="uploader-list"></div>' +
                        '<div class="btns">' +
                        '<div id="filePicker_m'+uploaderIndex+'" class="filePicker_d1 " title="上传图片">上传图片</div></div>' +
                        '<div class="showImgUploadYs" id="showImg'+uploaderIndex+'">';
                    if(imgUrl != "" && imgUrl != undefined){
                        objUpload += '<img src="' + getS(imgUrl) + '">';
                    }
                    objUpload += '</div></div></div>';
                    $td.append(objUpload);
                    uploadImage(uploaderIndex);
                }
                var objUpload = '<input type="hidden" data-type="goodsImg'+uploaderIndex+'" name="goodsImg" value="'+imgUrl+'">';
                $td.append(objUpload);
            });
        }
    }
    return step;

    function getS(imgUrl){
        if(imgUrl == "" || imgUrl == undefined){
            return "";
        }
        var imgPath = "http://storage.nachepin.com";
        return imgPath + imgUrl + "!198";
    }
});
