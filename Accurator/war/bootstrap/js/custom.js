var dataQueryURL = "http://eculture.cs.vu.nl:3737/utils?util=subset&";
var proxyURL = "http://127.0.0.1:8888/accurator/utility?proxy_url=";
// Account: Jasper Oosterman
var key = "7838d27f0e9ef76b3176eecd28824f79a00907aa";

// The Google DataTable objects
var inputDataTable = null;
var jobDataTable = null;
// The Google Table visualizations
var inputTable = null;
var jobTable = null;

$(document).ready(function() {
	// prepare the crowdflower object
	$.crowdflower.key(key).proxyURL(proxyURL);

	// create the visualization
	inputTable = new google.visualization.Table(document.getElementById('inputDataTable'));
	jobTable = new google.visualization.Table(document.getElementById('jobDataTable'));

	$("#inputCSVFile").change(function(upload) {
		var files = upload.target.files
		var file = files[0];
		var reader = new FileReader();
		reader.onload = (function(theFile) {
			return function(e) {
				var csv = reader.result;
				var table = $.csv.toArrays(csv);
				inputDataTable = google.visualization.arrayToDataTable(table);
				drawInputTable(inputDataTable);
			};
		})(file);
		reader.readAsText(file);
	});

	$("#btnQuery").click(function() {
		var field = $("#txtQueryFieldName").val();
		var value = $("#txtQueryFieldValue").val();
		var operation = $("#selQueryOperation").val();
		var url = dataQueryURL + "operation=" + operation + "&field=" + encodeURIComponent(field) + "&value=" + encodeURIComponent(value);
		var proxy = proxyURL + encodeURIComponent(url);
		$.get(proxy, function(data) {
			if (data.nritems === 0) {
				alertify.log("No result for this query");
				inputTable.clearChart();

			} else {
				// add the header row
				var table = new Array();
				var header = new Array();
				for (key in data.items[0]) {
					header.push(key);
				}
				table.push(header);
				// add the items
				for (item in data.items) {
					var row = new Array();
					for (key in data.items[0]) {
						var val = data.items[item][key];
						// Flatten arrays
						if (Array.isArray(val)) {
							val = JSON.stringify(val);
						}
						row.push(val);
					}
					table.push(row);
				}
				inputDataTable = google.visualization.arrayToDataTable(table);
				drawInputTable(inputDataTable);
			}

		});
		return false;
	});

	$("#btnSaveTable").click(function() {
		localStorage.AccuratorCrowdflowerInput = inputDataTable.toJSON();
		alertify.log("Table has been stored in local storage. Items: " + inputDataTable.getNumberOfRows());
		return false;
	});

	$("#btnLoadTable").click(function() {
		if (typeof localStorage.AccuratorCrowdflowerInput === 'undefined') {
			alertify.error("No table has been saved yet!");
		} else {
			inputDataTable = new google.visualization.DataTable(localStorage.AccuratorCrowdflowerInput);
			drawInputTable();
			alertify.log("Saved table has been restored. Items: " + inputDataTable.getNumberOfRows());
		}
		return false;
	});

	$("#btnReloadJobs").click(function() {
		reloadJobs();
		return false;
	});
	function reloadJobs() {
		$.crowdflower.jobs.read().success(function(data) {
			if (data && data.length > 0) {
				// add the header row
				var table = new Array();
				var header = new Array();
				for (key in data[0]) {
					header.push(key);
				}
				table.push(header);
				// add the items
				for ( var i = 0; i < data.length; i++) {
					var row = new Array();
					for (key in data[0]) {
						var val = data[i][key];
						// Flatten all values
						val = JSON.stringify(val);
						row.push(val);
					}
					table.push(row);
				}
				// create the DataTable and visualize it
				jobDataTable = google.visualization.arrayToDataTable(table);
				drawJobTable();
			}
		});
	}

	$("#btnCreateJob").click(function() {
		var attr = {
			title : $("#txtJobTitle").val()
		};
		$.crowdflower.jobs.create(attr).success(function(data) {
			console.log(data);
			reloadJobs();
		});
		return false;
	});

	$("#btnAddData").click(function() {
		// check if data was saved
		if (typeof localStorage.AccuratorCrowdflowerInput === 'undefined') {
			alertify.error("No data has been saved yet.");
		}
		else if(getSelectedJobIds().length===0){
			alertify.error("No jobs have been selected.");
		}
		else{
			var dataTable = new google.visualization.DataTable(localStorage.AccuratorCrowdflowerInput);
			dataTableToArray(dataTable);
		}
		return false;
	});

	$(document).keyup(function(event) {
		// trigger when the delete button is pressed
		if (inputTable != null && event.keyCode === 46) {
			var items = inputTable.getSelection();
			if (items.length > 0) {
				// delete the selected rows in reversed order to maintain
				// correct row index
				for ( var i = items.length; i > 0; i--) {
					var rowNr = items[i - 1].row;
					inputDataTable.removeRow(rowNr);
				}
				// redraw the table
				drawInputTable();
			}
		}
	});

	function dataTableToArray(dataTable){
		var data = dataTable.toJSON();
		console.log(data);
	}
	
	function drawInputTable() {
		inputTable.draw(inputDataTable, {
			page : "enable",
			pageSize : 8,
		});
		inputTable.setSelection(null);
	}

	function drawJobTable() {
		// get the labels of the table
		var labels = [];
		for ( var i = 0; i < jobDataTable.getNumberOfColumns(); i++) {
			labels.push(jobDataTable.getColumnLabel(i));
		}
		var interestingColumns = [ "id", "title", "created_at", "units_count", "golds_count", "completed" ];
		var columns = [];
		for ( var i = 0; i < interestingColumns.length; i++) {
			columns.push($.inArray(interestingColumns[i], labels));
		}

		var view = new google.visualization.DataView(jobDataTable);
		view.setColumns(columns);
		jobTable.draw(view, {
			page : "enable",
			pageSize : 10,
		});
	}

	function getColumnId(dataTable, label) {
		var labels = [];
		for ( var i = 0; i < dataTable.getNumberOfColumns(); i++) {
			labels.push(dataTable.getColumnLabel(i));
		}
		return $.inArray(label, labels)
	}

	function getSelectedJobIds() {
		var items = jobTable.getSelection();
		var ids = [];
		if (items.length > 0) {
			for ( var i = 0; i < items.length; i++) {
				var rowNr = items[i].row;
				var columnId = getColumnId(jobDataTable, "id")
				ids.push(jobDataTable.getValue(rowNr, columnId));
			}
		}
		return ids;
	}

});
