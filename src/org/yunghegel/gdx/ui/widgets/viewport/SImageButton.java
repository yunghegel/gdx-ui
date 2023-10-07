package org.yunghegel.gdx.ui.widgets.viewport;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.yunghegel.gdx.ui.UI;

public class SImageButton extends ImageButton {

    public SImageButton() {
        super(UI.getSkin());
    }

    public SImageButton(String styleName) {
        super(UI.getSkin(), styleName);
    }
}
