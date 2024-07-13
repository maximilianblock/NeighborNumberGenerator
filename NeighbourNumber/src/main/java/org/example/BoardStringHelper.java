package org.example;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class BoardStringHelper {

    //returns an arrayList containing all the Values of the boardString
    public static ArrayList<Value> getValuesFromString(String boardString) {
        ArrayList<Value> list = new ArrayList<>();
        String[] strings = boardString.split("\\*", 2);
        if (strings[1].equals("")) return list;
        String[] valueStrings = strings[1].split(";");
        for (String valStr : valueStrings) {
            list.add(getValueFromString(valStr));
        }

        return list;
    }

    //returns a list containing all values of targetString that are not in boardString
    // return = (values in targetString) - (values in boardString)
    public static ArrayList<Value> getValuesNotInString(String boardString, String targetString) {
        ArrayList<Value> arrayList = BoardStringHelper.getValuesFromString(boardString);
        ArrayList<Value> target = BoardStringHelper.getValuesFromString(targetString);

        for (Value val : arrayList) {
            target.remove(val);
        }
        return target;
    }

    //returns an array containing a version of the boardString without each value
    public static ArrayList<String> getStringWithoutEachValue(String boardString) {
        ArrayList<String> strings = new ArrayList<>();
        int length = boardString.length();
        if (length < 8) {
            strings.add(boardString.substring(0, 2));
            return strings;
        }
        strings.add(boardString.substring(0, 2) + boardString.substring(8, length));
        for (int i = 13; i <= length; i += 6) {
            strings.add(boardString.substring(0, i - 6) + boardString.substring(i, length));
        }
        Collections.shuffle(strings);
        return strings;
    }

    // creates a value out of the string representation of the value
    public static Value getValueFromString(String str) {
        String[] strings = str.split(",", 3);
        Value value = new Value(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), Integer.parseInt(strings[2]));
        return value;
    }

    //adds a value to the boardString but keeps order in tact
    public static String addValueToBoardStringInOrder(String boardString, Value val) {
        String[] strings = boardString.split("\\*", 2);
        if (strings[1].equals("")) {
            return boardString + val.getString();
        }
        ArrayList<Value> values = BoardStringHelper.getValuesFromString(boardString);

        int index = 0;
        for (Value currentValue : values) {
            if (currentValue.getPos().getX() > val.getPos().getX()
                    || (currentValue.getPos().getX() == val.getPos().getX()
                    && currentValue.getPos().getY() > val.getPos().getY())) {
                break;
            }
            index++;
        }
        values.add(index, val);

        String newBoardString = strings[0] + "*";
        for (Value value : values) {
            newBoardString = newBoardString + value.getString() + ";";
        }
        return newBoardString.substring(0, newBoardString.length() - 1);
    }

    //prints poard to console
    public static void printBoard(String s) {
        String[] strings = s.split("\\*", 2);
        int n = Integer.parseInt(strings[0]);
        ArrayList<Value> arrayList = getValuesFromString(s);
        int[][] array = new int[n][n];
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                array[x][y] = 0;
            }
        }
        for (Value val : arrayList) {
            array[val.getPos().getX()][val.getPos().getY()] = val.getVal();
        }

        String horizontalLine = "------";
        horizontalLine = horizontalLine.repeat(n);
        horizontalLine = horizontalLine + "-";
        for (int y = 0; y < n; y++) {
            System.out.println(horizontalLine);
            String line = "|  ";
            for (int x = 0; x < n; x++) {
                String c = " ";
                if (array[x][y] != 0) {
                    c = String.valueOf(array[x][y]);
                }
                line = line + c + "  |  ";
            }
            System.out.println(line);
        }
        System.out.println(horizontalLine);
    }

    //prints board s to printWriter
    public static void printBoardToFile(String s, PrintWriter printWriter) {
        String[] strings = s.split("\\*", 2);
        int n = Integer.parseInt(strings[0]);
        ArrayList<Value> arrayList = getValuesFromString(s);
        int[][] array = new int[n][n];
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                array[x][y] = 0;
            }
        }
        for (Value val : arrayList) {
            array[val.getPos().getX()][val.getPos().getY()] = val.getVal();
        }

        String horizontalLine = "------";
        horizontalLine = horizontalLine.repeat(n);
        horizontalLine = horizontalLine + "-";
        for (int y = 0; y < n; y++) {
            printWriter.println(horizontalLine);
            String line = "|  ";
            for (int x = 0; x < n; x++) {
                String c = " ";
                if (array[x][y] != 0) {
                    c = String.valueOf(array[x][y]);
                }
                line = line + c + "  |  ";
            }
            printWriter.println(line);
        }
        printWriter.println(horizontalLine);
    }

    //returns number of values in this string
    public static int getNumberOfValue(String boardString) {
        String[] Strings = boardString.split("\\*", 2);
        String[] valueStrings = Strings[1].split(";");
        return valueStrings.length;
    }

    //returns boardString but with value at position index removed
    public static String removeValueFromString(String boardString, int index) {
        int endOfFirstBlock = 2 + (index * 6);
        String firstBlock = boardString.substring(0, endOfFirstBlock);
        int startOfSecondBlock = endOfFirstBlock + 6;
        if (startOfSecondBlock >= boardString.length() - 1) {
            return firstBlock.substring(0, firstBlock.length() - 1);
        }
        String secondBlock = boardString.substring(startOfSecondBlock);
        return firstBlock + secondBlock;
    }

    // returns the boardString but with a random value removed
    public static String removeRandomValue(String boardString) {
        int number = getNumberOfValue(boardString);
        Random rand = new Random();
        int n = rand.nextInt(number);
        return removeValueFromString(boardString, n);
    }
}
