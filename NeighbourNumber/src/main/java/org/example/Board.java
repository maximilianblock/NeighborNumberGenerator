package org.example;

import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;

public class Board {
    //size
    int n;
    //array containing the variables
    IntVar[][] arr;

    public Board(int n) {
        this.n = n;
        arr = new IntVar[n][n];
    }

    public IntVar[][] getArr() {
        return arr;
    }

    public int getN() {
        return n;
    }

    //determines if all variables are instantiated
    public boolean allInstantiated() {
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                if (!arr[x][y].isInstantiated()) {
                    return false;
                }
            }
        }
        return true;
    }

    //returns boardString
    public String getBoardString() {
        if (this.allInstantiated()) {
            String boardString = n + "*";
            for (int x = 0; x < n; x++) {
                for (int y = 0; y < n; y++) {
                    if (arr[x][y].getValue() > 0) {
                        String subStr = x + "," + y + "," + arr[x][y].getValue();
                        boardString = boardString + subStr + ";";
                    }
                }
            }
            boardString = boardString.substring(0, boardString.length() - 1);
            return boardString;
        }
        return null;
    }

    //returns intVars as 1d array
    public IntVar[] getIntvars() {
        IntVar[] array = new IntVar[n * n];

        int c = 0;
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                array[c] = this.arr[x][y];
                c++;
            }
        }
        return array;
    }
}