package dev.nipafx.demo.modern.operations;

import dev.nipafx.demo.modern.page.ErrorPage;
import dev.nipafx.demo.modern.page.ExternalPage;
import dev.nipafx.demo.modern.page.GitHubIssuePage;
import dev.nipafx.demo.modern.page.GitHubPage;
import dev.nipafx.demo.modern.page.GitHubPrPage;
import dev.nipafx.demo.modern.page.Page;

import java.net.URI;

import static java.util.stream.Collectors.joining;

public class Pretty {

	private Pretty() {
		// private constructor to prevent instantiation
	}

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

}
