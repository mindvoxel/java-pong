import java.awt.Component;
import java.awt.event.KeyListener; //interface for overriding and defining behavior
import java.awt.event.KeyEvent; //includes all of the constants used for input


public class Input implements KeyListener{
	
	private boolean[] keys; 

	//constructor for assigning listener to 
	//jframe component
	public Input(Component c){
		c.addKeyListener(this);
		keys = new boolean[256]; 
	}

	@Override
	public void keyPressed(KeyEvent key) {
		if (key.getKeyCode() > 0 && key.getKeyCode() < 256){
			keys[key.getKeyCode()] = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent key) {
		if (key.getKeyCode() > 0 && key.getKeyCode() < 256){
			keys[key.getKeyCode()] = false;
		}
	}
	
	@Override 
	public void keyTyped(KeyEvent key){
		
	}
	
	//custom function for input polling
	 public boolean isKeyDown(int keyNum){
		 if (keyNum >0 && keyNum < 256){
			return keys[keyNum];
		 }
		 return false;
	 }
	
}