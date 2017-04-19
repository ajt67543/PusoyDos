package pusoydos;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameManager {
    private boolean firstMove;
    private boolean control;
    private boolean playerControl;
    public int numOfPasses;
    private ArrayList<Integer> handToBeat;
    private int handIdentifier;
    private ArrayList<Integer> cardsPlayed;
    private ArrayList<CardHand> players;
    private int turn, playersDone; 
    private GamePanel gp;
    
    public GameManager(GamePanel gp){
        players = setPlayers();
        numOfPasses = 0;
        handToBeat = new ArrayList<>();
        cardsPlayed = new ArrayList<>();
        playersDone = 0;
        this.gp = gp;
    }
    
    public int getPlayersDone(){
        return playersDone;
    }
    
    public ArrayList<Integer> getHandToBeat(){
        return handToBeat;
    }
    
    public ArrayList<CardHand> getPlayers(){
        return players;
    }
    
    public int getTurn(){
        return turn;
    }
    
    public boolean getControl(){
        return control;
    }
    
    private ArrayList<CardHand> setPlayers(){
        CardHand player = new CardHand(true);
        CardHand comp1 = new CardHand();
        CardHand comp2 = new CardHand();
        CardHand comp3 = new CardHand();
        return new ArrayList<>(Arrays.asList(player, comp1, comp2, comp3));
    }
    
    //Shuffles cards and deals them to each player.
    public void startGame(){
        int [] cards = new int[52];
        for(int i = 0; i < 52; i++){
            cards[i] = i;
        }
        shuffle(cards);
        setPlayerHands(cards);
        players.get(1).setComputerHand();
        players.get(2).setComputerHand();
        players.get(3).setComputerHand();
        setFirstMove();
    }
  
    private void setPlayerHands(int [] cards){
        ArrayList<Integer> h1 = new ArrayList<>();
        ArrayList<Integer> h2 = new ArrayList<>();
        ArrayList<Integer> h3 = new ArrayList<>();
        ArrayList<Integer> h4 = new ArrayList<>();
        for(int i = 0; i < 52; i += 4){
            h1.add(cards[i]);
            h2.add(cards[i + 1]);
            h3.add(cards[i + 2]);
            h4.add(cards[i + 3]);
        }
        players.get(0).setHand(h1);
        players.get(1).setHand(h2);
        players.get(2).setHand(h3);
        players.get(3).setHand(h4);
    }
    
    private void shuffle(int [] deck){
        Random rand = ThreadLocalRandom.current();
        for(int i = 0; i < deck.length; i++){
            int index = rand.nextInt(i + 1);
            int a = deck[index];
            deck[index] = deck[i];
            deck[i] = a;
        }
    }
    
    private void setFirstMove(){
        for(int i = 0; i < players.size(); i++){
            if(players.get(i).getHand().get(0) == 0){
                turn = i;
                firstMove = true;
                control = true;
                numOfPasses = 0;
                break;
            }
        }
    }
    //Checks the cards the player clicked on and see if it's a valid move.
    public boolean playerMove(ArrayList<Integer> hand){
        players.get(turn).sortHand(hand);
        boolean cond = false;
        if(firstMove){
            if(hand.contains(0) && checkPlayersHand(hand)){
                setHandToBeatPlayerTurn(hand);
                cond = true;
                firstMove = false;
                //Thread animation.
                startCardAnimation();
            }
        }
        else if(control){
            cond = checkPlayersHand(hand);
            if(cond){
                setHandToBeatPlayerTurn(hand);
                //Thread animation.
                startCardAnimation();
            }
        }
        else{
            cond = playerBeatCurrentHand(hand);
            if(cond){
                startCardAnimation();
            }
        }
        if(cond){
            playerControl = true;
            setTurn();
        }
        return cond;
    }
    
    private void setHandToBeatPlayerTurn(ArrayList<Integer> hand){
        handToBeat.clear();
        handToBeat.addAll(hand);
        if(handToBeat.size() == 5){
            setIdentifier(handToBeat);
        }
        players.get(turn).getHand().removeAll(handToBeat);
        resetParameters();
    }
    
    //Checks if the player played a correct hand.
    private boolean checkPlayersHand(ArrayList<Integer> hand){
        CardHand player = players.get(turn);
        return hand.size() == 1 || (hand.size() == 2 && player.pairCheck(
                hand.get(0), hand.get(1))) || (hand.size() == 5 &&
                (player.fourCheck(hand) || player.fullHouseCheck(hand) ||
                player.flushCheck(hand) || player.straightCheck(hand)));
    }
    
    private boolean playerBeatCurrentHand(ArrayList<Integer> hand){
        boolean cond = false;
        CardHand player = players.get(turn);
        if(handToBeat.size() == 1){
            if(hand.size() == 1 && hand.get(0) > handToBeat.get(0)){
                setHandToBeatPlayerTurn(hand);
                cond = true;
            }
        }
        else if(handToBeat.size() == 2){
            if(hand.size() == 2 && player.pairCheck(hand.get(0), hand.get(1)) &&
                    hand.get(1) > handToBeat.get(1)){
                setHandToBeatPlayerTurn(hand);
                cond = true;
            }
        }
        else{
            if(hand.size() == 5){
                cond = checkCombForPlayer(hand);
            }
        }
        return cond;
    }
    
    private boolean checkCombForPlayer(ArrayList<Integer> hand){
        boolean cond = false;
        int typeOfComb = typeOfComb(hand);
        if(typeOfComb != -1){
            //Create all the combination conditions.
            if(handIdentifier == 0){
                if(typeOfComb > 0 || (typeOfComb == 0 && hand.get(4) > 
                        handToBeat.get(4))){
                    cond = true;
                }
            }
            else if(handIdentifier == 1){
                if(typeOfComb > 1 || (typeOfComb == 1 && ((hand.get(4) % 4 == 
                        handToBeat.get(4) % 4 && hand.get(4) > 
                        handToBeat.get(4)) || 
                        hand.get(4) % 4 > handToBeat.get(4) % 4))){
                    cond = true;
                }
            }
            else if(handIdentifier == 2){
                if(typeOfComb > 2 || (typeOfComb == 2 && hand.get(2) > 
                        handToBeat.get(2))){
                    cond = true;
                }
            }
            else if(handIdentifier == 3){
                if(typeOfComb > 3 || (typeOfComb == 3 && hand.get(1) >
                        handToBeat.get(1))){
                    cond = true;
                }
            }
            else{
                if(typeOfComb == 4 && hand.get(4) > handToBeat.get(4)){
                    cond = true;
                }
            }
        }
        if(cond){
            setHandToBeatPlayerTurn(hand);
        }
        return cond;
    }
    
    private int typeOfComb(ArrayList<Integer> hand){
        int type = -1;
        CardHand player = players.get(turn);
        if(player.straightCheck(hand) && player.flushCheck(hand)){
            type = 4;
        }
        else if(player.straightCheck(hand)){
            type = 0;
        }
        else if(player.flushCheck(hand)){
            type = 1;
        }
        else if(player.fullHouseCheck(hand)){
            type = 2;
        }
        else if(player.fourCheck(hand)){
            type = 3;
        }
        return type;
    }
    
    public void playerPass(){
        numOfPasses++;
        determineControl();
        setTurn();
        
    }
   
    public boolean computerMove(){
        boolean pass = false;
        System.out.println("Turn " + turn);
        System.out.println("Hand " + players.get(turn).getHand());
        players.get(turn).showAllCards();
        if(firstMove){
            searchForThreeClubs();
            //Thread animation.
            startCardAnimation();
        }
        else if(control){
            playBestMove();
            //Thread animation.
            startCardAnimation();
        }
        else{
            pass = playToBeat();
            if(!pass){
                //Thread animation.
                startCardAnimation();
            }
            
        }
        System.out.println("Computer " + turn + " hand: " + players.get(turn).getHand());
        if(computerFinish()){
            playersDone++;
            control = true;
            numOfPasses = 0;
        }
        setTurn();
        System.out.println("Hand To Beat " + handToBeat);
        System.out.println("Control " + control);
        return pass;
    }
    //Checks if the computer can beat the current handToBeat.
    private boolean playToBeat(){
        boolean pass = false;
        if(handToBeat.size() == 1){
            pass = playSingle();
        }
        else if(handToBeat.size() == 2){
            pass = playPairs();
        }
        else{
            pass = playBestComb();
        }
        if(pass){
            if(playerControl){
                pass = compBeatPlayer();
            }
            if(pass){
                System.out.println("Computer Passed");
                numOfPasses++;
                determineControl(); 
            }
        }
        return pass;
    }
    
    private void setTurn(){
        turn++;
        if(turn == 4){
            turn = 0;
        }
        while(turn != 0 && players.get(turn).getHand().isEmpty()){
            turn++;
            if(turn == 4){
                turn = 0;
            }
        }
    }
    public void determineControl(){
        if(numOfPasses == players.size() - playersDone - 1){
            control = true;
            numOfPasses = 0;
        }
    }
    
    public boolean computerFinish(){
        if(players.get(turn).getHand().isEmpty()){
            return true;
        }
        else{
            return false;
        }
    }
    
    private void searchForThreeClubs(){
        if(combExists(players.get(turn).getCFour()) && 
                players.get(turn).getCFour().get(0).get(0) == 0){
            setHandToBeatComb(players.get(turn).getCFour().get(0), 
                    players.get(turn).getCFour());
            handIdentifier = 3;
        }
        else if(combExists(players.get(turn).getCFullHouse()) && 
                players.get(turn).getCFullHouse().get(0).get(0) == 0){
            setHandToBeatComb(players.get(turn).getCFullHouse().get(0), 
                    players.get(turn).getCFullHouse());
            handIdentifier = 2;
        }
        else if(combExists(players.get(turn).getCFlush()) && 
                players.get(turn).getCFlush().get(0).get(0) == 0){
            setHandToBeatComb(players.get(turn).getCFlush().get(0), 
                    players.get(turn).getCFlush());
            handIdentifier = checkForStraightFlush();
        }
        else if(combExists(players.get(turn).getCStraight()) && 
                players.get(turn).getCStraight().get(0).get(0) == 0){
            setHandToBeatComb(players.get(turn).getCStraight().get(0), 
                    players.get(turn).getCStraight());
            handIdentifier = 0;
        }
        else if(combExists(players.get(turn).getCPairs()) && 
                players.get(turn).getCPairs().get(0).get(0) == 0){
            setHandToBeatComb(players.get(turn).getCPairs().get(0), 
                    players.get(turn).getCPairs());
        }
        else{
            setHandToBeatSingle(players.get(turn).getCSingles().get(0), 
                    players.get(turn).getCSingles());
        }
        firstMove = false;
    }
    
    private boolean combExists(ArrayList<ArrayList<Integer>> comb){
        return !comb.isEmpty();
    }
    
    private void setHandToBeatComb(ArrayList<Integer> newHand, 
            ArrayList<ArrayList<Integer>> removeFromList){
        handToBeat.clear();
        handToBeat.addAll(newHand);
        removeFromList.remove(newHand);
        players.get(turn).getHand().removeAll(newHand);
        playerControl = false;
        resetParameters();
    }
    
    private void setHandToBeatSingle(Integer card, ArrayList<Integer> 
            removeFromList){
        handToBeat.clear();
        handToBeat.add(card);
        removeFromList.remove(card);
        players.get(turn).getHand().remove(card);
        playerControl = false;
        resetParameters();
    }
    
    private void playBestMove(){
        ArrayList<Integer> bestMove = fiveCombExists();
        if(!players.get(turn).getCTwos().isEmpty() && 
                players.get(turn).getCSingles().size() + 
                players.get(turn).getCTwos().size() == 5 && combWithTwos()){ //Checks if a 5-card combination exists with two.
            handToBeat.clear();
            handToBeat.addAll(players.get(turn).getHand());
            players.get(turn).getHand().clear();
        }
        else if(!bestMove.isEmpty()){
            if(players.get(turn).straightCheck(bestMove)){
                setHandToBeatComb(bestMove, players.get(turn).getCStraight());
            }
            else if(players.get(turn).flushCheck(bestMove)){
                setHandToBeatComb(bestMove, players.get(turn).getCFlush());
            }
            else if(players.get(turn).fullHouseCheck(bestMove)){
                setHandToBeatComb(bestMove, players.get(turn).getCFullHouse());
            }
            else{
                setHandToBeatComb(bestMove, players.get(turn).getCFour());
            }
            setIdentifier(bestMove);
        }
        else if(!players.get(turn).getCPairs().isEmpty()){
            setHandToBeatComb(players.get(turn).getCPairs().get(0), 
                    players.get(turn).getCPairs());
        }
        else{
            decideOnSingle();
        }
    }
    
    private boolean combWithTwos(){
        ArrayList<Integer> comb = new ArrayList<>();
        boolean cond = false;
        comb.addAll(players.get(turn).getHand());
        if(players.get(turn).fourCheck(comb) || 
                players.get(turn).fullHouseCheck(comb) || 
                players.get(turn).flushCheck(comb) || 
                players.get(turn).straightCheck(comb)){
            cond = true;
        }
        return cond;
    }
    
    /*
    Decide the best single to play. 
    */
    private void decideOnSingle(){
        if(playersDone == 2 && players.get(0).getHand().size() < 3){ //There are only 2 players left and the player is almost done. Play the highest card available.
            if(!players.get(turn).getCTwos().isEmpty()){
                    setHandToBeatSingle(players.get(turn).getCTwos().get(0), 
                        players.get(turn).getCTwos());
                }
            else if(!players.get(turn).getCSingles().isEmpty()){ 
                int index = players.get(turn).getCSingles().size() - 1;
                setHandToBeatSingle(players.get(turn).getCSingles().
                        get(index), players.get(turn).getCSingles());
            }
            else{
                setHandToBeatSingle(players.get(turn).getHand().get(
                        players.get(turn).getHand().size() - 1), 
                        new ArrayList<Integer>());
                players.get(turn).setComputerHand();
            }
        }
        else if(!players.get(turn).getCSingles().isEmpty()){
            setHandToBeatSingle(players.get(turn).getCSingles().get(0), 
                    players.get(turn).getCSingles());
        }
        else{
            setHandToBeatSingle(players.get(turn).getCTwos().get(0), 
                    players.get(turn).getCTwos());
        }
    }
    
    private ArrayList<Integer> fiveCombExists(){
        ArrayList<Integer> comb = new ArrayList<>();
        if(!players.get(turn).getCStraight().isEmpty()){
            comb.addAll(players.get(turn).getCStraight().get(0));
        }
        else if(!players.get(turn).getCFlush().isEmpty()){
            comb.addAll(players.get(turn).getCFlush().get(0));
        }
        else if(!players.get(turn).getCFullHouse().isEmpty()){
            comb.addAll(players.get(turn).getCFullHouse().get(0));
        }
        else if(!players.get(turn).getCFour().isEmpty()){
            comb.addAll(players.get(turn).getCFour().get(0));
        }
        return comb;
    }
    
    private Integer highestCardAvailable(){
        int highestCard = 51, i = cardsPlayed.size() - 1;
        while(highestCard == cardsPlayed.get(i)){
            highestCard--;
            i--;
        }
        return highestCard;
    }
    
    private void sortHand(ArrayList<Integer> hand){
        for(int i = 1; i < hand.size(); i++){
            for(int j = i - 1; j >= 0; j--){
                if(hand.get(j + 1) < hand.get(j)){
                    int temp = hand.get(j);
                    hand.set(j, hand.get(j + 1));
                    hand.set(j + 1, temp);
                }
                else{
                    break;
                }
            }
        }
    }
    //Sets what kind of five-card combination is the current handToBeat.
    private void setIdentifier(ArrayList<Integer> comb){
        if(players.get(0).straightCheck(comb)){
            if(players.get(0).flushCheck(comb)){
                handIdentifier = 4;
            }
            else{
                handIdentifier = 0;
            }
        }
        else if(players.get(0).flushCheck(comb)){
            handIdentifier = 1;
        }
        else if(players.get(0).fullHouseCheck(comb)){
            handIdentifier = 2;
        }
        else{
            handIdentifier = 3;
        }
    }
    
    private int checkForStraightFlush(){
        if(players.get(0).straightCheck(handToBeat) && 
                players.get(0).flushCheck(handToBeat)){
            return 4;
        }
        else if(players.get(0).flushCheck(handToBeat)){
            return 1;
        }
        else{
            return 0;
        }
    }
    
    private void resetParameters(){
        cardsPlayed.addAll(handToBeat);
        sortHand(cardsPlayed);
        control = false;
        numOfPasses = 0;
    }
    
    private boolean playSingle(){
        boolean pass = true;
        if(breakComb()){
            players.get(turn).setComputerHand();
            pass = false;
        }
        else{
            ArrayList<Integer> singles = players.get(turn).getCSingles();
            for(int i = 0; pass && i < singles.size(); i++){
                if(singles.get(i) > handToBeat.get(0)){
                    setHandToBeatSingle(singles.get(i), singles);
                    pass = false;
                }
            }
            if(pass && !players.get(turn).getCTwos().isEmpty()){
                pass = decideOnTwo();
            }
        }
        return pass;
    }
    
    private boolean breakComb(){    //Break apart a five-card combination for a single or a pair.
        boolean cond = false;
        if(players.size() < 4 && getRemainingPlayersHandNum() 
                <= players.size() * 2 && !haveHighestCard()){
            ArrayList<Integer> comb = fiveCombExists();
            if(!comb.isEmpty() && combCanBeatSingle(comb)){
                getBestSingleFromComb(comb);
                cond = true;
            }
            else{
                cond = getBestSingleFromPairs();
            }
        }
        return cond;
    }
    
    private int getRemainingPlayersHandNum(){
        int count = 0;
        for(int i = 0; i < players.size(); i++){
            if(i != turn){
                count += players.get(i).getHand().size();
            }
        }
        return count;
    }
    
    private boolean haveHighestCard(){
        Integer highestCard = highestCardAvailable();
        return players.get(turn).getCTwos().contains(highestCard) || 
                players.get(turn).getCSingles().contains(highestCard);
    }
    
    private boolean combCanBeatSingle(ArrayList<Integer> comb){
        boolean cond = false;
        int i = 0;
        while(!cond && i < comb.size()){
            if(handToBeat.get(0) < comb.get(i)){
                cond = true;
            }
            i++;
        }
        return cond;
    }
    
    private void getBestSingleFromComb(ArrayList<Integer> comb){
        if(!players.get(turn).fourCheck(comb)){
            breakFour(comb);
        }
        else if(!players.get(turn).fullHouseCheck(comb)){
            breakFullHouse(comb);
        }
        else if(!players.get(turn).flushCheck(comb)){
            breakFlush(comb);
        }
        else if(!players.get(turn).straightCheck(comb)){
            breakStraight(comb);
        }
    }
    private boolean breakFour(ArrayList<Integer> comb){
        boolean cond = false;
        if(comb.get(4) / 4 != comb.get(3) / 4){
            if(comb.get(0) > handToBeat.get(0)){
                setHandToBeatSingle(comb.get(4), comb);
                cond = true;
            }
        }
        else{
            if(comb.get(0) > handToBeat.get(0)){
                setHandToBeatSingle(comb.get(0), comb);
                cond = true;
            }
        }
        return cond;
    }
   
    private boolean breakFullHouse(ArrayList<Integer> comb){
        boolean cond = false;
        if(comb.get(2) > handToBeat.get(0)){
            setHandToBeatSingle(comb.get(2), comb);
            cond = true;
        }
        else{
            for(int i = 0; !cond && i < comb.size(); i++){
                if(comb.get(i) > handToBeat.get(0)){
                    setHandToBeatSingle(comb.get(i), comb);
                    cond = true;
                }
            }
        }
        return cond;
    }
    
    private boolean breakFlush(ArrayList<Integer> comb){
        boolean cond = false;
        for(int i = 0; !cond && i < comb.size(); i++){
            if(comb.get(i) > handToBeat.get(0)){
                setHandToBeatSingle(comb.get(i), comb);
                cond = true;
            }
        }
        return true;
    }
    private boolean breakStraight(ArrayList<Integer> comb){
        boolean cond = false;
        for(int i = 0; !cond && i < comb.size(); i++){
            if(comb.get(i) > handToBeat.get(0)){
                setHandToBeatSingle(comb.get(i), comb);
                cond = true;
            }
        }
        return true;
    }
    
    private boolean getBestSingleFromPairs(){
        boolean cond = false;
        for(int i = 0; !cond && i < players.get(turn).getCPairs().size(); i++){
            ArrayList<Integer> pair = players.get(turn).getCPairs().get(i);
            for(int j = 0; !cond && j < pair.size(); j++){
                if(pair.get(j) > handToBeat.get(0)){
                    setHandToBeatSingle(pair.get(j), pair);
                    cond = true;
                }
            }
        }
        return cond;
    }
    
    private boolean decideOnTwo(){
        boolean cond = true;
        Integer highCard = highestCardAvailable();
        if(players.get(turn).getCTwos().contains(highCard) && 
                highCard > handToBeat.get(0)){
            setHandToBeatSingle(highCard, players.get(turn).getCTwos());
            cond = false;
        }
        else if(players.get(turn).getHand().size() == 1 && 
                players.get(turn).getCTwos().get(0) > handToBeat.get(0)){
            setHandToBeatSingle(players.get(turn).getCTwos().get(0), 
                    players.get(turn).getCTwos());
            cond = false;
        }
        return cond;
    }
    
    private boolean playPairs(){
        boolean cond = true;
        if(players.get(turn).getCTwos().size() > 1 && 
                ((players.get(turn).getHand().size() == 3 && 
                players.get(turn).getCSingles().size() == 1) || 
                (players.get(turn).getHand().size() == 4 &&
                players.get(turn).getCPairs().size() == 1))){
            ArrayList<Integer> pairOfTwos = new ArrayList<>();
            pairOfTwos.add(players.get(turn).getCTwos().get(0));
            pairOfTwos.add(players.get(turn).getCTwos().get(1));
            setHandToBeatComb(pairOfTwos, new ArrayList<ArrayList<Integer>>());
            cond = false;
        }
        else if(!breakFourOrFull()){
            cond = false;
        }
        else if(!players.get(turn).getCPairs().isEmpty()){
            ArrayList<ArrayList<Integer>> pairsList = players.get(turn).
                    getCPairs();
            for(int i = 0; cond && i < pairsList.size(); i++){
                if(pairsList.get(i).get(1) > handToBeat.get(1)){
                    setHandToBeatComb(pairsList.get(i), pairsList);
                    cond = false;
                }
            }
        }
        return cond;
    }
    
    private boolean breakFourOrFull(){
        boolean cond = true;
        if(players.size() < 4 && getRemainingPlayersHandNum() 
                <= players.size() * 2 && !haveHighestCard()){
            if(!players.get(turn).getCFour().isEmpty()){
                cond = pairFromFour(cond);
            }
            if(cond && !players.get(turn).getCFullHouse().isEmpty()){
                cond = pairFromFull(cond);
            }
        }
        return cond;
    }
    
    private boolean pairFromFour(boolean cond){
        for(int i = 0; cond && i < players.get(turn).getCFour().size();
                i++){
            ArrayList<Integer> four = players.get(turn).getCFour().get(0);
            if(four.get(0) > handToBeat.get(0)){
                ArrayList<Integer> pair = new ArrayList<>(Arrays.asList(
                        four.get(0), four.get(1)));
                setHandToBeatComb(pair, 
                        new ArrayList<ArrayList<Integer>>());
                players.get(turn).setComputerHand();
                cond = false;
            }
        }
        return cond;
    }
    
    private boolean pairFromFull(boolean cond){
        for(int i = 0; cond && i < players.get(turn).getCFullHouse().size(); 
                i++){
            ArrayList<Integer> full = players.get(turn).getCFullHouse().get(i);
            ArrayList<Integer> pair = new ArrayList<>();
            if(full.get(2) / 4 == full.get(3) / 4){
                if(full.get(1) > handToBeat.get(1)){
                    pair.add(full.get(0));
                    pair.add(full.get(1));
                    setHandToBeatComb(pair, new ArrayList<ArrayList<Integer>>());
                    cond = false;
                }
                else if(full.get(2) > handToBeat.get(1)){
                    pair.add(full.get(2));
                    pair.add(full.get(3));
                    setHandToBeatComb(pair, new ArrayList<ArrayList<Integer>>());
                    cond = false;
                }
            }
            else{
                if(full.get(2) > handToBeat.get(1)){
                    pair.add(full.get(0));
                    pair.add(full.get(1));
                    setHandToBeatComb(pair, new ArrayList<ArrayList<Integer>>());
                    cond = false;
                }
                else if(full.get(4) > handToBeat.get(1)){
                    pair.add(full.get(3));
                    pair.add(full.get(4));
                    setHandToBeatComb(pair, new ArrayList<ArrayList<Integer>>());
                    cond = false;
                }
            }
        }
        return cond;
    }
    
    private boolean playBestComb(){
        boolean cond;
        if(handIdentifier == 0){
            cond = playComb(true, true, true, true, true);
        }
        else if(handIdentifier == 1){
            cond = playComb(false, true, true, true, true);
        }
        else if(handIdentifier == 2){
            cond = playComb(false, false, true, true, true);
        }
        else if(handIdentifier == 3){
            cond = playComb(false, false, false, true, true);
        }
        else{
            cond = playComb(false, false, false, false, true);
        }
        return cond;
    }
    
    private boolean playComb(boolean straight, boolean flush, 
            boolean fullHouse, boolean four, boolean straightFlush){
        boolean cond = true;
        if(straight && !players.get(turn).getCStraight().isEmpty()){
            cond = playStraight(players.get(turn).getCStraight(), cond);
        }
        if(flush && cond && !players.get(turn).getCFlush().isEmpty()){
            cond = playFlush(players.get(turn).getCFlush(), cond);
        }
        if(fullHouse && cond && !players.get(turn).getCFullHouse().isEmpty()){
            cond = playFull(players.get(turn).getCFullHouse(), cond);
        }
        if(four && cond && !players.get(turn).getCFour().isEmpty()){
            cond = playFour(players.get(turn).getCFour(), cond);
        }
        if(straightFlush && cond){
            cond = playStraightFlush(cond);
        }
        return cond;
    }
    
    private boolean playStraight(ArrayList<ArrayList<Integer>> straights, 
            boolean cond){
        for(int i = 0; cond && i < straights.size(); i++){
            if((straights.get(i).get(4) / 4 == handToBeat.get(4) / 4 && 
                    straights.get(i).get(4) % 4 > handToBeat.get(4) % 4) || 
                    straights.get(i).get(4) > handToBeat.get(4)){
                setHandToBeatComb(straights.get(i), straights);
                handIdentifier = 0;
                cond = false;
            }
        }
        return cond;
    }
    
    private boolean playFlush(ArrayList<ArrayList<Integer>> flushes, 
            boolean cond){
        for(int i = 0; cond && i < flushes.size(); i++){
            if(handIdentifier < 1 || 
                    (flushes.get(i).get(0) % 4 == handToBeat.get(0) % 4 && 
                    flushes.get(i).get(4) > handToBeat.get(4)) || 
                    flushes.get(i).get(0) % 4 > handToBeat.get(0) % 4){
                setHandToBeatComb(flushes.get(i), flushes);
                handIdentifier = 1;
                cond = false;
            }
        }
        return cond;
    }
    
    private boolean playFull(ArrayList<ArrayList<Integer>> fulls, boolean cond){
        for(int i = 0; cond && i < fulls.size(); i++){
            if(handIdentifier < 2 || fulls.get(i).get(2) > handToBeat.get(2)){
                setHandToBeatComb(fulls.get(i), fulls);
                handIdentifier = 2;
                cond = false;
            }
        }
        return cond;
    }
    
    private boolean playFour(ArrayList<ArrayList<Integer>> fours, boolean cond){
        for(int i = 0; cond && i < fours.size(); i++){
            if(handIdentifier < 3 || fours.get(i).get(1) > handToBeat.get(1)){
                setHandToBeatComb(fours.get(i), fours);
                handIdentifier = 3;
                cond = false;
            }
        }
        return cond;
    }
    
    private boolean playStraightFlush(boolean cond){
        ArrayList<ArrayList<Integer>> straightFlush;
        if(!players.get(turn).getCStraight().isEmpty()){
            straightFlush = players.get(turn).getCStraight();
            for(int i = 0; cond && i < straightFlush.size(); i++){
                if(players.get(turn).flushCheck(straightFlush.get(i)) &&
                        (handIdentifier < 4 || straightFlush.get(i).get(4) > 
                        handToBeat.get(4))){
                    setHandToBeatComb(straightFlush.get(i), straightFlush);
                    handIdentifier = 4;
                    cond = false;
                }
            }
        }
        if(cond && !players.get(turn).getCFlush().isEmpty()){
            straightFlush = players.get(turn).getCFlush();
            for(int i = 0; cond && i < straightFlush.size(); i++){
                if(players.get(turn).straightCheck(straightFlush.get(i)) &&
                        (handIdentifier < 4 || straightFlush.get(i).get(4) > 
                        handToBeat.get(4))){
                    setHandToBeatComb(straightFlush.get(i), straightFlush);
                    handIdentifier = 4;
                    cond = false;
                }
            }
        }
        return cond;
    }
    //The computer prevents the player from gaining control by beating the players' current hand.
    private boolean compBeatPlayer(){
        boolean cond = true;
        if(handToBeat.size() == 1){
            cond = beatPlayersSingle();
        }
        else if(handToBeat.size() == 2){
            if(players.get(turn).getHand().size() >= 2){
                cond = beatPlayersPair();
            }
        }
        else{
            if(players.get(turn).getHand().size() >= 5){
                cond = beatPlayersComb();
            }
        }
        players.get(turn).setComputerHand();
        return cond;
    }
    
    private boolean beatPlayersSingle(){
        boolean cond = true;
        for(int i = 0; cond && i < players.get(turn).getHand().size(); i++){
            if(players.get(turn).getHand().get(i) > handToBeat.get(0)){
                if(players.get(turn).getHand().get(i) / 4 == 12){
                    if(sacrificeTwo()){
                        setHandToBeatSingle(players.get(turn).getHand().get(i), 
                                new ArrayList<Integer>());
                        cond = false;
                    }
                }
                else{
                    setHandToBeatSingle(players.get(turn).getHand().get(i), 
                            new ArrayList<Integer>());
                    cond = false;
                }
            }
        }
        return cond;
    }
    //Computer forces the player to decide on using player's two or their highest card.
    private boolean sacrificeTwo(){ 
        Integer highestCard = highestCardAvailable();
        return players.get(0).getHand().contains(highestCard);
    }
    
    private boolean beatPlayersPair(){
        boolean cond = true;
        ArrayList<ArrayList<Integer>> pairs = players.get(turn).
                searchOnlyPairs();
        for(int i = 0; cond && i < pairs.size(); i++){
            if(pairs.get(i).get(1) > handToBeat.get(1)){
                setHandToBeatComb(pairs.get(i), 
                        new ArrayList<ArrayList<Integer>>());
                cond = false;
            }
        }
        return cond;
    }
    
    private boolean beatPlayersComb(){
        boolean cond = true;
        if(handIdentifier == 0){
            cond = bestCombToBeatPlayers(true, true, true, true);
        }
        else if(handIdentifier == 1){
            cond = bestCombToBeatPlayers(false, true, true, true);
        }
        else if(handIdentifier == 2){
            cond = bestCombToBeatPlayers(false, false, true, true);
        }
        else if(handIdentifier == 3){
            cond = bestCombToBeatPlayers(false, false, false, true);
        }
        else{
            cond = bestCombToBeatPlayers(false, false, false, false);
        }
        return cond;
    }
    
    private boolean bestCombToBeatPlayers(boolean checkStraight,
            boolean checkFlush, boolean checkFull, boolean checkFour){
        boolean cond = true;
        CardHand player = players.get(turn);
        ArrayList<ArrayList<Integer>> straights = player.searchStraight();
        if(checkStraight){
            removeStraightsFromTwos(straights);
            cond = playStraight(straights, cond);
        }
        if(cond && checkFlush){
            cond = flushBeatsPlayers(player, cond);
        }
        if(cond && checkFull){
            ArrayList<ArrayList<Integer>> fulls = player.searchFullHouse();
            removeCombsWithTwos(fulls);
            cond = playFull(fulls, cond);
        }
        if(cond && checkFour){
            ArrayList<ArrayList<Integer>> fours = player.searchFour();
            removeCombsWithTwos(fours);
            cond = playFour(fours, cond);
        }
        if(cond){
            cond = straightFlushBeatsPlayers(straights, player, cond);
        }
        return cond;
        
    }
    
    private void removeStraightsFromTwos(ArrayList<ArrayList<Integer>> 
            straights){
        ArrayList<ArrayList<Integer>> remove = new ArrayList<>();
        for(ArrayList<Integer> straight : straights){
            if(straight.contains(48) || straight.contains(49) || 
                    straight.contains(50) || straight.contains(51)){
                remove.add(straight);
            }
        }
        straights.removeAll(remove);
    }
    
    private boolean flushBeatsPlayers(CardHand player, boolean cond){
        ArrayList<ArrayList<Integer>> flushes = player.searchFlush();
        if(player.getHand().size() > 6){
            removeTwosFromFlushes(flushes);
        }
        for(int i = 0; cond && i < flushes.size(); i++){
            if(flushes.get(i).size() > 5){
                for(int j = 4; cond && j < flushes.get(i).size(); j++){
                    if(handIdentifier < 1 || flushes.get(i).get(j) > 
                            handToBeat.get(4)){
                        adjustFlush(flushes.get(i), flushes.get(i).get(j));
                        setHandToBeatComb(flushes.get(i), 
                                new ArrayList<ArrayList<Integer>>());
                        handIdentifier = 1;
                        cond = false;
                    }
                }
            }
            else if(flushes.get(i).size() == 5){
                if(handIdentifier < 1 || flushes.get(i).get(4) > 
                        handToBeat.get(4)){
                    setHandToBeatComb(flushes.get(i), 
                            new ArrayList<ArrayList<Integer>>());
                    handIdentifier = 1;
                    cond = false;
                }
            }
        }
        return cond;
    }
    
    private void removeTwosFromFlushes(ArrayList<ArrayList<Integer>> flushes){
        for(ArrayList<Integer> flush : flushes){
            if(flush.get(flush.size() - 1) / 4 == 12){
                flush.remove(flush.size() - 1);
            }
        }
    }
    
    private void adjustFlush(ArrayList<Integer> flush, Integer keep){
        ArrayList<Integer> remove = new ArrayList<>();
        for(int i = flush.size() - 1; flush.size() - remove.size() > 5 && 
                i >= 0; i++){
            if(flush.get(i) != keep.intValue() && 
                    players.get(turn).isPair(flush.get(i), 
                    players.get(turn).getCPairs())){
                remove.add(flush.get(i));
            }
        }
        flush.removeAll(remove);
        if(flush.size() > 5){
            remove.clear();
            for(int i = flush.size() - 1; flush.size() - remove.size() > 5; 
                    i++){
                if(flush.get(i) != keep.intValue()){
                    remove.add(flush.get(i));
                }
            }
            flush.removeAll(remove);
        }
    }
    
    private void removeCombsWithTwos(ArrayList<ArrayList<Integer>> combs){
        if(players.get(turn).getHand().size() < 7){
            ArrayList<ArrayList<Integer>> remove = new ArrayList<>();
            for(ArrayList<Integer> comb : combs){
                if(comb.contains(48) || comb.contains(49) || comb.contains(50) 
                        || comb.contains(51)){
                    remove.add(comb);
                }
            }
            combs.removeAll(remove);
        }
    }
    
    private boolean straightFlushBeatsPlayers(ArrayList<ArrayList<Integer>> 
            straights, CardHand player, boolean cond){
        if(player.getHand().size() == 5 && 
                player.straightCheck(player.getHand()) && 
                player.flushCheck(player.getHand())){
            setHandToBeatComb(player.getHand(), 
                    new ArrayList<ArrayList<Integer>>());
            handIdentifier = 4;
            cond = false;
        }
        else{
            for(ArrayList<Integer> straight : straights){
                if(player.flushCheck(straight) && (handIdentifier < 4 || 
                        straight.get(4) > handToBeat.get(4))){
                    setHandToBeatComb(straight, new 
                        ArrayList<ArrayList<Integer>>());
                    handIdentifier = 4;
                    cond = false;
                }
            }
        }
        return cond;
    }
    //Start thread animation after each player's turn.
    private void startCardAnimation(){
        if(turn == 0){
            gp.setButtonPanelVisible(false);
            SingleCardThread sct = new SingleCardThread(gp, 
                    gp.getBackCardImage(), new Point(gp.getMiddleOfScreen().x, 
                    gp.getPlayersYPos()), gp.getMiddleOfScreen());
            sct.start();
            UpdateDelay ud = new UpdateDelay(gp, 400, 0);
            ud.start();
        }
        else if(turn == 1){
            SingleCardThread sct = new SingleCardThread(gp, 
                    gp.getBackCardImage(), new Point(0, 
                    gp.getMiddleOfScreen().y), gp.getMiddleOfScreen());
            sct.start();
            UpdateDelay ud = new UpdateDelay(gp, 400, 1);
            ud.start();
            
        }
        else if(turn == 2){
            SingleCardThread sct = new SingleCardThread(gp, 
                    gp.getBackCardImage(), new Point(gp.getMiddleOfScreen().x,
                    0), gp.getMiddleOfScreen());
            sct.start();
            UpdateDelay ud = new UpdateDelay(gp, 400, 2);
            ud.start();
        }
        else{
            SingleCardThread sct = new SingleCardThread(gp, 
                gp.getBackCardImage(), new Point(gp.getScreenSize().width - 
                gp.getBackCardImage().getPreferredSize().width, 
                gp.getMiddleOfScreen().y), gp.getMiddleOfScreen());
            sct.start();
            UpdateDelay ud = new UpdateDelay(gp, 400, 3);
            ud.start();
        }
    }
    
    
    
    
}
