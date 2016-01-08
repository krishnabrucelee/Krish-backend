package ck.panda.domain.entity;

/**
 * Stuff entity.
 *
 */
public class Stuff {

    /** Description attribute. */
    private String description;

    /** Owner attribute. */
    private DomainUser owner;

    /** Details attribute. */
    private String details;

    /**
     * Get the description.
     * 
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description.
     *
     * @param description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the owner.
     * 
     * @return owner
     */
    public DomainUser getOwner() {
        return owner;
    }

    /**
     * Set the owner.
     * 
     * @param owner to set
     */
    public void setOwner(DomainUser owner) {
        this.owner = owner;
    }

    /**
     * Get the details.
     * 
     * @return details
     */
    public String getDetails() {
        return details;
    }

    /**
     * Set the details.
     *
     * @param details to set
     */
    public void setDetails(String details) {
        this.details = details;
    }
}
