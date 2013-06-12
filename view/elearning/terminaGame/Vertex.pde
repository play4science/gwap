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
		this.angle = TWO_PI - HALF_PI;
		this.newAngle = TWO_PI - HALF_PI;

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

	float[] getCorners(){
		float[] arr = {x - tw/2 , y - th/2 , x + tw/2, y - th/2 , x - tw/2 , y + th/2 , x + tw/2, y + th/2};
		return arr;  
	}
}
