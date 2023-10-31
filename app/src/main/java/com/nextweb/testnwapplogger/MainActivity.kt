package com.nextweb.testnwapplogger

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.installreferrer.api.InstallReferrerClient
import com.android.volley.BuildConfig
import com.nextweb.nwapplogger.NWAppLogger
import com.nextweb.testnwapplogger.ui.theme.TestNWAppLoggerTheme

class MainActivity : ComponentActivity(), NWAppLogger.OnGoogleReferrerDataCallback  {

    private lateinit var referrerClient: InstallReferrerClient;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestNWAppLoggerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }

        /* NWAppLogger 설정 start*/
        val nwappLogger = NWAppLogger.instance
        // 최초 앱 실행시 한번 호출
        nwappLogger.init(this, true, "url");

        // 파리미터 정보 set
        nwappLogger.setParameters(
            if (BuildConfig.DEBUG) "0" else "1",
            "사용자ID",
            "세션ID"
        )
        // 앱로그 출력 여부
        nwappLogger.setPrintLog(true)

        // 컴포넌트 액션 동작시
        nwappLogger.sendLog(
            this, "START"
        )
        /* NWAppLogger 설정 end*/
    }

    override fun onGoogleReferrerData(result: Boolean) {
        Log.d("onGoogleReferrerData", result.toString())
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestNWAppLoggerTheme {
        Greeting("Android")
    }
}