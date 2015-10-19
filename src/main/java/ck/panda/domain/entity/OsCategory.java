package ck.panda.domain.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;

/**
 * OS category entity data pull from cloud database for template creation and instance creation page.
 */
@Entity
@Table(name = "ck_oscategory")
@SuppressWarnings("serial")
public class OsCategory implements Serializable {

    /** Id of the OS category. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Name of the OS category. */
    @Size(min = 4, max = 25)
    @Column(name = "name", nullable = false)
    private String name;

    /** Unique id for the OS category. */
    @Column(name = "uuid")
    private String uuid;

    /** Created date and time. */
    @CreatedDate
    private DateTime createdDateTime;

    /**
     * @return id of the OS category
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     * the OS category id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return name of the OS category
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     * the OS category name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return UUID of the OS category
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid
     * the OS category UUID to set.
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the createdDateTime
     */
    public DateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * @param createdDateTime
     * the createdDateTime to set.
     */
    public void setCreatedDateTime(DateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }
}
