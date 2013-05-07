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
  float a;
  float b;
  boolean inMove;
  
  Vertex(int x, int y, int size, String s, float distance) {
    this.x = x;
    this.y = y;
    this.size = size;

    this.s = s;
    this.distance = distance;
    this.angle = PConstants.PI;
    this.newAngle = PConstants.PI;
    
    textSize(size);
    tw = textWidth(s);
    th = textAscent() +  textDescent();


    b = sqrt(2) * th;
    a = sqrt( sq(tw) / (1 -  sq(th/b)));
  }  

  //draw Node
  void display() {
    stroke(255);
    fill(0);

    rectMode(CENTER);
    rect(x,y,tw,th);

//    ellipseMode(CENTER);
//    ellipse(x, y, a, b);
    fill(255);
    textSize(size);
    textAlign(CENTER, CENTER);
    text(s, x, y ); 
  }

  void setMovement(int from, int to, int off) {
    //     println("setting movement from "  + from + " to "+  to + " off " + off  );
    this.angle = 2 * PConstants.PI * from / off + PConstants.PI;
    //     println("old angle " + angle);
    this.newAngle = 2 * PConstants.PI * to / off + PConstants.PI;
    //     println("new angle " + newAngle);
  }

  void move() {
    if (abs(newAngle - angle) > 0.01){
      inMove = true;
      angle += (newAngle - angle)/2;
      x = cx + (int)( distance *  sin(angle));
      y = cy + (int)( distance *  cos(angle));
    //    v.y = cy + v.distance * cos(rad + PI);
    } else {
      inMove = false;
    }  
    println("Vertex inMove: "+ inMove );
  }
}

