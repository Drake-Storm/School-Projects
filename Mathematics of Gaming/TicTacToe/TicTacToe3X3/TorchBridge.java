// Statement of Authorship:
// I Drake Storm Hackett, 000783796 certify that this material is my original work. No other person's work has been used without due acknowledgement. I have not made my work available to anyone else.

import java.util.*;

public class TorchBridge {

    //Variables
    public static boolean[] people = new boolean[6];    //The people to get across the bridge
    public static int[] times = setTimes();             //Each time for person to get across the bridge in minutes
    public static int totalCrossingTime = 25;           //The total time allowed for crossing
    public static boolean debugMode = false;             //Determines whether in debug mode or speed mode
    public static ArrayDeque<String> solutions = new ArrayDeque<>();              //tracks all solutions
    public static ArrayList<Integer> solutionTimes = new ArrayList<>();


    public static void debugWrite( String str ) {
        if ( debugMode )
            System.out.println( str );
    }


    /**
     * This is to set the times for each person to get across the bridge.
     * Just add each time in minutes as an int.
     */
    public static int[] setTimes() {
        int[] theTimes = {1, 2, 5, 7, 3, 4};
        return theTimes;
    }

    /**
     * This function will call the torchBridge function, getting a solution each time.
     * Then it will lower the totalCrossingTime and run it again,
     * this will continue until the torchBridge function does not get a solution.
     * This allows for all solutions to be found, even if it does take a while.
     * At the end it will print all the solutions
     */
    public static void solveTorchBridge(){
        boolean stop = false;

        while (!stop){

            if (torchBridge(people, times, 0, false, new LinkedList<>())){
                debugWrite("Time: " + totalCrossingTime + " solution: " + solutions.peek());
                for (int i = 0; i < people.length; i++) {
                    people[i] = false;
                }
                totalCrossingTime--;
                System.out.println("New Time: " + totalCrossingTime);
                stop = false;
            }else{
                debugWrite("Should have returned false");
                stop = true;
            }
        }
        printSolutions(solutions);
    }

    /**
     * This function will attempt to find a solution.
     * It iterates through all the people and sees which combination in which order will provide a solution
     * that is under the totalCrossingTime.
     * @param thosePeople = the people attempting to cross the bridge, represented as an array of boolean. true if on desired side, false if not
     * @param theTimes = the times for each person to cross the bridge individually, represented as an array of int
     * @param currentTime = the current time that has been taken up as an int
     * @param torch = boolean representing which side the torch is on (false = starting side), this allows to make sure no one crosses without the torch.
     * @param solution = Queue of strings representing each move made thus far
     * @return boolean value representing if a valid solution has been found
     */
    private static boolean torchBridge(boolean[] thosePeople, int[] theTimes, int currentTime, boolean torch, Deque<String> solution){
        boolean[] thePeople = thosePeople;

        if(!checktheTime(currentTime)){ //goes over the total time
            return false;
        }

        //Check if everyone is already on desired side
        boolean finished = true;
        for (boolean person : thePeople) {
            if(!person){
                finished = false;
            }
        }
        if(finished){
            if(!solutionTimes.contains(currentTime)){
                solutions.add(printSolution(solution, currentTime));
                solutionTimes.add(currentTime);
            }
            return true;
        }


        //Iterate through everyone
        for (int i = 0; i < thePeople.length; i++) {

            //Make sure the person could potentially cross within the allotted time
            if(theTimes[i] + currentTime <= totalCrossingTime){
                //Make sure they are on the same side as the torch
                if(thePeople[i] == torch){
                    for (int j = 1; j < thePeople.length; j++) {
                        //Make sure the person could potentially cross within the allotted time
                        if(theTimes[j] + currentTime <= totalCrossingTime){
                            //Make sure they are not the same person but that they are on the same side
                            if( (i != j) && (thePeople[j] == torch)){
                                if(thePeople[j] == torch && thePeople[i] == torch && (solution.isEmpty() || !solution.peek().equals(i + "&" + j + ", "))){
                                    solution.addFirst(i + "&" + j + ", ");
                                    thePeople[i] = !thePeople[i];
                                    thePeople[j] = !thePeople[j];
                                    int currentlyTime = currentTime;
                                    if(theTimes[i] >= theTimes[j]){
                                        currentlyTime += theTimes[i];
                                    }
                                    else{
                                        currentlyTime += theTimes[j];
                                    }

                                    if(torchBridge(thePeople, theTimes, currentlyTime, !torch, solution)){
                                        debugWrite("current positions: " + Arrays.toString(thePeople) + " Time: " + currentlyTime);
                                        return true;
                                    }
                                    else {

                                        solution.pop();
                                        thePeople[i] = !thePeople[i];
                                        thePeople[j] = !thePeople[j];
                                        debugWrite("Moving back one state");
                                        debugWrite("current positions: " + Arrays.toString(thePeople) + " Time: " + currentTime);
                                    }

                                }
                                if(solution.isEmpty() || !solution.peek().equals(i + ", ")){
                                    solution.addFirst(i + ", ");
                                    thePeople[i] = !thePeople[i];
                                    int currentlyTime = currentTime + theTimes[i];
                                    if(torchBridge(thePeople, theTimes, currentlyTime, !torch, solution)){
                                        debugWrite("current positions: " + Arrays.toString(thePeople) + " Time: " + currentlyTime);
                                        return true;
                                    }
                                    else {

                                        solution.pop();
                                        thePeople[i] = !thePeople[i];
                                        debugWrite("Moving back one state");
                                        debugWrite("current positions: " + Arrays.toString(thePeople) + " Time: " + currentTime);
                                    }
                                }
                            }
                        }

                    }

                    if(solution.isEmpty() || !solution.peek().equals(i + ", ")){
                        solution.addFirst(i + ", ");
                        thePeople[i] = !thePeople[i];
                        int currentlyTime = currentTime + theTimes[i];
                        if(torchBridge(thePeople, theTimes, currentlyTime, !torch, solution)){
                            debugWrite("current positions: " + Arrays.toString(thePeople) + " Time: " + currentlyTime);
                            return true;
                        }
                        else {
                            solution.pop();
                            thePeople[i] = !thePeople[i];
                            debugWrite("Moving back one state");
                            debugWrite("current positions: " + Arrays.toString(thePeople) + " Time: " + currentTime);
                        }
                    }
                }
            }


        }
        return false;
    }

    /**
     * This will print the solution to the screen as well as return it, so it can be added to the rest of the solutions.
     * Each move in the solution will be represented by numbers separated by commas, each number is the index of a person
     * in the people array.
     * @param solution = a queue of moves that got everyone to the other side of the bridge within the allotted time
     * @param time = the time the solution took
     * @return A string representing the solution
     */
    private static String printSolution(Deque<String> solution, int time) {
        String theSolution = "";
        while (!solution.isEmpty()) {
            theSolution += solution.pollLast() + " ";
        }
        theSolution += " in time: " + time;
        System.out.println(theSolution);
        return theSolution;
    }

    /**
     * This will display all the solutions
     * @param solutions = a queue of all the solutions found.
     */
    private static void printSolutions(Deque<String> solutions) {
        System.out.print("Solutions (Most optimal first): ");
        while (!solutions.isEmpty()) {
            System.out.print(solutions.pollLast() + "   &   ");
        }
        System.out.println();
    }


    /**
     * Check the current time is not over the total time
     * @param currentTime = the amount of time spent thus far
     * @return true if the current time is valid, false if not
     */
    private static boolean checktheTime(int currentTime){
        return (currentTime <= totalCrossingTime);
    }

    public static void main(String[] args) {
        solveTorchBridge();
    }
}
