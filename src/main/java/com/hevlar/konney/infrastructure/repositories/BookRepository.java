package com.hevlar.konney.infrastructure.repositories;

import com.hevlar.konney.infrastructure.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
}
