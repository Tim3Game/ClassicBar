package tfar.classicbar.overlays.vanilla;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import tfar.classicbar.Color;
import tfar.classicbar.config.ModConfig;
import tfar.classicbar.overlays.IBarOverlay;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;

public class ArmorToughness implements IBarOverlay {

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
    return ModConfig.displayToughnessBar.get() && player.getAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getValue() >= 1;
  }

  @Override
  public void renderBar(PlayerEntity player, int width, int height) {
    //armor toughness stuff
    double armorToughness = player.getAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getValue();
    //Push to avoid lasting changes
    int xStart = width / 2 + 10;
    int yStart = height - getSidedOffset();
    RenderSystem.pushMatrix();
    RenderSystem.enableBlend();
    int f;
    //draw bar portion
    Color.reset();
    int toughnessindex = (int) Math.min(Math.ceil(armorToughness / 20) - 1, ModConfig.armorToughnessColorValues.get().size() - 1);

    if (armorToughness <= 20) {
      f = xStart + 79 - getWidth(armorToughness, 20);
      if (!ModConfig.fullToughnessBar.get()) drawScaledBar(armorToughness, 20, f - 1, yStart, false);
      else drawTexturedModalRect(f, yStart, 0, 0, 81, 9);

      //calculate bar color
      hex2Color(ModConfig.armorToughnessColorValues.get().get(0)).color2Gl();
      //draw portion of bar based on armor toughness amount
      drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(armorToughness, 20), 7);

    } else {
      drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);
      //we have wrapped, draw 2 bars
      int size = ModConfig.armorToughnessColorValues.get().size();
      //if we are out of colors wrap the bar
      if (toughnessindex < size && armorToughness % 20 != 0) {

        //draw complete first bar
        hex2Color(ModConfig.armorToughnessColorValues.get().get(toughnessindex - 1)).color2Gl();
        drawTexturedModalRect(xStart, yStart + 1, 0, 10, 79, 7);

        //draw partial second bar
        f = xStart + 79 - getWidth(armorToughness % 20, 20);

        hex2Color(ModConfig.armorToughnessColorValues.get().get(toughnessindex)).color2Gl();
        drawTexturedModalRect(f, yStart + 1, 0, 10, getWidth(armorToughness % 20, 20), 7);
      }
      //case 2, bar is a multiple of 20 or it is capped
      else {
        //draw complete second bar
        hex2Color(ModConfig.armorToughnessColorValues.get().get(toughnessindex)).color2Gl();
        drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, 79, 7);
      }
    }

    //Revert our state back
    RenderSystem.popMatrix();
  }

  @Override
  public boolean shouldRenderText() {
    return ModConfig.showArmorToughnessNumbers.get();
  }

  @Override
  public void renderText(PlayerEntity player, int width, int height) {
    int xStart = width / 2 + 10;
    int yStart = height - getSidedOffset();
    double armorToughness = player.getAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getValue();
    int toughnessindex = (int) Math.min(Math.ceil(armorToughness / 20) - 1, ModConfig.armorToughnessColorValues.get().size() - 1);
    //draw armor toughness amount
    int iq1 = (int) Math.floor(armorToughness);
    int iq2 = (ModConfig.displayIcons.get()) ? 1 : 0;

    int toughnesscolor = Integer.decode(ModConfig.armorToughnessColorValues.get().get(toughnessindex));
    if (ModConfig.showPercent.get()) iq1 = (int) armorToughness * 5;
    drawStringOnHUD(iq1 + "", xStart + 9 * iq2 + rightTextOffset, yStart - 1, toughnesscolor);
  }

  @Override
  public void renderIcon(PlayerEntity player, int width, int height) {
    mc.getTextureManager().bindTexture(ICON_BAR);
    int xStart = width / 2 + 10;
    int yStart = height - getSidedOffset();
    //Draw armor toughness icon
    drawTexturedModalRect(xStart + 82, yStart, 83, 0, 9, 9);
  }

  @Override
  public String name() {
    return "armortoughness";
  }
}
