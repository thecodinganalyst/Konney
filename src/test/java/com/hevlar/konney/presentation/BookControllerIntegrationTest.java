package com.hevlar.konney.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hevlar.konney.infrastructure.entities.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class BookControllerIntegrationTest {

    String booksUrl = "/books";
    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    Book book;
    Book bookWithoutStartDate;
    Book bookWithoutEndDate;

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .label("2023")
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .build();
        bookWithoutStartDate = Book.builder()
                .label("2023")
                .endDate(LocalDate.of(2023, 12, 31))
                .build();
        bookWithoutEndDate = Book.builder()
                .label("2023")
                .startDate(LocalDate.of(2023, 1, 1))
                .build();
    }

    @Test
    void update() {
    }

    @Test
    void create_givenValidBook_willReturnBook() throws Exception {
        MvcResult result = mvc.perform(post(booksUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andReturn();
        assertThat(result.getResponse().getStatus(), is(HttpStatus.CREATED.value()));

        Book resultBook = objectMapper.readValue(result.getResponse().getContentAsString(), Book.class);
        assertThat(resultBook, equalToObject(book));
    }

    @Test
    void create_givenBookWithoutStartDate_willReturnBadRequest() throws Exception {
        MvcResult result = mvc.perform(post(booksUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookWithoutStartDate)))
                .andReturn();
        assertThat(result.getResponse().getStatus(), is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void create_givenBookWithoutEndDate_willReturnBadRequest() throws Exception {
        MvcResult result = mvc.perform(post(booksUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookWithoutEndDate)))
                .andReturn();
        assertThat(result.getResponse().getStatus(), is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void list() {
    }

    @Test
    void get() {
    }
}
