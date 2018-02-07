package com.programming4phone.stellar.wallet.api.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.programming4phone.stellar.wallet.api.dao.KeyDao;
import com.programming4phone.stellar.wallet.api.entity.WalletKeys;
import com.programming4phone.stellar.wallet.api.error.InvalidKeyException;
import com.programming4phone.stellar.wallet.api.error.KeyNotFoundException;
import com.programming4phone.stellar.wallet.api.error.TokenMissingException;
import com.programming4phone.stellar.wallet.api.error.TokenVerificationException;
import com.programming4phone.stellar.wallet.api.idtoken.TokenVerifier;

@CrossOrigin
@RestController
@RequestMapping("/wallet/key")
public class KeyController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private KeyDao keyDao;
	
	@Autowired
	private TokenVerifier tokenVerifier;
	
	/**
	 * Retrieve the account keys associated with a specific account number from the Redis database.  
	 * If the account key is not found in the database, this web service will return HTTP status code  
	 * 404 (NOT_FOUND). If the Google authentication token provided in the Authorization request header
	 * cannot be verified, this web service will return HTTP status code 401 (UNAUTHORIZED).
	 * @param authHeader HTTP Authorization request header (Bearer + Google authentication token)
	 * @param accountNumber Stellar account number (hashed public key)
	 * @return <b>WalletKeys</b> object containing hashed public key and encrypted private key
	 */
	@RequestMapping(value="/{accountNumber}", method=RequestMethod.GET, produces="application/json")
	public WalletKeys getAccountKeys(@RequestHeader("Authorization") String authHeader, @PathVariable String accountNumber) {
		tokenVerifier.verify(authHeader);
		logger.info("accountNumber: " + accountNumber);
		return keyDao.getSecretSeed(accountNumber);
	}

	/**
	 * Store the account keys associated with a specific account number into the Redis database.  
	 * If either of the account keys is invalid, this web service will return HTTP status code  
	 * 400 (BAD_REQUEST). If the Google authentication token provided in the Authorization request header
	 * cannot be verified, this web service will return HTTP status code 401 (UNAUTHORIZED).
	 * @param authHeader HTTP Authorization request header (Bearer + Google authentication token)
	 * @param walletKeys Object containing hashed public key and encrypted private key
	 */
	@RequestMapping(method=RequestMethod.PUT, consumes="application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public void saveAccount(@RequestHeader("Authorization") String authHeader, @RequestBody WalletKeys walletKeys) {
		tokenVerifier.verify(authHeader);
		logger.info("accountKeys: " + walletKeys.toString());
		keyDao.saveAccount(walletKeys);
	}
	
	/**
	 * Remove the account keys associated with a specific account number from the Redis database.  
	 * If the Google authentication token provided in the Authorization request header
	 * cannot be verified, this web service will return HTTP status code 401 (UNAUTHORIZED).
	 * @param authHeader HTTP Authorization request header (Bearer + Google authentication token)
	 * @param accountNumber Stellar account number (hashed public key)
	 */
	@RequestMapping(value="/delete/{accountNumber}", method=RequestMethod.DELETE)
	public void removeAccount(@RequestHeader("Authorization") String authHeader, @PathVariable String accountNumber) {
		tokenVerifier.verify(authHeader);
		logger.info("accountNumber: " + accountNumber);
		keyDao.removeAccount(accountNumber);
	}
	
	/**
	 * Exception handler that converts KeyNotFoundException to HTTP status 404 (NOT_FOUND)
	 */
	@ExceptionHandler(KeyNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public void noAccountFound() {
	}
	
	/**
	 * Exception handler that converts InvalidKeyException to HTTP status 400 (BAD_REQUEST)
	 */
	@ExceptionHandler(InvalidKeyException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void invalidKey() {
	}
	
	/**
	 * Exception handler that converts TokenMissingException to HTTP status 400 (BAD_REQUEST)
	 */
	@ExceptionHandler(TokenMissingException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void tokenMissing() {
	}
	
	/**
	 * Exception handler that converts TokenVerificationException to HTTP status 401 (UNAUTHORIZED)
	 */
	@ExceptionHandler(TokenVerificationException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public void tokenNotVerified() {
	}
}
