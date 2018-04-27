# CrawlerMan
A crawler-based search engine.

## Requirements
[Java 8](https://java.com/en/download/)+  
[MongoDB](https://www.mongodb.com/download-center)

## Running the application
```shell
gradle (./gradlew) run -PappArgs="['<seed_file>', '<max_URLs_count>', '<number_of_threads>']"
```
### Example
```shell
gradle (./gradlew) run -PappArgs="['seed.txt',1000,3]"
```
