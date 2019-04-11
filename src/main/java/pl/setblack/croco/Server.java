package pl.setblack.croco;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.concurrent.Promise;
import io.vavr.jackson.datatype.VavrModule;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

import static kotlin.io.ConsoleKt.readLine;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


public class Server {
    final AtomicReference<MatchState> matchState = new AtomicReference<>(MatchState.empty());
    final ObjectMapper mapper = new ObjectMapper();
    {
        mapper.registerModule(new VavrModule());
    }

    void start() {
        RouterFunction route = route(
                GET("/time"), request ->
                        ServerResponse.ok().body(fromObject(LocalDateTime.now().toString())))
                .andRoute(POST("/croco/{player}/{number}"), request -> {
                    String player = request.pathVariable("player");
                    String numberAsString = request.pathVariable("number");
                    BigDecimal number = new BigDecimal(numberAsString);

                    Promise<Result> promisedResult = Promise.make();



                    final Mono<Result> result = Mono.fromFuture(promisedResult.future().toCompletableFuture());
                    final Mono<String> stringResult = result.map ( res -> {
                        try {
                            return mapper.writer().writeValueAsString(res);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);//TODO vavr cud
                        }
                    });
                    matchState.getAndUpdate( match -> match.sayNumber(player, number, promisedResult));
                    return ServerResponse.ok().body(fromPublisher(stringResult, String.class ));
                });


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


    public static void main(String[] args) {
        new Server().start();
    }
}
