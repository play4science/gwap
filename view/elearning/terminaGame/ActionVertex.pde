/**
 * Extends the Vertex class. Represents an active vertex that can be clicked.
 */
class ActionVertex extends Vertex{

	/**
	 * a value that can be set to separate make actionVertex occupy more space. 
	 * Doesn't change its appearance
	 */
	int bonusMargin;

	/**
	 * if the mouse is currently over this vertex
	 */
	boolean over;

	/**
	 * a lighter color than the inherited color c.
	 */
	color lighter;

	/**
	 * Constructor. Calls the super constructor. 
	 */
	public ActionVertex(float x, float y, int size, String s, float distance, color c, String id){
		super(x,  y,  size,  s,  distance,  c, id);
		this.lighter =  lerpColor(c, color(255), 0.7);
		bonusMargin = 0;
		over = false;
		th += 2;
	}
	
	/**
	 * Overrides the inherited method. Displays underline under the  string s. 
	 * If over is true then the lighter color is used for the background.
	 */
	void display(){
		stroke(STROKE);
		fill(c);
		if(over){
			fill(lighter);
		} 

		rectMode(CENTER);
		rect(x,y,tw,th,radi);

		fill(TEXT);
		textSize(size);
		textLeading(h);
		textAlign(CENTER, CENTER);
		text(s, x, y - 1);
		float desc = 2;

		for(int i = 1;i <= n; i ++){
			rect(x,y - th/2 + i * h + desc, tw - 5,1);
		}

	}
	
	/**
	 * Method that is called when the mouse moves over this vertex. Over is set to true.
	 */
	void mouseOver(){
		bonusMargin = 15;
		this.over = true;
	}
	
	/**
	 * Method that is called when the mouse leaves this vertex. Over is set to false
	 */
	void mouseLeft(){
		bonusMargin = 0;
		over = false;
	}
	
	/**
	 * The action that is performed on a mouse click. 
	 * Calls the clickEvent method that has to be implemented in the surrounding javascript.
	 * Uses the vertex id variable for authentication.
	 */
	void action(){
		println("action of vertex " + s + " id: "  + id);
		clickEvent(id);
	}
	
	
	/**
	 * Overrides the inherited method.
	 * Returns the textheight plus the current bonusMargin * 0.5
	 */
	float getTh(){
		return th + bonusMargin * 2;
	}

	/**
	 * Overrides the inherited method.
	 * Returns the textwidth plus the current bonusMargin * 0.5
	 */
	float getTw(){
		return tw + bonusMargin * 2;
	}
	
	void setId(String string){
		println("action vertex " + s + " setting id " + string);
		id = string;
		println("set: " +id);
	}

	
}