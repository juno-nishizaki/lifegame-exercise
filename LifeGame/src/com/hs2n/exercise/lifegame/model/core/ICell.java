package com.hs2n.exercise.lifegame.model.core;

/**
 * ライフゲームの二次元平面のセルを表すインターフェースです。
 *
 * <p>
 * セルに配置する生命体には任意の型が指定できます。
 * ただし、配置する生命体のクラスが Immutable ではない
 * （インスタンスの生成後に内部状態が変わる可能性がある）場合は、
 * clone メソッドをオーバーライドして深いコピーを実装する必要があります。
 * </p>
 *
 * @author Juno NISHIZAKI
 *
 * @param <L> 生命体の型
 */
public interface ICell<L> {

    /**
     * 生命体が配置されているかを判定します。
     *
     * @return 生命体が配置されている場合 true、配置されていない場合 false
     */
    boolean hasLife();

    /**
     * 本セルに配置されている生命体を取得します。
     * hasLife メソッドで生命体が配置されていることを事前にチェックしてから
     * 本メソッドを呼び出すことを想定しています。
     *
     * @return 配置されている生命体
     * @throws IllegalStateException 生命体が配置されていない場合
     */
    L getLife() throws IllegalStateException;

    /**
     * 生命体を配置します。
     *
     * @param life 配置する生命体
     * @throws IllegalArgumentException 引数が null の場合
     */
    void putLife(L life) throws IllegalArgumentException;

    /**
     * 配置されている生命体を取り除きます。
     */
    void removeLife();

    /**
     * セルをコピーします。
     * 配置されている生命体の型が Clonable インターフェースを実装している場合は、
     * clone メソッドを呼び出してコピーします。
     *
     * @return コピーしたセル
     * @throws UnsupportedOperationException clone メソッドの呼び出しに失敗した場合
     */
    ICell<L> copyCell() throws UnsupportedOperationException;
}
