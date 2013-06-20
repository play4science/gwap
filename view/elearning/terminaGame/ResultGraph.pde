class ResultGraph extends TerminaGraph{
	ArrayList<Vertex> ownTags;
	ArrayList<Vertex> correctTags;
	ArrayList<Vertex> unknownTags;
	ArrayList<Vertex> wrongTags;
	ArrayList<Vertex> foreignTags;
	ArrayList<Vertex> ownTags;

	boolean high;
	RoundedArc arcDeTriomphe; 

	ResultGraph(){
		super();
		ownTags = new ArrayList(0);
		foreignTags = new ArrayList(0);
		correctTags = new ArrayList(0);
		wrongTags = new ArrayList(0);
		unknownTags = new ArrayList(0);  

		high = false;
		arcDeTriomphe = new RoundedArc(0, 1, 100, 20, this);

	}

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

	float[] getCornerDistances(Vertex v, float posx, float posy){
		float[] corners = v.getCorners();
		d_l_up = dist(corners[0], corners[1], posx ,posy);
		d_l_down = dist(corners[2], corners[3], posx ,posy);
		d_r_up = dist(corners[4], corners[5], posx ,posy);
		d_r_down = dist(corners[6], corners[7], posx ,posy);
		float[] arr = {d_l_up, d_l_down, d_r_up, d_r_down};
		return arr;
	}


	void setSize(int newWidth, int newHeight){
		super.setSize(newWidth, newHeight);
		updateVertexDistances();
		separateTags2();
		arcDeTriomphe.moving = true;
		arcDeTriomphe.shrinking = true;
	}

	Vertex newVertex(String s, int size, String matchType) {
		Vertex vert = super.newVertex(s,size,matchType);
		if(matchType == "directMatch")
			correctTags.add(vert);
		else if(matchType == "indirectMatch")
			unknownTags.add(vert);
		else
			wrongTags.add(vert);
		return vert;
	}

	void addOwnTag(String s, int size, String matchType) {
		Vertex vert = newVertex(s, size, matchType);
		vertices.add(0, vert);
		updatePositions();
		ownTags.add(0, vert);
	}

	void addForeignTag(String s, int size, String matchType) {
		Vertex vert = newVertex(s,size, matchType);
		vert.own = false;

		vertices.add(0, vert);
		updatePositions();
		foreignTags.add(0,vert);
	}

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

	void distributeEqually(ArrayList<Vertex> al, float from, float to, float off){
		float n = abs(from - to);
		int s = al.size();
		int i = 0;
		for(Vertex v : al){
			v.setMovement(from + i * n / s, off);
			i++;
		}
	}

	ArrayList<Vertex> filterMatching(ArrayList<Vertex> verts, color c){
		ArrayList<Vertex> al = new ArrayList<Vertex>();
		for(Vertex v : verts){
			if(v.c == c){
				al.add(v);
			}
		}
		return al;
	}

//	ArrayList<Vertex> getOwnCorrectTags() {
//	ArrayList<Vertex> owns = new ArrayList(); 
//	for (Vertex v : vertices)
//	if (v.own)
//	owns.add(v);
//	return owns;
//	}

//	ArrayList<Vertex> getForeignTags() {
//	foreigns = new ArrayList();
//	for (Vertex v : vertices)
//	if (! vert.own)
//	foreigns.add(v);
//	return foreigns;
//	} 

	void highlightOwnTags() {
		high = true;
	}

	void downlightOwnTags() {
		high = false;
	}

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

	boolean isArcMoving(){
		return arcDeTriomphe.shrinking || arcDeTriomphe.moving; 
	}
	
	void collideWithArc(){
		for(Vertex v : foreignTags){
			int[] bounds = arcDeTriomphe.getOuterBounds();
			float bonus = 0.15;
			boolean insideArc = (bounds[0] - bonus  < v.angle && v.angle < bounds[1] + bonus);
			if( insideArc){
				float distToStart = abs(v.angle - (bounds[0] - bonus));
				float distToStop = abs(v.angle - (bounds[1] + bonus));
				if(distToStop > distToStart){
					v.newAngle -= distToStart;
				} else {
					v.newAngle += distToStop;
				}
			}
		}
	}
	
	void mix(){
		mixList(ownTags);
		mixList(foreignTags);
	}
}
