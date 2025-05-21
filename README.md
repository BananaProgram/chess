# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

[![Sequence Diagram Better](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=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-buwXp-YqXs8kyAVSrPwWg0mAVhNc2YXtZjEsoKS6AYyLoTL5NgbVhhSHxXmWgFA7zjVQCQanXFMBmWMsAhg96iSzKeL-Fi8R8ALkVzhkXV+tdLEBC8Ii0N7pYDAGwHYwg8REguK7lTQNAl6aM2ZqzYwHKlUpO4HgUBMA1V732mWuE5rWSCsQImuBMgFA1A0Csn6hdg2l3cZc6uUagA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
