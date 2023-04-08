import org.json.JSONObject;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class CtrlGameCanvas {
  
    private Canvas cnv;
    private GraphicsContext gc;
    private AnimationTimer animationTimer;

    private double borderSize = 5;

    private String gameStatus = "playing";

    private int pointsP1 = 0;
    private int pointsP2 = 0;
    private double player1X = 700;
    private double player1Y = 200;
    private double player2X = 50;
    private double player2Y = 200;
    private final double playerWidth = 5;
    private final double playerHeight = 100;
    private final double playerHalf = playerHeight / 2;
    private double playerSpeed = 250;
    private final double playerSpeedIncrement = 15;
    public String playerDirection = "none";

    private double ballX = Double.POSITIVE_INFINITY;
    private double ballY = Double.POSITIVE_INFINITY;
    private final double ballSize = 15;
    private final double ballHalf = ballSize / 2;
    private double ballSpeed = 200;
    private final double ballSpeedIncrement = 25;
    private String ballDirection = "upRight";

    public static boolean start=false;
    
    public CtrlGameCanvas () { }
    // Iniciar el context i bucle de dibuix
    public void start (Canvas canvas) {

        cnv = canvas;

        // Define drawing context
        gc = canvas.getGraphicsContext2D();

        // Set initial positions
        ballX = cnv.getWidth() / 2;
        ballY = cnv.getHeight() / 2;
        player1Y = 270;
        player2Y = 270;
        
        // Init drawing bucle
        animationTimer = new UtilsFps(this::run, this::draw);
        animationTimer.start();
    }

    // Aturar el bucle de dibuix
    public void stop () {
        animationTimer.stop();
    }

    // Animar
    private void run(double fps) {
        if (fps < 1) return;
        if(start==true){
        final double boardWidth = cnv.getWidth();
        final double boardHeight = cnv.getHeight();
        // Move player
        switch (playerDirection) {
            case "up":
                player1Y = player1Y - playerSpeed / fps;
                player2Y = player2Y - playerSpeed / fps;
                break;
            case "down":
                player1Y = player1Y + playerSpeed / fps;
                player2Y = player2Y + playerSpeed / fps;
                break;
        }
        /*JSONObject obj1 = new JSONObject("{}");
        System.out.println(playerDirection);
        obj1.put("type", "movePlayer");
        obj1.put("playerDirection",playerDirection);
        obj1.put("player1Y",player1Y);
        obj1.put("player2Y", player2Y);
        Main.socketClient.safeSend(obj1.toString());
        //System.out.println("Send WebSocket: " + obj.toString());
        Main.socketClient.onMessage((response) -> {
            System.out.println("message");
            // JavaFX necessita que els canvis es facin des de el thread principal
            Platform.runLater(()->{ 
                // Fer aquí els canvis a la interficie
                JSONObject msgObj = new JSONObject(response);
                player1Y=msgObj.getDouble("player1Y");
                System.out.println("Player y: "+player1Y);
                player2Y=msgObj.getDouble("player2Y");
            });
        });*/

        //  Keep player in bounds
        final double playerMinY = 5 + borderSize + playerHalf;
        final double playerMaxY = boardHeight - playerHalf - 5 - borderSize;

        if (player1Y < playerMinY) {

            player1Y = playerMinY;

        } else if (player1Y > playerMaxY) {

            player1Y = playerMaxY;
        }

        if (player2Y < playerMinY) {

            player2Y = playerMinY;

        } else if (player2Y > playerMaxY) {

            player2Y = playerMaxY;
        }
        //Main.socketClient = UtilsWS.getSharedInstance(Main.protocolWS + "://" + Main.host + ":" + Main.port);
        JSONObject obj = new JSONObject("{}");
        obj.put("type", "ballDirection");
        obj.put("player1Y",player1Y);
        obj.put("player2Y", player2Y);
        Main.socketClient.safeSend(obj.toString());
        //System.out.println("Send WebSocket: " + obj.toString());
        Main.socketClient.onMessage((response) -> {
            //System.out.println("message");
            // JavaFX necessita que els canvis es facin des de el thread principal
            Platform.runLater(()->{ 
                // Fer aquí els canvis a la interficie
                JSONObject msgObj = new JSONObject(response);
                System.out.println(response);
                if(msgObj.getString("status").equals("Ball")){
                    ballDirection=msgObj.getString("ballDirection");
                    ballX=msgObj.getDouble("ballX");
                    ballY=msgObj.getDouble("ballY");
                    pointsP1=msgObj.getInt("pointsP1");
                    pointsP2=msgObj.getInt("pointsP2");
                    gameStatus=msgObj.getString("gameStatus");
                }
            });
        });
        // Move ball
        /*double ballNextX = ballX;
        double ballNextY = ballY;
        switch (ballDirection) {
            case "upRight": 
                ballNextX = ballX + ballSpeed / fps;
                ballNextY = ballY - ballSpeed / fps;
                break;
            case "upLeft": 
                ballNextX = ballX - ballSpeed / fps;
                ballNextY = ballY - ballSpeed / fps;
                break;
            case "downRight": 
                ballNextX = ballX + ballSpeed / fps;
                ballNextY = ballY + ballSpeed / fps;
                break;
            case "downLeft": 
                ballNextX = ballX - ballSpeed / fps;
                ballNextY = ballY + ballSpeed / fps;
                break;
        }

        // Check ball collision with board sides
        final double[][] lineBall = { {ballX, ballY}, {ballNextX, ballNextY} };

        final double[][] lineBoardLeft = { {borderSize, 0}, {borderSize, boardHeight} };
        final double[] intersectionLeft = findIntersection(lineBall, lineBoardLeft);

        final double boardMaxX = boardWidth - borderSize;
        final double[][] lineBoardRight = { {boardMaxX, 0}, {boardMaxX, boardHeight} };
        final double[] intersectionRight = findIntersection(lineBall, lineBoardRight);

        final double[][] lineBoardTop = { {0, borderSize}, {boardWidth, borderSize} };
        final double[] intersectionTop = findIntersection(lineBall, lineBoardTop);

        final double boardMaxY = boardHeight - borderSize;
        final double[][] lineBoardBot = { {0, boardMaxY}, {boardWidth, boardMaxY} };
        final double[] intersectionBot = findIntersection(lineBall, lineBoardBot);

        if (intersectionLeft != null) {
            switch (ballDirection) {
                case "upLeft": 
                    ballDirection = "upRight";
                    break;
                case "downLeft": 
                    ballDirection = "downRight";
                    break;
            }
            ballX = intersectionLeft[0] + 1;
            ballY = intersectionLeft[1];

        } else if (intersectionRight != null) {

            switch (ballDirection) {
                case "upRight": 
                    ballDirection = "upLeft";
                    break;
                case "downRight": 
                    ballDirection = "downLeft";
                    break;
            }
            ballX = intersectionRight[0] - 1;
            ballY = intersectionRight[1];

        } else if (intersectionTop != null) {

            switch (ballDirection) {
                case "upRight": 
                    ballDirection = "downRight"; 
                    break;
                case "upLeft": 
                    ballDirection = "downLeft"; 
                    break;
            }
            ballX = intersectionTop[0];
            ballY = intersectionTop[1] + 1;

        } else if (intersectionBot != null) {

            switch (ballDirection) {
                case "downRight": 
                    ballDirection = "upRight"; 
                    break;
                case "downLeft": 
                    ballDirection = "upLeft"; 
                    break;
            }
            ballX = intersectionBot[0];
            ballY = intersectionBot[1] - 1;

        } else {
            if (ballNextY > boardHeight) {
                gameStatus = "gameOver";
            } else {
                ballX = ballNextX;
                ballY = ballNextY;
            }
        }

        // Check ball collision with player
        final double[][] linePlayer1 = { {player1X, player1Y + playerHalf}, {player1X, player1Y - playerHalf} };
        final double[] intersectionPlayer1 = findIntersection(lineBall, linePlayer1);

        if (intersectionPlayer1 != null) {
            switch (ballDirection) {
                case "downRight":
                    ballDirection = "downLeft";
                    break;
                case "upRight": 
                    ballDirection = "upLeft";
                    break;
            }
            ballX = intersectionPlayer1[0] - 1; // cambiar si el jugador es el de la izquierda a + 1
            ballY = intersectionPlayer1[1];
            playerPoints = playerPoints + 1;
            ballSpeed = ballSpeed + ballSpeedIncrement;
            playerSpeed = playerSpeed + playerSpeedIncrement;
        }

        final double[][] linePlayer2 = { {player2X, player2Y - playerHalf}, {player2X, player2Y + playerHalf} };
        final double[] intersectionPlayer2 = findIntersection(lineBall, linePlayer2);

        if (intersectionPlayer2 != null) {
            switch (ballDirection) {
                case "downLeft":
                    ballDirection = "downRight";
                    break;
                case "upLeft": 
                    ballDirection = "upRight";
                    break;
            }
            ballX = intersectionPlayer2[0] + 1; // cambiar si el jugador es el de la izquierda a + 1
            ballY = intersectionPlayer2[1];
            playerPoints = playerPoints + 1;
            ballSpeed = ballSpeed + ballSpeedIncrement;
            playerSpeed = playerSpeed + playerSpeedIncrement;
        }
*/
        // Set player X position
        player1X = 700;
        player2X = 50;
    }
    }

    // Dibuixar
    private void draw() {

        // Clean drawing area
        gc.clearRect(0, 0, cnv.getWidth(), cnv.getHeight());

        // Draw board
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(borderSize);
        gc.strokeRect(0, 0, cnv.getWidth(), borderSize);
        gc.strokeRect(0, cnv.getHeight() - borderSize, cnv.getWidth(), borderSize);

        // Draw player
        gc.setStroke(Color.GREEN);
        gc.setLineWidth(playerWidth);
        gc.strokeRect(player1X, player1Y - playerHalf, playerWidth, playerHeight);

        gc.setStroke(Color.GREEN);
        gc.setLineWidth(playerWidth);
        gc.strokeRect(player2X, player2Y - playerHalf, playerWidth, playerHeight);

        // Draw ball
        gc.setFill(Color.BLACK);
        gc.fillArc(ballX - ballHalf, ballY - ballHalf, ballSize, ballSize, 0.0, 360, ArcType.ROUND);

        // Draw text with points
        final double boardCenterX = cnv.getWidth() / 2;
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 20));
        String pointsText = "Points P1: " + pointsP1 + " VS Points P2: " + pointsP2;
        drawText(gc, pointsText, boardCenterX, 20, "right");

        // Draw game over text
        if (gameStatus == "gameOver") {
            final double boardCenterY = cnv.getHeight() / 2;

            gc.setFont(new Font("Arial", 40));
            drawText(gc, "GAME OVER", boardCenterX, boardCenterY - 20, "center");

            gc.setFont(new Font("Arial", 20));
            drawText(gc, "You are a loser!", boardCenterX, boardCenterY + 20, "center");
        }
    }

    public static void drawText(GraphicsContext gc, String text, double x, double y, String alignment) {
        Text tempText = new Text(text);
        tempText.setFont(gc.getFont());
        final double textWidth = tempText.getLayoutBounds().getWidth();
        final double textHeight = tempText.getLayoutBounds().getHeight();
        switch (alignment) {
            case "center":
                x = x - textWidth / 2;
                y = y + textHeight / 2;
                break;
            case "right":
                x = x - textWidth;
                y = y + textHeight / 2;
                break;
            case "left":
                y = y + textHeight / 2;
                break;
        }
        gc.fillText(text, x, y);
    }

    public static double[] findIntersection(double[][] lineA, double[][] lineB) {
        double[] result = new double[2];
    
        final double aX0 = lineA[0][0];
        final double aY0 = lineA[0][1];
        final double aX1 = lineA[1][0];
        final double aY1 = lineA[1][1];
    
        final double bX0 = lineB[0][0];
        final double bY0 = lineB[0][1];
        final double bX1 = lineB[1][0];
        final double bY1 = lineB[1][1];
    
        double x, y;
    
        if (aX1 == aX0) { // lineA is vertical
            if (bX1 == bX0) { // lineB is vertical too
                return null;
            }
            x = aX0;
            final double bM = (bY1 - bY0) / (bX1 - bX0);
            final double bB = bY0 - bM * bX0;
            y = bM * x + bB;
        } else if (bX1 == bX0) { // lineB is vertical
            x = bX0;
            final double aM = (aY1 - aY0) / (aX1 - aX0);
            final double aB = aY0 - aM * aX0;
            y = aM * x + aB;
        } else {
            final double aM = (aY1 - aY0) / (aX1 - aX0);
            final double aB = aY0 - aM * aX0;
    
            final double bM = (bY1 - bY0) / (bX1 - bX0);
            final double bB = bY0 - bM * bX0;

            final double tolerance = 1e-5;
            if (Math.abs(aM - bM) < tolerance) { 
                return null;
            }
    
            x = (bB - aB) / (aM - bM);
            y = aM * x + aB;
        }
    
        // Check if the intersection point is within the bounding boxes of both line segments
        final double boundingBoxTolerance = 1e-5;
        final boolean withinA = x >= Math.min(aX0, aX1) - boundingBoxTolerance &&
                                x <= Math.max(aX0, aX1) + boundingBoxTolerance &&
                                y >= Math.min(aY0, aY1) - boundingBoxTolerance &&
                                y <= Math.max(aY0, aY1) + boundingBoxTolerance;
        final boolean withinB = x >= Math.min(bX0, bX1) - boundingBoxTolerance &&
                                x <= Math.max(bX0, bX1) + boundingBoxTolerance &&
                                y >= Math.min(bY0, bY1) - boundingBoxTolerance &&
                                y <= Math.max(bY0, bY1) + boundingBoxTolerance;

        if (withinA && withinB) {
            result[0] = x;
            result[1] = y;
        } else {
            return null;
        }
    
        return result;
    }
}