package pusoydos;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComputerThread extends Thread{
    private GamePanel gp;
    private GameManager gm;
    private ArrayList<CardImagePanel> playerCards;
    boolean pass;
    
    public ComputerThread(GamePanel gp){
        this.gp = gp;
        this.gm = gp.getGameManager();
        this.playerCards = gp.getPlayerCards();
        pass = false;
    }
    
    @Override
    public void start(){
        System.out.println("Computer Thread start");
        gp.setButtonPanelVisible(false);
        Thread ct = new Thread(this);
        ct.start();
    }
    
    @Override
    public void run(){
        try{
            while(gm.getTurn() != 0 && !gp.getGameStarting() && 
                    gm.getPlayersDone() < 3){
                if(pass){   //Give time for the message panel to finish before this thread continues.
                    Thread.sleep(500);
                }
                int turn = gm.getTurn();
                TextThread t = new TextThread(gp, turn + 1, false); //Display whose turn it is.
                t.start();
                t.join();
                Thread.sleep(3000); //Give time for display message to finish before computer makes its move.
                pass = gm.computerMove();
                if(pass){
                    if(turn == 0){
                        turn = 4;
                    }
                    TextThread t2 = new TextThread(gp, turn + 1, true);
                    t2.start();
                    t2.join();
                    
                }
                Thread.sleep(800);  //Give time for card animation to finish.
                gp.determinePanelToSet(turn);
            }
            if(pass){
                Thread.sleep(1000); //Give time for pass animation to finish.
            }
            if(gm.getPlayersDone() < 3){
                TextThread t = new TextThread(gp, gm.getTurn() + 1, false);
                t.start();
                Thread.sleep(2000);
                gp.setButtonPanelVisible(true);
                for(CardImagePanel card : playerCards){
                    card.addMouseListeners();
                }
                gp.setPassVisibility();
            }
            else{
                System.out.println("Game Done");
                gp.setButtonPanelVisible(false);
                gp.showGameOverMessage();
            }
        }
        catch (InterruptedException ex) {
            Logger.getLogger(ComputerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
