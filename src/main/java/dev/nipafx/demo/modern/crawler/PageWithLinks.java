package dev.nipafx.demo.modern.crawler;

import dev.nipafx.demo.modern.page.Page;

import java.net.URI;
import java.util.Set;

import static java.util.Objects.requireNonNull;

record PageWithLinks(Page page, Set<URI> links) {

	PageWithLinks {
		requireNonNull(page);
		requireNonNull(links);
		links = Set.copyOf(links);
	}

	public PageWithLinks(Page page) {
		this(page, Set.of());
	}

}
