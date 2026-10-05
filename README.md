# 🏦 Projet Systèmes Distribués – Application eBank

## 📌 Description

Ce projet consiste à développer une application bancaire basée sur une **architecture microservices** dans le cadre du module **Systèmes Parallèles et Distribués**.

L'application est organisée autour de plusieurs microservices indépendants permettant de gérer les différentes fonctionnalités du système bancaire.

Actuellement, le projet contient principalement les deux microservices suivants :

* **`ebank-service`** : gestion des comptes bancaires.
* **`customer-service`** : gestion des clients.

Le projet est développé progressivement et sera enrichi avec d'autres composants et fonctionnalités au fur et à mesure de son avancement.

---

## 🏗️ Architecture du projet

```text
                         ┌──────────────────────┐
                         │      eBank App       │
                         └──────────┬───────────┘
                                    │
                    ┌───────────────┴───────────────┐
                    │                               │
                    ▼                               ▼
          ┌──────────────────┐             ┌──────────────────┐
          │  Customer        │             │     eBank        │
          │  Service         │             │     Service      │
          │                  │             │                  │
          │ Gestion clients  │             │ Gestion comptes  │
          └──────────────────┘             └──────────────────┘
                    │                               │
                    └───────────────┬───────────────┘
                                    │
                                    ▼
                           Architecture REST
```

---

## 📦 Microservices

### 👤 Customer Service

Le microservice `customer-service` est dédié à la gestion des clients de l'application bancaire.

Il constitue un service indépendant pouvant être consommé à travers des APIs REST.

**Fonctionnalités développées :**

* Gestion des clients
* Exposition des données via une API REST
* Documentation de l'API avec Swagger/OpenAPI

---

### 💳 eBank Service

Le microservice `ebank-service` est dédié à la gestion des comptes bancaires.

Il permet notamment de gérer les informations relatives aux comptes bancaires.

**Fonctionnalités développées :**

* Gestion des comptes bancaires
* Création de comptes
* Consultation des comptes
* Recherche de comptes
* API REST
* Persistance des données avec JPA
* Base de données H2
* Documentation de l'API avec Swagger/OpenAPI

---

## 🛠️ Technologies utilisées

| Technologie          | Utilisation                            |
| -------------------- | -------------------------------------- |
| ☕ Java               | Langage de programmation               |
| 🌱 Spring Boot       | Développement des microservices        |
| 🌐 Spring Web        | APIs REST                              |
| 🗄️ Spring Data JPA  | Accès aux données                      |
| 💾 H2 Database       | Base de données                        |
| 🔍 Hibernate         | ORM                                    |
| 📚 Swagger / OpenAPI | Documentation et test des APIs         |
| 🔎 Eureka            | Service Discovery                      |
| ⚙️ Maven             | Gestion des dépendances et compilation |
| 🐙 Git / GitHub      | Gestion du code source                 |
| 💻 IntelliJ IDEA     | Environnement de développement         |

---

## 📁 Structure du projet

```text
ebank-ms-app/
│
├── customer-service/
│   ├── src/
│   └── pom.xml
│
├── ebank-service/
│   ├── src/
│   └── pom.xml
│
├── src/
├── .gitignore
├── .gitattributes
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

---

## ⚙️ Prérequis

Avant de lancer le projet, il faut disposer de :

* Java JDK
* Maven
* Git
* IntelliJ IDEA ou un autre IDE compatible Java
* Un navigateur Web pour accéder à Swagger

---

## 🚀 Installation

### 1. Cloner le projet

```bash
git clone https://github.com/hanane-ait/Projet---Syst-mes-Distribu-s2026.git
```

### 2. Accéder au projet

```bash
cd Projet---Syst-mes-Distribu-s2026
```

### 3. Compiler le projet

Avec Maven :

```bash
mvn clean install
```

ou avec le Maven Wrapper :

```bash
mvnw clean install
```

Sous Windows :

```cmd
mvnw.cmd clean install
```

---

# ▶️ Lancement des microservices

Les microservices doivent être lancés séparément depuis IntelliJ IDEA ou depuis le terminal.

## 💳 Ebank Service

Le microservice `ebank-service` utilise actuellement le port :

```text
8057
```

Une fois le service lancé, l'application est accessible à :

```text
http://localhost:8057
```

---

## 👤 Customer Service

Le port du `customer-service` dépend de la configuration définie dans son fichier `application.properties` ou `application.yml`.

Après le démarrage, l'URL correspondante peut être utilisée pour accéder à ses APIs.

---

# 📖 Documentation Swagger

Swagger permet de visualiser, tester et documenter les APIs REST des microservices.

## 💳 Swagger - Ebank Service

Pour accéder à Swagger du `ebank-service` :

```text
http://localhost:8057/swagger-ui/index.html
```

La documentation OpenAPI peut également être consultée via :

```text
http://localhost:8057/v3/api-docs
```

---

## 👤 Swagger - Customer Service

Pour le `customer-service`, l'URL dépend du port configuré.

Format :

```text
http://localhost:<PORT>/swagger-ui/index.html
```

---

# 🧪 Tests avec Swagger

Les APIs REST ont été testées à l'aide de l'interface Swagger UI.

Swagger permet notamment de :

* consulter les endpoints disponibles ;
* envoyer des requêtes HTTP ;
* tester les méthodes `GET`, `POST`, `PUT` et `DELETE` lorsqu'elles sont disponibles ;
* visualiser les réponses retournées par les services ;
* vérifier le fonctionnement des APIs.

---

## 📸 Captures des tests

Les captures d'écran des tests Swagger seront  :


### 💳 Ebank Service

#### Consultation des comptes bancaires
<img width="1629" height="806" alt="image" src="https://github.com/user-attachments/assets/a2a06055-4aa3-4bf6-9fbd-2b4d59fc8448" />


<img width="1680" height="845" alt="image" src="https://github.com/user-attachments/assets/c2e814f5-b257-4aab-976c-da77ce2b1cb4" />


---

### 👤 Customer Service

#### Consultation des clients
<img width="1690" height="826" alt="image" src="https://github.com/user-attachments/assets/56ec1bf0-4909-43e1-a5b6-9f03299d7ee5" />

<img width="1663" height="479" alt="image" src="https://github.com/user-attachments/assets/b6bcb5ef-8eb9-44a6-ab4b-e42f4b613623" />


---

# 🗄️ Base de données

Le microservice `ebank-service` utilise actuellement **H2 Database** pour la persistance des données.

La configuration utilise une base H2 en mémoire :

```text
jdbc:h2:mem:accounts-db
```

La console H2 est accessible depuis :

```text
http://localhost:8057/h2-console
```

---

# 🔎 Service Discovery

Le projet utilise **Eureka** afin de permettre la découverte des services dans l'architecture microservices.

Les différents services peuvent ainsi être enregistrés auprès du serveur Eureka et découverts dynamiquement.

Cette partie du projet est développée progressivement.

---

# 🌿 Gestion des versions avec Git

Le développement du projet est effectué progressivement avec Git et GitHub.

La branche principale de développement actuelle est :

```text
develop
```

Les modifications sont enregistrées régulièrement avec des commits afin de suivre l'évolution du projet.

Exemple :

```bash
git add .
git commit -m "Add bank account REST controller"
git push
```

La branche `main` sera utilisée pour la version stable/finale du projet.

---

# 📈 État actuel du projet

Le projet est actuellement **en cours de développement**.

### ✅ Déjà réalisé

* [x] Création du projet Spring Boot
* [x] Création du `ebank-service`
* [x] Création du `customer-service`
* [x] Mise en place de Spring Data JPA
* [x] Configuration de la base H2
* [x] Création des APIs REST
* [x] Documentation avec Swagger/OpenAPI
* [x] Première intégration avec Eureka
* [x] Gestion du projet avec Git et GitHub

### 🔄 En cours

* [ ] Finalisation du `customer-service`
* [ ] Communication entre les microservices
* [ ] Finalisation de la configuration Eureka
* [ ] Tests complets des APIs
* [ ] Amélioration de l'architecture
* [ ] Ajout d'autres fonctionnalités

### 🚀 À venir

* [ ] API Gateway
* [ ] Communication inter-microservices
* [ ] Gestion des erreurs
* [ ] Sécurité
* [ ] Tests unitaires et tests d'intégration
* [ ] Conteneurisation avec Docker
* [ ] Amélioration de la documentation

---

# 👩‍💻 Auteur

**Hanane Ait Lhaj**

Étudiante en Master **Systèmes Distribués et Intelligence Artificielle**

Projet réalisé dans le cadre du module :

**Systèmes Parallèles et Distribués – 2026**

---

## 📄 Licence

Ce projet est réalisé dans un cadre académique et pédagogique.
