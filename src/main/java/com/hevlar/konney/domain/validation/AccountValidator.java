package com.hevlar.konney.domain.validation;

import com.hevlar.konney.domain.entities.IAccount;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AccountValidator implements ConstraintValidator<ValidAccount, IAccount> {
    @Override
    public boolean isValid(IAccount value, ConstraintValidatorContext context) {
        if(value.isBalanceSheetAccountGroup()){
            if(value.getCurrency() == null || value.getOpeningBalance() == null || value.getOpeningDate() == null){
                return false;
            }
        }
        if(value.isIncomeStatementAccountGroup()){
            if(value.getCurrency() != null || value.getOpeningBalance() != null || value.getOpeningDate() != null){
                return false;
            }
        }
        return true;
    }
}
