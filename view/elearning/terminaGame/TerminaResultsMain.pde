/**
 * Main file for the sketch after the game. Implements the processing methods setup() and draw().
 */

/**
 * The ResultGraph that contains all displayable objects for this sketch.
 */
ResultGraph tg;

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

/**The yellow used in the Termina logo.
 * 
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
 * Background color of the worng Tags.
 */
color WRONG;

/**
 * Background color of the unknown Tags.
 */
color UNKNOWN;

/**
 * Whether there are displayed objects that are moving. If „moving“ is false, the current frame is stored in frame.
 */
boolean moving;

/**
 * The image of the steady sketch. Is used to shorten the calculation time of draw().
 */
PImage frame;

/**
 * Whether „frame“ has been set.
 */
boolean frameSet;

/**
 * Processing method.
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

	tg = new ResultGraph();
	frameRate(15);
	frame = get();
	frameSet = false;
	
}

/**
 * Processing method. Is excecuted continously. 
 * Used to calculate the new positions of the displayable objects within tg and to display them.
 */
void draw() {
	moving = tg.verticesMoving || tg.isArcMoving();
	if(moving){
		background(BACKGROUND);
		tg.collide();
		tg.collideWithCenter();

		if(tg.high){
			tg.collideWithArc();
		}
		
		tg.updateVertexDistances();

		tg.moveVertices();

		if (tg.high && tg.ownTags.size() > 0){
			tg.arcDeTriomphe.update();
			tg.arcDeTriomphe.shrinkExpand();
			tg.arcDeTriomphe.display();
		}

		fill(STROKE);
		stroke(STROKE);

		for(Vertex v : tg.vertices){
			v.collideWithBorders();
			line(tg.cx, tg.cy, v.x, v.y);
		}
		for(Vertex v : tg.vertices){
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

/**
 * calls tg.setTerm(...);
 */
void setTerm(String term){
	tg.setTerm(term);
}

/**
 * calls setSize()
 */
void setDisplayWidth(int w){
	tg.setSize(w, height);
}

/**
 * calls setSize()
 */
void setDisplayHeight(int h){
	tg.setSize(width,h);
}

/**
 * calls tg.setSize(). 
 */
void setSize(int w, int h){
	tg.setSize(w,h);
	frameSet = false;
}

/**
 * calls tg.addTag
 * @param s the tag
 * @param size the font size of the string
 * @param matchtype the matchtype of the tag
 * @param active if the new vertex should be an active vertex @see ActionVertex.pde
 */
void addOwnTag(String s, int size, String matchType, boolean active){
	tg.addTag(s,size,matchType, true, active);
}

/**
 * calls tg.addTag
 * @param s the tag
 * @param size the font size of the string
 * @param matchtype the matchtype of the tag
 * @param active if the new vertex should be an active vertex @see ActionVertex.pde 
 */
void addForeignTag(String s, int size, String matchType, boolean active){
	tg.addTag(s,size,matchType, false, active);
}

/**
 * calls tg.separateTags2();
 */
void separateTags(){
	tg.separateTags2();
}

/**
 * calls tg.highlightOwnTags();
 */
void highlightOwnTags(){
	tg.highlightOwnTags();
}

/**
 * calls tg.mix();
 */
void mix(){
	tg.mix();
	frameSet = false;
}

/**
 * moves the the first vertex in tg.vertices position „to“ of „of“ position. Should only be used for testing. 
 */
void moveToOff(float to, float of){
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

/**
 * calls tg.shrinkExpand()
 */
void shrinkExpand(){
	tg.arcDeTriomphe.shrinkExpand();
}

/**
 * Resets the current graph to it's initial state without term and vertices. 
 */
void reset(){
	int w = width;
	int h = height;
	setup();
	setSize(w,h);
	
}

/**
 * Processing method. Is called when the mouse is pressed over the processing sketch. 
 * If an actionVertex is clicked, then its action() method is called.
 */
void mousePressed(){
	for(ActionVertex av : tg.actionVertices){
		if(av.pointInside(mouseX,mouseY)){
			av.action();
		}
	}
}

/**
 * Processing method. Is called when the mouse is moved over the processing sketch. 
 * If the mouse is over an actionVertex, then its mouseOver() method is called. 
 */
void mouseMoved(){
	tg.verticesMoving = true;
	frameSet = false;

	for(Vertex v : tg.actionVertices){
		v.mouseLeft();
		if(v.pointInside(mouseX,mouseY)){
			tg.activeVertex = v;
			v.mouseOver();
		}
	}
}

