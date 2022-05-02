// Statement of Authorship:
// I Drake Storm Hackett, 000783796 certify that this material is my original work. No other person's work has been used without due acknowledgement. I have not made my work available to anyone else.
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class YahtzeeStrategy {
    // Before performing large numbers of sims, set this to false.
    // Printing to the screen is relatively slow and will cause your game to under perform.
    //final boolean _DEBUG_ = true;
    final boolean _DEBUG_ = false;

    public void debugWrite( String str ) {
        if ( _DEBUG_ )
            System.out.println( str );
    }


    Yahtzee game = new Yahtzee();

    // The basic structure of a turn is that you must roll the dice,
    // choose to score the hand or to reroll some/all of the dice.
    //
    // If scoring you must provide the decision making on what box to score.
    //
    // If rerolling you must provide the decision making on which dice to
    // reroll.
    //
    // You may score or reroll a second time after the first reroll.
    //
    // After the second reroll you must score or scratch (score a 0).


    // Used enumMap instead of boolean[] so I can use enums as indexes.
    // Keep track of which boxes I've already filled.
    Map<Yahtzee.Boxes, Boolean> boxFilled;

    boolean[] keep; // flag array of dice to keep
    int[] roll;  // current turn's dice state
    int upperTrack = 0; //To track the upperscore for if it is on track to getting the bonus


    // Track what pattern matches are in the roll.
    Map<Yahtzee.Boxes, Boolean> thisRollHas;

    public int play() {
        for (int turnNum = 1; turnNum <= 13; turnNum++) {
            debugWrite( game.toString() );
            debugWrite( "Playing turn " + turnNum + ": ");
            boxFilled = game.getScoreState();
            keep = new boolean[5];
            roll = game.play();


            debugWrite( "Turn " + turnNum + " Roll 1: " + Arrays.toString( roll ) );
            thisRollHas = game.has();

            // DO NOT SORT THE ROLL ARRAY - the order is significant!!
            // Since it is easier to reason with sorted arrays, we clone the
            // roll and work off a temporary copy.
            int[] tempRoll = roll.clone();
            Arrays.sort(tempRoll);
            boolean nextRoll = false;

            // does the roll have a yahtzee?
            if (thisRollHas.get(Yahtzee.Boxes.Y)) {
                // note that set score can fail if the pattern doesn't actually match up or
                // if the box is already filled.  Not a problem with yahtzee but maybe for
                // other patterns it is a problem.
                if (game.setScore("Y")) {
                    continue;
                }
            }

            //Score a full house if you can
            if(thisRollHas.get(Yahtzee.Boxes.FH) && !boxFilled.get(Yahtzee.Boxes.FH)){
                game.setScore("FH");
                continue;
            }

            //Check if large straight has already been scored
            if(!boxFilled.get(Yahtzee.Boxes.LS)){
                // does the roll have a large straight?
                if (thisRollHas.get(Yahtzee.Boxes.LS)) {
                    if (game.setScore("LS")) {
                        continue;
                    }
                }else if (thisRollHas.get(Yahtzee.Boxes.SS)) { //If rolled a small straight then keep it and roll for a LS
                    int[] indexes = new int[5];
                    for (int i = 0; i < 4; i++) { //Get rid of duplicates without actually affecting the real roll
                        if(tempRoll[i] == tempRoll[i+1]){
                            tempRoll[i] = 0;
                        }
                    }
                    for (int i = 0; i < 2; i++) {
                        Arrays.sort(tempRoll);

                        if(tempRoll[i + 1] == tempRoll[i] + 1 && tempRoll[i + 2] == tempRoll[i] + 2 && tempRoll[i + 3] == tempRoll[i] + 3){

                            for (int j = 0; j < roll.length; j++) {
                                if(roll[j] == tempRoll[i]){
                                    indexes[0] = j;
                                }
                                if(roll[j] == tempRoll[i+1]){
                                    indexes[1] = j;
                                }
                                if(roll[j] == tempRoll[i+2]){
                                    indexes[2] = j;
                                }
                                if(roll[j] == tempRoll[i+3]){
                                    indexes[3] = j;
                                }
                            }
                            keep[indexes[0]] = true;
                            keep[indexes[1]] = true;
                            keep[indexes[2]] = true;
                            keep[indexes[3]] = true;

                            nextRoll = true;
                            break;
                        }
                    }
                }
            }

            if(!nextRoll){ //Haven't found die to keep yet
                //Get a new sorted array again
                tempRoll = roll.clone();
                Arrays.sort(tempRoll);
                if(!boxFilled.get(Yahtzee.Boxes.SS)){ //If small straight has not already been scored
                    // does the roll have a small straight?
                    if (thisRollHas.get(Yahtzee.Boxes.SS)) {
                        if (game.setScore("SS")) {
                            continue;
                        }
                    }
                }

                // If we have a 3 of a kind or 4 of a kind, roll for yahtzee
                if (thisRollHas.get(Yahtzee.Boxes.FK) || thisRollHas.get(Yahtzee.Boxes.TK)) {
                    // if there is a 3 or 4 of a kind, the middle die is always
                    // part of the pattern, keep any die that matches it
                    for (int i = 0; i < roll.length; i++)
                        if (roll[i] == tempRoll[2]) keep[i] = true;
                        nextRoll = true;
                }
                if(!nextRoll){
                    //If there are any pairs then see if the upper box for that number has been filled, if it has don't keep it
                    //If it is a high number and the upper box was already filled then check if TK or FK have been filled
                    pp:
                    for (int i = 0; i < roll.length; i++) {
                        for (int j = 1; j < roll.length; j++) {
                            for (boolean kept : keep) {//If we already have some kept then we aren't looking for a different set of pairs
                                if(kept){
                                    break pp;
                                }
                            }
                            if(roll[i] == roll[j]){
                                switch (roll[i]){
                                    case 6:
                                        if(!boxFilled.get(Yahtzee.Boxes.U6)){
                                            keep[i] = true;
                                            keep[j] = true;
                                        }
                                        else if(!boxFilled.get(Yahtzee.Boxes.FK) || !boxFilled.get(Yahtzee.Boxes.TK)){
                                            keep[i] = true;
                                            keep[j] = true;
                                        }
                                        break;
                                    case 5:
                                        if(!boxFilled.get(Yahtzee.Boxes.U5)){
                                            keep[i] = true;
                                            keep[j] = true;
                                        }
                                        else if(!boxFilled.get(Yahtzee.Boxes.FK) || !boxFilled.get(Yahtzee.Boxes.TK)){
                                            keep[i] = true;
                                            keep[j] = true;
                                        }
                                        break;
                                    case 4:
                                        if(!boxFilled.get(Yahtzee.Boxes.U4)){
                                            keep[i] = true;
                                            keep[j] = true;
                                        }
                                        else if(!boxFilled.get(Yahtzee.Boxes.FK) || !boxFilled.get(Yahtzee.Boxes.TK)){
                                            keep[i] = true;
                                            keep[j] = true;
                                        }
                                        break;
                                    case 3:
                                        if(!boxFilled.get(Yahtzee.Boxes.U3)){
                                            keep[i] = true;
                                            keep[j] = true;
                                        }else{
                                            keep[i] = false;
                                            keep[j] = false;
                                        }
                                        break;
                                    case 2:
                                        if(!boxFilled.get(Yahtzee.Boxes.U2)){
                                            if(upperTrack >= 0){
                                                keep[i] = true;
                                                keep[j] = true;
                                            }else{
                                                keep[i] = false;
                                                keep[j] = false;
                                            }
                                        }else{
                                            keep[i] = false;
                                            keep[j] = false;
                                        }
                                        break;
                                    case 1:
                                        if(!boxFilled.get(Yahtzee.Boxes.U1)){
                                            if(upperTrack >= 0){
                                                keep[i] = true;
                                                keep[j] = true;
                                            }else{
                                                keep[i] = false;
                                                keep[j] = false;
                                            }
                                        }else{
                                            keep[i] = false;
                                            keep[j] = false;
                                        }
                                        break;
                                }
                            }
                        }
                    }


                }


            }



            // START ROLL 2
            roll = game.play(keep);
            //debugWrite("Kept: " + keep[0] + ", " + keep[1] + ", " + keep[2] + ", " + keep[3] + ", " + keep[4]);
            debugWrite( "Turn " + turnNum + " Roll 2: " + Arrays.toString( roll ) );
            thisRollHas = game.has();
            nextRoll = false;

            // DO NOT SORT THE ROLL ARRAY - the order is significant!!
            // Since it is easier to reason with sorted arrays, we clone the
            // roll and work off a temporary copy.
            tempRoll = roll.clone();
            Arrays.sort(tempRoll);

            // does the roll have a yahtzee?
            if (thisRollHas.get(Yahtzee.Boxes.Y)) {
                // note that set score can fail if the pattern doesn't actually match up or
                // if the box is already filled.  Not a problem with yahtzee but maybe for
                // other paterns it is a problem.
                if (game.setScore("Y")) {
                    continue;
                }
            }

            // does the roll have a large straight?
            if (thisRollHas.get(Yahtzee.Boxes.LS)) {
                if (game.setScore("LS")) {
                    continue;
                }
            }else if (thisRollHas.get(Yahtzee.Boxes.SS)) { //If rolled a small straight then keep it and roll for a LS
                int[] indexes = new int[5];
                for (int i = 0; i < 2; i++) { //Get rid of duplicates without actually affecting the real roll
                    if(tempRoll[i] == tempRoll[i+1]){
                        tempRoll[i] = 0;
                    }
                }
                for (int i = 0; i < 2; i++) {
                    Arrays.sort(tempRoll);
                    if(tempRoll[i + 1] == tempRoll[i] + 1 && tempRoll[i + 2] == tempRoll[i] + 2 && tempRoll[i + 3] == tempRoll[i] + 3){
                        for (int j = 0; j < roll.length; j++) {
                            if(roll[j] == tempRoll[i]){
                                indexes[0] = j;
                            }
                            if(roll[j] == tempRoll[i+1]){
                                indexes[1] = j;
                            }
                            if(roll[j] == tempRoll[i+2]){
                                indexes[2] = j;
                            }
                            if(roll[j] == tempRoll[i+3]){
                                indexes[3] = j;
                            }
                        }
                        keep[indexes[0]] = true;
                        keep[indexes[1]] = true;
                        keep[indexes[2]] = true;
                        keep[indexes[3]] = true;

                        nextRoll = true;
                    }
                }
            }

            if(thisRollHas.get(Yahtzee.Boxes.FH) && !boxFilled.get(Yahtzee.Boxes.FH)){
                if(!boxFilled.get(Yahtzee.Boxes.FH)){
                    game.setScore("FH");
                    continue;
                }
                else{
                    tempRoll = roll.clone();
                    Arrays.sort(tempRoll);
                    for (int i = 0; i < roll.length; i++) {
                        if(roll[i] != tempRoll[2] && keep[i]){ //If kept a pair during first roll but got a TK with another number than ditch the pair
                            keep[i] = false;
                            nextRoll = false;
                        }
                    }
                }

            }

            if(!nextRoll){
                tempRoll = roll.clone();
                Arrays.sort(tempRoll);
                if(!boxFilled.get(Yahtzee.Boxes.SS)){ //If small straight has not already been scored
                    // does the roll have a small straight?
                    if (thisRollHas.get(Yahtzee.Boxes.SS)) {
                        if (game.setScore("SS")) {
                            continue;
                        }
                    }
                }
                // If we have a 4 of a kind, roll for yahtzee
                if (thisRollHas.get(Yahtzee.Boxes.FK)) {
                    // if there is a 4 of a kind, the middle die is always
                    // part of the pattern, keep any die that matches it
                    for (int i = 0; i < roll.length; i++)
                        if (roll[i] == tempRoll[2]) keep[i] = true;
                    nextRoll = true;
                }

                // If we have a 3 of a kind check if it is worth keeping
                // otherwise roll all 5 dice
                if ( thisRollHas.get(Yahtzee.Boxes.TK)) {
                    // if there is a 3 of a kind, the middle die is always
                    // part of the pattern, keep any die that matches it

                    for (int i = 0; i < roll.length; i++) {
                        if (roll[i] == tempRoll[2]) {
                            //If the trip or quad is of either 1's, 2's, or 3's then only keep them if the upper boxes haven't been filled
                            if (roll[i] == 1) {
                                if (!boxFilled.get(Yahtzee.Boxes.U1)) {
                                    keep[i] = true;
                                    nextRoll = true;
                                }
                            } else if (roll[i] == 2) {
                                if (!boxFilled.get(Yahtzee.Boxes.U2)) {
                                    keep[i] = true;
                                    nextRoll = true;
                                }
                            } else if (roll[i] == 3) {
                                if (!boxFilled.get(Yahtzee.Boxes.U3)) {
                                    keep[i] = true;
                                    nextRoll = true;
                                }
                            } else {
                                keep[i] = true;
                                nextRoll = true;
                            }
                        }
                    }
                }
                //If there are already a pair or more that are being kept then do not look for another pair.
                int keptCount = 0;
                for (boolean kept : keep ) {
                    if (kept){
                        keptCount++;
                    }
                }

                if(keptCount >= 2){
                    nextRoll = true;
                }

                if(!nextRoll){
                    for (int i = 0; i < roll.length; i++) {
                        for (int j = 1; j < roll.length; j++) {
                            if(roll[i] == roll[j]){
                                switch (roll[i]){
                                    case 6:
                                        if(!boxFilled.get(Yahtzee.Boxes.U6)){
                                            keep[i] = true;
                                            keep[j] = true;
                                        }
                                        break;
                                    case 5:
                                        if(!boxFilled.get(Yahtzee.Boxes.U5)){
                                            keep[i] = true;
                                            keep[j] = true;
                                        }
                                        break;
                                    case 4:
                                        if(!boxFilled.get(Yahtzee.Boxes.U4)){
                                            keep[i] = true;
                                            keep[j] = true;
                                        }
                                        break;
                                    case 3:
                                        if(!boxFilled.get(Yahtzee.Boxes.U3)){
                                            keep[i] = true;
                                            keep[j] = true;
                                        }else{
                                            keep[i] = false;
                                            keep[j] = false;
                                        }
                                        break;
                                    case 2:
                                        if(!boxFilled.get(Yahtzee.Boxes.U2)){
                                            keep[i] = true;
                                            keep[j] = true;
                                        }else{
                                            keep[i] = false;
                                            keep[j] = false;
                                        }
                                        break;
                                    case 1:
                                        if(!boxFilled.get(Yahtzee.Boxes.U1)){
                                            keep[i] = true;
                                            keep[j] = true;
                                        }else{
                                            keep[i] = false;
                                            keep[j] = false;
                                        }
                                        break;
                                }
                                nextRoll = true;
                            }
                        }
                    }
                    if(!nextRoll){
                        // If no pairs or more then don't keep any single numbers in next roll
                        Arrays.fill(keep, false);
                    }
                }

            }

            // START ROLL 3
            roll = game.play(keep);
            //debugWrite("Kept: " + keep[0] + ", " + keep[1] + ", " + keep[2] + ", " + keep[3] + ", " + keep[4]);
            debugWrite( "Turn " + turnNum + " Roll 3: " + Arrays.toString( roll ) );
            thisRollHas = game.has();

            // DO NOT SORT THE ROLL ARRAY - the order is significant!!
            // Since it is easier to reason with sorted arrays, we clone the
            // roll and work off a temporary copy.
            tempRoll = roll.clone();
            Arrays.sort(tempRoll);

            // MUST SCORE SOMETHING!!
            if (thisRollHas.get(Yahtzee.Boxes.Y))
                if (game.setScore("Y")) {
                    continue;
                }

            if (thisRollHas.get(Yahtzee.Boxes.LS)) {
                if (game.setScore("LS")) {
                    continue;
                }
            }
            if (thisRollHas.get(Yahtzee.Boxes.SS)) {
                if (game.setScore("SS")) {
                    continue;
                }
            }

            if (thisRollHas.get(Yahtzee.Boxes.FK)) {

                //Figure out which number we rolled 4 of
                int numberToScore = 0;
                for (int i = 1; i < roll.length; i++) {
                    if (roll[i] == tempRoll[2]) {
                        numberToScore = roll[i];
                    }
                }
                if(!boxFilled.get(Yahtzee.Boxes.FK)){

                    if(game.getUpperScore() < 63){ //If we haven't gotten the bonus yet
                        switch (numberToScore){
                            case 6:
                                if(!boxFilled.get(Yahtzee.Boxes.U6)){
                                    game.setScore("U6");
                                    upperTrack += 6;
                                    continue;
                                }
                                break;
                            case 5:
                                if(!boxFilled.get(Yahtzee.Boxes.U5)){
                                    game.setScore("U5");
                                    upperTrack += 5;
                                    continue;
                                }
                                break;
                            case 4:
                                if(!boxFilled.get(Yahtzee.Boxes.U4)){
                                    game.setScore("U4");
                                    upperTrack += 4;
                                    continue;
                                }
                                break;
                            case 3:
                                if(!boxFilled.get(Yahtzee.Boxes.U3)){
                                    game.setScore("U3");
                                    upperTrack += 3;
                                    continue;
                                }
                                break;
                            case 2:
                                if(!boxFilled.get(Yahtzee.Boxes.U2)){
                                    game.setScore("U2");
                                    upperTrack += 2;
                                    continue;
                                }
                                break;

                            case 1:
                                if(!boxFilled.get(Yahtzee.Boxes.U1)){
                                    game.setScore("U1");
                                    upperTrack += 1;
                                    continue;
                                }
                                break;
                        }
                    }
                    if (game.setScore("FK")) {
                        continue;
                    }
                }


                switch (numberToScore) { //If we already scored FK
                    case 6:
                        if (!boxFilled.get(Yahtzee.Boxes.U6) && thisRollHas.get(Yahtzee.Boxes.U6)) {
                            game.setScore("U6");
                            upperTrack += 6;
                            continue;
                        }
                        break;
                    case 5:
                        if (!boxFilled.get(Yahtzee.Boxes.U5) && thisRollHas.get(Yahtzee.Boxes.U5)) {
                            game.setScore("U5");
                            upperTrack += 5;
                            continue;
                        }
                        break;
                    case 4:
                        if (!boxFilled.get(Yahtzee.Boxes.U4) && thisRollHas.get(Yahtzee.Boxes.U4)) {
                            game.setScore("U4");
                            upperTrack += 4;
                            continue;
                        }
                        break;
                    case 3:
                        if (!boxFilled.get(Yahtzee.Boxes.U3) && thisRollHas.get(Yahtzee.Boxes.U3)) {
                            game.setScore("U3");
                            upperTrack += 3;
                            continue;
                        }
                        break;
                    case 2:
                        if (!boxFilled.get(Yahtzee.Boxes.U2) && thisRollHas.get(Yahtzee.Boxes.U2)) {
                            game.setScore("U2");
                            upperTrack += 2;
                            continue;
                        }
                        break;
                    case 1:
                        if (!boxFilled.get(Yahtzee.Boxes.U1) && thisRollHas.get(Yahtzee.Boxes.U1)) {
                            game.setScore("U1");
                            upperTrack += 1;
                            continue;
                        }
                        break;
                }
            }

            if (thisRollHas.get(Yahtzee.Boxes.FH) && !boxFilled.get(Yahtzee.Boxes.FH)){
                if(game.setScore("FH")){
                    continue;
                }
            }

            //If this roll has three of a kind then check if we would prefer to put it in one of the upper scores
            if(thisRollHas.get(Yahtzee.Boxes.TK)){
                int number = 0;
                int count = 0;
                for (int i = 0; i < roll.length; i++) {
                    int num = roll[i];
                    int counter = 0;
                    for (int j = 0; j < roll.length; j++) {
                        if(roll[j] == num){
                            counter++;
                        }
                    }

                    if(counter >= 3){
                        number = num;
                        count = counter;
                    }
                }

                if(game.getUpperScore() < 63){
                    if(number == 4){
                        if(!boxFilled.get(Yahtzee.Boxes.U4)){
                            if(game.setScore("U4")){
                                if(count >=4){
                                    upperTrack += 4;
                                }
                                continue;
                            }
                        }
                    }
                    else if(number == 5){

                        if(!boxFilled.get(Yahtzee.Boxes.U5)){
                            if(game.setScore("U5")){
                                if(count >=4){
                                    upperTrack += 5;
                                }
                                continue;
                            }
                        }
                    }
                    else if(number == 6){
                        if(!boxFilled.get(Yahtzee.Boxes.U6)){
                            if(game.setScore("U6")){
                                if(count >=4){
                                    upperTrack += 6;
                                }
                                continue;
                            }
                        }
                    }

                }

                if(number >=3){
                    if (!boxFilled.get(Yahtzee.Boxes.TK)) {
                        if(game.setScore("TK")){
                            continue;
                        }
                    }
                }
            }


            // score it anywhere
            boolean scored = false;
            for (Yahtzee.Boxes b : Yahtzee.Boxes.values()) {
                switch (b) {
                    // yes, at this point I wish I hadn't used strings ...
                    // but I can set priority by rearranging things
                    case SS:
                        if (!boxFilled.get(b) && thisRollHas.get(b)) scored = game.setScore("SS");
                        break;
                    case FH:
                        if (!boxFilled.get(b) && thisRollHas.get(b)) scored = game.setScore("FH");
                        break;
                    case TK:
                        if(thisRollHas.get(b)){
                            int number = 0;
                            int count = 0;
                            for (int i = 0; i < roll.length; i++) {
                                int num = roll[i];
                                int counter = 0;
                                for (int j = 0; j < roll.length; j++) {
                                    if(roll[j] == num){
                                        counter++;
                                    }
                                }
                                if(counter >= 3){
                                    number = num;
                                    count = counter;
                                }
                            }
                            if (thisRollHas.get(b)){
                                if(number == 1){
                                    if(!boxFilled.get(Yahtzee.Boxes.U1)){
                                        scored = game.setScore("U1");
                                        if(count >= 4){
                                            upperTrack += 1;
                                        }
                                    }
                                }if(number == 2){
                                    if(!boxFilled.get(Yahtzee.Boxes.U2)){
                                        scored = game.setScore("U2");
                                        if(count >= 4){
                                            upperTrack += 2;
                                        }
                                    }
                                }if(number == 3){
                                    if(!boxFilled.get(Yahtzee.Boxes.U3)){
                                        scored = game.setScore("U3");
                                        if(count >= 4){
                                            upperTrack += 3;
                                        }
                                    }
                                }if(number == 4){
                                    if(!boxFilled.get(Yahtzee.Boxes.U4)){
                                        scored = game.setScore("U4");
                                        if(count >= 4){
                                            upperTrack += 4;
                                        }
                                    }
                                }if(number == 5){
                                    if(!boxFilled.get(Yahtzee.Boxes.U5)){
                                        scored = game.setScore("U5");
                                        if(count >= 4){
                                            upperTrack += 5;
                                        }
                                    }
                                }if(number == 6){
                                    if(!boxFilled.get(Yahtzee.Boxes.U6)){
                                        scored = game.setScore("U6");
                                        if(count >= 4){
                                            upperTrack += 6;
                                        }
                                    }
                                }
                            }
                            if(!scored){
                                if(!boxFilled.get(b) && thisRollHas.get(b)){
                                    scored = game.setScore("TK");
                                }
                            }
                            break;
                        }

                    case U6:
                        if (!boxFilled.get(b) && thisRollHas.get(b)) {

                            int count = 0;
                            for (int i = 0; i < roll.length; i++) {
                                if(roll[i] == 6){
                                    count++;
                                }
                            }
                            if(count >= 2){
                                scored = game.setScore("U6");
                                if (count == 2){
                                    upperTrack -= 6;
                                }
                                if(count == 4){
                                    upperTrack += 6;
                                }
                            }
                            else {
                                if(turnNum > 10){
                                    scored = game.setScore("U6");
                                    upperTrack -= 12;
                                }
                            }

                        }
                        break;
                    case U5:
                        if (!boxFilled.get(b) && thisRollHas.get(b)) {

                            int count = 0;
                            for (int i = 0; i < roll.length; i++) {
                                if(roll[i] == 5){
                                    count++;
                                }
                            }
                            if(count >= 2){
                                scored = game.setScore("U5");
                                if (count == 2){
                                    upperTrack -= 5;
                                }
                                if(count == 4){
                                    upperTrack += 5;
                                }
                            }
                            else {
                                if(turnNum > 10){
                                    scored = game.setScore("U5");
                                    upperTrack -= 10;
                                }
                            }

                        }
                        break;
                    case U4:
                        if (!boxFilled.get(b) && thisRollHas.get(b)){

                            int count = 0;
                            for (int i = 0; i < roll.length; i++) {
                                if(roll[i] == 4){
                                    count++;
                                }
                            }
                            if(count >= 2){
                                scored = game.setScore("U4");
                                if(count == 2){
                                    upperTrack -= 4;
                                }
                                if(count == 4){
                                    upperTrack += 4;
                                }
                            }
                            else {
                                if(turnNum > 10){
                                    scored = game.setScore("U4");
                                    upperTrack -= 8;
                                }
                            }

                        }
                        break;
                    case U3:
                        if (!boxFilled.get(b) && thisRollHas.get(b)) {

                            int count = 0;
                            for (int i = 0; i < roll.length; i++) {
                                if(roll[i] == 3){
                                    count++;
                                }
                            }
                            if(count >= 2){
                                scored = game.setScore("U3");
                                if(count == 2){
                                    upperTrack -= 3;
                                }
                                if(count == 4){
                                    upperTrack += 3;
                                }
                            }
                            else {
                                if(turnNum > 10){
                                    scored = game.setScore("U3");
                                    upperTrack -= 6;
                                }
                            }

                        }
                        break;
                    case U2:
                        if (!boxFilled.get(b) && thisRollHas.get(b)) {
                            scored = game.setScore("U2");
                            int count = 0;
                            for (int i = 0; i < roll.length; i++) {
                                if(roll[i] == 3){
                                    count++;
                                }
                            }
                            if(count == 2){
                                upperTrack -= 2;
                            }
                            if(count == 4){
                                upperTrack += 2;
                            }
                        }
                        break;
                    case U1:
                        if (!boxFilled.get(b) && thisRollHas.get(b)) {
                            scored = game.setScore("U1");
                            int count = 0;
                            for (int i = 0; i < roll.length; i++) {
                                if(roll[i] == 3){
                                    count++;
                                }
                            }
                            if(count == 2){
                                upperTrack -= 1;
                            }
                            if(count == 4){
                                upperTrack += 1;
                            }
                        }
                        break;
                    case C:
                        int score = 0;
                        for (int die : roll) {
                            score += die;
                        }
                        if(score >= 15 || turnNum > 10){
                            if (!boxFilled.get(b) && thisRollHas.get(b)) scored = game.setScore("C");
                        }
                        break;
                }

                if (scored) {
                    break;
                }
            }

            boolean scratched = false;
            if (!scored) {
                // must scratch, let's do it stupidly
                for (Yahtzee.Boxes b : Yahtzee.Boxes.values()) {

                    int upperScore = game.getUpperScore();
                    if(upperScore >= 63){ //Changes the order
                        switch (b){
                            case U1 :
                                if (!boxFilled.get(b)) scratched = game.scratchBox(b);
                                break;
                            case U2 :
                                if (!boxFilled.get(b)) scratched = game.scratchBox(b);
                                break;
                            case U3 :
                                if (!boxFilled.get(b)) scratched = game.scratchBox(b);
                                break;
                            case U4 :
                                if (!boxFilled.get(b)) scratched = game.scratchBox(b);
                                break;
                            case U5 :
                                if (!boxFilled.get(b)) scratched = game.scratchBox(b);
                                break;
                            case U6 :
                                if (!boxFilled.get(b)) scratched = game.scratchBox(b);
                                break;
                            case Y :
                                if(!boxFilled.get(Yahtzee.Boxes.C)){
                                    scored = game.setScore("C");
                                }
                                else if (!boxFilled.get(b)) scratched = game.scratchBox(b);
                                break;
                            case TK :
                                if(!boxFilled.get(Yahtzee.Boxes.C)){
                                    scored = game.setScore("C");
                                }
                                else if (!boxFilled.get(b)) scratched = game.scratchBox(b);
                                break;
                            case FK :
                                if(!boxFilled.get(Yahtzee.Boxes.C)){
                                    scored = game.setScore("C");
                                }
                                else if (!boxFilled.get(b)) scratched = game.scratchBox(b);
                                break;
                            case FH :
                                if(!boxFilled.get(Yahtzee.Boxes.C)){
                                    scored = game.setScore("C");
                                }
                                else if (!boxFilled.get(b)) scratched = game.scratchBox(b);
                                break;
                            case SS :
                                if(!boxFilled.get(Yahtzee.Boxes.C)){
                                    scored = game.setScore("C");
                                }
                                else if (!boxFilled.get(b)) scratched = game.scratchBox(b);
                                break;
                            case LS :
                                if(!boxFilled.get(Yahtzee.Boxes.C)){
                                    scored = game.setScore("C");
                                }
                                else if (!boxFilled.get(b)) scratched = game.scratchBox(b);
                                break;
                        }
                    }
                    else{
                        switch (b){
                            case U1 :
                                if (!boxFilled.get(b)) {
                                    scratched = game.scratchBox(b);
                                    upperTrack -= 3;
                                }
                                break;
                            case U2 :
                                if (!boxFilled.get(b)) {
                                    scratched = game.scratchBox(b);
                                    upperTrack -= 6;
                                }
                                break;
                            case Y :
                                if(!boxFilled.get(Yahtzee.Boxes.C)){
                                    scored = game.setScore("C");
                                }
                                else if (!boxFilled.get(b)) scratched = game.scratchBox(b);
                                break;
                            case TK :
                                if(!boxFilled.get(Yahtzee.Boxes.C)){
                                    scored = game.setScore("C");
                                }
                                else if (!boxFilled.get(b)) scratched = game.scratchBox(b);
                                break;
                            case FK :
                                if(!boxFilled.get(Yahtzee.Boxes.C)){
                                    scored = game.setScore("C");
                                }
                                else if (!boxFilled.get(b)) scratched = game.scratchBox(b);
                                break;
                            case FH :
                                if(!boxFilled.get(Yahtzee.Boxes.C)){
                                    scored = game.setScore("C");
                                }
                                else if (!boxFilled.get(b)) scratched = game.scratchBox(b);
                                break;
                            case SS :
                                if(!boxFilled.get(Yahtzee.Boxes.C)){
                                    scored = game.setScore("C");
                                }
                                else if (!boxFilled.get(b)) scratched = game.scratchBox(b);
                                break;
                            case LS :
                                if(!boxFilled.get(Yahtzee.Boxes.C)){
                                    scored = game.setScore("C");
                                }
                                else if (!boxFilled.get(b)) scratched = game.scratchBox(b);
                                break;
                            case U3 :
                                if(!boxFilled.get(Yahtzee.Boxes.C)){
                                    scored = game.setScore("C");
                                }
                                else if (!boxFilled.get(b)) {
                                    scratched = game.scratchBox(b);
                                    upperTrack -= 9;
                                }
                                break;
                            case U4 :
                                if(!boxFilled.get(Yahtzee.Boxes.C)){
                                    scored = game.setScore("C");
                                }
                                else if (!boxFilled.get(b)) {
                                    scratched = game.scratchBox(b);
                                    upperTrack -= 12;
                                }
                                break;
                            case U5 :
                                if(!boxFilled.get(Yahtzee.Boxes.C)){
                                    scored = game.setScore("C");
                                }
                                else if (!boxFilled.get(b)) {
                                    scratched = game.scratchBox(b);
                                    upperTrack -= 15;
                                }
                                break;
                            case U6 :
                                if(!boxFilled.get(Yahtzee.Boxes.C)){
                                    scored = game.setScore("C");
                                }
                                else if (!boxFilled.get(b)){
                                    scratched = game.scratchBox(b);
                                    upperTrack -= 18;
                                }
                                break;
                        }
                    }


                    if(scored){
                        debugWrite("Scored Chance");
                        scored = true;
                        break;
                    }

                    if (scratched) {
                        debugWrite("Scratched: " + String.valueOf(b));
                        break;
                    }
                }
            }

            if (!scored && !scratched){
                debugWrite("Invalid!!!!"); //To see exactly which turn is invalid
                System.err.println("Invalid game state, can't score, can't scratch.");
            }


            debugWrite( game.toString() );
        }
        return game.getGameScore() >= 0 ? game.getGameScore() : 0;
    }

}