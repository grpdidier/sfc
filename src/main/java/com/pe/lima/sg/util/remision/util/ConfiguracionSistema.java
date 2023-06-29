package com.pe.lima.sg.util.remision.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class ConfiguracionSistema {
	
	@Value("${remision.keystore.jks}")
    private String keystore;

	@Value("${remision.private.key.alias}")
    private String privateKeyAlias;

	@Value("${remision.private.key.pass}")
    private String privateKeyPass;

	@Value("${remision.key.store.pass}")
    private String keyStorePass;

	@Value("${remision.key.store.type}")
    private String keyStoreType;

	@Value("${remision.api.token.sunat.url}")
	private String apiTokenSunatUrl;
	
	@Value("${remision.api.token.sunat.client.id}")
	private String apiTokenSunatClientId;
		
	@Value("${remision.api.token.sunat.client.secret}")
	private String apiTokenSunatClientSecret;
	
	@Value("${remision.api.token.sunat.username}")
	private String apiTokenSunatUsername;       	

	@Value("${remision.api.token.sunat.password}")
	private String apiTokenSunatPassword;
	
	@Value("${remision.api.ticket.sunat.url}")
	private String apiTicketSunatUrl;
	
	@Value("${remision.api.envio.sunat.url}")
	private String apiEnvioSunatUrl;
}
