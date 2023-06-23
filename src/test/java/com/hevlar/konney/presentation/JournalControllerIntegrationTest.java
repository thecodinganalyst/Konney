package com.hevlar.konney.presentation;

import com.hevlar.konney.domain.valueobjects.AccountGroup;
import com.hevlar.konney.domain.valueobjects.EntryType;
import com.hevlar.konney.presentation.dto.AccountDto;
import com.hevlar.konney.presentation.dto.BookDto;
import com.hevlar.konney.presentation.dto.JournalDto;
import com.hevlar.konney.presentation.dto.JournalEntryDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
@ExtendWith(SpringExtension.class)
public class JournalControllerIntegrationTest extends ControllerIntegrationTestBase{

    String booksUrl = "/books";
    String accountsUrl = "/accounts";

    AccountDto cashId = AccountDto.builder().accountId("JCASH").build();
    AccountDto foodExpenseId = AccountDto.builder().accountId("JFOOD").build();
    AccountDto bankId = AccountDto.builder().accountId("JBANK").build();

    @BeforeEach
    void setUp() throws Exception {
        BookDto book2022 = BookDto.builder()
                .label("J2022")
                .startDate(LocalDate.of(2022, 1, 1))
                .endDate(LocalDate.of(2022, 12, 31))
                .closeUntilDate(LocalDate.of(2022, 2, 1))
                .build();
        postIfNotExist(book2022, booksUrl, booksUrl + "/J2022");

        BookDto book2025 = BookDto.builder()
                .label("J2025")
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .closeUntilDate(LocalDate.of(2025, 2, 1))
                .build();
        postIfNotExist(book2025, booksUrl, booksUrl + "/J2025");

        AccountDto cash = AccountDto.builder()
                .accountId("JCASH")
                .accountName("Cash")
                .accountGroup(AccountGroup.CurrentAsset)
                .openingBalance(new BigDecimal("100.00"))
                .currency("SGD")
                .openingDate(LocalDate.of(2022, 1, 1))
                .build();
        postIfNotExist(cash, generateAccountsUrl("J2022"), generateAccountsUrl("J2022", "JCASH"));

        AccountDto bank = AccountDto.builder()
                .accountId("JBANK")
                .accountName("Bank")
                .accountGroup(AccountGroup.CurrentAsset)
                .openingBalance(new BigDecimal("1000.00"))
                .currency("SGD")
                .openingDate(LocalDate.of(2022, 1, 1))
                .build();
        postIfNotExist(bank, generateAccountsUrl("J2022"), generateAccountsUrl("J2022", "JBANK"));

        AccountDto foodExpense = AccountDto.builder()
                .accountId("JFOOD")
                .accountName("Food")
                .accountGroup(AccountGroup.Expense)
                .build();
        postIfNotExist(foodExpense, generateAccountsUrl("J2022"), generateAccountsUrl("J2022", "JFOOD"));


    }

    @Test
    void create_givenValidJournal_willReturnJournal() throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 3, 2), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.CREATED);
        JournalDto resultJournal = (JournalDto) getResultObject(result, JournalDto.class);
        assertThat(resultJournal, equalToObject(journalDto));
    }

    @Test
    void create_givenJournalWithInvalidBook_willReturnNotFound() throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 1, 2), "test", LocalDate.of(2022, 1, 2), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("1234"));
        assertHttpStatus(result, HttpStatus.NOT_FOUND);
    }

    @ParameterizedTest
    @ValueSource(strings = {"2021-12-31", "2023-01-01"})
    void create_givenJournalWithTxDateBeforeAfterBookDate_willReturnBadRequest(LocalDate txDate) throws Exception {
        JournalDto journalDto = createJournal(txDate, "test", LocalDate.of(2022, 3, 1), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(strings = {"2021-12-31", "2023-01-01"})
    void create_givenJournalWithPostDateBeforeAfterBookDate_willReturnBadRequest(LocalDate postDate) throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 3, 1), "test", postDate, foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
    }

    @Test
    void create_givenJournalWithTxDateBeforeCloseDate_willReturnBadRequest() throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 1, 2), "test", LocalDate.of(2022, 3, 2), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
    }

    @Test
    void create_givenJournalWithPostDateBeforeCloseDate_willReturnBadRequest() throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 1, 2), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
    }

    @Test
    void create_givenJournalWithInvalidAccount_willReturnBadRequest() throws Exception {
        AccountDto invalid = AccountDto.builder().accountId("1234").build();
        JournalDto journalDto = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 1, 2), invalid, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
    }

    @Test
    void create_givenJournalWithUnbalancedEntries_willReturnBadRequest() throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 1, 2), foodExpenseId, new BigDecimal("11.00"), cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
    }

    @Test
    void list_willReturnJournalList() throws Exception {
        JournalDto journalDto2022 = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 3, 2), foodExpenseId, cashId, new BigDecimal("100.00"));
        post(journalDto2022, generateJournalsUrl("J2022"));

        MvcResult result = get(generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.OK);
        List<JournalDto> journalDtoList = getResultObjectList(result, JournalDto.class).stream().map(o -> (JournalDto)o).toList();
        assertThat(journalDtoList.get(0).getTxDate(), is(LocalDate.of(2022, 3, 2)));
        assertThat(journalDtoList.get(0).getPostDate(), is(LocalDate.of(2022, 3, 2)));
    }

    @Test
    void get_willReturnJournal() throws Exception {
        JournalDto journalDto2022 = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 3, 2), foodExpenseId, cashId, new BigDecimal("100.00"));
        MvcResult postResult = post(journalDto2022, generateJournalsUrl("J2022"));
        JournalDto postedJournalDto = (JournalDto) getResultObject(postResult, JournalDto.class);

        MvcResult getResult = get(generateJournalsUrl("J2022", postedJournalDto.getJournalId()));
        assertHttpStatus(getResult, HttpStatus.OK);
        JournalDto getJournalDto = (JournalDto) getResultObject(getResult, JournalDto.class);

        assertThat(getJournalDto, is(postedJournalDto));
    }

    @Test
    void update_givenValidJournal_willReturnJournal() throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 3, 2), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.CREATED);
        JournalDto insertedJournal = (JournalDto) getResultObject(result, JournalDto.class);

        JournalDto journalUpdate = createJournal(LocalDate.of(2022, 3, 3), "test again", LocalDate.of(2022, 3, 3), cashId, new BigDecimal("20.00"), foodExpenseId, new BigDecimal("20.00"));
        MvcResult updateResult = put(journalUpdate, generateJournalsUrl("J2022", insertedJournal.getJournalId()));
        assertHttpStatus(updateResult, HttpStatus.OK);
        JournalDto updatedJournal = (JournalDto) getResultObject(updateResult, JournalDto.class);

        assertThat(updatedJournal, is(journalUpdate));
    }



    private JournalDto createJournal(LocalDate txDate, String desc, LocalDate postDate, AccountDto debitAccount, AccountDto creditAccount, BigDecimal amount){
        return createJournal(txDate, desc, postDate, debitAccount, amount, creditAccount, amount);
    }

    private JournalDto createJournal(LocalDate txDate, String desc, LocalDate postDate, AccountDto debitAccount, BigDecimal debitAmount, AccountDto creditAccount, BigDecimal creditAmount){
        JournalEntryDto debit = JournalEntryDto.builder()
                .entryType(EntryType.Debit)
                .accountId(debitAccount.getAccountId())
                .amount(debitAmount)
                .build();
        JournalEntryDto credit = JournalEntryDto.builder()
                .entryType(EntryType.Credit)
                .accountId(creditAccount.getAccountId())
                .amount(creditAmount)
                .build();
        return JournalDto.builder()
                .txDate(txDate)
                .postDate(postDate)
                .description(desc)
                .entries(List.of(debit, credit))
                .build();
    }
}
