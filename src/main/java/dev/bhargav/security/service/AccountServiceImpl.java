package dev.bhargav.security.service;

import dev.bhargav.security.builder.AccountTransactionEventBuilder;
import dev.bhargav.security.constant.AccountConstants;
import dev.bhargav.security.deserializer.AccountDetailsDeserializer;
import dev.bhargav.security.entity.Account;
import dev.bhargav.security.exception.BadRequestException;
import dev.bhargav.security.exception.InsufficientAccountBalanceException;
import dev.bhargav.security.exception.ResourceNotFoundException;
import dev.bhargav.security.model.AccountDto;
import dev.bhargav.security.model.Transaction;
import dev.bhargav.security.repository.AccountRepository;
import dev.bhargav.security.serializer.AccountDetailsSerializer;
import dev.bhargav.security.service.AccountService;
import io.micrometer.common.util.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountDetailsDeserializer accountDetailsDeserializer;

    @Autowired
    AccountDetailsSerializer accountDetailsSerializer;

    @Value("${SERVICE_ACCOUNT}")
    private String serviceAccount;

    @Override
    public AccountDto getAccountInformation(String accountNumber) throws ResourceNotFoundException, BadRequestException {
        if (StringUtils.isBlank(accountNumber) || !NumberUtils.isDigits(accountNumber)) {
            throw new BadRequestException(AccountConstants.PROVIDE_VALID_INPUTS.getMessage() + AccountConstants.ACCOUNT_NUMBER.getMessage());
        }
        Optional<Account> byAccNo = Optional.ofNullable(this.accountRepository.findByAccNo(Integer.parseInt(accountNumber)));
        if (byAccNo.isEmpty()) {
            throw new ResourceNotFoundException(AccountConstants.ACCOUNT_NOT_FOUND.getMessage());
        }
        Account accountInformation = byAccNo.get();
        byAccNo.get().getTransactions().sort(Comparator.comparing(Transaction::getTs));
        return this.accountDetailsDeserializer.deserializeAccount(byAccNo.get());
    }

    @Override
    public AccountDto createAccount(AccountDto accountDto) throws BadRequestException {
        StringJoiner stringJoiner = new StringJoiner(AccountConstants.COMMA.getMessage());
        if (StringUtils.isBlank(accountDto.getAccountHolderName())) {
            stringJoiner.add(AccountConstants.ACCOUNT_HOLDER_NAME.getMessage());
        }
        if (StringUtils.isBlank(accountDto.getAccountBranch())) {
            stringJoiner.add(AccountConstants.ACCOUNT_BRANCH.getMessage());
        }

        if (stringJoiner.length() > 0) {
            throw new BadRequestException(AccountConstants.PROVIDE_VALID_INPUTS.getMessage() + stringJoiner);
        }

        Account account = this.accountDetailsSerializer.serializeAccount(accountDto);
        return this.accountDetailsDeserializer.deserializeAccount(this.accountRepository.save(account));
    }

    @Override
    public AccountDto updateAccountBranch(String accountNumber, String newBranch) throws ResourceNotFoundException, BadRequestException {
        StringJoiner stringJoiner = new StringJoiner(AccountConstants.COMMA.getMessage());
        if (StringUtils.isBlank(accountNumber) || !NumberUtils.isDigits(accountNumber)) {
            stringJoiner.add(AccountConstants.ACCOUNT_NUMBER.getMessage());
        }
        if (StringUtils.isBlank(newBranch)) {
            stringJoiner.add(AccountConstants.NEW_BRANCH.getMessage());
        }
        if (stringJoiner.length() > 0) {
            throw new BadRequestException(AccountConstants.PROVIDE_VALID_INPUTS.getMessage() + stringJoiner);
        }
        Optional<Account> byAccNo = Optional.ofNullable(this.accountRepository.findByAccNo(Integer.parseInt(accountNumber)));
        if (byAccNo.isEmpty()) {
            throw new ResourceNotFoundException(AccountConstants.ACCOUNT_NOT_FOUND.getMessage());
        }
        Account existingAccount = byAccNo.get();
        existingAccount.setBranch(newBranch);
        existingAccount.setTransactions(AccountTransactionEventBuilder
                .updateAccountSuccessfulEvent(existingAccount.getTransactions()));
        existingAccount.setModifiedDate(new Date());
        existingAccount.setModifiedBy(this.serviceAccount);
        Account updatedAccount = this.accountRepository.save(existingAccount);
        updatedAccount.getTransactions().sort(Comparator.comparing(Transaction::getTs));
        return this.accountDetailsDeserializer.deserializeAccount(updatedAccount);
    }

    @Override
    public void deleteAccount(String accountNumber) throws ResourceNotFoundException, BadRequestException {
        if (StringUtils.isBlank(accountNumber) || !NumberUtils.isDigits(accountNumber)) {
            throw new BadRequestException(AccountConstants.PROVIDE_VALID_INPUTS.getMessage() + AccountConstants.ACCOUNT_NUMBER.getMessage());
        }
        Optional<Account> byAccNo = Optional.ofNullable(this.accountRepository.findByAccNo(Integer.parseInt(accountNumber)));
        if (byAccNo.isEmpty()) {
            throw new ResourceNotFoundException(AccountConstants.ACCOUNT_NOT_FOUND.getMessage());
        }
        this.accountRepository.deleteByAccNo(Integer.parseInt(accountNumber));
    }

    @Override
    public AccountDto deposit(String accountNumber, String depositAmount) throws ResourceNotFoundException, BadRequestException {
        StringJoiner stringJoiner = new StringJoiner(AccountConstants.COMMA.getMessage());
        if (StringUtils.isBlank(accountNumber) || !NumberUtils.isDigits(accountNumber)) {
            stringJoiner.add(AccountConstants.ACCOUNT_NUMBER.getMessage());
        }
        if (StringUtils.isBlank(depositAmount) || !NumberUtils.isDigits(depositAmount)) {
            stringJoiner.add(AccountConstants.DEPOSIT_AMOUNT.getMessage());
        }
        if (stringJoiner.length() > 0) {
            throw new BadRequestException(AccountConstants.PROVIDE_VALID_INPUTS.getMessage() + stringJoiner);
        }

        Optional<Account> byAccNo = Optional.ofNullable(this.accountRepository.findByAccNo(Integer.parseInt(accountNumber)));
        if (byAccNo.isEmpty()) {
            throw new ResourceNotFoundException(AccountConstants.ACCOUNT_NOT_FOUND.getMessage());
        }
        Account existingAccount = byAccNo.get();
        int deposit = Integer.parseInt(depositAmount);
        Long newBalance = ((existingAccount.getBalance() != null) ? existingAccount.getBalance() : 0L) + deposit;
        existingAccount.setBalance(newBalance);
        existingAccount.setModifiedDate(new Date());
        existingAccount.setModifiedBy(this.serviceAccount);
        existingAccount.setTransactions(AccountTransactionEventBuilder
                .createDepositSuccessfulEvent(existingAccount.getTransactions(), deposit));
        Account updatedAccount = this.accountRepository.save(existingAccount);
        updatedAccount.getTransactions().sort(Comparator.comparing(Transaction::getTs));
        return this.accountDetailsDeserializer.deserializeAccount(updatedAccount);
    }

    @Override
    public AccountDto withdraw(String accountNumber, String withdrawalAmount) throws ResourceNotFoundException, BadRequestException, InsufficientAccountBalanceException {
        StringJoiner stringJoiner = new StringJoiner(AccountConstants.COMMA.getMessage());
        if (StringUtils.isBlank(accountNumber) || !NumberUtils.isDigits(accountNumber)) {
            stringJoiner.add(AccountConstants.ACCOUNT_NUMBER.getMessage());
        }
        if (StringUtils.isBlank(withdrawalAmount) || !NumberUtils.isDigits(withdrawalAmount)) {
            stringJoiner.add(AccountConstants.DEPOSIT_AMOUNT.getMessage());
        }
        if (stringJoiner.length() > 0) {
            throw new BadRequestException(AccountConstants.PROVIDE_VALID_INPUTS.getMessage() + stringJoiner);
        }

        Optional<Account> byAccNo = Optional.ofNullable(this.accountRepository.findByAccNo(Integer.parseInt(accountNumber)));
        if (byAccNo.isEmpty()) {
            throw new ResourceNotFoundException(AccountConstants.ACCOUNT_NOT_FOUND.getMessage());
        }
        Account existingAccount = byAccNo.get();
        Integer withdraw = Integer.parseInt(withdrawalAmount);

        Long existingBalance = ((existingAccount.getBalance() != null) ? existingAccount.getBalance() : 0L);
        if (Long.valueOf(withdraw) > existingBalance) {
            throw new InsufficientAccountBalanceException(AccountConstants.INSUFFICIENT_ACCOUNT_BALANCE.getMessage());
        }

        Long newBalance = existingBalance - withdraw;
        existingAccount.setBalance(newBalance);
        existingAccount.setModifiedDate(new Date());
        existingAccount.setModifiedBy(this.serviceAccount);
        existingAccount.setTransactions(AccountTransactionEventBuilder
                .createWithdrawalSuccessfulEvent(existingAccount.getTransactions(), withdraw));
        Account updatedAccount = this.accountRepository.save(existingAccount);
        updatedAccount.getTransactions().sort(Comparator.comparing(Transaction::getTs));
        return this.accountDetailsDeserializer.deserializeAccount(updatedAccount);
    }

    @Override
    public List<AccountDto> getAllAccounts() {
        return this.accountDetailsDeserializer.deserializeAccounts(this.accountRepository.findAll());
    }
}
