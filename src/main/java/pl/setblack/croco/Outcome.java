package pl.setblack.croco;

public enum Outcome {
    WAITING,
    WON,
    LOST,
    DRAW;

    Outcome opposite() {
        switch (this) {

            case WON:
                return LOST;
            case DRAW:
                return DRAW;
            case LOST:
                return WON;
            default:
            case WAITING:
                throw new IllegalStateException();
        }
    }


}
