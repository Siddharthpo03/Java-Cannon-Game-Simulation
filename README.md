# 🎯 Cannon Battle — 2 Player Artillery Game

A **2D local multiplayer cannon battle game** built with Java AWT/Swing, featuring real-time projectile physics, collision detection, particle effects, and a scoring system.

## ⬇️ Download & Play

### Quick Start (No build required!)
1. **Download** the latest [`cannon-game-1.0.0.jar`](https://github.com/Siddharthpo03/Java-Cannon-Game-Simulation/releases/latest) from Releases
2. **Run it**: Double-click the JAR, or run:
   ```bash
   java -jar cannon-game-1.0.0.jar
   ```
3. **Requirements**: Java 17+ installed ([Download Java](https://www.oracle.com/java/technologies/downloads/))

---

## 🎮 How to Play

Two players control cannons on opposite sides of the screen. Adjust your angle, size, and power — then fire! Score points by destroying your opponent's cannonballs mid-air.

### 🕹️ Keyboard Controls

| Action | Player 1 (Left) | Player 2 (Right) |
|--------|:---:|:---:|
| **Angle** | `W` / `S` | `↑` / `↓` |
| **Power** | `A` / `D` | `←` / `→` |
| **Size** | `Q` / `E` | `,` / `.` |
| **Fire** | `Space` or `F` | `Enter` |
| **Clear** | `C` | `Backspace` |
| **Color** | `1` – `6` | `Numpad 1` – `6` |

| Global | Key |
|--------|-----|
| Pause / Resume | `P` |
| Restart | `R` |

> 💡 **Mouse controls** also work — drag sliders and click FIRE buttons.

### 🏆 Scoring

| Event | Points |
|-------|:------:|
| Destroy opponent's ball (size/power advantage) | **+10** |
| Mutual destruction (equal stats) | **+5** each |

---

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
- **DRY**: `Cannon_1` and `Cannon_2` unified into one parameterized class
- **Encapsulation**: No global mutable state — input is encapsulated in `InputHandler`
- **Resource Loading**: All assets loaded from classpath (no hardcoded paths)
- **Stable Game Loop**: Nanosecond-precision timing with negative-sleep protection

---

## 🔧 Build From Source

### Prerequisites
- Java 17+
- Maven 3.6+ (optional — build script included)

### Option 1: Build Script (No Maven needed)
```bash
build.bat        # Compiles and packages JAR
run.bat          # Launches the game
```

### Option 2: Maven
```bash
mvn clean package
java -jar target/cannon-game-1.0.0.jar
```

### Option 3: Run from IDE
Run `com.cannongame.core.Main` as the main class.

---

## 📁 Project Structure

```
├── pom.xml                          → Maven build configuration
├── src/
│   └── main/
│       ├── java/com/cannongame/     → All source code (15 classes)
│       └── resources/sounds/        → Audio assets (WAV files)
├── build.bat                        → Windows build script
├── run.bat                          → Windows run script
├── .gitignore
└── README.md
```

## 🛠️ Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 17 |
| UI Framework | AWT/Swing |
| Audio | `javax.sound.sampled` |
| Build | Maven |
| Architecture | Layered (Core → Engine → Entities → UI → Audio) |

---

## 📜 License

This project is open source. Feel free to fork, modify, and share!
