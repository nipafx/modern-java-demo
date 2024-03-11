package dev.nipafx.demo.modern.crawler;

import dev.nipafx.demo.modern.page.ErrorPage;
import dev.nipafx.demo.modern.page.ExternalPage;
import dev.nipafx.demo.modern.page.GitHubIssuePage;
import dev.nipafx.demo.modern.page.GitHubPrPage;
import dev.nipafx.demo.modern.page.Page;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;

public class PageTreeFactory {

	private final HttpClient client;
	private final ConcurrentMap<URI, Page> resolvedPages;

	public PageTreeFactory(HttpClient client) {
		this.client = requireNonNull(client);
		resolvedPages = new ConcurrentHashMap<>();
	}

	public Page createPage(URI url, int depth) throws InterruptedException {
		if (resolvedPages.containsKey(url)) {
			System.out.printf("Found cached '%s'%n", url);
			return resolvedPages.get(url);
		}

		System.out.printf("Resolving '%s'...%n", url);
		var pageWithLinks = fetchPageWithLinks(url);
		var page = pageWithLinks.page();
		resolvedPages.computeIfAbsent(page.url(), __ -> page);
		System.out.printf("Resolved '%s' with children: %s%n", url, pageWithLinks.links());

		return switch (page) {
			case GitHubIssuePage(var isUrl, var content, _, int nr) ->
					new GitHubIssuePage(isUrl, content, resolveLinks(pageWithLinks.links(), depth - 1), nr);
			case GitHubPrPage(var prUrl, var content, _, int nr) ->
					new GitHubIssuePage(prUrl, content, resolveLinks(pageWithLinks.links(), depth - 1), nr);
			case ExternalPage _, ErrorPage _ -> page;
		};
	}

	private PageWithLinks fetchPageWithLinks(URI url) throws InterruptedException {
		try {
			var pageBody = fetchPageAsString(url);
			return PageFactory.parsePage(url, pageBody);
		} catch (InterruptedException iex) {
			throw iex;
		} catch (Exception ex) {
			return new PageWithLinks(new ErrorPage(url, ex));
		}
	}

	private String fetchPageAsString(URI url) throws IOException, InterruptedException {
		var request = HttpRequest
				.newBuilder(url)
				.GET()
				.build();
		return client
				.send(request, BodyHandlers.ofString())
				.body();
	}

	private Set<Page> resolveLinks(Set<URI> links, int depth) throws InterruptedException {
		if (depth < 0)
			return Collections.emptySet();

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
	}

}
