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
	WRONG = #FF6666;
	UNKNOWN = #FFBB00;

	Processing.logger = console;
	verticesMoving = false;
	this.width = 400;
	this.height = 300;

	size(width, height);

	tg = new TerminaGraph();
	frameRate(15);
}

void draw() {
	background(BACKGROUND);
	tg.collide();
	tg.collideWithCenter();

	tg.moveVertices();

	fill(STROKE);
	stroke(STROKE);

	for (Vertex v : tg.vertices) {
		v.collideWithBorders();
		line(tg.cx, tg.cy, v.x, v.y);
		v.display();
	}
	tg.centerVertex.display();
}

void setTerm(String term){
	tg.setTerm(term);
}

void setDisplayWidth(int w){
	tg.setSize(w, height);
}

void addTag(String s, int size, String matchType){
	tg.addTag(s,size,matchType);
}

void mix(){
	tg.mix();
}

void moveToOff(float to, float off){
	Vertex v = tg.vertices.get(0);
	v.setMovement(to,off);
}  

boolean isRight(float ax, float ay, float bx, float by, float cx, float cy){
	return ((bx - ax)*(cy - ay) - (by - ay)*(cx - ax)) > 0;
}

boolean isLeft(float ax, float ay, float bx, float by, float cx, float cy){
	return !isRight(ax,ay,bx,by,cx,cy); 
}  
