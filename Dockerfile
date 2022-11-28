 FROM maven:lastest
 RUN mkdir /psms
 WORKDIR /psms
 COPY . .
 EXPOSE 8081
 CMD ["mvn","spring-boot:run"]
