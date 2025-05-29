# GroovyScript Integration

With [GroovyScript](https://github.com/CleanroomMC/GroovyScript), you can add or remove recipes.

Recipes are stored in a `RecipeRegistry` and can be obtained as follows.

```groovy
def recipeMap = mods.clayium.recipe_registry_name
```

You can get a list of `RecipeRegistry` from [here](https://github.com/TRCDevelopers/Clayium/blob/develop/src/main/kotlin/com/github/trc/clayium/common/recipe/registry/CRecipes.kt).
The string surrounded by `""` is `recipe_registry_name`.

With the `RecipeRegistry` you can create a Builder:

```groovy
mods.clayium.clay_reactor.recipeBuilder()
```

## Builder functions

### Input / Output

You can add multiple inputs and outputs by calling the `input` or `output` methods multiple times.

```groovy
input(IIngredient ingredient)
output(ItemStack output)
```

### CEt

> [!IMPORTANT]
> The minimum value that can be used in `CEtMicro` is `10`.

```groovy
CEt(long)
CEtMilli(int) // 1 = 1mCE
CEtMicro(int) // 1 = 1uCE
```

### Other

```groovy
duration(long) // duration in ticks.
tier(int) // required tier of the recipe.
```

### Registering the recipe

Finally, call the `buildAndRegister()` method to register the recipe.

## Example

```groovy
mods.clayium.clay_reactor.recipeBuilder()
    .input(item('minecraft:dirt'))
    .input(ore('ingotGold') * 2)
    .output(item('minecraft:diamond'))
    .CEt(100).duration(1_000_000).tier(7)
    .buildAndRegister()
```
