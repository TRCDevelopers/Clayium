# レシピ設計

> [!NOTE]
> WIP. メモの段階.

- レシピとは、機械がCEを使って行う動作の情報
- 入力と出力が存在

例えば、 `丸石 + 1uCE -> 砂利`, `水 + 1uCE -> 蒸気 + 塩` など。
`ItemStack -> Fluid`でもいいし、アドオン側で`Mekanism Gas`を扱えたりするといい。

### クラス

#### `RecipeRegistry`

- `maxInputs, maxOutputs`: いまはアイテムだけだからこれでいいが、任意の入力を受け付けるには、入力の「種」を司るクラスが必要(JEIがやっているように)。
  - つまり`IIngredientType`

#### `Recipe`