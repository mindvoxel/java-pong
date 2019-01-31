public class Ball {
	//private members cannot be accesed from other classes
	private int directionVector = 2;
	private int xVelocity, yVelocity, xPos, yPos;
	private double angle;
	boolean destroyable;
		
	//When a ball is created, we can provide a default value
	//to the constructor
	public Ball(int xPos, int yPos, double angle){
		this.xPos = xPos;
		this.yPos = yPos;
		this.angle = angle;
		//init the velocity 
		xVelocity = (int)(Math.cos(angle) * (double) directionVector);
		yVelocity = (int)(Math.sin(angle) * (double) directionVector);
		destroyable = false;
		//System.out.println(angle);
	}
	//update position
	public void updateBall(){
		xPos += xVelocity;
		yPos += yVelocity;
		//System.out.println(angle);
		//right bound checking
		if (xPos > Game.GAME_WIDTH -(4 * Game.BALL_RADIUS)){
			reverseXVelocity();
			destroyable = true;
		}
		
		//left bound checking
		if (xPos < 0){
			reverseXVelocity();
			destroyable = true;
		}
		
		//down bound checking
		if (yPos > Game.GAME_HEIGHT - (6 * Game.BALL_RADIUS)){
			reverseYVelocity();
			
		}
		
		//upper bound checking
		if (yPos < 0){
			reverseYVelocity();
		}

	}
	
	//getters
	public int getxPos(){
		return this.xPos;
	}

	public int getyPos(){
		return this.yPos;
	}
	
	/*For swapping directions:
	would actually prefer for setting velocity to be more encapsulated
	and controlled by the Ball class*/
	public void reverseXVelocity(){
		xVelocity = -xVelocity;
	}
	
	public void reverseYVelocity(){
		yVelocity = -yVelocity;
	}
	
	public boolean isDestroyable(){
		return destroyable;
	}
	
	//setter
	public void setYVelocity(int y){
		yVelocity = y;
	}

}//end class
