package pusoydos;

import java.util.*;
//Programmed for the computer AI.
public class CardHand {
    private ArrayList<Integer> hand;
    private boolean playerStatus;
    private ArrayList<ArrayList<Integer>> cFour;
    private ArrayList<ArrayList<Integer>> cFullHouse;
    private ArrayList<ArrayList<Integer>> cFlush;
    private ArrayList<ArrayList<Integer>> cStraight;
    private ArrayList<ArrayList<Integer>> cPairs;
    private ArrayList<Integer> cSingles;
    private ArrayList<Integer> cTwos;

    public CardHand() {
        playerStatus = false;
        cFour = new ArrayList<>();
        cFullHouse = new ArrayList<>();
        cFlush = new ArrayList<>();
        cStraight = new ArrayList<>();
        cPairs = new ArrayList<>();
        cSingles = new ArrayList<>();
        cTwos = new ArrayList<>();
    } 
    public CardHand(boolean playerStatus){
        playerStatus = true;
        cFour = null;
        cFullHouse = null;
        cFlush = null;
        cStraight = null;
        cPairs = null;
        cSingles = null;
        cTwos = null;
    }
    
    public boolean getPlayerStatus(){
        return playerStatus;
    }
    
    public void setHand(ArrayList<Integer> cards){
        hand = cards;
        sortHand(hand);
    }
    
    public ArrayList<Integer> getHand(){
        return hand;
    }
    
    public ArrayList<ArrayList<Integer>> getCFour(){
        return cFour;
    }
    
    public ArrayList<ArrayList<Integer>> getCFullHouse(){
        return cFullHouse;
    }
    
    public ArrayList<ArrayList<Integer>> getCFlush(){
        return cFlush;
    }
    
    public ArrayList<ArrayList<Integer>> getCStraight(){
        return cStraight;
    }
    
    public ArrayList<ArrayList<Integer>> getCPairs(){
        return cPairs;
    }
    
    public ArrayList<Integer> getCSingles(){
        return cSingles;
    }
    
    public ArrayList<Integer> getCTwos(){
        return cTwos;
    }
    
    public void setComputerHand(){
        ArrayList<Integer> handCopy = new ArrayList<>();
        handCopy.addAll(hand);
        clearAll(true);
        getTwos();
        boolean cond = false;   //Indicate whether 2 combinations exist.
        setCombinations(true);
        if(hand.size() > 9){    //Find a possible 2-combination hand. If multiple hands exist with 2 combinations, pick the one that has the highest total sum of remaining cards.
            ArrayList<ArrayList<Integer>> bestComb = new ArrayList<>();
            if(cFour.size() > 0){
                ArrayList<ArrayList<Integer>> combsFour = 
                        twoCombsWithFour();
                bestComb = combsFour;
            }
            if(cFullHouse.size() > 0){
                ArrayList<ArrayList<Integer>> combsFull = twoCombsWithFull();
                bestComb = checkForBestCombs(bestComb, combsFull);
            }
            if(cFlush.size() > 0){
                ArrayList<ArrayList<Integer>> combsFlush = twoCombsWithFlush();
                bestComb = checkForBestCombs(bestComb, combsFlush);
            }
            if(cStraight.size() > 1){
                ArrayList<ArrayList<Integer>> combsStraight = twoStraights();
                bestComb = checkForBestCombs(bestComb, combsStraight);
            }
            if(!bestComb.isEmpty()){
                setTwoCombs(bestComb);
                cond = true;
            }
        }
        if(!cond){  //Two combinations don't exist, so get best possible 5 card combination.
            clearAll(false);
            setCombinations(false);
            oneCombination();
        }
        cPairs = searchOnlyPairs(); //Get pairs from the remaining hand and set singles.
        searchSingles();
        //showAllCards();
        hand = handCopy;
    }
    
    private ArrayList<ArrayList<Integer>> twoCombsWithFour(){
        ArrayList<ArrayList<Integer>> twoCombs = new ArrayList<>();
        if(cFour.size() > 1 ){
            twoCombs = pickFourOfAKind();
        }
        if(cFullHouse.size() > 0){
            ArrayList<ArrayList<Integer>> list = fourAndFullHouse();
            twoCombs = checkForBestCombs(twoCombs, list);
        }
        if(cFlush.size() > 0){
            ArrayList<ArrayList<Integer>> list = fourAndFlush();
            twoCombs = checkForBestCombs(twoCombs, list);
        }
        if(cStraight.size() > 0){
            ArrayList<ArrayList<Integer>> list = fourAndStraight();
            twoCombs = checkForBestCombs(twoCombs, list);
        }
        return twoCombs;
    }
    
    private ArrayList<ArrayList<Integer>> fourAndFullHouse(){
        ArrayList<ArrayList<Integer>> bestComb = new ArrayList<>();
        for(ArrayList<Integer> fourOfAKind : cFour){
            for(ArrayList<Integer> fullHouse : cFullHouse){
                if(shareCards(fourOfAKind, fullHouse).isEmpty()){
                    ArrayList<Integer> fourCopy = oneFourOfAKind(fourOfAKind, 
                            fullHouse), fullCopy = new ArrayList<>();
                    fullCopy.addAll(fullHouse);
                    ArrayList<ArrayList<Integer>> temp = new ArrayList<>();
                    temp.add(fourCopy);
                    temp.add(fullCopy);
                    bestComb = checkForBestCombs(bestComb, temp);
                }
            }
        }
        return bestComb;
    }
    
    private ArrayList<ArrayList<Integer>> fourAndFlush(){
        ArrayList<ArrayList<Integer>> bestComb = new ArrayList<>();
        for(ArrayList<Integer> fourOfAKind : cFour){
            for(ArrayList<Integer> flush : cFlush){
                ArrayList<Integer> sharedCards = shareCards(fourOfAKind, flush),
                        fourCopy = new ArrayList<>(), 
                        flushCopy = new ArrayList<>();
                fourCopy.addAll(fourOfAKind);
                flushCopy.addAll(flush);
                if(!sharedCards.isEmpty()){
                    if(flushCopy.size() - sharedCards.size() >= 5){
                        flushCopy.removeAll(sharedCards);
                        reduceFlush(flushCopy);
                        ArrayList<ArrayList<Integer>> temp = 
                                setFourAndFlush(fourCopy, flushCopy);
                        bestComb = checkForBestCombs(bestComb, temp);
                    }
                }
                else{
                    ArrayList<ArrayList<Integer>> temp = setFourAndFlush(
                            fourCopy, flushCopy);
                    bestComb = checkForBestCombs(bestComb, temp);
                }
            }
        }
        return bestComb;
    }
    
    private ArrayList<ArrayList<Integer>> setFourAndFlush(ArrayList<Integer> 
            fourCopy, ArrayList<Integer> flushCopy){
        ArrayList<ArrayList<Integer>> temp = new ArrayList<>();
        fourCopy = oneFourOfAKind(fourCopy, flushCopy);
        reduceFlush(flushCopy);
        temp.add(fourCopy);
        temp.add(flushCopy);
        return temp;
    }
    
    private ArrayList<ArrayList<Integer>> fourAndStraight(){
        ArrayList<ArrayList<Integer>> bestComb = new ArrayList<>();
        for(ArrayList<Integer> four : cFour){
            for(ArrayList<Integer> straight : cStraight){
                if(shareCards(four, straight).isEmpty()){
                    ArrayList<Integer> fourCopy = oneFourOfAKind(four, 
                            straight), straightCopy = new ArrayList<>();
                    straightCopy.addAll(straight);
                    ArrayList<ArrayList<Integer>> temp = new ArrayList<>();
                    temp.add(fourCopy);
                    temp.add(straightCopy);
                    bestComb = checkForBestCombs(bestComb, temp);
                }
            }
        }
        return bestComb;
    }
    
    private ArrayList<ArrayList<Integer>> twoCombsWithFull(){
        ArrayList<ArrayList<Integer>> twoCombs = new ArrayList<>();
        if(cFullHouse.size() > 1){
            twoCombs = twoFullHouses();
        }
        if(cFlush.size() > 0){
            ArrayList<ArrayList<Integer>> list = fullAndFlush();
            twoCombs = checkForBestCombs(twoCombs, list);
        }
        if(cStraight.size() > 0){
            ArrayList<ArrayList<Integer>> list = fullAndStraight();
            twoCombs = checkForBestCombs(twoCombs, list);
        }
        return twoCombs;
    }
    
    private ArrayList<ArrayList<Integer>> twoFullHouses(){
        ArrayList<ArrayList<Integer>> bestCombs = new ArrayList<>();
        for(int i = 0; i < cFullHouse.size(); i++){
            for(int j = i + 1; j < cFullHouse.size(); j++){
                ArrayList<Integer> full1 = new ArrayList<>();
                ArrayList<Integer> full2 = new ArrayList<>();
                full1.addAll(cFullHouse.get(i));
                full2.addAll(cFullHouse.get(j));
                if(shareCards(full1, full2).isEmpty()){
                    ArrayList<ArrayList<Integer>> temp = new ArrayList<>();
                    temp.add(full1);
                    temp.add(full2);
                    bestCombs = checkForBestCombs(bestCombs, temp);
                }
            }
        }
        return bestCombs;
    }
    
    private ArrayList<ArrayList<Integer>> fullAndFlush(){
        ArrayList<ArrayList<Integer>> bestCombs = new ArrayList<>();
        for(ArrayList<Integer> full : cFullHouse){
            for(ArrayList<Integer> flush : cFlush){
                ArrayList<Integer> sharedCards = shareCards(full, flush), 
                        fullCopy = new ArrayList<>(), flushCopy = 
                        new ArrayList<>();
                fullCopy.addAll(full);
                flushCopy.addAll(flush);
                ArrayList<ArrayList<Integer>> temp;
                if(!sharedCards.isEmpty()){
                    if(flush.size() - sharedCards.size() >= 5){
                        flushCopy.removeAll(sharedCards);
                        reduceFlush(flushCopy);
                        temp = setFullAndFlush(fullCopy, flushCopy);
                        bestCombs = checkForBestCombs(bestCombs, temp);
                    }
                }
                else{
                    temp = setFullAndFlush(fullCopy, flushCopy);
                    bestCombs = checkForBestCombs(bestCombs, temp);
                }
            }
        }
        return bestCombs;
    }
    
   private ArrayList<ArrayList<Integer>> setFullAndFlush(
           ArrayList<Integer> full, ArrayList<Integer> flush){
       ArrayList<ArrayList<Integer>> list = new ArrayList<>();
       list.add(full);
       reduceFlush(flush);
       reduceFlush(flush);
       list.add(flush);
       return list;
   }
   
   private ArrayList<ArrayList<Integer>> fullAndStraight(){
       ArrayList<ArrayList<Integer>> bestCombs = new ArrayList<>();
       for(ArrayList<Integer> full : cFullHouse){
           for(ArrayList<Integer> straight : cStraight){
               if(shareCards(full, straight).isEmpty()){
                   ArrayList<ArrayList<Integer>> temp = new ArrayList<>();
                   ArrayList<Integer> fullCopy = new ArrayList<>(), 
                           straightCopy = new ArrayList<>();
                   fullCopy.addAll(full);
                   straightCopy.addAll(straight);
                   temp.add(full);
                   temp.add(straight);
                   bestCombs = checkForBestCombs(bestCombs, temp);
               }
           }
       }
       return bestCombs;
   }
   
   private ArrayList<ArrayList<Integer>> twoCombsWithFlush(){
       ArrayList<ArrayList<Integer>> twoCombs = new ArrayList<>();
       if(cFlush.size() > 1){
           twoCombs = twoFlushes();
       }
       if(cStraight.size() > 0){
           ArrayList<ArrayList<Integer>> list = flushAndStraight();
           twoCombs = checkForBestCombs(twoCombs, list);
       }
       return twoCombs;
   }
   
   private ArrayList<ArrayList<Integer>> twoFlushes(){
       ArrayList<Integer> flush1 = new ArrayList<>();
       ArrayList<Integer> flush2 = new ArrayList<>();
       flush1.addAll(cFlush.get(0));
       flush2.addAll(cFlush.get(1));
       reduceFlush(flush1);
       reduceFlush(flush2);
       ArrayList<ArrayList<Integer>> twoFlushes = new ArrayList<>();
       twoFlushes.add(flush1);
       twoFlushes.add(flush2);
       return twoFlushes;
   }
   
   private ArrayList<ArrayList<Integer>> flushAndStraight(){
       ArrayList<ArrayList<Integer>> bestCombs = new ArrayList<>();
       for(ArrayList<Integer> flush : cFlush){
           for(ArrayList<Integer> straight : cStraight){
               ArrayList<Integer> flushCopy = new ArrayList<>(), straightCopy =
                       new ArrayList<>();
               flushCopy.addAll(flush);
               straightCopy.addAll(straight);
               ArrayList<ArrayList<Integer>> temp = setFlushAndStraight(
                       flushCopy, straightCopy);
               bestCombs = checkForBestCombs(bestCombs, temp);
           }
       }
       return bestCombs;
   }
   
   private ArrayList<ArrayList<Integer>> setFlushAndStraight(ArrayList<Integer> 
           flush, ArrayList<Integer> straight){
       ArrayList<ArrayList<Integer>> combs = new ArrayList<>();
       ArrayList<Integer> sharedCards = shareCards(flush, straight);
       int pairsInSharedCards = numOfPairsInComb(sharedCards);
       if(sharedCards.isEmpty()){
           reduceFlush(flush);
           combs.add(flush);
           combs.add(straight);
       }
       else if(flush.size() - (sharedCards.size() + pairsInSharedCards) >= 5){
           ArrayList<Integer> removeCards = new ArrayList<>();
           for(Integer card : sharedCards){
               if(!isPair(card, cPairs)){
                   removeCards.add(card);
               }
           }
           flush.removeAll(removeCards);
           if(flush.size() > 5){
               sharedCards.removeAll(removeCards);
               reduceFlush2(flush, sharedCards);
           }
           combs.add(flush);
           combs.add(straight);
       }
       
       
       return combs;
   }
   
   private void reduceFlush2(ArrayList<Integer> flush, 
           ArrayList<Integer> sharedCards){
       if(sharedCards.isEmpty()){   //Take out cards that are shared between the flush and another comb.
           reduceFlush(flush);
       }
       else{
           ArrayList<Integer> remove = new ArrayList<>();
           for(int i = flush.size() - 1; i >= 0; i--){
               if(!sharedCards.contains(flush.get(i)) && isPair(flush.get(i), 
                       cPairs)){
                   remove.add(flush.get(i));
               }
           }
           int i = 0;
           while(i < remove.size() && flush.size() > 5){
               flush.remove(remove.get(i));
           }
           if(flush.size() > 5){
               int index = flush.size() - 5;
               remove = new ArrayList<>();
               for(int j = flush.size() - 1, count = 0; count < index; j--, 
                       count++){
                   remove.add(flush.get(j));
               }
               flush.removeAll(remove);
           }
       }
   }
   
   private ArrayList<ArrayList<Integer>> twoStraights(){
       ArrayList<ArrayList<Integer>> twoStraights = new ArrayList<>();
       for(int i = 0; i < cStraight.size(); i++){
           for(int j = i + 1; j < cStraight.size(); j++){
               ArrayList<Integer> straight1 = new ArrayList<>(), straight2 = 
                       new ArrayList<>();
               straight1.addAll(cStraight.get(i));
               straight2.addAll(cStraight.get(j));
               if(shareCards(straight1, straight2).isEmpty()){
                   ArrayList<ArrayList<Integer>> temp = new ArrayList<>();
                   temp.add(straight1);
                   temp.add(straight2);
                   twoStraights = checkForBestCombs(twoStraights, temp);
               }
           }
       }
       return twoStraights;
   }
    
   private void setTwoCombs(ArrayList<ArrayList<Integer>> bestCombs){
       clearAll(false);
       for(ArrayList<Integer> list : bestCombs){
           if(fourCheck(list)){
               cFour.add(list);
           }
           else if(fullHouseCheck(list)){
               cFullHouse.add(list);
           }
           else if(flushCheck(list)){
               cFlush.add(list);
           }
           else{
               cStraight.add(list);
           }
       }
       adjustHand(bestCombs);
   }
   //Get only 1 combination.
   private void oneCombination(){
       ArrayList<Integer> comb = new ArrayList<>();
       if(cFlush.size() == 1 && numOfPairsInComb(cFlush.get(0)) < 3){
           comb = cFlush.get(0);
       }
       if(cStraight.size() > 0){
           ArrayList<ArrayList<Integer>> bestStraights = bestStraight();
           if(!bestStraights.isEmpty()){
               if(comb.isEmpty()){
                   comb = bestStraights.get(0);
               }
               else{
                   if(numOfPairsInComb(comb) > numOfPairsInComb(
                           bestStraights.get(0))){
                       comb = bestStraights.get(0);
                   }
               }
           }
       }
       if(comb.isEmpty()){
           if(cFour.size() > 0){
               comb = oneFourOfAKind(cFour.get(0), new ArrayList<Integer>());
           }
           else if(cFullHouse.size() > 0 && cPairs.size() > 0){
               comb = getOneFullHouse();
           }
       }
       if(!comb.isEmpty()){
           setOneCombination(comb);
       }
       else{
           clearAll(false);
       }
   }
   
   private void setOneCombination(ArrayList<Integer> comb){
       ArrayList<Integer> list = new ArrayList<>();
       list.addAll(comb);
       clearAll(false);
       if(fourCheck(list)){
           cFour.add(list);
           adjustHand(cFour);
       }
       else if(fullHouseCheck(list)){
           cFullHouse.add(list);
           adjustHand(cFullHouse);
       }
       else if(flushCheck(list)){
           reduceFlush(list);
           cFlush.add(list);
           adjustHand(cFlush);
       }
       else{
           cStraight.add(list);
           adjustHand(cStraight);
       }
   }
   
   private ArrayList<ArrayList<Integer>> bestStraight(){
       ArrayList<ArrayList<Integer>> bestStraights = new ArrayList<>();
       int pairsInBestStraight = 3;
       for(ArrayList<Integer> list : cStraight){
           int numOfPairsInStraight = numOfPairsInComb(list);
           if(numOfPairsInStraight < pairsInBestStraight){
               bestStraights.clear();
               bestStraights.add(list);
               pairsInBestStraight = numOfPairsInStraight;
           }
           else if(numOfPairsInStraight == pairsInBestStraight &&
                   numOfPairsInStraight < 3){
               bestStraights.add(list);
           }
       }
       return bestStraights;
   }
   
   private void clearAll(boolean clearTwos){
       cFour.clear();
       cFullHouse.clear();
       cFlush.clear();
       cStraight.clear();
       cPairs.clear();
       cSingles.clear();
       if(clearTwos){
            cTwos.clear();
       }
   }
    
    //If true, set new combinations list1 becomes list2.
    private ArrayList<ArrayList<Integer>> checkForBestCombs(
            ArrayList<ArrayList<Integer>> list1,
            ArrayList<ArrayList<Integer>> list2){
        if(!list2.isEmpty()){
            if(!list1.isEmpty()){ 
                ArrayList<Integer> rCards1 = 
                        getRemainingCards(list1.get(0), list1.get(1));
                ArrayList<Integer> rCards2 =
                        getRemainingCards(list2.get(0), list2.get(1));
                if(hasPair(rCards1) && hasPair(rCards2)){
                    if(rCards1.get(1) < rCards2.get(1)){
                        list1 = list2;
                    }
                }
                else if(!hasPair(rCards1) && hasPair(rCards2)){
                    list1 = list2;
                }
                else if(!hasPair(rCards1) && !hasPair(rCards2)){
                    if(getSum(rCards1) < getSum(rCards2)){
                        list1 = list2;
                    }
                }
            }
            else{
                list1 = list2;
            }
        }
        return list1;
    }
    
    private boolean hasPair(ArrayList<Integer> list){
        boolean cond = false;
        for(Integer card : list){
            if(isPair(card, cPairs) || isPair(card, cFullHouse)){
                cond = true;
                break;
            }
        }
        return cond;
    }
    
    private int getSum(ArrayList<Integer> list){
        int sum = 0;
        for(Integer card : list){
            sum += card;
        }
        return sum;
    }
    //Check if two combinations share cards.
    private ArrayList<Integer> shareCards(ArrayList<Integer> list1, 
            ArrayList<Integer> list2){
        ArrayList<Integer> sharedCards = new ArrayList<>();
        for(Integer card : list1){
            if(list2.contains(card)){
                sharedCards.add(card);
            }
        }
        sortHand(sharedCards);
        return sharedCards;
    }
    
    private void getTwos(){
        for(int i = hand.size() - 1; i >= 0 && hand.get(i) / 4 == 12; i--){
            cTwos.add(hand.get(i));
        }
        sortHand(cTwos);
        hand.removeAll(cTwos);
    }
    
    public void showAllCards(){
        System.out.println("Twos: " + cTwos);
        System.out.println("Four Of a Kind: " + cFour);
        System.out.println("Full House: " + cFullHouse);
        System.out.println("Flush: " + cFlush);
        System.out.println("Straight: " + cStraight);
        System.out.println("Pairs: " + cPairs);
        System.out.println("Singles: " + cSingles);
    }
    
    public void setCombinations(boolean cond){
        searchSimilarCards();
        if(hand.size() >= 5){
            cFlush = searchFlush();
            cStraight = searchStraight();
        }
        searchSingles();
        if(cond){
            getRestOfThrees();
            cFullHouse = getAllPossibleFullHouses();
        }
    }
    
    private void getRestOfThrees(){
        if(cFour.size() > 0){
            for(ArrayList<Integer> list : cFour){
                cFullHouse.add(new ArrayList<>(Arrays.asList(list.get(0), 
                        list.get(1), list.get(2))));
                cFullHouse.add(new ArrayList<>(Arrays.asList(list.get(0), 
                        list.get(1), list.get(3))));
                cFullHouse.add(new ArrayList<>(Arrays.asList(list.get(0), 
                        list.get(2), list.get(3))));
                cFullHouse.add(new ArrayList<>(Arrays.asList(list.get(1), 
                        list.get(2), list.get(3))));
            }
        }
    }
    
    private ArrayList<ArrayList<Integer>> getAllPossibleFullHouses(){
        ArrayList<ArrayList<Integer>> allFullHouses = new ArrayList<>();
        for(ArrayList<Integer> three : cFullHouse){
            allFullHouses.addAll(pairsFromFourOfAKind(three));
            allFullHouses.addAll(pairsFromOtherThrees(three));
            allFullHouses.addAll(restOfPairs(three));
        }
        return allFullHouses;
    }
    
    private ArrayList<ArrayList<Integer>> pairsFromFourOfAKind
        (ArrayList<Integer> three){
        ArrayList<ArrayList<Integer>> returnList = new ArrayList<>();
        for(ArrayList<Integer> four : cFour){
            if(!three.contains(four.get(0)) && !three.contains(four.get(1)) &&
                    !three.contains(four.get(2)) &&
                    !three.contains(four.get(3))){
                returnList.add(new ArrayList<>(Arrays.asList(three.get(0), 
                        three.get(1), three.get(2), four.get(0), four.get(1))));
                returnList.add(new ArrayList<>(Arrays.asList(three.get(0), 
                        three.get(1), three.get(2), four.get(0), four.get(2))));
                returnList.add(new ArrayList<>(Arrays.asList(three.get(0), 
                        three.get(1), three.get(2), four.get(0), four.get(3))));
                returnList.add(new ArrayList<>(Arrays.asList(three.get(0), 
                        three.get(1), three.get(2), four.get(1), four.get(2))));
                returnList.add(new ArrayList<>(Arrays.asList(three.get(0), 
                        three.get(1), three.get(2), four.get(1), four.get(3))));
                returnList.add(new ArrayList<>(Arrays.asList(three.get(0), 
                        three.get(1), three.get(2), four.get(2), four.get(3))));
            }
        }
        for(ArrayList<Integer> list : returnList){
            sortHand(list);
        }
        return returnList;
    }
        
    private ArrayList<ArrayList<Integer>> pairsFromOtherThrees(
            ArrayList<Integer> three){
        ArrayList<ArrayList<Integer>> returnList = new ArrayList<>();
        ArrayList<ArrayList<Integer>> otherThrees = new ArrayList<>();
        otherThrees.addAll(cFullHouse);
        otherThrees.remove(three);
        for(ArrayList<Integer> list : otherThrees){
            if(!three.contains(list.get(0)) && !three.contains(list.get(1))){
                returnList.add(new ArrayList<>(Arrays.asList(three.get(0), 
                        three.get(1), three.get(2), list.get(0), list.get(1))));
                returnList.add(new ArrayList<>(Arrays.asList(three.get(0), 
                        three.get(1), three.get(2), list.get(0), list.get(2))));
                returnList.add(new ArrayList<>(Arrays.asList(three.get(0), 
                        three.get(1), three.get(2), list.get(1), list.get(2))));
            }
        }
        for(ArrayList<Integer> list : returnList){
            sortHand(list);
        }
        return returnList;
    }
    
    private ArrayList<ArrayList<Integer>> restOfPairs (
            ArrayList<Integer> three){
        ArrayList<ArrayList<Integer>> returnList = new ArrayList<>();
        for(ArrayList<Integer> pair : cPairs){
            
            ArrayList<Integer> temp = new ArrayList<>();
            temp.addAll(three);
            temp.addAll(pair);
            sortHand(temp);
            returnList.add(temp);
        }
        return returnList;
    }
    //Get all types of pairs, and four/three of a kinds.
    private void searchSimilarCards(){
        for(int i = 0; i < hand.size() - 1; i++){
            if((hand.get(i) / 4 == hand.get(i + 1) / 4)){
                if(i < hand.size() - 2 && hand.get(i + 1) / 4 == 
                        hand.get(i + 2) / 4){
                    if(i < hand.size() - 3 && hand.get(i + 2) / 4 == 
                            hand.get(i + 3) / 4){
                        cFour.add(new ArrayList<>(Arrays.asList(hand.get(i), 
                                hand.get(i + 1), hand.get(i + 2), 
                                hand.get(i + 3))));
                        i = i + 3;
                    }
                    else{
                        cFullHouse.add(new ArrayList<>(Arrays.asList(hand.get(i),
                                hand.get(i + 1), hand.get(i + 2))));
                        i = i + 2;
                    }
                }
                else{
                    cPairs.add(new ArrayList<>(Arrays.asList(hand.get(i),
                            hand.get(i + 1))));
                    i = i + 1;
                }
            }   
        }
    }
    
    public ArrayList<ArrayList<Integer>> searchFlush(){
        ArrayList<ArrayList<Integer>> flush = new ArrayList<>();
        ArrayList<Integer> clubs = new ArrayList<>();
        ArrayList<Integer> spades = new ArrayList<>();
        ArrayList<Integer> hearts = new ArrayList<>();
        ArrayList<Integer> diamonds = new ArrayList<>();
        for (Integer card : hand) {
            if (card % 4 == 0) {
                clubs.add(card);
            } 
            else if (card % 4 == 1) {
                spades.add(card);
            }
            else if (card % 4 == 2) {
                hearts.add(card);
            }
            else{
                diamonds.add(card);
            }
        }
        if(clubs.size() >= 5){
            flush.add(clubs);
        }
        if(spades.size() >= 5){
            flush.add(spades);
        }
        if(hearts.size() >= 5){
            flush.add(hearts);
        }
        if(diamonds.size() >= 5){
            flush.add(diamonds);
        }
        return flush;
    }
    
    public ArrayList<ArrayList<Integer>> searchStraight(){
        ArrayList<ArrayList<Integer>> straights = new ArrayList<>();
        ArrayList<Integer> similarCards = removeSimilarCards();
        sortHand(hand);
        for(int i = 0; i < hand.size() - 4; i++){
            ArrayList<Integer> temp = new ArrayList<>(Arrays.asList(hand.get(i), 
                    hand.get(i + 1), hand.get(i + 2), hand.get(i + 3), 
                    hand.get(i + 4)));
            if(straightCheck(temp)){
                straights.add(temp);
            }
        }
        hand.addAll(similarCards);
        sortHand(hand);
        return straights;
    }
    
    public ArrayList<ArrayList<Integer>> searchOnlyPairs(){
        ArrayList<ArrayList<Integer>> pairs = new ArrayList<>();
        for(int i = 0; i < hand.size() - 1; i++){
            if(hand.get(i) / 4 == hand.get(i + 1) / 4){
                pairs.add(new ArrayList<>(Arrays.asList(hand.get(i), 
                        hand.get(i + 1))));
                i++;
            }
        }
        return pairs;
    }
    
    private void searchSingles(){
        cSingles.addAll(hand);
        for(ArrayList<Integer> list : cFour){
            cSingles.removeAll(list);
        }
        for(ArrayList<Integer> list : cFullHouse){
            cSingles.removeAll(list);
        }
        for(ArrayList<Integer> list : cFlush){
            cSingles.removeAll(list);
        }
        for(ArrayList<Integer> list : cStraight){
            cSingles.removeAll(list);
        }
        for(ArrayList<Integer> list : cPairs){
            cSingles.removeAll(list);
        }
    }
   
    public boolean straightCheck(ArrayList<Integer> cards){
        boolean sc = true;
        int i = 0;     
        if((cards.get(3) / 4 == 11 && cards.get(4) / 4 == 12 && 
                cards.get(0) / 4 == 0 && cards.get(1) / 4 == 1 && 
                cards.get(2) / 4 == 2) || (cards.get(4) / 4 == 12 && 
                cards.get(0) / 4 == 0 && cards.get(1) / 4 == 1 && 
                cards.get(2) / 4 == 2 && cards.get(3) / 4 == 3)){
        }
        else{
            while(sc && i < cards.size() - 1){
                int card1 = cards.get(i) / 4;
                int card2 = cards.get(i + 1) / 4;
                if(card1 + 1 != card2){
                    sc = false;
                }    
                i++;
            }
        }
        return sc;
    }
    
    public boolean flushCheck(ArrayList<Integer> cards){
        boolean fc = false;
        if((cards.get(0) % 4) == (cards.get(1) % 4) && (cards.get(1) % 4) == 
                (cards.get(2) % 4) && (cards.get(2) % 4) == (cards.get(3) % 4)
                && (cards.get(3) % 4) == (cards.get(4) % 4)){
            fc = true;
        }
        return fc;
    }
    
    public boolean fullHouseCheck(ArrayList<Integer> cards){
        boolean fhc = false;    
        if((cards.get(0) / 4 == cards.get(1) / 4 && cards.get(1) / 4 == 
                cards.get(2) / 4 && pairCheck(cards.get(3), cards.get(4))) ^ 
                (pairCheck(cards.get(0), cards.get(1)) && cards.get(2) / 4 == 
                cards.get(3) / 4 && cards.get(3) / 4 == cards.get(4) / 4)){
            fhc = true;
        }
        return fhc;
    }
    
    public boolean fourCheck(ArrayList<Integer> cards){
        boolean fc = false;
        if((pairCheck(cards.get(0), cards.get(1)) && pairCheck(cards.get(1), 
                cards.get(2)) && pairCheck(cards.get(2), cards.get(3))) ^ 
                (pairCheck(cards.get(1), cards.get(2)) && pairCheck(cards.get(2), 
                cards.get(3)) && pairCheck(cards.get(3), cards.get(4)))){
            fc = true;
        }
        return fc;
    }
    
    public boolean pairCheck(int c1, int c2){
        boolean pc = false;
        if(c1 / 4 == c2 / 4){
            pc = true;
        }
        return pc;
    }
    
    public  void sortHand(ArrayList<Integer> hand){
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
    
    void showCards(ArrayList<Integer> hand){
        for(int i = 0; i < hand.size(); i++){
            System.out.print(hand.get(i) + " ");
        }
        System.out.println();
    }
    
    private ArrayList<Integer> removeSimilarCards(){
        ArrayList<Integer> similarCards = new ArrayList<>();
        for(int i = 0; i < cFour.size(); i++){
            similarCards.add(cFour.get(i).get(1));
            similarCards.add(cFour.get(i).get(2));
            similarCards.add(cFour.get(i).get(3));
        }
        for(int i = 0; i < cFullHouse.size(); i++){
            similarCards.add(cFullHouse.get(i).get(1));
            similarCards.add(cFullHouse.get(i).get(2));
        }
        for(int i = 0; i < cPairs.size(); i++){
            similarCards.add(cPairs.get(i).get(1));
        }
        hand.removeAll(similarCards);
        return similarCards;
    }
    
    private ArrayList<ArrayList<Integer>> pickFourOfAKind(){
        ArrayList<ArrayList<Integer>> comb;
        if(cFour.size() == 3){
            comb = threeFourOfAKind();
        }
        else if(cFour.size() == 2 && hand.size() + cTwos.size() > 9){
            comb = twoFourOfAKind();
        }
        else{
            comb = new ArrayList<>();
        }
        for(ArrayList<Integer> list : comb){
            sortHand(list);
        }
        return comb;
    }
    
    private ArrayList<ArrayList<Integer>> threeFourOfAKind(){
        ArrayList<ArrayList<Integer>> comb = new ArrayList<>();
        comb.addAll(cFour);
        if(cSingles.isEmpty()){
            comb.get(0).add(comb.get(2).get(0));
            comb.get(1).add(comb.get(2).get(1));
        }
        else{
            ArrayList<Integer> list = getRemainingCards(comb.get(0), 
                    comb.get(1));
            comb.get(0).add(list.get(0));
            comb.get(1).add(list.get(1));
        }
        comb.remove(2);
        return comb;
    }
    
    private ArrayList<ArrayList<Integer>> twoFourOfAKind(){
        ArrayList<ArrayList<Integer>> comb = new ArrayList<>();
        comb.addAll(cFour);
        if(cSingles.size() > 1){
            comb.get(0).add(cSingles.get(0));
            comb.get(1).add(cSingles.get(1));
        }
        else{
            ArrayList<Integer> list = getRemainingCards(comb.get(0), 
                    comb.get(1));
            if(list.isEmpty()){
                comb.get(0).add(comb.get(1).get(0));
                comb.remove(1);
            }
            else if(list.size() == 1){
                comb.get(0).add(list.get(0));
            }
            else{
                comb.get(0).add(list.get(0));
                comb.get(1).add(list.get(1));
            }
        }  
        return comb;
    }
    
    private ArrayList<Integer> oneFourOfAKind(ArrayList<Integer> four, 
            ArrayList<Integer> remove){
        ArrayList<Integer> comb = new ArrayList<>();
        comb.addAll(four);
        if(cSingles.size() > 0){
            comb.add(cSingles.get(0));
        }
        else{
            ArrayList<Integer> list = getRemainingCards(comb, remove);
            comb.add(list.get(0));
        }
        sortHand(comb);
        return comb;
    }
    
    private ArrayList<Integer> getOneFullHouse(){
        ArrayList<Integer> comb = new ArrayList();
        comb.addAll(cFullHouse.get(0));
        ArrayList<Integer> lowestPair = lowestPair();
        comb.addAll(lowestPair);
        sortHand(comb);
        return comb;
    }
    
    private ArrayList<Integer> lowestPair(){
        ArrayList<Integer> returnPair;
        if(cFullHouse.size() > 1){
            if(!cPairs.isEmpty() && 
                    cPairs.get(0).get(0) < cFullHouse.get(1).get(0)){
                returnPair = cPairs.get(0);
            }
            else{
                ArrayList<Integer> temp = cFullHouse.get(1);
                temp.remove(2);
                returnPair = temp;
            }
        }
        else{
            returnPair = cPairs.get(0);
        }
        return returnPair;
    }
    
    private void adjustHand(ArrayList<ArrayList<Integer>> lists){
        for (ArrayList<Integer> list : lists) {
            hand.removeAll(list);
        }
    }
    
    public boolean isPair(int card, ArrayList<ArrayList<Integer>> lists){
        for(ArrayList<Integer> list : lists){
            if(list.contains(card)){
                return true;
            }
        }
        return false;
    }
    //Reduce a flush to size 5.
    private void reduceFlush(ArrayList<Integer> list){
        ArrayList<Integer> remove = new ArrayList<>();
        for(int i = list.size() - 1; i >= 0 && list.size() - 
                remove.size() > 5; i--){
            if(isPair(list.get(i), cPairs)){
                remove.add(list.get(i));
            }
        }
        list.removeAll(remove);
        //Add condition for flush is still more than 5 cards.
        if(list.size() > 5){
            remove.clear();
            int i = list.size() - 1;
            while(list.size() - remove.size() > 5){
                remove.add(list.get(i));
                i--;
            }
            list.removeAll(remove);
        }
    }
    
    private int numOfPairsInComb(ArrayList<Integer> list){
        int pairsInList = 0;
        for(Integer card : list){
            if(isPair(card, cPairs)){
                pairsInList++;
            }
        }
        return pairsInList;
    }
    
    private ArrayList<Integer> getRemainingCards(ArrayList<Integer> comb1, 
            ArrayList<Integer> comb2){
        ArrayList<Integer> list = new ArrayList<>();
        list.addAll(hand);
        list.removeAll(comb1);
        list.removeAll(comb2);
        list.addAll(cTwos);
        sortHand(list);
        return list;
    }
    
    public ArrayList<ArrayList<Integer>> searchFullHouse(){
        ArrayList<ArrayList<Integer>> fulls = new ArrayList<>();
        ArrayList<ArrayList<Integer>> threes = searchThrees();
        ArrayList<ArrayList<Integer>> pairs = searchOnlyPairs();
        for (ArrayList<Integer> three : threes) {
            for (ArrayList<Integer> pair : pairs) {
                if (!three.contains(pair.get(0))) {
                    ArrayList<Integer> full = new ArrayList<>();
                    full.addAll(three);
                    full.addAll(pair);
                    sortHand(full);
                    fulls.add(full);
                }
            }
        }
        return fulls;
    }
    
    public ArrayList<ArrayList<Integer>> searchThrees(){
        ArrayList<ArrayList<Integer>> threes = new ArrayList<>();
        for(int i = 0; i < hand.size() - 2; i++){
            if(hand.get(i) / 4 == hand.get(i + 1) / 4 && hand.get(i + 1) / 4 
                    == hand.get(i + 2) / 4){
                threes.add(new ArrayList<>(Arrays.asList(hand.get(i), 
                        hand.get(i + 1), hand.get(i + 2))));
            }
        }
        return threes;
    }
    
    public ArrayList<ArrayList<Integer>> searchFour(){
        ArrayList<ArrayList<Integer>> fours = new ArrayList<>();
        for(int i = 0; i < hand.size() - 3; i++){
            if(hand.get(i) / 4 == hand.get(i + 1) / 4 && hand.get(i + 1) / 4 ==
                    hand.get(i + 2) / 4 && hand.get(i + 2) / 4 == 
                    hand.get(i + 3) / 4){
                fours.add(new ArrayList<>(Arrays.asList(hand.get(i), 
                        hand.get(i + 1), hand.get(i + 2), hand.get(i + 3))));
                if(!cSingles.isEmpty()){
                    fours.get(i).add(cSingles.get(0));
                }
                else{
                    for(int j = 0; fours.get(i).size() < 5 && 
                            j < hand.size(); j++){
                        if(!fours.get(i).contains(hand.get(j))){
                            fours.get(i).add(hand.get(j));
                        }
                    }
                }
            }
        }
        
        return fours;
    }
}
