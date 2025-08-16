# Machines

## Processing Machines

### Auto Crafter

Automates crafting table recipes.

It works by placing a pattern of the desired recipe in the left 9 slots,
and the actual crafting ingredients in the central 9 slots.
The ingredients in the central 9 slots are rearranged to match the pattern as closely as possible.
From Tier 6 onwards, it requires CE to operate.

In addition to items, filters can also be placed in the pattern slots,
allowing for ambiguous material specifications using ore dictionaries and similar methods.

## Builder Machines

### Block Breaker
**Tier 6** \
Operates without energy, breaking a single block in front of it.
Its efficiency is the same as the ranged miner.

Fluid-related operations are not implemented.

### Ranged Miner
**Tier 8** \
A Quarry. Range can be specified using clay markers.
**It can be accelerated by irradiating a Clay Laser on it.** \
The acceleration rate is given by the formula: \
$r = (1 + 4 \log_{10}\left(\frac{\text{Laser Energy}}{1000} + 1\right))$

It consumes $0.01 \times r$ CE per tick and adds $100 \times r$ to the progress. \
The progress required to mine a block is determined by the block's hardness $h$: \
$P := 400 \times (0.1 + h)$ \
Blocks with a hardness of -1, such as bedrock, are not mined.

It can mine up to 10 blocks per tick at maximum. This value can be changed in the config.

::: info
The following advanced miners also do not change energy consumption or efficiency based on the presence of filters (including Fortune and Silk Touch).
:::

### Advanced Ranged Miner

**Tier 9** \
An upgraded version of the Ranged Miner. It can mine blocks with Fortune or Silk Touch.
It has additional filter slots for Fortune and Silk Touch.
If the filters match, the blocks are mined with Fortune or Silk Touch applied.
Fortune level is fixed at 3.

### Ranged Replacer

**Tier 10** \
An upgraded version of the Advanced Ranged Miner. It can also replace mined blocks.

Unlike the original, it only replaces blocks that have been mined.