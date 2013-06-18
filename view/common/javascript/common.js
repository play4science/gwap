/**
 * Common JavaScript components
 */

/* ******************************************************************************** */
/* *** Countdown *** */

function Countdown() {
	this.duration = 0;
	this.seconds = 0;
	this.callbackStep = function() {
		document.getElementById('countdown').innerHTML = this.seconds;
	};
	this.callbackStop = function() {
	};
}
Countdown.prototype.start = function(duration, callbackStep, callbackStop) {
	this.duration = duration;
	this.seconds = duration;
	if (callbackStep != undefined)
		this.callbackStep = callbackStep;
	if (callbackStop != undefined)
		this.callbackStop = callbackStop;

	if (this.active == undefined)
		this.active = window.setInterval("countdown.countdown()", 1000);
	this.callbackStep();
};

Countdown.prototype.stopOnly = function() {
	window.clearInterval(this.active);
	this.active = undefined;
};
Countdown.prototype.stop = function() {
	this.stopOnly();
	this.callbackStop();
};
Countdown.prototype.countdown = function() {
	this.seconds--;
	this.callbackStep();
	if (this.seconds <= 0)
		this.stop();
};

if (countdown != undefined) {
	countdown.stopOnly();
}
var countdown = new Countdown();

function setMaxWidthPossible(element, substract, maxwidth) {
	var width = jQuery(document).width() - substract;
	if (maxwidth && (width > maxwidth))
		width = maxwidth;
	element.style.width = width+"px";
}

/* ******************************************************************************** */
/* *** Effects extensions */

Effect.DropUp = function(element) {
	element = $(element);
	var oldStyle = {
		top : element.getStyle('top'),
		left : element.getStyle('left'),
		opacity : element.getInlineOpacity()
	};
	return new Effect.Parallel([ new Effect.Move(element, {
		x : 0,
		y : -100,
		sync : true
	}), new Effect.Opacity(element, {
		sync : true,
		to : 0.0
	}) ], Object.extend({
		duration : 0.5,
		beforeSetup : function(effect) {
			effect.effects[0].element.makePositioned();
		},
		afterFinishInternal : function(effect) {
			effect.effects[0].element.hide().undoPositioned()
					.setStyle(oldStyle);
		}
	}, arguments[1] || {}));
};

Effect.DropUpBenchmark = function(element) {
	element = $(element);
	var count = 0;
	var oldStyle = {
		top : element.getStyle('top'),
		left : element.getStyle('left'),
		opacity : element.getInlineOpacity()
	};
	return new Effect.Parallel([ new Effect.Move(element, {
		x : 0,
		y : 200,
		sync : true
	}), new Effect.Opacity(element, {
		sync : true,
		to : 0.0
	}) ], Object.extend({
		duration : 2.5,
		beforeSetup : function(effect) {
			effect.effects[0].element.makePositioned();
		},
		afterUpdate : function(effect) {
			count = count + 1;
		},
		afterFinishInternal : function(effect) {
			effect.effects[0].element.hide().undoPositioned()
					.setStyle(oldStyle);
			element.setStyle(oldStyle);
			element.show();
			element.update(count);
		}
	}, arguments[1] || {}));
};

function setAjaxLoader(nodeId) {
	var node = document.getElementById(nodeId);
	node.style.backgroundImage = "url('img/ajax-loader.gif')";
	node.style.backgroundRepeat = 'no-repeat';
	node.style.backgroundPosition = 'right';
};

function unsetAjaxLoader(nodeId) {
	var node = document.getElementById(nodeId);
	node.style.backgroundImage = "none";
};

var previouslyActiveItem = 0;
function setItemActive(active) {
	jQuery('#' + previouslyActiveItem).css('background-color','transparent');
	jQuery('#' + active).css('background-color','#B8C9E1');
	previouslyActiveItem = active;
}