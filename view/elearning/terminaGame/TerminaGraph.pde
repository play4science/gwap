class TerminaGraph {

	/**
	* The Vertex in the center off the visualisation.
	 */
	Vertex centerVertex;
	
	/**
	 * X coordinate of the center. Gets updated by setSize()
	 */
	float cx;

	/**
	 * Y coordinate of the center
	 */
	float cy;

	/**
	 * The term of this gameround. Is set by setTerm()
	 */
	String term;

	/**
	 * Contains all vertices that surround the centerVertex.
	 */
	ArrayList vertices;

	/**
	 * The default distance of a vertex to (cx,cy). Depends on the current size of the sketch.
	 */
	float defaultDistance;
	

	/**
	 * Is true if at least one of the vertices is moving, and false if they have all stopped. Gets updated by TerminaMain.draw(). 
	 */
	boolean verticesMoving;


	/**
	 * Constructor
	 */
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

	/**
	 * Sets the term of this TerminaGraph and centerVertex.s.
	 */
	void setTerm(String term) {
		this.term = term;    
		centerVertex = new Vertex(cx, cy, 20, term, 0, GREY,term);
	}


	/**
	 * Checks if there is any pair of vertices that overlap. 
	 * If so, the newAngle attributes of the according vertices are changed to 
	 * increase the distance between the vertices. Is called by TerminaMain.draw();
	 */
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

	/**
	 * If two vertices overlap.
	 */
	boolean overlapping(Vertex v, Vertex w){
		float w1 = v.getTw() / 2;
		float w2 = w.getTw() / 2;
		float h1 = v.getTh() / 2;
		float h2 = w.getTh() / 2;
		float dx = abs(v.x - w.x);
		float dy = abs(v.y - w.y);

		return (w1 + w2) > dx && (h1 + h2) > dy;
	}

	/**
	 * Mixes the order of the Vertices in the vertices list randomly, 
	 * and changes their angles accordingly. Used in the pastSession.xhtml.
	 */
	void mix() {
		mixList(vertices);
		updatePositions();
	}

	/**
	 * Mixes the vertices in al randomly.
	 */
	void mixList(ArrayList<Vertex> al){
		for (int i = 0; i < al.size(); i++) {
			int j = (int)random(0, al.size());
			Vertex v = al.get(i);
			Vertex w = al.get(j);
			al.set(j, v);
			al.set(i, w);
		}
	}
	
	/**
	 * Constructs a new vertex. 
	 * Maps the matchType parameter to the color of the new vertex. 
	 * NewVertex() should not be called independently.
	 * @param s the string displayed in the new vertex.
	 * @param size  the fontsize that is used to display s
	 * @param matchType: either „directMatch“,“indirectMatch“ or „WRONG“
	 * @return  a new Vertex
	 */
	Vertex newVertex(String s, int size, String matchType, String id) {
		Vertex vert = new Vertex(cx, cy - (int)defaultDistance, size, s, defaultDistance, new color(0), id);
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

	/**Calls newVertex(...) with the given parameters, adds it to vertices list and calls updatePositions(). 
	 * 
	 */
	void addTag(String s, int size, String matchType, String id){
		Vertex vert = newVertex(s,size,matchType, id);
		this.verticesMoving = true;
		vertices.add(0, vert);
		updatePositions();
	}

	/**
	 * Arranges the vertices arround the center in order of their occurence in the vertices list.
	 */
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
	
	/**
	 * Sets the size of this sketch, and arranges the vertices accordingly.
	 */
	void setSize(int newWidth, int newHeight){
		this.width = newWidth;
		this.height = newHeight;
		size(newWidth, newHeight);
		cx = (int) newWidth / 2;
		cy = (int) newHeight / 2;
		centerVertex.x = cx;
		centerVertex.y = cy;
		defaultDistance = min(newWidth, newHeight)/3;
		resetVertexDistances();
		verticesMoving = true;
	}

	/**
	 * Sets every vertex distance to default.
	 */
	void resetVertexDistances(){
		for(Vertex v : vertices ){
			v.distance = defaultDistance;
			v.moving = true;
			v.updatePosition();
		}

	}
	
	/**
	 * Calls move() on every vertex in the vertices list. Updates the verticesMoving attribute.
	 */
	void moveVertices(){
		if(verticesMoving){
			verticesMoving = false;
			for(Vertex v : vertices){
				v.move();
				verticesMoving = verticesMoving || v.moving;
			}
		}  
	}

	/**
	 * Checks if there is a vertex in the vertices list that overlaps with centerVertex. 
	 * If so, the newAngle and the distance attribute are changed to move the vertex away 
	 * from the center.
	 */
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
//				println(v.s + " collides with center. distance " + v.distance);
				v.distance ++;
//				println("new distance " + v.distance);
			}

		}

	}

}
