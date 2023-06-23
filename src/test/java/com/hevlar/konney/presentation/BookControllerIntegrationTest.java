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
class BookControllerIntegrationTest extends ControllerIntegrationTestBase {
    BookDto book2024;
    BookDto book2023;
    BookDto book2022;
    BookDto bookWithoutStartDate;
    BookDto bookWithoutEndDate;
    @BeforeEach
    void setUp() {
        book2024 = new BookDto(
                "2024",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                LocalDate.of(2024, 1, 1));
        book2023 = new BookDto(
                "2023",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                LocalDate.of(2023, 1, 1));
        book2022 = new BookDto(
                "2022",
                LocalDate.of(2022, 1, 1),
                LocalDate.of(2022, 12, 31),
                LocalDate.of(2022, 2, 1));
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
        MvcResult result = post(book2024, booksUrl);
        assertHttpStatus(result, HttpStatus.CREATED);
        assertThat(getResultObject(result, BookDto.class), equalToObject(book2024));
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
        postIfNotExist(book2023, booksUrl, booksUrl + "/" + book2023.getLabel());
        MvcResult result = post(book2023, booksUrl);
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(5)
    void list_givenBooksExists_willReturnBooks() throws Exception {
        postIfNotExist(book2024, booksUrl, booksUrl + "/" + book2024.getLabel());
        postIfNotExist(book2023, booksUrl, booksUrl + "/" + book2023.getLabel());
        postIfNotExist(book2022, booksUrl, booksUrl + "/" + book2022.getLabel());

        MvcResult result = get(booksUrl);
        assertHttpStatus(result, HttpStatus.OK);

        List<BookDto> resultBookList = getResultObjectList(result, BookDto.class).stream().map(o -> (BookDto)o).toList();
        assertThat(resultBookList, hasItems(book2024, book2023, book2022));
    }

    @Test
    @Order(6)
    void get_givenBookExists_willReturnBook() throws Exception {
        postIfNotExist(book2023, booksUrl, booksUrl + "/" + book2023.getLabel());
        MvcResult result = get(booksUrl + "/" + book2023.getLabel());
        assertThat(getResultObject(result, BookDto.class), equalToObject(book2023));
    }

    @Test
    @Order(7)
    void get_givenBookDoesNotExists_willReturnNotFound() throws Exception {
        MvcResult result = get(booksUrl + "/1234");
        assertHttpStatus(result, HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(8)
    void update_givenBookExists_willUpdate() throws Exception {
        postIfNotExist(book2024, booksUrl, booksUrl + "/" + book2023.getLabel());
        BookDto book11 = BookDto.builder()
                .label("2024")
                .startDate(LocalDate.of(2023,4, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .closeUntilDate(LocalDate.of(2023, 4, 1))
                .build();
        MvcResult result = put(book11, booksUrl + "/2024");
        assertHttpStatus(result, HttpStatus.OK);
        assertThat(getResultObject(result, BookDto.class), equalToObject(book11));
    }

    @Test
    @Order(9)
    void update_givenBookDoesNotExist_willReturnNotFound() throws Exception {
        MvcResult result = put(book2022, booksUrl + "/1234");
        assertHttpStatus(result, HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(10)
    void update_givenBookWithoutStartDate_willReturnBadRequest() throws Exception {
        postIfNotExist(book2023, booksUrl, booksUrl + "/" + book2023.getLabel());
        MvcResult result = put(bookWithoutStartDate, booksUrl + "/" + book2023.getLabel());
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(11)
    void update_givenBookWithoutEndDate_willReturnBadRequest() throws Exception {
        postIfNotExist(book2023, booksUrl, booksUrl + "/" + book2023.getLabel());
        MvcResult result = put(bookWithoutEndDate, booksUrl + "/" + book2023.getLabel());
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
    }

}
