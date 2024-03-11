# Modern Java in Action

A repository for my live-coding talk [Modern Java in Action](https://nipafx.dev/talk-java-action).

## Next

Records:
* create `record PageWithLinks(Page page, Set<URI> links)`
	* additional constructor without `links`

Modules:
* fix errors in `PageFactory`: `requires org.jsoup;`
* fix errors in `PageTreeFactory`: `requires java.net.http;`

HTTP client:
* instantiate `HttpClient` in `GitHubCrawl`:
	```java
	var client = HttpClient.newHttpClient();
	```
* `PageTreeFactory::fetchPageAsString`:
	```java
	var request = HttpRequest
	  .newBuilder(url)
	  .GET()
	  .build();
	return client
	  .send(request, BodyHandlers.ofString())
	  .body();
	```

Structured Concurrency:
* `PageTreeFactory::resolveLinks`:
	```java
	try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
	  var futurePages = new ArrayList<Subtask<Page>>();
	  for (URI link : links)
		  futurePages.add(scope.fork(() -> createPage(link, depth)));

	  scope.join();
	  scope.throwIfFailed();

	  return futurePages.stream()
			  .map(Subtask::get)
			  .collect(toSet());
	} catch (ExecutionException ex) {
	  // this should not happen as `ErrorPage` instances should have been created for all errors
	  throw new IllegalStateException("Error cases should have been handled during page creation!", ex);
	}
	```

Run:
* add breakpoint for issue #740
* run with arguments `https://github.com/junit-pioneer/junit-pioneer/issues/624 10`
* create and show thread dump
