package dev.nipafx.demo.modern;

import dev.nipafx.demo.modern.operations.Pretty;
import dev.nipafx.demo.modern.operations.Statistician;
import dev.nipafx.demo.modern.page.ExternalPage;
import dev.nipafx.demo.modern.page.GitHubIssuePage;
import dev.nipafx.demo.modern.page.GitHubPrPage;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

public class GitHubCrawl {

	/**
	 * @param args 0: path to GitHub issue or PR page
	 *             1: depth of tree that will be built
	 */
	public static void main(String[] args) throws Exception {
		var config = Configuration.parse(args);

		var rootPage = new GitHubIssuePage(URI.create("https://github.com/junit-pioneer/junit-pioneer/issues/624"), "",
				Set.of(
						new GitHubPrPage(URI.create("https://github.com/junit-pioneer/junit-pioneer/pull/629"), "", Set.of(), 629),
						new ExternalPage(URI.create("https://fasterxml.github.io/jackson-databind/javadoc/2.7/com/fasterxml/jackson/databind/ObjectMapper.html#findAndRegisterModules()"), "")
				), 624);

		System.out.println(Statistician.evaluate(rootPage));
		System.out.println(Pretty.pageList(rootPage));
	}

	private record Configuration(URI seedUrl, int depth) {

		static Configuration parse(String[] args) throws URISyntaxException {
			if (args.length < 2)
				throw new IllegalArgumentException("Please specify the seed URL and depth.");
			var seedUrl = new URI(args[0]);
			var depth = Integer.parseInt(args[1]);
			return new Configuration(seedUrl, depth);
		}

	}

}
