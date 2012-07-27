var menuWidth = 443; // width of the menu column
var windowSpaceRight = 63; // space for borders etc.
var windowSpaceTop = 180; // space above resource
var minDisplayWidth = 300;
var minDisplayHeight = 300;
var windowWidth=630;
var windowHeight=460;
var maxDisplayHeight;

function bodyLoad() {
	//updateServerWindowSize(window.innerHeight, window.innerWidth);
	var resource = jQuery('#resource');
	if (resource) {
		jQuery("<img/>") // Make in memory copy of image to avoid css issues
			.attr("src", resource.attr("src"))
			.load(function() {
				realImgWidth = this.width;   // Note: $(this).width() will not
				realImgHeight = this.height; // work for in memory images.
				updateClientWindowSize();
			});
	}
	updateClientWindowSize();
};

function bodyResize() {
	updateClientWindowSize();
	// updateServerWindowSize(window.innerHeight, window.innerWidth);
};

function getWindowSize() {
	if (window.innerWidth && window.innerHeight) {
		 windowWidth = window.innerWidth;
		 windowHeight = window.innerHeight;		 
	}	
	else if (document.compatMode=='CSS1Compat' &&
		    document.documentElement &&
		    document.documentElement.offsetWidth ) {
		 windowWidth = document.documentElement.offsetWidth;
		 windowHeight = document.documentElement.offsetHeight;
	}
	else if (document.body && document.body.offsetWidth) {
	 windowWidth = document.body.offsetWidth;
	 windowHeight = document.body.offsetHeight;
	}

}


//Store the actual size of the current displayed resource
var realImgWidth, realImgHeight;

function updateClientWindowSize() {
	getWindowSize();
	//console.log("window size:"+windowWidth+"x"+windowHeight);
	// set body width to current browser window width
	var body = document.getElementsByTagName('body')[0];
	body.style.width = (windowWidth - windowSpaceRight) + "px";

	var maxDisplayWidth = windowWidth - windowSpaceRight - menuWidth;
	if (maxDisplayWidth<0)
		maxDisplayWidth=0;
		
	var canvas = $('gridcanvas');
	
	//If we are using a Karido Grid, use the entire available space, minus a border
	if (canvas) {
		windowSpaceTop=canvas.cumulativeOffset().top+80;
		maxDisplayHeight = Math.min(windowHeight - windowSpaceTop, maxDisplayWidth);
	}
	else
		maxDisplayHeight = windowHeight - windowSpaceTop;
	
	if (maxDisplayHeight<0)
		maxDisplayHeight=0;

	var displayRelation = maxDisplayWidth / maxDisplayHeight;
	//console.log("maxDisplay: " + maxDisplayWidth + "x" + maxDisplayHeight + " - " + displayRelation);

	var padding = 11;

	// set resource width to remaining width
	var resource = jQuery('#resource');
	if (resource && realImgWidth && realImgHeight) {
		var imageDefaultWidth = realImgWidth;
		var imageDefaultHeight = realImgHeight;
		var imageRelation = imageDefaultWidth / imageDefaultHeight;
		//console.log("imageDefault: " + imageDefaultWidth + " - " + imageDefaultHeight + " - " + imageRelation);

		var imageScalingFactorWidth = Math.max(maxDisplayWidth
				/ imageDefaultWidth, minDisplayWidth / imageDefaultWidth);
		var imageScalingFactorHeight = Math.max(maxDisplayHeight
				/ imageDefaultHeight, minDisplayHeight / imageDefaultHeight);
		var imageScalingFactor = Math.min(imageScalingFactorWidth,
				imageScalingFactorHeight);		 		
		//console.log("imageScalingFactor: " + imageScalingFactor);

		var imageWidth = imageDefaultWidth * imageScalingFactor;
		var imageHeight = imageDefaultHeight * imageScalingFactor;
		//console.log("image: " + imageWidth + " - " + imageHeight);

		resource.width(imageWidth);
		resource.height(imageHeight);
	}
	;

	contentWidth = maxDisplayWidth + 2 * padding;

	var content = document.getElementById('content');
	if (content) {
		contentWidth = maxDisplayWidth + 2 * padding;
		content.style.width = contentWidth + "px";
	}

	if (canvas) {
		var size = maxDisplayHeight;
		canvas.style.width = size + "px";
		canvas.style.height = size + "px";

		updateGridImages();
		// updateGridImages(size);
	}

};

function addHandler(image) {
	if ($(image).up('.focussed_guesser') == undefined) {
		var parent = $(image).up('.gridcell');

		parent.onmouseover = function() {
			imageGridOver(image);
		};
		parent.onmouseout = function() {
			imageGridOut(image);
		};
	}
}

function setImageSize(image, scale) {
	//if (image.realWidth == undefined || image.realWidth == 0) {
	//	image.realWidth = image.width;
	//	image.realHeight = image.height;
	//}

	var containerSize = maxDisplayHeight / 3;

	var imageDefaultWidth = image.width;
	var imageDefaultHeight = image.height;

	var styles = '';

	// Only center the image:
	var styles = 'top: ' + (containerSize - imageDefaultHeight) / 2 + 'px;'
			+ 'left: ' + (containerSize - imageDefaultWidth) / 2 + 'px;'
			+ 'display: block;';

	image.setStyle(styles);
	return;

	var rWidth, rHeight;
	if (imageDefaultWidth > imageDefaultHeight) {
		rWidth = imageDefaultWidth * containerSize / imageDefaultHeight * scale;
		rHeight = containerSize * scale;

		styles += 'height: ' + (scale * 100) + '%; width: auto;';

	} else {
		rWidth = containerSize * scale;
		rHeight = imageDefaultHeight * containerSize / imageDefaultWidth
				* scale;

		styles += 'width: ' + (scale * 100) + '%; height: auto;';
	}

	rHeight = imageDefaultHeight;
	rWidth = imageDefaultWidth;

	styles += 'top: ' + (containerSize - rHeight) / 2 + 'px;' + 'left: '
			+ (containerSize - rWidth) / 2 + 'px;' + 'display: block;';

	image.setStyle(styles);
}

function updateGridImage(image) {
	if (image.width==0)
	{
		//If image is incomplete (see Chrome and AdBlock), check back later
		setTimeout(function() {			
			updateGridImage(image);
			//Fix for IE Garbage Collection
			image=null;
			}, 100);
		return;
	}
	
	setImageSize(image, 1.0);	
}

function updateGridImages(screen, hidden) {
	if (maxDisplayHeight == undefined)
		updateClientWindowSize();

	var imageSize=Math.ceil(maxDisplayHeight/3/100)*100;

	

	if (typeof (screen) != 'undefined') {
		// Update all hidden images
		$(hidden).select(".gridimage").each(function(img) {			
			img.src=img.alt+'?size='+imageSize;
			//addHandler(img);
			updateGridImage(img);							
		});		
		
		// Show hidden images
		$(screen).update($(hidden).innerHTML);
	
		// Update all visible images after they are loaded completely
		$(screen).select(".gridimage").each(function(img) {			
			if (!img.complete) {								
				//Again, Internet Explorer workaround
				//img.observe('load',				
				setTimeout(function() {					
					updateGridImage(img);
					//Fix for IE Garbage Collection
					img=null;
					}, 100);				
			};
				
		});


		$(hidden).remove();
	}
		
	// Update visible images (in case of browser resize)	
	$$(".memorygridscreen .gridimage").each(function(img) {
		img.src=img.alt+'?size='+imageSize;
		
		updateGridImage(img);				
		
		img.observe('load', function(event) {				
			updateGridImage(event.element()); });		
		}
	);

	// alert(img.src);
	/*
	 * if (!img.complete) { img.hide(); img.observe('load', function(event){
	 * event.element().show(); }); }
	 */

}

function imageGridOver(image) {
	owner = image.up('.gridcell');
	owner.style.overflow = 'visible';
	image.setStyle({
		zIndex : '1000'
	});
	setImageSize(image, 1.5);
}

function imageGridOut(image) {
	owner = image.up('.gridcell');
	owner.style.overflow = 'hidden';
	image.setStyle({
		zIndex : 'auto'
	});
	setImageSize(image, 1.0);
}

