/*
 *  Created by Vaclav Pernicka
 */

package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.views.EntityView;

import java.util.List;

/**
 *
 * @author Vaclav Pernicka
 */
public interface IEntityViewDAO {
    List<EntityView> findAll();
}
