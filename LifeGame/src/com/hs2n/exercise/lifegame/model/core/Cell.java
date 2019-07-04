package com.hs2n.exercise.lifegame.model.core;

import java.lang.reflect.InvocationTargetException;

/**
 * ライフゲームの二次元平面のセルを表すクラスです。
 *
 * @author Juno NISHIZAKI
 *
 * @param <L> 生命体の型
 */
public class Cell<L> implements ICell<L> {

    /**
     * セルに配置された生命体です。
     */
    private L life;

    /**
     * 生命体が配置されていない新規セルを構築します。
     */
    public Cell() {
        this(null);
    }

    /**
     * 指定された生命体を配置した新規セルを構築します。
     *
     * @param life 生命体
     */
    private Cell(L life) {
        this.life = life;
    }

    /**
     * 生命体が配置されているかを判定します。
     */
    @Override
    public boolean hasLife() {
        return life != null;
    }

    /**
     * 本セルに配置されている生命体を取得します。
     */
    @Override
    public L getLife() throws IllegalStateException {
        if (!hasLife()) {
            throw new IllegalStateException();
        }
        return life;
    }

    /**
     * 生命体を配置します。
     */
    @Override
    public void putLife(L life) throws IllegalArgumentException {
        if (life == null) {
            throw new IllegalArgumentException();
        }
        this.life = life;
    }

    /**
     *生命体を取り除きます。
     */
    @Override
    public void removeLife() {
        life = null;
    }

    /**
     * セルをコピーします。
     * 配置されている生命体の型が Clonable インターフェースを実装している場合は、
     * clone メソッドを呼び出してコピーします。
     */
    @Override
    public ICell<L> copyCell() throws UnsupportedOperationException {
        return new Cell<>(cloneIfImpremented(life));
    }

    /**
     * 配置されている生命体の型が Clonable インターフェースを実装している場合は、
     * clone メソッドを呼び出してコピーします。
     * それ以外の場合は、指定された値をそのまま返します。
     *
     * @param <L> 生命体の型
     * @param sourceLife コピー元の生命体
     * @return コピーした生命体
     */
    private static <L> L cloneIfImpremented(L sourceLife) {
        // Clonable インターフェースを実装していない場合。指定された値をそのまま返す
        // （null の場合も含む）
        if (!(sourceLife instanceof Cloneable)) {
            return sourceLife;
        }

        try {
            // clone メソッドを呼び出す
            // public でオーバーライドした clone メソッドを
            // 適切に呼び出す方法が思いつかなかったので、
            // リフレクション API を使って、元々 Object クラスで定義されている
            // clone メソッドの公開性を無理やり書き換えて呼び出すようにしている
            // このブロックまで到達した場合、Clonable インターフェースが
            // 実装済みであることは明らかなので、素直に sourceLife.clone() と
            // 呼び出せてもいい気がするのだが…
            var cloneMethod = sourceLife.getClass().getDeclaredMethod("clone");
            cloneMethod.setAccessible(true);
            @SuppressWarnings("unchecked")
            L cloneLife = (L) cloneMethod.invoke(sourceLife);
            return cloneLife;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // 例外が発生した場合は、非検査例外でラップして再スローする
            // （本来は発生しえないはずだが、保証しきれないので苦し紛れの対処）
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * セルの状態を文字列表現に変換します。
     * この文字列表現は、主にデバッグの目的で使用します。
     */
    @Override
    public String toString() {
        if (!hasLife()) {
            return ".";
        } else {
            return life.toString();
        }
    }
}
