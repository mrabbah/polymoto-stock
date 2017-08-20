CREATE TABLE parametrage
(
  id bigint NOT NULL,
  "version" bigint NOT NULL,
  afficher_remise_facture boolean NOT NULL,
  afficher_tva_facture boolean NOT NULL,
  CONSTRAINT parametrage_pkey PRIMARY KEY (id)
)
WITH (OIDS=FALSE);
ALTER TABLE parametrage OWNER TO root;