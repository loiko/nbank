FROM maven:3.9.11-eclipse-temurin-17

ARG TEST_PROFILE=api
ARG APIBASEURL=http://192.168.0.101:4111
ARG UIBASEURL=http://192.168.0.101:3000
ARG DB_URL=jdbc:postgresql://192.168.0.101:5433/nbank
ARG DB_USERNAME=postgres
ARG DB_PASSWORD=postgres

ENV TEST_PROFILE=${TEST_PROFILE}
ENV APIBASEURL=${APIBASEURL}
ENV UIBASEURL=${UIBASEURL}
ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY . .

USER root

CMD /bin/bash -c " \
    mkdir -p /app/logs ; \
    { \
    echo '>>> Running tests with profile: ${TEST_PROFILE}' ; \
    mvn test -q -P ${TEST_PROFILE} ; \
    \
    echo '>>> Running surefire-report:report' ; \
    mvn -DskipTests=true surefire-report:report ; \
   } > /app/logs/run.log 2>&1"