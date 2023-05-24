public class BoardState {
    private final byte[] board;
    private byte playerMove;

    public BoardState(byte[] b, byte pM) {
        board = b;
        this.playerMove = pM;
    }
}
