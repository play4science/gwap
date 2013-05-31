TerminaGraph tg;
color BACKGROUND;  
color STROKE;
color BLUE;
color YELLOW;
color RED;
color TEXT;
color GREY;

color CORRECT;
color WRONG;
color UNKNOWN;


void setup() {
	BACKGROUND = color(255);  
	STROKE = color(0);
	BLUE = color(14, 30, 191);
	YELLOW = color(255, 216, 59);
	RED = color(255, 55, 55);
	TEXT = color(0);
	GREY = color(220);

	CORRECT = #00FF00;
	WRONG = #FF0000;
	UNKNOWN = #CC8811;

	Processing.logger = console;

	this.width = 400;
	this.height = 300;

	size(width, height);

	tg = new TerminaGraph();
	println("this.tg " + tg);
	frameRate(15);
}

void draw() {
	background(BACKGROUND);
	tg.collide();
	if(tg.type == "ResultGraph"){
		ResultGraph rg = (ResultGraph)tg;
		int n = 0;
		if(rg.correctTags.size() > 0)
			n++;
		if(rg.wrongTags.size() > 0)
			n++;
		if(rg.unknownTags.size() > 0)
			n++;
		if( n >= 2 ){
			rg.updateVertexDistances();
		}
	}
	for(Vertex v : tg.vertices)
		v.move();

	if(tg.type == "ResultGraph"){
		ResultGraph rg = (ResultGraph)tg;
		if (rg.high && rg.ownTags.size() > 0){
			rg.updateArc();
			rg.arcDeTriomphe.display();    
		}
	}
	fill(STROKE);
	stroke(STROKE);

	for (Vertex v : tg.vertices) {
		line(tg.cx, tg.cy, v.x, v.y);
		v.display();
	}
	tg.centerVertex.display();
}

void setTerm(String term){
	tg.setTerm(term);
}

void setDisplayWidth(int w){
	tg.setDisplayWidth(w);
}

void addTag(String s, float distance, int size, String matchType){
	tg.addTag(s,distance,size,matchType);
}

void addOwnTag(String s, float distance, int size, String matchType){
	ResultGraph rg = (ResultGraph)tg;
	rg.addOwnTag(s,distance,size,matchType);
}

void addForeignTag(String s, float distance, int size, String matchType){
	ResultGraph rg = (ResultGraph)tg;
	rg.addForeignTag(s,distance,size,matchType);
}

void separateTags(){
	ResultGraph rg = (ResultGraph)tg;
	rg.separateTags();
}

void highlightOwnTags(){
	ResultGraph rg = (ResultGraph)tg;
	rg.highlightOwnTags();
}

void setResultGraph(){
	tg = new ResultGraph();
}