package com.skynet.javafx.views;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.skynet.javafx.jfxsupport.AbstractFxmlView;
import com.skynet.javafx.jfxsupport.FXMLView;

@Component
@Scope("prototype")  // This ensures a new instance is created each time
@FXMLView("/fxml/facture.fxml")
public class FactureView extends AbstractFxmlView {
    // No additional code needed
}


