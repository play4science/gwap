class Vertex {

  int x;
  int y;
  int size;
  String s;
  float distance;
  float angle;
  float newAngle;
  float tw;
  float th;
  float h;
  float a;
  float b;
  float radi;
  color c;
  
  Vertex(int x, int y, int size, String s, float distance, color c) {
	
	println(s + hex(c));
    this.radi = 4;
    
    this.c = c;
    
    this.x = x;
    this.y = y;
    this.size = size;

    this.s = s;
    
    this.distance = distance;
    this.angle = PI;
    this.newAngle = PI;

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

  void setMovement(int to, int off) {
    //     println("setting movement from "  + from + " to "+  to + " off " + off  );
    //this.angle = 2 * PConstants.PI * from / off + PI;
    //     println("old angle " + angle);
    this.newAngle = 2 * PConstants.PI * to / off + PI;
    //     println("new angle " + newAngle);
  }

  void move() {
    if (abs(newAngle - angle) > 0.01) {
      angle += (newAngle - angle)/2;
      x = cx + (int)( distance *  sin(angle));
      y = cy + (int)( distance *  cos(angle));
    } 
  }
}
