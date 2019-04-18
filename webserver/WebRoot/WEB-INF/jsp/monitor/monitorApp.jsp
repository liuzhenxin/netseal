<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">监控</h2>
</div>
		<ul class="breadcrumb" style="">
	<li>
		<i class="fa fa-home"></i>
		<i style="color:rgb(42,63,84); font-style:normal;" id="breadcrumbSeal">监控    /    应用监控</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
	<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
			应用层</a>
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
															<div class="row-fluid" style="text-aligen:center">
																<div class="contariner-fluid" id="SocketNums">
																	<div id="SocketNum" style="height:400px;width:100%;text-aligen:center"></div>
																</div>
															</div>
														</div>
													</div>
												</div>
												<div class="col-lg-12">
													<div class="panel panel-default">
														<div class="panel-body">
															<div class="row-fluid" style="text-aligen:center">
																<div class="contariner-fluid" id="ActiveCs">
																	<div id="ActiveC" style="height:400px;width:100%;text-aligen:center"></div>
																</div>
															</div>
														</div>
													</div>
												</div>
												<div class="col-lg-12">
													<div class="panel panel-default">
														<div class="panel-body">
															<div class="row-fluid" style="text-aligen:center">
																<div class="contariner-fluid" id="heaps">
																	<div id="heap" style="height:400px;width:100%;text-aligen:center"></div>
																</div>
															</div>
														</div>
													</div>
												</div>
												
												<!-- <div class="">
													<div class="col-lg-12">
														<div class="panel panel-default">
															<div class="panel-body">
																<div class="row-fluid" style="text-aligen:center">
																	<div class="contariner-fluid" id="PoolingPs">
																		<div id="PoolingP" style="height:400%;width:100%;text-aligen:center"></div>
																	</div>
																</div>
															</div>
														</div>
													</div>
													<div class="col-lg-12">
														<div class="panel panel-default">
															<div class="panel-body">
																<div class="row-fluid" style="text-aligen:center">
																	<div class="contariner-fluid" id="ActiveCs">
																		<div id="ActiveC" style="height:400%;width:100%;text-aligen:center"></div>
																	</div>
																</div>
															</div>
														</div>
													</div>
												</div> -->
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

	var SocketNum = {
		maxLength : 20,
		chart : echarts.init(document.getElementById("SocketNum")),
		option : {
			title : {
				text : 'TCP连接数量',
				x : 'left'
			},
			tooltip : {
				trigger : 'axis'
			},
			legend : {
				orient : 'horizontal',
				data : [ 'TCP连接数量' ],
				top : '15%',
				x : 'left',
				width : 'window.innerWidth',
				height : 'window.innerHeight*0.8'

			},
			grid : [ {
				x : '10%',
				y : '45%',
				width : '75%',
				height : '45%',
				borderWidth:1
			} ],
			xAxis : {
				name : '',
				type : 'category',
				boundaryGap : false,

				data : []
			},
			yAxis : [ {
				type : 'value',
				name : 'TCP连接数量',
				position : 'left',
				axisLine : {
					lineStyle : {
						color : '#675bba'
					}
				},
				axisLabel : {
					formatter : '{value} '
				}
			} ],
			series : [ {
				name : 'TCP连接数量',
				type : 'line',
				smooth : true,
				data : []
			} ]
		},
		update : function(data) {
			var axisData = (new Date(data['systime'])).toLocaleTimeString().replace(/^\D*/, '');
			if (SocketNum['option']['xAxis']['data'].length > SocketNum['maxLength']) {
				SocketNum['option']['xAxis']['data'].shift();
				SocketNum['option']['series'][0]['data'].shift();
			}
			SocketNum['option']['xAxis'].data.push(axisData);
			SocketNum['option']['series'][0].data.push(data['SocketNum']);
			SocketNum['chart'].setOption(SocketNum['option']);
		},
		resize : function() {
			this.chart.resize();
		}
	};

	var ActiveC = {
		maxLength : 20,
		chart : echarts.init(document.getElementById("ActiveC")),
		option : {
			title : {
				text : 'JDBC连接池信息',
				x : 'left'
			},
			tooltip : {
				trigger : 'axis'
			},
			legend : {
				orient : 'horizontal',
				data : [ '活动数','空闲数' ],
				top : '15%',
				x : 'left',
				width : 'window.innerWidth',
				height : 'window.innerHeight*0.8'

			},
			grid : [ {
				x : '10%',
				y : '45%',
				width : '75%',
				height : '45%'
			} ],
			xAxis : {
				name : '',
				type : 'category',
				boundaryGap : false,

				data : []
			},
			yAxis : [ {
				type : 'value',
				name : '活动数',
				position : 'left',
				axisLine : {
					lineStyle : {
						color : '#C1232B'
					}
				},
				axisLabel : {
					formatter : '{value} '
				}
			},{
				type : 'value',
				name : '空闲数',
				position : 'right',
				axisLine : {
					lineStyle : {
						color : '#675bba'
					}
				},
				axisLabel : {
					formatter : '{value} '
				}
			} ],
			series : [ {
				name : '活动数',
				type : 'line',
				smooth : true,
				data : []
			},{
				name : '空闲数',
				type : 'line',
				smooth : true,
				yAxisIndex: 1,
				data : []
			} ]
		},
		update : function(data) {
			var axisData = (new Date(data['systime'])).toLocaleTimeString().replace(/^\D*/, '');
			if (ActiveC['option']['xAxis']['data'].length > ActiveC['maxLength']) {
				ActiveC['option']['xAxis']['data'].shift();
				ActiveC['option']['series'][0]['data'].shift();
				ActiveC['option']['series'][1]['data'].shift();
			}
			ActiveC['option']['xAxis'].data.push(axisData);
			ActiveC['option']['series'][0].data.push(data['ActiveC']);
			ActiveC['option']['series'][1].data.push(data['PoolingC']);
			ActiveC['chart'].setOption(ActiveC['option']);
		},
		resize : function() {
			this.chart.resize();
		}
	};
	
	
	
	var heap = {
		maxLength : 20,
		chart:echarts.init(document.getElementById("heap")),
		option:{
			title : [					
			    {text: 'JVM内存使用情况(单位:MB)',subtext: '',x:'left'}
			],
			color: ['#3398DB'],
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
		    xAxis : [
				{
					type : 'category',
					data : [ '堆内存总量', '已用堆内存', '已用栈内存' ],
					axisTick : {
						alignWithLabel : true
					}
				} ],
			yAxis : [
				{
					type : 'value'
				}],
			series : [ {
				name : '内存量',
				type : 'bar',
				barWidth: '30%',
				data : [ {
					value : 0,
					name : '堆内存总量'
				},{
					value : 0,
					name : '已用堆内存'
				}, {
					value : 0,
					name : '已用栈内存'
				} ]
			} ]
		},
		update : function(data) {
			heap['option']['series'][0]['data'][0]['value'] = data['heapMax'];
			heap['option']['series'][0]['data'][1]['value'] = data['heapUsed'];
			heap['option']['series'][0]['data'][2]['value'] = data['stackUsed'];
			heap['chart'].setOption(heap['option']);
		},
		resize : function() {
			this.chart.resize();
		}
	};

	$(function() {
		buildCharts();
		$("div.panel.panel-default").mouseover(function() {
			$(this).css("outline", "#DCDCDC outset thick");
		});
		$("div.panel.panel-default").mouseout(function() {
			$(this).css("outline", "");
		});
		resize();
	});

	function buildCharts() {
		$.get("${ctx }/monitor/MonitorDataAPP.do",function(data, status) {
			if (status) {
				data = JSON.parse(data);
				SocketNum.update(data);
				heap.update(data);
				ActiveC.update(data);
				timer = setTimeout("buildCharts()", 5000);
			}
		}).fail(function() {
				$("#config").empty();
				$("#config").text("获取信息发生错误");
			});
	}
	function newOption(text, name) {
		var result = new Object();
		result = {
			title : {
				text : text,
				x : 'left'
			},
			tooltip : {
				formatter : "{b} <br/>{c} {a} "
			},
			series : {
				name : 'RPM',
				type : 'gauge',
				center : [ '50%', '60%' ],
				min : 1000,
				max : 9000,
				splitNumber : 4,
				radius : '75%',
				axisLine : { // 坐标轴线
					lineStyle : { // 属性lineStyle控制线条样式
						width : 5
					}
				},
				axisTick : { // 坐标轴小标记
					length : 10, // 属性length控制线长
					lineStyle : { // 属性lineStyle控制线条样式
						color : 'auto'
					}
				},
				splitLine : { // 分隔线
					length : 10, // 属性length控制线长
					lineStyle : { // 属性lineStyle（详见lineStyle）控制线条样式
						color : 'auto'
					}
				},
				title : {
					textStyle : { // 其余属性默认使用全局文本样式，详见TEXTSTYLE
						fontWeight : 'bolder',
						fontSize : 10,
						fontStyle : 'italic'
					}
				},
				detail : {
					textStyle : { // 其余属性默认使用全局文本样式，详见TEXTSTYLE
						fontWeight : 'none'
					}
				},
				data : {
					value : 0,
					name : name
				}
			}
		}
		return result;
	}
	window.onresize = function(event) {
		resize();
	};
	function resize() {
		SocketNum.resize();
		heap.resize();
		ActiveC.resize();

	}
</script>
