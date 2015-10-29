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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * Template are the first level hierarchy and we are creating the instance based
 * on the template selection.
 *
 * Get the template information and push to the CS server for template creation
 * also update the application database for user view.
 */
@Entity
@Table(name = "ck_template")
@SuppressWarnings("serial")
public class Template implements Serializable {

    /** RootDiskController enum type used to list the static root disk controller values. */
    public enum RootDiskController {
        /** Root disk controller type as scsi. */
        scsi,
        /** Root disk controller type as ide. */
        ide
    }

    /** NicAdapter enum type used to list the static NIC adapter values. */
    public enum NicAdapter {
        /** NIC adapter type as E1000. */
        E1000,
        /** NIC adapter type as PCNET32. */
        PCNET32,
        /** NIC adapter type as VMXNET2. */
        VMXNET2,
        /** NIC adapter type as VMXNET3. */
        VMXNET3
    }

    /** KeyboardType enum type used to list the static Keyboard type values. */
    public enum KeyboardType {
        /** Keyboard type as US. */
        US_Keyboard,
        /** Keyboard type as UK. */
        UK_Keyboard,
        /** Keyboard type as Japanese. */
        Japanese_Keyboard,
        /** Keyboard type as simplified chinese. */
        Simplified_Chinese
    }

    /** Format enum type used to list the static format values. */
    public enum Format {
        /** Hypervisor format type as VHD. */
        VHD,
        /** Hypervisor format type as VHDX. */
        VHDX,
        /** Hypervisor format type as QCOW2. */
        QCOW2,
        /** Hypervisor format type as RAW. */
        RAW,
        /** Hypervisor format type as VMDK. */
        VMDK,
        /** Hypervisor format type as OVA. */
        OVA,
        /** Hypervisor format type as BareMetal. */
        BareMetal,
        /** Hypervisor format type as TAR. */
        TAR
    }

    /** TemplateType enum type used to list the static template type values. */
    public enum Type {
        /** Template type as SYSTEM. */
        SYSTEM,
        /** Template type as BUILTIN. */
        BUILTIN,
        /** Template type as PERHOST. */
        PERHOST,
        /** Template type as USER. */
        USER,
        /** Template type as ROUTING. */
        ROUTING
    }

    /** Status enum type used to list the status values. */
    public enum Status {
        /** Template status as Active. */
        Active,
        /** Template status as InActive. */
        InActive
    }

    /** Id of the template. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Unique id of the template. */
    @Column(name = "uuid")
    private String uuid;

    /** Unique name of the template. */
    @Column(name = "unique_name")
    private String uniqueName;

    /** Name of the template. */
    @NotEmpty
    @Size(min = 4, max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    /** Type of the template. */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Type type;

    /** Description of the template. */
    @NotEmpty
    @Column(name = "description", nullable = false)
    private String description;

    /** URL of the template. */
    @Column(name = "url")
    private String url;

    /** Reference URL of the template. */
    @Column(name = "reference_url")
    private String referenceUrl;

    /** Zone type id. */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "zone_id", referencedColumnName = "id")
    private Zone zone;

    /** Transient zone of the template. */
    @Transient
    private String transZone;

    /** Hypervisor type id. */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "hypervisor_type_id", referencedColumnName = "id")
    private Hypervisor hypervisor;

    /** Transient hypervisor of the template. */
    @Transient
    private String transHypervisor;

    /** Original XS Version of the template. */
    @Column(name = "xs_version", columnDefinition = "tinyint default 0")
    private Boolean xsVersion;

    /** Root disk controller id. */
    @Column(name = "root_disk_controller")
    @Enumerated(EnumType.STRING)
    private RootDiskController rootDiskController;

    /** NIC adapter type id. */
    @Column(name = "nic_adapter")
    @Enumerated(EnumType.STRING)
    private NicAdapter nicAdapter;

    /** Keyboard type id. */
    @Column(name = "keyboard_type")
    @Enumerated(EnumType.STRING)
    private KeyboardType keyboardType;

    /** Format id. */
    @NotNull
    @Column(name = "format")
    @Enumerated(EnumType.STRING)
    private Format format;

    /** OS category id. */
    @ManyToOne
    @JoinColumn(name = "os_category", referencedColumnName = "id")
    private OsCategory osCategory;

    /** OS type id. */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "os_type", referencedColumnName = "id")
    private OsType osType;

    /** Transient OS type of the template. */
    @Transient
    private String transOsType;

    /** OS version of the template. */
    @Column(name = "os_version")
    private String osVersion;

    /** Cost of the template. */
    @Column(name = "cost")
    private Integer cost;

    /** Minimum core of the template. */
    @Column(name = "minimum_core")
    private Integer minimumCore;

    /** Minimum memory of the template. */
    @Column(name = "minimum_memory")
    private Integer minimumMemory;

    /** Template architecture. */
    @Column(name = "architecture")
    private String architecture;

    /** Extractable of the template. */
    @Column(name = "extractable", columnDefinition = "tinyint default 0")
    private Boolean extractable;

    /** Featured of the template. */
    @Column(name = "featured", columnDefinition = "tinyint default 0")
    private Boolean featured;

    /** Routing of the template. */
    @Column(name = "routing", columnDefinition = "tinyint default 0")
    private Boolean routing;

    /** Password Enabled of the template. */
    @Column(name = "password_enabled", columnDefinition = "tinyint default 0")
    private Boolean passwordEnabled;

    /** One time Chargeable of the template. */
    @Column(name = "one_time_chargeable", columnDefinition = "tinyint default 0")
    private Boolean oneTimeChargeable;

    /** Dynamically Scalable of the template. */
    @Column(name = "dynamically_scalable", columnDefinition = "tinyint default 0")
    private Boolean dynamicallyScalable;

    /** HVM of the template. */
    @Column(name = "hvm", columnDefinition = "tinyint default 0")
    private Boolean hvm;

    /** Share of the template. */
    @Column(name = "share", columnDefinition = "tinyint default 0")
    private Boolean share;

    /** Detailed description of the template. */
    @Column(name = "detailed_description", columnDefinition = "TEXT")
    private String detailedDescription;

    /** Template current state. */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    /** Template updated count. */
    @Column(name = "update_count")
    private Integer updateCount;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /** Created by user. */
    @CreatedBy
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    @OneToOne
    private User createdBy;

    /** Last updated by user. */
    @LastModifiedBy
    @JoinColumn(name = "updated_by", referencedColumnName = "id")
    @OneToOne
    private User updatedBy;

    /** Created date and time. */
    @CreatedDate
    @Column(name = "created_date_time")
    private DateTime createdDateTime;

    /** Last updated date and time. */
    @LastModifiedDate
    @Column(name = "updated_date_time")
    private DateTime updatedDateTime;

    /** Share of the template. */
    @Transient
    private Boolean syncFlag;

    /** Display text of the template. */
    @Column(name = "display_text")
    private String displayText;

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
     * Get the uuid.
     * @return uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the uuid.
     * @param uuid - the String to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the unique name.
     * @return uniqueName
     */
    public String getUniqueName() {
        return uniqueName;
    }

    /**
     * Set the unique name.
     * @param uniqueName - the String to set
     */
    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
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

    /**
     * Get the type.
     * @return type
     */
    public Type getType() {
        return type;
    }

    /**
     * Set the type.
     * @param type - the String to set
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Get the description.
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description.
     * @param description - the String to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the url.
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set the url.
     * @param url - the String to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Get the reference URL.
     * @return referenceUrl
     */
    public String getReferenceUrl() {
        return referenceUrl;
    }

    /**
     * Set the reference URL.
     * @param referenceUrl - the String to set
     */
    public void setReferenceUrl(String referenceUrl) {
        this.referenceUrl = referenceUrl;
    }

    /**
     * Get the zone.
     * @return zone
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * Set the zone.
     * @param zone - the Zone entity to set
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * Get the hypervisor type.
     * @return hypervisor
     */
    public Hypervisor getHypervisor() {
        return hypervisor;
    }

    /**
     * Set the hypervisor type.
     * @param hypervisor - the Hypervisor entity to set
     */
    public void setHypervisor(Hypervisor hypervisor) {
        this.hypervisor = hypervisor;
    }

    /**
     * Get the original XS version.
     * @return xsVersion
     */
    public Boolean getXsVersion() {
        return xsVersion;
    }

    /**
     * Set the original XS version.
     * @param xsVersion - the Boolean to set
     */
    public void setXsVersion(Boolean xsVersion) {
        this.xsVersion = xsVersion;
    }

    /**
     * Get the root disk controller.
     * @return rootDiskController
     */
    public RootDiskController getRootDiskController() {
        return rootDiskController;
    }

    /**
     * Set the root disk controller.
     * @param rootDiskController - the RootDiskController enum to set
     */
    public void setRootDiskController(RootDiskController rootDiskController) {
        this.rootDiskController = rootDiskController;
    }

    /**
     * Get the NIC adapter.
     * @return nicAdapter
     */
    public NicAdapter getNicAdapter() {
        return nicAdapter;
    }

    /**
     * Set the NIC adapter.
     * @param nicAdapter - the NicAdapter enum to set
     */
    public void setNicAdapter(NicAdapter nicAdapter) {
        this.nicAdapter = nicAdapter;
    }

    /**
     * Get the keyboard type.
     * @return keyboardType
     */
    public KeyboardType getKeyboardType() {
        return keyboardType;
    }

    /**
     * Set the keyboard type.
     * @param keyboardType - the KeyboardType enum to set
     */
    public void setKeyboardType(KeyboardType keyboardType) {
        this.keyboardType = keyboardType;
    }

    /**
     * Get the format.
     * @return format
     */
    public Format getFormat() {
        return format;
    }

    /**
     * Set the format.
     * @param format - the Format enum to set
     */
    public void setFormat(Format format) {
        this.format = format;
    }

    /**
     * Get the OS category.
     * @return osCategory
     */
    public OsCategory getOsCategory() {
        return osCategory;
    }

    /**
     * Set the OS category.
     * @param osCategory - the osCategory entity to set
     */
    public void setOsCategory(OsCategory osCategory) {
        this.osCategory = osCategory;
    }

    /**
     * Get the OS type.
     * @return osType
     */
    public OsType getOsType() {
        return osType;
    }

    /**
     * Set the OS type.
     * @param osType - the osType entity to set
     */
    public void setOsType(OsType osType) {
        this.osType = osType;
    }

    /**
     * Get the OS version.
     * @return osVersion
     */
    public String getOsVersion() {
        return osVersion;
    }

    /**
     * Set the OS version.
     * @param osVersion - the String to set
     */
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    /**
     * Get the cost.
     * @return cost
     */
    public Integer getCost() {
        return cost;
    }

    /**
     * Set the cost.
     * @param cost - the Integer to set
     */
    public void setCost(Integer cost) {
        this.cost = cost;
    }

    /**
     * Get the minimum core.
     * @return minimumCore
     */
    public Integer getMinimumCore() {
        return minimumCore;
    }

    /**
     * Set the minimum core.
     * @param minimumCore - the Integer to set
     */
    public void setMinimumCore(Integer minimumCore) {
        this.minimumCore = minimumCore;
    }

    /**
     * Get the minimum memory.
     * @return minimumMemory
     */
    public Integer getMinimumMemory() {
        return minimumMemory;
    }

    /**
     * Set the minimum memory.
     * @param minimumMemory - the Integer to set
     */
    public void setMinimumMemory(Integer minimumMemory) {
        this.minimumMemory = minimumMemory;
    }

    /**
     * Get the architecture.
     * @return architecture
     */
    public String getArchitecture() {
        return architecture;
    }

    /**
     * Set the architecture.
     * @param architecture - the String to set
     */
    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    /**
     * Get the extractable.
     * @return extractable
     */
    public Boolean getExtractable() {
        return extractable;
    }

    /**
     * Set the extractable.
     * @param extractable - the Boolean to set
     */
    public void setExtractable(Boolean extractable) {
        this.extractable = extractable;
    }

    /**
     * Get the featured.
     * @return featured
     */
    public Boolean getFeatured() {
        return featured;
    }

    /**
     * Set the featured.
     * @param featured - the Boolean to set
     */
    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    /**
     * Get the routing.
     * @return routing
     */
    public Boolean getRouting() {
        return routing;
    }

    /**
     * Set the routing.
     * @param routing - the Boolean to set
     */
    public void setRouting(Boolean routing) {
        this.routing = routing;
    }

    /**
     * Get the password enabled.
     * @return passwordEnabled
     */
    public Boolean getPasswordEnabled() {
        return passwordEnabled;
    }

    /**
     * Set the password enabled.
     * @param passwordEnabled - the Boolean to set
     */
    public void setPasswordEnabled(Boolean passwordEnabled) {
        this.passwordEnabled = passwordEnabled;
    }

    /**
     * Get the one time chargeable.
     * @return oneTimeChargeable
     */
    public Boolean getOneTimeChargeable() {
        return oneTimeChargeable;
    }

    /**
     * Set the one time chargeable.
     * @param oneTimeChargeable - the Boolean to set
     */
    public void setOneTimeChargeable(Boolean oneTimeChargeable) {
        this.oneTimeChargeable = oneTimeChargeable;
    }

    /**
     * Get the dynamically scalable.
     * @return dynamicallyScalable
     */
    public Boolean getDynamicallyScalable() {
        return dynamicallyScalable;
    }

    /**
     * Set the dynamically scalable.
     * @param dynamicallyScalable - the Boolean to set
     */
    public void setDynamicallyScalable(Boolean dynamicallyScalable) {
        this.dynamicallyScalable = dynamicallyScalable;
    }

    /**
     * Get the HVM.
     * @return hvm
     */
    public Boolean getHvm() {
        return hvm;
    }

    /**
     * Set the HVM.
     * @param hvm - the Boolean to set
     */
    public void setHvm(Boolean hvm) {
        this.hvm = hvm;
    }

    /**
     * Get the share.
     * @return share
     */
    public Boolean getShare() {
        return share;
    }

    /**
     * Set the share.
     * @param share - the Boolean to set
     */
    public void setShare(Boolean share) {
        this.share = share;
    }

    /**
     * Get the detailed description.
     * @return detailedDescription
     */
    public String getDetailedDescription() {
        return detailedDescription;
    }

    /**
     * Set the detailed description.
     * @param detailedDescription - the String to set
     */
    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    /**
     * Get the status.
     * @return status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the status.
     * @param status - the String to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get the update count.
     * @return updateCount
     */
    public Integer getUpdateCount() {
        return updateCount;
    }

    /**
     * Set the update count.
     * @param updateCount - the Integer to set
     */
    public void setUpdateCount(Integer updateCount) {
        this.updateCount = updateCount;
    }

    /**
     * Get the version.
     * @return version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set the version.
     * @param version - the Long to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the created by.
     * @return createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created by.
     * @param createdBy - the User entity to set
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated by.
     * @return updatedBy
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated by.
     * @param updatedBy - the User entity to set
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the created date time.
     * @return the createdDateTime
     */
    public DateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date time.
     * @param createdDateTime - the DateTime to set
     */
    public void setCreatedDateTime(DateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updated date time.
     * @return updatedDateTime
     */
    public DateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updated date time.
     * @param updatedDateTime - the DateTime to set
     */
    public void setUpdatedDateTime(DateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get the sync flag for temporary usage.
     * @return syncFlag
     */
    public Boolean getSyncFlag() {
        return syncFlag;
    }

    /**
     * Set the sync flag for temporary usage.
     * @param syncFlag - the Boolean to set
     */
    public void setSyncFlag(Boolean syncFlag) {
        this.syncFlag = syncFlag;
    }

    /**
     * Get the display text.
     * @return displayText
     */
    public String getDisplayText() {
		return displayText;
	}

    /**
     * Set the display text.
     * @param displayText - the String to set
     */
	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	/**
     * Get the Transient type.
     * @return transZone
     */
    public String getTransZone() {
        return transZone;
    }

    /**
     * Set the Transient type.
     * @param transZone - the String to set
     */
    public void setTransZone(String transZone) {
        this.transZone = transZone;
    }

    /**
     * Get the Transient OS type.
     * @return transOsType
     */
    public String getTransOsType() {
        return transOsType;
    }

    /**
     * Set the Transient OS type.
     * @param transOsType - the String to set
     */
    public void setTransOsType(String transOsType) {
        this.transOsType = transOsType;
    }

    /**
     * Get the Transient hypervisor.
     * @return transHypervisor
     */
    public String getTransHypervisor() {
        return transHypervisor;
    }

    /**
     * Set the Transient hypervisor.
     * @param transHypervisor - the String to set
     */
    public void setTransHypervisor(String transHypervisor) {
        this.transHypervisor = transHypervisor;
    }

    /**
     * Convert JSONObject to template entity.
     *
     * @param object json object
     * @return template entity object.
     * @throws JSONException handles json exception.
     */
    public static Template convert(JSONObject object) throws JSONException {
        Template template = new Template();
        template.setSyncFlag(false);
        template.uuid = object.get("id").toString();
        template.name = object.get("name").toString();
        template.description = object.get("displaytext").toString();
        template.share = object.getBoolean("ispublic");
        template.passwordEnabled = object.getBoolean("passwordenabled");
        template.featured = object.getBoolean("isfeatured");
        template.extractable = object.getBoolean("isextractable");
        template.dynamicallyScalable = object.getBoolean("isdynamicallyscalable");
        template.transOsType = object.get("ostypeid").toString();
        template.transZone = object.get("zoneid").toString();
        template.transHypervisor = object.get("hypervisor").toString();
        template.setFormat(template.getFormat().valueOf(object.get("format").toString()));
        template.setType(template.getType().valueOf(object.get("templatetype").toString()));
        template.setStatus(template.getStatus().valueOf("Active"));
        return template;
    }

    /**
     *Mapping entity object into list.
     *
     * @param templateList list of templates.
     * @return template map
     */
    public static Map<String, Template> convert(List<Template> templateList) {
        Map<String, Template> templateMap = new HashMap<String, Template>();

        for (Template template : templateList) {
            templateMap.put(template.getUuid(), template);
        }

        return templateMap;
    }
}