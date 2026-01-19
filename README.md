# Connect Four (Java / Swing)

This repository contains a simple **Connect Four** game implemented in **Java** using **Swing** for the graphical user interface.  
The project was developed during my bachelor studies as a small exercise in object-oriented design, GUI programming, and basic architectural patterns.

## Features

- Classic **Connect Four** gameplay  
- Graphical user interface built with **Java Swing**
- Clear separation between game logic and UI
- Observer-based design for board state updates
- Thread-safe UI initialization using the Swing Event Dispatch Thread

## Project Structure

- `Main` – Application entry point  
- `C4Frame` – Main application window  
- `C4Panel` – Game board rendering and interaction  
- `ObservedBoard` – Game state and logic  
- `C4Observable` / `C4Observer` – Observer pattern interfaces  

## How to Run

1. Make sure you have **Java 8 or newer** installed.
2. Compile the project:
   ```bash
   javac connectFour/Main.java
3. Run the application:
    ```bash
    java connectFour.Main

## Purpose

- This project was created as part of my bachelor studies to practice:
- Object-oriented programming in Java
- GUI development with Swing
- Basic software design patterns (Observer)
- Clean project structure and documentation
