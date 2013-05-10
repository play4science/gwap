Vertex centerVertex;
int cx;
int cy;
String term;
ArrayList<Vertex> vertices;
String newTerm;
boolean doneMoving;

color BACKGROUND = color(255);  
color STROKE = color(0);
color BLUE = color(14, 30, 191);
color YELLOW = color(255, 216, 59);
color RED = color(255, 55, 55);
color TEXT = color(0);

void setup() {
  size(400, 400);
  cx = 200;
  cy = 200;

  background(BACKGROUND);
  term = "term";
  newTerm = "";
  centerVertex = new Vertex(cx, cy, 20, term, 0);
  centerVertex.display();
  vertices = new ArrayList(0);
  stroke(STROKE);
  fill(YELLOW);

  frameRate(15);
}

void draw() {
  background(BACKGROUND);
  doneMoving = true;

  for (Vertex v : vertices) {
    v.move();
    fill(STROKE);
    line(cx, cy, v.x, v.y);
    v.display();
  }
  collide();


  centerVertex.display();
}

void setTerm(String term) {
  this.term = term;    
  centerVertex = new Vertex(cx, cy, 20, term, 0);
}


void collide() {
  if (vertices.size() > 1) {
    for (int i = 0; i < vertices.size(); i++) {
      Vertex v = vertices.get(i);
      Vertex w = vertices.get((i + 1) % vertices.size());
      int w1 = (int)( v.tw / 2);
      int w2 = (int)( w.tw / 2);
      int h1 = (int)( v.th / 2);
      int h2 = (int)( w.th / 2);
      int dx = abs(v.x - w.x);
      int dy = abs(v.y - w.y);
      if ( (w1 + w2) > dx && (h1 + h2) > dy) {
        if (i == 0) {
          w.newAngle += 0.02;
        } 
        else if (i == vertices.size() - 1) {
          v.newAngle -= 0.02;
        } 
        else {
          v.newAngle -= 0.02;
          w.newAngle += 0.02;
        }
      }
    }
  }
}



void keyPressed() {
  if (key == ENTER && !newTerm.equals("")) {
    if (newTerm.trim().equals("mix")) {  
      println("mix!");
      for(int i = 0; i < vertices.size(); i++){
        int j = (int)random(0, vertices.size());
        Vertex v = vertices.get(i);
        Vertex w = vertices.get(j);
        vertices.set(j,v);
        vertices.set(i,w);
        v.setMovement(j,vertices.size());
        w.setMovement(i,vertices.size());
      }  
    } 
    else {
      newVertex(newTerm, 100, 20);
    }
    newTerm = "";
  } 
  else {
    newTerm = newTerm + key;
  }
}

void newVertex(String s, int distance, int size) {
  Vertex vert = new Vertex(cx, cy - distance, size, s, distance);
  vertices.add(0, vert);

  int n = vertices.size();

  //update the positions
  if (n < 7) { //small amount
    for (int i = 1; i < n; i++) {

      int to = i;
      int off = 7;

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

