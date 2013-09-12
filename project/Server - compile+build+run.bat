javac -Xlint:unchecked -d cls -classpath bin\lib\log4j-1.2.16.jar src\server\com\netcracker\romenskiy\gui\*.java src\server\com\netcracker\romenskiy\gui\model\*.java src\server\com\netcracker\romenskiy\messages\*.java src\server\com\netcracker\romenskiy\*.java
pause

jar cmf res\server_manifest.mf Server.jar -C cls\ server
pause

java -jar Server.jar
pause