package com.sharyar.Electrify.ElectronicsShop.config;

import com.sharyar.Electrify.ElectronicsShop.controllers.CategoryController;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectConfig {

    @Bean
    public ModelMapper getModelMapper(){
        return new ModelMapper();
    }


}
