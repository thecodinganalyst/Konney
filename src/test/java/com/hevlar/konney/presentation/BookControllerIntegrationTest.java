package com.hevlar.konney.presentation;

import com.hevlar.konney.infrastructure.entities.Book;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
class BookControllerIntegrationTest extends ControllerIntegrationTestBase<Book> {

    String booksUrl = "/books";

    Book book1;
    Book book2;
    Book bookWithoutStartDate;
    Book bookWithoutEndDate;
    @BeforeEach
    void setUp() {
        book1 = Book.builder()
                .label("2023")
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .build();
        book2 = Book.builder()
                .label("2022")
                .startDate(LocalDate.of(2022, 1, 1))
                .endDate(LocalDate.of(2022, 12, 31))
                .build();
        bookWithoutStartDate = Book.builder()
                .label("2023")
                .endDate(LocalDate.of(2023, 12, 31))
                .build();
        bookWithoutEndDate = Book.builder()
                .label("2023")
                .startDate(LocalDate.of(2023, 1, 1))
                .build();
    }

    @Test
    @Order(1)
    void create_givenValidBook_willReturnBook() throws Exception {
        MvcResult result = post(book1, booksUrl);
        assertHttpStatus(result, HttpStatus.CREATED);
        assertThat(getResultObject(result, Book.class), equalToObject(book1));
    }

    @Test
    @Order(2)
    void create_givenBookWithoutStartDate_willReturnBadRequest() throws Exception {
        MvcResult result = post(bookWithoutStartDate, booksUrl);
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(3)
    void create_givenBookWithoutEndDate_willReturnBadRequest() throws Exception {
        MvcResult result = post(bookWithoutEndDate, booksUrl);
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(4)
    void create_givenBookAlreadyExists_willReturnBadRequest() throws Exception {
        post(book1, booksUrl);
        MvcResult result = post(book1, booksUrl);
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(5)
    void list_given2BooksExists_willReturn2Books() throws Exception {
        post(book1, booksUrl);
        post(book2, booksUrl);

        MvcResult result = get(booksUrl);
        assertHttpStatus(result, HttpStatus.OK);

        List<Book> resultBookList = getResultObjectList(result, Book.class);
        assertThat(resultBookList, hasSize(2));
        assertThat(resultBookList.get(0), equalToObject(book1));
        assertThat(resultBookList.get(1), equalToObject(book2));
    }

    @Test
    @Order(6)
    void get_givenBookExists_willReturnBook() throws Exception {
        post(book1, booksUrl);
        MvcResult result = get(booksUrl + "/" + book1.getLabel());
        assertThat(getResultObject(result, Book.class), equalToObject(book1));
    }

    @Test
    @Order(7)
    void get_givenBookDoesNotExists_willReturnBadRequest() throws Exception {
        MvcResult result = get(booksUrl + "/" + book1.getLabel());
        assertHttpStatus(result, HttpStatus.OK);
    }

    @Test
    @Order(8)
    void update_givenBookExists_willUpdate() throws Exception {
        post(book1, booksUrl);
        Book book11 = Book.builder()
                .label("2023")
                .startDate(LocalDate.of(2023,4, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .build();
        MvcResult result = patch(book11, booksUrl + "/" + 2023);
        assertHttpStatus(result, HttpStatus.OK);
        assertThat(getResultObject(result, Book.class), equalToObject(book11));
    }

    @Test
    @Order(9)
    void update_givenBookDoesNotExist_willReturnBadRequest() throws Exception {
        MvcResult result = patch(book2, booksUrl + "/1234");
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
    }

}
