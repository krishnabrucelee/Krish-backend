package ck.panda.domain.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Configuration entity for cloudstack.
 *
 */
@Entity
@Table(name = "ck_configuration")
@SuppressWarnings("serial")
public class CloudStackConfiguration implements Serializable {

    /** the id of the Configuration. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** URL for cloudStack connectivity. */
    @Column(name = "url")
    private String apiURL;

    /** API key for connection establishment. */
    @NotEmpty
    @Size(min = 86, max = 100)
    @Column(name = "api_key")
    private String apiKey;

    /** Secret key for authentication. */
    @NotEmpty
    @Size(min = 86, max = 100)
    @Column(name = "secret_key")
    private String secretKey;

    /**
     * @return  id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the apiKey
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * @return the secretKey
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * @param id  to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param apiKey to set
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * @param secretKey to set
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * @return the apiURL
     */
    public String getApiURL() {
        return apiURL;
    }

    /**
     * @param apiURL to set
     */
    public void setApiURL(String apiURL) {
        this.apiURL = apiURL;
    }
}
