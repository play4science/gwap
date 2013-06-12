ResultGraph tg;
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
boolean moving;
PImage frame;
boolean frameSet;

void setup() {
	BACKGROUND = color(255);  
	STROKE = color(0);
	BLUE = color(14, 30, 191);
	YELLOW = color(255, 216, 59);
	RED = color(255, 55, 55);
	TEXT = color(0);
	GREY = color(220);

	CORRECT = #00FF00;
	WRONG = #FF6666;
	UNKNOWN = #FFBB00;

	Processing.logger = console;
	verticesMoving = false;
	this.width = 400;
	this.height = 300;

	size(width, height);

	tg = new ResultGraph();
	frameRate(15);
	frame = get();
	frameSet = false;
}

void draw() {
	moving = tg.verticesMoving || tg.isArcMoving();
	if(moving){
		background(BACKGROUND);
		tg.collide();
		tg.collideWithCenter();

		tg.updateVertexDistances();

		tg.moveVertices();

		if (tg.high && tg.ownTags.size() > 0){
			tg.arcDeTriomphe.update();
			tg.arcDeTriomphe.shrinkExpand();
			tg.arcDeTriomphe.display();    
		}

		fill(STROKE);
		stroke(STROKE);

		for(Vertex v : tg.wrongTags){
			v.collideWithBorders();
			line(tg.cx, tg.cy, v.x, v.y);
			v.display();			
		}
		for(Vertex v : tg.unknownTags){
			v.collideWithBorders();
			line(tg.cx, tg.cy, v.x, v.y);
			v.display();			
		}
		for(Vertex v : tg.correctTags){
			v.collideWithBorders();
			line(tg.cx, tg.cy, v.x, v.y);
			v.display();			
		}
			
		tg.centerVertex.display();
	} else {
		if(!frameSet){
			frame = get();
			frameSet = true;
		} 
		image(frame,0,0);
	}
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
	tg.addOwnTag(s,distance,size,matchType);
}

void addForeignTag(String s, float distance, int size, String matchType){
	tg.addForeignTag(s,distance,size,matchType);
}

void separateTags(){
	tg.separateTags2();
}

void highlightOwnTags(){
	tg.highlightOwnTags();
}

void mix(){
	tg.mix();
}

void moveToOff(float to, float of){
	Vertex v = tg.vertices.get(0);
	v.setMovement(to,off);
}  

boolean isRight(float ax, float ay, float bx, float by, float cx, float cy){
	return ((bx - ax)*(cy - ay) - (by - ay)*(cx - ax)) > 0;
}

boolean isLeft(float ax, float ay, float bx, float by, float cx, float cy){
	return !isRight(ax,ay,bx,by,cx,cy); 
}  

void shrinkExpand(){
	tg.arcDeTriomphe.shrinkExpand();
}