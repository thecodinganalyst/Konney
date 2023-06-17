package com.hevlar.konney.presentation;

import com.hevlar.konney.application.IBookService;
import com.hevlar.konney.infrastructure.entities.Book;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/books")
public class BookController extends ValidationController{
    private final IBookService service;

    public BookController(IBookService service){
        this.service = service;
    }

    @PatchMapping("/{label}")
    public Book update(@PathVariable("label") String label, @RequestBody @Valid Book book){
        try{
            return service.updateBook(label, book);
        }catch (NoSuchElementException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book create(@RequestBody @Valid Book book){
        try{
            return service.createBook(book);
        }catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping
    public List<Book> list(){
        return service.listBooks();
    }

    @GetMapping("/{label}")
    public Book get(@PathVariable("label") String label){
        try{
            return service.getBook(label);
        }catch (NoSuchElementException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
