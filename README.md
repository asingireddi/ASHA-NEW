# Rehlko MSCVP Service 

REHLKO - Miracle Supply Chain Visibility Portal
---



Installations required 
---
Java 8, Maven, STS, MySQL database

1. Install Java 8 (Download from [here](https://www.oracle.com/in/java/technologies/javase/javase-jdk8-downloads.html))
2. Install Apache Maven
3. Install Spring tool suite (STS). Add these plugins if you don't have Java docs, Sql editor, Wiki editor.
4. Install MySQL database in your machine.  
Create a user : `test` as username and `test` as password.  



Configuring Lombok
---
rehlko-mscvp-service builds entities using project lombok and the spring tool suite needs the lombok jar to run any tests which uses these entities.

To enable lombok in STS/Eclipse follow set up instructions using : [https://projectlombok.org/setup/eclipse](https://projectlombok.org/setup/eclipse).

OR

1. Download version `1.16.10` and keep it in a folder (Do not delete this jar). 
   Download the Lombok jar from [here](https://projectlombok.org/all-versions). (Go to older versions to find `lombok-1.16.10`)
2. Close STS/Eclipse

If file does not get detected in the Lombok installer. Then click on Specify location and select `.ini` file which is at Eclipse/STS installed folder.

`If you face any issue with STS when specifying IDE path, try below one`

Rename the SpringToolSuite4.ini to sts.ini and  SpringToolSuite4.exe to sts.exe and now it is detectable in the Lombok installer.

Import Eclipse or STS settings :
---
[Import these settings](https://gitlab.com/mscvp/dev-workbench#import-eclipsests-settings)