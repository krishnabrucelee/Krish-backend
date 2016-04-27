package ck.panda.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.TemplateCost;
import ck.panda.domain.repository.jpa.TemplateCostRepository;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service implementation for Template cost entity.
 *
 */
@Service
public class TemplateCostServiceImpl implements TemplateCostService {

    /** Template repository reference. */
    @Autowired
    private TemplateCostRepository templateCostRepository;

    @Override
    public TemplateCost save(TemplateCost templateCost) throws Exception {
        return templateCostRepository.save(templateCost);
    }

    @Override
    public TemplateCost update(TemplateCost templateCost) throws Exception {
        return templateCostRepository.save(templateCost);
    }

    @Override
    public void delete(TemplateCost templateCost) throws Exception {
        templateCostRepository.delete(templateCost);
    }

    @Override
    public void delete(Long id) throws Exception {
        templateCostRepository.delete(id);
    }

    @Override
    public TemplateCost find(Long id) throws Exception {
        return templateCostRepository.findOne(id);
    }

    @Override
    public Page<TemplateCost> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return templateCostRepository.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<TemplateCost> findAll() throws Exception {
        return (List<TemplateCost>) templateCostRepository.findAll();
    }

    @Override
    public TemplateCost findByTemplateCost(Long templateId, Double cost) {
        return templateCostRepository.findByTemplateCost(templateId, cost);
    }

    @Override
    public List<TemplateCost> findAllByTemplateCost(Long templateId) {
        return templateCostRepository.findByTemplateCost(templateId);
    }
}
