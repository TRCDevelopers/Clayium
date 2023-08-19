package com.github.trcdeveloppers.clayium.interfaces;

import net.minecraft.util.EnumFacing;

public interface IConnectable {
    default int getInput(EnumFacing facing) {
        return -1;
    }

    default int getOutput(EnumFacing facing) {
        return -1;
    }

    default int rollInput(EnumFacing facing) {
        return -1;
    }

    default int rollOutput(EnumFacing facing) {
        return -1;
    }

    void setInput(EnumFacing facing, int mode);

    void setOutput(EnumFacing facing, int mode);

    void clearInput(EnumFacing facing);

    void clearOutput(EnumFacing facing);

}
