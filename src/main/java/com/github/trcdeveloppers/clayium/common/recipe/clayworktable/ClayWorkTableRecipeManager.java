package com.github.trcdeveloppers.clayium.common.recipe.clayworktable;

import com.github.trcdeveloppers.clayium.Clayium;
import com.github.trcdeveloppers.clayium.common.blocks.machines.clayworktable.ClayWorkTableMethod;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.github.trcdeveloppers.clayium.Clayium.MOD_ID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ClayWorkTableRecipeManager {

    public static ClayWorkTableRecipeManager INSTANCE = new ClayWorkTableRecipeManager();

    private final List<ClayWorkTableRecipe> recipes = new ArrayList<>();

    private ClayWorkTableRecipeManager() {
        CraftingHelper.findFiles(
            Loader.instance().getIndexedModList().get("clayium"),
            "assets/clayium/machine_recipes/clay_work_table/",
            null,
            (root, file) -> {
                String relative = root.relativize(file).toString();
                if (!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_"))
                    return true;

                String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
                ResourceLocation key = new ResourceLocation(MOD_ID, "clay_work_table." + name);
                try (BufferedReader reader = Files.newBufferedReader(file)) {
                    JsonObject json = Objects.requireNonNull(JsonUtils.fromJson(CraftingHelper.GSON, reader, JsonObject.class));
                    Optional<ClayWorkTableRecipe> recipe = getRecipe(json);
                    if (!recipe.isPresent()) {
                        Clayium.LOGGER.error("Invalid recipe found at {}", key);
                        return true;
                    }
                    this.recipes.add(recipe.get());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return true;
            },
            true,
            true
        );
    }

    private static Optional<ClayWorkTableRecipe> getRecipe(JsonObject json) {
        if (json.isJsonNull()) {
            throw new JsonSyntaxException("json cannot be null");
        }

        if (!(json.has("input") && json.has("output") && json.has("method") && json.has("clicks"))) {
            return Optional.empty();
        }

        JsonObject inputJson = json.getAsJsonObject("input");
        if ((!inputJson.has("item") && inputJson.has("amount"))) { return Optional.empty(); }
        Item inputItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(inputJson.get("item").getAsString()));
        if (inputItem == null) {
            Clayium.LOGGER.error("ClayWorkTable Recipe: input item not found");
            return Optional.empty();
        }


        JsonObject outputJson = json.getAsJsonObject("output");
        if ((!outputJson.has("item") && outputJson.has("amount"))) { return Optional.empty(); }
        Item outputItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(outputJson.get("item").getAsString()));
        if (outputItem == null) {
            Clayium.LOGGER.error("ClayWorkTable Recipe: output item not found");
            return Optional.empty();
        }

        JsonObject secondaryOutputJson = json.getAsJsonObject("output_2");
        Item secondaryOutputItem = null;
        if (secondaryOutputJson != null) {
            secondaryOutputItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(secondaryOutputJson.get("item").getAsString()));
            if (secondaryOutputItem == null) {
                Clayium.LOGGER.error("ClayWorkTable Recipe: secondaryOutput item not found");
                return Optional.empty();
            }
        }

        ClayWorkTableMethod method = ClayWorkTableMethod.fromId(json.get("method").getAsInt());
        if (method == null) {
            Clayium.LOGGER.error("ClayWorkTable Recipe: method ID must be in the range 1-6 but got {}", json.get("method").getAsInt());
            return Optional.empty();
        }

        int clicks = json.get("clicks").getAsInt();

        return Optional.of(new ClayWorkTableRecipe(
            new ItemStack(inputItem, inputJson.get("amount").getAsInt()),
            new ItemStack(outputItem, outputJson.get("amount").getAsInt()),
            secondaryOutputItem == null ? ItemStack.EMPTY : new ItemStack(secondaryOutputItem, secondaryOutputJson.get("amount").getAsInt()),
            method,
            clicks
        ));
    }

    /**
     * @param input can be {@link ItemStack#EMPTY}, in which case it always returns {@link Optional#empty()}.
     */
    public Optional<ClayWorkTableRecipe> getRecipeFor(ItemStack input, ClayWorkTableMethod method) {
        if (input.isEmpty()) {
            return Optional.empty();
        }
        for (ClayWorkTableRecipe recipe : this.recipes) {
            if (recipe.matches(input, method)) {
                return Optional.of(recipe);
            }
        }

        return Optional.empty();
    }
}
