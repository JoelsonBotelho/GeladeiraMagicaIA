# 🧊 Geladeira Mágica IA

Aplicação web para cadastro de alimentos e geração de receitas inteligentes utilizando **Inteligência Artificial (Google Gemini)**. O objectivo é ajudar a reduzir o desperdício alimentar, sugerindo receitas práticas com base nos ingredientes disponíveis.

---

## 📋 Índice

- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Funcionalidades](#-funcionalidades)
- [Pré-requisitos](#-pré-requisitos)
- [Como Executar](#-como-executar)
- [Configuração das Variáveis de Ambiente](#-configuração-das-variáveis-de-ambiente)
- [Estrutura do Projecto](#-estrutura-do-projecto)
- [Endpoints da API](#-endpoints-da-api)
- [Modelo de Dados](#-modelo-de-dados)
- [Interface Web](#-interface-web)
- [Melhorias Futuras](#-melhorias-futuras)
- [Autor](#-autor)

---

## 🛠 Tecnologias Utilizadas

| Tecnologia              | Versão    |
|-------------------------|-----------|
| Java                    | 21        |
| Spring Boot             | 4.1.0       |
| Maven                   | 3.9+      |
| H2 Database             | (runtime) |
| JPA / Hibernate         | (Spring Data) |
| Thymeleaf               | (templates) |
| Flyway                  | (migrações) |
| Lombok                  | 1.18.38   |
| Google Gemini API       | (IA generativa) |
| WebClient (Spring WebFlux) | (comunicação reactiva) |
| HTML + CSS              | (interface) |

---

## ⚙ Funcionalidades

- ✅ **CRUD completo de alimentos** — Cadastrar, listar, actualizar e excluir itens da geladeira
- ✅ **Geração de receitas com IA (Google Gemini)** — Receitas criativas baseadas nos ingredientes cadastrados
- ✅ **Priorização de alimentos próximos à validade** — A IA prioriza ingredientes que vencem primeiro para evitar desperdício
- ✅ **Interface Web responsiva (Thymeleaf)** — Design moderno e adaptável a dispositivos móveis
- ✅ **API REST** — Endpoints JSON para integração com outros serviços
- ✅ **Banco de dados H2 em memória** — Fácil de testar sem configuração externa
- ✅ **Console H2** — Consulta directa à base de dados pelo browser

---

## 📦 Pré-requisitos

- **Java 21** (JDK) instalado
- **Maven 3.9+** (ou usar o `mvnw` incluído)
- **Chave da API Google Gemini** (gratuita no [Google AI Studio](https://aistudio.google.com/))

Verifique a instalação:

```bash
java --version
mvn --version
```

---

## 🚀 Como Executar

### 1. Clonar o repositório

```bash
git clone https://github.com/JoelsonBotelho/GeladeiraMagicaIA.git
cd GeladeiraMagicaIA
```

### 2. Configurar variáveis de ambiente

Crie um ficheiro `.env` na raiz do projecto (ou exporte as variáveis):

```env
# Banco de dados H2 (valores padrão)
DATABASE_URL=jdbc:h2:mem:geladeira
DATABASE_USERNAME=sa
DATABASE_PASSWORD=

# Google Gemini API
GEMINI_API_KEY=sua_chave_aqui
GEMINI_API_URL=https://generativelanguage.googleapis.com
```

> **Nota:** O projecto usa **Gemini API** (não OpenAI), apesar do nome do serviço `ChatGptService`. Certifique-se de configurar a variável `GEMINI_API_KEY`.

### 3. Executar com Maven

```bash
# Usando o Maven Wrapper (recomendado)
./mvnw spring-boot:run

# Ou se tiver Maven instalado globalmente
mvn spring-boot:run
```

A aplicação iniciará em: **http://localhost:8080**

### 4. Aceder às interfaces

| Interface                | URL                              |
|--------------------------|----------------------------------|
| 🧊 Web UI — Geladeira     | http://localhost:8080/geladeira   |
| 🗄️ Console H2            | http://localhost:8080/h2-console  |
| 📋 API — Listar alimentos | http://localhost:8080/food        |

> **Console H2:** JDBC URL: `jdbc:h2:mem:geladeira` — User: `sa` — Password: *(vazio)*

---

## 🔧 Configuração das Variáveis de Ambiente

O projecto utiliza variáveis de ambiente para segurança e flexibilidade:

| Variável              | Descrição                                   | Obrigatória |
|-----------------------|---------------------------------------------|:-----------:|
| `DATABASE_URL`        | URL de conexão do banco H2                  | ✅          |
| `DATABASE_USERNAME`   | Usuário do banco de dados                   | ✅          |
| `DATABASE_PASSWORD`   | Senha do banco de dados                     | ✅          |
| `GEMINI_API_KEY`      | Chave da API do Google Gemini               | ✅          |
| `GEMINI_API_URL`      | URL base da API Gemini                      | ✅          |
| `OPENAI_API_KEY`      | (Não utilizado actualmente — reservado)     | ❌          |

> ⚠️ **Importante:** Sem a `GEMINI_API_KEY` configurada, a aplicação lançará um erro ao iniciar.

---

## 🗂 Estrutura do Projecto

```
GeladeiraMagicaIA/
├── src/
│   ├── main/
│   │   ├── java/dev/java10x/GeladeiraMagicaIA/
│   │   │   ├── GeladeiraMagicaIaApplication.java       # Classe principal
│   │   │   ├── config/
│   │   │   │   └── WebClientConfig.java                # Configuração do WebClient (Gemini)
│   │   │   ├── controller/
│   │   │   │   ├── FoodItemController.java             # Controller REST (/food)
│   │   │   │   ├── GeladeiraController.java            # Controller Web (/geladeira)
│   │   │   │   └── RecipeController.java               # Controller REST de receitas
│   │   │   ├── model/
│   │   │   │   └── FoodItemModel.java                  # Entidade JPA (food_item)
│   │   │   ├── repository/
│   │   │   │   └── FoodItemRepository.java             # Repositório JPA
│   │   │   └── service/
│   │   │       ├── ChatGptService.java                 # Serviço de IA (Gemini)
│   │   │       └── FoodItemService.java                # Lógica de negócio
│   │   ├── resources/
│   │   │   ├── application.properties                  # Configurações
│   │   │   ├── static/css/
│   │   │   │   └── geladeira.css                       # Estilos da interface
│   │   │   ├── templates/
│   │   │   │   └── geladeira.html                      # Página principal
│   │   │   └── db/migration/
│   │   │       └── V1__create_food_item_table.sql      # Migração Flyway
│   └── test/
│       └── java/dev/java10x/GeladeiraMagicaIA/
│           └── GeladeiraMagicaIaApplicationTests.java
├── pom.xml                                             # Configuração Maven
├── mvnw / mvnw.cmd                                     # Maven Wrapper
├── .gitignore
└── README.md                                           # (este ficheiro)
```

---

## 🌐 Endpoints da API

### Alimentos (`/food`)

| Método   | Rota          | Descrição                 | Corpo (JSON)         |
|----------|---------------|---------------------------|-----------------------|
| `POST`   | `/food`       | Cadastrar um alimento     | `FoodItemModel`       |
| `GET`    | `/food`       | Listar todos os alimentos | —                     |
| `GET`    | `/food/{id}`  | Buscar alimento por ID    | —                     |
| `PUT`    | `/food/{id}`  | Actualizar alimento       | `FoodItemModel`       |
| `DELETE` | `/food/{id}`  | Remover alimento          | —                     |

### Receitas (`/generate`)

| Método | Rota         | Descrição                                      |
|--------|--------------|------------------------------------------------|
| `GET`  | `/generate`  | Gerar receita com IA com base nos alimentos    |

### Interface Web (`/geladeira`)

| Método | Rota                   | Descrição                          |
|--------|------------------------|------------------------------------|
| `GET`  | `/geladeira`          | Página principal da geladeira      |
| `POST` | `/geladeira/adicionar` | Adicionar alimento via formulário  |
| `POST` | `/geladeira/gerar-receita` | Gerar receita via interface    |
| `POST` | `/geladeira/deletar`  | Remover alimento                   |

---

## 📊 Modelo de Dados

### `food_item`

| Campo          | Tipo       | Descrição                                    |
|----------------|------------|----------------------------------------------|
| `id`           | `Long`     | Identificador único (PK, auto-incremento)    |
| `nome`         | `String`   | Nome do alimento                             |
| `categoria`    | `String`   | Categoria (Frutas, Vegetais, Carnes, ...)    |
| `quantidade`   | `Integer`  | Quantidade disponível                        |
| `data_validade`| `LocalDate`| Data de validade (padrão: 15 dias a partir de hoje) |

---

## 🖥 Interface Web

A interface foi desenvolvida com **Thymeleaf** e **CSS customizado**, oferecendo:

![Geladeira Mágica IA](https://via.placeholder.com/800x400?text=Geladeira+Magica+IA+-+Screenshot)

- 🎨 Design moderno com gradiente azul escuro
- 📱 Layout responsivo (adaptável a dispositivos móveis)
- ➕ Formulário completo para adicionar alimentos com:
  - Nome, categoria (dropdown), quantidade e data de validade
- 📋 Lista de alimentos com:
  - Contador dinâmico de itens
  - Detalhes: quantidade e validade formatada
  - Botão para excluir item com confirmação
- 🤖 Painel de receitas IA:
  - Botão "Gerar receita" (desabilitado se não houver alimentos)
  - Renderização de Markdown seguro (com `marked` + `DOMPurify`)
  - Mensagens de erro amigáveis

---

## 🔮 Melhorias Futuras

- [ ] Dockerização da aplicação
- [ ] Autenticação e autorização (Spring Security)
- [ ] Migração para banco persistente (PostgreSQL, MySQL)
- [ ] Testes unitários e de integração mais abrangentes
- [ ] Paginação na listagem de alimentos
- [ ] Suporte a múltiplos idiomas
- [ ] Upload de fotos dos alimentos
- [ ] Integração com WhatsApp para sugestão de receitas

---

## 👤 Autor

Desenvolvido por **Joelson Botelho** como projecto de estudo do curso **Java 10x**.

<div align="center">
  <a href="https://github.com/JoelsonBotelho">
    <img src="https://img.shields.io/badge/GitHub-JoelsonBotelho-181717?style=for-the-badge&logo=github" alt="GitHub">
  </a>
  <a href="https://www.linkedin.com/in/joelson-botelho/">
    <img src="https://img.shields.io/badge/LinkedIn-Joelson%20Botelho-0A66C2?style=for-the-badge&logo=linkedin" alt="LinkedIn">
  </a>
</div>

---

<p align="center">
  Feito com ☕, 🧊 e 🤖
</p>

