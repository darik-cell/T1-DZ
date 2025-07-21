# ---------- Stage 1: build ----------
FROM gradle:8.8-jdk17 AS build
WORKDIR /project
COPY . .
RUN gradle clean war --no-daemon

# ---------- Stage 2: runtime ----------
FROM tomcat:10.1-jdk17
# выключаем дефолтные приложения Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*
# копируем наш WAR
COPY --from=build /project/build/libs/help-service.war /usr/local/tomcat/webapps/help-service.war
# папка для файла базы
RUN mkdir -p /opt/h2-data
EXPOSE 8080
