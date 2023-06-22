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
@RequestMapping("/books/{label}/journals")
public class JournalController {
    private final IJournalService service;
    public JournalController(IJournalService service){
        this.service = service;
    }

    @PatchMapping("/{journalId}")
    public Journal update(@PathVariable("label") String label, @PathVariable("journalId") Long journalId, @RequestBody @Valid Journal journal){
        try{
            return service.updateJournal(label, journalId, journal);
        }catch (NoSuchElementException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Journal create(@PathVariable("label") String label, @RequestBody @Valid Journal journal){
        try{
            return service.createJournal(label, journal);
        }catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping
    public List<Journal> list(@PathVariable("label") String label){
        return service.listJournals(label);
    }

    @GetMapping("/{journalId}")
    public Journal get(@PathVariable("label") String label, @PathVariable("journalId") Long journalId){
        try{
            return service.getJournal(label, journalId);
        }catch (NoSuchElementException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
