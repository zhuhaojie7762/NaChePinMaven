$(document).ready(function(){
    /*属性管理 start*/
    $("#add_shuxing").click(function(){
        var fenlei=$(this).next('div');
        fenlei.slideToggle();
    });
    $("#add_quxiao,#add_quxiao02").click(function(){
        $(this).parent().parent().parent().hide();
    });
    $("#add_fenleitab td span.upbtn").click(function(){
        var updiv=$(this).next('div');
        updiv.slideToggle();
    });
    $("#add_fenleitab td .tdquxiao").click(function(){
        $(this).parent().parent().hide();
    });
    $("#add_fenleitab td span.chakan").click(function(){
        var ckdiv=$(this).next('div');
        ckdiv.slideToggle();
    });
    $("#add_fenleitab td .add_clbtn").click(function(){
        $(this).parent().parent().hide();
    });
    //新增、修改
    $('.add_moreshan').unbind("click").bind("click", function () {
        var lengt=$(this).parent().parent().parent().next().children('thead').children('tr').length;
        var boxs=$(this).parent().parent().parent().next().children('thead');
        var c=0;
        for(var i=lengt-1;i>0;i--)
        {
            if(boxs.children('tr').eq(i).children('td').eq(0).children('input').is(':checked')==true)
            {
                c=1;
                boxs.children('tr').eq(i).remove();
            }
        }
        if(c==0)
        {
            alert('请选择删除项!');
        }

    });
    $('.Adds').unbind("click").bind("click", function () {
        $(this).parent().parent().parent().next().children('thead').append("<tr align='center'><td><input type='checkbox' name='ckb' /></td><td><input type='text' /></td></tr>");
    });

    $('.Addss').unbind("click").bind("click", function () {
        for(var i=0;i<5;i++)
        {
            $(this).parent().parent().parent().next().children('thead').append("<tr align='center'><td><input type='checkbox' name='ckb' /></td><td><input type='text' /></td></tr>");
        }
    });
    $('.allCkb').unbind("change").bind("change", function () {
        if($(this).is(':checked')==true)
        {
            var lengt=$(this).parent().parent().parent().parent('thead').children('tr').length;
            for(var i=1;i<lengt;i++)
            {
                $(this).parent().parent().parent().parent('thead').children('tr').eq(i).children('td').eq(0).children('input').prop("checked",true);
            }
        }else
        {
            var lengt=$(this).parent().parent().parent().parent('thead').children('tr').length;

            for(var i=1;i<lengt;i++)
            {
                $(this).parent().parent().parent().parent('thead').children('tr').eq(i).children('td').eq(0).children('input').prop("checked",false);
            }
        }
    });
    /*属性管理 end*/
});