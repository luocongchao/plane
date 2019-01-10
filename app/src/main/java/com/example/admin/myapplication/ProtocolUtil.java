package com.example.admin.myapplication;

public class ProtocolUtil {

    public static BitData getHighBitLowBit(char data) {
        return new BitData((char) (data >> 8), (char) (data & 0xFF));
    }

    public static int mergeHighBitLowBit(char h, char l) {
        return (h << 8) | l;
    }

    public static int mergeHighBitLowBit2(char h, char l) {
        short r = 0;
        r <<= 8;
        r |= h;
        r <<= 8;
        r |= l;
        return r;
    }
}
