function menuClick (menuName,ele) {
	if(!$(ele).parents('li').hasClass('treeview')){
		$('.menu-open').slideUp(500, function () {
			$('.menu-open').removeClass('menu-open');
		});
	}
	$(".sidebar-menu li.active").removeClass('active');
	$($(".sidebar-menu span:contains("+menuName+") ")[0]).parents('li').addClass('active');
};

angular.module('PEAWorkbench').controller("sideMenuController",['$scope','$location','services', '$http', '$rootScope', function($scope,$location,services, $http, $rootScope){
	
	$scope.init = function () {
		services.getTranslation($rootScope, $http, $('#i18nLang').val());
		$scope.setMenus();
	}
	
	/*var menuClick = function(menuName,$event) {
		var checkElement = $event.currentTarget;
		if(!$(checkElement).parents('li').hasClass('treeview')){
			$('.menu-open').slideUp(500, function () {
				$('.menu-open').removeClass('menu-open');
			});
		}
		$(".sidebar-menu li.active").removeClass('active');
		$($(".sidebar-menu span:contains("+menuName+") ")[0]).parents('li').addClass('active');
	};*/
	
	$scope.setMenus = function () {
		var sidebarMenuEl = $($(".sidebar-menu")[0]);
		var menuJson;
		$http.get('translation/menu.json').success(function(response) {
			menuJson = response;
			
			function chkAccessibility(accessibleGroups) {
				var isAccessible = true;
				//var accessibleGroups = value.accessGroup;
				if (accessibleGroups != undefined) {
					isAccessible = false;
					var userGroups = $('#userGroups').text();
					$.each(accessibleGroups.split(','), function (idx, v) {
						if (userGroups != undefined && userGroups.indexOf(v) > -1) {
							isAccessible = true;
						}
					});
				}
				return isAccessible;
			}
			
			$.each(menuJson, function(key, value) {
				var li, activeCls = '';
				
				if (key == 'settings') {
					return true;
				}
				
				if (!chkAccessibility(value.accessGroup)) {
					return true;
				}
				
				if (menuJson.settings.defaultActiveMenu == key) {
					activeCls = 'active';
				}
				if (value != undefined && !jQuery.isEmptyObject(value.subMenu)) {
					li = '<li class="treeview ' + activeCls + '">' +
								'<a >' +
								'<i class="'+ value.iClass +'"></i>' + 
								'<span>'+ value.displayName +'</span> ' +
								'<i class="fa fa-angle-left pull-right"></i>' +
								'</a>' +
								'<ul class="treeview-menu">';
								$.each(value.subMenu, function (k, val) {
									if (!chkAccessibility(val.accessGroup)) {
										return true;
									}
									
									li += '<li class="uploadFileLi"  onclick="menuClick(\''+ val.displayName +'\',this)" ><a href="#'+ val.href +'"><i class="'+ val.iClass +'"></i><span>'+ val.displayName +'</span></a></li>';
								});
									/*'<li class="uploadFileLi"  ng-click="menuClick('Upload Data File',$event)" ><a href="#fileUpload"><i class="fa fa-upload"></i><span>Upload Data File</span></a></li>' +
									'<li ng-click="menuClick('Monitor Data File Status',$event)"><a href="#monitorDataFileStatus"><i class="fa fa-file-text-o"></i> <span>Monitor Data File Status</span></a></li>' +
									'<li ng-click="menuClick('Manage Lookup Tables',$event)"><a href="#manageLookupTables"><i class="fa fa-table"></i><span> Manage Lookup Tables</span></a></li>' +
									'<li ng-click="menuClick('Mapping Data',$event)"><a href="#mappingData"><i class="fa fa-chain"></i><span>Mapping Data</span></a></li>' +*/
						li += 	'</ul>' +
							'</li>';
				}
				else {
					li = 	'<li class="' + activeCls + '" onclick="menuClick(\''+ value.displayName +'\',this)">' +
									'<a href="#'+ value.href +'" >' +
									'<i class="'+ value.iClass +'"></i> <span>'+ value.displayName +'</span></i>' +
									'</a>' +
								'</li>';
				}
				sidebarMenuEl.append(li);
			});
		});
		//element.html(scope.dynamic);
        //$compile(element.contents())(scope);
	}
	
	
	$scope.$watch('$viewContentLoaded', function(){
		var path = $location.path().substring(1);
		var imgVal = document.getElementById("sideMenuUSerImg").src.split("data:image/jpeg;base64,")[1] ;
		if(imgVal== undefined ||  imgVal.trim() == ""){
			document.getElementById("sideMenuUSerImg").src = "dist/img/defaultUserProfile.png" ;
			document.getElementById("headerUserImg").src = "dist/img/defaultUserProfile.png";
		}
		var selectMenu = $(".sidebar-menu a[href='#"+path+"']").find('span').text();
		$(".sidebar-menu li.active").removeClass('active');
		$($(".sidebar-menu span:contains('"+selectMenu+"') ")[0]).parents('li').addClass('active');
		
	});
	$scope.init();
}]);
		
angular.module('PEAWorkbench').controller("dashboardController", ['$scope','$http','services','$location', '$rootScope', function($scope, $http, services, $location, $rootScope){
	
	$scope.exportDataset = function (datasetId) {
		window.location.href = $('#contextPath').val() + '/ViewDatasets/export?fltrDatasetId=' + datasetId;
	}
	
	$scope.createDataSet=function(){
		window.location.href = $('#contextPath').val() + '/dashboard#/createDatasets';
	}
	
	$scope.formatDate = function(date){
		var dateOut = new Date(date);
		return dateOut;
	}
	
	$('button[id=saveUserSettings]').confirmation({
		singleton: true,
		title: 'This will redirect to dashboard, do you want to continue?',
		onConfirm: function () {
			$('.ajaxLoader').show();
			
			var userDateFormatSetting 	= $('select[id=userDateFormatSetting]').val()[0];
			var userCurrencySetting 	= $('select[id=userCurrencySetting]').val()[0];
			var userLangSetting 		= $('select[id=userLangSetting]').val()[0];
			
			$.ajax({
				type : 'POST',
				url :$('#contextPath').val() + '/saveUserSettings',
				data : {
					dateFormat: userDateFormatSetting,
					currencyFormat: userCurrencySetting,
					langFormat: userLangSetting
				},
				success : function(response) {
					$('.ajaxLoader').hide();
					var response = JSON.parse(response);
					if(response['SUCCESS'] == 'TRUE') {
						services.showNotification(response['Message']);
					} else {
						services.showNotification(response['Message'],{type: 'danger'});
					}
					//Redirect to dashboard
					window.location.href = $('#contextPath').val() + '/dashboard';
				},
				error : function(e) {
					$('.ajaxLoader').hide();
					console.error(e);
				}
			});
		},
		 onCancel: function() {
			
		},
	});
	
	$('button[id=saveUserSettings]').click(function () {
		console.log($(this));
		
		
	});
	
	$scope.deleteDataset= function(datasetId){
		var deletePara ={
				'datasetId':datasetId
		};
			$('#deleteDataset_'+datasetId).confirmation({
				singleton: true,
				onConfirm: function () {
					$('.ajaxLoader').show();
					$.ajax({
						type : 'POST',
						url :$('#contextPath').val() + '/makeDatasetInactive',
						data : deletePara,
						success : function(response) {
							$('.ajaxLoader').hide();
							var response = JSON.parse(response);
							if(response['SUCCESS'] == "true") {
								services.showNotification(response['Message']);
								$("tr#row_"+datasetId).remove();
							} else {
								services.showNotification(response['Message'],{type: 'danger'});
							}
							$('#deleteDataset_'+datasetId).confirmation('destroy');
						},
						error : function(e) {
							datatable.opts.scope['dtInstance' + opts.tableName].reloadData($.noop, false);
							$('.ajaxLoader').hide();
							console.error(e);
							$('#deleteDataset_'+datasetId).confirmation('destroy');
						}
					});
				},
				 onCancel: function() {
					$('#deleteDataset_'+datasetId).confirmation('destroy');
				},
		});
		$('#deleteDataset_'+datasetId).confirmation('show');
	}
	
	$scope.plotDataAvailabilityChart = function (opts) {
		
		var opts = opts ||  {};
		
		var dataAvailabilityStatisticCategories = [];
		var dataAvailabilityStatisticShipmentData = [];
		var dataAvailabilityStatisticEPOSData = [];
		var dataAvailabilityStatisticRatio = [];
		angular.forEach(opts.data.dataAvailabilityStatistics, function(value, key){
			dataAvailabilityStatisticCategories.push(value.promotionEndMonth == null ? 0 : value.promotionEndMonth);
			dataAvailabilityStatisticShipmentData.push(value.shipmentVolume == null ? 0 : value.shipmentVolume);
			dataAvailabilityStatisticEPOSData.push(value.eposvolume == null ? 0 : value.eposvolume);
			dataAvailabilityStatisticRatio.push(value.sellOutToshipmentRatio == null ? 0 : value.sellOutToshipmentRatio);
		});
		
		dataAvailabilityChart = new Highcharts.Chart({
			chart: {
				type: 'column',
				renderTo: 'dataAvailabilityChart'
			},
			credits: {
				enabled: false
			},
			title: {
				text: '',
				style: {
					display: 'none'
				}
			},
			colors: ["#00247D", "#45C3B8", "#CF142B"],
			xAxis: {
				categories: dataAvailabilityStatisticCategories,
				crosshair: true
			},
			yAxis: [{ // Secondary yAxis
				title: {
					text: 'Ratio %',
					style: {
						color: '#CF142B'
					}
				},
				labels: {
					format: '{value}%',
					style: {
						color: '#CF142B'
					}
				},
				beginAtZero: true,
//				max: 500,
				opposite: true
			}, { // Primary yAxis
				labels: {
					/*formatter: function() {
				           var ret,
				               numericSymbols = ['K','M','G','T','P','E'],
				               i = numericSymbols.length;
				           if(this.value >=1000) {
				               while (i-- && ret === undefined) {
				                   multi = Math.pow(1000, i + 1);
				                   if (this.value >= multi && numericSymbols[i] !== null) {
				                      ret = (this.value / multi) + numericSymbols[i];
				                   }
				               }
				           }
				           return (ret ? ret : this.value);
				       },*/
					style: {
						color: '#434348'
					}
					
				},
				
				title: {
					text: '',
					style: {
						color: Highcharts.getOptions().colors[1]
					}
				}
				
			}, ],
			tooltip: {
//				headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
//				pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name} : </td>' + '<td style="padding:0"><b>{point.y} </b></td></tr>',
//				footerFormat: '</table>',
				
				formatter: function() {
					//console.log(this);
					var month = this.x;
					var xx = {};
					
					var tooltip = '<span style="font-size:10px">' + month + '</span><table>';
					
					if (this.points != null) {
						$.each(this.points, function (key, value) {
							xx[value.series.name] = {};
							xx[value.series.name]['color'] = value.series.color;
							if (value.point.y < 0) {
								value.point.y = parseInt(value.point.y) * -1;
								value.point.y = '<span style="color:red">(' + value.point.y + ')</span>';
							}
							xx[value.series.name]['value'] = value.point.y;
							
							if (key == 2) {
								xx[value.series.name]['value'] += "%";
							}
							
							tooltip += '<tr><td style="color:' + xx[value.series.name].color + ';padding:0">' + value.series.name + ' : </td>' + '<td style="padding:0">   <b>' + xx[value.series.name].value + '</b></td></tr>';
							
						});
						
						tooltip += '</table>';
					}
					
					return tooltip;
	            },
				
				shared: true,
				useHTML: true
			},
			exporting: {
				enabled: false
			},
			plotOptions: {
				series: {
					pointWidth: 30 ,
					marker: {
						 radius: 6
					}
				}
			},
			series: [{
				name: 'SellIn Data',
				type: 'column',
				yAxis: 1,
				data: dataAvailabilityStatisticShipmentData,
				tooltip: {
					valueSuffix: ''
				}
			}, {
				name: 'SellOut Data',
				type: 'column',
				yAxis: 1,
				data: dataAvailabilityStatisticEPOSData,
				tooltip: {
					valueSuffix: ''
				}
			}, {
				name: 'Ratio%',
				 type: 'line', //spline
				
				data: dataAvailabilityStatisticRatio,
				tooltip: {
					valueSuffix: '%'
				}
			}],
			
		});
	};
	
	$scope.plotBusinessException = function (opts) {
		
		var opts = opts || {};
		var businessexceptionCategories = [];
		var businessexceptionClean = [];
		var businessexceptionException = [];
		
		if (opts.data.dataQualityReport && opts.data.dataQualityReport.businessexception) {
			angular.forEach(opts.data.dataQualityReport.businessexception, function(value, key){
				if($.inArray( value.month, businessexceptionCategories)== -1){//check for month is absent in array.
					if(value.exceptionFlag == "Clean" ){
						businessexceptionCategories.push(value.month);
						businessexceptionClean.push(value.count);
						var isUpdated = false;
						angular.forEach(opts.data.dataQualityReport.businessexception, function(valueNew, keyNew){
							if(value.month == valueNew.month && valueNew.exceptionFlag == "Exception"  ){
								businessexceptionException.push(valueNew.count);
								isUpdated = true;
							}
						});
						if(isUpdated == false){
							businessexceptionException.push(null);
						}
					}else{
						businessexceptionCategories.push(value.month);
						businessexceptionException.push(value.count);
						var isUpdated = false;
						angular.forEach(opts.data.dataQualityReport.businessexception, function(valueNew, keyNew){
							if(value.month == valueNew.month && valueNew.exceptionFlag == "Clean"  ){
								businessexceptionClean.push(valueNew.count);
								isUpdated = true;
							}
						});
						if(isUpdated == false){
							businessexceptionClean.push(null);
						}
					}
				}
			});
		}
		
		var newWidth= $("#businessexception").width();
		$("#technicalIssuesChart").width(newWidth);
		
		var businessexceptionChart = new Highcharts.Chart({
			chart: {
				type: 'column',
				renderTo: 'businessexception'
			},
			credits: {
				enabled: false
			},
			exporting: {
				enabled: false
			},
			title: {
				text: '',
				style: {
					display: 'none'
				}
			},
			
			colors: ["#7CB5EC", "#F7A35C"],
			xAxis: {
				categories: businessexceptionCategories
			},
			yAxis: {title: { text: 'No Of Records' } },
			plotOptions: {
				series: {
					allowPointSelect: true
				}
			},
			series: [{
				name: 'CLEAN',
				data: businessexceptionClean,
				
			}, {
				name: 'EXCEPTIONS',
				data: businessexceptionException,
				
			}],
			tooltip: {
				headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
				pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name} : </td>' + '<td style="padding:0"><b>{point.y} </b></td></tr>',
				footerFormat: '</table>',
				shared: true,
				useHTML: true,
				crosshairs: true,
			    animation: true,
			},
		});
		
	};
	
	$scope.plotTechnicalIssuesChart = function (opts) {
		var opts = opts || {};
		var technicalIssuesChartCategories = [];
		var technicalIssuesChartDataQuality = [];
		var technicalIssuesChartCheckDuplicate = [];
		var technicalIssuesChartReferentialIntegrity = [];
		var technicalIssuesChartFileValidation = [];
		function updateTechnicalIssues(processName,month){
			var isUpdated = false;
			angular.forEach(opts.data.dataQualityReport.technicaldq, function(valueNew, keyNew){
				if(month == valueNew.month && valueNew.processName == processName){
					switch(processName) {
				    case "Data Quality":
				    	technicalIssuesChartDataQuality.push(valueNew.count);
						isUpdated = true;
				        break;
				    case "Check duplicates":
				    	technicalIssuesChartCheckDuplicate.push(valueNew.count);
						isUpdated = true;
				        break;
				    case "Referential Integrity":
				    	technicalIssuesChartReferentialIntegrity.push(valueNew.count);
						isUpdated = true;
				        break;
				    case "File Validation":
				        	technicalIssuesChartFileValidation.push(valueNew.count);
				        	isUpdated = true;
				        	break;
					}
				}
			});
			return isUpdated;
		}
		if (opts.data.dataQualityReport  && opts.data.dataQualityReport.technicaldq) {
			angular.forEach(opts.data.dataQualityReport.technicaldq, function(value, key){
				if($.inArray( value.month, technicalIssuesChartCategories)== -1){//check for month is absent in array.
					technicalIssuesChartCategories.push(value.month);
					var returnVal= updateTechnicalIssues("Data Quality",value.month);
					if(returnVal == false){
						technicalIssuesChartDataQuality.push(null);
					}
					var returnVal= updateTechnicalIssues("Check duplicates",value.month);
					if(returnVal == false){
						technicalIssuesChartCheckDuplicate.push(null);
					}
					var returnVal= updateTechnicalIssues("Referential Integrity",value.month);
					if(returnVal == false){
						technicalIssuesChartReferentialIntegrity.push(null);
					}
					var returnVal= updateTechnicalIssues("File Validation",value.month);
					if(returnVal == false){
						technicalIssuesChartFileValidation.push(null);
					}
				}
			});
			technicalIssuesChartCategories = new Highcharts.Chart({
				chart: {
					type: 'column',
					renderTo: 'technicalIssuesChart'
				},
				title: {
					text: '',
					style: {
						display: 'none'
					}
				},
				credits: {
					enabled: false
				},
				exporting: {
					enabled: false
				},
				colors: ["#7CB5EC", "#F7A35C","green", "red"],
				xAxis: {
					categories: technicalIssuesChartCategories
				},
				
				yAxis: [{ 
		            gridLineWidth: 0,
		            title: {
		                text: 'count',
		                style: {
		                    color: Highcharts.getOptions().colors[1]
		                }
		            },
		            labels: {
		                style: {
		                    color: Highcharts.getOptions().colors[1]
		                }
		            }
		        },{ 
			            labels: {
			                style: {
			                    color: Highcharts.getOptions().colors[3]
			                }
			            },
			            title: {
			                text: '',
			                style: {
			                    color: Highcharts.getOptions().colors[3]
			                }
			            },
			            opposite: true
			        }],
				plotOptions: {
					series: {
						allowPointSelect: true
					}
				},
				series: [{
					name: 'Data Quality',
					data: technicalIssuesChartDataQuality,
					
				}, {
					name: 'Check Duplicates',
					data: technicalIssuesChartCheckDuplicate,
					yAxis: 1
					
				},{
					name:'Referential Integrity',
					data:technicalIssuesChartReferentialIntegrity,
					
				},{
					name:'File Validation',
					data:technicalIssuesChartFileValidation,
					
				},  
				
				],
				tooltip: {
					headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
					pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name} : </td>' + '<td style="padding:0"><b>{point.y} </b></td></tr>',
					footerFormat: '</table>',
					shared: true,
					useHTML: true,
					crosshairs: true,
				    animation: true,
				},
			});
		}
		else{
			$('#technicalIssuesChart').html('<div><h2 style="text-align:center;margin-top:0px;line-height: 10;">No Technical Issues Found !</h2><div>');
		}
	};
	
	$scope.plotMappingExceptionChart = function (opts) {
		
		var opts = opts || {};
		
		if(opts.data.dataQualityReport && opts.data.dataQualityReport.mappingexception){
			var mappingexception =opts.data.dataQualityReport.mappingexception;
			Highcharts.setOptions({
				colors: [ '#008000','#FF0000']
			});
			
			opts.totalCustMapping = mappingexception.mappedCustomerCount + mappingexception.UnmappedCustomerCount;
			opts.totalProdMapping = mappingexception.UnmappedProductCount + mappingexception.mappedProdCount;		
			
	        opts.totalCustMappingPercentage = mappingexception.mappedCustomerCount/opts.totalCustMapping*100+"%"; 	        
	        opts.totalProdMappingPercentage = mappingexception.mappedProdCount/opts.totalProdMapping*100+"%" ;
	        
	        opts.CustMappingOutOFText = mappingexception.mappedCustomerCount + ' out of ' + opts.totalCustMapping; 
	        opts.ProdMappingOutOFText = mappingexception.mappedProdCount + ' out of ' + opts.totalProdMapping;
	        
			var newWidth= ($(".dataIssuesSummaryTab").width()/2)-5;
			$("#customerContainer").width(newWidth);
			$("#ProductContainer").width(newWidth);
		
			var chart = new Highcharts.Chart({
		        chart: {
		            renderTo: 'customerContainer',
		            type: 'pie'		          
		        },
		        title:"",
		        credits: {
		            enabled: false
		        },
		        plotOptions: {
		            pie: {
		                innerSize: '60%',
		                allowPointSelect: true,
	                    cursor: 'pointer',
	                    dataLabels: {
	                        enabled: false
	                    },
	                    showInLegend: true
		            },
		           
		        },
		        series: [{
		        	name: 'Value',
		            data:[ ['Mapped',mappingexception.mappedCustomerCount],
		      		                ['Unmapped',mappingexception.UnmappedCustomerCount]]
		        }]
		    });
		
			var chart1 = new Highcharts.Chart({
		        chart: {
		            renderTo: 'ProductContainer',
		            type: 'pie'
		           
		        },
		        title:"",
		        credits: {
		            enabled: false
		        },
		        plotOptions: {
		            pie: {
		                innerSize: '60%',
		                allowPointSelect: true,
	                    cursor: 'pointer',
	                    dataLabels: {
	                        enabled: false
	                    },
	                    showInLegend: true
		            }
		            
		        },
		        series: [{
		        	 name: 'Value',
		            data: [
		                ['Mapped', mappingexception.mappedProdCount],
		                ['Unmapped',  mappingexception.UnmappedProductCount],
		         ]}]
		    });
		}
	}
	
	$scope.init = function () {
		$("#datepicker").datepicker({
			format: "M-yyyy",
			viewMode: "months",
			minViewMode: "months",
			endDate: '+0d',
			autoclose: true,
		});
		var d = new Date();
		$scope.month = new Array();
		$scope.month = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
		var currMonth = $scope.month[d.getMonth()];
		var currYear = d.getFullYear();
		$('#datepicker').datepicker('setDate', "" + currMonth + "-" + currYear);
		$('#datepicker').on('changeDate', function(dateObj){
			var date= ""+$scope.month[dateObj.date.getMonth()]+"-"+dateObj.date.getFullYear();
			$('.ajaxLoader').show();
			$http({
				url: $('#contextPath').val() + "/plugins/tree/dataQualityReport1.json",
				method: "POST",
				data:date 
			}).success(function(data, status, headers, config) {
				
				var EYNTKChartCategories =[];
				var EYNTKChartClean = [];
				var EYNTKChartACTION = [];
				angular.forEach(data.EYNTK, function(value, key){
					EYNTKChartCategories.push(value.month);
					EYNTKChartClean.push(value.clean);
					EYNTKChartACTION.push(value.action);
				});
				EYNTKChart.xAxis[0].update({categories:EYNTKChartCategories}, true);
				EYNTKChart.series[0].setData(EYNTKChartClean);
				EYNTKChart.series[1].setData(EYNTKChartACTION);
				var OPSOChartCategories = [];
				var OPSOChartClean = [];
				var OPSOChartACTION = [];
				angular.forEach(data.OPSO, function(value, key){
					OPSOChartCategories.push(value.month);
					OPSOChartClean.push(value.clean);
					OPSOChartACTION.push(value.action);
				});
				OPSOChart.xAxis[0].update({categories:OPSOChartCategories}, true);
				OPSOChart.series[0].setData(OPSOChartClean);
				OPSOChart.series[1].setData(OPSOChartACTION);
				$('.ajaxLoader').hide();
			}).error(function(data, status, headers, config) {
				$scope.status = status;
				$('.ajaxLoader').hide();
			});
		});
		
		
		$('.ajaxLoader').show();
		
		$http({
			url: $('#contextPath').val() + "/dashboard/data",
			method: "GET",
		}).success(function(data, status, headers, config) {
			$scope.data = data;
			$scope.data.currentUser = $("#headerUserName").text() ;
			
			Highcharts.setOptions({
				lang: {
					thousandsSep: ''
				}
			});
			
			$scope.plotDataAvailabilityChart({
				data: $scope.data 
			});
			
			$scope.plotBusinessException({
				data: $scope.data
			});
		  
		  	$scope.plotTechnicalIssuesChart({
		  		data: $scope.data
		  	});
			
			$scope.plotMappingExceptionChart({
				data: $scope.data
			});

			$('.ajaxLoader').hide();
			
		}).error(function(data, status, headers, config) {
			$scope.status = status;
			$('.ajaxLoader').hide();
		});
	};

		
    
	$scope.redirect= function(source){
		$rootScope.monitorDataFileStatusSourceType = source;
		$location.url('/monitorDataFileStatus');
	}
	
	$scope.redirectToDQReport = function (){
		var activeEle = $('.DQReport .tab-content .active').attr('id');
		switch (activeEle) {
			case 'mappingIssues':$location.url('/mappingData');
				break;
			case 'technicalIssues':$location.url('/dataQuality');
				break;				
			case 'exceptionMangement':
				$rootScope.viewExceptionDatasetType= $('.DQReport ul.nav-tabs li.active a').text();
				$location.url('/viewExceptions');
				break;
			default:
				break;
		}
	}
	
	$scope.redirectToDataSets = function (){
		$rootScope.exportDatasetsDataType= $('.datasetbox ul.nav-tabs li.active a').text();
		$location.url('/exportDatasets');
	}
	
	$scope.redirectFromDataSetsWithName = function (option,source){
		$rootScope.exportDatasetsDataType= $('.datasetbox ul.nav-tabs li.active a').text();
		$rootScope.exportDatasetsDataset= option;
		$location.url('/exportDatasets');
	}
	
	$scope.navTabClick= function(tabName,$event) {
		if(tabName=='OPSO'){
			$('.DQReport #small' ).text('Exception handling');
		}
		else if(tabName=='EYNTK')
		{
			$('.DQReport #small').text('Action handling');
		}
		$($event.target).parents('ul').next(".tab-content").children().removeClass('active');
		$("#"+tabName).addClass('active');
	};
	$scope.init();
}]);
		
angular.module('PEAWorkbench').controller("monitorJobLogsController", ['$scope', '$rootScope', '$compile', '$http', 'DTOptionsBuilder', 'DTColumnBuilder', 'services', function($scope, $rootScope, $compile, $http, DTOptionsBuilder, DTColumnBuilder, services){
	$scope.openModal = function (index) {
		$("#monitorJobModal").modal("show");
	}
	
	$scope.services = services;
	
	$scope.services.datatableInit({
		tables: ['MonitorJobLogs'],
		http: $http,
		scope: $scope,
		rootScope: $rootScope,
		compile: $compile,
		dtColumnBuilder: DTColumnBuilder,
		dtOptionsBuilder: DTOptionsBuilder,
		isTable: false
	});
}]);
		
angular.module('PEAWorkbench').controller("userProfileController", ['$scope','$http','services','$timeout', function($scope,$http,services,$timeout){
	$scope.data = {};
	$scope.success = false;

	$scope.emailFormat = /^[a-z]+[a-z0-9._]+@[a-z]+\.[a-z.]{2,5}$/;
	
	$scope.services =services;
    function readURL(input) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();

            reader.onload = function (e) {
                //$('#image_upload_preview').attr('src', e.target.result);
            	var data = e.target.result;
                if (data.match(/^data:image\//)) {
                    $('#image_upload_preview').attr('src', data);
                } else {
                	var msg = 'Invalid file. Please upload an image file';
					services.showNotification(msg,{type: 'danger'
					});
					$('#uploadPhoto').val('');
					
                }
            }

            reader.readAsDataURL(input.files[0]);
        }
    }

    $("#uploadPhoto").change(function () {
    	readURL(this);
    });
    $scope.getInfo = function getInfo() {
		// Sending request to fetch data
		$('.ajaxLoader').show();
		
		$http({
			url: $('#contextPath').val() + '/userProfile/getinfo',
			method: "GET",
			
		}).success(function(data, status, headers, config) {
			$('.ajaxLoader').hide();
			var arrayBufferView = new Uint8Array( data.photo );
		    var blob = new Blob( [ arrayBufferView ], { type: "image/jpeg" } );
		    var urlCreator = window.URL || window.webkitURL;
		    var imageUrl = urlCreator.createObjectURL( blob );				    
			$scope.data = data;
			if(data.photo == null || data.photo.length == 0) {
				document.getElementById("image_upload_preview").src = "dist/img/defaultUserProfile1.png";
			}else{
				document.getElementById("image_upload_preview").src = imageUrl;
				document.getElementById("sideMenuUSerImg").src = imageUrl;
				document.getElementById("headerUserImg").src = imageUrl;
			}
		}).error(function(data, status, headers, config) {
			$('.ajaxLoader').hide();
			$scope.status = status;
		});
    }
    
	$scope.init = function () {
		$scope.getInfo();
	}
	
	$scope.init();
		
	$("#user").submit(function (event) {
		//disable the default form submission
		event.preventDefault();
		
		//grab all form data  
		var formData = $(this).serialize();
		
		var formData = new FormData($('#user')[0]);
		
		// Posting data to file
		$('.ajaxLoader').show();
		$.ajax({
			method  : "POST",
			url     : $('#contextPath').val() + '/userProfile/update',
			data    : formData, //forms user object
			async	: false,
			cache	: false,
			contentType	: false,
			processData	: false,
			success	: function(response) {
				$('.ajaxLoader').hide();
				response = JSON.parse(response);
				
				if(response['SUCCESS'] == 'TRUE') {
					$scope.init();
					if (response['UserObject'] != null) {
						$('#defaultDateFormat').val(response.UserObject.dateFormatUI);
						$('#i18nLang').val(response.UserObject.language);
					}
					services.showNotification(response['Message']);
				} else {
					services.showNotification(response['Message'],{ type: 'danger' });
				}
			},
			error 	: function(e) {
				console.log(e);
			}
		});
	});
}]);
