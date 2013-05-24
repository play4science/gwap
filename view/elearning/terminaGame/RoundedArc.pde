class RoundedArc{
  float  start;
  float  stop;
  float  r;
  float  rs;
  
  RoundedArc(float start, float stop, float r, float rs){
    this.start = start;
    this.stop = stop;
    this.r = r;
    this.rs = rs;  
  }
  
  void display(){
    noStroke();
    fill(GREY);
    arc(cx, cy, 2 * (r + rs), 2 * (r + rs), start , stop );
    fill(BACKGROUND);
    ellipse(cx,cy,2 * (r - rs), 2 * (r - rs) );
    float startX = cx + r * cos(start);
    float startY = cy + r * sin(start);
    float stopX = cx + r * cos(stop);
    float stopY = cy + r * sin(stop);
    fill(GREY);
    ellipse(startX,startY, 2 * rs,2 * rs);
    ellipse(stopX,stopY, 2 * rs, 2 * rs);
  }
  
  
}
