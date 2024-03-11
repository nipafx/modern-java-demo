# Modern Java in Action

A repository for my live-coding talk [Modern Java in Action](https://nipafx.dev/talk-java-action).

## Next

This is where string templates used to come in.
Since [they're out](https://bugs.openjdk.org/browse/JDK-8329948) in JDK 23, there's nothing to do here except boring string formatting and method calls:
* change output in `GitHubCrawl::main` to:
	```java
	System.out.printf("""

			---

			%s
			%s


			""", Statistician.evaluate(rootPage), Pretty.pageList(rootPage));
	```
* parse to HTML in `ResultServer::serve`:
	```java
	var html = Jsoup.parse("""...""");
	```
  and update call to `Files.writeString` on next line
* add info in `ResultServer::pageHtml`:
	```java
	return join(RAW."""
			<div class="page level-%d">
				<a href="%s">%s</a>
				%s
  			</div>
			""".formatted(
					level,
					page.url().toString(),
					Pretty.pageName(page),
					reference ? "<span class=\"ref\"></span>" : "");
	```
