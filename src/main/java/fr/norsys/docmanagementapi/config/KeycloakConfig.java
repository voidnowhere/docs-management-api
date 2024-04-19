package fr.norsys.docmanagementapi.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("keycloak.admin")
@Configuration
public class KeycloakConfig {

    private String serverUrl;
    private String clientId;
    private String realm;
    private String grantType;
    private String username;
    private String password;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .grantType(grantType)
                .username(username)
                .password(password)
                .build();
    }


    public String getServerUrl() {
        return serverUrl;
    }

    public KeycloakConfig setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        return this;
    }

    public String getClientId() {
        return clientId;
    }

    public KeycloakConfig setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public String getRealm() {
        return realm;
    }

    public KeycloakConfig setRealm(String realm) {
        this.realm = realm;
        return this;
    }

    public String getGrantType() {
        return grantType;
    }

    public KeycloakConfig setGrantType(String grantType) {
        this.grantType = grantType;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public KeycloakConfig setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public KeycloakConfig setPassword(String password) {
        this.password = password;
        return this;
    }

}

