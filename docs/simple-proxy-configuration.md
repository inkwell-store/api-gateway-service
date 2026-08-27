# Simple proxy configuration

Gateway uses another approach to handle requests. Instead of annotation based model it uses a Functional model built around a main abstraction `RouterFunction<ServerResponse>`.

Within this function I can declare a Builder and configure a route with `predicates`, `filters` and `handlers` to decide if a routing match is found, modify the request if necessary and forward it.

* **Route**: the main block used by the gateway. It builds a routing configuration to handle incoming requests and returning responses.
* **Predicates**: used to match a configured route to a path from an incoming request
* **Filters**: perform operations on a request. There are two types of filters, those applied **before** the handler and those **after** the handler.
* **Handler**: once a route is matched by a predicate the handler decides what to do with the request.

Unfortunately, the documentation available at official docs is not well documented, so the following example shows how to setup a very basic proxy implementation:

```java
    @Bean
    RouterFunction<ServerResponse> authorizationServiceRouting() {
        return GatewayRouterFunctions
                .route("authorization-service")        // sets the routeId
                .route(
                    RequestPredicates.path("/hello"),  // defines a predicate to match against 
                    HandlerFunctions.http()            // defines proxy handler
                )
                .before(
                    BeforeFilterFunctions.uri("http://localhost:8081") // defines the destination of the proxy request
                ) 
                .build();
    }

```

In the example above the `/hello` path is proxy to `http://localhost:8081` and gateway then adds the path matched to this destination URI. The final request url is `http://localhost:8081/hello`. If the request is sent to a service that might expect a path like: `api/hello` then you need to use a filter (a before filter) to rewrite the matched path to the proper one, like:

```java
// now the destination can become http://localhost:8081/api/hello
.before(BeforeFilterFunctions.rewritePath("/hello", "/api/hello"))
```
