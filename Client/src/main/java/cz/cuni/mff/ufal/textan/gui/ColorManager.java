package cz.cuni.mff.ufal.textan.gui;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.gui.Utils.IdType;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.paint.Color;

/**
 *
 * @author Vaclav Pernicka
 */
public class ColorManager {
    private static final double TEXT_SATURATION = 1.0;
    private static final double TEXT_BRIGHTNESS = 0.5;
    
    private static final double BG_SATURATION = 0.2;
    private static final double BG_BRIGHTNESS = 0.98;    
    
    private static final ColorManager instance = new ColorManager();

    public static ColorManager getInstance() {
        return instance;
    }

    private ColorManager() {
        
    }
    
    private final Map<Pair<Long, IdType>, Double> idToHue = new HashMap<>();

    
    
    public Color getTextColor(final long id, final IdType type) {
        ensureHueExists(id, type);
        return Color.hsb(idToHue.get(new Pair<>(id, type)), TEXT_SATURATION, TEXT_BRIGHTNESS);
    }

    public Color getBGColor(final long id, final IdType type) {
        ensureHueExists(id, type);
        return Color.hsb(idToHue.get(new Pair<>(id, type)), BG_SATURATION, BG_BRIGHTNESS);
    }
    
    public void resetColors() {
        idToHue.clear();
    }
    
    private void ensureHueExists(final long id, final IdType type) {
        if (!idToHue.containsKey(new Pair<>(id, type))) {
            idToHue.put(new Pair<>(id, type), generateNewHue());
        }
    }
    /**
     * Generates new hue based on color count
     * @param id
     * @return numbers in a row like this: 
     * 1/2  1/4 3/4  1/8 3/8 5/8 7/8  1/16 3/16 5/16 7/16 ...
     */
    private double generateNewHue() {
        int newColorCount = idToHue.size()+1;
        long highestOneBit = Integer.highestOneBit(newColorCount);
        double dividend = (newColorCount-highestOneBit) * 2 + 1;
        double divisor = highestOneBit*2;
        return 360*dividend/divisor;
    }
}
