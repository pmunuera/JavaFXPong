import java.net.URL;
import java.util.ResourceBundle;

import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

public class CtrlStats implements Initializable{
    @FXML
    private Label guanyades,perdudes,temps,partidaLlarga,tocs,partidaTocs,nom;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub
        
    }
    public void reestart(){
        guanyades.setText("0");
        perdudes.setText("0");
        temps.setText("0 minuts 0 segons");
        partidaLlarga.setText("");
        tocs.setText("0");
        partidaTocs.setText("");
    }
    public void loadStats(String response){
        JSONObject objResponse = new JSONObject(response);
         if (objResponse.getString("status").equals("OK")) {
            guanyades.setText(String.valueOf(objResponse.getInt("guanyades")));
            perdudes.setText(String.valueOf(objResponse.getInt("perdudes")));
            tocs.setText(String.valueOf(objResponse.getInt("maxTocs")));
            temps.setText(objResponse.getString("temps"));
            partidaLlarga.setText(objResponse.getString("partidaLlarga"));
            partidaTocs.setText(objResponse.getString("partidaTocs"));
            nom.setText(objResponse.getString("nom"));
            UtilsViews.setViewAnimating("ViewStats");
         }
         else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("ERROR");
            alert.setContentText("Aquest usuari no té partides");
            alert.showAndWait();
         }
        
    }
    @FXML
    public void goBack(){
        JSONObject obj = new JSONObject("{}");
        obj.put("type", "loadUsers");
        obj.put("id",CtrlSign.id);
        Main.socketClient.safeSend(obj.toString());
        UtilsViews.setViewAnimating("ViewUsuaris");
    }
}
