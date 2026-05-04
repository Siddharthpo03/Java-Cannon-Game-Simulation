# 🎯 Cannon Battle — 2 Player Artillery Game

A 2D local multiplayer cannon battle game built with Java AWT/Swing, featuring real-time projectile physics, collision detection, particle effects, and a scoring system.

## 🎮 How to Play

- **Two players** control cannons on opposite sides of the screen
- Adjust **Angle**, **Size**, and **Power** using sliders
- Choose your cannonball **color** from the palette
- Press **FIRE** to launch!
- Score points by destroying your opponent's projectiles

### Controls

| Key | Action |
|-----|--------|
| `P` | Pause / Resume |
| `R` | Restart Game |
| Mouse | Drag sliders, click buttons |

### Scoring

| Event | Points |
|-------|--------|
| Destroy opponent's ball (size/power advantage) | +10 |
| Mutual destruction (equal stats) | +5 each |

## 🏗️ Architecture

```
src/main/java/com/cannongame/
├── core/        → Game loop, state management, config constants
├── engine/      → Physics engine, collision detection
├── entities/    → Ball, Cannon, Cloud, FireEffect
├── ui/          → GamePanel, HUD, sliders, input handling
└── audio/       → Resource-based sound manager
```

### Design Principles

- **Single Responsibility**: Each class has one clear job
- **DRY**: Cannon_1 and Cannon_2 unified into one parameterized class
- **Encapsulation**: No global mutable state — input is encapsulated in InputHandler
- **Resource Loading**: All assets loaded from classpath (no hardcoded paths)
- **Stable Game Loop**: Nanosecond-precision timing with negative-sleep protection

## 🔧 Build & Run

### Prerequisites
- Java 17+
- Maven 3.6+

### Build
```bash
mvn clean package
```

### Run
```bash
java -jar target/cannon-game-1.0.0.jar
```

### Run from IDE
Run `com.cannongame.core.Main` as the main class.

## 📁 Project Structure

```
├── pom.xml                          → Maven build configuration
├── src/
│   └── main/
│       ├── java/com/cannongame/     → All source code (15 classes)
│       └── resources/sounds/        → Audio assets (WAV files)
├── .gitignore
└── README.md
```

## 🛠️ Tech Stack

- **Language**: Java 17
- **UI Framework**: AWT/Swing (no external dependencies)
- **Audio**: javax.sound.sampled
- **Build**: Maven
- **Architecture**: Layered (Core → Engine → Entities → UI → Audio)
