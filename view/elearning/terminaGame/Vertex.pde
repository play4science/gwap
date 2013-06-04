class Vertex {

	float x;
	float y;
	int size;
	String s;
	float distance;
	float angle;
	float newAngle;
	float tw;
	float th;
	float h;
//	float a;
//	float b;
	float radi;
	color c;
	boolean own;
	boolean moving;

	Vertex(float x, float y, int size, String s, float distance, color c) {
		this.own = true;
		this.radi = 4;
		this.moving = false;
		this.c = c;

		this.x = x;
		this.y = y;
		this.size = size;

		this.s = s;

		this.distance = distance;
		this.angle = - HALF_PI;
		this.newAngle = - HALF_PI;

		textSize(size);
		String[] words = s.split("\n");
		int n = words.length;
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

	void setMovement(float to, float off) {
		//this.angle = 2 * PConstants.PI * from / off + PI;
		println("set movement called");
		this.newAngle = - 2 * PI * to / off - HALF_PI;
		if(abs(newAngle - angle) > 0.0001){
			this.moving = true;
			tg.verticesMoving = true;
			println(s +" moving to " + to + " off " + off );
		}
		else
			moving = false;
	}

	void move() {
		if (abs(newAngle - angle) > 0.0001) {
			moving = true;
			tg.verticesMoving = true;
			angle += (newAngle - angle)/2;
			x = tg.cx + ( distance *  cos(angle));
			y = tg.cy + ( distance *  sin(angle));
		} else 
			moving = false;
	}

	void collideWithBorders(){
		float l = x - tw/2;
		float r = x + tw/2;
		float u = y - th/2;
		float d = y + th/2;

		if(u < 0){
//			println("vertex " + s + " up");
			m = (y - tg.cy) /(x - tg.cx);
			distance = dist((th / 2 - tg.cy)/ (m) + tg.cx, th/2, tg.cx, tg.cy);
			move();
		} 
		if(d > height){
//			println("vertex " + s + " down");
			m = (y - tg.cy) /(x - tg.cx);
			distance = dist((2 * height - th - 2 * tg.cy)/(2 * m) + tg.cx, height - th / 2 , tg.cx,tg.cy);
			move();
		}
		if(l < 0){
//			println("vertex " + s + " left");
			m = (y - tg.cy) /(x - tg.cx);
			distance = dist(tw / 2, m * (tw / 2 - tg.cx) + tg.cy, tg.cx,tg.cy);
			move();
		}
		if(r > width){
//			println("vertex " + s + " right");
			m = (y - tg.cy) /(x - tg.cx);
			distance = dist(width - tw /2 , m * (width - tw / 2 - tg.cx) + tg.cy, tg.cx, tg.cy);
			move();
		}
	}

	float[] getCorners(){
		float[] arr = {x - tw/2 , y - th/2 , x + tw/2, y - th/2 , x - tw/2 , y + th/2 , x + tw/2, y + th/2};
		return arr;  
	}
}
