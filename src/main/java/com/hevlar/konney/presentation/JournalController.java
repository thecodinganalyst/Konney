package com.hevlar.konney.presentation;

import com.hevlar.konney.application.IJournalService;
import com.hevlar.konney.infrastructure.entities.Journal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/books/{bookId}/journals")
public class JournalController {
    private final IJournalService service;
    public JournalController(IJournalService service){
        this.service = service;
    }

    @PatchMapping("/{journalId}")
    public Journal update(@PathVariable("journalId") Long journalId, @RequestBody @Valid Journal journal){
        try{
            return service.updateJournal(journalId, journal);
        }catch (NoSuchElementException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Journal create(@RequestBody @Valid Journal journal){
        try{
            return service.createJournal(journal);
        }catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping
    public List<Journal> list(){
        return service.listJournals();
    }

    @GetMapping("/{journalId}")
    public Journal get(@PathVariable("journalId") Long journalId){
        try{
            return service.getJournal(journalId);
        }catch (NoSuchElementException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
