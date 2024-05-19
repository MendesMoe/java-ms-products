# java-ms-product
A highly efficient, microservices-based order processing application utilizing Spring Boot. Features include product catalog management, bulk product import via CSV processing with Spring Batch, and MongoDB for data persistence.

## Tecnologias Utilizadas

Este projeto foi desenvolvido com as seguintes tecnologias e bibliotecas:

- **Java 17**: Versão do Java utilizada no projeto.
- **Spring Boot**: Framework principal para a criação de aplicações Spring.
- **Spring Boot DevTools**: Para desenvolvimento rápido com reinício automático.
- **Spring Boot Starter Security**: Para autenticação e segurança da aplicação.
- **Spring Boot Starter Web**: Para construção de aplicações web, usando o Spring MVC.
- **Spring Boot Starter Data Jpa**: Para auxílio na persistência no Banco de Dados.
- **Spring Boot Validation**: Para validação de campos.
- **Spring Batch**: Para criação dos produtos no estoque.
- **Springdoc OpenAPI**: Para documentação da API REST com Swagger.
- **Lombok**: Para reduzir o código boilerplate, como getters e setters.
- **MongoDB**: banco de dados não relacional.
- **Docker**: utilização de container para criação de ambiente de execução local


### Execução das aplicações
Este repositório contém uma aplicação e um banco de dados. 
Porém, sua execução pode depender de outras aplicações, então sugerimos os passos abaixo para garantir que os containers estejam rodando antes de iniciar os testes.<br>
Isso inclui a criação de uma rede no docker para que os containers possam se comunicar.<br>
O comando abaixo para a criação de rede é necessário somente para o primeiro container, os demais vão assumir essa mesma rede.
````shell
docker network create my_network -d bridge
````
Após a criação da rede, precisamos executar o comando abaixo para fazer o build da aplicação e também do banco de dados. Este comando é responsável por executar o arquivo compose, e este por sua vez fará o build da aplicação usando o Dockerfile, e também irá baixar, configurar e executar o banco de dados.
````shell
docker-compose up --build
````
Assim que o processo for concluído, tanto a aplicação quanto o banco de dados estarão rodando em containers compartilhando a mesma rede.<br>
Ja existe um arquivo products-java.csv com alguns produtos que será importado ao projeto pelo Spring Batch quando a aplicação for iniciada.


## Documentação
A Collection utilizada no Postman está disponível na URL:
https://github.com/MendesMoe/java-ms-orders/blob/main/src/main/resources/PosTech_TC4.postman_collection.json

A documentação detalhada da API está acessível na URL:
http://localhost:8080/swagger-ui/index.html

## Portas configuradas

App: http://localhost:8080 

Mongo: mongodb://localhost:27017/msproducts
