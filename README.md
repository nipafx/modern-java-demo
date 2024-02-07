# Modern Java in Action

A repository for my live-coding talk [Modern Java in Action](https://nipafx.dev/talk-java-action).

## Next

Sealed types:
* `sealed interface Page permits ErrorPage, SuccessfulPage` with `URI url()`
* `sealed interface SuccessfulPage extends Page permits ExternalPage, GitHubPage` with `String content()`
* `sealed interface GitHubPage extends SuccessfulPage permits GitHubIssuePage, GitHubPrPage` with `Set<Page> links()` and
	```java
	default Stream<Page> subtree() {
		var subtree = new ArrayList<Page>(Set.of(this));
		var upcomingPages = new LinkedHashSet<>(this.links());

		while (!upcomingPages.isEmpty()) {
			var nextPage = upcomingPages.removeFirst();
			if (!subtree.contains(nextPage) && nextPage instanceof GitHubPage nextGhPage)
				new LinkedHashSet<>(nextGhPage.links())
						.reversed()
						.forEach(upcomingPages::addFirst);
			subtree.add(nextPage);
		}

		return subtree.stream();
	}
	```
