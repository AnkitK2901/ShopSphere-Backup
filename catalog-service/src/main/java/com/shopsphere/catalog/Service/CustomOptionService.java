package com.shopsphere.catalog.Service;
import com.shopsphere.catalog.Entity.CustomOption;
import java.util.List;

public interface CustomOptionService {
    CustomOption saveOption(CustomOption option);
    List<CustomOption> getAllOptions();
    CustomOption getOptionById(Long id);
    void deleteOption(Long id);

}

