package org.example;

public class App {
    public static void main(String[] args) {

        //Change location where the logfiles containing the generated instances and data will be written to
        String logFolderPath = "E:\\IntellijProjects\\NeighbourNumber\\target\\Logs\\";

        //example usage below
        //this will generate 4 instances of size 5x5 using a probabilistic TopDown algorithm with multiple branches and 25 iterations
        int size = 5;
        int num = 4;


        PuzzleGenerator.genereateNPuzzlesWithData(num, size, PuzzleGenerator.Difficulty.HARD, true, false, false, 25, logFolderPath);
    }
}
