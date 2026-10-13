# EBank — Microservices

Projet réalisé avec **Java 21, Maven et Spring Boot 4.1.1**. Extraits de code et résultats attendus des étapes de la démonstration.

## 1. Créer les deux microservices métier

Créer Customer Service et EBank Service avec **Spring Web, Spring Data JPA, H2, Lombok, Discovery Client, Config Client et Actuator**. Organiser chaque service en entités, repositories, services et contrôleurs REST.

| Service | Port | Données | Endpoints |
| --- | --- | --- | --- |
| Customer Service | 8056 | `id`, `name`, `email` | GET/POST `/customers`, GET `/customers/{id}` |
| EBank Service | 8057 | `id`, `createdAt`, `balance`, `type`, `customerId` | GET/POST `/accounts`, GET `/accounts/{id}` |

Utiliser des bases H2 en mémoire et désactiver provisoirement les services techniques :

```properties
spring.cloud.discovery.enabled=false
spring.cloud.config.enabled=false
```

Initialiser **3 clients et 15 comptes** avec des `CommandLineRunner`. Générer l'identifiant du compte avec `UUID.randomUUID().toString()` et sa date avec `new Date()`.

## 2. Tester les API avec Swagger

Ajouter **Springdoc OpenAPI** dans les deux services et les redémarrer.

```text
http://localhost:8056/swagger-ui.html
http://localhost:8057/swagger-ui.html
```

**Résultat :** liste, recherche par identifiant et ajout des clients/comptes. Pour créer un compte, envoyer seulement `balance`, `type` et `customerId`. Le champ `customer` est encore `null`.

## 3. Configurer la Gateway avec des adresses statiques

Créer Gateway Service avec **Reactive Gateway, Discovery Client et Actuator**, sur le port **9999**. Garder Discovery désactivé et configurer les routes dans `application.yml`.

| Route | Chemins | Adresse fixée |
| --- | --- | --- |
| R1 | Clients | `http://localhost:8056` |
| R2 | Comptes | `http://localhost:8057` |

Écrire les prédicats `Path=` sans espace avant `=`.

```text
http://localhost:9999/customers
http://localhost:9999/accounts
```

**Résultat :** la Gateway transmet les requêtes aux adresses configurées.

## 4. Passer à la découverte et au routage dynamiques

Créer Discovery Service avec **Eureka Server**, activer `@EnableEurekaServer` et configurer :

```properties
server.port=8761
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

Activer Discovery dans Customer Service, EBank Service et la Gateway :

```properties
spring.cloud.discovery.enabled=true
```

Démarrer Eureka en premier, puis les autres services. Vérifier leur enregistrement sur `http://localhost:8761`.

Dans la Gateway, commenter les routes statiques et ajouter :

```java
@Bean
public DiscoveryClientRouteDefinitionLocator dynamicRoutes(
        ReactiveDiscoveryClient rdc, DiscoveryLocatorProperties dp) {
    return new DiscoveryClientRouteDefinitionLocator(rdc, dp);
}
```

Redémarrer la Gateway et utiliser les noms enregistrés dans Eureka :

```text
http://localhost:9999/CUSTOMER-SERVICE/customers
http://localhost:9999/EBANK-SERVICE/accounts
```

**Résultat :** les services sont accessibles par leur nom, sans fixer leurs adresses dans les routes.

## 5. Relier les comptes aux clients avec OpenFeign

Ajouter **OpenFeign** dans EBank Service et activer `@EnableFeignClients`.

```java
@FeignClient(name = "customer-service")
public interface CustomerRestClient {
    @GetMapping("/customers/{id}")
    Customer getCustomerById(@PathVariable Long id);
}
```

Créer un modèle `Customer` sans `@Entity`. Dans `BankAccount`, ajouter :

```java
@Transient
private Customer customer;
```

Injecter `CustomerRestClient` dans la couche service et enrichir le compte avant de le retourner :

```java
bankAccount.setCustomer(
    customerRestClient.getCustomerById(bankAccount.getCustomerId())
);
```

Vérifier également le client avant l'enregistrement du compte ; en cas d'exception, relancer une `RuntimeException` avec le message reçu.

**Résultat :** le compte consulté contient les informations du client récupérées depuis Customer Service.

## 6. Ajouter et tester la tolérance aux pannes

Ajouter **Resilience4j** et annoter l'appel à Customer Service :

```java
@CircuitBreaker(name = "customer-service", fallbackMethod = "getDefaultCustomer")
```

Créer `getDefaultCustomer` avec **l'identifiant d'abord, puis l'exception**. Retourner un client par défaut indiquant **« not available »**.

| Test | Résultat attendu |
| --- | --- |
| Customer Service disponible | Compte avec les informations du client. |
| Customer Service arrêté | Compte accessible avec les informations client par défaut. |
| Service redémarré, après une nouvelle tentative | Informations du client à nouveau disponibles. |

