package com.algorithm;

public class CheckSumUtil {
    //Array of shift by
    private static final int[] shift = {
            7, 12, 17, 22,
            5,  9, 14, 20,
            4, 11, 16, 23,
            6, 10, 15, 21
    };

    //Takes in the byte array and return Hexadecimal string
    protected static String toHexString(byte[] b){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            sb.append(String.format("%02X", b[i] & 0xFF));
        }
        return sb.toString();
    }

    protected static int rotateLeft(int a, int f, int j, int[] buffer,  int bufferIndex, int[] table){
        int amount =  shift[(j >>> 4 << 2) | (j & 3)];
        return Integer.rotateLeft(a + f + buffer[bufferIndex] + table[j], amount);
    }

    protected static int determineBuffer(byte[] message, byte[] paddingBytes, int index, int j, int[] buffer ){
        if (index < message.length)
            return ((int) message[index] << 24) | (buffer[j >>> 2] >>> 8);
        else
            return ((int) paddingBytes[index - message.length] << 24) | (buffer[j >>> 2] >>> 8);
    }
}
