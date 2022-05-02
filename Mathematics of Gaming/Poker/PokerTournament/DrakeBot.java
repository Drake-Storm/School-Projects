// Statement of Authorship:
// I Drake Storm Hackett, 000783796 certify that this material is my original work. No other person's work has been used without due acknowledgement. I have not made my work available to anyone else.

package PokerTournament;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DrakeBot extends pokerPlayer{

    public Map<String, Integer> otherPlayers;                       //Used to track other players and their chip totals
    public ArrayList<String> playersInHand = new ArrayList<>();     //Used to track players in given hand
    public int bigBlind, smallBlind = 0;                            //Used to track blinds
    public int bet;                                                 //Used to track the current bet
    public int putIntoPot;                                          //Used to track how much this bot has already put in the pot
    public boolean possFlush;                                       //Used to track betting/raising amounts
    public boolean possStraight;                                    //Used to track betting/raising amounts
    public boolean possFH;                                          //Used to track betting/raising amounts
    public boolean poss4k;                                          //Used to track betting/raising amounts
    public boolean poss3k;                                          //Used to track betting/raising amounts
    public boolean poss2Pair;                                       //Used to track betting/raising amounts

    public DrakeBot(String name, int chips){
        super(name, chips);
    }

    @Override
    public void notification(String msg) {
        // Notifications from the dealer come through here.
        Pattern startPat = Pattern.compile( "^Starting hand \\d+, please ante up\\.$" );
        Pattern flopPat = Pattern.compile( "^.*Dealer shows (.)(.) (.)(.) (.)(.).*$", Pattern.DOTALL );
        Pattern riverTurnPat = Pattern.compile( "^.*Dealer shows (.)(.).*$", Pattern.DOTALL );
        Pattern playersSeatedPat = Pattern.compile("^Seated at this game are");
        Pattern playerCalledPat = Pattern.compile("calls");
        Pattern playerRaisedPat = Pattern.compile("raised");
        Pattern playerAllInPat = Pattern.compile("all in");
        Pattern playerFoldedPat = Pattern.compile(" has folded\\.");
        Pattern playerBustedPat = Pattern.compile("has busted at hand");
        Pattern playerWinsPat = Pattern.compile("wins");
        Pattern blindsRaise = Pattern.compile("^Large Blind is ");
        Pattern playerPaidBlind = Pattern.compile(" has paid the ");
        Pattern betTracker = Pattern.compile("^The bet is ");
        Matcher matcher;

        // Sample response, you can also call your own processing
        if ( msg.equals( "Hello Players.  New Game Starting." )) {
            System.out.printf( "%s replies:%n\tHello dealer%n", name );
        } else if ( ( matcher = startPat.matcher(msg) ).matches() ) {
            this.holeCards.clear();
            this.tableCards.clear();
            this.playersInHand.clear();
            this.playersInHand.addAll(this.otherPlayers.keySet());
            this.putIntoPot = 0;
            this.bet = 0;
            this.poss2Pair = false;
            this.poss3k = false;
            this.poss4k = false;
            this.possFH = false;
            this.possFlush = false;
            this.possStraight = false;
        } else if ( ( matcher = playerAllInPat.matcher(msg) ).find() ){
            String[] theMessage = msg.split(" ");
            for (String name : theMessage) {
                if (this.otherPlayers.containsKey(name)){
                    this.otherPlayers.put(name, 0);
                    break;
                }
            }
        } else if ( ( matcher = flopPat.matcher(msg) ).matches() || ( matcher = riverTurnPat.matcher(msg) ).matches() ) {
            this.bet = 0;
            String[] card = new String[2];
            for (int i = 1; i <= matcher.groupCount(); i++) {
                card[(i - 1) % 2] = matcher.group(i);
                if (i % 2 == 0) {
                    tableCards.add(card);
                    card = new String[2];
                }
            }
        } else if ( ( matcher = playersSeatedPat.matcher(msg) ).find() ){
            //Initialize other players with starting chip total
            String[] theMessage = msg.split(",| and ");
            this.otherPlayers = new HashMap<String, Integer>();
            for (String message : theMessage) {
                if(!message.contains(name)){
                    if(message.contains(".")){
                        message = message.replace(".", "");
                    }
                    this.otherPlayers.put(message.substring(message.lastIndexOf(" ")+1), this.chipTotal);
                }
            }
        } else if ( ( matcher = playerCalledPat.matcher(msg) ).find() ){
            //When call then remove that amount from other players chips
            String[] theMessage = msg.split(" calls| \\.\\.\\.");
            if(!theMessage[0].contains(name)){
                this.otherPlayers.put(theMessage[0].substring(theMessage[0].lastIndexOf(" ") + 1), (this.otherPlayers.get(theMessage[0].substring(theMessage[0].lastIndexOf(" ") + 1)) - Integer.parseInt(theMessage[1].substring(theMessage[1].lastIndexOf(" ") + 1)) ) );
            }
        } else if ( (matcher = playerRaisedPat.matcher(msg) ).find() ){
            //When raised then remove that amount from other players chips
            String[] theMessage = msg.split(" has called | and raised by | \\.");
            if(!theMessage[0].contains(name)){
                int oldChips = this.otherPlayers.get(theMessage[0].substring(theMessage[0].lastIndexOf(" ") + 1));
                int minus = Integer.parseInt(theMessage[1]) + Integer.parseInt(theMessage[2].substring(0, theMessage[2].length() - 1));
                this.otherPlayers.put(theMessage[0].substring(theMessage[0].lastIndexOf(" ") + 1), oldChips - minus);
            }
        } else if( (matcher = playerFoldedPat.matcher(msg) ).find() ){
            String[] theMessage = msg.split(" ");
            for (String name : theMessage) {
                if (this.playersInHand.contains(name)){
                    this.playersInHand.remove(name);
                    break;
                }
            }
        } else if( (matcher = playerBustedPat.matcher(msg) ).find() ){
            String[] theMessage = msg.split(" ");
            for (String name : theMessage) {
                if (this.otherPlayers.containsKey(name)){
                    this.otherPlayers.remove(name);
                    break;
                }
            }
        } else if( (matcher = playerWinsPat.matcher(msg) ).find() ){
            if(!msg.contains("!")){
                int chipsWon = Integer.parseInt(msg.replaceAll("[^0-9]", ""));
                String[] theMessage = msg.split(" ");
                int oldChips = 0;
                for (String name : theMessage) {
                    if (this.otherPlayers.containsKey(name)){
                        oldChips = this.otherPlayers.get(name);
                        this.otherPlayers.put(name, oldChips + chipsWon);
                        break;
                    }
                }
            }
        }else if ( ( matcher = blindsRaise.matcher(msg) ).find() ) {
            if(this.bigBlind == 0){
                this.bigBlind = 100;
                this.smallBlind = 50;
            }
            else{
                this.bigBlind = this.bigBlind*2;
                this.smallBlind = this.smallBlind*2;
            }
        }else if ( ( matcher = playerPaidBlind.matcher(msg) ).find() ) {
            String[] theMessage = msg.split(" has paid the | blind\\.");
            if(!theMessage[0].contains(this.name)){
                String playerName = theMessage[0].substring(theMessage[0].lastIndexOf(" ") + 1);
                int oldChips = this.otherPlayers.get(playerName);
                if(theMessage[1].equals("small")){
                    this.otherPlayers.put(playerName, oldChips - smallBlind);
                } else if(theMessage[1].equals("big")){
                    this.otherPlayers.put(playerName, oldChips - bigBlind);
                }
            }
            else{
                if(theMessage[1].equals("small")){
                    this.putIntoPot += smallBlind;
                } else if(theMessage[1].equals("big")){
                    this.putIntoPot += bigBlind;
                }
            }
        }
        else if ( ( matcher = betTracker.matcher(msg) ).find() ) {
            String[] theMessage = msg.split("The bet is | to ");
            if(theMessage[2].contains(this.name)){
                this.bet = Integer.parseInt(theMessage[1]);
            }
        }
    }

    @Override
    public String chooseAction(List<String> actions) {
        String[] card1 = this.holeCards.get(0);
        String[] card2 = this.holeCards.get(1);
        boolean pocketFlush = card1[1].equals(card2[1]);
        boolean pocketPair = card1[0].equals(card2[0]);
        if(this.tableCards.isEmpty()){
            //PreFlop
            if(card1[0].equals(card2[0])){
                int theNumber = 0;
                String face = "";
                try {
                    theNumber = Integer.parseInt(card1[0]);
                }
                catch (Exception e) {
                    face = card1[0];
                }
                //Pocket pairs
                if(actions.contains("check")){
                    //No one has bet yet
                    if(actions.contains("bet")){
                        this.poss4k = true;
                        return "bet";
                    }
                    else if(actions.contains("raise")){
                        this.poss4k = true;
                        return "raise";
                    }
                    else{
                        return "check";
                    }
                }
                else{
                    if(theNumber >= 8 || !face.equals("")){
                        if(this.bet == this.bigBlind){
                            if(actions.contains("raise")){
                                this.poss4k = true;
                                return "raise";
                            } else {
                                this.putIntoPot += this.bet;
                                return "call";
                            }
                        }else {
                            this.putIntoPot += this.bet;
                            return "call";
                        }
                    }
                    else{
                        if(actions.contains("call")){
                            this.putIntoPot += this.bet;
                            return "call";
                        }
                    }
                }
            } //Pairs
            else if(card1[1].equals(card2[1])){
                int theNumber1 = 0;
                int theNumber2 = 0;
                //Same suit
                if(card1[0].matches("T|J|Q|K|A")){
                    switch (card1[0]){
                        case "T":
                            theNumber1 = 10;
                            break;
                        case "J":
                            theNumber1 = 11;
                            break;
                        case "Q":
                            theNumber1 = 12;
                            break;
                        case "K":
                            theNumber1 = 13;
                            break;
                        case "A":
                            theNumber1 = 14;
                            break;
                    }
                }
                else{
                    theNumber1 = Integer.parseInt(card1[0]);
                }
                if(card2[0].matches("T|J|Q|K|A")){
                    switch (card2[0]){
                        case "T":
                            theNumber2 = 10;
                            break;
                        case "J":
                            theNumber2 = 11;
                            break;
                        case "Q":
                            theNumber2 = 12;
                            break;
                        case "K":
                            theNumber2 = 13;
                            break;
                        case "A":
                            theNumber2 = 14;
                            break;
                    }
                }
                else{
                    theNumber2 = Integer.parseInt(card2[0]);
                }
                int theDifference = Math.abs(theNumber1 - theNumber2);

                if(theNumber1 == 14 && theNumber2 <= 5){
                    theDifference = theNumber2 - 1;
                }else if (theNumber2 == 14 && theNumber1 <= 5){
                    theDifference = theNumber1 - 1;
                }

                if(theDifference <= 5){
                    //Possible straight flush
                    if (actions.contains("check")){
                        //no bet made
                        if(theNumber1 >= 10 || theNumber2 >= 10){
                            if(actions.contains("bet")){
                                this.possStraight = true;
                                this.possFlush = true;
                                return "bet";
                            }
                            else{
                                this.possStraight = true;
                                this.possFlush = true;
                                return "raise";
                            }
                        }
                        else{
                            return "check";
                        }
                    }
                    else{
                        if(theNumber1 >= 10 || theNumber2 >= 10){
                            if(this.bet == this.bigBlind){
                                if(actions.contains("raise")){
                                    this.possStraight = true;
                                    this.possFlush = true;
                                    return "raise";
                                }
                                else {
                                    this.possStraight = true;
                                    this.possFlush = true;
                                    return "call";
                                }
                            }
                            else{
                                this.putIntoPot += this.bet;
                                return "call";
                            }
                        }
                        else{
                            this.putIntoPot += this.bet;
                            return "call";
                        }
                    }
                }
                else{
                    //possible flush
                    if(actions.contains("check")){
                        //no bets made
                        return "check";
                    }
                    else {
                        if (actions.contains("call")){
                            this.putIntoPot += this.bet;
                            return "call";
                        }
                    }
                }
            } //Same suit

            int theNumber1 = 0;
            int theNumber2 = 0;

            if(card1[0].matches("T|J|Q|K|A")){
                switch (card1[0]){
                    case "T":
                        theNumber1 = 10;
                        break;
                    case "J":
                        theNumber1 = 11;
                        break;
                    case "Q":
                        theNumber1 = 12;
                        break;
                    case "K":
                        theNumber1 = 13;
                        break;
                    case "A":
                        theNumber1 = 14;
                        break;
                }
            }
            else{
                theNumber1 = Integer.parseInt(card1[0]);
            }
            if(card2[0].matches("T|J|Q|K|A")){
                switch (card2[0]){
                    case "T":
                        theNumber2 = 10;
                        break;
                    case "J":
                        theNumber2 = 11;
                        break;
                    case "Q":
                        theNumber2 = 12;
                        break;
                    case "K":
                        theNumber2 = 13;
                        break;
                    case "A":
                        theNumber2 = 14;
                        break;
                }
            }
            else{
                theNumber2 = Integer.parseInt(card2[0]);
            }
            int theDifference = Math.abs(theNumber1 - theNumber2);

            if(theNumber1 == 14 && theNumber2 <= 5){
                theDifference = theNumber2 - 1;
            }else if (theNumber2 == 14 && theNumber1 <= 5){
                theDifference = theNumber1 - 1;
            }

            if(theDifference <= 5){
                //possible straight
                if (actions.contains("check")){
                    //no bet made
                    return "check";
                }
                else{
                    if(actions.contains("call")){
                        if (this.bet <= this.chipTotal/10 && this.putIntoPot <= this.chipTotal/10){
                            this.putIntoPot += this.bet;
                            return "call";
                        }
                    }
                }
            } //possible straight

            if(theNumber1 >= 9 || theNumber2 >= 9){
                if(actions.contains("check")){
                    return "check";
                }else{
                    if (this.bet <= this.chipTotal/2 && this.putIntoPot <= this.chipTotal/4){
                        this.putIntoPot += this.bet;
                        return "call";
                    }
                }
            }
            else{
                if(actions.contains("check")){
                    return "check";
                }else{
                    if (this.bet <= this.chipTotal/10 && this.putIntoPot <= this.chipTotal/10){
                        this.putIntoPot += this.bet;
                        return "call";
                    }
                }
            }
            if (actions.contains("check")){
                return "check";
            }

            if(this.bet == 0){
                return "call";
            }
            return "fold";
        }

        else if(this.tableCards.size() == 3){
            //Flop
            int counter=0;
            boolean opponentsBetter = false;
            Map<String, Integer> suits = new HashMap<>();
            suits.put("D", 0);
            suits.put("S", 0);
            suits.put("H", 0);
            suits.put("C", 0);
            if(pocketFlush){
                suits.put(card1[1], 2);
            }
            else{
                suits.put(card1[1], 1);
                suits.put(card2[1], 1);
            }
            for (String[] card : tableCards) {
                suits.put(card[1], suits.get(card[1]) + 1);
            }
            if (suits.containsValue(3) || suits.containsValue(4) || suits.containsValue(5)){
                //possibility of getting flush

                if(suits.containsValue(5)){
                    //Flush on flop
                    if(actions.contains("raise")){
                        this.possFlush = true;
                        return "raise";
                    }
                    else {
                        if(actions.contains("bet")){
                            this.possFlush = true;
                            return "bet";
                        }
                    }
                }
                else{

                    String suit = "";
                    for (String theSuit : suits.keySet()) {
                        if(suits.get(theSuit) >= 3){
                            suit = theSuit;
                        }
                    }

                    if(!card1[1].equals(suit) && !card2[1].equals(suit) ){
                        //opponents possible flush
                        opponentsBetter = true;
                    }

                    String[][] cards = {card1, card2, this.tableCards.get(0), this.tableCards.get(1), this.tableCards.get(2)};
                    ArrayList<Integer> numbers = new ArrayList<>();
                    for (String[] card : cards) {
                        switch (card[0]){
                            case "A":
                                numbers.add(14);
                                numbers.add(1);
                                break;
                            case "K":
                                numbers.add(13);
                                break;
                            case "Q":
                                numbers.add(12);
                                break;
                            case "J":
                                numbers.add(11);
                                break;
                            case "T":
                                numbers.add(10);
                                break;
                            case "9":
                                numbers.add(9);
                                break;
                            case "8":
                                numbers.add(8);
                                break;
                            case "7":
                                numbers.add(7);
                                break;
                            case "6":
                                numbers.add(6);
                                break;
                            case "5":
                                numbers.add(5);
                                break;
                            case "4":
                                numbers.add(4);
                                break;
                            case "3":
                                numbers.add(3);
                                break;
                            case "2":
                                numbers.add(2);
                                break;
                        }
                    }
                    Collections.sort(numbers);
                    counter = 0;
                    for (int number : numbers) {
                        if(number < numbers.get(3) + 2 && number > numbers.get(3) - 2){
                            counter++;
                        }
                    }
                    if(counter == 3){
                        //3 in a row
                        if(actions.contains("raise")){
                            this.possStraight = true;
                            return "raise";
                        }
                        else {
                            if(actions.contains("bet")){
                                this.possStraight = true;
                                return "bet";
                            }
                        }
                    }
                    else {
                        if(actions.contains("check")){
                            return "check";
                        }
                        else{
                            this.putIntoPot+=this.bet;
                            return "call";
                        }
                    }
                }
            }
            //Check for possible straight
            String[][] cards = {card1, card2, this.tableCards.get(0), this.tableCards.get(1), this.tableCards.get(2)};
            ArrayList<Integer> numbers = new ArrayList<>();
            for (String[] card : cards) {
                switch (card[0]){
                    case "A":
                        numbers.add(14);
                        numbers.add(1);
                        break;
                    case "K":
                        numbers.add(13);
                        break;
                    case "Q":
                        numbers.add(12);
                        break;
                    case "J":
                        numbers.add(11);
                        break;
                    case "T":
                        numbers.add(10);
                        break;
                    case "9":
                        numbers.add(9);
                        break;
                    case "8":
                        numbers.add(8);
                        break;
                    case "7":
                        numbers.add(7);
                        break;
                    case "6":
                        numbers.add(6);
                        break;
                    case "5":
                        numbers.add(5);
                        break;
                    case "4":
                        numbers.add(4);
                        break;
                    case "3":
                        numbers.add(3);
                        break;
                    case "2":
                        numbers.add(2);
                        break;
                }
            }
            Collections.sort(numbers);
            counter = 0;
            for (int number : numbers) {
                if(number < numbers.get(3) + 2 && number > numbers.get(3) - 2){
                    counter++;
                }
            }
            if(counter == 3){
                //3 in a row
                if(actions.contains("check")){
                    return "check";
                }
                else {
                    if(!opponentsBetter){
                        this.putIntoPot+=this.bet;
                        return "call";
                    }
                }
            }else {
                if(actions.contains("check")){
                    return "check";
                }
            }

            int pairCounter = 0;
            Map<Integer, Integer> timesOccurred = new HashMap<>();
            for (int num : numbers) {
                if(timesOccurred.containsKey(num)){
                    int count = timesOccurred.get(num);
                    timesOccurred.put(num, count + 1);
                }
                else{
                    timesOccurred.put(num, 1);
                }
            }
            for (Integer number : timesOccurred.keySet()) {
                if(timesOccurred.get(number) == 2){
                    pairCounter++;
                }
                else if (timesOccurred.get(number) >= 3){
                    if (actions.contains("raise")){
                        this.poss4k = true;
                        return "raise";
                    }
                    else{
                        if(actions.contains("bet")){
                            this.poss4k = true;
                            return "bet";
                        }
                        else{
                            this.putIntoPot += this.bet;
                            return "call";
                        }
                    }
                }
            }
            if (pairCounter >= 2){
                if (actions.contains("raise")){
                    this.possFH = true;
                    return "raise";
                }
                else{
                    if(actions.contains("bet")){
                        this.possFH = true;
                        return "bet";
                    }
                    else{
                        this.putIntoPot += this.bet;
                        return "call";
                    }
                }
            }
            else if (pairCounter == 1){
                int pairNum = 0;
                for (Integer i : timesOccurred.keySet()) {
                    if(timesOccurred.get(i) >=2 ){
                        pairNum = i;
                    }
                }
                if(pairNum >= 10){
                    if(actions.contains("call")){
                        this.putIntoPot+=this.bet;
                        return "call";
                    }
                    else{
                        return "check";
                    }
                }else {
                    if (actions.contains("check")){
                        return "check";
                    }
                    else{
                        if (this.bet <= this.chipTotal/2){
                            this.putIntoPot+=this.bet;
                            return "call";
                        }
                    }
                }
            }

            if(opponentsBetter){
                if (actions.contains("check")){
                    return "check";
                }
                if(this.bet == 0){
                    return "call";
                }
                return "fold";
            }

        }
        else if(this.tableCards.size() == 4){
            //Turn
            boolean opponentsBetter = false;
            String[][] cards = {card1, card2, this.tableCards.get(0), this.tableCards.get(1), this.tableCards.get(2), this.tableCards.get(3)};
            ArrayList<Integer> numbers = new ArrayList<>();
            for (String[] card : cards) {
                switch (card[0]){
                    case "A":
                        numbers.add(14);
                        numbers.add(1);
                        break;
                    case "K":
                        numbers.add(13);
                        break;
                    case "Q":
                        numbers.add(12);
                        break;
                    case "J":
                        numbers.add(11);
                        break;
                    case "T":
                        numbers.add(10);
                        break;
                    case "9":
                        numbers.add(9);
                        break;
                    case "8":
                        numbers.add(8);
                        break;
                    case "7":
                        numbers.add(7);
                        break;
                    case "6":
                        numbers.add(6);
                        break;
                    case "5":
                        numbers.add(5);
                        break;
                    case "4":
                        numbers.add(4);
                        break;
                    case "3":
                        numbers.add(3);
                        break;
                    case "2":
                        numbers.add(2);
                        break;
                }
            }
            Collections.sort(numbers);
            Map<String, Integer> suits = new HashMap<>();
            suits.put("D", 0);
            suits.put("S", 0);
            suits.put("H", 0);
            suits.put("C", 0);
            if(pocketFlush){
                suits.put(card1[1], 2);
            }
            else{
                suits.put(card1[1], 1);
                suits.put(card2[1], 1);
            }
            for (String[] card : tableCards) {
                suits.put(card[1], suits.get(card[1]) + 1);
            }

            if(suits.containsValue(5) || suits.containsValue(6)){
                //Flush

                if(actions.contains("raise")){
                    this.possFlush = true;
                    return "raise";
                }
                else {
                    if(actions.contains("bet")){
                        this.possFlush = true;
                        return "bet";
                    }
                    else{
                        this.putIntoPot += this.bet;
                        return "call";
                    }
                }
            }
            else if(suits.containsValue(4)){
                String suit = "";
                for (String theSuit : suits.keySet()) {
                    if(suits.get(theSuit) >= 3){
                        suit = theSuit;
                    }
                }

                if(!card1[1].equals(suit) && !card2[1].equals(suit) ){
                    //opponents possible flush
                    opponentsBetter = true;
                }

                //Possible flush
                if(actions.contains("check")){
                    if(opponentsBetter){
                        if (actions.contains("check")){
                            return "check";
                        }
                        if(this.bet == 0){
                            return "call";
                        }
                        return "fold";
                    }
                    if(actions.contains("bet")){
                        this.possFlush = true;
                        return "bet";
                    }
                    else{
                        if(actions.contains("raise")){
                            this.possFlush = true;
                            return "raise";
                        }
                        else{
                            this.putIntoPot += this.bet;
                            return "call";
                        }
                    }
                }
                if(actions.contains("call")){
                    if(opponentsBetter){
                        if (actions.contains("check")){
                            return "check";
                        }
                        if(this.bet == 0){
                            return "call";
                        }
                        return "fold";
                    }
                    else{
                        this.putIntoPot += this.bet;
                        return "call";
                    }
                }
            }


            int pairCounter = 0;
            Map<Integer, Integer> timesOccurred = new HashMap<>();
            for (int num : numbers) {
                if(timesOccurred.containsKey(num)){
                    int count = timesOccurred.get(num);
                    timesOccurred.put(num, count + 1);
                }
                else{
                    timesOccurred.put(num, 1);
                }
            }

            for (Integer number : timesOccurred.keySet()) {
                if(timesOccurred.get(number) >= 2){
                    pairCounter++;
                }
                if (timesOccurred.get(number) == 4){
                    if (actions.contains("raise")){
                        this.poss4k = true;
                        return "raise";
                    }
                    else{
                        if(actions.contains("bet")){
                            this.poss4k = true;
                            return "bet";
                        }
                        else{
                            this.putIntoPot += this.bet;
                            return "call";
                        }
                    }
                }
            }
            if(pairCounter >= 2){
                //Check for full house
                if(timesOccurred.containsValue(3)){
                    if (actions.contains("raise")){
                        this.possFH = true;
                        return "raise";
                    }
                    else{
                        if(actions.contains("bet")){
                            this.possFH = true;
                            return "bet";
                        }
                        else{
                            this.putIntoPot += this.bet;
                            return "call";
                        }
                    }
                }
                else{
                    if(actions.contains("check")){
                        return "check";
                    }
                    else{
                        this.putIntoPot += this.bet;
                        return "call";
                    }
                }
            }

            boolean possibleStraight = false;
            if(numbers.contains(numbers.get(0) + 1) && numbers.contains(numbers.get(0) + 2) && numbers.contains(numbers.get(0) + 3)){
                possibleStraight = true;
            }
            else if(numbers.contains(numbers.get(1) + 1) && numbers.contains(numbers.get(1) + 2) && numbers.contains(numbers.get(1) + 3)){
                possibleStraight = true;
            }else if(numbers.contains(numbers.get(2) + 1) && numbers.contains(numbers.get(2) + 2) && numbers.contains(numbers.get(2) + 3)){
                possibleStraight = true;
            }

            if(possibleStraight){
                if(actions.contains("check")){
                    return "check";
                }
                else{
                    if(opponentsBetter){
                        if (actions.contains("check")){
                            return "check";
                        }
                        if(this.bet == 0){
                            return "call";
                        }
                        return "fold";
                    }
                    else{
                        this.putIntoPot += this.bet;
                        return "call";
                    }
                }
            }

            if(timesOccurred.containsValue(3)){
                if(opponentsBetter){
                    if (actions.contains("check")){
                        return "check";
                    }
                    if(this.bet == 0){
                        return "call";
                    }
                    return "fold";
                }
                else{
                    if (actions.contains("raise")){
                        this.poss3k = true;
                        return "raise";
                    }
                    else{
                        if(actions.contains("bet")){
                            this.poss3k = true;
                            return "bet";
                        }
                        else{
                            this.putIntoPot += this.bet;
                            return "call";
                        }
                    }
                }
            }

            if(pairCounter == 1){
                String numOfPair = "";
                for (int num : timesOccurred.keySet()) {
                    if(timesOccurred.get(num) >= 2){
                        numOfPair = String.valueOf(num);
                    }
                }

                if(card1[0].equals( numOfPair) || card2[0].equals(numOfPair)){
                    if(actions.contains("check")){
                        return "check";
                    }
                    else{
                        int theNumberOfThePair = 0;
                        try {
                            theNumberOfThePair = Integer.parseInt(numOfPair);
                        }
                        catch (Exception e){
                            this.putIntoPot += this.bet;
                            return "call";
                        }
                        if(theNumberOfThePair >= 8){
                            this.putIntoPot += this.bet;
                            return "call";
                        }
                    }
                }
            }
        }
        else{
            //River
            String[][] cards = {card1, card2, this.tableCards.get(0), this.tableCards.get(1), this.tableCards.get(2), this.tableCards.get(3)};
            ArrayList<Integer> numbers = new ArrayList<>();
            for (String[] card : cards) {
                switch (card[0]){
                    case "A":
                        numbers.add(14);
                        numbers.add(1);
                        break;
                    case "K":
                        numbers.add(13);
                        break;
                    case "Q":
                        numbers.add(12);
                        break;
                    case "J":
                        numbers.add(11);
                        break;
                    case "T":
                        numbers.add(10);
                        break;
                    case "9":
                        numbers.add(9);
                        break;
                    case "8":
                        numbers.add(8);
                        break;
                    case "7":
                        numbers.add(7);
                        break;
                    case "6":
                        numbers.add(6);
                        break;
                    case "5":
                        numbers.add(5);
                        break;
                    case "4":
                        numbers.add(4);
                        break;
                    case "3":
                        numbers.add(3);
                        break;
                    case "2":
                        numbers.add(2);
                        break;
                }
            }
            Collections.sort(numbers);
            Map<String, Integer> suits = new HashMap<>();
            suits.put("D", 0);
            suits.put("S", 0);
            suits.put("H", 0);
            suits.put("C", 0);
            if(pocketFlush){
                suits.put(card1[1], 2);
            }
            else{
                suits.put(card1[1], 1);
                suits.put(card2[1], 1);
            }
            for (String[] card : tableCards) {
                suits.put(card[1], suits.get(card[1]) + 1);
            }

            if(suits.containsValue(5) || suits.containsValue(6)){
                //Flush

                String suit = "";
                for (String theSuit : suits.keySet()) {
                    if(suits.get(theSuit) >= 5){
                        suit = theSuit;
                    }
                }
                if(pocketFlush){
                    if (actions.contains("raise")){
                        this.possFlush = true;
                        return "raise";
                    }
                    else{
                        if(actions.contains("bet")){
                            this.possFlush = true;
                            return "bet";
                        }
                        else{
                            this.putIntoPot += this.bet;
                            return "call";
                        }
                    }
                }
                else if (card1[1].equals(suit) || card2[1].equals(suit)){
                    if (actions.contains("check")){
                        return "check";
                    }
                    else{
                        this.putIntoPot += this.bet;
                        return "call";
                    }
                }
            }

            if(numbers.contains(numbers.get(0) + 1) && numbers.contains(numbers.get(0) + 2) && numbers.contains(numbers.get(0) + 3) && numbers.contains(numbers.get(0) + 4)){
                //Straight
                if(actions.contains("check")){
                    return "check";
                }
                else{
                    this.putIntoPot += this.bet;
                    return "call";
                }
            } else if(numbers.contains(numbers.get(1) + 1) && numbers.contains(numbers.get(1) + 2) && numbers.contains(numbers.get(1) + 3) && numbers.contains(numbers.get(1) + 4)){
                //Straight
                if(actions.contains("check")){
                    return "check";
                }
                else{
                    this.putIntoPot += this.bet;
                    return "call";
                }
            } else if(numbers.contains(numbers.get(2) + 1) && numbers.contains(numbers.get(2) + 2) && numbers.contains(numbers.get(2) + 3) && numbers.contains(numbers.get(2) + 4)){
                //Straight
                if(actions.contains("check")){
                    return "check";
                }
                else{
                    this.putIntoPot += this.bet;
                    return "call";
                }
            }

            int pairCounter = 0;
            Map<Integer, Integer> timesOccurred = new HashMap<>();
            for (int num : numbers) {
                if(timesOccurred.containsKey(num)){
                    int count = timesOccurred.get(num);
                    timesOccurred.put(num, count + 1);
                }
                else{
                    timesOccurred.put(num, 1);
                }
            }

            if(timesOccurred.containsValue(3) && timesOccurred.containsValue(2)){
                //Full house
                if (actions.contains("raise")){
                    this.possFH = true;
                    return "raise";
                }
                else{
                    if(actions.contains("bet")){
                        this.possFH = true;
                        return "bet";
                    }
                    else{
                        this.putIntoPot += this.bet;
                        return "call";
                    }
                }
            }

            if(timesOccurred.containsValue(4)){
                //4 of a kind
                String num = "";
                for (int number : timesOccurred.keySet()) {
                    if(timesOccurred.get(number) == 4){
                        num = String.valueOf(number);
                    }
                }
                if(card1[0].equals(num) || card2[0].equals(num)){
                    //4 of a kind achieved with hole cards, otherwise they would be there for everyone
                    if (actions.contains("raise")){
                        this.poss4k = true;
                        return "raise";
                    }
                    else{
                        if(actions.contains("bet")){
                            this.poss4k = true;
                            return "bet";
                        }
                        else{
                            this.putIntoPot += this.bet;
                            return "call";
                        }
                    }
                }
            }

            if(timesOccurred.containsValue(3)){
                //3 of a kind
                String num = "";
                for (int number : timesOccurred.keySet()) {
                    if(timesOccurred.get(number) == 3){
                        num = String.valueOf(number);
                    }
                }
                if(card1[0].equals(num) || card2[0].equals(num)){
                    //3 of a kind achieved with hole cards, otherwise they would be there for everyone
                    if (actions.contains("check")){
                        if(actions.contains("bet")){
                            this.poss3k = true;
                            return "bet";
                        }
                        else{
                            if(actions.contains("raise")){
                                this.poss3k = true;
                                return "raise";
                            }
                            else{
                                this.putIntoPot += this.bet;
                                return "call";
                            }
                        }
                    }
                    else{
                        this.putIntoPot += this.bet;
                        return "call";
                    }
                }
            }

            for (Integer number : timesOccurred.keySet()) {
                if(timesOccurred.get(number) >= 2){
                    pairCounter++;
                }
            }

            if(pairCounter >= 2){
                //Two pair
                if (actions.contains("check")){
                    if(actions.contains("bet")){
                        this.poss2Pair = true;
                        return "bet";
                    }
                    else{
                        if(actions.contains("raise")){
                            this.poss2Pair = true;
                            return "raise";
                        }
                        else{
                            this.putIntoPot += this.bet;
                            return "call";
                        }
                    }
                }
                else{
                    this.putIntoPot += this.bet;
                    return "call";
                }
            }
            else if(pairCounter == 1){
                //Pair
                if(actions.contains("check")){
                    return "check";
                }

                if(this.bet <= this.chipTotal/2 || this.putIntoPot >= this.chipTotal/2){
                    this.putIntoPot += this.bet;
                    return "call";
                }
                int num = 0;
                for (int number : timesOccurred.keySet()) {
                    if(timesOccurred.get(number) == 2){
                        num = number;
                    }
                }
                if(num >= 10){
                    this.putIntoPot += this.bet;
                    return "call";
                }
            }
        }

        if(actions.contains("check")){
            return "check";
        }
        if(this.putIntoPot >= this.chipTotal/5){
            boolean highest = true;
            int theNumber1 = 0;
            int theNumber2 = 0;
            //Same suit
            if(card1[0].matches("T|J|Q|K|A")){
                switch (card1[0]){
                    case "T":
                        theNumber1 = 10;
                        break;
                    case "J":
                        theNumber1 = 11;
                        break;
                    case "Q":
                        theNumber1 = 12;
                        break;
                    case "K":
                        theNumber1 = 13;
                        break;
                    case "A":
                        theNumber1 = 14;
                        break;
                }
            }
            else{
                theNumber1 = Integer.parseInt(card1[0]);
            }
            if(card2[0].matches("T|J|Q|K|A")){
                switch (card2[0]){
                    case "T":
                        theNumber2 = 10;
                        break;
                    case "J":
                        theNumber2 = 11;
                        break;
                    case "Q":
                        theNumber2 = 12;
                        break;
                    case "K":
                        theNumber2 = 13;
                        break;
                    case "A":
                        theNumber2 = 14;
                        break;
                }
            }
            else{
                theNumber2 = Integer.parseInt(card2[0]);
            }
            for (String[] card : this.tableCards) {
                int theNumber3 = 0;
                if(card[0].matches("[TJQKA]")){
                    switch (card2[0]) {
                        case "T" -> theNumber3 = 10;
                        case "J" -> theNumber3 = 11;
                        case "Q" -> theNumber3 = 12;
                        case "K" -> theNumber3 = 13;
                        case "A" -> theNumber3 = 14;
                    }
                }
                else{
                    theNumber3 = Integer.parseInt(card[0]);
                }
                if(theNumber3 >= theNumber1 && theNumber3 >= theNumber2){
                    highest = false;
                }
            }
            if (highest){
                //hole cards contain high card on table
                if(this.bet <= this.chipTotal/5){
                    this.putIntoPot += this.bet;
                    return "call";
                }
            }
        }

        if(this.bet == 0){
            return "call";
        }
        if (actions.contains("check")){
            return "check";
        }
        return "fold";
    }

    @Override
    public int betAmount() {
        //to do minimum just put 0, dealer will make it minimum

        //This first part checks if a player in the hand is a lot shorter in chips than this bot.
        //If so then this bot can 'bully' them into either folding or going all in
        boolean bully = false;
        int bullyAmount = 0;
        for (String player : this.playersInHand) {
            if(this.otherPlayers.get(player) <= this.chipTotal/4){
                bully = true;
                bullyAmount = this.otherPlayers.get(player);
            }
        }

        if(bully){
            return bullyAmount;
        }

        if(this.possFlush){
            this.putIntoPot += this.bigBlind * 4;
            return this.bigBlind * 4;
        }
        else if(this.poss4k){
            this.putIntoPot += this.bigBlind * 3;
            return this.bigBlind * 3;
        }
        else if(this.possFH){
            this.putIntoPot += this.bigBlind * 3;
            return this.bigBlind * 3;
        }
        else if(this.poss3k){
            this.putIntoPot += this.bigBlind * 2;
            return this.bigBlind * 2;
        }
        else if(this.possStraight){
            this.putIntoPot += this.bigBlind * 2;
            return this.bigBlind * 2;
        }
        else if(this.poss2Pair){
            this.putIntoPot += this.bigBlind;
            return this.bigBlind;
        }
        return 0;
    }

    @Override
    public int raiseAmount() {
        //to do minimum just put 0, dealer will make it minimum

        //This first part checks if a player in the hand is a lot shorter in chips than this bot.
        //If so then this bot can 'bully' them into either folding or going all in
        boolean bully = false;
        int bullyAmount = 0;
        for (String player : this.playersInHand) {
            if(this.otherPlayers.get(player) <= this.chipTotal/4){
                bully = true;
                bullyAmount = this.otherPlayers.get(player);
            }
        }

        if(bully){
            return bullyAmount;
        }

        if(this.possFlush){
            this.putIntoPot += this.bet + (this.bigBlind * 4);
            return this.bet + (this.bigBlind * 4);
        }
        else if(this.poss4k){
            this.putIntoPot += this.bet + (this.bigBlind * 3);
            return this.bet + (this.bigBlind * 3);
        }
        else if(this.possFH){
            this.putIntoPot += this.bet + (this.bigBlind * 3);
            return this.bet + (this.bigBlind * 3);
        }
        else if(this.poss3k){
            this.putIntoPot += this.bet + (this.bigBlind * 2);
            return this.bet + (this.bigBlind * 2);
        }
        else if(this.possStraight){
            this.putIntoPot += this.bet + (this.bigBlind * 2);
            return this.bet + (this.bigBlind * 2);
        }
        else if(this.poss2Pair){
            this.putIntoPot += this.bet + (this.bigBlind);
            return this.bet + this.bigBlind;
        }
        return 0;
    }
}
