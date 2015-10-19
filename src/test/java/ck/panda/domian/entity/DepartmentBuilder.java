package ck.panda.domian.entity;

import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.User;
import org.joda.time.DateTime;

/**
 *
 * @author Krishna <krishnakumar@assistanz.com>
 */
public class DepartmentBuilder {

    /**
     * Department Object.
     */
    private Department department;

    /**
     * Department Builder.
     */
    public DepartmentBuilder() {
        department = new Department();
    }

    /**
     * @param id Department.
     * @return id.
     */
    public DepartmentBuilder id(Long id) {
        department.setId(id);
        return this;
    }

    /**
     * @param name Department name.
     * @return name
     */
    public DepartmentBuilder name(String name) {
        department.setName(name);
        return this;
    }

    /**
     * @param description Department description.
     * @return description
     */
    public DepartmentBuilder description(String description) {
        department.setDescription(description);
        return this;
    }

    /**
     * @param createdDateTime DateTime.
     * @return createdDateTime
     */
    public DepartmentBuilder createdDateTime(DateTime createdDateTime) {
        department.setCreatedDateTime(createdDateTime);
        return this;
    }

    /**
     * @param lastModifiedDateTime DateTime.
     * @return lastModifiedDateTime
     */
    public DepartmentBuilder lastModifiedDateTime(DateTime lastModifiedDateTime) {
        department.setLastModifiedDateTime(lastModifiedDateTime);
        return this;
    }

    /**
     * @param createdBy User
     * @return CreatedBy
     */
    public DepartmentBuilder createdBy(User createdBy) {
        department.setCreatedBy(createdBy);
        return this;
    }

    /**
     * @param updatedBy User
     * @return UpdatedBy
     */
    public DepartmentBuilder updatedBy(User updatedBy) {
        department.setUpdatedBy(updatedBy);
        return this;
    }

    /**
     * @param version version
     * @return version
     */
    public DepartmentBuilder version(Long version) {
        department.setVersion(version);
        return this;
    }

    /**
     * @return department
     */
    public Department build() {
        return department;
    }
}
