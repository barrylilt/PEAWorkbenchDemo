function populateProduct() {
	//if ($('#countryCode').val() == 'CA') {
	//	return;
	//}
	var d = new Date();
	var st = d.getTime();
	var catArr = $('#categoryDropdown').val();
	if (catArr != null) {
		var categoryName = "";
		$.each(catArr, function(index, value) {
			categoryName = categoryName + "'" + value + "'" + ",";
		});
		categoryName = categoryName.substring(0, categoryName.length - 1);
		//		console.log(categoryName);
		if (categoryName == "selectCategory") {
			$("#productDropdown").val('selectProduct');
		} else {
			$.ajax({
				"type" : "GET",
				"url" : "/HarmonizerWorkbench/getProductForCategory",
				"dataType" : "text",
				"data" : {
					"categoryName" : categoryName
				},
				"success" : function(data) {
					//console.log("data---" + data);

					var strArr = new Array(data);
					strArr = data.split(",");
					//console.log(typeof strArr);
					//console.log("strArr-" + strArr);

					$('#productDropdown option[value!="selectProduct"]')
							.remove();
					var select = document.getElementById("productDropdown");
					for (var i = 0; i < strArr.length; i++) {
						var opt = strArr[i];
						var el = document.createElement("option");
						el.textContent = opt;
						el.value = opt;
						el.title=opt;
						select.appendChild(el);
					}
				},
				"error" : function() {
					console.log("error");
				}
			});

		}

		d = new Date();
		var et = d.getTime();
		console.log("Time taken by populateProduct:" + (et - st) + "ms.");
	}
}

function populateNielsenList() {
	var d = new Date();
	var st = d.getTime();

	var aprsAccountArr = $('#aprsAccountDropdown').val();
	if (aprsAccountArr != null) {
		var aprsAccount = "";
		$.each(aprsAccountArr, function(index, value) {
			aprsAccount = aprsAccount + "'" + value + "'" + ",";
		});
		aprsAccount = aprsAccount.substring(0, aprsAccount.length - 1);
		if (aprsAccount == "aprsAccount") {
			$("#NielsenAccountDropdown").val('nielsenAccount');
		} else {
			$
					.ajax({
						"type" : "GET",
						"url" : "/HarmonizerWorkbench/getNielsenAccForAPRSAcc",
						"dataType" : "text",
						"data" : {
							"aprsAccName" : aprsAccount,
						},
						"success" : function(data) {
							console.log("data---" + data);

							var strArr = new Array(data);
							strArr = data.split(",");
							console.log(typeof strArr);
							console.log("strArr-" + strArr);

							$(
									'#NielsenAccountDropdown option[value!="nielsenAccount"]')
									.remove();
							var select = document
									.getElementById("NielsenAccountDropdown");
							for (var i = 0; i < strArr.length; i++) {
								var opt = strArr[i];
								var el = document.createElement("option");
								el.textContent = opt;
								el.value = opt;
								select.appendChild(el);
							}
						},
						"error" : function() {
							console.log("error");
						}
					});
		}
		d = new Date();
		var et = d.getTime();
		console.log("Time taken by populateNielsenList:" + (et - st) + "ms.");
	}
}
function clearFilter() {
	$.each($('#accord-filters-1 .selectpicker'), function(index, value) {
		$(value).val('Show All');
	});
	
	$('#exceptionDateRange').val($('#defaultDatasetDateRange').val());
	// Cleared PPG Name & Nielsen Account select fields 
	//$('#NielsenAccountDropdown option[value!="nielsenAccount"]').remove();
	//if ($('#countryCode').val() != 'CA') {
		$('#productDropdown option[value!="selectProduct"]').remove();
	//}
}
