package org.nix.zhangpei;

/**
 * @author zhangpei
 * @version 1.0
 * @date 2018/12/30
 */
public enum Piece {

    /**
     * 代表棋子
     */
    PLAYER_RED("红棋"),PLAYER_BLACK("黑棋");

    private String name;

    Piece(String name) {
        this.name = name;
    }

    /**
     * 得到除自己外的另一个对象值，比如当前值为红棋，则执行该方法时得到黑棋
     * @return
     */
    public Piece getOther(){
        if (getName().equals(PLAYER_BLACK.getName())){
            return PLAYER_RED;
        }
        return PLAYER_BLACK;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Piece{" +
                "name='" + name + '\'' +
                '}';
    }
}
