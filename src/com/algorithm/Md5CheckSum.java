package com.algorithm;

/**
 * @author Ishaan Gupta
 */
public class Md5CheckSum {

    //Some constants for the algorithm
    private static final int buffer_A = 0x67452301;
    private static final int buffer_B = (int)0xEFCDAB89L;
    private static final int buffer_C = (int)0x98BADCFEL;
    private static final int buffer_D = 0x10325476;
    private static final int[] table = new int[64];

    //Processing done before algorithm
    private static byte[] preprocessing(byte[] message, int totalLen){
        int k = 0;
        while(k < table.length){
            table[k] = (int)(long)((1L << 32) * Math.abs(Math.sin(k + 1)));
            k++;
        }

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

    //Processing done after algorithm
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

    public static byte[] computeMD5(byte[] message) {
        byte[] paddingBytes = preprocessing(message, ((message.length + 8) >>> 6) + 1 << 6);
        int a = buffer_A;
        int b = buffer_B;
        int c = buffer_C;
        int d = buffer_D;

        int[] buffer = new int[16];
        int index = 0;
        for (int i = 0; i < ((message.length + 8) >>> 6) + 1; i ++)
        {
            index = i << 6;
            int k = 0;
            while(k < 64) {
                buffer[k >>> 2] = CheckSumUtil.determineBuffer(message, paddingBytes, index, k, buffer);
                k++;
                index++;
            }

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
                int temp = b + CheckSumUtil.rotateLeft(a, f,j, buffer,  bufferIndex, table);
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

    //Accepts string to compute the MD5Checksum hash using helper methods
    public static String executeMD5CheckSum(String st){
        byte[] b = st.getBytes();
        byte[] modifiedByteArray =  computeMD5(b);
        String stt = CheckSumUtil.toHexString(modifiedByteArray);
        return "0x" + stt;
    }
}
