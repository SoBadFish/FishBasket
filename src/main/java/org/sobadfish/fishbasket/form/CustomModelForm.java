package org.sobadfish.fishbasket.form;

import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseModal;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowModal;

public abstract class CustomModelForm implements ICustomForm<FormResponseModal> {

    public int formId;

    public Player playerInfo;

    private String title = "";

    private String content = "";
    private String button1 = "";
    private String button2 = "";

    public boolean isRunnable = false;

    public CustomModelForm(String title, String content, String trueButtonText, String falseButtonText, Player playerInfo) {
        this.title = title;
        this.content = content;
        this.button1 = trueButtonText;
        this.button2 = falseButtonText;
        this.playerInfo = playerInfo;
    }

    public abstract void callback(FormResponseModal response);

    @Override
    public Player getPlayerInfo() {
        return playerInfo;
    }

    @Override
    public void setFormId(int formId) {
        this.formId = formId;
    }

    @Override
    public int getFormId() {
        return formId;
    }

    @Override
    public FormWindow asWindows() {
        return new FormWindowModal(title, content, button1, button2);

    }

    @Override
    public boolean isRunnable() {
        return isRunnable;
    }


    @Override
    public void setRunnable(boolean runnable) {
        isRunnable = runnable;
    }



    @Override
    public void callbackData(FormResponse response) {
        if(response instanceof FormResponseModal) {
            callback((FormResponseModal) response);
        }
    }

    public void setContent(String content) {
        this.content = content;
    }
}
