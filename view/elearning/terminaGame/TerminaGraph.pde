Vertex centerVertex;
int cx;
int cy;
String term;
ArrayList<Vertex> vertices;
String newTerm;
boolean doneMoving;
void setup() {

  println("entered setup");
  //loop();
  size(400, 400);
  cx = 200;
  cy = 200;
  background(0);
  term = "term";
  newTerm = "";
  centerVertex = new Vertex(cx, cy, 20, term, 0);
  centerVertex.display();
  println("center displayed");
  vertices = new ArrayList(0);
  stroke(255);
  fill(255);

  frameRate(15);
}

void draw() {
//  println("draw is called!");
  background(0);
//  stroke(255);
//  fill(0);
  doneMoving = false;
  
  for (Vertex v : vertices) {
    v.move();
    doneMoving = doneMoving || !v.inMove;
    line(cx, cy, v.x, v.y);
    v.display();
  }
//  println("doneMoving: " + doneMoving);
//  if(doneMoving){
//    deCollide();
//  }
//  collide();
  
  centerVertex.display();
  
}

void deCollide(){
  if(vertices.size() > 1){
    for(int i = 0; i < vertices.size(); i++){
      Vertex v = vertices.get(i);
      Vertex w = vertices.get((i + 1) % vertices.size());
      int w1 = (int)( v.tw / 2);
      int w2 = (int)( w.tw / 2);
      int h1 = (int)( v.th / 2);
      int h2 = (int)( w.th / 2);
      int dx = abs(v.x - w.x);
      int dy = abs(v.y - w.y);
      println("h1 " + h1 + "  h2 " + h2 + "  w1 " + w1 + "  w2 " + w2  + "  dx " + dx + "  dy " + dy);
      if( (w1 + w2) > dx && (h1 + h2) > dy){
        println("decolliding!");
        for(Vertex vert : vertices){
          vert.distance = vert.distance * 1.01;  
        }  
      }
    } 
  }
}  

void collide(){
  if(vertices.size() > 1){
    for(int i = 0; i < vertices.size(); i++){
      Vertex v = vertices.get(i);
      Vertex w = vertices.get((i + 1) % vertices.size());
      int w1 = (int)( v.tw / 2);
      int w2 = (int)( w.tw / 2);
      int h1 = (int)( v.th / 2);
      int h2 = (int)( w.th / 2);
      int dx = abs(v.x - w.x);
      int dy = abs(v.y - w.y);
      println("h1 " + h1 + "  h2 " + h2 + "  w1 " + w1 + "  w2 " + w2  + "  dx " + dx + "  dy " + dy);
      if( (w1 + w2) < dx && (h1 + h2) < dy){
        println("decolliding!");
        for(Vertex vert : vertices){
          vert.distance = vert.distance * 0.99;  
        }  
      }
    } 
  }
}

void keyPressed() {
  if (key == ENTER && !newTerm.equals("")) {
    if (newTerm.trim().equals("mix")) {  
      println("mix!");
      for (int i = 0; i < vertices.size(); i++) {
        Vertex v = vertices.get(i);
        v.setMovement(i, (i + 1) % vertices.size(), vertices.size());
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
//      println("vertex: " + i);
      int from = i-1;
      int to = i;
      int off = 7;

      Vertex v = vertices.get(i);
      v.setMovement(from, to, off);
    }
  } 
  else {
    for (int i = 1; i < n; i++) {
      int from = i - 1;
      int to = i;
      int off = n + 1;
      Vertex v = vertices.get(i);
      v.setMovement(from, to, off);
    }
  }
}

