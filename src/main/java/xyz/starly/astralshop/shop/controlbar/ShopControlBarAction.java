package xyz.starly.astralshop.shop.controlbar;

public enum ShopControlBarAction {

    PREV_PAGE,
    NEXT_PAGE,
    BACK,
    CLOSE,
    NONE;

    public static ShopControlBarAction fromString(String actionStr) {
        for (ShopControlBarAction action : values()) {
            if (action.name().equalsIgnoreCase(actionStr)) {
                return action;
            }
        }
        return NONE;
    }
}