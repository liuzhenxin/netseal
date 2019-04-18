/**
 * NetSeal通用js文件
 */

//---分页--综合--面向对象---laypage
   function page(cont,pages,pageNum,url,judge){	 
	   //分页
		laypage({
			cont : cont,
			skip : true,//跳转页面选项
			pages : pages, //可以叫服务端把总页数放在某一个隐藏域，再获取。假设我们获取到的是18
			
			curr : function() { //通过url获取当前页，也可以同上（pages）方式获取
				var pageNo = pageNum; // 当前页(后台获取到的)
				return pageNo ? pageNo : 1; // 返回当前页码值
			}(),
			jump : function(e, first) { //触发分页后的回调  
				if(!judge){//---judge--是用来判断某个div加载
					if (!first) { //一定要加此判断，否则初始时会无限刷新
						loadUrl(url+e.curr); 					
					}
				}else{
					if (!first) { //一定要加此判断，否则初始时会无限刷新
						$(judge).load(url+ e.curr);
					}
				}
			}
		});
}
   
   //---icheck 方法的提用----
   function icheck(attr){
		 
		  //----
		  $(attr).click(function(){
				if(this.checked){
					$(":checkbox").prop("checked",true);
			    }else{  
			    	$(":checkbox").prop("checked",false);
			    }
			});
			$('[name=checkboxt]:checkbox').click(function(){
				if(!this.checked){
					$(attr).prop("checked",false);
				}
				checkFlag = true;
				$('[name=checkboxt]:checkbox').each(function(){
					if(!this.checked){
						checkFlag = false;
					}
				});
				if(checkFlag){
					$(attr).prop("checked",true);
				}
			});
			
   }
   
    //总和--删除和重置密码
	function judgeCheckbox(title,url,refresh) {
		
		var id = "";
		var index = 0;
		$("[name=checkboxt]:checkbox").each(function(){
			if(this.checked){
				if(index == 0){
					id = $(this).val();
				}else{
					id = id + "," + $(this).val();
				}
				index++;
			}
		});
		if(index == 0){
			layer.alert( "请至少选择一条要操作的记录",{icon:0});
			return;
		} 
		//---开始之前的判断结束--可以单独抽取出来
			layer.confirm("确定要"+title+"?",{btn:["确定","取消"]},function(){
				$.ajax({
					url : url,
					type : "get",
					data : "id=" + id,
					dataType : "json",
					success : function(data) {
						if(data.success){
							layer.alert(data.message,{icon:1});
							loadUrl(refresh);
						}else{
							layer.alert(data.message,{icon:2});
							loadUrl(refresh);
						}
					},
					error : function() {
						layer.alert("请求失败",{icon:2});
					}
				});
			});
	} 
   
	//---三个页面间的切换与隐藏
	 //---页面的显示和隐藏
		function shoHide(sw, tb, hd,option) {
			  
			if ($( sw).is(":hidden")) {
				$( sw).show();
				$( tb).hide();
				$( hd).hide();
			} else if ($( sw).is(":visible") && $( tb).is(":hidden")) {
				$( sw).hide();
				$( hd).hide();
				$( tb).show();
			} else {
				$( sw).hide();
				$( hd).hide();
				$( tb).show();
			}
			if(typeof(option) != "undefined"){
				$(option).data('bootstrapValidator').resetForm();
			}
			
		}
   
   
   