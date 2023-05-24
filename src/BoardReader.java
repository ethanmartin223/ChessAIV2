import java.util.HashSet;
import java.util.Set;

public abstract class BoardReader {
    public static final byte NULL_SPACE = 0x0;

    public static final byte BLACK_PAWN = 0x1;
    public static final byte BLACK_KNIGHT = 0x2;
    public static final byte BLACK_BISHOP = 0x3;
    public static final byte BLACK_ROOK = 0x4;
    public static final byte BLACK_QUEEN = 0x5;
    public static final byte BLACK_KING = 0x6;

    public static final byte WHITE_PAWN = 0xA;
    public static final byte WHITE_KNIGHT = 0xB;
    public static final byte WHITE_BISHOP = 0xC;
    public static final byte WHITE_ROOK = 0xD;
    public static final byte WHITE_QUEEN = 0xE;
    public static final byte WHITE_KING = 0xF;

    //directions that pieces can move in
    public static final byte[][] DIAGONAL_MOVES = new byte[][] {{-1,1}, {1,1}, {-1,-1}, {1,-1}};
    public static final byte[][] HORIZONTAL_MOVES = new byte[][] {{-1,0}, {0,1}, {0,-1}, {1,0}};
    public static final byte[][] ALL_DIR_MOVES = new byte[][] {{-1,1}, {1,1}, {-1,-1}, {1,-1},
            {-1,0}, {0,1}, {0,-1}, {1,0}};
    public static final byte[][] L_MOVES = new byte[][] {{-2,1}, {-2,-1}, {2,-1}, {2,1},
            {1,2}, {-1,2}, {1,-2}, {-1,-2}};

    //prevent instantiation of BoardReader
    private BoardReader() {}

    // If ~136 bytes are reserved per board, we can store on avg 10_000_000 boards in memory at aprox 1.36gb ram
    // On average there are about 35 valid moves per board state

    // All of these assume that board is 8x8
    // a1 is ALWAYS white and considered to be 0th position

    public static byte getIndexForPieceAtXY(byte x, byte y) {
        return (byte)(y*8+x);
    }

    private static int guessNOfMovesCanLookAheadBasedOnMemoryRemaining() {
        return 0;
    }

    /** Returns true if the piece at p1X,p1Y is the same color as the piece at p2X,p2Y*/
    private static boolean piecesAreSameColor(byte[] board, byte p1X, byte p1Y, byte p2X, byte p2Y) {
        return getPieceAt(board, getIndexForPieceAtXY(p1X, p1Y))>0x9 ==
                getPieceAt(board, getIndexForPieceAtXY(p2X, p2Y))>0x9;
    }

    /** Returns the piece byte identifier at the specified index arr**/
    private static byte getPieceAt(byte[] board, int index) {
        return board[index];
    }

    /** Returns the piece byte identifier at the specified x,y coordinates **/
    private static byte getPieceAt(byte[] board, byte x, byte y) {
        return board[getIndexForPieceAtXY(x,y)];
    }

    /** Sets the piece at the specified x,y coordinate to pieceValue*/
    public static void setPieceAt(byte[] board, byte x, byte y, byte pieceValue) {
        board[getIndexForPieceAtXY(x,y)] = pieceValue;
    }

    /** Checks if piece at sX,sY can move or take the piece at dX,dY coordinates */
    private static boolean isValidMove(byte[] board, byte sX, byte sY, byte dX, byte dY) {
        if (dX > -1 && dY > -1 && dY < 8 && dX < 8)
            return !piecesAreSameColor(board, sX, sY, dX, dY);
        return false;
    }

    private static boolean isValidMoveWithoutTaking(byte[] board, byte sX, byte sY, byte dX, byte dY) {
        if (dX > -1 && dY > -1 && dY < 8 && dX < 8)
            return getPieceAt(board, dX, dY) == NULL_SPACE;
        return false;
    }

    /** Moves piece from sx, sy to dx, dy */
    public static void movePiece(byte[] board, byte sX, byte sY, byte dX, byte dY) {
        byte k = getIndexForPieceAtXY(sX, sY);
        byte movingPiece = board[k];
        board[k] = NULL_SPACE;
        board[getIndexForPieceAtXY(dX, dY)] = movingPiece;
    }

    /** Gets valid moves for the piece at x,y */
    public static Set<byte[]> getValidMovesForPieceAtXY(byte[] board, byte x, byte y) {
        Set<byte[]> possibleMoves = new HashSet<byte[]>();
        byte piece = getPieceAt(board, x, y);
        switch (piece) {
            case BLACK_BISHOP, WHITE_BISHOP -> getValidMoveRayHelper(board, x, y, possibleMoves, DIAGONAL_MOVES);
            case BLACK_KNIGHT, WHITE_KNIGHT -> getValidMoveHelper(board, x, y, possibleMoves, L_MOVES);
            case BLACK_KING, WHITE_KING ->     getValidMoveHelper(board, x, y, possibleMoves, ALL_DIR_MOVES);
            case BLACK_ROOK, WHITE_ROOK ->     getValidMoveRayHelper(board, x, y, possibleMoves, HORIZONTAL_MOVES);
            case BLACK_QUEEN, WHITE_QUEEN ->   getValidMoveRayHelper(board, x, y, possibleMoves, ALL_DIR_MOVES);
            case BLACK_PAWN, WHITE_PAWN ->     getValidMoveForPawn(board, x, y, possibleMoves);
        }
        return possibleMoves;
    }

    private static void getValidMoveForPawn(byte[] board, byte x, byte y, Set<byte[]> possibleMoves) {
        byte direction = (byte) (getPieceAt(board, x, y) > 0x9 ? 1 : -1);
        if (isValidMoveWithoutTaking(board, x, y, x, (byte) (y + direction))) {
            possibleMoves.add(new byte[]{x, (byte) (y + direction)});
            if ((y == 1 || y == 6) && isValidMoveWithoutTaking(board, x, y, x, (byte) (y + direction * 2)))
                possibleMoves.add(new byte[]{x, (byte) (y + direction * 2)});
        }
        if (isValidMove(board, x, y, (byte) (x + 1), (byte) (y + direction)) && !(NULL_SPACE == getPieceAt(board, (byte) (x + 1), (byte) (y + direction)))
                && !piecesAreSameColor(board, x, y, (byte) (x + 1), (byte) (y + direction)))
            possibleMoves.add(new byte[]{(byte) (x + 1), (byte) (y + direction)});
        if (isValidMove(board, x, y, (byte) (x - 1), (byte) (y + direction)) && !(NULL_SPACE == getPieceAt(board, (byte) (x - 1), (byte) (y + direction)))
                && !piecesAreSameColor(board, x, y, (byte) (x - 1), (byte) (y + direction)))
            possibleMoves.add(new byte[]{(byte) (x - 1), (byte) (y + direction)});
    }

    /** Shoots a ray in all directions listed in byte[][] lMoves and returns all possible moves for that direction*/
    private static void getValidMoveRayHelper(byte[] board, byte x, byte y, Set<byte[]> possibleMoves, byte[][] lMoves) {
        for (byte[] dir : lMoves) {
            byte tx=(byte)(x+dir[0]),ty=(byte)(y+dir[1]);
            while (isValidMove(board, x, y, tx, ty)) {
                possibleMoves.add(new byte[] {tx, ty});
                tx += dir[0];
                ty += dir[1];
            }
        }
    }

    /* adds all valid moves of lMoves to possibleMoves for the piece at x,y*/
    private static void getValidMoveHelper(byte[] board, byte x, byte y, Set<byte[]> possibleMoves, byte[][] lMoves) {
        for (byte[] dir : lMoves) {
            byte tx=(byte)(x+dir[0]),ty=(byte)(y+dir[1]);
            if (isValidMove(board, x, y, tx, ty)) {
                possibleMoves.add(new byte[]{tx, ty});
            }
        }
    }

    /** Returns a new byte[] array with length 64 which contains the default layout of a chess board
     * at the start of the game. */
    public static byte[] getNewBoard() {
        byte[] outputBoard =  new byte[64];
        for (int i = 8; i <16; i++) outputBoard[i] = WHITE_PAWN;
        outputBoard[0] = WHITE_ROOK;
        outputBoard[1] = WHITE_KNIGHT;
        outputBoard[2] = WHITE_BISHOP;
        outputBoard[3] = WHITE_KING;
        outputBoard[4] = WHITE_QUEEN;
        outputBoard[5] = WHITE_BISHOP;
        outputBoard[6] = WHITE_KNIGHT;
        outputBoard[7] = WHITE_ROOK;
        for (int i = 48; i <56; i++) outputBoard[i] = BLACK_PAWN;
        outputBoard[56] = BLACK_ROOK;
        outputBoard[57] = BLACK_KNIGHT;
        outputBoard[58] = BLACK_BISHOP;
        outputBoard[59] = BLACK_KING;
        outputBoard[60] = BLACK_QUEEN;
        outputBoard[61] = BLACK_BISHOP;
        outputBoard[62] = BLACK_KNIGHT;
        outputBoard[63] = BLACK_ROOK;
        return outputBoard;
    }

    public static void displayBoard(byte[] board) {
        for (int i = 0; i<8; System.out.print("\033[34m"+i+++"  \033[0m"));

        System.out.println();
        for (int i = 0; i < board.length; i++) {
            if (i%8==0 && i!=0) System.out.println("\033[34m"+((i/8)-1)+"  \033[0m");
            if (board[i] < 0x9) System.out.print(board[i]+"  ");
            else System.out.print(board[i]+" ");
        }
        System.out.println("\033[34m"+7+"  \033[0m");
    }
}
