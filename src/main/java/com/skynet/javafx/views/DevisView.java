package com.skynet.javafx.views;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.skynet.javafx.jfxsupport.AbstractFxmlView;
import com.skynet.javafx.jfxsupport.FXMLView;

@Component
@Scope("prototype")
@FXMLView("/fxml/devis.fxml")  // Update path to match actual file location
public class DevisView extends AbstractFxmlView {
}
