package org.jurassicraft.server.api;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.Random;

public interface CleanableItem
{
    boolean isCleanable(ItemStack stack);

    ItemStack getCleanedItem(ItemStack stack, Random random);

    static CleanableItem getCleanableItem(ItemStack stack)
    {
        if (stack != null)
        {
            Item item = stack.getItem();

            if (item instanceof ItemBlock)
            {
                Block block = ((ItemBlock) item).getBlock();

                if (block instanceof CleanableItem)
                {
                    return (CleanableItem) block;
                }
            }
            else if (item instanceof CleanableItem)
            {
                return (CleanableItem) item;
            }
        }

        return null;
    }

    static boolean isCleanableItem(ItemStack stack)
    {
        return getCleanableItem(stack) != null;
    }
}
