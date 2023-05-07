import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

public class ControllerItem {
    
    @FXML
    private Label title;

    @FXML
    private void handleMenuAction() {
        
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }


}
