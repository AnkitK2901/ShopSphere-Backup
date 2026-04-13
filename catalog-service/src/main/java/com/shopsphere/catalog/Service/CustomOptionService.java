package com.shopsphere.catalog.Service;

import com.shopsphere.catalog.Entity.CustomOption;

import java.util.List;

public interface CustomOptionService {
    public CustomOption saveOption(CustomOption option);

    public List<CustomOption> getAllOptions();
}
