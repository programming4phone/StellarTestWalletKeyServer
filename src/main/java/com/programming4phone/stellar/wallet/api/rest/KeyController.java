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
	
	@RequestMapping(value="/{accountNumber}", method=RequestMethod.GET, produces="application/json")
	public WalletKeys getAccountKeys(@RequestHeader("Authorization") String authHeader, @PathVariable String accountNumber) {
		tokenVerifier.verify(authHeader);
		logger.info("accountNumber: " + accountNumber);
		return keyDao.getSecretSeed(accountNumber);
	}

	@RequestMapping(method=RequestMethod.PUT, consumes="application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public void saveAccount(@RequestHeader("Authorization") String authHeader, @RequestBody WalletKeys walletKeys) {
		tokenVerifier.verify(authHeader);
		logger.info("accountKeys: " + walletKeys.toString());
		keyDao.saveAccount(walletKeys);
	}
	
	@RequestMapping(value="/delete/{accountNumber}", method=RequestMethod.DELETE)
	public void removeAccount(@RequestHeader("Authorization") String authHeader, @PathVariable String accountNumber) {
		tokenVerifier.verify(authHeader);
		logger.info("accountNumber: " + accountNumber);
		keyDao.removeAccount(accountNumber);
	}
	
	@ExceptionHandler(KeyNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public void noAccountFound() {
	}
	
	@ExceptionHandler(InvalidKeyException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void invalidKey() {
	}
	
	@ExceptionHandler(TokenMissingException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void tokenMissing() {
	}
	
	@ExceptionHandler(TokenVerificationException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public void tokenNotVerified() {
	}
}
