package tfar.classicbar.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static tfar.classicbar.ModConfig.displayIcons;
import static tfar.classicbar.ModConfig.displayToughnessBar;
import static tfar.classicbar.ModUtils.*;

/*
    Class handles the drawing of the oxygen bar
 */

public class OxygenBarRenderer {
    private final Minecraft mc = Minecraft.getMinecraft();
    ;
    private int updateCounter = 0;
    private double playerAir = 1;
    private double lastAir = 1;

    private boolean forceUpdateIcons = false;

    public OxygenBarRenderer() {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void renderOxygenBar(RenderGameOverlayEvent.Pre event) {


        Entity renderViewEnity = this.mc.getRenderViewEntity();
        if (event.getType() != RenderGameOverlayEvent.ElementType.AIR
                || event.isCanceled()
                || !(renderViewEnity instanceof EntityPlayer)) {
            return;
        }
        event.setCanceled(true);
        EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();
        double air = player.getAir();
        if (player.getAir()>=300)return;
        int scaledWidth = event.getResolution().getScaledWidth();
        int scaledHeight = event.getResolution().getScaledHeight();
        //Push to avoid lasting changes

        updateCounter = mc.ingameGUI.getUpdateCounter();

        if (air != playerAir || forceUpdateIcons) {
            forceUpdateIcons = false;
        }

        playerAir = air;
        float xStart = scaledWidth / 2f + 9;
        float yStart = scaledHeight - 49;
        if(displayToughnessBar && player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()>0)yStart-=10;

        mc.profiler.startSection("air");
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();

        //Bind our Custom bar
        mc.getTextureManager().bindTexture(ICON_BAR);
        //Bar background
        drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);

        //draw portion of bar based on air amount

        float f = xStart+80-getWidth(air,300);
        GlStateManager.color(0,.9f,.9f);
        drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(air,300), 7);

        //draw air amount
        int h1 = (int) Math.floor(air);

        int c = 0x00dddd;
        int i3 = displayIcons ? 1 : 0;

        drawStringOnHUD(h1 + "", xStart + 81 + 10 * i3, yStart - 1, c, 0);
        //Reset back to normal settings

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        mc.getTextureManager().bindTexture(ICON_VANILLA);
        GuiIngameForge.left_height += 10;

        if (displayIcons) {
            //Draw air icon
            drawTexturedModalRect(xStart + 82, yStart, 16, 18, 9, 9);
        }

        GlStateManager.disableBlend();
        //Revert our state back
        GlStateManager.popMatrix();
        mc.profiler.endSection();
        event.setCanceled(true);
    }

}