package com.hevlar.konney.application;

import com.hevlar.konney.infrastructure.entities.Book;

import java.util.List;

public interface IBookService {
    List<Book> listBooks();
    Book getBook(String label) throws BookkeepingException;
    Book createBook(Book book);
    Book updateBook(String label, Book book) throws BookkeepingException;
    void deleteBook(String label) throws BookkeepingException;
}
