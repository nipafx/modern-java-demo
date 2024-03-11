package dev.nipafx.demo.modern.page;

import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public record GitHubPrPage(URI url, String content, Set<Page> links, int prNumber) implements GitHubPage {

	public GitHubPrPage {
		requireNonNull(url);
		requireNonNull(content);
		links = Set.copyOf(links);
		if (prNumber <= 0)
			throw new IllegalArgumentException("PR number must be 1 or greater - was '%s' at '%s'.".formatted(prNumber, url));
	}

	public GitHubPrPage(URI url, String content, int prNumber) {
		this(url, content, new HashSet<>(), prNumber);
	}

	@Override
	public boolean equals(Object other) {
		return other == this
				|| other instanceof GitHubPrPage page
				&& this.url.equals(page.url());
	}

	@Override
	public int hashCode() {
		return Objects.hash(url);
	}

}
