$(document).ready(function(){
  if($("#AD_TL").length>0){
	  $("#AD_TL").html("<div class=SearchEngine_AD1>" + $("#ADCode_TL").html() + "</div>");
  }
  if($("#AD_BL").length>0){
	  $("#AD_BL").html("<div class=SearchEngine_AD1>" + $("#ADCode_BL").html() + "</div>");
  }
  
  if($("#AD_BR").length>0){
	  $("#AD_BR").html("<div class=SearchEngine_AD1>" + $("#ADCode_BR").html() + "</div>");
  }	

//2016-11-03  
  if($("#AD_TR").length>0){
	  $("#AD_TR").html("<div class=SearchEngine_AD1>" + $("#ADCode_TR").html() + "</div>");
  }
  /*
  if($('#AD_TR').length>0&&$('#ADCode_TR').length>0){
	  if($('script[id=ADCode_TR_js]').length>0){$('script[id=ADCode_TR_js]').remove();}
	  var _html	= $('#ADCode_TR').html();
	  $('#ADCode_TR').remove();
	  $('#AD_TR').html("<div class=SearchEngine_AD1>" + _html + "</div>");
  }*/
//2016-11-03 
  
  if($("#AD_T").length>0){
	  $("#AD_T").html("<div class=SearchEngine_AD1>" + $("#ADCode_T").html() + "</div>");
  }

  if($("#AD_B").length>0){
	  $("#AD_B").html("<div class=SearchEngine_AD1>" + $("#ADCode_B").html() + "</div>");
  }
  
  if($("#AD_M").length>0){
	  $("#AD_M").html("<div class=SearchEngine_AD1>" + $("#ADCode_M").html() + "</div>");
  } 
  
  if($("#AD_MT").length>0){
	  $("#AD_MT").html("<div class=AD_M1>" + $("#ADCode_MT1").html() + "</div>"+"<div class=AD_M2>" + $("#ADCode_MT2").html() + "</div>");
  } 
  
  if($("#AD_MB").length>0){
	  $("#AD_MB").html("<div class=AD_M2>" + $("#ADCode_MB1").html() + "</div>"+"<div class=AD_M1>" + $("#ADCode_MB2").html() + "</div>");
  } 
});
