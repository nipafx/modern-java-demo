package dev.nipafx.demo.modern;

import dev.nipafx.demo.modern.crawler.PageTreeFactory;
import dev.nipafx.demo.modern.operations.Pretty;
import dev.nipafx.demo.modern.operations.ResultServer;
import dev.nipafx.demo.modern.operations.Statistician;
import dev.nipafx.demo.modern.page.ExternalPage;
import dev.nipafx.demo.modern.page.GitHubIssuePage;
import dev.nipafx.demo.modern.page.GitHubPrPage;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.nio.file.Path;
import java.util.Set;

import static dev.nipafx.demo.modern.Util.join;
import static java.lang.StringTemplate.RAW;

public class GitHubCrawl {

	/**
	 * @param args 0: path to GitHub issue or PR page
	 *             1: depth of tree that will be built
	 */
	public static void main(String[] args) throws Exception {
		var config = Configuration.parse(args);

		System.out.printf("%nTo see virtual threads in action, run this while the app is resolving a bunch of links:%n");
		System.out.printf("jcmd %s Thread.dump_to_file -format=json -overwrite threads.json%n%n", ProcessHandle.current().pid());

		var client = HttpClient.newHttpClient();
		var factory = new PageTreeFactory(client);
		var rootPage = factory.createPage(config.seedUrl(), config.depth());

		System.out.println(join(RAW."""

				---

				\{Statistician.evaluate(rootPage)}

				\{Pretty.pageList(rootPage)}

				"""));

		ResultServer.serve(rootPage, Path.of("serve"));
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
