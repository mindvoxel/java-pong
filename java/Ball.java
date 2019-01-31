public class Ball {
	public static final int RADIUS = 10; //size of the Ball

	private int directionVector = 2;
	private int xVelocity, yVelocity, xPos, yPos;
	private double angle;
	private boolean destroyable;
		
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
		if (xPos > Game.WINDOW_WIDTH -(4 * RADIUS)){
			reverseXVelocity();
			destroyable = true;
		}
		
		//left bound checking
		if (xPos < 0){
			reverseXVelocity();
			destroyable = true;
		}
		
		//down bound checking
		if (yPos > Game.WINDOW_HEIGHT - (6 * RADIUS)){
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
