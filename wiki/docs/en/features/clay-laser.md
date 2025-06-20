# Clay Laser

## Laser Energy

> [!NOTE]
> The Laser Energy formula has not been changed from the original version.

First, calculate the energy values for each color ($E_1, E_2, E_3$).

For each color, define the parameters as follows:

| $i$ | $r$ | $b_i$ | $m_i$ |         $n_i$          |
|:---:|:---:|:-----:|:-----:|:----------------------:|
|  1  | 0.1 |  2.5  | 1000  | number of blue lasers  |
|  2  | 0.1 |  1.8  |  300  | number of green lasers |
|  3  | 0.1 |  1.5  |  100  |  number of red lasers  |

The energy value for each color is calculated by the following formula:
$$
E_i = m_i ^ {a_i} \cdot \frac{1 + rn_iC_i^{n_i}}{1+rC_i^{n_i}}
$$
where
$$
\begin{aligned}
C_i=bi^{(1+r)\log_{m_i}(\frac{1+r}{r})} \\\\
a_i = \frac{\ln(\frac{1 + r}{C_i^{-n_i}+r})}{\ln(\frac{1}{r}(1+r))}
\end{aligned}
$$

If $E_i < 1$, substitute $E_i = 1$.

Finally, the total laser energy, $E$, is the product of these individual energy values, minus one:

$$
E = E_1 \cdot E_2 \cdot E_3 - 1
$$
