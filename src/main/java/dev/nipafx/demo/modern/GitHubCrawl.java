package dev.nipafx.demo.modern;

import dev.nipafx.demo.modern.crawler.PageTreeFactory;
import dev.nipafx.demo.modern.operations.Pretty;
import dev.nipafx.demo.modern.operations.Statistician;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;

public class GitHubCrawl {

	/**
	 * @param args 0: path to GitHub issue or PR page
	 *             1: depth of tree that will be built
	 */
	public static void main(String[] args) throws Exception {
		var config = Configuration.parse(args);

		System.out.printf("%nTo see virtual threads in action, run this while the app is resolving a bunch of links:%n");
		System.out.printf("jcmd %s Thread.dump_to_file -format=json -overwrite threads.json%n%n", ProcessHandle.current().pid());

		// TODO
		var client = (HttpClient) null;
		var factory = new PageTreeFactory(client);
		var rootPage = factory.createPage(config.seedUrl(), config.depth());

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
