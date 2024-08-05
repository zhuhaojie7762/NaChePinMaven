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

    deleteTd.innerHTML = '<input type="button" value="删除" class="del-guige sui-btn btn-reset" onclick="delColorRow(this);" />';
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
    deleteTd.innerHTML = '<input type="button" value="删除" class="del-guige sui-btn btn-reset" onclick="delSizeRow(this);" />';
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
    var deleteTd = newTr.insertCell();
    //设置列内容和属性
    nameTd.innerHTML = '<input name="specValue" maxlength="10" value="" type="text" width="190px" />';
    deleteTd.innerHTML = '<input type="button" value="删除" class="del-guige sui-btn btn-reset" onclick="delGuigeRow(this);" />';
    //uploadImage(guiGeIndex);
}
function delGuigeRow(obj)
{
    document.getElementById("guigeTable").deleteRow(obj.parentElement.parentElement.rowIndex);
    $(".div_contentlist label").trigger("change");
}
/*添加商品规格 end*/
