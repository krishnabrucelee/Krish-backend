package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.IpAddress;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.Nic;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.repository.jpa.IpaddressRepository;
import ck.panda.domain.repository.jpa.NicRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackNicService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 * IpAddress service implementation class.
 */
@Service
public class IpaddressServiceImpl implements IpaddressService {

    @Override
    public IpAddress save(IpAddress t) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IpAddress update(IpAddress t) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void delete(IpAddress t) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(Long id) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public IpAddress find(Long id) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Page<IpAddress> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<IpAddress> findAll() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IpAddress findbyUUID(String uuid) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IpAddress softDelete(IpAddress ipaddress) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<IpAddress> findByNetwork(Long networkId) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<IpAddress> findAllFromCSServer() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Page<IpAddress> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
