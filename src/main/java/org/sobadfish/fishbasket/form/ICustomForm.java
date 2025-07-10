package org.sobadfish.fishbasket.form;

import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.window.FormWindow;

public interface ICustomForm<T extends FormResponse> {

    void callbackData(FormResponse response);

    int getFormId();

    /**
     * 创建表单
     * */
    void onCreateView();

    void setFormId(int formId);

    FormWindow asWindows();


    Player getPlayerInfo();


    boolean isCanRemove();


    /**
     * 判断此窗口是否执行过
     * */
    boolean isRunnable();

    void setRunnable(boolean runnable);
}
