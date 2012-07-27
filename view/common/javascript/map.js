var gmap;
var map = new Map();

function Map() {
	this.markers = [];
	this.markerImage = new google.maps.MarkerImage('img/marker_blue.png',
			new google.maps.Size(20, 20), new google.maps.Point(0, 0), new google.maps.Point(10, 10));
	this.markerImageHover = new google.maps.MarkerImage('img/marker_hover.png',
			new google.maps.Size(20, 20), new google.maps.Point(0, 0), new google.maps.Point(10, 10));
	this.markerImageSelected = new google.maps.MarkerImage('img/marker_hover.png',
			new google.maps.Size(20, 20), new google.maps.Point(0, 0), new google.maps.Point(10, 10));
//	this.shadowImage = new google.maps.MarkerImage('img/shadow_Marker.png', 
//			new google.maps.Size(24, 17), new google.maps.Point(0, 0), new google.maps.Point(2, 17));
	this.shadowImage = null;
	this.selectedHistory = [];
	this.selected;
	this.breadcrumbLocations = '';
	this.percentageView = false; //if false the map is not clickable but you can use e.g.mouse over 
	this.enabled = true;
	this.rootLocationId;
	this.hierarchyName = 'mit';
	this.extendedName = false;
	this.highlightNeighbors = true;
	this.mapIsReset = false;
	this.baseURL = ""; // must be set

	this.gmapOptions = {
		zoom : 5,
		center : new google.maps.LatLng(42.5, 12.0),
		mapTypeId : google.maps.MapTypeId.TERRAIN,
		mapTypeControl : false,
		navigationControlOptions : {
			style : google.maps.NavigationControlStyle.SMALL
		},
		streetViewControl : false
	};

	/** overwrite these methods with your own code, 
	 * e.g. map.mouseOver = function(object, event) { $('title').innerHTML = object.title; } **/
	this.mouseOver = function(object, event) { };
	this.mouseOut = function(object, event) { };
	this.mouseClick = function (object, event) { };


	this.initializeMap = function() {
		gmap = new google.maps.Map(document.getElementById("gmap"), map.gmapOptions);
		map.displayMarkers();
	};
	
	this.getBaseURL = function() {
		if (this.extendedName)
			return this.baseURL + "seam/resource/rest/geodata/markers-extended-title/";
		else
			return this.baseURL + "seam/resource/rest/geodata/markers/";
	};

	this.mouseClickInternal = function(event) {
		if (!map.enabled || map.percentageView)
			return;
		map.mapIsReset = false;
		if (map.selected && map.selected instanceof google.maps.Marker) {
			 map.selected.setIcon(map.markerImage);
			 if (map.breadcrumbLocations && $(map.breadcrumbLocations))
				 $(map.breadcrumbLocations).removeChild($(map.breadcrumbLocations).lastChild);
		} else if (map.selected) {
			map.selected.setMap(null);
			map.selectedHistory.push(map.selected);
		}
		if (this) {
			var isMarker = this instanceof google.maps.Marker;
			map.setSelected(this, isMarker);
			if (!isMarker)
				map.displayMarkers(this.id);
		}
		
		map.updateLocationIdField();
		
		map.mouseClick(this, event);
	};
	
	this.setSelected = function(newSelectedObject, keepOldSelectedOnMap) {
		if (map.selected && !keepOldSelectedOnMap)
			map.selected.setMap(null);
		map.selected = newSelectedObject;
		if (map.selected instanceof google.maps.Marker) {
			 map.selected.setIcon(map.markerImageSelected);
		} else {
			map.selected.setOptions({
				clickable : false,
				fillColor : "#81BEF7" , 
				fillOpacity:0.5,
//				strokeColor : "#0000FF",
//				strokeOpacity : 1,
//				strokeWeight: 4,
				zIndex : -1
			});
			map.selected.setMap(gmap);
		}
		
		if (map.breadcrumbLocations && $(map.breadcrumbLocations)) {
			if ($(map.breadcrumbLocations).lastChild)
				$(map.breadcrumbLocations).removeChild($(map.breadcrumbLocations).lastChild);
			var link = document.createElement("a");
			link.setAttribute("onclick", "map.displayParentMarkers("+map.selected.id+")");
			link.appendChild(document.createTextNode(map.selected.title));
			var div = document.createElement("div");
			div.setAttribute("id", "bl_"+map.selected.id);
			div.appendChild(document.createTextNode(" > "));
			div.appendChild(link);
			$(map.breadcrumbLocations).appendChild(div);
			new Effect.Highlight(div);
		}
	};

	this.mouseOverInternal = function(event) {
		if (this instanceof google.maps.Marker && this != map.selected)
			this.setIcon(map.markerImageHover);
		if (!map.enabled)
			return;
		if (map.highlightNeighbors && !map.percentageView){
			this.setOptions({fillColor:"#2E64FE", fillOpacity:0.5});
			if (map.neighbors) {
				for(var i = 0; i < this.neighbors.length; i++){
					for(var j = 0; j < map.markers.length; j++){
						if(map.markers[j].id == this.neighbors[i]){
							map.markers[j].setOptions({fillColor:"#81BEF7", fillOpacity:0.5});
						}
					}
				}
			}
		}
		if (map.mouseOver)
			map.mouseOver(this, event);
	};

	this.mouseOutInternal = function(event) {
		if (this instanceof google.maps.Marker && this != map.selected)
			this.setIcon(map.markerImage);
		if (!map.enabled)
			return;
		if (map.mouseOut)
			map.mouseOut(this, event);
		if (map.highlightNeighbors && !map.percentageView){
			for(var i = 0; i < map.markers.length; i++){
				map.markers[i].setOptions({fillOpacity:0});
			}
		}
	};
	
	this.updateLocationIdField = function() {
		value = null;
		if (map.selected)
			value = map.selected.id;
		if ($('locationAssignmentForm:locationId'))
			$('locationAssignmentForm:locationId').value = value;
		else if ($('form:locationId'))
			$('form:locationId').value = value;
		else if ($('locationId'))
			$('locationId').value = value;
	};
	
	this.displayMarkers = function(parentId) {
		this.mapIsReset = false;
		if (!parentId) {
			if (map.rootLocationId)
				parentId = map.rootLocationId;
			else if (map.hierarchyName) {
				jQuery.ajax({
					url      : map.getBaseURL() + map.hierarchyName, 
					dataType : "json", 
					success  : map.displayMarkersCallback,
				});
				return;
			}
		}
		jQuery.ajax({
					url      : map.getBaseURL() + map.hierarchyName + "/" + parentId, 
					dataType : "json", 
					success  : map.displayMarkersCallback,
				});
	};
	
	this.displayPercentages = function(statementId) {
		jQuery.ajax({
					url      : "seam/resource/rest/geodata/percentages" + "/" + statementId, 
					dataType : "json", 
					success  : map.displayMarkersCallback,
				});
		
	};
	
	this.displayPercentagesForAdmin = function(statementId) {
		jQuery.ajax({
					url      : "../seam/resource/rest/geodata/percentages" + "/" + statementId, 
					dataType : "json", 
					success  : map.displayMarkersCallback,
				});
		
	};
	

	
	this.displayPercentagesByBet = function(betId) {
		jQuery.ajax({
			url      : "seam/resource/rest/geodata/percentagesbybet" + "/" + betId, 
			dataType : "json", 
			success  : map.displayMarkersCallback,
		});
	};
	
	this.resetSelection = function() {
		this.resize();
		if (!this.mapIsReset) {
			if (this.selected)
				this.selected.setMap(null);
			this.selected = null;
			this.selectedHistory = [];
			if (map.breadcrumbLocations && $(map.breadcrumbLocations))
				$(map.breadcrumbLocations).innerHTML = '';
			this.displayMarkers(this.rootLocationId);
			this.mapIsReset = true;
		}
	};

	this.displayParentMarkers = function(parentId) {
		this.mapIsReset = false;
		// determine parentId
		if (!parentId) {
			if (this.selectedHistory.length > 0)
				parentId = this.selectedHistory.last().id;
			else
				parentId = this.rootLocationId;
		}
		if (parentId) {
			if (this.selected) {
				this.selected.setMap(null);
				this.selected = null;
			}
			for (var i = this.selectedHistory.length-1; i >= 0; i--) {
				this.selected = this.selectedHistory.pop();
				if (this.selected.id == parentId) {
					this.selected.setMap(gmap);
					break;
				} else {
					this.selected.setMap(null);
				}
			}
			if (this.breadcrumbLocations && $(this.breadcrumbLocations)) {
				var locs = $(this.breadcrumbLocations).childNodes;
				for (var i = locs.length-1; i >= 0; i--) {
					if (locs[i].nodeType == 1 && parentId == locs[i].getAttribute("id").substr(3))
						break;
					$(this.breadcrumbLocations).removeChild(locs[i]);
				}
			}
			this.displayMarkers(parentId);
		};
		this.updateLocationIdField();
	};
	
	this.displayMarkersCallback = function(data) {
		//TODO
		
		var markers = data["markers"];
		// Delete old this.markers
		if (map.markers)
			for ( var i = 0; i < map.markers.length; i++) {
				if (map.markers[i])
					map.markers[i].setMap(null);
			}
		map.markers = [];
		// Add new map.markers
		for (var i = 0; i < markers.length; i++) {
			var marker = markers[i];
			map.markers.push(marker);
			google.maps.event.addListener(marker, 'click', map.mouseClickInternal);
			google.maps.event.addListener(marker, 'mouseover', map.mouseOverInternal);
			google.maps.event.addListener(marker, 'mouseout', map.mouseOutInternal);
		}

		if (data["parentMarker"]) {
			map.setSelected(data["parentMarker"]);
			
		}
		if (markers.length > 0)
			gmap.fitBounds(data["bounds"]);
	};
	
	this.getSelectedTitle = function() {
		if (this.selected != null) {
			return this.selected.title;
		} else
			return null;
	};
	
	this.resize = function() {
		google.maps.event.trigger(gmap, 'resize');
	};
}