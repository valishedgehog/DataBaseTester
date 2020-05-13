# DBMS testing framework

Testing framework for SelSQL.

## Ubuntu dependencies

#### Build SelSQL
```bash
git clone https://github.com/hehogcode/SelSQL.git
sudo apt install gcc g++ cmake bison flex libboost-dev -y
cd SelSQL
cmake CMakeList.txt
make
```

#### Build DataBaseTester

```bash
git clone https://github.com/hehogcode/DataBaseTester.git
sudo apt install openjdk-8-jdk maven
cd DataBaseTester
mvn clean install
```

#### Run DataBaseTester
```bash
java -jar TestModule/target/Tester.jar
```
## Configuration
Example config file:
```
{
    "testsFolder": "/home/anton/Workspace/DataBaseTester/TestModule/tests",
    "serverExecutable": "/home/anton/Workspace/SelSQL/Server/Server",
    "port": 18666
}
```

## Tests structure

Example of test folder:
```
TestFolder
    expected
        hello.expected
        world.expected
    results
        hello.codes
        hello.out
        world.codes
        world.out
    hello.in
    world.in
```

The ```TestFolder``` folder contains a test whose name matches the name of the folder.

Files with the extension **.in** are test files with SQL queries and commands for the testing framework. 
In the example above, there are two tests called ```hello``` and ```world```.

The ```expected``` folder contains the expected output for each test 
in files with the extension **.expected**. The output should match exactly what you want, including white space and line breaks. 
In the example above, there are two files with the expected output for each test: ```hello.expected``` and ```world.expected```.

The ```results``` folder contains valid tests results. Files with the extension **.out** store the received output, which will then be compared with the corresponding **.expected** file. Files with the extension **.codes** contain operation codes, executable commands and their output in json format.

The test framework will automatically create the necessary folders and files using the *create* command.

## Command line interface

- ### ```run``` command
Run test or list of tests for executing. You can write ```run all``` for running all available tests from tests root folder. 

Examples:
```
run testname
run testname1 testname2
run all
```

- ### ```ls``` command
Aliases: ```list```, ```lst```, ```dir```

List available tests in tests root folder

- ### ```create``` command
Aliases: ```new```

Create new folder with test. Takes the name of the test or a list of names as an argument.

Examples:
```
create testname
new testname
create testname1 testname2
```

- ### ```delete``` command
Aliases: ```remove```, ```del```, ```rm```

Removes folder with test. Takes the name of the test or a list of names as an argument.

Examples:
```
rm testname
delete testname
del testname1 testname2
```

- ### ```clear``` command
Aliases: ```cls```

Clears the screen of terminal. Not all of terminals are supported. Check if your terminal supports ANSI escape codes.

- ### ```client``` command
Run client for SelSQL

- ### ```exit``` command
Aliases: ```quit```

Exit from testing framework

## Test framework commands

You can use these commands inside tests (**.in** files).

- ### ```[@Clear]```

Clear the database (deletes the folder with the current database and re-creates it).

- ### ```[@PrintLevel] MAIN|EXTENDED|NONE```
Default value: MAIN

Sets the level of the desired output. For printing complete testing progress, use EXTENDED. To get only the test result (no error output) use NONE.

- ### ```@@ Some Text```
Comment. Just a comment.

- ### ```[@Sleep] TIME```
Sleep for TIME milliseconds

- ### ```[#Repeat] TIMES```
Preprocessor command.
Repeat next expression TIMES times. In expression you can use $i variable. It will be replaced from 1 to TIMES on each iteration.

Example:
```
Input:
[#Repeat] 3
SELECT * FROM mytable WHERE somefield=$i

Output:
SELECT * FROM mytable WHERE somefield=1
SELECT * FROM mytable WHERE somefield=2
SELECT * FROM mytable WHERE somefield=3
```

- ### ```[@NoOutput]```
Toggles no output mode. All output between pair of such commands will be ignored and will not write to .out and .codes files.

- ### ```[@Echo] SOME TEXT```
Echo some text to [TestInfo]

- ### ```[@RestartServer]```
Restart client server. Has 3 variations:
```
1) [@RestartServer]
2) [@RestartServer] TIME
3) [@RestartServer] TIME MINTIME
```

1) Reloads the client server in the same thread of execution.
2) Reboots the server at a random moment in time interval 0..TIME asynchronously from the main thread.
3) Reboots the server at a random time in the interval MINTIME..TIME asynchronously from the main thread.

- ### ```[@Print] SOME TEXT```
Print something to output file