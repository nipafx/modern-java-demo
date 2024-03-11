# Modern Java in Action

A repository for my live-coding talk [Modern Java in Action](https://nipafx.dev/talk-java-action).

## Next

Operations:
* implement methods in `Pretty`:
	```java
	public static String pageList(Page rootPage) {
		if (!(rootPage instanceof GitHubPage ghPage))
			return pageName(rootPage);

		return ghPage
				.subtree()
				.map(Pretty::pageName)
				.collect(joining("\n"));
	}

	public static String pageName(Page page) {
		return switch (page) {
			case ErrorPage(URI url, _) -> "💥 ERROR: " + url.getHost();
			case ExternalPage(URI url, _) -> "💤 EXTERNAL: " + url.getHost();
			case GitHubIssuePage(_, _, _, int nr) -> "🐈 ISSUE #" + nr;
			case GitHubPrPage(_, _, _, int nr) -> "🐙 PR #" + nr;
		};
	}
	```
* implement `Statistician::evaluatePage`:
	```java
	private void evaluatePage(Page page) {
		if (evaluatedPages.contains(page))
			return;
		evaluatedPages.add(page);

		switch (page) {
			case ErrorPage _ -> numberOfErrors++;
			case ExternalPage _ -> numberOfExternalLinks++;
			case GitHubIssuePage _ -> numberOfIssues++;
			case GitHubPrPage _ -> numberOfPrs++;
		}
	}
	```

Run `GitHubCrawl`.
