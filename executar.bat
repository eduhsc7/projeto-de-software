@echo off
chcp 65001 >nul
if not exist bin mkdir bin
javac -encoding UTF-8 -d bin src\entity\*.java src\service\*.java src\collection\*.java src\controller\*.java src\boundary\*.java src\Main.java
if %errorlevel% equ 0 (
    java -cp bin Main %*
)
