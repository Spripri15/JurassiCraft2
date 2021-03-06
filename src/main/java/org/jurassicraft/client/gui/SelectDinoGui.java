package org.jurassicraft.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.entity.base.EntityHandler;
import org.jurassicraft.server.message.PlacePaddockSignMessage;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectDinoGui extends GuiScreen
{
    private final Map<Integer, ResourceLocation> TEXTURES = new HashMap<>();

    private int page;
    private int pageCount;

    public int columnsPerPage = 5;
    public int rowsPerPage = 3;

    private GuiButton forward;
    private GuiButton backward;

    private BlockPos pos;
    private EnumFacing facing;
    private EnumHand hand;

    private List<Dinosaur> dinosaurs;

    public SelectDinoGui(BlockPos pos, EnumFacing facing, EnumHand hand)
    {
        this.pos = pos;
        this.facing = facing;
        this.hand = hand;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.buttonList.add(new GuiButton(0, (this.width - 150) / 2, this.height / 5 + 150, 150, 20, I18n.format("gui.cancel")));
        this.buttonList.add(backward = new GuiButton(1, this.width / 2 - 105, this.height / 5 + 150, 20, 20, "<"));
        this.buttonList.add(forward = new GuiButton(2, this.width / 2 + 85, this.height / 5 + 150, 20, 20, ">"));

        page = 0;

        dinosaurs = new ArrayList<>(EntityHandler.INSTANCE.getRegisteredDinosaurs());

        Collections.sort(dinosaurs);

        pageCount = dinosaurs.size() / (columnsPerPage * rowsPerPage);

        enableDisablePages();
    }

    /**
     * Called when a mouse button is released.  Args : mouseX, mouseY, releaseButton
     */
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);

        if (state == 0)
        {
            int signsPerPage = columnsPerPage * rowsPerPage;
            int xOffset = 0;
            int yOffset = 0;

            pageCount = dinosaurs.size() / signsPerPage;

            for (int i = 0; i < dinosaurs.size(); i++)
            {
                if (i >= signsPerPage * page && i < signsPerPage * (page + 1))
                {
                    float scale = 3F;

                    int x = (int) ((width / 2 - 140 + xOffset) / scale);
                    int y = (int) ((height / 8 + yOffset - 20) / scale);

                    float scaledMouseX = mouseX / scale;
                    float scaledMouseY = mouseY / scale;

                    if (scaledMouseX > x && scaledMouseY > y && scaledMouseX < x + 16 && scaledMouseY < y + 16)
                    {
                        selectDinosaur(dinosaurs.get(i));
                        break;
                    }

                    xOffset += 180 / scale;

                    if (i % columnsPerPage >= columnsPerPage - 1)
                    {
                        xOffset = 0;
                        yOffset += 180 / scale;
                    }
                }
            }
        }
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        int id = button.id;

        if (id == 1)
        {
            if (page > 0)
            {
                page--;
            }

        }
        else if (id == 2)
        {
            if (page < pageCount)
            {
                page++;
            }
        }
        else
        {
            mc.displayGuiScreen(null);
        }

        enableDisablePages();
    }

    public void enableDisablePages()
    {
        backward.enabled = !(page <= 0);
        forward.enabled = page < pageCount;
    }

    public void selectDinosaur(Dinosaur dinosaur)
    {
        mc.displayGuiScreen(null);

        if (!mc.thePlayer.capabilities.isCreativeMode)
        {
            InventoryPlayer inventory = mc.thePlayer.inventory;
            inventory.decrStackSize(inventory.currentItem, 1);
        }

        JurassiCraft.NETWORK_WRAPPER.sendToServer(new PlacePaddockSignMessage(hand, facing, pos, dinosaur));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);

        int signsPerPage = columnsPerPage * rowsPerPage;
        int xOffset = 0;
        int yOffset = 0;

        pageCount = dinosaurs.size() / signsPerPage;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        int i = 0;

        for (Dinosaur dinosaur : dinosaurs)
        {
            if (i >= signsPerPage * page && i < signsPerPage * (page + 1))
            {
                int id = EntityHandler.INSTANCE.getDinosaurId(dinosaur);

                GlStateManager.pushMatrix();

                ResourceLocation texture = TEXTURES.get(id);

                if (texture == null)
                {
                    texture = new ResourceLocation(JurassiCraft.MODID, "textures/paddock/" + EntityHandler.INSTANCE.getDinosaurById(id).getName().toLowerCase() + ".png");
                    TEXTURES.put(id, texture);
                }

                mc.getTextureManager().bindTexture(texture);

                float scale = 3.0F;

                GlStateManager.scale(scale, scale, scale);

                int x = (int) ((width / 2 - 140 + xOffset) / scale);
                int y = (int) ((height / 8 + yOffset - 20) / scale);

                float scaledMouseX = mouseX / scale;
                float scaledMouseY = mouseY / scale;

                if (scaledMouseX > x && scaledMouseY > y && scaledMouseX < x + 16 && scaledMouseY < y + 16)
                {
                    drawBoxOutline(x - 1, y - 1, 18, 17, 1, 0x60606060);
                }

                drawTexturedModalRect(x, y, 0, 0, 16, 16, 16, 16);

                float textScale = 0.22F;

                GlStateManager.scale(textScale, textScale, textScale);

                drawCenteredString(mc.fontRendererObj, dinosaur.getName(), (int) ((x + 8) / textScale), (int) ((y + 17) / textScale), 0xFFFFFF);

                GlStateManager.popMatrix();

                xOffset += 180 / scale;

                if (i % columnsPerPage >= columnsPerPage - 1)
                {
                    xOffset = 0;
                    yOffset += 180 / scale;
                }
            }

            i++;
        }
    }

    public void drawScaledRect(int x, int y, int width, int height, int colour)
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        float red = (float) (colour >> 24 & 255) / 255.0F;
        float green = (float) (colour >> 16 & 255) / 255.0F;
        float blue = (float) (colour >> 8 & 255) / 255.0F;
        float a = (float) (colour & 255) / 255.0F;

        GL11.glColor4f(red, green, blue, a);

        float f = 1.0F / (float) width;
        float f1 = 1.0F / (float) height;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexBuffer = tessellator.getBuffer();
        vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexBuffer.pos((double) (x), (double) (y + height), (double) this.zLevel).tex((double) (0), (double) ((float) (height) * f1)).endVertex();
        vertexBuffer.pos((double) (x + width), (double) (y + height), (double) this.zLevel).tex((double) ((float) (width) * f), (double) ((float) (height) * f1)).endVertex();
        vertexBuffer.pos((double) (x + width), (double) (y), (double) this.zLevel).tex((double) ((float) (width) * f), (double) ((float) 0)).endVertex();
        vertexBuffer.pos((double) (x), (double) (y), (double) this.zLevel).tex((double) ((float) 0), (double) ((float) 0)).endVertex();
        tessellator.draw();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public void drawBoxOutline(int x, int y, int sizeX, int sizeY, int borderSize, int colour)
    {
        drawScaledRect(x, y, sizeX, borderSize, colour);
        drawScaledRect(x + sizeX, y, borderSize, sizeY + borderSize, colour);
        drawScaledRect(x, y + borderSize, borderSize, sizeY, colour);
        drawScaledRect(x + borderSize, y + sizeY, sizeX - borderSize, borderSize, colour);
    }

    public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height, int textureWidth, int textureHeight)
    {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(x, y + height, this.zLevel).tex(textureX / textureWidth, (textureY + height) / textureHeight).endVertex();
        vertexbuffer.pos(x + width, y + height, this.zLevel).tex((textureX + width) / textureWidth, (textureY + height) / textureHeight).endVertex();
        vertexbuffer.pos(x + width, y, this.zLevel).tex((textureX + width) / textureWidth, textureY / textureHeight).endVertex();
        vertexbuffer.pos(x, y, this.zLevel).tex(textureX / width, textureY / textureHeight).endVertex();
        tessellator.draw();
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
