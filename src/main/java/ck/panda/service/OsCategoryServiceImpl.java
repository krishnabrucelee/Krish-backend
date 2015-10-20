package ck.panda.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.OsCategory;
import ck.panda.domain.repository.jpa.OsCategoryRepository;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service implementation for OS category entity.
 *
 */
@Service
public class OsCategoryServiceImpl implements OsCategoryService {

    /** OS category repository reference. */
    @Autowired
    private OsCategoryRepository oscategoryrepository;

    @Override
    public List<OsCategory> findAll() throws Exception {
        return (List<OsCategory>) oscategoryrepository.findAll();
    }

    @Override
    public OsCategory save(OsCategory oscategory) throws Exception {
        return oscategoryrepository.save(oscategory);
    }

    @Override
    public OsCategory update(OsCategory oscategory) throws Exception {
        return oscategoryrepository.save(oscategory);
    }

    @Override
    public void delete(OsCategory oscategory) throws Exception {
        oscategoryrepository.delete(oscategory);
    }

    @Override
    public void delete(Long id) throws Exception {
        oscategoryrepository.delete(id);
    }

    @Override
    public OsCategory find(Long id) throws Exception {
        return oscategoryrepository.findOne(id);
    }

    @Override
    public Page<OsCategory> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return null;
    }

}
