package com.hevlar.konney.infrastructure.repositories;

import com.hevlar.konney.infrastructure.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    boolean existsByBookLabel(String label);
    List<Account> findAllByBookLabel(String label);
    Optional<Account> findByAccountIdAndBookLabel(String accountId, String label);
    List<Account> findAllByAccountIdInAndBookLabel(List<String> accountIds, String label);
}
