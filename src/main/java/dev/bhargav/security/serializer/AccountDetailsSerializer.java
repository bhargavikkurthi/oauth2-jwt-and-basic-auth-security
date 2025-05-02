package dev.bhargav.security.serializer;

import dev.bhargav.security.builder.AccountTransactionEventBuilder;
import dev.bhargav.security.entity.Account;
import dev.bhargav.security.model.AccountDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AccountDetailsSerializer {

    @Value("${SERVICE_ACCOUNT}")
    private String serviceAccount;

    public Account serializeAccount(AccountDto accountDto) {
        return Account.builder()
                .accNo(Integer.valueOf(RandomStringUtils.randomNumeric(5, 5)))
                .holderName(accountDto.getAccountHolderName())
                .startDate(new Date())
                .branch(accountDto.getAccountBranch())
                .balance(0L)
                .transactions(AccountTransactionEventBuilder.createAccountSuccessfulEvent())
                .createdBy(this.serviceAccount)
                .createdDate(new Date())
                .build();
    }
}
