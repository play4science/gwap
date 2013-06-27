
function newEntry(id, association, score, matchType){
	var pjs = Processing.getInstanceById(id);
	var size = 20;
	
	pjs.addTag(setLineBreaks(association), size, matchType, association);
}

function setLineBreaks(tag){
	tag = tag.trim();
	tag = tag.replace(/ +(?= )/g,''); //replace multiple whitespaces 
	tag = tag.replace(/ /g,"\n"); //replace whitespaces with linebreak
	return tag;
}