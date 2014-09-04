@ECHO OFF

SET CLASSPATH=TextAn-server.jar
SET SETUPER_CLASS_NAME=cz.cuni.mff.ufal.textan.server.setup.Setuper

java -cp %CLASSPATH% %SETUPER_CLASS_NAME% %*