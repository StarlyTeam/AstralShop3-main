package xyz.starly.astralshop.shop.controlbar;

public enum ControlBarAction {

    PREV_PAGE,
    NEXT_PAGE,
    NONE;

    public static ControlBarAction fromString(String actionStr) {
        for (ControlBarAction action : values()) {
            if (action.name().equalsIgnoreCase(actionStr)) {
                return action;
            }
        }
        return NONE;
    }
}