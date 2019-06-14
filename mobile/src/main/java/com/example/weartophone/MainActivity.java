package com.example.weartophone;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks , GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient googleApiClient = null;
    public static final String TAG = "MyDataMap";
    public static final String WEARABLE_DATA_PATH = "/wearable/data/path";

    private EditText etText;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this);
        builder.addApi(Wearable.API);
        builder.addConnectionCallbacks(this);
        builder.addOnConnectionFailedListener(this);
        googleApiClient = builder.build();


        etText = findViewById(R.id.etText);
        btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {

        if(googleApiClient!=null && googleApiClient.isConnected())
        {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        // this is the point where we send message
        sendMessage();
    }

    public class SendMessageToDataLayer extends Thread{

        String path;
        String message;

        public SendMessageToDataLayer(String path,String message)
        {
            this.path = path;
            this.message = message;
        }

        //override the run method
        //after calling start method it will start run()
        @Override
        public void run() {
            // we have to send to the connected devices via bluetooth,the are called node
            NodeApi.GetConnectedNodesResult nodesList = Wearable.NodeApi.getConnectedNodes(googleApiClient).await();

            //get the connected nodes

            for(Node node : nodesList.getNodes())
            {
                MessageApi.SendMessageResult messageResult = Wearable.MessageApi
                        .sendMessage(googleApiClient,node.getId(),path,message.getBytes()).await();
                if(messageResult.getStatus().isSuccess())
                {
                    Toast.makeText(MainActivity.this, "Message sent to :  " + node.getDisplayName() + " node id : " + node.getId(), Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(MainActivity.this, "Error while sending a message to node", Toast.LENGTH_SHORT).show();
                }
            }


        }

    }

    private void sendMessage() {
        if(googleApiClient.isConnected())
        {
            String message =((EditText) findViewById(R.id.etText)).getText().toString();
            if(message==null)
            {
                message="Hello world";

            }

            // we have to create a new thread , to make sure it will not block main thread
            new SendMessageToDataLayer(WEARABLE_DATA_PATH,message).start();


        }
        else
        {

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
