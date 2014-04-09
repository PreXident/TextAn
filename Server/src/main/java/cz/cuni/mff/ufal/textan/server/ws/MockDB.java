package cz.cuni.mff.ufal.textan.server.ws;

import cz.cuni.mff.ufal.textan.commons.models.ObjectType;
import cz.cuni.mff.ufal.textan.commons.models.Relation;
import cz.cuni.mff.ufal.textan.commons.models.RelationType;

import java.util.ArrayList;
import java.util.List;

/**
 * Class mocking db.
 */
public class MockDB {

    static public final List<ObjectType> objectTypes = new ArrayList<>();
    static public final List<cz.cuni.mff.ufal.textan.commons.models.Object> objects = new ArrayList<>();
    static public final List<RelationType> relationTypes = new ArrayList<>();
    static public final ArrayList<Relation> relations = new ArrayList<>();

    static {
        //ObjectTypes
        final ObjectType person = new ObjectType();
        person.setId(0);
        person.setName("Person");
        objectTypes.add(person);
        final ObjectType gun = new ObjectType();
        gun.setId(1);
        gun.setName("Gun");
        objectTypes.add(gun);
        //Objects
        final cz.cuni.mff.ufal.textan.commons.models.Object pepa = new cz.cuni.mff.ufal.textan.commons.models.Object();
        pepa.setId(0);
        pepa.setObjectType(person);
        pepa.setAliases(new cz.cuni.mff.ufal.textan.commons.models.Object.Aliases());
        pepa.getAliases().getAlias().add("Pepa");
        objects.add(pepa);
        final cz.cuni.mff.ufal.textan.commons.models.Object flinta = new cz.cuni.mff.ufal.textan.commons.models.Object();
        flinta.setId(1);
        flinta.setObjectType(gun);
        flinta.setAliases(new cz.cuni.mff.ufal.textan.commons.models.Object.Aliases());
        flinta.getAliases().getAlias().add("flinta");
        objects.add(flinta);
        final cz.cuni.mff.ufal.textan.commons.models.Object franta = new cz.cuni.mff.ufal.textan.commons.models.Object();
        franta.setId(2);
        franta.setObjectType(person);
        franta.setAliases(new cz.cuni.mff.ufal.textan.commons.models.Object.Aliases());
        franta.getAliases().getAlias().add("Franta");
        objects.add(franta);
        //RelationTypes
        final RelationType vlastnit = new RelationType();
        vlastnit.setId(0);
        vlastnit.setName("vlastnit");
        final RelationType zabit = new RelationType();
        zabit.setId(1);
        zabit.setName("zabit");
        //Relation
        //Pepa vlastni flintu
        final Relation pepaVlastniFlintu = new Relation();
        pepaVlastniFlintu.setId(0);
        pepaVlastniFlintu.setRelationType(vlastnit);
        pepaVlastniFlintu.setObjectInRelationIds(new Relation.ObjectInRelationIds());
        final Relation.ObjectInRelationIds.InRelation pepaVlastni = new Relation.ObjectInRelationIds.InRelation();
        pepaVlastni.setObjectId(pepa.getId());
        pepaVlastni.setOrder(-1);
        pepaVlastniFlintu.getObjectInRelationIds().getInRelations().add(pepaVlastni);
        final Relation.ObjectInRelationIds.InRelation flintaVlastnena = new Relation.ObjectInRelationIds.InRelation();
        flintaVlastnena.setObjectId(flinta.getId());
        flintaVlastnena.setOrder(1);
        pepaVlastniFlintu.getObjectInRelationIds().getInRelations().add(flintaVlastnena);
        relations.add(pepaVlastniFlintu);
        //Pepa zastrelil flintou Frantu
        final Relation pepaZastrelilFrantu = new Relation();
        pepaZastrelilFrantu.setId(1);
        pepaZastrelilFrantu.setRelationType(zabit);
        pepaZastrelilFrantu.setObjectInRelationIds(new Relation.ObjectInRelationIds());
        final Relation.ObjectInRelationIds.InRelation pepaStrili = new Relation.ObjectInRelationIds.InRelation();
        pepaStrili.setObjectId(pepa.getId());
        pepaStrili.setOrder(-1);
        pepaZastrelilFrantu.getObjectInRelationIds().getInRelations().add(pepaStrili);
        final Relation.ObjectInRelationIds.InRelation flintaStrili = new Relation.ObjectInRelationIds.InRelation();
        flintaStrili.setObjectId(flinta.getId());
        flintaStrili.setOrder(1);
        pepaZastrelilFrantu.getObjectInRelationIds().getInRelations().add(flintaStrili);
        final Relation.ObjectInRelationIds.InRelation frantaZastrelen = new Relation.ObjectInRelationIds.InRelation();
        frantaZastrelen.setObjectId(franta.getId());
        frantaZastrelen.setOrder(2);
        pepaZastrelilFrantu.getObjectInRelationIds().getInRelations().add(frantaZastrelen);
        relations.add(pepaZastrelilFrantu);
    }

    /**
     * Utility classes need no constructor.
     */
    private MockDB() {
        //nothing
    }
}
