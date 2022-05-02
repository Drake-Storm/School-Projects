//I Drake Storm Hackett, 000783796 certify that this material is my original work. No other person's work has been used without due acknowledgement. I have not made my work available to anyone else.

package com.company;

import java.security.SecureRandom;
import java.util.Scanner;

public class Main {

    /**
     * This function generates the doors and sets one to be the winner
     * @param numberOfDoors = the length of the list of doors
     * @return a list of boolean values, most of which automatically set to false with one set to true
     */
    public static boolean[] doors(int numberOfDoors){

        SecureRandom random = new SecureRandom();
        boolean[] theDoors = new boolean[numberOfDoors];
        int winner = random.nextInt(numberOfDoors - 1);
        theDoors[winner] = true;
        return theDoors;
    }

    /**
     * This function will do the experiment of always switching doors for the Monty hall game.
     * First asking the player how many doors to use, then how many simulations to run.
     * It tracks the number of simulations won and at the end outputs that to the player.
     */
    public static void experiment(){
        Scanner scanner = new Scanner(System.in);
        SecureRandom random = new SecureRandom();
        int numDoors = 0;
        int numSimulations = 0;
        System.out.println("How many doors should we use in the experiment? (3 or more)");
        do {
            try {
                numDoors = scanner.nextInt();
                if(numDoors < 3){
                    System.out.println("I'm sorry, we need at least 3 doors to play the game");
                }
            }catch (Exception e){
                System.out.println("Value must be an integer.");
                scanner.next();
            }
        }while (numDoors < 3);

        System.out.println("How many times should we run the experiment? (1 or more)");
        do {
            try {
                numSimulations = scanner.nextInt();
                if(numSimulations < 1){
                    System.out.println("I'm sorry, we need at least 1 run to do the experiment");
                }
            }catch (Exception e){
                System.out.println("Value must be an integer.");
                scanner.next();
            }
        }while (numSimulations < 1);

        int numberOfWins = 0;

        for(int i = 0; i < numSimulations; i++){
            boolean[] theDoors = doors(numDoors);
            int selection = random.nextInt(numDoors);
            boolean[] newDoors = new boolean[2];
            newDoors[0] = theDoors[selection];
            int otherDoor = selection;
            int openDoor = 1;

            if(newDoors[0]){
                while (otherDoor == selection){
                    otherDoor = random.nextInt(theDoors.length - 1);
                }
                newDoors[1] = theDoors[otherDoor];
            }
            else{
                for (int j = 0; j < theDoors.length; j++){
                    if(theDoors[j]){
                        newDoors[1] = theDoors[j];
                    }
                }
            }

            //Switching doors
            openDoor = 0;

            boolean loss = newDoors[openDoor];

            if(!loss){
                numberOfWins += 1;
            }
        }

        System.out.println("See you later player, you simulated " + numSimulations + " games, and won " + numberOfWins + " of them.");
    }

    public static void main(String[] args) {

        System.out.println("Welcome to Drake Storm Hackett's Door Swapper.");

        //Begin interactive game
        Scanner scanner = new Scanner(System.in);
        SecureRandom random = new SecureRandom();
        int numDoors = 0;
        System.out.println("How many doors should we use in this game? (3 or more)");
        do {
            try {
                numDoors = scanner.nextInt();
                if(numDoors < 3){
                    System.out.println("I'm sorry, we need at least 3 doors to play the game");
                }
            }catch (Exception e){
                System.out.println("Value must be an integer.");
                scanner.next();
            }
        }while (numDoors < 3);

        boolean[] theDoors = doors(numDoors);
        int selection = 0;
        System.out.println("Which door would you like to select?");
        do {
            try {
                selection = scanner.nextInt();
                if(selection > numDoors){
                    System.out.println("I'm sorry, there are only " + numDoors + " doors to choose from");
                }
            }catch (Exception e){
                System.out.println("Value must be an integer.");
                scanner.next();
            }
        }while (0 < selection && selection > numDoors);

        boolean[] newDoors = new boolean[2];
        newDoors[0] = theDoors[selection - 1];
        int otherDoor = 0;
        int playerDoor = selection;
        int openDoor = 1;

        if(newDoors[0]){
            otherDoor = playerDoor - 1;
            while (otherDoor == playerDoor - 1){
                otherDoor = random.nextInt(theDoors.length - 1);
            }
            newDoors[1] = theDoors[otherDoor];
        }
        else{
            for (int i = 0; i < theDoors.length; i++){
                if(theDoors[i]){
                    newDoors[1] = theDoors[i];
                    otherDoor = i;
                }
            }
        }
        int compDoor = otherDoor + 1;

        System.out.println("I've eliminated all doors except #" + selection + " that you chose, and door #" + compDoor);
        System.out.println("Before we see which one is the winner, would you like to trade your door for mine? (Y/N)");
        boolean inputOK = false;
        boolean switchDoors = false;
        do {
            try{
                String switching = scanner.next().toLowerCase();
                if(switching.equals("y") || switching.equals("n")){
                    inputOK = true;
                    if(switching.equals("y")){
                        switchDoors = true;
                    }
                }
                else{
                    System.out.println("input must be either y or n");
                }
            }catch (Exception e){
                System.out.println("input must be either y or n");
            }
        } while (!inputOK);

        if(switchDoors){
            playerDoor = compDoor;
            compDoor = selection;
            openDoor = 0;

            System.out.println("Okay player! We've switched doors, #" + playerDoor + " is all yours");
        }

        System.out.print("Opening door #" + compDoor);
        boolean loss = newDoors[openDoor];

        if(loss){
            System.out.println(" - which is a winner! That means your door, #" + playerDoor + " is not the winner.");
        }
        else{
            System.out.println(" - which is a loser! That means your door, #" + playerDoor + " is the winner!");
        }


        System.out.println("Would you be willing to perform an experiment to see if swapping doors is always the better choice for the player to win? (Y/N)");
        boolean experimenting = false;
        inputOK = false;
        do {
            try{
                String willExperiment = scanner.next().toLowerCase();
                if(willExperiment.equals("y") || willExperiment.equals("n")){
                    inputOK = true;
                    if(willExperiment.equals("y")){
                        experimenting = true;
                    }
                }
                else{
                    System.out.println("input must be either y or n");
                }
            }catch (Exception e){
                System.out.println("input must be either y or n");
            }
        } while (!inputOK);

        if(experimenting){
            experiment();
            System.out.println("""
                    I believe always switching doors is a good strategy, especially when there are lots of doors originally.
                    This is because the chances that the original selection was correct is slim, even more so the more doors there were.
                    In my runs of the experiment I have played 10,000 games with 100 doors each time and won over 9,000 every time I ran the experiment.
                    I have also ran the experiment multiple times, playing 10,000 games each time, with only 3 doors and those won over 6,000 times each time I ran the experiment.
                    Therefore, always switching doors will give you the winning door most of the time with even more chances of winning the more doors there were originally.""");     //opinion on always switching doors, with reasoning.
        }
        System.exit(0);
    }
}
