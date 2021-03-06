package tfar.classicbar;

import tfar.classicbar.config.ModConfig;

import java.util.List;


public class ColorUtils {
    public static Color hex2Color(String s) {
        int i1 = Integer.decode(s);
        int r = i1 >> 16 & 0xFF;
        int g = i1 >> 8 & 0xFF;
        int b = i1 & 0xFF;
        return new Color(r, g, b);
    }

    public static Color calculateScaledColor(double d1, double d2, int effect) {
        double d3 = (d1 / d2);

        List<? extends String> colorCodes;
        List<? extends Double> colorFractions;

        switch (effect){
            case 16: colorCodes = ModConfig.normalColors.get();
            colorFractions = ModConfig.normalFractions.get(); break;
            case 52: colorCodes = ModConfig.poisonedColors.get();
                colorFractions = ModConfig.poisonedFractions.get(); break;
            case 88: colorCodes = ModConfig.witheredColors.get();
                colorFractions = ModConfig.witheredFractions.get(); break;
            default: return Color.BLACK;
        }

        if (colorCodes.size() != colorFractions.size()) return Color.BLACK;
        int i1 = colorFractions.size() - 1;
        int i3 = 0;
        for (int i2 = 0; i2 < i1; i2++) {
            if (d3 < colorFractions.get(i2)) break;
            i3++;
        }

        //return first color in the list if health is too low
        if (d3 <= colorFractions.get(0))
            return hex2Color(colorCodes.get(0));
        //return last color in the list if health is too high
        if (d3 >= colorFractions.get(colorFractions.size() - 1))
            return hex2Color(colorCodes.get(colorCodes.size() - 1));

        Color c1 = hex2Color(colorCodes.get(i3 - 1));
        Color c2 = hex2Color(colorCodes.get(i3));

        double d4 = d3 - colorFractions.get(i3 - 1) / (colorFractions.get(i3) - colorFractions.get(i3 - 1));
        return c1.colorBlend(c2, d4);
    }
}
