package com.hevlar.konney.domain.entities;

import java.math.BigDecimal;

public interface IJournalEntry {
    Long getEntryId();
    IAccount getAccount();
    String getCurrency();
    BigDecimal getAmount();
    IJournal getJournal();
}
