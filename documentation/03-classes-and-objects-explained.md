# Book: classes, objects, constructors, and getters

Imagine a blank book-information card with spaces for an ID, title, author, and year. A class describes the shape of that card. An object is one particular filled-in card.

## Class versus object

```java
public class Book {
}
```

Book is a class: a type you defined. This declaration does not create a Hobbit book.

```java
Book book = new Book("sample-id", "The Hobbit", "J. R. R. Tolkien", 1937);
```

This creates one Book object. `book` is a variable referring to it. Creating a second object gives you a separate book with its own details.

## Fields: information belonging to an object

```java
private String id;
private String title;
private String author;
private int publicationYear;
```

These fields are inside the class but outside methods. Each Book object has its own values for them.

private prevents other classes from directly accessing these fields. This is part of encapsulation: the class controls how its data is accessed.

The field types describe what can be stored. An ID can contain letters and numbers, so it is text rather than an int. The publication year is a whole number.

## Constructor: filling the card when creating it

```java
public Book(String id, String title, String author, int publicationYear) {
    this.id = id;
    this.title = title;
    this.author = author;
    this.publicationYear = publicationYear;
}
```

A constructor has the same name as its class and no return type, not even void. It runs as part of creating an object with new.

In `this.title = title`:

- this.title is the field on the object being created.
- title is the constructor parameter supplied by the caller.
- The assignment stores the supplied value in that object's field.

The names happen to match, which is why this helps distinguish them. Java is case-sensitive: book and Book are different names.

## Arguments must be in order

```java
new Book("sample-id", "The Hobbit", "J. R. R. Tolkien", 1937)
```

The arguments match ID, title, author, and year in that order. Standard Java calls do not use labels such as `id: "sample-id"`. Commas separate the arguments.

If you swap the title and author, both are Strings, so the code could compile but store the wrong meaning. Understanding argument order matters.

## References: variables point to objects

```java
Book first = new Book("sample-id", "The Hobbit", "J. R. R. Tolkien", 1937);
Book anotherName = first;
```

The second line does not create another Book. Both variables refer to the same object. Think of two labels pointing to the same card.

This matters when your ShelfEntry holds a Book and the repository holds a ShelfEntry: passing those objects around does not automatically make copies.

## Getters: reading private information

```java
public String getTitle() {
    return this.title;
}
```

Code outside Book can ask for the title through this public method:

```java
System.out.println(book.getTitle());
```

The remaining getters use the same pattern. getPublicationYear returns int rather than String.

| Method | Result type |
|---|---|
| getId() | String |
| getTitle() | String |
| getAuthor() | String |
| getPublicationYear() | int |

A getter reads information; it does not print it by itself. System.out.println prints the returned value.

## Why no setters on Book?

In our current implementation, Book exposes getters and no public setters. That keeps the example focused on creating book details and reading them. The fields are private but not declared final. We have not built a full validation or immutability design, and you do not need to assume every Book value is valid.

Reading status is stored elsewhere because it describes your relationship with the book, rather than the book's title or author.

## A Book is not automatically on a shelf

```java
Book book = new Book("sample-id", "The Hobbit", "J. R. R. Tolkien", 1937);
```

Only creates a Book. It does not change a repository. Storage happens through the add operation, now coordinated by BookshelfService.

## Files and names

A public class named Book lives in Book.java. ShelfEntry and Main have their own files. All currently share the package com.ketheleen.bookshelf, so they can refer to each other's types without imports for those same-package classes.

## Sample IDs versus real IDs

Earlier practice code used OL27448W for The Hobbit. The actual API response we received identifies The Hobbit as `/works/OL27482W`. Treat the earlier value as sample data, not the correct real identifier. The current source still contains the sample ID in the manually created book.

## Check your understanding

Does defining class Book create a particular book? No.

Does new Book(...) create a book object? Yes.

Does assigning one Book variable to another copy the object? No; it copies the reference.

Next: [Enums and shelf entries](04-reading-status-and-shelf-entry-explained.md).
