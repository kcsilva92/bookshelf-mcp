# Understanding BookshelfService

Your service coordinates bookshelf operations and checks the rules. The repository handles storing and finding entries.

```text
Main → BookshelfService → BookshelfRepository
```

The implementation currently supplied to the service is `InMemoryBookshelfRepository`. It stores entries in a list while the program runs; those entries disappear when the program stops.

## 1. Holding the repository

```java
private final BookshelfRepository repository;
```

This field holds the repository the service will use.

- `private`: only this class accesses the field directly.
- `final`: once assigned, the field cannot point to a different repository. The repository's contents can still change.
- `BookshelfRepository`: the interface describing the available storage operations.

## 2. The constructor

```java
public BookshelfService(BookshelfRepository repository) {
    if (repository == null) {
        throw new IllegalArgumentException("Repository is required.");
    }
    this.repository = repository;
}
```

In `Main`, you write:

```java
BookshelfRepository repository = new InMemoryBookshelfRepository();
BookshelfService service = new BookshelfService(repository);
```

That passes your existing repository into the service. The service does not create a separate repository.

`null` means no object was supplied. The check prevents creating a service without storage. `throw` stops the operation with an error explaining the problem. If the error is not caught, it can stop the program.

In `this.repository = repository`, `this.repository` is the field belonging to the service object, and `repository` is the constructor parameter.

## 3. Adding a book

```java
public void addBook(Book book, ReadingStatus status) {
    if (book == null) {
        throw new IllegalArgumentException("Book is required.");
    }
    validateBookId(book.getId());
    validateStatus(status);

    if (repository.findByBookId(book.getId()) != null) {
        throw new IllegalArgumentException(
            "Book already on shelf: " + book.getId()
        );
    }

    ShelfEntry entry = new ShelfEntry(book, status);
    repository.add(entry);
}
```

This method follows a sequence:

1. Check that a book was supplied.
2. Check its ID and the supplied status.
3. Look for an existing entry with the same book ID.
4. If none exists, create a `ShelfEntry` and store it.

The duplicate check uses:

```java
repository.findByBookId(book.getId()) != null
```

If the lookup returns an entry, that book is already on the shelf, so the service rejects another addition. The current rule compares IDs, not titles.

`void` means the method returns no value.

Example call from `Main`:

```java
service.addBook(book, ReadingStatus.TO_READ);
```

Creating a book or a shelf entry does not automatically store it. The service calls `repository.add(entry)` to store the entry. Its reading status does not affect whether it counts as an entry on the shelf.

## 4. Getting the books

```java
public List<ShelfEntry> getBooks() {
    return repository.findAll();
}
```

This asks the repository for its entries and returns the result to the caller.

Your `Main` uses it here:

```java
List<ShelfEntry> shelf = service.getBooks();
```

`List<ShelfEntry>` means a list containing shelf entries. The service does not need to know how the repository stores them.

Your in-memory repository returns a copy of its list. Adding or removing entries from that returned list does not change the repository's internal list. However, the `ShelfEntry` objects inside both lists are shared; this is not a deep copy.

## 5. Updating reading status

```java
public void updateStatus(String bookId, ReadingStatus status) {
    validateBookId(bookId);
    validateStatus(status);

    ShelfEntry entry = repository.findByBookId(bookId);
    if (entry == null) {
        throw new IllegalArgumentException("Book not found: " + bookId);
    }

    entry.setStatus(status);
}
```

For this call:

```java
service.updateStatus("OL27448W", ReadingStatus.READING);
```

The service checks the inputs, finds the Hobbit entry, and changes its status.

It updates the existing entry; it does not add another one. The shelf count stays the same.

The in-memory repository holds that same `ShelfEntry` object, so later retrieval shows the updated status. A future database implementation will need a way to persist updates explicitly; changing a Java object alone does not necessarily update a database.

## 6. The validation helpers

```java
private void validateBookId(String bookId) {
    if (bookId == null || bookId.isBlank()) {
        throw new IllegalArgumentException("Book ID is required.");
    }
}
```

`||` means "or": reject the ID if it is missing or blank. `isBlank()` catches both `""` and text containing only spaces.

Java checks the left side first. If `bookId` is `null`, it skips `isBlank()`, avoiding a call on a missing object. This is called short-circuit evaluation.

```java
private void validateStatus(ReadingStatus status) {
    if (status == null) {
        throw new IllegalArgumentException("Reading status is required.");
    }
}
```

Your enum restricts which statuses are allowed (`TO_READ`, `READING`, and `READ`), but a variable can still contain `null`. This check requires an actual status.

Both helpers are `private` because they support operations inside the service. Reusing them keeps validation consistent between adding a book and updating its status.

## Responsibilities of the classes

| Class or interface | Responsibility |
|---|---|
| `Book` | Holds the ID, title, author, and publication year. |
| `ReadingStatus` | Defines the three allowed reading statuses. |
| `ShelfEntry` | Connects a book with its current reading status. |
| `BookshelfRepository` | Describes the storage operations. |
| `InMemoryBookshelfRepository` | Implements storage using an in-memory list. |
| `BookshelfService` | Coordinates operations and checks bookshelf rules. |
| `Main` | Creates objects, calls the service, and displays results. |

## Example usage

```java
BookshelfRepository repository = new InMemoryBookshelfRepository();
BookshelfService service = new BookshelfService(repository);

Book book = new Book("OL27448W", "The Hobbit", "J. R. R. Tolkien", 1937);

service.addBook(book, ReadingStatus.TO_READ);
service.updateStatus(book.getId(), ReadingStatus.READING);

List<ShelfEntry> shelf = service.getBooks();
System.out.println("Books on shelf: " + shelf.size());

for (ShelfEntry entry : shelf) {
    System.out.println(entry.getBook().getTitle() + " - " + entry.getStatus());
}
```

This snippet belongs inside a method such as `main`. Include `import java.util.List;` below the package declaration.

Expected output:

```text
Books on shelf: 1
The Hobbit - READING
```

## Current scope

The service checks missing inputs, blank IDs, duplicate IDs, and missing books during status updates. It does not yet validate titles, authors, or publication years. It also does not call Open Library or expose MCP tools; those are later integration steps.
