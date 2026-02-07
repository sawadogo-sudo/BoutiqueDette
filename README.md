
Projet : Carnet de dettes numérique pour boutique de quartier

Membres du groupe:
Sawadogo Amidou
Sawadogo Loukouman


Ce projet consiste à développer une application mobile Android permettant à un boutiquier de gérer numériquement les crédits accordés à ses clients. L'objectif est de remplacer le carnet papier traditionnel afin d'éviter les erreurs, les pertes d'informations et faciliter le suivi des remboursements.

		1. Architecture du Projet

Nous avons structuré ce projet autour de deux piliers pour garantir la robustesse et la centralisation des données.

		a. Architecture Logicielle : MVVM (Model-View-ViewModel)

L'application est développée en Java  en utilisant le pattern MVVM :
Model :Gère la logique métier et la communication avec la source de données distante.
View : Gère l'interface utilisateur (Activities et fichiers XML).
ViewModel :Sert d'intermédiaire, préparant les données pour la vue tout en survivant aux changements de configuration.

		 b. Architecture Système : Client-Serveur

Conformément aux consignes, aucune donnée n'est stockée uniquement en local:

Client :Application Android (Java).
Serveur distant : Utilisation d'un service de base de données en ligne (PostgreSQL via Supabase).
Communication : L'accès se fait via des API sécurisées.

		 2. Fonctionnalités implémentées

L'application intègre les fonctionnalités suivantes :
Authentification :Accès sécurisé par identifiant et mot de passe pour le boutiquier.
Gestion des clients :Ajout, modification, suppression et listing des clients (Nom, Téléphone, Adresse).
Gestion des dettes : Enregistrement par produit, montant et date.
Gestion des paiements :Enregistrement des remboursements et recalcul automatique du solde restant.
Tableau de bord : Liste des clients avec solde total et mise en évidence des clients les plus endettés.
Historique :Visualisation complète des dettes et paiements par client.



		3. Étapes d'installation et de test 

		a. Installation

		1. Cloner le dépôt : git clone 				 https://github.com/sawadogo-sudo/boutiquedette.git
		2. Ouvrir le projet dans Android Studio.
		3. Synchroniser le projet avec les fichiers Gradle.
		b. Test
1. S'assurer que l'appareil dispose d'une connexion Internet.
2. Lancer l'application (Run app).
3. Se connecter avec les identifiants configurés.

		conclusion

le développement de l'application Boutiquedette a permis de répondre efficacement aux problématiques
 concrètes de gestion rencontrées par les commerçants de proximité. Ce projet nous a non seulement 
 permis de maîtriser l'interaction entre une interface mobile native en Java et un service de backend 
 distant, mais aussi de respecter rigoureusement les contraintes d'un cahier des charges professionnel.
Le bilan de ce travail s'articule autour de trois points clés :
Centralisation : Grâce à l'utilisation d'une base de données distante (Supabase), la sécurité des données 
est garantie contre la perte ou le vol du matériel physique, contrairement au carnet papier traditionnel.
Fiabilité : De plus, l'automatisation des calculs de solde élimine les erreurs manuelles courantes, assurant 
ainsi une gestion rigoureuse des remboursements.
Perspectives : Enfin, l'architecture MVVM choisie offre une grande flexibilité, permettant d'envisager
 sereinement l'ajout de fonctionnalités futures telles que l'envoi de rappels par SMS ou l'affichage de 
 graphiques statistiques.
En somme, ce projet constitue une base solide pour la modernisation des outils de gestion des boutiques
 de quartier au Burkina Faso.
