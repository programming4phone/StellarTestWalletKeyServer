package com.programming4phone.stellar.wallet.api.entity;

public class WalletKeys {
	private String accountNumber;
	private String secretSeed;
	
	public String getAccountNumber() {
		return accountNumber;
	}
	public WalletKeys setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
		return this;
	}
	public String getSecretSeed() {
		return secretSeed;
	}
	public WalletKeys setSecretSeed(String secretSeed) {
		this.secretSeed = secretSeed;
		return this;
	}
	
	@Override
	public String toString() {
		return "WalletKeys [accountNumber=" + accountNumber + ", secretSeed=" + secretSeed + "]";
	}
	
}
