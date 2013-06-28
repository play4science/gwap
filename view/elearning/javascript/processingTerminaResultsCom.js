/**
 * Stores the given parameters in the according lists.
 */
function newUserTag(round, tag,score, appearence){
	userTags[round - 1].push(tag);
	scores[round - 1].push(score);
	userTagAppearences[round - 1].push(parseInt(appearence));
}

/**
 * Stores id in the according list. 
 * If the number of stored ids equals the number of played rounds, setUpGraphs() is called.
 * @param id
 */
function newProcessingInstance(id){
	var waiting = setInterval(function(){
		var pjs = Processing.getInstanceById(id);
		if(typeof pjs != 'undefined'){
			clearInterval(waiting);
			//add Instance
			processingInstances.push(pjs);
			console.log("number of processing instances: " + processingInstances.length);
			if(processingInstances.length == numberOfRounds)
				setUpGraphs();
		}			
	},10);
}

/**
 * Stores a term in the according list. 
 */
function newTerm(term){
	console.log("new term: " + term);
	terms.push(term);
}

/**
 * Stores the given parameters in the according lists.
 */
function newForeignTag(round, tag, appearence, matchtype){
	foreignTags[round - 1].push(tag);
	foreignAppearences[round - 1].push(appearence);
	foreignMatchTypes[round -1].push(matchtype);
}

/**
 * Is called when the number of stored processing ids reaches the number of played rounds. 
 * Each processing instance is then referenced by its id, and the stored data is passed to it.
 */
function setUpGraphs(){
	console.log("setUpGraphs called");
	for(var i = 0; i < numberOfRounds; i++){
		var pjs = processingInstances[i];
		term = terms[i];
		console.log("term in round " + i + " : " + term  );

		pjs.setTerm(setLineBreaks(term));
		console.log("term set. adding user tags");
		for(var j = 0; j < userTags[i].length; j++){
			var type = score2matchType(scores[i][j]);
			var max = Math.max.apply(Math, foreignAppearences[i]);
			var min = Math.min.apply(Math, foreignAppearences[i]);
			var tag = userTags[i][j];
			pjs.addOwnTag(setLineBreaks(tag), getScale(max,min, userTagAppearences[i][j]), type, false, tag);
		}
		console.log("user tags added, foreign tags");
		for(var j = 0; j < foreignTags[i].length; j++){
			var ft = foreignTags[i][j];
			if(! containsIgnoreCase(userTags[i], ft))
				pjs.addForeignTag(setLineBreaks(ft), getScale(i, foreignAppearences[i][j]), foreignMatchTypes[i][j], false, ft);
		}
		console.log("foreign tags added, separating.");
		pjs.separateTags();
		console.log("separate done");
		pjs.highlightOwnTags();
		console.log("highlight done");
	}
}

/**
 * maps scores (+1,0,-1) to Strings („directMatch“, „indirectMatch“,“WRONG“).
 * @param score the obtained score of this tag. either +1 -1 or 0.
 * @returns the according matchType to the score
 */
function score2matchType(score){
	type = "";
	if(score == ""){
		type = "indirectMatch";
	} else if(parseInt(score) > 0){
		type = "directMatch";
	}
	return type;
}

/**
 * Maps the appearance of a certain tag to a text size. Appearances are mapped linearly to the interval [10,25]. 
 * @param max the maximum appearance of any tag played in this round
 * @param min the minimum appearance of any tag played in this round
 * @param appearence the appearance of this tag.
 * @returns {Number} the scaled text size
 */
function getScale(max,min, appearence){
	var scale = 1;
	if(appearence > min){
		scale = 15 * (appearence - min)/(max - min);
	}
	return 10 + scale;
}

/**
 * @return if the array arr contains a string tag by ignoring the cases of the string.
 */
function containsIgnoreCase(arr, tag){
	var b = false;
	var u = tag.toUpperCase();
	for(var i = 0; i < arr.length; i++){
		var a = arr[i].toUpperCase();
		b = b || a == u;
	}
	return b; 
}

/**
 * Replaces sets linebreaks in strings between words.
 * @param tag the original tag
 * @returns the tag with linebreaks
 */
function setLineBreaks(tag){
	tag = tag.trim();
	tag = tag.replace(/ +(?= )/g,''); //replace multiple whitespaces 
	tag = tag.replace(/ /g,"\n"); //replace whitespaces with linebreak
	return tag;
}