package net.lakazatong.compactcircuitsmod.utils;

public class BitField {
    private int bits;

    public BitField(int initialBits) {
        this.bits = initialBits;
    }

    public boolean isSet(int index) {
        return (bits & (1 << index)) != 0;
    }

    public void setBit(int index, boolean value) {
        if (value) {
            bits |= (1 << index); // Set bit to 1
        } else {
            bits &= ~(1 << index); // Clear bit to 0
        }
    }

    public int getBits() {
        return bits;
    }

    public void setBits(int newBits) {
        this.bits = newBits;
    }
}
