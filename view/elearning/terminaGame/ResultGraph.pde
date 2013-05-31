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
		this.type = "ResultGraph";
		ownTags = new ArrayList(0);
		foreignTags = new ArrayList(0);
		correctTags = new ArrayList(0);
		wrongTags = new ArrayList(0);
		unknownTags = new ArrayList(0);  

		high = false;
		arcDeTriomphe = new RoundedArc(0, 1, 100, 20);

	}

	float getBiggestCornerDistance(ArrayList<Vertex> al){
		float outerRadius = 0;

		for(Vertex v : al){
			d_l_up = dist(v.x - v.tw /2, v.y - v.th / 2, cx ,cy);
			d_l_down = dist(v.x - v.tw /2, v.y + v.th / 2, cx ,cy);
			d_r_up = dist(v.x + v.tw /2, v.y - v.th / 2, cx ,cy);
			d_r_down = dist(v.x + v.tw /2, v.y + v.th / 2, cx ,cy);
			float[] arr = {d_l_up, d_l_down, d_r_up, d_r_down};
			m = max(arr);
			if (m > outerRadius)
				outerRadius = m;
		}
		return outerRadius;
	}

	float getSmallestCornerDistance(ArrayList<Vertex> al){
		float innerRadius = width;

		for(Vertex v : al){
			float[] arr = getCornerDistances(v);
			m = min(arr);
			if (m < innerRadius)
				innerRadius = m;
		}
		return innerRadius;
	}

	float[] getCornerDistances(Vertex v){
		d_l_up = dist(v.x - v.tw /2, v.y - v.th / 2, cx ,cy);
		d_l_down = dist(v.x - v.tw /2, v.y + v.th / 2, cx ,cy);
		d_r_up = dist(v.x + v.tw /2, v.y - v.th / 2, cx ,cy);
		d_r_down = dist(v.x + v.tw /2, v.y + v.th / 2, cx ,cy);
		float[] arr = {d_l_up, d_l_down, d_r_up, d_r_down};
		return arr;
	}

	void updateArc(){
		outerRadius = getBiggestCornerDistance(ownTags);
		innerRadius = getSmallestCornerDistance(ownTags);
		arcDeTriomphe.rs = (outerRadius - innerRadius)/2;
		arcDeTriomphe.r = (outerRadius + innerRadius)/2;

		float start = 2 * PI;
		float stop = 0;
		for(Vertex v: ownTags){
			if (v.angle > stop)
				stop = v.angle;
			if(v.angle < start)
				start = v.angle;
		}

		start = - start + HALF_PI;
		stop = - stop + HALF_PI;
		
		if(start < stop){
			arcDeTriomphe.start = - start + HALF_PI;
			arcDeTriomphe.stop = - stop + HALF_PI;
		} else {
			arcDeTriomphe.start = stop;
			arcDeTriomphe.stop =  start;       
		}
//		noStroke();
//		float pStart_x = cx + arcDeTriomphe.r * cos(arcDeTriomphe.start);
//		float pStart_y = cy + arcDeTriomphe.r * sin(arcDeTriomphe.start);
		//
//		float pStop_x = cx + arcDeTriomphe.r * cos(arcDeTriomphe.stop);
//		float pStop_y = cy + arcDeTriomphe.r * sin(arcDeTriomphe.stop);
		//
//		Vertex v = ownTags.get(0);
		//
//		float p1x = v.x + v.tw / 2;
//		float p1y = v.y + v.th / 2;
//		float p2x = v.x - v.tw / 2;
//		float p2y = v.y + v.th / 2;
////		println(p1x + " " + p1y + " " + pStart_x + " " +pStop_x  + " " + arcDeTriomphe.rs +  " " +  dist(p1x,p1y, pStart_x, pStart_y) );
//		if(dist(p1x,p1y, pStart_x, pStart_y) > arcDeTriomphe.rs){
//		float alpha = acos((p1x - cx)/arcDeTriomphe.r);
//		float beta = 2 * asin(arcDeTriomphe.rs / (2* arcDeTriomphe.r));
////		println("debording " + v.s);
//		arcDeTriomphe.stop = - alpha - beta;
//		} else if(dist(p2x,p2y,pStop_x, pStop_y) > arcDeTriomphe.rs){
//		float alpha = acos((p2x - cx)/arcDeTriomphe.r);
//		float beta = 2 * asin(arcDeTriomphe.rs / (2* arcDeTriomphe.r));
//		arcDeTriomphe.start = - alpha + beta;
		//
//		}
		arcDeTriomphe.rs *= 1.1;

	}

	Vertex newVertex(String s, float distance, int size, String matchType) {
		Vertex vert = super.newVertex(s,distance,size,matchType);
		if(matchType == "directMatch")
			correctTags.add(vert);
		else if(matchType == "indirectMatch")
			unknownTags.add(vert);
		else
			wrongTags.add(vert);
		return vert;
	}

	void addOwnTag(String s, int distance, int size, String matchType) {
		Vertex vert = newVertex(s, distance, size, matchType);
		vertices.add(0, vert);
		updatePositions();
		ownTags.add(0, vert);
	}

	void addForeignTag(String s, int distance, int size, String matchType) {
		Vertex vert = newVertex(s, distance, size, matchType);
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


	ArrayList<Vertex> getOwnTags() {
		ArrayList<Vertex> owns = new ArrayList(); 
		for (Vertex v : vertices)
			if (v.own)
				owns.add(v);
		return owns;
	}

	ArrayList<Vertex> getForeignTags() {
		foreigns = new ArrayList();
		for (Vertex v : vertices)
			if (! vert.own)
				foreigns.add(v);
		return foreigns;
	} 

	void highlightOwnTags() {
		high = true;
	}

	void downlightOwnTags() {
		high = false;
	}

	void updateVertexDistances() {
		float currdist = 100; 

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
