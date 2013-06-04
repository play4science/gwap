function newUserTag(round, tag,score, appearence){
	tag = tag.trim();
	tag = tag.replace(/ +(?= )/g,''); //replace multiple whitespaces 
	tag = tag.replace(/ /g,"\n"); //replace whitespaces with linebreak
	console.log("new user tag. round: " + round + ", tag: " + tag + ", score: " + score);
	userTags[round - 1].push(tag);
	scores[round - 1].push(score);
	userTagAppearences[round - 1].push(parseInt(appearence));
}

function newProcessingInstance(id){
	var waiting = setInterval(function(){
		var pjs = Processing.getInstanceById(id);
		if(typeof pjs != 'undefined'){
			clearInterval(waiting);
			//add Instance
			processingInstances.push(pjs);
			pjs.setResultGraph();
			console.log("number of processing instances: " + processingInstances.length);
			if(processingInstances.length == numberOfRounds)
				setUpGraphs();
		}			
	},10);
}

function newTerm(term){
	console.log("new term: " + term);
	terms.push(term);
}

function newForeignTag(round, tag, appearence, matchtype){
	tag = tag.trim();
	tag = tag.replace(/ +(?= )/g,''); //replace multiple whitespaces 
	tag = tag.replace(/ /g,"\n"); //replace whitespaces with linebreak
	foreignTags[round - 1].push(tag);
	foreignAppearences[round - 1].push(appearence);
	foreignMatchTypes[round -1].push(matchtype);
}

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
			pjs.addOwnTag(userTags[i][j], 100, getScale(i, userTagAppearences[i][j]), type);
		}
		console.log("user tags added, foreign tags");
		for(var j = 0; j < foreignTags[i].length; j++){
			var ft = foreignTags[i][j];
			if(! containsIgnoreCase(userTags[i], ft))
				pjs.addForeignTag(ft, 100, getScale(i, foreignAppearences[i][j]), foreignMatchTypes[i][j]);
		}
		console.log("foreign tags added, separating.");
		pjs.separateTags();
		console.log("separate done");
		pjs.highlightOwnTags();
		console.log("highlight done");
	}
}

function score2matchType(score){
	type = "";
	if(score == ""){
		type = "indirectMatch";
	} else if(parseInt(score) > 0){
		type = "directMatch";
	}
	return type;
}

function getScale(round, appearence){
	var max = Math.max.apply(Math, foreignAppearences[round]);
	var min = Math.min.apply(Math, foreignAppearences[round]);
	var scale = 1;
	if(appearence > min){
		scale = 15 * (appearence - min)/(max - min);
	}
	return 10 + scale;
}

function containsIgnoreCase(arr, tag){
	var b = false;
	var u = tag.toUpperCase();
	for(var i = 0; i < arr.length; i++){
		var a = arr[i].toUpperCase();
		b = b || a == u;
	}
	return b; 
}

function setLineBreaks(tag){
	tag = tag.trim();
	tag = tag.replace(/ +(?= )/g,''); //replace multiple whitespaces 
	tag = tag.replace(/ /g,"\n"); //replace whitespaces with linebreak
	return tag;
}