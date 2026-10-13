# JUnit tests explained, one small step at a time

A learning guide for your Bookshelf MCP project. You do not need to understand this whole document in one sitting. Read one section, open the matching code, and take your time.

## 1. What is a test?

Imagine you have a toy box. You put one toy inside. You expect there to be one toy in the box.

A test asks: **Did the thing I expected actually happen?**

For your project, the toy box is the bookshelf, and the toys are shelf entries.

```text
Do something: add one book.
Expect something: the shelf contains one entry.
Check: does it really contain one entry?
```

A software test is Java code that performs an operation and checks its result. You already checked your application manually when you ran `Main` and read the terminal output. Automated tests let the computer repeat those checks for you.

## 2. What are JUnit and Maven?

Think of JUnit as a checker with a checklist. You write the checklist; JUnit carries it out.

Think of Maven as the organiser. It gathers the required libraries, compiles your code, and asks the test runner to run the checks.

| Name | Job in this project |
|---|---|
| You, the developer | Decide what the application should do and write checks for it. |
| JUnit Jupiter | Provides annotations and assertions for our JUnit 5 tests. |
| Maven | Builds the project when you run Maven commands. |
| Maven Surefire | Discovers and runs the tests during `mvn test`. |

JUnit does not invent the bookshelf rules. We decided that duplicate IDs must be rejected, then wrote a test for that rule. AI can help write tests, but you still need to understand their expectations.

## 3. Where does everything live?

```text
bookshelf-mcp/
├── pom.xml
├── documentation/
│   ├── 07-bookshelf-service-explained.md
│   └── 08-junit-tests-explained.md
├── src/
│   ├── main/java/com/ketheleen/bookshelf/
│   │   └── BookshelfService.java
│   └── test/java/com/ketheleen/bookshelf/
│       └── BookshelfServiceTest.java
└── target/
    ├── classes/
    ├── test-classes/
    └── surefire-reports/
```

- `src/main/java` contains application code.
- `src/test/java` contains code that checks the application.
- `pom.xml` tells Maven about dependencies and build settings.
- `target` contains generated files. Maven creates it outside `src`, which is correct.
- `target/surefire-reports` contains test results after a test run.

Edit your source files, not generated files in `target`.

## 4. Why did we change pom.xml?

JUnit is a library: reusable Java code written by other people. Your project needs that library to understand names such as `@Test` and `assertEquals`.

We added this dependency:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.13.4</version>
    <scope>test</scope>
</dependency>
```

The group and artifact identify the library. The version selects its release. `test` scope means it is used for test compilation and execution, rather than as a normal application dependency.

We also configured Maven Surefire version `3.5.4` to run the tests. You do not need to memorise these version numbers.

## 5. The top of the test file

Open `src/test/java/com/ketheleen/bookshelf/BookshelfServiceTest.java` alongside this guide.

```java
package com.ketheleen.bookshelf;
```

The test belongs to the same Java package as your bookshelf classes, even though its file is in a different source folder.

```java
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
```

Imports let the file use these types by their short names.

```java
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
```

These are **static imports**. They let us call assertion methods directly, such as `assertTrue(...)`, instead of writing `Assertions.assertTrue(...)` each time. An assertion is a check that can pass or fail.

```java
class BookshelfServiceTest {
    private BookshelfService service;
    private Book book;
}
```

The test class holds a service and a book for its tests to use. Its name ends in `Test`, matching Maven Surefire's usual test naming convention. JUnit 5 test classes and test methods do not have to be `public`; leaving that word out here is intentional.

There is no `main` method. The test runner starts the tests for us.

## 6. Before each test: a fresh toy box

```java
@BeforeEach
void setUp() {
    service = new BookshelfService(new InMemoryBookshelfRepository());
    book = new Book("OL27448W", "The Hobbit", "J. R. R. Tolkien", 1937);
}
```

`@BeforeEach` means: **Run this method before every test method.**

The name `setUp` is our choice. The annotation is what gives the method its special role.

The first line creates an empty repository and passes it to a new service. The second line creates a Hobbit book object.

**Creating the book does not add it to the shelf.** The shelf starts empty until a test calls `service.addBook(...)`.

Think of resetting the toy box before each game:

```text
Fresh setup → test A
Fresh setup → test B
Fresh setup → test C
```

Books added in one test do not carry over into the next test. This makes the tests independent. We do not rely on tests running in the order they appear in the file.

`void` means this method returns no value.

## 7. Three steps in a test

Many tests follow a pattern called **Arrange, Act, Assert**:

1. **Arrange:** prepare the things you need.
2. **Act:** perform the operation you want to check.
3. **Assert:** check the result.

Toy box version:

```text
Arrange: get an empty box and a toy.
Act: put the toy into the box.
Assert: check there is one toy inside.
```

In our tests, `setUp()` does some of the arranging before the test starts. These are a helpful pattern, not special Java keywords.

## 8. Test one: a new shelf is empty

```java
@Test
void newShelfIsEmpty() {
    assertTrue(service.getBooks().isEmpty());
}
```

`@Test` tells JUnit this method is a test. `newShelfIsEmpty` describes what it checks.

Read the expression inside out:

```java
service.getBooks()
```

Ask the service for its shelf entries.

```java
service.getBooks().isEmpty()
```

Ask the list whether it contains zero entries. The result is a boolean: `true` or `false`.

```java
assertTrue(service.getBooks().isEmpty());
```

Require the answer to be `true`.

| Shelf contents | isEmpty() | Test result |
|---|---|---|
| No entries | `true` | Pass |
| One or more entries | `false` | Fail |

If we added a book before this assertion, the assertion would fail. That is the checker doing its job: an occupied shelf is not empty.

**Pause:** say what this test expects in your own words before continuing.

## 9. Test two: adding a book stores it correctly

```java
@Test
void addingBookStoresItsDetailsAndStatus() {
    service.addBook(book, ReadingStatus.TO_READ);

    List<ShelfEntry> shelf = service.getBooks();
    assertEquals(1, shelf.size());
    assertEquals("OL27448W", shelf.get(0).getBook().getId());
    assertEquals("The Hobbit", shelf.get(0).getBook().getTitle());
    assertEquals(ReadingStatus.TO_READ, shelf.get(0).getStatus());
}
```

### Perform the operation

```java
service.addBook(book, ReadingStatus.TO_READ);
```

This adds the book prepared by `setUp()` with a `TO_READ` status. The service creates a shelf entry and asks the repository to store it.

### Read the result

```java
List<ShelfEntry> shelf = service.getBooks();
```

We retrieve the entries and name the returned list `shelf`.

### Check the count

```java
assertEquals(1, shelf.size());
```

The order is:

```java
assertEquals(expected, actual);
```

We expect `1`. The actual value is `shelf.size()`. If the service added nothing or added two entries, this assertion would fail.

### Understand get(0)

Java list positions start at zero:

| Position | Meaning |
|---|---|
| `0` | First entry |
| `1` | Second entry |
| `2` | Third entry |

So `shelf.get(0)` gets the first entry. It does not count entries or search by book ID.

### Follow the chain

```java
shelf.get(0).getBook().getTitle()
```

Read it from left to right:

```text
shelf → first ShelfEntry → its Book → that book's title
```

Compare that title with the expected one:

```java
assertEquals("The Hobbit", shelf.get(0).getBook().getTitle());
```

If the actual title were `"Teste"`, this check would fail.

The ID assertion follows the same pattern. The status assertion calls `getStatus()` directly on the shelf entry because status belongs to `ShelfEntry`, not `Book`.

These assertions check the count, ID, title, and status. They do not check every property: for example, this test does not yet assert the author or publication year.

## 10. Test three: reject duplicate IDs

```java
@Test
void duplicateIdIsRejectedWithoutChangingTheShelf() {
    service.addBook(book, ReadingStatus.TO_READ);
    Book duplicate = new Book(book.getId(), "Another title", "Another author", 2000);

    assertThrows(IllegalArgumentException.class,
        () -> service.addBook(duplicate, ReadingStatus.READ));

    assertEquals(1, service.getBooks().size());
    assertEquals("The Hobbit", service.getBooks().get(0).getBook().getTitle());
    assertEquals(ReadingStatus.TO_READ, service.getBooks().get(0).getStatus());
}
```

First, we store the original book. Then we create a **different book object with the same ID**. Even though the title and author differ, the service should reject it because our rule uses the ID to identify a book.

### What is an exception?

An exception is Java's way of signalling that something went wrong during an operation. Your service uses `IllegalArgumentException` when an input or requested operation is unacceptable.

For example, it throws this exception when the book ID is already on the shelf.

### What does assertThrows mean?

```java
assertThrows(IllegalArgumentException.class,
    () -> service.addBook(duplicate, ReadingStatus.READ));
```

Read it as:

> Run this operation. I expect an IllegalArgumentException.

`IllegalArgumentException.class` tells JUnit which exception type we expect. It is a reference to that Java type, not a new exception object.

`()` followed by `->` forms a **lambda**: a small piece of code we give to JUnit to execute. Here it means, roughly, "when you run this action, call addBook with these arguments." JUnit needs to run the action itself so it can observe the exception.

Do not worry about mastering lambdas today. Recognising that this one supplies an action is enough.

| What happens during the supplied action? | Assertion result |
|---|---|
| Expected exception type, or a subtype, is thrown | Pass |
| No exception is thrown | Fail |
| An unrelated exception type is thrown | Fail |

An expected exception inside `assertThrows` is a successful test outcome. It is not an unexpected test error.

### Why check the shelf afterwards?

A broken service might store the duplicate and then throw an exception. The exception check alone would miss that mistake.

The remaining assertions check that there is still one entry, with the original title and status. The rejected operation must leave the existing shelf intact.

## 11. Test four: update only the selected book

```java
@Test
void updatingStatusChangesOnlyTheSelectedBook() {
    Book secondBook = new Book("second", "Second book", "Author", 2000);
    service.addBook(book, ReadingStatus.TO_READ);
    service.addBook(secondBook, ReadingStatus.TO_READ);

    service.updateStatus("second", ReadingStatus.READING);

    List<ShelfEntry> shelf = service.getBooks();
    assertEquals(2, shelf.size());
    assertEquals(ReadingStatus.TO_READ, shelf.get(0).getStatus());
    assertEquals(ReadingStatus.READING, shelf.get(1).getStatus());
}
```

Arrange: create and add two books, both `TO_READ`.

Act: update the book whose ID is `"second"`.

Assert:

- The shelf still has two entries. Updating should not add or remove an entry.
- The first book stays `TO_READ`.
- The second book becomes `READING`.

Checking both statuses helps catch a bug where the service updates the wrong entry or changes every entry.

This test uses positions because the current repository preserves insertion order. If we later change that contract, we should adjust the test to locate entries by ID instead.

## 12. Test five: reject updating a missing book

```java
@Test
void updatingMissingBookIsRejectedWithoutChangingExistingEntries() {
    service.addBook(book, ReadingStatus.TO_READ);

    assertThrows(IllegalArgumentException.class,
        () -> service.updateStatus("missing", ReadingStatus.READ));

    assertEquals(1, service.getBooks().size());
    assertEquals(ReadingStatus.TO_READ, service.getBooks().get(0).getStatus());
}
```

We put the Hobbit on the shelf, then request an update for ID `"missing"`. No book with that ID exists.

We expect the service to reject the request with an exception. Afterwards, the original book should still be present with its original status.

This checks both the rejection and its effect on existing data.

## 13. Running the tests

Open the terminal in the project folder containing `pom.xml` and run:

```sh
mvn test
```

Maven compiles the code and runs the tests. The first run may download dependencies. Your `Main` method is not needed to run these tests.

To run only this test class:

```sh
mvn -Dtest=BookshelfServiceTest test
```

To run only the empty-shelf test:

```sh
mvn '-Dtest=BookshelfServiceTest#newShelfIsEmpty' test
```

The quotes keep the full selection argument together for the shell.

## 14. Understanding your successful output

You saw:

```text
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

| Output | Meaning |
|---|---|
| `Tests run: 5` | Five test cases were run in this test class. |
| `Failures: 0` | No checks failed. |
| `Errors: 0` | No unexpected test errors were reported. |
| `Skipped: 0` | No tests were skipped. |
| `BUILD SUCCESS` | Maven completed this build successfully. |

The five tests contain more than five assertions. Maven counts test cases, not individual assertions.

`Time elapsed` for the test class and Maven's `Total time` measure different scopes. Maven also spends time preparing and compiling the project.

Passing tests show that the tested scenarios behaved as expected. They do not prove every possible scenario is correct, or that Agentforce integration works. These tests exercise your Java service with an in-memory repository; they do not use Open Library, a database, or Agentforce.

## 15. What a failure can teach you

Suppose a check expects `1`, but the shelf contains `2`. JUnit may report a message like:

```text
expected: <1> but was: <2>
```

This tells you where the expectation and reality differ. A failing test can reveal a bug, but the test itself could also have the wrong expectation. Read both before making a change.

To see a failure safely, you can temporarily change:

```java
assertEquals(1, shelf.size());
```

to:

```java
assertEquals(99, shelf.size());
```

Run that test, observe the failure, then restore `1` and run it again. This deliberately incorrect expectation is a learning exercise, not a rule the application should satisfy.

## 16. Small exercises, one at a time

### Exercise A: Predict the result

After adding one book, would this pass?

```java
assertTrue(service.getBooks().isEmpty());
```

Answer: no. The shelf is occupied, so `isEmpty()` returns `false`.

### Exercise B: Check the author

Add one assertion to the adding-book test that checks the author is `"J. R. R. Tolkien"`.

Use the title assertion as a pattern, but call `getAuthor()`.

Possible answer:

```java
assertEquals("J. R. R. Tolkien", shelf.get(0).getBook().getAuthor());
```

### Exercise C: Check the year

Try checking the publication year yourself before reading this answer:

```java
assertEquals(1937, shelf.get(0).getBook().getPublicationYear());
```

`1937` is an integer, so it has no quotation marks.

### Exercise D: Explain a test aloud

Choose one test and describe:

1. What do we prepare?
2. What action do we perform?
3. What do we expect afterwards?

Being able to answer those questions matters more than memorising every line.

## 17. What is not tested yet?

The current five tests cover the main operations we discussed. The service also checks missing books supplied to `addBook`, missing repositories, blank or missing IDs, and missing statuses. Those validation branches do not yet have their own JUnit tests in this file.

We can add those gradually when you understand the existing tests. You do not need to build a huge test file all at once.

## 18. Tiny glossary

| Word | Plain meaning |
|---|---|
| Test | Code that performs a scenario and checks the result. |
| Assertion | A check of an expected result. |
| Expected | What we believe should happen. |
| Actual | What really happened. |
| Annotation | A marker such as `@Test` that gives Java tools information. |
| Setup | Preparation before a test. |
| Exception | Java's way of signalling a problem during execution. |
| Lambda | A small piece of executable code supplied as a value. |
| Dependency | A library the project uses. |
| Test runner | The machinery that discovers and executes tests. |

## 19. A gentle reading order

Start with sections 1, 6, and 8. Run only `newShelfIsEmpty`. Then read section 9 and run the adding-book test. Leave `assertThrows` and the exception tests until those first checks feel familiar.

Your goal is to understand one small check, then build from it.

## Official references

For later reading, rather than something you need to memorise now:

- [JUnit 5.13.4 User Guide](https://docs.junit.org/5.13.4/user-guide/) explains annotations, assertions, and test lifecycle.
- [Maven Surefire: Using JUnit Platform](https://maven.apache.org/surefire/maven-surefire-plugin/examples/junit-platform.html) explains running JUnit through Maven.
