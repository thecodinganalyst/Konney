package com.hevlar.konney.presentation;

import com.hevlar.konney.application.BookkeepingException;
import com.hevlar.konney.application.BookkeepingNotFoundException;
import com.hevlar.konney.application.IJournalService;
import com.hevlar.konney.infrastructure.entities.Journal;
import com.hevlar.konney.presentation.dto.JournalDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/books/{label}/journals")
public class JournalController extends ValidationController{
    private final IJournalService service;
    public JournalController(IJournalService service){
        this.service = service;
    }

    @PutMapping("/{journalId}")
    public JournalDto update(@PathVariable("label") String label, @PathVariable("journalId") Long journalId, @RequestBody @Valid JournalDto journalDto){
        try{
            Journal journal = service.updateJournal(label, journalId, journalDto.toJournal());
            return JournalDto.fromJournal(journal);
        }catch(BookkeepingNotFoundException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }catch (BookkeepingException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JournalDto create(@PathVariable("label") String label, @RequestBody @Valid JournalDto journalDto){
        try{
            Journal journal = service.createJournal(label, journalDto.toJournal());
            return JournalDto.fromJournal(journal);
        }catch(BookkeepingNotFoundException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }catch (BookkeepingException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping
    public List<JournalDto> list(@PathVariable("label") String label){
        List<Journal> journalList = service.listJournals(label);
        return JournalDto.fromJournalList(journalList);
    }

    @GetMapping("/{journalId}")
    public JournalDto get(@PathVariable("label") String label, @PathVariable("journalId") Long journalId){
        try{
            Journal journal = service.getJournal(label, journalId);
            return JournalDto.fromJournal(journal);
        }catch(BookkeepingNotFoundException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }catch (BookkeepingException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @DeleteMapping("/{journalId}")
    public void delete(@PathVariable("label") String label, @PathVariable("journalId") Long journalId){
        try{
            service.deleteJournal(label, journalId);
        }catch(BookkeepingNotFoundException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }catch (BookkeepingException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
