package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.StorageOffering;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service class for Storage Offering. This service provides basic CRUD and essential api's for Storage Offering related
 * business actions.
 */
@Service
public interface StorageOfferingService extends CRUDService<StorageOffering> {

    /**
     * To get list of Storage Offer from cloudstack server.
     *
     * @return os types list from server
     * @throws Exception unhandled errors.
     */
    List<StorageOffering> findAllFromCSServer() throws Exception;

    /**
     * To get Uuid list of Storage Offer from cloudstack server.
     *
     * @param uuid unique id.
     * @return storage tags
     */
    StorageOffering findUuid(String uuid);

    /**
     * To get Tags list of Storage Offer from cloudstack server.
     * @param userId user id
     * @param isActive unique id.
     * @return storage tags
     * @throws Exception unhandled errors
     */
    List<String> findTags(Long userId, Boolean isActive) throws Exception;

    /**
     * To get Tags list of Storage Offer from cloudstack server.
     *
     * @param tags tags.
     * @param userId user id
     * @return storage tags
     * @throws Exception unhandled errors
     */
    List<StorageOffering> findAllByTags(String tags, Long userId) throws Exception;

    /**
     * Soft delete for storage offering.
     *
     * @param storage object
     * @return storage
     * @throws Exception unhandled errors.
     */
    StorageOffering softDelete(StorageOffering storage) throws Exception;

    /**
     * To get Tags list of Storage Offer by domain.
     *
     * @param tags tags.
     * @param domainId domain id
     * @return storage tags
     * @throws Exception unhandled errors
     */
    List<StorageOffering> findByDomain(String tags, Long domainId) throws Exception;

    /**
     * Paging and Sorting for displaying more number of elements in list.
     *
     * @param domainId of the domain.
     * @param pagingAndSorting sortable method.
     * @return sorted values.
     * @throws Exception unhandled errors.
     */
    Page<StorageOffering> findAllByDomainId(Long domainId, PagingAndSorting pagingAndSorting) throws Exception;

    /**
     * Find all storage offerings by domain id and search text along with pagination.
     *
     * @param domainId of the storage offering.
     * @param pagingAndSorting for pagination.
     * @param searchText for storage offering.
     * @return storage offering.
     * @throws Exception if error occurs.
     */
    Page<StorageOffering> findAllByDomainIdAndSearchText(Long domainId, PagingAndSorting pagingAndSorting,
            String searchText) throws Exception;
}
