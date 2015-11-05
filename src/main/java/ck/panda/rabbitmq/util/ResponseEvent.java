package ck.panda.rabbitmq.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Event response from RabbitMQ.
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseEvent {

   /** event id. */
   private String id;

   /** event uuid. */
   private String entityuuid;

   /** event status. */
   private String status;

   /** event date and time. */
   private String eventDateTime;

   /** event domain account. */
   private String account;

   /** event user. */
   private String user;

   /** event type. */
   private String event;

   /** event type starts with. */
   private String eventStart;

   /** event type. */
   private String description;

   /** event subject. */
   private String subject;

   /** event body. */
   private String body;

   /** VM for event. */
   private String VirtualMachine;

   /** Zone for event. */
   private String DataCenter;

   /** Zone for event. */
   private String zone;

   /** Network for event. */
   private String Network;

   /** Resource for event. */
   private String resource;

   /** ServiceOffering for event. */
   private String ServiceOffering;

   /** ServiceOffering for event. */
   private String VirtualMachineTemplate;

   /** entity for event. */
   private String entity;


   /**
    * @return the entityuuid.
    */
   public String getEntityuuid() {
      return entityuuid;
   }

   /**
    * @param entityuuid the entity uuid to set.
    */
   public void setEntityuuid(String entityuuid) {
      this.entityuuid = entityuuid;
   }

   /**
    * @return the status.
    */
   public String getStatus() {
      return status;
   }

   /**
    * @param status the status to set.
    */
   public void setStatus(String status) {
      this.status = status;
   }

   /**
    * @return the eventDateTime.
    */
   public String getEventDateTime() {
      return eventDateTime;
   }

   /**
    * @param eventDateTime the event date and time to set
    */
   public void setEventDateTime(String eventDateTime) {
      this.eventDateTime = eventDateTime;
   }

   /**
    * @return the account.
    */
   public String getAccount() {
      return account;
   }

   /**
    * @param account the account to set.
    */
   public void setAccount(String account) {
      this.account = account;
   }

   /**
    * @return the user.
    */
   public String getUser() {
      return user;
   }

   /**
    * @param user the user to set.
    */
   public void setUser(String user) {
      this.user = user;
   }

   /**
    * @return the event.
    */
   public String getEvent() {
      return event;
   }

   /**
    * @param event the event to set.
    */
   public void setEvent(String event) {
      this.event = event;
      setEventStart(this.event);
   }

   /**
    * @return the description.
    */
   public String getDescription() {
      return description;
   }

   /**
    * @param description the description to set.
    */
   public void setDescription(String description) {
      this.description = description;
   }

   /**
    * @return the subject.
    */
   public String getSubject() {
      return subject;
   }

   /**
    * @param subject the subject to set.
    */
   public void setSubject(String subject) {
      this.subject = subject;
   }

   /**
    * @return the body.
    */
   public String getBody() {
      return body;
   }

   /**
    * @param body the body to set.
    */
   public void setBody(String body) {
      this.body = body;
   }

   /**
    * @return the id.
    */
   public String getId() {
      return id;
   }

   /**
    * @param id the id to set.
    */
   public void setId(String id) {
      this.id = id;
   }

   /**
    * @return the dataCenter
    */
   public String getDataCenter() {
      return DataCenter;
   }

   /**
    * @param dataCenter the dataCenter to set.
    */
   public void setDataCenter(String dataCenter) {
      DataCenter = dataCenter;
   }

   /**
    * @return the zone.
    */
   public String getZone() {
      return zone;
   }

   /**
    * @param zone the zone to set.
    */
   public void setZone(String zone) {
      this.zone = zone;
   }

   /**
    * @return the network.
    */
   public String getNetwork() {
      return Network;
   }

   /**
    * @param network the network to set.
    */
   public void setNetwork(String network) {
      Network = network;
   }

   /**
    * @return the resource
    */
   public String getResource() {
      return resource;
   }

   /**
    * @param resource the resource to set.
    */
   public void setResource(String resource) {
      this.resource = resource;
   }

   /**
    * @return the virtualMachine.
    */
   public String getVirtualMachine() {
      return VirtualMachine;
   }

   /**
    * @param virtualMachine the virtual machine to set.
    */
   public void setVirtualMachine(String virtualMachine) {
      VirtualMachine = virtualMachine;
   }

   /**
    * @return the serviceOffering.
    */
   public String getServiceOffering() {
      return ServiceOffering;
   }

   /**
    * @param serviceOffering the service offering to set.
    */
   public void setServiceOffering(String serviceOffering) {
      ServiceOffering = serviceOffering;
   }

   /**
    * @return the virtualMachineTemplate.
    */
   public String getVirtualMachineTemplate() {
      return VirtualMachineTemplate;
   }

   /**
    * @param virtualMachineTemplate the virtual machine template to set.
    */
   public void setVirtualMachineTemplate(String virtualMachineTemplate) {
      VirtualMachineTemplate = virtualMachineTemplate;
   }

   /**
    * @return the entity.
    */
   public String getEntity() {
      return entity;
   }

   /**
    * @param entity the entity to set.
    */
   public void setEntity(String entity) {
      this.entity = entity;
   }

   /**
    * @return the event start with string of event type.
    */
   public String getEventStart() {
      return eventStart;
   }

   /**
    * @param eventStart the event start with string to set.
    */
   public void setEventStart(String eventStart) {
      if (this.event != null) {
         this.eventStart = this.event.substring(0, this.event.indexOf('.', 0)) + ".";
      } else {
         this.eventStart = eventStart;
      }
   }

   @Override
   public String toString() {
      return "ResponseEvent [id=" + id + ", entityuuid=" + entityuuid + ", status=" + status + ", eventDateTime="
            + eventDateTime + ", account=" + account + ", user=" + user + ", event=" + event + ", eventStart = " + eventStart + " description="
            + description + ", subject=" + subject + ", body=" + body + ", VirtualMachine=" + VirtualMachine
            + ", DataCenter=" + DataCenter + ", zone=" + zone + ", Network=" + Network + ", resource=" + resource
            + ", ServiceOffering=" + ServiceOffering + ", VirtualMachineTemplate=" + VirtualMachineTemplate
            + ", entity=" + entity + "]";
   }


}
