<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>

<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">监控</h2>
</div>
		<ul class="breadcrumb" style="">
	<li>
		<i class="fa fa-home"></i>
		<i style="color:rgb(42,63,84); font-style:normal;" id="breadcrumbSeal">监控    /    系统监控</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
	<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
			系统层</a>
	</li>
</ul>


<div id="myTabContent" class="tab-content" style="margin-top:20px;">
				<div class="tab-pane fade in active" id="home">
					<section class="content">						
						<div class="row">
						  <div class="col-xs-12">
							<div class="x_panel" >	
	 
		<div class="container-fluid" style="width: 80%;">
				<div class="row">
						<div class="col-lg-12 col-md-6">
							
				  			<div class="tab-content marginTop">
						    <div role="tabpanel" class="tab-pane active" id="config">
						    	<div class="row clearfix" id="network" >
						    	<div class="">
						    	
							    		<div class="col-lg-12">
							    			<div class="panel panel-default">
											  	<div class="panel-body">
											    	<div class="row-fluid">
											  			<div class="contariner-fluid">
															<div id="disk" style="height:400px;width:100%;text-aligen:center">
															</div>
														</div>
											    	</div>
											  	</div>
											</div>
							    		</div>
							    		<div class="col-lg-12">
								    		<div class="col-lg-6">
								    			<div class="panel panel-default">
												  	<div class="panel-body">
												    	<div class="row-fluid">
												  			<div class="contariner-fluid">
																<div id="memA" style="height:400px;width:100%;text-aligen:center">
																</div>
															</div>
												    	</div>
												  	</div>
												</div>
								    		</div>
								    		<div class="col-lg-6">
								    			<div class="panel panel-default">
												  	<div class="panel-body">
												    	<div class="row-fluid">
												  			<div class="contariner-fluid">
																<div id="memS" style="height:400px;width:100%;text-aligen:center">
																</div>
															</div>
												    	</div>
												  	</div>
												</div>
								    		</div>
							    		</div>
						    		</div>
						    		<div class="">
						    			<div class="col-lg-12">
							    			<div class="panel panel-default">
											  	<div class="panel-body" id="body" >
											    	<div class="row-fluid" style="text-aligen:center">
											    		<div class="contariner-fluid" id="cpus">
											    			<div id="cpu" style="height:400px;width:100%;text-aligen:center">
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
			</div>
		  </section>
	    </div>
  	  </div>  
	

	
	

	<script type="text/javascript">
		var cpu = {
			maxLength:20,
			chart:echarts.init(document.getElementById("cpu")),
			option:{
		    	title : {
						text: 'CPU状况',			       
					 	x:'left'
				},
			    tooltip : {
			        trigger: 'axis'
			    },
			    legend: {
			    	orient: 'horizontal',
			        data:['系统使用率','用户使用率'],
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
		                 name: '用户使用率',
		                 min: 0,
		                 max: 100,
		                 scale:false,
		                 position: 'right',
		                 axisLine: {
		                     lineStyle: {
		                         color: '#5793f3'
		                     }
		                 },
		                 axisLabel: {
		                     formatter: '{value} % '
		                 }
		             },
		             {
		                 type: 'value',
		                 name: '系统使用率',
		                 min: 0,
		                 max: 100,
		                 position: 'left',
		                 axisLine: {
		                     lineStyle: {
		                         color: '#675bba'
		                     }
		                 },
		                 axisLabel: {
		                     formatter: '{value} %'
		                 }
		             }
			    ],
			    series : [
					{
					    name:'系统使用率',
					    type:'line',
					    smooth:true,
					    data:[]
					},
					{
					    name:'用户使用率',
					    type:'line',
					    smooth:true,
					    yAxisIndex: 1,
					    data:[]
					}
			   	]			
			},
			update:function(data){
				var axisData = (new Date(data['systime'])).toLocaleTimeString().replace(/^\D*/,'');
				if(cpu['option']['xAxis']['data'].length > cpu['maxLength']){
					cpu['option']['xAxis']['data'].shift();
					cpu['option']['series'][0]['data'].shift();
					cpu['option']['series'][1]['data'].shift();
				}
				cpu['option']['xAxis'].data.push(axisData);
				cpu['option']['series'][0].data.push(data['cpu_sys']);
				cpu['option']['series'][1].data.push(data['cpu_use']);
				cpu['chart'].setOption(cpu['option']);
			},
			resize:function(){
				this.chart.resize();
			}
		};
		
		var disk = echarts.init(document.getElementById("disk"));
		disk.setOption({
			title : [{text: '磁盘情况(MB)',subtext: '',x:'left'}],
			tooltip :{
				trigger: 'axis',
				axisPointer : {            // 坐标轴指示器，坐标轴触发有效
		           type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
				}
		   	},	    		
		   	grid: {
		        left: '10%',
		        right: '15%',
		        bottom: '10%',
		        containLabel: true
		    },	   
		    xAxis: {
		    	type : 'category',
		        data: [],
		        axisTick : {
					alignWithLabel : true
				}
		    },
		    yAxis: {},
		    series: [{
		        name: '总量',
		        color: ['#3398DB'],
		        barWidth: '20%',
		        type: 'bar',
		        data: []
		    },{
		        name: '已用',
		        color: ['#C1232B'],
		        barWidth: '20%',
		        type: 'bar',
		        data: []
		    }]
		});
		
		var memA = {
				chart:echarts.init(document.getElementById("memA")),
				option:{
					title : [					
					    {text: '内存信息(MB)',subtext: '',x:'left'}
					],
					tooltip :{
				        trigger: 'item',
				        formatter: "{a} <br/>{b} : {c} ({d}%)"
				   	},	    		
				    legend: {
				        orient : 'horizontal',
				        top:'20%',
				        data:['used','free']
				    },
				    series : [
				        {
				          name:'内存信息(MB)',
				          type:'pie',
				          center: ['50%', '60%'],
				          radius:'50%',
				          data:[
				                {value:0, name:'used'},
				                {value:0, name:'free'}	
				          ]
				        }
				    ]					
				},
				update:function(data){
					memA['option']['series'][0]['data'][0]['value'] = data['mem_Tused'];
					memA['option']['series'][0]['data'][1]['value'] = data['mem_Tfree'];;
					memA['chart'].setOption(memA['option']);
				},
				resize:function(){
					this.chart.resize();
				}
			};
		
		
		var memS = {
				chart:echarts.init(document.getElementById("memS")),
				option:{
					title : [					
					    {text: '交换区信息(MB)',subtext: '',x:'left'}
					],
					tooltip :{
				        trigger: 'item',
				        formatter: "{a} <br/>{b} : {c} ({d}%)"
				   	},	    		
				    legend: {
				        orient : 'horizontal',
				        top:'20%',
				        data:['used','free']
				    },
				    series : [
				        {
				          name:'交换区信息(MB)',
				          type:'pie',
				          center: ['50%', '60%'],
				          radius:'50%',
				          data:[
				                {value:0, name:'used'},
				                {value:0, name:'free'}	
				          ]
				        }
				    ]					
				},
				update:function(data){
					memS['option']['series'][0]['data'][0]['value'] = data['mem_Sused'];
					memS['option']['series'][0]['data'][1]['value'] = data['mem_Sfree'];
					memS['chart'].setOption(memS['option']);
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
			$.get("${ctx }/monitor/monitorDataSystem.do", function(data, status){
				if(status){
					 // 填入数据
				    disk.setOption({
				        xAxis: {
				            data: data.nameList
				        },
				        series: [{
				            name: '总量',
				            data: data.totalList
				        },
				        {
				            name: '已用',
				            data: data.usedList
				        }]
				    });
					
					cpu.update(data);
					memA.update(data);
					memS.update(data);
					
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
			cpu.resize();
            memA.resize();
            memS.resize();
		}
	</script>
