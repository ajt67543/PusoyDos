package pusoydos;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class GamePanel extends JPanel{
    public JPanel topPanel, leftPanel, rightPanel, middlePanel, buttonPanel, 
            messagePanel;
    private final CardImagePanel[] cipArray = loadCardImages();
    private int[] cardValues = new int[52];
    private ArrayList<CardImagePanel> playerCards;
    private Dimension screenSize;
    private GameManager gm;
    private JFrame frame;
    private JButton play, pass;
    private Point middleOfScreen;
    private JTextPane message;
    private boolean gameStarting, gameOver;
    private PusoyDos pd;
    
    public GamePanel(JFrame frame, PusoyDos pd){
        this.frame = frame;
        this.pd = pd;
        this.setLayout(null);
        this.setBackground(new Color(30, 90, 25));
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        playerCards = new ArrayList<>();
        middleOfScreen = new Point((screenSize.width / 2) - 
                (cipArray[0].getPreferredSize().width / 2), (screenSize.height -
                        frame.getInsets().top - 
                        (cipArray[0].getPreferredSize().height / 2)) / 2 
                        - frame.getInsets().top);
        setPanels();
        buttonPanel = new JPanel();
        setMessagePanel();
        gameStarting = false;
    }
    
    public boolean getGameStarting(){
        return gameStarting;
    }
    
    public GamePanel getGamePanel(){
        return this;
    }
    
    public void setButtonPanelVisible(boolean cond){
        buttonPanel.setVisible(cond);
    }
    
    public GameManager getGameManager(){
        return gm;
    }
    
    public ArrayList<CardImagePanel> getPlayerCards(){
        return playerCards;
    }
    
    public CardImagePanel getBackCardImage(){
        return cipArray[52];
    }
    
    public CardImagePanel getBackSideCardImage(){
        return cipArray[53];
    }
    
    public Point getMiddleOfScreen(){
        return middleOfScreen;
    }
    
    public Dimension getScreenSize(){
        return screenSize;
    }
    
    //Starts card dealing animation.
    public void startThread() throws InterruptedException{
        turnOffPanels();
        CardImagePanel placeHolder = new CardImagePanel(
                cipArray[52].getCImage(), 52);
        placeHolder.setPreferredSize(placeHolder.getPreferredSize());
        Point startLocation = new Point((screenSize.width / 2) - 
                (placeHolder.getPreferredSize().width / 2), middlePanel.getY());
        placeHolder.setBounds(startLocation.x, startLocation.y, 
                placeHolder.getPreferredSize().width, 
                placeHolder.getPreferredSize().height);
        this.add(placeHolder);  
        DealCardsThread dealDown = new DealCardsThread(placeHolder, this, 
                startLocation, new Point(placeHolder.getX(), getPlayersYPos()));
        dealDown.start();
        DealCardsThread dealLeft = new DealCardsThread(placeHolder, this, 
                startLocation, new Point(0, placeHolder.getY()));
        dealLeft.start();
        DealCardsThread dealUp = new DealCardsThread(placeHolder, this, 
                startLocation, new Point(placeHolder.getX(), 0));
        dealUp.start();
        DealCardsThread dealRight = new DealCardsThread(placeHolder, this, 
                startLocation, new Point(screenSize.width - 
                placeHolder.getPreferredSize().height, placeHolder.getY()));
        dealRight.start();
        WaitThread wait = new WaitThread(placeHolder, this, 650, true, true);   //Delays panels setting.
        wait.start();
        placeHolder = null;
    }

    
    private CardImagePanel[] loadCardImages(){
        CardImagePanel[] imageArray = new CardImagePanel[54];
        File imageFolder = new File("PNG-cards-1.3");
        File[] fArray = imageFolder.listFiles();
        int wScale = 0, hScale = 0;
        for(int i = 0; i < 53; i++){
            try{
                BufferedImage image = ImageIO.read(fArray[i]);
                if(i == 0){
                    wScale = image.getWidth() * 30/100;
                    hScale = image.getHeight() * 30/100;
                }
                image = scaleImage(image, wScale, hScale);
                String path = fArray[i].getName();
                int cardValue = getCardValue(path);
                CardImagePanel cip = new CardImagePanel(image, cardValue);
                cip.setPreferredSize(cip.getPreferredSize());
                imageArray[cardValue] = cip;
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
        //Rotated image panel.
        CardImagePanel cip = new CardImagePanel(imageArray[52].getCImage(), 52);
        cip.setRotate(true);
        cip.setPreferredSize(cip.getPreferredSize());
        imageArray[53] = cip;
        return imageArray;
    }
    
    private static BufferedImage scaleImage(BufferedImage image, int wScale,
            int hScale){
        Image scaledImage = image.getScaledInstance(wScale, hScale, 
                Image.SCALE_SMOOTH);
        image = new BufferedImage(wScale, hScale, image.getType());
        image.getGraphics().drawImage(scaledImage, 0, 0, null);
        return image;
    }

    private static int getCardValue(String path){
        int cardValue;
        if(path.contains("3")){
            cardValue = 0;
        }
        else if(path.contains("4")){
            cardValue = 4;
        }
        else if(path.contains("5")){
            cardValue = 8;
        }
        else if(path.contains("6")){
            cardValue = 12;
        }
        else if(path.contains("7")){
            cardValue = 16;
        }
        else if(path.contains("8")){
            cardValue = 20;
        }
        else if(path.contains("9")){
            cardValue = 24;
        }
        else if(path.contains("10")){
            cardValue = 28;
        }
        else if(path.contains("jack")){
            cardValue = 32;
        }
        else if(path.contains("queen")){
            cardValue = 36;
        }
        else if(path.contains("king")){
            cardValue = 40;
        }
        else if(path.contains("ace")){
            cardValue = 44;
        }
        else if(path.contains("2")){
            cardValue = 48;
        }
        else{   //Back of card image.
            cardValue = 52;
            return cardValue;
        }
        return cardValue + getCardSuit(path);       
    }
    
    private static int getCardSuit(String path){
        if(path.contains("clubs")){
            return 0;
        }
        else if(path.contains("spades")){
            return 1;
        }
        else if(path.contains("hearts")){
            return 2;
        }
        else{
            return 3;
        }
    }
    
    //Sets layouts and background color of the panels.
    private void setPanels(){
        OverlapLayout leftLayout = new OverlapLayout(new Point(0, 
                cipArray[53].getPreferredSize().height / 2));
        OverlapLayout rightLayout = new OverlapLayout(new Point(0, 
                cipArray[53].getPreferredSize().height / 2));
        Color background = new Color(30, 90, 25);
        topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,
                - cipArray[0].getPreferredSize().width / 2, 0));
        topPanel.setBackground(background);     
        this.add(topPanel);
        leftPanel = new JPanel(leftLayout);
        leftPanel.setBackground(background);
        this.add(leftPanel);
        rightPanel = new JPanel(rightLayout);
        rightPanel.setBackground(background);
        this.add(rightPanel);
        middlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        middlePanel.setBackground(background);
        Dimension cardSize = cipArray[0].getPreferredSize();
        middlePanel.setBounds((screenSize.width / 2) - 
                ((cardSize.width * 5) / 2), middleOfScreen.y, 
                cardSize.width * 5, cardSize.height);
        this.add(middlePanel);  
    }
    
    //Sets the bounds of the panels.
    public void setPanelSizes(boolean setPlayers, boolean setLeft, 
            boolean setTop, boolean setRight){
        int cardWidth = cipArray[0].getPreferredSize().width,
                cardHeight = cipArray[0].getPreferredSize().height, topBorder = 
                frame.getInsets().top, length;
        if(setLeft){
            length = (cardWidth / 2) * gm.getPlayers().get(1).getHand().size() + 
                (cardWidth / 2);
            leftPanel.setBounds(0, ((screenSize.height - topBorder - length) 
                    / 2) - topBorder, cardHeight, length);
            addCardsToPanel(gm.getPlayers().get(1).getHand(), leftPanel, true);
        }
        if(setTop){
            length = (cardWidth / 2) * gm.getPlayers().get(2).getHand().size() + 
                (cardWidth / 2);
            topPanel.setBounds(panelLength(2), 0, length, cardHeight);
            addCardsToPanel(gm.getPlayers().get(2).getHand(), topPanel, false);
        }
        if(setRight){
            length = (cardWidth / 2) * gm.getPlayers().get(3).getHand().size() + 
                (cardWidth / 2);
            rightPanel.setBounds(screenSize.width - cardHeight, 
                    ((screenSize.height - topBorder - length) / 2) - topBorder, 
                    cardHeight, length);
            addCardsToPanel(gm.getPlayers().get(3).getHand(), rightPanel, true);
        }
        if(setPlayers){
            playerHand();
        }
        int middleSizeW = cardWidth * gm.getHandToBeat().size();
        middlePanel.setBounds((screenSize.width / 2) - (middleSizeW / 2), 
              middleOfScreen.y, middleSizeW, cardHeight);
        addCardsToMiddlePanel();
        revalidate();
        repaint(); 
    }

    private void addCardsToPanel(ArrayList<Integer> hand, JPanel panel, 
            boolean rotate){
        for(int i = 0; i < hand.size(); i++){
            CardImagePanel cip = new CardImagePanel(cipArray[53].getCImage(),
                    52);
            cip.setPreferredSize(cip.getPreferredSize());
            cip.setRotate(rotate);
            panel.add(cip);
        }
    }
    
    private void addCardsToMiddlePanel(){
        for(int i = 0; i < gm.getHandToBeat().size(); i++){
            CardImagePanel cip = cipArray[gm.getHandToBeat().get(i)];
            cip.setPreferredSize(cip.getPreferredSize());
            middlePanel.add(cip);
        }
    }
    
    //Sets positions for the player's cards.
    private void playerHand(){
        ArrayList<Integer> cards = gm.getPlayers().get(0).getHand();
        int yPos = getPlayersYPos();
        int startingPosition = panelLength(0);
        for(int i = 0; i < cards.size(); i++){
            int increment = ((cipArray[0].getPreferredSize().width / 2) * i);
            CardImagePanel cip = new CardImagePanel(
                    cipArray[cards.get(i)].getCImage(), cards.get(i));
            cip.setSize(cip.getPreferredSize());
            cip.setBounds(startingPosition + increment, yPos, cip.getWidth(), 
                    cip.getHeight());
            
            if(gm.getTurn() == 0){
                cip.addMouseListeners();
            }
            
            playerCards.add(cip);
            this.add(cip);
            cip.getParent().setComponentZOrder(cip, 0);
        }
    }
    
    //Gets the x for the top and players' panels.
    private int panelLength(int turn){
        int size = gm.getPlayers().get(turn).getHand().size();
        double i = 1 + ((size - 1) * .5);
        double position = (screenSize.width / 2) - 
                ((i * cipArray[0].getPreferredSize().width) / 2);
        return (int) position;
    }
    
    //Gets the y position for the players' cards.
    public int getPlayersYPos(){
        return GraphicsEnvironment.getLocalGraphicsEnvironment().
                getMaximumWindowBounds().height - cipArray[0].getPreferredSize()
                .height - Toolkit.getDefaultToolkit().getScreenInsets(
                        frame.getGraphicsConfiguration()).bottom;
    }
    
    //Starts the game.
    public void playGame() throws InterruptedException{
        gameStarting = true;
        gameOver = false;
        clearPanels(true, true, true, true);
        buttonPanel.removeAll();
        for(CardImagePanel card : cipArray){
            card.setClicked(false);
            card.removeListeners();
        }
        gameStarting = false;
        startThread();        
        gm = new GameManager(this);
        gm.startGame();
    }
    
    private void clearPanels(boolean clearPlayer, boolean clearLeft,
            boolean clearTop, boolean clearRight){
        if(clearLeft){
            leftPanel.removeAll();
        }
        if(clearTop){
            topPanel.removeAll();
        }
        if(clearRight){
            rightPanel.removeAll();
        }
        if(clearPlayer){
            for(CardImagePanel playerCard : playerCards) {
                this.remove(playerCard);
            }
            playerCards.clear();
        }
        middlePanel.removeAll();        
    }
    
    public void setButtons(){
        buttonPanel.add(setPlayButton());
        buttonPanel.add(setPassButton());
        buttonPanel.setBackground(new Color(30, 90, 25));
        buttonPanel.setBounds((screenSize.width / 2) - 
                play.getPreferredSize().width, middlePanel.getY() + 
                cipArray[0].getPreferredSize().height + screenSize.height * 5 
                / 100, (play.getPreferredSize().width * 2) +
                play.getInsets().left, play.getPreferredSize().height + 
                play.getInsets().bottom);
        this.add(buttonPanel);
    }
    
    private JButton setPlayButton(){
        play = new JButton("Play");
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae){
                int turn = gm.getTurn();
                ArrayList<Integer> submit = clickedCards();
                if(gm.playerMove(submit)){  //Player played a correct move.
                    buttonPanel.setVisible(false);
                    resetPlayerCards();
                    if(gm.getPlayers().get(0).getHand().isEmpty()){
                        gameOver = true;
                        buttonPanel.setVisible(false);
                        showGameOverMessage();
                    }
                    else{
                        ComputerThread ct = new ComputerThread(getGamePanel()); //Sets the computers to go
                        ct.start();
                    }
                }
                else{   //Player played a wrong move.
                    resetPlayerCards();
                    determinePanelToSet(turn);
                }
            } 
            private ArrayList<Integer> clickedCards(){  
                ArrayList<Integer> clicked = new ArrayList<>();
                for(int i = 0; i < playerCards.size(); i++){
                    if(playerCards.get(i).getClicked()){
                        clicked.add(playerCards.get(i).getValue());
                    }
                }
                return clicked;
            }
            
            private void resetPlayerCards(){
                for(CardImagePanel card : playerCards){ //Remove mouseListeners from players cards.
                    card.resetPosition();
                    card.setClicked(false);
                    card.removeListeners();
                }
            }
        });
        play.setPreferredSize(new Dimension(screenSize.width * 5 / 100,
                screenSize.height * 5 / 100));
        play.setFont(new Font("", Font.PLAIN, 20));
        return play;
    }
    
    //After every turn, set the panel according to whose turn it was.
    public void determinePanelToSet(int turn){
        if(turn == 0){
            clearPanels(true, false, false, false);
            setPanelSizes(true, false, false, false);
        }
        else if(turn == 1){
            clearPanels(false, true, false, false);
            if(!gm.computerFinish()){
                setPanelSizes(false, true, false, false);
            }
            else{
                setPanelSizes(false, false, false, false);
            }
        }
        else if(turn == 2){
            clearPanels(false, false, true, false);
            if(!gm.computerFinish()){
                setPanelSizes(false, false, true, false);
            }
            else{
                setPanelSizes(false, false, false, false);
            }
        }
        else{
            clearPanels(false, false, false, true);
            if(!gm.computerFinish()){
                setPanelSizes(false, false, false, true);
            }
            else{
                setPanelSizes(false, false, false, false);
            }
        }
    }
    
    private JButton setPassButton(){
        pass = new JButton("Pass"); 
        pass.setPreferredSize(new Dimension(screenSize.width * 5 / 100,
                screenSize.height * 5 / 100));
        pass.setFont(new Font("", Font.PLAIN, 20));
        pass.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
                gm.playerPass();
                for(CardImagePanel card : playerCards){ //Remove mouseListeners from players cards.
                    card.resetPosition();
                    card.setClicked(false);
                    card.removeListeners();
                }
                determinePanelToSet(0);
                ComputerThread ct = new ComputerThread(getGamePanel());
                ct.start();
            }
        });
        pass.setVisible(false);
        return pass;
    }
    
    public void setPassVisibility(){
        if(gm.getTurn() == 0 && !gm.getControl()){
            pass.setVisible(true);
        }
        else{
            pass.setVisible(false);
        }
    }
    
    @Override
    public void paint(Graphics g){
        super.paint(g);
    }
    
    private void turnOffPanels(){
        leftPanel.setVisible(false);
        topPanel.setVisible(false);
        rightPanel.setVisible(false);
        middlePanel.setVisible(false);
        buttonPanel.setVisible(false);
    }
    
    public void turnOnPanels(){
        leftPanel.setVisible(true);
        topPanel.setVisible(true);
        rightPanel.setVisible(true);
        middlePanel.setVisible(true);
        buttonPanel.setVisible(true);
    }
    
    private void setMessagePanel(){
        messagePanel = new JPanel();
        message = new JTextPane();
        message.setPreferredSize(new Dimension(screenSize.width * 3 / 10, 
                screenSize.height * 15 / 100));
        message.setEditable(false);
        message.setFont(new Font("", Font.BOLD, 80));
        message.setForeground(Color.white);
        message.setText("Player 3 Passed");
        message.setBackground(new Color(30, 90, 25));
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        message.setParagraphAttributes(center, false);
        messagePanel.add(message);
        messagePanel.setBounds(getMessagePanelX(), 
                cipArray[0].getPreferredSize().height +
                ((middlePanel.getY() - cipArray[0].getPreferredSize().height) / 
                4), message.getPreferredSize().width, 
                message.getPreferredSize().height);
        messagePanel.setBackground(new Color(30, 90, 25));
        this.add(messagePanel);
        messagePanel.setVisible(false);
    }
    
    public int getMessagePanelX(){
        return (screenSize.width / 2) - (screenSize.width * 3 / 10 / 2);
    }
    
    public JPanel getMessagePanel(){
        return messagePanel;
    }
    
    public JTextPane getMessagePane(){
        return message;
    }
    
    public boolean getGameOver(){
        return gameOver;
    }
    
    public void showGameOverMessage(){
        System.out.println("Game Over");
        String place;
        if(gm.getPlayersDone()  == 0){
            place = "1st";
            pd.setStats(0);
        }
        else if(gm.getPlayersDone() == 1){
            place = "2nd";
            pd.setStats(1);
        }
        else if(gm.getPlayersDone() == 2){
            place = "3rd";
            pd.setStats(2);
        }
        else{
            place = "4th";
            pd.setStats(3);
        }
        System.out.println(place);
        TextThread t1 = new TextThread(this, "Game Over \n You placed " + place);
        t1.start();
    }
    
    
}
