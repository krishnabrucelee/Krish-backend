package ck.panda.email.util;

/** Event for Usage */
public class Usage {

    /** Invoice domain name. */
    private String domainName;

    /** Invoice domain name. */
    private String domainUserName;

    /** start date. */
    private String startDate;

    /** End date. */
    private String endDate;

    /** Status for usage. */
    private String status;

    /**
     * Get the domainName of Usage.
     *
     * @return the domainName
     */
    public String getDomainName() {
        return domainName;
    }

    /**
     * Set the domainName of Usage.
     *
     * @param domainName the domainName to set
     */
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    /**
     * Get the domainUserName of Usage.
     *
     * @return the domainUserName
     */
    public String getDomainUserName() {
        return domainUserName;
    }

    /**
     * Set the domainUserName of Usage.
     *
     * @param domainUserName the domainUserName to set
     */
    public void setDomainUserName(String domainUserName) {
        this.domainUserName = domainUserName;
    }

    /**
     * Get the startDate of Usage.
     *
     * @return the startDate
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Set the startDate of Usage.
     *
     * @param startDate the startDate to set
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * Get the endDate of Usage.
     *
     * @return the endDate
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Set the endDate of Usage.
     *
     * @param endDate the endDate to set
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * Get the status of Usage.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the status of Usage.
     *
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

}
