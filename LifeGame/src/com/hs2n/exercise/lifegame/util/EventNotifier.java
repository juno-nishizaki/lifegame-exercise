package com.hs2n.exercise.lifegame.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.BiConsumer;

import javax.swing.SwingUtilities;

/**
 * イベント通知を制御するクラスです。
 *
 * @author Juno NISHIZAKI
 *
 * @param <S> イベント通知元オブジェクトの型
 * @param <P> イベント通知先に渡すパラメーターの型
 */
public class EventNotifier<S, P> {

    /**
     * イベントハンドラーのリストを操作するときの排他制御に使用します。
     */
    private final Object lockObj = new Object();

    /**
     * イベント通知時に使用するスレッドプールです。
     */
    private ExecutorService eventDispatcher;

    /**
     * イベントハンドラーのリストです。
     */
    private List<BiConsumer<S, P>> eventHandlerList = new ArrayList<>();

    /**
     * イベント通知元オブジェクトです。
     */
    private S sender;

    /**
     * AWT/Swing のイベントディスパッチスレッドに処理を委譲するかを判定するフラグです。
     */
    private boolean isDelegateEDT;

    /**
     * イベント通知オブジェクトを構築します。
     *
     * @param sender イベント通知元オブジェクト
     * @param isDelegateEDT AWT/Swing のイベントディスパッチスレッドに処理を委譲するか
     */
    public EventNotifier(S sender, boolean isDelegateEDT) {
        this.sender = sender;
        this.isDelegateEDT = isDelegateEDT;

        initializeEventDispatcher();
    }

    /**
     * スレッドプールをキュー上限なしの単一スレッドで初期化します。
     * スレッドプールはデーモンスレッドで動作します。
     */
    private void initializeEventDispatcher() {
        // デーモンスレッドを扱うために独自の ThreadFactory を用意する
        var threadFactory = new ThreadFactory() {

            @Override
            public Thread newThread(Runnable r) {
                var thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
        };

        // キュー上限なしの単一スレッドで動作するスレッドプールを生成する
        eventDispatcher = Executors.newSingleThreadExecutor(threadFactory);
    }

    /**
     * イベントハンドラーを追加します。
     *
     * @param eventHandler イベントハンドラー
     */
    public void addEventHandler(BiConsumer<S, P> eventHandler) {
        synchronized (lockObj) {
            eventHandlerList.add(eventHandler);
        }
    }

    /**
     * 保持しているイベントハンドラーにイベントを通知します。
     *
     * @param eventParams イベント通知先に渡すパラメーター
     */
    public void fire(P eventParams) {
        synchronized (lockObj) {
            eventHandlerList.stream().forEach(eventHandler -> {
                var command = createCommand(eventHandler, eventParams);
                eventDispatcher.execute(command);
            });
        }
    }

    /**
     * スレッドプールに渡すコマンドを生成します
     *
     * @param eventHandler イベントハンドラー
     * @param eventParams イベント通知先に渡すパラメーター
     * @return スレッドプールに渡すコマンド
     */
    private Runnable createCommand(BiConsumer<S, P> eventHandler, P eventParams) {
        if (isDelegateEDT) {
            // AWT/Swing のイベントディスパッチスレッドに処理を委譲する場合、
            // イベントハンドラーの呼び出しを SwingUtilities.invokeLater でラップする
            return () -> {
                SwingUtilities.invokeLater(() -> eventHandler.accept(sender, eventParams));
            };
        } else {
            // 処理を委譲しない場合は、イベントハンドラーをそのまま呼び出す
            return () -> eventHandler.accept(sender, eventParams);
        }

        // 上記の処理をラムダ式を使わずに記述するとしたら、以下のようになる
        //
        //        if (isDelegateEDT) {
        //            return new Runnable() {
        //                @Override
        //                public void run() {
        //                    SwingUtilities.invokeLater(new Runnable() {
        //                        @Override
        //                        public void run() {
        //                            eventHandler.accept(sender, eventParams);
        //                        }
        //                    });
        //                }
        //            };
        //        } else {
        //            return new Runnable() {
        //                @Override
        //                public void run() {
        //                    eventHandler.accept(sender, eventParams);
        //                }
        //            };
        //        }
        //
        // 必要（だが冗長）な記述が増えてしまい可読性も低くなるため
        // 関数型インターフェース（抽象メソッドを 1 つだけもつインターフェース）は
        // 積極的にラムダ式を使って記述した方がよいと思う
    }
}
