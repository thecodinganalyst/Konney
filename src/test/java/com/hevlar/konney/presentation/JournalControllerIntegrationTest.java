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

    AccountDto cashId = AccountDto.builder().accountId("JCASH").build();
    AccountDto foodExpenseId = AccountDto.builder().accountId("JFOOD").build();
    AccountDto bankId = AccountDto.builder().accountId("JBANK").build();
    AccountDto usdBankId = AccountDto.builder().accountId("JUSDBANK").build();
    AccountDto transportExpenseId = AccountDto.builder().accountId("JTRANSPORT").build();

    BookDto book2022;

    @BeforeEach
    void setUp() throws Exception {
        book2022 = BookDto.builder()
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

        AccountDto transportExpense = AccountDto.builder()
                .accountId("JTRANSPORT")
                .accountName("Transport")
                .accountGroup(AccountGroup.Expense)
                .build();
        postIfNotExist(transportExpense, generateAccountsUrl("J2022"), generateAccountsUrl("J2022", "JTRANSPORT"));

        AccountDto usdBank = AccountDto.builder()
                .accountId("JUSDBANK")
                .accountName("JUsdBank")
                .accountGroup(AccountGroup.CurrentAsset)
                .openingBalance(new BigDecimal("1000.00"))
                .currency("USD")
                .openingDate(LocalDate.of(2022, 1, 1))
                .build();
        postIfNotExist(usdBank, generateAccountsUrl("J2022"), generateAccountsUrl("J2022", "JUSDBANK"));
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
        assertHttpMessage(result, "Book not found");
    }

    @ParameterizedTest
    @ValueSource(strings = {"2021-12-31", "2023-01-01"})
    void create_givenJournalWithTxDateBeforeAfterBookDate_willReturnBadRequest(LocalDate txDate) throws Exception {
        JournalDto journalDto = createJournal(txDate, "test", LocalDate.of(2022, 3, 1), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
        if(txDate.isBefore(book2022.getStartDate())){
            assertHttpMessage(result, "Journal transaction date cannot be before book start date");
        }
        if(txDate.isAfter(book2022.getEndDate())){
            assertHttpMessage(result, "Journal transaction date cannot be after book end date");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"2021-12-31", "2023-01-01"})
    void create_givenJournalWithPostDateBeforeAfterBookDate_willReturnBadRequest(LocalDate postDate) throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 3, 1), "test", postDate, foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);

        if(postDate.isBefore(book2022.getStartDate())){
            assertHttpMessage(result, "Journal post date cannot be before book start date");
        }
        if(postDate.isAfter(book2022.getEndDate())){
            assertHttpMessage(result, "Journal post date cannot be after book end date");
        }
    }

    @Test
    void create_givenJournalWithTxDateBeforeCloseDate_willReturnBadRequest() throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 1, 2), "test", LocalDate.of(2022, 3, 2), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
        assertHttpMessage(result, "Journal transaction date is before book close date");
    }

    @Test
    void create_givenJournalWithPostDateBeforeCloseDate_willReturnBadRequest() throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 1, 2), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
        assertHttpMessage(result, "Journal post date is before book close date");
    }

    @Test
    void create_givenJournalWithInvalidAccount_willReturnBadRequest() throws Exception {
        AccountDto invalid = AccountDto.builder().accountId("1234").build();
        JournalDto journalDto = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 3, 2), invalid, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
        assertHttpMessage(result, "Accounts [1234] not found");
    }

    @Test
    void create_givenJournalWithUnbalancedEntries_willReturnBadRequest() throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 3, 2), foodExpenseId, new BigDecimal("11.00"), cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
        assertHttpMessage(result, "Debit amount does not tally with credit amount");
    }

    @Test
    void create_givenJournalWithUnbalancedEntriesButDifferentCurrency_willReturnJournal() throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 3, 2), cashId, new BigDecimal("11.00"), usdBankId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.CREATED);
        JournalDto savedJournal = (JournalDto) getResultObject(result, JournalDto.class);
        assertThat(savedJournal, is(journalDto));
    }

    @Test
    void create_givenJournalWithMultipleEntriesOfSameCurrency_willReturnJournal() throws Exception {
        JournalEntryDto entryDto1 = JournalEntryDto.builder()
                .entryType(EntryType.Credit)
                .accountId(bankId.getAccountId())
                .amount(new BigDecimal("40.00"))
                .build();
        JournalEntryDto entryDto2 = JournalEntryDto.builder()
                .entryType(EntryType.Credit)
                .accountId(cashId.getAccountId())
                .amount(new BigDecimal("10.00"))
                .build();
        JournalEntryDto entryDto3 = JournalEntryDto.builder()
                .entryType(EntryType.Debit)
                .accountId(foodExpenseId.getAccountId())
                .amount(new BigDecimal("30.00"))
                .build();
        JournalEntryDto entryDto4 = JournalEntryDto.builder()
                .entryType(EntryType.Debit)
                .accountId(transportExpenseId.getAccountId())
                .amount(new BigDecimal("20.00"))
                .build();
        JournalDto journalDto = JournalDto.builder()
                .txDate(LocalDate.of(2022, 4, 1))
                .description("test")
                .postDate(LocalDate.of(2022, 4, 2))
                .entries(List.of(entryDto1, entryDto2, entryDto3, entryDto4))
                .build();

        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.CREATED);
        JournalDto savedJournal = (JournalDto) getResultObject(result, JournalDto.class);
        assertThat(savedJournal, is(journalDto));
    }

    @Test
    void create_givenJournalWithMultipleEntriesOfSameCurrencyButNotBalance_willReturnBadRequest() throws Exception {
        JournalEntryDto entryDto1 = JournalEntryDto.builder()
                .entryType(EntryType.Credit)
                .accountId(bankId.getAccountId())
                .amount(new BigDecimal("40.00"))
                .build();
        JournalEntryDto entryDto2 = JournalEntryDto.builder()
                .entryType(EntryType.Credit)
                .accountId(cashId.getAccountId())
                .amount(new BigDecimal("10.00"))
                .build();
        JournalEntryDto entryDto3 = JournalEntryDto.builder()
                .entryType(EntryType.Debit)
                .accountId(foodExpenseId.getAccountId())
                .amount(new BigDecimal("30.00"))
                .build();
        JournalEntryDto entryDto4 = JournalEntryDto.builder()
                .entryType(EntryType.Debit)
                .accountId(transportExpenseId.getAccountId())
                .amount(new BigDecimal("30.00"))
                .build();
        JournalDto journalDto = JournalDto.builder()
                .txDate(LocalDate.of(2022, 4, 1))
                .description("test")
                .postDate(LocalDate.of(2022, 4, 2))
                .entries(List.of(entryDto1, entryDto2, entryDto3, entryDto4))
                .build();

        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
        assertHttpMessage(result, "Debit amount does not tally with credit amount");
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
    void get_givenInvalidBook_willReturnNotFound() throws Exception {
        JournalDto journalDto2022 = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 3, 2), foodExpenseId, cashId, new BigDecimal("100.00"));
        MvcResult postResult = post(journalDto2022, generateJournalsUrl("J2022"));
        JournalDto postedJournalDto = (JournalDto) getResultObject(postResult, JournalDto.class);

        MvcResult getResult = get(generateJournalsUrl("J1234", postedJournalDto.getJournalId()));
        assertHttpStatus(getResult, HttpStatus.NOT_FOUND);
        assertHttpMessage(getResult, "Journal not found");
    }

    @Test
    void get_givenInvalidJournal_willReturnNotFound() throws Exception {
        MvcResult getResult = get(generateJournalsUrl("J2022", 1234L));
        assertHttpStatus(getResult, HttpStatus.NOT_FOUND);
        assertHttpMessage(getResult, "Journal not found");
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

    @Test
    void update_givenBookNotFound_willReturnNotFound() throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 3, 2), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.CREATED);
        JournalDto insertedJournal = (JournalDto) getResultObject(result, JournalDto.class);

        JournalDto journalUpdate = createJournal(LocalDate.of(2022, 3, 3), "test again", LocalDate.of(2022, 3, 3), cashId, new BigDecimal("20.00"), foodExpenseId, new BigDecimal("20.00"));
        MvcResult updateResult = put(journalUpdate, generateJournalsUrl("J1234", insertedJournal.getJournalId()));
        assertHttpStatus(updateResult, HttpStatus.NOT_FOUND);
    }

    @Test
    void update_givenJournalNotFound_willReturnNotFound() throws Exception {
        JournalDto journalUpdate = createJournal(LocalDate.of(2022, 3, 3), "test again", LocalDate.of(2022, 3, 3), cashId, new BigDecimal("20.00"), foodExpenseId, new BigDecimal("20.00"));
        MvcResult updateResult = put(journalUpdate, generateJournalsUrl("J2022", 1234567890L));
        assertHttpStatus(updateResult, HttpStatus.NOT_FOUND);
    }

    @ParameterizedTest
    @ValueSource(strings = {"2021-12-31", "2023-01-01"})
    void update_givenJournalWithTxDateBeforeAfterBookDate_willReturnBadRequest(LocalDate txDate) throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 3, 2), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.CREATED);
        JournalDto insertedJournal = (JournalDto) getResultObject(result, JournalDto.class);

        JournalDto journalUpdate = createJournal(txDate, "test", LocalDate.of(2022, 3, 1), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult updateResult = put(journalUpdate, generateJournalsUrl("J2022", insertedJournal.getJournalId()));
        assertHttpStatus(updateResult, HttpStatus.BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(strings = {"2021-12-31", "2023-01-01"})
    void update_givenJournalWithPostDateBeforeAfterBookDate_willReturnBadRequest(LocalDate postDate) throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 3, 2), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.CREATED);
        JournalDto insertedJournal = (JournalDto) getResultObject(result, JournalDto.class);

        JournalDto journalUpdate = createJournal(LocalDate.of(2022, 3, 1), "test", postDate, foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult updateResult = put(journalUpdate, generateJournalsUrl("J2022", insertedJournal.getJournalId()));
        assertHttpStatus(updateResult, HttpStatus.BAD_REQUEST);
    }

    @Test
    void update_givenJournalWithTxDateBeforeCloseDate_willReturnBadRequest() throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 3, 2), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.CREATED);
        JournalDto insertedJournal = (JournalDto) getResultObject(result, JournalDto.class);

        JournalDto journalUpdate = createJournal(LocalDate.of(2022, 1, 2), "test", LocalDate.of(2022, 3, 2), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult updateResult = put(journalUpdate, generateJournalsUrl("J2022", insertedJournal.getJournalId()));
        assertHttpStatus(updateResult, HttpStatus.BAD_REQUEST);
    }

    @Test
    void update_givenJournalWithPostDateBeforeCloseDate_willReturnBadRequest() throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 3, 2), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.CREATED);
        JournalDto insertedJournal = (JournalDto) getResultObject(result, JournalDto.class);

        JournalDto journalUpdate = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 1, 2), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult updateResult = put(journalUpdate, generateJournalsUrl("J2022", insertedJournal.getJournalId()));
        assertHttpStatus(updateResult, HttpStatus.BAD_REQUEST);
    }

    @Test
    void update_givenJournalWithInvalidAccount_willReturnBadRequest() throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 3, 2), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.CREATED);
        JournalDto insertedJournal = (JournalDto) getResultObject(result, JournalDto.class);

        AccountDto invalid = AccountDto.builder().accountId("1234").build();
        JournalDto journalUpdate = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 1, 2), invalid, cashId, new BigDecimal("10.00"));
        MvcResult updateResult = put(journalUpdate, generateJournalsUrl("J2022", insertedJournal.getJournalId()));
        assertHttpStatus(updateResult, HttpStatus.BAD_REQUEST);
    }

    @Test
    void update_givenJournalWithUnbalancedEntries_willReturnBadRequest() throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 3, 2), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.CREATED);
        JournalDto insertedJournal = (JournalDto) getResultObject(result, JournalDto.class);

        JournalDto journalUpdate = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 3, 2), foodExpenseId, new BigDecimal("11.00"), cashId, new BigDecimal("10.00"));
        MvcResult updateResult = put(journalUpdate, generateJournalsUrl("J2022", insertedJournal.getJournalId()));
        assertHttpStatus(updateResult, HttpStatus.BAD_REQUEST);
        assertThat(updateResult.getResponse().getErrorMessage(), is("Debit amount does not tally with credit amount"));
    }

    @Test
    void update_givenJournalWithUnbalancedEntriesButDifferentCurrency_willReturnJournal() throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 3, 2), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.CREATED);
        JournalDto insertedJournal = (JournalDto) getResultObject(result, JournalDto.class);

        JournalDto journalUpdate = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 3, 2), cashId, new BigDecimal("11.00"), usdBankId, new BigDecimal("10.00"));
        result = put(journalUpdate, generateJournalsUrl("J2022", insertedJournal.getJournalId()));
        assertHttpStatus(result, HttpStatus.OK);
        JournalDto savedJournal = (JournalDto) getResultObject(result, JournalDto.class);
        assertThat(savedJournal, is(journalUpdate));
    }

    @Test
    void update_givenJournalWithMultipleEntriesOfSameCurrency_willReturnJournal() throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 3, 2), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.CREATED);
        JournalDto insertedJournal = (JournalDto) getResultObject(result, JournalDto.class);

        JournalEntryDto entryDto1 = JournalEntryDto.builder()
                .entryType(EntryType.Credit)
                .accountId(bankId.getAccountId())
                .amount(new BigDecimal("40.00"))
                .build();
        JournalEntryDto entryDto2 = JournalEntryDto.builder()
                .entryType(EntryType.Credit)
                .accountId(cashId.getAccountId())
                .amount(new BigDecimal("10.00"))
                .build();
        JournalEntryDto entryDto3 = JournalEntryDto.builder()
                .entryType(EntryType.Debit)
                .accountId(foodExpenseId.getAccountId())
                .amount(new BigDecimal("20.00"))
                .build();
        JournalEntryDto entryDto4 = JournalEntryDto.builder()
                .entryType(EntryType.Debit)
                .accountId(transportExpenseId.getAccountId())
                .amount(new BigDecimal("30.00"))
                .build();
        JournalDto journalUpdate = JournalDto.builder()
                .txDate(LocalDate.of(2022, 4, 1))
                .description("test")
                .postDate(LocalDate.of(2022, 4, 2))
                .entries(List.of(entryDto1, entryDto2, entryDto3, entryDto4))
                .build();

        result = put(journalUpdate, generateJournalsUrl("J2022", insertedJournal.getJournalId()));
        assertHttpStatus(result, HttpStatus.OK);
        JournalDto savedJournal = (JournalDto) getResultObject(result, JournalDto.class);
        assertThat(savedJournal, is(journalUpdate));
    }

    @Test
    void update_givenJournalWithMultipleEntriesOfSameCurrencyButNotBalance_willReturnBadRequest() throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 3, 2), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.CREATED);
        JournalDto insertedJournal = (JournalDto) getResultObject(result, JournalDto.class);

        JournalEntryDto entryDto1 = JournalEntryDto.builder()
                .entryType(EntryType.Credit)
                .accountId(bankId.getAccountId())
                .amount(new BigDecimal("40.00"))
                .build();
        JournalEntryDto entryDto2 = JournalEntryDto.builder()
                .entryType(EntryType.Credit)
                .accountId(cashId.getAccountId())
                .amount(new BigDecimal("10.00"))
                .build();
        JournalEntryDto entryDto3 = JournalEntryDto.builder()
                .entryType(EntryType.Debit)
                .accountId(foodExpenseId.getAccountId())
                .amount(new BigDecimal("30.00"))
                .build();
        JournalEntryDto entryDto4 = JournalEntryDto.builder()
                .entryType(EntryType.Debit)
                .accountId(transportExpenseId.getAccountId())
                .amount(new BigDecimal("30.00"))
                .build();
        JournalDto journalUpdate = JournalDto.builder()
                .txDate(LocalDate.of(2022, 4, 1))
                .description("test")
                .postDate(LocalDate.of(2022, 4, 2))
                .entries(List.of(entryDto1, entryDto2, entryDto3, entryDto4))
                .build();

        result = put(journalUpdate, generateJournalsUrl("J2022", insertedJournal.getJournalId()));
        assertHttpStatus(result, HttpStatus.BAD_REQUEST);
        assertHttpMessage(result, "Debit amount does not tally with credit amount");
    }

    @Test
    void delete_givenJournalAfterBookCloseDate_willDeleteJournal() throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 3, 2), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.CREATED);
        JournalDto insertedJournal = (JournalDto) getResultObject(result, JournalDto.class);

        MvcResult deleteResult = delete(generateJournalsUrl("J2022", insertedJournal.getJournalId()));
        assertHttpStatus(deleteResult, HttpStatus.OK);
    }

    @Test
    void delete_givenJournalWithInvalidBook_willReturnNotFound() throws Exception {
        JournalDto journalDto = createJournal(LocalDate.of(2022, 3, 2), "test", LocalDate.of(2022, 3, 2), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDto, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.CREATED);
        JournalDto insertedJournal = (JournalDto) getResultObject(result, JournalDto.class);

        MvcResult deleteResult = delete(generateJournalsUrl("J1234", insertedJournal.getJournalId()));
        assertHttpStatus(deleteResult, HttpStatus.NOT_FOUND);
    }

    @Test
    void delete_givenInvalidJournal_willReturnNotFound() throws Exception {
        MvcResult deleteResult = delete(generateJournalsUrl("J2022", 1234L));
        assertHttpStatus(deleteResult, HttpStatus.NOT_FOUND);
    }

    @Test
    void delete_givenJournalWithDateBeforeBookCloseDate_willReturnBadRequest() throws Exception {
        JournalDto journalDtoTxDateBeforeBookClose = createJournal(LocalDate.of(2022, 2, 1), "test", LocalDate.of(2022, 2, 3), foodExpenseId, cashId, new BigDecimal("10.00"));
        MvcResult result = post(journalDtoTxDateBeforeBookClose, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.CREATED);
        journalDtoTxDateBeforeBookClose = (JournalDto) getResultObject(result, JournalDto.class);

        JournalDto journalDtoPostDateBeforeBookClose = createJournal(LocalDate.of(2022, 2, 3), "test", LocalDate.of(2022, 2, 1), foodExpenseId, cashId, new BigDecimal("10.00"));
        result = post(journalDtoPostDateBeforeBookClose, generateJournalsUrl("J2022"));
        assertHttpStatus(result, HttpStatus.CREATED);
        journalDtoPostDateBeforeBookClose = (JournalDto) getResultObject(result, JournalDto.class);

        book2022.setCloseUntilDate(LocalDate.of(2022, 2, 2));
        result = put(book2022, booksUrl + "/J2022");
        assertHttpStatus(result, HttpStatus.OK);

        MvcResult deleteResult = delete(generateJournalsUrl("J2022", journalDtoTxDateBeforeBookClose.getJournalId()));
        assertHttpStatus(deleteResult, HttpStatus.BAD_REQUEST);

        deleteResult = delete(generateJournalsUrl("J2022", journalDtoPostDateBeforeBookClose.getJournalId()));
        assertHttpStatus(deleteResult, HttpStatus.BAD_REQUEST);
    }

}
