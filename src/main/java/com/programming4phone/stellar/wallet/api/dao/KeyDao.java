package com.programming4phone.stellar.wallet.api.dao;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.programming4phone.stellar.wallet.api.entity.WalletKeys;
import com.programming4phone.stellar.wallet.api.error.InvalidKeyException;
import com.programming4phone.stellar.wallet.api.error.KeyNotFoundException;

@Component
public class KeyDao {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	/**
	 * Removes the account number from the Redis database.
	 * @param accountNumber String
	 */
	public void removeAccount(String accountNumber) {
		logger.info("accountNumber: " + accountNumber);
		stringRedisTemplate.delete(accountNumber);
		logger.info("deleted accountNumber: " + accountNumber);
	}
	
	/**
	 * Save an account to the Redis database. The key is set to PERSIST so that it never expires.
	 * @param accountKeys AccountKeys
	 * @throws com.programming4phone.stellar.wallet.api.error.InvalidKeyException
	 */
	public void saveAccount(WalletKeys walletKeys) {
		logger.info("walletKeys: " + walletKeys.toString());
		Optional.ofNullable(walletKeys.getAccountNumber()).orElseThrow(InvalidKeyException::new);
		Optional.ofNullable(walletKeys.getSecretSeed()).orElseThrow(InvalidKeyException::new);
		stringRedisTemplate.opsForValue().set(walletKeys.getAccountNumber(), walletKeys.getSecretSeed());
		logger.info("set accountKeys: " + walletKeys.toString());
		stringRedisTemplate.persist(walletKeys.getAccountNumber());
		logger.info("persist accountKeys: " + walletKeys.toString());
	}
	
	/**
	 * Retrieve the secret seed for an account number. An exception is thrown
	 * if the key does not exist, ultimately resulting in an Http Status code 404 (NOT_FOUND).
	 * @param accountNumber String
	 * @return AccountKeys containing account number and secret seed
	 * @throws com.programming4phone.stellar.wallet.api.error.KeyNotFoundException
	 */
	public WalletKeys getSecretSeed(String accountNumber) {
		logger.info("accountNumber: " + accountNumber);
		String secretSeed = Optional.ofNullable(stringRedisTemplate.opsForValue().get(accountNumber))
				.orElseThrow(KeyNotFoundException::new);
		logger.info("secretSeed: " + secretSeed);
		WalletKeys walletKeys = new WalletKeys().setAccountNumber(accountNumber).setSecretSeed(secretSeed);
		logger.info("walletKeys: " + walletKeys.toString());
		return walletKeys;
	}
}
