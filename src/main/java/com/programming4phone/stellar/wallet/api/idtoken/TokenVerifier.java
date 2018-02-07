package com.programming4phone.stellar.wallet.api.idtoken;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.programming4phone.stellar.wallet.api.error.TokenMissingException;
import com.programming4phone.stellar.wallet.api.error.TokenVerificationException;

@Component
public class TokenVerifier {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String VERIFY_URL = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token={token}";

	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${client.id}")
	private String clientId;
	
	/**
	 * Verify that token supplied at UI login is indeed meant for the wallet application client.
	 * The token is transported to the wallet web services in the Authorization request header.
	 * The header value is prefixed with <i>Bearer</i>. A token that is validly issued for an application 
	 * can still fail to verify if the token expiration time stamp has passed. 
	 * @param header String containing the value of the Authorization request header
	 * @return <b>boolean</b> true, if the token is valid
	 * @throws com.programming4phone.stellar.wallet.api.error.TokenMissingException
	 * @throws com.programming4phone.stellar.wallet.api.error.TokenVerificationException
	 */
	public boolean verify(String header) {
		Optional.ofNullable(header).orElseThrow(TokenMissingException::new);
		String authToken = header.substring(7); // Header value is prefixed with "Bearer"
	
		boolean result = true;
		try {
			/*
			 * See https://developers.google.com/identity/sign-in/web/backend-auth
			 * If the token is properly signed and the iss and exp claims have the  
			 * expected values, you will get a HTTP 200 response. 
			 * 
			 * The aud claim should contain your app's client IDs. If it does not 
			 * then the token is invalid!
			 */
			TokenClaims tokenClaims = restTemplate.getForObject(VERIFY_URL, TokenClaims.class, authToken);
			logger.info("tokenClaims: " + tokenClaims.toString());
			if(!clientId.equals(tokenClaims.getAud())) {
				logger.error("Unable to verify token due to aud claim mismatch.");
				throw new TokenVerificationException();
			}
		}
		catch(RestClientException e) {
			logger.error("Unable to verify token",e);
			/*
			 * If the token is expired (exp claim contains long value of time) or
			 * the token has been corrupted in any way, the Google API web service  
			 * invocation will return http status 400 BAD_REQUEST.
			 * 
			 * To quickly convert the value of the exp claim to a real time/date value
			 * enter the long value here: https://www.epochconverter.com/
			 * 
			 * From testing it appears that the token expires after about an hour.
			 */
			throw new TokenVerificationException();
		}
		return result;
	}
}
