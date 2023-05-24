import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        byte[] currentBoard = BoardReader.getNewBoard();
        BoardReader.displayBoard(currentBoard);
        System.out.println("\n\n");

        //BoardReader.movePiece(currentBoard, (byte)3,(byte)1, (byte)3,(byte) 2);
        //BoardReader.movePiece(currentBoard, (byte)2,(byte)0, (byte)4,(byte) 2);
        BoardReader.displayBoard(currentBoard);
        int i = 0;
        Map<byte[], Set<byte[]>> moves = BoardReader.getMovesMapForAllPieceLocationsByColor(currentBoard, BoardReader.BLACK_COLOR);
        for (byte[] key : moves.keySet()) {
            System.out.println("\n"+Arrays.toString(key)+" -> " +
                    "");
            for (byte[] b : moves.get(key)) {
                System.out.println("\t"+Arrays.toString(b));
                i++;
            }
        }
        System.out.println(i);
    }
}