package dev.bhargav.security.service;

import dev.bhargav.security.exception.BadRequestException;
import dev.bhargav.security.exception.InsufficientAccountBalanceException;
import dev.bhargav.security.exception.ResourceNotFoundException;
import dev.bhargav.security.model.AccountDto;

import java.util.List;

public interface AccountService {

    public AccountDto getAccountInformation(String accountNumber) throws ResourceNotFoundException, BadRequestException;

    AccountDto createAccount(AccountDto accountDto) throws BadRequestException;

    AccountDto updateAccountBranch(String accountNumber, String newBranch) throws ResourceNotFoundException, BadRequestException;

    void deleteAccount(String accountNumber) throws ResourceNotFoundException, BadRequestException;

    AccountDto deposit(String accountNumber, String depositAmount) throws ResourceNotFoundException, BadRequestException;

    AccountDto withdraw(String accountNumber, String withdrawalAmount) throws ResourceNotFoundException, BadRequestException, InsufficientAccountBalanceException;

    List<AccountDto> getAllAccounts();
}
