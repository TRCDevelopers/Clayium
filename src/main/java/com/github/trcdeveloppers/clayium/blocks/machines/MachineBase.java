package com.github.trcdeveloppers.clayium.blocks.machines;

import com.github.trcdeveloppers.clayium.blocks.ClayiumBlocks;
import com.github.trcdeveloppers.clayium.interfaces.IClayEnergyContainer;
import com.github.trcdeveloppers.clayium.interfaces.IConnectable;
import com.github.trcdeveloppers.clayium.interfaces.IPipable;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import net.minecraft.util.EnumFacing;

import java.util.HashMap;
import java.util.Map;

public abstract class MachineBase extends ClayiumBlocks.ClayiumBlock implements ITiered, IClayEnergyContainer, IPipable, IConnectable {
    private final Map<EnumFacing, Integer> inputMap = new HashMap<>();
    private final Map<EnumFacing, Integer> outputMap = new HashMap<>();
    private long clayEnergy = 0L;
    private boolean isPipe = false;

    @Override
    public long getClayEnergy() {
        return clayEnergy;
    }

    public void setClayEnergy(long clayEnergy) {
        this.clayEnergy = clayEnergy;
    }

    @Override
    public void setInput(EnumFacing facing, int mode) {
        inputMap.put(facing, mode);
    }

    @Override
    public void setOutput(EnumFacing facing, int mode) {
        outputMap.put(facing, mode);
    }

    @Override
    public void clearOutput(EnumFacing facing) {
        outputMap.put(facing, -1);
    }

    @Override
    public void clearInput(EnumFacing facing) {
        inputMap.put(facing, -1);
    }

    @Override
    public void setPipe(boolean isPipe) {
        this.isPipe = isPipe;
    }

    @Override
    public boolean isPipe() {
        return this.isPipe;
    }
}
