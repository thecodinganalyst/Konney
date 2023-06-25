package com.hevlar.konney.presentation;

import com.hevlar.konney.application.BookkeepingException;
import com.hevlar.konney.application.BookkeepingNotFoundException;
import com.hevlar.konney.application.IBookService;
import com.hevlar.konney.infrastructure.entities.Book;
import com.hevlar.konney.presentation.dto.BookDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController extends ValidationController{
    private final IBookService service;

    public BookController(IBookService service){
        this.service = service;
    }

    @PutMapping("/{label}")
    public BookDto update(@PathVariable("label") String label, @RequestBody @Valid BookDto bookDto){
        try {
            Book book = service.updateBook(label, bookDto.toBook());
            return BookDto.fromBook(book);
        }catch(BookkeepingNotFoundException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }catch (BookkeepingException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @DeleteMapping("/{label}")
    public void delete(@PathVariable("label") String label){
        try {
            service.deleteBook(label);
        }catch(BookkeepingNotFoundException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }catch (BookkeepingException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto create(@RequestBody @Valid BookDto bookDto){
        try{
            Book book = service.createBook(bookDto.toBook());
            return BookDto.fromBook(book);
        }catch (BookkeepingException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping
    public List<BookDto> list(){
        List<Book> bookList = service.listBooks();
        return BookDto.fromBookList(bookList);
    }

    @GetMapping("/{label}")
    public BookDto get(@PathVariable("label") String label){
        try{
            Book book = service.getBook(label);
            return BookDto.fromBook(book);
        }catch(BookkeepingNotFoundException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }
}
