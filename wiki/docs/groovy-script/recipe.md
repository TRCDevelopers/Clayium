# Groovy Script連携

[GroovyScript](https://github.com/CleanroomMC/GroovyScript)を用いて、レシピの追加や削除を行えます。

レシピは`RecipeRegistry`に保存されています。
`RecipeRegistry`は、以下のように取得します。

```groovy
def recipeMap = mods.clayium.recipe_registry_name
```

`RecipeRegistry`の一覧は、[ここ](https://github.com/TRCDevelopers/Clayium/blob/develop/src/main/kotlin/com/github/trc/clayium/common/recipe/registry/CRecipes.kt)から確認できます。`""`で囲われた部分が`recipe_registry_name`です。

`RecipeRegistry`があれば、`recipeBuilder`メソッドを呼ぶことでBuilderを取得できます。

```groovy
mods.clayium.clay_reactor.recipeBuilder()
```

## ビルダーの使い方

### インプット・アウトプットの追加

`input`・`output`メソッドを複数回を呼ぶことで、複数のインプット・アウトプットを追加できます。

```groovy
input(IIngredient ingredient)
output(ItemStack output)
```

### CEtの指定

> [!IMPORTANT]
> `CEtMicro`で使用できる最小値は`10`です。

```groovy
CEt(long)
CEtMilli(int) // 1 = 1mCE
CEtMicro(int) // 1 = 1uCE
```

### 他の情報の指定

```groovy
duration(long) // レシピの加工時間をtick単位で指定します。
tier(int) // レシピを実行するのに必要なtierを指定します。
```

### レシピの登録

最後に、`buildAndRegister()`メソッドを呼んでレシピを登録します。

## 例

```groovy
mods.clayium.clay_reactor.recipeBuilder()
    .input(item('minecraft:dirt'))
    .input(ore('ingotGold') * 2)
    .output(item('minecraft:diamond'))
    .CEt(100).duration(1_000_000).tier(7)
    .buildAndRegister()
```
