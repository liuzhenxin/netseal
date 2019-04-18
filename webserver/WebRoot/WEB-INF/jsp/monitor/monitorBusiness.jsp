<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>

<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">监控</h2>
</div>
<ul class="breadcrumb" style="">
	<li>
		<i class="fa fa-home"></i>
		<i style="color:rgb(42,63,84); font-style:normal;" id="breadcrumbSeal">监控    /    业务监控</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
	<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
			业务层</a>
	</li>
</ul>
<div id="myTabContent" class="tab-content" style="margin-top: 20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">
			<div class="row">
				<div class="col-xs-12">
					<div class="x_panel">
						<div class="container-fluid" style="width: 80%;">
							<div class="row">
								<div class="col-lg-12 col-md-6">
									<div class="tab-content marginTop">
										<div role="tabpanel" class="tab-pane active" id="config">
											<div class="row" id="network">
												<div class="col-lg-12">
													<div class="panel panel-default">
														<div class="panel-body">
															<div class="row-fluid">
																<div class="contariner-fluid">
																	<div id="Business" style="width:100%; height: 400px; text-aligen: center">
																	</div>
																</div>
															</div>
														</div>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</section>
	</div>
</div>


<script type="text/javascript">
	var Business = {
		maxLength:20,
		chart:echarts.init(document.getElementById("Business")),
		option:{
	    	title : {
				text: '系统交易情况',			       
			 	x:'left'
			},
		    tooltip : {
		        trigger: 'axis'
		    },
		    legend: {
		    	orient: 'horizontal',
		        data:['成功数','失败数'],
				top:'15%',
				x:'left',
		        width: 'window.innerWidth',
		        height :'window.innerHeight*0.8'
		       
		    },
		    grid: [
		           {x: '15%', y: '45%', width: '75%', height: '45%'}
		       ],
		    xAxis : {
	        	name:'',
	            type : 'category',
	            boundaryGap : false,
	            data : []
		    },
		    yAxis : [
	             {
	                 type: 'value',
	                 name: '成功数',
	                 position: 'left',
	                 axisLine: {
	                     lineStyle: {
	                         color: '#675bba'
	                     }
	                 },
	                 axisLabel: {
	                     formatter: '{value} '
	                 }
	             },
	             {
	                 type: 'value',
	                 name: '失败数',
	                 scale:false,
	                 position: 'right',
	                 axisLine: {
	                     lineStyle: {
	                         color: '#5793f3'
	                     }
	                 },
	                 axisLabel: {
	                     formatter: '{value} '
	                 }
	             }
		    ],
		    series : [
				{
				    name:'成功数',
				    type:'line',
				    smooth:true,
				    data:[]
				},
				{
				    name:'失败数',
				    type:'line',
				    smooth:true,
				    yAxisIndex: 1,
				    data:[]
				}
		   	]			
		},
		update:function(data){
			var axisData = (new Date(data['systime'])).toLocaleTimeString().replace(/^\D*/,'');
			if(Business['option']['xAxis']['data'].length > Business['maxLength']){
				Business['option']['xAxis']['data'].shift();
				Business['option']['series'][0]['data'].shift();
				Business['option']['series'][1]['data'].shift();
			}
			Business['option']['xAxis'].data.push(axisData);
			Business['option']['series'][0].data.push(data['dealSucc']);
			Business['option']['series'][1].data.push(data['dealFail']);
			Business['chart'].setOption(Business['option']);
		},
		resize:function(){
			this.chart.resize();
		}
	};
	
	$(function(){
		buildCharts();
		$("div.panel.panel-default").mouseover(function(){
			$(this).css("outline", "#DCDCDC outset thick");
		});
		$("div.panel.panel-default").mouseout(function(){
			$(this).css("outline", "");
		});
		resize();
	});
	
	function buildCharts(){
		$.get("${ctx }/monitor/monitorDataBusn.do", function(data, status){
			if(status){
				data = JSON.parse(data);
				Business.update(data);
			    timer = setTimeout("buildCharts()", 5000);
			}
		}).fail(function(){
			$("#config").empty();
			$("#config").text("获取信息发生错误");
		});
	}
	
	function newOption(text, name){
		var result = new Object();
		result = {
			title:{
	   			text: text,
		        x:'left'
	   		},	    		    
		    tooltip : {
		        formatter: "{b} <br/>{c} {a} "
		    },
		    series :{
	            name: 'RPM',
	            type: 'gauge',
	            center:['50%','60%'],
	            min: 1000,
	            max: 9000,
	            splitNumber: 4,
	            radius: '75%',
	            axisLine: {            // 坐标轴线
	                lineStyle: {       // 属性lineStyle控制线条样式
	                    width: 5
	                }
	            },
	            axisTick: {            // 坐标轴小标记
	                length: 10,        // 属性length控制线长
	                lineStyle: {       // 属性lineStyle控制线条样式
	                    color: 'auto'
	                }
	            },
	            splitLine: {           // 分隔线
	                length: 10,         // 属性length控制线长
	                lineStyle: {       // 属性lineStyle（详见lineStyle）控制线条样式
	                    color: 'auto'
	                }
	            },
	            title : {
	                textStyle: {       // 其余属性默认使用全局文本样式，详见TEXTSTYLE
	                    fontWeight: 'bolder',
	                    fontSize: 10,
	                    fontStyle: 'italic'
	                }
	            },
	            detail : {
	                textStyle: {       // 其余属性默认使用全局文本样式，详见TEXTSTYLE
	                    fontWeight: 'none'
	                }
	            },
	            data:{value: 0, name: name}
	        }
		}
		return result;
	}
	
	window.onresize = function(event) {
	    resize();
	};
	
	function resize(){
		Business.resize();       
	}
	
</script>
