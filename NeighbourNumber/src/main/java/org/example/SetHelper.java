package org.example;

public class SetHelper {
    //returns an array containing all numbers 1 to num-1
    //usefull for the min part of the minMaxConstraint
    public static int[] setLessThan(int num) {
        int count = num - 1;
        if (count <= 0) {
            return new int[0];
        }
        int[] array = new int[count];
        int x = 0;
        for (int i = 0; i < count; i++) {
            x++;
            array[i] = x;
        }
        return array;
    }
}
