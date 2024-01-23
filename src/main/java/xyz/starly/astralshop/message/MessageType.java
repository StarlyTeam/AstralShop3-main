package xyz.starly.astralshop.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageType {

    NONE("lang"),
    NORMAL("message.normal"),
    ERROR("message.error");

    private final String key;
}