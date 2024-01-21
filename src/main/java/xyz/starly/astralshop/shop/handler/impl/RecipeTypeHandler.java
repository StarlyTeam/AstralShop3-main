package xyz.starly.astralshop.shop.handler.impl;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.KnowledgeBookMeta;
import xyz.starly.astralshop.shop.handler.ItemTypeHandler;

import java.util.ArrayList;
import java.util.List;

public class RecipeTypeHandler implements ItemTypeHandler {

    @Override
    public void serialize(ItemStack itemStack, ConfigurationSection section) {
        // TODO 구현
    }

    @Override
    public void deserialize(ItemStack itemStack, ConfigurationSection section) {
        if (itemStack.getItemMeta() instanceof KnowledgeBookMeta) {
            KnowledgeBookMeta knowledgeBookMeta = (KnowledgeBookMeta) itemStack.getItemMeta();

            if (section.isList("recipes")) {
                List<String> recipeNames = section.getStringList("recipes");
                List<NamespacedKey> recipes = new ArrayList<>();

                for (String recipeName : recipeNames) {
                    NamespacedKey key = NamespacedKey.minecraft(recipeName.toLowerCase());
                    recipes.add(key);
                }

                knowledgeBookMeta.setRecipes(recipes);
                itemStack.setItemMeta(knowledgeBookMeta);
            }
        }
    }
}