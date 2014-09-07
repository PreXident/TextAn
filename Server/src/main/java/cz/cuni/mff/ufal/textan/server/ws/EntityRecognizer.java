package cz.cuni.mff.ufal.textan.server.ws;

import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetEntitiesFromStringResponse;
import cz.cuni.mff.ufal.textan.commons.models.entityrecognizer.RecognizeEntitiesRequest;
import cz.cuni.mff.ufal.textan.commons.models.entityrecognizer.RecognizeEntitiesResponse;
import cz.cuni.mff.ufal.textan.commons.ws.IEntityRecognizer;
import cz.cuni.mff.ufal.textan.server.models.Entity;
import cz.cuni.mff.ufal.textan.server.services.NamedEntityRecognizerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebParam;
import java.util.List;

/**
 * A simple web service implementation, facade for the police.
 */
@javax.jws.WebService(
        serviceName = "EntityRecognizerService",
        portName = "EntityRecognizerPort",
        targetNamespace = "http://ws.commons.textan.ufal.mff.cuni.cz",
        wsdlLocation = "classpath:wsdl/EntityRecognizer.wsdl",
        endpointInterface = "cz.cuni.mff.ufal.textan.commons.ws.IEntityRecognizer")
public class EntityRecognizer implements IEntityRecognizer {

    private static final Logger LOG = LoggerFactory.getLogger(EntityRecognizer.class);

    private final NamedEntityRecognizerService namedEntityService;

    public EntityRecognizer(NamedEntityRecognizerService namedEntityService) {
        this.namedEntityService = namedEntityService;
    }

    @Override
    public RecognizeEntitiesResponse recognizeEntities(
            @WebParam(partName = "recognizeEntitiesRequest", name = "recognizeEntitiesRequest", targetNamespace = "http://models.commons.textan.ufal.mff.cuni.cz/entityRecognizer")
            RecognizeEntitiesRequest recognizeEntitiesRequest) {

        LOG.info("Executing operation recognizeEntities: {}", recognizeEntitiesRequest);

        List<Entity> entities = namedEntityService.getEntities(recognizeEntitiesRequest.getText(), null);

        RecognizeEntitiesResponse response = new RecognizeEntitiesResponse();
        for (Entity entity : entities) {
            response.getEntities().add(entity.toCommonsEntity());
        }

        LOG.info("Executed operation recognizeEntities: {}", response);
        return response;
    }
}
