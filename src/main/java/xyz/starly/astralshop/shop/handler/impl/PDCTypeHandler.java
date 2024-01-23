package xyz.starly.astralshop.shop.handler.impl;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.starly.astralshop.shop.handler.ItemTypeHandler;

import java.util.List;
import java.util.Objects;

public class PDCTypeHandler implements ItemTypeHandler {

    @Override
    public void serialize(ItemStack itemStack, ConfigurationSection section) {
        // TODO 구현
    }

    @Override
    @SuppressWarnings("deprecation")
    public void deserialize(ItemStack itemStack, ConfigurationSection section) {
        if (section.isConfigurationSection("pdc")) {
            ConfigurationSection pdcSection = section.getConfigurationSection("pdc");
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                if (pdcSection != null) {
                    for (String key : pdcSection.getKeys(false)) {
                        ConfigurationSection dataSection = pdcSection.getConfigurationSection(key);
                        if (dataSection != null) {
                            String namespace = Objects.requireNonNull(dataSection.getString("namespace")).toLowerCase();
                            String type = dataSection.getString("type", "STRING");
                            NamespacedKey namespacedKey = new NamespacedKey(namespace, key);

                            switch (type.toUpperCase()) {
                                case "BYTE":
                                    byte byteValue = (byte) dataSection.getInt("value");
                                    container.set(namespacedKey, PersistentDataType.BYTE, byteValue);
                                    break;
                                case "SHORT":
                                    short shortValue = (short) dataSection.getInt("value");
                                    container.set(namespacedKey, PersistentDataType.SHORT, shortValue);
                                    break;
                                case "INTEGER":
                                    int intValue = dataSection.getInt("value");
                                    container.set(namespacedKey, PersistentDataType.INTEGER, intValue);
                                    break;
                                case "LONG":
                                    long longValue = dataSection.getLong("value");
                                    container.set(namespacedKey, PersistentDataType.LONG, longValue);
                                    break;
                                case "FLOAT":
                                    float floatValue = (float) dataSection.getDouble("value");
                                    container.set(namespacedKey, PersistentDataType.FLOAT, floatValue);
                                    break;
                                case "DOUBLE":
                                    double doubleValue = dataSection.getDouble("value");
                                    container.set(namespacedKey, PersistentDataType.DOUBLE, doubleValue);
                                    break;
                                case "BOOLEAN":
                                    boolean booleanValue = dataSection.getBoolean("value");
                                    container.set(namespacedKey, PersistentDataType.BOOLEAN, booleanValue);
                                    break;
                                case "BYTE_ARRAY":
                                    byte[] byteArrayValue = Objects.requireNonNull(dataSection.getString("value")).getBytes();
                                    container.set(namespacedKey, PersistentDataType.BYTE_ARRAY, byteArrayValue);
                                    break;
                                case "INTEGER_ARRAY":
                                    List<Integer> intList = dataSection.getIntegerList("value");
                                    int[] intArray = intList.stream().mapToInt(i -> i).toArray();
                                    container.set(namespacedKey, PersistentDataType.INTEGER_ARRAY, intArray);
                                    break;

                                case "LONG_ARRAY":
                                    List<Long> longList = dataSection.getLongList("value");
                                    long[] longArray = new long[longList.size()];
                                    for (int i = 0; i < longList.size(); i++) {
                                        longArray[i] = longList.get(i);
                                    }
                                    container.set(namespacedKey, PersistentDataType.LONG_ARRAY, longArray);
                                    break;
                                case "STRING":
                                default:
                                    String stringValue = dataSection.getString("value");
                                    if (stringValue != null) {
                                        container.set(namespacedKey, PersistentDataType.STRING, stringValue);
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
            itemStack.setItemMeta(itemMeta);
        }
    }
}