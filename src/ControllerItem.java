import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

public class ControllerItem {
    
    @FXML
    private Label id,title;
    private int idUser;
    private String nom;

    @FXML
    private void handleMenuAction() {
        /*CtrlStats cs = (CtrlStats) UtilsViews.getController("ViewStats");
        cs.reestart();*/
        JSONObject obj = new JSONObject("{}");
        obj.put("type", "getStats");
        obj.put("playerId",idUser);
        obj.put("id",CtrlSign.id);
        obj.put("nom",this.nom);
        Main.socketClient.safeSend(obj.toString());
    }

    public void setTitle(String title) {
        this.title.setText(title);
        this.nom=title;
    }
    public void setId(int id){
        this.id.setText(String.valueOf(id));
        idUser=id;
    }
    public String getTitle(){
        return this.nom;
    }

}
