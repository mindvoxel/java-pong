package pong;

import javax.swing.*; //window
import java.awt.*; //painting graphics and images
import java.util.Random; //random number generator
import java.awt.image.BufferedImage; //for double buffering, swing isn't great for animations
import java.awt.event.KeyEvent; //includes all of the constants used for input

import player.Input;
import sound.SoundHandler;

/**Implements the Runnable interface, so Game will be treated as a Thread to be executed
Included in java.lang This class contains all of the game logic and an inner class
for drawing the game.*/
public class Game extends JFrame implements Runnable {

	//constants 
	protected static final int WINDOW_HEIGHT = 450; // the height of the game window
	protected static final int WINDOW_WIDTH = 450; // the width of the game window

	//static vars, placed here instead of Paddle because scores don't necessarily belong to a paddle; didn't want extra bloat code 
	//for score getter, setter, incrementation, etc.
	protected static int leftScore = 0;
	protected static int rightScore = 0;

	private static int behavior_time = 0;

	//instance variables
	private Paddle player1; //player paddle
	private Paddle player2; //enemy paddle
	private Ball ball;
	private Random random; //for generating random integers
	private boolean gameOver = true;

	private Input input; //instance variable for handling input

	//locks for concurrency
	private Object ballLock = new Object(); 
	private Object player1Lock = new Object();
	private Object player2Lock = new Object();

	//each string is a path to an audio resource, which gets passed to custom audio class method for playback
	private String miss;
	private String paddleHit;
	private String wallHit;

	//where execution begins
	public static void main(String[] args){
		new Game(); //create a new game object
	}
	
	//constructor for starting the game
	public Game(){
		//Startup stuff
		initSound();
	    initCanvas();
		//register input to the jFrame, which is polled
	    input = new Input(this); 
		//start the game
		startGameThread();
	} //end constructor, game init.

	/*set up sound strings*/
	public void initSound(){
		 this.miss = "../sounds/miss.wav";
		 this.paddleHit = "../sounds/paddle_hit.wav";
		 this.wallHit = "../sounds/wall_hit.wav";
		 //create a new sound Handler object
	}

	//Set up Canvas which is a child of Component and add it to (this) JFrame
	public void initCanvas(){
	    Canvas myCanvas = new Canvas();
		myCanvas.setFocusable(true);

		//housekeeping for window stuff
		setLayout(new GridLayout());
		setTitle("Java Pong");
		setVisible(true);
		setSize(WINDOW_HEIGHT, WINDOW_WIDTH);
		setVisible(true);

		//if the window is not resizeable the window does not open on certain linux machines
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(myCanvas);
		
		//request focus so the JFrame is getting the input, for sure
		requestFocus();
	}

	//Starts the Thread which runs the game loop and various processing/updates
	public void startGameThread(){
		//set the game start running
		Thread gameThread = new Thread(this,  "New Game Thread");
		try{
			//waits for this current thread to die before beginning execution
			gameThread.join();
		//most exceptions are contained in java.lang
		}catch(InterruptedException ex){
			ex.printStackTrace();
		}
		//actually run the game
		gameThread.start();
	}

	/*main game loop
	this is run when the gameThread.start() is run*/
	public void run(){
		//random object for creating a ball in a random position
		random = new Random();
		
		/*instantiate all of the game objects, once*/
		player1 = new Paddle(Paddle.WIDTH, WINDOW_HEIGHT / 2);
		player2 = new Paddle(WINDOW_WIDTH - Paddle.WIDTH, WINDOW_HEIGHT /2);
		synchronized (ballLock) { // We will create the ball - make sure it doesn't get painted at the same time
			ball = new Ball(WINDOW_WIDTH / 2, random.nextInt(150) + 150,
					(random.nextInt(120) + 120) * (Math.PI / 180.0));
		}
		
		/*Game loop should always be running*/
		while (true){
			updateInput(); //Also includes check for reset (enter) in case of gameOver mode
			//System.out.println(Thread.activeCount());//print the number of threads currently running
			try{
		        Thread.sleep(5);//tells the game how often to refresh
			}catch (Exception ex){
				System.out.println("Couldn't sleep for some reason.");	
			}
			if (gameOver == false){
				doplayer2Behavior(); //ai
				player1.update(); //update the player object (check the bounds and update the position)
				player2.update(); //update other paddle
				ball.updateBall(); //update the ball object
				destroyBall(); //point ball to null if it goes behind paddle (and creates a new one)
				doCollision(); //checks for collisions between padles and ball
				checkWallBounce(); //for playing the wall sounds
				gameOver();
				repaint(); // repaint component (draw event in gamemaker)

			} else{ //Game Over, man! 
				ball.updateBall();
				checkWallBounce();
				repaint();
			}
		} //end while
	}//end run

	//polls the player input
	public void updateInput(){
		if (!gameOver){
			if (input.isKeyDown(KeyEvent.VK_UP)){
				player1.moveUp();
				//System.out.println("UP");
			}
			if (input.isKeyDown(KeyEvent.VK_DOWN)){
				player1.moveDown();
				//System.out.println("DOWN");
			}
		}
		if (gameOver && input.isKeyDown(KeyEvent.VK_ENTER)){
			leftScore = 0;
			rightScore = 0;
			gameOver = false;
			player1 = new Paddle(25, WINDOW_HEIGHT / 2);
			player2 = new Paddle(WINDOW_WIDTH - 50, WINDOW_HEIGHT /2);
		}
	}

	/*points the ball to null if it goes behind
	/either of the paddles*/
	public void destroyBall(){
		if (ball.isDestroyable()){
			SoundHandler.playSound(miss);
			synchronized (ballLock) { // We will delete the ball - make sure it doesn't get painted at the same time
				ball = null;
			}
			/*creates the ball in the middle of the screen*/
			int ball_rand = random.nextInt(120); 
			/*a ball_rand of 0 will create a ball that bounces vertically, forever */ 
			while (ball_rand == 0){
			      ball_rand = random.nextInt(120);
			}

			//System.out.println("ball seed " + ball_rand);
			synchronized (ballLock) { // We will create the ball - make sure it doesn't get painted at the same time
				ball = new Ball(WINDOW_WIDTH / 2, ball_rand + 120, (ball_rand + 120) * (Math.PI / 180));
			}
		}
	}

    //This method contains the AI for the other paddle. Behavior time may be different depending on platoform.
    public void doplayer2Behavior() {
		behavior_time++;
		  //progressively improves the AI based on total score
      if (player2.getXPos() - ball.getXPos() < 50 + ((leftScore + rightScore) * 10)) { 
        if (ball.getYPos() > player2.getYPos()) {
          // System.out.println("AI UP");
          player2.moveDown();
        } else if (ball.getYPos() < player2.getYPos()) {
          // System.out.println("AI DOWN");
          player2.moveUp();
        } else if (ball.getYPos() == player2.getYPos()) {
          // System.out.println("AI STOP");
          player2.stop();
        }
      }else{
        if (behavior_time > 50){
           behavior_time = 0;
           //will either generate a 0, 1, or 2
           int choice = random.nextInt(3);
           //System.out.println(choice);
           switch(choice){
             case 0: player2.moveDown(); break;
             case 1: player2.moveUp(); break;
           }
          }//end if behavior time
        //System.out.println(behavior_time);
      }//end else	
	  }//end function

	//for playing the wall sounds, else-if because don't want any sounds to play or wall collision behavior to happen simultaneously
	public void checkWallBounce(){
		if ((ball.getYPos() >= (WINDOW_HEIGHT - (6 * Ball.RADIUS))) || (ball.getYPos() <= 0)){
			//System.out.println("Top or bottom \'wall\' was hit");
			SoundHandler.playSound(wallHit); //do this regardless of whether the game is over or not
		}else if (ball.getXPos() == (WINDOW_WIDTH - (4 * Ball.RADIUS))){ 
			if (gameOver){
				SoundHandler.playSound(wallHit);
			}else{
				SoundHandler.playSound(miss); //only play out-of-bounds x misses if there is a game in progress
				leftScore++;
			}
		}else if(ball.getXPos() == 0){
			if (gameOver){
				SoundHandler.playSound(wallHit);
			}else{
				SoundHandler.playSound(miss); 
				rightScore++;
			}
		}
	}

	//Check for the moment where the paddles and the ball collide
	public void doCollision(){
		//left paddle collision
		for (int colY =  player1.getYPos(); colY <  player1.getYPos() + Paddle.HEIGHT; colY++){
			if (  ball.getXPos() ==  player1.getXPos() &&   ball.getYPos() + Ball.RADIUS == colY){
				ball.reverseXVelocity();
				SoundHandler.playSound(paddleHit);
				ball.setYVelocity(player1.getVelocity());
				//System.out.println("COLLISION");
			}
		}

		//right paddle collision
		for (int colY =  player2.getYPos(); colY <  player2.getYPos() + Paddle.HEIGHT; colY++){
			if (ball.getXPos() ==  player2.getXPos() - Paddle.WIDTH &&  ball.getYPos() + Ball.RADIUS == colY){
				ball.reverseXVelocity();
				SoundHandler.playSound(paddleHit);
				//System.out.println("COLLISION");
				ball.setYVelocity(player1.getVelocity());
			}
		}
	}

	/*checks whether or not either of the paddles have scored 7 points -- if they have
	/then destroy the paddles and restart the game.*/
	public void gameOver(){
		if((leftScore >= 7 || rightScore >= 7) &&  gameOver == false){
			gameOver = true;
			player1 = null;
			player2 = null;
		}
	}
	//Nested class
	private class Canvas extends JPanel{

		public void paint(Graphics g){
			//weird swing graphics housekeeping
			Graphics2D g2 = (Graphics2D) g;

			//drawing the 'sprites' for the game
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, WINDOW_HEIGHT, WINDOW_WIDTH); // fill the whole screen black
			g2.setColor(Color.WHITE);

			//only draw the paddles when there is still a game in progress, and don't attempt to draw paddles when they are null
			if (gameOver == false && player1 != null && player2 != null){
				synchronized(player1Lock){
					g2.fillRect( player1.getXPos(),  player1.getYPos(), Paddle.WIDTH, Paddle.HEIGHT); // draw player paddle
				}
				synchronized(player2Lock){
					g2.fillRect( player2.getXPos(),  player2.getYPos(), Paddle.WIDTH, Paddle.HEIGHT); // draw computer paddle
				}	
			}else{
				g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
				g2.drawString("Press the 'enter' key to start a new game.", 55, WINDOW_HEIGHT - 100);
			}

			synchronized (ballLock) { // Wait until nothing else is creating/deleting the ball
				if (ball != null) {
					g2.fillOval(ball.getXPos(), ball.getYPos(), Ball.RADIUS * 2, Ball.RADIUS * 2);
				}
			}

			for (int i =0; i < WINDOW_WIDTH; i+=10){ //dotted line
				g2.drawLine(WINDOW_WIDTH/2,i,WINDOW_WIDTH/2,i +5);
			}

			g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 72));
			g2.drawString("" + leftScore, WINDOW_WIDTH/2 -150, 100);
			g2.drawString("" + rightScore, WINDOW_WIDTH/2 + 100, 100);

		}//end paint method
	}//end Canvas nested class
}//end Game class
