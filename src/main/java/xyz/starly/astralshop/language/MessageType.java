package xyz.starly.astralshop.language;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageType {

    NONE("message"),
    NORMAL("message.normal"),
    ERROR("message.error");

    private final String key;
}