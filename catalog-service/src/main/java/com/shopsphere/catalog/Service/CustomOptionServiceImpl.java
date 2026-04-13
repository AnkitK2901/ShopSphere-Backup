package com.shopsphere.catalog.Service;

import com.shopsphere.catalog.Entity.CustomOption;
import com.shopsphere.catalog.Repository.CustomOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomOptionServiceImpl implements CustomOptionService{

    @Autowired
    private CustomOptionRepository optionRepository;

    @Override
    public CustomOption saveOption(CustomOption option) {
        return optionRepository.save(option);
    }

    @Override
    public List<CustomOption> getAllOptions() {
        return optionRepository.findAll();
    }
}
