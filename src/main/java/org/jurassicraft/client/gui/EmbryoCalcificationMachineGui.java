package org.jurassicraft.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jurassicraft.server.container.EmbryoCalcificationMachineContainer;

@SideOnly(Side.CLIENT)
public class EmbryoCalcificationMachineGui extends GuiContainer
{
    private static final ResourceLocation texture = new ResourceLocation("jurassicraft:textures/gui/embryo_calcification_machine.png");
    /**
     * The player inventory bound to this GUI.
     */
    private final InventoryPlayer playerInventory;
    private IInventory calcificationMachine;

    public EmbryoCalcificationMachineGui(InventoryPlayer playerInv, IInventory fossilGrinder)
    {
        super(new EmbryoCalcificationMachineContainer(playerInv, (TileEntity) fossilGrinder));
        this.playerInventory = playerInv;
        this.calcificationMachine = fossilGrinder;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = this.calcificationMachine.getDisplayName().getUnformattedText();
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 4, 4210752);
        this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Args : renderPartialTicks, mouseX, mouseY
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

        int progress = this.getProgress(24);
        int progress1 = this.getProgress(9);
        int progress2 = this.getProgress(20);
        this.drawTexturedModalRect(k + 67, l + 31, 176, 14, progress + 1, 16);
        // Syringe Top
        this.drawTexturedModalRect(k + 38, l + 32, 177, 32, 9, progress1);
        // Syringe Inside
        this.drawTexturedModalRect(k + 38, l + 38, 197, 38, 9, progress2);
        // Clean up
        this.drawTexturedModalRect(k + 38, l + 32, 187, 32, 9, progress1 - 1);

    }

    private int getProgress(int scale)
    {
        int j = this.calcificationMachine.getField(0);
        int k = this.calcificationMachine.getField(1);
        return k != 0 && j != 0 ? j * scale / k : 0;
    }
}
