package ck.panda.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.BillableItem;
import ck.panda.domain.repository.jpa.BillableItemRepository;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * BillableItemService service implementation class.
 *
 */
@Service
public class BillableItemServiceImpl implements BillableItemService {

  /** Logger attribute. */
  private static final Logger LOGGER = LoggerFactory.getLogger(BillableItemServiceImpl.class);

  /** BillableItem repository reference. */
  @Autowired
  private BillableItemRepository billableItemRepo;

  @Override
  public BillableItem save(BillableItem billableItem) throws Exception {
      LOGGER.debug("Billabel item created");
      return billableItemRepo.save(billableItem);
  }

  @Override
  public BillableItem update(BillableItem billableItem) throws Exception {
      LOGGER.debug("Billabel item updated");
    return billableItemRepo.save(billableItem);
  }

  @Override
  public void delete(BillableItem domain) throws Exception {
    billableItemRepo.delete(domain);
  }

  @Override
  public void delete(Long id) throws Exception {
    billableItemRepo.delete(id);
  }

  @Override
  public BillableItem find(Long id) throws Exception {
      return billableItemRepo.findOne(id);
  }

  @Override
  public Page<BillableItem> findAll(PagingAndSorting pagingAndSorting) throws Exception {
    return billableItemRepo.findAll(pagingAndSorting.toPageRequest());
  }

  @Override
  public List<BillableItem> findAll() throws Exception {
      return (List<BillableItem>) billableItemRepo.findAll();
  }

}

