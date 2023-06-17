package com.hevlar.konney.presentation;

import com.hevlar.konney.application.IAccountService;
import com.hevlar.konney.infrastructure.entities.Account;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/books/{label}/accounts")
public class AccountController extends ValidationController{

    private final IAccountService service;

    public AccountController(IAccountService service){
        this.service = service;
    }

    @PatchMapping("/{accountId}")
    public Account update(@PathVariable("label") String label, @PathVariable("accountId") String accountId, @RequestBody @Valid Account account){
        try{
            return service.updateAccount(label, accountId, account);
        }catch (NoSuchElementException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account create(@PathVariable("label") String label, @RequestBody @Valid Account account){
        try{
            return service.createAccount(label, account);
        }catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping
    public List<Account> list(@PathVariable("label") String label){
        return service.listAccounts(label);
    }

    @GetMapping("/{accountId}")
    public Account get(@PathVariable("label") String label, @PathVariable("accountId") String accountId){
        try{
            return service.getAccount(label, accountId);
        }catch (NoSuchElementException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
