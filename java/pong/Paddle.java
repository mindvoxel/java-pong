package pong;

public class Paddle {

	public static final int WIDTH = 13; //how wide the paddle is
	public static final int HEIGHT = 70; //how tall the paddle is

	private int xPos, yPos;
	private int velocity;
	
	//constructor 
	public Paddle(int xPos, int yPos) {
		this.xPos = xPos;
		this.yPos = yPos;
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
		velocity = -2;
	}

	public void moveDown(){
		velocity = 2;
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
