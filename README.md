# Modern Java in Action

A repository for my live-coding talk [Modern Java in Action](https://nipafx.dev/talk-java-action).

## Next

Records:
* `record ExternalPage(URI url, String content)`
	* compact constructor checks all arguments
	* `equals` with `instanceof`
* `record GitHubPrPage(URI url, String content, Set<URI> links, int prNumber)`
	* compact constructor checks all arguments
	* additional constructor without `links`
	* `equals` with `instanceof`
