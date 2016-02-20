package guimodule;

import processing.core.PApplet;

public class MyDisplay extends PApplet{
	
	public void setup()
	{
		size(400, 400);
		background(0, 255, 255);
	}
	public void draw()
	{
		//draw a big circle
		fill(255, 0, 0);
		ellipse(200, 200, 390, 390);
		
		//draw the left eye
		fill(0, 0, 0);
		ellipse(120, 130, 50, 70);
		//draw the right eye
		ellipse(280, 130, 50, 70);
		
		//draw the mouth/smile
		noFill();//don't fill the smile; only show the edge
		arc(200, 280, 150, 75, 0, PI);
	}
}
