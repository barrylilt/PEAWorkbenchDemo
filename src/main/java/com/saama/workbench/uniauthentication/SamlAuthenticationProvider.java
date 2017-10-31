package com.saama.workbench.uniauthentication;

import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;
import org.opensaml.common.SAMLException;
import org.opensaml.common.SAMLRuntimeException;
import org.opensaml.xml.encryption.DecryptionException;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.validation.ValidationException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.providers.ExpiringUsernameAuthenticationToken;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLAuthenticationToken;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.context.SAMLMessageContext;

public class SamlAuthenticationProvider extends SAMLAuthenticationProvider {
	private static final Logger logger = Logger.getLogger(SamlAuthenticationProvider.class);

	public Authentication authenticate(Authentication authentication) {
		SAMLAuthenticationToken token = (SAMLAuthenticationToken) authentication;
		SAMLMessageContext context = token.getCredentials();
		SAMLCredential credential;
		if (context == null) {
			throw new AuthenticationServiceException("SAML message context is not available in the authentication token");
		}
		try {

			if ("urn:oasis:names:tc:SAML:2.0:profiles:SSO:browser"
					.equals(context.getCommunicationProfileId())) {
				credential = this.consumer
						.processAuthenticationResponse(context);
			} else {
				// SAMLCredential credential;
				if ("urn:oasis:names:tc:SAML:2.0:profiles:holder-of-key:SSO:browser"
						.equals(context.getCommunicationProfileId())) {
					credential = this.hokConsumer
							.processAuthenticationResponse(context);
				} else {
					throw new SAMLException(
							"Unsupported profile encountered in the context "
									+ context.getCommunicationProfileId());
				}
			}
		} catch (SAMLRuntimeException e) {
			logger.debug("Error validating SAML message", e);
			this.samlLogger.log("AuthNResponse", "FAILURE", context, e);
			throw new AuthenticationServiceException(
					"Error validating SAML message", e);
		} catch (SAMLException e) {
			logger.debug("Error validating SAML message", e);
			this.samlLogger.log("AuthNResponse", "FAILURE", context, e);
			throw new AuthenticationServiceException(
					"Error validating SAML message", e);
		} catch (ValidationException e) {
			logger.debug("Error validating signature", e);
			this.samlLogger.log("AuthNResponse", "FAILURE", context, e);
			throw new AuthenticationServiceException(
					"Error validating SAML message signature", e);
		} catch (SecurityException e) {
			logger.debug("Error validating signature", e);
			this.samlLogger.log("AuthNResponse", "FAILURE", context, e);
			throw new AuthenticationServiceException(
					"Error validating SAML message signature", e);
		} catch (DecryptionException e) {
			logger.debug("Error decrypting SAML message", e);
			this.samlLogger.log("AuthNResponse", "FAILURE", context, e);
			throw new AuthenticationServiceException(
					"Error decrypting SAML message", e);
		}
		// SAMLCredential credential;
		Object theUserDetails = getUserDetails(credential);
		Object principal = getPrincipal(credential, theUserDetails);
		Collection<? extends GrantedAuthority> entitlements = getEntitlements(
				credential, theUserDetails);

		Date expiration = getExpirationDate(credential);

		SAMLCredential authenticationCredential = credential;

		ExpiringUsernameAuthenticationToken result = new ExpiringUsernameAuthenticationToken(
				expiration, principal, authenticationCredential, entitlements);

		result.setDetails(theUserDetails);

		this.samlLogger.log("AuthNResponse", "SUCCESS", context, result, null);

		return result;
	}
}
