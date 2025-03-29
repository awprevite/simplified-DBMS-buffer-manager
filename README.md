# CS 4432 Database Systems II

## Simplified DBMS Buffer Manager

## Section I

Project created in IntelliJ. To use, clone the repository, open with IntelliJ and press run.

Alternatively follow these steps in the terminal once cloned.

1. Navigate to the project folder directory  
2. javac -d out src/*.java  
3. java -cp out Main  

Both these methods will begin the program and prompt you to enter a size for the buffer. Recommended to test it with 3.

There are 5 commands that can be issued:

 - GET recordNumber - returns the 40 bytes of information the indicated record contains if the file can exist in the buffer

 - SET recordNumber new40ByteSequence - updates the 40 bytes of information for the indicated record if the file can exist in the buffer

 - PIN fileNumber - pins the indicated file if there is space in the buffer

 - UNPIN fileNumber - unpins the indicated file if it is pinned

 - EXIT - terminates

It is important to note they must be types in all CAPITALS.

## Section II

Every test case in the provided file, testcase_commands_and_output.txt, is replicated with the correct functionality. No fails.

## Section III

### Added features
 - Type EXIT to end the program  
 - Enter buffer size on program launch rather than argument. This improves consistency for the program  
 - Error handling, not easy to break the program, includes number format exceptions and enforced ranges when handing user input  
 - Uses a first in first out replacement policy  
 - Buffer graphic. Clean terminal output that shows the size of the buffer, the locations of files, and indicates if the file is pinned. An "o" below the file number means pinned, an empty space means it is not  

```
---------- Buffer ----------  
 |   3   |   7   |   5   |  
 |   o   |       |   o   |  
---------- Pinned ----------
```

### Notes
 - While dirty bit is checked, nothing is actually written back to disk because the buffer exists in the same manner as the disk  
 - There is no functioning queue, if a request cannot be served due to lack of space in the buffer and all files pinned, the request is denied