# Estágio de build
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Estágio de execução
FROM openjdk:17-jdk-slim
WORKDIR /app
# Crie um usuário e grupo não-root
RUN groupadd -r appgroup && useradd -r -g appgroup appuser
# Copie o JAR do estágio de build
COPY --from=build /app/target/*.jar app.jar
# Defina permissões (opcional, mas bom para segurança se necessário)
# RUN chown appuser:appgroup app.jar
# Mude para o usuário não-root
USER appuser
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]