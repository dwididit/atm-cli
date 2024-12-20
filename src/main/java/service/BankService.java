package service;

import enums.TransferMode;

import java.math.BigDecimal;

public interface BankService {
    void createAccount(String name);
    void deposit(BigDecimal amount);
    void withdraw(BigDecimal amount);
    void transfer(String target, BigDecimal amount);
    void setTransferMode(TransferMode transferMode);
}