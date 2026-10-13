# ReadingStatus and ShelfEntry

A book has a title regardless of whether you have read it. Your reading status is information about that book on your shelf. We separated those ideas into Book and ShelfEntry.

## An enum is a fixed set of named choices

```java
public enum ReadingStatus {
    TO_READ,
    READING,
    READ
}
```

Think of three buttons: want to read, reading now, and finished. ReadingStatus defines the allowed choices.

Using plain text could allow inconsistent values such as "reading", "Reading", and "started". The enum gives your Java code a shared vocabulary.

```java
ReadingStatus status = ReadingStatus.TO_READ;
```

ReadingStatus is the type. status is the variable name. ReadingStatus.TO_READ selects one enum constant. Enum constant names conventionally use uppercase letters and underscores.

An enum variable can still be null, meaning no choice was supplied. The service checks this separately.

## ShelfEntry connects two things

```java
public class ShelfEntry {
    private Book book;
    private ReadingStatus status;
}
```

These are types you created yourself. Fields do not have to be only String or int.

```text
ShelfEntry
├── book → Book with title, ID, author, year
└── status → TO_READ, READING, or READ
```

This is an example of composition: an object holds another object as part of its information. You do not need to memorise the term to understand the relationship.

## The constructor

```java
public ShelfEntry(Book book, ReadingStatus status) {
    this.book = book;
    this.status = status;
}
```

It receives an existing Book object and an enum value. It stores the Book reference; it does not recreate the book's title and author.

```java
ShelfEntry entry = new ShelfEntry(book, ReadingStatus.TO_READ);
```

The constructor is named ShelfEntry because it belongs to that class. Adding another constructor named Book to Book.java would not create a shelf entry.

## Reading values with getters

```java
public Book getBook() {
    return this.book;
}

public ReadingStatus getStatus() {
    return this.status;
}
```

The return type matches the field being read. getBook returns a Book; getStatus returns a ReadingStatus.

```java
entry.getBook().getTitle()
```

Follow the chain: entry → its Book → that Book's title. The entry does not have its own title field.

## Updating a field with a setter

```java
public void setStatus(ReadingStatus status) {
    this.status = status;
}
```

A setter changes a field. void means no result is returned.

```java
entry.setStatus(ReadingStatus.READING);
entry.setStatus(ReadingStatus.READ);
```

Both lines change the same entry. They do not create two more entries. Its final status becomes READ.

In the current class, this setter does not enforce a required order of transitions. You can choose any non-null enum value through the service; we have not created rules that force TO_READ → READING → READ.

## Status and shelf count are separate

An entry counts when it is stored with add, regardless of its status. TO_READ is not a special counting operation.

```text
Add one entry → shelf count increases.
Change its status → shelf count stays the same.
```

Creating ShelfEntry alone does not add it to a list. In Main, the service creates entries and stores them for us.

## Why the service is involved now

Early exercises called entry.setStatus directly. Later we added service.updateStatus(bookId, status), so callers can identify a book by ID and let the service find and validate it.

The setter still exists. The service uses it after applying its rules. Calling the setter directly bypasses those service checks, which is one reason the service is the intended entry point for bookshelf operations.

## Tiny exercise

If an entry is READ and you change it to READING, how many new books did you add? Zero. You changed one property of an existing entry.

Next: [Lists, loops, and lookup](05-lists-and-loops-explained.md).
