package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

public class PuzzleGenerator {

    //generates n puzzles of size n
    public static void genereateNPuzzles(int numberOfPuzzles, int size, Difficulty difficulty, boolean probabilistic, boolean bottomUp, boolean singleBranch, int searchParameter, boolean debugPrints, String logFolderPath) {
        PrintWriter printWriter = null;
        int totalNum = size * 3;
        try {
            LocalDateTime ldt = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");
            String fileName = ldt.format(formatter) + "-log.txt";
            printWriter = new PrintWriter(logFolderPath + fileName);
            printWriter.println("Generated " + numberOfPuzzles + " levels of size " + size + "x" + size + ".");
            printWriter.println("Difficulty = " + difficulty.name());
            printWriter.println("Using " + (probabilistic ? "probabilistic" : "systematic") + " search and " + (bottomUp ? "bottom up" : "top down") + ".");
            if (!bottomUp && probabilistic) {
                printWriter.println((singleBranch ? "single branch" : "check all children of current node") + " variant of Top down probabilistic was used.");
            }
            printWriter.println("SearchParameter = " + searchParameter);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Couldnt create PrintWriter. Perhaps the filePath is faulty.");
            System.out.println("Will resume with printing to file");
        }
        String[] boardStrings = new String[numberOfPuzzles];

        for (int i = 0; i < numberOfPuzzles; i++) {
            boardStrings[i] = PuzzleGenerator.generateSinglePuzzle(size, difficulty, probabilistic, bottomUp, singleBranch, searchParameter, debugPrints);
        }

        for (int i = 0; i < numberOfPuzzles; i++) {
            System.out.println("---------------------------------------------------------------");
            System.out.println(boardStrings[i]);
            BoardStringHelper.printBoard(boardStrings[i]);
            System.out.println(BoardStringHelper.getNumberOfValue(boardStrings[i]) + " out of " + totalNum + " given.");

            if (printWriter != null) {
                printWriter.println("---------------------------------------------------------------");
                printWriter.println(boardStrings[i]);
                BoardStringHelper.printBoardToFile(boardStrings[i], printWriter);
                printWriter.println(BoardStringHelper.getNumberOfValue(boardStrings[i]) + " out of " + totalNum + " given.");
            }
        }
        if (printWriter != null) {
            printWriter.close();
        }
    }

    //generates n puzzle like above, but records data and writes it to file
    public static void genereateNPuzzlesWithData(int numberOfPuzzles, int size, Difficulty difficulty, boolean probabilistic, boolean bottomUp, boolean singleBranch, int searchParameter, String logFolderPath) {
        PrintWriter printWriter = null;
        try {
            LocalDateTime ldt = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-AAAA");
            String fileName = ldt.format(formatter) + "-log.txt";
            printWriter = new PrintWriter(logFolderPath + fileName);
            printWriter.println("Generated " + numberOfPuzzles + " levels of size " + size + "x" + size + ".");
            printWriter.println("Difficulty = " + difficulty.name());
            printWriter.println("Using " + (probabilistic ? "probabilistic" : "systematic" + " search and " + (bottomUp ? "bottom up" : "top down" + ".")));
            if (!bottomUp && probabilistic) {
                printWriter.println((singleBranch ? "single branch" : "check all children of current node") + " variant of Top down probabilistic was used.");
            }
            printWriter.println("SearchParameter = " + searchParameter);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Couldnt create PrintWriter. Perhaps the filePath is faulty.");
            System.out.println("Will resume with printing to file");
        }
        ExplorationData[] dataArray = new ExplorationData[numberOfPuzzles];

        for (int i = 0; i < numberOfPuzzles; i++) {
            dataArray[i] = PuzzleGenerator.generateSinglePuzzleWithData(size, difficulty, probabilistic, bottomUp, singleBranch, searchParameter);
        }

        System.out.println("Generation done. Printing results now");


        long minTotal = Long.MAX_VALUE;
        long maxTotal = 0;
        long sumTotal = 0;

        long minSolve = Long.MAX_VALUE;
        long maxSolve = 0;
        long sumSolve = 0;

        long minNodes = Long.MAX_VALUE;
        long maxNodes = 0;
        long totalNodes = 0;

        long minLeafs = Long.MAX_VALUE;
        long maxLeafs = 0;
        long totalLeafs = 0;

        long minNonLeafs = Long.MAX_VALUE;
        long maxNonLeafs = 0;
        long totalNonLeafs = 0;

        long minDupes = Long.MAX_VALUE;
        long maxDupes = 0;
        long totalDupesAvoided = 0;

        double minDepth = size * 3;
        double maxDepth = 0;
        double depthSum = 0;


        long minPreset = Long.MAX_VALUE;
        long maxPreset = 0;
        long presetSum = 0;
        for (int i = 0; i < numberOfPuzzles; i++) {
            System.out.println("---------------------------------------------------------------");
            ExplorationData data = dataArray[i];
            data.printToConsole();


            if (minTotal > data.totalTime) {
                minTotal = data.totalTime;
            }
            if (data.totalTime > maxTotal) {
                maxTotal = data.totalTime;
            }
            sumTotal += data.totalTime;

            if (data.timeSpentSolving < minSolve) {
                minSolve = data.timeSpentSolving;
            }
            if (data.timeSpentSolving > maxSolve) {
                maxSolve = data.timeSpentSolving;
            }
            sumSolve += data.timeSpentSolving;

            if (data.nodesVisited < minNodes) {
                minNodes = data.nodesVisited;
            }
            if (data.nodesVisited > maxNodes) {
                maxNodes = data.nodesVisited;
            }
            totalNodes += data.nodesVisited;

            if (data.leafsFound < minLeafs) {
                minLeafs = data.leafsFound;
            }
            if (data.leafsFound > maxLeafs) {
                maxLeafs = data.leafsFound;
            }
            totalLeafs += data.leafsFound;

            if (data.nonLeafsFound < minNonLeafs) {
                minNonLeafs = data.nonLeafsFound;
            }
            if (data.nonLeafsFound > maxNonLeafs) {
                maxNonLeafs = data.nonLeafsFound;
            }
            totalNonLeafs += data.nonLeafsFound;

            if (data.duplicateNodesAvoided < minDupes) {
                minDupes = data.duplicateNodesAvoided;
            }
            if (data.duplicateNodesAvoided > maxDupes) {
                maxDupes = data.duplicateNodesAvoided;
            }
            totalDupesAvoided += data.duplicateNodesAvoided;

            if (data.averageDepthReached < minDepth) {
                minDepth = data.averageDepthReached;
            }
            if (data.averageDepthReached > maxDepth) {
                maxDepth = data.averageDepthReached;
            }
            depthSum += data.averageDepthReached;

            if (data.presetNumberCount < minPreset) {
                minPreset = data.presetNumberCount;
            }
            if (data.presetNumberCount > maxPreset) {
                maxPreset = data.presetNumberCount;
            }
            presetSum += data.presetNumberCount;

            if (printWriter != null) {
                printWriter.println("---------------------------------------------------------------");
                data.printToFile(printWriter);
            }
        }

        double averageTime = sumTotal;
        averageTime = averageTime / numberOfPuzzles;
        double averageSolverTime = sumSolve;
        averageSolverTime = averageSolverTime / numberOfPuzzles;

        double averageNodes = (double) totalNodes / numberOfPuzzles;
        double averageLeafs = (double) totalLeafs / numberOfPuzzles;
        double averageNonLeafs = (double) totalNonLeafs / numberOfPuzzles;
        double averageDupesAvoided = (double) totalDupesAvoided / numberOfPuzzles;
        double averageDepth = depthSum / numberOfPuzzles;
        double averagePreset = (double) presetSum / numberOfPuzzles;

        if (printWriter != null) {
            printWriter.println("----------------------------------------------------");
            printWriter.println("Min/Max and average values below");
            printWriter.println("");
            printWriter.println("");
            String name;
            name = "total time";
            printWriter.println("Average " + name + " = " + averageTime + " milliseconds or " + (averageTime / 1000) + " seconds");
            printWriter.println("Max " + name + " = " + maxTotal);
            printWriter.println("Min " + name + " = " + minTotal);
            printWriter.println("");

            name = "solving time";
            printWriter.println("Average " + name + " = " + averageSolverTime + " milliseconds or " + (averageSolverTime / 1000) + " seconds");
            printWriter.println("Max " + name + " = " + maxSolve);
            printWriter.println("Min " + name + " = " + minSolve);
            printWriter.println("");

            name = "number of nodes visitied";
            printWriter.println("Average " + name + " = " + averageNodes);
            printWriter.println("Max " + name + " = " + maxNodes);
            printWriter.println("Min " + name + " = " + minNodes);
            printWriter.println("");

            name = "number of leafs visited";
            printWriter.println("Average " + name + " = " + averageLeafs);
            printWriter.println("Max " + name + " = " + maxLeafs);
            printWriter.println("Min " + name + " = " + minLeafs);
            printWriter.println("");

            name = "number of non leafs visited";
            printWriter.println("Average " + name + " = " + averageNonLeafs);
            printWriter.println("Max " + name + " = " + maxNonLeafs);
            printWriter.println("Min " + name + " = " + minNonLeafs);
            printWriter.println("");

            name = "number of dupes avoided due to HashSet";
            printWriter.println("Average " + name + " = " + averageDupesAvoided);
            printWriter.println("Max " + name + " = " + maxDupes);
            printWriter.println("Min " + name + " = " + minDupes);
            printWriter.println("");

            name = "depth in case of probabilistic search";
            printWriter.println("Average " + name + " = " + averageDepth);
            printWriter.println("Max " + name + " = " + maxDepth);
            printWriter.println("Min " + name + " = " + minDepth);
            printWriter.println("");

            name = "number of preset values";
            printWriter.println("Average " + name + " = " + averagePreset);
            printWriter.println("Max " + name + " = " + maxPreset);
            printWriter.println("Min " + name + " = " + minPreset);
            printWriter.println("");

            printWriter.println("total Time for all iterations = " + (sumTotal / 1000) + " seconds.");
            printWriter.close();
        }
    }

    public static String generateSinglePuzzle(int size, Difficulty difficulty, boolean probabilistic, boolean bottomUp, boolean singleBranch, int searchParameter, boolean debugPrints) {
        String boardString = generateSolution(size);
        BoardStringHelper.printBoard(boardString);
        System.out.println("This solution was generated. Now taking away hints to create a puzzle.");

        if (probabilistic) {
            if (bottomUp) {
                boardString = exploreProbabilisticBottomUp(boardString, size, difficulty, searchParameter, debugPrints);
            } else {
                if (singleBranch) {
                    boardString = exploreProbabilisticTopDownSingleBranch(boardString, size, difficulty, searchParameter, debugPrints);
                } else {
                    boardString = exploreProbabilisticTopDownMulti(boardString, size, difficulty, searchParameter, debugPrints);
                }
            }
        } else {
            if (bottomUp) {
                boardString = exploreTreeBottomUp(boardString, size, difficulty, debugPrints);
            } else {
                boardString = exploreTreeTopDown(boardString, size, searchParameter, difficulty, debugPrints);
            }
        }

        System.out.println("puzzle generation done. This is the result:");
        System.out.println(boardString);
        BoardStringHelper.printBoard(boardString);
        return boardString;
    }

    public static ExplorationData generateSinglePuzzleWithData(int size, Difficulty difficulty, boolean probabilistic, boolean bottomUp, boolean singleBranch, int searchParameter) {
        String boardString = generateSolution(size);
        ExplorationData data;
        long timeStart = System.currentTimeMillis();
        if (probabilistic) {
            if (bottomUp) {
                data = exploreProbabilisticBottomUpWithData(boardString, size, difficulty, searchParameter);
            } else {
                if (singleBranch) {
                    data = exploreProbabilisticTopDownSingleBranchWithData(boardString, size, difficulty, searchParameter);
                } else {
                    data = exploreProbabilisticTopDownMultiWithData(boardString, size, difficulty, searchParameter);
                }
            }
        } else {
            if (bottomUp) {
                data = exploreTreeBottomUpWithData(boardString, size, difficulty);
            } else {
                data = exploreTreeTopDownWithData(boardString, size, searchParameter, difficulty);
            }
        }
        long timeEnd = System.currentTimeMillis();
        data.totalTime = timeEnd - timeStart;
        return data;
    }

    // Generates a solved puzzle and returns the boardString.
    // All numbers will be saved as values inside the valueList
    public static String generateSolution(int size) {
        String boardString;
        while (true) {
            boardString = size + "*";
            //Difficulty.HARD to get best propagation since random search is not very performant
            NeighbourNumberSolver gameSolver = new NeighbourNumberSolver(boardString, Difficulty.HARD);
            if (gameSolver.randomSolve()) {
                return gameSolver.getBoard().getBoardString();
            }
        }
    }

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    private static String exploreProbabilisticTopDownSingleBranch(String boardString, int size, Difficulty difficulty, int numIterations, boolean debugPrints) {
        int minNum = 3 * size;
        String bestBoardString = boardString;

        for (int iteration = 0; iteration < numIterations; iteration++) {
            if (debugPrints) System.out.println("Starting exploration iteration " + (iteration + 1));
            boolean isAcceptable = true;
            String currentBoardstring = boardString;
            String lastBoardstring = currentBoardstring;
            while (isAcceptable) {
                lastBoardstring = currentBoardstring;
                currentBoardstring = BoardStringHelper.removeRandomValue(currentBoardstring);
                isAcceptable = NeighbourNumberSolver.checkDifficulty(currentBoardstring, difficulty);
            }
            int valueNumber = BoardStringHelper.getNumberOfValue(lastBoardstring);
            if (valueNumber < minNum) {
                minNum = valueNumber;
                bestBoardString = lastBoardstring;
            }
        }
        return bestBoardString;
    }

    private static String exploreProbabilisticTopDownMulti(String boardString, int size, Difficulty difficulty, int numIterations, boolean debugPrints) {
        int minNum = 3 * size;
        String bestBoardString = boardString;

        for (int iteration = 0; iteration < numIterations; iteration++) {
            if (debugPrints) System.out.println("Starting exploration iteration " + (iteration + 1));
            boolean viableNeighborFound = true;
            String currentBoardstring = boardString;
            String lastBoardstring = currentBoardstring;
            while (viableNeighborFound) {
                lastBoardstring = currentBoardstring;
                ArrayList<String> stringsToTry = BoardStringHelper.getStringWithoutEachValue(lastBoardstring);
                viableNeighborFound = false;
                for (String str : stringsToTry) {
                    if (NeighbourNumberSolver.checkDifficulty(str, difficulty)) {
                        currentBoardstring = str;
                        viableNeighborFound = true;
                        break;
                    }
                }
            }
            int valueNumber = BoardStringHelper.getNumberOfValue(lastBoardstring);
            if (valueNumber < minNum) {
                minNum = valueNumber;
                bestBoardString = lastBoardstring;
            }
        }
        return bestBoardString;
    }

    private static ExplorationData exploreProbabilisticTopDownSingleBranchWithData(String boardString, int size, Difficulty difficulty, int numIterations) {
        ExplorationData data = new ExplorationData(size, true, true);
        data.solutionString = boardString;
        data.numberOfIterations = numIterations;
        int[] depthReached = new int[numIterations];

        int minNum = 3 * size;
        String bestBoardString = boardString;

        for (int iteration = 0; iteration < numIterations; iteration++) {
            boolean isAcceptable = true;
            String currentBoardstring = boardString;
            String lastBoardstring = currentBoardstring;
            while (isAcceptable) {
                data.nonLeafsFound++;
                data.nodesVisited++;
                lastBoardstring = currentBoardstring;
                currentBoardstring = BoardStringHelper.removeRandomValue(currentBoardstring);
                long solveTimeStart = System.currentTimeMillis();
                isAcceptable = NeighbourNumberSolver.checkDifficulty(currentBoardstring, difficulty);
                long solveTimeEnd = System.currentTimeMillis();
                data.timeSpentSolving += solveTimeEnd - solveTimeStart;
            }
            data.nonLeafsFound--;
            data.leafsFound++;
            int valueNumber = BoardStringHelper.getNumberOfValue(lastBoardstring);
            depthReached[iteration] = valueNumber;
            if (valueNumber < minNum) {
                minNum = valueNumber;
                bestBoardString = lastBoardstring;
            }
        }

        int sum = 0;
        for (int i = 0; i < numIterations; i++) {
            sum += depthReached[i];
        }
        data.averageDepthReached = (double) sum / numIterations;
        data.presetNumberCount = minNum;
        data.puzzleString = bestBoardString;
        return data;
    }

    private static ExplorationData exploreProbabilisticTopDownMultiWithData(String boardString, int size, Difficulty difficulty, int numIterations) {
        ExplorationData data = new ExplorationData(size, true, true);
        data.solutionString = boardString;
        data.numberOfIterations = numIterations;
        int[] depthReached = new int[numIterations];

        int minNum = 3 * size;
        String bestBoardString = boardString;

        for (int iteration = 0; iteration < numIterations; iteration++) {
            boolean viableNeighborFound = true;
            String currentBoardstring = boardString;
            String lastBoardstring = currentBoardstring;
            while (viableNeighborFound) {
                data.nonLeafsFound++;
                data.nodesVisited++;
                lastBoardstring = currentBoardstring;
                ArrayList<String> stringsToTry = BoardStringHelper.getStringWithoutEachValue(lastBoardstring);
                viableNeighborFound = false;
                long solveTimeStart;
                long solveTimeEnd;
                for (String str : stringsToTry) {
                    solveTimeStart = System.currentTimeMillis();
                    data.nodesVisited++;
                    if (NeighbourNumberSolver.checkDifficulty(str, difficulty)) {
                        currentBoardstring = str;
                        data.nonLeafsFound++;
                        solveTimeEnd = System.currentTimeMillis();
                        data.timeSpentSolving += solveTimeEnd - solveTimeStart;
                        viableNeighborFound = true;
                        break;
                    }
                    data.leafsFound++;
                    solveTimeEnd = System.currentTimeMillis();
                    data.timeSpentSolving += solveTimeEnd - solveTimeStart;
                }
            }
            int valueNumber = BoardStringHelper.getNumberOfValue(lastBoardstring);
            depthReached[iteration] = valueNumber;
            if (valueNumber < minNum) {
                minNum = valueNumber;
                bestBoardString = lastBoardstring;
            }
        }

        int sum = 0;
        for (int i = 0; i < numIterations; i++) {
            sum += depthReached[i];
        }
        data.averageDepthReached = (double) sum / numIterations;
        data.presetNumberCount = minNum;
        data.puzzleString = bestBoardString;
        return data;
    }

    private static String exploreProbabilisticBottomUp(String boardString, int size, Difficulty difficulty, int numIterations, boolean debugPrints) {
        int minNum = 3 * size;
        String bestBoardString = boardString;

        for (int iteration = 0; iteration < numIterations; iteration++) {
            if (debugPrints) System.out.println("Starting exploration iteration " + (iteration + 1));

            ArrayList<Value> remainingValues = BoardStringHelper.getValuesFromString(boardString);
            boolean isAcceptable = false;
            String currentBoardstring = size + "*";

            while (!isAcceptable) {
                if (remainingValues.size() == 0) break;
                int index = ThreadLocalRandom.current().nextInt(0, remainingValues.size());
                if (debugPrints)
                    System.out.println("Adding " + remainingValues.get(index).getString() + " to the boardString.");
                currentBoardstring = BoardStringHelper.addValueToBoardStringInOrder(currentBoardstring, remainingValues.get(index));
                remainingValues.remove(index);
                if (debugPrints) System.out.println(currentBoardstring);
                if (debugPrints) System.out.println("checking difficulty of this board");
                if (debugPrints) BoardStringHelper.printBoard(currentBoardstring);
                isAcceptable = NeighbourNumberSolver.checkDifficulty(currentBoardstring, difficulty);
                if (debugPrints) {
                    String debugString = isAcceptable ? "Board is solvable." : "Board is not yet solvable";
                    System.out.println(debugString);
                }
            }
            int valueNumber = BoardStringHelper.getNumberOfValue(currentBoardstring);
            if (valueNumber < minNum) {
                minNum = valueNumber;
                bestBoardString = currentBoardstring;
                if (debugPrints) {
                    System.out.println(bestBoardString + " is the new smallest instance");
                }
            }
        }
        return bestBoardString;
    }

    private static ExplorationData exploreProbabilisticBottomUpWithData(String boardString, int size, Difficulty difficulty, int numIterations) {
        ExplorationData data = new ExplorationData(size, true, false);
        data.solutionString = boardString;
        data.numberOfIterations = numIterations;
        int minNum = 3 * size;
        String bestBoardString = boardString;
        int[] depthReached = new int[numIterations];

        for (int iteration = 0; iteration < numIterations; iteration++) {

            ArrayList<Value> remainingValues = BoardStringHelper.getValuesFromString(boardString);
            boolean isAcceptable = false;
            String currentBoardstring = size + "*";
            while (!isAcceptable) {
                if (remainingValues.size() == 0) break;
                data.nonLeafsFound++;
                int index = ThreadLocalRandom.current().nextInt(0, remainingValues.size());
                currentBoardstring = BoardStringHelper.addValueToBoardStringInOrder(currentBoardstring, remainingValues.get(index));
                remainingValues.remove(index);
                data.nodesVisited++;
                long solveTimeStart = System.currentTimeMillis();
                isAcceptable = NeighbourNumberSolver.checkDifficulty(currentBoardstring, difficulty);
                long solveTimeEnd = System.currentTimeMillis();
                data.timeSpentSolving += solveTimeEnd - solveTimeStart;
            }
            data.leafsFound++;
            int valueNumber = BoardStringHelper.getNumberOfValue(currentBoardstring);
            depthReached[iteration] = valueNumber;
            if (valueNumber < minNum) {
                minNum = valueNumber;
                bestBoardString = currentBoardstring;

            }
        }

        int sum = 0;
        for (int i = 0; i < numIterations; i++) {
            sum += depthReached[i];
        }
        data.averageDepthReached = (double) sum / numIterations;
        data.presetNumberCount = minNum;
        data.puzzleString = bestBoardString;
        return data;
    }

    private static String exploreTreeTopDown(String boardString, int size, int minRemainingNumbers, Difficulty difficulty, boolean debugPrint) {
        HashSet<String> visited = new HashSet<>();
        Stack<String> boardStack = new Stack<>();
        //add children to stack
        for (int i = 0; i < BoardStringHelper.getNumberOfValue(boardString); i++) {
            boardStack.add(BoardStringHelper.removeValueFromString(boardString, i));
        }
        int minNum = 3 * size;

        if (debugPrint) System.out.println("Exploring whole searchtree.");
        while (!boardStack.isEmpty()) {
            String currentBoard = boardStack.pop();
            boolean isAcceptable = /*NeighbourNumberSolver.hasOnlyOneSolution(currentBoard)
                    &&*/ NeighbourNumberSolver.checkDifficulty(currentBoard, difficulty);
            if (isAcceptable) {
                if (debugPrint) BoardStringHelper.printBoard(currentBoard);
                if (debugPrint) System.out.println(currentBoard);
                int currentNum = BoardStringHelper.getNumberOfValue(currentBoard);
                if (currentNum < minNum) {
                    minNum = currentNum;
                    boardString = currentBoard;
                    if (currentNum <= minRemainingNumbers) {
                        return boardString;
                    }
                    if (debugPrint)
                        System.out.println(currentBoard + " is the new smallest instance with only " + currentNum + " given values.");
                }
                //adding children
                for (int i = 0; i < BoardStringHelper.getNumberOfValue(currentBoard); i++) {
                    String boardToAdd = BoardStringHelper.removeValueFromString(currentBoard, i);
                    if (!visited.contains(boardToAdd)) {
                        boardStack.add(boardToAdd);
                        visited.add(boardToAdd);
                    } else {
                        if (debugPrint) System.out.println("Cutoff happend due to Hashset");
                    }
                }
            }
        }
        return boardString;
    }

    private static ExplorationData exploreTreeTopDownWithData(String boardString, int size, int minRemainingNumbers, Difficulty difficulty) {
        ExplorationData data = new ExplorationData(size, false, true);
        data.solutionString = boardString;
        HashSet<String> visited = new HashSet<>();
        Stack<String> boardStack = new Stack<>();
        //add children to stack
        for (int i = 0; i < BoardStringHelper.getNumberOfValue(boardString); i++) {
            boardStack.add(BoardStringHelper.removeValueFromString(boardString, i));
        }
        int minNum = 3 * size;

        while (!boardStack.isEmpty()) {
            String currentBoard = boardStack.pop();
            data.nodesVisited++;
            long solveTimeStart = System.currentTimeMillis();
            boolean isAcceptable = NeighbourNumberSolver.checkDifficulty(currentBoard, difficulty);
            long solveTimeEnd = System.currentTimeMillis();
            data.timeSpentSolving += solveTimeEnd - solveTimeStart;
            if (isAcceptable) {
                data.nonLeafsFound++;
                int currentNum = BoardStringHelper.getNumberOfValue(currentBoard);
                if (currentNum < minNum) {
                    minNum = currentNum;
                    boardString = currentBoard;
                    if (currentNum <= minRemainingNumbers) {
                        data.presetNumberCount = minNum;
                        data.puzzleString = boardString;
                        return data;
                    }
                }
                //adding children
                for (int i = 0; i < BoardStringHelper.getNumberOfValue(currentBoard); i++) {
                    String boardToAdd = BoardStringHelper.removeValueFromString(currentBoard, i);
                    if (!visited.contains(boardToAdd)) {
                        boardStack.add(boardToAdd);
                        visited.add(boardToAdd);
                    } else {
                        data.duplicateNodesAvoided++;
                    }
                }
            } else {
                data.leafsFound++;
            }
        }
        data.presetNumberCount = minNum;
        data.puzzleString = boardString;
        return data;
    }

    private static String exploreTreeBottomUp(String boardString, int size, Difficulty difficulty, boolean debugPrint) {
        HashSet<String> visited = new HashSet<>();
        LinkedList<String> queue = new LinkedList<>();
        //add children to stack
        queue.add(size + "*");
        visited.add(size + "*");
        int layer = 0;

        if (debugPrint) System.out.println("Starting BFS");
        while (!queue.isEmpty()) {
            String currentBoard = queue.pollFirst();
            if (BoardStringHelper.getNumberOfValue(currentBoard) == layer + 1) {
                layer++;
                System.out.println("Starting to search in layer " + layer);
            }
            if (debugPrint) {
                System.out.println("Trying to solve this board: " + currentBoard);
                //BoardStringHelper.printBoard(currentBoard);
            }
            boolean isAcceptable = /*NeighbourNumberSolver.hasOnlyOneSolution(currentBoard)
                    &&*/ NeighbourNumberSolver.checkDifficulty(currentBoard, difficulty);
            if (isAcceptable) {
                System.out.println("Solvable instance found.");
                return currentBoard;
            }
            //adding children
            ArrayList<Value> missingValues = BoardStringHelper.getValuesNotInString(currentBoard, boardString);
            for (Value val : missingValues) {
                String nextString = BoardStringHelper.addValueToBoardStringInOrder(currentBoard, val);
                if (!visited.contains(nextString)) {
                    queue.add(nextString);
                    visited.add(nextString);
                    if (debugPrint) System.out.println("Added " + nextString + " to queue");
                }
            }
        }
        return null;
    }

    private static ExplorationData exploreTreeBottomUpWithData(String boardString, int size, Difficulty difficulty) {
        ExplorationData data = new ExplorationData(size, false, false);
        data.solutionString = boardString;

        HashSet<String> visited = new HashSet<>();
        LinkedList<String> queue = new LinkedList<>();
        //add children to stack
        queue.add(size + "*");
        visited.add(size + "*");
        int layer = 0;

        while (!queue.isEmpty()) {
            data.nodesVisited++;
            String currentBoard = queue.pollFirst();
            if (BoardStringHelper.getNumberOfValue(currentBoard) == layer + 1) {
                layer++;
            }
            long solveTimeStart = System.currentTimeMillis();
            boolean isAcceptable = NeighbourNumberSolver.checkDifficulty(currentBoard, difficulty);
            long solveTimeEnd = System.currentTimeMillis();
            data.timeSpentSolving += solveTimeEnd - solveTimeStart;
            if (isAcceptable) {
                data.leafsFound++;
                data.presetNumberCount = BoardStringHelper.getNumberOfValue(currentBoard);
                data.puzzleString = currentBoard;
                return data;
            }
            data.nonLeafsFound++;
            //adding children
            ArrayList<Value> missingValues = BoardStringHelper.getValuesNotInString(currentBoard, boardString);
            for (Value val : missingValues) {
                String nextString = BoardStringHelper.addValueToBoardStringInOrder(currentBoard, val);
                if (!visited.contains(nextString)) {
                    queue.add(nextString);
                    visited.add(nextString);
                } else {
                    data.duplicateNodesAvoided++;
                }
            }
        }
        data.presetNumberCount = BoardStringHelper.getNumberOfValue(boardString);
        data.puzzleString = boardString;
        return null;
    }
}
