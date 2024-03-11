# Modern Java in Action

A repository for my live-coding talk [Modern Java in Action](https://nipafx.dev/talk-java-action).

## Next

Launch from terminal:
* close IDE
* verify existence of `jar/org.jsoup...jar`
* launch with:
  ```java
  java -p jars --enable-preview \
	  src/main/java/dev/nipafx/demo/modern/GitHubCrawl.java \
	  https://github.com/junit-pioneer/junit-pioneer/issues/624 10
  ```
