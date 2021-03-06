package tfar.classicbar.overlays.vanilla;


import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraftforge.client.gui.ForgeIngameGui;
import tfar.classicbar.Color;
import tfar.classicbar.config.ModConfig;
import tfar.classicbar.overlays.IBarOverlay;

import static tfar.classicbar.ColorUtils.calculateScaledColor;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.showHealthNumbers;

public class Health implements IBarOverlay {

  private double playerHealth = 0;
  private long healthUpdateCounter = 0;
  private double lastPlayerHealth = 0;

  public boolean side;

  @Override
  public IBarOverlay setSide(boolean side) {
    this.side = side;
    return this;
  }

  @Override
  public boolean rightHandSide() {
    return side;
  }

  @Override
  public boolean shouldRender(PlayerEntity player) {
    return true;
  }

  @Override
  public void renderBar(PlayerEntity player, int width, int height) {
    int updateCounter = mc.ingameGUI.getTicks();

    double health = player.getHealth();
    boolean highlight = healthUpdateCounter > (long) updateCounter && (healthUpdateCounter - (long) updateCounter) / 3 % 2 == 1;

    //player is damaged and resistant
    if (health < playerHealth && player.hurtResistantTime > 0) {
      healthUpdateCounter = (long) (updateCounter + 20);
      lastPlayerHealth = playerHealth;
    } else if (health > playerHealth && player.hurtResistantTime > 0) {
      healthUpdateCounter = (long) (updateCounter + 10);
      /* lastPlayerHealth = playerHealth;*/
    }
    playerHealth = health;
    double displayHealth = health + (lastPlayerHealth - health) * ((double) player.hurtResistantTime / player.maxHurtResistantTime);

    int xStart = width / 2 - 91;
    int yStart = height - ForgeIngameGui.left_height;
    double maxHealth = player.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getValue();

    RenderSystem.pushMatrix();
    RenderSystem.enableBlend();
    int k5 = 16;

    if (player.isPotionActive(Effects.POISON)) k5 += 36;//evaluates to 52
    else if (player.isPotionActive(Effects.WITHER)) k5 += 72;//evaluates to 88

    int i4 = (highlight) ? 18 : 0;

    //Bar background
    drawTexturedModalRect(xStart, yStart, 0, i4, 81, 9);

    //is the bar changing
    //Pass 1, draw bar portion
    int alpha = health <= 0 ? 1 : health / maxHealth <= ModConfig.lowHealthThreshold.get() && ModConfig.lowHealthWarning.get() ?
            (int) (System.currentTimeMillis() / 250) % 2 : 1;

    //interpolate the bar
    if (displayHealth != health) {
      //reset to white
      RenderSystem.color4f(1, 1, 1, alpha);
      if (displayHealth > health) {
        //draw interpolation
        drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(displayHealth, maxHealth), 7);
        //Health is increasing, idk what to do here
      } else {/*
                  f = xStart + getWidth(health, maxHealth);
                  drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(health - displayHealth, maxHealth), 7, general.style, true, true);*/
      }
    }

    //calculate bar color

    calculateScaledColor(health, maxHealth, k5).color2Gla(alpha);
    //draw portion of bar based on health remaining
    drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(health, maxHealth), 7);

    if (k5 == 52) {
      //draw poison overlay
      RenderSystem.color4f(0, .5f, 0, .5f);
      drawTexturedModalRect(xStart + 1, yStart + 1, 1, 36, getWidth(health, maxHealth), 7);
    }

    Color.reset();

    RenderSystem.disableBlend();
    RenderSystem.popMatrix();
  }

  @Override
  public boolean shouldRenderText() {
    return showHealthNumbers.get();
  }

  @Override
  public void renderText(PlayerEntity player, int width, int height) {
    double health = player.getHealth();

    int xStart = width / 2 - 91;
    int yStart = height - ForgeIngameGui.left_height;
    double maxHealth = player.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getValue();

    int k5 = 16;

    if (player.isPotionActive(Effects.POISON)) k5 += 36;//evaluates to 52
    else if (player.isPotionActive(Effects.WITHER)) k5 += 72;//evaluates to 88

    int h1 = (int) Math.round(health);
    int i2 = ModConfig.displayIcons.get() ? 1 : 0;
    if (ModConfig.showPercent.get()) h1 = (int) (100 * health / maxHealth);
    int i1 = getStringLength(h1 + "");

    drawStringOnHUD(h1 + "", xStart - 9 * i2 - i1 + leftTextOffset, yStart - 1, calculateScaledColor(health, maxHealth, k5).colorToText());
  }

  @Override
  public void renderIcon(PlayerEntity player, int width, int height) {
    mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);

    int k5 = 16;

    if (player.isPotionActive(Effects.POISON)) k5 += 36;//evaluates to 52
    else if (player.isPotionActive(Effects.WITHER)) k5 += 72;//evaluates to 88

    int xStart = width / 2 - 91;
    int yStart = height - ForgeIngameGui.left_height;
    int i5 = (player.world.getWorldInfo().isHardcore()) ? 5 : 0;
    //Draw health icon
    //heart background
    Color.reset();

    drawTexturedModalRect(xStart - 10, yStart, 16, 9 * i5, 9, 9);
    //heart
    drawTexturedModalRect(xStart - 10, yStart, 36 + k5, 9 * i5, 9, 9);
  }

  @Override
  public String name() {
    return "health";
  }
}