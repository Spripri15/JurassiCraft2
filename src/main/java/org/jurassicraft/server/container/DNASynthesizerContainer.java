package org.jurassicraft.server.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.jurassicraft.server.container.slot.CustomSlot;
import org.jurassicraft.server.container.slot.SynthesizableItemSlot;
import org.jurassicraft.server.item.ItemHandler;
import org.jurassicraft.server.tile.DNASynthesizerTile;

public class DNASynthesizerContainer extends SyncedFieldContainer
{
    private DNASynthesizerTile dnaSynthesizer;

    public DNASynthesizerContainer(InventoryPlayer playerInventory, TileEntity tileEntity)
    {
        super((IInventory) tileEntity);

        this.dnaSynthesizer = (DNASynthesizerTile) tileEntity;
        this.addSlotToContainer(new SynthesizableItemSlot(dnaSynthesizer, 0, 38, 22));
        this.addSlotToContainer(new CustomSlot(dnaSynthesizer, 1, 24, 49, ItemHandler.INSTANCE.EMPTY_TEST_TUBE));
        this.addSlotToContainer(new CustomSlot(dnaSynthesizer, 2, 50, 49, ItemHandler.INSTANCE.DNA_NUCLEOTIDES));

        int i;

        for (i = 0; i < 2; i++)
        {
            for (int j = 0; j < 2; j++)
            {
                this.addSlotToContainer(new Slot(dnaSynthesizer, i + (j * 2) + 3, i * 18 + 119, j * 18 + 26));
            }
        }

        for (i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);

        if (!player.worldObj.isRemote)
        {
            dnaSynthesizer.closeInventory(player);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return dnaSynthesizer.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int i)
    {
        return null;
    }
}
