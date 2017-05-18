package com.usamin.nana.extractor;
import static spark.Spark.*;

//http://sparkjava.com/documentation#views-and-templates
//curl localhost:4567/hello/world
public class spark {
	public spark() {
		
		get("/hello/:name", (request, response) -> {
		    return "Hello: " + request.params(":name");
		});
		
		// matches "GET /say/hello/to/world"
		// request.splat()[0] is 'hello' and request.splat()[1] 'world'
		get("/say/*/to/*", (request, response) -> {
			System.err.println(request);
			System.err.println(request.splat());
			System.err.println(request.params()); //empty since no :item
		    return "Number of splat parameters: " + request.splat().length;
		});
		
	}
	

}
