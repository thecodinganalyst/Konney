package com.hevlar.konney.presentation;

import com.hevlar.konney.application.BookkeepingException;
import com.hevlar.konney.application.IAccountService;
import com.hevlar.konney.infrastructure.entities.Account;
import com.hevlar.konney.presentation.dto.AccountDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/books/{label}/accounts")
public class AccountController extends ValidationController{

    private final IAccountService service;

    public AccountController(IAccountService service){
        this.service = service;
    }

    @PatchMapping("/{accountId}")
    public AccountDto update(@PathVariable("label") String label, @PathVariable("accountId") String accountId, @RequestBody @Valid AccountDto accountDto){
        try {
            Account account = accountDto.toAccount();
            Account updated = service.updateAccount(label, accountId, account);
            return AccountDto.fromAccount(updated);
        } catch (BookkeepingException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto create(@PathVariable("label") String label, @RequestBody @Valid AccountDto accountDto){
        try{
            Account account = accountDto.toAccount();
            Account created = service.createAccount(label, account);
            return AccountDto.fromAccount(created);
        }catch (BookkeepingException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping
    public List<AccountDto> list(@PathVariable("label") String label){
        return AccountDto.fromAccountList(this.service.listAccounts(label));
    }

    @GetMapping("/{accountId}")
    public AccountDto get(@PathVariable("label") String label, @PathVariable("accountId") String accountId){
        try{
            Account retrieved = service.getAccount(label, accountId);
            return AccountDto.fromAccount(retrieved);
        }catch (BookkeepingException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
