import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        byte[] currentBoard = BoardReader.getNewBoard();
        BoardReader.displayBoard(currentBoard);
        System.out.println("\n\n");

        BoardReader.movePiece(currentBoard, (byte)3,(byte)1, (byte)3,(byte) 2);
        BoardReader.movePiece(currentBoard, (byte)2,(byte)0, (byte)4,(byte) 2);
        BoardReader.displayBoard(currentBoard);
        for (byte[] b : BoardReader.getValidMovesForPieceAtXY(currentBoard, (byte) 4, (byte) 2)) {
            System.out.println(Arrays.toString(b));
        }
    }
}