package org.example;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import java.util.Random;

public class NeighbourNumberSolver {

    Board board;
    Model model;
    Solver solver;


    public NeighbourNumberSolver(String boardString, PuzzleGenerator.Difficulty difficulty) {
        model = new Model("NeighbourNumberSolver");
        getBoardFromString(boardString, difficulty);
    }

    // prepares board and model for solving. Adds constraints according to difficulty
    public void getBoardFromString(String boardString, PuzzleGenerator.Difficulty difficulty) {
        int n = 0;

        String[] strings = boardString.split("\\*", 2);

        n = Integer.parseInt(strings[0]);
        board = new Board(n);


        int[][] array = new int[n][n];
        int[] rowCounts = new int[n];
        int[] colCounts = new int[n];
        for (int x = 0; x < n; x++) {

            rowCounts[x] = 0;
            colCounts[x] = 0;

            for (int y = 0; y < n; y++) {
                array[x][y] = 0;
            }
        }

        if (!strings[1].equals("")) {
            String[] valueStrings = strings[1].split(";");
            for (String valStr : valueStrings) {
                Value val = BoardStringHelper.getValueFromString(valStr);
                array[val.getPos().getX()][val.getPos().getY()] = val.getVal();
            }
        }

        prepareVariables(n, array, rowCounts, colCounts);
        addConstraints(rowCounts, colCounts, difficulty);
    }

    //adds IntVars to model
    // non zero values in preArray correspond to a preset value and get created as intvar with a domain of a single number
    // fills rowCount and colCount with amount of preset values in each row or column
    // rows or columns with 3 values have all fields created with domain of single number, since they are already solved
    private void prepareVariables(int n, int[][] preArray, int[] rowCount, int[] colCount) {
        IntVar[][] arr = board.getArr();
        for (int x = 0; x < n; x++) {
            int count = 0;
            for (int y = 0; y < n; y++) {
                if (preArray[x][y] != 0) {
                    count++;
                }
            }
            colCount[x] = count;
            if (count == 3) {
                for (int y = 0; y < n; y++) {
                    arr[x][y] = model.intVar(getVarName(x, y), preArray[x][y]);
                }
            }
        }

        for (int y = 0; y < n; y++) {
            int count = 0;
            for (int x = 0; x < n; x++) {
                if (preArray[x][y] != 0) {
                    count++;
                }
            }
            rowCount[y] = count;
            if (count == 3) {
                for (int x = 0; x < n; x++) {
                    if (arr[x][y] == null) {
                        arr[x][y] = model.intVar(getVarName(x, y), preArray[x][y]);
                    }
                }
            } else {
                for (int x = 0; x < n; x++) {
                    if (arr[x][y] == null) {
                        if (preArray[x][y] != 0) {
                            arr[x][y] = model.intVar(getVarName(x, y), preArray[x][y]);
                        } else {
                            arr[x][y] = model.intVar(getVarName(x, y), 0, 9);
                        }
                    }
                }
            }
        }
    }

    //constructs name of a board variable for debugging
    private String getVarName(int x, int y) {
        return "BoardVar:" + x + "," + y;
    }

    //adds constraints according to difficulty
    private void addConstraints(int[] rowCount, int[] colCount, PuzzleGenerator.Difficulty difficulty) {
        addMinimalConstraints(rowCount, colCount);
        if (difficulty != PuzzleGenerator.Difficulty.EASY) {
            if (difficulty == PuzzleGenerator.Difficulty.HARD) {
                addExtraConstraints(rowCount, colCount, true);
            } else {
                addExtraConstraints(rowCount, colCount, false);
            }
        }
    }

    private void addMinimalConstraints(int[] rowCount, int[] colCount) {
        addOnlyThreeConstraint(rowCount, colCount);
        addNumberDifferenceConstraints(rowCount, colCount);
    }

    //add constraint that allows only 3 number (value > 0) per row or column
    private void addOnlyThreeConstraint(int[] rowCount, int[] colCount) {
        int n = board.getN();
        IntVar[][] array = board.getArr();
        int limit = n - 3;
        IntVar var = model.intVar(limit);
        for (int x = 0; x < n; x++) {
            if (colCount[x] == 3) continue;
            IntVar[] column = new IntVar[n];
            for (int y = 0; y < n; y++) {
                column[y] = array[x][y];
            }
            model.count(0, column, var).post();
        }

        for (int y = 0; y < n; y++) {
            if (rowCount[y] == 3) continue;
            IntVar[] row = new IntVar[n];
            for (int x = 0; x < n; x++) {
                row[x] = array[x][y];
            }
            model.count(0, row, var).post();
        }
    }

    // Adds the constraint that disallows same number in a row or column.
    // Also adds the constraint that forces the difference in values of two
    // numbers to be equal to the number of lines between the tiles they're placed in.
    private void addNumberDifferenceConstraints(int[] rowCount, int[] colCount) {
        int n = board.getN();
        IntVar[][] arr = board.getArr();
        IntVar var1;
        IntVar var2;
        for (int y = 0; y < n; y++) {
            if (rowCount[y] == 3) continue;
            for (int x1 = 0; x1 < n - 1; x1++) {
                for (int x2 = x1 + 1; x2 < n; x2++) {
                    int dif = Math.abs(x1 - x2);
                    var1 = arr[x1][y];
                    var2 = arr[x2][y];
                    BoolVar bothGreaterZero = var1.gt(0).and(var2.gt(0)).boolVar();
                    BoolVar var = bothGreaterZero.and(boolVarAllValuesZero(x1, x2, y, true)).boolVar();
                    model.ifThen(
                            var,
                            var1.sub(var2).abs().eq(dif).decompose()
                    );
                    model.ifThen(
                            bothGreaterZero,
                            model.arithm(var1, "!=", var2)
                    );
                }
            }
        }
        for (int x = 0; x < n; x++) {
            if (colCount[x] == 3) continue;
            for (int y1 = 0; y1 < n - 1; y1++) {
                for (int y2 = y1 + 1; y2 < n; y2++) {
                    int dif = Math.abs(y1 - y2);
                    var1 = arr[x][y1];
                    var2 = arr[x][y2];
                    //System.out.println(var1.toString() + ", " + var2.toString() + " dif = " + dif);
                    BoolVar bothGreaterZero = var1.gt(0).and(var2.gt(0)).boolVar();
                    BoolVar var = bothGreaterZero.and(boolVarAllValuesZero(y1, y2, x, false)).boolVar();
                    model.ifThen(
                            var,
                            var1.sub(var2).abs().eq(dif).decompose()
                    );
                    model.ifThen(
                            bothGreaterZero,
                            model.arithm(var1, "!=", var2)
                    );
                }
            }
        }
    }

    // Constructs a boolvar that is true, if all values between two cells are equal to 0
    // or if there are no cells between them
    // axisIsX = true -> coord1 and coord2 are x components, otherAxis is y
    // axisIsX = false -> coord1 and coord2 are y components, otherAxis is x
    // otherAxis is the other coordinate component that both cells share
    private BoolVar boolVarAllValuesZero(int coord1, int coord2, int otherAxis, Boolean axisIsX) {
        int dif = Math.abs(coord1 - coord2);
        if (dif > 1) {
            BoolVar bool;
            IntVar[][] arr = board.getArr();
            IntVar[] vars = new IntVar[dif - 1];
            if (axisIsX) {
                //System.out.println("coord1 = " + coord1 + ", coord2 = " + coord2 + ", dif = " + dif);
                for (int x = 0; x + coord1 + 1 < coord2; x++) {
                    vars[x] = arr[x + coord1 + 1][otherAxis];
                }
            } else {
                for (int y = 0; y + coord1 + 1 < coord2; y++) {
                    vars[y] = arr[otherAxis][y + coord1 + 1];
                }
            }
            bool = model.allEqual(vars).reify().and(vars[0].eq(0)).boolVar();
            return bool;
        }
        // no cells between
        return model.boolVar(true);
    }

    //adds redundant constraints, adds the oddEvenConstraint if allConstraints is true
    private void addExtraConstraints(int[] rowCount, int[] colCount, boolean allConstraints) {
        int n = board.getN();
        IntVar[][] arr = board.getArr();
        IntVar var1;
        IntVar var2;
        for (int y = 0; y < n; y++) {
            if (rowCount[y] == 3) continue;
            for (int x1 = 0; x1 < n; x1++) {
                for (int x2 = 0; x2 < n; x2++) {
                    if (x1 == x2) continue;
                    int distance = Math.abs(x1 - x2);
                    var1 = arr[x1][y];
                    var2 = arr[x2][y];
                    if (allConstraints) {
                        addOddEvenConstraint(var1, var2, distance);
                    }
                    addMinMaxConstraint(var1, var2, distance);
                    addNotEqualConstraint(var1, var2);
                }
            }
        }
        for (int x = 0; x < n; x++) {
            if (colCount[x] == 3) continue;
            for (int y1 = 0; y1 < n; y1++) {
                for (int y2 = 0; y2 < n; y2++) {
                    if (y1 == y2) continue;
                    int distance = Math.abs(y1 - y2);
                    var1 = arr[x][y1];
                    var2 = arr[x][y2];
                    if (allConstraints) {
                        addOddEvenConstraint(var1, var2, distance);
                    }
                    addMinMaxConstraint(var1, var2, distance);
                    addNotEqualConstraint(var1, var2);
                }
            }
        }
    }

    // Adds a constraint that takes out odd/even numbers from the possible values of var2
    // based on the value of var1 and the distance to var2
    private void addOddEvenConstraint(IntVar var1, IntVar var2, int distance) {
        if (distance % 2 == 0) { //both must be odd or even
            model.ifThen(//both odd
                    var1.mod(2).eq(1).boolVar(),
                    var2.notin(2, 4, 6, 8).decompose()
            );
            model.ifThen(//both even
                    var1.gt(0).and(var1.mod(2).eq(0)).boolVar(),
                    var2.notin(1, 3, 5, 7, 9).decompose()
            );
        } else {//one odd, one even
            model.ifThen(//var1 odd, var2 even
                    var1.mod(2).eq(1).boolVar(),
                    var2.notin(1, 3, 5, 7, 9).decompose()
            );
            model.ifThen(//var1 even, var2 odd
                    var1.gt(0).and(var1.mod(2).eq(0)).boolVar(),
                    var2.notin(2, 4, 6, 8).decompose()
            );
        }
    }

    // Adds a constraint that limits max and min values of var2 depending on value
    // of var1 and distance between var1 and var2
    private void addMinMaxConstraint(IntVar var1, IntVar var2, int distance) {
        var1.gt(0).imp(var2.le(var1.add(distance))).post();
        for (int i = 9; i > 2; i--) {
            if (i - distance >= 2) {
                var1.eq(i).imp(var2.notin(SetHelper.setLessThan(i - distance))).post();
            }
        }
    }

    //another not equal constraint for better propagation
    private void addNotEqualConstraint(IntVar var1, IntVar var2) {
        model.ifThen(
                var1.gt(0).boolVar(),
                model.arithm(var1, "!=", var2)
        );
    }

    // tries to solve with a random search strategy to get more diverse starting solutions
    public boolean randomSolve() {
        Solver solver = model.getSolver();
        long seed = new Random().nextLong();
        solver.setSearch(Search.randomSearch(board.getIntvars(), seed));
        return solver.solve();
    }

    //checks if the board is solvable by only propagation
    private boolean propagate() {
        Solver solver = model.getSolver();
        try {
            solver.propagate();
        } catch (Exception ex) {
            return false;
        }
        if (board.allInstantiated()) {
            return true;
        }
        return false;
    }

    // Checks if a puzzle is easy/medium/hard. Returns false if it is too hard. Otherwise returns false.
    public static boolean checkDifficulty(String boardString, PuzzleGenerator.Difficulty difficulty) {
        NeighbourNumberSolver neighbourNumberSolver = new NeighbourNumberSolver(boardString, difficulty);
        return neighbourNumberSolver.propagate();
    }

    public Board getBoard() {
        return board;
    }
}
