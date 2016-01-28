package ck.panda.domain.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.json.JSONObject;
import ck.panda.util.JsonUtil;

/**
 * Secondary Vm IP Address from Nic.
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

    /** Unique ID from Cloud Stack. */
    @Column(name = "uuid")
    private String uuid;

    /** Nic id . */
    @Column(name = "nic_id")
    private Long nicId;

    /** Network ip Address to establish a connection. */
    @Column(name = "primary_ip_address")
    private String primaryIpAddress;

    /** Net mask value of Network . */
    @Column(name = "guest_ipaddress")
    private String guestIpAddress;

    /** Instance nic id. */
    @JoinColumn(name = "instance_id", referencedColumnName = "id", updatable = false, insertable = false)
    @OneToOne
    private VmInstance vmInstance;

    /** Instance id for nic. */
    @Column(name = "instance_id")
    private Long vmInstanceId;

    /** Is this secondary vm ipaddress is active. */
    @Column(name = "is_Active")
    private Boolean isActive;

    /** Transient network of the instance. */
    @Transient
    private String transvmInstanceId;

    /** Transient nic id. */
    @Transient
    private String transNicId;

    /** Temporary variable. */
    @Transient
    private Boolean syncFlag;

    /**
     * Get the id of the vm Ip address.
     *
     * @return the id of the vm Ip address
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the  vm Ip address.
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the uuid of the Nic.
     *
     * @return the uuid of the Nic
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the uuid of the Nic.
     *
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    /**
     * Get the guestIpAddress.
     *
     * @return the guestIpAddress
     */
    public String getGuestIpAddress() {
        return guestIpAddress;
    }

    /**
     * Set the guestIpAddress.
     *
     * @param guestIpAddress to set
     */
    public void setGuestIpAddress(String guestIpAddress) {
        this.guestIpAddress = guestIpAddress;
    }

    /**
     * Get isActive.
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set the isActive.
     *
     * @param isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

   /**
     * @return the nicId
     */
    public Long getNicId() {
        return nicId;
    }

    /**
     * @param nicId the nicId to set
     */
    public void setNicId(Long nicId) {
        this.nicId = nicId;
    }

/**
     * @return the transvmInstanceId
     */
    public String getTransvmInstanceId() {
        return transvmInstanceId;
    }

    /**
     * @param transvmInstanceId the transvmInstanceId to set
     */
    public void setTransvmInstanceId(String transvmInstanceId) {
        this.transvmInstanceId = transvmInstanceId;
    }

    /**
     * @return the transNicId
     */
    public String getTransNicId() {
        return transNicId;
    }

    /**
     * Set the transNicId.
     *
     * @param transNicId  to set
     */
    public void setTransNicId(String transNicId) {
        this.transNicId = transNicId;
    }

/** Get the syncFlag.
    *
    * @return the syncFlag
    */
   public Boolean getSyncFlag() {
       return syncFlag;
   }

   /**
    * Set the syncFlag.
    *
    * @param syncFlag to set
    */
   public void setSyncFlag(Boolean syncFlag) {
       this.syncFlag = syncFlag;
   }

    /**
     * Get the primaryIpAddress .
     *
     * @return the primaryIpAddress
     */
   public String getPrimaryIpAddress() {
       return primaryIpAddress;
   }

   /**
    * Set the primaryIpAddress .
    *
    * @param primaryIpAddress to set
    */
   public void setPrimaryIpAddress(String primaryIpAddress) {
       this.primaryIpAddress = primaryIpAddress;
   }

    /**
     * Convert JSONObject to vm ip address entity.
     *
     * @param jsonObject json object
     * @return vm ipaddress entity object.
     * @throws Exception unhandled errors.
     */
    public static VmIpaddress convert(JSONObject jsonObject) throws Exception {
        VmIpaddress vm = new VmIpaddress();
        vm.setUuid(JsonUtil.getStringValue(jsonObject, "id"));
        vm.setGuestIpAddress(JsonUtil.getStringValue(jsonObject, "ipaddress"));
        vm.setTransNicId(JsonUtil.getStringValue(jsonObject, "nicuuid"));
        vm.setTransvmInstanceId(JsonUtil.getStringValue(jsonObject, "nicuuid"));
        vm.setIsActive(true);
        vm.setSyncFlag(false);
        return vm;
    }
        /**
         * Mapping entity object into list.
         *
         * @param vmIpaddressList list of secondary ip address.
         * @return vmIpaddressListMap secondary ip address.
         */
    public static Map<String, VmIpaddress> convert(List<VmIpaddress> vmIpaddressList) {
        Map<String, VmIpaddress> vmIpaddressListMap = new HashMap<String, VmIpaddress>();

        for (VmIpaddress nic : vmIpaddressList) {
            vmIpaddressListMap.put(nic.getUuid(), nic);
        }
        return vmIpaddressListMap;
    }
}
