package com.hevlar.konney.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.hevlar.konney.domain.valueobjects.EntryType;
import com.hevlar.konney.presentation.dto.AccountDto;
import com.hevlar.konney.presentation.dto.JournalDto;
import com.hevlar.konney.presentation.dto.JournalEntryDto;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class ControllerIntegrationTestBase {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    String booksUrl = "/books";
    String accountsUrl = "/accounts";
    String journalsUrl = "/journals";

    protected MvcResult post(Object t, String url) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(t)))
                .andReturn();
    }

    protected MvcResult postIfNotExist(Object t, String postUrl, String getUrl) throws Exception {
        MvcResult result = get(getUrl);
        if(result.getResponse().getStatus() != HttpStatus.OK.value()){
            return post(t, postUrl);
        }
        return null;
    }

    protected MvcResult get(String url) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.get(url)).andReturn();
    }

    protected MvcResult put(Object t, String url) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(t)))
                .andReturn();
    }

    protected MvcResult delete(String url) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.delete(url)).andReturn();
    }

    protected void assertHttpStatus(MvcResult result, HttpStatus status){
        assertThat(result.getResponse().getStatus(), is(status.value()));
    }

    protected void assertHttpMessage(MvcResult result, String expectedMessage){
        assertThat(result.getResponse().getErrorMessage(), is(expectedMessage));
    }

    protected Object getResultObject(MvcResult result, Class<?> classRef ) throws Exception {
        return objectMapper.readValue(result.getResponse().getContentAsString(), classRef);
    }

    protected List<Object> getResultObjectList(MvcResult result, Class<?> classRef ) throws Exception {
        ObjectReader reader = objectMapper.readerForListOf(classRef);
        return reader.readValue(result.getResponse().getContentAsString());
    }

    protected String generateAccountsUrl(String label, String accountId){
        return booksUrl + "/" + label + accountsUrl + "/" + accountId;
    }

    protected String generateAccountsUrl(String label){
        return booksUrl + "/" + label + accountsUrl;
    }

    protected String generateJournalsUrl(String label, Long journalId){
        return booksUrl + "/" + label + journalsUrl + "/" + journalId.toString();
    }

    protected String generateJournalsUrl(String label){
        return booksUrl + "/" + label + journalsUrl;
    }

    protected JournalDto createJournal(LocalDate txDate, String desc, LocalDate postDate, AccountDto debitAccount, AccountDto creditAccount, BigDecimal amount){
        return createJournal(txDate, desc, postDate, debitAccount, amount, creditAccount, amount);
    }

    protected JournalDto createJournal(LocalDate txDate, String desc, LocalDate postDate, AccountDto debitAccount, BigDecimal debitAmount, AccountDto creditAccount, BigDecimal creditAmount){
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
