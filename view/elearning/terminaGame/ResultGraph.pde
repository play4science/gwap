/**
 * Extends TerminaGraph. Contains several lists of vertices to represent different kind of tags. 
 * Contains an RoundedArc object.
 */
class ResultGraph extends TerminaGraph{

	/**
	 * Represents the tags that were given by the current user.
	 */
	ArrayList<Vertex> ownTags;

	/**
	 * Represents all correct tags.
	 */
	ArrayList<Vertex> correctTags;

	/**
	 * Represents all unknonw tags.
	 */
	ArrayList<Vertex> unknownTags;

	/**
	 * Represents all wrong tags
	 */
	ArrayList<Vertex> wrongTags;

	/**
	 * Represents all tags that were given by other users.
	 */
	ArrayList<Vertex> foreignTags;

	/**
	 * The actionVertices of this sketch. 
	 */
	ArrayList<Vertex> actionVertices;

	/**
	 *  Whether arcDeTriomphe should be displayed.
	 */
	boolean high;

	/**
	 * If displayed, it highlights the tags that were given by the current user.
	 */
	RoundedArc arcDeTriomphe; 
	
	/**
	 * Constructor
	 */
	ResultGraph(){
		super();
		ownTags = new ArrayList(0);
		foreignTags = new ArrayList(0);
		correctTags = new ArrayList(0);
		wrongTags = new ArrayList(0);
		unknownTags = new ArrayList(0);  
		actionVertices = new ArrayList(0);
		high = false;
		arcDeTriomphe = new RoundedArc(0, 1, 100, 20, this);
	}

	/**
	 * Returns the biggest distance from the center that the corners of the vertices in al have. 
	 * Is used to the correct parameters in arcDeTriomphe.
	 */
	float getBiggestCornerDistance(ArrayList<Vertex> al){
		float outerRadius = 0;
		for(Vertex v : al){
			float[] arr = getCornerDistances(v,cx,cy);
			m = max(arr);
			if (m > outerRadius)
				outerRadius = m;
		}
		return outerRadius;
	}

	/**
	 * Returns the smallest distance from the center that the corners of the vertices in al have. 
	 * Is used to set the correct parameters in arcDeTriomphe.
	 */
	float getSmallestCornerDistance(ArrayList<Vertex> al){
		float innerRadius = width;

		for(Vertex v : al){
			float[] arr = getCornerDistances(v,cx,cy);
			m = min(arr);
			if (m < innerRadius)
				innerRadius = m;
		}
		return innerRadius;
	}

	/**
	 * Returns the distances the corners of v have from (posx,posy).
	 */
	float[] getCornerDistances(Vertex v, float posx, float posy){
		float[] corners = v.getCorners();
		d_l_up = dist(corners[0], corners[1], posx ,posy);
		d_l_down = dist(corners[2], corners[3], posx ,posy);
		d_r_up = dist(corners[4], corners[5], posx ,posy);
		d_r_down = dist(corners[6], corners[7], posx ,posy);
		float[] arr = {d_l_up, d_l_down, d_r_up, d_r_down};
		return arr;
	}


	/**
	 * Extends the inherited method by adding the new Vertex to 
	 * wrongTags, correctTags or unknownTags, according to matchType.
	 */
	void setSize(int newWidth, int newHeight){
		super.setSize(newWidth, newHeight);
		updateVertexDistances();
		separateTags2();
		arcDeTriomphe.moving = true;
		arcDeTriomphe.shrinking = true;
	}


	/**
	 * Calls the inherited newVertex() method, and adds the new vertex to the given lists.
	 * @param s the tag
	 * @param size the text size of this tag
	 * @param matchType the matchtype of this tag. 
	 * @param own whether the tag has been given by the current user or a foreign user.
	 * @param active whether the new Vertex should be active.
	 * 
	 */
	void addTag(String s, int size, String matchType, boolean own, boolean active, String id) {
		Vertex vert = newVertex(s, size, matchType, id);
		vertices.add(0, vert);
		updatePositions();

		if(active){
			ActionVertex avert = new ActionVertex(vert.x, vert.y, vert.size, vert.s, vert.distance, vert.c, vert.id);
			actionVertices.add(0,avert);
			vert = avert;
		}
		if(matchType == "directMatch"){
			correctTags.add(0,vert);
		} else if(matchType == "indirectMatch"){
			unknownTags.add(0,vert);
		} else {
			wrongTags.add(0,vert);
		}
		
		if(own){
			ownTags.add(0, vert);
		} else {
			vert.own = false;
			foreignTags.add(0,vert);
		}
		
	}

	/**
	 * Arranges the vertices to separate ownTags and ForeignTags.
	 */
	void separateTags(){

		_vertices = new ArrayList();
		for(Vertex v : ownTags)
			_vertices.add(v);

		for(Vertex v : foreignTags)
			_vertices.add(v);

		vertices = _vertices; 

		int n = vertices.size();
		int owns = ownTags.size();

		int cf = 0;
		int uf = 0;
		int wf = 0;

		int co = 0;
		int uo = 0;
		int wo = 0;

		for(Vertex v : vertices){
			if(v.own){
				if(v.c == CORRECT)
					co++;
				if(v.c == WRONG)
					wo++;
				if(v.c == UNKNOWN)
					uo++;
			} else {
				if(v.c == CORRECT)
					cf++;
				if(v.c == WRONG)
					wf++;
				if(v.c == UNKNOWN)
					uf++;
			}
		}

		int mf = max(cf,uf,wf);
		int mo = max(co,uo,wo);
		int _wf = 0;
		int _cf = 0;
		int _uf = 0;

		int _wo = 0;
		int _co = 0;
		int _uo = 0;

		for(int i = 0; i < n; i++){
			Vertex vert = vertices.get(i);
			if (i < owns){
				if(vert.c == WRONG){
					vert.setMovement(3 * _wo , 3 * (mo + mf + 1));
					_wo++;
				} else if(vert.c == CORRECT){
					vert.setMovement(3 * _co  + 1, 3 * (mo + mf + 1));
					_co++;
				} else if(vert.c == UNKNOWN){
					vert.setMovement(3 * _uo + 2, 3 * (mo + mf + 1));
					_uo++;
				}
			} else {
				if(vert.c == WRONG){
					vert.setMovement(3 * (mo + _wf + 1 ), 3 * (mo + mf + 1));
					_wf++;
				} else if(vert.c == CORRECT){
					vert.setMovement(3 * (mo + _cf + 1) + 1, 3 * (mo + mf + 1));
					_cf++;
				} else if(vert.c == UNKNOWN){
					vert.setMovement(3 * (mo + _uf + 1) + 2, 3 * (mo + mf + 1));
					_uf++;
				}
			}
		}  
	}

	/**
	 * Alternatice arrangement to separateTags(). Currently in use.
	 */
	void separateTags2(){
		_vertices = new ArrayList();
		for(Vertex v : ownTags)
			_vertices.add(v);

		for(Vertex v : foreignTags)
			_vertices.add(v);

		vertices = _vertices; 

		int owns = ownTags.size();
		int foreigns = foreignTags.size();

		int n = owns + foreigns + 2;

		distributeEqually(filterMatching(ownTags,CORRECT), 0, owns, n);
		distributeEqually(filterMatching(ownTags,UNKNOWN), 0.5, owns + 0.5, n);
		distributeEqually(filterMatching(ownTags,WRONG), 1, owns + 1, n);
		distributeEqually(filterMatching(foreignTags,CORRECT), owns + 1 , n - 1, n);
		distributeEqually(filterMatching(foreignTags,WRONG), owns + 1.5, n - 0.5, n);
		distributeEqually(filterMatching(foreignTags,UNKNOWN), owns + 2, n , n);

	}

	/**
	 * Spreads the vertices in al from position „from“ to position „to“ in a total of „of“ positions. 
	 * Is used by separateTags2().
	 * distributeEqually(someVerts, 2,3,4) arranges all vertices in someVerts in the second and third quater around the center. All verties then have equal distances. 
	 */
	void distributeEqually(ArrayList<Vertex> al, float from, float to, float off){
		float n = abs(from - to);
		int s = al.size();
		int i = 0;
		for(Vertex v : al){
			v.setMovement(from + i * n / s, off);
			i++;
		}
	}

	/**
	 * Returns all vertices in verts that have color c.
	 */
	ArrayList<Vertex> filterMatching(ArrayList<Vertex> verts, color c){
		ArrayList<Vertex> al = new ArrayList<Vertex>();
		for(Vertex v : verts){
			if(v.c == c){
				al.add(v);
			}
		}
		return al;
	}

	/**
	 * Sets high to true.
	 */
	void highlightOwnTags() {
		high = true;
	}

	/**
	 * Sets high to false.
	 */
	void downlightOwnTags() {
		high = false;
	}

	/**
	 * Moves the wrongTags further from the center, and the correctTags closer to the center.
	 */
	void updateVertexDistances() {
		int n = 0;
		if(correctTags.size() > 0)
			n++;
		if(wrongTags.size() > 0)
			n++;
		if(unknownTags.size() > 0)
			n++;
		if( n >= 2 ){

			float currdist = defaultDistance; 

			for (Vertex v : correctTags) {
				v.distance = currdist;
			}

			currdist = getBiggestCornerDistance(correctTags);

			for (Vertex v : unknownTags) {
				v.distance = currdist;
			}

			currdist2 = getBiggestCornerDistance(unknownTags);
			if (currdist2 > currdist)
				currdist = currdist2;

			for (Vertex v : wrongTags) {
				v.distance = currdist;
			}
		}
	}

	/**
	 * Whether arcDeTriomphe is still moving.
	 */
	boolean isArcMoving(){
		return arcDeTriomphe.shrinking || arcDeTriomphe.moving; 
	}
	
	/**
	 * Checks if there is a vertex in foreignTags that collides with arcDeTriomphe.
	 * If so, the colliding vertex is pushed outside of the arc.
	 */
	void collideWithArc(){
		for(Vertex v : foreignTags){
			float[] bounds = arcDeTriomphe.getOuterBounds();
			float bonus = 0.15;
			boolean insideArc = (bounds[0] - bonus  < v.angle && v.angle < bounds[1] + bonus);
			if( insideArc){
				float distToStart = abs(v.angle - bounds[0]);
				float distToStop = abs(v.angle - bounds[1]);
				if(distToStop > distToStart){
					v.newAngle -= distToStart;
				} else {
					v.newAngle += distToStop;
				}
			}
		}
	}
	
	/**
	 * mixes ownTags and foreignTags seperately
	 */
	void mix(){
		mixList(ownTags);
		mixList(foreignTags);
	}
}
