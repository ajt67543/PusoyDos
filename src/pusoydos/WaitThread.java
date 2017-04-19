package pusoydos;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WaitThread extends Thread{
    private CardImagePanel placeHolder;
    private GamePanel parent;
    private boolean turnOffButtons, start;
    private int waitTime;
    
    public WaitThread(CardImagePanel placeHolder, GamePanel parent, int 
            waitTime, boolean turnOffButtons, boolean start){
        this.placeHolder = placeHolder;
        this.parent = parent;
        this.waitTime = waitTime;
        this.turnOffButtons = turnOffButtons;
        this.start = start;
    }
    
    @Override
    public void start(){
        Thread wait = new Thread(this);
        wait.start();
    }
    
    @Override
    public void run(){
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException ex) {
            Logger.getLogger(GamePanel.class.getName()).log(Level.
                    SEVERE, null, ex);
        }
        parent.remove(placeHolder);
        placeHolder = null;
        parent.turnOnPanels();
        parent.setButtons();
        parent.setPanelSizes(true, true, true, true);
        parent.setPassVisibility();
        if(parent.getGameManager().getTurn() != 0 && start){
            ComputerThread ct = new ComputerThread(parent);
            ct.start();
        }
        else if(start){
            TextThread t = new TextThread(parent,
                    parent.getGameManager().getTurn() + 1, false);
            t.start();
        }
    }
}
