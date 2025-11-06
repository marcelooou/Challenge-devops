# 🚀 Solução de Rastreamento de Frotas para a Mottu (Sprint 4: CI/CD)

Este projeto é a evolução do desafio anterior, focado na entrega da **Sprint 4 de DevOps Tools & Cloud Computing** da FIAP.

A solução é uma **API RESTful** desenvolvida em **Java com Spring Boot**, que gerencia as localizações de motos em tempo real.  
A grande mudança desta sprint é que o **deploy não é mais manual** — ele é **100% automatizado** por meio de uma esteira de **CI/CD no Azure DevOps Pipelines**.

A pipeline compila o código, constrói uma imagem Docker e a publica no **Azure App Service for Containers**.

---

## 👨‍💻 Integrantes

| Nome | RM |
|------|----|
| Marcelo Siqueira Bonfim | RM558254 |
| Antonio Caue | RM558891 |
| Felipe Gomes Costa Orikasa| RM557435  |

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Java 17  
- **Framework:** Spring Boot 3.2  
- **Conteinerização:** Docker  
- **Orquestração de CI/CD:** Azure DevOps Pipelines (YAML Multi-stage)  
- **Repositório de Artefatos:** Azure Container Registry (ACR)  
- **Plataforma de Deploy:** Azure App Service for Containers (PaaS)  
- **Banco de Dados:** Azure SQL Database (PaaS)

---

## 🏗️ Fluxo de CI/CD Automatizado

A arquitetura desta solução é totalmente focada em automação.  
O deploy manual da Sprint 3 foi **completamente substituído**.

### 🔹 Gatilho (Push)
O desenvolvedor faz um **git push** para a branch `main` no GitHub.

### 🔹 Pipeline (Trigger)
O **Azure DevOps** detecta o push e inicia automaticamente a pipeline `azure-pipelines.yml`.

### 🔹 Estágio 1: CI (Build)

1. A pipeline usa um agente local (**Self-Hosted**) para rodar os jobs.  
2. O `Dockerfile` é usado para compilar o projeto Java (`mvn clean package`).  
3. Uma imagem Docker `mottu-java:latest` é construída.  
4. A imagem é enviada (**push**) para o nosso **Azure Container Registry (marcelodevops.azurecr.io)**.

### 🔹 Estágio 2: CD (Deploy)

1. Assim que o Build termina com sucesso, o estágio de Deploy começa automaticamente (`dependsOn: Build`).  
2. A pipeline se conecta ao **Azure App Service** (`marcelodevops`) e o instrui a baixar e rodar a nova imagem `:latest` publicada no ACR.  
3. O App Service inicia o contêiner Java.  
4. A aplicação **Spring Boot** lê as **Variáveis de Ambiente** (do App Service) para se conectar ao **Azure SQL**, ao **ACR** e ao **MQTT**.

---

## ⚙️ Como a Automação Funciona

Diferente da Sprint 3, não há mais um passo a passo manual.  
Toda a infraestrutura já foi criada e o deploy é **100% automatizado pela pipeline**.

- **Build (CI):** qualquer `git push` na branch `main` dispara o stage `Build` no Azure DevOps.  
- **Deploy (CD):** assim que o `Build` termina com sucesso, o stage `Deploy` é executado automaticamente e atualiza o App Service em produção.

---

## 🌐 Endpoints da API

A aplicação está no ar e pode ser acessada pela seguinte URL base:

🔗 **https://marcelodevops.azurewebsites.net**

---

## 📄 Swagger UI (Recomendado)

Para testar todos os endpoints de forma interativa:  
👉 [https://marcelodevops.azurewebsites.net/swagger-ui/index.html](https://marcelodevops.azurewebsites.net/swagger-ui/index.html)

### az group delete --name ChallengeDevOpsMottu --yes --no-wait