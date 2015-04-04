/**********************************************************************************************************/
// Harman HKController IPC Example
// This simplified example show how to build simple connections to the HKController Application
//
// For more information, see Android Docs http://developer.android.com/guide/components/bound-services.html
/***********************************************************************************************************/

package fuc.hackgt;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import fuc.hackgt.ConvertService;

public class MainActivity extends Activity {
    private Messenger messenger;
    int connStatus = 0; //0:not connected, 1:connected

    //connect button event handler - Button 1
    public void sendConnect(View view) {
        ServiceConnection sConn;

        logString("Sending Connect Request to HKConnect App.");

        sConn = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                messenger = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                // We are connected to the service
                messenger = new Messenger(service);
                logString("onServiceConnected: New Message object created:" + name.toString());
                connStatus = 1;
                unlockButtons();
            }
        };

        //bind to service.
        bindService(new Intent("com.harman.commom.device.model.imp.ConvertService"), sConn, Context.BIND_AUTO_CREATE);

    }

    //Send Play Command event handler - Button 2
    public void sendPlay(View view) {

        try {
            if(connStatus == 1) {
                logString("Sending Play Request to HKConnect.");
                String val = ""; //not yet implemented
                Message msg = Message.obtain(null, ConvertService.MSG_ID_START_PLAYING_ALL);

                msg.replyTo = new Messenger(new ResponseHandler());

                // We pass the value and set in msg object
                Bundle b = new Bundle();
                b.putString("data", val);
                msg.setData(b);

                //send message to HKConnect app
                messenger.send(msg);
            } else {
                logString("Please tap the connect button before attempting to communicate with the HK Controller App.");
            }
        } catch (RemoteException e) {
            logString(e.toString());
        }
    }

    //Send Pause Command event handler - Button 3
    public void sendPause(View view) {
        try {
            if(connStatus == 1) {
                logString("Sending Pause Request to HKConnect.");
                String val = ""; //not yet implemented
                Message msg = Message.obtain(null, ConvertService.MSG_ID_STOP_PLAYING_ALL);

                msg.replyTo = new Messenger(new ResponseHandler());

                // We pass the value and set in msg object
                Bundle b = new Bundle();
                b.putString("data", val);
                msg.setData(b);

                //send message to HKConnect App
                messenger.send(msg);
            } else {
                logString("Please tap the connect button before attempting to communicate with the HK Controller App.");
            }
        } catch (RemoteException e) {
            logString( e.toString());
        }
    }


    //Send Query Status event handler - Button 4
    public void sendQuery(View view) {

        try {
            if(connStatus == 1) {
                logString( "Sending Query to HKConnect.");
                String val = ""; //not yet implemented
                Message msg = Message.obtain(null, ConvertService.MSG_ID_QUERY_DEVICE);

                msg.replyTo = new Messenger(new ResponseHandler());

                // We pass the value and set in msg object
                Bundle b = new Bundle();
                b.putString("data", val);
                msg.setData(b);

                //send message to HKConnect app
                messenger.send(msg);
            } else {
                logString( "Please tap the connect button before attempting to communicate with the HK Controller App.");
            }

        } catch (RemoteException e) {
            logString( e.toString());
        }

    }

    // This class handles the Service response
    class ResponseHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            int respCode = msg.what;
            String str = msg.getData().getString("respData");

            switch (respCode) {

                default:
                    logString("Length:" + str.length()+ "Bytes | " + "Data: " + str);
                    break;
            }
        }
    }

    //simple logging function for Log.d AND screen
    private void logString(String strLog) {
        Log.d("CommLog", strLog);

        //update the text edit field on screen
        EditText editText = (EditText) findViewById(R.id.log_text);
        String stuffIn = editText.getText().toString();
        String stuffOut = stuffIn + "\n" + strLog;
        editText.setText(stuffOut);
    }

    //quickly enable the buttons once communication is established with the HK Connect application
    private void unlockButtons() {

        Button sendplay = (Button) findViewById(R.id.send_play);
        sendplay.setEnabled(true);

        Button sendpause = (Button) findViewById(R.id.send_pause);
        sendpause.setEnabled(true);

        Button sendquery = (Button) findViewById(R.id.send_query);
        sendquery.setEnabled(true);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        Log.d("Destroy", "shutting things down...");
    }
}
