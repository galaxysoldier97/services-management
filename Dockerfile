# Etapa de construcción con Maven
FROM maven:3.9.5-eclipse-temurin-11 AS builder

WORKDIR /app

# Configurar el repositorio local de Maven
ENV MAVEN_REPO_LOCAL=/root/.m2/repository

# Copiar configuración de Maven y dependencias necesarias
COPY .m2/settings.xml /root/.m2/settings.xml
COPY pom.xml .
COPY .m2/repository $MAVEN_REPO_LOCAL
COPY libs/parent-5.0.10.pom $MAVEN_REPO_LOCAL/mc/monacotelecom/buildfwk/parent/5.0.10/parent-5.0.10.pom
COPY . .

# Instalar dependencias en modo offline y compilar
RUN mvn dependency:go-offline -Dmaven.repo.local=$MAVEN_REPO_LOCAL
RUN mvn clean install -DskipTests -Dgit-commit-id.skip=true -fn -Dmaven.repo.local=$MAVEN_REPO_LOCAL

# Subir los artefactos a Nexus
RUN mvn deploy -DskipTests --settings /root/.m2/settings.xml

CMD ["sh"]