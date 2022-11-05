package com.algorithm;

public class Md5CheckSum {
    
    public static String paddingBits(String input){
        String result = convertStringToBinary(input);
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

    public static void main(String[] args) {
        String input = "They are deterministic";
        String paddedString = paddingBits(input);
        String result = paddingLength(input.length() * 8, paddedString);
        System.out.println(result);
        System.out.println(result.length());
    }
}
