package com.example.admin.myapplication;

class BitData {
    public final char H;
    public final char L;

    public BitData(char h, char l) {
        H = h;
        L = l;
    }

    @Override
    public String toString() {
        return "BitData{" +
                "H=" + H +
                ", L=" + L +
                '}';
    }
}
