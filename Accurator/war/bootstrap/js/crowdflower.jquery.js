/**
 * Utility functions
 */
/**
 * Adds a format function to strings using the {1} format.
 */
// first, checks if it isn't implemented yet
if (!String.prototype.format) {
	String.prototype.format = function() {
		"use strict";
		var args = arguments;
		return this.replace(/{(\d+)}/g, function(match, number) {
			return typeof args[number] !== 'undefined' ? args[number] : match;
		});
	};
}

/**
 * The crowdflower object that contains all the API methods in JS function calls
 */
(function($) {
	"use strict";
	var settings = {
		proxyURL : null,
		baseURL : "https://api.crowdflower.com/v1/",
		format : ".json",
		key : null,
	};

	var jobParamPrefix = "job[";
	var unitParamPrefix = "unit[";
	var judgementParamPrefix = "judgement[";
	var paramPostfix = "]";

	var httpMethods = {
		GET : "GET",
		POST : "POST",
		PUT : "PUT",
		DELETE : "DELETE",
	};

	var defaultAjaxSettings = {
		dataType : "json",
		done : null,
		fail : null,
	}

	/**
	 * @param objSettings
	 *            Object containing properties for AJAX request
	 * @returns the provided setting with the default setting. Provided setting
	 *          will override the default.
	 */
	function ajaxSettings(objSettings) {
		return $.extend({}, defaultAjaxSettings, objSettings);
	}

	/**
	 * Returns the full URL, made up by the proxyURL, the baseURL and the trail
	 * as defined by the API call. The baseURL and trail are URL encoded if
	 * proxyURL not is empty.
	 * 
	 * @param url
	 */
	function fullURL(path, urlParams, addFormat) {
		if (typeof addFormat === "undefined") {
			addFormat = true;
		}

		var result = null;
		var trail = settings.baseURL + path;
		if (addFormat) {
			trail += settings.format;
		}
		trail += "?" + "key=" + settings.key + "&";
		// add the urlParams
		if (urlParams) {
			for ( var param in urlParams) {
				trail += param + "=" + urlParams[param] + "&";
			}
		}
		console.log(trail);
		if (!settings.proxyURL) {
			result = trail;
		} else {
			var escapedTrail = encodeURIComponent(trail);
			result = settings.proxyURL + escapedTrail;
		}
		return result;
	}

	function apiURL(path, urlParams, addFormat) {
		if (typeof addFormat === "undefined") {
			addFormat = true;
		}
		var trail = settings.baseURL + path;
		if (addFormat) {
			trail += settings.format;
		}
		trail += "?" + "key=" + settings.key + "&";
		// add the urlParams
		if (urlParams) {
			for ( var param in urlParams) {
				trail += param + "=" + urlParams[param] + "&";
			}
		}
		return trail;
	}

	function dataUpload(path, attr, data, contentType, method) {
		if (data) {
			// check if the data is an array of objects
			var goodData = true;
			goodData = goodData && $.isArray(data);
			for ( var row in data) {
				goodData = goodData && $.isPlainObject(data[row]);
			}
			if (!goodData) {
				throw "[data upload] Data needs to be an Array of Objects";
			}
			// serialize the data. Write out only the objects (not
			// the array.).
			var strData = "";
			for ( var row in data) {
				strData += JSON.stringify(data[row]) + "\n";
			}

			// add the attributes to the URL so strData can be sent
			// as data
			return $.ajax(fullURL(path, attr), ajaxSettings({
				data : strData,
				type : method,
				processData : false,
				contentType : contentType,
			}));

		} else {
			throw "[data upload] Data was not provided";
		}
	}

	var allowedAttr = {
		"job.downloadURL" : [ "type", "full" ],
		"job.upload.json" : [ "force" ],
		"job.update" : [ "auto_order", "auto_order_threshold", "auto_order_timeout", "cml", "cml_fields", "confidence_fields", "css",
			"custom_key", "excluded_countries", "gold_per_assignment", "included_countries", "instructions", "js", "judgments_per_unit",
			"language", "max_judgments_per_unit", "max_judgments_per_worker", "min_unit_confidence", "options", "pages_per_assignment",
			"problem", "send_judgments_webhook", "state", "title", "units_per_assignment", "webhook_uri" ],
		"job.copy" : [ "all_units", "gold" ],
		"job.gold" : [ "reset", "check", "with" ],
		"job.units.create" : [ "job_id", "missed_count", "difficulty", "state", "data", "agreement", "golden" ],
		"job.unit.update" : [ "job_id", "missed_count", "difficulty", "state", "data", "agreement", "golden" ],
		"job.judgment.update" : [ "webhook_sent_at", "reviewed", "missed", "tainted", "country", "region", "city", "golden", "unit_state",
			"data" ],
		"job.judgment.read" : [ "page", "limit" ],
		"job.judgments.create" : [ "webhook_sent_at", "reviewed", "missed", "tainted", "country", "region", "city", "golden", "unit_state",
			"data" ],
		"job.orders.create" : [ "job_id", "debit[units_count]", "channels" ],
		"jobs.upload.json" : [ "force" ],
		"jobs.create" : [ "auto_order", "auto_order_threshold", "auto_order_timeout", "cml", "cml_fields", "confidence_fields", "css",
			"custom_key", "excluded_countries", "gold_per_assignment", "included_countries", "instructions", "js", "judgments_per_unit",
			"language", "max_judgments_per_unit", "max_judgments_per_worker", "min_unit_confidence", "options", "pages_per_assignment",
			"problem", "send_judgments_webhook", "state", "title", "units_per_assignment", "webhook_uri" ],

	};

	/**
	 * Case 1: !allowedAttr || !attr : return true Case 2: allowedAttr && attr:
	 * Check whether the attributes are allowed for the function. * Throws an
	 * expection if attr contains an attribute that is a function.
	 * 
	 * @param attr
	 *            Object
	 * @param allowedAttr
	 *            Array of strings
	 */
	function checkAttributes(attr, allowedAttr) {
		if (attr && allowedAttr) {
			for ( var at in attr) {
				if ($.inArray(at, allowedAttr) === -1) {
					throw "[check attributes] Attribute '" + at + "' is not allowed. Allowed attributes: " + JSON.stringify(allowedAttr);
				}
				if ($.isFunction(attr[at])) {
					throw "[check attributes] The value of an attribute can't be a function";
				}
			}
		}
		return true;
	}

	/**
	 * Checks whether !attr.
	 * 
	 * @param attr
	 *            Object
	 * @param allowedAttr
	 *            Array of strings
	 */
	function requireAttributes(attr, requiredAttr) {
		// if !requiredAttr only check whether there are any attributes
		if (!requiredAttr) {
			if (!attr) {
				throw "Attributes are required.";
			}
		}
		// if requiredAttr check whether all required attributes are
		// in attr
		else {
			if (attr) {
				for ( var i = 0; i < requiredAttr.length; i++) {
					if (!attr[requiredAttr[i]]) {
						throw "Required attribute was not provided. Required attributes: " + JSON.stringify(requiredAttr);
					}
				}
			} else {
				throw "Attributes are requires";
			}
		}
	}

	$.crowdflower = {
		key : function(strKey) {
			settings.key = strKey;
			return this;
		},
		proxyURL : function(strProxyURL) {
			settings.proxyURL = strProxyURL;
			return this;
		},
		ajaxFail : function(func) {
			defaultAjaxSettings.fail = func;
			return this;
		},
		ajaxDone : function(func) {
			defaultAjaxSettings.done = func;
			return this;
		},
		allowedAttributes : function(fname) {
			return allowedAttr[fname];
		},
		job : function(job_id) {
			if (!job_id || !$.isNumeric(job_id)) {
				throw "Please provide a valid job id";
			}
			return {
				download : function(attr) {
					var path = "jobs/{0}.csv".format(job_id);
					checkAttributes(attr, allowedAttr["job.download"]);
					var downloadURL = apiURL(path, attr, false);
					var hiddenLinkID = "hiddenDownloader";
					var link = document.getElementById(hiddenLinkID);
					if (link === null) {
						link = document.createElement("a");
						link.id = hiddenLinkID;
						link.style.display = "none";
						document.body.appendChild(link);
					}
					link.href = downloadURL;
					link.target = "_blank";
					link.click();
				},
				downloadURL: function(attr){
					var path = "jobs/{0}.csv".format(job_id);
					checkAttributes(attr, allowedAttr["job.download"]);
					var downloadURL = apiURL(path, attr, false);
					return downloadURL;
				},
				upload : (function() {
					var path = "jobs/{0}/upload".format(job_id);
					var contentType = "application/json";
					return {
						json : function(attr, data) {
							checkAttributes(attr, allowedAttr["job.upload.json"]);
							return dataUpload(path, attr, data, contentType, httpMethods.POST);
						},
						feed : function() {
							throw "[upload.feed] NotImplementedException";
						},
						spreadsheet : function() {
							throw "[upload.spreadsheet] NotImplementedException";
						},
					};
				}()),
				read : function() {
					var path = "jobs/{0}".format(job_id);
					return $.ajax(fullURL(path), ajaxSettings({
						type : httpMethods.GET,
					}));
				},
				update : function(attr) {
					var path = "jobs/{0}".format(job_id);
					checkAttributes(attr, allowedAttr["job.update"]);
					requireAttributes(attr);
					// transform for prefix and postfix
					var attrFixed = {};
					for ( var at in attr) {
						attrFixed[jobParamPrefix + at + paramPostfix] = attr[at];
					}
					return $.ajax(fullURL(path), ajaxSettings({
						data : attrFixed,
						type : httpMethods.PUT,
					}));
				},
				remove : function() {
					var path = "jobs/{0}".format(job_id);
					return $.ajax(fullURL(path), ajaxSettings({
						type : httpMethods.DELETE,
					}));
				},
				copy : function(attr) {
					var path = "jobs/{0}/copy".format(job_id);
					checkAttributes(attr, allowedAttr["job.copy"]);
					return $.ajax(fullURL(path), ajaxSettings({
						data : attr,
						type : httpMethods.POST,
					}));
				},
				pause : function() {
					var path = "jobs/{0}/pause".format(job_id);
					return $.ajax(fullURL(path), ajaxSettings({
						type : httpMethods.GET,
					}));
				},
				resume : function() {
					var path = "jobs/{0}/resume".format();
					return $.ajax(fullURL(path), ajaxSettings({
						type : httpMethods.GET,
					}));
				},
				cancel : function() {
					var path = "jobs/{0}/cancel".format(job_id);
					return $.ajax(fullURL(path), ajaxSettings({
						type : httpMethods.GET,
					}));
				},
				ping : function() {
					var path = "jobs/{0}/ping".format(job_id);
					return $.ajax(fullURL(path), ajaxSettings({
						type : httpMethods.GET,
					}));

				},
				status : function() {
					return this.ping();
				},
				legend : function() {
					var path = "jobs/{0}/legend".format(job_id);
					return $.ajax(fullURL(path), ajaxSettings({
						type : httpMethods.GET,
					}));
				},
				gold : function(attr, data) {
					var path = "jobs/{0}/gold".format(job_id);
					checkAttributes(attr, allowedAttr["job.gold"]);
					throw "NotImplementedException";
				},
				channels : (function() {
					var path = "jobs/{0}/channels".format(job_id);
					return {
						view : function() {
							return $.ajax(fullURL(path, null, false), ajaxSettings({
								type : httpMethods.GET,
							}));
						},
						set : function(channels) {
							// check channels data is an array of Strings
							var goodData = true;
							goodData = goodData && $.isArray(channels);
							for ( var i = 0; i < channels.length; i++) {
								goodData = goodData && $.type(channels[i]) === "string";
							}
							if (!goodData) {
								throw "[channels.set] Data must be an array of strings";
							}
							var attr = {
								channels : channels
							};
							return $.ajax(fullURL(path, null, false), ajaxSettings({
								data : attr,
								type : httpMethods.PUT,
							}));
						},
					};
				}()),
				units : {
					create : function(attr) {
						var path = "jobs/{0}/units".format(job_id);
						checkAttributes(attr, allowedAttr["job.units.create"]);
						// transform for prefix and postfix
						var attrFixed = {};
						for ( var at in attr) {
							// special case for data
							if (at === "data") {
								if (!$.isPlainObject(attr[at])) {
									throw "[unit.create] Attribute 'data' needs to be a plain object";
								}
								for ( var key in attr[at]) {
									var formattedKey = unitParamPrefix + at + paramPostfix + "[" + key + "]";
									attrFixed[formattedKey] = attr[at][key];
								}
							} else {
								attrFixed[unitParamPrefix + at + paramPostfix] = attr[at];
							}
						}
						return $.ajax(fullURL(path), ajaxSettings({
							data : attrFixed,
							type : httpMethods.POST,
						}));
					},
					ping : function() {
						var path = "jobs/{0}/units/ping".format(job_id);
						return $.ajax(fullURL(path), ajaxSettings({
							type : httpMethods.GET,
						}));
					},
					status : function() {
						return this.ping();
					},
					read : function() {
						var path = "jobs/{0}/units".format(job_id);
						return $.ajax(fullURL(path), ajaxSettings({
							type : httpMethods.GET,
						}));
					},
					split : function() {
						throw "[units.split] NotImplementedException";
					}

				},
				unit : function(unit_id) {
					if (!unit_id || !$.isNumeric(unit_id)) {
						throw "Please provide a valid unit id";
					}
					return {
						read : function() {
							var path = "jobs/{0}/units/{1}".format(job_id, unit_id);
							return $.ajax(fullURL(path, null, true), ajaxSettings({
								type : httpMethods.GET,
							}));
						},
						update : function(attr) {
							var path = "jobs/{0}/units/{1}".format(job_id, unit_id);
							checkAttributes(attr, allowedAttr["job.unit.update"]);
							// transform for prefix and postfix
							var attrFixed = {};

							for ( var at in attr) {
								// special case for data
								if (at === "data") {
									if (!$.isPlainObject(attr[at])) {
										throw "[unit.create] Attribute 'data' needs to be a plain object";
									}
									for ( var key in attr[at]) {
										var formattedKey = unitParamPrefix + at + paramPostfix + "[" + key + "]";
										attrFixed[formattedKey] = attr[at][key];
									}
								} else {
									attrFixed[unitParamPrefix + at + paramPostfix] = attr[at];
								}
							}
							return $.ajax(fullURL(path), ajaxSettings({
								data : attrFixed,
								type : httpMethods.PUT,
							}));
						},
						destroy : function() {
							var path = "jobs/{0}/units/{1}".format(job_id, unit_id);
							return $.ajax(fullURL(path), ajaxSettings({
								type : httpMethods.DELETE,
							}));
						},
						cancel : function() {
							var path = "jobs/{0}/units/{1}/cancel".format(job_id, unit_id);
							return $.ajax(fullURL(path), ajaxSettings({
								type : httpMethods.POST,
							}));
						}
					};
				},
				judgment : function(judgment_id) {
					if (!judgment_id || !$.isNumeric(judgment_id)) {
						throw "Please provide a valid judgment id";
					}
					return {
						update : function(attr) {
							var path = "jobs/{0}/judgments/{1}".format(job_id, judgment_id);
							checkAttributes(attr, allowedAttr["job.judgment.update"]);
							// transform for prefix and postfix
							var attrFixed = {};
							for ( var at in attr) {
								// special case for data
								if (at === "data") {
									if (!$.isPlainObject(attr[at])) {
										throw "[judgments.update] Attribute 'data' needs to be a plain object";
									}
									for ( var key in attr[at]) {
										var formattedKey = judgementParamPrefix + at + paramPostfix + "[" + key + "]";
										attrFixed[formattedKey] = attr[at][key];
									}
								} else {
									attrFixed[judgementParamPrefix + at + paramPostfix] = attr[at];
								}
							}
							return $.ajax(fullURL(path), ajaxSettings({
								data : attrFixed,
								type : httpMethods.PUT,
							}));
						},
						read : function(attr) {
							var path = "jobs/{0}/judgments/{1}".format(job_id, judgment_id);
							checkAttributes(attr, allowedAttr["job.judgment.read"]);
							return $.ajax(fullURL(path), ajaxSettings({
								data : attr,
								type : httpMethods.GET,
							}));

						},
						remove : function() {
							var path = "jobs/{0}/judgments/{1}".format(job_id, judgment_id);
							return $.ajax(fullURL(path), ajaxSettings({
								type : httpMethods.DELETE,
							}));
						}
					};
				},
				judgments : {
					create : function(attr) {
						var path = "jobs/{0}/judgments".format(job_id);
						checkAttributes(attr, allowedAttr["job.judgments.create"]);
						// transform for prefix and postfix
						var attrFixed = {};
						for ( var at in attr) {
							// special case for data
							if (at === "data") {
								if (!$.isPlainObject(attr[at])) {
									throw "[judgments.create] Attribute 'data' needs to be a plain object";
								}
								for ( var key in attr[at]) {
									var formattedKey = judgementParamPrefix + at + paramPostfix + "[" + key + "]";
									attrFixed[formattedKey] = attr[at][key];
								}
							} else {
								attrFixed[judgementParamPrefix + at + paramPostfix] = attr[at];
							}
						}
						return $.ajax(fullURL(path), ajaxSettings({
							data : attrFixed,
							type : httpMethods.POST,
						}));
					}
				},
				orders : {
					create : function(attr) {
						var path = "jobs/{0}/orders".format(job_id);
						checkAttributes(attr, allowedAttr["job.orders.create"]);
						return $.ajax(fullURL(path, null, false), ajaxSettings({
							data : attr,
							type : httpMethods.POST,
						}));
					}
				},
				order : function(order_id) {
					if (!order_id || !$.isNumeric(order_id)) {
						throw "Please provide a valid order id";
					}
					return {
						read : function() {
							var path = "jobs/{0}/orders/{1}".format(job_id, order_id);
							return $.ajax(fullURL(path), ajaxSettings({
								type : httpMethods.GET,
							}));
						},
					};
				},
			};
		},
		jobs : {
			upload : (function() {
				var path = "jobs/upload";
				var contentType = "application/json";
				return {
					json : function(attr, data) {
						checkAttributes(attr, allowedAttr["jobs.upload.json"]);
						return dataUpload(path, attr, data, contentType, httpMethods.POST);
					},
					feed : function() {
						throw "[upload.feed] NotImplementedException";
					},
					spreadsheet : function() {
						throw "[upload.spreadsheet] NotImplementedException";
					},
				};
			}()),
			create : function(attr) {
				var path = "jobs";
				checkAttributes(attr, allowedAttr["jobs.create"]);
				requireAttributes(attr);
				// transform for prefix and postfix
				var attrFixed = {};
				for ( var at in attr) {
					attrFixed[jobParamPrefix + at + paramPostfix] = attr[at];
				}
				return $.ajax(fullURL(path), ajaxSettings({
					data : attrFixed,
					type : httpMethods.POST,
				}));
			},
			read : function() {
				var path = "jobs";
				return $.ajax(fullURL(path), ajaxSettings({
					type : httpMethods.GET,
				}));
			}
		},
	};
}(jQuery));