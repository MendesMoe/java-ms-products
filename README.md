# java-ms-product
A highly efficient, microservices-based order processing application utilizing Spring Boot. Features include product catalog management, bulk product import via CSV processing with Spring Batch, and MongoDB for data persistence.

```
docker pull mongo
docker run -d --name mongodb -p 27017:27017 mongo
docker-compose up --build
````
docker ps
````
Verifique o nome do container e pode lancar :
mongo --host localhost --port 27017
````
Quando a conexao for feita você pode visualisar as tables e criar a 'msproducts'
````shell
show databases
use msproducts
````
Ja existe um arquivo products-java.csv com alguns produtos no projeto. Ele sera importado para a db desde a execucao do ms. Você pode consulta-los usando o endipoint:
````
http://localhost:8081/api/products
````
Todos os endpoints da API de produtos foram detalhados usando Swagger e estao disponiveis na url:
````
http://localhost:8081/swagger-ui/index.html
````