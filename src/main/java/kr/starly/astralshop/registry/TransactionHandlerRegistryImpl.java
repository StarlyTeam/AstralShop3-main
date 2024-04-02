package kr.starly.astralshop.registry;

import kr.starly.astralshop.api.addon.TransactionHandler;
import kr.starly.astralshop.api.registry.TransactionHandlerRegistry;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class TransactionHandlerRegistryImpl implements TransactionHandlerRegistry {

    private final Map<String, TransactionHandler> handlers = new HashMap<>();

    @Override
    public TransactionHandler getHandler(String name) {
        return handlers.get(name);
    }

    @Override
    public void register(TransactionHandler handler) {
        handlers.put(handler.getName(), handler);
    }

    @Override
    public void unregister(String name) {
        handlers.remove(name);
    }
}