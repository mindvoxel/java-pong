## Looking for a new java game engine? 

Look no further than https://createindiestudios.itch.io/jpxlz. A Kotlin/Java game engine tailor made for 2D retro/pixel-art games!

java-pong 
======
Java-pong is a table tennis game built with the Java 7 API. The user plays against a simple AI and the winner is the player who scores seven points first. You can play the game [here](cool-free-games.com/java-pong) (Website is not yet functional).

## Game Instructions

Use the arrow keys to move the left paddle.
Press enter to start a new game (when a game is not currently started). 

## Getting Started

### Prerequisites for Downloading and Running the Source Code

Download the Java Development Kit (JDK). Versions 7 and above would be suitable. Read the [installation instructions](http://www.oracle.com/technetwork/java/javase/downloads/index.html). 

### Downloading and Running the Source Code

1. Fork and clone the java-pong repository.
2. Navigate to the cloned repository's `java` directory with the command `cd java` and run 
~~~
javac player/*.java pong/*.java sound/*.java
~~~
   to compile all of the project’s java files. 

3. Run the game using:
~~~
java pong.Game
~~~
4. You can view the view the performance of java-pong with the command `jconsole <pid of pong.Game on your computer here>`. The goal is to minimize the number of threads while maximizing performance! 

## Bugs and support
* To report bugs and request features, please use the GitHub issue tracker at:
<br /> https://github.com/chris-wolff/java-pong/issues
* For more information on contributing, see the [contributing guide](https://github.com/chris-wolff/java-pong/blob/master/CONTRIBUTING.md)

