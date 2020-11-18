package com.hyphenate.easeui.modules.chat.interfaces;

public interface IChatInputMenu {

    /**
     * 设置自定义菜单
     * @param menu
     */
    void setCustomPrimaryMenu(IChatPrimaryMenu menu);

    /**
     * 设置自定义表情
     * @param menu
     */
    void setCustomEmojiconMenu(IChatEmojiconMenu menu);

    /**
     * 设置自定义扩展菜单
     * @param menu
     */
    void setCustomExtendMenu(IChatExtendMenu menu);

    /**
     * 是否展示表情菜单
     * @param show
     */
    void showEmojiconMenu(boolean show);

    /**
     * 是否展示扩展菜单
     * @param show
     */
    void showExtendMenu(boolean show);

    /**
     * 设置菜单监听事件
     * @param listener
     */
    void setChatInputMenuListener(ChatInputMenuListener listener);

    /**
     * 获取菜单
     * @return
     */
    IChatPrimaryMenu getPrimaryMenu();

    /**
     * 获取表情菜单
     * @return
     */
    IChatEmojiconMenu getEmojiconMenu();

    /**
     * 获取扩展菜单
     * @return
     */
    IChatExtendMenu getChatExtendMenu();

    /**
     * 点击返回
     * @return
     */
    boolean onBackPressed();
}
