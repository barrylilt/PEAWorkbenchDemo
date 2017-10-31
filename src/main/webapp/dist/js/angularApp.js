var workbench = angular.module('PEAWorkbench', ['ngRoute', 'datatables'])
.run(function($rootScope) {
	$rootScope.test = new Date();
});
workbench.config(function ($routeProvider, $provide, $httpProvider) {
	var initInjector = angular.injector(['ng']);
	var $http = initInjector.get('$http');
	$routeProvider.
	otherwise({
		controller: 'dashboardController',
		templateUrl: 'partials/dashboard.html'
	});
	$http.get('translation/menu.json').success(function(response) {
		var menuJson = response;
		function setURLInRouteProvider (json) {
			$.each(json, function(key, value) {
				if (!jQuery.isEmptyObject(value)) {
					if (!jQuery.isEmptyObject(value.subMenu)) {
						setURLInRouteProvider(value.subMenu);
					}
					else if (value.href != undefined && value.templateUrl != undefined && value.controller != undefined) {
						var isAccessible = true;
						var accessibleGroups = value.accessGroup;
						if (accessibleGroups != undefined) {
							isAccessible = false;
							var userGroups = $('#userGroups').text();
							$.each(accessibleGroups.split(','), function (idx, v) {
								if (userGroups != undefined && userGroups.indexOf(v) > -1) {
									isAccessible = true;
								}
							});
						} 
						
						if (isAccessible) {
							$routeProvider = $routeProvider.when('/' + value.href, {
								templateUrl: value.templateUrl,
								controller:	value.controller
							});
						}
					}
				}
			});
		}
		
		setURLInRouteProvider(menuJson);
		
		$routeProvider.
			otherwise({
				redirectTo: '/dashboard'
			});
	});
	
	/*$.each(urls, function(key, value) {
		if (value.restrictedRoles != 'ABC')
			$routeProvider = $routeProvider.when('/' + key, value);
	});*/
	
	/*$routeProvider.
	when('/dashboard', {
		templateUrl: 'partials/dashboard.html',
		controller: 'dashboardController'
			
	}).
	when('/fileUpload', {
		templateUrl: 'partials/fileUpload.html',
		controller: 'fileUploadController'
	}).
	when('/monitorDataFileStatus', {
		templateUrl: 'partials/monitorDataFileStatus.html',
		controller: 'monitorDataFileStatusController'
	}).
	when('/manageLookupTables', {
		templateUrl: 'partials/manageLookupTables.html',
		controller: 'manageLookupTablesController'
	}).
	when('/mappingData', {
		templateUrl: 'partials/mappingData.html',
		controller: 'mappingDataController'
	}).
	when('/monitorJobLogs', {
		templateUrl: 'partials/monitorJobLogs.html',
		controller: 'monitorJobLogsController'
	}).
	when('/viewHierarchies', {
		templateUrl: 'partials/viewHierarchies.html',
		controller: 'viewHierarchiesController'
	}).
	when('/viewDataConfig', {
		templateUrl: 'partials/viewDataConfig.html',
		controller: 'configureDataViewsController'
	}).
	when('/viewExceptions', {
		templateUrl: 'partials/viewExceptions.html',
		controller: 'viewExceptionsController'
	}).
	when('/viewPromotedProducts', {
		templateUrl: 'partials/viewPromotedProducts.html',
		controller: 'viewPromotedProductController'
	}).
	when('/viewWeeklyPromotions', {
		templateUrl: 'partials/viewWeeklyPromotions.html',
		controller: 'viewWeeklyPromotionsController'
	}).
	when('/viewDataAvailability', {
		templateUrl: 'partials/viewDataAvailability.html',
		controller: 'viewDataAvailabilityStatsController'
	}).
	when('/createDatasets', {
		templateUrl: 'partials/createDatasets.html',
		controller: 'createDatasetsController'
	}).
	when('/exportDatasets', {
		templateUrl: 'partials/exportDatasets.html',
		controller: 'exportDatasetsController'
	}).
	when('/userProfile', {
		templateUrl: 'partials/userProfile.html',
		controller: 'userProfileController'
	}).
	otherwise({
		redirectTo: '/dashboard'
	});*/
	
	
	var httpInterceptor = function($provide, $httpProvider) {
		$provide.factory('httpInterceptor', function($q) {
			return {
				response : function(response) {
					if (response.data && response.data.indexOf && response.data.indexOf('name="password"') > -1) {
						window.document.location = 'login';
					}
					return response || $q.when(response);
				},
				responseError : function(rejection) {
					if (rejection.status === 401) {
						// you are not autorized
					}
					if (rejection.status === -1) {
						window.document.location = 'login';
					}
					return $q.reject(rejection);
				}
			};
		});
		$httpProvider.interceptors.push('httpInterceptor');
	};
	
	httpInterceptor($provide, $httpProvider);
	
});

workbench.service('translationService', function($resource) {  
    this.getTranslation = function($scope, language) {

        var languageFilePath = 'translation_' + language + '.json';
        $resource(languageFilePath).get(function (data) {
            $scope.translation = data;
        });
    };
});

workbench.factory('services', function() {                                                                                                                                                   
    return {
        datatableInit: function(opts) {
        	var datatable = {};
        	datatable.drawCallback = function (opts) {
        		opts = opts || {};
        		var btnIdSeperator = '|=';
        		opts['preURL'] = datatable.opts.scope['preURL' + opts.tableName];
        		$('[id*="' + opts.tableName + btnIdSeperator + 'deletebtn' + btnIdSeperator + '"]').confirmation({
        			singleton: true,
        			onConfirm: function () {
	        			var currentDeleteBtn = this[0], input = {};
						if (currentDeleteBtn.id.split(btnIdSeperator).length < 4 || currentDeleteBtn.id.split(btnIdSeperator)[3].trim().length < 1) {
							console.warn('Key column is no defined properly');
							return;
						}
						input['key'] = currentDeleteBtn.id.split(btnIdSeperator)[3];
						$('.ajaxLoader').show();
						$.ajax({
							type : 'POST',
							url : opts.preURL + '/delete',
							data : input,
							success : function(response) {
								datatable.opts.scope['dtInstance' + opts.tableName].reloadData($.noop, false);
								$('.ajaxLoader').hide();
								var response = JSON.parse(response);
								datatable.opts.scope.services.showNotification(response['Message']);
								datatable.opts.scope['dtInstance'+opts.tableName]._renderer.rerender(); 
							},
							error : function(e) {
								datatable.opts.scope['dtInstance' + opts.tableName].reloadData($.noop, false);
								$('.ajaxLoader').hide();
								console.error(e);
							}
						});
						
					}
        		});
				
				$('[id*="' + opts.tableName + btnIdSeperator + 'savebtn' + btnIdSeperator + '"]').removeAttr('click');
				//$('[id*=' + opts.tableName + '-savebtn-]').click(function () {
				$('[id*="' + opts.tableName + btnIdSeperator + 'savebtn' + btnIdSeperator + '"]').confirmation({ 
					singleton: true,
					onConfirm: function(){
						var currentSaveBtn = this[0];
						
						var rowId = currentSaveBtn.id.split(btnIdSeperator)[2];
						var rowKey = currentSaveBtn.id.split(btnIdSeperator)[3];
						var input = {}, isEmptyField = false;
						if (currentSaveBtn.id.split(btnIdSeperator).length < 4 || currentSaveBtn.id.split(btnIdSeperator)[3].trim().length < 1) {
							console.warn('Key column is not defined properly');
							return;
						} 
						input['key'] = currentSaveBtn.id.split(btnIdSeperator)[3];
						$('[class^=' + opts.tableName + '-ef-' + rowId + ']').each(function (i, el) {
							if (el.value != null && el.value.trim().length > 0) {
								input[$(el).attr('class').split(' ')[0].split('-')[4]] = el.value.trim();
							}
							else {
								isEmptyField = true;
								return false;
							}
						});
						if (isEmptyField) {
							datatable.opts.scope.services.showNotification('Editable field cannot be empty!', {
								type: 'danger'
							});
							return false;
						}
						$('.ajaxLoader').show();
						$.ajax({
							type : 'POST',
							url : opts.preURL + '/update',
							data : input,
							success : function(response) {
								datatable.opts.scope['dtInstance' + opts.tableName].reloadData($.noop, false);
								$('.ajaxLoader').hide();
								var response = JSON.parse(response);
								datatable.opts.scope.services.showNotification(response['Message']);
								/*datatable.opts.scope['dtInstance'+opts.tableName]._renderer.rerender(); */
							},
							error : function(e) {
								datatable.opts.scope['dtInstance' + opts.tableName].reloadData($.noop, false);
								$('.ajaxLoader').hide();
								console.error(e);
							}
						});
					}
				})
				$('[id*="' + opts.tableName + btnIdSeperator + 'savebtn' + btnIdSeperator + '"]').confirmation('hide')
						//});
        	}
        	
        	datatable.getBlankDTOptions = function (opts) {
    			return datatable.opts.dtOptionsBuilder.newOptions()
    				.withOption('ajax', {
    					dataSrc : 'data',
    					url : $('#contextPath').val() + '/datatable/ng/blank',
    					type : 'GET',
    				})
    		}
    		
        	datatable.getDTOptions = function (opts) {
    			var opts = opts || {};
    			
    			opts['scrollY'] = (datatable.opts.scrollY || 300);
    			
    			var exportBtn = '<div class="dropdown" style="float: left; margin: 0 5px 0 0;">' +
    			    '<button class="btn btn-primary dropdown-toggle" id="menu1" type="button" data-toggle="dropdown">Export<span class="caret"></span></button>' +
    			    '<ul class="dropdown-menu" role="menu" aria-labelledby="menu1">' +
    			      '<li role="presentation"><a role="menuitem" tabindex="-1" href="#">CSV</a></li>' +
    			      '<li role="presentation"><a role="menuitem" tabindex="-1" ng-click="exportAction()">Excel</a></li>' +
    			    '</ul>' +
    			  '</div>';
    			
    			var applyFilterBtn = '<div style="float: left; margin: 0 5px 0 0;" ><button class="btn btn-primary" id="' + opts.tableName + '-apply-filter-btn" type="button">Apply Filter</button></div>';
    			
    			var lang = { 
					decimal: '', 
					emptyTable: 'No data available in table',
					info: 'Showing _START_ to _END_ of _TOTAL_ rows', 
					infoEmpty: 'Showing 0 to 0 of 0 rows', 
					infoFiltered: '(filtered from _MAX_ total rows)', 
					infoPostFix: '',
					thousands: ',',
					lengthMenu: 'Show _MENU_ rows', 
					loadingRecords: 'Loading...', 
					processing: (datatable.opts.processingMsg != null) ? datatable.opts.processingMsg : 'Processing...',
					search: '<div class="input-group"><span class="input-group-addon"><span class="glyphicon glyphicon-search" ></span></span>', 
					searchPlaceholder: 'Search',
					zeroRecords: 'No matching records found',
					paginate: { 'first': 'First', 'last': 'Last', 'next': 'Next', 'previous': 'Previous' },
					aria: { 'sortAscending': ': activate to sort column ascending', 'sortDescending': ': activate to sort column descending' }
				}
    			
//    			if (datatable.opts.buttons) {
//	    			if (datatable.opts.buttons.export) {
//	    				lang.search = exportBtn + lang.search;
//	    			}
//	    			if (datatable.opts.buttons.applyFilter) {
//	    				lang.search = applyFilterBtn + lang.search;
//	    			}
//    			}
    			
    			return datatable.opts.dtOptionsBuilder.newOptions()
    				.withOption('ajax', {
    					dataSrc : 'data',
    					url : datatable.opts.scope['preURL' + opts.tableName] + '/data',
    					type : 'POST',
    					cache:    false,
    					beforeSend: function (x,y) {
    						
    						//var months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
    						var inputData = '';
    						//console.log(opts.tableName);
    						
    		    			if (datatable.opts.scope[opts.tableName] && datatable.opts.scope[opts.tableName]['inputParam']) {
    		    				//console.log(datatable.opts.scope[opts.tableName]['inputParam']);
    		    				inputData = datatable.opts.scope.services.getInputUrlParam(datatable.opts.scope[opts.tableName]['inputParam'], datatable.opts);
    		    			}
    		    			y.data += '&' + inputData;
    		    			if (datatable.opts.chkBeforeSendReq) {
    		    				return datatable.opts.chkBeforeSendReq();
    		    			}
    					}
    				})	
    				.withDataProp('data')
    				.withOption('processing', true)
    				.withOption('serverSide', true)
    				.withOption('scrollY', opts.scrollY)
    				.withOption('scrollX', true)
    				.withOption('destroy',true)
    				.withOption('lengthMenu', datatable.opts.lengthMenu || [10, 20, 50, 100])
    				.withOption('order', datatable.opts.order || [0, 'asc'])
    				.withOption('pagingType',datatable.opts.pagingType ||'full_numbers')
    				.withOption('fixedColumns',  datatable.opts.fixedColumns ||{leftColumns: 0})
    				.withOption('drawCallback', function (oSettings) {
    					datatable.drawCallback({
    						tableName: opts.tableName
    					});
    					
    					if (datatable.opts.onDrawCallback) {
    						datatable.opts.onDrawCallback();
    					}
    					$('#' + opts.tableName + ' .dataTables_scroll').css('visibility', 'visible');
    					/*if(datatable.opts.scope.search !== "" ){
    						var searchVar =  datatable.opts.scope.search;
    						datatable.opts.scope.search = "";
    						;
    						datatable.opts.scope.search.isInstanceSet= true;
    					}*/
    				})
    				.withOption('initComplete', function (oSettings) {
    					//$('.dropdown-toggle').dropdown();
    						
    					datatable.opts.scope.exportAction = function () {
    	            		//console.log(datatable.opts);
    	            	}
    					if (datatable.opts.onInitComplete) {
    						datatable.opts.onInitComplete();
    					}
    					// Apply Filter action
        				if (datatable.opts.buttons && datatable.opts.buttons.applyFilter) {
        					$('#' + opts.tableName + '-apply-filter-btn').click(function () {
        						//console.log(datatable.opts.scope);
        						datatable.opts.scope['dtInstance' + opts.tableName].reloadData($.noop, false);
        					});
        				}
    				})
    				.withOption('language',lang)
    				.withOption('searchHighlight', true)
    				.withPaginationType('full_numbers')
    				;
    		}

        	datatable.getDTColumns = function (opts) {
    			var columns = [], opts = opts || {}, colTooltip = {}, columnCls = {}, dtColCls = [];
    			
    			if (datatable.opts.scope[opts.tableName] && datatable.opts.scope[opts.tableName]['columnTooltip']) {
					colTooltip = datatable.opts.scope[opts.tableName]['columnTooltip'];
				}
    			
    			if (datatable.opts.scope[opts.tableName] && datatable.opts.scope[opts.tableName]['columnCls']) {
    				columnCls = datatable.opts.scope[opts.tableName]['columnCls'];
				}
    			angular.forEach(opts.COLUMNS, function(value, key) {
    				dtColCls = [];
    				value = datatable.opts.rootScope.translate(value, value);
    				if (datatable.opts.columnsToExclude && datatable.opts.columnsToExclude.indexOf(key) > -1) {
    					return true;
    				}
    				
    				if (colTooltip[columns.length]) {
    					//data-toggle="tooltip" data-placement="left" data-original-
    					value = '<span title="' + colTooltip[columns.length] + '">' + value + '</span>';
    				}
    				var x = datatable.opts.dtColumnBuilder.newColumn(''+key).withTitle(''+value);
    				
    				if (datatable.opts.colNotToSort && datatable.opts.colNotToSort.indexOf(columns.length) > -1) {
    					x = x.notSortable();
    				}
    				
    				if (opts.colWidthMap && opts.colWidthMap[key] != null) {
    					x = x.withOption('width', opts.colWidthMap[key] + 'px');
    				}
    				if (opts.colTypeMap && opts.colTypeMap[key] != null && opts.colTypeMap[key] == 'N') {
    					dtColCls.push('text-right-align');
    				}
    				if (columnCls && columnCls[columns.length]) {
    					dtColCls.push(columnCls[columns.length]);
    				}
    				x.withOption('class', dtColCls.join(' '));
    				
    				if (datatable.opts.columnToHide && datatable.opts.columnToHide.indexOf(key) > -1) {
    					x = x.notVisible();
    				}
    				
    				columns.push(x);
    			});
    			return columns;
    		}

        	datatable.initDatatable = function (opts) {
    			datatable.opts.http.get(datatable.opts.scope['preURL' + opts.tableName] + '/columns')
    				.success(function(response, status, headers, config) {
    					var opts = opts || {}, widthOpts = ' width="100%" ';
    					opts['COLUMNS'] = response.COLUMNS;
    					opts['tableName'] = response.tableName;
    					opts['colWidthMap'] = response.colWidthMap;
    					opts['colTypeMap'] = response.colTypeMap;
    					
    					if (datatable.opts.scope[opts.tableName]) {
    						datatable.opts.scope[opts.tableName]['columns'] = opts.COLUMNS;
    					}
    					else {
    						datatable.opts.scope[opts.tableName] = {};
    						datatable.opts.scope[opts.tableName]['columns'] = opts.COLUMNS;
    					}
    					
    					datatable.opts.scope['dtOptions' + opts.tableName] = datatable.getDTOptions(opts);
    					datatable.opts.scope['dtColumns' + opts.tableName] = datatable.getDTColumns(opts);
    					datatable.opts.scope['dtInstance' + opts.tableName] = {};

    					if (opts['colWidthMap'] && Object.keys(opts['colWidthMap']).length > 0) {
    						widthOpts = '';
    					}

    					if($("#"+opts.tableName+"TableId_wrapper").length <= 0 ){
    						var tableHTML = '<table id="' + opts.tableName + 'TableId" datatable="" dt-options="dtOptions' + opts.tableName + '" dt-columns="dtColumns' + opts.tableName + '" dt-instance="dtInstance' + opts.tableName + '"  class="table table-bordered table-striped" ' + widthOpts + '>';
	    					if (datatable.opts.scope[opts.tableName] && datatable.opts.scope[opts.tableName]['headerHTML'] != null) {
	    						tableHTML += datatable.opts.scope[opts.tableName]['headerHTML'];
	    					}

	    					tableHTML += '</table>';
	    					
	            			var compiled = datatable.opts.compile(tableHTML)(datatable.opts.scope);
	            			$('#' + opts.tableName).append(compiled);
    					}
    				});
    			
    			//console.log(datatable.opts.scope);
    		}
    		
    		/*datatable.reloadData = function () {
    			datatable.opts.scope.dtInstance.reloadData(function(data){ console.log(data)}, true);
    		}*/
    		
        	datatable.blankInit = function (opts) {
        		datatable.opts.scope['dtOptions' + opts.tableName] = datatable.getBlankDTOptions(opts);
        		datatable.opts.scope['dtColumns'  + opts.tableName] = [datatable.opts.dtColumnBuilder.newColumn('id').withTitle('ID').notVisible()];
    		}
        	datatable.init = function (opts) {
        		datatable.opts = opts;
        		
        		if (datatable.opts.chkBeforeSendReq == undefined) {
	        		datatable.opts.chkBeforeSendReq = function () {
	        			return true;
	        		}
        		}
        		
        		datatable.opts.scope.exportAction = function () {
            		//console.log(datatable.opts);
            	}
        		
        		angular.forEach(datatable.opts.tables, function(tab, key) {
        			var preURL = $('#contextPath').val() + '/datatable/' + tab + '/ng';
        			if (!datatable.opts.isTable) {
        				preURL =  $('#contextPath').val() + '/' + tab + '/ng';
        			}
        			datatable.opts.scope['preURL' + tab] = preURL;
//        			datatable.blankInit(angular.extend(opts, {
//    					tableName: tab
//    				}));
        			datatable.initDatatable(angular.extend(datatable.opts, {
    					tableName: tab,
    				}));
    			});
        	}
        	datatable.init(opts);
        },
        doSomethingElse: function() {
        	//console.log('doSomethingElse');
        },
        select: {
        	sortOptions: function (options) {
        		//var options = $('#masterFamilyNameList option');
        	    var arr = options.map(function(_, o) {
        	        return {
        	            t: o.text,
        	            v: o.value
        	        };
        	    }).get();
        	    arr.sort(function(o1, o2) {
        	        return o1.t.toLowerCase() > o2.t.toLowerCase() ? 1 : o1.t.toLowerCase() < o2.t.toLowerCase() ? -1 : 0;
        	    });
        	    options.each(function(i, o) {
        	    	o.value = arr[i].v;
        	        o.text = arr[i].t;
        	    });
        	},
        	setOptions: function (opts, eraseOldOptions) {
        		function sortOptions(options) {
            		//var options = $('#masterFamilyNameList option');
            	    var arr = options.map(function(_, o) {
            	        return {
            	            t: o.text,
            	            v: o.value
            	        };
            	    }).get();
            	    arr.sort(function(o1, o2) {
            	        return o1.t.toLowerCase() > o2.t.toLowerCase() ? 1 : o1.t.toLowerCase() < o2.t.toLowerCase() ? -1 : 0;
            	    });
            	    options.each(function(i, o) {
            	    	o.value = arr[i].v;
            	        o.text = arr[i].t;
            	    });
            	};
            	
        		function setMultiSelectOpts(opts, selectEl) {
        			
        			if ($('#' + opts.selectId + ' optgroup').length > 0) {
        				for (var x = 0; x < $('#' + opts.selectId + ' optgroup').length; x++) {
        					sortOptions($($('#' + opts.selectId + ' optgroup')[x]).find('option'));
        				}
        			}
        			else {
        				sortOptions($('#' + opts.selectId + ' option'));
        			}
        			
        			
        			if (opts.multipleSelect) {
						selectEl.multipleSelect(opts.multipleSelect);
						
						if(opts.multipleSelect.checkAll) {
							selectEl.multipleSelect("checkAll");
						}
						
						if(opts.multipleSelect.defaultValue && opts.multipleSelect.defaultValue.length > 0) {
							selectEl.multipleSelect("setSelects", opts.multipleSelect.defaultValue);
						}
						else if(opts.multipleSelect.defaultFirstValueIfNotSelected && opts.multipleSelect.defaultFirstValueIfNotSelected == true ) {
							selectEl.multipleSelect("setSelects", [$(""+selectEl.selector+" option:first").val()]);
						}
						
						if(opts.multipleSelect.hideErrorValue){
							$('span:contains(' + opts.multipleSelect.hideErrorValue + ')').addClass('ng-hide');
						}
        			}
        		};
        		
        		var selectEl = $('#' + opts.selectId);
        		var inputParams = opts.inputParams || {};
        		
        		if (eraseOldOptions) {
        			selectEl.empty();
        		}
        		if (opts.optionsData) {
        			if(opts.optionsDataWithData == true){
        				$.each(opts.optionsData, function (k, v) {
							selectEl.append($('<option data="'+v.data+'" value="'+v.key+'">'+v.value+'</option>'));
						});
        			} 
        			else {
	        			$.each(opts.optionsData, function (k, v) {
	        				if ($.isArray(opts.optionsData)) {
	        					k = v;
	        				}
	        				
	        				if ($.isPlainObject(v)) {
	        					var optgroup = $('<optgroup>', {
	        						label: k
        						});
	        					$.each(v, function (k1, v1) {
	        						if ($($('#' + opts.selectId + '')).find('option[value=' + k1 + ']').length < 1) {
		        						optgroup.append($('<option>', { 
		    						        value: k1,
		    						        text : v1 
		    						    }));
	        						}
	        					});
	        					selectEl.append(optgroup);
	        				}
	        				else {
	        					selectEl.append($('<option>', { 
							        value: k,
							        text : v 
							    }));
	        				}
							
						});
        			}
        			setMultiSelectOpts(opts, selectEl);
        			
        			if(opts.onComplete){
						opts.onComplete();
					}
        		} 
        		else {
        			if (opts.multipleSelect) {
//        				selectEl.append($('<option>', { 
//					        value: '-1',
//					        text : 'Loading...'
//					    }));
        				
        			}
        			
	        		$.ajax({
						type : 'GET',
						url : $('#contextPath').val() + opts.url,
						data : inputParams,
						success : function(response) {
							var resp = JSON.parse(response);
							if(opts.changeRespData){
								resp= opts.changeRespData(resp);
							}
							//This is to keep ajax resp in $scope
							if (opts && opts.scope) {
								if (opts.scope[opts.selectId]) {
									// do nothing 
								} else {
									opts.scope[opts.selectId] = {};
								}
								opts.scope[opts.selectId]['data'] = resp;
							}
							
							$.each(resp, function (key, val) {
								var t = '', k = key, v = null;
								
								if (typeof val == 'object') {
									if (opts && opts.valueKey && val[opts.valueKey]) {
										v = val[opts.valueKey]; 
									}
									else {
										v = val['value'];
									}
									
									if (opts && opts.keyKey && val[opts.keyKey]) {
										k = val[opts.keyKey];
									}
									
									if (opts && opts.valueTooltipKey && val[opts.valueTooltipKey]) {
										t = val[opts.valueTooltipKey];
									}
								}
								if (v == null) {
									v = val;
								}
								
								selectEl.append($('<option>', { 
							        value: k,
							        text : v,
							        title: t
							    }));
								
							});
							setMultiSelectOpts(opts, selectEl);
							if(opts.onAjaxSuccess){
								opts.onAjaxSuccess();
							}
						},
						error : function(e) {
							console.error(e);
						}
					});
        		}
        	}
		},
		setup: function(path){
			var selectMenu = $(".sidebar-menu a[href='#"+path+"']").find('span').text();
			$(".sidebar-menu li.active").removeClass('active');
			$($(".sidebar-menu span:contains('"+selectMenu+"') ")[0]).parents('li').addClass('active');
		},
		getInputUrlParam: function (inputParamArr, opts) {
			var inputData = '', jsonOp = {}, opts = opts || {}, isJsonResp = opts.jsonResp || false;
			var months = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];
			angular.forEach(inputParamArr, function(value, key) {
				var el = $('#' + value.id), reqVal = '', reqVal = '';	
				switch(value.type) {
					case 'datepicker':
						var val = '';
						switch(value.format) {
							case 'month':
								val = months[new Date($('#' + value.id).data('datepicker').viewDate).getMonth()];
								break;
							case 'year':
								val = new Date($('#' + value.id).data('datepicker').viewDate).getFullYear();
								break;
							default:
								val = new Date($('#' + value.id).data('datepicker').viewDate).getTime();
						}
						reqKey = (value.reqParam ? value.reqParam : value.id);
						reqVal = val;
//						inputData += '&' + (value.reqParam ? value.reqParam : value.id) + '=' + val;
						break;
					case 'daterangepicker':
						var val = '';
						if ($('#' + value.id).val() && $('#' + value.id).val().trim().length > 0)
							val += $.datepicker.formatDate('yy-mm-dd', new Date($('#' + value.id).data('daterangepicker').startDate)) + ' and ' + $.datepicker.formatDate('yy-mm-dd', new Date($('#' + value.id).data('daterangepicker').endDate))
							
						reqKey = (value.reqParam ? value.reqParam : value.id);
						reqVal = val;
						
//						inputData += '&' + (value.reqParam ? value.reqParam : value.id) + '=' + val;
						break;
					case 'select':
						var val = '';
						switch(value.format) {
							case 'multiSelect':
								if ($('#' + value.id) && $('#' + value.id).val() != null){
									val = $('#' + value.id).val().join('|');
									if(value.byText){
										seletedVal  = $('#' + value.id).multipleSelect('getSelects', 'text');
										val = seletedVal.join("|");
									}
								}else if (value.defaultValue != null) {
									val = value.defaultValue.join('|');
								}
								
								break;
							case 'html-multiSelect':
								if ($('#' + value.id +' option').length >0 ){
									var options = $('#' + value.id +' option');
									var strValue =[];
									var values = $.map(options ,function(option) {
										strValue.push(option.value.replace('string:',''));
									});
									
									val = strValue.join('|');
								}
								break;
							default:
								if ($('#' + value.id) && $('#' + value.id).val() != null) {
									val = $('#' + value.id).val();
									
									if(value.byText){
										seletedVal  = $('#' + value.id).multipleSelect('getSelects', 'text');
										val = seletedVal.join("|");
									}
								}
								break;
						}
						reqKey = (value.reqParam ? value.reqParam : value.id);
						reqVal = val;
//						inputData += '&' + (value.reqParam ? value.reqParam : value.id) + '=' + val;
						break;
					case 'rootscope':
						var val = '';
						if (value.id && opts.rootScope && opts.rootScope[value.id] != null) {
							val = opts.rootScope[value.id];
						}
						reqKey = (value.reqParam ? value.reqParam : value.id);
						reqVal = val;
						break;
					case 'scope':
						var val = '';
						if (value.id && opts.scope && opts.scope[value.id] != null) {
							val = opts.scope[value.id];
						}
						reqKey = (value.reqParam ? value.reqParam : value.id);
						reqVal = val;
						break;
					case 'radio':
						var val = '';
						if (value.id && $('[name=' + value.id + ']').length > 0) {
							val = $('[name=' + value.id + ']:checked').val();
						}
						reqKey = (value.reqParam ? value.reqParam : value.id);
						reqVal = val;
						break;
					default:
						//nothing
						break;
				}
				if (reqVal == '' && value.defaultValue) {
					reqVal = value.defaultValue;
				}
				if (reqKey.trim().length > 0) {
					inputData += '&' + reqKey + '=' + encodeURIComponent(reqVal);
					if (!!isJsonResp) {
						jsonOp[reqKey] = encodeURIComponent(reqVal);
					}
				}
			});
			if (!!isJsonResp) {
				return jsonOp;
			}
			return inputData.substr(1);
		},
		dynamicSort: function (property) {
		   return function(a, b) {
		       return (a[property] < b[property]) ? -1 : (a[property] > b[property]) ? 1 : 0;
		   }
		},
		showNotification: function(msg, opts){
			var opts = opts || {};
			var notify = $.notify({
    			// options
     			message: msg,
    			
    		}, {
    			// settings
    			element: 'body',
    			position: null,
    			type: (opts.type || 'info'),
    			allow_dismiss: true,
    				placement: {
    				from: 'top',
    				align: 'center'
    			},
    			allow_duplicates: false,
    			delay: 4000,
    			offset: 60,
    			spacing: 10,
    			z_index: 1031,
       			mouse_over: null,
    			animate: {
    				enter: 'animated fadeInDown',
    				exit: 'animated fadeOutUp'
    			},
    			onShow: null,
    			onShown: null,
    			onClose: null,
    			onClosed: null,
    			icon_type: 'class',
    			template: '<div data-notify="container" class="col-xs-11 col-sm-3 alert alert-{0}" role="alert">' +
    				'<button type="button" aria-hidden="true" class="close" data-notify="dismiss" style="color:white;">x</button>' +
    				'<span data-notify="title">{1}</span>'+
      				'<span data-notify="message">{2}</span>' +
    				
    			'</div>' 
    		});
    	    
		},
		getTranslation: function($rootScope, $http, language) {
            if (language == undefined) {
            	language = 'en'
            }
            
            $http.get('translation/translation_' + language + '.json').success(function(response) {
            	$rootScope.TRANSLATION = response;
            	
            	$rootScope.translate = function(_key, _default) {
            		if ($rootScope.TRANSLATION[_key] != null) {
                		return $rootScope.TRANSLATION[_key];
                	}
                	if (_default != null) {
                		return _default; 
                	}
                	return _key;
                }
            })
            .error(function (error, status){
                console.debug(error); 
            });
        }
    }
});