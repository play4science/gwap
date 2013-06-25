/**
 * An displayable object that is used to highlight the tags that were given by the current user.
 */
class RoundedArc{

	/**
	 * The angle where the highlighting starts.
	 */
	float  start;

	/**
	 * The angle where the highlighting stops.
	 */
	float  stop;

	/**
	 * The middle radius of the middle radius of the arc. r + rs is the outer radius, r – rs is the inner radius.
	 */
	float  r;

	/**
	 * The radius of the circles at start and stop of the arc.
	 */
	float  rs;

	/**
	 * The ResultGraph of this sketch.
	 */
	ResultGraph rg;

	/**
	 * The first Vertex to be highlighted. Usully the Vertex in rg.ownTags with the smallest angle.
	 */
	Vertex first;

	/**
	 * The last Vertex to be highlighted. Usually the vertex in rg.ownTags with the biggest angle.
	 */
	Vertex last;

	/**
	 * Extra value used to include debording vertices. Used in calculation of start.
	 */
	float startMargin;

	/**
	 * Extra value used to include debording vertices. Used in calculation of stop.
	 */
	float stopMargin;
	
	/**
	 * Whether start or stop are changing due to moving vertices.
	 */
	boolean moving;

	/**
	 * Whether start or stop are changing due debording vertices.
	 */
	boolean shrinking;
	
	/**
	 * Constructor
	 */
	RoundedArc(float start, float stop, float r, float rs, ResultGraph rg){
		this.first = rg.vertices.get(0);
		this.last = rg.vertices.get(0);

		this.start = start;
		this.stop = stop;
		this.r = r;
		this.rs = rs;  
		this.rg = rg;
		
		moving = true;
		shrinking = true;
	}

	/**
	 * Displays this arc. Color GREY is used.
	 */
	void display(){
		noStroke();
		fill(GREY);
		arc(tg.cx, tg.cy, 2 * (r + rs), 2 * (r + rs), start , stop );
		fill(BACKGROUND);
		ellipse(tg.cx,tg.cy,2 * (r - rs), 2 * (r - rs) );
		float startX = tg.cx + r * cos(start);
		float startY = tg.cy + r * sin(start);
		float stopX = tg.cx + r * cos(stop);
		float stopY = tg.cy + r * sin(stop);
		fill(GREY);
		ellipse(startX,startY, 2 * rs,2 * rs);
		ellipse(stopX,stopY, 2 * rs, 2 * rs);
	}


	/**
	 * Updates r,rs, start and stop according to changes in rg.ownTags. StartMargin and stopMargin are used.
	 */
	void update(){
		outerRadius = rg.getBiggestCornerDistance(rg.ownTags);
		innerRadius = rg.getSmallestCornerDistance(rg.ownTags);
		rs = (outerRadius - innerRadius)/2;
		r = (outerRadius + innerRadius)/2;
		
		first = rg.ownTags.get(0);
		last = rg.ownTags.get(0);
		
		float zero = 0;
		if(rg.foreignTags.size() != 0){
			float zero = rg.foreignTags.get(0).angle;
		}
		
		for(Vertex v: rg.ownTags){
			float alpha = v.angle - zero;
			float beta = first.angle - zero;
			float gamma = last.angle - zero; 
			if(alpha < 0){
				alpha += TWO_PI;
			}
			if(beta < 0){
				beta += TWO_PI;
			}
			if(gamma < 0){
				gamma += TWO_PI;
			}
			if (alpha < beta){
				first = v;
			}
	
			if(alpha > gamma){
				last = v;
			}
		}
		
		float _start = first.angle + startMargin;
		float _stop = last.angle + stopMargin;

		rs *= 1.1;
		if(_start > _stop){
			_start -= TWO_PI;  
		}
		if(_start != start || _stop != stop){
			moving = true;
			start = _start;
			stop = _stop;
		} else {
			moving = false;
		}
	}

	/**
	 * Updates startMargin and stopMargin. 
	 * Used to include debording vertices or reduce the arcs length if possible.
	 */
	void shrinkExpand(){
//		println("shrinkexp");
//		println(first.s);
//		println(last.s);
		shrinking = false;
		float mx = rg.cx + r * cos(start);
		float my = rg.cy + r * sin(start);
		float[] corners = first.getCorners();
		float[] leftCorners = new float[0];

		for(int i = 0; i < corners.length; i += 2){    
			float coy = corners[i + 1];
			float cox = corners[i];
			if(isLeft(rg.cx,rg.cy,mx,my,cox,coy)){
				leftCorners = append(leftCorners, cox);
				leftCorners = append(leftCorners, coy);  
			}
		}
		
		float d = 0;
		for(int i = 0; i < leftCorners.length; i +=2){
//			float vx = -rg.cy + my;
//			float vy = rg.cx - mx;
//			float l = sqrt(sq(vx) + sq(vy));
//			vx = vx / l;
//			vy = vy / l;
//			float _d = (leftCorners[i] - rg.cx)*vx + (leftCorners[i+1]- rg.cy)*vy;
			float _d = dist(mx,my,leftCorners[i], leftCorners[i+1]);
			if(d < _d)
				d = _d;  
		}

		if(d < rs * 0.9){    //first is inside arc
			startMargin += 0.01; 
			shrinking = true;
		} else if (d > rs){ //first debords
			startMargin -= 0.01;
			shrinking = true;
		} 

		mx = rg.cx + r * cos(stop);
		my = rg.cy + r * sin(stop);
		corners = last.getCorners();
		float[] rightCorners = new float[0];

		for(int i = 0; i < corners.length; i += 2){    
			float cox = corners[i];
			float coy = corners[i + 1];

			if(isRight(rg.cx,rg.cy,mx,my,cox,coy)){
				rightCorners = append(rightCorners, cox);
				rightCorners = append(rightCorners, coy);  
			}
		}

		float d = 0;
		for(int i = 0; i < rightCorners.length; i +=2){
			float _d = dist(mx,my,rightCorners[i], rightCorners[i+1]);
			if(d < _d)
				d = _d;  
		}
		if(d < rs * 0.9){    //last is inside arc
			stopMargin -= 0.01; 
			shrinking = true;
		} else if (d > rs ){ //last debords
			stopMargin += 0.01;
			shrinking = true;
		}

	}
	
	/**
	 * Returns the angles of the outer bounds of this arc. 
	 * The outer bounds are not start and stop because of the circles drawn at the beginning and end of the arc. 
	 */
	float[] getOuterBounds(){
		float add = 2 * atan(rs / (2 * r));
		float[] bounds = {start - add, stop + add};
		return bounds;
	}
}
