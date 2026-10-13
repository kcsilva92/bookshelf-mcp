package com.ketheleen.bookshelf;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookshelfServiceTest {
  private BookshelfService service;
  private Book book;

  // Give every test a fresh shelf so tests do not depend on each other.
  @BeforeEach
  void setUp() {
    service = new BookshelfService(new InMemoryBookshelfRepository());
    book = new Book("OL27448W", "The Hobbit", "J. R. R. Tolkien", 1937);
  }

  @Test
  void newShelfIsEmpty() {
    assertTrue(service.getBooks().isEmpty());
  }

  @Test
  void addingBookStoresItsDetailsAndStatus() {
    // Arrange in setUp, act here, then assert the expected result.
    service.addBook(book, ReadingStatus.TO_READ);

    List<ShelfEntry> shelf = service.getBooks();
    assertEquals(1, shelf.size());
    assertEquals("OL27448W", shelf.get(0).getBook().getId());
    assertEquals("The Hobbit", shelf.get(0).getBook().getTitle());
    assertEquals(ReadingStatus.TO_READ, shelf.get(0).getStatus());
  }

  @Test
  void duplicateIdIsRejectedWithoutChangingTheShelf() {
    service.addBook(book, ReadingStatus.TO_READ);
    // A different object with the same ID is still a duplicate book.
    Book duplicate = new Book(book.getId(), "Another title", "Another author", 2000);

    // The lambda () -> supplies the operation JUnit should check for an error.
    assertThrows(IllegalArgumentException.class,
        () -> service.addBook(duplicate, ReadingStatus.READ));

    assertEquals(1, service.getBooks().size());
    assertEquals("The Hobbit", service.getBooks().get(0).getBook().getTitle());
    assertEquals(ReadingStatus.TO_READ, service.getBooks().get(0).getStatus());
  }

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

  @Test
  void updatingMissingBookIsRejectedWithoutChangingExistingEntries() {
    service.addBook(book, ReadingStatus.TO_READ);

    assertThrows(IllegalArgumentException.class,
        () -> service.updateStatus("missing", ReadingStatus.READ));

    assertEquals(1, service.getBooks().size());
    assertEquals(ReadingStatus.TO_READ, service.getBooks().get(0).getStatus());
  }
}
