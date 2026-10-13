# Your Bookshelf MCP learning guide

These lessons cover what we have worked on so far, from the first project setup through creating a book from Open Library JSON and adding it to the shelf. They use your own code and the questions you asked along the way.

Read one lesson at a time. Open the matching Java file beside it, and use **Cmd + Shift + V** to preview Markdown in VS Code. Examples explain existing lessons; they are not instructions to paste every snippet into the project.

## Suggested reading order

| Order | Lesson | What you will understand |
|---|---|---|
| 1 | [Setup and project structure](01-java-setup-explained.md) | Java, Maven, VS Code, pom.xml, packages, src, and target. |
| 2 | [Java basics](02-java-basics-explained.md) | Variables, types, methods, main, printing, scope, and conditions. |
| 3 | [Classes and objects](03-classes-and-objects-explained.md) | Book, fields, constructors, this, references, and getters. |
| 4 | [Reading status and shelf entries](04-reading-status-and-shelf-entry-explained.md) | Enums, ShelfEntry, setters, and object relationships. |
| 5 | [Lists and loops](05-lists-and-loops-explained.md) | ArrayList, add, size, get(0), loops, ID lookup, and list copies. |
| 6 | [Repository and interfaces](06-repository-and-interfaces-explained.md) | Interface contracts, implementations, Override, and storage. |
| 7 | [Bookshelf service](07-bookshelf-service-explained.md) | Operations, validation, duplicates, exceptions, and status updates. |
| 8 | [JUnit tests](08-junit-tests-explained.md) | All five tests, setup, assertions, expected errors, and test results. |
| 9 | [Open Library and HTTP](09-open-library-http-explained.md) | Requests, responses, URLs, GET, Java HttpClient, and throws. |
| 10 | [JSON and Jackson](10-json-and-jackson-explained.md) | Keys and values, arrays, pretty-printing, and extracting response fields. |
| 11 | [From API result to shelf](11-api-book-to-shelf-explained.md) | Reading the author, creating a Book, adding it to the shelf, and checking the updated count. |
| 12 | [Java foundation finished](12-java-foundation-finished.md) | Current simplified code, error handling, missing-data rules, offline tests, and the transition to MCP. |

The service and JUnit guides already existed and remain the detailed lessons for those topics. The new guides connect to them rather than replacing them.

## Where we stopped

The Java preparation stage is complete. The client now builds a dynamic search URL, handles HTTP and JSON, and returns a Book. Main adds that book with status TO_READ and displays the shelf once. The demonstration books were removed, so the expected count is one.

All 14 tests passed after a clean build. The tests run offline; a live run of the simplified Main still needs checking on your machine. Lessons 9–11 preserve the earlier exercises; lesson 12 explains the current code.

The next stage is selecting and adding the MCP communication layer. No MCP server, persistence, hosting, or Salesforce connection has been built yet.

## Resume in another chat

Read [continue-here.md](continue-here.md) for the progress handoff and learning preferences. It is a session note rather than another lesson.

Suggested message:

> Please read documentation/continue-here.md and guide me from where we stopped. I want to write the code myself, one small step at a time.

## How to study without getting overwhelmed

1. Choose one short section rather than a whole file.
2. Find the corresponding code in your project.
3. Explain the line in your own words.
4. Predict what it will do before running it.
5. Compare the result with your prediction.

You do not need to memorise every import or keyword. Focus on the purpose of the operation and the values moving through it.

No lessons on unbuilt integration or hosting stages are included here.
