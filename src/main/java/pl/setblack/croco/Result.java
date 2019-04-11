package pl.setblack.croco;

import io.vavr.control.Option;

import java.math.BigDecimal;

public class Result {
    public final Outcome outcome;

    public final BigDecimal yourNumber;

    public final Option<BigDecimal> otherNumber;

    public final Option<String> otherPlayer;

    public Result(Outcome outcome, BigDecimal yourNumber,Option<BigDecimal> otherNumber, Option<String> otherPlayer) {
        this.outcome = outcome;
        this.yourNumber = yourNumber;
        this.otherNumber = otherNumber;
        this.otherPlayer = otherPlayer;
    }

    static Result initialNumber(BigDecimal yourNumber ) {
        return  new Result(Outcome.WAITING, yourNumber, Option.none(), Option.none());
    }

    static Result from(Outcome outcome, BigDecimal yourNumber, BigDecimal otherNumber, String otherPlayer ) {
        return  new Result(outcome, yourNumber, Option.some(otherNumber), Option.some(otherPlayer));
    }

    Result opposite(final String player) {
        return Result.from(this.outcome.opposite(), this.otherNumber.getOrElseThrow( ()-> new IllegalStateException()), this.yourNumber, player);
    }

}
