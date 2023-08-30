package com.github.trcdeveloppers.clayium.items.parts.shards;

import com.github.trcdeveloppers.clayium.annotation.CItem;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@CItem(registryName = "adv_industrial_clay_shard")
public class ItemAdvIndustrialClayShard extends Item implements ClayiumItems.ClayiumItem {

    public ItemAdvIndustrialClayShard() {
        super();
        setCreativeTab(CLAYIUM);
    }

}