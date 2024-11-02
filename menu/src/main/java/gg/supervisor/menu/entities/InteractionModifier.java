package gg.supervisor.menu.entities;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum InteractionModifier {
    PREVENT_ITEM_PLACE,
    PREVENT_ITEM_TAKE,
    PREVENT_ITEM_SWAP,
    PREVENT_ITEM_DROP,
    PREVENT_OTHER_ACTIONS;

    public static final Set<InteractionModifier> VALUES = Collections.unmodifiableSet(EnumSet.allOf(InteractionModifier.class));
}
