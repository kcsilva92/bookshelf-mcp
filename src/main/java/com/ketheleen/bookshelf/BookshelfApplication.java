package com.ketheleen.bookshelf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class BookshelfApplication {
  public static void main(String[] args) {
    SpringApplication.run(BookshelfApplication.class, args);
  }
  @Bean
  public BookshelfService bookshelfService() {
    return new BookshelfService(new InMemoryBookshelfRepository());
  }
}
