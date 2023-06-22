package com.hevlar.konney.presentation;

import com.hevlar.konney.domain.valueobjects.AccountGroup;
import com.hevlar.konney.domain.valueobjects.EntryType;
import com.hevlar.konney.presentation.dto.AccountDto;
import com.hevlar.konney.presentation.dto.BookDto;
import com.hevlar.konney.presentation.dto.JournalDto;
import com.hevlar.konney.presentation.dto.JournalEntryDto;
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
import static org.hamcrest.Matchers.equalToObject;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(SpringExtension.class)
public class JournalControllerIntegrationTest extends ControllerIntegrationTestBase{

    String booksUrl = "/books";
    String accountsUrl = "/accounts";
    BookDto book2022;
    AccountDto cash;
    AccountDto bank;
    AccountDto foodExpense;

    @BeforeEach
    void setUp() throws Exception {
        book2022 = BookDto.builder()
                .label("2022")
                .startDate(LocalDate.of(2022, 1, 1))
                .endDate(LocalDate.of(2022, 12, 31))
                .closeUntilDate(LocalDate.of(2022, 1, 1))
                .build();
        postIfNotExist(book2022, booksUrl, booksUrl + "/" + 2022);

        cash = AccountDto.builder()
                .accountId("CASH")
                .accountName("Cash")
                .accountGroup(AccountGroup.CurrentAsset)
                .openingBalance(new BigDecimal("100.00"))
                .currency("SGD")
                .openingDate(LocalDate.of(2022, 1, 1))
                .build();
        mvc.perform(MockMvcRequestBuilders.post(generateAccountsUrl("2022"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cash)))
                .andReturn();

        bank = AccountDto.builder()
                .accountId("BANK")
                .accountName("Bank")
                .accountGroup(AccountGroup.CurrentAsset)
                .openingBalance(new BigDecimal("1000.00"))
                .currency("SGD")
                .openingDate(LocalDate.of(2022, 1, 1))
                .build();
        mvc.perform(MockMvcRequestBuilders.post(generateAccountsUrl("2022"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bank)))
                .andReturn();

        foodExpense = AccountDto.builder()
                .accountId("FOOD")
                .accountName("Food")
                .accountGroup(AccountGroup.Expense)
                .build();
        mvc.perform(MockMvcRequestBuilders.post(generateAccountsUrl("2022"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(foodExpense)))
                .andReturn();

    }

    @Test
    void create_givenValidJournal_willReturnJournal() throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 1, 2), "test", LocalDate.of(2022, 1, 2), foodExpense, cash, BigDecimal.valueOf(10.00));
        MvcResult result = post(journalDto, generateJournalsUrl(book2022.getLabel()));
        assertHttpStatus(result, HttpStatus.CREATED);
        JournalDto resultJournal = (JournalDto) getResultObject(result, JournalDto.class);
        assertThat(resultJournal, equalToObject(journalDto));
    }

    @Test
    void create_givenJournalWithInvalidBook_willReturnNotFound() throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 1, 2), "test", LocalDate.of(2022, 1, 2), foodExpense, cash, BigDecimal.valueOf(10.00));
        MvcResult result = post(journalDto, generateJournalsUrl("1234"));
        assertHttpStatus(result, HttpStatus.NOT_FOUND);
    }

    private JournalDto createJournal(LocalDate txDate, String desc, LocalDate postDate, AccountDto debitAccount, AccountDto creditAccount, BigDecimal amount){
        JournalEntryDto debit = JournalEntryDto.builder()
                .entryType(EntryType.Debit)
                .accountId(debitAccount.getAccountId())
                .amount(amount)
                .build();
        JournalEntryDto credit = JournalEntryDto.builder()
                .entryType(EntryType.Credit)
                .accountId(creditAccount.getAccountId())
                .amount(amount)
                .build();
        return JournalDto.builder()
                .txDate(txDate)
                .postDate(postDate)
                .description(desc)
                .entries(List.of(debit, credit))
                .build();
    }
}
