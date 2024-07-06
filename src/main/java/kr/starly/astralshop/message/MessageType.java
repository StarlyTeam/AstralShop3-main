package kr.starly.astralshop.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageType {

    NONE("message"),
    NORMAL("message.normal"),
    ERROR("message.error"),
    SIMPLE_HANDLER("message.simple-handler");

    private final String key;
}