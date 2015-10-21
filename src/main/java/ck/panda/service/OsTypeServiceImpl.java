package ck.panda.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.OsType;
import ck.panda.domain.repository.jpa.OsTypeRepository;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service implementation for OS type entity.
 *
 */
@Service
public class OsTypeServiceImpl implements OsTypeService {

    /** OS type repository reference. */
    @Autowired
    private OsTypeRepository ostyperepository;

    @Override
    public List<OsType> findAll() throws Exception {
        return (List<OsType>) ostyperepository.findAll();
    }

    @Override
    public OsType save(OsType ostype) throws Exception {
        return ostyperepository.save(ostype);
    }

    @Override
    public OsType update(OsType ostype) throws Exception {
        return ostyperepository.save(ostype);
    }

    @Override
    public void delete(OsType ostype) throws Exception {
        ostyperepository.delete(ostype);
    }

    @Override
    public void delete(Long id) throws Exception {
        ostyperepository.delete(id);
    }

    @Override
    public OsType find(Long id) throws Exception {
        return ostyperepository.findOne(id);
    }

    @Override
    public Page<OsType> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return null;
    }

}
