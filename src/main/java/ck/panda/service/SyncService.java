package ck.panda.service;

/**
 * Interface for synchronization with the CloudStack.
 *
 */
public interface SyncService {

    /**
     * consits of method to be called.
     * @throws Exception
     */
     void sync() throws Exception;
}
