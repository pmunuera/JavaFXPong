import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;

import javax.swing.Action;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class CtrlGameCanvas {
    public static Canvas cnv;
    private GraphicsContext gc;
    private AnimationTimer animationTimer;

    private double borderSize = 5;

    public static String gameStatus = "waiting";
    public static int playingAs = 0;
    private int pointsP1 = 0;
    private int pointsP2 = 0;
    private double player1X = 50;
    public static double player1Y = 200;
    private double player2X = 700;
    public static double player2Y = 200;
    private int numPlayers;
    private final double playerWidth = 5;
    private final double playerHeight = 100;
    private final double playerHalf = playerHeight / 2;
    private double playerSpeed = 250;
    private final double playerSpeedIncrement = 15;
    public String player1Direction = "none";
    public String player2Direction = "none";
    private double ballX = Double.POSITIVE_INFINITY;
    private double ballY = Double.POSITIVE_INFINITY;
    private final double ballSize = 15;
    private final double ballHalf = ballSize / 2;
    private Calendar iniciCalendar;
    private Calendar finalCalendar;
    private long iniciPartida;
    private long finalPartida;
    private long tempsTotal;
    private boolean partidaEnviada=false;
    private double ballSpeed = 200;
    private final double ballSpeedIncrement = 25;
    private String ballDirection = "upRight";
    public static boolean logged=false;
    private String colorP1 = "";
    private String colorP2 = "";
    private String usernameP1 = "";
    private String usernameP2 = "";

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
        if(Main.socketClient!=null){
            Main.socketClient.onMessage((response) -> {
                // JavaFX necessita que els canvis es facin des de el thread principal
                Platform.runLater(()->{ 
                    if(response!=null){
                        JSONObject msgObj = new JSONObject(response);
                        String type = msgObj.getString("type");
                        //System.out.println(msgObj);
                        if(logged==false){
                            if (type.equals("clients")) {
                                CtrlSign.id=msgObj.getString("id");
                                JSONArray JSONlist = msgObj.getJSONArray("list");
                                ArrayList<String> list = new ArrayList<>();
                                if(JSONlist.length()==1){
                                    Main.playerId=(String) JSONlist.get(0);
                                    System.out.println(Main.playerId);
                                }
                                else if (JSONlist.length()>1){
                                    Main.playerId=(String) JSONlist.get(JSONlist.length()-1);
                                    System.out.println(Main.playerId);
                                }
                                UtilsViews.setViewAnimating("ViewLogin");
                                System.out.println(Main.playerId);
                            }
                        }
                        if(type.equals("confirmationRegister")){
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setHeaderText(null);
                            alert.setTitle("OK");
                            alert.setContentText("Usuari registrat correctament");
                            alert.showAndWait();
                            UtilsViews.setViewAnimating("ViewLogin");
                        }
                        if(type.equals("confirmationLogin")){
                            if(msgObj.getString("status").equals("OK")){
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.setHeaderText(null);
                                alert.setTitle("OK");
                                alert.setContentText("Login correcte");
                                Optional<ButtonType> action = alert.showAndWait();
                                if(action.get()==ButtonType.OK){
                                    CtrlLogin.idUsuari=msgObj.getInt("idUsuari");
                                    System.out.println(CtrlLogin.idUsuari);
                                    UtilsViews.setViewAnimating("ViewSelect");
                                    logged=true;
                                }
                            }
                            else{
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setHeaderText(null);
                                alert.setTitle("ERROR");
                                alert.setContentText("Login incorrecte");
                                alert.showAndWait(); 
                            }
                        }
                        if(type.equals("userList")){
                            CtrlUsuaris cu = (CtrlUsuaris) UtilsViews.getController("ViewUsuaris");
                            cu.loadList(response);
                        }
                        if(type.equals("stats")){
                            CtrlStats cs = (CtrlStats) UtilsViews.getController("ViewStats");
                            cs.loadStats(response);
                        }
                        if(type.equals("playingAs")){
                            System.out.println(msgObj);
                            playingAs=1;
                        }
                        if(type.equals("color")){
                            if(msgObj.getString("status").equals("OK")){
                                System.out.println(msgObj);
                                colorP1=msgObj.getString("color1");
                                usernameP1=msgObj.getString("username1");
                                colorP2=msgObj.getString("color2");
                                usernameP2=msgObj.getString("username2");
                                if(playingAs==0){
                                    playingAs=2;
                                }
                                start=true;
                            }
                            else{
                                colorP1="";
                                usernameP1="";
                                colorP2="";
                                usernameP2="";
                            }
                        }
                            
                            
                    }
                    
                });
            });
    }
        if(gameStatus.equalsIgnoreCase("waiting")){
            CtrlGame ctrlGame = (CtrlGame) UtilsViews.getController("ViewGame");
            ctrlGame.invisibleButton();
        }
        //System.out.println(playingAs);
        if(start==true){
            final double boardWidth = cnv.getWidth();
            final double boardHeight = cnv.getHeight();
            // Move ball
            JSONObject obj = new JSONObject("{}");
            obj.put("type", "ballDirection");
            obj.put("player1Y",player1Y);
            obj.put("player2Y", player2Y);
            Main.socketClient.safeSend(obj.toString());
            // Move player
            JSONObject obj1 = new JSONObject("{}");
            obj1.put("type", "movePlayer");
            obj1.put("player1Direction",player1Direction);
            obj1.put("player2Direction",player2Direction);
            obj1.put("player1Y",player1Y);
            obj1.put("player2Y", player2Y);
            Main.socketClient.safeSend(obj1.toString());
            Main.socketClient.onMessage((response) -> {
                // JavaFX necessita que els canvis es facin des de el thread principal
                Platform.runLater(()->{ 
                    // Fer aquí els canvis a la interficie
                    JSONObject msgObj = new JSONObject(response);
                    if(msgObj.getString("status").equals("MovePlayer")){
                        player1Y=msgObj.getDouble("player1Y");
                        player2Y=msgObj.getDouble("player2Y");
                    }
                    if(msgObj.getString("status").equals("Disconnect")){
                        Main.socketClient.close();
                        this.stop();
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText(null);
                        alert.setTitle("Rival disconnected");
                        alert.setContentText("Your rival desconnected, we disconnected you too");
                        alert.showAndWait();
                        UtilsViews.setViewAnimating("ViewSign");
                    }
                    if(msgObj.getString("status").equals("Ball")){
                        ballDirection=msgObj.getString("ballDirection");
                        ballX=msgObj.getDouble("ballX");
                        ballY=msgObj.getDouble("ballY");
                        pointsP1=msgObj.getInt("pointsP1");
                        pointsP2=msgObj.getInt("pointsP2");
                        gameStatus=msgObj.getString("gameStatus");
                        gameStatus=msgObj.getString("gameStatus");
                    }
                });
            });

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
            // Set player X position
            player1X = 50;
            player2X = 700;
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
        if(!colorP1.equals("")){
            //System.out.println(colorP1);
            gc.setStroke(Color.valueOf(colorP1));
            gc.setLineWidth(playerWidth);
            gc.strokeRect(player1X, player1Y - playerHalf, playerWidth, playerHeight);
        }
        if(!colorP2.equals("")){
            gc.setStroke(Color.valueOf(colorP2));
            gc.setLineWidth(playerWidth);
            gc.strokeRect(player2X, player2Y - playerHalf, playerWidth, playerHeight);
        }

        // Draw ball
        gc.setFill(Color.BLACK);
        gc.fillArc(ballX - ballHalf, ballY - ballHalf, ballSize, ballSize, 0.0, 360, ArcType.ROUND);

        // Draw text with points
        final double boardCenterX = cnv.getWidth() / 2;
        final double boardCenterY = cnv.getHeight() / 2;
        if(!usernameP1.equals("")){
            gc.setFill(Color.valueOf(colorP1));
            gc.setFont(new Font("Arial", 20));
            String pointsText1 = usernameP1 +": "+ pointsP1;
            drawText(gc, pointsText1, boardCenterX - 50, 20, "center");
        }
        gc.setFill(Color.BLACK);

        String vs = "VS";
        drawText(gc, vs, boardCenterX, 20, "center");
        if(!usernameP2.equals("")){
            gc.setFill(Color.valueOf(colorP2));
            String pointsText2 = usernameP2 +": "+ pointsP2;
            drawText(gc, pointsText2, boardCenterX + 50, 20, "center");
        }
        
        //Draw waiting text
        if(gameStatus.equals("waiting")){
            gc.setFont(new Font("Arial", 40));
            drawText(gc, "Waiting for player two", boardCenterX, boardCenterY - 20, "center");
        }
        if(gameStatus.equals("syncing")){
            gc.setFont(new Font("Arial", 20));
            drawText(gc, "Syncronizing with new player 3,2,1...", boardCenterX, boardCenterY + 20, "center");
            iniciCalendar=Calendar.getInstance();
            iniciPartida=iniciCalendar.getTimeInMillis();
            partidaEnviada=false;
        }
        // Draw game over text
        if ((pointsP1==5||pointsP2==5)&&gameStatus.equalsIgnoreCase("gameOver")) {
            finalCalendar=Calendar.getInstance();
            finalPartida=finalCalendar.getTimeInMillis();
            tempsTotal=finalPartida-iniciPartida;
            
            gc.setFont(new Font("Arial", 40));
            drawText(gc, "GAME OVER", boardCenterX, boardCenterY - 20, "center");

            gc.setFont(new Font("Arial", 20));
            if(pointsP1==5&&playingAs==1||pointsP2==5&&playingAs==2){
                drawText(gc, "You win!", boardCenterX, boardCenterY + 20, "center");
                if(partidaEnviada==false){
                    JSONObject obj = new JSONObject("{}");
                    obj.put("type", "saveMatch");
                    obj.put("time",tempsTotal);
                    obj.put("id",CtrlLogin.idUsuari);
                    Main.socketClient.safeSend(obj.toString());
                    partidaEnviada=true;
                }
            }
            else{
                drawText(gc, "You lose!", boardCenterX, boardCenterY + 20, "center");
            }
            CtrlGame ctrlGame = (CtrlGame) UtilsViews.getController("ViewGame");
            ctrlGame.buttonSetter();
            
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