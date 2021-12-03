package mobi.acpm.inspeckage.webserver;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import mobi.acpm.inspeckage.R;

/**
 * Created by acpm on 17/11/15.
 */
public class InspeckageService extends Service {
    private final static String TAG = "InspeckageService";
    private MediaPlayer mMediaPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private WebServer ws;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Context context = getApplicationContext();

        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.silent);
        mMediaPlayer.setLooping(true);

        String host = null;
        int port = 8008;
        if (intent != null && intent.getExtras() != null) {
            host = intent.getStringExtra("host");
            port = intent.getIntExtra("port", 8008);
        }

        try {

            ws = new WebServer(host, port, context);


        } catch (IOException e) {
            e.printStackTrace();
        }

        //强制后台运行
        startPlayMusic();

        Toast.makeText(this, "Service started on port " + port, Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //if(ws!=null)
        //    ws.stop();
        stopPlayMusic();

        Log.d("Inspeckage", "onDestroy: Service stopped");
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }


    private void startPlayMusic(){
        if(mMediaPlayer != null){
            mMediaPlayer.start();
        }

        try {
            ws.start();    //服务器开启，TODO？后台运行1个小时1*60*60*1000
            Log.e("WEBSTART", "WebServer started");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("WEBSTART", "WebServer start fail!");
        }
    }

    private void stopPlayMusic(){
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
        }

        if (ws != null) {
            ws.closeAllConnections();
            ws = null;
            Log.e("onPause", "app pause, so web server close");
        }
    }
}
