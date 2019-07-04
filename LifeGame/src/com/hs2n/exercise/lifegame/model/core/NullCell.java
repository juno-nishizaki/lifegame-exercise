package com.hs2n.exercise.lifegame.model.core;

/**
 * 無効セルを表すクラスです。
 * いわゆる Null Object に相当します。
 *
 * @author Juno NISHIZAKI
 *
 * @param <L> 生命体の型
 */
public final class NullCell<L> extends Cell<L> {

    /**
     * 生命体を配置しようとしても何も起こらないようにオーバーライドしています。
     */
    @Override
    public void putLife(L life) {}

    /**
     * コピー時は自分自身を返すことで、無駄なインスタンスを生成しないようにしています。
     */
    @Override
    public ICell<L> copyCell() {
        return this;
    }

    /**
     * 無効セルを文字列表現に変換します。
     * この文字列表現は、主にデバッグの目的で使用します。
     */
    @Override
    public String toString() {
        return " ";
    }

}
