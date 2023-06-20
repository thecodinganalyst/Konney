package com.hevlar.konney.presentation;

import com.hevlar.konney.domain.valueobjects.AccountGroup;
import com.hevlar.konney.presentation.dto.AccountDto;
import com.hevlar.konney.presentation.dto.BookDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
class AccountControllerIntegrationTest extends ControllerIntegrationTestBase<AccountDto>{

    String booksUrl = "/books";
    String accountsUrl = "/accounts";
    BookDto book2022;
    BookDto book2023;
    AccountDto cash;
    AccountDto bank;
    AccountDto foodExpense;

    @BeforeEach
    void setUp() throws Exception {
        book2022 = BookDto.builder()
                .label("2022")
                .startDate(LocalDate.of(2022, 1, 1))
                .endDate(LocalDate.of(2022, 12, 31))
                .build();
        mvc.perform(MockMvcRequestBuilders.post(booksUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book2022)))
                .andReturn();

        book2023 = BookDto.builder()
                .label("2023")
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .build();
        mvc.perform(MockMvcRequestBuilders.post(booksUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book2022)))
                .andReturn();

        cash = AccountDto.builder()
                .accountId("CASH")
                .accountName("Cash")
                .accountGroup(AccountGroup.CurrentAsset)
                .openingBalance(new BigDecimal("100.00"))
                .currency("SGD")
                .openingDate(LocalDate.of(2022, 1, 1))
                .build();

        bank = AccountDto.builder()
                .accountId("BANK")
                .accountName("Bank")
                .accountGroup(AccountGroup.CurrentAsset)
                .openingBalance(new BigDecimal("1000.00"))
                .currency("SGD")
                .openingDate(LocalDate.of(2022, 1, 1))
                .build();

        foodExpense = AccountDto.builder()
                .accountId("FOOD")
                .accountName("Food")
                .accountGroup(AccountGroup.Expense)
                .build();
    }

    @Test
    @Order(1)
    void create_givenValidAccountAndBookLabel_willCreateAccount() throws Exception {
        MvcResult result = post(cash, generateAccountsUrl(book2022.getLabel()));
        assertHttpStatus(result, HttpStatus.CREATED);
        AccountDto resultAccount = getResultObject(result, AccountDto.class);
        assertThat(resultAccount, equalToObject(cash));
    }

    @Test
    @Order(2)
    void create_givenInvalidAccount_willReturnBadRequest() throws Exception {
        AccountDto invalidBalanceSheetAccount = AccountDto.builder()
                .accountId("INVALID_BS_ACCOUNT")
                .accountName("Invalid BS Account")
                .accountGroup(AccountGroup.CurrentAsset)
                .openingBalance(new BigDecimal("100.00"))
                .currency("SGD")
                .build();

        MvcResult result = post(invalidBalanceSheetAccount, generateAccountsUrl(book2022.getLabel()));
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);

        AccountDto invalidIncomeStatementAccount = AccountDto.builder()
                .accountId("INVALID_IS_ACCOUNT")
                .accountName("Invalid IS Account")
                .accountGroup(AccountGroup.Expense)
                .currency("SGD")
                .build();

        result = post(invalidIncomeStatementAccount, generateAccountsUrl(book2022.getLabel()));
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(3)
    void create_givenInvalidBook_willReturnNotFound() throws Exception {
        MvcResult result = post(cash, generateAccountsUrl("1234"));
        assertHttpStatus(result, HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(4)
    void create_givenInvalidOpeningDate_willReturnBadRequest() throws Exception {
        AccountDto invalidOpeningDateAccount1 = AccountDto.builder()
                .accountId("INVALID_DATE_ACCOUNT_1")
                .accountName("Invalid Date Account 1")
                .accountGroup(AccountGroup.CurrentAsset)
                .openingDate(LocalDate.of(2021, 12, 31))
                .openingBalance(new BigDecimal("100.00"))
                .currency("SGD")
                .build();

        MvcResult result = post(invalidOpeningDateAccount1, generateAccountsUrl("2022"));
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);

        AccountDto invalidOpeningDateAccount2 = AccountDto.builder()
                .accountId("INVALID_DATE_ACCOUNT_1")
                .accountName("Invalid Date Account 1")
                .accountGroup(AccountGroup.CurrentAsset)
                .openingDate(LocalDate.of(2023, 1, 31))
                .openingBalance(new BigDecimal("100.00"))
                .currency("SGD")
                .build();

        result = post(invalidOpeningDateAccount2, generateAccountsUrl("2022"));
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(5)
    void list_willReturnAccountsOnlyAvailableInBook() throws Exception {
        AccountDto account23 = AccountDto.builder()
                .accountId("test")
                .accountName("test")
                .accountGroup(AccountGroup.Expense)
                .build();
        postIfNotExist(account23, generateAccountsUrl("2023"), generateAccountsUrl("2023", "test"));
        postIfNotExist(cash, generateAccountsUrl("2022"), generateAccountsUrl("2022", cash.getAccountId()));
        postIfNotExist(foodExpense, generateAccountsUrl("2022"), generateAccountsUrl("2022", foodExpense.getAccountId()));
        postIfNotExist(bank, generateAccountsUrl("2022"), generateAccountsUrl("2022", bank.getAccountId()));

        MvcResult result = get(generateAccountsUrl("2022"));
        assertHttpStatus(result, HttpStatus.OK);
        List<AccountDto> resultAccountList = getResultObjectList(result, AccountDto.class);
        assertThat(resultAccountList.size(), is(3));
    }

    @Test
    @Order(6)
    void get_givenAccountExists_willReturnAccount() throws Exception {
        postIfNotExist(cash, generateAccountsUrl("2022"), generateAccountsUrl("2022", cash.getAccountId()));
        MvcResult result = get(generateAccountsUrl("2022", cash.getAccountId()));
        assertHttpStatus(result, HttpStatus.OK);
        assertThat(getResultObject(result, AccountDto.class), is(cash));
    }

    @Test
    @Order(7)
    void get_givenAccountDoesNotExists_willReturnNotFound() throws Exception {
        MvcResult result = get(generateAccountsUrl("2022", "NotExists"));
        assertHttpStatus(result, HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(8)
    void get_givenBookDoesNotExists_willReturnNotFound() throws Exception {
        MvcResult result = get(generateAccountsUrl("1234", "cash"));
        assertHttpStatus(result, HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(9)
    void update_givenValidAccount_willReturnUpdated() throws Exception{
        AccountDto updatedBank = new AccountDto(
                "BANK",
                "bank",
                AccountGroup.CurrentAsset,
                LocalDate.of(2022, 1, 2),
                "SGD",
                new BigDecimal("2000.00"));
        postIfNotExist(bank, generateAccountsUrl("2022"), generateAccountsUrl("2022", "BANK"));
        MvcResult result = put(updatedBank, generateAccountsUrl("2022", "BANK"));
        assertHttpStatus(result, HttpStatus.OK);
        assertThat(getResultObject(result, AccountDto.class), equalToObject(updatedBank));
    }

    @Test
    @Order(10)
    void update_givenInvalidAccount_willReturnBadRequest() throws Exception {
        postIfNotExist(cash, generateAccountsUrl("2022"), generateAccountsUrl("2022", "CASH"));
        AccountDto invalidBalanceSheetAccount = AccountDto.builder()
                .accountId("CASH")
                .accountName("Invalid BS Account")
                .accountGroup(AccountGroup.CurrentAsset)
                .openingBalance(new BigDecimal("100.00"))
                .currency("SGD")
                .build();

        MvcResult result = put(invalidBalanceSheetAccount, generateAccountsUrl("2022", "CASH"));
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);

        postIfNotExist(foodExpense, generateAccountsUrl("2022"), generateAccountsUrl("2022", "FOOD"));
        AccountDto invalidIncomeStatementAccount = AccountDto.builder()
                .accountId("FOOD")
                .accountName("Invalid IS Account")
                .accountGroup(AccountGroup.Expense)
                .currency("SGD")
                .build();

        result = put(invalidIncomeStatementAccount, generateAccountsUrl("2022", "FOOD"));
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(11)
    void update_givenInvalidBook_willReturnNotFound() throws Exception {
        MvcResult result = put(cash, generateAccountsUrl("1234", "CASH"));
        assertHttpStatus(result, HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(12)
    void update_givenInvalidOpeningDate_willReturnBadRequest() throws Exception {
        AccountDto invalidOpeningDateAccount1 = AccountDto.builder()
                .accountId("CASH")
                .accountName("Invalid Date Account 1")
                .accountGroup(AccountGroup.CurrentAsset)
                .openingDate(LocalDate.of(2021, 12, 31))
                .openingBalance(new BigDecimal("100.00"))
                .currency("SGD")
                .build();

        MvcResult result = put(invalidOpeningDateAccount1, generateAccountsUrl("2022", "CASH"));
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);

        AccountDto invalidOpeningDateAccount2 = AccountDto.builder()
                .accountId("CASH")
                .accountName("Invalid Date Account 1")
                .accountGroup(AccountGroup.CurrentAsset)
                .openingDate(LocalDate.of(2023, 1, 31))
                .openingBalance(new BigDecimal("100.00"))
                .currency("SGD")
                .build();

        result = put(invalidOpeningDateAccount2, generateAccountsUrl("2022", "CASH"));
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(13)
    void update_givenInvalidAccountId_willReturnNotFound() throws Exception {
        MvcResult result = put(cash, generateAccountsUrl("2022", "NONEXISTENTACCOUNTID"));
        assertHttpStatus(result, HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(14)
    void update_givenJournalExists_willReturnBadRequest() throws Exception {
        //TODO: add test
    }

    private String generateAccountsUrl(String label, String accountId){
        return booksUrl + "/" + label + "/" + accountsUrl + "/" + accountId;
    }

    private String generateAccountsUrl(String label){
        return booksUrl + "/" + label + "/" + accountsUrl;
    }
}
