// Statement of Authorship:
// I Drake Storm Hackett, 000783796 certify that this material is my original work. No other person's work has been used without due acknowledgement. I have not made my work available to anyone else.
package com.company;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

class Node {
    public int x;
    public int y;
    public LinkedList<Node> neighbours = new LinkedList<>();

    public Node(int x, int y ){
        this.x = x;
        this.y = y;
    }

    public void addNeighbour(Node node){
        this.neighbours.add(node);
    }

    @Override
    public String toString() {
        return "( " + x + ", " + y + ')';
    }
}


public class Main {

    private static final String heuristic = "Manhattan";
    public static Node goalNode = new Node(0, 0);
    public static Node startNode = new Node(0, 0);
    final static boolean _DEBUG_ = false;

    /**
     * This will print any debugging output provided that _DEBUG_ is true
     * @param str = the string wishing to print
     */
    public static void debugWrite( String str ) {
        if ( _DEBUG_ )
            System.out.println( str );
    }

    /**
     * This is the Heuristic function for finding the fScores
     * @param fromNode = the node to find the fScore of
     * @return the distance from the current node to the goal node
     */
    public static double h(Node fromNode){
        if(heuristic.equals("Euclidean")){
            return Math.sqrt(Math.pow((fromNode.x - goalNode.x), 2) + Math.pow((fromNode.y - goalNode.y), 2));
        }
        else if(heuristic.equals("Manhattan")){
            return ((Math.abs(fromNode.x - goalNode.x)) + (Math.abs(fromNode.y - goalNode.y)));
        }
        else{
            return Double.MAX_VALUE;
        }
    }

    /**
     * This will reconstruct the shortest path given
     * @param cameFrom = a HashMap of nodes representing the previous steps taken
     * @param current = a Node representing the end of the desired path
     * @return a LinkedList of Nodes representing the shortest path
     */
    public static LinkedList<Node> ReconstructPath(HashMap<Node, Node> cameFrom, Node current){
        LinkedList<Node> totalPath = new LinkedList<>();
        totalPath.add(current);

        while (cameFrom.containsKey(current)){
            current = cameFrom.get(current);
            totalPath.add(current);
        }
        return totalPath;
    }

    /**
     * This is the AStar path finding algorithm
     * @param maze = a 2D array representing a map
     * @return a LinkedList showing the shortest path through the map, with each node representing the x and y indexes in the 2d array
     */
    public static LinkedList<Node> AStar(int[][] maze ){
        LinkedList<Node> openSet = new LinkedList<>();
        openSet.add(startNode);

        HashMap<Node, Node> cameFrom = new HashMap<Node, Node>();
        HashMap<String, Double> gScore = new HashMap<String, Double>();
        gScore.put(startNode.toString(), (double) 0);

        HashMap<String, Double> fScore = new HashMap<String, Double>();
        fScore.put(startNode.toString(), h(startNode));

        while(!openSet.isEmpty()){
            double lowVal = Integer.MIN_VALUE;
            Node current = startNode;
            for (Node node : openSet) {
                if(fScore.get(node.toString()) > lowVal){
                    lowVal = fScore.get(node.toString());
                    current = node;
                }
            }
            debugWrite("Current Node: " + current.toString());
            debugWrite("Open Set: " + openSet.toString());

            if (current.toString().equals(goalNode.toString())){
                return ReconstructPath(cameFrom, current);
            }
            openSet.remove(current);

            //Add neighbours but make sure they are not out of bounds
            if(current.x != 0){
                if(current.y != 0){
                    current.addNeighbour(new Node(current.x - 1, current.y - 1));
                }
                current.addNeighbour(new Node(current.x - 1, current.y));

                if(maze[current.x].length > current.y + 1){
                    current.addNeighbour(new Node(current.x - 1, current.y + 1));
                }
            }
            if(maze.length > (current.x + 1)){
                if(current.y != 0){
                    current.addNeighbour(new Node(current.x + 1, current.y - 1));
                }
                current.addNeighbour(new Node(current.x + 1, current.y));

                if(maze[current.x].length > current.y + 1){
                    current.addNeighbour(new Node(current.x + 1, current.y + 1));
                }
            }
            if(current.y != 0){
                current.addNeighbour(new Node(current.x, current.y - 1));
            }
            if(maze[current.x].length > current.y + 1){
                current.addNeighbour(new Node(current.x , current.y + 1));
            }

            for (Node neighbour : current.neighbours) {
                //Make sure the neighbour is not a wall
                if(maze[neighbour.x][neighbour.y] != Integer.MAX_VALUE){
                    double tentative_gScore = 0;
                    //If the movement is not diagonal
                    if(neighbour.x == current.x || neighbour.y == current.y){
                        tentative_gScore = gScore.get(current.toString()) + 1;
                    }
                    else{
                        tentative_gScore = gScore.get(current.toString()) + Math.sqrt(2);
                    }
                    if(!gScore.containsKey(neighbour.toString()) || tentative_gScore < gScore.get(neighbour.toString())){
                        cameFrom.put(neighbour, current);
                        gScore.put(neighbour.toString(), tentative_gScore);
                        fScore.put(neighbour.toString(), (gScore.get(neighbour.toString()) + h(neighbour)));
                        if(!openSet.contains(neighbour)){
                            openSet.add(neighbour);
                        }
                    }

                    debugWrite("Neighbour: " + neighbour.toString() + " fScore: " + fScore.get(neighbour.toString()));
                }
            }
        }

        return openSet;
    }

    public static void main(String[] args) {
        int m = Integer.MAX_VALUE;
        int[][] maze1 = {
                {m, m, m, m, m, 1, m, m, m, m, m},
                {m, 1, m, 1, 1, 1, m, 1, 1, 1, m},
                {m, 1, m, 1, m, 1, m, 1, m, 1, m},
                {m, 1, m, 1, m, 1, m, 1, m, 1, m},
                {m, 1, m, 1, m, m, m, m, m, 1, m},
                {m, 1, m, 1, 1, 1, 1, 1, 1, 1, m},
                {m, 1, m, m, m, m, m, m, m, 1, m},
                {m, 1, 1, 1, m, 1, 1, 1, m, 1, m},
                {m, 1, m, m, m, 1, m, 1, m, 1, m},
                {m, 1, 1, 1, 1, 1, m, 1, 1, 1, m},
                {m, m, m, m, m, 1, m, m, m, m, m}
        };
        /**
         * Shortest path for map 2 using Euclidean Distance:
         * [( 0, 5), ( 1, 4), ( 2, 3), ( 3, 3), ( 4, 3), ( 5, 4),
         * ( 5, 5), ( 5, 6), ( 5, 7), ( 5, 8), ( 6, 9), ( 7, 9),
         * ( 8, 9), ( 9, 8), ( 8, 7), ( 7, 6), ( 8, 5), ( 9, 4), ( 10, 5)]
         *
         * Shortest path for map 2 using Manhattan Distance:
         * [( 0, 5), ( 1, 4), ( 2, 3), ( 3, 3), ( 4, 3), ( 5, 4),
         * ( 5, 5), ( 5, 6), ( 5, 7), ( 5, 8), ( 6, 9), ( 7, 9),
         * ( 8, 9), ( 9, 8), ( 8, 7), ( 7, 6), ( 8, 5), ( 9, 4), ( 10, 5)]
         */

        startNode = new Node(0, 5);
        goalNode = new Node(10, 5);

        LinkedList path1 = AStar(maze1);
        Collections.reverse(path1);

        System.out.print("Map 1: " + path1.toString() + "\n");


        int[][] maze2 = {
                {m, m, m, m, m, m, m, m, m, 1, m, m, m, m, m, m, m, m, m, m, m},
                {m, 1, m, 1, 1, 1, 1, 1, 1, 1, m, 1, 1, 1, 1, 1, 1, 1, m, 1, m},
                {m, 1, m, 1, m, m, m, m, m, 1, m, 1, m, m, m, 1, m, 1, m, 1, m},
                {m, 1, m, 1, m, 1, 1, 1, m, 1, 1, 1, m, 1, m, 1, m, 1, 1, 1, m},
                {m, 1, m, 1, m, m, m, 1, m, m, m, m, m, 1, m, 1, m, m, m, 1, m},
                {m, 1, m, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, m, 1, 1, 1, m, 1, m},
                {m, 1, m, m, m, m, m, m, m, m, m, 1, m, m, m, m, m, 1, m, 1, m},
                {m, 1, 1, 1, 1, 1, m, 1, 1, 1, m, 1, m, 1, 1, 1, 1, 1, m, 1, m},
                {m, m, m, 1, m, m, m, 1, m, 1, m, 1, m, 1, m, m, m, m, m, 1, m},
                {m, 1, 1, 1, m, 1, 1, 1, m, 1, m, 1, m, 1, 1, 1, 1, 1, m, 1, m},
                {m, 1, m, m, m, 1, m, m, m, 1, m, 1, m, m, m, m, m, 1, m, m, m},
                {m, 1, 1, 1, 1, 1, m, 1, 1, 1, m, 1, m, 1, 1, 1, m, 1, 1, 1, m},
                {m, 1, m, m, m, m, m, 1, m, m, m, 1, m, m, m, 1, m, m, m, 1, m},
                {m, 1, m, 1, 1, 1, m, 1, 1, 1, 1, 1, m, 1, 1, 1, 1, 1, m, 1, m},
                {m, 1, m, m, m, 1, m, m, m, m, m, m, m, 1, m, m, m, m, m, 1, m},
                {m, 1, 1, 1, m, 1, 1, 1, 1, 1, 1, 1, 1, 1, m, 1, 1, 1, m, 1, m},
                {m, m, m, 1, m, 1, m, m, m, 1, m, m, m, m, m, 1, m, 1, m, 1, m},
                {m, 1, 1, 1, m, 1, 1, 1, m, 1, m, 1, 1, 1, 1, 1, m, 1, 1, 1, m},
                {m, 1, m, m, m, m, m, 1, m, 1, m, 1, m, m, m, m, m, m, m, 1, m},
                {m, 1, 1, 1, 1, 1, 1, 1, m, 1, m, 1, 1, 1, 1, 1, m, 1, 1, 1, m},
                {m, m, m, m, m, m, m, m, m, m, m, 1, m, m, m, m, m, m, m, m, m},
        };
        /**
         * Shortest path for map 2 using Euclidean Distance:
         * [( 0, 9), ( 1, 8), ( 2, 9), ( 3, 10), ( 2, 11), ( 1, 12), ( 1, 13), ( 1, 14), ( 2, 15),
         * ( 3, 15), ( 4, 15), ( 5, 16), ( 6, 17), ( 7, 16), ( 7, 15), ( 7, 14), ( 8, 13), ( 9, 14),
         * ( 9, 15), ( 9, 16), ( 10, 17), ( 11, 18), ( 12, 19), ( 13, 19), ( 14, 19), ( 15, 19), ( 16, 19),
         * ( 17, 18), ( 16, 17), ( 15, 16), ( 16, 15), ( 17, 14), ( 17, 13), ( 17, 12), ( 18, 11), ( 19, 12), ( 20, 11)]
         *
         * Shortest path for map 2 using Manhattan Distance:
         * [( 0, 9), ( 1, 8), ( 2, 9), ( 3, 10), ( 2, 11), ( 1, 12), ( 1, 13), ( 1, 14), ( 2, 15),
         * ( 3, 15), ( 4, 15), ( 5, 16), ( 6, 17), ( 7, 16), ( 7, 15), ( 7, 14), ( 8, 13), ( 9, 14),
         * ( 9, 15), ( 9, 16), ( 10, 17), ( 11, 18), ( 12, 19), ( 13, 19), ( 14, 19), ( 15, 19), ( 16, 19),
         * ( 17, 18), ( 16, 17), ( 15, 16), ( 16, 15), ( 17, 14), ( 17, 13), ( 17, 12), ( 18, 11), ( 19, 12), ( 20, 11)]
         */

        startNode = new Node(0, 9);
        goalNode = new Node(20, 11);

        LinkedList path2 = AStar(maze2);
        Collections.reverse(path2);

        System.out.print("Map 2: " + path2.toString() + "\n");
    }
}
