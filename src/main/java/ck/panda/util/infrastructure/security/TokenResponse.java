package ck.panda.util.infrastructure.security;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Token response object.
 *
 */
public class TokenResponse {
    /** Token. */
    @JsonProperty
    private String token;

    /**
     * Default constructor.
     */
    public TokenResponse() {
    }

    /**
     * Parameterized constructor.
     * @param token to set
     */
    public TokenResponse(String token) {
        this.token = token;
    }
}
