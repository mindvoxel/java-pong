/* a pong clone created by Christopher Wolff */

import javax.swing.*; //window
import java.awt.*; //painting graphics and images
import java.util.Random; //random number generator
import java.awt.image.BufferedImage; // for double buffering
import java.awt.event.KeyEvent; //includes all of the constants used for input
import java.io.File; //for loading .wav files. Dependant on the current working directory. ie. what happens when pwd is typed in a shell.
import javax.sound.sampled.AudioSystem; //for playing sounds
import javax.sound.sampled.Clip; //for playing sounds
import javax.sound.sampled.LineUnavailableException; //audio exception

//imports below used to test exceptions on ubuntu
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.AudioFormat;

/*Implements the Runnable interface, so Game will be treated as a Thread to be executed
Included in java.lang*/
public class Game extends JFrame implements Runnable {

	//constants (can't access non-static from a static context?)
	public static final int GAME_HEIGHT = 450; // the height of the game window
	public static final int GAME_WIDTH = 450; // the width of the game window
	public static final int PADDLE_WIDTH = 13; //how wide the paddle is
	public static final int PADDLE_HEIGHT = 70; //how tall the paddle is
	public static final int BALL_RADIUS = 10; //size of the Ball

	//static vars
	public static int left_score = 0;
	public static int right_score = 0;

	//instance variables
	private BufferedImage myBuff; //for making the game double buffered
	private Paddle p1; //player paddle
	private Paddle p2; //enemy paddle
	private Ball b;
        private Random ballRand; //for random number generator
	private Input gameInput; //instance variable for handling input
	boolean gameOver = false;

	//file instance variables (sounds -- all graphics are rendered with Java)
	//changed the vars to String rather than File.
	private String miss;
	private String paddle_hit;
	private String wall_hit;

	//where execution begins
	public static void main(String[] args){
		new Game(); //create a new game object
	}
	
	//constructor for starting the game
	public Game(){
		/*Startup stuff*/
		initSound();
	        initCanvas();
		//set up the double buffer
		myBuff = new BufferedImage(GAME_HEIGHT, GAME_WIDTH, BufferedImage.TYPE_INT_RGB);
		//register input to the jFrame, which is polled
	        gameInput = new Input(this); 

		//start the game
		startGameThread();
		
	} //end constructor, game init.

	public void initSound(){
		//set up sound files (. can be used to specify the relative path)
		//setting sounds as string for path instead of File
		 this.miss = "./sounds/miss.wav";
		 this.paddle_hit = "./sounds/paddle_hit.wav";
		 this.wall_hit = "./sounds/wall_hit.wav";
	}

	/*Set up Canvas which is a child of Component and add it to (this) JFrame*/
	public void initCanvas(){
	        Canvas myCanvas = new Canvas();
		myCanvas.setFocusable(true);

		//housekeeping for window stuff
		setLayout(new GridLayout());
		setTitle("Rolo Pong");
		setVisible(true);
		setSize(GAME_HEIGHT, GAME_WIDTH);
		setVisible(true);

		//if the window is not resizeable the window does not open on certain linux machines
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(myCanvas);
		
		//request focus so the JFrame is getting the input, for sure
		requestFocus();
	}

	/*Starts the Thread which runs the game loop and various processing/updates*/
	public void startGameThread(){
		//set the game start running
		Thread gameThread = new Thread(this);
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

	
	//for playing sound files
	public void playSound(String sound){
		try {
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(this.getClass().getResource(sound));
           		AudioFormat format = inputStream.getFormat();
            		DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip clip = (Clip)AudioSystem.getLine(info);
            		clip.open(inputStream);
            		clip.start();
		//if the audio clips compete for resources
		}catch(LineUnavailableException ex){
			System.out.println("handled strange audio exception");
		}catch(Exception ex){
			System.out.println("general clip problem");
			ex.printStackTrace();
		}
	}

	//main game loop
	//this is run when the Thread.start() is run
	public void run(){
		//random object for creating a ball in a random position
		ballRand = new Random();

                /*instantiate all of the game objects, once*/
		p1 = new Paddle(25, GAME_HEIGHT / 2);
		p2 = new Paddle(GAME_WIDTH - 50, GAME_HEIGHT /2);
		b = new Ball(GAME_WIDTH/2, ballRand.nextInt(150) + 150,  (ballRand.nextInt(120) + 120) * (Math.PI / 180.0));
		
		/*Game loop should always be running*/
		while (true){
			updateInput(); //Also includes check for reset (enter) in case of gameOver mode
			try{
		        	Thread.sleep(5);//tells the game how often to refresh
			}catch (Exception ex){
				System.out.println("Couldn't sleep for some reason.");	
			}
			if (gameOver == false){
				doP2Behavior(); //ai
				p1.update(); //update the player object (check the bounds and update the position)
				p2.update(); //update other paddle
				b.updateBall(); //update the ball object
				destroyBall(); //point ball to null if it goes behind paddle (and creates a new one)
				doCollision(); //checks for collisions between padles and ball
				checkWallBounce(); //for playing the wall sounds
				gameOver();
				repaint(); // repaint component (draw event in gamemaker)

			} else{ //Game Over, man! 
				b.updateBall();
				checkWallBounce();
				gameOver();
				repaint();
			}
		} //end while
	}//end run

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
			left_score =0;
			right_score =0;
			gameOver = false;
			p1 = new Paddle(25, GAME_HEIGHT / 2);
			p2 = new Paddle(GAME_WIDTH - 50, GAME_HEIGHT /2);
		}
	}

	/*points the ball to null if it goes behind
	/either of the paddles*/
	public void destroyBall(){
		Random ballRand = new Random();
		if (b.isDestroyable()){
			playSound(miss);
			b = null;
			/*creates the ball in the middle of the screen*/
			b = new Ball(GAME_WIDTH/2, ballRand.nextInt(120) + 120,  (ballRand.nextInt(120) + 120 ) * (Math.PI / 180.0));
		}
	}

    //This method contains the AI for the other paddle
    public void doP2Behavior() {
        //System.out.println("ball y: " + b.getyPos() + " paddle y: " + p2.getyPos());
		if (p2.getxPos() - b.getxPos() < 150) { // delays the AI reaction time
			if (b.getyPos() > p2.getyPos()) {
				// System.out.println("AI UP");
				p2.moveDown();
			} else if (b.getyPos() < p2.getyPos()) {
				// System.out.println("AI DOWN");
				p2.moveUp();
			} else if (b.getyPos() == p2.getyPos()) {
				// System.out.println("AI STOP");
				p2.stop();
			}
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
				left_score++;
			}
		}

		if (b.getxPos() == 0){
			playSound(wall_hit);
			if (gameOver == false){
				right_score++;
			}
		}
	}

        //Check for the moment where the paddles and the ball collide
	public void doCollision(){
		//left paddle collision
		for (int colY =  p1.getyPos(); colY <  p1.getyPos() + PADDLE_HEIGHT; colY++){
			if (  b.getxPos() ==  p1.getxPos() &&   b.getyPos() + BALL_RADIUS == colY){
				b.changeX();
				playSound(paddle_hit);
				b.setYVelocity(p1.getVelocity());
				//System.out.println("COLLISION");
			}
		}

		//right paddle collision
		for (int colY =  p2.getyPos(); colY <  p2.getyPos() + PADDLE_HEIGHT; colY++){
			if (  b.getxPos() ==  p2.getxPos() - PADDLE_WIDTH &&   b.getyPos() + BALL_RADIUS == colY){
				b.changeX();
				playSound(paddle_hit);
				//System.out.println("COLLISION");
				b.setYVelocity(p1.getVelocity());
			}
		}
	}

        /*checks whether or not either of the paddles have scored 7 points -- if they have
        /then destroy the paddles and restart the game.*/
	public void gameOver(){
			if((left_score >= 7 || right_score >= 7) &&  gameOver == false){
				gameOver = true;
				p1 = null;
				p2 = null;
			}
	}
	//Nested class
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
			g2.drawString("" + left_score, GAME_WIDTH/2 -150, 100);
			g2.drawString("" + right_score, GAME_WIDTH/2 + 100, 100);

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
			back_buffer_drawer.drawString("" + left_score, GAME_WIDTH/2 - 150, 100);
			back_buffer_drawer.drawString("" + right_score, GAME_WIDTH/2 + 100, 100);

			//draw the back buffer to the screen
			g2.drawImage(myBuff, 0, 0, this);
		}//end paint method
	}//end Canvas nested class
}//end Game class
