package cz.cuni.mff.ufal.textan.textpro.configs;

import cz.cuni.mff.ufal.textan.data.configs.DataConfig;
import cz.cuni.mff.ufal.textan.data.repositories.dao.*;
import cz.cuni.mff.ufal.textan.textpro.ITextPro;
import cz.cuni.mff.ufal.textan.textpro.TextPro;
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
public class TextProConfig {

    /**
     * Creates a Spring bean with the type ITextPro.
     * (This method is invoked only once at startup and than works like a singleton.)
     * @return
     */
    @Bean(initMethod = "learn")
    @DependsOn("logInterceptorHack")
    @Autowired
    public ITextPro textPro(
            IObjectTypeTableDAO objectTypeTableDAO,
            IObjectTableDAO objectTableDAO,
            IAliasTableDAO aliasTableDAO,
            IAliasOccurrenceTableDAO aliasOccurrenceTableDAO,
            IJoinedObjectsTableDAO joinedObjectsTableDAO,
            IRelationTypeTableDAO relationTypeTableDAO,
            IRelationTableDAO relationTableDAO,
            IRelationOccurrenceTableDAO relationOccurrenceTableDAO
            ) {
        return new TextPro(
                aliasOccurrenceTableDAO,
                relationTypeTableDAO,
                aliasTableDAO,
                joinedObjectsTableDAO,
                objectTableDAO,
                objectTypeTableDAO,
                relationOccurrenceTableDAO,
                relationTableDAO
        );
    }
}
