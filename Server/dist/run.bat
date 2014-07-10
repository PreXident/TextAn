@ECHO OFF

REM Start script for Textan server in Windows style (Windows cannot start process in background).

REM The program name
SET TEXTAN_SERVER_NAME=TextAn server
REM The path to a directory with TextAn
SET TEXTAN_SERVER_PATH=.
REM The relative path to the TextAn jar in a directory with TextAn (should not be changed)
SET TEXTAN_SERVER_JAR=TextAn-server.jar

CD %TEXTAN_SERVER_PATH%
ECHO Stating %TEXTAN_SERVER_NAME%...
java -jar %TEXTAN_SERVER_JAR% %*