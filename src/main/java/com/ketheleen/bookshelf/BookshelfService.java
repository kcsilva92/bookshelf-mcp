package com.ketheleen.bookshelf;

import java.util.List;

public class BookshelfService {
  // The repository handles storage; the service handles bookshelf rules.
  private final BookshelfRepository repository;

  public BookshelfService(BookshelfRepository repository) {
    if (repository == null) {
      throw new IllegalArgumentException("Repository is required.");
    }
    this.repository = repository;
  }

  // create a shelf entry and store it in the repository.
  public void addBook(Book book, ReadingStatus status) {
    if (book == null) {
      throw new IllegalArgumentException("Book is required.");
    }
    validateBookId(book.getId());
    validateStatus(status);

    // A book ID identifies a book, so it must appear only once on the shelf.
    if (repository.findByBookId(book.getId()) != null) {
      throw new IllegalArgumentException("Book already on shelf: " + book.getId());
    }

    ShelfEntry entry = new ShelfEntry(book, status);
    repository.add(entry);
  }

  // retrieve all shelf entries from repository
  public List<ShelfEntry> getBooks() {
    return repository.findAll();
  }

  // Update the existing entry rather than adding another entry to the shelf.
  public void updateStatus(String bookId, ReadingStatus status) {
    validateBookId(bookId);
    validateStatus(status);

    ShelfEntry entry = repository.findByBookId(bookId);
    if (entry == null) {
      throw new IllegalArgumentException("Book not found: " + bookId);
    }

    entry.setStatus(status);
  }

  // Private helpers keep the same validation rules in both operations.
  private void validateBookId(String bookId) {
    if (bookId == null || bookId.isBlank()) {
      throw new IllegalArgumentException("Book ID is required.");
    }
  }

  private void validateStatus(ReadingStatus status) {
    if (status == null) {
      throw new IllegalArgumentException("Reading status is required.");
    }
  }
}
