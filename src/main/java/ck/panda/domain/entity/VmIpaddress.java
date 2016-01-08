package ck.panda.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * .Secondary Vm IP Address.
 *
 */

@Entity
@Table(name = "vmIpaddress")
public class VmIpaddress {

     /** ID of the nic. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Instance nic id. */
    @JoinColumn(name = "instance_id", referencedColumnName = "id", updatable = false, insertable = false)
    @OneToOne
    private VmInstance vmInstance;

    /** Instance id for nic. */
    @Column(name = "instance_id")
    private Long vmInstanceId;

    /**
     * Get the id of the vm Ip address.
     *
     * @return the id of the vm Ip address
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the  vm Ip address
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }


    /**
     * Get the instance.
     *
     * @return the vminstance
     */
    public VmInstance getVmInstance() {
        return vmInstance;
    }

    /**
     * Set the vminstance.
     *
     * @param vmInstance to set
     */
    public void setVmInstance(VmInstance vmInstance) {
        this.vmInstance = vmInstance;
    }

    /**
     * Get instance Id.
     *
     * @return the vmInstanceId
     */
    public Long getVmInstanceId() {
        return vmInstanceId;
    }

    /**
     * Set the vmInstanceId .
     *
     * @param vmInstanceId to set
     */
    public void setVmInstanceId(Long vmInstanceId) {
        this.vmInstanceId = vmInstanceId;
    }


}
