rmdir /S /Q bin
mkdir bin
javac -cp jna.jar -d bin src/libs/*.java
javac -cp bin;jna.jar -d bin src/democode/*.java
javac -cp bin;jna.jar -d bin src/net/javajeff/jtwain/*.java
javac -cp bin;jna.jar -d bin src/*.java
