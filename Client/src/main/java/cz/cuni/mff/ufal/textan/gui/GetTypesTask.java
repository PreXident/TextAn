/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.gui;

import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.ObjectType;
import java.text.Collator;
import java.util.List;
import javafx.concurrent.Task;

/**
 * Simple task to get sorted list of object types.
 */
public class GetTypesTask extends Task<List<ObjectType>> {

    /** Client for communicating with the server. */
    final Client client;

    /**
     * Only constructor.
     * @param client communicator with the server
     */
    public GetTypesTask(final Client client) {
        this.client = client;
    }

    @Override
    protected List<ObjectType> call() throws Exception {
        List<ObjectType> types = client.getObjectTypesList();
        final Collator collator = Collator.getInstance();
        types.sort((type1, type2) -> collator.compare(type1.getName(), type2.getName()));
        types.add(0, null);
        return types;
    }
}
