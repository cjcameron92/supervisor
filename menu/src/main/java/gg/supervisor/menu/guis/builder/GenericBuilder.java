package gg.supervisor.menu.guis.builder;

import gg.supervisor.menu.entities.InteractionModifier;
import gg.supervisor.menu.exception.MenuException;
import gg.supervisor.menu.guis.BaseGui;
import gg.supervisor.util.chat.Text;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;

@SuppressWarnings({"FieldMayBeFinal", "unchecked"})
public abstract class GenericBuilder<G extends BaseGui, B extends GenericBuilder<G, B>> {

    private final EnumSet<InteractionModifier> interactionModifiers = EnumSet.noneOf(InteractionModifier.class);
    private Component title = Text.translate("Inventory");
    private int rows = 3;

    private SchemaBuilder schemaBuilder = new SchemaBuilder();

    private Consumer<G> editConsumer;

    @Contract("_ -> this")
    public B rows(int rows) {
        this.rows = rows;
        return (B) this;
    }

    @Contract("_ -> this")
    public B title(Component title) {
        this.title = title;
        return (B) this;
    }

    @Contract("_ -> this")
    public B title(String title) {
        this.title = Text.translate(title);
        return (B) this;
    }

    @Contract("_ -> this")
    public B schemaBuilder(Consumer<SchemaBuilder> schemaBuilder) {
        this.schemaBuilder.clear();
        schemaBuilder.accept(this.schemaBuilder);

        return (B) this;
    }

    @Contract(" -> this")
    public B disableItemPlace() {
        interactionModifiers.add(InteractionModifier.PREVENT_ITEM_PLACE);
        return (B) this;
    }

    @Contract(" -> this")
    public B disableItemTake() {
        interactionModifiers.add(InteractionModifier.PREVENT_ITEM_TAKE);
        return (B) this;
    }

    @Contract(" -> this")
    public B disableItemSwap() {
        interactionModifiers.add(InteractionModifier.PREVENT_ITEM_SWAP);
        return (B) this;
    }

    @Contract(" -> this")
    public B disableItemDrop() {
        interactionModifiers.add(InteractionModifier.PREVENT_ITEM_DROP);
        return (B) this;
    }

    @Contract(" -> this")
    public B disableOtherActions() {
        interactionModifiers.add(InteractionModifier.PREVENT_OTHER_ACTIONS);
        return (B) this;
    }

    @Contract(" -> this")
    public B disableAllInteractions() {
        interactionModifiers.addAll(InteractionModifier.VALUES);
        return (B) this;
    }

    @Contract(" -> this")
    public B enableItemPlace() {
        interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_PLACE);
        return (B) this;
    }

    @Contract(" -> this")
    public B enableItemTake() {
        interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_TAKE);
        return (B) this;
    }

    @Contract(" -> this")
    public B enableItemSwap() {
        interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_SWAP);
        return (B) this;
    }

    @Contract(" -> this")
    public B enableItemDrop() {
        interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_DROP);
        return (B) this;
    }

    @Contract(" -> this")
    public B enableOtherActions() {
        interactionModifiers.remove(InteractionModifier.PREVENT_OTHER_ACTIONS);
        return (B) this;
    }

    @Contract(" -> this")
    public B enableAllInteractions() {
        interactionModifiers.clear();
        return (B) this;
    }

    @Contract("_ -> this")
    public B apply(Consumer<G> consumer) {
        this.editConsumer = consumer;
        return (B) this;
    }

    protected Component getTitle() {
        if (title == null) {
            throw new MenuException("GUI title is missing!");
        }

        return title;
    }

    protected int getRows() {
        return rows;
    }

    protected Consumer<G> getConsumer() {
        return editConsumer;
    }

    protected SchemaBuilder getSchemaBuilder() {
        return schemaBuilder;
    }

    protected Set<InteractionModifier> getModifiers() {
        return interactionModifiers;
    }


}
