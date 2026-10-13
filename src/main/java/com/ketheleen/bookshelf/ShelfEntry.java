package com.ketheleen.bookshelf;

public class ShelfEntry {
  private final Book book;
  private ReadingStatus status; 

  // constructor
  public ShelfEntry(Book book, ReadingStatus status){
    this.book = book;
    this.status = status;
  }

  // getters
  public Book getBook(){
    return this.book;
  }
  public ReadingStatus getStatus(){
    return this.status;
  }

  //setter: status to change whenever user starts reading
  public void setStatus(ReadingStatus status){
    this.status = status;
  }
  
}
