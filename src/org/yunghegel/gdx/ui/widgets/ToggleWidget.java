package org.yunghegel.gdx.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.kotcrab.vis.ui.widget.VisTable;

public class ToggleWidget extends Container<STable> {
    public STable firstTable = new STable();
    public STable secondTable = new STable();
    public boolean firstTableEnabled;

    public ToggleWidget(){
        super();
        setActor(firstTable);

        firstTableEnabled = true;
    }

    public void toggle(){
        if(firstTableEnabled){
            setActor(secondTable);
            firstTableEnabled = false;
        }else{
            setActor(firstTable);
            firstTableEnabled = true;
        }
    }

    public void setFirstTable(STable firstTable){
        this.firstTable = firstTable;
    }

    public void setSecondTable(STable secondTable){
        this.secondTable = secondTable;
    }

    public void showFirstTable(){
        setActor(firstTable);
        firstTableEnabled = true;
    }

    public void showSecondTable(){
        setActor(secondTable);
        firstTableEnabled = false;
    }
}
