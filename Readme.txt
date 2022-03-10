A single-threaded web server in Java.

Command-line arguments
`--port <#>`: port number to listen on (e.g.: 8081)
`--directory <dir>`: where to serve files from (e.g.: /tmp)
`--responses <#>`: how many responses to make (0 = no limit)
'--threads <#>': how many threads to use (default is 1 if nothing entered)

Author: Diptanshu Giri

Github repo: https://github.com/msu-denver-cs/cs39acspring2020assignment02-dgiri12

Java version: 11.0.4

JUnit version: 4.13.1

Building
The overall form of the directories are from Intellij, the following commands can be run from the main project directory.

javac -d out/production/Concurrent01 src/MyHttpServer.java
javac -d out/production/Concurrent01 -cp out/production/Concurrent01:lib/junit-4.13.1.jar:lib/hamcrest-core-1.3.jar src/MyHttpServerTest.java
    javac -d out/production/Concurrent01 -cp out/production/Concurrent01:lib/junit-4.13.1.jar:lib/hamcrest-core-1.3.jar src/ArgumentsTest.java
    

Running
  java -cp out/production/Concurrent01 MyHttpServer --port 8081 --responses 1 --directory /tmp --threads 10 
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

Running with thread (Bench results) (Details please see Thread-benchresults.txt)
Server can handle at most threads of 92. Server works best with 70 threads.
Request per second	->	no. of threads
1951.29			->	1
2083.03			->	50
2060.72			->	92

### Testing
   java -ea -cp out/production/Concurrent01:lib/junit-4.13.1.jar:lib/hamcrest-core-1.3.jar org.junit.runner.JUnitCore MyHttpServerTest
    java -ea -cp out/production/Concurrent01:lib/junit-4.13.1.jar:lib/hamcrest-core-1.3.jar org.junit.runner.JUnitCore ArgumentsTest
