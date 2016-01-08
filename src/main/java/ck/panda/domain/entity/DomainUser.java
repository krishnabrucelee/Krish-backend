package ck.panda.domain.entity;

/**
 * Domain User.
 *
 */
public class DomainUser {
    /** User name attributes. */
    private String username;

    /**
     * Parameterized constructor.
     * 
     * @param username to set
     */
    public DomainUser(String username) {
        this.username = username;
    }

    /**
     * Get the user name.
     * 
     * @return user name
     */
    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return username;
    }
}
