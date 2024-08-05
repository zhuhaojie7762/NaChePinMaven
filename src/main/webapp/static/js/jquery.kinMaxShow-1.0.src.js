/**
 +-------------------------------------------------------------------------------------------------------------
 * [全屏焦点图]插件 jquery.kinMaxShow
 +-------------------------------------------------------------------------------------------------------------
 * @author   Mr.kin
 * @version  1.0
 * @file  	 jquery.kinMaxShow-1.0.src.js
 * @info  	 报告BUG、建议�?�索取最新版�? 请Mail：Mr.kin@foxmail.com(注：邮件标题请包含kinMaxShow 以便于邮箱自动归�?)
 * @date  	 2013-07-07
 +-------------------------------------------------------------------------------------------------------------
 */
 /* 代码整理：懒人之�? www.lanrenzhijia.com */
(function($){
	$.fn.kinMaxShow = function(user_options){
			//默认设置
			var default_options = {
						//幻灯片高�? 默认500
						height:500,
						//幻灯片切换间隔时�? 单位:�?
						intervalTime:6,
						//幻灯片切换时�? 单位：毫�? ,若设置为0 则无切换效果 直接跳到下一�?
						switchTime:1000,
						//擦除效果(切换) jQuery自带�? "linear" �? "swing" ,如需要其他擦除效果请使用 jquery.easing.js  插件
						easing:'linear',
						//图片对齐方式
						imageAlign:'center center',
						//按钮
						button:{
								//按钮鼠标切换事件 可�?�事�? click、mouseover
								switchEvent:'click',
								//按钮上是否显示索引数字，�?1�?始，默认不显�?
								showIndex:false,
								//按钮样式
								//正常 按钮样式  支持常规CSS样式，方法同jQuery css({key:val,…�?�})
								normal:{width:'14px',height:'14px',lineHeight:'14px',right:'10px',bottom:'10px',fontSize:'10px',background:"#cccaca",border:"1px solid #ffffff",color:"#666666",textAlign:'center',marginRight:'8px',fontFamily:"Verdana",float:'left'},
								//当前 按钮样式
								focus:{background:"#CC0000",border:"1px solid #FF0000",color:"#000000"}
							},
						//切换回调 index 当前图片索引，action 动作 的切�? 还是 切出 �?:fadeIn或fadeOut ，函数内 this指向 当前图片容器对象 可用来操作里面元素的动作 详情见demo�?
						callback:function(index,action){}
					
			};
			options = jQuery.extend(true,{},default_options,user_options);
			/* 代码整理：懒人之�? www.lanrenzhijia.com *//* 代码整理：懒人之�? www.lanrenzhijia.com */
			var k = {};
			
			//当前选择�?
			k.selector = $(this).selector;
			
			//判断是否有多个对�? 如�?�取了多个对象抛出错误，同一页面可以使用多个 但需要分别调用并且建议�?�择符用id�?
			if($(this).length>1){
				$.error('kinMaxShow error[More than one selected object]');
				return false;	
			}
			
			//当前操作对象
			k.self = this;
			//当前图片索引
			k.index = 0;
			//前一个图片索�?
			k.lindex = 0;
			//图片数量
			k.size = $(k.self).children('div').size();
			//CSS class命名空间前缀
			k.prename = 'kinMaxShow_';
			//数据存储
			k.data = {};
			//支持函数集合
			k.fn = {};
			
			//加载 解析幻灯片宽和高
			k.onload = function(){
				//设置容器尺寸 并且暂时隐藏内容部分
				$(k.self).css({width:'100%',height:options.height,overflow:'hidden',position:'relative'}).children('div').addClass(k.prename+'image_item').hide();
				//初始�?
				k.init();
					
			};			
			
			/* 代码整理：懒人之�? www.lanrenzhijia.com */
			//初始�?
			k.init = function(){
				
				k.setData();
				k.setLayout();
				k.setAnimate();
				
			};
			//数据
			k.setData = function(){
				k.data.title = new Array();
				$(k.self).children('div').each(function(){
					k.data.title.push($(this).find('img').attr('alt'));
				})					
			};
			
			//布局
			k.setLayout = function(){
				
				//image 容器
				$(k.self).children('div').wrapAll('<div class="'+k.prename+'image_box"></div>');
				$('.'+k.prename+'image_item',k.self).each(function() {
                    var a = $(this).children('a');
					if(a.length){
						var image = a.children('img').attr('src');
						a.children('img').remove();
					}else{
						var image = $(this).children('img').attr('src');
						$(this).children('img').remove();		
					}
					//
					$(this).css({background:'url('+image+') no-repeat '+options.imageAlign,'z-index':0});
					
                });/* 代码整理：懒人之�? www.lanrenzhijia.com */
				
				$('.'+k.prename+'image_item',k.self).eq(0).css('z-index','1');
				
				//button 容器
				if(options.button.normal.display!='none'){
					var button_list = '';
					for(i=1;i<=k.size;i++){
						if(options.button.showIndex){
							button_list+='<li>'+i+'</li>';
						}else{
							button_list+='<li> </li>';	
						}
					}				
					$(k.self).append('<ul class="'+k.prename+'button">'+button_list+'</ul>');
					$('.'+k.prename+'button li',k.self).eq(0).addClass('focus');
				}
				
				//设置 css
				k.setCSS();	
				
				//显示内容
				$('.'+k.prename+'image_item:gt(0)',k.self).css('z-index',0).css({opacity:0});
				$('.'+k.prename+'image_item',k.self).show();
				$(k.self).css({overflow:'visible',visibility:'visible',display:'block'});

				
			};
			
			//CSS
			k.setCSS = function(){
				
			var cssCode = '<style type="text/css">';
				cssCode+= k.selector+' *{ margin:0;padding:0;} ';
				cssCode+= k.selector+' .'+k.prename+'image_box{width:100%;height:'+parseInt(options.height)+'px;position:relative;z-index:1;} ';
				cssCode+= k.selector+' .'+k.prename+'image_box .'+k.prename+'image_item{width:100%;height:'+parseInt(options.height)+'px;position:absolute;overflow:hidden;} ';
				cssCode+= k.selector+' .'+k.prename+'image_box .'+k.prename+'image_item a{width:100%;height:'+parseInt(options.height)+'px;display:block;text-decoration:none;padding:0;margin:0;background:transparent;text-indent:0;outline:none;hide-focus:expression(this.hideFocus=true);} ';
				if(options.button.normal.display!='none'){
					cssCode+= k.selector+' .'+k.prename+'button{'+k.fn.objToCss(options.button.normal,['top','right','bottom','left'],true)+';position:absolute;list-style:none;z-index:2;}';				
					cssCode+= k.selector+' .'+k.prename+'button li{'+k.fn.objToCss(options.button.normal,['top','right','bottom','left'])+';cursor:pointer;-webkit-text-size-adjust:none;}';				
					cssCode+= k.selector+' .'+k.prename+'button li.focus{'+k.fn.objToCss(options.button.focus,['top','right','bottom','left'])+';cursor:default;}';				
				}
				cssCode+= '</style>';
				$(k.self).prepend(cssCode);
					
			}/* 代码整理：懒人之�? www.lanrenzhijia.com */
			
			//动画管理
			k.setAnimate = function(){
				
				options.callback.call($('.'+k.prename+'image_item:eq('+k.index+')',k.self),k.index,'fadeIn');
				
				var overDelayTimer;//当switchEvent是mouseover�?  执行延迟计时�?
				$('.'+k.prename+'button',k.self).delegate('li',options.button.switchEvent,function(){
					_this = this;
					function setChange(){
						k.index = $(_this).index();
						k.setOpacity();
					}
					if(options.button.switchEvent=='mouseover'){
						overDelayTimer = setTimeout(setChange,200);
					}else{
						setChange();
					}
				})
				//mouseover 延时
				if(options.button.switchEvent=='mouseover'){
					$('.'+k.prename+'button',k.self).delegate('li','mouseout',function(){
						clearTimeout(overDelayTimer);
					})						
				}				
				
				//设置索引
				k.index  = 1;
				k.lindex = 0;
				//自动切换定时�?
				k.data.moveTimer = setInterval(k.setOpacity,options.intervalTime*1000+options.switchTime);

			};/* 代码整理：懒人之�? www.lanrenzhijia.com */
			
			//擦除(切换)
			k.setOpacity = function(){
				
				//回调 fadeOut callback
				options.callback.call($('.'+k.prename+'image_item:eq('+(k.lindex)+')',k.self),k.lindex,'fadeOut');
				//清除切换定时�?
				clearInterval(k.data.moveTimer);
				//按钮切换
				if(options.button.normal.display!='none'){
					$('ul.'+k.prename+'button li',k.self).removeClass('focus');
					$('ul.'+k.prename+'button li',k.self).eq(k.index).addClass('focus');
				}
				
				//停止执行中的动画
				$('.'+k.prename+'image_item:animated',k.self).stop(true,false);
				//设置上一个显示的z-index�?0
				$('.'+k.prename+'image_item',k.self).css('z-index',0);
				//设置当前显示的z-index�?1
				$('.'+k.prename+'image_item',k.self).eq(k.index).css({opacity:0,'z-index':1});
	
				$('.'+k.prename+'image_item',k.self).eq(k.index).animate({opacity:1},{duration:options.switchTime,easing:options.easing,complete:function(){
						$('.'+k.prename+'image_box .'+k.prename+'image_item:not(:eq('+k.index+'))',k.self).css({opacity:0});
						//回调 fadeIn callback
						options.callback.call($('.'+k.prename+'image_item:eq('+k.index+')',k.self),k.index,'fadeIn');
						//自动切换定时�?
						k.data.moveTimer = setInterval(k.setOpacity,options.intervalTime*1000+options.switchTime);
						k.lindex = k.index;
						if(k.index==k.size-1){
							k.index=0;
						}else{
							k.index++;
						}
					}
				});
				
			};
			/* 代码整理：懒人之�? www.lanrenzhijia.com */
			//运行			
			k.run = function(){
				k.onload();
			};
			
			/* obj 对象样式，带�?"-"的需要转为驼峰式写法 如：font-size:12px; fontSize:12px;  excArr:不需要转换的列表排除在外�? 类型 数组 ['test1','opacity'] 若excFlag为ture则只转换excArr数组中的CSS*/
			k.fn.objToCss = function(obj,excArr,excFlag){
				excFlag = excFlag?true:false;
				var isIE = navigator.userAgent.indexOf("MSIE")!=-1;
				var style = '';
				if(excFlag){
					for (var key in obj){
						if($.inArray(key,excArr)!=-1){
							pKey = key.replace(/([A-Z])/,KtoLowerCase);
							if(pKey=='opacity' && isIE){
								style +="filter:alpha(opacity="+(obj[key]*100)+");";
							}else{
								style +=pKey+":"+obj[key]+";";	
							}
						}
					};		/* 代码整理：懒人之�? www.lanrenzhijia.com */			
				}else{
					for (var key in obj){
						if($.isArray(excArr)){
							if($.inArray(key,excArr)==-1){
								pKey = key.replace(/([A-Z])/,KtoLowerCase);
								if(pKey=='opacity' && isIE){
									style +="filter:alpha(opacity="+(obj[key]*100)+");";
								}else{
									style +=pKey+":"+obj[key]+";";	
								}
							}
						}else{
							pKey = key.replace(/([A-Z])/,KtoLowerCase);
							if(pKey=='opacity' && isIE){
								style +="filter:alpha(opacity="+(obj[key]*100)+");";
							}else{
								style +=pKey+":"+obj[key]+";";	
							}					 
						}
					};	
				}
				/* 代码整理：懒人之�? www.lanrenzhijia.com */
				
				function KtoLowerCase(word){
					var str='';
					str = '-'+word.toLowerCase();
					return str; 
				};
				return style;
			};
			
			/* 运行 */
			k.run();

		
		
	}	
	
})(jQuery)

/* 代码整理：懒人之�? www.lanrenzhijia.com */