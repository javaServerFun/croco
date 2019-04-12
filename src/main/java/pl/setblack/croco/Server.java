package pl.setblack.croco;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.API;
import io.vavr.concurrent.Promise;
import io.vavr.jackson.datatype.VavrModule;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

import static kotlin.io.ConsoleKt.readLine;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


public class Server {
    private final AtomicReference<MatchState> matchState = new AtomicReference<>(MatchState.empty());
    private final ObjectMapper mapper = new ObjectMapper();
    private final Clock clock;
    public Server(Clock clock) {
        this.clock = clock;
        mapper.registerModule(new VavrModule());
    }

    private void start() {
        RouterFunction route = createRoute();


        HttpHandler httpHandler = RouterFunctions.toHttpHandler(route);
        ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
        DisposableServer server = HttpServer
                .create()
                .host("localhost")
                .port(8080)
                .handle(adapter)
                .bindNow();
        System.out.println("press enter");

        readLine();

        server.disposeNow();
    }

    RouterFunction createRoute() {
        return route(
                    GET("/time"), request ->
                            ServerResponse.ok().body(fromObject(LocalDateTime.now().toString())))
                    .andRoute(POST("/croco/{player}/{number}"), postCroco());
    }

    @NotNull
    private HandlerFunction<ServerResponse> postCroco() {
        return request -> {
            String player = request.pathVariable("player");
            String numberAsString = request.pathVariable("number");
            BigDecimal number = new BigDecimal(numberAsString);
            Promise<Result> promisedResult = Promise.make();
            final Mono<Result> result = Mono.fromFuture(promisedResult.future().toCompletableFuture());
            final Mono<String> stringResult = result.map ( res ->
                API.unchecked( () -> mapper.writer().writeValueAsString(res)).apply() //checked exception handling trick
            );
            matchState.getAndUpdate( match -> match.sayNumber(player, number, promisedResult));
            return ServerResponse.ok().body(fromPublisher(stringResult, String.class ));
        };
    }


    public static void main(String[] args) {
        new Server(Clock.systemDefaultZone()).start();
    }
}
