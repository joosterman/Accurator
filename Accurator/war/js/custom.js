var dataQueryURL = "http://eculture.cs.vu.nl:3737/utils?util=subset&";
//var proxyURL = "http://127.0.0.1:8888/accurator/utility?proxy_url=";
var proxyURL = "https://rma-accurator.appspot.com/accurator/utility?proxy_url=";
// Account: Jasper Oosterman
var key = "7838d27f0e9ef76b3176eecd28824f79a00907aa";

// The Google DataTable objects
var inputDataTable = null;
var jobDataTable = null;
// The Google Table visualizations
var inputTable = null;
var jobTable = null;

$(document)
	.ready(function() {
		// prepare the crowdflower object
		$.crowdflower.key(key).proxyURL(proxyURL);
		// prepare the default HTTP error handler
		$.crowdflower.ajaxFail(function(jqXHR, textStatus, errorThrown) {
			if (textStatus) {
				alertify.error("Crowdflower HTTP error: " + textStatus + "<br />" + errorThrown);
			}
		});
		// prepare the default http success handler (this might still give
		// crowdflower error)
		$.crowdflower.ajaxDone(function(data) {
			if (data.errors) {
				var error = "Crowdflower error(s): <ul>";
				for ( var i = 0; i < data.errors.length; i++) {
					error += "<li>" + data.errors[i] + "</li>";
				}
				error += "</ul>";
				alertify.error(error);
			}
		});

		// create the visualization
		inputTable = new google.visualization.Table(document.getElementById('inputDataTable'));
		jobTable = new google.visualization.Table(document.getElementById('jobDataTable'));

		$("#formNewJob").submit(function(event) {
			// get the text fields and the values from the form elements
			var data = $(this).serializeArray();
			console.log(data);
			// store into one attr object
			var attr = {};
			for ( var i = 0; i < data.length; i++) {
				var name = data[i].name;
				var value = data[i].value;
				attr[name] = value;
			}

			$.crowdflower.jobs.create(attr).success(function(data) {
				setTimeout(function() {
					reloadJobs();
				}, 2000);
			});

			return false;
		})

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
			var url = dataQueryURL + "operation=" + operation + "&field=" + encodeURIComponent(field) + "&value="
				+ encodeURIComponent(value);
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
			if(jobTable){
				closePopovers();
			}
		});

		function reloadJobs() {
			closePopovers();
			$.crowdflower.jobs.read().done(function(data) {
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

		/**
		 * Parsed the DataTable and creates an array of objects. The column
		 * names become the object properties.
		 * 
		 * @param dataTable
		 *            A Google Visualization dataTable
		 * @return Array of objects
		 */
		function dataTableToArray(dataTable) {
			// put the datatable in simple JSON
			var strData = dataTable.toJSON();
			var data = JSON.parse(strData)
			// get the columns names (these will be the object property names
			var columns = [];
			for ( var i = 0; i < data.cols.length; i++) {
				var col = data.cols[i];
				var colName = col.label
				columns.push(colName);
			}
			// get the data rows and put in to objects
			var objs = [];
			for ( var i = 0; i < data.rows.length; i++) {
				var obj = {};
				var row = data.rows[i];
				var values = row.c;
				for ( var j = 0; j < values.length; j++) {
					var value = values[j].v;
					obj[columns[j]] = value;
				}
				objs.push(obj);
			}
			return objs;
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
			// add the handlers to the rows
			$("#jobDataTable tr:not(:first) td:first-child")
				.popover({
					placement : "top",
					trigger : "click",
					title : "Job operations",
					html : true,
					content : function() {
						// get the id of the job from the table
						var id = +($(this)[0].innerText);
						// hide other popovers
						// closePopovers();
						// HACK: add the click handlers in a scheduled task so
						// that the buttons are rendered first
						setTimeout(function() {
							$("#btnUpdateJob").click(function() {
								updateJob(id);
								return false;
							});
							$("#btnDeleteJob").click(function() {
								deleteJob(id);
								return false;
							});
							$("#btnAddDataToJob").click(function() {
								addDataToJob(id);
								return false;
							});
							$("#btnCopyJob").click(function() {
								copyJob(id);
								return false;
							});
							$("#btnDownloadJob").click(function() {
								downloadJob(id);
								return false;
							});
						}, 200);
						// show a nicely formatted button group
						return "<div class='btn-group'>  <button id='btnUpdateJob' class='btn'>Update</button>  <button id='btnDeleteJob' class='btn'>Delete</button>  <button id='btnAddDataToJob' class='btn'>Add saved data</button> <button id='btnCopyJob' class='btn'>Copy</button><button id='btnDownloadJob' class='btn'>Download</button>	</div>";
					},
					container : "body",
				});
		}

		function closePopovers() {
			$("#jobDataTable tr:not(:first) td:first-child").popover("hide");
		}

		function updateJob(job_id) {
		}
		function deleteJob(job_id) {
			// confirm delete
			bootbox.confirm("Are you sure you want to delete job with id " + job_id + "?", function(e) {
				if (e) {
					$.crowdflower.job(job_id).remove().success(function() {
						alertify.log("Job " + job_id + " successfully deleted.")
					});
					// close the popovers
					closePopovers();
					// reload the jobs
					reloadJobs();
				} else {
					// user clicked "cancel",do nothing
				}
			});
		}
		function addDataToJob(job_id) {
			if (typeof localStorage.AccuratorCrowdflowerInput === 'undefined') {
				alertify.error("No data has been saved yet.");
			} else {
				var dataTable = new google.visualization.DataTable(localStorage.AccuratorCrowdflowerInput);
				var data = dataTableToArray(dataTable);
				$.crowdflower.job(job_id).upload.json(null, data).success(function(d) {
					alertify.log(data.length + " items have been loaded into job " + job_id);
					setTimeout(function() {
						reloadJobs();
					}, 2000);
				});
			}
		}

		function copyJob(job_id) {
			$.crowdflower.job(job_id).copy({
				all_units : false,
				gold : false
			}).success(function(data) {
				alertify.log("Job " + job_id + " has been successfully copied.");
				setTimeout(function() {
					reloadJobs();
				}, 2000);
			});
		}

		function downloadJob(job_id) {
			console.log("here");
			$.crowdflower.job(job_id).download({
				type : "source"
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
