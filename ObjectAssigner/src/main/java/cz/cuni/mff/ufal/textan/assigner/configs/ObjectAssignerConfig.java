package cz.cuni.mff.ufal.textan.assigner.configs;

import cz.cuni.mff.ufal.textan.data.configs.DataConfig;
import cz.cuni.mff.ufal.textan.data.repositories.dao.*;
import cz.cuni.mff.ufal.textan.assigner.IObjectAssigner;
import cz.cuni.mff.ufal.textan.assigner.ObjectAssigner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

/**
 * Spring configuration for TextPro subproject.
 * @author Petr Fanta
 * @see cz.cuni.mff.ufal.textan.data.configs.DataConfig
 */
@Configuration
@Import(DataConfig.class)
public class ObjectAssignerConfig {

    /**
     * Creates a Spring bean with the type IObjectAssigner.
     * This method is invoked only once at startup and then works like a singleton.
     * @param objectTableDAO object DAO to use
     * @param aliasTableDAO alias DAO to use
     * @param documentTableDAO document DAO to use
     * @return a Spring bean with the type IObjectAssigner
     */
    @Bean(initMethod = "learn")
    @DependsOn({"transactionManager", "exceptionTranslation"})
    @Autowired
    public IObjectAssigner textPro(
            final IObjectTableDAO objectTableDAO,
            final IAliasTableDAO aliasTableDAO,
            final IDocumentTableDAO documentTableDAO) {
        return new ObjectAssigner(
                aliasTableDAO,
                objectTableDAO,
                documentTableDAO
        );
    }
}
