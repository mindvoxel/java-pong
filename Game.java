/* a pong clone created by Christopher Wolff */

import javax.swing.*; //window
import java.awt.*;
import java.util.Random; //random number generator
import java.awt.image.BufferedImage; // for double buffering
import java.awt.event.KeyEvent; //includes all of the constants used for input
import java.io.File; //for loading .wav files
import javax.sound.sampled.AudioSystem; //for playing sounds
import javax.sound.sampled.Clip; //for playing sounds

public class Game extends JFrame implements Runnable {

	//constants
	public static final int GAME_HEIGHT = 450; // the height of the game window
	public static final int GAME_WIDTH = 450; // the width of the game window
	public static final int PADDLE_WIDTH = 13; //how wide the paddle is
	public static final int PADDLE_HEIGHT = 70; //how tall the paddle is
	public static final int BALL_RADIUS = 10; //size of the Ball
	
	//static vars
	public static int LEFT_SCORE = 0;
	public static int RIGHT_SCORE = 0;
	
	//instance variables
	private boolean running; //controlling whether or not the game is running
	private BufferedImage myBuff; //for making the game double buffered
	private PlayerPaddle p1; //player paddle
	private PlayerPaddle p2; //enemy paddle
	private Ball b; //ball 
	private Input gameInput; //instance variable for handling input
	boolean gameOver = false;
	
	//file instance variables
	private File miss;
	private File paddle_hit;
	private File wall_hit;

	//where execution begins
	public static void main(String[] args){
		new Game(); //create a new game object
	}
	
	//constructor for starting the game
	public Game(){
		//want the game to start running
		running = true;
		
		//set up sound files
		 miss = new File("miss.wav");
		 paddle_hit = new File("paddle_hit.wav");
		 wall_hit = new File("wall_hit.wav");
		
		//set up the double buffer
		myBuff = new BufferedImage(GAME_HEIGHT, GAME_WIDTH, BufferedImage.TYPE_INT_RGB); 
		
		//random object for doing random ball stuff
		Random ballSeed = new Random();

		//sets up the canvas which is a subclass of component
		Canvas myCanvas = new Canvas();
		myCanvas.setFocusable(true);
		
		//housekeeping for window stuff
		setLayout(new GridLayout());
		setTitle("Rolo Pong");
		setVisible(true);
		setSize(GAME_HEIGHT, GAME_WIDTH);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(myCanvas);
		gameInput = new Input(this); //register input to the jFrame, which is polled
		
		//makes the game start running
		Thread gameThread = new Thread(this);
		
		gameThread.start();
		p1 = new PlayerPaddle(25, GAME_HEIGHT / 2);
		p2 = new PlayerPaddle(GAME_WIDTH - 50, GAME_HEIGHT /2);
		b = new Ball(GAME_WIDTH/2, ballSeed.nextInt(150) + 150,  (ballSeed.nextInt(120) + 120) * (Math.PI / 180.0));
		
		
	} //end constructor, game init. 
	
	//for playing sound files
	public void playSound(File sound){
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(sound));
			clip.start();
		}catch(Exception ex){
			System.out.println("PROBLEM WITH THE CLIP");
		}
	}
	
	//main game loop
	//this is run when the Thread.start() is run
	public void run(){
		while (running){
			updateInput(); //if put inside the try then there is a chance user input won't be polled
			try {
				if (gameOver == false){
					Thread.sleep(5);//tells the game how often to refresh
					doP2Behavior(); //ai
					p1.update(); //update the player object (check the bounds and update the position)
					p2.update(); //update other paddle
					b.updateBall(); //update the ball object
					destroyBall(); //point ball to null if it goes behind paddle (and creates a new one)
					doCollision(); //checks for collisions between padles and ball
					checkWallBounce(); //for playing the wall sounds
					gameOver();
					repaint(); // repaint component (draw event in gamemaker)
				} else{
					Thread.sleep(5);
					b.updateBall();
					checkWallBounce();
					gameOver();
					repaint();
				}
			}
			
			//if the thread is interrupted
			catch (InterruptedException ex){
				ex.printStackTrace();
			}

		} //end while

	} //end run
	
	//polls the player input
	public void updateInput(){
		if (!gameOver){
			if (gameInput.isKeyDown(KeyEvent.VK_UP)){
				p1.moveUp();
				//System.out.println("UP");
			}
			if (gameInput.isKeyDown(KeyEvent.VK_DOWN)){
				p1.moveDown();
				//System.out.println("DOWN");
			}
		}
		
		if (gameOver && gameInput.isKeyDown(KeyEvent.VK_ENTER)){
			LEFT_SCORE =0;
			RIGHT_SCORE =0;
			gameOver = false;
			p1 = new PlayerPaddle(25, GAME_HEIGHT / 2);
			p2 = new PlayerPaddle(GAME_WIDTH - 50, GAME_HEIGHT /2);
		}
	}
	
	//points the ball to null if it goes behind
	//either of the paddles
	public void destroyBall(){
		Random ballSeed = new Random();
		if (b.isDestroyable()){
			playSound(miss);
			b = null;
			b = new Ball(GAME_WIDTH/2, ballSeed.nextInt(120) + 120,  (ballSeed.nextInt(120) + 120 ) * (Math.PI / 180.0));
		}
	}
	
	public void doP2Behavior(){
		System.out.println("ball y:" + b.getyPos() +  "paddle y: " + p2.getyPos());
		if(b.getyPos() > p2.getyPos()){
			//System.out.println("AI UP");
			p2.moveDown();
		} else if (b.getyPos() < p2.getyPos()) {
			//System.out.println("AI DOWN");
			p2.moveUp();
		} else if (b.getyPos() == p2.getyPos()) {
			//System.out.println("AI STOP");
			p2.stop();
		}
	}
	
	//for playing the wall sounds
	public void checkWallBounce(){
		if (b.getyPos() > Game.GAME_HEIGHT - (6 * Game.BALL_RADIUS)){
			playSound(wall_hit);
		}  
		if (b.getyPos() < 0){
			playSound(wall_hit);
		}
		
		if (b.getxPos() == GAME_WIDTH -(4 * Game.BALL_RADIUS)){
			playSound(wall_hit);
			if (gameOver == false){
				LEFT_SCORE++;
			}
		}
		
		if (b.getxPos() == 0){
			playSound(wall_hit);
			if (gameOver == false){
				RIGHT_SCORE++;
			}
		}
	}
	
	public void doCollision(){
		//print the position of the left paddle and the ball
		//System.out.println(p1 + " x: " + p1.getxPos() + " y: " + p1.getyPos());
		//System.out.println(b + " x: " +  b.getxPos() + " y: " +  b.getyPos());
		
		//left paddle collision
		for (int colY =  p1.getyPos(); colY <  p1.getyPos() + PADDLE_HEIGHT; colY++){
			if (  b.getxPos() ==  p1.getxPos() &&   b.getyPos() + BALL_RADIUS == colY){
				b.changeX();
				playSound(paddle_hit);
				b.setYVelocity(p1.getVelocity());
				System.out.println("COLLISION");
			}
		}

		//right paddle collision
		for (int colY =  p2.getyPos(); colY <  p2.getyPos() + PADDLE_HEIGHT; colY++){
			if (  b.getxPos() ==  p2.getxPos() - PADDLE_WIDTH &&   b.getyPos() + BALL_RADIUS == colY){
				b.changeX();
				playSound(paddle_hit);
				System.out.println("COLLISION");
				b.setYVelocity(p1.getVelocity());
			}
		}	
		
		
	}
	
	public void gameOver(){
			if((LEFT_SCORE >= 7 || RIGHT_SCORE >= 7) &&  gameOver == false){
				gameOver = true;
				p1 = null;
				p2 = null;
			}
	}

	private class Canvas extends JPanel{

		public void paint(Graphics g){
			//weird graphics housekeeping
			Graphics2D g2 = (Graphics2D) g;
			
			//drawing the 'sprites' for the game
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, GAME_HEIGHT, GAME_WIDTH); // fill the whole screen black
			g2.setColor(Color.WHITE); 
			if (gameOver == false){
				g2.fillRect(  p1.getxPos(),  p1.getyPos(), PADDLE_WIDTH, PADDLE_HEIGHT); // draw player paddle
				g2.fillRect( p2.getxPos(),  p2.getyPos(), PADDLE_WIDTH, PADDLE_HEIGHT); // draw computer paddle
			}
			g2.fillOval(  b.getxPos(),  b.getyPos(), BALL_RADIUS * 2, BALL_RADIUS * 2);
			for (int i =0; i < GAME_WIDTH; i+=10){ //dotted line 
				g2.drawLine(GAME_WIDTH/2,i,GAME_WIDTH/2,i +5);
			}
			g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 72));
			g2.drawString("" + LEFT_SCORE, GAME_WIDTH/2 -150, 100);
			g2.drawString("" + RIGHT_SCORE, GAME_WIDTH/2 + 100, 100);
			
			//double buffering
			Graphics2D back_buffer_drawer = (Graphics2D) myBuff.getGraphics(); 
			
			//Drawing the 'sprites' for the game (to the back bufer
			back_buffer_drawer.setColor(Color.BLACK);
			back_buffer_drawer.fillRect(0, 0, GAME_HEIGHT, GAME_WIDTH); // fill the whole screen black
			back_buffer_drawer.setColor(Color.WHITE); 
			if (gameOver == false){
				back_buffer_drawer.fillRect(  p1.getxPos(),  p1.getyPos(), PADDLE_WIDTH, PADDLE_HEIGHT); // draw player paddle
				back_buffer_drawer.fillRect( p2.getxPos(),  p2.getyPos(), PADDLE_WIDTH, PADDLE_HEIGHT); // draw computer paddle
			}
			back_buffer_drawer.fillOval(  b.getxPos(),  b.getyPos(), BALL_RADIUS * 2, BALL_RADIUS * 2);
			for (int i =0; i < GAME_WIDTH; i+=10){ //dotted line 
				back_buffer_drawer.drawLine(GAME_WIDTH/2,i,GAME_WIDTH/2,i +5);
			}
			back_buffer_drawer.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 72));
			back_buffer_drawer.drawString("" + LEFT_SCORE, GAME_WIDTH/2 - 150, 100);
			back_buffer_drawer.drawString("" + RIGHT_SCORE, GAME_WIDTH/2 + 100, 100);
			
			//draw the back buffer to the screen
			g2.drawImage(myBuff, 0, 0, this); 

		}

	}

}
