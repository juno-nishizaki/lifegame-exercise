package com.hs2n.exercise.lifegame.model;

/**
 * 単一種族の生命体を表す列挙型です。
 *
 * @author Juno NISHIZAKI
 *
 */
public enum MonoLife {
    /**
     * 唯一の種族を表す値です。
     */
    ONE("o");

    /**
     * 列挙型の文字列表現です。
     *
     * <p>
     * この文字列表現は主にデバッグの目的で、toString メソッドの呼び出し時に使用します。
     * </p>
     */
    private String toStringValue;

    /**
     * 指定された文字列表現を保持する列挙型を構築します。
     *
     * @param toStringValue 列挙型の文字列表現
     */
    private MonoLife(String toStringValue) {
        this.toStringValue = toStringValue;
    }

    /**
     * 列挙型の文字列表現を返します。
     */
    @Override
    public String toString() {
        return toStringValue;
    }
}
