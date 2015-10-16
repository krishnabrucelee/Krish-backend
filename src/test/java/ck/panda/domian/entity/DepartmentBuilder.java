package ck.panda.domian.entity;

import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.User;
import org.joda.time.DateTime;

/**
 *
 * @author Krishna <krishnakumar@assistanz.com>
 */
public class DepartmentBuilder {

    private Department department;

    public DepartmentBuilder() {
        department = new Department();
    }

    public DepartmentBuilder id(Long id) {
        department.setId(id);
        return this;
    }

    public DepartmentBuilder name(String name) {
        department.setName(name);
        return this;
    }

    public DepartmentBuilder description(String description) {
        department.setDescription(description);
        return this;
    }

    public DepartmentBuilder createdDateTime(DateTime createdDateTime) {
        department.setCreatedDateTime(createdDateTime);
        return this;
    }

    public DepartmentBuilder lastModifiedDateTime(DateTime lastModifiedDateTime) {
        department.setLastModifiedDateTime(lastModifiedDateTime);
        return this;
    }

    public DepartmentBuilder createdBy(User createdBy) {
        department.setCreatedBy(createdBy);
        return this;
    }

    public DepartmentBuilder updatedBy(User updatedBy) {
        department.setUpdatedBy(updatedBy);
        return this;
    }

    public DepartmentBuilder version(Long version) {
        department.setVersion(version);
        return this;
    }

    public Department build() {
        return department;
    }
}
