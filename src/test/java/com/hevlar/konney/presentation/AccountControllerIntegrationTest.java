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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToObject;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
class AccountControllerIntegrationTest extends ControllerIntegrationTestBase<AccountDto>{

    String booksUrl = "/books";
    String accountsUrl = "/accounts";
    BookDto book;
    AccountDto cash;
    AccountDto foodExpense;
    AccountDto invalidBalanceSheetAccount;
    AccountDto invalidIncomeStatementAccount;

    @BeforeEach
    void setUp() throws Exception {
        book = BookDto.builder()
                .label("2022")
                .startDate(LocalDate.of(2022, 1, 1))
                .endDate(LocalDate.of(2022, 12, 31))
                .build();
        mvc.perform(MockMvcRequestBuilders.post(booksUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andReturn();

        cash = AccountDto.builder()
                .accountId("CASH")
                .accountName("Cash")
                .accountGroup(AccountGroup.CurrentAsset)
                .openingBalance(new BigDecimal("100.00"))
                .currency("SGD")
                .openingDate(LocalDate.of(2022, 1, 1))
                .build();

        foodExpense = AccountDto.builder()
                .accountId("FOOD")
                .accountName("Food")
                .accountGroup(AccountGroup.Expense)
                .build();

        invalidBalanceSheetAccount = AccountDto.builder()
                .accountId("INVALID_BS_ACCOUNT")
                .accountName("Invalid BS Account")
                .accountGroup(AccountGroup.CurrentAsset)
                .openingBalance(new BigDecimal("100.00"))
                .currency("SGD")
                .build();

        invalidIncomeStatementAccount = AccountDto.builder()
                .accountId("INVALID_IS_ACCOUNT")
                .accountName("Invalid IS Account")
                .accountGroup(AccountGroup.Expense)
                .currency("SGD")
                .build();
    }

    @Test
    @Order(1)
    void create_givenValidAccountAndBookLabel_willCreateAccount() throws Exception {
        MvcResult result = post(cash, generateAccountsUrl(book.getLabel()));
        assertHttpStatus(result, HttpStatus.CREATED);
        AccountDto resultAccount = getResultObject(result, AccountDto.class);
        assertThat(resultAccount, equalToObject(cash));
    }

    @Test
    @Order(2)
    void create_givenInvalidAccount_willReturnBadRequest() throws Exception {
        MvcResult result = post(invalidBalanceSheetAccount, generateAccountsUrl(book.getLabel()));
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);

        result = post(invalidIncomeStatementAccount, generateAccountsUrl(book.getLabel()));
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
    }

    @Test
    void list() {
    }

    @Test
    void get() {
    }

    @Test
    void update() {
    }

    private String generateAccountsUrl(String label, String accountId){
        return booksUrl + "/" + label + "/" + accountsUrl + "/" + accountId;
    }

    private String generateAccountsUrl(String label){
        return booksUrl + "/" + label + "/" + accountsUrl;
    }
}
