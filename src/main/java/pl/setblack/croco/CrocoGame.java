package pl.setblack.croco;



import io.vavr.Tuple;
import io.vavr.Tuple2;

import java.math.BigDecimal;

public interface CrocoGame {
        Tuple2<CrocoGame, Result> sayNumber(String player, BigDecimal number);
}


class GameEmpty implements CrocoGame {

    public static final GameEmpty INSTANCE = new GameEmpty();
    @Override
    public Tuple2<CrocoGame, Result> sayNumber(String player, BigDecimal number) {
        return Tuple.of(new InitialNumber(player, number), Result.initialNumber(number) );
    }
}


class InitialNumber implements CrocoGame {
    public final String otherPlayer;

    public final BigDecimal prevValue;

    InitialNumber(String otherPlayer, BigDecimal prevValue) {
        this.otherPlayer = otherPlayer;
        this.prevValue = prevValue;
    }


    @Override
    public Tuple2<CrocoGame, Result> sayNumber(String player, BigDecimal number) {
        return Tuple.of( GameEmpty.INSTANCE,  calcResult(player, number));
    }

    private Result calcResult(String player, BigDecimal number) {
            int diff = number.compareTo(prevValue);
            if ( diff > 0) {
                return Result.from(Outcome.WON, number, prevValue,  player);
            } else if (diff < 0){
                return Result.from(Outcome.LOST, number, prevValue,  player);
            } else {
                return Result.from(Outcome.DRAW, number, prevValue, player);
            }
    }
}