package com.hevlar.konney.presentation;

import com.hevlar.konney.domain.valueobjects.AccountGroup;
import com.hevlar.konney.presentation.dto.AccountDto;
import com.hevlar.konney.presentation.dto.BookDto;
import com.hevlar.konney.presentation.dto.JournalDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
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
    BookDto bookWithoutCloseUntilDate;
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
                .closeUntilDate(LocalDate.of(2023, 2, 1))
                .build();
        bookWithoutEndDate = BookDto.builder()
                .label("2023")
                .startDate(LocalDate.of(2023, 1, 1))
                .closeUntilDate(LocalDate.of(2023, 2, 1))
                .build();
        bookWithoutCloseUntilDate = BookDto.builder()
                .label("2023")
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 12, 31))
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
        List<ErrorDto> errorDtoList = getResultObjectList(result, ErrorDto.class)
                .stream()
                .map(e -> (ErrorDto)e)
                .toList();
        assertThat(errorDtoList.size(), is(1));
        ErrorDto expected = new ErrorDto("bookDto", "startDate", "null", "must not be null");
        assertThat(errorDtoList.get(0), is(expected));
    }

    @Test
    @Order(3)
    void create_givenBookWithoutEndDate_willReturnBadRequest() throws Exception {
        MvcResult result = post(bookWithoutEndDate, booksUrl);
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
        List<ErrorDto> errorDtoList = getResultObjectList(result, ErrorDto.class)
                .stream()
                .map(e -> (ErrorDto)e)
                .toList();
        assertThat(errorDtoList.size(), is(1));
        ErrorDto expected = new ErrorDto("bookDto", "endDate", "null", "must not be null");
        assertThat(errorDtoList.get(0), is(expected));
    }

    @Test
    @Order(4)
    void create_givenBookWithoutCloseUntilDate_willReturnBadRequest() throws Exception {
        MvcResult result = post(bookWithoutCloseUntilDate, booksUrl);
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
        List<ErrorDto> errorDtoList = getResultObjectList(result, ErrorDto.class)
                .stream()
                .map(e -> (ErrorDto)e)
                .toList();
        assertThat(errorDtoList.size(), is(1));
        ErrorDto expected = new ErrorDto("bookDto", "closeUntilDate", "null", "must not be null");
        assertThat(errorDtoList.get(0), is(expected));
    }

    @Test
    @Order(5)
    void create_givenBookAlreadyExists_willReturnBadRequest() throws Exception {
        postIfNotExist(book2023, booksUrl, booksUrl + "/" + book2023.getLabel());
        MvcResult result = post(book2023, booksUrl);
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
        assertHttpMessage(result, "Book with the same label already exists");
    }

    @Test
    @Order(6)
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
    @Order(7)
    void get_givenBookExists_willReturnBook() throws Exception {
        postIfNotExist(book2023, booksUrl, booksUrl + "/" + book2023.getLabel());
        MvcResult result = get(booksUrl + "/" + book2023.getLabel());
        assertThat(getResultObject(result, BookDto.class), equalToObject(book2023));
    }

    @Test
    @Order(8)
    void get_givenBookDoesNotExists_willReturnNotFound() throws Exception {
        MvcResult result = get(booksUrl + "/1234");
        assertHttpStatus(result, HttpStatus.NOT_FOUND);
        assertHttpMessage(result, "Book not found");
    }

    @Test
    @Order(9)
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
    @Order(10)
    void update_givenBookDoesNotExist_willReturnNotFound() throws Exception {
        MvcResult result = put(book2022, booksUrl + "/1234");
        assertHttpStatus(result, HttpStatus.NOT_FOUND);
        assertHttpMessage(result, "Book not found");
    }

    @Test
    @Order(11)
    void update_givenBookWithoutStartDate_willReturnBadRequest() throws Exception {
        postIfNotExist(book2023, booksUrl, booksUrl + "/" + book2023.getLabel());
        MvcResult result = put(bookWithoutStartDate, booksUrl + "/" + book2023.getLabel());
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);

        List<ErrorDto> errorDtoList = getResultObjectList(result, ErrorDto.class)
                .stream()
                .map(e -> (ErrorDto)e)
                .toList();
        assertThat(errorDtoList.size(), is(1));
        ErrorDto expected = new ErrorDto("bookDto", "startDate", "null", "must not be null");
        assertThat(errorDtoList.get(0), is(expected));
    }

    @Test
    @Order(12)
    void update_givenBookWithoutEndDate_willReturnBadRequest() throws Exception {
        postIfNotExist(book2023, booksUrl, booksUrl + "/" + book2023.getLabel());
        MvcResult result = put(bookWithoutEndDate, booksUrl + "/" + book2023.getLabel());
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);

        List<ErrorDto> errorDtoList = getResultObjectList(result, ErrorDto.class)
                .stream()
                .map(e -> (ErrorDto)e)
                .toList();
        assertThat(errorDtoList.size(), is(1));
        ErrorDto expected = new ErrorDto("bookDto", "endDate", "null", "must not be null");
        assertThat(errorDtoList.get(0), is(expected));
    }

    @Test
    @Order(13)
    void update_givenBookWithoutCloseUntilDate_willReturnBadRequest() throws Exception {
        postIfNotExist(book2023, booksUrl, booksUrl + "/" + book2023.getLabel());
        MvcResult result = put(bookWithoutCloseUntilDate, booksUrl + "/" + book2023.getLabel());
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);

        List<ErrorDto> errorDtoList = getResultObjectList(result, ErrorDto.class)
                .stream()
                .map(e -> (ErrorDto)e)
                .toList();
        assertThat(errorDtoList.size(), is(1));
        ErrorDto expected = new ErrorDto("bookDto", "closeUntilDate", "null", "must not be null");
        assertThat(errorDtoList.get(0), is(expected));
    }

    @Test
    @Order(14)
    void update_givenBookWithCloseUntilDateBeforeStartDate_willReturnBadRequest() throws Exception {
        postIfNotExist(book2023, booksUrl, booksUrl + "/" + book2023.getLabel());
        BookDto invalid = new BookDto(
                "2023",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                LocalDate.of(2022, 1, 1));

        MvcResult result = put(invalid, booksUrl + "/" + book2023.getLabel());
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
        assertHttpMessage(result, "Close until date cannot be before book start date");
    }

    @Test
    @Order(15)
    void update_givenBookWithCloseUntilDateAfterEndDate_willReturnBadRequest() throws Exception {
        postIfNotExist(book2023, booksUrl, booksUrl + "/" + book2023.getLabel());
        BookDto invalid = new BookDto(
                "2023",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                LocalDate.of(2024, 1, 1));

        MvcResult result = put(invalid, booksUrl + "/" + book2023.getLabel());
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
        assertHttpMessage(result, "Close until date cannot be after book end date");
    }

    @Test
    @Order(16)
    void update_givenBookWithAccountBeforeBookStartDate_willReturnBadRequest() throws Exception {
        BookDto bookWithAccountBeforeStartDate = BookDto.builder()
                .label("bookWithAccountBeforeStartDate")
                .startDate(LocalDate.of(2022, 1, 1))
                .endDate(LocalDate.of(2022, 12, 31))
                .closeUntilDate(LocalDate.of(2022, 3, 1))
                .build();
        MvcResult result = postIfNotExist(bookWithAccountBeforeStartDate, booksUrl, booksUrl + "/" + bookWithAccountBeforeStartDate.getLabel());
        assertHttpStatus(result, HttpStatus.CREATED);
        bookWithAccountBeforeStartDate = (BookDto) getResultObject(result, BookDto.class);

        AccountDto accountTest = AccountDto.builder()
                .accountId("accountTest")
                .accountName("accountTest")
                .accountGroup(AccountGroup.CurrentAsset)
                .currency("SGD")
                .openingBalance(new BigDecimal("100.00"))
                .openingDate(LocalDate.of(2022, 1, 2))
                .build();
        result = post(accountTest, generateAccountsUrl(bookWithAccountBeforeStartDate.getLabel()));
        assertHttpStatus(result, HttpStatus.CREATED);

        bookWithAccountBeforeStartDate.setStartDate(LocalDate.of(2022, 2, 1));
        result = put(bookWithAccountBeforeStartDate, booksUrl + "/" + bookWithAccountBeforeStartDate.getLabel());
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
        assertHttpMessage(result, "Cannot modify book start date as there are accounts with opening date before book start date");
    }

    @Test
    @Order(17)
    void update_givenBookWithAccountAfterBookEndDate_willReturnBadRequest() throws Exception {
        BookDto bookWithAccountAfterEndDate = BookDto.builder()
                .label("bookWithAccountAfterEndDate")
                .startDate(LocalDate.of(1999, 1, 1))
                .endDate(LocalDate.of(1999, 12, 31))
                .closeUntilDate(LocalDate.of(1999, 3, 1))
                .build();
        MvcResult result = postIfNotExist(bookWithAccountAfterEndDate, booksUrl, booksUrl + "/" + bookWithAccountAfterEndDate.getLabel());
        assertHttpStatus(result, HttpStatus.CREATED);
        bookWithAccountAfterEndDate = (BookDto) getResultObject(result, BookDto.class);

        AccountDto accountTest = AccountDto.builder()
                .accountId("accountTest")
                .accountName("accountTest")
                .accountGroup(AccountGroup.CurrentAsset)
                .currency("SGD")
                .openingBalance(new BigDecimal("100.00"))
                .openingDate(LocalDate.of(1999, 12, 1))
                .build();
        result = post(accountTest, generateAccountsUrl(bookWithAccountAfterEndDate.getLabel()));
        assertHttpStatus(result, HttpStatus.CREATED);

        bookWithAccountAfterEndDate.setEndDate(LocalDate.of(1999, 11, 1));
        result = put(bookWithAccountAfterEndDate, booksUrl + "/" + bookWithAccountAfterEndDate.getLabel());
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
        assertHttpMessage(result, "Cannot modify book end date as there are accounts with opening date after book end date");
    }

    @Test
    @Order(18)
    void update_givenBookWithJournalTxDateAfterBookEndDate_willReturnBadRequest() throws Exception {
        BookDto bookWithJournalTxDateAfterEndDate = BookDto.builder()
                .label("bookWithJournalTxDateAfterEndDate")
                .startDate(LocalDate.of(2022, 1, 1))
                .endDate(LocalDate.of(2022, 12, 31))
                .closeUntilDate(LocalDate.of(2022, 3, 1))
                .build();
        MvcResult result = postIfNotExist(bookWithJournalTxDateAfterEndDate, booksUrl, booksUrl + "/" + bookWithJournalTxDateAfterEndDate.getLabel());
        assertHttpStatus(result, HttpStatus.CREATED);
        bookWithJournalTxDateAfterEndDate = (BookDto) getResultObject(result, BookDto.class);

        AccountDto accountFood = AccountDto.builder()
                .accountId("FOOD")
                .accountName("Food")
                .accountGroup(AccountGroup.Expense)
                .build();
        result = post(accountFood, generateAccountsUrl(bookWithJournalTxDateAfterEndDate.getLabel()));
        assertHttpStatus(result, HttpStatus.CREATED);

        AccountDto accountCash = AccountDto.builder()
                .accountId("CASH")
                .accountName("Cash")
                .accountGroup(AccountGroup.CurrentAsset)
                .currency("SGD")
                .openingBalance(new BigDecimal("100.00"))
                .openingDate(LocalDate.of(2022, 1, 1))
                .build();
        result = post(accountCash, generateAccountsUrl(bookWithJournalTxDateAfterEndDate.getLabel()));
        assertHttpStatus(result, HttpStatus.CREATED);

        JournalDto journalDto = createJournal(LocalDate.of(2022, 12, 1), "test", LocalDate.of(2022, 12, 1), accountFood, accountCash, new BigDecimal("10.00"));
        result = post(journalDto, generateJournalsUrl(bookWithJournalTxDateAfterEndDate.getLabel()));
        assertHttpStatus(result, HttpStatus.CREATED);

        bookWithJournalTxDateAfterEndDate.setEndDate(LocalDate.of(2022, 10, 1));
        result = put(bookWithJournalTxDateAfterEndDate, booksUrl + "/" + bookWithJournalTxDateAfterEndDate.getLabel());
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
        assertHttpMessage(result, "Cannot modify book end date as there are journals with transaction date or post date after book end date");
    }

    @Test
    @Order(19)
    void delete_givenBookExistsWithoutAccounts_willReturnOK() throws Exception {
        BookDto bookToDelete = BookDto.builder()
                .label("2999")
                .startDate(LocalDate.of(2999,4, 1))
                .endDate(LocalDate.of(2999, 12, 31))
                .closeUntilDate(LocalDate.of(2999, 4, 1))
                .build();
        MvcResult result = post(bookToDelete, booksUrl);
        assertHttpStatus(result, HttpStatus.CREATED);

        result = delete(booksUrl + "/" + bookToDelete.getLabel());
        assertHttpStatus(result, HttpStatus.OK);
    }

    @Test
    @Order(20)
    void delete_givenBookDoesNotExists_willReturnNotFound() throws Exception {
        MvcResult result = delete(booksUrl + "/1234");
        assertHttpStatus(result, HttpStatus.NOT_FOUND);
        assertHttpMessage(result, "Book not found");
    }

    @Test
    @Order(21)
    void delete_givenBookWithAccount_willReturnBadRequest() throws Exception {
        BookDto bookWithAccount = BookDto.builder()
                .label("bookWithAccount")
                .startDate(LocalDate.of(1999, 1, 1))
                .endDate(LocalDate.of(1999, 12, 31))
                .closeUntilDate(LocalDate.of(1999, 3, 1))
                .build();
        MvcResult result = postIfNotExist(bookWithAccount, booksUrl, booksUrl + "/" + bookWithAccount.getLabel());
        assertHttpStatus(result, HttpStatus.CREATED);
        bookWithAccount = (BookDto) getResultObject(result, BookDto.class);

        AccountDto accountTest = AccountDto.builder()
                .accountId("accountTest")
                .accountName("accountTest")
                .accountGroup(AccountGroup.CurrentAsset)
                .currency("SGD")
                .openingBalance(new BigDecimal("100.00"))
                .openingDate(LocalDate.of(1999, 12, 1))
                .build();
        result = post(accountTest, generateAccountsUrl(bookWithAccount.getLabel()));
        assertHttpStatus(result, HttpStatus.CREATED);

        result = delete(booksUrl + "/bookWithAccount");
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
        assertHttpMessage(result, "Cannot delete book when accounts exists");
    }
}
