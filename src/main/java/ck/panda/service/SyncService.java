package ck.panda.service;

import org.springframework.stereotype.Service;
import ck.panda.util.CloudStackServer;
import ck.panda.util.error.exception.ApplicationException;
import org.json.JSONObject;

/**
 * Synchronization of zone,domain, region , template with cloudStack. *
 */
@Service
public interface SyncService {

   /**
    * Sync initialize method used to set CS server api,secret Key.
    *
    * @param server inject cloudstack server.
    * @throws Exception handles unhandled errors.
    */
   void init(CloudStackServer server) throws Exception;

   /**
    * Sync method consists of method to be called.
    *
    * @throws Exception handles unhandled errors.
    */
   void sync() throws Exception;

   /**
    * Sync with CloudStack server Domain.
    *
    * @throws ApplicationException unhandled application errors.
    * @throws Exception cloudstack unhandled errors
    */
   void syncDomain() throws ApplicationException, Exception;

   /**
    * Sync with Cloud Server Zone.
    *
    * @throws ApplicationException unhandled application errors.
    * @throws Exception cloudstack unhandled errors.
    */
   void syncZone() throws ApplicationException, Exception;

   /**
    * Sync with Cloud Server Region.
    *
    * @throws ApplicationException unhandled application errors.
    * @throws Exception cloudstack unhandled errors.
    */
   void syncRegion() throws ApplicationException, Exception;

   /**
    * Sync with Cloud Server Hypervisor.
    *
    * @throws ApplicationException unhandled application errors.
    * @throws Exception cloudstack unhandled errors.
    */
   void syncHypervisor() throws ApplicationException, Exception;

   /**
    * Sync with Cloud Server OSCategory.
    *
    * @throws ApplicationException unhandled application errors.
    * @throws Exception cloudstack unhandled errors.
    */
   void syncOsCategory() throws ApplicationException, Exception;

   /**
    * Sync with CloudStack server osType.
    *
    * @throws ApplicationException unhandled application errors.
    * @throws Exception cloudstack unhandled errors
    */
   void syncOsTypes() throws ApplicationException, Exception;

   /**
    * Sync with CloudStack server Storage Offering.
    *
    * @throws ApplicationException unhandled application errors.
    * @throws Exception cloudstack unhandled errors
    */
   void syncStorageOffering() throws ApplicationException, Exception;

   /**
    * Sync with Cloud Server Account.
    *
    * @throws ApplicationException unhandled application errors.
    * @throws Exception cloudstack unhandled errors.
    */
   void syncUser() throws ApplicationException, Exception;

   /**
    * Sync with CloudStack server Network offering.
    *
    * @throws ApplicationException unhandled application errors.
    * @throws Exception cloudstack unhandled errors
    */
   void syncNetworkOffering() throws ApplicationException, Exception;

   /**
    * Sync with CloudStack server Network.
    *
    * @throws ApplicationException unhandled application errors.
    * @throws Exception cloudstack unhandled errors
    */
   void syncNetwork() throws ApplicationException, Exception;

   /**
    * Sync with CloudStack server Compute Offering.
    *
    * @throws ApplicationException unhandled application errors.
    * @throws Exception cloudstack unhandled errors
    */
   void syncComputeOffering() throws ApplicationException, Exception;

   /**
    * Sync with CloudStack server Templates.
    *
    * @throws ApplicationException unhandled application errors.
    * @throws Exception cloudstack unhandled errors
    */
   void syncTemplates() throws ApplicationException, Exception;

   /**
   * Sync with CloudStack server Templates.
   *
   * @throws ApplicationException unhandled application errors.
   * @throws Exception cloudstack unhandled errors
   */
  void syncDepartment() throws ApplicationException, Exception;

  /**
   * Sync with Cloud Server Account.
   * @throws ApplicationException unhandled application errors.
   * @throws Exception cloudstack unhandled errors.
   */
  void syncVolume() throws ApplicationException, Exception;

  /**
   * Sync with CloudStack server Instance list.
   *
   * @throws Exception cloudstack unhandled errors
   */
  void syncInstances() throws  Exception;

  /**
   * Sync with CloudStack server Instance snapshot list.
   *
   * @throws Exception cloudstack unhandled errors
   */
  void syncVmSnapshots() throws Exception;

  /**
   * Sync with CloudStack server Instance snapshot list.
   *
   * @param Object response json object.
   * @throws Exception cloudstack unhandled errors
   */
  void syncResourceStatus(String Object) throws  Exception;

  /**
   * Sync with CloudStack server ResourceLimit list.
   *
   * @param Object response json object.
   * @throws Exception cloudstack unhandled errors
   */
  void syncResourceLimit() throws ApplicationException, Exception;

  /**
   * Sync with Cloud Server Account.
   * @throws ApplicationException unhandled application errors.
   * @throws Exception cloudstack unhandled errors.
   */
  void syncResourceLimitDomain(String domainId) throws ApplicationException, Exception;

  /**
   * Sync with Cloud Server Account.
   * @throws ApplicationException unhandled application errors.
   * @throws Exception cloudstack unhandled errors.
   */
  void syncResourceLimitDepartment(String domainId, String department) throws ApplicationException, Exception;

  /**
   * Sync with Cloud Server Account.
   * @throws ApplicationException unhandled application errors.
   * @throws Exception cloudstack unhandled errors.
   */
  void syncResourceLimitProject(String projectId) throws ApplicationException, Exception;

  /**
   * Sync with Cloud Server Account.
   * @throws ApplicationException unhandled application errors.
   * @throws Exception cloudstack unhandled errors.
   */
  void syncProject() throws ApplicationException, Exception;

}
