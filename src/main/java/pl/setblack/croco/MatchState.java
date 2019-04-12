package pl.setblack.croco;

import io.vavr.Tuple2;
import io.vavr.concurrent.Future;
import io.vavr.concurrent.Promise;
import io.vavr.control.Option;

import java.math.BigDecimal;

public class MatchState {

    public final CrocoGame gameState;

    public final Option<Promise<Result>> initialPlayerResult;

    public MatchState(CrocoGame gameState, Option<Promise<Result>> initialPlayerResult) {
        this.gameState = gameState;
        this.initialPlayerResult = initialPlayerResult;
    }

    static MatchState empty() {
        return new MatchState(GameEmpty.INSTANCE, Option.none());
    }

    MatchState sayNumber(String player, BigDecimal value, Promise<Result> resultPromise) {
        Tuple2<CrocoGame, Result> newGameState = gameState.sayNumber(player, value);
        if ( newGameState._2.outcome == Outcome.WAITING) {
            return new MatchState(newGameState._1, Option.some(resultPromise));
        } else {
            Result thisPlayerResult = newGameState._2;
            resultPromise.success(thisPlayerResult);
            initialPlayerResult.forEach( promise -> promise.success(thisPlayerResult.opposite(player)));
            return new MatchState(newGameState._1, Option.none() );
        }
    }
}
