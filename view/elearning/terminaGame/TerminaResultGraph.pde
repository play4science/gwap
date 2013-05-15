String s;
void setup(){
 size(400,200);
 background(15,23,24);
 stroke(255); 
 s = "initial";
 frameRate(15); 
 println("setup");
}

void draw(){
	background(0); 
	text(s, 200,100);
}

void setTerm(string i){
	println("setting term " + i);
	this.s = i;
}