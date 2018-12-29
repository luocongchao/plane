package com.example.admin.myapplication;

public class ProtocolUtil {

    public static BitData getHighBitLowBit(char data) {
        return new BitData((char) (data >> 8), (char) (data & 0xFF));
    }

    public static int mergeHighBitLowBit(char h, char l) {
        return (h << 8) | l;
    }
}
