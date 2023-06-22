package com.hevlar.konney.presentation.dto;

import com.hevlar.konney.domain.entities.IAccount;
import com.hevlar.konney.domain.entities.IBook;
import com.hevlar.konney.domain.entities.IJournal;
import com.hevlar.konney.infrastructure.entities.Book;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookDto implements IBook {

    String label;
    LocalDate startDate;
    LocalDate endDate;
    LocalDate closeUntilDate;

    public Book toBook(){
        return Book.builder()
                .label(label)
                .startDate(startDate)
                .endDate(endDate)
                .closeUntilDate(closeUntilDate)
                .build();
    }

    public static BookDto fromBook(Book book){
        return new BookDto(
                book.getLabel(),
                book.getStartDate(),
                book.getEndDate(),
                book.getCloseUntilDate());

    }

    public static List<BookDto> fromBookList(List<Book> bookList){
        return bookList.stream()
                .map(BookDto::fromBook)
                .toList();
    }

    @Override
    public List<? extends IAccount> getAccountList() {
        return null;
    }

    @Override
    public List<? extends IJournal> getJournalList() {
        return null;
    }
}
