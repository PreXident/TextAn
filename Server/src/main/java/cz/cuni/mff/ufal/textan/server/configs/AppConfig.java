package cz.cuni.mff.ufal.textan.server.configs;

import cz.cuni.mff.ufal.textan.server.DataProviderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cz.cuni.mff.ufal.textan.data.repositories.Data;

/*
 * Created by Petr Fanta on 8.12.13.
 */

/**
 * Spring configuration file for main context
 */
@Configuration
public class AppConfig {

    @Bean
    public Data dataSource() {
        return new Data();
    }

    @Bean
    public DataProviderService dataProviderService() {
        return new  DataProviderService(dataSource());
    }
}
