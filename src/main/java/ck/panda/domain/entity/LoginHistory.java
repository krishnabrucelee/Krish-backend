package ck.panda.domain.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "login_history")
@SuppressWarnings("serial")
@EntityListeners(AuditingEntityListener.class)
public class LoginHistory implements Serializable {

    /** Unique ID of the login user. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** User id of the login. */
    @Column(name = "user_id")
    private Long userId;

    /** Already Login of the user. */
    @Column(name = "is_already_login")
    private Boolean isAlreadyLogin;

    /** Token details of the login user. */
    @Column(name = "login_token")
    private String loginToken;

    /** Remember me of the login . */
    @Column(name = "remember_me")
    private String rememberMe;

    /** Last used details of the login user. */
    @Column(name = "remember_me_expire")
    private Long rememberMeExpireDate;

    /**
     * Get the id of LoginHistory.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of LoginHistory.
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the userId of LoginHistory.
     *
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Set the userId of LoginHistory.
     *
     * @param long1 the userId to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Get the isAlreadyLogin of LoginHistory.
     *
     * @return the isAlreadyLogin
     */
    public Boolean getIsAlreadyLogin() {
        return isAlreadyLogin;
    }

    /**
     * Set the isAlreadyLogin of LoginHistory.
     *
     * @param isAlreadyLogin the isAlreadyLogin to set
     */
    public void setIsAlreadyLogin(Boolean isAlreadyLogin) {
        this.isAlreadyLogin = isAlreadyLogin;
    }

    /**
     * Get the loginToken of LoginHistory.
     *
     * @return the loginToken
     */
    public String getLoginToken() {
        return loginToken;
    }

    /**
     * Set the loginToken of LoginHistory.
     *
     * @param loginToken the loginToken to set
     */
    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    /**
     * Get the rememberMe of LoginHistory.
     *
     * @return the rememberMe
     */
    public String getRememberMe() {
        return rememberMe;
    }

    /**
     * Set the rememberMe of LoginHistory.
     *
     * @param rememberMe the rememberMe to set
     */
    public void setRememberMe(String rememberMe) {
        this.rememberMe = rememberMe;
    }

    /**
     * Get the rememberMeExpireDate of LoginHistory.
     *
     * @return the rememberMeExpireDate
     */
    public Long getRememberMeExpireDate() {
        return rememberMeExpireDate;
    }

    /**
     * Set the rememberMeExpireDate of LoginHistory.
     *
     * @param rememberMeExpireDate the rememberMeExpireDate to set
     */
    public void setRememberMeExpireDate(Long rememberMeExpireDate) {
        this.rememberMeExpireDate = rememberMeExpireDate;
    }

}
