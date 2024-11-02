package gg.supervisor.dummy.plugin.model;

import java.util.EnumMap;
import java.util.Map;



public class PickaxeData {

    private final Map<EnchantType, Long> enchants = new EnumMap<>(EnchantType.class);
    private long blocksBroken = 0;

    public Map<EnchantType, Long> getEnchants() {
        return enchants;
    }

    public long getBlocksBroken() {
        return blocksBroken;
    }

    public void setBlocksBroken(long blocksBroken) {
        this.blocksBroken = blocksBroken;
    }

    enum EnchantType {

        EXAMPLE
    }
}
