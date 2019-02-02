package sound;

//confusing audio imports
import javax.sound.sampled.*;

//for audio files
import java.io.File;
import java.io.FileNotFoundException;

/*Sound Handler may get its own seperate thread in the future, but may be unnecessay because
Clip or AudioSystem creates a new thread for */ 
public final class SoundHandler{

    //for playing sound files, sound is the path to the file 
	public static void playSound(String sound){
		Clip clip = null;
        AudioInputStream inputStream = null;
		try {
            //System.out.println("Working Directory = " + System.getProperty("user.dir"));
			inputStream = AudioSystem.getAudioInputStream(new File(sound)); 
			//System.out.println("input stream reference is: " + inputStream);
			AudioFormat format = inputStream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(inputStream); //think this creates a new thread for every sound that is played
			clip.start();
            inputStream.close();
        }
        catch(FileNotFoundException ex){
            System.out.println("Could not find file!");
            ex.printStackTrace();
        }catch(LineUnavailableException ex){
			System.out.println("Audio competed for resources!");
		}catch(Exception ex){
			System.out.println("Something went wrong with audio clip!");
			ex.printStackTrace();
		}finally{
            /*sauce: https://stackoverflow.com/questions/837974/determine-when-to-close-a-sound-playing-thread-in-java
            Duct-tape solution.*/
			clip.addLineListener(new LineListener() {
                public void update(LineEvent evt) {
                    if (evt.getType() == LineEvent.Type.STOP) {
                    evt.getLine().close(); //stop the thread playing the sound once it stops
                    }//end if
                }//end update
            });//end inner class
        }//end finally block
    }//end sound function
}