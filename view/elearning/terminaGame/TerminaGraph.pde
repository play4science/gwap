Vertex centerVertex;
int cx;
int cy;
String term;
ArrayList<Vertex> vertices;
ArrayList<Vertex> ownTags;
ArrayList<Vertex> foreignTags;

String newTerm;
boolean doneMoving;

color BACKGROUND = color(255);  
color STROKE = color(0);
color BLUE = color(14, 30, 191);
color YELLOW = color(255, 216, 59);
color RED = color(255, 55, 55);
color TEXT = color(0);
color GREY = color(50);

color CORRECT = #00FF00;
color WRONG = #FF0000;
color UNKNOWN = #CC8811;

void setup() {
  this.width = 400;
  this.height = 400;
  size(width, height);
  cx = (int) width/2;
  cy = (int) height/2;

  background(BACKGROUND);
  term = "term";
  newTerm = "";
  centerVertex = new Vertex(cx, cy, 20, term, 0, CORRECT);
  centerVertex.display();
  vertices = new ArrayList(0);
  ownTags = new ArrayList(0);
  foreignTags = new ArrayList(0);
  stroke(STROKE);
  fill(YELLOW);

  frameRate(15);
}

void draw() {
  background(BACKGROUND);

  collide();

  for (Vertex v : vertices) {
    v.move();
    fill(STROKE);
    line(cx, cy, v.x, v.y);
    v.display();
  }


  centerVertex.display();
}

void setTerm(String term) {
  this.term = term;    
  centerVertex = new Vertex(cx, cy, 20, term, 0, CORRECT);
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
      //println("mix!");
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
      newVertex(newTerm, 100, 20, "");
    }
    newTerm = "";
  } 
  else {
    newTerm = newTerm + key;
  }
}

Vertex newVertex(String s, int distance, int size, String matchType) {
  color c = WRONG;
  if(matchType == "directMatch"){
    c = CORRECT;
  }
  if(matchType == "indirectMatch"){
    c = UNKNOWN;
  }
    
  Vertex vert = new Vertex(cx, cy - distance, size, s, distance, c);
  return vert;
}

void updatePositions(){
  int n = vertices.size();
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

void setDisplayWidth(int width){
  this.width = width;
//  externals.canvas.width = width;
  size(width, height);
  cx = (int) width / 2;
}

void addOwnTag(String s, int distance, int size, String matchType){
  Vertex vert = newVertex(s, distance, size, matchType);
  vertices.add(0, vert);
  updatePositions();
  ownTags.add(0, vert);
}

void seperateTags(){
  _vertices = new ArrayList();
  for(Vertex v : ownTags)
    _vertices.add(v);
    
  for(Vertex v : foreignTags)
    _vertices.add(v);
    
  vertices = _vertices; 

  int n = vertices.size();
  int owns = ownTags.size;
  int foreigns = 0;
  for(int i = 0; i < n; i++){
    Vertex vert = vertices.get(i);
    if (i < owns){
       vert.setMovement(i, n + 2);      
    } else {
       vert.setMovement(i+1, n +2);
    }

  }  

}

void addForeignTag(String s, int distance, int size, String matchType){
  Vertex vert = newVertex(s, distance, size, matchType);
  
  vertices.add(0,vert);
  updatePositions();
  
  foreignTags.add(0, vert);
}

String getOwnTags(){
 String s = "";
 for (Vertex vert : ownTags){
   s = s + " " + vert.s;
 }
 return s;
}

String getForeignTags(){
 String s = "";
 for (Vertex vert : foreignTags){
   s = s + " " + vert.s;
 }
 return s;
} 
