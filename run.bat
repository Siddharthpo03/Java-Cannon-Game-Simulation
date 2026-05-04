@echo off
REM ═══════════════════════════════════════════════════════════
REM  Cannon Game — Run Script
REM ═══════════════════════════════════════════════════════════

if not exist target\cannon-game-1.0.0.jar (
    echo JAR not found. Run build.bat first!
    exit /b 1
)

java -jar target\cannon-game-1.0.0.jar
