# Repository, interfaces, and in-memory storage

A repository is the part of our application responsible for storing and retrieving shelf entries. It gives the rest of the code a place to ask for storage operations.

## Why move storage out of Main?

At first Main created the list, added entries, and printed everything. That was useful for learning. As the example grew, storage details mixed with demonstration and printing code.

We moved the list into InMemoryBookshelfRepository. Main now asks the service to perform operations, and the service uses the repository.

```text
Main: create objects and show output
  ↓
BookshelfService: check rules and coordinate operations
  ↓
InMemoryBookshelfRepository: store and retrieve entries
```

This is organisation of responsibilities, not something Java requires for every small program.

## An interface is a promise of available operations

```java
public interface BookshelfRepository {
    void add(ShelfEntry entry);
    List<ShelfEntry> findAll();
    ShelfEntry findByBookId(String bookId);
}
```

This interface needs `import java.util.List;` below its package declaration.

Think of a menu describing what you can request. The interface names the operations, their inputs, and their return types. It does not supply these methods' storage code.

| Operation | Input | Returned result |
|---|---|---|
| add | A shelf entry | None: void. |
| findAll | No parameters | A list of entries. |
| findByBookId | A book ID String | Matching entry, or null in our implementation. |

These declarations end in semicolons. The body of each operation is in the implementation class. The methods shown here are implicitly public.

## Implementing the promise

```java
public class InMemoryBookshelfRepository implements BookshelfRepository {
    private List<ShelfEntry> entries = new ArrayList<>();
}
```

implements means this class supplies the interface's operations. Java checks that it supplies the required methods. The class imports List and ArrayList.

InMemory means information stays in the running program's memory. Stopping and restarting this application loses those entries. No database or file storage has been added.

## add stores the supplied entry

```java
@Override
public void add(ShelfEntry entry) {
    entries.add(entry);
}
```

@Override marks this method as implementing or overriding an inherited method. Java can flag a mistake if it does not match the expected declaration.

entries is the internal list. entry is the supplied object. Adding the reference to the list does not create another Book.

## findAll returns a list copy

```java
@Override
public List<ShelfEntry> findAll() {
    return new ArrayList<>(entries);
}
```

This gives callers their own list container. They cannot add or remove elements from the repository's internal list by editing that returned list. The entries inside it remain shared objects.

The distinction explains why status changes remain visible: the object itself is shared, even though the list container is copied.

## findByBookId searches the stored entries

```java
@Override
public ShelfEntry findByBookId(String bookId) {
    for (ShelfEntry entry : entries) {
        if (entry.getBook().getId().equals(bookId)) {
            return entry;
        }
    }
    return null;
}
```

Each ID is compared with the requested String using equals. The first match ends the method. If no match exists, null signals absence.

Callers must check for null before accessing methods on a result. Your service performs that check and reports a missing-book error.

## Interface type, implementation object

```java
BookshelfRepository repository = new InMemoryBookshelfRepository();
```

The left side describes how we will use the object: through the BookshelfRepository interface. The right side creates the concrete object that performs those operations.

A single repository object is created here. The interface is not another storage object.

```java
BookshelfService service = new BookshelfService(repository);
```

The service receives that same object. It can call add, findAll, and findByBookId without inspecting the ArrayList field.

You cannot create this interface directly with new BookshelfRepository(). It describes a contract rather than providing the concrete implementation shown here.

## Repository versus service rules

The repository stores entries. The service checks rules such as rejecting duplicate IDs and missing statuses.

ArrayList does not know what a duplicate book ID means. Keeping the check in the service makes that application rule explicit. Main now calls service.addBook rather than building and storing entries itself.

The current classes are learning implementations. The repository does not independently guard every invalid input, and getBooks returns shared mutable entries. Do not assume these examples enforce every possible restriction.

## Follow one request

```java
service.addBook(book, ReadingStatus.TO_READ);
```

The service validates the book, checks whether its ID already exists, creates an entry, then calls repository.add. The repository stores the entry in its list.

## Check your understanding

Which part decides that duplicates are unacceptable? The service.

Which part currently holds the actual list? InMemoryBookshelfRepository.

Does the interface contain that list? No.

Next: [Service guide](07-bookshelf-service-explained.md), then [JUnit guide](08-junit-tests-explained.md).
