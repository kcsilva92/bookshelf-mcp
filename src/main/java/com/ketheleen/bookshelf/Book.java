package com.ketheleen.bookshelf;

public class Book {

  // fields - book objects
  private final String id;
  private final String title;
  private final String author;
  // Zero represents an unknown publication year.
  private final int publicationYear;

  // constructor
  public Book(String id, String title, String author, int publicationYear){
    this.id = id;
    this.title = title;
    this.author = author;
    this.publicationYear = publicationYear;
  }

  // getters - to allow other code to call it
  public String getTitle(){
    return this.title;
  }
  public String getId(){
    return this.id;
  }
  public String getAuthor(){
    return this.author;
  }
  public int getPublicationYear(){
    return this.publicationYear;
  }

}
