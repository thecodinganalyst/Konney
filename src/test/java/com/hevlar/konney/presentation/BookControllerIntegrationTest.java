package com.hevlar.konney.presentation;

import com.hevlar.konney.presentation.dto.BookDto;
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
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
class BookControllerIntegrationTest extends ControllerIntegrationTestBase<BookDto> {

    String booksUrl = "/books";
    BookDto book1;
    BookDto book2;
    BookDto bookWithoutStartDate;
    BookDto bookWithoutEndDate;
    @BeforeEach
    void setUp() {

        book1 = new BookDto(
                "2023",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31));
        book2 = new BookDto(
                "2022",
                LocalDate.of(2022, 1, 1),
                LocalDate.of(2022, 12, 31));
        bookWithoutStartDate = BookDto.builder()
                .label("2023")
                .endDate(LocalDate.of(2023, 12, 31))
                .build();
        bookWithoutEndDate = BookDto.builder()
                .label("2023")
                .startDate(LocalDate.of(2023, 1, 1))
                .build();
    }

    @Test
    @Order(1)
    void create_givenValidBook_willReturnBook() throws Exception {
        MvcResult result = post(book1, booksUrl);
        assertHttpStatus(result, HttpStatus.CREATED);
        assertThat(getResultObject(result, BookDto.class), equalToObject(book1));
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
        postIfNotExist(book1, booksUrl, booksUrl + "/" + book1.getLabel());
        MvcResult result = post(book1, booksUrl);
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(5)
    void list_given2BooksExists_willReturn2Books() throws Exception {
        postIfNotExist(book1, booksUrl, booksUrl + "/" + book1.getLabel());
        postIfNotExist(book2, booksUrl, booksUrl + "/" + book2.getLabel());

        MvcResult result = get(booksUrl);
        assertHttpStatus(result, HttpStatus.OK);

        List<BookDto> resultBookList = getResultObjectList(result, BookDto.class);
        assertThat(resultBookList, hasSize(2));
        assertThat(resultBookList, containsInAnyOrder(book1, book2));
    }

    @Test
    @Order(6)
    void get_givenBookExists_willReturnBook() throws Exception {
        postIfNotExist(book1, booksUrl, booksUrl + "/" + book1.getLabel());
        MvcResult result = get(booksUrl + "/" + book1.getLabel());
        assertThat(getResultObject(result, BookDto.class), equalToObject(book1));
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
        postIfNotExist(book1, booksUrl, booksUrl + "/" + book1.getLabel());
        BookDto book11 = BookDto.builder()
                .label("2023")
                .startDate(LocalDate.of(2023,4, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .build();
        MvcResult result = patch(book11, booksUrl + "/" + 2023);
        assertHttpStatus(result, HttpStatus.OK);
        assertThat(getResultObject(result, BookDto.class), equalToObject(book11));
    }

    @Test
    @Order(9)
    void update_givenBookDoesNotExist_willReturnBadRequest() throws Exception {
        MvcResult result = patch(book2, booksUrl + "/1234");
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
    }

}
