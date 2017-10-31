angular.module('PEAWorkbench').controller("tableauReportController", ['$scope','$compile', 'services', '$http' ,'$timeout','$location', '$rootScope',function($scope, $compile, services, $http, $timeout, $location, $rootScope){
	$scope.services = services;
	$scope.iFrameReportURL = '';
	
	$scope.getReportURL = function () {
		$('.ajaxLoader').show();
		
		var iFrameHTML = '<iframe src="<iFrameReportURL>" width="100%" height="700"></iframe>';
		
		$.ajax({
			type: 	'GET',
			url:	$('#contextPath').val() + '/getReportURL',
			data: 	{},
			success: function(response) {
				$('.ajaxLoader').hide();
				var response = JSON.parse(response);
				if(response['SUCCESS'] == 'TRUE') {
					$scope.iFrameReportURL = response['reportURL'];
					iFrameHTML = iFrameHTML.replace('<iFrameReportURL>', $scope.iFrameReportURL);
					var compiled = $compile(iFrameHTML)($scope);
        			$('#iFrameDivId').append(compiled);
				} 
				else {
					services.showNotification('Something went wrong, due to - ' + response['Message'], { type: 'danger' });
				}
			},
			error : function(e) {
				$('.ajaxLoader').hide();
				console.error(e);
			}
		});
	}
	
	$scope.init = function () {
		$scope.getReportURL();
	}
	
	$scope.init();
}]);

	angular.module('PEAWorkbench').controller("createDatasetsController", ['$scope','services', '$http' ,'$timeout','$location', '$rootScope',function($scope,services,$http, $timeout,$location,$rootScope){
		// create a blank object to hold our form information
		// $scope will allow this to pass between controller and view
		$scope.currentUser = $("#headerUserName").text() ;
		$scope.dataset = {datasetType : "EYNTK"};
		$scope.data;
		$scope.success = false;
		$scope.msg="Dataset created successfully";
		$scope.services =services;
		
		$scope.defaultDateFormat = $('#defaultDateFormat').val();
		
		$scope.formatDate = function(date){
			var dateOut = new Date(date);
			return dateOut;
		};
		$scope.exportDataset = function (datasetId) {
			window.location.href = $('#contextPath').val() + '/ViewDatasets/export?fltrDatasetId=' + datasetId;
		}
		$scope.redirectFromDataSetsWithName = function (option,source){
			$rootScope.exportDatasetsDataType= source;
			$rootScope.exportDatasetsDataset= option;
			$location.url('/exportDatasets');
		}
		
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
		
		$scope.init = function(){
			$('.ajaxLoader').show();
			$http({
				url: $('#contextPath').val() + "/getDatasetObjectList",
				method: "GET",
			}).success(function(data, status, headers, config) {
				var arr = $.map(data, function(el) { return el });
				$scope.data = arr;
				$('.ajaxLoader').hide();
			}).error(function(data, status, headers, config) {
				$scope.status = status;
				$('.ajaxLoader').hide();
			});
			$('input[name="daterange"]').daterangepicker({
				autoUpdateInput: false,
				locale: {
					cancelLabel: 'Clear'
				},
				opens:'center',
				format: $scope.defaultDateFormat,
				startDate : moment().subtract(29, 'days')
			});

			$('input[name="daterange"]').on('apply.daterangepicker', function(ev, picker) {
				$(this).val(picker.startDate.format($scope.defaultDateFormat) + ' - ' + picker.endDate.format($scope.defaultDateFormat));
			});
			$('input[name="daterange"]').on('cancel.daterangepicker', function(ev, picker) {
				$(this).val('');
			});
		}
		$('[name=datasetType]').change(function () {
			if($scope.dataset.datasetType == 'EYNTK') {
					$('input[name="daterange"]').daterangepicker({
						autoUpdateInput: false,
						locale: {
							cancelLabel: 'Clear',
							
						},
						opens:'center',
						format: $scope.defaultDateFormat,
						startDate : moment().subtract(29, 'days')
					});
				}
				else if($scope.dataset.datasetType == 'OPSO') {
					$('input[name="daterange"]').daterangepicker({
						autoUpdateInput: false,
						locale: {
							cancelLabel: 'Clear',
						},
						opens:'center',
						format: $scope.defaultDateFormat,
						maxDate: new Date(),
						startDate : moment().subtract(29, 'days')
					});
				}
			$('input[name="daterange"]').on('apply.daterangepicker', function(ev, picker) {
				$(this).val(picker.startDate.format($scope.defaultDateFormat) + ' - ' + picker.endDate.format($scope.defaultDateFormat));
			});
			$('input[name="daterange"]').on('cancel.daterangepicker', function(ev, picker) {
				$(this).val('');
			});
		});
		$scope.createDataset = function(){
			$('.ajaxLoader').show();
			var data = $('#createDatasetForm').serializeArray().reduce(function(obj, item) {
			    obj[item.name] = item.value;
			    return obj;
			}, {});
			var name = $("#datasetName").val();var isNamePresent = false;
			
			$.each($scope.data, function(key,value) {
				if(value.datasetName){
					if(name.toUpperCase().trim() == value.datasetName.toUpperCase().trim() && isNamePresent == false){
						isNamePresent = true;
						return false;
					}
				}
			});
			if(isNamePresent){
				services.showNotification("Dataset name already present.",{
					type: 'danger'});
				$('.ajaxLoader').hide();
			}
			else {
				$.ajax({
					type : 'POST',
					url : $('#contextPath').val() + '/createDataset',
					data : data,
					success : function(response) {
						//$("#dataSetCustomerSelect").multipleSelect("setSelects", ['[',']']);
						//$("#dataSetProductSelect").multipleSelect("setSelects", ['[',']']);
						$("#dateRange, #datasetName").val('');
						$scope.msg =response;
						$scope.dataset = {};
						$scope.dataset = {
								datasetType : "EYNTK",
								datasetName : ""
						};
						$(".help-block").addClass("ng-hide");
						$.ajax({
							type : 'GET',
							url : $('#contextPath').val() + '/getDatasetObjectList',
							success : function(dataset) {
								var dataset = JSON.parse(dataset);
								var arr = $.map(dataset, function(el) { return el });
								$scope.$apply(function() {
									$scope.data = arr;
								});
								services.showNotification(response);
								$('.ajaxLoader').hide();
								$(".has-error").removeClass("has-error");
								$(".help-block").addClass("ng-hide");
								
							},
							error : function(e) {
								console.log(e);
							}
						});
						
					},
					error : function(e) {
						console.log(e);
					}
				});
			}
		}
		$scope.init();
	}]);
	
	angular.module('PEAWorkbench').controller("exportDatasetsController", ['$scope','$compile', '$http', 'DTOptionsBuilder', 'DTColumnBuilder', 'services', '$rootScope','$location', function($scope, $compile, $http, DTOptionsBuilder, DTColumnBuilder, services,$rootScope,$location){
		$scope.AllexportDatasetBizRuleSelectlist=[];
		$scope.exportDatasetDatasetSelect=[];
		$scope.services = services;
		var currentPage = 'ViewDatasets';
		$scope[currentPage] = {};
		$scope.getProdCategoryBrandList =[];
		$scope.dataType = $rootScope.exportDatasetsDataType !== "" ?  $rootScope.exportDatasetsDataType : "EYNTK" ;
		$scope.dataType = $scope.dataType == undefined ? "EYNTK" : $scope.dataType;
		$scope.dataset = $rootScope.exportDatasetsDataset !== "" ?  $rootScope.exportDatasetsDataset : "" ;
		$scope.dataset = $scope.dataset == undefined ? undefined : $scope.dataset;
		
		$scope.defaultDateFormat = $('#defaultDateFormat').val();
		
		services.setup('exportDatasets');
		$scope.datasetTypeChange =function(value){
			var optResp= [];
			$('[name=exportDatasetDatasetType]').attr('disabled', 'disabled');
			if(value== "EYNTK"){
				$("#dateLabel").html('Shipment Start Date');
				$("#bizRuleLabel").html("Action Rules");
				$("#report").css("display","none");
				optResp = $scope.changeResponseOnType("EYNTK");
				optDatasetResp =$scope.changeDatasetResponseOnType("EYNTK");
			}else{
				$("#dateLabel").html('In-Store End Date');
				$("#bizRuleLabel").html("Exception Rules");
				$("#report").css("display","inline-block");
				optResp = $scope.changeResponseOnType("OPSO");
				optDatasetResp =$scope.changeDatasetResponseOnType("OPSO");
			}
			$('#exportDatasetBizRuleSelect').multipleSelect('disable');
			$('#exportDatasetDatasetSelect').multipleSelect('disable');
			$("#exportDatasetBizRuleSelect").empty();
			$("#exportDatasetDatasetSelect").empty();
			$.each(optResp, function (key, val) {
				var t = '', k = key, v = null;
				if (typeof val == 'object') {
					v = val['ruleShortDesc'];
					k = val['ruleId'];
					t = val['ruleDescription'];
				}
				$("#exportDatasetBizRuleSelect").append($('<option>', { 
					value: k,
					text : v,
					title: t
				}));
			});
			
			$.each(optDatasetResp, function (key, val) {
				var t = '', k = key, v = null;
				if (typeof val == 'object') {
					v = val['datasetName'];
					k = val['datasetId'];
					t = val['datasetName'];
				}
				$("#exportDatasetDatasetSelect").append($('<option>', { 
					value: k,
					text : v,
					title: t
				}));
			});
			var selectEl = $("#exportDatasetDatasetSelect");
			$('[name=exportDatasetDatasetType]').removeAttr('disabled');
			$("#exportDatasetBizRuleSelect").multipleSelect("refresh");
			$("#exportDatasetDatasetSelect").multipleSelect("refresh");
			$("#exportDatasetBizRuleSelect").multipleSelect('enable');
			$("#exportDatasetDatasetSelect").multipleSelect('enable');
			$("#exportDatasetDatasetSelect").multipleSelect("setSelects", [$(""+selectEl.selector+" option:first").val()]);	
		}
		
		$scope['OverLappingPromotionProduct'] = {};
		if($rootScope.exportDatasetDatasetType && $scope.dataset == undefined){
			$scope.dataType = $rootScope.exportDatasetDatasetType;
			$scope.datasetTypeChange($rootScope.exportDatasetDatasetType);
		}
		
		$scope.dataset;
		$scope[currentPage]['inputParam'] = [{
			id: 'exportDatasetDatasetType',
			type: 'radio',
			reqParam: 'fltrPromoType',
		},{
			id: 'exportDatasetDatasetSelect',
			type: 'select',
//			format: 'multiSelect',
			reqParam: 'fltrDatasetId',
			defaultValue: $scope.dataset
		}, {
			id: 'exportDatasetStatusSelect',
			type: 'select',
			format: 'multiSelect',
			reqParam: 'fltrStatus',
			defaultValue:$rootScope.exportDatasetStatusSelect
		}, {
			id: 'exportDatasetCustomerSelect',
			type: 'select',
			format: 'multiSelect',
			reqParam: 'fltrCustId',
			defaultValue:$rootScope.exportDatasetCustomerSelect
		},{
			id: 'exportDatasetBizRuleSelect',
			type: 'select',
			format: 'multiSelect',
			reqParam: 'fltrBizRuleId',
			defaultValue:$rootScope.exportDatasetBizRuleSelect
		},{
			id: 'exportDatasetInstoredate',
			type: 'daterangepicker',
			reqParam: 'fltrInStoreDate'
		},{
			id: 'exportDatasetBrandSelect',
			type: 'select',
			format: 'multiSelect',
			reqParam: 'fltrProdBrandSelect',
			defaultValue:$rootScope.exportDatasetBrandSelect
		},{
			id: 'exportDatasetCategorySelect',
			type: 'select',
			format: 'multiSelect',
			reqParam: 'fltrProdCategorySelect',
			defaultValue:$rootScope.exportDatasetCategorySelect
		}];
		
		$scope.applyFilter = function () {
			$("#ViewDatasetsTableId_processing").html('Processing...');
			$rootScope.exportDatasetDatasetType = $rootScope.exportDatasetsDataType =$("input[name='exportDatasetDatasetType']:checked").val();
			$rootScope.exportDatasetsDataset =  $('#exportDatasetDatasetSelect').val()[0];
			$rootScope.exportDatasetDatasetSelect = $('#exportDatasetDatasetSelect').val();
			$rootScope.exportDatasetCustomerSelect = $('#exportDatasetCustomerSelect').val();
			$rootScope.exportDatasetStatusSelect = $('#exportDatasetStatusSelect').val();
			$rootScope.exportDatasetBrandSelect = $('#exportDatasetBrandSelect').val();
			$rootScope.exportDatasetCategorySelect = $('#exportDatasetCategorySelect').val();
			$rootScope.exportDatasetBizRuleSelect = $("#exportDatasetBizRuleSelect").val();
			
			
			$rootScope.exportDatasetInstoreStartDate = new moment(new Date($('#exportDatasetInstoredate').data('daterangepicker').startDate)).format($scope.defaultDateFormat);
			$rootScope.exportDatasetInstoreEndDate = new moment(new Date($('#exportDatasetInstoredate').data('daterangepicker').endDate)).format($scope.defaultDateFormat); 
			
			if ($scope['dtInstance' + currentPage])
				$scope['dtInstance' + currentPage].reloadData();
		}
		
		
		$scope.clearFilter = function () {
			//$('#exportDatasetDatasetSelect').multipleSelect('uncheckAll');
			$('#exportDatasetStatusSelect').multipleSelect('uncheckAll');
			$('#exportDatasetCustomerSelect').multipleSelect('uncheckAll');
			$('#exportDatasetBizRuleSelect').multipleSelect('uncheckAll');
			$('#exportDatasetBrandSelect').multipleSelect('uncheckAll');
			$('#exportDatasetCategorySelect').multipleSelect('uncheckAll');
			$('input[name="datefilter"]').val('');
			$rootScope.exportDatasetDatasetType = "EYNTK";
			$rootScope.exportDatasetDatasetSelect = [];
			$rootScope.exportDatasetStatusSelect = [];
			$rootScope.exportDatasetCustomerSelect = [];
			$rootScope.exportDatasetBizRuleSelect = [];
			$rootScope.exportDatasetBrandSelect = [];
			$rootScope.exportDatasetCategorySelect = [];
			if($rootScope.exportDatasetBrandSelect = []){
				$('#exportDatasetBrandSelect').multipleSelect('disable');
			}
			$rootScope.viewExceptionsInstoreStartDate = null;
			$rootScope.viewExceptionsInstoreEndDate =null;
			$rootScope.exportDatasetsDataset ="";
			
			$($scope[currentPage]['inputParam']).each(function (idx, jo) { if(jo.defaultValue != null) jo.defaultValue = undefined; });
			
			/*if ($scope['dtInstance' + currentPage])
				$scope['dtInstance' + currentPage].reloadData();*/
		}
		
		$scope.report = function () {
			var inputParams = $("#exportDatasetDatasetSelect").val()[0];
			window.location.href = $('#contextPath').val() + '/OPSOReportPromo/export?fltrDatasetId=' + inputParams;
		}
		
		
		$scope.export = function () {
			var inputParams = $scope.services.getInputUrlParam($scope[currentPage]['inputParam']);
			window.location.href = $('#contextPath').val() + '/' + currentPage + '/export?' + inputParams;
		}
		
		var showOverlapping = function (){
			var msg="<div class='box-body'><div id='OverLappingPromotionProduct'></div>";
			title="View Overlapping Promotion";
			BootstrapDialog.show({
				title : title,
				id : 'save',
				message:msg,
				onshown:function(){
					$.ajax({
						type : 'GET',
						url :  $('#contextPath').val() + '/getOverlappingPromotionList?promoId='+$rootScope.promoId,
						success : function(response) {
							var obj = JSON.parse(response);
							var selectStr ="";var count = 0 ;var valueArray =[]; var selectFirstOpt =[];
							$.each(obj, function( index, value ) {
								valueArray = value.split('|');
								if (count == 0){
									$rootScope.promoOverId = index;
									count++;
									selectFirstOpt[0]=valueArray[1];
									selectFirstOpt[1]=valueArray[2];
								}
								
								selectStr = selectStr+ '<option value="'+index+'" data="'+valueArray[2]+'">'+valueArray[0]+' </option>';
							});
							$scope['OverLappingPromotionProduct']['headerHTML'] =   
								'<thead>' + 
								 '<tr>' + 
									'<th rowspan="1" style="width:112px;" ></th>' + 
									'<th colspan="1" style="border-bottom: 3px solid #f4f4f4;text-align:center" ><label id="preMonth">Promotion</label><br/><input type="text" class="form-control" style="width:200px;background-color:white;"  title="'+$scope.promoName+'" value="'+$scope.promoName+'" readonly /><br/><span>Ship Date:'+selectFirstOpt[0]+'</span></th>' + 
									'<th colspan="1" style="border-bottom: 3px solid #f4f4f4; text-align:center"><span id="currentMonth">Overlapping Promotion<br/><select id="selectOverlappingpromotion" class="form-control" style="width:200px;"  >'+selectStr+'</select></span><br/><span>Ship Date:</span><span id="ovarlappingDate">'+selectFirstOpt[1]+'</span></th>' + 
								'</tr> ' + 
								'<tr>' + 
									'<th style="width:100px;" ></th>' + 
									'<th style="width:200px;" ></th>' + 
									'<th style="width:200px;" ></th>' + 
								'</tr>' + 
								'</thead>'; 
							$rootScope.promoDrivId = $rootScope.promoId;
							$scope.services.datatableInit({
								tables: ['OverLappingPromotionProduct'],
								http: $http,
								scope: $scope,
								rootScope: $rootScope,
								compile: $compile,
								dtColumnBuilder: DTColumnBuilder,
								dtOptionsBuilder: DTOptionsBuilder,
								isTable: false,
								scrollY: '250',
								onDrawCallback: function () {
									$('#selectOverlappingpromotion').unbind('change');
									$('#selectOverlappingpromotion').change(function () {
										$("#ovarlappingDate").html($("#selectOverlappingpromotion option:selected").attr("data"));
										$rootScope.promoOverId = $(this).val();
										if ($scope['dtInstanceOverLappingPromotionProduct'])
											$scope['dtInstanceOverLappingPromotionProduct'].reloadData();
									});
								}
							});
						},
						error : function(e) {
							$('.ajaxLoader').hide();
							console.error(e);
						}
					});
					
				},
				buttons: [
				{
					id:'btn-cancel',
					label: 'Ok',
					action: function(dialogRef) {
						dialogRef.close();
						$('.modal-backdrop').remove();
					}
				}]
			});
		}
		
		
		$scope.initTable = function (opts) {
			$scope.services.datatableInit({
				tables: [currentPage],
				http: $http,
				scope: $scope,
				rootScope: $rootScope,
				compile: $compile,
				dtColumnBuilder: DTColumnBuilder,
				dtOptionsBuilder: DTOptionsBuilder,
				fixedColumns:{leftColumns: 2},
				colNotToSort: [0],
				order: [1, 'asc'],
				processingMsg: 'Select Filter ',
				chkBeforeSendReq: function () {
					if ($('#exportDatasetDatasetSelect').val() != null && $('#exportDatasetDatasetSelect').val().length > 0) {
						return true;
					}
					return false;
				},
				onDrawCallback: function () {
					$(".overlappingLink").unbind('click');
					$('.overlappingLink').click(function () {
						$rootScope.promoId = this.id.replace('overlappingLink_', '');
						$scope.promoName = $(this).parents('td').find('.promotionName').val();
						showOverlapping();
					});
				},
				isTable: false
			});
		}
		
		$scope.firstRequest = function () {
			if ($scope['dtInstance' + currentPage] != null)
				$scope['dtInstance' + currentPage].reloadData();
		} 
		var setBrandVal = function(){
			var selectBrand=$("#exportDatasetBrandSelect");
			selectBrand.multipleSelect("disable");
			selectBrand.find('option').remove();
			var brandList= [];
			if($("#exportDatasetCategorySelect").val()){
				$.each($("#exportDatasetCategorySelect").val(), function(index, item) {
					var obj= $scope.getProdCategoryBrandList.filter(function (element) { return element.category == item })
					$.each(obj[0].brand, function (brandKey, brandVal) {
						brandList.push(brandVal);
					});
				});
			}
			var updatedBrandList = $.unique(brandList).sort();
			$.each(updatedBrandList, function (brandKey, brandVal) {
				selectBrand.append($('<option>', { 
					value: brandVal,
					text : brandVal,
					title: brandVal
				}));
			});
			selectBrand.multipleSelect("refresh");
			if(updatedBrandList.length >0)
				selectBrand.multipleSelect("enable");
			if($rootScope.exportDatasetBrandSelect){
				selectBrand.multipleSelect("setSelects", $rootScope.exportDatasetBrandSelect);
			}
			selectBrand.multipleSelect("enable");
		}
		$scope.init =function(){
			var toggleCheckbox = function(element)
			{
				element.checked = !element.checked;
			}
			
			$('input[name="datefilter"]').daterangepicker({
				autoUpdateInput: false,
				locale: {
					cancelLabel: 'Clear'
				},
				opens:'center',
				format: $scope.defaultDateFormat,
				//maxDate: new Date(),
				startDate : moment().subtract(29, 'days')
			});
			$('input[name="datefilter"]').on('apply.daterangepicker', function(ev, picker) {
				$(this).val(picker.startDate.format($scope.defaultDateFormat) + ' - ' + picker.endDate.format($scope.defaultDateFormat));
			});

			$('input[name="datefilter"]').on('cancel.daterangepicker', function(ev, picker) {
				$(this).val('');
			});
			if($rootScope.viewExceptionsInstoreStartDate){
				$('input[name="datefilter"]').data('daterangepicker').setStartDate($rootScope.viewExceptionsInstoreStartDate);
			}
			if($rootScope.viewExceptionsInstoreEndDate){
				$('input[name="datefilter"]').data('daterangepicker').setEndDate($rootScope.viewExceptionsInstoreEndDate);
			}
			
			$('#exportDatasetDatasetSelect').on('change', function() {
				var selected = $("#exportDatasetDatasetSelect").multipleSelect("getSelects", "text");
				if(selected.length > 0 && $scope.exportDatasetDatasetSelect != null) {
					var datasetText = selected[0]; 
					var data =  $scope.exportDatasetDatasetSelect;
					var selValueObjArray =  data.filter(function(data) {return data.datasetName == datasetText});
					var selValueObj = selValueObjArray[0];
					if(selValueObj != null && selValueObj.startingDate == null && selValueObj.endingDate == null){
						$('input[name="datefilter"]').val('');
					}
					else {
						var startDate = new Date(selValueObj.startingDate);
						var endDate =new Date(selValueObj.endingDate);
						$('input[name="datefilter"]').data('daterangepicker').setStartDate(startDate);
						$('input[name="datefilter"]').data('daterangepicker').setEndDate(endDate);	
					}
//					var custArray = selValueObj.customerMappingId.split(',');
//					var prodArray = selValueObj.productMappingId.split(',');
					$('input[name="datefilter"]').data('daterangepicker').setStartDate(startDate);
					$('input[name="datefilter"]').data('daterangepicker').setEndDate(endDate);
//					$('#exportDatasetCustomerSelect').multipleSelect("setSelects", custArray);
					//$('#exportDatasetProductSelect').multipleSelect("setSelects", prodArray);
				}
			});
			
			$scope.services.select.setOptions({
				scope: $scope,
				selectId: 'exportDatasetDatasetSelect', 
				url: '/getDatasetObjectList', 
				valueKey: 'datasetName',
				keyKey:'datasetId',
				multipleSelect: {
					filter: true,
					placeholder: 'Select Dataset',
					width: '100%',
					single: true,
					defaultValue: $scope.dataset == undefined || ($scope.dataset.trim && $scope.dataset.trim().length < 1) ? undefined : [$scope.dataset],
					defaultFirstValueIfNotSelected:true
				},
				onAjaxSuccess : function(){
					var selectEl = $('#exportDatasetDatasetSelect');
					if($scope.dataset == undefined) {
						if (selectEl.multipleSelect("getSelects").length > 0)
							var interval = setInterval(function () {
								if ($("#ViewDatasetsTableId_processing").length > 0) {
									$("#ViewDatasetsTableId_processing").html('Processing...');
									clearInterval(interval);
								}
							}, 1000);
						
						selectEl.multipleSelect("setSelects", [$(""+selectEl.selector+" option:first").val()]);
					}
					if ($scope.firstRequest != null) {
						$scope.firstRequest();
						$scope.firstRequest = undefined;
					}
				},
				changeRespData : function(response){
					$scope.AllexportDatasetDatasetSelectlist =response;
					$.each(response,function(key, val){
						$scope.exportDatasetDatasetSelect.push(val);
					});
					var type= $scope.dataType;;
					var optResp = $scope.changeDatasetResponseOnType(type);
					return optResp;
				}
			}, true);
			
			$scope.changeDatasetResponseOnType =function(type){
				var optResp=[];
				$.each($scope.AllexportDatasetDatasetSelectlist, function (key, val) {
					if(val.datasetType == type){
						optResp.push(val);
					}
				});
				return optResp;
			} 
			
			$scope.services.select.setOptions({
				selectId: 'exportDatasetStatusSelect', 
				optionsData: {
					clean: 'Clean',
					exception: 'Exceptions'
				},
				inputParams: {},
				multipleSelect: {
					filter: true,
					placeholder: 'Select Status',
					width: '100%',
					defaultValue:$rootScope.exportDatasetStatusSelect
				}
			}, true);
			$scope.services.select.setOptions({
				selectId: 'exportDatasetBizRuleSelect',
				url: '/getBizRuleBeanMap',
				inputParams: {},
				keyKey: 'ruleId',
				valueKey: 'ruleShortDesc',
				valueTooltipKey: 'ruleDescription',
				multipleSelect: {
					filter: true,
					placeholder: 'Select Exception Rules',
					width: '100%',
					minimumCountSelected: 1,
					defaultValue:$rootScope.exportDatasetBizRuleSelect
				},
				changeRespData : function(response){
					$scope.AllexportDatasetBizRuleSelectlist =response;
					var type=$("input[name=exportDatasetDatasetType]").val();
					var optResp = $scope.changeResponseOnType(type);
					return optResp;
				}
			}, true);
			
			$scope.changeResponseOnType= function(type){
				var optResp=[];
				$.each($scope.AllexportDatasetBizRuleSelectlist, function (key, val) {
					if(val.ruleType == type){
						optResp.push(val);
					}
				});
				return optResp;
			} 
			
			$scope.services.select.setOptions({
				selectId: 'exportDatasetCustomerSelect', 
				url: '/getCustomerList',
				keyKey: 'customerId',
				valueKey: 'customerName',
				inputParams: {},
				multipleSelect: {
					filter: true,
					placeholder: 'Select Customers',
					width: '100%',
					minimumCountSelected: 1,
					defaultValue:$rootScope.exportDatasetCustomerSelect
				}
			}, true);
			$.ajax({
				type : 'GET',
				url : $('#contextPath').val() + "/getProdCategoryBrandList",
				success : function(response) {
				var resp = JSON.parse(response);
				$scope.getProdCategoryBrandList =resp;
					var selectCategory = $('#exportDatasetCategorySelect');
					var selectBrand = $('#exportDatasetBrandSelect')
					$.each(resp, function (key, val) {
						var t =  val.category, k = val.category, v = val.category;
						selectCategory.append($('<option>', { 
							value: k,
							text : v,
							title: t
						}));
					});
					selectCategory.multipleSelect({
						filter: true,
						placeholder: 'Select Category',
						width: '100%',
						minimumCountSelected: 1,
						defaultValue:$rootScope.exportDatasetCategorySelect
					});
					selectBrand.multipleSelect({
						filter: true,
						placeholder: 'Select Brand',
						width: '100%',
						minimumCountSelected: 1,
						
					});
					if(!$rootScope.exportDatasetBrandSelect){
						selectBrand.multipleSelect("disable");
					}
					if($rootScope.exportDatasetCategorySelect){
						selectCategory.multipleSelect("setSelects", $rootScope.exportDatasetCategorySelect);
						setBrandVal();
					}
					$("#exportDatasetCategorySelect").change(function() {
						setBrandVal();
					});
				},
				error : function(e) {
					console.error(e);
				}
			});
			$scope.initTable();
			var value = $scope.dataType;
			
			if(value== "EYNTK"){
				$("#dateLabel").html('Shipment Start Date');
				$("#bizRuleLabel").html("Action Rules");
				$("#report").css("display","none");
			}else{
				$("#dateLabel").html('In-Store End Date');
				$("#bizRuleLabel").html("Exception Rules");
				$("#report").css("display","inline-block");
			}
		}
		$scope.init();
	}]);
	
	