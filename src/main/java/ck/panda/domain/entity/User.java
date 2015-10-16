package ck.panda.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * User entity.
 *
 */
@Entity
@Table(name = "oex_users")
public class User {

    /** Id of the Subject. */
    @Id
    @GeneratedValue
    private Long id;

    /** Name of the User. */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Get the id.
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id.
     * @param id - the Long to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the name.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     * @param name - the String to set
     */
    public void setName(String name) {
        this.name = name;
    }

}
