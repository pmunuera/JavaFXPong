import java.net.URL;
import java.util.ResourceBundle;

import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class CtrlSelect implements Initializable{

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub
        
    }

    @FXML
    public void play(){
        CtrlGameCanvas.start=true;
        UtilsViews.setViewAnimating("ViewGame"); 
    }
    @FXML
    public void stats(){
        JSONObject obj = new JSONObject("{}");
        obj.put("type", "loadUsers");
        obj.put("id",CtrlSign.id);
        Main.socketClient.safeSend(obj.toString());
        UtilsViews.setViewAnimating("ViewUsuaris");
    }
    
}
