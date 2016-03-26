package ck.panda.email.util;

/** Event for Resource */
public class Resource {

    /** Resource name */
    private String resourceName;

    /** Resource zone */
    private String zone;

    /** Resource percentage */
    private String percentage;

    /** Resource memory */
    private String memory;

    /** Resource cpu */
    private String cpu;

    /** Resource primaryStorage */
    private String primaryStorage;

    /** Resource ip */
    private String ip;

    /**
     * Get the resourceName of Resource.
     *
     * @return the resourceName
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Set the resourceName of Resource.
     *
     * @param resourceName the resourceName to set
     */
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * Get the zone of Resource.
     *
     * @return the zone
     */
    public String getZone() {
        return zone;
    }

    /**
     * Set the zone of Resource.
     *
     * @param zone the zone to set
     */
    public void setZone(String zone) {
        this.zone = zone;
    }

    /**
     * Get the percentage of Resource.
     *
     * @return the percentage
     */
    public String getPercentage() {
        return percentage;
    }

    /**
     * Set the percentage of Resource.
     *
     * @param percentage the percentage to set
     */
    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    /**
     * Get the memory of Resource.
     *
     * @return the memory
     */
    public String getMemory() {
        return memory;
    }

    /**
     * Set the memory of Resource.
     *
     * @param object the memory to set
     */
    public void setMemory(String memory) {
        this.memory = memory;
    }

    /**
     * Get the cpu of Resource.
     *
     * @return the cpu
     */
    public String getCpu() {
        return cpu;
    }

    /**
     * Set the cpu of Resource.
     *
     * @param cpu the cpu to set
     */
    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    /**
     * Get the primaryStorage of Resource.
     *
     * @return the primaryStorage
     */
    public String getPrimaryStorage() {
        return primaryStorage;
    }

    /**
     * Set the primaryStorage of Resource.
     *
     * @param primaryStorage the primaryStorage to set
     */
    public void setPrimaryStorage(String primaryStorage) {
        this.primaryStorage = primaryStorage;
    }

    /**
     * Get the ip of Resource.
     *
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * Set the ip of Resource.
     *
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

}
