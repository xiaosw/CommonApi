package com.xiaosw.simple

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.xiaosw.api.annotation.MeasureTimeMillis
import com.xiaosw.api.extend.onClick
import com.xiaosw.api.hook.annotation.NotRegister
import com.xiaosw.api.logger.Logger

/**
 * @ClassName: [NotRegisterActivity]
 * @Description:
 *
 * Created by admin at 2021-01-07
 * @Email xiaosw0802@163.com
 */
@NotRegister
class NotRegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(TextView(this).also {
            it.text = "Start"
            it.onClick {
                startActivity(Intent(this, NotRegisterActivity::class.java))
            }
            it.setOnClickListener {

            }
        })
        Logger.e("onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.e("onDestroy")
    }

    @MeasureTimeMillis
    fun measureTimeInMillis() {

    }

}