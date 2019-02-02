package sound;

//confusing audio imports
import javax.sound.sampled.*;

//for audio files
import java.io.File;
import java.io.FileNotFoundException;

/**Sound Handler gets its own seperate Thread */ 
public class SoundHandler implements Runnable{

    //for playing sound files, sound is the path to the file 
	public void playSound(String sound){
		Clip clip = null;
		try {
            System.out.println("Working Directory = " + System.getProperty("user.dir"));
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(sound)); 
			//System.out.println("input stream reference is: " + inputStream);
			AudioFormat format = inputStream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(inputStream); //think this creates a new thread for every sound
			clip.start();
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
			//clip.stop();
		}

    }


    public void run(){
        //playSound();
    }

}