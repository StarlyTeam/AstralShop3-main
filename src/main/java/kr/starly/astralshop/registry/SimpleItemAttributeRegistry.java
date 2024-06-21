package kr.starly.astralshop.registry;

import kr.starly.astralshop.api.addon.ItemAttributeProvider;
import kr.starly.astralshop.api.registry.ItemAttributeRegistry;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SimpleItemAttributeRegistry implements ItemAttributeRegistry {

    private final Map<String, ItemAttributeProvider> providers = new HashMap<>();

    @Override
    public ItemAttributeProvider getProvider(String name) {
        return providers.get(name);
    }

    @Override
    public void register(ItemAttributeProvider provider) {
        providers.put(provider.getName(), provider);
    }

    @Override
    public void unregister(String name) {
        providers.remove(name);
    }
}