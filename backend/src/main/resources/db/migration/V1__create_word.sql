CREATE TABLE word (
                      id    BIGINT       NOT NULL AUTO_INCREMENT,
                      label VARCHAR(100) NOT NULL,
                      PRIMARY KEY (id),
                      CONSTRAINT uk_word_label UNIQUE (label)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_as_ci;

INSERT INTO word (label) VALUES
                             ('avion'), ('maison'), ('ordinateur'), ('soleil'), ('chat'),
                             ('chien'), ('arbre'), ('voiture'), ('fenêtre'), ('livre'),
                             ('table'), ('chaise'), ('école'), ('jardin'), ('fleur'),
                             ('montagne'), ('rivière'), ('nuage'), ('pomme'), ('bateau'),
                             ('étoile'), ('musique'), ('crayon'), ('télévision'), ('papillon'),
                             ('boulangerie'), ('parapluie'), ('hôpital'), ('bibliothèque'), ('éléphant');