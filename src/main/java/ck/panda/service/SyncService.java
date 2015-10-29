package ck.panda.service;

import org.springframework.stereotype.Service;

/**
 * Synchronization of zone,domain, region , template with cloudStack.
 *
 */
@Service
public interface SyncService {

    /**
     * Sync method consists of method to be called.
     *
     * @throws Exception handles unhandled errors.
     */
     void sync() throws Exception;
}
