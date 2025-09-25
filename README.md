# Dungeon of Doom

A 2D command-line based dungeon crawler game written in Java. Players navigate a map to collect gold and find the exit while competing against an intelligent Bot opponent.

## Screenshots

**Game Start & Map Selection**
*The game begins by prompting the user to select a map from the `maps` directory.*
<br><br>
<img src="./screenshots/DoD_gameplay.jpg" alt="Map selection screen" width="700" />

**Example Maps**
*The game supports multiple map layouts, which are loaded from the `maps` directory.*

<table>
  <tr>
    <td><img src="./screenshots/DoD_map_pathfinder2.png" alt="Pathfinder2 map" width="200"/></td>
    <td><img src="./screenshots/DoD_map_win98.jpg" alt="Windows 98 map" width="200"/></td>
  </tr>
</table>

## How to Run

1.  **Prerequisites**:
    *   You must have a Java Development Kit (JDK) installed.

2.  **Compile**:
    Open a terminal in the project's root directory and compile the source files. This command will place the compiled `.class` files into an `out` directory.
    ```bash
    javac -d out src/*.java
    ```

3.  **Run**:
    Execute the game from the root directory using the following command. The game will automatically find and list the available maps from the `maps/` directory.
    ```bash
    java -cp out Game
    ```

### Gameplay Elements

The game world is composed of several elements, each represented by a character in the `LOOK` view:

**Map Tiles:**
*   `#` - Wall: Impassable terrain.
*   `.` - Floor: Empty space where players can move.
*   `G` - Gold: Tile containing gold that players can pick up.
*   `E` - Exit: Tile where players can exit the dungeon if they have collected enough gold.

**Players:**
*   `P` - Human Player: Your character.
*   `B` - Bot Player: The AI opponent.

### Game Controls

All commands are case-insensitive. Each command uses one turn.

*   **`HELLO`**
    *   Displays the total amount of gold required to win the game.

*   **`GOLD`**
    *   Shows the amount of gold you currently own.

*   **`PICKUP`**
    *   Picks up any gold on your current tile.

*   **`MOVE <N|S|E|W>`**
    *   Moves your player one tile in the specified direction (North, South, East, or West). You cannot move into walls.
    *   *Example:* `MOVE E`

*   **`LOOK`**
    *   Displays a 5x5 grid of the map centered on your player, showing walls, gold, other players, and the exit.

*   **`QUIT`**
    *   Exits the game. If you are on an exit tile (`E`) and have collected enough gold, you win. Otherwise, you lose.

## Project Structure

*   `src/`: Contains all Java source code (`.java` files). The entry point of the application is the `main` method in the `Game.java` file.
*   `maps/`: Contains all valid map files. The game reads any `.txt` files from this directory, excluding any `README.txt` files.
*   `out/`: The output directory for compiled Java `.class` files (this directory is created by the compile command).

## Software Design & Architecture

The application is built using object-oriented principles to create a robust, modular, and extensible codebase.

`Player` is an abstract parent class that groups shared functionality inherited by the `Human` and `Bot` classes. This design means subclasses are only responsible for their own specific implementations (e.g., `Human` processing user input). The `Player` class is abstract because it doesn't make sense to instantiate a generic `Player` without a specific type, and this approach simplifies adding new player types in the future.

This same design philosophy applies to the abstract `Tile` class. The architecture also supports different `Bot` strategies by making `Bot` an abstract class extended by `SmartBot` and `OmniscientBot`. This can be further expanded with new strategies (e.g., a `LooterBot`).

The abstract methods within these parent classes form a contract that any subclass must implement. Subtype polymorphism is used for methods like `Tile.printTile()` and `Player.playTurn()`, with these methods being declared as abstract to ensure they are always implemented by subclasses. This process leads to consistent behaviour and a reduction in code duplication.