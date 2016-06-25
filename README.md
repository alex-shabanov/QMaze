###Author and Contributor List
####Alex Shabanov
------------------------------
List of Folders & Java Files
```
Main folders included in project:

- manifests
  - AndroidManifest.xml
- src/main/java/com/qmaze
  - QMaze.java
  - GameButtons.java
  - Constants.java
- res
  - drawable
  - layout
  - menu
  - minmap
  - values
```
===================
###About this app
```
This application is a game maze which finds a shortest path between start point and end point
by avoiding obstacle squares. The start and end point in the maze are placed by the user by
simply touching one of the squares. User can reset start and end location of square points.
After setting start and end square points, user can then place obstacle red squares.
The purpose of this maze is to reach the end point by avoiding all red square obstacles.
In case the dead end is reached, the best traced path so far gets backtracked until another
path is found or no path is found.

All the buttons in this app are created dynamically. That is, no xml code is present in the
layout content xml files for any of the buttons at compile time. 
This project features a seaparte Thread with Runnable which uses Handler to update main UI thread,
so that our UI thread is not overloaded. Because of this and the onSavedInstance state method, 
this app also supports screen orientation with no content beeing lost. 
```
=======================
###Running Instructions
```
Application can be launched using Android Studio 2.1.2 using JDK 1.8.
Android SDK Platform 6.0, 5.1, 5.0
git fork https://github.com/alex-shabanov/QMaze.git  
to create a copy of the project on GitHub account from where it can be pulled to local machine, or
git clone https://github.com/alex-shabanov/QMaze.git 
to create a copy of the porject on local machine where it can then be pushed to GitHub account
Add the QMaze folder to your Android project workspace.
```
=========================
###Examples

![alt tag](https://github.com/alex-shabanov/QMaze/blob/master/Screenshots/image1.png) ![alt tag](https://github.com/alex-shabanov/QMaze/blob/master/Screenshots/image2.png)
![alt tag](https://github.com/alex-shabanov/QMaze/blob/master/Screenshots/image3.png) ![alt tag](https://github.com/alex-shabanov/QMaze/blob/master/Screenshots/image4.png)
![alt tag](https://github.com/alex-shabanov/QMaze/blob/master/Screenshots/image5.png) ![alt tag](https://github.com/alex-shabanov/QMaze/blob/master/Screenshots/image6.png)
![alt tag](https://github.com/alex-shabanov/QMaze/blob/master/Screenshots/image7.png) ![alt tag](https://github.com/alex-shabanov/QMaze/blob/master/Screenshots/image8.png)
![alt tag](https://github.com/alex-shabanov/QMaze/blob/master/Screenshots/image9.png) ![alt tag](https://github.com/alex-shabanov/QMaze/blob/master/Screenshots/image10.png)
![alt tag](https://github.com/alex-shabanov/QMaze/blob/master/Screenshots/image11.png) ![alt tag](https://github.com/alex-shabanov/QMaze/blob/master/Screenshots/image12.png)
![alt tag](https://github.com/alex-shabanov/QMaze/blob/master/Screenshots/image13.png) ![alt tag](https://github.com/alex-shabanov/QMaze/blob/master/Screenshots/image14.png)
![alt tag](https://github.com/alex-shabanov/QMaze/blob/master/Screenshots/image15.png) ![alt tag](https://github.com/alex-shabanov/QMaze/blob/master/Screenshots/image16.png)
