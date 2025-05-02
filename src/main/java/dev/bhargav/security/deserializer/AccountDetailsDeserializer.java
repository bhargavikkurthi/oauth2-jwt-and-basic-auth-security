package dev.bhargav.security.deserializer;

import dev.bhargav.security.entity.Account;
import dev.bhargav.security.model.AccountDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AccountDetailsDeserializer {

    public AccountDto deserializeAccount(Account account) {
        return AccountDto.builder()
                .accountNumber(account.getAccNo())
                .accountHolderName(account.getHolderName())
                .accountStartDate(account.getStartDate())
                .accountBranch(account.getBranch())
                .accountBalance(account.getBalance())
                .accountTransactions(account.getTransactions())
                .build();
    }

    public List<AccountDto> deserializeAccounts(List<Account> accountList) {
        List<AccountDto> accounts = new ArrayList<>();
        accountList.forEach(account -> accounts.add(this.deserializeAccount(account)));
        return accounts;
    }
}
