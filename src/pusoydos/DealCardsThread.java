package pusoydos;

import java.awt.*;
import java.util.concurrent.CountDownLatch;
import javax.swing.*;

public class DealCardsThread extends Thread{
    private Point start, finalPosition;
    private double xInc, yInc;
    private CardImagePanel card;
    private CardImagePanel [] cipArray;
    private GamePanel parent;
    private Thread dealThread;
   
    public DealCardsThread(CardImagePanel card, GamePanel parent, Point start,
            Point finalPosition){
        this.card = card;
        this.parent = parent;
        this.start = start;
        this.finalPosition = finalPosition;
        cipArray = new CardImagePanel[13];
        for(int i = 0; i < cipArray.length; i++){
            cipArray[i] = new CardImagePanel(card.getCImage(), 52);
            cipArray[i].setPreferredSize(cipArray[i].getPreferredSize());
            cipArray[i].setBounds(start.x, start.y, cipArray[i].
                    getPreferredSize().width, cipArray[i].getPreferredSize().
                            height);
        }
        this.xInc = (finalPosition.x - start.x) / 30;
        this.yInc = (finalPosition.y - start.y) / 30;
    }
    
    @Override
    public void start(){
        dealThread = new Thread(this);
        dealThread.start();
    }
    
    @Override
    public void run(){
        try{
            for(int i = 0; i < cipArray.length; i++){
                parent.add(cipArray[i]);
                CardMovementThread cmt = new CardMovementThread(cipArray[i], 
                        parent, finalPosition, xInc, yInc);
                cmt.start();
                Thread.sleep(50);
            }
        }
        catch(InterruptedException ex){
            System.out.println("failed");
        }
        for(int i = 0; i < cipArray.length; i++){
            parent.remove(cipArray[i]);
            cipArray[i] = null;
        }
        cipArray = null;
        card = null;
        parent = null;
    }
    
    
}
