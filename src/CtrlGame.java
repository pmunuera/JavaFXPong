import java.net.URL;
import java.util.ResourceBundle;

import org.json.JSONObject;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class CtrlGame implements Initializable {

    @FXML
    private AnchorPane anchor;

    @FXML
    private Canvas canvas;

    @FXML
    public static Button button;

    private static CtrlGameCanvas ctrlCanvas = new CtrlGameCanvas();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Initialize canvas responsive size
        UtilsViews.parentContainer.heightProperty().addListener((observable, oldValue, newvalue) -> {
            updateCanvasSize();
        });
        UtilsViews.parentContainer.widthProperty().addListener((observable, oldValue, newvalue) -> {
            updateCanvasSize();
        });
    }

    public void drawingStart () {
        ctrlCanvas.start(canvas);
    }

    public void drawingStop () {
        ctrlCanvas.stop();
    }

    public void updateCanvasSize () {

        final double width = UtilsViews.parentContainer.getWidth();
        final double height = UtilsViews.parentContainer.getHeight();

        // Set Canvas size
        canvas.setWidth(width);
        canvas.setHeight(height);
    }

    public void keyEvent (KeyEvent evt) {

        // Quan apretem una tecla
        if (evt.getEventType() == KeyEvent.KEY_PRESSED) {
            if(Main.socketClient!=null){
            if (evt.getCode() == KeyCode.UP || evt.getCode() == KeyCode.W) {
                JSONObject obj = new JSONObject("{}");
                obj.put("type", "playerDirection");
                obj.put("direction","up");
                obj.put("player",CtrlGameCanvas.playingAs);
                Main.socketClient.safeSend(obj.toString());
                //System.out.println("Send WebSocket: " + obj.toString());
            }
            if (evt.getCode() == KeyCode.DOWN || evt.getCode() == KeyCode.S) {
                JSONObject obj = new JSONObject("{}");
                obj.put("type", "playerDirection");
                obj.put("direction","down");
                obj.put("player",CtrlGameCanvas.playingAs);
                Main.socketClient.safeSend(obj.toString());
            }
                Main.socketClient.onMessage((response) -> {
                    //System.out.println("message");
                    // JavaFX necessita que els canvis es facin des de el thread principal
                    Platform.runLater(()->{ 
                        // Fer aquí els canvis a la interficie
                        JSONObject msgObj = new JSONObject(response);
                        if(msgObj.getString("status").equals("Direction")){
                            if(msgObj.getInt("player")==1){
                                ctrlCanvas.player1Direction = (String) msgObj.get("playerDirection");
                            }
                            if(msgObj.getInt("player")==2){
                                ctrlCanvas.player2Direction = (String) msgObj.get("playerDirection");
                            }
                            }
                    });
                });
        }
        }

        // Quan deixem anar la tecla
        if (evt.getEventType() == KeyEvent.KEY_RELEASED) {
            if(Main.socketClient!=null){
            if (evt.getCode() == KeyCode.UP || evt.getCode() == KeyCode.W) {
                if (ctrlCanvas.player1Direction.equals("up")) {
                    JSONObject obj = new JSONObject("{}");
                    obj.put("type", "playerDirection");
                    obj.put("direction","none");
                    Main.socketClient.safeSend(obj.toString());
                }
                if (ctrlCanvas.player2Direction.equals("up")) {
                    JSONObject obj = new JSONObject("{}");
                    obj.put("type", "playerDirection");
                    obj.put("direction","none");
                    Main.socketClient.safeSend(obj.toString());
                }
            }
            if (evt.getCode() == KeyCode.DOWN || evt.getCode() == KeyCode.S) {
                if (ctrlCanvas.player1Direction.equals("down")) {
                    JSONObject obj = new JSONObject("{}");
                    obj.put("type", "playerDirection");
                    obj.put("direction","none");
                    Main.socketClient.safeSend(obj.toString());
                }
                if (ctrlCanvas.player2Direction.equals("down")) {
                    JSONObject obj = new JSONObject("{}");
                    obj.put("type", "playerDirection");
                    obj.put("direction","none");
                    Main.socketClient.safeSend(obj.toString());
                }
            }
            Main.socketClient.onMessage((response) -> {
                //System.out.println("message");
                // JavaFX necessita que els canvis es facin des de el thread principal
                Platform.runLater(()->{ 
                    // Fer aquí els canvis a la interficie
                    JSONObject msgObj = new JSONObject(response);
                    //System.out.println(response);
                    if(msgObj.getString("status").equals("Direction")){
                        if(msgObj.getInt("player")==1){
                            ctrlCanvas.player1Direction = (String) msgObj.get("playerDirection");
                        }
                        if(msgObj.getInt("player")==2){
                            ctrlCanvas.player2Direction = (String) msgObj.get("playerDirection");
                        }
                        }
                });
            });
        }
        }
    }
    public static void buttonSetter(){
        button.setDisable(false);
        button.setVisible(true);
    }
    @FXML
    private void playAgain(){
        ctrlCanvas.start(ctrlCanvas.cnv);
        Main.socketClient = UtilsWS.getSharedInstance(Main.protocolWS + "://" + Main.host + ":" + Main.port);
    }
}