$(document).ready(function(e) {
	//-----------------初始化--------------------
	var jsonData = [];
	
	Common.ajax("/admin/goods/classes/all", null, function(json) {
		if (json.code == Common.SUCC) {
			jsonData = json.data;
		} else {
			Msg.danger("服务异常");
		}
	});
	
	var treeid = $("#TreeList");
	
	var jsonDataTree = transData(jsonData, 'id', 'pid', 'children');   
	//console.log(jsonDataTree); 
	
	var treeNodeStr = initTreeNode(jsonDataTree, 'id', 'pid', 'children', true)
	initTree(treeid, treeNodeStr);
	//-------------------------------------
	
	
    var TreeName = 'TreeList';//树状ID
    var PrentNodeClass = 'ParentNode';//父节点的标识
    var ChildNodeClass = 'ChildNode';//没有下级子节点的标识
    var ChildrenListClass = 'Row';//子节点被包着的外层样式
    var NewNodeName = '新建';//默认新建节点的名称
    var Orititle = 'Temptitle';//保存原来名称的属性名称

    var TModuleNode,TChildNode,TModuleNodeName;
    TModuleNode = $('#TreeList .'+PrentNodeClass);//顶层节点
    TChildNode = $('.'+ChildNodeClass);
    TModuleNodeName = $('#TreeList .' + PrentNodeClass + ' .title');//顶层节点名称
    TModuleNode.removeClass('show').addClass('hidden');
    if(TModuleNode.next().hasClass(ChildrenListClass)){
        TModuleNode.next().css('display','none');//紧跟的下一个是子节点
    }

    //========================编辑区域的HTML源码==============================
    function EditHTML(NewName){
        var str = '<div class="title">' + NewName + '</div>';
        str	+= '<div class="editBT"></div>';
        str	+= '<div class="editArea"><span>[编辑]</span><span>[添加同级]</span><span>[添加下级]</span><span>[删除]</span><span class="add_treetime">创建时间：2016-12-08</span></div>';
        return str;
    }
    
  //========================编辑区域的HTML源码==============================
    function EditHTMLWithOutTianJiaXiaJi(NewName){
        var str = '<div class="title">' + NewName + '</div>';
        str	+= '<div class="editBT"></div>';
        str	+= '<div class="editArea"><span>[编辑]</span><span>[添加同级]</span><span></span><span>[删除]</span><span class="add_treetime">创建时间：2016-12-08</span></div>';
        return str;
    }

    //==========================树状展开收缩的效果============================
    TModuleNodeName.click(function(){
        TModuleNodeName_Click($(this));
    });

    //-------------------------------(定义)----------------------------------
    function TModuleNodeName_Click(Obj){
        if(Obj.has('input').length==0){//非编辑模式下进行
            var tempNode = Obj.parent();
            if(tempNode.hasClass('hidden')){
                tempNode.removeClass('hidden').addClass('show');
                if(tempNode.next().hasClass(ChildrenListClass)){
                    tempNode.next().css('display','');
                }
            }
            else{
                tempNode.removeClass('show').addClass('hidden');
                if(tempNode.next().hasClass(ChildrenListClass)){
                    tempNode.next().css('display','none');
                }
            }
        }
    }
    //==========================鼠标经过、离开节点的效果============================
    with(TModuleNode){
        mouseover(function(){
            TNode_MouseOver($(this));
        });

        mouseout(function(){
            TNode_MouseOut($(this));
        });
    }

    with(TChildNode){
        mouseover(function(){
            TNode_MouseOver($(this));
        });

        mouseout(function(){
            TNode_MouseOut($(this));
        });
    }

    //-------------------------------(定义)----------------------------------
    function TNode_MouseOver(Obj){
        if(!(Obj.hasClass('show'))){
            Obj.addClass('mouseOver');
        }
    }

    function TNode_MouseOut(Obj){
        Obj.removeClass('mouseOver');
    }

    //==========================编辑区操作============================
    $('.editArea').each(function(){
        EditArea_Event($(this));
    });
    //-------------------------------(定义)----------------------------------
    function EditArea_Event(Obj){
        var objParent = Obj.parent();
        var objTitle = objParent.find('.title');//节点名称
        //-----------------编辑区的鼠标效果------------------
        Obj.children().each(function(){
            with($(this)){
                mouseover(function(){
                    $(this).addClass('mouseOver');
                });
                mouseout(function(){
                    $(this).removeClass('mouseOver');
                });
            }
        });
        //-------------------------------------------------
        Obj.children().each(function(index, element) {
            $(this).click(function(){
                if($('#TreeList').has('input').length==0){
                    switch(index){
                        case 0:	EditNode(objTitle,objTitle.html());break;//编辑
                        case 1:	AddNode(0,objParent,NewNodeRename(0,objTitle));break;//添加同级目录
                        case 2:	AddNode(1,objParent,NewNodeRename(1,objTitle));break;//添加下级目录
                        case 3:	DelNode(objParent);break;//删除
                    }
                }
                else{
                    alert('请先取消编辑状态！');
                }
            });
        });
    }
    //************************************************************************************************************
    //************************************************************************************************************

    //===============================验证编辑结果================================
    function CheckEdititon(pNode,text){
        var SameLevelTags	= new Array(PrentNodeClass,ChildNodeClass);
        var SameLevelTag	= '';
        for(var i=0;i<SameLevelTags.length;i++){
            if(pNode.parent().attr('class').indexOf(SameLevelTags[i]) > -1){
                SameLevelTag = SameLevelTags[i];
                break;
            }
        }
        if(SameLevelTag!=''){
            if(text!=''){
                //---------------- 根据节点样式遍历同级节点 --------------------
                var IsExsit = false;
                pNode.parent().parent().children('div').children('.title').each(function(){
                    if(pNode.find('input').val()==$(this).html()){
                        IsExsit = true;
                        alert('抱歉！同级已有相同名称！');
                        return false;
                    }
                });
                return !IsExsit;
            }

            else{
                alert('不能为空!');
                return false;
            }
        }
    }

    //=================================自动命名================================
    function NewNodeRename(tag,pNode){
        //---------------- 根据节点样式遍历同级节点 --------------------
        var MaxNum = 0;
        var TObj;
        if(tag==0){//添加同级目录
            if(pNode.attr('id')==TreeName){
                TObj = pNode.children('div').children('.title');
            }
            else{
                TObj = pNode.parent().parent().children('div').children('.title');
            }
        }
        else{//添加下级目录
            if(pNode.parent().next().html()!=null){//原来已有子节点
                TObj = pNode.parent().next().children('div').children('.title');
            }
            else{//没有子节点
                TObj = null;
            }
        }

        if(TObj){
            TObj.each(function(){
                var CurrStr = $(this).html();
                var temp;
                if(CurrStr.indexOf(NewNodeName)>-1){
                    temp = parseInt(CurrStr.replace(NewNodeName,''));
                    if(!isNaN(temp)){
                        if(!(temp<MaxNum)){
                            MaxNum = temp + 1;
                        }
                    }
                    else{
                        MaxNum = 1;
                    }
                }
            });
        }

        var TempNewNodeName = NewNodeName;
        if(MaxNum>0){
            TempNewNodeName	+= MaxNum;
        }
        return TempNewNodeName;
    }

    //=============================== 编辑定义 ================================
    function EditNode(obj,text){
        obj.attr(Orititle,text);//将原来的text保存到Orititle中
        obj.html("<input type='text' class=input value=" + text + ">");//切换成编辑模式
        obj.parent().find('.editBT').html("<div class=ok title=确定></div><div class=cannel title=取消></div>");
        obj.has('input').children().first().focusEnd();//聚焦到编辑框内

        obj.parent().find('.ok').click(function(){
            Edit_OK(obj);
        });

        obj.parent().find('.cannel').click(function(){
            Edit_Cannel(obj);
        });
    }

    //=============================== 添加节点 ================================
    function AddNode(tag,obj,NameStr){
        if(tag==0 || tag==1){
        	
        	var id = $(obj).data("id");
        	var pid = $(obj).data("pid");
        	var level = $(obj).data("level");
        	
        	var nowLevel = level;
        	
            if(tag==0){//添加同级目录
            	var newNode = $('<div class=' + ChildNodeClass + ' data-pid="' + pid + '" data-level="' + level + '"></div>');
                newNode.appendTo(obj.parent());
            }
            else{
            	nowLevel = level + 1
            	var newNode = $('<div class=' + ChildNodeClass + ' data-pid="' + id + '" data-level="' + nowLevel + '"></div>');
            	//添加下级目录
                if(!(obj.next()) || (obj.next().attr('class')!=ChildrenListClass)){//最后一个节点和class!=ChildrenListClass都表示没有子节点
                    var ChildrenList = $('<div class=' + ChildrenListClass + '></div>');
                    ChildrenList.insertAfter(obj);//将子节点的”外壳“加入到对象后面
                    newNode.appendTo(ChildrenList);//将子节点加入到”外壳“内
                }
                else{
                    newNode.appendTo(obj.next());//将子节点加入到”外壳“内
                }
                obj.attr('class',PrentNodeClass + ' show');//激活父节点展开状态模式
                obj.next().css('display','');//展开子节点列表
            }

            with(newNode){
            	if (nowLevel < 4) {
            		html(EditHTML(NameStr));
            	} else {
            		html(EditHTMLWithOutTianJiaXiaJi(NameStr));
            	}
                
                //---------------------------------动态添加事件-------------------------------
                mouseover(function(){
                    TNode_MouseOver($(this));
                });

                mouseout(function(){
                    TNode_MouseOut($(this));
                });

                find('.title').click(function(){
                    TModuleNodeName_Click($(this));
                });

                find('.editArea').each(function(){
                    EditArea_Event($(this));
                });
                //---------------------------------------------------------------------------
            }
            EditNode(newNode.find('.title'),newNode.find('.title').html());//添加后自动切换到编辑状态
        }
    }

    //=============================== [删除]按钮 ================================
    function DelNode(obj){
    	var objParent = obj.parent();
        var objChildren = obj.next('.Row');
		if (objChildren.length > 0 || $(obj).data("id") == 1) {
			Msg.danger("存在子节点或者是根节点，不允许删除");
			return;
		}
    	
        if(confirm('确定要删除吗？')){
        	
        	Common.ajax("/admin/goods/classes/del", {"id":$(obj).data("id")}, function(json) {
        		if (json.code == Common.SUCC) {
        			
        			obj.remove();//基于Jquery是利用析构函数，所以“删除”后其相关属性仍然存在，除非针对ID来操作就可以彻底删除
                    objChildren.remove();//删除对象所有子节点
                    ChangeParent(objParent);
                    
        		} else {
        			Msg.danger(json.message);
        		}
        	});
        }
    }

    //=============================== 编辑[确定]按钮 ================================
    function Edit_OK(obj){
    	var ojbParent = obj.parent();
    	
    	var id = $(ojbParent).data("id");
    	var pid = $(ojbParent).data("pid");
    	var level = $(ojbParent).data("level");
    	
        var tempText = obj.has('input').children().first().val();

        if(CheckEdititon(obj,tempText)){
        	//编辑成功 更新后台数据库
        	Common.ajax("/admin/goods/classes/edit", {"id":id, "pid":pid, "level":level, "name" : tempText}, function(json) {
        		if (json.code == Common.SUCC) {
        			
        			if (id == undefined)  {
        				$(ojbParent).attr("data-id",json.data.id);
        			} else {
        				$(ojbParent).data("id", json.data.id);
        			}
        	    	$(ojbParent).data("pid", json.data.pid);
        	    	$(ojbParent).data("level", json.data.level);
        			
        			obj.html(tempText);
        		} else {
        			Msg.danger("服务异常");
        			obj.html(obj.attr(Orititle));
        		}
        	});
        }
        else{
            obj.html(obj.attr(Orititle));
        }
        obj.removeAttr(Orititle);
        obj.parent().find('.editBT').html('');
    }

    //=============================== 编辑[取消]按钮 ================================
    function Edit_Cannel(obj){
    	var ojbParent = obj.parent();
    	var id = $(ojbParent).data("id");
    	
    	if (id == undefined)  {
    		
    		var ojbParentParent = obj.parent().parent();
    		var objChildren = ojbParentParent.next('.Row');
    		
    		ojbParent.remove();//基于Jquery是利用析构函数，所以“删除”后其相关属性仍然存在，除非针对ID来操作就可以彻底删除
            objChildren.remove();//删除对象所有子节点
            ChangeParent(ojbParentParent);
    	} else {
    		obj.html(obj.attr(Orititle));
            obj.removeAttr(Orititle);
            obj.parent().find('.editBT').html('');
    	}
    }

    //=============================== 改变父节点样式 ================================
    function ChangeParent(obj){
        if(obj.find('.ChildNode').length==0){//没有子节点
            obj.prev('.'+PrentNodeClass).attr('class',ChildNodeClass);
            obj.remove();
        }
    }
    $.fn.setCursorPosition = function(position){
        if(this.lengh == 0) return this;
        return $(this).setSelection(position, position);
    }
    $.fn.setSelection = function(selectionStart, selectionEnd) {
        if(this.lengh == 0) return this;
        input = this[0];

        if (input.createTextRange) {
            var range = input.createTextRange();
            range.collapse(true);
            range.moveEnd('character', selectionEnd);
            range.moveStart('character', selectionStart);
            range.select();
        } else if (input.setSelectionRange) {
            input.focus();
            input.setSelectionRange(selectionStart, selectionEnd);
        }
        return this;
    }
    $.fn.focusEnd = function(){
        this.setCursorPosition(this.val().length);
    }
});


/**   
 * json格式转树状结构   
 * @param   {json}      json数据   
 * @param   {String}    id的字符串   
 * @param   {String}    父id的字符串   
 * @param   {String}    children的字符串   
 * @return  {Array}     数组   
 */
function transData(jsonDataList, idStr, pidStr, childerStr){    
    var r = [], hash = {}, id = idStr, pid = pidStr, children = childerStr, i = 0, j = 0, len = jsonDataList.length;
    for(; i < len; i++){    
        hash[jsonDataList[i][id]] = jsonDataList[i];    
    }    
    for(; j < len; j++){    
        var aVal = jsonDataList[j], hashVP = hash[aVal[pid]];    
        if(hashVP){    
            !hashVP[children] && (hashVP[children] = []);    
            hashVP[children].push(aVal);    
        }else{    
            r.push(aVal);    
        }    
    }    
    return r;
}  

function initTreeNode(jsonDataTree, idStr, pidStr, childerStr, isHidden) {
	
	var treeNode = "";
	var i = 0, len = jsonDataTree.length, id = idStr, pid = pidStr, children = childerStr;
	
	var parentNodeHidden = '<div class="ParentNode hidden">';
	var parentNodeShow = '<div class="ParentNode show">';
	var childNodeHidden = '<div class="ChildNode hidden" >';
	var childNodeShow = '<div class="ChildNode show" >';
	
	var editBt = '<div class="editBT"></div>';
	
	var row = '<div class="Row" style="display: none;">';
	var divEnd = '</div>';
	
	
	
	for(; i < len; i++){
		
		if (jsonDataTree[i].level > 4) {
			return "";
		}
			
		
		var dateset = ' data-id="' + jsonDataTree[i][id] + '" data-pid="' + jsonDataTree[i][pid] + '" data-level="'+ jsonDataTree[i].level + '">';
		 
		//不存在子节点
		if (jsonDataTree[i][children] == undefined) {
			
			if (isHidden) {
				treeNode += childNodeHidden.substr(0, childNodeHidden.length -1) + dateset;
			} else {
				treeNode += childNodeShow.substr(0, childNodeShow.length -1) + dateset;
			}
			
			treeNode += '<div class="title">' + jsonDataTree[i].name + '</div>';
			treeNode += editBt;
			
			if (jsonDataTree[i].level >= 4) {
				treeNode += '<div class="editArea">'
       			 + '<span>[编辑]</span>'
       			 + '<span>[添加同级]</span>'
       			 + '<span></span>'
       			 + '<span>[删除]</span>'
       			 + '<span class="add_treetime">创建时间：' + jsonDataTree[i].created + '</span>'
       		  + '</div>';
			} else {
				treeNode += '<div class="editArea">'
	       			 + '<span>[编辑]</span>'
	       			 + '<span>[添加同级]</span>'
	       			 + '<span>[添加下级]</span>'
	       			 + '<span>[删除]</span>'
	       			 + '<span class="add_treetime">创建时间：' + jsonDataTree[i].created + '</span>'
	       		  + '</div>';
			}
			
            treeNode += divEnd;
			
		} else {
			
			if (isHidden) {
				treeNode += parentNodeHidden.substr(0, parentNodeHidden.length -1) + dateset;
			} else {
				treeNode += parentNodeShow.substr(0, parentNodeShow.length -1) + dateset;
			}
			
			treeNode += '<div class="title">' + jsonDataTree[i].name + '</div>';
			treeNode += editBt;
			
			if (jsonDataTree[i].level >= 4) {
				treeNode += '<div class="editArea">'
       			 + '<span>[编辑]</span>'
       			 + '<span>[添加同级]</span>'
       			 + '<span></span>'
       			 + '<span>[删除]</span>'
       			 + '<span class="add_treetime">创建时间：' + jsonDataTree[i].created + '</span>'
       		  + '</div>';
			} else {
				treeNode += '<div class="editArea">'
	       			 + '<span>[编辑]</span>'
	       			 + '<span>[添加同级]</span>'
	       			 + '<span>[添加下级]</span>'
	       			 + '<span>[删除]</span>'
	       			 + '<span class="add_treetime">创建时间：' + jsonDataTree[i].created + '</span>'
	       		  + '</div>';
			}
			
			
			
            treeNode += divEnd;
            		  
			treeNode += row;
			
			treeNode += initTreeNode(jsonDataTree[i][children], idStr, pidStr, childerStr, false);
			
			treeNode += divEnd;
		}
    }
	
	return treeNode;
}

function initTree(treeid, treeNodeStr) {
	$(treeid).html(treeNodeStr);
}