package ck.panda.email.util;

public class Alert {

    /** Alert Subject */
    private String subject;

    /** Alert details */
    private String details;

    /** Alert zone */
    private String zone;

    /** Alert podId */
    private String podId;

    /**
     * Get the subject of Alert.
     *
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Set the subject of Alert.
     *
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Get the details of Alert.
     *
     * @return the details
     */
    public String getDetails() {
        return details;
    }

    /**
     * Set the details of Alert.
     *
     * @param details the details to set
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * Get the zone of Alert.
     *
     * @return the zone
     */
    public String getZone() {
        return zone;
    }

    /**
     * Set the zone of Alert.
     *
     * @param zone the zone to set
     */
    public void setZone(String zone) {
        this.zone = zone;
    }

    /**
     * Get the podId of Alert.
     *
     * @return the podId
     */
    public String getPodId() {
        return podId;
    }

    /**
     * Set the podId of Alert.
     *
     * @param podId the podId to set
     */
    public void setPodId(String podId) {
        this.podId = podId;
    }

}
