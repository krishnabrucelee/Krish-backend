package ck.panda.domain.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.json.JSONObject;
import ck.panda.constants.CloudStackConstants;
import ck.panda.util.JsonUtil;

/**
 * Secondary Vm IP Address.
 *
 */
@Entity
@Table(name = "vm_ipaddress")
@SuppressWarnings("serial")
public class VmIpaddress implements Serializable {

     /** Constant for nic uuid. */
    public static final String CS_NIC_UUID = "nicid";

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

    /** Types of Ip Adddress .*/
    @Column(name = "ip_type")
    @Enumerated(EnumType.STRING)
    private IpType ipType;

    /** Transient network of the instance. */
    @Transient
    private String transvmInstanceId;

    /** Transient nic id. */
    @Transient
    private String transNicId;


    /** Temporary variable. */
    @Transient
    private Boolean syncFlag;

    /** Types of Ip Adddress .*/
    public enum IpType {

    	/** Primary ip address. */
        primaryIpAddress,

        /** Secondary Ip address . */
        secondaryIpAddress
    }

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
     * Get the vm instance id.
     *
     * @return the vmInstanceId
     */
    public Long getVmInstanceId() {
        return vmInstanceId;
    }

    /**
     * Set the vm instance id.
     *
     * @param vmInstanceId to set
     */
    public void setVmInstanceId(Long vmInstanceId) {
        this.vmInstanceId = vmInstanceId;
    }

    /**
     * Get the guest ip address.
     *
     * @return the guestIpAddress
     */
    public String getGuestIpAddress() {
        return guestIpAddress;
    }

    /**
     * Set the guest ip address.
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
     *  Get the transient nic id .
     *
     * @return the transient NicId .
     */
    public String getTransNicId() {
        return transNicId;
    }

    /**
     * Set the transient  nic id.
     *
     * @param transNicId to set
     */
    public void setTransNicId(String transNicId) {
        this.transNicId = transNicId;
    }

    /**
     * Get the nic id.
     *
     * @return the nicId
     */
    public Long getNicId() {
        return nicId;
    }

    /**
     * Set the  nic id.
     *
     * @param nicId  to set
     */
    public void setNicId(Long nicId) {
        this.nicId = nicId;
    }

    /**
     * Get the transient vminstance id.
     *
     * @return the transvmInstanceId
     */
    public String getTransvmInstanceId() {
        return transvmInstanceId;
    }

    /**
     * Set the transient vm instance id.
     *
     * @param transvmInstanceId  to set
     */
    public void setTransvmInstanceId(String transvmInstanceId) {
        this.transvmInstanceId = transvmInstanceId;
    }

   /** Get the sync flag.
    *
    * @return the syncFlag
    */
   public Boolean getSyncFlag() {
       return syncFlag;
   }

   /**
    * Set the sync flag.
    *
    * @param syncFlag to set
    */
   public void setSyncFlag(Boolean syncFlag) {
       this.syncFlag = syncFlag;
   }

    /**
     * @return the ipType
     */
   public IpType getIpType() {
    return ipType;
}

   /**
    * @param ipType the ipType to set
    */
   public void setIpType(IpType ipType) {
       this.ipType = ipType;
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
        vm.setUuid(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_ID));
        vm.setGuestIpAddress(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_IP_ADDRESS));
        vm.setIpType(IpType.secondaryIpAddress);
        vm.setTransNicId(JsonUtil.getStringValue(jsonObject, CS_NIC_UUID));
        vm.setIsActive(true);
        vm.setSyncFlag(false);
        return vm;
    }

    /**
     * Mapping entity object into list.
     *
         * @param vmIpaddressList of secondary ip address
         * @return vmIpaddressListMap secondary ip address.
     */
    public static Map<String, VmIpaddress> convert(List<VmIpaddress> vmIpaddressList) {
        Map<String, VmIpaddress> vmIpaddressMap = new HashMap<String, VmIpaddress>();

        for (VmIpaddress vmIp : vmIpaddressList) {
            vmIpaddressMap.put(vmIp.getUuid(), vmIp);
        }


        return vmIpaddressMap;
    }

}
