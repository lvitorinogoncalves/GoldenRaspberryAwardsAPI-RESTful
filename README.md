# Golden Raspberry Awards API

## Descrição

Este projeto é uma API para gerenciar informações dos prêmios Golden Raspberry Awards.

## Pré-requisitos

* Java: 11 ou superior
* Maven: 3.6.0 ou superior

## Estrutura do Projeto

* pom.xml: Arquivo de configuração do Maven.
* mvnw e mvnw.cmd: Scripts para executar o Maven Wrapper.
* src/: Diretório contendo o código-fonte do projeto.

### Configuração do Ambiente

1. Clone o repositório para sua máquina local:
```SH
git clone <url-do-repositorio>
```


2. Navegue até o diretório do projeto:
```SH
cd golden-raspberry-awards-api
```

## Como Executar
### Usando Maven Wrapper
Você pode usar o Maven Wrapper incluído no projeto para executar os comandos Maven, sem precisar instalar o Maven globalmente.

* No Windows:
```SH
mvnw.cmd spring-boot:run
```

* No Unix/MacOS:
```SH
./mvnw spring-boot:run
```

### Usando Maven
Se você já tiver o Maven instalado, pode simplesmente executar:
```SH
mvn spring-boot:run
```

A API estará disponível em [http://localhost:8080](http://localhost:8080)

### Executando Testes de Integração

Os testes de integração são configurados para serem executados com o Maven. Para executar todos os testes, incluindo os testes de integração, utilize o seguinte comando:

#### Usando Maven Wrapper

* No Windows:
```SH
mvnw.cmd test
```

* No Unix/MacOS:
```SH
./mvnw test
```

#### Usando Maven

Se você já tiver o Maven instalado, pode simplesmente executar:
```SH
mvn test
```

Isso executará todos os testes unitários e de integração configurados no projeto.

#### Estrutura de Diretórios
* src/main/java: Contém o código-fonte da aplicação.
* src/main/resources: Contém os arquivos de configuração e recursos estáticos.
* src/test/java: Contém os testes unitários e de integração.

#### Funcionalidades da API

##### 1. Inicializar a Aplicação
* Endpoint: GET /api/movies/initialized
* Descrição: Verifica se a aplicação está inicializada.
* Resposta Exemplo:
```JSON
"Initialized"
```

##### 2. Obter Intervalos de Prêmios dos Produtores
* Endpoint: GET /api/movies/producers-prize-intervals
* Descrição: Obtém um mapa dos produtores e seus respectivos intervalos de prêmios.
* Resposta Exemplo:
```JSON
{
  "Producer Name": [
    {
      "producer": "Producer Name",
      "firstPrizeYear": 2000,
      "lastPrizeYear": 2020
    }
  ]
}
```

##### 3. Obter Todos os Filmes
* Endpoint: GET /api/movies/all
* Descrição: Retorna a lista de todos os filmes armazenados no banco de dados.
* Resposta Exemplo:
```JSON
[
  {
    "id": 1,
    "title": "Movie Title",
    "studio": "Studio Name",
    "producer": "Producer Name",
    "year": 2022,
    "winner": true
  },
  {
    "id": 2,
    "title": "Another Movie Title",
    "studio": "Another Studio",
    "producer": "Another Producer",
    "year": 2021,
    "winner": false
  }
]
```

##### 4. Obter Filme por ID
* Endpoint: GET /api/movies/{id}
* Descrição: Retorna os detalhes de um filme específico baseado no ID fornecido.
* Parâmetros: id (ID do filme)
* Resposta Exemplo:
```JSON
{
  "id": 1,
  "title": "Movie Title",
  "studio": "Studio Name",
  "producer": "Producer Name",
  "year": 2022,
  "winner": true
}
```

##### 5. Criar um Novo Filme
* Endpoint: POST /api/movies/create
* Descrição: Cria um novo filme e o adiciona ao banco de dados.
* Corpo da Requisição Exemplo:
```JSON
{
  "title": "New Movie Title",
  "studio": "New Studio",
  "producer": "New Producer",
  "year": 2023,
  "winner": false
}
```

* Resposta Exemplo:
```JSON
{
  "id": 3,
  "title": "New Movie Title",
  "studio": "New Studio",
  "producer": "New Producer",
  "year": 2023,
  "winner": false
}
```

##### 6. Atualizar um Filme
* Endpoint: PUT /api/movies/{id}
* Descrição: Atualiza as informações de um filme existente baseado no ID fornecido.
* Parâmetros: id (ID do filme)
* Corpo da Requisição Exemplo:
```JSON
{
  "title": "Updated Movie Title",
  "studio": "Updated Studio",
  "producer": "Updated Producer",
  "year": 2024,
  "winner": true
}
```

* Resposta Exemplo:
```JSON
{
  "id": 1,
  "title": "Updated Movie Title",
  "studio": "Updated Studio",
  "producer": "Updated Producer",
  "year": 2024,
  "winner": true
}
```

##### 7. Excluir um Filme
* Endpoint: DELETE /api/movies/{id}
* Descrição: Exclui um filme existente baseado no ID fornecido.
* Parâmetros: id (ID do filme)
* Resposta Exemplo:
* * Se bem-sucedido:
```JSON
{}
```
* * Se o filme não for encontrado:
```JSON
{
  "error": "Not Found"
}
```

#### Notas Adicionais
Certifique-se de ter todas as dependências necessárias especificadas no pom.xml.
Você pode personalizar a configuração da aplicação modificando os arquivos em src/main/resources.
