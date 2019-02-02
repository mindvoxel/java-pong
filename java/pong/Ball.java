package pong;

public class Ball {
	public static final int RADIUS = 10; //size of the Ball

	private int directionVector = 2;
	private int xVelocity, yVelocity, xPos, yPos;
	private double angle;
	private boolean destroyable; //is this field neccessary?
		
	public Ball(int xPos, int yPos, double angle){
		//in case a crazy person tries to create a Ball with values outside of the Game screen
		if((yPos >= (Game.WINDOW_HEIGHT - (6 * RADIUS))) 
			|| (yPos <= 0) 
			|| (xPos == (Game.WINDOW_WIDTH - (4 * RADIUS)))
			|| (xPos == 0)){
			//then set ball x and y to be middle of the screen
			this.xPos = Game.WINDOW_WIDTH/2;
			this.yPos = Game.WINDOW_HEIGHT/2;			
		}else{
			this.xPos = xPos;
			this.yPos = yPos;
		}

		this.angle = angle;
		//init the velocity in both directions
		xVelocity = (int)(Math.cos(angle) * (double) directionVector);
		yVelocity = (int)(Math.sin(angle) * (double) directionVector);
		destroyable = false;
		System.out.println(angle);
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
	public int getXPos(){
		return this.xPos;
	}

	public int getYPos(){
		return this.yPos;
	}
	
	//for swapping directions
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
