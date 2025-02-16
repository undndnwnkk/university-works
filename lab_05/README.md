# Collection Management Application

## Navigation
- [Overview](#overview)
- [Features](#features)
- [Project Structure](#project-structure)
    - [Storage System](#1-storage-system)
    - [Command Handling](#2-command-handling)
    - [Data Handling](#3-data-handling)
- [How It Works](#how-it-works)
    - [Initialization](#initialization)
    - [Adding a Flat](#adding-a-flat)
    - [Saving Data](#saving-data)
    - [Filtering Data](#filtering-data)
    - [Executing a Script](#executing-a-script)
- [Installation & Running](#installation--running)
    - [Requirements](#requirements)
    - [Compilation & Execution](#compilation--execution)
- [Javadoc documentation](#javadoc-documentation)
- [Diagram of project](#project-diagram)

## Overview
This project is a command-line application for managing a collection of real estate flats. It provides a set of commands to manipulate, filter, and store information about flats. The data is stored in a JSON file, allowing persistent data storage and retrieval.

## Features
- Add new flats to the collection
- Update existing flats
- Remove flats by ID or remove the first element
- Sort, reorder, and filter flats by various parameters
- Display all stored flats
- Save and load flats from a JSON file
- Execute scripts containing multiple commands

## Project Structure
### 1. **Storage System**
The `Storage` class manages the collection of flats, loads them from a JSON file, and provides an interface for handling various operations.

### 2. **Command Handling**
The `CommandsList` class processes user input and executes the appropriate commands. The available commands include:
- `help` - Displays available commands
- `info` - Shows information about the collection
- `show` - Displays all flats in the collection
- `add` - Adds a new flat
- `update` - Updates an existing flat by ID
- `remove_by_id` - Removes a flat by its ID
- `remove_first` - Removes the first flat in the collection
- `clear` - Clears the entire collection
- `save` - Saves the current state of the collection to a file
- `execute_script` - Executes commands from a script file
- `reorder` - Reorders the collection in reverse order
- `sort` - Sorts the collection by ID
- `min_by_id` - Displays the flat with the smallest ID
- `filter_by_transport` - Filters flats by transport type
- `filter_contains_name` - Displays flats whose names contain a given substring

### 3. **Data Handling**
- The `Reader` class reads flat data from a JSON file
- The `Writer` class saves the collection to a JSON file

## How It Works
### Initialization
1. The program is executed with a JSON filename as an argument.
2. The `Storage` class loads data from the specified file.
3. A loop runs, prompting the user to enter commands.
4. The `CommandsList` class processes and executes the entered command.

### Adding a Flat
1. The user enters the `add` command.
2. The program prompts for the flat's name, coordinates, area, number of rooms, furnish type, view, and transport type.
3. The new flat is validated and added to the collection.

### Saving Data
1. The user enters the `save` command.
2. The `Storage` class calls the `Writer` class to write the collection to a JSON file.

### Filtering Data
1. The user enters a filtering command like `filter_by_transport`.
2. The application searches for flats matching the given criteria.
3. The matching flats are displayed.

### Executing a Script
1. The user enters the `execute_script` command followed by a filename.
2. The program reads the script file line by line.
3. Each command is executed sequentially.

## Installation & Running
### Requirements
- Java 11+
- Gson library (for JSON parsing)

### Compilation & Execution
1. Compile the project:
   ```sh
   javac -d out -cp src/libraries/gson-2.12.1.jar -sourcepath src $(find src -name "*.java")
   ```
2. Create a JAR file:
   ```sh
   jar cfm app.jar src/META-INF/MANIFEST.mf -C out .
   ```
3. Run the application with a JSON file:
   ```sh
   java -cp app.jar:src/libraries/gson-2.12.1.jar application.Main data.json
   ```

### Javadoc documentation
1. Create docs/ folder with html-files:
```sh
javadoc -d docs -sourcepath src -subpackages application storage commands readWrite -classpath src/libraries/gson-2.12.1.jar
```
2. Go to docs/ folder
```sh
cd src/docs/
```
3. Open index.html file:
```sh
xdg-open index.html  # Linux
open index.html      # macOS
start index.html     # Windows (in command line)
```

### Project diagram
![Diagram](https://github.com/undndnwnkk/university-works/raw/main/lab_05/diagram.png)
