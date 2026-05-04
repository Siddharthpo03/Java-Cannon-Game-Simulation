@echo off
REM ═══════════════════════════════════════════════════════════
REM  Cannon Game — Build Script
REM  Compiles all sources and packages into an executable JAR
REM ═══════════════════════════════════════════════════════════

echo [BUILD] Cleaning target directory...
if exist target rmdir /s /q target
mkdir target\classes

echo [BUILD] Compiling Java sources...
javac -d target\classes -sourcepath src\main\java src\main\java\com\cannongame\core\*.java src\main\java\com\cannongame\engine\*.java src\main\java\com\cannongame\entities\*.java src\main\java\com\cannongame\ui\*.java src\main\java\com\cannongame\audio\*.java

if %errorlevel% neq 0 (
    echo [BUILD] FAILED — Compilation errors!
    exit /b 1
)

echo [BUILD] Copying resources...
xcopy /s /i src\main\resources\sounds target\classes\sounds >nul

echo [BUILD] Creating manifest...
echo Main-Class: com.cannongame.core.Main> target\MANIFEST.MF

echo [BUILD] Packaging JAR...
jar cfm target\cannon-game-1.0.0.jar target\MANIFEST.MF -C target\classes .

if %errorlevel% neq 0 (
    echo [BUILD] FAILED — JAR packaging error!
    exit /b 1
)

echo [BUILD] SUCCESS — target\cannon-game-1.0.0.jar
echo [BUILD] Run with: java -jar target\cannon-game-1.0.0.jar
