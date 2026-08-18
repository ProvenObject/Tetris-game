# Tetris (Java Swing)

A complete Tetris game built from scratch in pure Java using Swing.

I rebuilt this project step-by-step with a strong focus on actually understanding every part of the code instead of just following a tutorial.

---

## Features

- All 7 classic tetrominoes with rotation
- Soft drop & Hard drop
- Line clearing with classic scoring
- Increasing speed / levels
- Next piece preview
- Pause
- Game Over + Restart
- Clean side panel showing Score, Level and Lines

---

## Controls

| Key     | Action          |
|---------|-----------------|
| ← →     | Move left/right |
| ↑       | Rotate          |
| ↓       | Soft drop       |
| Space   | Hard drop       |
| P       | Pause / Unpause |
| R       | Restart (after Game Over) |

---

What I Learned
This project was intentionally built in small steps so I could properly understand the fundamentals of game development:

Separating game data (board array) from rendering (paintComponent)
Creating a simple game loop with javax.swing.Timer
Collision detection before applying movement
Managing game state (playing, paused, game over)
Handling keyboard input cleanly
Implementing line clearing and scoring systems

These concepts transfer directly to real game engines (Godot, Unity, etc.).

Technologies

Java
Java Swing (JFrame, JPanel, Timer, KeyListener)

## How to Run

### Requirements
- Java 17 or higher (or any reasonably modern JDK)

### Steps
1. Clone the repository
2. Open the project in IntelliJ / Eclipse / VS Code
3. Run the `Tetris` class (it contains the `main` method)

Or from the terminal:

```bash
javac src/*.java
java -cp src Tetris
