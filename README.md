# java-pong
This is a pong clone that I created with only the Java 9 API. The AI is rather simplistic. Props if you can find a way to beat it.

Known bug that needs fixing:
Occasionally, the game does not recieve input from the player. This has something to do with the polled input not going through
to the game loop -- or the thread sleeping is not allowing the game to recieve input. 


