/**
 * Main file for the sketch during the game. Implements the processing methods setup() and draw(). 
 */

/**
 * The TerminaGraph that holds the displayed information of the ongoing game.
 */
TerminaGraph tg;

/**
 * The background color of visualization
 */
color BACKGROUND;  

/**
 * The color used to draw lines and borders around shapes.
 */
color STROKE;

/**
 * The blue used in the Termina logo.
 */
color BLUE;

/**
 * The yellow used in the Termina logo.
 */
color YELLOW;

/**
 * The red used in the Termina logo.
 */
color RED;

/**
 * The color of the displayed text.
 */
color TEXT;

/**
 * A light grey used for highlighting. 
 */
color GREY;

/**
 * Background color of the correct Tags.
 */
color CORRECT;

/**
 * Background color of the wrong Tags.
 */
color WRONG;

/**
 * Background color of the unknown Tags.
 */
color UNKNOWN;

/**
 *Processing method.
Called once when the programm is started. Used to set initial properties as size etc.
 */
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

/**
 * Processing method. Is excecuted continously. Used to calculate the new positions of the vertices of tg and to display them afterwards.
 */
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
	}
	for(Vertex v : tg.vertices){
		v.display();
	}
	
	tg.centerVertex.display();
}

/**
 * Sets the string of the centerVertex of tg.
 */
void setTerm(String term){
	tg.setTerm(term);
}

/**
 * Sets the width of the processing instance.
 */
void setDisplayWidth(int w){
	tg.setSize(w, height);
}

/**
 * Calls tg.addTag with the given parameters.
 */
void addTag(String s, int size, String matchType){
	tg.addTag(s,size,matchType);
}

/**
 * Calls tg.mix().
 */
void mix(){
	tg.mix();
}

void moveToOff(float to, float off){
	Vertex v = tg.vertices.get(0);
	v.setMovement(to,off);
}  
/**
 * Determines if the point with coordinates (cx,cy) is right of the line from (ax,ay) to (bx,by)
 */
boolean isRight(float ax, float ay, float bx, float by, float cx, float cy){
	return ((bx - ax)*(cy - ay) - (by - ay)*(cx - ax)) > 0;
}

/**
 * See isRight().
 */
boolean isLeft(float ax, float ay, float bx, float by, float cx, float cy){
	return !isRight(ax,ay,bx,by,cx,cy); 
}  
