package pusoydos;
//Thread used to set the panel sizes and cards. 
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateDelay extends Thread{
    private GamePanel gp;
    private int delay, turn;
    private Thread dThread;
    
    public UpdateDelay(GamePanel gp, int delay, int turn){
        this.gp = gp;
        this.delay = delay;
        this.turn = turn;
    }
    
    public void start(){
        dThread = new Thread(this);
        dThread.start();
    }
    
    public void run(){
        try {
            Thread.sleep(delay);
            gp.determinePanelToSet(turn);
        } catch (InterruptedException ex) {
            Logger.getLogger(UpdateDelay.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
