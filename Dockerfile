FROM java:8
EXPOSE 8080
ADD ./target/compare-database-table-1.0-SNAPSHOT.jar ./
ENTRYPOINT ["java","-jar","compare-database-table-1.0-SNAPSHOT.jar"]
