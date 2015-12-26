package ck.panda.domian.entity;

import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.User;
import java.time.ZonedDateTime;

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
        department.setUserName(name);
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
    public DepartmentBuilder createdDateTime(ZonedDateTime createdDateTime) {
        department.setCreatedDateTime(createdDateTime);
        return this;
    }

    /**
     * @param updatedDateTime DateTime.
     * @return updatedDateTime
     */
    public DepartmentBuilder updatedDateTime(ZonedDateTime updatedDateTime) {
        department.setUpdatedDateTime(updatedDateTime);
        return this;
    }

    /**
     * @param createdBy User
     * @return CreatedBy
     */
    public DepartmentBuilder createdBy(User createdBy) {
        department.setCreatedBy(createdBy.getId());
        return this;
    }

    /**
     * @param updatedBy User
     * @return UpdatedBy
     */
    public DepartmentBuilder updatedBy(User updatedBy) {
        department.setUpdatedBy(updatedBy.getId());
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
