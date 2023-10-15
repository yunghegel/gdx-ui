package org.yunghegel.gdx.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;

public class Frame extends Table {

//    public static class FrameStyle {
//        public Drawable headerBackgroundLeft, headerBackgroundRight, bodyBackground;
//    }

    private VisTable titleTable;
    private VisTable contentTable;
    private VisTable client;

    public Frame(Actor title) {
        titleTable = new VisTable();
        titleTable.setSkin(VisUI.getSkin());
        titleTable.setBackground("rounded-button-dark");
        titleTable.setColor(0.7f, 0.7f, 0.7f, 1f);
        titleTable.pad(4);

        if(title != null) titleTable.add(title).padLeft(5).padRight(5);

        VisTable titleLeft = new VisTable();

//        titleLeft.setBackground(style.headerBackgroundLeft);

        VisTable titleRight = new VisTable();
//        titleRight.setBackground(style.headerBackgroundRight);

        VisTable headerTable = new VisTable();
        headerTable.setBackground("tint-border");
        headerTable.setColor(0.3f, 0.3f, 0.3f, 1f);

        setSkin(VisUI.getSkin());

        contentTable = new VisTable();
        headerTable.add(titleLeft).bottom();
        headerTable.add(titleTable).pad(4);
        headerTable.add(titleRight).growX().bottom();

        client = new VisTable();
//        client.setBackground(style.bodyBackground);
        client.add(contentTable).grow();

        add(headerTable).growX().row();
        add(client).grow().fill().row();

    }
    public VisTable getContentTable(){
        return contentTable;
    }
    public VisTable getTitleTable() {
        return titleTable;
    }
    public void showContent(boolean enabled) {
        client.clear();
        if(enabled) client.add(contentTable).grow();
    }
}