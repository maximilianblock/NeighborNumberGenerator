package org.example;

import java.io.PrintStream;
import java.io.PrintWriter;

public class ExplorationData {

    public int sizeOfInstance;
    public int nodesVisited;
    public int leafsFound;
    public int duplicateNodesAvoided; // due to hashset
    public int nonLeafsFound;
    public String puzzleString;
    public String solutionString;
    public long timeSpentSolving; // time spent in the checkDifficulty Method
    public long totalTime;
    public int presetNumberCount; // number of preset numbers
    public boolean topDown;
    public double averageDepthReached;
    public int numberOfIterations; //for probabilistic search
    public boolean probabilistic;

    public ExplorationData(int size, boolean prob, boolean topDown) {
        this.topDown = topDown;
        probabilistic = prob;
        sizeOfInstance = size;
        nodesVisited = 0;
        leafsFound = 0;
        nonLeafsFound = 0;
        timeSpentSolving = 0;
        totalTime = 0;
        numberOfIterations = 0;
        duplicateNodesAvoided = 0;
        averageDepthReached = 0;
    }

    public void printToConsole() {
        PrintStream stream = System.out;
        stream.println("Solution:");
        BoardStringHelper.printBoard(solutionString);
        stream.println("Puzzle:");
        BoardStringHelper.printBoard(puzzleString);
        int maxNum = sizeOfInstance * 3;
        int missingNum = maxNum - presetNumberCount;
        stream.println(presetNumberCount + " out of " + maxNum + " given. " + missingNum + " to fill.");
        stream.println("Total time taken = " + totalTime + " milliseconds or " + totalTime / 1000 + " seconds.");
        stream.println("Out of that " + timeSpentSolving + " milliseconds or " + timeSpentSolving / 1000 + " seconds were spent actually solving");
        stream.println("");
        stream.println(nodesVisited + " nodes were visited.");
        stream.println("Out of that " + leafsFound + " were leafs and " + nonLeafsFound + " were non leafs.");
        if (!probabilistic) {
            stream.println(duplicateNodesAvoided + " Nodes were avoided due to the HashSet.");
        }
        stream.println("");
        if (probabilistic) {
            stream.println("Probabilistic search was used with " + numberOfIterations + " iterations.");
            stream.println("The average search depth was " + averageDepthReached);
        }
        if (topDown) {
            stream.println("Search direction was Top Down, meaning from the solved puzzle hints were removed.");
        } else {
            stream.println("Search direction was Bottom Up, meaning to the empty puzzle hints were added.");
        }
    }

    public void printToFile(PrintWriter stream) {
        stream.println("Solution:");
        BoardStringHelper.printBoardToFile(solutionString, stream);
        stream.println("Puzzle:");
        BoardStringHelper.printBoardToFile(puzzleString, stream);
        int maxNum = sizeOfInstance * 3;
        int missingNum = maxNum - presetNumberCount;
        stream.println(presetNumberCount + " out of " + maxNum + " given. " + missingNum + " to fill.");
        stream.println("Total time taken = " + totalTime + " milliseconds or " + totalTime / 1000 + " seconds.");
        stream.println("Out of that " + timeSpentSolving + " milliseconds or " + timeSpentSolving / 1000 + " seconds were spent actually solving");
        stream.println("");
        stream.println(nodesVisited + " nodes were visited.");
        stream.println("Out of that " + leafsFound + " were leafs and " + nonLeafsFound + " were non leafs.");
        if (!probabilistic) {
            stream.println(duplicateNodesAvoided + " Nodes were avoided due to the HashSet.");
        }
        stream.println("");
        if (probabilistic) {
            stream.println("Probabilistic search was used with " + numberOfIterations + " iterations.");
            stream.println("The average search depth was " + averageDepthReached);
        }
        if (topDown) {
            stream.println("Search direction was Top Down, meaning from the solved puzzle hints were removed.");
        } else {
            stream.println("Search direction was Bottom Up, meaning to the empty puzzle hints were added.");
        }
    }
}
