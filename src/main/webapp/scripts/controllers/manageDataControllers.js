angular.module('PEAWorkbench').controller("configureDataFilesController", ['$scope', 'services', function ($scope, services) {
	$scope.init = function () {
		$("#configureDataFilesTable").DataTable({
			"paging": true,
			"lengthChange": false,
			"searching": false,
			"ordering": true,
			"info": true,
			"autoWidth": false,
		});
	}
	$scope.init();
}]);
angular.module('PEAWorkbench').controller("fileUploadController", ['$scope', 'services', '$location', function ($scope, services, $location) {
	var sourceFilesType = {
		'SellOut': ['SellOut_yyyymmdd.txt'],
		'SellIn': ['SellIn_yyyymmdd.txt'],
		'Promotion': ['Promotion_yyyymmdd.txt']
		
	}
	
	$scope.setInitData = function() {
		$.each(sourceFilesType, function(key, value) {
			$('#sourcetype').append($('<option>', {
				value: key,
				text: key
			}));
		});
	};
	$('#sourcetype').change(function () {
		var strArr = [];
		if ($('#sourcetype').val() != null) strArr = sourceFilesType[$('#sourcetype').val()];
		/*if($('#sourcetype').val() == 'SelloutData') {
		strArr = ['SelloutData_yyyyMMdd.txt' ];
		}
		if($('#sourcetype').val()=='SellinData') {
		strArr = ['SellinData_ddMMyyyy.txt', 'SellinData_ddMMyyyy.txt'];
		}
		if($('#sourcetype').val()=='PromotionData') {
		strArr = ['PromotionData_ddMMyyyy.txt', 'PromotionData_ddMMyyyy.txt' ];
		}*/
		
		$('#fileNameFormat option[value!="selectProduct"]').remove();
		//			var select = document.getElementById("fileNameFormat");
		for (var i = 0; i < strArr.length; i++) {
			$('#fileNameFormat').append($('<option>', {
				value: strArr[i],
				text: strArr[i]
			}));
		}
	});
	$scope.init = function () {
		Dropzone.autoDiscover = false;
		$scope.isOverwrite = false;
		$scope.currentFileName = "";
		$scope.setInitData();
		$scope.fileDropzone = new Dropzone("div#holder", {
			url: $('#contextPath').val() + '/uploadFile',
			addRemoveLinks: true,
			maxFiles: 1,
			paramName: "uploadVersion",
			maxFilesize: 100, //MB
			acceptedFiles: '.txt,.csv',
			autoProcessQueue: false,
			dictCancelUpload: true,
			dictDefaultMessage: "Drop file here or click to upload.",
			removedfile: function (file) {
				$("button[type='submit']").prop('disabled', true);
				var _ref;
				return (_ref = file.previewElement) != null ? _ref.parentNode.removeChild(file.previewElement) : void 0;
			},
			clickable: true,
			drop: function (file, xhr, formData) {
				$("button[type='submit']").prop('disabled', false);
			},
			accept: function (file, done) {
				
				if (this.files[1] != null) {
					this.removeFile(this.files[0]);
				}
				if (file.size <= 0) {
					var msg = 'File is empty.';
					services.showNotification(msg, {
						type: 'danger'
					});
					this.removeFile(this.files[0]);
				}
				$scope.currentFileName = file.name;
				
				if (file.name.length > 0) {
					$("button[type='submit']").prop('disabled', false);
				}
				done();
			},
			sending: function (file, xhr, formData) {
				$('.ajaxLoader').show();
				var filename = file.name;
				var fileFormat = $("#fileNameFormat").val();
				var arrFirst = filename.split("_");
				var arrsecond = fileFormat.split("_");
				var fileDate = "";
				formData.append('sourceType', $("#sourcetype").val());
				formData.append('fileNameFormat', $("#fileNameFormat").val());
				formData.append('checkOverwrite', $scope.isOverwrite);
				if (arrFirst[1]) {
					fileDate = arrFirst[1].split(".")
				}
				if (arrFirst[0] == arrsecond[0] && isValidDate(fileDate[0])) {} else {
					$('.ajaxLoader').hide();
					var msg = 'File name format does not match';
					services.showNotification(msg, {
						type: 'danger'
					});
					$scope.fileDropzone.removeFile(file);
				}
				if (file.size < '99999999') {} else {
					$('.ajaxLoader').hide();
					var msg = 'File size should be below 100MB.';
					services.showNotification(msg, {
						type: 'danger'
					});
					$scope.fileDropzone.removeFile(file);
				}
			},
			success: function (response) {
				$('.ajaxLoader').hide();
				var msg = 'File uploaded successfully.';
				services.showNotification(msg);
				$scope.fileDropzone.removeAllFiles();
			}
		});
		$scope.fileDropzone.on("maxfilesexceeded", function (file) {
			this.removeAllFiles();
			this.addFile(file);
		});
	}

	function isValidDate(subject) {
		if ((subject.match(/^[0-9]{4}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])/)) && (subject.length == 8)) {
			return true;
		} else {
			return false;
		}
	}
	$("#upload").submit(function (event) {
		console.debug($scope.currentFileName+" "+$("#sourcetype").val());
		$.ajax({
			type: 'POST',
			url: $('#contextPath').val() + '/uploadFile',
			data: {
				'fileName': $scope.currentFileName,
				'sourceType': $("#sourcetype").val()
			},
			success: function (response) {
				if (response == 'File already exist.') {
					BootstrapDialog.show({
						title: 'File already exist',
						message: 'Do you want to replace it?',
						buttons: [{
							label: 'Yes',
							action: function (dialogItself) {
								$scope.isOverwrite = true;
								dialogItself.close();
								$scope.fileDropzone.processQueue($scope.isOverwrite);
							}
						}, {
							label: 'No',
							action: function (dialogItself) {
								dialogItself.close();
								$scope.fileDropzone.removeAllFiles();
							}
						}]
					});
				} else {
					$scope.fileDropzone.processQueue();
				}
			}
		})
	});
	$scope.init();
}]);
angular.module('PEAWorkbench').controller("monitorDataFileStatusController", ['$scope', '$compile', '$http', 'DTOptionsBuilder', 'DTColumnBuilder', 'services', '$location', '$rootScope', function ($scope, $compile, $http, DTOptionsBuilder, DTColumnBuilder, services, $location, $rootScope) {
	services.setup('monitorDataFileStatus');
	$scope.search = "";
	$scope.searchCol = "";
	var currentPage = 'MonitorDataFileStatus';
	$scope[currentPage] = {};
	$scope[currentPage]['inputParam'] = [{
		id: 'monitorDataFileStatusSourceType',
		type: 'rootscope',
		reqParam: 'fltrSourceType',
	}];
	$scope.$watch('$viewContentLoaded', function () {
		var path = $location.path().substring(1);
		var selectMenu = $(".sidebar-menu a[href='#" + path + "']").find('span').text();
		$(".sidebar-menu li.active").removeClass('active');
		$($(".sidebar-menu span:contains('" + selectMenu + "') ")[0]).parents('li').addClass('active');
		if ($location.search().source) {
			$scope.search = window.location.href.split('?source=')[1];
			$scope.searchCol = 1;
		}
	});
	$scope.openModal = function (index) {
		$("#ModalFail").modal("show");
	}
	$scope.services = services;
	var newTableHeight = $(".main-sidebar").height() - 270;
	$scope.services.datatableInit({
		tables: [currentPage],
		http: $http,
		scope: $scope,
		rootScope: $rootScope,
		compile: $compile,
		dtColumnBuilder: DTColumnBuilder,
		dtOptionsBuilder: DTOptionsBuilder,
		isTable: false,
		scrollY: newTableHeight,
		order: [3, 'desc'],
		onDrawCallback: function () {
			$rootScope.monitorDataFileStatusSourceType = "";
		}
	});
	//$("input[type='search']").val($scope.search).trigger('keyup');
}]);
angular.module('PEAWorkbench').controller("manageLookupTablesController", ['$scope', '$rootScope', '$compile', '$http', 'DTOptionsBuilder', 'DTColumnBuilder', 'services', '$timeout', function ($scope, $rootScope, $compile, $http, DTOptionsBuilder, DTColumnBuilder, services, $timeout) {
	$scope.services = services;
	$scope['PromoMechanic'] = {};
	$scope['PromoMechanic']['inputParam'] = [{
		id: 'promoMechanicDatepicker',
		type: 'datepicker',
		format: 'month',
		reqParam: 'fltrMonth',
		defaultValue: 'Aug'
	}, {
		id: 'promoMechanicDatepicker',
		type: 'datepicker',
		format: 'year',
		reqParam: 'fltrYear'
	}, {
		id: 'customerPromoMechanicSelect',
		type: 'select',
		format: 'multiSelect',
		reqParam: 'fltrCustomer',
		defaultValue: $scope.fltrAccount
	}];
	$scope['PromoMechanic']['columnCls'] = {
		9: 'display-none'
	}
	$scope.promoMechanicApplyFilter = function () {
		$scope.viewPromoMechanicChkBeforeSendReq = undefined;
		$("#PromoMechanicTableId_processing").html('Processing...');
		if ($scope['dtInstancePromoMechanic']) $scope['dtInstancePromoMechanic'].reloadData($.noop, false);
	}
	$scope.savePromoMechanic = function () {
		$('.ajaxLoader').show();
		var tableName = 'PromoMechanic',
			btnIdSeperator = '|=',
			data = {};
		$('[id*="' + tableName + btnIdSeperator + 'savebtn' + btnIdSeperator + '"]').each(function (key, value) {
			var currentSaveBtn = this;
			var key = currentSaveBtn.id.split(btnIdSeperator)[3];
			var rowId = currentSaveBtn.id.split(btnIdSeperator)[2];
			var inputCol = {};
			$('[class^=' + tableName + '-ef-' + rowId + ']').each(function (i, el) {
				if (el.value != null && el.value.trim().length > 0) {
					inputCol[$(el).attr('class').split(' ')[0].split('-')[4]] = el.value.trim();
				}
			});
			data[key] = inputCol;
		});
		$.ajax({
			type: 'POST',
			url: $('#contextPath').val() + '/datatable/savePromoMechanic',
			data: {
				tableName: tableName,
				editData: JSON.stringify(data)
			},
			success: function (response) {
				$scope['dtInstance' + tableName].reloadData($.noop, false);
				$('.ajaxLoader').hide();
				var response = JSON.parse(response);
				if (response['SUCCESS'] || response['SUCCESS'] == 'true') {
					$scope.services.showNotification(response['Message']);
				} else {
					$scope.services.showNotification(response['Message'], {
						type: 'danger'
					});
				}
			},
			error: function (e) {
				$scope['dtInstance' + tableName].reloadData($.noop, false);
				$('.ajaxLoader').hide();
				console.error(e);
			}
		});
	}
	$scope.init = function (opts) {
		$("#customerGroupingPromoMechanicSelectdiv").on("click", "input[type=radio]", function () {
			$scope.setCustomerPromoSelectList();
		});
		$('#promoMechanicDatepicker').datepicker({
			format: 'M-yyyy',
			viewMode: 'months',
			minViewMode: 'months',
			endDate: '+0d',
			autoclose: true
		});
		var d = new Date();
		var currMonth = d.getMonth() + 1;
		var currYear = d.getFullYear();
		$('#promoMechanicDatepicker').datepicker('setDate', '' + currMonth + '-' + currYear);
		$("#promoMechanicDatepicker").on("changeDate", function (dateObj) {
			$scope.setCustomerPromoSelectList();
		});
		$scope.services.datatableInit({
			//'MarketBusiness', 'CaseConverter', 
			tables: ['CustomerGroupings', 'CustomerChannel', 'SelloutDataPref'],
			http: $http,
			scope: $scope,
			rootScope: $rootScope,
			compile: $compile,
			dtColumnBuilder: DTColumnBuilder,
			dtOptionsBuilder: DTOptionsBuilder,
			isTable: true
		});

		$scope.viewPromoMechanicChkBeforeSendReq = function () {
			return false;
		}
		$scope.services.datatableInit({
			//'MarketBusiness', 'CaseConverter', 
			tables: ['PromoMechanic'],
			http: $http,
			scope: $scope,
			rootScope: $rootScope,
			compile: $compile,
			dtColumnBuilder: DTColumnBuilder,
			dtOptionsBuilder: DTOptionsBuilder,
			isTable: true,
			chkBeforeSendReq: function () {
				if ($scope.viewPromoMechanicChkBeforeSendReq) return $scope.viewPromoMechanicChkBeforeSendReq();
				return true;
			},
			processingMsg: $rootScope.translate('Select Filter ', 'Select Filter '),
		});
		$scope.services.select.setOptions({
			selectId: 'customerGroupingPromoMechanicSelect',
			url: '/OpsoCustomerGroups',
			inputParams: {},
			multipleSelect: {
				filter: true,
				placeholder: 'Select Customer Grouping',
				width: '100%',
				minimumCountSelected: 1,
				single: true,
				defaultFirstValueIfNotSelected: true
			},
			onAjaxSuccess: function () {
				$("#customerPromoMechanicSelect").multipleSelect();
				$scope.setCustomerPromoSelectList();
			},
		}, true);
		$scope.setCustomerPromoSelectList = function () {
				$("#customerPromoMechanicSelect").multipleSelect("disable");
				$("#promoMechanicApplyBtn").prop("disabled", true);
				var months = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];
				var selMonth = months[new Date($('#promoMechanicDatepicker').data('datepicker').viewDate).getMonth()];
				var selYear = new Date($('#promoMechanicDatepicker').data('datepicker').viewDate).getFullYear();
				var selOpsoCustomerGroup = $("#customerGroupingPromoMechanicSelect").multipleSelect("getSelects", "text").toString();
				$scope.services.select.setOptions({
					selectId: 'customerPromoMechanicSelect',
					url: '/getCustomerList',
					keyKey: 'customerId',
					valueKey: 'customerName',
					inputParams: {
						Month: selMonth,
						Year: selYear,
						OpsoCustomerGroup: selOpsoCustomerGroup
					},
					multipleSelect: {
						filter: true,
						placeholder: 'Select Customer',
						width: '100%',
						minimumCountSelected: 1,
						checkAll: true
					},
					onAjaxSuccess: function () {
						$("#customerPromoMechanicSelect").multipleSelect("enable");
						$("#promoMechanicApplyBtn").prop("disabled", false);
					}
				}, true);
			}
			/*$scope.services.select.setOptions({
				selectId: 'customerPromoMechanicSelect', 
				url: '/getCustomerList', 
				keyKey: 'customerId',
				valueKey: 'customerName',
				inputParams: {},
				multipleSelect: {
					filter: true,
					placeholder: 'Select Customer',
					width: '100%',
					minimumCountSelected: 1,
					checkAll: true
				}
			}, true);*/
	}
	$scope.navTabClick = function (tabName, $event) {
		$($event.target).parents('ul').next(".tab-content").children().removeClass('active');
		$("#" + tabName).addClass('active');
		if ($scope['dtInstance' + tabName]) {
			$("#" + tabName + " .dataTables_scroll").css('visibility', 'hidden');
			$scope['dtInstance' + tabName].dataTable.fnAdjustColumnSizing();
		} else if (tabName == "OPSO") {
			setTimeout(function () {
				$("#OPSOTabs li.active").trigger('click');
			}, 10);
			if (tabName == "PromoMechanic") {
				$(".OPSOAdd").hide();
			}
			$("#PromoMechanic .dataTables_scroll").css('visibility', 'hidden');
			$scope['dtInstancePromoMechanic'].dataTable.fnAdjustColumnSizing();
		} else if (tabName == "EYNTK") {
			setTimeout(function () {
				$("#EYNTKTabs li.active").trigger('click');
			}, 10);
		}
		if (tabName == "PromoMechanic") {
			$(".OPSOAdd").hide();
		} else if (tabName == "CustomerMapping") {
			$(".OPSOAdd").show();
		}
	};
	$scope.exportFile = function () {
		var topActiveTab = $("#manageLookupTablesTabs .active a").text();
//		var currentDivID = $("#" + topActiveTab + " ul.nav-tabs li.active a").attr('href');
		var currentDivID = $("#manageLookupTablesTabs .active a").attr('href');
		window.location.href = $('#contextPath').val() + '/datatable/' + currentDivID + '/export';
	}
	$scope.add = function () {
		
		var topActiveTab = $("#manageLookupTablesTabs .active a").attr('href');
	//	var currentDivID = $("#" + topActiveTab + " ul.nav-tabs li.active a").attr('href');
		var currentDivID = $("#manageLookupTablesTabs .active a").attr('href');
		var msg = "",
			title = "",
			type = "EYNTK";
	
		switch (currentDivID) {
			case 'CustomerGroupings':
				type = 'EYNTK';
				//										
				msg = "<div class='box-body'>" +
					"<form  class='form-horizontal " + currentDivID + "form' autocomplete='off'  >" +
					"<div class=''><div class='form-group row'>" +
					"<span style='color: red' class='col-sm-12' id='lookup-add-error-msg'></span>" +
					"</div>" +
					"<div class='form-group row' >" +
					"<label for='account' class='col-sm-2 control-label'>Account</label>" +
					"<div class='col-sm-10'><select class='form-control' name='Account' id='account' placeholder='Account'></select></div>" +
					"</div>" +
					"<div class='form-group row'>" +
					"<label for='sector' class='col-sm-2 control-label'>Sector</label>" +
					"<div class='col-sm-10'><select class='form-control' name='Sector' id='sector' placeholder='Sector'></select></div>" +
					"</div>" +
					"<div class='form-group row'>" +
					"<label for='ad' class='col-sm-2 control-label'>User Groups</label>" +
					"<div class='col-sm-10'><input type='text' class='form-control' id='AD' name='AD' placeholder='AD' maxlength ='64'></div>" +
					"</div>" +
					"</form>" +
					"</div>";
				title = "Add Customer Grouping";
				break;
			case 'CustomerChannel':
				type = 'EYNTK';
				//										
				msg = "<div class='box-body'>" +
					"<form  class='form-horizontal " + currentDivID + "form' autocomplete='off'  >" +
					"<div class=''><div class='form-group row'>" +
					"<span style='color: red' class='col-sm-12' id='lookup-add-error-msg'></span>" +
					"</div>" +
					"<div class='form-group row' >" +
					"<label for='account' class='col-sm-2 control-label'>Customer</label>" +
					"<div class='col-sm-10'><select class='form-control' name='CustomerName' id='customer' placeholder='CustomerName'></select></div>" +
					"</div>" +
					"<div class='form-group row'>" +
					"<label for='sector' class='col-sm-2 control-label'>Channel</label>" +
					"<div class='col-sm-10'><input type='text' class='form-control' id='channel' name='channel' placeholder='channel' maxlength ='64'></div>" +
					"</div>" 
					 +
					"</form>" +
					"</div>";
				title = "Add Customer Channel";
				break;
			case 'SelloutDataPref':
				type = 'EYNTK';
				msg = 	"<div class='box-body'>" +
							"<form  class='form-horizontal " + currentDivID + "form' autocomplete='off'>" +
								"<div class=''>"+
									"<div class='form-group row'>" +
										"<span style='color: red' class='col-sm-12' id='lookup-add-error-msg'></span>" +
									"</div>" +
								
								"<div class='form-group row' >" +
									"<label for='account' class='col-sm-2 control-label'>Customer</label>" +
									"<div class='col-sm-10'><select class='form-control' name='Customer' id='customer' placeholder='customer'></select></div>" +
								"</div>" +
									
								"<div class=''>"+
									"<div class='form-group row'>" +
										"<label for='position' class='col-sm-2 control-label'>Sell Out Source</label>" +
										"<div class='col-sm-10'>"+
											"<select class='form-control' name='SellOutSource' id='SellOutSource' placeholder='Sell Out Source'>"+
												"<option value='Nielsen'>Nielsen</option>" +
												"<option value='IRI - Advantage'>IRI - Advantage</option>" +
											"</select>"+
									"</div>" +
								"</div>" +
								"<div class='form-group row'>" +
									"<label for='limit' class='col-sm-2 control-label'>Preference</label>" +
									"<div class='col-sm-10'>"+
										"<input type='text' class='form-control' name='Preference' id='preference' onkeypress='return event.charCode >= 48 && event.charCode <= 57' placeholder='Limit' maxlength ='13' >"+
									"</div>" +
								"</div>" +
							"</form>" +
						"</div>";
				title = "Sellout Data Pref";
				break;
//			case 'MarketBusiness':
//				type = 'EYNTK';
//				msg = "<div class='box-body'><form  class='form-horizontal MarketBusinessform' autocomplete='off'><div class='form-group row'><label for='market' class='col-sm-2 control-label'>Market</label><div class='col-sm-10'><input type='text' class='form-control' id='market' name='Market' placeholder='Market' maxlength ='128'></div></div><div class='form-group row'><label for='Business' class='col-sm-2 control-label'>Business</label><div class='col-sm-10'><input type='text' class='form-control' name='Business'  id='business' placeholder='Business' maxlength ='128'></div></div></div>";
//				title = "Add Market To Business";
//				break;
//			case 'CaseConverter':
//				type = 'EYNTK';
//				msg = "<div class='box-body'><form  class='form-horizontal CaseConverterform' autocomplete='off'><div class='form-group row'><label for='productName' class='col-sm-4 control-label'>Product</label><div class='col-sm-8'><input type='text' class='form-control' name='Product' id='product' placeholder='Product' maxlength ='128'></div></div><div class='form-group row'><label for='businessType' class='col-sm-4 control-label'>Business Type</label><div class='col-sm-8'><input type='text' class='form-control' name='BusinessType' id='businessType' placeholder='Business Type' maxlength ='128'></div></div><div class='form-group row'><label for='convert' class='col-sm-4 control-label'>Convert</label><div class='col-sm-8'><input type='text' class='form-control' name='isConvert' id='convert' placeholder='Convert' maxlength ='3'></div></div></form></div>";
//				title = "Add Case Converter";
//				break;
//			case 'CustomerGrouping':
//				type = 'OPSO';
//				//										
//				msg = "<div class='box-body'>" +
//					"<form  class='form-horizontal CustomerMappingform' autocomplete='off'>" +
//					"<div class=''><div class='form-group row'>" +
//					"<span style='color: red' class='col-sm-12' id='lookup-add-error-msg'></span>" +
//					"</div>" +
//					"<div class='form-group row'>" +
//					"<label for='col1' class='col-sm-2 control-label'>Customer</label>" +
//					"<div class='col-sm-10'><select class='form-control'  name='Customer' id='customer' placeholder='Customer'></select></div>" +
//					"</div>" +
//					"<div class='form-group row'>" +
//					"<label for='Groupings' class='col-sm-2 control-label'>Groupings</label>" +
//					"<div class='col-sm-10'><input type='text' class='form-control' name='Groupings' id='groupings' placeholder='Groupings' maxlength ='128'></div>" +
//					"</div>" +
//					"</form>" +
//					"</div>";
//				title = "Add Customer Mapping";
//				break;
			default:
				msg = "Not applicable to this tab!";
		}
		BootstrapDialog.show({
			title: title,
			id: 'save',
			message: msg,
			onshown: function (dialog) {
				$('.box-body form input').keyup(function () {
					if ($(this).val().trim() == "") {
						$(this).css("border-color", "red").addClass("invalid");
						return false;
					} else {
						$(this).css("border-color", "#d2d6de").removeClass("invalid");
					}
				});
				
				switch (currentDivID) {
					case 'CustomerGroupings':
						$.ajax({
							type: 'GET',
							url: $('#contextPath').val() + '/getLookTableAccounts',
							data: {
								TYPE: type
							},
							success: function (response) {
								var jResp = JSON.parse(response);
								if (jResp.list != undefined) {
									$.each(jResp.list, function (idx, v) {
										$('#account').append($('<option>', {
											value: v,
											text: v
										}));
									});
								}
							}
						});

						$.ajax({
							type: 'GET',
							url: $('#contextPath').val() + '/getLookTableSectors',
							data: {

							},
							success: function (response) {
								var jResp = JSON.parse(response);
								if (jResp.list != undefined) {
									$.each(jResp.list, function (idx, v) {
										$('#sector').append($('<option>', {
											value: v,
											text: v
										}));
									});
								}
							}
						});
						break;
					case 'CustomerChannel':
						$.ajax({
							type: 'GET',
							url: $('#contextPath').val() + '/getLookTableAccounts',
							data: {
								TYPE: type
							},
							success: function (response) {
								var jResp = JSON.parse(response);
								if (jResp.list != undefined) {
									$.each(jResp.list, function (idx, v) {
										$('#customer').append($('<option>', {
											value: v,
											text: v
										}));
									});
								}
							}
						});
						break;
					case 'SelloutDataPref':
						$.ajax({
							type: 'GET',
							url: $('#contextPath').val() + '/getLookTableAccounts',
							data: {
								TYPE: type
							},
							success: function (response) {
								var jResp = JSON.parse(response);
								if (jResp.list != undefined) {
									$.each(jResp.list, function (idx, v) {
										$('#customer').append($('<option>', {
											value: v,
											text: v
										}));
									});
								}
							}
						});
						break;
					default:
						// do nothing
				}
				$('select').select2();
			},
			buttons: [{
				id: 'btn-save',
				label: 'Add',
				autospin: false,
				action: function (dialogRef) {
					$('.' + currentDivID + 'form input').each(function (index) {
						if ($(this).val().trim() == "") {
							$(this).css("border-color", "red").addClass("invalid");
						} else {
							$(this).css("border-color", "#d2d6de").removeClass("invalid");
						}
					});
					if ($(".invalid").length == 0) {
						$('.ajaxLoader').show();
						
						var data = $('.' + currentDivID + 'form').serializeArray().reduce(function (obj, item) {
							obj[item.name] = item.value;
							return obj;
						}, {});
						
						$.ajax({
							type: 'POST',
							url: $('#contextPath').val() + '/datatable/' + currentDivID + '/add',
							data: {
								editData: JSON.stringify(data)
							},
							success: function (response) {
								var message = 'Record Added Successfully.',
									isSuccess = true;
								if (response != null) {
									console.debug(response);
									var resp = JSON.parse(response);
									message = resp['Message'];
									if (resp['SUCCESS'] != null && !resp['SUCCESS'])
										isSuccess = false;
								}
								$('.ajaxLoader').hide();
								if (isSuccess) {
									dialogRef.close();
									$scope.services.showNotification(message);
									$('.modal-backdrop').remove();
									$scope['dtInstance' + currentDivID]._renderer.rerender();
								} else {
									$('#lookup-add-error-msg').html(message);
								}
							}
						})
					}
				}
			}, {
				id: 'btn-cancel',
				label: 'Cancel',
				action: function (dialogRef) {
					dialogRef.close();
					$('.modal-backdrop').remove();
				}
			}]
		});
	}
	$scope.init();
}]);
angular.module('PEAWorkbench').controller("mappingDataController", ['$scope', '$compile', '$http', 'DTOptionsBuilder', 'DTColumnBuilder', 'services', '$timeout', '$rootScope', '$q', function ($scope, $compile, $http, DTOptionsBuilder, DTColumnBuilder, services, $timeout, $rootScope, $q) {
	$scope.services = services;
	$scope.intProductList = {};
	$scope.brandList = {};
	$scope.formList = {};
	$scope.subFormList = {};
	$scope.CustList = {};
	$scope.intProdlist;
	var mappingDataProductPage = 'MappingData';
	var mappingDataCustomerPage = 'CustMappingData';
	$scope[mappingDataProductPage] = {};
	$scope[mappingDataCustomerPage] = {};
	$rootScope.mappingDataDimensionSelect = $rootScope.mappingDataDimensionSelect ? $rootScope.mappingDataDimensionSelect : 'customer';
	//		var mappingDataProductPage = 'MappingData';
	//		var mappingDataCustomerPage = 'CustMappingData';
	$scope[mappingDataProductPage] = {};
	$scope[mappingDataCustomerPage] = {};
	$scope[mappingDataProductPage]['inputParam'] = [{
		id: 'mappingType',
		type: 'radio',
		reqParam: 'fltrMapping',
	}, {
		id: 'mappingDataSourceSelect',
		type: 'select',
		reqParam: 'fltrSourceId',
	}, {
		id: 'mappingDataCategoriesSelect',
		type: 'select',
		format: 'multiSelect',
		reqParam: 'fltrCategory',
	}, {
		id: 'mappingDataSourceSelect',
		type: 'select',
		byText: true,
		reqParam: 'fltrSource',
	}];
	$scope[mappingDataCustomerPage]['inputParam'] = [{
		id: 'mappingType',
		type: 'radio',
		reqParam: 'fltrMapping',
	}, {
		id: 'mappingDataSourceSelect',
		type: 'select',
		reqParam: 'fltrSourceId',
	}];
	$scope.setPageSection = function (opts) {
		var opts = opts || {};
		if ($("#mappingDataDimensionSelect").multipleSelect("getSelects")[0] == 'customer') {
			if ($scope['dtInstance' + mappingDataCustomerPage] != null && $scope['dtInstance' + mappingDataCustomerPage].dataTable != null) $scope['dtInstance' + mappingDataCustomerPage].dataTable.fnAdjustColumnSizing();
			$(".product").hide();
			$(".customer").show();
		} else {
			if (!opts.forceInit && $scope['dtInstance' + mappingDataProductPage] != null && $scope['dtInstance' + mappingDataProductPage].dataTable != null) {
				$scope['dtInstance' + mappingDataProductPage].dataTable.fnAdjustColumnSizing();
			} else {
				var headerHTML =
					'<thead>' +
						'<tr>' +
							'<th style="width:200px;"><span id="' + mappingDataProductPage + 'ExtProdName">External Product Name</span>c</th>' +
							'<th style="width:200px;"><span id="' + mappingDataProductPage + 'IntProdCode">Internal Product Code</span></th>' +
							'<th style="width:200px;"><span id="' + mappingDataProductPage + 'IntProdName">Internal Product Name</span></th>' +
							'<th style="width:70px;">Confidence</th>' +
							'<th style="width:50px;">Actions</th>' +
						'</tr>' +
					'</thead>';

				opts.colNotToSort = [4];
				//opts.columnsToExclude = ['ExtProductName'];
//				$scope[mappingDataProductPage]['headerHTML'] = headerHTML;
				$scope.services.datatableInit({
					tables: [mappingDataProductPage],
					http: $http,
					scope: $scope,
					rootScope: $rootScope,
					compile: $compile,
					colNotToSort: opts.colNotToSort || [],
					dtColumnBuilder: DTColumnBuilder,
					dtOptionsBuilder: DTOptionsBuilder,
					columnsToExclude: opts.columnsToExclude || [],
					onDrawCallback: function () {
						
						// Change column name as per mapping type selection
						// Change column name as per mapping type selection
						if ('unmapped' == $('[name=mappingType]:checked').val()) {
							$($('th[aria-label*="Internal Product Code"]')[0]).html('Suggested Product Code');
							$($('th[aria-label*="Internal Product Name"]')[0]).html('Suggested Product Name');
							$($('table[dt-instance="dtInstance' + mappingDataProductPage + '"] th[aria-label*="Confidence"]')[0]).html('Confidence');
						}
						else {
							$($('th[aria-label*="Internal Product Code"]')[0]).html('Internal Product Code');
							$($('th[aria-label*="Internal Product Name"]')[0]).html('Internal Product Name');
							$($('table[dt-instance="dtInstance' + mappingDataProductPage + '"] th[aria-label*="Confidence"]')[0]).html('Share Weightage');
						}
						
						$("#prodSourceLbl").text($("#mappingDataSourceSelect").multipleSelect("getSelects", "text")[0]);
						$(".edit").unbind('click');
						$('.edit').click(function () {
							$scope.editClick($(this));
						});
						$('.prod-unmapped').confirmation({
							singleton: true,
							onConfirm: function () {
								$('.ajaxLoader').show();
								$.ajax({
									type: 'POST',
									url: $('#contextPath').val() + '/' + mappingDataProductPage + '/unmapExtMapping',
									data: {
										'extProdId': $(this).attr('data-id'),
										'uniProdId': $(this).attr('intProd-id')
									},
									success: function (response) {
										$scope['dtInstance' + mappingDataProductPage].reloadData($.noop, false);
										$('.ajaxLoader').hide();
										var response = JSON.parse(response);
										$scope.services.showNotification(response['Message']);
									},
									error: function (e) {
										$scope['dtInstance' + mappingDataProductPage].reloadData($.noop, false);
										$('.ajaxLoader').hide();
										console.error(e);
									}
								});
							}
						});
						
						//Action to map suggested mapping
						$('.prod-map').confirmation({
							singleton: true,
							onConfirm: function () {
								$('.ajaxLoader').show();
								$.ajax({
									type: 'POST',
									url: $('#contextPath').val() + '/' + mappingDataProductPage + '/mapExtMapping',
									data: {
										'extProdId': $(this).attr('data-id'),
										'intProdId': $(this).attr('intProd-id')
									},
									success: function (response) {
										$scope['dtInstance' + mappingDataProductPage].reloadData($.noop, false);
										$('.ajaxLoader').hide();
										var response = JSON.parse(response);
										$scope.services.showNotification(response['Message']);
									},
									error: function (e) {
										$scope['dtInstance' + mappingDataProductPage].reloadData($.noop, false);
										$('.ajaxLoader').hide();
										console.error(e);
									}
								});
							}
						});
						
						$('.prod-ignored').confirmation({
							singleton: true,
							onConfirm: function () {
								$('.ajaxLoader').show();
								$.ajax({
									type: 'POST',
									url: $('#contextPath').val() + '/' + mappingDataProductPage + '/markIgnored',
									data: {
										'extProdId': $(this).attr('data-id')
									},
									success: function (response) {
										$scope['dtInstance' + mappingDataProductPage].reloadData($.noop, false);
										$('.ajaxLoader').hide();
										var response = JSON.parse(response);
										$scope.services.showNotification(response['Message']);
									},
									error: function (e) {
										$scope['dtInstance' + mappingDataProductPage].reloadData($.noop, false);
										$('.ajaxLoader').hide();
										console.error(e);
									}
								});
							}
						})
					},
					isTable: false
				});
			}
			$(".product").show();
			$(".customer").hide();
		}
	}
	
	$scope.fnSortObjArray = function (column) {
		return function(o1, o2) {
	        return o1[column].toLowerCase() > o2[column].toLowerCase() ? 1 : o1[column].toLowerCase() < o2[column].toLowerCase() ? -1 : 0;
	    }
	}
	
	$scope.applyFilter = function () {
		var isKantarProduct = false;
		if ($("#mappingDataDimensionSelect").multipleSelect("getSelects")[0] == 'product') {
			if ($('#mappingDataSourceSelect').multipleSelect("getSelects")[0] == 2) {
				isKantarProduct = true;
			}
			if ($scope['dtInstance' + mappingDataProductPage] != null) {
				$scope['dtInstance' + mappingDataProductPage].dataTable.fnDestroy();
				$scope['dtInstance' + mappingDataProductPage] = null;
			}
			$('#' + mappingDataProductPage + 'TableId').remove();
		}
		$scope.setPageSection({
			kantar: isKantarProduct,
			forceInit: isKantarProduct
		});
	}
	$scope.export = function () {
		var activeTab = mappingDataProductPage;
		if ($("#mappingDataDimensionSelect").multipleSelect("getSelects")[0] == 'customer') {
			activeTab = mappingDataCustomerPage;
		}
		var inputParams = $scope.services.getInputUrlParam($scope[activeTab]['inputParam']);
		window.location.href = $('#contextPath').val() + '/' + activeTab + '/export?' + inputParams;
	}
	$('.ajaxLoader').show();
	$http({
		url: $('#contextPath').val() + "/getMappedDataMetaData",
		method: "GET",
	}).success(function (data, status, headers, config) {
		$scope.intProdlist = data.Product;
		$scope.intCustlist = data.Customer;
		$scope.sourceCategoryMap = data.Categories;
		$scope.setCategoryCombo({
			elSourceSelect: $("#mappingDataSourceSelect")
		});

		if ($scope.intProdlist != null) {
			$scope.intProductList = $scope.intProdlist;
			//			angular.forEach($scope.intProdlist, function(value, key) {
			//				
			//			});

			//			angular.forEach($scope.intProdlist.Brand, function(value, key) {
			//				//$scope.brandList[value.key]=value.key ;
			//				$scope.brandList[value.key] = {
			//					'key': value.key,
			//					'value': value.key,
			//					'data': value.value
			//				};
			//				if (key == 0) {
			//					angular.forEach(value.value, function(formValue, formKey) {
			//						$scope.formList[formValue] = formValue;
			//						if (formKey == 0) {
			//							var searchText = value.key + '|' + formValue;
			//							var obj = $.grep($scope.intProdlist.BrandForm, function(k) {
			//								return k.key == searchText;
			//							});
			//							if (obj.length > 0 && obj[0].value.length > 0) {
			//								angular.forEach(obj[0].value, function(subFormValue, subFormKey) {
			//									$scope.subFormList[subFormValue] = subFormValue;
			//								});
			//							}
			//						}
			//					});
			//				}
			//			});
		}
		
		if ($scope.intCustlist != null) {
			$scope.intCustlist.sort($scope.fnSortObjArray('customerName'));
			
			angular.forEach($scope.intCustlist, function (value, key) {
				$scope.CustList[value.customerId] = value.customerName + ' (' + value.customerCode + ')';
			});
		}
		$('.ajaxLoader').hide();
	});
	$scope.services.select.setOptions({
		selectId: 'mappingDataSourceSelect',
		url: '/getExtSourceList',
		inputParam: {},
		multipleSelect: {
			filter: true,
			placeholder: 'Select Source',
			width: '100%',
			//defaultValue:$rootScope.mappingDataSourceSelect,
			defaultFirstValueIfNotSelected: true,
			single: true,
		},
		onAjaxSuccess: function () {
			
			var headerHTML =
				'<thead>' +
					'<tr>' +
						'<th rowspan="2" colspan="1" ><span id="' + mappingDataCustomerPage + 'ExtCustName">External Customer Name</span></th>' +
						'<th colspan="3" ><span id="' + mappingDataCustomerPage + 'IntCustCode">Internal Customer</span></th>' +
						'<th rowspan="1" colspan="1" >Actions</th>' +
					'</tr>' +
					'<tr>' +
						'<th style="width:200px;"></th>' +
						'<th style="width:200px;">Customer Code</th>' +
						'<th style="width:200px;">Customer Name</th>' +
						'<th style="width:70px;">Confidence</th>' +
						'<th style="width:50px;"></th>' +
					'</tr>' +
				'</thead>';

//			$scope[mappingDataCustomerPage]['headerHTML'] = headerHTML;
			
			$scope.services.datatableInit({
				tables: [mappingDataCustomerPage],
				http: $http,
				scope: $scope,
				rootScope: $rootScope,
				compile: $compile,
				colNotToSort: [4],
				order: [3, 'desc'],
				dtColumnBuilder: DTColumnBuilder,
				dtOptionsBuilder: DTOptionsBuilder,
				onDrawCallback: function () {
					// Change column name as per mapping type selection
					if ('unmapped' == $('[name=mappingType]:checked').val()) {
						$($('th[aria-label*="Internal Customer Code"]')[0]).html('Suggested Customer Code');
						$($('th[aria-label*="Internal Customer Name"]')[0]).html('Suggested Customer Name');
						$($('table[dt-instance="dtInstance' + mappingDataCustomerPage + '"] th[aria-label*="Confidence"]')[0]).html('Confidence');
					}
					else {
						$($('th[aria-label*="Internal Customer Code"]')[0]).html('Internal Customer Code');
						$($('th[aria-label*="Internal Customer Name"]')[0]).html('Internal Customer Name');
						$($('table[dt-instance="dtInstance' + mappingDataCustomerPage + '"] th[aria-label*="Confidence"]')[0]).html('Share Weightage');
					}
					
					$('#custSourceLbl').text($("#mappingDataSourceSelect").multipleSelect("getSelects", "text")[0]);
					$(".custEdit").unbind('click');
					$('.custEdit').click(function () {
						$scope.custEditClick($(this));
					});
					$('.cust-unmapped').confirmation({
						singleton: true,
						onConfirm: function () {
							$('.ajaxLoader').show();
							var custID = $(this).attr('data-extid');
							$.ajax({
								type: 'POST',
								url: $('#contextPath').val() + '/' + mappingDataCustomerPage + '/unmapExtMapping',
								data: {
									'extCustId': custID
								},
								success: function (response) {
									$scope['dtInstance' + mappingDataCustomerPage].reloadData($.noop, false);
									$('.ajaxLoader').hide();
									var response = JSON.parse(response);
									$scope.services.showNotification(response['Message']);
									//datatable.opts.scope['dtInstance'+opts.tableName]._renderer.rerender(); 
								},
								error: function (e) {
									$scope['dtInstance' + mappingDataCustomerPage].reloadData($.noop, false);
									$('.ajaxLoader').hide();
									console.error(e);
								}
							});
						}
					});
					
					$('.cust-map').confirmation({
						singleton: true,
						onConfirm: function () {
							var extCustID = $(this).attr('data-extid');
							var intCustID = $(this).attr('data-intCustId');
							$('.ajaxLoader').show();
							$.ajax({
								type: 'POST',
								url: $('#contextPath').val() + '/' + mappingDataCustomerPage + '/mapExtMapping',
								data: {
									'extCustId': extCustID,
									'intCustId': intCustID
								},
								success: function (response) {
									$scope['dtInstance' + mappingDataCustomerPage].reloadData($.noop, false);
									$('.ajaxLoader').hide();
									var response = JSON.parse(response);
									$scope.services.showNotification(response['Message']);
									//datatable.opts.scope['dtInstance'+opts.tableName]._renderer.rerender(); 
								},
								error: function (e) {
									$scope['dtInstance' + mappingDataCustomerPage].reloadData($.noop, false);
									$('.ajaxLoader').hide();
									console.error(e);
								}
							});
						}
					});
				},
				isTable: false
			});
		}
	}, true);
	$scope.services.select.setOptions({
		selectId: 'mappingDataDimensionSelect',
		optionsData: {
			customer: 'Customer',
			product: 'Product',
		},
		inputParam: {},
		multipleSelect: {
			filter: true,
			placeholder: 'Select Dimension',
			width: '100%',
			single: true,
			defaultFirstValueIfNotSelected: true
		},
	}, true);
	$scope.services.select.setOptions({
		selectId: 'mappingDataCategoriesSelect',
		//			url:	'/getCategoryList',
		optionsData: [],
		//			keyKey: 'key',
		//			valueKey: 'value',
		inputParam: {},
		multipleSelect: {
			filter: true,
			placeholder: 'Select Category',
			//defaultFirstValueIfNotSelected:true,
			width: '100%',
			//single: true
		},
		onAjaxSuccess: function () {
			$("#mappingDataCategoriesSelect").multipleSelect("disable");
		}
	}, true);
	$("#mappingDataDimensionSelect").on('change', function () {
		if ($(this).val()[0] == 'product') {
			$("#mappingDataCategoriesSelect").multipleSelect("enable");
		} else {
			$("#mappingDataCategoriesSelect").multipleSelect("uncheckAll");
			$("#mappingDataCategoriesSelect").multipleSelect("disable");
		}
	});
	$scope.setCategoryCombo = function (opts) {
		var opts = opts || {},
			vOptionsData = [];
		if (opts.elSourceSelect.val() && opts.elSourceSelect.val()[0] != undefined && $scope.sourceCategoryMap != undefined) {
			vOptionsData = $scope.sourceCategoryMap[opts.elSourceSelect.val()[0]];
		}
		if (vOptionsData == undefined) {
			vOptionsData = [];
		}
		$scope.services.select.setOptions({
			selectId: 'mappingDataCategoriesSelect',
			//				url:	'/getCategoryList',
			optionsData: vOptionsData,
			//				keyKey: 'key',
			//				valueKey: 'value',
			inputParam: {},
			multipleSelect: {
				filter: true,
				placeholder: 'Select Category',
				//defaultFirstValueIfNotSelected:true,
				width: '100%',
				//single: true,
			},
			onAjaxSuccess: function () {
				//					$("#mappingDataCategoriesSelect").multipleSelect("disable");
			}
		}, true);
		if ($('#mappingDataDimensionSelect').val()[0] == 'product') {
			$("#mappingDataCategoriesSelect").multipleSelect("enable");
		} else {
			$("#mappingDataCategoriesSelect").multipleSelect("disable");
		}
	}
	$("#mappingDataSourceSelect").on('change', function () {
		$scope.setCategoryCombo({
			elSourceSelect: $("#mappingDataSourceSelect")
		});
	});
	//		$("#mappingDataCategoriesSelect").multipleSelect("disable");
	$scope.custEditClick = function (obj) {

		var extCustId = $(obj).attr('data-extid');
		
		function initSelect (idx, opts) {
			var opts = opts || {};
			var selectId = 'mappingDataPopUpCustomerSelect' + idx;
			$scope.services.select.setOptions({
				selectId: selectId,
				optionsData: opts.selectOptionData,
				inputParam: {},
				multipleSelect: {
					filter: true,
					placeholder: 'Select Customer',
					width: '300px',
					defaultValue: [opts.defaultCustId],
					defaultFirstValueIfNotSelected: true,
					single: true
				},
			}, true);
			
			if (opts.defaultCustId == '' || opts.defaultCustId == undefined || opts.defaultCustId == '0') {
				$('#' + selectId).multipleSelect('setSelects', [$('' + $('#' + selectId).selector + ' option:first').val()]);
			}
		}
		
		function setSelectCustomerListPosition () {
			if ($("#selectMapCustomerTblId tbody").get(0).scrollHeight > $("#selectMapCustomerTblId tbody").height()) {
				$("#selectMapCustomerTblId .ms-parent").attr("style", "position: relative");
			} else {
				$("#selectMapCustomerTblId .ms-parent").attr("style", "position: absolute ");
			}
		}
		
		customerPersentageCal = function () {
			var defaultEachPer = (100 / $("#selectMapCustomerTblId tbody tr").length).toFixed(2);
			$("#selectMapCustomerTblId tbody tr td .percentageInput").each(function (key, val) {
				$(this).val(defaultEachPer);
			});
		}
		
		calCustomerTotalPercentage = function () {
			var total = 0;
			$("#selectMapCustomerTblId tbody tr td .percentageInput").each(function (key, val) {
				total = Number(total) + Number(this.value);
			});
			$("#totalPercentage").text("Total Percentage: " + parseFloat(total).toFixed(2) + "%");
		}
		
		function mappingDataPopUpCustAddbtnClick(opts) {
			var opts = opts || {};
			
			var rowNumber = Number($('#selectMapCustomerTblId tbody tr').length) + 1;
			if (opts._this != undefined) {
				var td = $(opts._this).parents('td');
				//console.debug($(td));
				var tdText = '<button  class="mappingDataPopUpCustRemovebtn btn btn-primary btn-simple btn-xs details-control" data-placement="left" data-toggle="tooltip" tooltip-title="Remove Mapping" data-title="Remove Mapping" data-original-title="" title="" style="margin:2px 2px;" data="row' + rowNumber + '" ><i class="fa fa-minus"></i></button>'
				$(td).append(tdText);
				$("#selectMapCustomerTblId .mappingDataPopUpCustAddbtn").tooltip('hide');
				$("#selectMapCustomerTblId .mappingDataPopUpCustAddbtn").remove();
			}
			
			text = '<tr>' +
						'<td>'+
						'<select id="mappingDataPopUpCustomerSelect' + rowNumber + '" class="" size="2" multiple="multiple" ></select>' +
					'</td>'+
					'<td id="actionTd" >' +
						'<input id="mappingDataPopUpPercentageInput' + rowNumber + '" type="number" class="form-control percentageInput" min="0" max="100" value="100" /> %' +
						'<button  class="mappingDataPopUpCustAddbtn btn btn-primary btn-simple btn-xs details-control" data-placement="left" data-toggle="tooltip" tooltip-title="Add Mapping" data-title="Add Mapping" data-original-title="" title="" style="margin:2px 2px;" data="row' + rowNumber + '" >' +
							'<i class="fa fa-plus" id="row' + rowNumber + '" ></i>'+ 
						'</button>' +
					'</td>' +
				'</tr>';

			$('#selectMapCustomerTblId tbody').append(text);
			initSelect (rowNumber, opts);
			
			customerPersentageCal();
			calCustomerTotalPercentage();
			//setSelectCustomerListPosition();
			
			$('#selectMapCustomerTblId').off('click', '.mappingDataPopUpCustAddbtn');
			$('#selectMapCustomerTblId').on('click', '.mappingDataPopUpCustAddbtn', function () {
				opts['_this'] = this;
				mappingDataPopUpCustAddbtnClick(opts);
			});
			
			$('#selectMapCustomerTblId').off('click', '.mappingDataPopUpCustRemovebtn');
			$("#selectMapCustomerTblId").on('click', '.mappingDataPopUpCustRemovebtn', function () {
				var tr = $(this).parents('tr');
				$(tr).remove();
				$("#selectMapCustomerTblId tbody tr").each(function (key, val) {
					var rowNumber = key + 1;
					$(val).children("td").children("[id^=mappingDataPopUpCustomerSelect]").attr('id', 'popUpIntProductSelect' + rowNumber);
					$(val).children("td").children("[id^=mappingDataPopUpPercentageInput]").attr('id', 'popUpPercentageInput' + rowNumber);
				});
				
				customerPersentageCal();
				calCustomerTotalPercentage();
				//$scope.setSelectListPostion();
			});
			
			$('#selectMapCustomerTblId').off('change', '.percentageInput');
			$('#selectMapCustomerTblId').on('change', '.percentageInput', function () {
				calCustomerTotalPercentage();
			});
		}
		
		

		$.ajax({
			type: 'GET',
			url: $('#contextPath').val() + '/' + mappingDataCustomerPage + '/getMappedCustomers',
			data: {
				extCustId: extCustId
			},
			success: function (response) {
				var suggestedCustomers = null,
					resp = JSON.parse(response);
				if (resp.suggestedCustomers != null) {
					suggestedCustomers = resp.suggestedCustomers;
				}

				var msg =
					'<div id="main_content_wrap" class="outer">' +
						'<div class="row">' +
							'<div class="col-md-5">Mapping for External Customer  </div>' +
							'<label id="selectedExternalCustomer"> ' + $(obj).attr('data-extcustname') + '</label>' +
						'</div>' +
						'<br\>' +
						'<span>Select Internal Customer</span><span id="totalPercentage">Total Percentage: 100%</span>'+
						'<br\>' +
						'<div class="row">' +
							'<table id="selectMapCustomerTblId" class="table table-bordered table-striped" cellspacing="0" width="100%" style="margin-top:5px;">' +
								'<thead>' +
									'<tr>' +
										'<th>Internal Customer </th>'+
										'<th></th>' +
									'</tr>' +
								'</thead>' +
								'<tbody>' +
//									'<tr>' +
//										'<td>'+
//											'<select id="mappingDataPopUpCustomerSelect1" class="" size="2" multiple="multiple" ></select>' +
//										'</td>'+
//										'<td id="actionTd" >' +
//											'<input id="mappingDataPopUpPercentageInput1" type="number" class="form-control percentageInput" min="0" max="100" value="100" /> %' +
//											'<button  class="mappingDataPopUpCustAddbtn btn btn-primary btn-simple btn-xs details-control" data-placement="left" data-toggle="tooltip" tooltip-title="Add Mapping" data-title="Add Mapping" data-original-title="" title="" style="margin:2px 2px;" data="row1" >' +
//												'<i class="fa fa-plus" id="row1" ></i>'+ 
//											'</button>' +
//										'</td>' +
//									'</tr>' +
								'</tbody>' +
							'</table>'+
						'</div>' +
						'<div>'+
							'<span id="percentageError"></span>'+
						'</div>' +
					'</div>';

				title = "Mapping For External Customer ";
				
				defaultCustId = $(obj).attr('data-IntCustId');
				
				BootstrapDialog.show({
					title: title,
					id: 'custMappingPopUp',
					message: '<div id="mappingPopUp" ></div>',
					closable: true,
					closeByBackdrop: false,
					onshown: function () {
						$('#mappingPopUp').append(msg);
						
						var objSuggestedCustomers = {};
						if (suggestedCustomers != null) {
							$.each(suggestedCustomers, function (key, value) {
								objSuggestedCustomers[value.id] = value.name + ' (' + value.code + ')' + ' ( Match Index -' + value.distance + ')';
							});
						}


						var mappingDataPopUpCustomerSelectOptionData = {};
						if ($.isEmptyObject(objSuggestedCustomers)) {
							mappingDataPopUpCustomerSelectOptionData = $scope.CustList
						} else {
							mappingDataPopUpCustomerSelectOptionData = {
								'Suggested Customers': objSuggestedCustomers,
								'All Customers': $scope.CustList
							};
						}
						
						
//						$("#selectMapCustomerTblId").off('click', '.mappingDataPopUpCustAddbtn');
//						$("#selectMapCustomerTblId").on('click', '.mappingDataPopUpCustAddbtn', function () {
//							mappingDataPopUpCustAddbtnClick({
//								selectOptionData: mappingDataPopUpCustomerSelectOptionData,
//								defaultCustId: defaultCustId,
//								_this: this
//							});
//						});
//						
//						initSelect(1, {
//							selectOptionData: mappingDataPopUpCustomerSelectOptionData,
//							defaultCustId: defaultCustId
//						});
						
						
						if (resp && resp['mapData'] != null && resp['mapData'].length > 0) {
							for (var row in resp['mapData']) {
								var mappedCustId = resp['mapData'][row][0];
								if (Number(row) == 0) {
									mappingDataPopUpCustAddbtnClick({
										selectOptionData: mappingDataPopUpCustomerSelectOptionData,
										defaultCustId: mappedCustId,
										_this: this
									});
								}
								else {
									$('#selectMapCustomerTblId .mappingDataPopUpCustAddbtn').trigger('click');
									initSelect(Number(row) + 1, {
										selectOptionData: mappingDataPopUpCustomerSelectOptionData,
										defaultCustId: mappedCustId
									});
								}
								
//								initSelect(row+1, {
//									selectOptionData: mappingDataPopUpCustomerSelectOptionData,
//									defaultCustId: mappedCustId
//								});
//								mappingDataPopUpCustAddbtnClick({
//									selectOptionData: mappingDataPopUpCustomerSelectOptionData,
//									defaultCustId: mappedCustId,
//									_this: this
//								});

							}
//						} else {
//							mappingDataPopUpCustAddbtnClick({
//								selectOptionData: mappingDataPopUpCustomerSelectOptionData,
//								defaultCustId: mappedCustId,
//								_this: this
//							});
						}
						else{
							mappingDataPopUpCustAddbtnClick({
								selectOptionData: mappingDataPopUpCustomerSelectOptionData,
								//defaultCustId: mappedCustId
							});
						}
					},
					buttons: [{
						id: 'save-btn',
						label: 'Save',
						action: function (dialogRef) {
							var extCustId = $(obj).attr('data-extid');
//							var customerId = $('#mappingDataPopUpCustomerSelect').multipleSelect('getSelects')[0];
							var sourceId = $('#mappingDataSourceSelect').multipleSelect("getSelects")[0];
							var isInvalid = false, joIntCust = {};
							
							if ($(".percentageInput:empty").length > 0) {
								$("#percentageError").html("");
								var total = 0;
								$(".percentageInput").each(function (index) {
									total = Number(total) + Number(this.value);
								});
								total = parseFloat(total).toFixed(2);
								if (total < 99.98 || total > 100.02) {
									$scope.canExit = false;
									$("#percentageError").html("Please enter valid value for percentage");
									isInvalid = true;
								} else {
									$("#percentageError").html("");
									$(".percentageInput").each(function (index) {
										if (Number(this.value) == 0) {
											$("#percentageError").html("Please enter valid value for percentage");
											isInvalid = true;
											return false;
										}
									});
								}
							} else {
								$("#percentageError").html("Enter value for Product");
								isInvalid = true;
							}
							
							if (!isInvalid) {
								$("#percentageError").html("");
								
								$("#selectMapCustomerTblId tbody tr").each(function (key, val) { 
									if ($(val).find('[id^=mappingDataPopUpCustomerSelect]') != undefined) { 
										var intCust = $($(val).find('[id^=mappingDataPopUpCustomerSelect]')[0]).val();
										var intCustPerc = $($(val).find('[id^=mappingDataPopUpPercentageInput]')[0]).val();
										console.log(intCustPerc);
										intCustPerc /= 100.00;
										if (intCust.length > 0) {
											if (joIntCust[intCust[0]] != undefined) {
												$("#percentageError").html("Selected products are same.");
												isInvalid = true;
												return false;
											}
										}
										joIntCust[intCust[0]] = {
											'CustomerId': intCust[0],
											'percentage': intCustPerc
										};
									} 
								});
							}
							
							if (isInvalid) {
								return false;
							}
							
							$('.ajaxLoader').show();
							var mapData = {
								extCustId: extCustId,
								intCustomer: JSON.stringify(joIntCust),
								sourceId: sourceId
							};
							
							$.ajax({
								type: 'POST',
								url: $('#contextPath').val() + '/CustMappingData/saveCustMapping',
								data: mapData,
								success: function (response) {
									var message = 'Mapping saved Successfully.',
										isSuccess = true;
									if (response != null) {
										console.debug(response);
										var resp = JSON.parse(response);
										message = resp['Message'];
										if (resp['SUCCESS'] != null && !resp['SUCCESS']) isSuccess = false;
									}
									$('.ajaxLoader').hide();
									if (isSuccess) {
										dialogRef.close();
										$scope['dtInstance' + mappingDataCustomerPage].reloadData($.noop, false);
										$scope.services.showNotification(message);
										$('.modal-backdrop').remove();
									} else {}
								}
							});
						}
					}, {
						id: 'btn-cancel',
						label: 'Cancel',
						action: function (dialogRef) {
							dialogRef.close();
							$('.modal-backdrop').remove();
						}
					}]
				});
			}
		});
	};
	$scope.setPageSection();
	/*  $scope.initSelectBox= function(rowNumber){
        	$scope.services.select.setOptions({
				selectId: 'popUpBrandSelect'+rowNumber, 
				optionsData: $scope.brandList,
				inputParam: {},
				multipleSelect: {
					filter: true,
					placeholder: 'Select Brand',
					width: '100%',
					defaultValue:$rootScope.popUpBrandSelect,
					single: true
				},
			}, true);
        }*/
	$scope.persentageCal = function () {
		var defaultEachPer = (100 / $("#selectMapProduct tbody tr").length).toFixed(2);
		$("#selectMapProduct tbody tr td .percentageInput").each(function (key, val) {
			$(this).val(defaultEachPer);
		});
	}
	$scope.initSelectBox = function (rowNumber, opts) {

		var popUpIntProductSelectOptionsData = {};
		if ($.isEmptyObject(opts.suggestedIntProducts)) {
			popUpIntProductSelectOptionsData = $scope.intProductList
		} else {
			popUpIntProductSelectOptionsData = {
				'Suggested Products': opts.suggestedIntProducts,
				'All Products': $scope.intProductList
			};
		}

		$scope.services.select.setOptions({
			selectId: 'popUpIntProductSelect' + rowNumber,
			//optionsDataWithData: true,
			optionsData: popUpIntProductSelectOptionsData,
			inputParam: {},
			multipleSelect: {
				filter: true,
				placeholder: 'Select Brand',
				width: '100%',
				defaultValue: $rootScope.popUpBrandSelect,
				single: true
			},
		}, true);

		/*$scope.services.select.setOptions({
			selectId: 'popUpBrandSelect' + rowNumber,
			optionsDataWithData: true,
			optionsData: $scope.brandList,
			inputParam: {},
			multipleSelect: {
				filter: true,
				placeholder: 'Select Brand',
				width: '100%',
				defaultValue: $rootScope.popUpBrandSelect,
				single: true
			},
		}, true);
		$scope.services.select.setOptions({
			selectId: 'popUpFormSelect' + rowNumber,
			optionsData: $scope.formList,
			inputParam: {},
			multipleSelect: {
				filter: true,
				placeholder: 'Select Form',
				width: '100%',
				defaultValue: $rootScope.popUpBrandselect,
				single: true
			},
		}, true);
		$scope.services.select.setOptions({
			selectId: 'popUpSubFormSelect' + rowNumber,
			optionsData: $scope.subFormList,
			inputParam: {},
			multipleSelect: {
				filter: true,
				placeholder: 'Select Sub-Form',
				width: '100%',
				defaultValue: $rootScope.popUpBrandselect,
				single: true
			},
		}, true);*/
		/*$("#popUpBrandSelect" + rowNumber).on("change", function() {
			if ($(this).multipleSelect("getSelects").length == 1) {
				var numb = $(this).attr('id').match(/\d/g);
				var currentRowNumber = numb.join("");
				var optDatasetResp = $(this).find('option[value="' + $(this).multipleSelect("getSelects")[0] + '"]').attr('data').split(",");
				var selectEl = $("#popUpFormSelect" + currentRowNumber);
				selectEl.multipleSelect('disable');
				selectEl.empty();
				$.each(optDatasetResp, function(key, val) {
					selectEl.append($('<option>', {
						value: val,
						text: val,
						title: val
					}));
				});
				selectEl.multipleSelect("enable");
				selectEl.multipleSelect("refresh");
			}
		});
		$("#popUpFormSelect" + rowNumber).on("change", function() {
			if ((!($(this).next('div.ms-parent').children('.ms-choice').hasClass('disabled'))) && ($(this).multipleSelect("getSelects").length == 1)) {
				var numb = $(this).attr('id').match(/\d/g);
				var currentRowNumber = numb.join("");
				var searchText = $("#popUpBrandSelect" + currentRowNumber).multipleSelect("getSelects")[0] + '|' + $("#popUpFormSelect" + currentRowNumber).multipleSelect("getSelects")[0];
				var obj = $.grep($scope.intProdlist.BrandForm, function(k) {
					return k.key == searchText;
				});
				if (obj.length > 0 && obj[0].value.length > 0) {
					var optDatasetResp = obj[0].value;
					var selectEl = $("#popUpSubFormSelect" + currentRowNumber);
					selectEl.multipleSelect('disable');
					selectEl.empty();
					$.each(optDatasetResp, function(key, val) {
						selectEl.append($('<option>', {
							value: val,
							text: val,
							title: val
						}));
					});
					selectEl.multipleSelect("enable");
					selectEl.multipleSelect("refresh");
				}
			}
		});*/
	}
	$scope.addMappingPopUpRowProd = function (opts) {
		var opts = opts || {};
		$("#selectMapProduct").on('change', '.percentageInput', function () {
			$scope.calTotalPercentage();
		})
		$scope.setSelectListPostion = function () {
			if ($("#selectMapProduct tbody").get(0).scrollHeight > $("#selectMapProduct tbody").height()) {
				$("#selectMapProduct .ms-parent").attr("style", "position: relative");
			} else {
				$("#selectMapProduct .ms-parent").attr("style", "position: absolute ");
			}
		}
		$("#selectMapProduct").on('click', '.mappingDataRemovebtn', function () {
			//$("#totalPercentage").text("Total Percentage: 100%");
			var tr = $(this).parents('tr');
			$(tr).remove();
			$scope.setSelectListPostion();
			$("#selectMapProduct tbody tr").each(function (key, val) {
				var rowNumber = key + 1;
				$(val).children("td").children("[id^=popUpIntProductSelect]").attr('id', 'popUpIntProductSelect' + rowNumber);
				/*$(val).children("td").children("[id^=popUpBrandSelect]").attr('id', 'popUpBrandSelect' + rowNumber);
				$(val).children("td").children("[id^=popUpFormSelect]").attr('id', 'popUpFormSelect' + rowNumber);
				$(val).children("td").children("[id^=popUpSubFormSelect]").attr('id', 'popUpSubFormSelect' + rowNumber);*/
				$(val).children("td").children("[id^=popUpPercentageInput]").attr('id', 'popUpPercentageInput' + rowNumber);
			});
			$scope.persentageCal();
			$scope.calTotalPercentage();
		});
		$("#selectMapProduct").off('click', '.mappingDataAddbtn');
		$("#selectMapProduct").on('click', '.mappingDataAddbtn', function () {
			//$("#totalPercentage").text("Total Percentage: 100%");
			var td = $(this).parents('td');
			var rowNumber = Number($('#selectMapProduct tbody tr').length) + 1;
			var tdText = '<button  class="mappingDataRemovebtn btn btn-primary btn-simple btn-xs details-control" data-placement="left" data-toggle="tooltip" tooltip-title="Remove Mapping" data-title="Remove Mapping" data-original-title="" title="" style="margin:2px 2px;" data="row1" ><i class="fa fa-minus"></i></button>'
			$(td).append(tdText);
			$("#selectMapProduct .mappingDataAddbtn").tooltip('hide');
			$("#selectMapProduct .mappingDataAddbtn").remove();
			text = '<tr>' +
				'<td><select id="popUpIntProductSelect' + rowNumber + '"></select></td>'
				/*+ '<td><select id="popUpBrandSelect' + rowNumber + '"></select></td>' 
				+ '<td><select id="popUpFormSelect' + rowNumber + '"></select></td>' 
				+ '<td><select id="popUpSubFormSelect' + rowNumber + '"></select></td>'*/
				+
				'<td id="actionTd" >' +
				'<input type="number" id="popUpPercentageInput' + rowNumber + '" class="form-control percentageInput" min="0" max="100" value="100" /> %' +
				'<button  class="mappingDataAddbtn btn btn-primary btn-simple btn-xs details-control" data-placement="left" data-toggle="tooltip" tooltip-title="Add Mapping" data-title="Add Mapping" data-original-title="" title="" style="margin:2px 2px;" data="row1" >' + '<i class="fa fa-plus" id="row1" ></i></button>' +
				'</td>' +
				'</tr>';

			$('#selectMapProduct tbody').append(text);
			$scope.initSelectBox(rowNumber, {
				suggestedIntProducts: opts.suggestedIntProducts
			});
			$scope.persentageCal();
			$scope.setSelectListPostion();
			$scope.calTotalPercentage();
		});
		$scope.initSelectBox(parseInt(opts.rowIdx), {
			suggestedIntProducts: opts.suggestedIntProducts
		});
	
		if (opts.brand != "") $("#popUpBrandSelect" + (parseInt(opts.rowIdx))).multipleSelect("setSelects", [opts.brand]);
		if (opts.form != "") $("#popUpFormSelect" + (parseInt(opts.rowIdx))).multipleSelect("setSelects", [opts.form]);
		if (opts.subForm != "") $("#popUpSubFormSelect" + (parseInt(opts.rowIdx))).multipleSelect("setSelects", [opts.subForm]);
		if (opts.intProduct != "") $("#popUpIntProductSelect" + (parseInt(opts.rowIdx))).multipleSelect("setSelects", [opts.intProduct]);
	}
	$scope.calTotalPercentage = function () {
		var total = 0;
		$("#selectMapProduct tbody tr td .percentageInput").each(function (key, val) {
			total = Number(total) + Number(this.value);
		});
		$("#totalPercentage").text("Total Percentage: " + parseFloat(total).toFixed(2) + "%");
	}
	$scope.editClick = function (obj) {
		var extprodlist = $(obj).attr('data-extprodlist');
		var extprodText = "";

		/*if ($("#mappingDataSourceSelect").multipleSelect("getSelects", "text")[0] == "Kantar") {
			extprodText = extprodlist[3]
		} else {
			extprodText = extprodlist[0] + " | " + extprodlist[1] + " | " + extprodlist[2];
		}*/

		extprodText = extprodlist

		msg = '<div id="main_content_wrap" class="outer"><span>Mapping for ' + $("#mappingDataSourceSelect").multipleSelect("getSelects", "text")[0] + ' Product</span><label id="selectedNielsenProduct">' + extprodText + '</label>' +
			'<br/><span>Select Internal Product</span><span id="totalPercentage">Total Percentage: 100%</span>' +
			'<div>' + 
			'<table id="selectMapProduct" class="table table-bordered table-striped" cellspacing="0" width="100%" style="margin-top:5px;">' +
			'<thead>' +
			'<tr>' +
			'<th>Internal Product</th>'
			/*+ '<th>Brand</th>' 
			+ '<th>Form</th>' 
			+ '<th>SubForm</th>'*/
			+
			'<th></th>' +
			'</tr>' +
			'</thead>' +
			'<tbody>' +
			'<tr>' +
			'<td><select id="popUpIntProductSelect1"></select></td>'
			/*+ '<td><select id="popUpBrandSelect1"></select></td>' 
			+ '<td><select id="popUpFormSelect1"></select></td>' 
			+ '<td><select id="popUpSubFormSelect1"></select></td>'*/
			+
			'<td id="actionTd" >' +
			'<input id="popUpPercentageInput1" type="number" class="form-control percentageInput" min="0" max="100" value="100" /> %' +
			'<button  class="mappingDataAddbtn btn btn-primary btn-simple btn-xs details-control" data-placement="left" data-toggle="tooltip" tooltip-title="Add Mapping" data-title="Add Mapping" data-original-title="" title="" style="margin:2px 2px;" data="row1" >' +
			'<i class="fa fa-plus" id="row1" ></i></button>' +
			'</td>' +
			'</tr>' +
			'</tbody>' +
			'</table><div><span id="percentageError"></span></div></div>' +
			'</div>';

		var extProdId = $(obj).attr('data-id');
		var sourceId = $('#mappingDataSourceSelect').multipleSelect("getSelects")[0];
		title = "Mapping For " + $("#mappingDataSourceSelect").multipleSelect("getSelects", "text")[0] + " Product ";
		BootstrapDialog.show({
			title: title,
			id: 'productMappingPopUp',
			message: '<div id="mappingPopUp" ></div>',
			closable: true,
			closeByBackdrop: false,
			onshown: function () {
				$('#mappingPopUp').append(msg);
				$timeout(function () {
					var intProdList = $(obj).attr('data-intprodlist').split("|");
					$.ajax({
							type: 'GET',
							url: $('#contextPath').val() + '/' + mappingDataProductPage + '/getMappedProducts',
							data: {
								extProdId: extProdId
							},
							success: function (response) {
								var resp = JSON.parse(response),
									suggestedIntProducts = {};

								if (resp && resp['suggestedProducts'] != null && resp['suggestedProducts'].length > 0) {
									for (var row in resp['suggestedProducts']) {
										suggestedIntProducts[resp['suggestedProducts'][row]['id']] = resp['suggestedProducts'][row]['name'] + ' (' + resp['suggestedProducts'][row]['code'] + ')' + ' (Match Index - ' + resp['suggestedProducts'][row]['distance'] + ')';
									}
								}
									
								if (resp && resp['mapData'] != null && resp['mapData'].length > 0) {
									for (var row in resp['mapData']) {
										var mappedProductId = resp['mapData'][row][0];

										$("#selectMapProduct .mappingDataAddbtn").trigger('click');
										$scope.addMappingPopUpRowProd({
											rowIdx: parseInt(row) + 1,
											intProduct: mappedProductId,
											suggestedIntProducts: suggestedIntProducts
										});

									}
								} else {
									$scope.addMappingPopUpRowProd({
										rowIdx: 1,
										suggestedIntProducts: suggestedIntProducts
									});
								}
							},
							failure: function (response) {}
						})
						//					}
				}, 100);
			},
			buttons: [{
				id: 'save-btn',
				label: 'Save',
				action: function (dialogRef) {
					var percentageErr = true;
					if ($(".percentageInput:empty").length > 0) {
						$("#percentageError").html("");
						var total = 0;
						$(".percentageInput").each(function (index) {
							total = Number(total) + Number(this.value);
						});
						total = parseFloat(total).toFixed(2);
						if (total < 99.98 || total > 100.02) {
							$scope.canExit = false;
							$("#percentageError").html("Please enter valid value for percentage");
						} else {
							$("#percentageError").html("");
							$(".percentageInput").each(function (index) {
								if (Number(this.value) == 0) {
									$("#percentageError").html("Please enter valid value for percentage");
									return false;
								}
							});
						}
					} else {
						$("#percentageError").html("Enter value for Product");
					}
					if ($("#percentageError").text().trim() == "") {
						var isInvalid = false;
						$("#percentageError").html("");
						$("#selectMapProduct tbody tr").each(function (key, val) {
							if (isInvalid == false) {
								var rowNumber = key + 1;
								var currentForm = $("#popUpFormSelect" + rowNumber).multipleSelect("getSelects")[0];
								for (var count = rowNumber + 1; count <= $("#selectMapProduct tbody tr").length; count++) {
									if (currentForm == $("#popUpFormSelect" + count).multipleSelect("getSelects")[0]) {
										if (($("#popUpIntProductSelect" + rowNumber).multipleSelect("getSelects")[0] == $("#popUpIntProductSelect" + count).multipleSelect("getSelects")[0])) {
											$("#percentageError").html("Selected products are same.");
											isInvalid = true;
											return false;
										}
									}
								}
							} else {
								return false;
							}
						});
					}

					if ($("#percentageError").html().trim() == "") {
						$('.ajaxLoader').show();
						var mapData = {
							extProdId: extProdId,
							intProduct: '',
							sourceId: sourceId
						};
						var prodSeparator = '!|';
						var separator = '|';
						var intProduct = '';
						$("#selectMapProduct tbody tr").each(function (key, val) {
							var rowNumber = key + 1;
							var obj = [];
							obj.push($("#popUpIntProductSelect" + rowNumber).multipleSelect("getSelects")[0]);
							obj.push($("#popUpPercentageInput" + rowNumber).val());
							if (intProduct == '') intProduct += obj.join(separator);
							else intProduct += prodSeparator + obj.join(separator);
						});
						mapData.intProduct = intProduct;
						$.ajax({
							type: 'POST',
							url: $('#contextPath').val() + '/' + mappingDataProductPage + '/saveMapping',
							data: mapData,
							success: function (response) {
								var message = 'Mapping saved Successfully.',
									isSuccess = true;
								if (response != null) {
									console.debug(response);
									var resp = JSON.parse(response);
									message = resp['Message'];
									if (resp['SUCCESS'] != null && !resp['SUCCESS']) isSuccess = false;
								}
								$('.ajaxLoader').hide();
								if (isSuccess) {
									dialogRef.close();
									$scope.services.showNotification(message);
									$scope['dtInstance' + mappingDataProductPage].reloadData($.noop, false);
									$('.modal-backdrop').remove();
								} else {}
							}
						})
					}
				}
			}, {
				id: 'btn-cancel',
				label: 'Cancel',
				action: function (dialogRef) {
					dialogRef.close();
					$('.modal-backdrop').remove();
				}
			}]
		});
	}
}]);

angular.module('PEAWorkbench').controller("dataOverrideController", ['$scope', 'services', function ($scope, services) {
	$scope.services = services;
	$scope.promotionChangeData = {};
	$('.saveDateChange').prop('disabled',true);
	
	$scope.defaultDateFormat = $('#defaultDateFormat').val();
	
	$scope.init = function () {
		$('input[name="datefilter"]').daterangepicker({
			autoUpdateInput: false,
			locale: {
				cancelLabel: 'Clear'
			},
			opens: 'center',
			format: $scope.defaultDateFormat,
			//maxDate: new Date(),
			startDate: moment().subtract(29, 'days')
		});
		$('input[name="datefilter"]').on('apply.daterangepicker', function (ev, picker) {
			$(this).val(picker.startDate.format($scope.defaultDateFormat) + ' - ' + picker.endDate.format($scope.defaultDateFormat));
		});

		$('input[name="datefilter"]').on('cancel.daterangepicker', function (ev, picker) {
			$(this).val('');
		});
		$.getJSON($('#contextPath').val() + '/promotionPeriodAlignmentcusNprodData', function (data) {
			$scope.services.select.setOptions({
				selectId: 'dataOverrideCustomerSelect',
				optionsData: data.customer,
				inputParam: {},
				multipleSelect: {
					filter: true,
					placeholder: 'Select Customer',
					width: '100%',
					single: true,
					defaultFirstValueIfNotSelected: true
				},
			}, true);
			$scope.services.select.setOptions({
				selectId: 'dataOverrideProductSelect',
				optionsData: data.product,
				inputParam: {},
				multipleSelect: {
					filter: true,
					placeholder: 'Select Product',
					width: '100%',
					single: true,
					defaultFirstValueIfNotSelected: true
				},
			}, true);
//			$('input[name="datefilter"]').data('daterangepicker').setStartDate(data.StartDate);
//			$('input[name="datefilter"]').data('daterangepicker').setEndDate(data.endDate);

		});
		
		$scope.popoverDelBtnClick = function () {
			var el = $(this);
			console.debug(el.attr('data-promoId'))
			$scope.promotionChangeData[el.attr('data-promoId')] = undefined;
			$scope.updatePromoPopover();
		};
		
		
		$scope.updatePromoPopover = function () {
			$scope.popoverTemplate = 
				'<div class="table-responsive">'+
				'<table class="table table-condensed">'+
					'<thead >'+
						'<tr>'+
							'<th>Promo Id</th>'+
							'<th class="col-sm-4">Start Date</th>'+
							'<th class="col-sm-4">End Date</th>'+
							'<th class="col-sm-2">Delete</th>'+
						'</tr>'+
					'</thead>'+
					'<tbody>';
			
			$.each($scope.promotionChangeData, function(key, value){
				if (value != undefined) {
					$scope.popoverTemplate = $scope.popoverTemplate +
							'<tr>'+
								'<td class="col-sm-2">'+ value.promoId + '</td>'+
								'<td class="col-sm-4">' + value.startDate +'</td>'+
								'<td class="col-sm-4">' + value.endDate + '</td>'+
								'<td class="col-sm-2"> <button ng-click="popoverDelBtnClick" class="btn btn-primary btn-simple btn-xs details-control pop-del-promo-btn" data-toggle="tooltip" tooltip-title="Remove Promotion" data-title="Remove" data-promoId="'+ value.promoId + '"><i class="fa fa-minus"></i></button></td>' +
							'</tr>';
				}
			});
			
			$scope.popoverTemplate = $scope.popoverTemplate +
					'</tbody>'+
				'</table>'+
				'</div>';
			
			var options = {
					title: 'Promotion Changed Date <button  class="btn btn-primary btn-simple btn-xs details-control pop-save-promo-btn" data-toggle="tooltip" tooltip-title="Save Changes" data-title="Save changes">Save changes</button>',
					singleton: true,
					placement: 'left',
					html:true,
			};
			var p = $('.saveDateChange').popover(options);
			/*p.on("show.bs.popover", function(e){
			    p.data("bs.popover").tip().css({"max-width": "370px","top": "330.5px","left": "1136.73px"});
			});*/
		}
		
		$scope.showPopover = function () {
			$(".popover-content").html($scope.popoverTemplate);
			
			$('.pop-del-promo-btn').click(function () {
				var el = $(this);
				console.debug(el.attr('data-promoId'))
				$scope.promotionChangeData[el.attr('data-promoId')] = undefined;
				
				$scope.updatePromoPopover();
				
				$scope.showPopover();
				
			});
			
			$('.pop-save-promo-btn').confirmation({
				title: 'Are you sure to save below changes?',
				singleton: true,
				placement: 'bottom',
				onConfirm: function () {
					$('.ajaxLoader').show();
					$.ajax({
						type: 'POST',
						url: $('#contextPath').val() + '/savePromotionData',
						data: {
							ProductId: $('#dataOverrideProductSelect').multipleSelect("getSelects")[0],
							productName: $('#dataOverrideProductSelect').multipleSelect("getSelects", "text")[0],
							CustomerId: $('#dataOverrideCustomerSelect').multipleSelect("getSelects")[0],
							customerName: $('#dataOverrideCustomerSelect').multipleSelect("getSelects", "text")[0],
							PromoAlignData: JSON.stringify($scope.promotionChangeData)
						},
						success: function (response) {
							$('.ajaxLoader').hide();
							var resp = JSON.parse(response);
							services.showNotification(resp.Message);
							$('.saveDateChange').popover('hide');
							$scope.applyFilter();
						},
						error: function (e) {
							$('.ajaxLoader').hide();
							console.debug(e);
						}
					});
				}
			});
		}
		
		$scope.applyFilter = function () {
			$('.ajaxLoader').show();
			
			$.ajax({
				type: 'GET',
				url: $('#contextPath').val() + '/promotionPeriodAlignmentData',
				data: {
					'startDate': $.datepicker.formatDate('yy-mm-dd', new Date($('#dataOverrideDateRange').data('daterangepicker').startDate)),
					'endDate': $.datepicker.formatDate('yy-mm-dd', new Date($('#dataOverrideDateRange').data('daterangepicker').endDate)),
					'productName': $('#dataOverrideProductSelect').multipleSelect("getSelects", "text")[0],
					'customerName': $('#dataOverrideCustomerSelect').multipleSelect("getSelects", "text")[0]
				},
				success: function (response) {
					$('.ajaxLoader').hide();
					var response = JSON.parse(response);
					$('#promotionOveride').highcharts({
						chart: {
							//zoomType: 'x'
						},
						title: {
							text: 'Sellout Time Series'
						},
						credits: {
							enabled: false
						},
						/*subtitle: {
							text: document.ontouchstart === undefined ?
								'Click and drag in the plot area to zoom in' : 'Pinch the chart to zoom in'
						},*/
						xAxis: {
							type: 'datetime'
						},
						yAxis: {
							title: {
								text: 'Volume'
							}
						},
						legend: {
							enabled: false
						},
						plotOptions: {
							area: {
								fillColor: {
									linearGradient: {
										x1: 0,
										y1: 0,
										x2: 0,
										y2: 1
									},
									stops: [
										[0, Highcharts.getOptions().colors[0]],
										[1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
									]
								},
								marker: {
									radius: 2
								},
								lineWidth: 1,
								states: {
									hover: {
										lineWidth: 1
									}
								},
								threshold: null
							}
						},

						series: [{
							type: 'area',
							name: 'volume',
							data: response.topGraph

						}]
					});
					var newData = [];
					var xAxisCat = [];
					var promotionData = {};
					$.each(response.bottomGraph, function (key, value) {
						var colOpts = {}, newDateRange = [value[3], value[4]];
						
						colOpts.low = newDateRange[0];
						colOpts.high = newDateRange[1];
						/*if (value[5] < 10000)
							colOpts.color = '#3BBEE3';
*/
						if (value[7] != null && value[7] == 'Cancelled')
							colOpts.color = 'black';

						newData.push(colOpts);
						xAxisCat.push(value[0]);
						promotionData[value[0]] = {
								Promotion: value[1],
								BaseUnitVolume: value[5],
								IncrementalUnitVolume: value[6]
						};
					})
					
					var chkData = [];
					angular.forEach(newData, function(valueOuter, key) {
						var isOverlapped = false;
						angular.forEach(newData, function(valueInner, key) {
							if (	(valueOuter.low > valueInner.low && valueOuter.low < valueInner.high)  
									|| (valueOuter.high > valueInner.low && valueOuter.high < valueInner.high) 
									//|| (valueOuter.low < valueInner.low && valueOuter.low < valueInner.high) && ((valueOuter.high > valueInner.low && valueOuter.high > valueInner.high))
							) {
								valueOuter.isOverlapped = true;
								valueOuter.color = '#dd4b39';
								return false;
							}
						});
						chkData.push(valueOuter)
					});
					
					newData = chkData;
					
					$(document).on("click", ".saveDateChange ", function() {
						setTimeout(function(){
							$scope.showPopover();
						}, 100);
					});
					
					var dateRangePromotion =$('#dateRangePromotion').highcharts({

						chart: {
							type: 'columnrange',
							inverted: true,
							//zoomType: 'y'
						},
						plotOptions: {
//					        series: {
//					            pointWidth: 10//width of the column bars irrespective of the chart size
//					        },
					        
					        series: {
					        	pointWidth: 10,
					            dataLabels: {
					               color: '#B0B0B3'
					            },
					            marker: {
					               lineColor: '#333'
					            }
					         },
					         boxplot: {
					            fillColor: '#505053'
					         },
					         candlestick: {
					            lineColor: 'white'
					         },
					         errorbar: {
					            color: 'white'
					         }
					       
					    },
						credits: {
							enabled: false
						},

						title: {
							text: 'Date Range for Promotion'
						},
						xAxis: {
							categories: xAxisCat,
							labels: {
								enabled: false
							},
							lineWidth: 1,
							   minorGridLineWidth: 0,
							   lineColor: 'transparent',
							            
							   labels: {
							       enabled: false
							   },
							   minorTickLength: 0,
							   tickLength: 0
						},

						tooltip: {
							formatter: function () {
								var low = new moment(new Date(this.point.low)).format($('#defaultDateFormat').val());
								var high = new moment(new Date(this.point.high)).format($('#defaultDateFormat').val());
								return 	'Promo Id 	- <b>' + this.x + '</b><br>' + 
										'Promotion 	- <b>' + promotionData[this.x]['Promotion'] + '</b><br>' +
										'Range 		- <b>' + low + '</b> To <b>' + high + '</b><br>' +
										'Base Unit Volume - <b>' + promotionData[this.x]['BaseUnitVolume'] + '</b><br>' +
										'Incremental Unit Volume - <b>' + promotionData[this.x]['IncrementalUnitVolume'] + '</b>';
							}
						},

						yAxis: {
							type: 'datetime',
							title: {
								text: 'Date Range'
							},
							scrollbar: {
								enabled: true,
								showFull: false
							},
							
						},

						legend: {
							enabled: false
						},

						series: [{
							color: '#3C8DBC',
							name: 'Date Range',
							data: newData,
							events: {
			                    click: function (event) {
			                    	$('.saveDateChange').popover('hide')
			                    	//series.data.push({ y: parseInt(Data[i]), color: '#FF0000' });
			                    	var LocalThis = this;
			                    	
			                       	var endDate = Highcharts.dateFormat('%d/%m/%Y', event.point.high);
			                    	var promoId = event.point.category;
			                    	var diffDays = (event.point.high-event.point.low)/1000/60/60/24;
			                      // $scope.openDateRangePopUp(promotionName,startDate,endDate,diffDays);
			                    	msg = "<form  class='form-horizontal dateRangeChangeForm' autocomplete='off'>" +
									"<div class='form-group row'>" +
										"<label for='startdate' class='col-sm-2 control-label'>Start Date</label>" +
											"<div class='col-sm-4'>" +
												"<input type='text' id='promotionStartDate' class='form-control pull-right' data-date-format='dd/mm/yyyy' name='datefilter'  placeholder='Select Date Range'  style='width:100%'>" +
											"</div>" +
										"<label for='startdate' class='col-sm-2 control-label'>End Date</label>" +
												"<label type='text' id='promotionEndDate' class='control-label pull-left col-sm-4'   style='text-align:left;'>"+endDate+"</label>" +
								"</form>";
			                    	event.point.update({state:'none'},true);
			                    	
									BootstrapDialog.show({
							            title: 'Change Promotion Date Range for : ' + promoId,
							            message: msg,
							            id: 'promoDateDia',
							            onshown: function (dialog) {
							            	$('#storeBtn').prop('disabled',true);
							            	$('#promotionStartDate').datepicker({
							    				autoUpdateInput: false,
							    				locale: {
							    					cancelLabel: 'Clear'
							    				},
							    				opens: 'center',
							    				//format: $scope.defaultDateFormat,
							    			});
							            	var stDate= Highcharts.dateFormat('%d/%m/%Y', event.point.low);
							            	$('#promotionStartDate').datepicker('setDate', stDate );
							            	$('#promotionStartDate').datepicker()
							                .on('changeDate', function(e) {
							                
							                	var selectedStartDate  =  new Date($("#promotionStartDate").data('datepicker').getFormattedDate('yyyy-mm-dd'));
							                	selectedStartDate.setDate(selectedStartDate.getDate() + diffDays);
							                	var updatedEndDate= Highcharts.dateFormat('%d/%m/%Y', selectedStartDate);
							                	$("#promotionEndDate").text(Highcharts.dateFormat('%d/%m/%Y', selectedStartDate));
							                	if( Highcharts.dateFormat('%d/%m/%Y', new Date($("#promotionStartDate").data('datepicker').getFormattedDate('yyyy-mm-dd'))) === Highcharts.dateFormat('%d/%m/%Y', event.point.low)){
							                		$('#storeBtn').prop('disabled',true);
							                	}else{
							                		$('#storeBtn').prop('disabled',false);
							                	}
							                });
							            },
							            buttons: [{
											label: 'Store',
											id:'storeBtn',
											action: function (dialogItself) {
												var dateFormat = '%Y-%m-%d';
												var startDate = $("#promotionStartDate").data('datepicker').getFormattedDate('yyyy/mm/dd');
												var ed = $("#promotionEndDate").text().split('/'); 
												//var endDate = Highcharts.dateFormat(dateFormat, new Date(ed[2], parseInt(ed[1]) - 1, ed[0]));
												
												var endDate = ed[2] + '/' + ed[1] + '/' + ed[0];
												
												if( startDate !=  Highcharts.dateFormat('dateFormat', event.point.low)){
													$scope.promotionChangeData[promoId] = {
														promoId : promoId,
														startDate : startDate,
														endDate : endDate,
														oldStartDate: Highcharts.dateFormat(dateFormat, event.point.low),
														oldEndDate:Highcharts.dateFormat(dateFormat, event.point.high) 
													};
													
													$scope.updatePromoPopover();
													/*$scope.popoverTemplate = 
														'<table border="1">'+
															'<thead>'+
																'<tr>'+
																	'<th>Promo Name</th>'+
																	'<th>Start Date</th>'+
																	'<th>End Date</th>'+
																	'<th>Delete</th>'+
																'</tr>'+
															'</thead>'+
															'<tbody>';
													
													$.each($scope.promotionChangeData, function(key,value){
														$scope.popoverTemplate = $scope.popoverTemplate +
																'<tr>'+
																	'<td>'+ value.promoName + '</td>'+
																	'<td>' + value.startDate +'</td>'+
																	'<td>' + value.endDate + '</td>'+
																	'<td> <button  class="btn btn-primary btn-simple btn-xs details-control pop-del-promo-btn" data-toggle="tooltip" tooltip-title="Remove Promotion" data-title="Remove" data-promoId="'+ value.promoName + '"><i class="fa fa-minus"></i></button></td>' +
																'</tr>';
													});
													
													$scope.popoverTemplate = $scope.popoverTemplate +
															'</tbody>'+
														'</table>';
													
													var options = {
															title: 'Promotion Changed Date <button  class="btn btn-primary btn-simple btn-xs details-control pop-save-promo-btn" data-toggle="tooltip" tooltip-title="Save Changes" data-title="Save changes">Save changes</button>',
															singleton: true,
															placement: 'bottom',
															html:true
													};
													$('.saveDateChange').popover(options);*/
													//event.point.update = LocalThis.update;
													//event.point.update({color:"#FFA799"},true);
												}
												
												if(Object.keys($scope.promotionChangeData).length >0){
													$('.saveDateChange').prop('disabled',false);
												}
												dialogItself.close();
											}
										}, {
											label: 'Cancel',
											action: function (dialogItself) {
												dialogItself.close();
											}
										}]
							        });
			                    }
							}
						}]
					});
				},
				error: function (e) {
					$('.ajaxLoader').hide();
					console.error(e);
				}
			});
		}
	}
	$scope.init();
}]);

angular.module('PEAWorkbench').controller("manageDataQualityRejectController", ['$scope', '$rootScope', '$compile', '$http', 'DTOptionsBuilder', 'DTColumnBuilder', 'services', '$timeout', function ($scope, $rootScope, $compile, $http, DTOptionsBuilder, DTColumnBuilder, services, $timeout) {
	$scope.services = services;
	var currentPage ="DQ_REJECTS", chkBeforeSendReqMethod = currentPage + 'ChkBeforeSendReq';
	
	$scope.defaultDateFormat = $('#defaultDateFormat').val();
	
	$scope[currentPage] = {};
	$scope[currentPage]['inputParam'] = [ {
		id: 'dataQualityProcessName',
		type: 'select',
		format: 'multiSelect',
		reqParam: 'fltrProcess',
		//defaultValue: $scope.fltrProcess
	},{
		id: 'dataQualityFileName',
		type: 'select',
		format: 'multiSelect',
		reqParam: 'fltrFile',
		//defaultValue: $scope.fltrFile
	},{
		id: 'dataQualityIssueName',
		type: 'select',
		format: 'multiSelect',
		reqParam: 'fltrIssue',
		//defaultValue: $scope.fltrIssue
	},{
		id: 'dataQualityDate',
		type: 'daterangepicker',
		reqParam: 'fltrdataQualityDate'
	}];
	
	$scope.applyFilter = function () {
		
		$scope[chkBeforeSendReqMethod] = undefined;
		$("#" + currentPage + "TableId_processing").html('Processing...');
		
		if ($scope['dtInstance' + currentPage])
			$scope['dtInstance' + currentPage].reloadData();
	}
	$scope.init = function () {
		
		$('input[name="datefilter"]').daterangepicker({
			autoUpdateInput: false,
			locale: {
				cancelLabel: 'Clear'
			},
			opens: 'center',
			format: $scope.defaultDateFormat,
			//maxDate: new Date(),
			startDate: moment().subtract(29, 'days')
		});
		$('input[name="datefilter"]').on('apply.daterangepicker', function (ev, picker) {
			$(this).val(picker.startDate.format($scope.defaultDateFormat) + ' - ' + picker.endDate.format($scope.defaultDateFormat));
		});

		$('input[name="datefilter"]').on('cancel.daterangepicker', function (ev, picker) {
			$(this).val('');
		});
		
		$.getJSON($('#contextPath').val() + '/dataQualityStatusMeta', function (data) {
			$scope.services.select.setOptions({
				selectId: 'dataQualityProcessName',
				optionsData: data.processName,
				inputParam: {},
				multipleSelect: {
					filter: true,
					placeholder: 'Select Process',
					width: '100%',
					single: true,
//					defaultFirstValueIfNotSelected: true
				},
				onComplete: function () {
					$('#dataQualityProcessName').on('change', function () {
						$("#dataQualityFileName").multipleSelect("uncheckAll");
						$("#dataQualityIssueName").multipleSelect("uncheckAll");
					});
				}
			}, true);
			$scope.services.select.setOptions({
				selectId: 'dataQualityFileName',
				optionsData: data.filename,
				inputParam: {},
				multipleSelect: {
					filter: true,
					placeholder: 'Select File',
					width: '100%',
					single: true,
//					defaultFirstValueIfNotSelected: true
				},
			}, true);
			$scope.services.select.setOptions({
				selectId: 'dataQualityIssueName',
				optionsData: data.issuename,
				inputParam: {},
				multipleSelect: {
					filter: true,
					placeholder: 'Select Issue',
					width: '100%',
					single: true,
//					defaultFirstValueIfNotSelected: true
				},
			}, true);
		});
		
		$scope[chkBeforeSendReqMethod] = function () {
			return false;
		};
		
		$scope.saveEditChanges = function () {
			$('.ajaxLoader').show();
			$.ajax({
				type: 'POST',
				url: $('#contextPath').val() + '/saveDataQualityStatus',
				data: {
					editedStatus: JSON.stringify($scope.editedStatus)
				},
				success: function (response) {
					$('.ajaxLoader').hide();
					var resp = JSON.parse(response), type = undefined;
					if (resp.SUCCESS != 'TRUE') {
						type = 'danger';
					}
					services.showNotification(resp.Message, {
						type: type
					});
					
					if ($scope['dtInstance' + currentPage])
						$scope['dtInstance' + currentPage].reloadData();
				},
				error: function (e) {
					$('.ajaxLoader').hide();
					console.error(e);
					services.showNotification("Something went wrong, Please try later.", {
						type: 'danger'
					});
				}
				
			})
			
		};
		
		$scope.editedStatus = undefined;
		
		$scope.services.datatableInit({
			tables: [currentPage],
			http: $http,
			scope: $scope,
			rootScope: $rootScope,
			compile: $compile,
			dtColumnBuilder: DTColumnBuilder,
			dtOptionsBuilder: DTOptionsBuilder,
			processingMsg: 'Select Filter',
			onInitComplete:function() {
				$('#' + currentPage + 'TableId_length').append( $( '<button class="btn btn-primary ng-binding" id="saveChanges" data-original-title="" title="" style="margin-left:10px;">Save Changes</button>' ) );

				$('#saveChanges').confirmation({
					singleton: true,
					title: 'Are you sure want to save the changes?',
					placement: 'bottom',
					onConfirm: function () {
						$scope.saveEditChanges();
					}
				});
			},
			chkBeforeSendReq: function () {
				if ($scope[chkBeforeSendReqMethod])
					return $scope[chkBeforeSendReqMethod]();
				return true;
			},
			onDrawCallback: function () {
				$('[class^=' + currentPage + '-ef-select]').off('change');
				$('[class^=' + currentPage + '-ef-select]').on('change', function (e) {
					var _this = this;
					$(_this).addClass('border-red');
					var rejectId = $(_this).attr('data-rowKey');
					var status = $(_this).val();
					if ($scope.editedStatus == undefined) {
						$scope.editedStatus = {};
					}
					
					if ($scope.editedStatus[status] == undefined) {
						$scope.editedStatus[status] = [rejectId];
					}
					else {
						if ($scope.editedStatus[status].indexOf(rejectId) < 0)
							$scope.editedStatus[status].push(rejectId);
					}
				});
			},
			isTable: false
		});
		
	}
	$scope.init();
}]);

angular.module('PEAWorkbench').controller("manageBusinessRulesController", ['$scope', '$rootScope', '$compile', '$http', 'DTOptionsBuilder', 'DTColumnBuilder', 'services', '$timeout', function ($scope, $rootScope, $compile, $http, DTOptionsBuilder, DTColumnBuilder, services, $timeout) {
	$scope.services = services;
	var currentPage = 'ManageBusinessRules';
	
	$scope.data = {
		Constraints: [{
				key: '==',
				value: 'Equals'
			}, {
				key: '!=',
				value: 'Not Equals'
			}, {
				key: '<',
				value: 'Less Than'
			}, {
				key: '<=',
				value: 'Less Than & Equal'
			}, {
				key: '>',
				value: 'Greater Than'
			}, {
				key: '>',
				value: 'Greater Than & Equal'
			}
		],
		ConstraintColumns: [],
		ActionColumns: [],
	}
	
	$scope.applyActions = function () {
		
		function fnClick (opts) {
			var opts = opts || {};
			var x = function () {
				var _thisEl = $(this);
				var vRuleName = _thisEl.attr('data-RuleName');
				
				$('.ajaxLoader').show();
				$.ajax({
					type: 'POST',
					url: $('#contextPath').val() + '/manageBR/' + opts.status,
					data: {
						RuleName: vRuleName
					},
					success: function (response) {
						$('.ajaxLoader').hide();
						var response = JSON.parse(response);
						
						if (response['SUCCESS'] || response['SUCCESS'] == 'true') {
							$scope.services.showNotification(response['Message']);
						} else {
							$scope.services.showNotification(response['Message'], {
								type: 'danger'
							});
						}
						
						$scope['dtInstance' + currentPage].reloadData($.noop, false);
					},
					failure: function() {
						$('.ajaxLoader').hide();
						var msg = 'Something went wrong, please try later!';
						services.showNotification(msg);
					}
				});
			};
			return x;
		}
		
		$('.BRDelete').unbind('click');
		$('.BRDelete').click(fnClick({
			status: 'delete'
		}));
		
		$('.BREnable').unbind('click');
		$('.BREnable').click(fnClick({
			status: 'enable'
		}));
		
		$('.BRDisable').unbind('click');
		$('.BRDisable').click(fnClick({
			status: 'disable'
		}));
		
		$('.BREdit').unbind('click');
		$('.BREdit').click($scope.addBR);
	}
	
	$scope.init = function () {
		$scope.services.datatableInit({
			tables: [currentPage],
			http: $http,
			scope: $scope,
			rootScope: $rootScope,
			compile: $compile,
			dtColumnBuilder: DTColumnBuilder,
			dtOptionsBuilder: DTOptionsBuilder,
//			processingMsg: 'Select Filter',
			onInitComplete:function() {
				$( '#' + currentPage + 'TableId_length' ).append( 
					$('<button class="btn btn-primary ng-binding BRAdd" id="addBRBtn" data-original-title="" title="Add New Business Rule" style="margin-left:10px;">Add</button>')
				);
				
				$('#addBRBtn').click($scope.addBR);
			},
			onDrawCallback: function () {
				$scope.applyActions();
			},
			isTable: false
		});
	}
	
	function getDivConstraintsChild(num, opts) {
		return "<div class='form-group row divConstraintsChild" + num + "' >" +
			"<label for='Constraints' class='col-sm-3 control-label'>Constraints</label>" +
			"<div class='col-sm-3' style='padding:0 5px 0 15px;'><select class='form-control' name='ConstraintColumn" + num + "' id='ConstraintColumn" + num + "' placeholder='Column'></select></div>" +
			"<div class='col-sm-2' style='padding:0 5px 0 5px;'><select class='form-control' name='ConstraintCondition" + num + "' id='ConstraintCondition" + num + "' placeholder='Constraint'></select></div>" +
			"<div class='col-sm-3' style='padding:0 15px 0 5px;'><input type='text' class='form-control' id='ConstraintValue" + num + "' name='ConstraintValue" + num + "' placeholder='Value' maxlength ='64'></div>" +
			"<div class='constraintsAddDelBtn'><div class='col-sm-1' style='padding:0 15px 0 5px;'><button  class='BRAddConstraintBtn btn btn-primary btn-simple btn-xs details-control' data-placement='left' data-toggle='tooltip' tooltip-title='Add Constraints' data-title='Add Constraints' data-original-title='' title='' style='margin:2px 2px;' data='row" + num + "' ><i class='fa fa-plus' id='row" + num + "' ></i></button></div></div>" +
		"</div>";
	}
	
	function getDivActionsChild(num) {
		return "<div class='form-group row divActionsChild" + num + "'>" +
			"<label for='sector' class='col-sm-3 control-label'>Action Columns</label>" +
			"<div class='col-sm-3' style='padding:0 5px 0 15px;'><select class='form-control' name='ActionColumn" + num + "' id='ActionColumn" + num + "' placeholder='Column'></select></div>" +
			"<div class='col-sm-3' style='padding:0 15px 0 5px;'><input type='text' class='form-control' id='ActionValue" + num + "' name='ActionValue" + num + "' placeholder='Value' maxlength ='64'></div>" +
			"<div class='actionsAddDelBtn'><div class='col-sm-1' style='padding:0 15px 0 5px;'><button  class='BRAddActionBtn btn btn-primary btn-simple btn-xs details-control' data-placement='left' data-toggle='tooltip' tooltip-title='Add Action Columns' data-title='Add Action Columns' data-original-title='' title='' style='margin:2px 2px;' data='row" + num + "' ><i class='fa fa-plus' id='row" + num + "' ></i></button></div></div>" +
		"</div>";
	}
	
	var clickBRAddConstraintBtn = function (opts) {
		//debugger;
		$scope.initConstrainsRowNum = $scope.initConstrainsRowNum + 1;
		var opts = opts || {};
		//var rowNum = $('.divConstraints')[0].children.length + 1;
		var rowNum = $scope.initConstrainsRowNum;
		var delBtn = "<div class='col-sm-1' style='padding:0 15px 0 5px;'><button  class='BRRemConstraintBtn btn btn-primary btn-simple btn-xs' data-placement='left' data-toggle='tooltip' tooltip-title='Remove Constraints' data-title='Remove Constraints' data-original-title='' title='' style='margin:2px 2px;' data='row' ><i class='fa fa-minus'></i></button></div>";
		$('.constraintsAddDelBtn').empty();
		$('.constraintsAddDelBtn').append(delBtn);
		$('.divConstraints').append(getDivConstraintsChild(rowNum, opts));
		
		
		setBRFormConstraintSelectFields(rowNum);
		if (opts.constraint) {
			$('#ConstraintColumn' + rowNum).val(opts.constraint.column);
			$('#ConstraintCondition' + rowNum).val(opts.constraint.condition);
			$('#ConstraintValue' + rowNum).val(opts.constraint.value);
		}
		
		$('.BRAddConstraintBtn').unbind('click');
		$('.BRAddConstraintBtn').click(clickBRAddConstraintBtn);
		
		$('.BRRemConstraintBtn').unbind('click');
		$('.BRRemConstraintBtn').click(function(){
			$(this).parents().eq(2).remove();
		});
	}
	
	var clickBRAddActionBtn = function (opts) {
		//var rowNum = $('.divActions')[0].children.length + 1;
		$scope.initActionsRowNum = $scope.initActionsRowNum + 1;
		var rowNum = $scope.initActionsRowNum;
		var delBtn = "<div class='col-sm-1' style='padding:0 15px 0 5px;'><button  class='BRRemActionBtn btn btn-primary btn-simple btn-xs' data-placement='left' data-toggle='tooltip' tooltip-title='Remove Action Column' data-title='Remove Action Column' data-original-title='' title='' style='margin:2px 2px;' data='row' ><i class='fa fa-minus' ></i></button></div>";
		$('.actionsAddDelBtn').empty();
		$('.actionsAddDelBtn').append(delBtn);
		$('.divActions').append(getDivActionsChild(rowNum));
		
		setBRFormActionSelectFields(rowNum);
		if (opts.action) {
			$('#ActionColumn' + rowNum).val(opts.action.column);
			$('#ActionValue' + rowNum).val(opts.action.value);
		}
		
		$('.BRAddConstraintBtn').unbind('click');
		$('.BRAddConstraintBtn').click(clickBRAddConstraintBtn);
		
		$('.BRAddActionBtn').unbind('click');
		$('.BRAddActionBtn').click(clickBRAddActionBtn);
		
		$('.BRRemActionBtn').unbind('click');
		$('.BRRemActionBtn').click(function(){
			$(this).parents().eq(2).remove();
		});
		
	}
	
	
	
	function setBRFormConstraintSelectFields(num) {
		$('#ConstraintColumn' + num).empty();
		$.each($scope.data.ConstraintColumns, function (idx, v) {
			$('#ConstraintColumn' + num).append($('<option>', {
				value: v,
				text: v
			}));
		});

		$('#ConstraintCondition' + num).empty();
		$.each($scope.data.Constraints, function (idx, v) {
			$('#ConstraintCondition' + num).append($('<option>', {
				value: v.key,
				text: v.value
			}));
		});
	}
	
	function setBRFormActionSelectFields(num) {
		$('#ActionColumn' + num).empty();
		$.each($scope.data.ActionColumns, function (idx, v) {
			$('#ActionColumn' + num).append($('<option>', {
				value: v,
				text: v
			}));
		});
	}
	
	function setEditFields (editData, isAddCalled) {
		if (editData.constraints != null) {
			var alConstratins = editData.constraints.split(' AND ');
			$.each(alConstratins, function (idx, constraintTuple) {
				var constraint = {};
				constraint.column = constraintTuple.split(' ')[0];
				constraint.condition = constraintTuple.split(' ')[1];
				constraint.value = constraintTuple.split(' ')[2];
				
				clickBRAddConstraintBtn({
					constraint: constraint
				});
			});
		}
		
		if (editData.actionColumns != null) {
			var alActions = editData.actionColumns.split(' AND ');
			$.each(alActions, function (idx, actionTuple) {
				var action = {};
				action.column = actionTuple.split(' ')[0];
				action.condition = actionTuple.split(' ')[1];
				action.value = actionTuple.split(' ')[2];
				
				clickBRAddActionBtn({
					action: action
				});
			});
		}
		
		if (isAddCalled) {
			setBRFormConstraintSelectFields(1);
			setBRFormActionSelectFields(1);
		}
		
		$('select').select2();
	}
	
	$scope.addBR = function () {
		var title = 'Add Business Rule'
		var _thisEl = $(this), isAddCalled = false; 
		var opts = {
			editData: {}
		};
		if (_thisEl.attr('class') != null && _thisEl.attr('class').indexOf('BREdit') > -1) {
			opts.editData.ruleName = _thisEl.attr('data-RuleName');
			opts.editData.description = _thisEl.attr('data-Description');
			opts.editData.constraints = _thisEl.attr('data-Constraints');
			opts.editData.actionColumns = _thisEl.attr('data-ActionColumns');
			
			console.debug('Edit Data', opts.editData);
		}
		if (opts.editData.ruleName == null) {
			isAddCalled = true;
		}
		
		var msg = 
			"<div class='box-body'>" +
				"<form  class='form-horizontal addBRform' autocomplete='off'  >" +
					"<div class=''>" + 
						"<div class='form-group row'>" +
						"<span style='color: red' class='col-sm-12' id='br-add-error-msg'></span>" +
					"</div>" +
					"<div class='form-group row'>" +
						"<label for='RuleName' class='col-sm-3 control-label'>Rule Name</label>" +
						"<div class='col-sm-9'><input type='text' class='form-control' id='RuleName' name='RuleName' placeholder='Rule Name' maxlength ='64' value='" + (opts.editData.ruleName != null ? opts.editData.ruleName : '') + "'></div>" +
					"</div>" +
					"<div class='form-group row'>" +
						"<label for='Description' class='col-sm-3 control-label'>Description</label>" +
						"<div class='col-sm-9'><input type='text' class='form-control' id='Description' name='Description' placeholder='Description' maxlength ='64' value='" + (opts.editData.description != null ? opts.editData.description : '') + "'></div>" +
					"</div>" +
					"<div class='divConstraints'>" +
						(( !isAddCalled ) ? "" : getDivConstraintsChild(1))
						+
					"</div>" +
					"<div class='divActions'>" +
						(( !isAddCalled ) ? "" : getDivActionsChild(1))
						+
					"</div>" +
				"</form>" +
			"</div>";
		

		if(!isAddCalled){
			title = 'Edit Business Rule'
		}
		function formattedInput (data) {
			var x = {
				Constraint: {},
				Action: {}
			}; 
			var CONSTRAINT_COLUMN = 'ConstraintColumn', 
				CONSTRAINT_CONDITION = 'ConstraintCondition', 
				CONSTRAINT_VALUE = 'ConstraintValue',
				ACTION_COLUMN = 'ActionColumn',
				ACTION_VALUE = 'ActionValue';

			var txt = {
				Common: {
					RuleName: 'RuleName',
					Description: 'Description',
				},
				Constraint: {
					Column: 'ConstraintColumn', 
					Condition: 'ConstraintCondition', 
					Value: 'ConstraintValue'
				},
				Action: {
					Column: 'ActionColumn', 
					Value: 'ActionValue'
				}
			};	
				
			$.each(data, function (key, value) {
				$.each(txt.Constraint, function (k, txt1) {
					if (key.indexOf(txt1) > -1) {
						var idxx = key.replace(txt1, '');
						if (x.Constraint[idxx] == null) 
							x.Constraint[idxx] = {};
						var con = x.Constraint[idxx];
						con[k] = value;
						x.Constraint[idxx] = con;
					}
				});
				
				$.each(txt.Action, function (k, txt1) {
					if (key.indexOf(txt1) > -1) {
						var idxx = key.replace(txt1, '');
						if (x.Action[idxx] == null) 
							x.Action[idxx] = {};
						var con = x.Action[idxx];
						con[k] = value;
						x.Action[idxx] = con;
					}
				});
				
				$.each(txt.Common, function (k, txt1) {
					if (key.indexOf(txt1) > -1) {
						x[k] = value;
					}
				});
			});
			var x1 = {};
			x1.Constraint = Object.values(x.Constraint);
			x1.Action = Object.values(x.Action);
			x1.RuleName = x.RuleName;
			x1.Description = x.Description;
			
			return x1;
		}
		
		BootstrapDialog.show({
			title: title,
			id: 'AddBRDialog',
			message: msg,
			onshown: function (dialog) {
//				$('.box-body form input').keyup(function () {
//					if ($(this).val().trim() == "") {
//						$(this).css("border-color", "red").addClass("invalid");
//						return false;
//					} else {
//						$(this).css("border-color", "#d2d6de").removeClass("invalid");
//					}
//				});
				if(!isAddCalled){
					$('#RuleName').prop('readonly', true);
				}
				$scope.initConstrainsRowNum = 0;
				$scope.initActionsRowNum = 0;
				if ($scope.data.ConstraintColumns.length < 1) {
					$.ajax({
						type: 'GET',
						url: $('#contextPath').val() + '/manageBR/constrints/getColumnList',
						data: {},
						success: function (response) {
							var jResp = JSON.parse(response);
							if (jResp['SUCCESS'] != null && jResp['SUCCESS']) {
								if (jResp.COLUMNS != undefined) {
									$scope.data.ConstraintColumns = jResp.COLUMNS;
									$scope.data.ActionColumns = jResp.COLUMNS;

									setEditFields(opts.editData, isAddCalled);
								}
							}
							else {
								$('#lookup-add-error-msg').html('Unable to fetch columns  - ' + jResp.Message);
							}
						},
						failure: function () {
							$('#lookup-add-error-msg').html('Unable to fetch columns');
						}
					});
				}
				else {
					setEditFields(opts.editData, isAddCalled);
				}
				
				
			},
			buttons: [{
				id: 'btn-save',
				label: isAddCalled ? 'Add' : 'Update',
				autospin: false,
				action: function (dialogRef) {
//					$('.' + currentDivID + 'form input').each(function (index) {
//						if ($(this).val().trim() == "") {
//							$(this).css("border-color", "red").addClass("invalid");
//						} else {
//							$(this).css("border-color", "#d2d6de").removeClass("invalid");
//						}
//					});
//					if ($(".invalid").length == 0) {
//						$('.ajaxLoader').show();
//						
						var data = $('.addBRform').serializeArray().reduce(function (obj, item) {
							obj[item.name] = item.value;
							return obj;
						}, {});
						
						data = formattedInput(data);
						
						$('.ajaxLoader').show(); 
						$.ajax({
							type: 'POST',
							url: $('#contextPath').val() + '/manageBR/add',
							data: {
								editData: JSON.stringify(data)
							},
							success: function (response) {
								$('.ajaxLoader').hide();
								var response = JSON.parse(response);
								
								if (response['SUCCESS'] || response['SUCCESS'] == 'true') {
									$scope.services.showNotification(response['Message']);
								} 
								else {
									$scope.services.showNotification(response['Message'], {
										type: 'danger'
									});
								}
								dialogRef.close();
								$scope['dtInstance' + currentPage].reloadData($.noop, false);
							},
							failure: function () {
								$('.ajaxLoader').hide();
								dialogRef.close();
								var msg = 'Something went wrong, please try later!';
								services.showNotification(msg, {
									type: 'danger'
								});
							}
						})
//					}
				}
			}, {
				id: 'btn-cancel',
				label: 'Cancel',
				action: function (dialogRef) {
					dialogRef.close();
					$('.modal-backdrop').remove();
				}
			}]
		});
	}
	
	$scope.init();

}]);	
