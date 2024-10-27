package gg.supervisor.util.chat;

public enum Symbols {

    HEART("❤"),
    HEARTS("💕"),
    DOUBLE_HEARTS("❣"),
    HEART_BEAT("💓"),
    HEART_PULSE("💗"),
    BULLET("•"),
    LEFT_ARROW("←"),
    SMALL_RIGHT("»"),
    CIRCLE("⬤"),
    SMALL_LEFT("«"),
    DOUBLE_ARROW("⇒"),
    RIGHT_ARROW("→"),
    SQUARE("█"),
    PICKAXE("⛏"),
    CLUB("♣"),
    SHIELD("🛡"),
    CRUSADER_SHIELD("⛨"),
    SWORDS("⚔"),
    COIN("⛀"),
    COINS("⛁"),
    BOLDER_COINS("⛃"),
    DIAMOND("♦"),
    SPADE("♠"),
    STAR("⭐"),
    HEART_SUIT("♥"),
    MUSIC_NOTE("♫"),
    LIGHTNING("⚡"),
    FLAME("🔥"),
    SKULL("☠"),
    SUN("☀"),
    MOON("☽"),
    CLOUD("☁"),
    UMBRELLA("☂"),
    SNOWMAN("☃"),
    COMET("☄"),
    LOCK("🔒"),
    BOLD_STAR("★"),
    STARS("✨"),
    BELL("🔔"),
    COFFEE("☕"),
    PENCIL("✎"),
    TICK("✓"),
    CROSS("✖"),
    FLEUR_DE_LIS("⚜"),
    KEY("🔑"),
    BEGINNER("🔰"),
    UNLOCK("🔓"),
    HOURGLASS("⌛"),
    WATCH("⌚"),
    TELESCOPE("🔭"),
    TENT("⛺"),
    COMPASS("🧭"),
    GLOBE("🌐"),
    GIFT("🎁"),
    SWORD("\uD83D\uDDE1"),
    BOW("\uD83C\uDFF9"),
    AXE("\uD83E\uDE93"),
    TRIDENT("\uD83D\uDD31"),
    POTION("\uD83E\uDDEA"),
    GEM("\uD83D\uDC8E"),
    BUCKET("\uD83E\uDEA3"),
    CYCLONE("\uD83C\uDF00"),
    RIBBON("\uD83C\uDF80"),
    JAPANESE_CASTLE("\uD83C\uDFEF"),
    ;

    private final String symbol;

    Symbols(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
