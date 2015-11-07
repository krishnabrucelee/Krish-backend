package ck.panda.domain.entity;

import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * VM Instances are main entity in our panda project to keep track the status of
 * each instances.
 *
 * Based on VM Instances we can create and destroy, update instance, list Active
 * VM's from cloud stack, etc.,
 */
@Entity
@Table(name = "ck_vm_instance")
public class VmInstance {

   /** Unique Id of the instance. */
   @Id
   @GeneratedValue
   @Column(name = "id")
   private Long id;

   /** Name of the instance. */
   @NotEmpty
   @Size(min = 4, max = 20)
   @Column(name = "name", nullable = false)
   private String name;

   /** cloudstack's instance uuid. */
   @Column(name = "uuid")
   private String uuid;

   /** instance vnc password. */
   @Column(name = "vnc_password")
   private String vncPassword;

   /** Instance owner id. */
   @JoinColumn(name = "instance_owner_id", referencedColumnName = "id", updatable = false, insertable = false)
   @OneToOne
   private User instanceOwner;

   /** Instance owner id. */
   @NotNull
   @Column(name = "instance_owner_id")
   private Long instanceOwnerId;

   /** Instance application id. */
   @Column(name = "application_name")
   private String application;

   /** List of Application Class for an instance. */
   @ManyToMany
   private List<Application> applicationList;

   /** Instance project id. */
   @JoinColumn(name = "project_id", referencedColumnName = "id", updatable = false, insertable = false)
   @ManyToOne
   private Project project;

   /** Instance project id. */
   @Column(name = "project_id")
   private Long projectId;

   /** Instance department id. */
   @JoinColumn(name = "department_id", referencedColumnName = "id", updatable = false, insertable = false)
   @ManyToOne
   private Department department;

   /** Instance department id. */
   @NotNull
   @Column(name = "department_id")
   private Long departmentId;

   /** Instance template id. */
   @JoinColumn(name = "template_id", referencedColumnName = "id", updatable = false, insertable = false)
   @OneToOne
   private Template template;

   /** Instance template id. */
   @Column(name = "template_id")
   private Long templateId;

   /** Instance domain id. */
   @JoinColumn(name = "domain_id", referencedColumnName = "Id", updatable = false, insertable = false)
   @ManyToOne
   private Domain domain;

   /** Instance domain id. */
   @NotNull
   @Column(name = "domain_id")
   private Long domainId;

   /** Instance zone. */
   @JoinColumn(name = "zone_id", referencedColumnName = "Id", updatable = false, insertable = false)
   @ManyToOne
   private Zone zone;

   /** Instance zone id. */
   @NotNull
   @Column(name = "zone_id")
   private Long zoneId;

   /** Instance compute offer. */
   @JoinColumn(name = "compute_offer_id", referencedColumnName = "Id", updatable = false, insertable = false)
   @OneToOne
   private ComputeOffering computeOffering;

   /** Instance compute offer id. */
   @Column(name = "compute_offer_id")
   private Long computeOfferingId;

   /** Instance disk offer. */
   @JoinColumn(name = "storage_offer_id", referencedColumnName = "Id", updatable = false, insertable = false)
   @OneToOne
   private ComputeOffering storageOffering;

   /** Instance disk offer id. */
   @Column(name = "storage_offer_id")
   private Long storageOfferingId;

   /** Instance network offer. */
   @JoinColumn(name = "network_offer_id", referencedColumnName = "Id", updatable = false, insertable = false)
   @OneToOne
   private ComputeOffering networkOffering;

   /** Instance network offer id. */
   @Column(name = "network_offer_id")
   private Long networkOfferingId;

   /** Instance network. */
   @JoinColumn(name = "network_id", referencedColumnName = "Id", updatable = false, insertable = false)
   @OneToOne
   private GuestNetwork network;

   /** Instance network id. */
   @Column(name = "network_id")
   private Long networkId;

   /** Instance network uuid. */
   @Transient
   private String networkUuid;

   /** Instance current state. */
   @Column(name = "status")
   private Status status;

   /** Enumeration status for instance. */
   public enum Status {
        /** Running status of instance. */
        Running,
        /** destroyed status of instance. */
        Destroyed,
        /** Stopped status of instance. */
        Stopped,
        /** after launch or start instance get status as starting. */
        Starting,
        /** after stop or destroy instance get status as stopping. */
        Stopping,
        /** after destroy or expunge instance get status as expunging. */
        Expunging,
        /** while instance creation if get failure get status as Error . */
        Error,
        /** while instance creation if get status as creating . */
        Creating,
        /** after launch instance if get status as ready . */
        Ready
   }

   /** Instance host id. */
   @Column(name = "host_id")
   private String hostId;

   /** Instance pod id. */
   @Column(name = "pod_id")
   private String podId;

   /** Instance event message. */
   @Column(name = "event_message")
   private String eventMessage;

   /** Event type. */
   @Column(name = "instance_event_type")
   private String eventType;

   /** Check instance available or not. */
   @Column(name = "is_removed")
   private Boolean isRemoved;

   /** instance private ip address. */
   @Column(name = "instance_private_ip")
   private String ipAddress;

   /** Version attribute to handle optimistic locking. */
   @Version
   @Column(name = "version")
   private Long version;

   /** Created by user. */
   @CreatedBy
   @JoinColumn(name = "created_user_id", referencedColumnName = "id")
   @OneToOne
   private User createdBy;

   /** Last updated by user. */
   @LastModifiedBy
   @JoinColumn(name = "updated_user_id", referencedColumnName = "id")
   @OneToOne
   private User updatedBy;

   /** Created date and time. */
   @CreatedDate
   @Column(name = "created_date_time")
   @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
   private ZonedDateTime createdDateTime;

   /** Last modified date and time. */
   @LastModifiedDate
   @Column(name = "updated_date_time")
   @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
   private ZonedDateTime updatedDateTime;


   /**
    * Get the the status of instance.
    *
    * @return the status.
    */
   public Status getStatus() {
      return status;
   }

   /**
    * Set the instance status.
    *
    * @param status to set.
    */
   public void setStatus(Status status) {
      this.status = status;
   }

   /**
    * Check whether instance removed or not.
    *
    * @return the isRemoved.
    */
   public Boolean getIsRemoved() {
      return isRemoved;
   }

   /**
    * Delete the instance.
    *
    * @param isRemoved to set.
    */
   public void setIsRemoved(Boolean isRemoved) {
      this.isRemoved = isRemoved;
   }

   /**
    * get the instance id.
    *
    * @return the id.
    */
   public Long getId() {
      return id;
   }

   /**
    * set the instance id.
    *
    * @param id to set.
    */
   public void setId(Long id) {
      this.id = id;
   }

   /**
    * get the instance.
    *
    * @return the name.
    */
   public String getName() {
      return name;
   }

   /**
    * set the instance name.
    *
    * @param name to set.
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * get instance UUID.
    *
    * @return the uuid.
    */
   public String getUuid() {
      return uuid;
   }

   /**
    * set UUID from cloud stack.
    *
    * @param uuid to set.
    */
   public void setUuid(String uuid) {
      this.uuid = uuid;
   }

   /**
    * get instance owner.
    *
    * @return the instanceOwner.
    */
   public User getInstanceOwner() {
      return instanceOwner;
   }

   /**
    * set instance owner.
    *
    * @param instanceOwner to set.
    */
   public void setInstanceOwner(User instanceOwner) {
      this.instanceOwner = instanceOwner;
   }

   /**
    * @return the application.
    */
   public String getApplication() {
      return application;
   }

   /**
    * @param application the application name to set.
    */
   public void setApplication(String application) {
      this.application = application;
   }

   /**
    * get instance project.
    *
    * @return the project.
    */
   public Project getProject() {
      return project;
   }

   /**
    * set instance project.
    *
    * @param project to set.
    */
   public void setProject(Project project) {
      this.project = project;
   }

   /**
    * get instance department.
    *
    * @return the department
    */
   public Department getDepartment() {
      return department;
   }

   /**
    * set instance department.
    *
    * @param department to set.
    */
   public void setDepartment(Department department) {
      this.department = department;
   }

   /**
    * get instance template.
    *
    * @return the template.
    */
   public Template getTemplate() {
      return template;
   }

   /**
    * set instance template.
    *
    * @param template to set.
    */
   public void setTemplate(Template template) {
      this.template = template;
   }

   /**
    * @return the computeOfferingId.
    */
   public Long getComputeOfferingId() {
      return computeOfferingId;
   }

   /**
    * @param computeOfferingId the compute offering id to set.
    */
   public void setComputeOfferingId(Long computeOfferingId) {
      this.computeOfferingId = computeOfferingId;
   }

   /**
    * @return the storageOfferingId.
    */
   public Long getStorageOfferingId() {
      return storageOfferingId;
   }

   /**
    * @param storageOfferingId the storage offering id to set.
    */
   public void setStorageOfferingId(Long storageOfferingId) {
      this.storageOfferingId = storageOfferingId;
   }

   /**
    * @return the networkOfferingId.
    */
   public Long getNetworkOfferingId() {
      return networkOfferingId;
   }

   /**
    * @param networkOfferingId the network offering id to set.
    */
   public void setNetworkOfferingId(Long networkOfferingId) {
      this.networkOfferingId = networkOfferingId;
   }

   /**
    * Get the version count.
    *
    * @return the version
    */
   public Long getVersion() {
      return version;
   }

   /**
    * Set the version count.
    *
    * @param version to set.
    */
   public void setVersion(Long version) {
      this.version = version;
   }

   /**
    * Get created user id.
    *
    * @return the createdBy.
    */
   public User getCreatedBy() {
      return createdBy;
   }

   /**
    * Set created user id.
    *
    * @param createdBy to set.
    */
   public void setCreatedBy(User createdBy) {
      this.createdBy = createdBy;
   }

   /**
    * Get the last modified user id.
    *
    * @return the updated user.
    */
   public User getUpdatedBy() {
      return updatedBy;
   }

   /**
    * Set the last modified user id.
    *
    * @param updatedBy to set.
    */
   public void setUpdatedBy(User updatedBy) {
      this.updatedBy = updatedBy;
   }

   /**
    * Get the created date and time.
    *
    * @return the createdDateTime.
    */
   public ZonedDateTime getCreatedDateTime() {
      return createdDateTime;
   }

   /**
    * Set the created date and time.
    *
    * @param createdDateTime to set.
    */
   public void setCreatedDateTime(ZonedDateTime createdDateTime) {
      this.createdDateTime = createdDateTime;
   }

   /**
    * Get last modified date and time.
    *
    * @return the updatedDateTime.
    */
   public ZonedDateTime getLastModifiedDateTime() {
      return updatedDateTime;
   }

   /**
    * Set last modified date and time.
    *
    * @param updatedDateTime last modified  to set
    */
   public void setLastModifiedDateTime(ZonedDateTime updatedDateTime) {
      this.updatedDateTime = updatedDateTime;
   }

   /**
    * get domain for instance.
    *
    * @return the domain
    */
   public Domain getDomain() {
      return domain;
   }

   /**
    * set domain for instance.
    *
    * @param domain domain to set.
    */
   public void setDomain(Domain domain) {
      this.domain = domain;
   }

   /**
    * Get the instance IPAddress.
    *
    * @return the ipAddress
    */
   public String getIpAddress() {
      return ipAddress;
   }

   /**
    * Set the instance IPAddress.
    *
    * @param ipAddress ip address to set.
    */
   public void setIpAddress(String ipAddress) {
      this.ipAddress = ipAddress;
   }

   /**
    * Get the VNC console password.
    *
    * @return the vncPassword.
    */
   public String getVncPassword() {
      return vncPassword;
   }

   /**
    * Set the VNC console password.
    *
    * @param vncPassword vnc password to set.
    */
   public void setVncPassword(String vncPassword) {
      this.vncPassword = vncPassword;
   }

   /**
    * Get the host id of instance.
    *
    * @return the hostId.
    */
   public String getHostId() {
      return hostId;
   }

   /**
    * Set the host id of instance.
    *
    * @param hostID to set.
    */
   public void setHostId(String hostID) {
      this.hostId = hostID;
   }

   /**
    * Get the pod id.
    *
    * @return the podId.
    */
   public String getPodId() {
      return podId;
   }

   /**
    * Set the pod id.
    *
    * @param podID to set.
    */
   public void setPodId(String podID) {
      this.podId = podID;
   }

   /**
    * Get the event type.
    *
    * @return the event type.
    */
   public String getEventType() {
      return eventType;
   }

   /**
    * Set the eventType.
    *
    * @param eventType the event type to set.
    */
   public void setEventType(String eventType) {
      this.eventType = eventType;
   }

   /**
    * @return the zone.
    */
   public Zone getZone() {
      return zone;
   }

   /**
    * @param zone the zone to set.
    */
   public void setZone(Zone zone) {
      this.zone = zone;
   }

   /**
    * @return the instance owner id.
    */
   public Long getInstanceOwnerId() {
      return instanceOwnerId;
   }

   /**
    * @param instanceOwnerId the instance owner id to set.
    */
   public void setInstanceOwnerId(Long instanceOwnerId) {
      this.instanceOwnerId = instanceOwnerId;
   }

   /**
    * @return the projectId.
    */
   public Long getProjectId() {
      return projectId;
   }

   /**
    * @param projectId the project id to set.
    */
   public void setProjectId(Long projectId) {
      this.projectId = projectId;
   }

   /**
    * @return the departmentId.
    */
   public Long getDepartmentId() {
      return departmentId;
   }

   /**
    * @param departmentId the department id to set.
    */
   public void setDepartmentId(Long departmentId) {
      this.departmentId = departmentId;
   }

   /**
    * @return the templateId.
    */
   public Long getTemplateId() {
      return templateId;
   }

   /**
    * @param templateId the template id to set.
    */
   public void setTemplateId(Long templateId) {
      this.templateId = templateId;
   }

   /**
    * @return the domainId.
    */
   public Long getDomainId() {
      return domainId;
   }

   /**
    * @param domainId the domain id to set.
    */
   public void setDomainId(Long domainId) {
      this.domainId = domainId;
   }

   /**
    * @return the zoneId.
    */
   public Long getZoneId() {
      return zoneId;
   }

   /**
    * @param zoneId  the zone id to set.
    */
   public void setZoneId(Long zoneId) {
      this.zoneId = zoneId;
   }

   /**
    * @return the computeOffering.
    */
   public ComputeOffering getComputeOffering() {
      return computeOffering;
   }

   /**
    * @param computeOffering compute Offering to set.
    */
   public void setComputeOffering(ComputeOffering computeOffering) {
      this.computeOffering = computeOffering;
   }

   /**
    * Get the application list.
    *
    * @return the application list.
    */
   public List<Application> getApplicationList() {
      return applicationList;
   }

   /**
    * Set the application list.
    *
    * @param applicationList the application list to set.
    */
   public void setApplicationList(List<Application> applicationList) {
      this.applicationList = applicationList;
   }

   /**
    * Get the storage Offering.
    *
    * @return the storageOffering.
    */
   public ComputeOffering getStorageOffering() {
      return storageOffering;
   }

   /**
    * Set the storage Offering.
    *
    * @param storageOffering the storage Offering to set.
    */
   public void setStorageOffering(ComputeOffering storageOffering) {
      this.storageOffering = storageOffering;
   }

   /**
    * Get the network Offering.
    *
    * @return the networkOffering.
    */
   public ComputeOffering getNetworkOffering() {
      return networkOffering;
   }

   /**
    * Set the network Offering.
    *
    * @param networkOffering the network Offering to set.
    */
   public void setNetworkOffering(ComputeOffering networkOffering) {
      this.networkOffering = networkOffering;
   }

   /**
    * Get the network.
    *
    * @return the network.
    */
   public GuestNetwork getNetwork() {
      return network;
   }

   /**
    * Set the network.
    *
    * @param network the network to set.
    */
   public void setNetwork(GuestNetwork network) {
      this.network = network;
   }

   /**
    * Get the network id.
    *
    * @return the networkId.
    */
   public Long getNetworkId() {
      return networkId;
   }

   /**
    * Set the network id..
    *
    * @param networkId network id to set.
    */
   public void setNetworkId(Long networkId) {
      this.networkId = networkId;
   }

   /**
    * Get the networkUuid.
    *
    * @return the networkUuid.
    */
   public String getNetworkUuid() {
      return networkUuid;
   }

   /**
    * Set the networkUuid.
    *
    * @param networkUuid network uuid to set.
    */
   public void setNetworkUuid(String networkUuid) {
      this.networkUuid = networkUuid;
   }

   /**
    * @return the eventMessage.
    */
   public String getEventMessage() {
      return eventMessage;
   }

   /**
    * @param eventMessage event message to set.
    */
   public void setEventMessage(String eventMessage) {
      this.eventMessage = eventMessage;
   }

   @Override
   public String toString() {
      return "VmInstance [Id=" + id + ", name=" + name + ", uuid=" + uuid + ", vncPassword=" + vncPassword
            + ", instanceOwner=" + instanceOwner + ", instanceOwnerId=" + instanceOwnerId + ", application="
            + application + ", applicationList=" + applicationList + ", project=" + project + ", projectId=" + projectId
            + ", department=" + department + ", departmentId=" + departmentId + ", template=" + template
            + ", templateId=" + templateId + ", domain=" + domain + ", domainId=" + domainId + ", zone=" + zone
            + ", zoneId=" + zoneId + ", computeOffering=" + computeOffering + ", computeOfferingId=" + computeOfferingId
            + ", storageOffering=" + storageOffering + "" + ", networkOffering="
            + networkOffering + ", networkOfferingId=" + networkOfferingId + ", network=" + network + ", networkId="
            + networkId + ", status=" + status + ", hostId=" + hostId + ", podId=" + podId + ", type=" + eventType
            + ", isRemoved=" + isRemoved + ", ipAddress=" + ipAddress + ", version=" + version + ", createdBy="
            + createdBy + ", updatedBy=" + updatedBy + ", createdDateTime=" + createdDateTime
            + ", lastModifiedDateTime=" + updatedDateTime + "]";
   }

}
