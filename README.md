# Trading Robot

## Description

Trading robot that can be used to trade on various exchanges. The robot is designed to be modular
and can be easily extended to support new exchanges and trading strategies.

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)

## Installation

1. Clone the repository:
    ```sh
    git clone git@github.com:ShadowInsights/trading-robot.git
    ```
2. Navigate to the project directory:
    ```sh
    cd trading-robot
    ```
3. Build the project using Gradle:
    ```sh
    ./gradlew build
    ```
4. Build Fat JAR:
    ```sh
    ./gradlew shadowJar
    ```

## Usage

Instructions on how to run and use the project. For example:

1. Run application in development mode:
   ```sh
   ./gradlew runInDevelopmentMode
   ```
2. Build and run application in production mode:
   ```sh
   ./gradlew shadowJar
   java -Dconfig.file=/application.conf -jar build/libs/trading-robot.jar
   ```