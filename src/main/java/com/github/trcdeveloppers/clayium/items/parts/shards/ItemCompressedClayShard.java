package com.github.trcdeveloppers.clayium.items.parts.shards;

import com.github.trcdeveloppers.clayium.annotation.CItem;
import com.github.trcdeveloppers.clayium.items.ClayiumItems;
import net.minecraft.item.Item;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
@CItem(registryName = "compressed_clay_shard")
public class ItemCompressedClayShard extends Item implements ClayiumItems.ClayiumItem {

    public ItemCompressedClayShard() {
        super();
        setCreativeTab(CLAYIUM);
    }

}