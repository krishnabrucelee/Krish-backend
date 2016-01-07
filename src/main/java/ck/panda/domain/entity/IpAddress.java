package ck.panda.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ck_ip_address")
public class IpAddress {

      /** Unique Id of the IP address. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /**
     * Get the id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id.
     *
     * @param id  to set
     */
    public void setId(Long id) {
        this.id = id;
    }



}
