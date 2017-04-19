package pusoydos;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class CardMovementThread extends Thread{
    private GamePanel parent;
    private CardImagePanel panel;
    private Point finalPosition;
    private double xInc, yInc;
    private Thread t;
    
    CardMovementThread(CardImagePanel panel, GamePanel parent, Point
            finalPosition, double xInc, double yInc){
        
        this.panel = panel;
        this.parent = parent;
        this.finalPosition = finalPosition;
        this.xInc = xInc;
        this.yInc = yInc;
    }
    @Override
    public void start(){
        t = new Thread(this);
        t.start();
    }
    
    @Override
    public void run(){
        try{
            for(int i = 0; i < 30; i++){
                int x = panel.getX() + (int) xInc;
                int y = panel.getY() + (int) yInc;
                panel.setLocation(x, y);
                parent.repaint();
                Thread.sleep(10);
            }
        } 
        catch(InterruptedException ex){
            System.out.println("failed");
        }
        parent.remove(panel);
        panel = null;
    }
}
