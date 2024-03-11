package dev.nipafx.demo.modern.page;

import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public record GitHubIssuePage(URI url, String content, Set<Page> links, int issueNumber) implements GitHubPage {

	public GitHubIssuePage {
		requireNonNull(url);
		requireNonNull(content);
		links = Set.copyOf(links);
		if (issueNumber <= 0)
			throw new IllegalArgumentException("Issue number must be 1 or greater - was '%s' at '%s'.".formatted(issueNumber, url));
	}

	public GitHubIssuePage(URI url, String content, int issueNumber) {
		this(url, content, new HashSet<>(), issueNumber);
	}

	@Override
	public boolean equals(Object other) {
		return other == this
				|| other instanceof GitHubIssuePage page
				&& this.url.equals(page.url());
	}

	@Override
	public int hashCode() {
		return Objects.hash(url);
	}

}
