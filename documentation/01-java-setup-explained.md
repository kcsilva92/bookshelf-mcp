# Java, Maven, VS Code, and your project folders

This is the first lesson. No prior Java knowledge is required. All examples describe tools and files already used in this project.

## What are we building?

A small bookshelf application: it describes books, stores shelf entries, and tracks whether you want to read, are reading, or have finished a book. We began with ordinary Java so you could understand each piece. This guide stops at the work already completed.

## Three tools with three jobs

Think of writing a recipe, preparing its ingredients, and cooking it. Different tools help with different parts.

| Tool | What it does |
|---|---|
| VS Code | Lets you write, edit, and run code through installed extensions. |
| Java JDK | Includes the Java compiler and runtime needed to build and run Java programs. |
| Maven | Organises building, dependencies, and test execution. |

VS Code is the editor, not the Java language. The Microsoft Extension Pack for Java supplies editor features for Java projects. Installing Maven does not create your bookshelf code for you.

## The checks you ran

```sh
java -version
```

This prints the Java runtime found by your terminal.

```sh
mvn -version
```

This prints Maven's version and the Java runtime Maven uses.

You originally had Java 21. After installing Maven through Homebrew, Maven reported Java 27. VS Code still ran your application using Java 21. These commands can report different Java installations because each tool can have its own configuration. We set the project to target Java 21.

`command not found: mvn` meant the shell could not find the Maven command. It was a setup problem, not a mistake in Book.java.

## Folder map

```text
bookshelf-mcp/
├── pom.xml
├── documentation/
├── src/
│   ├── main/java/com/ketheleen/bookshelf/
│   │   ├── Book.java
│   │   └── Main.java
│   └── test/java/com/ketheleen/bookshelf/
│       └── BookshelfServiceTest.java
└── target/
```

Maven conventionally reads application code from `src/main/java` and tests from `src/test/java`. `documentation` holds the notes you are reading.

`target` is outside `src`, which is correct. It contains generated compiled code and reports. You edit the Java source in `src`; build tools regenerate files in `target`.

A `.java` file contains readable source code. A compiled `.class` file contains instructions for the Java runtime. Running a program uses compiled code, so save your source before running it.

## What pom.xml tells Maven

`pom.xml` is a configuration file written in XML. XML uses opening and closing tags, such as `<version>` and `</version>`. It is not Java code.

Your main project values are:

```xml
<groupId>com.ketheleen</groupId>
<artifactId>bookshelf-mcp</artifactId>
<version>1.0-SNAPSHOT</version>
```

The group identifies the project owner or organisation. The artifact identifies this project. The version identifies this development version; `SNAPSHOT` indicates ongoing development.

```xml
<maven.compiler.release>21</maven.compiler.release>
<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
```

The release setting tells compilation to target Java 21 language/API compatibility and class format. It does not switch Maven's own Java runtime. UTF-8 is the character encoding for source files.

A dependency is a reusable library your code needs. You have Jackson for JSON and JUnit for testing. Maven obtains their Java libraries based on pom.xml. JUnit has `test` scope because it is used for tests.

A plugin performs build work. Your Surefire plugin runs the tests.

## Packages and imports

Your Java files begin with:

```java
package com.ketheleen.bookshelf;
```

A package groups related types and helps avoid naming clashes. Its folder path below the source root is `com/ketheleen/bookshelf`.

The source root is `src/main/java`, so `main.java` is not part of the package name. When VS Code expected `main.java.com.ketheleen.bookshelf`, it had not recognised the correct source root. Reloading the project resolved it.

An import lets you use a type from another package by its short name:

```java
import java.util.List;
```

Imports do not install libraries. Maven dependencies make external libraries available; imports let a file refer to their types conveniently.

## Running and testing

Run Main using VS Code's Run action or Run Without Debugging. The long command shown in the terminal is VS Code launching Java. Your program's output follows it.

The terminal prompt ending in `%` means the shell is ready for another command. It is not part of your book title.

```sh
mvn test
```

This builds and runs tests. It does not mean Main is executed.

## Saving conflicts

If VS Code says the file on disk is newer, the editor and disk have different versions. Use Compare to inspect both before deciding what to keep. Blindly overwriting can erase a newer change. This happened during the HTTP-client exercise.

## Check your understanding

Where do you edit application code? Under `src/main/java`.

Where do tests live? Under `src/test/java`.

Should you edit generated class files in target? No; edit the source and rebuild.

Next: [Java basics](02-java-basics-explained.md).
