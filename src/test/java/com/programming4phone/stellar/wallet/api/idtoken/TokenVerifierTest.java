package com.programming4phone.stellar.wallet.api.idtoken;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.programming4phone.stellar.wallet.api.error.TokenMissingException;
import com.programming4phone.stellar.wallet.api.error.TokenVerificationException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TokenVerifierTest {

	/*
	 * Please note: The valid token in TEST_AUTH_HEADER will most likely be expired
	 * by the time you test this. The browser debug console log will contain a valid
	 * token. It will only be good for 1 hour do test quickly!)
	 * 
	 * This really is an integration test as it hits the live Google server!
	 */
	private final static String TEST_EXPIRED_AUTH_HEADER = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6ImJhNGRlZDdmNWE5MjQyOWYyMzM1NjFhMzZmZjYxM2VkMzg3NjJjM2QifQ.eyJhenAiOiIxNDg2NDY2MzA3NjktYjIyZW5pNTA5OHVnM2h0YWF0MTNoaDE0aTN1YnM3cjAuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiIxNDg2NDY2MzA3NjktYjIyZW5pNTA5OHVnM2h0YWF0MTNoaDE0aTN1YnM3cjAuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTExMTMxNjIyMTkwMTI4ODIxOTUiLCJlbWFpbCI6ImphbTNzOGVsbEBnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiYXRfaGFzaCI6IlIteGhBUmNNSEd5REYtUHoyNlpJb1EiLCJleHAiOjE1MTc5MzcyOTIsImlzcyI6ImFjY291bnRzLmdvb2dsZS5jb20iLCJqdGkiOiIxZWVjZTJlYTIzZGViNTg3ZTVjN2RmNTViNzgyYzcxMjVkMGNlZTJhIiwiaWF0IjoxNTE3OTMzNjkyLCJuYW1lIjoiSmFtZXMgQmVsbCIsInBpY3R1cmUiOiJodHRwczovL2xoNC5nb29nbGV1c2VyY29udGVudC5jb20vLUNyWTFkQUZ0RVdFL0FBQUFBQUFBQUFJL0FBQUFBQUFBQUh3L0V6Q3BOVlVTTlJBL3M5Ni1jL3Bob3RvLmpwZyIsImdpdmVuX25hbWUiOiJKYW1lcyIsImZhbWlseV9uYW1lIjoiQmVsbCIsImxvY2FsZSI6ImVuIn0.I-bgrfUScNPOfadO51fU6DW2Qyobh9AfLZBXgHMJwdhKsGYdQNWVyyW3-r5y39O1sNACDYk2aaTAJa5IWQ8FqwVvtv1sLxMhxxpDpn6uVJJ63htp9RP3hA9UJyRRgltDatxRui1pzYmGP0PmtWxKRw5Q-60S7y0xRZRcuBhjesen5Wbqa7ms2Dbzau0mcqKRx0_hMdSGTSFbN_TXmigr86mcB9BEYm-4ucOkep43SCoS8X43Ue8rx7USRcTQ1b9tPhxFGrUyRZ1qOZkFO0joyZhxPLcj8h9tsgLTNT5H00DgGX7dqrTrY-QzEdm_3V7ZgVmCQwrKq4CYZRfvD_s1cQ";
	private final static String TEST_AUTH_HEADER = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6ImJhNGRlZDdmNWE5MjQyOWYyMzM1NjFhMzZmZjYxM2VkMzg3NjJjM2QifQ.eyJhenAiOiIxNDg2NDY2MzA3NjktYjIyZW5pNTA5OHVnM2h0YWF0MTNoaDE0aTN1YnM3cjAuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiIxNDg2NDY2MzA3NjktYjIyZW5pNTA5OHVnM2h0YWF0MTNoaDE0aTN1YnM3cjAuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTExMTMxNjIyMTkwMTI4ODIxOTUiLCJlbWFpbCI6ImphbTNzOGVsbEBnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiYXRfaGFzaCI6IjhUYmZMWC1XcHg2VjMyNEppY21mZEEiLCJleHAiOjE1MTc5NDU2MzAsImlzcyI6ImFjY291bnRzLmdvb2dsZS5jb20iLCJqdGkiOiI2YTExZThmNGFiZTI0NTJmMWY4ZTZmNTM0NDg4NDQ0YjEyNGNmOWNjIiwiaWF0IjoxNTE3OTQyMDMwLCJuYW1lIjoiSmFtZXMgQmVsbCIsInBpY3R1cmUiOiJodHRwczovL2xoNC5nb29nbGV1c2VyY29udGVudC5jb20vLUNyWTFkQUZ0RVdFL0FBQUFBQUFBQUFJL0FBQUFBQUFBQUh3L0V6Q3BOVlVTTlJBL3M5Ni1jL3Bob3RvLmpwZyIsImdpdmVuX25hbWUiOiJKYW1lcyIsImZhbWlseV9uYW1lIjoiQmVsbCIsImxvY2FsZSI6ImVuIn0.CQ0pWx91q50HLUUir_dg_LeyAJgFGBotcjS9C6tY__nm2wRTAnysnKGGYOkIwLJy7Mq85lxiLRwv0K5W2zzy3AqsIB7SdcurSiVcERXV__0PjGNOgduPI2ztx-3fbSRR984HltQNGrVlFQMetbRF9tCUSyKaszawl2buYwf1As4YPBS_vBz0i1cuqSpSwbUE3vYVvmLRgopDJHbzy87dSJJLrAqthSPdMOj-JkAYuATRI5ZdWk05ImDRMeFqTeBqxXFyVYn5m54b6BnHviPdekJ-jodaeK7dyaPlSMJzTvBOT6dvZxHJuWX3ZKodUs8UNAe_wyS7mi86b3kBOTVQWg";
	private final static String TEST_BAD_AUTH_HEADER = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImJhNGRlZDdmNWE5MjQyOWYyMzM1NjFhMzZmZjYxM2VkMzg3NjJjM2QifQ.eyJhenAiOiIxNDg2NDY2MzA3NjktYjIyZW5pNTA5OHVnM2h0YWF0MTNoaDE0aTN1YnM3cjAuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiIxNDg2NDY2MzA3NjktYjIyZW5pNTA5OHVnM2h0YWF0MTNoaDE0aTN1YnM3cjAuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTExMTMxNjIyMTkwMTI4ODIxOTUiLCJlbWFpbCI6ImphbTNzOGVsbEBnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiYXRfaGFzaCI6IjhUYmZMWC1XcHg2VjMyNEppY21mZEEiLCJleHAiOjE1MTc5NDU2MzAsImlzcyI6ImFjY291bnRzLmdvb2dsZS5jb20iLCJqdGkiOiI2YTExZThmNGFiZTI0NTJmMWY4ZTZmNTM0NDg4NDQ0YjEyNGNmOWNjIiwiaWF0IjoxNTE3OTQyMDMwLCJuYW1lIjoiSmFtZXMgQmVsbCIsInBpY3R1cmUiOiJodHRwczovL2xoNC5nb29nbGV1c2VyY29udGVudC5jb20vLUNyWTFkQUZ0RVdFL0FBQUFBQUFBQUFJL0FBQUFBQUFBQUh3L0V6Q3BOVlVTTlJBL3M5Ni1jL3Bob3RvLmpwZyIsImdpdmVuX25hbWUiOiJKYW1lcyIsImZhbWlseV9uYW1lIjoiQmVsbCIsImxvY2FsZSI6ImVuIn0.CQ0pWx91q50HLUUir_dg_LeyAJgFGBotcjS9C6tY__nm2wRTAnysnKGGYOkIwLJy7Mq85lxiLRwv0K5W2zzy3AqsIB7SdcurSiVcERXV__0PjGNOgduPI2ztx-3fbSRR984HltQNGrVlFQMetbRF9tCUSyKaszawl2buYwf1As4YPBS_vBz0i1cuqSpSwbUE3vYVvmLRgopDJHbzy87dSJJLrAqthSPdMOj-JkAYuATRI5ZdWk05ImDRMeFqTeBqxXFyVYn5m54b6BnHviPdekJ-jodaeK7dyaPlSMJzTvBOT6dvZxHJuWX3ZKodUs8UNAe_wyS7mi86b3kBOTVQWg";

	@Autowired
	private TokenVerifier tokenVerifier;
	
	@Ignore
	@Test
	public void tokenTest() {
		boolean verifies;
		verifies = tokenVerifier.verify(TEST_AUTH_HEADER);
		assertTrue(verifies);
		
		try {
			verifies = tokenVerifier.verify(TEST_EXPIRED_AUTH_HEADER);
			fail("Expired token verified successfully");
		} catch(TokenVerificationException e) {
			assertNotNull(e);
		}
		
		try {
			verifies = tokenVerifier.verify(TEST_BAD_AUTH_HEADER);
			fail("Bad header verified successfully");
		} catch(TokenVerificationException e) {
			assertNotNull(e);
		}
		
		try {
			verifies = tokenVerifier.verify(null);
			fail("NULL header verified successfully");
		} catch(TokenMissingException e) {
			assertNotNull(e);
		}
	}
}
