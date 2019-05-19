package com.example.lights;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {
    StringRequest buildStrRequests(String url, final ToggleButton toggleBtn, final boolean[] justOpened, final int idx){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("STATE1", response);
                        if(response.equals("0")){ // the relay switch is off
                            Log.d("STATE1", "Relay is OFF");
                            toggleBtn.setChecked(false);
                        }else if(response.equals("1")){
                            Log.d("STATE1", "Relay is ON");
                            justOpened[idx] = true;
                            toggleBtn.setChecked(true);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        return stringRequest;
    }
    void setOnCheckedListener(final String url, final ToggleButton toggleBtn, final boolean[] justOpened, final int idx, final RequestQueue queue){
        toggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked && justOpened[idx]) {
                        justOpened[idx] = false;
                    } else if (!isChecked || isChecked && !justOpened[idx]) {
                        queue.add(buildStrRequests(url, toggleBtn, justOpened, idx));
                    }

                }
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ToggleButton toggleBtn1 = (ToggleButton) findViewById(R.id.toggleButton1);
        final ToggleButton toggleBtn2 = (ToggleButton) findViewById(R.id.toggleButton2);
        final boolean[] justOpened = {false, false};
        final RequestQueue queue = Volley.newRequestQueue(getApplication());
        // Make an HTTP get request to get the status of our relay switch - is it on or off?
        //  - if the relay switch is activated - change our toggle button to be set to "on",
        //  - so that when the app is first opened, the toggle buttons are correctly set
        queue.add(buildStrRequests("http://192.168.1.12:5000/status/2", toggleBtn1, justOpened, 0));
        queue.add(buildStrRequests("http://192.168.1.12:5000/status/3", toggleBtn2, justOpened, 1));

        // Set an event listener for when our button is turned on or off to make an HTTP request to turn
        // the relay switch on or off
        setOnCheckedListener("http://192.168.1.12:5000/2", toggleBtn1, justOpened, 0, queue);
        setOnCheckedListener("http://192.168.1.12:5000/3", toggleBtn2, justOpened, 1, queue);
    }
}
