/**
 * Sends a query to the seam background to get the played tags to a specific term. Variables from the selectionForm in pastSessionGraphs are used.
 * If succesfull, the dataCallback function is called.
 */
function dataQuery(){
	//initializeVariables();
	jQuery.ajax({
		url      : "seam/resource/rest/pastTerminaSession/userResults", 
		data	 : {'term': currentTerm, 
			'owr': document.getElementById("selectionForm:chk1").checked, 
			'fwr': document.getElementById("selectionForm:chk2").checked,
			'mfn': document.getElementsByName("selectionForm:mfnSpinner")[0].value,
			'mon': document.getElementsByName("selectionForm:monSpinner")[0].value},
			dataType : "json", 
			success  : dataCallback,
	});
}

/**
 * Stores the information in "data" in according variables and calls displayData() and expandTree() afterwards.
 * @param data
 */
function dataCallback(data){
	//data zerlegen
	term = data["term"];
	maxApp = data["maxApp"];
	minApp = data["minApp"];
	foreignTags = data["foreigns"];
	ownTags = data["owns"];
	topics = data["topics"];

	//display
	expandTreeAt(topics,term);
	displayData();
}

/**
 * Expands the termSelectionTree of pastSessionGraphs.xhtml at the given topics and highlights the last term thats equal to term.
 * Topic components of the termSelectionTree have ids like: 'termSelectionTree:Kapitel 4::topic'
 * Term components of the termSelectionTree have ids like: 'termSelectionTree:Kapitel 5:statische Typprüfung::term'
 * @param topics the topics to be expanded.
 * @param term the term to be highlighted.
 */
function expandTreeAt(topics, term){
	if(topics.indexOf(currentTopic) == -1){
		for(var i = 0; i < topics.length; i++){
			var topicId = "termSelectionTree:" + topics[i] + "::topic";
			document.getElementById(topicId).component.expand();
			var termId = "termSelectionTree:" + topics[i] + ":" + term + "::term"; 
			document.getElementById(termId).component.toggleSelection();
		}
	} 
}

/**
 * Passes the data that is stored in the according variables to the processing sketch with id "TerminaGraph".
 */
function displayData(){
	pjs = Processing.getInstanceById("TerminaGraph");
	pjs.reset();
	pjs.setTerm(setLineBreaks(term));

	for(var i = 0; i < foreignTags.length; i++){
		var tag = foreignTags[i];
		pjs.addForeignTag(setLineBreaks(tag["tag"]), getScale(maxApp, minApp, tag["appearence"]), tag["matchType"], tag["isPlayedTerm"], tag["tag"]);
	}

	for(var i = 0; i < ownTags.length; i++){
		var tag = ownTags[i];
		pjs.addOwnTag(setLineBreaks(tag["tag"]), getScale(maxApp, minApp, tag["appearence"]), tag["matchType"], tag["isPlayedTerm"], tag["tag"]);
	}

	pjs.mix();
	pjs.separateTags();
	pjs.highlightOwnTags();
}

/**
 * sets currentTerm to val.
 */
function selectTerm( val ){
	currentTerm = val;
}

/**
 * sets currentTopic. Is called when a term in the termSelectionTree is clicked.
 * @param e the element that is selected.
 */
function setCurrentTopic(e){
	elt = Event.element(e);
	topicId = Tree.Item.findComponent(elt).id;
	currentTopic = topicId.split(":")[1];
}

/**
 * when an ActionVertex in the TerminaGraph is clicked this function is called. 
 * @param id the id of the clicked actionVertex.
 */
function clickEvent(id){
	currentTerm = id;
	dataQuery();
}