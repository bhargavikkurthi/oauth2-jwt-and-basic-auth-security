package dev.bhargav.security.repository;

import dev.bhargav.security.entity.Account;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(value = "select * from account where acc_no = ?1", nativeQuery = true)
    Account findByAccNo(Integer accountNumber);

    @Modifying
    @Transactional
    @Query(value = "delete from account where acc_no = ?1", nativeQuery = true)
    void deleteByAccNo(Integer accountNumber);
}
