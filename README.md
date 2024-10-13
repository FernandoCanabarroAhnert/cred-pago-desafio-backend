# Desafio Backend: Mini e-Commerce 🛒

![Java](https://img.shields.io/badge/java-FF5722.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-F57F17?style=for-the-badge&logo=Hibernate&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-003B6F?style=for-the-badge&logo=postgresql&logoColor=white)
![PgAdmin](https://img.shields.io/badge/PgAdmin-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-%23DC382D?style=for-the-badge&logo=Redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![SendGrid](https://img.shields.io/badge/SendGrid-00BFFF?style=for-the-badge&logo=maildotru&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![OAuth2](https://img.shields.io/badge/OAuth2-F80000?style=for-the-badge&logo=openid&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-181717?style=for-the-badge&logo=github&logoColor=white)
![Testcontainers](https://img.shields.io/badge/Testcontainers-%2300BCD4?style=for-the-badge&logo=Docker&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit5-%2325A162?style=for-the-badge&logo=JUnit5&logoColor=white)
![JaCoCo](https://img.shields.io/badge/JaCoCo-%238C4C00?style=for-the-badge&logo=Codecov&logoColor=white)

## Modelo de Domínio
![CredPago-Classes](https://github.com/user-attachments/assets/0a2e7f4f-0305-4853-872c-fe2883efd8d1)

## O que é o desafio? 🤔

O desafio, encontrado no GitHub da CredPago, é do funcionamento de um sistema de um mini e-commerce, em que há as funcionalidades de CRUD de produtos, adicionar e remover itens do Carrinho de Compras e realizar Transações.

Para adicionar segurança ao projeto, eu utilizei o OAuth2 e JWT, com 2FA (Two-Factor Authentication), com o envio de e-mail para a ativação de conta, através do Sendgrid

Como um diferencial, eu implementei Test Containers para a execução dos Testes de Integração. Test Containers é uma ferramenta que facilita o uso de containers Docker para executar testes de integração. Ele permite que você inicie ambientes completos de banco de dados, sistemas de mensageria, navegadores e muito mais dentro de containers durante a execução dos testes, garantindo que eles sejam executados em ambientes controlados e reproduzíveis. Isso torna os testes mais confiáveis, pois elimina dependências externas e inconsistências entre ambientes locais e de CI/CD.

A aplicação utiliza o banco de dados relacional PostgreSQL, com a interface gráfica do PgAdmin, e o banco de dados NoSQL em memória Redis, para realizar o cache e otimizar a performance da aplicação. Todos os serviços estão rodando via Docker. Por fim, para realizar o CI/CD da aplicação, eu utilizei o Github Actions.

O desafio pode ser encontrado aqui: <https://github.com/lucasmpw/desafio-backend>

<p align="left" width="100%">
    <img width="25%" src="https://github.com/user-attachments/assets/7f060209-9c1b-4085-914b-4b4f1b0cd704"> 
</p>

### Serviço RESTful 🚀

* Desenvolvimento de um serviço RESTful para toda a aplicação.

## Tecnologias 💻
 
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Security](https://spring.io/projects/spring-security)
- [JWT](https://jwt.io/)
- [OAuth2](https://oauth.net/2/)
- [SpringDoc OpenAPI 3](https://springdoc.org/v2/#spring-webflux-support)
- [H2](https://www.baeldung.com/spring-boot-h2-database)
- [PostgreSQL](https://www.postgresql.org/)
- [PgAdmin](https://www.pgadmin.org/)
- [Redis](https://redis.io/)
- [Docker](https://www.docker.com/)
- [SendGrid](https://sendgrid.com/en-us)
- [JUnit5](https://junit.org/junit5/)
- [Mockito](https://site.mockito.org/)
- [MockMvc](https://docs.spring.io/spring-framework/reference/testing/spring-mvc-test-framework.html)
- [Jacoco](https://www.eclemma.org/jacoco/)
- [TestContainers](https://testcontainers.com/)
- [GithubActions](https://docs.github.com/pt/actions)
- [Bean Validation](https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html)


## Práticas adotadas ✨

- SOLID, DRY, YAGNI, KISS
- API REST
- TDD
- Consultas com Spring Data JPA
- Injeção de Dependências
- Testes Automatizados
- Geração automática do Swagger com a OpenAPI 3
- Autenticação e Autorização com OAuth2 e JWT com 2FA

## Diferenciais 🔥

Alguns diferenciais que não foram solicitados no desafio:

* Utilização de Docker
* Uso do Redis para Cache
* Cadastro de Usuário em 2 etapas
* SendGrid para envio de emails
* Testes Unitários e de Integração
* TDD - Test Driven Development
* Utilização de Test Containers
* Cobertura de Testes com Jacoco
* Tratamento de exceções
* Validações com Constraints Customizados
* Documentação Swagger

## Como executar 🎉

1.Clonar repositório git:

```text
git clone https://github.com/FernandoCanabarroAhnert/cred-pago-desafio-backend.git
```

2.Instalar dependências.

```text
mvn clean install
```

3.Executar a aplicação Spring Boot.

4.Testar endpoints através do Postman ou da url
<http://localhost:8080/swagger-ui/index.html#/>

### Usando Docker 🐳

- Clonar repositório git
- Construir o projeto:
```
./mvnw clean package
```
- Construir a imagem:
```
./mvnw spring-boot:build-image
```
- Executar o container:
```
docker run --name desafio-credpago -p 8080:8080  -d desafio-credpago:0.0.1-SNAPSHOT
```
## API Endpoints 📚

Para fazer as requisições HTTP abaixo, foi utilizada a ferramenta [Postman](https://www.postman.com/):
- Collection do Postman completa: [Postman-Collection](https://github.com/user-attachments/files/17356287/CredPago.postman_collection.json)
- Environment do Postman: [Postman-Environment](https://github.com/user-attachments/files/17356289/CredPago.Env.postman_environment.json)


- Inserir Produto
```
$ http POST http://localhost:8080/store/api/v1/products


{

  "artist": "Kansas",
  "year": "1976",
  "album": "Leftoverture",
  "price": 110,
  "thumb": "https://upload.wikimedia.org/wikipedia/en/3/3c/Kansas_Leftoverture.jpg"

}

```

- Realizar Transação
```
$ http POST http://localhost:8080/store/api/v1/transactions/buy

- entrada:

{
    "creditCardId": 2
}

- saída:

{
    "id": 1,
    "moment": "12/10/2024 21:04",
    "products": [
        {
            "productId": 3,
            "artist": "Led Zeppelin",
            "year": "1971",
            "album": "Led Zeppelin IV",
            "price": 130,
            "thumb": "led_zeppelin_iv_thumb.jpg",
            "quantity": 1
        },
        {
            "productId": 13,
            "artist": "Prince",
            "year": "1984",
            "album": "Purple Rain",
            "price": 175,
            "thumb": "purple_rain_thumb.jpg",
            "quantity": 2
        }
    ],
    "totalToPay": 480,
    "creditCard": {
        "id": 2,
        "holderName": "Alex Green",
        "cardNumber": "**** **** **** 4321",
        "expirationDate": "12/25"
    },
    "userId": 2,
    "userEmail": "alex@gmail.com"
}

```


