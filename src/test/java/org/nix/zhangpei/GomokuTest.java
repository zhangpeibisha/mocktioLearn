package org.nix.zhangpei;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GomokuTest {
    /**
     * 定义mock对象
     */
    private Gomoku gomoku;
    /**
     * 定义mock对象
     */
    @Mock
    private Point point;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        /**
         * 我们的目的是配置棋盘的大小
         * 先配置为 3*3的棋盘
         *
         * 只有自己现在编写的测试的类或方法需要实际的
         * 其他依赖都使用mock来代替，mock的所有行为都将先行定义才可以
         */
        gomoku = new Gomoku(10, 10);
    }

    @Test
    public void aplyOutIndex() {
        /**
         * 需求描述
         * 1. 如果落子超出了棋盘报错
         * 2. 如果落子没有超出则正常通过
         */
        // 目前正常落子
        when(point.getX()).thenReturn(9);
        when(point.getY()).thenReturn(3);
        boolean aply = gomoku.aply(point);
        assertFalse(aply);
    }

    /**
     * 检测超出范围的值是否能够得到期望的结果
     */
    @Test(expected = RuntimeException.class)
    public void aplyOutIndexError() {
        // 超出X范围落子
        when(point.getX()).thenReturn(11);
        when(point.getY()).thenReturn(3);
        // 执行aply，程序期望他当输入值超过棋盘时抛出异常
        gomoku.aply(point);
        // 执行aply，程序期望他当输入值超过棋盘时抛出异常
        when(point.getX()).thenReturn(3);
        when(point.getY()).thenReturn(11);
        gomoku.aply(point);
        // 执行aply，程序期望他当输入值超过棋盘时抛出异常
        when(point.getX()).thenReturn(-1);
        when(point.getY()).thenReturn(3);
        gomoku.aply(point);
        // 执行aply，程序期望他当输入值超过棋盘时抛出异常
        when(point.getX()).thenReturn(3);
        when(point.getY()).thenReturn(-1);
        gomoku.aply(point);
    }

    /**
     * 如果棋子落在已经有棋子的地方则抛出异常
     * 棋子只能放在，没有棋子的地方
     */
    @Test(expected = RuntimeException.class)
    public void piecePlaceholderTest() {
        // 获取满足边界条件的棋子
        when(point.getX()).thenReturn(3);
        when(point.getY()).thenReturn(3);
        // 根据需求，在满足边界条件的情况下，一个坐标必须能够放的下一颗棋子
        // 当下了一子后，下一次下棋应该是黑棋下子
        // 首先获取当前下棋的人
        Piece currPiece = gomoku.getCurrPiece();
        gomoku.aply(point);
        // 已经下完棋了后，当前下棋的人应该是另外一个颜色的棋子
        assertEquals(currPiece.getOther(), gomoku.getCurrPiece());
        StringBuilder builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        // 根据需求，当同一个位子放两次棋子必然报错
        gomoku.aply(point);
        fail("由于同一个位子下了两个子，所以不能够运行到这里");
    }

    /**
     * 根据文档规则，当落子后需要判断这盘棋是否赢了
     * 因此根据文档，需要在八个方向同一个棋子连城五个子及以上的直线则判断获胜
     */
    @Test
    public void winTest() {
        StringBuilder builder;
        builder = gomoku.printCheckerBoard();
        // 获取到初始化的棋盘
        StringBuilder init = builder;
        System.out.println(builder);
        // 首先计算能够根据预期获胜的棋子
        // 1.首先搜寻横排棋子是否有五个链接
        // 1.1 搜寻左边的棋子数量，当搜寻到不同类别的棋子时退出当前搜寻
        // 1.2 搜寻右边的棋子数量，当搜寻到不同类别的棋子时退出当前搜寻
        gomoku.aply(new Point(1, 0)); //红棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(0, 1)); //黑棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(0, 0)); //红棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(1, 1)); //黑棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(2, 0)); //红棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(2, 1)); //黑棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(4, 0));//红棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(3, 1)); //黑棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        boolean aply = gomoku.aply(new Point(3, 0)); //红棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        assertTrue("判断失败，期望是赢局，但是返回却是还未结束的标值", aply);
        System.err.println(" =================================================================================== ");
        // 初始化棋盘，下下一局
        gomoku.initCheckerboard();
        assertEquals("棋盘初始化有问题", gomoku.printCheckerBoard().toString(), init.toString());
        // 2.再次搜素竖排棋子是否有五个链接
        // 2.1 先搜寻上面
        // 2.2 再搜寻下面
        gomoku.aply(new Point(0, 5)); //红棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(1, 6)); //黑棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(0, 6)); //红棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(1, 7)); //黑棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(0, 7)); //红棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(1, 8)); //黑棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(0, 8)); //红棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(1, 9)); //黑棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        aply = gomoku.aply(new Point(0, 9));//红棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        assertTrue("判断失败，期望是赢局，但是返回却是还未结束的标值", aply);
        // 3.再次搜寻左上角到右下角是否有五个链接
        // 初始化棋盘，下下一局
        gomoku.initCheckerboard();
        assertEquals("棋盘初始化有问题", gomoku.printCheckerBoard().toString(), init.toString());

        gomoku.aply(new Point(0, 0)); //红棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(0, 1)); //黑棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(1, 1)); //红棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(1, 2)); //黑棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(2, 2)); //红棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(2, 3)); //黑棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(3, 3));//红棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(3, 4)); //黑棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        aply = gomoku.aply(new Point(4, 4)); //红棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        assertTrue("判断失败，期望是赢局，但是返回却是还未结束的标值", aply);
        System.err.println(" =================================================================================== ");

        // 4.再次搜寻右上角到左下角是否有五个链接
        gomoku.initCheckerboard();
        assertEquals("棋盘初始化有问题", gomoku.printCheckerBoard().toString(), init.toString());

        gomoku.aply(new Point(9, 0)); //红棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(0, 1)); //黑棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(8, 1)); //红棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(1, 2)); //黑棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(7, 2)); //红棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(2, 3)); //黑棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(6, 3));//红棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        gomoku.aply(new Point(3, 4)); //黑棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        aply = gomoku.aply(new Point(5, 4)); //红棋
        builder = gomoku.printCheckerBoard();
        System.out.println(builder);
        assertTrue("判断失败，期望是赢局，但是返回却是还未结束的标值", aply);
        System.err.println(" =================================================================================== ");
    }

}