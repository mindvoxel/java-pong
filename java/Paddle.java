//Created by Christopher Wolff
public class Paddle {

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
		if (yPos > Game.GAME_HEIGHT - 110){
			yPos = Game.GAME_HEIGHT - 110;
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
	public int getxPos() {
		return xPos;
	}
	
	//getter
	public int getyPos() {
		return yPos;
	}

	public int getVelocity(){
		return velocity;
	}
	
	//setter
	private void setyPos(int yPos) {
		this.yPos = yPos;
	}

}
