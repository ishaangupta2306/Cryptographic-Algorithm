package com.algorithm;

import java.util.Arrays;
//https://rosettacode.org/wiki/MD5/Implementation
public class Md5CheckSum {

    private static int bufferA = 0x67452301;
    private static int bufferB = (int)0xEFCDAB89;
    private static int bufferC = (int)0x98BADCFE;
    private static int bufferD = 0x10325476;
    private static int k[] = new int[64];

    private static final int INIT_A = 0x67452301;
    private static final int INIT_B = (int)0xEFCDAB89L;
    private static final int INIT_C = (int)0x98BADCFEL;
    private static final int INIT_D = 0x10325476;

    private static final int[] SHIFT_AMTS = {
            7, 12, 17, 22,
            5,  9, 14, 20,
            4, 11, 16, 23,
            6, 10, 15, 21
    };

    private static final int[] TABLE_T = new int[64];
    static
    {
        for (int i = 0; i < 64; i++)
            TABLE_T[i] = (int)(long)((1L << 32) * Math.abs(Math.sin(i + 1)));
    }

    private static final int[] r = {
            7, 12, 17, 22,
            5,  9, 14, 20,
            4, 11, 16, 23,
            6, 10, 15, 21
    };



    private static void setup(){
        for(int i = 0; i < 64; i++){
            k[i] = (int) Math.floor(Math.abs(Math.sin(i + 1)) * (Math.pow(2,32)));
        }
    }

    public static String paddingBits(String input){
        String result = convertStringToBinary(input);
        System.out.println("result" + result.length());
        int stringLength = result.length();
        int x = 0;
        if(stringLength < 512){
            x = 0;
        }

        else{
            while(x < stringLength){
                x += 512;
            }
        }
        System.out.println("x" + x);
        int multiple = x+1;
        int padding_zeroes = (512 * multiple) - (64 + 1 + stringLength);
        String paddedString = result + "1";
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < padding_zeroes; i++){
            builder.append("0");
        }
        paddedString += builder.toString();
        return paddedString;
    }
    //https://www.techiedelight.com/convert-integer-binary-string-java/
    public static String paddingLength(int inputLength, String bitPaddedString){
        String x = Integer.toBinaryString(inputLength);
        System.out.println(x);
        String value = String.format("%64s", Integer.toBinaryString(inputLength)).replaceAll(" ", "0");
        System.out.println(value);
        return (bitPaddedString + value);
    }

    private static int leftrotate (int x, int c){
        return (x << c) | (x >> (32-c));
    }

    private static String processEachBlock(String paddedString){
        String[] chunks = paddedString.split("(?<=\\G.{" + 512 + "})");
        System.out.println(Arrays.toString(chunks));
        for(String chunk : chunks){
            String s = chunk;
            String[] littleChunks = s.split("(?<=\\G.{" + 32 + "})");
            int a = bufferA;
            int b = bufferB;
            int c = bufferC;
            int d = bufferD;

            int f = 0, g = 0, h = 0;
            for(int i = 0; i < 64; i++){
                if (i <= 15){
                    f = (b & c) | ((~b) & d);
                    g = i;
                }

                else if(i <= 16 && i >= 31){
                    f = (d & b) | ((~d) & c);
                    g = (5*i + 1) % 16;
                }

                else if(i <= 32 && i >= 47){
                    f = b ^ c ^ d;
                    g = (3*i + 5) % 16;
                }

                else if(i <= 48 && i >= 63){
                    f = c ^ (b | (~d));
                    g = (7*i) % 16;

                }
                int temp = d;
                d = c;
                c = b;
                int x = Integer.parseInt(littleChunks[g],2);
                b = b + leftrotate((a + f + k[i] + x), r[i%4]);
                a = temp;


            }
            //Add this chunk's hash to result so far:
            bufferA = bufferA + a;
            bufferB = bufferB + b;
            bufferC = bufferC + c;
            bufferD = bufferD + d;

        }

        String digest = String.valueOf(bufferA) + String.valueOf(bufferB) + String.valueOf(bufferC) + String.valueOf(bufferD);
        return digest;

    }

    private static byte[] preprocessing(byte[] message, int totalLen){
        byte[] paddingBytes = new byte[totalLen - message.length];
        paddingBytes[0] = (byte)0x80;

        long messageLenBits = (long)message.length << 3;
        for (int i = 0; i < 8; i++)
        {
            paddingBytes[paddingBytes.length - 8 + i] = (byte)messageLenBits;
            messageLenBits >>>= 8;
        }
        return paddingBytes;
    }

    private static int determineBuffer(byte[] message, byte[] paddingBytes, int index, int j, int[] buffer ){
        if (index < message.length)
            return ((int) message[index] << 24) | (buffer[j >>> 2] >>> 8);
        else
        return ((int) paddingBytes[index - message.length] << 24) | (buffer[j >>> 2] >>> 8);
    }

    private static byte[] postProcessing(int a, int b, int c, int d){
        byte[] result = new byte[16];
        int count = 0;
        for (int i = 0; i < 4; i++)
        {
            int n = (i == 0) ? a : ((i == 1) ? b : ((i == 2) ? c : d));
            for (int j = 0; j < 4; j++)
            {
                result[count++] = (byte)n;
                n >>>= 8;
            }
        }
        return result;
    }

    private static int rotateLeft(int a, int f, int j, int[] buffer,  int bufferIndex){
        int amount =  SHIFT_AMTS[(j >>> 4 << 2) | (j & 3)];
        return Integer.rotateLeft(a + f + buffer[bufferIndex] + TABLE_T[j], amount);
    }

    public static byte[] computeMD5(byte[] message) {
        byte[] paddingBytes = preprocessing(message, ((message.length + 8) >>> 6) + 1 << 6);
        int a = INIT_A;
        int b = INIT_B;
        int c = INIT_C;
        int d = INIT_D;

        int[] buffer = new int[16];
        int index = 0;
        for (int i = 0; i < ((message.length + 8) >>> 6) + 1; i ++)
        {
            index = i << 6;
            for (int j = 0; j < 64; j++, index++)
                buffer[j >>> 2] = determineBuffer(message, paddingBytes, index, j, buffer);

            int originalA = a;
            int originalB = b;
            int originalC = c;
            int originalD = d;
            for (int j = 0; j < 64; j++)
            {
                int f = 0;
                int bufferIndex = j;
                if (j >>> 4 == 0) {
                    f = (b & c) | (~b & d);
                } else if (j >>> 4 == 1) {
                    f = (b & d) | (c & ~d);
                    bufferIndex = (bufferIndex * 5 + 1) & 0x0F;
                } else if (j >>> 4 == 2) {
                    f = b ^ c ^ d;
                    bufferIndex = (bufferIndex * 3 + 5) & 0x0F;
                } else if (j >>> 4 == 3) {
                    f = c ^ (b | ~d);
                    bufferIndex = (bufferIndex * 7) & 0x0F;
                }
                int temp = b + rotateLeft(a, f,j, buffer,  bufferIndex);
                a = d;
                d = c;
                c = b;
                b = temp;
            }
            a += originalA;
            b += originalB;
            c += originalC;
            d += originalD;
        }
        return postProcessing(a, b, c, d);
    }

    public static String toHexString(byte[] b){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++)
        {
            sb.append(String.format("%02X", b[i] & 0xFF));
        }
        return sb.toString();
    }



    public static String convertStringToBinary(String input) {
        StringBuilder result = new StringBuilder();
        char[] chars = input.toCharArray();
        for (char aChar : chars) {
            result.append(String.format("%8s", Integer.toBinaryString(aChar))
                            .replaceAll(" ", "0")
            );
        }
        return result.toString();
    }

    public static void main(String[] args)
    {
        String[] testStrings = { "", "a", "abc", "message digest", "abcdefghijklmnopqrstuvwxyz", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", "12345678901234567890123456789012345678901234567890123456789012345678901234567890" };
        for (String s : testStrings)
            System.out.println("0x" + toHexString(computeMD5(s.getBytes())) + " <== \"" + s + "\"");
        return;
    }

//    public static void main(String[] args) {
//        String input = "They are deterministic";
//        String paddedString = paddingBits(input);
//        String result = paddingLength(input.length() * 8, paddedString);
//        System.out.println(result);
//        System.out.println(result.length());
//        String finalResult = processEachBlock(result);
//        System.out.println(finalResult);
//        System.out.println(finalResult.length());
//    }
}
