-- inserts

INSERT INTO `document`(`id_document`,`added`,`processed`,`text`)
	VALUES (1, NOW(), NOW(), "Josef Novak, známý také pod pseudonymem Pepíček, narozen dne 19.10.1975 byl spatřen dne 18.9.2013 v ulici K novým domům v Praze 5. J. Novak upoutal pozornost postaršího páru, když poškozoval cizí majetek baseballovou pálkou. Policejní hlídka, kterou pár přivolal, po příjezdu na místo činu v 10:28 zajistila podezřelého a důkazy. Podezřelý pan Novák svým chováním poškodil dodávku Ford Tranzit s SPZ AHL 30-24 registrovanou na společnost Čistírna a prádelna Karel Novák a přilehlý stánek s rychlým občerstvením U Mrože. Podezřelý byl následně převezen do vazby a obviněn dle zákona č. 106/1999 Sb. z výtržnictví.");

INSERT INTO `objecttype`(`id_object_type`,`name`) VALUES(1, "Osoba");
INSERT INTO `objecttype`(`id_object_type`,`name`) VALUES(2, "Datum");
INSERT INTO `objecttype`(`id_object_type`,`name`) VALUES(3, "Ulice");
INSERT INTO `objecttype`(`id_object_type`,`name`) VALUES(4, "Město");
INSERT INTO `objecttype`(`id_object_type`,`name`) VALUES(5, "Zbraň");
INSERT INTO `objecttype`(`id_object_type`,`name`) VALUES(6, "Čas");
INSERT INTO `objecttype`(`id_object_type`,`name`) VALUES(7, "Typ automobilu");
INSERT INTO `objecttype`(`id_object_type`,`name`) VALUES(8, "SPZ");
INSERT INTO `objecttype`(`id_object_type`,`name`) VALUES(9, "Podnik");
INSERT INTO `objecttype`(`id_object_type`,`name`) VALUES(10, "Zákon");
INSERT INTO `objecttype`(`id_object_type`,`name`) VALUES(11, "Droga");

-- TODO: better names for relation types
INSERT INTO `relationtype`(`id_relation_type`,`name`) VALUES(1, "narozen");
INSERT INTO `relationtype`(`id_relation_type`,`name`) VALUES(2, "viděn");
INSERT INTO `relationtype`(`id_relation_type`,`name`) VALUES(3, "poškozoval");
INSERT INTO `relationtype`(`id_relation_type`,`name`) VALUES(4, "zadržen");

INSERT INTO `object`(`id_object`,`id_object_type`,`data`) VALUES (1, 1, "");
	INSERT INTO `alias`(`id_alias`,`id_object`,`alias`) VALUES (1, 1, "Josef Novak");
		INSERT INTO `aliasoccurrence`(`id_alias_occurence`,`id_alias`,`id_document`,`position`) VALUES(1, 1, 1, 0);
	INSERT INTO `alias`(`id_alias`,`id_object`,`alias`) VALUES (2, 1, "Pepíček");
		INSERT INTO `aliasoccurrence`(`id_alias_occurence`,`id_alias`,`id_document`,`position`) VALUES(2, 2, 1, 40);
	INSERT INTO `alias`(`id_alias`,`id_object`,`alias`) VALUES (3, 1, "J. Novak");
		INSERT INTO `aliasoccurrence`(`id_alias_occurence`,`id_alias`,`id_document`,`position`) VALUES(3, 3, 1, 131);
	INSERT INTO `alias`(`id_alias`,`id_object`,`alias`) VALUES (4, 1, "Novák");
		INSERT INTO `aliasoccurrence`(`id_alias_occurence`,`id_alias`,`id_document`,`position`) VALUES(4, 4, 1, 344);

INSERT INTO `object`(`id_object`,`id_object_type`,`data`) VALUES (2, 2, ""); 
	INSERT INTO `alias`(`id_alias`,`id_object`,`alias`) VALUES (5, 2, "19.10.1975");
		INSERT INTO `aliasoccurrence`(`id_alias_occurence`,`id_alias`,`id_document`,`position`) VALUES(5, 5, 1, 61);

INSERT INTO `object`(`id_object`,`id_object_type`,`data`) VALUES (3, 2, ""); 
	INSERT INTO `alias`(`id_alias`,`id_object`,`alias`) VALUES (6, 3, "18.9.2013");
		INSERT INTO `aliasoccurrence`(`id_alias_occurence`,`id_alias`,`id_document`,`position`) VALUES(6, 6, 1, 88);

INSERT INTO `object`(`id_object`,`id_object_type`,`data`) VALUES (4, 3, ""); 
	INSERT INTO `alias`(`id_alias`,`id_object`,`alias`) VALUES (7, 4, "K novým domům");
		INSERT INTO `aliasoccurrence`(`id_alias_occurence`,`id_alias`,`id_document`,`position`) VALUES(7, 7, 1, 106);

INSERT INTO `object`(`id_object`,`id_object_type`,`data`) VALUES (5, 4, ""); 
	INSERT INTO `alias`(`id_alias`,`id_object`,`alias`) VALUES (8, 5, "Praze 5");
		INSERT INTO `aliasoccurrence`(`id_alias_occurence`,`id_alias`,`id_document`,`position`) VALUES(8, 8, 1, 122);

INSERT INTO `object`(`id_object`,`id_object_type`,`data`) VALUES (6, 5, ""); 
	INSERT INTO `alias`(`id_alias`,`id_object`,`alias`) VALUES (9, 6, "baseballovou pálkou");
		INSERT INTO `aliasoccurrence`(`id_alias_occurence`,`id_alias`,`id_document`,`position`) VALUES(9, 9, 1, 204);

INSERT INTO `relation`(`id_relation`,`id_relation_type`) VALUES (1, 1);
	INSERT INTO `relationoccurrence`(`id_relation_occurence`,`id_relation`,`id_document`,`position`,`anchor`) VALUES (1, 1, 1, 49, "narozen");

	INSERT INTO `isinrelation`(`id_is_in_relation`, `id_relation`, `id_object`,`order_in_relation`) VALUES (1, 1, 1, 0);
	INSERT INTO `isinrelation`(`id_is_in_relation`, `id_relation`, `id_object`,`order_in_relation`) VALUES (2, 1, 2, 0);



-- todo: finish

-- cleaning
-- todo:
















