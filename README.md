# snake-game
Simple snake game with added features: time limit and obstacles

---

# 2D Snake Game – Java (SE)

> **Jednoduchá verze klasického Snake pro výukové účely**
>  Grafika: Swing  |  Logika: čistá Java  |  Licence: MIT

---

## Obsah

1. [Jak projekt spustit](#jak-projekt-spustit)
2. [Struktura balíčku](#struktura-balíčku)
3. [Popis tříd a metod](#popis-tříd-a-metod)

    * [`GameMain`](#gamemain)
    * [`GameGraphic`](#gamegraphic)
    * [`GameLogic`](#gamelogic)
    * [`Snake`](#snake)
    * [`Ball`](#ball)
4. [Ovládání a herní mechaniky](#ovládání-a-herní-mechaniky)
5. [Úprava grafiky (sprite hada)](#úprava-grafiky)
6. [TODO a nápady na rozšíření](#todo)

---

## Jak projekt spustit&#x20;

```bash
# 1 ) Zkompiluj všechny .java soubory ve stejném adresáři 
javac *.java

# 2 ) Spusť hlavní třídu
java GameMain
```

> Minimální JDK 8 +. Projekt nemá žádné externí závislosti.

Pokud generuješ **JAR**:

```bash
jar --create --file snake.jar *.class resources/
java -jar snake.jar
```

---

## Struktura balíčku&#x20;

```
📁 src/
   ├── GameMain.java      # start aplikace
   ├── GameGraphic.java   # vykreslování a ovládání
   ├── GameLogic.java     # herní pravidla & časy
   ├── Snake.java         # reprezentace hada
   ├── Ball.java          # jídlo (červený míček)
   └── snake.png          # (volitelný) sprite hada
```

---

## Popis tříd a metod&#x20;

### `GameMain`&#x20;

| Metoda                | Co dělá                                                                                                |
| --------------------- | ------------------------------------------------------------------------------------------------------ |
| `main(String[] args)` | **Spouštěcí bod**. Vytvoří `JFrame`, vloží panel `GameGraphic`, nastaví okno a volá `graphic.start()`. |

> Logika je zde úmyslně minimální – vše ostatní drží specializované třídy.

---

### `GameGraphic`&#x20;

Swingový `JPanel`, jenž zajišťuje **vykreslování herního pole**, obsluhu kláves a hlavní herní smyčku (vlákno).

| Klíčové pole        | Význam                                      |
| ------------------- | ------------------------------------------- |
| `CELL_SIZE`         | rozměr jedné mřížkové buňky v px            |
| `COLS/ROWS`         | počet sloupců/řádků mřížky                  |
| `GameLogic logic`   | instance pravidel (modelu)                  |
| `Thread loopThread` | hlavní smyčka ručně řízená `Thread.sleep()` |

| Metoda                       | Proč tu je                                                                                     |
| ---------------------------- | ---------------------------------------------------------------------------------------------- |
| `start()`                    | Spustí vlákno, nastaví focus panelu. Zabraňuje duplicitnímu spuštění kontrolou `isAlive()`.    |
| `run()`                      | Nekonečná smyčka: `logic.update()` → `repaint()` → čeká `delay` ms (rychlost roste s levelem). |
| `paintComponent(Graphics g)` | Vykreslí mřížku, hada, míček, překážky, HUD a případně obrazovku Game Over.                    |
| `keyPressed(KeyEvent e)`     | Mapa WSAD/šipek pro změnu směru, `R` restartuje hru, pokud je Game Over.                       |
| `restartGame()`              | Bezpečně ukončí staré vlákno (`join()`), vytvoří novou `GameLogic`, znovu zavolá `start()`.    |

> **Výhoda rozdělení** – panel nezná podrobnosti skórování, jen si ptá `logic`.

---

### `GameLogic`&#x20;

Centrální *model* – drží všechny proměnné hry a zapouzdřuje pravidla.

| Pole                    | Funkce                                  |
| ----------------------- | --------------------------------------- |
| `cols/rows`             | rozměry mřížky (v buňkách)              |
| `Snake snake`           | instance hada (deque bodů)              |
| `Ball ball`             | aktuální jídlo                          |
| `List<Point> obstacles` | překážky (přibývají každým levelem)     |
| `int score`             | počet sebraných míčků                   |
| `int level`             | úroveň 1‑5 (5 =endless)                 |
| `int timeRemaining`     | odpočet v „tickech“ (neklesá v endless) |
| `boolean endless`       | true, pokud level == 5                  |
| `boolean gameOver`      | true po kolizi nebo vypršení času       |

| Metoda                  | Vysvětlení                                                                                                                                              |
| ----------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`update()`**          | Hlavní krok modelu – zavolá `snake.move()`, pak `checkCollisions()`, poté odečte čas (pokud není endless).                                              |
| **`checkCollisions()`** | 1) Výstup mimo pole → `gameOver`. 2) Kousnutí těla/obstacles → `gameOver`. 3) Míček sněden → `score++`, `snake.grow()`, relokace míčku, test level‑upu. |
| **`levelUp()`**         | `level++`; pro level < 5 nastaví nový čas z `LEVEL_TIME`, pro level 5 zapne `endless=true`; vždy znovu vygeneruje překážky.                             |
| `ensureBallSafe()`      | Relokuje míček, dokud není na hadovi nebo překážce.                                                                                                     |
| `generateObstacles()`   | Náhodně rozloží N bloků, kde N roste s levelem (`8 + (level−1)×4`).                                                                                     |

> **Level‑up logika**: od verze 2 se úroveň posune každé 2 body (`score % 2 == 0`).

---

### `Snake`&#x20;

Reprezentuje hadí tělo jako `Deque<Point>` (+ aktuální `Direction`).

| Metoda                      | Co provede                                                                       |
| --------------------------- | -------------------------------------------------------------------------------- |
| `setDirection(Direction d)` | Změní směr, **pokud nejde o 180° otočku** (ochrana před okamžitým kousnutím).    |
| `move()`                    | Přidá nový `head` podle směru; pokud `grow` není true, odstraní poslední článek. |
| `grow()`                    | Nastaví flag; při příštím `move()` tělo neukousne ocásek.                        |
| `contains(Point p)`         | Pravda, když bod leží na jakémkoliv segmentu.                                    |

---

### `Ball`&#x20;

Objekt jídla – držení aktuální polohy a generování nové.

| Metoda                           | Popis                                                                                                                 |
| -------------------------------- | --------------------------------------------------------------------------------------------------------------------- |
| `relocate(int width,int height)` | Zvolí náhodný bod v políčku `[0,width) × [0,height)`; vlastní kontrolu kolize zajišťuje `GameLogic.ensureBallSafe()`. |

---

## Ovládání a herní mechaniky&#x20;

| Klávesa     | Akce                 |
| ----------- | -------------------- |
| **↑ ↓ ← →** | změna směru hada     |
| **R**       | restart po Game Over |

* Level 1 začíná s časem 60 ticků, **každé 2 bodíky** se level zvýší; v levelu 5 se čas zastaví (endless).
* Po každém level‑upu roste rychlost a přibývají překážky.

---

##
