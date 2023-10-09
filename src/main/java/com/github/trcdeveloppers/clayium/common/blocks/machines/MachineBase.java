package com.github.trcdeveloppers.clayium.common.blocks.machines;

import com.github.trcdeveloppers.clayium.common.interfaces.IClayEnergyContainer;
import com.github.trcdeveloppers.clayium.common.interfaces.IConnectable;
import com.github.trcdeveloppers.clayium.common.interfaces.IPipable;
import com.github.trcdeveloppers.clayium.common.interfaces.ITiered;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumFacing;

import java.util.HashMap;
import java.util.Map;

public abstract class MachineBase extends Block implements ITiered, IClayEnergyContainer, IPipable, IConnectable {
    private final Map<EnumFacing, Integer> inputMap = new HashMap<>();
    private final Map<EnumFacing, Integer> outputMap = new HashMap<>();
    private long clayEnergy = 0L;
    private boolean isPipe = false;

    public MachineBase(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }
    public MachineBase(Material materialIn) {
        super(materialIn);
    }

    @Override
    public long getClayEnergy() {
        return clayEnergy;
    }

    @Override
    public void setClayEnergy(long clayEnergy) {
        this.clayEnergy = clayEnergy;
    }

    @Override
    public long addClayEnergy(long energy) {
        return (this.clayEnergy += energy);
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
