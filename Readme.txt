A single-threaded web server in Java.

Command-line arguments
`--port <#>`: port number to listen on (e.g.: 8080)
`--directory <dir>`: where to serve files from (e.g.: /tmp)
`--responses <#>`: how many responses to make (0 = no limit)

Author: Diptanshu Giri

Github repo: https://github.com/msu-denver-cs/assignment-01-dgiri12

Java version: 11.0.4

JUnit version: 4.13.1

Building
The overall form of the directories are from Intellij, the following commands can be run from the main project directory.

javac -d out/production/Concurrent01 src/MyHttpServer.java
javac -d out/production/Concurrent01 -cp out/production/Concurrent01:lib/junit-4.13.1.jar:lib/hamcrest-core-1.3.jar src/MyHttpServerTest.java
    javac -d out/production/Concurrent01 -cp out/production/Concurrent01:lib/junit-4.13.1.jar:lib/hamcrest-core-1.3.jar src/ArgumentsTest.java
    

Running
  java -cp out/production/Concurrent01 MyHttpServer --port 8080 --responses 1 --directory /tmp 
If your browser says "Server dropped connection unexpectedly", try running the server on a different port number.

The /tmp/hello.html file looks like:

    <html>
    <head>
        <title>Hello world!</title>
    <body>
        Hello world!
    </body>
    </html>

Point your browser at http://localhost:8080/hello.html or other URIs like http://localhost:8080/tmp/hello.html. Whatever directory argument is put above, that becomes the homepage. If directory argument was "/tmp", the server will only respond to http://localhost:8080/tmp, and it won't respond to http://localhost:8080/, since it's scope is only within the tmp folder then.

### Testing
   java -ea -cp out/production/Concurrent01:lib/junit-4.13.1.jar:lib/hamcrest-core-1.3.jar org.junit.runner.JUnitCore MyHttpServerTest
    java -ea -cp out/production/Concurrent01:lib/junit-4.13.1.jar:lib/hamcrest-core-1.3.jar org.junit.runner.JUnitCore ArgumentsTest
