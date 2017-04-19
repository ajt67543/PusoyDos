package pusoydos;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

public class TextThread extends Thread{
    private GamePanel gp;
    private int xInc;
    private Thread t;
    private JPanel messagePanel;
    private boolean pass;
    
    public TextThread(GamePanel gp, int turn, boolean pass){
        this.gp = gp;
        this.xInc = (gp.getScreenSize().width - gp.getMessagePanelX()) / 20;
        String message;
        if(turn == 1){
            if(gp.getGameManager().getControl()){
                message = "You are in Control";
            }
            else{
                message = "Your turn";
            }
        }
        else if(pass){
            message = "Player " + turn + " " + "passed";
        }
        else if(gp.getGameManager().getControl()){
            message = "Player " + turn + " is in Control";
        }
        else{
            message = "Player " + turn + "'s " + "turn";
        }
        gp.getMessagePane().setText(message);
        this.messagePanel = gp.getMessagePanel();
        messagePanel.setBounds(gp.getScreenSize().width, messagePanel.getY(), 
                messagePanel.getPreferredSize().width, 
                messagePanel.getPreferredSize().height);
        messagePanel.setVisible(true);
        this.pass = pass;
    }
    
    public TextThread(GamePanel gp, String gameOver){
        this.gp = gp;
        messagePanel = this.gp.getMessagePanel();
        this.xInc = (gp.getScreenSize().width - gp.getMessagePanelX()) / 20;
        messagePanel.setBounds(gp.getScreenSize().width, messagePanel.getY(), 
                messagePanel.getPreferredSize().width, 
                messagePanel.getPreferredSize().height);
        gp.getMessagePane().setText(gameOver);
        messagePanel.setVisible(true);
        this.pass = false;
    }
    
    public void start(){
        t = new Thread(this);
        t.start();
                
    }
    
    public void run(){
        try{
            if(!pass){
                Thread.sleep(700);
            }
            for(int i = 0; i < 20; i++){
                int x = messagePanel.getX() - xInc;
                int y = messagePanel.getY();
                messagePanel.setLocation(x, y);
                Thread.sleep(10);
            }
            Thread.sleep(700);
            for(int i = 0; i < 30; i++){
                int x = messagePanel.getX() - xInc;
                int y = messagePanel.getY();
                messagePanel.setLocation(x, y);
                Thread.sleep(10);
            }
        }
        catch (InterruptedException ex) {
            Logger.getLogger(TextThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        messagePanel.setVisible(false);
        System.out.println("Thread end");
        
    }
}
