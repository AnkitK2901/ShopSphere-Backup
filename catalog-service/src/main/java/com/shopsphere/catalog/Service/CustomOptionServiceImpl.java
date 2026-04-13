package com.shopsphere.catalog.Service;
import com.shopsphere.catalog.Entity.CustomOption;
import com.shopsphere.catalog.Exception.ResourceNotFoundException;
import com.shopsphere.catalog.Repository.CustomOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
public class CustomOptionServiceImpl implements CustomOptionService {
    private static final Logger logger = LoggerFactory.getLogger(CustomOptionServiceImpl.class);

    @Autowired
    private CustomOptionRepository optionRepository;

    @Override
    public CustomOption saveOption(CustomOption option) {
        logger.info("Saving option: {}", option.getType());
        return optionRepository.save(option);
    }

    @Override
    public List<CustomOption> getAllOptions() {
        logger.info("Fetching all options");
        return optionRepository.findAll();
    }

    @Override
    public CustomOption getOptionById(Long id) {
        return optionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Option not found with id: " + id));
    }

    @Override
    public void deleteOption(Long id) {
        CustomOption option = getOptionById(id);
        optionRepository.delete(option);
    }
}