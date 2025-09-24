# 🚀 Solução de Rastreamento de Frotas para a Mottu

Este projeto é uma solução de **back-end** para o desafio da **Sprint 3 de DevOps da FIAP**, focada no rastreamento de frotas da **Mottu**.  
A aplicação é uma **API RESTful** desenvolvida em **Java com Spring Boot**, que gerencia as localizações de motos em tempo real.  
Toda a infraestrutura está hospedada na **nuvem Microsoft Azure**.

---

## 👨‍💻 Integrantes

- Marcelo Siqueira Bonfim – RM558254  
- Antonio Caue Araujo da Silva – RM558891  
- Felipe Gomes Costa Orikasa – RM557435  

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Java 17  
- **Framework:** Spring Boot  
- **Banco de Dados:** Azure SQL Database (PaaS)  
- **Plataforma de Deploy:** Azure App Service (PaaS)  
- **Ferramentas de Automação:** Azure CLI, Maven  

---

## 🏗️ Arquitetura da Solução

- Os **dados de localização das motos** são armazenados no **Azure SQL Database**, um serviço de PaaS totalmente integrado à aplicação.  

---

## ⚙️ Instalação e Deploy (Passo a Passo)

Para criar a infraestrutura e fazer o deploy da aplicação no **Azure**, siga estes passos em um terminal com o **Azure CLI** instalado e autenticado:

### 1️⃣ Criar Recursos no Azure

```bash
# Cria o Grupo de Recursos
az group create --name ChallengeDevOpsMottu --location "eastus2"

# Cria o servidor Azure SQL e o banco de dados
az sql server create --name servidormottu --resource-group ChallengeDevOpsMottu --location "eastus2" --admin-user devopsadmin --admin-password "28070505Mm@"
az sql db create --resource-group ChallengeDevOpsMottu --server servidormottu --name bdmottufiap --edition Basic

# Cria a regra de firewall (substitua pelo seu IP público)
az sql server firewall-rule create --resource-group ChallengeDevOpsMottu --server servidormottu --name AllowMyIP --start-ip-address <SEU-IP-PUBLICO> --end-ip-address <SEU-IP-PUBLICO>

# Cria o Plano de Serviço e o App Service
az appservice plan create --name plan-mottu-fiap --resource-group ChallengeDevOpsMottu --sku F1 --is-linux
az webapp create --resource-group ChallengeDevOpsMottu --plan plan-mottu-fiap --name app-mottu-fiap --runtime "JAVA|17-java17"

2️⃣ Compilar e Fazer o Deploy

mvn clean package

#eploy:

az webapp deploy --resource-group ChallengeDevOpsMottu --name app-mottu-fiap --src-path target/mottu-location-0.0.1-SNAPSHOT.jar

#Exemplos de Endpoints
http://app-mottu-fiap.azurewebsites.net/

### ➕ Criar uma Moto (POST)
POST /api/motos
Content-Type: application/json

{
  "placa": "ABC1234",
  "modelo": "Honda CG 160"
}

### 📍 Criar uma Localização (POST)
POST /api/locations/moto/placa/ABC1234
Content-Type: application/json

{
  "latitude": -23.5505,
  "longitude": -46.6333
}

### 📋 Listar Localizações (GET)
GET /api/locations

### 🔄 Atualizar Localização (PUT)
PUT /api/locations/{id}
Content-Type: application/json

{
  "latitude": -23.5678,
  "longitude": -46.6543
}

### ❌ Excluir Localização (DELETE)
DELETE /api/locations/{id}

az group delete --name ChallengeDevOpsMottu --yes --no-wait


