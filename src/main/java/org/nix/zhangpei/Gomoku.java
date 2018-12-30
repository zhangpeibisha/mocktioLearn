package org.nix.zhangpei;

/**
 * 五子棋核心部件
 *
 * @author zhangpei
 * @version 1.0
 * @date 2018/12/30
 */
public class Gomoku {

    private Integer maxX;

    private Integer minX;

    private Integer maxY;

    private Integer minY;

    private Piece[][] checkerboard;

    private Piece currPiece;
    /**
     * 五子棋赢的常数
     */
    private static final Integer WIN_NUMBER = 5;

    /**
     * 默认0开始为坐标
     * 默认红旗优先
     *
     * @param maxX
     * @param maxY
     */
    public Gomoku(Integer maxX, Integer maxY) {
        this(maxX, 0, maxY, 0, Piece.PLAYER_RED);
    }

    /**
     * 默认0开始为坐标
     * 自定义什么棋子优先
     *
     * @param maxX
     * @param maxY
     */
    public Gomoku(Integer maxX, Integer maxY, Piece currPiece) {
        this(maxX, 0, maxY, 0, currPiece);
    }

    /**
     * 自定义开始结束坐标
     * 默认红旗优先
     *
     * @param maxX
     * @param minX
     * @param maxY
     * @param minY
     */
    public Gomoku(Integer maxX, Integer minX, Integer maxY, Integer minY) {
        this(maxX, minX, maxY, minY, Piece.PLAYER_RED);
    }

    /**
     * 自定义开始结束坐标
     * 自定义什么棋子优先
     *
     * @param maxX
     * @param minX
     * @param maxY
     * @param minY
     */
    public Gomoku(Integer maxX, Integer minX, Integer maxY, Integer minY, Piece currPiece) {
        this.maxX = maxX;
        this.minX = minX;
        this.maxY = maxY;
        this.minY = minY;
        checkerboard = new Piece[maxY][maxX];
        this.currPiece = currPiece;
    }

    /**
     * 玩的入口
     *
     * @param point 下子位子
     * @return 是否下子成功
     */
    public boolean aply(Point point) {
        Integer x = point.getX();
        if (x >= maxX) {
            throw new RuntimeException(String.format("落子已经超出棋盘X轴范围:%s:%s", x, maxX - 1));
        }
        if (x < minX) {
            throw new RuntimeException(String.format("落子已经超出棋盘X轴范围:%s:%s", x, minX));
        }
        Integer y = point.getY();
        if (y >= maxY) {
            throw new RuntimeException(String.format("落子已经超出棋盘Y轴范围:%s:%s", x, maxY - 1));
        }
        if (y < minY) {
            throw new RuntimeException(String.format("落子已经超出棋盘y轴范围:%s:%s", x, minY));
        }
        if (checkerboard[y][x] != null) {
            throw new RuntimeException(String.format("当前位置%s:%s已经落子", x, y));
        }
        // 下棋
        checkerboard[y][x] = currPiece;
        // 判断是否胜利
        if (win(point)) {
            System.err.println("======================="+currPiece.getName()+"胜利了=======================");
            return true;
        }
        // 如果没有赢，返回false，换下一位棋手
        currPiece = currPiece.getOther();
        return false;
    }

    public boolean win(Point point) {
        System.out.println(point);
        int weatAndEast = weatAndEast(point);
        System.out.println(currPiece.getName() + " 横向搜寻 weatAndEast:" + weatAndEast);
        if (weatAndEast >= WIN_NUMBER) {
            return true;
        }
        int northAndSouth = northAndSouth(point);
        System.out.println(currPiece.getName() + " 垂直搜索 northAndSouth:" + northAndSouth);
        if (northAndSouth >= WIN_NUMBER) {
            return true;
        }
        int northwestAndSoutheast = northwestAndSoutheast(point);
        System.out.println(currPiece.getName() + " 左上角到右下角 northAndSouth:" + northwestAndSoutheast);
        if (northwestAndSoutheast >= WIN_NUMBER) {
            return true;
        }
        int southwestAndNortheast = southwestAndNortheast(point);
        System.out.println(currPiece.getName() + " 左下角到右上角 southwestAndNortheast:" + southwestAndNortheast);
        return southwestAndNortheast >= WIN_NUMBER;
    }

    /**
     * 横向搜寻是否满足赢
     * 1.首先搜寻横排棋子是否有五个链接
     * 1.1 搜寻左边的棋子数量，当搜寻到不同类别的棋子时退出当前搜寻
     * 1.2 搜寻右边的棋子数量，当搜寻到不同类别的棋子时退出当前搜寻
     *
     * @param point
     * @return
     */
    private int weatAndEast(Point point) {
        return (pieceConnectionNumber(point, 0, Direction.DIRECTION_LEFT)
                + pieceConnectionNumber(point, 0, Direction.DIRECTION_RIGHT)) - 1;
    }

    /**
     * 垂直搜索
     * 2.再次搜素竖排棋子是否有五个链接
     * 2.1 先搜寻上面
     * 2.2 再搜寻下面
     *
     * @param point
     * @return
     */
    private int northAndSouth(Point point) {
        return (pieceConnectionNumber(point, 0, Direction.DIRECTION_UNDER)
                + pieceConnectionNumber(point, 0, Direction.DIRECTION_ON)) - 1;
    }

    /**
     * 左上角到右下角
     *
     * @return
     */
    private int northwestAndSoutheast(Point point) {
        return (pieceConnectionNumber(point, 0, Direction.DIRECTION_TOP_LEFT)
                + pieceConnectionNumber(point, 0, Direction.DIRECTION_BOTTOM_RIGHT)) - 1;
    }

    /**
     * 左下角到右上角
     *
     * @return
     */
    private int southwestAndNortheast(Point point) {
        return (pieceConnectionNumber(point, 0, Direction.DIRECTION_BOTTOM_LEFT)
                + pieceConnectionNumber(point, 0, Direction.DIRECTION_TOP_RIGHT)) - 1;
    }

    /**
     * 查询插入一个点时，他的上+下、左+右、左上+右下、左下+右上的连续数量是多少
     *
     * @param point     插入的点
     * @param number    连珠有多少个
     * @param direction 指定查询方向
     * @return 指定方向上的连珠
     */
    private int pieceConnectionNumber(Point point, int number, Direction direction) {
        int x = point.getX();
        int y = point.getY();
        if (number >= WIN_NUMBER) {
            return WIN_NUMBER;
        }
        if (x >= maxX || y >= maxY || x < minX || y < minY) {
            return number;
        }
        Piece piece = checkerboard[y][x];
        // 如果是空值获取不是同类型棋子，返回值
        if (piece == null || !piece.getName().equals(currPiece.getName())) {
            return number;
        }
        switch (direction) {
            // 上
            case DIRECTION_ON:
                return pieceConnectionNumber(new Point(x, y - 1), ++number, direction);
            // 下
            case DIRECTION_UNDER:
                return pieceConnectionNumber(new Point(x, y + 1), ++number, direction);

            // 左
            case DIRECTION_LEFT:
                return pieceConnectionNumber(new Point(x - 1, y), ++number, direction);
            // 右
            case DIRECTION_RIGHT:
                return pieceConnectionNumber(new Point(x + 1, y), ++number, direction);

            // 左上
            case DIRECTION_TOP_LEFT:
                return pieceConnectionNumber(new Point(x - 1, y - 1), ++number, direction);
            // 右下
            case DIRECTION_BOTTOM_RIGHT:
                return pieceConnectionNumber(new Point(x + 1, y + 1), ++number, direction);

            // 左下
            case DIRECTION_BOTTOM_LEFT:
                return pieceConnectionNumber(new Point(x - 1, y + 1), ++number, direction);
            // 右上
            case DIRECTION_TOP_RIGHT:
                return pieceConnectionNumber(new Point(x + 1, y - 1), ++number, direction);
            // 错误
            default:
                throw new RuntimeException("指示方位错误");
        }

    }

    /**
     * 初始化棋盘
     */
    public void initCheckerboard() {
        checkerboard = new Piece[maxY][maxX];
    }

    /**
     * 打印棋盘
     *
     * @return 棋盘字符串
     */
    public StringBuilder printCheckerBoard() {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        int j = 0;
        for (Piece[] pieces : checkerboard) {
            for (Piece piece : pieces) {
                builder.append("[");
                if (piece == null) {
                    builder.append("空空");
                } else {
                    builder.append(piece.getName());
                }
                builder.append(String.format("{%s:%s}", j, i));
                builder.append("]");
                j++;
            }
            builder.append("\n");
            i++;
            j = 0;
        }
        return builder;
    }

    public Integer getMaxX() {
        return maxX;
    }

    public void setMaxX(Integer maxX) {
        this.maxX = maxX;
    }

    public Integer getMinX() {
        return minX;
    }

    public void setMinX(Integer minX) {
        this.minX = minX;
    }

    public Integer getMaxY() {
        return maxY;
    }

    public void setMaxY(Integer maxY) {
        this.maxY = maxY;
    }

    public Integer getMinY() {
        return minY;
    }

    public void setMinY(Integer minY) {
        this.minY = minY;
    }

    public Piece[][] getCheckerboard() {
        return checkerboard;
    }

    public void setCheckerboard(Piece[][] checkerboard) {
        this.checkerboard = checkerboard;
    }

    public Piece getCurrPiece() {
        return currPiece;
    }

    public void setCurrPiece(Piece currPiece) {
        this.currPiece = currPiece;
    }
}
