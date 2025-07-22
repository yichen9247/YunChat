# 构建后端（若无需求可选择将下面的命令注释掉）
FROM maven:3.8.6 AS server-build
WORKDIR /app
COPY ./YunChat-Server /app/YunChat-Server
COPY ./Docker/application.yml /app/YunChat-Server/src/main/resources/config/application.yml
WORKDIR /app/YunChat-Server
RUN mvn clean package -DskipTests

# 构建前端（若无需求可选择将下面的命令注释掉）
FROM node:22 AS client-build
WORKDIR /app/YunChat-Client
COPY ./YunChat-Client /app/YunChat-Client
RUN rm -rf /app/YunChat-Client/.env.production
RUN rm -rf /app/YunChat-Client/.env.development
ARG VITE_SERVER_IP
ARG VITE_SERVER_URL
ARG VITE_APP_SECRET_KEY
ARG VITE_APP_SCAN_LOGIN
ARG VITE_APP_AUTO_SHOWIMAGE
ENV VITE_SERVER_IP=$VITE_SERVER_IP
ENV VITE_SERVER_URL=$VITE_SERVER_URL
ENV VITE_APP_DIALOG=$VITE_APP_SCAN_LOGIN
ENV VITE_APP_SECRET_KEY=$VITE_APP_SECRET_KEY
ENV VITE_APP_AUTO_SHOWIMAGE=$VITE_APP_AUTO_SHOWIMAGE
RUN npm install
RUN npm run build

# 运行后端（若无需求可选择将下面的命令注释掉）
FROM openjdk:17-jdk-slim AS server-run
WORKDIR /app
COPY --from=server-build /app/YunChat-Server/target/YunChat-2.3.2/* /app/YunChat-Server/server-build/
COPY --from=server-build /app/YunChat-Server/upload/avatar/0/default/* /app/YunChat-Server/server-build/upload/avatar/0/default/
EXPOSE 8081
WORKDIR /app/YunChat-Server/server-build
ENTRYPOINT ["java", "-jar", "yunchat-2.3.2.jar"]

# 运行前端（若无需求可选择将下面的命令注释掉）
FROM nginx:alpine AS client-run
WORKDIR /app
COPY ./Docker/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=client-build /app/YunChat-Client/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]