class TerminaGraph {

	Vertex centerVertex;
	float cx;
	float cy;
	String term;
	ArrayList vertices;
	float defaultDistance;
	
	boolean verticesMoving;

	TerminaGraph() {
		defaultDistance = min(width, height)/3;
		verticesMoving = true;
		cx = width/2;
		cy = height/2;

		term = "term";
		centerVertex = new Vertex(cx, cy, 20, term, 0, GREY);
		centerVertex.display();
		vertices = new ArrayList();

	}

	void setTerm(String term) {
		this.term = term;    
		centerVertex = new Vertex(cx, cy, 20, term, 0, GREY);
	}


	void collide() {
		if (vertices.size() > 1) {
			for(Vertex v : vertices){
				for(Vertex w : vertices){
					if (overlapping(v,w)) {
						if(abs(w.angle - v.angle) > PI){ // v and w are close but their angles differ largely
							if(w.angle < v.angle){
								w.newAngle += 0.02;
								v.newAngle -= 0.02;
							} else {
								w.newAngle -= 0.02;
								v.newAngle += 0.02;
							}
						} else {
							if(w.angle < v.angle){
								w.newAngle -= 0.02;
								v.newAngle += 0.02;
							} else {
								w.newAngle += 0.02;
								v.newAngle -= 0.02;
							}
						}
					}
				}
			}
		}
	}

	boolean overlapping(Vertex v, Vertex w){
		float w1 = v.tw / 2;
		float w2 = w.tw / 2;
		float h1 = v.th / 2;
		float h2 = w.th / 2;
		float dx = abs(v.x - w.x);
		float dy = abs(v.y - w.y);

		return (w1 + w2) > dx && (h1 + h2) > dy;
	}

	void mix() {
		for (int i = 0; i < vertices.size(); i++) {
			int j = (int)random(0, vertices.size());
			Vertex v = vertices.get(i);
			Vertex w = vertices.get(j);
			vertices.set(j, v);
			vertices.set(i, w);
			v.setMovement(j, vertices.size());
			w.setMovement(i, vertices.size());
		}
	}

	Vertex newVertex(String s, int size, String matchType) {
		Vertex vert = new Vertex(cx, cy - (int)defaultDistance, size, s, defaultDistance, new color(0));
		tg.inmove = true;
		if (matchType == "directMatch") {
			vert.c = CORRECT;
		} 
		else if (matchType == "indirectMatch") {
			vert.c = UNKNOWN;
		} 
		else {
			vert.c = WRONG;
		}
		return vert;
	}

	void addTag(String s, int size, String matchType){
		println("addTag");
		Vertex vert = newVertex(s,size,matchType);
		this.verticesMoving = true;
		vertices.add(0, vert);
		updatePositions();
	}

	void updatePositions() {
		int n = vertices.size();
		if (n < 7) { //small amount
			int off = 7;
			for (int i = 1; i < n; i++) {
				int to = i;
				Vertex v = vertices.get(i);
				v.setMovement(to, off);
			}
		} 
		else {
			for (int i = 1; i < n; i++) {
				int to = i;
				int off = n + 1;
				Vertex v = vertices.get(i);
				v.setMovement(to, off);
			}
		}
	}
	
	void setSize(int newWidth, int newHeight){
		println("setsize! " + newWidth  + " " + newHeight);
		this.width = newWidth;
		this.height = newHeight;
		size(newWidth, newHeight);
		cx = (int) newWidth / 2;
		cy = (int) newHeight / 2;
		centerVertex.x = cx;
		centerVertex.y = cy;
		defaultDistance = min(newWidth, newHeight)/3;
		updateDistances();
		draw();
	}

	void updateDistances(){
		for(Vertex v : vertices ){
			v.distance = defaultDistance;
			v.moving = true;
			verticesMoving = true;
			v.updatePosition();
		}
		
	}
	
	void moveVertices(){
		if(verticesMoving){
			verticesMoving = false;
			for(Vertex v : vertices){
				v.move();
				verticesMoving = verticesMoving || v.moving;
			}
		}  
	}

	void collideWithCenter(){
		for(Vertex v : vertices){
			if(overlapping(v,centerVertex)){
				if(v.y > cy){
					if(v.x > cx){
						v.newAngle += 0.02;
					} else {
						v.newAngle -= 0.02;            
					}
				} else {
					if(v.x > cx){
						v.newAngle -= 0.02;
					} else {
						v.newAngle += 0.02;
					}
				} 
				v.distance ++;
			}

		}

	}

}
