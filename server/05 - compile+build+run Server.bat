javac -d cls src\server\com\netcracker\romenskiy\*.java src\client\com\netcracker\romenskiy\*.java
jar cmf res\smanifest.mf bin\Server.jar -C cls\ server
jar cmf res\cmanifest.mf bin\Client.jar -C cls\ client
java -jar bin\Server.jar
pause