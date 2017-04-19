package pusoydos;

import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;
//Thread used to animate a player moving their cards to the middle of the screen.
public class SingleCardThread extends Thread{
    private GamePanel gp;
    private CardImagePanel cip;
    private Thread sct;
    private int xInc, yInc;
    
    public SingleCardThread(GamePanel gp, CardImagePanel cip, Point start, Point finish){
        this.gp = gp;
        this.cip = new CardImagePanel(gp.getBackCardImage().getCImage(), 52);
        System.out.println(cip);
        this.cip.setPreferredSize(cip.getPreferredSize());
        this.cip.setBounds(start.x, start.y, cip.getPreferredSize().width, cip.getPreferredSize().height);
        gp.add(this.cip);
        xInc = (finish.x - start.x) / 30;
        yInc = (finish.y - start.y) / 30;
    }
    
    public void start(){
        sct = new Thread(this);
        sct.start();
    }

    public void run(){
        try{
            for(int i = 0; i < 30; i++){
                int x = cip.getX() + xInc;
                int y = cip.getY() + yInc;
                cip.setLocation(x, y);
                gp.repaint();
                Thread.sleep(5);
            }
        }
        catch (InterruptedException ex) 
        {
            Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        gp.remove(cip);
        gp.repaint();
        
    }
}
