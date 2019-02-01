package pong;

public class Paddle {

	public static final int WIDTH = 13; //how wide the paddle is
	public static final int HEIGHT = 70; //how tall the paddle is
	private static final int VELOCITY_CONSTANT = 2; //gets rid of magic number 

	private int xPos, yPos;
	private int velocity;
	
	//constructor 
	public Paddle(int xPos, int yPos) {
		//prevent from creating 'bad' paddles outside of the top or bottom of window
		if (yPos > Game.WINDOW_HEIGHT - 110 || yPos < 0){
			this.yPos = Game.WINDOW_HEIGHT / 2;//just create one in middle of height, wouldnt do this for x because dont know if p1 or p2
		}else{
			this.xPos = xPos;
			this.yPos = yPos;
		}
	}
	
	//update the position of the paddles
	public void update(){
		//update the yPos
		yPos +=velocity;
		
		//top of the screen
		if (yPos < 0){
			yPos = 0;
		}
		
		//bottom of the screen
		if (yPos > Game.WINDOW_HEIGHT - 110){
			yPos = Game.WINDOW_HEIGHT - 110;
		}
	}
	
	public void moveUp(){
		velocity = -VELOCITY_CONSTANT;
	}

	public void moveDown(){
		velocity = VELOCITY_CONSTANT;
	}

	public void stop(){
		velocity = 0;
	}
	
	//getter
	public int getXPos() {
		return xPos;
	}
	
	//getter
	public int getYPos() {
		return yPos;
	}

	public int getVelocity(){
		return velocity;
	}
	
}
