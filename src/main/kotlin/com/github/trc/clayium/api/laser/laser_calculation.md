# Laser Energy Calculation

> [!NOTE]
> The content of this page is mostly an English translation of the official [Japanese wiki](https://clayium.wiki.fc2.com/wiki/%E3%83%AC%E3%83%BC%E3%82%B6%E3%83%B).

First, calculate the energy values ($E_1, E_2, E_3$) for each color.

And the parameters are defined as follows:

| $i$ | $r$ | $b_i$ | $m_i$ |        $n_i$        |
|:---:|:---:|:-----:|:-----:|:-------------------:|
|  1  | 0.1 |  2.5  | 1000  | num of blue lasers  |
|  2  | 0.1 |  1.8  |  300  | num of green lasers |
|  3  | 0.1 |  1.5  |  100  |  num of red lasers  |

$$
E_i = m_i ^ {a_i} \cdot \frac{1 + rn_iC_i^{n_i}}{1+rC_i^{n_i}}
$$

where

$$
C_i=bi^{(1+r)\log_{m_i}(\frac{1+r}{r})}, \\ {} \\
a_i = \frac{\ln(\frac{1 + r}{C_i^{-n_i}+r})}{\ln(\frac{1}{r}(1+r))}
$$

Then, if $E_i < 1$, we reassign $E_i = 1$.

Finally, the laser energy is their product - 1, i.e.

$$
E = E_1 \cdot E_2 \cdot E_3 - 1
$$
