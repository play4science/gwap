	/**
	 * A vertex is a displayable representation of a tag given during the termina game. 
	 */
class Vertex {
	
	/**
	 * X-coordinate of the Posiiton of this vertex.
	 */
	float x;

	/**
	 * Y-coordinate of the Posiiton of this vertex.
	 */
	float y;

	/**
	 * The fontsize that is used to display s.
	 */
	int size;

	/**
	 * The string that is displayed in this vertex. May contain linebreaks. 
	 */
	String s;

	/**
	 * The distance of the point (x,y) from the center.
	 */
	float distance;

	/**
	 * The angle under which the Vertex appears. 
	 * @see terminaGraph.updatePosition() for details. The values of angle should range from 0 to 2*Pi. 
	 */
	float angle;

	/**
	 * The angle towards the vertice is moving. 
	 */
	float newAngle;

	/**
	 * The textwidth needed to display s.
	 */
	float tw;

	/**
	 * The textheight needed to display s.
	 */
	float th;

	/**
	 * The sum of the Ascent and the Descent off the current font. Used to calculate th.
	 */
	float h;


	/**
	 * The radius of the rounded corners of the text box.
	 */
	float radi;

	/**
	 * The color that is used to draw the text box. (usually WRONG, CORRECT, or UNKNOWN)
	 */
	color c;

	/**
	 * Whether the vertex represents a tag that is given by the current player or by someone else. 
	 * Should only be used for a resutlGraph, not during the game. 
	 */
	boolean own;

	/**
	 * Whether the difference of newAngle and angle is bigger than a small threshold. 
	 * If true, the will move if move() is called.
	 */
	boolean moving;

	/**
	 * The number of lines needed to display s.
	 */
	int n;
	
	/**
	 * The id of this vertex. While vertices can normally identified by s, 
	 * it is sometimes usefull for a vertex to have an authentic id. 
	 */
	String id;
	
	/**
	 * Constructor
	 */
	Vertex(float x, float y, int size, String s, float distance, color c, String id) {
		this.own = true;
		this.radi = 4;
		this.moving = false;
		this.c = c;

		this.x = x;
		this.y = y;
		this.size = size;

		this.s = s;
		this.id = id;

		this.distance = distance;
		this.angle = TWO_PI - HALF_PI;
		this.newAngle = TWO_PI - HALF_PI;

		textSize(size);
		String[] words = s.split("\n");
		n = words.length;
		int[] lengths = new int[n];
		for (int i = 0; i < n; i++) {
			lengths[i] = (int) textWidth(words[i]);
		}
		tw = max(lengths) + radi;
		//println(tw);
		h = textAscent() + textDescent();
		th = n * h + radi;
		//println(th);
		//b = sqrt(2) * th;
		//a = sqrt( sq(tw) / (1 -  sq(th/b)));
	}  

	/**
	 * Displays the vertex. The point (x,y) is the center of the displayed rectangle and the displayed text. 
	 * See processing textAlign() and rectMode() methods for detail. 
	 */
	void display() {
		stroke(STROKE);
		fill(c);


		//ellipseMode(CENTER);
		//ellipse(x, y, a, b);

		rectMode(CENTER);
		rect(x,y,tw,th,radi);

		fill(TEXT);
		textSize(size);
		textLeading(h);
		textAlign(CENTER, CENTER);
		text(s, x, y);
		

	}

	/**
	 * Sets new Angle to – 2 * PI * to / off – PI /2. 
	 * By this means, if setMovement(7,9) is called, the vertex will move to position 7 of 9 equally arranged positions. 
	 */
	void setMovement(float to, float of) {
		float na = (- TWO_PI * to / of - HALF_PI)%TWO_PI;
		if(na < 0){
			na += TWO_PI;  
		}

		if(abs(na - angle) > 0.001){
			this.newAngle = na;
			this.moving = true;
			tg.verticesMoving = true;
		}
		else
			newAngle = angle;
		moving = false;
	}

	/**
	 * Updates the angle according to newAngle and calls updatePosition() afterwards. 
	 */
	void move() {
		if (abs(newAngle % TWO_PI - angle % TWO_PI) > 0.001) {
			moving = true;
			tg.verticesMoving = true;

			if(abs(newAngle - TWO_PI - angle) <  abs(newAngle - angle)){
				angle += (newAngle - TWO_PI - angle)/2;  
			} else {
				angle += (newAngle - angle)/2;  
			}

			updatePosition();
		} else 
			moving = false;
	}

	/**
	 * Recalculates x and y off the vertex, according to its angle, 
	 * its distance from the center of the sketch, and the current center of the sketch. 
	 */
	void updatePosition(){
		if(angle < 0){
			angle += TWO_PI;
			newAngle += TWO_PI;
		}
		if(angle > TWO_PI){
			angle -= TWO_PI;
			newAngle -= TWO_PI;  
		}
		x = tg.cx + ( distance *  cos(angle));
		y = tg.cy + ( distance *  sin(angle));
	}

	/**
	 * Checks whether the vertex is outside the sketch and sets its distance accordingly.
	 */
	void collideWithBorders(){
		boolean l = x - tw/2 < 0;
		boolean r = x + tw/2 > width;
		boolean u = y - th/2 < 0;
		boolean d = y + th/2 > height;
		if(l || u || r || d){
			float dx = (x - tg.cx);
			float dy = (y - tg.cy);
			boolean vertical = dx < 1;
			float m;
			if(!vertical)
				float m = dy/dx;
			if(u){
				if(vertical){
					distance = tg.cy - th/2;
				} else {
					distance = dist((th / 2 - tg.cy)/ (m) + tg.cx, th/2, tg.cx, tg.cy);
				}
			} 
			if(d){
				if(vertical){
					distance = height - th/2 - tg.cy;  
				} else {
					distance = dist((height - th/2 - tg.cy)/(m) + tg.cx, height - th / 2 , tg.cx,tg.cy);
				}
				distance -= 2;
			}

			if(l){
				distance = dist(tw / 2, m * (tw / 2 - tg.cx) + tg.cy, tg.cx,tg.cy);
			}
			if(r){
				distance = dist(width - tw /2 , m * (width - tw / 2 - tg.cx) + tg.cy, tg.cx, tg.cy);
			}
			updatePosition();
		}
	}

	/**
	 * Returns the the coordinates of the corners of the vertex. 
	 * The array contains the coordinates in the following order: [x1,y1,x2,y2,x3,y3,x4,y4].
	 * Upper left corner, upper right corner, lower left corner, lower right corner. 
	 */
	float[] getCorners(){
		float[] arr = {x - tw/2 , y - th/2 , x + tw/2, y - th/2 , x - tw/2 , y + th/2 , x + tw/2, y + th/2};
		return arr;  
	}
	
	/**
	 * I (px,py) lies inside the textarea of this vertex.
	 */
	boolean pointInside(float px, float py){
		return px > x - tw/2 && px < x + tw/2 && py > y - th/2 && py < y + th/2;  
	}
	
	/**
	 * returns the text width of this vertex.
	 */
	float getTw(){
		return tw;
	}

	/**
	 * returns the text heigth of this vertex.
	 */
	float getTh(){
		return th;
	}
	
	void setId(String string){
//		println("vertex " + s + " setting id " + string);
//		id = string;
//		println("set: " +id);
	}
}
