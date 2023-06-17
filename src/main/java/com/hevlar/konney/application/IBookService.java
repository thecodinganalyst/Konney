package com.hevlar.konney.application;

import com.hevlar.konney.infrastructure.entities.Book;

import java.util.List;

public interface IBookService {
    List<Book> listBooks();
    Book getBook(String label);
    Book createBook(Book book);
    Book updateBook(String label, Book book) throws Exception;
    void deleteBook(String label) throws Exception;
}
