var n = "#{gameSession.getGameRounds().size()}";
document.write("<table>");
for(var i = 1; i <= n; i++){
	document.write("<tr><td>Runde: " + i);
	writeProcessingCanvas(n);
	document.write("</td></tr>");
}
document.write("</table>");

function writeProcessingCanvas(id){
	var string = "<canvas id=\"" + id + "\" data-processing-sources=\"terminaGame/TerminaResultGraph.pde\"> </canvas> <br />";
	document.write(string);
}		

function setTerm(id, term){
	var pjs = Processing.getInstanceById('id');
	pjs.setTerm(term);

}

function addLoadEvent(func) {
	var oldonload = window.onload;
	if (typeof window.onload != 'function') {
		window.onload = func;
	} else {
		window.onload = function() {
			oldonload();
			func();
		};
	}
}


