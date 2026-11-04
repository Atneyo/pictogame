# Pictogame

## Project presentation

This application allows to play at Pictorush boardgame without playmaker for each round.

## TODO

- trouver comment faire prononcer les mots
- créer des listes de mots
    - créer une base de donnée
    - avoir une banque de mots
    - la personne peut choisir le nombre de mots pour chaque partie
    - sort une liste aléatoire
    - doit éviter sortir les même mots deux fois lors d'une partie
- l'appli doit permettre de lire les mots au top départ
- l'appli doit permettre de demander les cases à deviner et d'affihcer la réponse quand c'est voulu
    - il faut que la personne dont c'est le tour puisse rentrer le mot et si c'est pas validé c'est course pour les autres pour tenter et si c'est pas validé ça affiche la réponse
- l'appli doit permettre de compter les points et d'afficher la piste de score
- backend
    renvoyer une liste de mots en donnant en paramètre le nombre de mots


## PLAN
- créer la structure backend/ configurer le projet
- créer une base de données
- créer la route qui permet d'obtenir une liste de mots de taille n (n paramètre)
- gérer les exceptions
- créer des tests unitaires
- créer la structure frontend/ configurer le projet
- créer la base frontend (routes et page d'accueil)
- créer le fichier pour appeler l'API et une méthode pour appeler la route getWords
- créer la page où les mots sont appelés avec un bouton pour récupérer une liste qui permet de l'afficher, un bouton pour lancer la dictée/ un bouton pause
- ajouter des tests
- tout mettre dans un docker
- ajouter de nouvelles fonctionnalités


## Structure du code
```
pictogame/
├── docker-compose.yml
├── backend/                             # Spring Boot, Java, Maven
│   └── src/main/java/pictogame/
│       ├── models/
│       ├── repository/                  # Accès à la base de données
│       ├── endpoint/
│       ├── common/
│       ├── config/
│       ├── service/
│       ├── exception/
│       └── MyApplication.java
└── frontend/                            # React + Vite
    └── src/
        ├── App.jsx                      # Interface principale
        ├── services/
        │   └── api.js                   # Appels HTTP vers le backend
        ├── pages/
        ├── assets/
        │   └── styles/                  # Notamment global.css
        ├── hooks/
        │   └── useWordReader.js      # Synthèse vocale + intervalle
        └── components/
```

## Prerequis/ utilisé par le projet

Maven, Java 25, Spring Boot 4.1.2, 