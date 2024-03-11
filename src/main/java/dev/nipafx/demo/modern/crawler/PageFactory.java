package dev.nipafx.demo.modern.crawler;

import dev.nipafx.demo.modern.page.ExternalPage;
import dev.nipafx.demo.modern.page.GitHubIssuePage;
import dev.nipafx.demo.modern.page.GitHubPrPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

class PageFactory {

	private static final Set<String> GITHUB_HOSTS = Set.of("github.com", "user-images.githubusercontent.com");
	private static final Pattern GITHUB_TRACKED_PAGE = Pattern.compile("/issues/\\d+/?$|/pull/\\d+/?$");
	private static final Pattern GITHUB_ISSUE_NUMBER = Pattern.compile(".*/issues/(\\d+)/?.*");
	private static final Pattern GITHUB_PR_NUMBER = Pattern.compile(".*/pull/(\\d+)/?.*");

	private static final String GITHUB_ISSUE_CONTENT_SELECTOR = "#show_issue";
	private static final String GITHUB_PR_CONTENT_SELECTOR = ".clearfix.js-issues-results";

	private PageFactory() {
		// private constructor to prevent instantiation of factory class
	}

	public static PageWithLinks parsePage(URI url, String html) {
		// turn this into an `if`, I dare you!
		return switch (url) {
			case URI u when u.getHost().equals("github.com") && u.getPath().contains("/issues/") -> parseIssuePage(url, html);
			case URI u when u.getHost().equals("github.com") && u.getPath().contains("/pull/") -> parsePrPage(url, html);
			default -> parseExternalPage(url, html);
		};
	}

	static PageWithLinks parseIssuePage(URI url, String html) {
		var document = Jsoup.parse(html);
		var content = extractContent(document, GITHUB_ISSUE_CONTENT_SELECTOR);
		var links = extractLinks(url, document, GITHUB_ISSUE_CONTENT_SELECTOR);
		var issueNr = getFirstMatchAsNumber(GITHUB_ISSUE_NUMBER, url);
		return new PageWithLinks(new GitHubIssuePage(url, content, issueNr), links);
	}

	static PageWithLinks parsePrPage(URI url, String html) {
		var document = Jsoup.parse(html);
		var content = extractContent(document, GITHUB_PR_CONTENT_SELECTOR);
		var links = extractLinks(url, document, GITHUB_PR_CONTENT_SELECTOR);
		var issueNr = getFirstMatchAsNumber(GITHUB_PR_NUMBER, url);
		return new PageWithLinks(new GitHubPrPage(url, content, issueNr), links);
	}

	private static PageWithLinks parseExternalPage(URI url, String html) {
		return new PageWithLinks(new ExternalPage(url, html), Set.of());
	}

	private static String extractContent(Document document, String cssContentSelector) {
		var selectedElements = document.select(cssContentSelector);
		if (selectedElements.size() != 1)
			throw new IllegalArgumentException("The CSS selector '%s' yielded %d elements".formatted(cssContentSelector, selectedElements.size()));
		return selectedElements.getFirst().toString();
	}

	private static Set<URI> extractLinks(URI url, Document document, String cssContentSelector) {
		return document
				.select(cssContentSelector + " a[href]").stream()
				.map(element -> element.attribute("href").getValue())
				.flatMap(href -> normalizePotentialLink(url, href))
				.filter(PageFactory::shouldRegisterLink)
				.collect(toSet());
	}

	private static Stream<URI> normalizePotentialLink(URI pageUrl, String href) {
		if (href == null || href.isBlank())
			return Stream.empty();

		try {
			var url = pageUrl.resolve(new URI(href));
			var isCyclicLink = url.equals(pageUrl);
			if (isCyclicLink)
				return Stream.empty();
			return Stream.of(url);
		} catch (URISyntaxException ex) {
			// nothing to be done
			return Stream.empty();
		}
	}

	private static boolean shouldRegisterLink(URI url) {
		if (url.getHost() == null)
			return false;

		var isExternalUrl = !GITHUB_HOSTS.contains(url.getHost());
		return isExternalUrl || GITHUB_TRACKED_PAGE.matcher(url.toString()).find();
	}

	private static int getFirstMatchAsNumber(Pattern pattern, URI url) {
		var issueNumberMatcher = pattern.matcher(url.toString());
		var found = issueNumberMatcher.find();
		if (!found)
			throw new IllegalStateException("Alleged issue/PR URL %s does not seem to contain a number.".formatted(url));
		return Integer.parseInt(issueNumberMatcher.group(1));
	}

}
