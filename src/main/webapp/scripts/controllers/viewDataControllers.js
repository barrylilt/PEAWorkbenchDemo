	//angular.module('PEAWorkbench').controller('viewHierarchiesController', ['$scope','services', function($scope,services){
	angular.module('PEAWorkbench').controller('viewHierarchiesController', ['$scope', '$compile', '$http', 'services', '$rootScope', function($scope, $compile, $http, services, $rootScope) {		
		$scope.services = services;
		$scope.currentTab = 'customer';
		
		$scope.init = function () {
			$('#customersTree').jstree({
				'core' : {
					'data' : {
						"url" : "/PEAWorkbenchDemo/viewCustomerHierarchy",
						"dataType" : "json" // needed only if you do not supply JSON headers
					}
				},
				"plugins" : [ "search" ]
			});
			filter = $('#customersfilter');
			glyphiconSearch=$("#customersGlyphiconSearch");
			var to = false;
			filter.keyup(function (e) {console.log("here");
				if(e.keyCode == 13){
					if(to) {
						clearTimeout(to); 
					}
					to = setTimeout(function () {
						var v = filter.val();
						$('#customersTree').jstree(true).search(v);
					}, 250);
				}else{
					return false; // returning false will prevent the event from bubbling up.
				}
			});
			glyphiconSearch.click(function(e) {
				var e =$.Event("keyup");
				e.which = 13;
				e.keyCode =13;
				filter.trigger(e);
			});
			
			
			$('#treeProduct').jstree({
				'core' : {
					'data' : {
						"url" : "/PEAWorkbenchDemo/viewProductHierarchy",
						"dataType" : "json" // needed only if you do not supply JSON headers
					}
				},
				"plugins" : [ "search" ]
			});
			filterProduct = $('#filterProduct');
			glyphiconProductSearch=$("#productGlyphiconSearch");
			var to = false;
			filterProduct.keyup(function (e) {
				if(e.keyCode == 13){
					if(to) {
						clearTimeout(to); 
					}
					to = setTimeout(function () {
						var v = filterProduct.val();
						$('#treeProduct').jstree(true).search(v);
					}, 250);
				}else{
					return false; // returning false will prevent the event from bubbling up.
				}
			});
			glyphiconProductSearch.click(function(e) {
				var e =$.Event("keyup");
				e.which = 13;
				e.keyCode =13;
				filterProduct.trigger(e);
			});
		}
		
		$scope.exportFunction = function(div, option){
			window.location.href = $('#contextPath').val() + '/' + div + '/export?format=' + option;
		}
		
		$scope.clearFilter = function () {
			$('#' + $scope.variable.id.customer.customerSelect).multipleSelect('uncheckAll');
			$('#' + $scope.variable.id.product.productSelect).multipleSelect('uncheckAll');
			
			$('#' + $scope.variable.id.customer.levelSelect).multipleSelect('uncheckAll');
			$('#' + $scope.variable.id.product.levelSelect).multipleSelect('uncheckAll');
		}
		
		$scope.navTabClick = function(tabName, $event) {
			$($event.target).parents('ul').next('.tab-content').children().removeClass('active');
			$('#'+tabName).addClass('active');
			
			if (tabName == 'customers') {
				$scope.currentTab = 'customer';
			}
			else {
				$scope.currentTab = 'product';
			}
			
			
//			if(tabName.trim() == "customers"){
//				$(".customerSearch").show();
//				$(".productSearch").hide();
//			}else{
//				$(".customerSearch").hide();
//				$(".productSearch").show();
//			}
		};
		//$scope.init();
		
		$scope.getTemplate = function() {
	        var result = new primitives.orgdiagram.TemplateConfig();
	        var buttons = [];

	        result.name = $scope.currentTab + 'CustomTemplate';

	        buttons.push(new primitives.orgdiagram.ButtonConfig('add', 'ui-icon-plus', 'Add'));
	        buttons.push(new primitives.orgdiagram.ButtonConfig('edit', 'ui-icon-pencil', 'Edit'));
//	        buttons.push(new primitives.orgdiagram.ButtonConfig('move', 'ui-icon-arrow-4', 'Move'));
	        buttons.push(new primitives.orgdiagram.ButtonConfig('delete', 'ui-icon-close', 'Delete'));

	        result.buttons = buttons;

	        result.itemSize = new primitives.common.Size(174, 94);
	        result.minimizedItemSize = new primitives.common.Size(3, 3);
	        result.highlightPadding = new primitives.common.Thickness(2, 2, 2, 2);


	        var itemTemplate = jQuery(
	            '<div class="bp-item">' +
	            '<table style="width: 100%; height: 100%;">' +
	            '<tr name="titleBackground">' + 
	            '<td name="title" align="center" style="height: 2.15em; vertical-align:middle; text-align:center; font-weight:bold; color:white;">' +
	            '<div name="titleBackground" style="width: 95%; height: 95%"></div>' +
	            // '<div name="title" class="bp-item" style="top: 3px; left: 6px; width: 162px; height: 24px; text-align:center;"></div>'
	            // + '<div name="phone" class="bp-item" style="top: 26px; left: 6px; width: 162px; height: 18px; font-size: 12px; text-align:center;"></div>'
	            // + '<div name="email" class="bp-item" style="top: 44px; left: 6px; width: 162px; height: 18px; font-size: 12px; text-align:center;"></div>'
	            '</td></tr><tr><td name="productImageDiv align="center" style="height: 36px; vertical-align:middle; text-align:center;">' + '<img src="" name="productImage" style="width: 36px; height: 36px;" >' +
	            '</td></tr><tr><td align="center" style="height: 1.15em; font-size: 10px; vertical-align:middle; text-align:center;">' +
	            $scope.currentTab + ' ID:&nbsp;<span name="description"></span></td></tr></table></div>'
	        ).css({
	            width: result.itemSize.width + "px",
	            height: result.itemSize.height + "px",
	            'font-family': 'Trebuchet MS, Tahoma, Verdana, Arial, sans-serif',
	        }).addClass("bp-item bp-corner-all bt-item-frame");

	        itemTemplate.find("table").css({
	            //background: "#eeeeee url(images/ui-bg_highlight-soft_100_eeeeee_1x100.png) 50% top repeat-x"
	        });

	        result.itemTemplate = itemTemplate.wrap('<div>').parent().html();

	        return result;
	    };
	    
	    $scope.onTemplateRender = function(event, data) {
	        var itemConfig = data.context;

	        switch (data.renderingMode) {
	            case primitives.common.RenderingMode.Create:
	                /* Initialize widgets here */
	                break;
	            case primitives.common.RenderingMode.Update:
	                /* Update widgets here */
	                break;
	        }

	        data.element.find("[name=photo]").attr({
	            "src": itemConfig.image,
	            "alt": itemConfig.title
	        });
	        data.element.find("[name=titleBackground]").css({
	            "background": itemConfig.itemTitleColor
	        });

	        var fields = ["title", "description"];
	        // var fields = ["title", "description", "phone", "email"];
	        for (var index = 0; index < fields.length; index++) {
	            var field = fields[index];

	            var element = data.element.find("[name=" + field + "]");
	            if (element.text() != itemConfig[field]) {
	                element.text(itemConfig[field]);
	                element.attr({
	                    title: itemConfig[field]
	                });
	            }
	        }

	        if (typeof itemConfig.image !== 'undefined') {
	            var imgElement = data.element.find("[name=productImage]");
	            imgElement.attr({
	                'src': itemConfig.image,
	                'alt': itemConfig.title
	            });
	        }
	    };
	    
	    $scope.btnDeleteAction = function (opts) {
	    	var opts = opts || {};
	    	
	    	BootstrapDialog.show({
	            title: 'Delete ' + opts.tabText,
	            message: 'Do you want to delete ' + opts.tabText +' (' + opts.parentCode + ' - ' + opts.parentName + ')?',
	            cssClass: 'login-dialog',
	            buttons: [{
	                label: 'Delete',
	                cssClass: 'btn-primary',
	                action: function(dialog){
	                	dialog.close();
	                	$('.ajaxLoader').show();
	                	$.ajax({
	        				type: 'POST',
	        				url: $('#contextPath').val() + '/hierarchy/' + opts.currentTab + '/deleteHierarchy',
	        				data: {
	        					parentId: opts.parentId
	        				},
	        				success: function (response) {
	        					$('.ajaxLoader').hide();
	        					$scope.applyFilter();
	        					$scope.fetchInitData({
	        						forced: true
	        					});
	        				},
	        				error: function (e) {
	        					$('.ajaxLoader').hide();
	        					console.error(e);
	        					dialogRef.close();
	        					$scope.services.showNotification('Something went wrong, Please try later.');
	        				}
	        			});
	                    
	                }
	            }, {
	                label: 'Cancel',
	                cssClass: 'btn-primary',
	                action: function(dialog){
	                	dialog.close();
	                }
	            }]
	        });
	    }
	    
	    $scope.btnAddAction = function (opts) {
	    	var currentTab = opts.currentTab;
	    	var tabText = opts.tabText;
	    	
	    	var msg = 	"<div class='box-body'>" +
							"<form  class='form-horizontal add" + tabText + "Form' autocomplete='off'  >" +
								"<div class=''><div class='form-group row'>" +
									"<span style='color: red' class='col-sm-12' id='hierarchyAddEditFormErrMsg'></span>" +
								"</div>" +
								"<div class='form-group row' >" +
									"<label for='name' class='col-sm-3 control-label'>" + tabText + " Name</label>" +
									"<div class='col-sm-9'><input type='text' class='form-control' id='name' name='name' placeholder='" + tabText + " Name' maxlength ='64'></div>" +
								"</div>" +
								"<div class='form-group row'>" +
									"<label for='code' class='col-sm-3 control-label'>" + tabText + " Code</label>" +
									"<div class='col-sm-9'><input type='text' class='form-control' id='code' name='code' placeholder='" + tabText + " Code' maxlength ='64'></div>" +
								"</div>" +
								"<div class='form-group row'>" +
									"<label for='code' class='col-sm-3 control-label'>" + tabText + " Image</label>" +
									"<div class='col-sm-9'>" +
										//"<input type='file' class='form-control upload' id='photo' name='photo' placeholder='" + tabText + " Code' maxlength ='64' accept='image/*'>" +
									
										"<div class='author'>" +
											"<img id='hierarchyImgUploadPreview'  src='' alt='' width='70px'/> " +
											"<div class='fileUpload btn btn-primary'>" +
												"<span>" + $rootScope.translate('UPLOAD_PHOTO','UPLOAD PHOTO') + "</span>" + 
												"<input type='file' id='photo' name='photo' class='upload' accept='image/*'/>" +
												"<span class='upload' id='upload-file-info' style='display:none'></span>" +
											"</div>" +
										"</div>" +
									
									"</div>" +
								"</div>" +
								"<input type='hidden' class='form-control' id='nodeId' name='nodeId' placeholder='" + tabText + " Code' maxlength ='64'></div>" +
							"</form>" +
						"</div>";
	    	
	    	BootstrapDialog.show({
				title: 'Add ' + tabText + ' ( Parent ' + tabText + ' :: ' + opts.parentCode + ' - ' + opts.parentName + ' )',
				message: msg,
				onshown: function (dialog) {
					$('#nodeId').val(opts.parentId);
					$('#hierarchyImgUploadPreview').attr('src', opts.parentImage);
					
					$("#photo").change(function () {
						var input = this;
						if (input.files && input.files[0]) {
				            var reader = new FileReader();

				            reader.onload = function (e) {
				                //$('#image_upload_preview').attr('src', e.target.result);
				            	var data = e.target.result;
				                if (data.match(/^data:image\//)) {
				                	$('#hierarchyImgUploadPreview').attr('src', data);
				                } 
				                else {
				                	var msg = 'Invalid file. Please upload an image file';
				                	$('#hierarchyAddEditFormErrMsg').text(msg);
									$('#photo').val('');
				                }
				            }
				            reader.readAsDataURL(input.files[0]);
				        }
				    });
				},
				buttons: [{
					id: 'btn-save',
					label: 'Add',
					autospin: false,
					action: function (dialogRef) {
						var formEl = $('.add' + tabText + 'Form');
						$('.add' + tabText + 'Form input').each(function (index) {
							if ($(this).attr('name') != 'photo' && $(this).val().trim() == "") {
								$(this).css("border-color", "red").addClass("invalid");
							} else {
								$(this).css("border-color", "#d2d6de").removeClass("invalid");
							}
						});
						
						if ($('.invalid').length == 0) {
							$('.ajaxLoader').show();
							
//							var data = $('.add' + tabText + 'Form').serializeArray().reduce(function (obj, item) {
//								obj[item.name] = item.value;
//								return obj;
//							}, {});
//							data['parentId'] = opts.parentId;
							
							formEl.submit(function (event) {
								//disable the default form submission
								event.preventDefault();
								//grab all form data  
								var formData = $(this).serialize();
								
								var data = new FormData(formEl[0]);
								
								$('.ajaxLoader').show();
								$.ajax({
									type: 'POST',
									url: $('#contextPath').val() + '/hierarchy/' + currentTab + '/addIntoHierarchy',
									data: data,
									async	: false,
									cache	: false,
									contentType	: false,
									processData	: false,
									success: function (response) {
										$('.ajaxLoader').hide();
										dialogRef.close();
										$scope.applyFilter();
										$scope.fetchInitData({
											forced: true
										});
									},
									error: function (e) {
										$('.ajaxLoader').hide();
										console.error(e);
										dialogRef.close();
										$scope.services.showNotification('Something went wrong, Please try later.');
									}
								});
							});
							
							formEl.submit();
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
	    
	    $scope.btnEditAction = function (opts) {
	    	var currentTab = opts.currentTab;
	    	var tabText = opts.tabText;
	    	
	    	var msg = 	"<div class='box-body'>" +
							"<form id='edit" + tabText + "FormId' class='form-horizontal edit" + tabText + "Form' autocomplete='off'  >" +
								"<div class=''><div class='form-group row'>" +
									"<span style='color: red' class='col-sm-12' id='hierarchyAddEditFormErrMsg'></span>" +
								"</div>" +
								"<div class='form-group row' >" +
									"<label for='name' class='col-sm-3 control-label'>" + tabText + " Name</label>" +
									"<div class='col-sm-9'><input type='text' class='form-control' id='name' name='name' placeholder='" + tabText + " Name' maxlength ='64'></div>" +
								"</div>" +
								"<div class='form-group row'>" +
									"<label for='code' class='col-sm-3 control-label'>" + tabText + " Code</label>" +
									"<div class='col-sm-9'><input type='text' class='form-control' id='code' name='code' placeholder='" + tabText + " Code' maxlength ='64' readOnly></div>" +
								"</div>" +
								"<div class='form-group row'>" +
									"<label for='code' class='col-sm-3 control-label'>" + tabText + " Image</label>" +
									"<div class='col-sm-9'>" +
										//"<input type='file' class='form-control' id='photo' name='photo' placeholder='" + tabText + " Code' maxlength ='64'>" +
										"<div class='author'>" +
											"<img id='hierarchyImgUploadPreview'  src='' alt='' width='70px'/> " +
											"<div class='fileUpload btn btn-primary' style='margin-left: 15px;'>" +
												"<span>" + $rootScope.translate('UPLOAD_PHOTO','UPLOAD PHOTO') + "</span>" + 
												"<input type='file' id='photo' name='photo' class='upload' accept='image/*'/>" +
												"<span class='upload' id='upload-file-info' style='display:none'></span>" +
											"</div>" +
										"</div>" +
									"</div>" +
								"</div>" +
								"<input type='hidden' class='form-control' id='nodeId' name='nodeId' placeholder='" + tabText + " Code' maxlength ='64'>" +
							"</form>" +
						"</div>"; 
	    	
	    	BootstrapDialog.show({
				title: 'Edit ' + tabText + ' ( ' + opts.parentCode + ' - ' + opts.parentName + ' )',
				message: msg,
				onshown: function (dialog) {
					$('#name').val(opts.parentName);
					$('#code').val(opts.parentCode);
					$('#nodeId').val(opts.parentId);
					$('#hierarchyImgUploadPreview').attr('src', opts.parentImage);
					
					$("#photo").change(function () {
						var input = this;
						if (input.files && input.files[0]) {
				            var reader = new FileReader();

				            reader.onload = function (e) {
				                var data = e.target.result;
				                if (data.match(/^data:image\//)) {
				                	$('#hierarchyImgUploadPreview').attr('src', data);
				                } 
				                else {
				                	var msg = 'Invalid file. Please upload an image file';
									$('#hierarchyAddEditFormErrMsg').text(msg);
									$('#photo').val('');
				                }
				            }
				            reader.readAsDataURL(input.files[0]);
				        }
				    });
				},
				buttons: [{
					id: 'btn-save',
					label: 'Save',
					autospin: false,
					action: function (dialogRef) {
						var formEl = $('.edit' + tabText + 'Form');
						$('.edit' + tabText + 'Form input').each(function (index) {
							if ($(this).attr('name') != 'photo' && $(this).val().trim() == "") {
								$(this).css("border-color", "red").addClass("invalid");
							} else {
								$(this).css("border-color", "#d2d6de").removeClass("invalid");
							}
						});
						
						if ($('.invalid').length == 0) {
//							var data = formEl.serializeArray().reduce(function (obj, item) {
//								obj[item.name] = item.value;
//								return obj;
//							}, {});
//							data['parentId'] = opts.parentId;
							
							formEl.submit(function (event) {
								//disable the default form submission
								event.preventDefault();
								
								debugger;
								
								//grab all form data  
								var formData = $(this).serialize();
								
								var data = new FormData(formEl[0]);
								$('.ajaxLoader').show();
								$.ajax({
									type: 'POST',
									url: $('#contextPath').val() + '/hierarchy/' + currentTab + '/editHierarchy',
									data: data,
									async	: false,
									cache	: false,
									contentType	: false,
									processData	: false,
									success: function (response) {
										$('.ajaxLoader').hide();
										dialogRef.close();
										$scope.applyFilter();
										$scope.fetchInitData({
											forced: true
										});
									},
									error: function (e) {
										$('.ajaxLoader').hide();
										console.error(e);
										dialogRef.close();
										$scope.services.showNotification('Something went wrong, Please try later.');
									}
								});
							});
							
							formEl.submit();
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
		
		$scope.makeChartSettings = function(opts) {
	        var options = new primitives.orgdiagram.Config();
	        var buttons = [];

	        options.items = opts.items;
	        options.cursorItem = 0;
	        // options.hasSelectorCheckbox = primitives.common.Enabled.True;
	        options.hasSelectorCheckbox = primitives.common.Enabled.False;
	        options.emptyDiagramMessage = 'No items selected by the filters.';
	        options.templates = [$scope.getTemplate()];
	        options.defaultTemplateName = $scope.currentTab + 'CustomTemplate';
	        options.onItemRender = $scope.onTemplateRender;

	        buttons.push(new primitives.orgdiagram.ButtonConfig('add', 'ui-icon-plus', 'Add'));
	        buttons.push(new primitives.orgdiagram.ButtonConfig('edit', 'ui-icon-pencil', 'Edit'));
	        buttons.push(new primitives.orgdiagram.ButtonConfig('move', 'ui-icon-arrow-4', 'Move'));
	        buttons.push(new primitives.orgdiagram.ButtonConfig('delete', 'ui-icon-close', 'Delete'));

	        // options.hasButtons = primitives.common.Enabled.True;
	        options.hasButtons = primitives.common.Enabled.Auto;
	        options.buttons = buttons;
	        // options.buttonsPanelSize = new primitives.common.Size(18, 72);
	        
	        
	        options.onButtonClick = function (e, data) {
	        	var opts = {};
	        	opts.currentTab = $scope.currentTab;
		    	opts.tabText = 'Product';
		    	
		    	if (opts.currentTab == 'customer') {
		    		opts.tabText = 'Customer';
		    	}
	        	
//	        	if ($scope.currentTab == 'product') {
	        		if (data.name == 'add') {
	        			var defaultImgURL = 'images/unknown.png';
	        			opts = $.extend(opts, {
	        				parentName: data.context.title,
			        		parentCode: data.context.description,
			        		parentImage: defaultImgURL,
			        		parentId: data.context.id
	        			});
	        			
			        	$scope.btnAddAction(opts);
	        		}
	        		
	        		if (data.name == 'edit') {
	        			
	        			opts = $.extend(opts, {
	        				parentName: data.context.title,
			        		parentCode: data.context.description,
			        		parentImage: data.context.image,
			        		parentId: data.context.id
	        			});
	        			
			        	$scope.btnEditAction(opts);
	        		}
	        		
	        		if (data.name == 'delete') {
	        			opts = $.extend(opts, {
	                		parentId: data.context.id,
	                		parentCode: data.context.description,
	                		parentName: data.context.title
			        	});
	                	
	                	$scope.btnDeleteAction(opts);
	        		}
//	        	}
            };
	        
            return options;
	    };
	    
		
		$scope.generateChart = function() {
	        if (typeof $scope[$scope.currentTab + 'Chart'] !== 'undefined') {
	            $('#' + $scope.currentTab + 'Chart').orgDiagram('update', primitives.orgdiagram.UpdateMode.Refresh);
	        } else {
	            $scope[$scope.currentTab + 'Chart'] = $('#' + $scope.currentTab + 'Chart').orgDiagram($scope.chartSettings);
	        }
	    };

	    $scope.makeItemsFromResults = function(results) {
	        var items = [], imgURL = 'images/customer2.png', defaultImgURL = 'images/unknown.png';
	        
	        if ($scope.currentTab == 'product') {
	        	imgURL = 'images/product2.jpg';
	        }

	        if (typeof results !== 'undefined') {
	            for (var i = 0; i < results.length; i++) {
	                var parentId, childId, imgURL = defaultImgURL;

	                childId = results[i].childId;
	                parentId = results[i].parentId;

	                if (results[i].distance === 0) {
	                    parentId = null;
	                }
	                
	                if (results[i].childImg != null) {
	                	imgURL = results[i].childImg;
	                }

	                items.push(new primitives.orgdiagram.ItemConfig({
	                    id: childId,
	                    parent: parentId,
	                    title: results[i].childName,
	                    groupTitle: '<span style="margin-left: 12px;">Level ' + results[i].childLevel + '</span>',
	                    description: results[i].childCode,
	                    image: imgURL
	                }));
	            }
	        }
	        return items;
	    };
		
		$scope.showChart = function (opts) {
			$('.ajaxLoader').show();
			
			$.ajax({
				type : 'GET',
				url : $('#contextPath').val() + '/hierarchy/' + $scope.currentTab +'/data',
				data : {
					rootId: (opts.rootId || 1)
				},
				success : function(response) {
					$('.ajaxLoader').hide();
					var response = JSON.parse(response);
					var items = $scope.makeItemsFromResults(response.data);
			        // console.log(items);
					
					$scope.chartSettings = $scope.makeChartSettings({
			            items: items,
			            cursorItem: 0
			        });

			        $('#' + $scope.currentTab +'Chart').orgDiagram($scope.chartSettings);
			        $scope.generateChart();
					
				},
				error : function(e) {
					$('.ajaxLoader').hide();
					console.error(e);
					$scope.services.showNotification(response['Message']);
				}
			});
		}
		
		$scope.getLevelList = function (opts) {
			var data = {};
			
			if ($scope.init[opts.type]['data'] == null) {
				return data;
			}
			
			$.each($scope.init[opts.type]['data'], function (idx, node) {
				if (node != null) {
					data[node.levelNumber] = node.levelName + ' L' + node.levelNumber; 
				}
			});
			
			return data;
		}
		
		$scope.getLevelProductList = function(levelId, opts) {
			var selectProducts = $('#' + $scope.variable.id.product.productSelect), opts = opts || {};;
			selectProducts.multipleSelect('disable');
			selectProducts.find('option').remove();
			selectProducts.multipleSelect('uncheckAll');
			var productList= [];
			if(levelId != null && $scope.init != null && $scope.init[$scope.currentTab]['data'] != null) {
				var objProducts = $scope.init[$scope.currentTab]['data'].filter(function (el) { return el.levelNumber == levelId });
				$.each(objProducts, function (idx, product) {
					productList.push(product);
				});
			}
			
			productList = $.unique(productList).sort($scope.services.dynamicSort('childName'));
			$.each(productList, function (idx, product) {
				selectProducts.append($('<option>', { 
					value: product.childId,
					text : product.childName,
					title: product.childName
				}));
			});
			
			selectProducts.multipleSelect('refresh');
			
			if(productList.length > 0) {
				selectProducts.multipleSelect('enable');
				if (productList.length == 1) {
					selectProducts.multipleSelect('checkAll');
				}
				else if (opts.defaultValue != null) {
					selectProducts.multipleSelect('setSelects', opts.defaultValue);
				}
			}
		}
		
		$scope.getLevelCustomerList = function(levelId, opts) {
			var selectEl = $('#' + $scope.variable.id.customer.customerSelect), opts = opts || {};
			selectEl.multipleSelect('disable');
			selectEl.find('option').remove();
			selectEl.multipleSelect('uncheckAll');
			var selectDataList= [];
			if(levelId != null && $scope.init != null && $scope.init[$scope.currentTab]['data'] != null) {
				var objects = $scope.init[$scope.currentTab]['data'].filter(function (el) { return el.levelNumber == levelId });
				$.each(objects, function (idx, obj) {
					selectDataList.push(obj);
				});
			}
			
			selectDataList = $.unique(selectDataList).sort($scope.services.dynamicSort('childName'));
			$.each(selectDataList, function (idx, obj) {
				selectEl.append($('<option>', { 
					value: obj.childId,
					text : obj.childName,
					title: obj.childName
				}));
			});
			
			selectEl.multipleSelect('refresh');
			
			if(selectDataList.length > 0) {
				selectEl.multipleSelect('enable');
				if (selectDataList.length == 1) {
					selectEl.multipleSelect('checkAll');
				}
				else if (opts.defaultValue != null) {
					selectEl.multipleSelect('setSelects', opts.defaultValue);
				}
			}
		}
		
		$scope.setInitEls = function (opts) {
			
			var opts = opts || {};
			//Customers
			$scope.services.select.setOptions({
				selectId: $scope.variable.id.customer.levelSelect,
				optionsData: $scope.getLevelList({
					type: 'customer'
				}),
				multipleSelect: {
					filter: true,
					placeholder: 'Select Level',
//					width: '200',
					minimumCountSelected: 1,
					single: true,
					defaultValue: [opts[$scope.variable.id.customer.levelSelect]]
				},
				onComplete: function () {
					var levelSelectEl = $('#' + $scope.variable.id.customer.levelSelect);
					levelSelectEl.off('change');
					levelSelectEl.change(function() {
						if (levelSelectEl.val() != null)
							$scope.getLevelCustomerList(levelSelectEl.val()[0]);
					});
				}
			}, true);
			
			$scope.services.select.setOptions({
				selectId: $scope.variable.id.customer.customerSelect,
				optionsData: {},
				multipleSelect: {
					filter: true,
					placeholder: 'Select Customer',
//					width: '200',
					minimumCountSelected: 1,
					single: true,
					defaultValue: [opts[$scope.variable.id.customer.customerSelect]]
				},
				onComplete: function () {
					var selectEl = $('#' + $scope.variable.id.customer.customerSelect);
					selectEl.multipleSelect('disable');
					
					if (opts[$scope.variable.id.customer.levelSelect] != null) {
						$scope.getLevelCustomerList(opts[$scope.variable.id.customer.levelSelect], {
							defaultValue: [opts[$scope.variable.id.customer.customerSelect]]
						});
					}
				}
			}, true);
			
			//Products
			$scope.services.select.setOptions({
				selectId: $scope.variable.id.product.levelSelect,
				optionsData: $scope.getLevelList({
					type: 'product'
				}),
				multipleSelect: {
					filter: true,
					placeholder: 'Select Level',
//					width: '200',
					minimumCountSelected: 1,
					single: true,
					defaultValue: [opts[$scope.variable.id.product.levelSelect]]
				},
				onComplete: function () {
					var levelSelectEl = $('#' + $scope.variable.id.product.levelSelect);
					levelSelectEl.off('change');
					levelSelectEl.change(function() {
						if (levelSelectEl.val() != null)
							$scope.getLevelProductList(levelSelectEl.val()[0]);
					});
				}
			}, true);
			
			$scope.services.select.setOptions({
				selectId: $scope.variable.id.product.productSelect,
				optionsData: {},
				multipleSelect: {
					filter: true,
					placeholder: 'Select Product',
//					width: '200',
					minimumCountSelected: 1,
					single: true,
					defaultValue: [opts[$scope.variable.id.product.productSelect]]
				},
				onComplete: function () {
					var selectEl = $('#' + $scope.variable.id.product.productSelect);
					selectEl.multipleSelect('disable');
					
					if (opts[$scope.variable.id.product.levelSelect] != null) {
						$scope.getLevelProductList(opts[$scope.variable.id.product.levelSelect], {
							defaultValue: [opts[$scope.variable.id.product.productSelect]]
						});
					}
				}
			}, true);
		}
		
		$scope.applyFilter = function () {
			var selectEl = $('#' + $scope.variable.id.product.productSelect);
			
			if ($scope.currentTab == 'customer') {
				selectEl = $('#' + $scope.variable.id.customer.customerSelect);
			}
			if (selectEl.val() != null) {
				$scope.showChart({
					rootId: selectEl.val()[0]
				});
			}
		}
		
		$scope.getInitFieldsData = function () {
			var opts = {};
			var initFields = [
			                  $scope.variable.id.customer.levelSelect,
			                  $scope.variable.id.customer.customerSelect,
			                  $scope.variable.id.product.levelSelect,
			                  $scope.variable.id.product.productSelect,
			                 ]
			
			$.each(initFields, function (idx, val) {
				if ($('#' + val) && $('#' + val).val() != null)
					opts[val] = $('#' + val).val()[0];
			});
			
			console.log(opts);
			return opts;
		}
		
		$scope.fetchInitData = function (opts) {
			var opts = opts || {};
			
			opts = $.extend(opts, $scope.getInitFieldsData());
			
			$.ajax({
				type : 'GET',
				url : $('#contextPath').val() + '/hierarchy/initData',
				data : {
					forced: opts.forced || false
				},
				success : function(response) {
					$('.ajaxLoader').hide();
					response = JSON.parse(response);
					$scope.init['product']['data'] = response.data.product;
					$scope.init['customer']['data'] = response.data.customer;
					
					$scope.setInitEls(opts);
				},
				error : function(e) {
					$('.ajaxLoader').hide();
					console.error(e);
					$scope.services.showNotification('Something went wrong!!');
				}
			});
		}
		
		$scope.init2 = function () {
			$scope.variable = {};
			$scope.init = {
				product: {
					
				},
				customer: {
					
				}
			};
			
			$scope.variable= {
				id: {
					customer: {
						levelSelect : 'viewHierarchiesCustomerLevelSelect',
						customerSelect : 'viewHierarchiesCustomersSelect'
					},
					product: {
						levelSelect : 'viewHierarchiesProductLevelSelect',
						productSelect : 'viewHierarchiesProductsSelect'
					}
				}
			};
			
			$scope.fetchInitData({
				forced: false
			});
		}
		
		 $scope.init2();
		
	}]);
	
	angular.module('PEAWorkbench').controller('viewExceptionsController', ['$scope', '$compile', '$http', 'DTOptionsBuilder', 'DTColumnBuilder', 'services', '$rootScope', '$location', function($scope, $compile, $http, DTOptionsBuilder, DTColumnBuilder, services, $rootScope, $location) {
		$scope.services = services;
		$scope.allDatasetBizRuleSelectlist=[];
		var currentPage = 'ViewExceptions';
		$scope.getProdCategoryBrandList =[];
		$scope[currentPage] = {};
		$scope.dataType = $rootScope.exportDatasetsDataType !== "" ?  $rootScope.exportDatasetsDataType : "EYNTK" ;
		$scope.dataType = $scope.dataType == undefined ? "EYNTK" : $scope.dataType;
		
		$scope.defaultDateFormat = $('#defaultDateFormat').val();
		
		$scope.changeResponseOnType = function(type) {
			var optResp=[];
			$.each($scope.allDatasetBizRuleSelectlist, function (key, val) {
				if(val.ruleType == type){
					optResp.push(val);
				}
			});
			return optResp;
		}
		
		$scope.reprocessPromotions = function () {
			
			var promoIds = [];
			$('[class^=cb-reprocess]').each(function(i, el) {
				if ($(el).prop("checked"))
					if (promoIds.indexOf($(el).attr('data-PromoId')) < 0)
						promoIds.push($(el).attr('data-PromoId'));
			});
			
			if (promoIds.length < 1) {
				$scope.services.showNotification("No record was marked to reprocess!");
				return;
			}
			
			$('.ajaxLoader').show();
			
			$.ajax({
				type : 'POST',
				url : $('#contextPath').val() + '/' + currentPage + '/reprocessPromotions',
				data : {
					PromoID: promoIds.join(',')
				},
				success : function(response) {
					
					$('.ajaxLoader').hide();
					var response = JSON.parse(response);
					$scope.services.showNotification(response['Message']);
				},
				error : function(e) {
					$('.ajaxLoader').hide();
					console.error(e);
					$scope.services.showNotification(response['Message']);
				}
			});
		}
		
		$scope.saveEditChanges = function () {
			var editedData = {};
			var promoIds = [];
			
			$('[class^="' + currentPage + '-ef-"][class*="border-red"]').each(function(i, el) {
				
				var rowPromoId = $(el).attr('data-promoid'), isChkOn = false;
				$('.DTFC_LeftBodyWrapper input[type=checkbox][data-promoid=' + rowPromoId +']').each(function (idx, chkEl) {
					isChkOn = $(chkEl).prop("checked");
					return !isChkOn;
				});
				
				// Skipped promotion editing changes if check box is not checked.
				if (!isChkOn) {
					return true;
				}
				
				if (editedData[$(el).attr('data-rowkey')] == null) {
					editedData[$(el).attr('data-rowkey')] = {};
				}
				var class1 = $(el).attr('class').split(' ')[0].split('-');
				editedData[$(el).attr('data-rowkey')][class1[class1.length - 1]] = el.value;
				if (promoIds.indexOf($(el).attr('data-PromoId')) < 0)
					promoIds.push($(el).attr('data-PromoId'));
			});
			
			if (promoIds.length < 1) {
				$scope.services.showNotification("No record was changed!");
				return;
			}
			
			$('.ajaxLoader').show();
			
			$.ajax({
				type : 'POST',
				url : $('#contextPath').val() + '/' + currentPage + '/saveEditChanges',
				data : {
					editData: JSON.stringify(editedData),
					PromoID: promoIds.join(',')
				},
				success : function(response) {
					
					$('.ajaxLoader').hide();
					
					$('[class^="' + currentPage + '-ef-"][class*="border-red"]').each(function(i, el) {
						$(el).removeClass('border-red');
					});
					
					var response = JSON.parse(response);
					$scope.services.showNotification(response['Message']);
					
					//Reload
					if ($scope['dtInstance' + currentPage])
						$scope['dtInstance' + currentPage].reloadData();
					
				},
				error : function(e) {
					$('.ajaxLoader').hide();
					console.error(e);
					$scope.services.showNotification(response['Message']);
				}
			});
		}
		
		$scope.datasetTypeChange =function(value){
			var optResp= [];
			if(value== "EYNTK"){
				$("#dateLabel").html('Shipment Start Date');
				$("#bizRuleLabel").html("Action Rules");
				optResp = $scope.changeResponseOnType("EYNTK");
				$('input[name="datefilter"]').daterangepicker({
					autoUpdateInput: false,
					locale: {
						cancelLabel: 'Clear',
					},
					format: $scope.defaultDateFormat, //'MM/DD/YYYY'
					opens:'center',
					//maxDate: new Date(),
					startDate : moment().subtract(29, 'days')
				});
			}else{
				$("#dateLabel").html('In-Store End Date');
				$("#bizRuleLabel").html("Exception Rules");
				optResp = $scope.changeResponseOnType("OPSO");
				$('input[name="datefilter"]').daterangepicker({
					autoUpdateInput: false,
					locale: {
						cancelLabel: 'Clear',
					},
					format: $scope.defaultDateFormat,
					opens:'center',
					maxDate: new Date(),
					startDate : moment().subtract(29, 'days')
				});
			}
			$("#viewExceptionsBizRuleSelect").empty();
			$.each(optResp, function (key, val) {
				var t = '', k = key, v = null;
				if (typeof val == 'object') {
					v = val['ruleShortDesc'];
					k = val['ruleId'];
					t = val['ruleDescription'];
				}
				$("#viewExceptionsBizRuleSelect").append($('<option>', { 
					value: k,
					text : v,
					title: t
				}));
			});
			$("#viewExceptionsBizRuleSelect").multipleSelect("refresh");
			
			$("#viewExceptionsInstoredate").on('cancel.daterangepicker', function(ev, picker) {
				$(this).val('');
			});
		}
		if($rootScope.viewExceptionDatasetType){
			$scope.dataType = $rootScope.viewExceptionDatasetType;
			$scope.datasetTypeChange($rootScope.viewExceptionDatasetType);
		}
		services.setup('viewExceptions');
		$scope['OverLappingPromotionProduct'] = {};
		
		$scope['OverLappingPromotionProduct']['inputParam'] = [{
			id: 'promoId',
			type: 'rootscope',
			reqParam: 'fltrPromoId',
			defaultValue:'123'
		}, {
			id: 'promoDrivId',
			type: 'rootscope',
			reqParam: 'promoDrivId',
			defaultValue:'1-BTOA3M'
			
		}, {
			id: 'promoOverId',
			type: 'rootscope',
			reqParam: 'promoOverId',
			defaultValue:'1-CD5IMV'
		}];
		
		$scope[currentPage]['inputParam'] = [{
			id: 'viewExceptionDatasetType',
			type: 'radio',
			reqParam: 'fltrPromoType',
			defaultValue:$rootScope.viewExceptionDatasetType
		}, {
			id: 'viewExceptionsStatusSelect',
			type: 'select',
			format: 'multiSelect',
			reqParam: 'fltrStatus',
			defaultValue:$rootScope.viewExceptionsStatusSelect
		},{
			id: 'viewExceptionspPromoStatusSelect',
			type: 'select',
			format: 'multiSelect',
			reqParam: 'fltrPromoStatus',
			defaultValue:$rootScope.viewExceptionspPromoStatusSelect
		}, {
			id: 'viewExceptionsCustomerSelect',
			type: 'select',
			format: 'multiSelect',
			reqParam: 'fltrCustId',
			defaultValue:$rootScope.viewExceptionsCustomerSelect
		}, {
			id: 'viewExceptionsInstoredate',
			type: 'daterangepicker',
			reqParam: 'fltrInStoreDate',
			defaultValue:$rootScope.viewExceptionsInstoredate
		}, {
			id: 'viewExceptionsBizRuleSelect',
			type: 'select',
			format: 'multiSelect',
			reqParam: 'fltrBizRuleSelect',
			defaultValue:$rootScope.viewExceptionsBizRuleSelect
		},{
			id: 'viewExceptionsBrandSelect',
			type: 'select',
			format: 'multiSelect',
			reqParam: 'fltrProdBrandSelect',
			defaultValue:$rootScope.viewExceptionsBrandSelect
		},{
			id: 'viewExceptionsCategorySelect',
			type: 'select',
			format: 'multiSelect',
			reqParam: 'fltrProdCategorySelect',
			defaultValue:$rootScope.viewExceptionsCategorySelect
		}];
		
		$scope.applyFilter = function () {
			//$rootScope.isViewExceptionApplyFilterSet = true;
			
			$scope.viewExceptionChkBeforeSendReq = undefined;
			$("#ViewExceptionsTableId_processing").html('Processing...');
			
			
			$rootScope.viewExceptionDatasetType =$("input[name='viewExceptionDatasetType']:checked").val();
			$rootScope.viewExceptionsStatusSelect = $('#viewExceptionsStatusSelect').val();
			$rootScope.viewExceptionspPromoStatusSelect = $('#viewExceptionspPromoStatusSelect').val();
			$rootScope.viewExceptionsCustomerSelect = $('#viewExceptionsCustomerSelect').val();
			$rootScope.viewExceptionsBizRuleSelect = $('#viewExceptionsBizRuleSelect').val();
			$rootScope.viewExceptionsCategorySelect = $('#viewExceptionsCategorySelect').val();
			$rootScope.viewExceptionsBrandSelect = $('#viewExceptionsBrandSelect').val();
			
			if($("#viewExceptionsInstoredate").val().length > 0){
				$rootScope.viewExceptionsInstoreStartDate = new moment(new Date($('#viewExceptionsInstoredate').data('daterangepicker').startDate)).format($scope.defaultDateFormat);
				$rootScope.viewExceptionsInstoreEndDate = new moment(new Date($('#viewExceptionsInstoredate').data('daterangepicker').endDate)).format($scope.defaultDateFormat);
			}
			
			if ($scope['dtInstance' + currentPage])
				$scope['dtInstance' + currentPage].reloadData();
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
		$scope.clearFilter = function () {
			$('#viewExceptionsStatusSelect').multipleSelect('uncheckAll');
			$('#viewExceptionspPromoStatusSelect').multipleSelect('uncheckAll');
			$('#viewExceptionsCustomerSelect').multipleSelect('uncheckAll');
			$('#viewExceptionsBizRuleSelect').multipleSelect('uncheckAll');
			$('#viewExceptionsBrandSelect').multipleSelect('uncheckAll');
			$('#viewExceptionsCategorySelect').multipleSelect('uncheckAll');
			$rootScope.viewExceptionDatasetType = "EYNTK";
			$rootScope.viewExceptionsStatusSelect = [];
			$rootScope.viewExceptionsCustomerSelect = [];
			$rootScope.viewExceptionsBizRuleSelect = [];
			$rootScope.viewExceptionsBrandSelect = [];
			$rootScope.viewExceptionsCategorySelect = [];
			$rootScope.viewExceptionspPromoStatusSelect=[];
			$rootScope.viewExceptionsInstoreStartDate = "";
			$rootScope.viewExceptionsInstoreEndDate = "";
			$('input[name="datefilter"]').val('');
			
			$($scope[currentPage]['inputParam']).each(function (idx, jo) { if(jo.defaultValue != null) jo.defaultValue = undefined; });
			
			/*if ($scope['dtInstance' + currentPage])
				$scope['dtInstance' + currentPage].reloadData();*/
		}
		
		$scope.export = function () {
			var inputParams = $scope.services.getInputUrlParam($scope[currentPage]['inputParam']);
			window.location.href = $('#contextPath').val() + '/' + currentPage + '/export?' + inputParams;
		}		
		
		$scope.viewExceptionChkBeforeSendReq = function () {
			return false;
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
				colNotToSort: [0],
				order: [1, 'asc'],
				fixedColumns:{leftColumns: 2},
				chkBeforeSendReq: function () {
					if ($scope.viewExceptionChkBeforeSendReq)
						return $scope.viewExceptionChkBeforeSendReq();
					return true;
				},
				processingMsg: "Select Filter ",
				onInitComplete:function(){
					$( "#ViewExceptionsTableId_length" ).append( 
						$( 
							'<button class="btn btn-primary ng-binding" id="saveChanges" data-original-title="" title="" style="margin-left:10px;">Save & Reprocess</button>'
							/*+ '<button class="btn btn-primary ng-binding" id="reprocessPromotions" data-original-title="" title="" style="margin-left:10px;">Reprocess</button>'*/
						) 
					);

					$('#saveChanges').confirmation({
						singleton: true,
						title: 'Are you sure want to save the changes?',
						placement: 'bottom',
						onConfirm: function () {
							$scope.saveEditChanges();
						}
					});
					
					$('#reprocessPromotions').confirmation({
						singleton: true,
						title: 'Are you sure want to Reprocess selcted Promotions?',
						placement: 'bottom',
						onConfirm: function () {
							$scope.reprocessPromotions();
							
							//Reload
							if ($scope['dtInstance' + currentPage])
								$scope['dtInstance' + currentPage].reloadData();
						}
					});
				},
				onDrawCallback: function () {
					$('.viewPromotedProductLink').click(function () {
						$rootScope.selPromotionId = this.id.replace('viewPromotedProductLink_', '');
						$($(".sidebar-menu span:contains('View Promoted Products') ")[0]).trigger('click');
					});
					$(".overlappingLink").unbind('click');
					$('.overlappingLink').click(function () {
						$rootScope.promoId = this.id.replace('overlappingLink_', '');
						$scope.promoName = $(this).parents('td').find('.promotionName').val();
						showOverlapping();
					});
					
					// Editable input boxes
					$('[class^=' + currentPage + '-ef-]').on('change', function (e) {
						var _this = this;
						$(_this).addClass('border-red'); //css("border", "red solid 1px");
						var rowPromoId = $(_this).attr('data-promoid');
						$('input[type=checkbox][data-promoid=' + rowPromoId + ']').each(function (idx, chkEl) {
							chkEl.checked = true;
						});
					});
					$('[class^="ViewExceptions"][class*="clsNumeric"]').forceNumericOnly();
				},
				isTable: false
			});
		}
		
		var setBrandVal = function(){
			var selectBrand=$("#viewExceptionsBrandSelect");
			selectBrand.multipleSelect("disable");
			selectBrand.find('option').remove();
			selectBrand.multipleSelect('uncheckAll');
			var brandList= [];
			if($("#viewExceptionsCategorySelect").val()){
				$.each($("#viewExceptionsCategorySelect").val(), function(index, item) {
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
			if($rootScope.viewExceptionsBrandSelect){
				selectBrand.multipleSelect("setSelects", $rootScope.viewExceptionsBrandSelect);
			}
		}
		
		$scope.init = function () {
			var toggleCheckbox = function(element)
			{
				element.checked = !element.checked;
			}
			
			if($scope.dataType == 'EYNTK'){
				$('input[name="datefilter"]').daterangepicker({
					autoUpdateInput: false,
					locale: {
						cancelLabel: 'Clear',
					},
					format: $scope.defaultDateFormat, //'MM/DD/YYYY'
					opens:'center',
					//maxDate: new Date(),
					startDate : moment().subtract(29, 'days')
				});
			}
			else if($scope.dataType == 'OPSO') {
				$('input[name="datefilter"]').daterangepicker({
					autoUpdateInput: false,
					locale: {
						cancelLabel: 'Clear',
					},
					format: $scope.defaultDateFormat,
					opens:'center',
					maxDate: new Date(),
					startDate : moment().subtract(29, 'days')
				});
			}
			if($rootScope.viewExceptionsInstoreStartDate){
				$('input[name="datefilter"]').data('daterangepicker').setStartDate($rootScope.viewExceptionsInstoreStartDate);
			}
			if($rootScope.viewExceptionsInstoreEndDate){
				$('input[name="datefilter"]').data('daterangepicker').setEndDate($rootScope.viewExceptionsInstoreEndDate);
			}
			
			$("#viewExceptionsInstoredate").on('cancel.daterangepicker', function(ev, picker) {
				$(this).val('');
			});
			
			$scope.services.select.setOptions({
				selectId: 'viewExceptionsStatusSelect', 
				optionsData: {
					clean: 'Clean',
					exception: 'Exceptions'
				},
				inputParam: {},
				multipleSelect: {
					filter: true,
					placeholder: 'Select Status',
					width: '100%',
					defaultValue:$rootScope.viewExceptionsStatusSelect
				},
				
			}, true);
			
			
			$scope.services.select.setOptions({
				selectId: 'viewExceptionsBizRuleSelect', 
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
					defaultValue:$rootScope.viewExceptionsBizRuleSelect
				},
				changeRespData: function(response){
					$scope.allDatasetBizRuleSelectlist = response;
					var type=$("input[name=viewExceptionDatasetType]:checked").val();
					var optResp = $scope.changeResponseOnType(type);
					return optResp;
				}
			}, true);
		
			$scope.services.select.setOptions({
				selectId: 'viewExceptionspPromoStatusSelect', 
				optionsData: {
					Cancelled: 'Cancelled',
					Finalized: 'Finalized',
					Planned: 'Planned',
					Stopped:'Stopped'
				}, 
				keyKey: 'promoStatusId',
				valueKey: 'Promo Status',
				inputParam: {},
				multipleSelect: {
					filter: true,
					placeholder: 'Select Promo Status',
					width: '100%',
					minimumCountSelected: 1,
					defaultValue:$rootScope.viewExceptionspPromoStatusSelect
				}
			}, true);
			
			$scope.services.select.setOptions({
				selectId: 'viewExceptionsCustomerSelect', 
				url: '/getCustomerList', 
				keyKey: 'customerId',
				valueKey: 'customerName',
				inputParam: {},
				multipleSelect: {
					filter: true,
					placeholder: 'Select Customers',
					width: '100%',
					minimumCountSelected: 1,
					defaultValue:$rootScope.viewExceptionsCustomerSelect
				}
			}, true);
			$.ajax({
				type : 'GET',
				url : $('#contextPath').val() + "/getProdCategoryBrandList",
				success : function(response) {
				var resp = JSON.parse(response);
				$scope.getProdCategoryBrandList =resp;
					var selectCategory = $('#viewExceptionsCategorySelect');
					var selectBrand = $('#viewExceptionsBrandSelect');
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
						checkAll:true
					});
					
					selectBrand.multipleSelect({
						filter: true,
						placeholder: 'Select Brand',
						width: '100%',
						minimumCountSelected: 1,
					});
					
					if(!$rootScope.viewExceptionsBrandSelect){
						selectBrand.multipleSelect("disable");
					}
					if($rootScope.viewExceptionsCategorySelect){
						selectCategory.multipleSelect("setSelects", $rootScope.viewExceptionsCategorySelect);
						setBrandVal();
					}
					$("#viewExceptionsCategorySelect").change(function() {
						setBrandVal();
					});
				},
				error : function(e) {
					console.error(e);
				}
			});
			$scope.initTable();
		}
		$scope.init();
	}]);
	
	angular.module('PEAWorkbench').controller('viewPromotedProductController', ['$scope', '$compile', '$http', 'DTOptionsBuilder', 'DTColumnBuilder', 'services', '$rootScope', function($scope, $compile, $http, DTOptionsBuilder, DTColumnBuilder, services, $rootScope) {
		var lastChecked= null;
		$(document).on('click', '#applyBtn', function(event) {
			$scope.applyFilter();
		});
		$(document).on('click', '#clear', function(event) {
			$(".selectAll").prop('checked', false);
			var currentOpt= $(this);
			 $scope.$apply(function () {
				 $('#viewPromotedProductsPromotionSelect option').each(function() {
					 optId = currentOpt.val().replace('string:','');
					 var location= $scope.promotionOptions.map(function (element) {return element.value;}).indexOf(optId);
					 $scope.promotionOptions.splice(location,1);
				 });
			 });
			 $(".selectAll").prop('checked', false);
			$(".dataTableCheckbox:checked").each(function() {
				$(this).prop('checked', false);
			});
		});
		$(document).on('click', '.promotionLabel', function(event) {
			$('#promotionModal').modal({
			    backdrop: 'static',
			    keyboard: false
			},'toggle');
			$(".selectAll").prop('checked', false);
			if(($('#PromotionListTableId_processing').length <= 0) || ($('#PromotionListTableId_processing').css('display') != 'none') )
				$("#applyBtn, #clear").prop('disabled', true);
			if($scope['dtInstanceProductList'])
				$scope['dtInstancePromotionList'].DataTable.search('').draw();
			//$scope['dtInstanceProductList'].dataTable.fnAdjustColumnSizing();
		});
		$(document).on('click', '.dataTableCheckbox', function(event) {
			if (lastChecked && event.shiftKey) {
				var i = $('input[type="checkbox"]').index(lastChecked);
				var j = $('input[type="checkbox"]').index(event.target);
				var checkboxes = [];
				if (i == 0) {
					checkboxes.push($('input[type="checkbox"]:first'));
				}
				if (j > i) {
					checkboxes.push($('input[type="checkbox"]:gt(' + (i > 0 ? (i - 1) : 0) + '):lt(' + (j - i) + ')'));
				} else {
					checkboxes = $('input[type="checkbox"]:gt(' + j + '):lt(' + (i - j) + ')');
				}
				if (!$(event.target).is(':checked')) {
					//				$(checkboxes).removeAttr('checked');
					$(checkboxes).each(function() {
						$(this).prop('checked', false);
						$(this).trigger('change');
					});
				} else {
					//$(checkboxes).attr('checked', 'checked');
					$(checkboxes).each(function() {
						$(this).prop('checked', true);
						$(this).trigger('change');
					});
				}
			}
			
			lastChecked = event.target;
			
		});
		$('body').on('change', '.selectAll', function() {
			if (this.checked) {
				$(".dataTableCheckbox").prop('checked', true);
			 }else{
				$(".dataTableCheckbox").prop('checked', false);
			 }
			 $('.dataTableCheckbox').map(function()
			{
				$(this).trigger('change');
			});
		});
		$('body').on('change', '.dataTableCheckbox', function() {
			var chkBoxObj = $(this);
			var optlabel= $(this).attr('data');
			var optId= $(this).attr('id');
			var isPresentInListArray = $scope.promotionOptions.filter(function (option) { return option.value == optId });
			$scope.$apply(function () {
				if(isPresentInListArray.length == 0){
					$scope.promotionOptions.push({label:optlabel,value:optId});
				}else{
					 if (chkBoxObj.prop('checked') == true) {
					 }else{
						$(".selectAll").prop('checked', false);
						 var location= $scope.promotionOptions.map(function (element) {return element.value;}).indexOf(optId);
						 $scope.promotionOptions.splice(location,1);
					 }
				}
			});
		})
		
		$scope.promotionOptions = [];		
		$scope.services = services;
		$scope.backBtn= false;
		var currentPage = 'ViewPromotedProducts';
		$scope[currentPage] = {};
		$scope[currentPage]['inputParam'] = [{
			id: 'viewPromotedProductsPromotionSelect',
			type: 'select',
			format: 'html-multiSelect',
			reqParam: 'fltrPromotionId',
			defaultValue: $rootScope.selPromotionId
		}];
		
		$scope.initTable = function (opts) {
			
			if( $rootScope.selPromotionId && $rootScope.selPromotionId != "" ){
				$scope.services.datatableInit({
					tables: [currentPage],
					http: $http,
					scope: $scope,
					rootScope: $rootScope,
					compile: $compile,
					dtColumnBuilder: DTColumnBuilder,
					dtOptionsBuilder: DTOptionsBuilder,
					fixedColumns:{leftColumns: 1},
					isTable: false
				});
			}
			$scope.services.datatableInit({
				tables: ['PromotionList'],
				http: $http,
				scope: $scope,
				rootScope: $rootScope,
				compile: $compile,
				dtColumnBuilder: DTColumnBuilder,
				dtOptionsBuilder: DTOptionsBuilder,
				lengthMenu:[50, 100, 150, 200],
				pagingType: 'numbers',
				order: [1, 'asc'],
				colNotToSort:[0],
				onDrawCallback: function () {
					
					var options = $('#viewPromotedProductsPromotionSelect option');
					var values = $.map(options ,function(option) {
						$("#"+option.value.replace('string:','')).prop('checked', true);
					});
					if ($('.dataTableCheckbox').is(':not(:checked)')){
						$(".selectAll").prop('checked', false);
					}else{
						$(".selectAll").prop('checked', true);
					}
					$("#applyBtn, #clear").prop('disabled', false);
				},
//				buttons: {
//					applyFilter: true,
//					export: true
//				},
				isTable: false
			});
			
		}
		
		$scope.applyFilter = function () {
			//if ($("#ViewPromotedProductsTableId_wrapper").length > 0 ){
			if($scope['dtInstance' + currentPage] ){
				if($scope['dtInstance' + currentPage].DataTable)
					$scope['dtInstance' + currentPage].reloadData();
				//}
			}else{
				$scope.services.datatableInit({
					tables: [currentPage],
					http: $http,
					scope: $scope,
					rootScope: $rootScope,
					compile: $compile,
					dtColumnBuilder: DTColumnBuilder,
					dtOptionsBuilder: DTOptionsBuilder,
					fixedColumns:{leftColumns: 1},
					isTable: false
				});
			}
			$('#promotionModal').modal('hide');
			
		};
		
		$scope.init = function () {
			if($rootScope.selPromotionId){
				$scope.backBtn = true;
			}
			/*$scope.services.select.setOptions({
				selectId: 'viewPromotedProductsPromotionSelect', 
				url: '/getPromotionList', 
				inputParam: {},
				multipleSelect: {
					filter: true,
					placeholder: 'Select Promotions',
					width: '300',
					minimumCountSelected: 1,
					defaultValue: [$rootScope.selPromotionId]
				}
			}, true);*/
			
			$scope.initTable();
			$rootScope.selPromotionId = undefined;
		}
		$scope.init();
		$scope.back = function(){
			window.history.back();
		}
	}]);
	
	angular.module('PEAWorkbench').controller('viewWeeklyPromotionsController', ['$scope', '$compile', '$http', 'DTOptionsBuilder', 'DTColumnBuilder', 'services', function($scope, $compile, $http, DTOptionsBuilder, DTColumnBuilder, services) {
		$scope.services = services;
		$scope.initTable = function (opts) {
			$scope.services.datatableInit({
				tables: ['ViewWeeklyPromotion'],
				http: $http,
				scope: $scope,
				rootScope: $rootScope,
				compile: $compile,
				dtColumnBuilder: DTColumnBuilder,
				dtOptionsBuilder: DTOptionsBuilder,
				isTable: false
			});
		}
		
		$scope.init = function () {
			$scope.initTable();
		}
		$scope.init();
	}]);
	
	angular.module('PEAWorkbench').controller('viewDataAvailabilityStatsController', ['$scope', '$compile', '$http', 'DTOptionsBuilder', 'DTColumnBuilder', 'services','$location' , '$rootScope', function($scope, $compile, $http, DTOptionsBuilder, DTColumnBuilder, services,$location, $rootScope) {
		$scope.services = services;
		$scope.services.setup('viewDataAvailability');
		var currentPage = 'DataAvailability';
		$scope.accountName ='All'; 
		$scope.month = new Array();
		$scope.month = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
		$scope[currentPage] = {};
		$scope[currentPage]['inputParam'] = [{
			id: 'dataAvailabilityDatepicker',
			type: 'datepicker',
			format: 'month',
			reqParam: 'month'
		}, {
			id: 'dataAvailabilityDatepicker',
			type: 'datepicker',
			format: 'year',
			reqParam: 'year'
		}, {
			id: 'prevMonth',
			type: 'scope',
			reqParam: 'prevMonth',
			defaultValue: '12'
		}, {
			id: 'prevYear',
			type: 'scope',
			reqParam: 'prevYear',
			defaultValue: '2015'
		}, {
			id: 'curMonth',
			type: 'scope',
			reqParam: 'curMonth',
			defaultValue: '1'
		}, {
			id: 'curYear',
			type: 'scope',
			reqParam: 'curYear',
			defaultValue: '2016'
		},{
			id: 'dataAvailabilityAccountTableSelect',
			type: 'select',
			format: 'multiSelect',
			reqParam: 'fltrAccount',
			byText:true,
			defaultValue: $scope.fltrAccount
		}];
		var currentDate = new Date();
		var curMonth = $scope.month[currentDate.getMonth()];
		var curYear  = currentDate.getFullYear();
		var prev1MonDate = new Date(currentDate.setMonth(currentDate.getMonth() - 1))
		var prevMonth = $scope.month[prev1MonDate.getMonth()];
		var prevYear = prev1MonDate.getFullYear();
		
		$scope[currentPage]['headerHTML'] =   
			'<thead>' + 
		 '<tr>' + 
			'<th rowspan="2" style="width:112px;" >Account</th>' + 
			'<th colspan="4" style="border-bottom: 3px solid #f4f4f4;border-right: 3px solid #f4f4f4; text-align:center" ><span id="preMonth">'+ prevMonth+' '+prevYear+'</span></th>' + 
			'<th colspan="4" style="border-bottom: 3px solid #f4f4f4; text-align:center"><span id="currentMonth">'+ curMonth+' '+curYear+'</span></th>' + 
		'</tr> ' + 
		'<tr>' + 
		/*'	<th  style="width:112px;" ></th>' +*/ 
		'	<th style="width:100px;" >Shipment Volume</th>' + 
		'	<th style="width:100px;" >EPOS Volume</th>' + 
		'	<th style="width:100px;"  >Ratio %</th>' + 
		'	<th style="width:100px;" title="This is the difference of ratio between month and last month." >Diff %</th>' + 
		'	<th style="width:100px;"  >Shipment Volume</th>' + 
		'	<th style="width:100px;" >EPOS Volume</th>' + 
		'	<th style="width:100px;" >Ratio %</th>' + 
		'	<th style="width:100px;" title="This is the difference of ratio between month and last month." >Diff %</th>' + 
		'</tr>' + 
	'</thead>'; 
		//$scope[currentPage]['headerHTML'] = headerHTML;
		
		$scope[currentPage]['columnTooltip'] = {
			5: 'This is the difference of ratio between current month and last month.'
		};
		
		$scope[currentPage]['columnCls'] = {
			0: 'right-border',
			4: 'right-border'
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
				onDrawCallback: function () {
					if($(".dataTables_scrollBody table tbody tr:last td").length > 2 ){
						$($(".dataTables_scrollBody table tbody tr:last td")[0]).text('Total');
						angular.forEach($(".dataTables_scrollBody table tbody tr:last td"), function(value, key) {
							$(value).css("font-weight", "bold");
						});
					}
				},
				isTable: false
			});
		}
		
		
		
		//$scope.initTable();
		
		
		$scope.services.select.setOptions({
			selectId: 'dataAvailabilityAccountSelect', 
			url: '/getAccountList', 
			inputParams: {},
			multipleSelect: {
				filter: true,
				placeholder: 'Select Account',
				width: '200',
				minimumCountSelected: 1,
				checkAll: true
			}
		}, true);
		$scope.services.select.setOptions({
			selectId: 'dataAvailabilityAccountTableSelect', 
			url: '/getAccountList', 
			inputParams: {},
			multipleSelect: {
				filter: true,
				placeholder: 'Select Account',
				width: '200',
				minimumCountSelected: 1,
				checkAll: true
			}
		}, true);
		
		
		
		$scope.navTabClick= function(tabName,$event) {
			$($event.target).parents('ul').next('.tab-content').children().removeClass('active');
			$('#' + tabName).addClass('active');
			if(tabName.trim() == "table"){
				if($scope['dtInstanceDataAvailability']){
				$("#DataAvailability .dataTables_scroll").css('visibility', 'hidden');
				$scope['dtInstanceDataAvailability'].dataTable.fnAdjustColumnSizing();
				
				}
			}
		};
		$scope.applyFilter = function(){
			 var seletedVal = $('#dataAvailabilityAccountSelect').multipleSelect('getSelects', 'text');
			 if($('#dataAvailabilityAccountSelect option').length == $("#dataAvailabilityAccountSelect").multipleSelect("getSelects", "text").length){
				 $scope.accountName  = "All";
			 }else{
				 $scope.accountName  = $('#dataAvailabilityAccountSelect').multipleSelect('getSelects', 'text').toString();
			 }
			 $('.ajaxLoader').show();
			 	$.ajax({
			 		url: $('#contextPath').val() + '/viewGrouppedDataAvailability',
					method: 'GET',
					data: {
						selAccounts: seletedVal.join("|")
					},
					success: function (response) {
						var response = JSON.parse(response);
						var dataAvailabilityStatisticCategories = [];
						var dataAvailabilityStatisticShipmentData = [];
						var dataAvailabilityStatisticEPOSData = [];
						var dataAvailabilityStatisticRatio = [];
						angular.forEach(response, function(value, key){
							dataAvailabilityStatisticCategories.push(value.promotionEndMonth == null ? 0 : value.promotionEndMonth);
							dataAvailabilityStatisticShipmentData.push(value.shipmentVolume == null ? 0 : value.shipmentVolume);
							dataAvailabilityStatisticEPOSData.push(value.eposvolume == null ? 0 : value.eposvolume);
							dataAvailabilityStatisticRatio.push(value.sellOutToshipmentRatio == null ? 0 : value.sellOutToshipmentRatio);
						});
						dataAvailabilityChart.xAxis[0].update({categories:dataAvailabilityStatisticCategories}, true);
						dataAvailabilityChart.series[0].setData(dataAvailabilityStatisticShipmentData);
						dataAvailabilityChart.series[1].setData(dataAvailabilityStatisticEPOSData);
						dataAvailabilityChart.series[2].setData(dataAvailabilityStatisticRatio);
						$('.ajaxLoader').hide();
					},
					error: function (e) {
						$scope.status = e;
						$('.ajaxLoader').hide();
					}
			 	})
		}
		$scope.setCurPrevMonYrToScope = function (selectedDate) {
			$scope['curMonth'] = selectedDate.getMonth() + 1;
			$scope['curYear'] = selectedDate.getFullYear();
			var prev1MonDate = new Date();
			var prev1MonDate = new Date(selectedDate.setMonth(selectedDate.getMonth() - 1));
			$scope['prevMonth'] = prev1MonDate.getMonth() + 1;
			$scope['prevYear'] = prev1MonDate.getFullYear();
		}
		
		$scope.exportFunction  = function () {
			$scope.setCurPrevMonYrToScope(new Date($('#dataAvailabilityDatepicker').data('datepicker').viewDate));
			$scope[currentPage]['inputParam'];
			var seletedVal =[];
			if($('#dataAvailabilityAccountTableSelect option').length == $("#dataAvailabilityAccountTableSelect").multipleSelect("getSelects","text").length){
				seletedVal  = "";
			}else{
				seletedVal = $('#dataAvailabilityAccountTableSelect').multipleSelect('getSelects', 'text');
			} 
			if(seletedVal != ""){
				seletedVal=  seletedVal.join("|");
			}
			 var inputParams = $scope.services.getInputUrlParam($scope[currentPage]['inputParam']);
			window.location.href = $('#contextPath').val() + '/' + currentPage + '/export?curMonth=' + $scope['curMonth']+'&curYear='+$scope['curYear']+'&prevMonth='+$scope['prevMonth']+'&prevYear='+$scope['prevYear']+'&fltrAccount='+seletedVal ;
		}	
		
		$scope.tableviewApplyFilter =function(){
			$("#currentMonth").text( $scope.month[$scope['curMonth']-1]+' '+$scope['curYear']);
			$("#preMonth").text( $scope.month[$scope['prevMonth']-1]+' '+$scope['prevYear']);
			if ($scope['dtInstance' + currentPage])
				$scope['dtInstance' + currentPage].reloadData();
		}
		
		$scope.init =function(){
			$('#dataAvailabilityDatepicker').datepicker({
				format: 'M-yyyy',
				viewMode: 'months',
				minViewMode: 'months',
				endDate: '+0d',
				autoclose: true,
			});
			var d = new Date();
			var currMonth = $scope.month[d.getMonth()];
			var currYear = d.getFullYear();
			$('#dataAvailabilityDatepicker').datepicker('setDate', '' + currMonth + '-' + currYear);
			$('#dataAvailabilityDatepicker').on('changeDate', function(dateObj){
				$scope.setCurPrevMonYrToScope(dateObj.date);
			});
			
			$scope.setCurPrevMonYrToScope(new Date($('#dataAvailabilityDatepicker').data('datepicker').viewDate));
			
		$('.ajaxLoader').show();
		$http({
			url: $('#contextPath').val() + "/viewGrouppedDataAvailability",
			method: "GET",
		}).success(function(data, status, headers, config) {
			$scope.data = data;
			var dataAvailabilityStatisticCategories = [];
			var dataAvailabilityStatisticShipmentData = [];
			var dataAvailabilityStatisticEPOSData = [];
			var dataAvailabilityStatisticRatio = [];
			
			angular.forEach($scope.data, function(value, key){
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
					opposite: true,
					
				}, { // Primary yAxis
					labels: {
						/*formatter: function() {
					           var ret,
					               numericSymbols = ['Thousand','Million','Billion','Trillion',' quadrillion','E'],
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
					},
				}, ],
				tooltip: {
/*					headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
					pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name} : </td>' + '<td style="padding:0"><b>{point.y} </b></td></tr>',
					formatter: function(){
						console.log('kn - ' +this.y)
						if(this.y < 0){
							return '<tr><td style="color:{series.color};padding:0">{series.name} : </td>' + '<td style="padding:0"><b style="color:red;">{point.y} </b></td></tr>'
						}
					},
*/					
					formatter: function() {
						console.log(this);
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
		            positioner: function(boxWidth, boxHeight, point) {
		                return {
		                    x: point.plotX + 20,
		                    y: point.plotY
		                };
		            },
					//footerFormat: '</table>',
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
					name: 'Sellin Data',
					type: 'column',
					yAxis: 1,
					data: dataAvailabilityStatisticShipmentData,
					tooltip: {
						valueSuffix: ''
					}
				}, {
					name: 'Sellout Data',
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
			$('.ajaxLoader').hide();
		});
		$scope.initTable();
	}
		
		$scope.init();
		
	}]);
	
	angular.module('PEAWorkbench').controller('configureDataViewsController', ['$scope', 'services', '$http', function($scope,services, $http){
		
		$scope.getColumns = function () {
			$('.ajaxLoader').show();
			$http({
				url: $('#contextPath').val() + '/config/getViewColumns',
				method: "GET",
				data: {
					nn: 'nn'
				}
			}).success(function(data, status, headers, config) {
				$('.ajaxLoader').hide();
				$scope.viewColumns = data;
				$scope.refreshColumns();
			}).error(function(data, status, headers, config) {
				$('.ajaxLoader').hide();
			});
		}
		

		$scope.refreshColumns = function () {
			console.log($scope.selectedView);
			var display = 'display', hidden = 'hidden', view = $scope.selectedView;
			var columns = $scope.viewColumns[view][display];
			$('ul#list2').empty();
			var li = '<li class="ui-state-default unsortable uw-unmovable">'+
				'<span class="ui-icon ui-icon-arrowthick-2-n-s userexceptionreport_choosecoloumn_li_span"></span>'+
				'</li> ';
			$('ul#list2').append(li);
			if(columns != null && columns.length > 0 ){
				angular.forEach(columns, function(value, key) {
					var li = "";
					if(value == 'PromoID' || value == "Promo ID"){
						var li = '<li class="ui-state-default  unsortable uw-unmovable"> ' +
						'<span class="ui-icon ui-icon-arrowthick-2-n-s userexceptionreport_choosecoloumn_li_span"></span> ' +
						'<div class="liinnertext">' + value + '</div> ' +
					'</li> ';
					}else{
						var li = '<li class="ui-state-default"> ' +
								'<span class="ui-icon ui-icon-arrowthick-2-n-s userexceptionreport_choosecoloumn_li_span"></span> ' +
								'<div class="liinnertext">' + value + '</div> ' +
							'</li> ';
					}
					$('ul#list2').append(li);
				});
			}
			
			columns = $scope.viewColumns[view][hidden];
			$('ul#list1').empty();
			var li = '<li class="ui-state-default unsortable uw-unmovable">'+
				'<span class="ui-icon ui-icon-arrowthick-2-n-s userexceptionreport_choosecoloumn_li_span"></span>'+
				'</li> ';
			$('ul#list1').append(li);
			if (columns != null && columns.length > 0) {
				angular.forEach(columns, function(value, key) {
					var li = '<li class="ui-state-default"> ' +
								'<span class="ui-icon ui-icon-arrowthick-2-n-s userexceptionreport_choosecoloumn_li_span"></span> ' +
								'<div class="liinnertext">' + value + '</div> ' +
							'</li> ';
					$('ul#list1').append(li);
				});
			}
			$('#list2 li').mousedown(function(e) {
				console.log('event');
				$( '#list2 li' ).each(function() {
					if ( $( this ).hasClass('uw-unmovable') ) {
				   		$( this).removeClass('selected')
				    }
				});
			    if ( $( this ).hasClass('uw-unmovable') ) { 
			   		$( this).removeClass( 'selected' )
			    }
			});
			
			$('#list1 li').mousedown(function(e) {
				$( '#list1 li' ).each(function() {
					if ( $( this ).hasClass('uw-unmovable') ) {
						$( this).removeClass( 'selected' )
					}
				});
				if ( $( this ).hasClass('uw-unmovable') ) {
					$( this).removeClass( 'selected' )
				}
			});
		}
		
		$scope.applyConfigChanges = function () {
			$('.ajaxLoader').show();
			var view = $scope.selectedView, displayColumns = [], hiddenColumns = [];
			$('ul#list1 .liinnertext').each(function (a, b) {
				hiddenColumns.push($(b).text());
			});
			$('ul#list2 .liinnertext').each(function (a, b) {
				displayColumns.push($(b).text());
			});
			$(".ajaxLoader").show();
			$.ajax({
				type : 'POST',
				url: $('#contextPath').val() + '/config/saveViewColumns',
				data: {
					'hiddenColumns': hiddenColumns.join('|'),
					'displayColumns': displayColumns.join('|'),
					'view': view
				},
				success : function(response) {
					$('.ajaxLoader').hide();
					if (response) {
						response = JSON.parse(response);
						if (response.Message) 
							services.showNotification(response.Message);
					}
				},
				error : function(e) {
					$('.ajaxLoader').hide();
					console.error(e);
				}
			});
		}
		
		$scope.init = function () {
			$('.sidebar-menu li.active').removeClass('active');
	    	//$($('.sidebar-menu span:contains('Manage Data')')[0]).parents('li').addClass('active');
	    	$($('.sidebar-menu span:contains("Configure") ')[1]).parents('li').addClass('active');
	    	
	    	
	    	$scope.configureViews = [{ id: 'ViewExceptions', name: 'Manage Exceptions' }, 
	    	                         { id: 'ViewPromotedProducts', name: 'View Promoted Products' }, 
	    	                         { id: 'ViewDatasets', name: 'Export Datasets' }];
	    	$scope.selectedView = 'ViewExceptions';
	    	$scope.getColumns();
			
	    	$('ul.sortable').multisortable();
			$('ul#list1').sortable('option', 'connectWith', 'ul#list2');
			$('ul#list2').sortable('option', 'connectWith', 'ul#list1');
			$('.sortable').sortable({
				items : 'li:not(.unsortable)'
			});
			$('.sortable').disableSelection();
			$( '#list2 li.uw-unmovable' ).hide();
			var newLi = $('<li class="ui-state-default unsortable uw-unmovable" style="border:0px;height:0px;"></li>').insertAfter( '#list2 li.uw-unmovable' ); 
			
		

			function toggle(source) {
				checkboxes = document.getElementsByName('colList');
				for (var i = 0, n = checkboxes.length; i < n; i++) {
					checkboxes[i].checked = source.checked;
				}
				$('input[name=selectAll]').each(function() {
					this.nextSibling.nodeValue = source.checked ? 'Unselect all': 'Select all';
				});
			}

			function setColStr() {
				var colStrSetting = $('#list2 li').map(function(i, n) {
					console.log(i + '   ok.....' + $(n).text());
					return $(n).text();
				}).get().join(',') + ',Grouped Column';

				var hiddenStrSetting = $('#list1 li').map(function(i, n) {
					//console.log(i+'   ok.....'+$(n).text());
					return $(n).text();
				}).get().join(',');

				var f = document.getElementById('userSettingsForm');
				var hidden = document.createElement('input');
				hidden.type = 'hidden';
				hidden.name = 'colStrSetting';
				hidden.value = colStrSetting;

				f.appendChild(hidden);

				hidden = document.createElement('input');
				hidden.type = 'hidden';
				hidden.name = 'hiddenStrSetting';
				hidden.value = hiddenStrSetting;

				f.appendChild(hidden);

				$('#displayColumns').val(colStrSetting);
				$('#hiddenColumns').val(hiddenStrSetting);

				f.submit();
			}
		}
		$scope.init();
	}]);
	
	angular.module('PEAWorkbench').controller('reprocessPromotionsController', ['$scope', '$compile', '$http', 'DTOptionsBuilder', 'DTColumnBuilder', 'services', '$rootScope', '$location', function($scope, $compile, $http, DTOptionsBuilder, DTColumnBuilder, services, $rootScope, $location) {
		$scope.services = services;
		var currentPage = 'ReprocessPromotions';
		
		
		$scope.updateReprocessPromotions = function () {
			
			var editedData = {};
			
			$('[class^="' + currentPage + '-ef-"][class*="border-red"]').each(function(i, el) {
				var cls = $(el).attr('class').split(' ')[0], 
				rowKey = $(el).attr('data-rowkey');
				
				if (editedData[rowKey] == null) {
					editedData[rowKey] = {};
				}
				
				var clsTkn = cls.split('-');
				if (clsTkn.length > 2) {
					if (clsTkn[2] == 'cb') {
						editedData[rowKey][clsTkn[clsTkn.length - 1]] = ($(el).is(':checked') ? 'Y' : 'N'); 
					}
				}
			});
			
			if ($.isEmptyObject(editedData)) {
				$scope.services.showNotification('No row is updated.');
				return false;
			}
			
			$('.ajaxLoader').show();
			
			$.ajax({
				type : 'POST',
				url : $('#contextPath').val() + '/' + currentPage + '/saveEditChanges',
				data : {
					editData: JSON.stringify(editedData)
				},
				success : function(response) {
					
					$('.ajaxLoader').hide();
					
					$('[class^="' + currentPage + '-ef-"][class*="border-red"]').each(function(i, el) {
						$(el).removeClass('border-red');
					});
					
					var response = JSON.parse(response);
					$scope.services.showNotification(response['Message']);
					
					//Reload
					if ($scope['dtInstance' + currentPage])
						$scope['dtInstance' + currentPage].reloadData();
					
				},
				error : function(e) {
					$('.ajaxLoader').hide();
					console.error(e);
					$scope.services.showNotification(response['Message']);
				}
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
				
				onInitComplete:function(){
					$( '#' + currentPage + 'TableId_length' ).append( 
						$( 
							'<button class="btn btn-primary ng-binding" id="updateReprocessPromotions" data-original-title="" title="" style="margin-left:10px;">Update</button>'
						) 
					);

					$('#updateReprocessPromotions').confirmation({
						singleton: true,
						title: 'Are you sure want to save the changes?',
						placement: 'bottom',
						onConfirm: function () {
							$scope.updateReprocessPromotions();
							
							//Reload
							if ($scope['dtInstance' + currentPage])
								$scope['dtInstance' + currentPage].reloadData();
						}
					});
				},
				onDrawCallback: function () {
					// Editable input boxes
					$('[class^=' + currentPage + '-ef-]').on('change', function (e) {
						var _this = this;
						$(_this).addClass('border-red'); //css("border", "red solid 1px");
					});
					
				},
				isTable: false
			});
		}
		
		$scope.init = function () {
			$scope.initTable();
		}
		
		$scope.init();
		
	}]);
	