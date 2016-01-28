package ck.panda.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Item;
import ck.panda.domain.repository.jpa.ItemRepository;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * ItemService service implementation class.
 *
 */
@Service
public class ItemServiceImpl implements ItemService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemServiceImpl.class);

    /** Item repository reference. */
    @Autowired
    private ItemRepository billableItemRepo;

    @Override
    public Item save(Item item) throws Exception {
        LOGGER.debug("Billabel item created");
        return billableItemRepo.save(item);
    }

    @Override
    public Item update(Item item) throws Exception {
        LOGGER.debug("Billabel item updated");
        return billableItemRepo.save(item);
    }

    @Override
    public void delete(Item domain) throws Exception {
        billableItemRepo.delete(domain);
    }

    @Override
    public void delete(Long id) throws Exception {
        billableItemRepo.delete(id);
    }

    @Override
    public Item find(Long id) throws Exception {
        return billableItemRepo.findOne(id);
    }

    @Override
    public Page<Item> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return billableItemRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Item> findAll() throws Exception {
        return (List<Item>) billableItemRepo.findAll();
    }

    @Override
    public Page<Item> findAllByIsActive(PagingAndSorting pagingAndSorting, Boolean isActive) throws Exception {
        return billableItemRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), isActive);
    }

    @Override
    public List<Item> findAllByIsActive(Boolean isActive) throws Exception {
        return billableItemRepo.findAllByIsActive(isActive);
    }
}
