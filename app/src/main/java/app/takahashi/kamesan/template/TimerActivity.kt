package app.takahashi.kamesan.template

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.util.*
import kotlin.concurrent.schedule

class TimerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //kotlinでできる簡単なタイマーイベントの実装
        //第一引数は何ミリセカンド後に実行するか
        //第二引数は何ミリセカンド間隔で実行するかを指定する
        //下記の例は、この記述の実行後すぐ（0ミリセカンド後）に1000ミリセカンド間隔でイベント発生
        //第三引数は処理を記述したラムダ式
        Timer().schedule(0, 1000) {
            Log.v("nullpo", "callback0")
            //1回で終了
            this.cancel()
        }

/*        //コールバックされる処理を変数として保存し、それを処理させるパターン
        //こうすると引数内に記述する処理を最小限にすることができる
        var timerCallback1: TimerTask.() -> Unit = {
            Log.v("nullpo", "callback1")
            this.cancel()
        }
        Timer().schedule(0, 1000, timerCallback1)

        //timerCallback1をラムダ式でなくしっかりとした式が表すと以下のような記述になる
        var timerCallback2: TimerTask.() -> Unit = fun TimerTask.() {
            Log.v("nullpo", "callback2")
            this.cancel()
        }
        Timer().schedule(0, 1000, timerCallback2)*/

        //処理内容を別クラスとして宣言するパターン
        //TimerCallback3というクラスを作りTimerTaskクラスを継承する
        Timer().schedule(TimerCallback3(), 0, 1000)
    }

    class TimerCallback3 : TimerTask() {
        override fun run() {
            Log.v("nullpo", "callback3")
//            this.cancel()
        }
    }
}