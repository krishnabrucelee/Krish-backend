package ck.panda.service;

public interface UpdateResourceCountService {

    /**
     * Update count for domain by resource type and resource object.
     *
     * @param resourceObject validation check for resource object such as VM, Volume, Network, IP.
     * @param resourceType type of resource.
     * @param accountTypeId resource account type id such as project id, department id, domain id.
     * @param accountType resource account type such as project, department, domain.
     * @param status delete or create resource.
     * @return validation message, whether success or error.
     * @throws Exception Quota limit check exception.
     */
    String QuotaUpdateByResourceObject( Object resourceObject, String resourceType, Long accountTypeId, String accountType, String status) throws Exception;

}
