package pusoydos;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;


public class PusoyDos extends JFrame {

    private JFrame frame;
    private JMenuBar mb;
    private Dimension screenSize;
    private HelpWindow hw;
    private StatsWindow sw;
    private GamePanel gp;
    private int[] stats = new int[4];

    public PusoyDos(){
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        resetStats();
        setFrameParams();
    }
    
    public void setStats(int index){
        stats[index]++;
    }
    
    private void setFrameParams() {
        frame = new JFrame("PUSOY DOS");
        frame.setMinimumSize(screenSize);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        setMenuBar();
        gp = new GamePanel(frame, this);
        hw = new HelpWindow(frame, "How To Play", screenSize);
        sw = new StatsWindow(frame, "Statistics", screenSize);
        frame.setVisible(true);
        frame.add(gp);
    }

    void setMenuBar() {
        mb = new JMenuBar();
        mb.add(playBar());
        mb.add(optionBar());
        frame.setJMenuBar(mb);
    }
    
    private JMenu playBar(){
        JMenu play = new JMenu("Play");
        play.setFont(new Font("", Font.BOLD, 20));
        JMenuItem startGame = new JMenuItem(" New Game");
        startGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae){
                try {
                    gp.playGame();
                } catch (InterruptedException ex) {
                    Logger.getLogger(PusoyDos.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        JMenuItem scoreBoard = new JMenuItem(" Scoreboard");
        scoreBoard.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
                sw.setStats(stats);
                sw.setVisible(true);
            }
        });
        JMenuItem resetStats = new JMenuItem(" Reset Scoreboard");
        resetStats.addActionListener(new ActionListener(){
            @Override 
            public void actionPerformed(ActionEvent ae){
                resetStats();
            }
        });
        startGame.setFont(new Font("", Font.BOLD, 20));
        scoreBoard.setFont(new Font("", Font.BOLD, 20));
        resetStats.setFont(new Font("", Font.BOLD, 20));
        play.add(startGame);
        play.add(scoreBoard);
        play.add(resetStats);
        return play;
    }
    
    private JMenu optionBar(){
        JMenu options = new JMenu("Options");
        options.setFont(new Font("", Font.BOLD, 20));
        JMenuItem instructions = new JMenuItem(" How to Play");
        instructions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                hw.setVisible(true);
            }
        });
        instructions.setFont(new Font("", Font.BOLD, 20));
        options.add(instructions);
        JMenuItem exit = new JMenuItem(" Exit");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                frame.dispatchEvent(new WindowEvent(frame, 
                        WindowEvent.WINDOW_CLOSING));
            }
        });
        exit.setFont(new Font("", Font.BOLD, 20));
        options.add(exit);
        return options;
    }
    
    private void resetStats(){
        for(int i = 0; i < stats.length; i++){
            stats[i] = 0;
        }
    }
    
    public static void main(String[] args) {
        PusoyDos pd = new PusoyDos();         
    }
    
    
    
    

}


