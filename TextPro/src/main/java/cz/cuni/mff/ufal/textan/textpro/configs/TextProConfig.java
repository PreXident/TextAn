package cz.cuni.mff.ufal.textan.textpro.configs;

import cz.cuni.mff.ufal.textan.data.configs.DataConfig;
import cz.cuni.mff.ufal.textan.data.repositories.dao.*;
import cz.cuni.mff.ufal.textan.textpro.ITextPro;
import cz.cuni.mff.ufal.textan.textpro.TextPro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring configuration for TextPro subproject.
 * @author Petr Fanta
 * @see cz.cuni.mff.ufal.textan.data.configs.DataConfig
 */
@Configuration
@Import(DataConfig.class)
public class TextProConfig {

    @Autowired
    IAliasOccurrenceTableDAO aliasOccurrenceTableDAO;

    @Autowired
    IAliasTableDAO aliasTableDAO;

    @Autowired
    IJoinedObjectsTableDAO joinedObjectsTableDAO;

    @Autowired
    IObjectTableDAO objectTableDAO;

    @Autowired
    IObjectTypeTableDAO objectTypeTableDAO;

    @Autowired
    IRelationOccurrenceTableDAO relationOccurrenceTableDAO;

    @Autowired
    IRelationTableDAO relationTableDAO;

    @Autowired
    IRelationTypeTableDAO relationTypeTableDAO;

    /**
     * Creates a Spring bean with the type ITextPro.
     * (This method is invoked only once at startup and than works like a singleton.)
     * @return
     */
    @Bean
    public ITextPro textPro() {
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
