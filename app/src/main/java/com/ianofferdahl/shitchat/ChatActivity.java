package com.ianofferdahl.shitchat;

import android.app.ActionBar;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ianofferdahl.shitchat.DataModels.DeviceMessageRequest;
import com.ianofferdahl.shitchat.DataModels.DeviceRequest;
import com.ianofferdahl.shitchat.DataModels.Messages;
import com.ianofferdahl.shitchat.DataModels.TestObject;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Timer;
import java.util.TimerTask;


public class ChatActivity extends Activity {

    private double maxWidth;

    private RelativeLayout chatLayout;

    private View lastMessage;

    private EditText speakEditText;

    private Button sendMessageButton;

    private ScrollView chatScrollView;

    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        maxWidth = dm.widthPixels/1.2;

        this.chatLayout = (RelativeLayout) findViewById(R.id.chat_layout);
        this.speakEditText = (EditText) findViewById(R.id.speak_edit_text);
        this.sendMessageButton = (Button) findViewById(R.id.send_message_button);
        this.chatScrollView = (ScrollView) findViewById(R.id.chat_scroll_view);



        this.sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMessage(speakEditText.getText().toString());
                new SendMessageRequestTask().execute(speakEditText.getText().toString());
                speakEditText.setText("");
                //new GetMessagesRequestTask().execute();
            }
        });

        //fillSampleConversation();
        new GetFriendRequestTask().execute();

        setUpRequestTimer();

    }

    @Override
    protected void onPause() {
        cancelTimer();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Utils.resetUUID();
        super.onDestroy();
    }

    private void setUpRequestTimer()
    {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new GetMessagesRequestTask().execute();
            }
        }, 1000, 1000);
    }

    private void cancelTimer()
    {
        timer.cancel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //new HttpRequestTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reset) {
            this.recreate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fillSampleConversation()
    {
        this.addPartnerMessage("Hello fellow Pooper");
        this.addMessage("Hello back to you");
        this.addPartnerMessage("How are you doing today?");
        this.addMessage("Not so good, just had a couple dozen tacos");
        this.addPartnerMessage("Sweet baby Jesus, a couple DOZEN???");
        this.addMessage("My buddy and I had a competition going to see who could eat more. He ended up winning.");
        this.addPartnerMessage("How many did he eat?");
        this.addMessage("127 and a half");
        this.addPartnerMessage("He couldn't finish the last half for an even 128?");
        this.addMessage("Have you ever tried to eat 128 tacos?");
        this.addPartnerMessage("No...");
        this.addMessage("Well sit down and try it some time, its life-changing");
        this.addMessage("and colon-changing");
        this.addMessage("In retrospect, hot sauce might not have been the best idea");
        this.addPartnerMessage("At least you learned something from this whole experience");
        this.addMessage("Only that tacos will turn all matter in your body into poop");
    }


    private void addPartnerMessage(String message) {

        TextView partnerMessage = new TextView(this);
        partnerMessage.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        partnerMessage.setText(message);
        partnerMessage.setPadding(16, 16, 16, 16);
        partnerMessage.setTextColor(getResources().getColor(R.color.white));
        partnerMessage.setBackgroundResource(R.drawable.partner_message_background);
        partnerMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.chat_text_size));
        partnerMessage.setMaxWidth((int) maxWidth);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,4,0,4);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            partnerMessage.setId(Utils.generateViewId());
        } else {
            partnerMessage.setId(View.generateViewId());
        }

        if (lastMessage != null) {
            params.addRule(RelativeLayout.BELOW, lastMessage.getId());
        }

        lastMessage = partnerMessage;
        chatLayout.addView(partnerMessage, params);

        chatScrollView.post(new Runnable() {
            public void run() {
                chatScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

    }

    private void addMessage(String message) {

        TextView sentMessage = new TextView(this);
        sentMessage.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        sentMessage.setText(message);
        sentMessage.setPadding(16, 16, 16, 16);
        sentMessage.setTextColor(getResources().getColor(R.color.white));
        sentMessage.setBackgroundResource(R.drawable.message_background);
        sentMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.chat_text_size));
        sentMessage.setMaxWidth((int) maxWidth);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            sentMessage.setId(Utils.generateViewId());
        } else {
            sentMessage.setId(View.generateViewId());
        }


        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 4, 0, 4);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);

        if (lastMessage != null) {
            params.addRule(RelativeLayout.BELOW, lastMessage.getId());
        }

        lastMessage = sentMessage;
        chatLayout.addView(sentMessage, params);

        chatScrollView.post(new Runnable() {
            public void run() {
                chatScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

    }

    private void testObjectReceived(TestObject testObject) {

        getActionBar().setTitle(testObject.getName() + " " + testObject.getGuid());

    }

    private class HttpRequestTask extends AsyncTask<Void, Void, TestObject> {
        @Override
        protected TestObject doInBackground(Void... params) {
            try {
                final String url = "https://ianofferdahl.com:9797/Response";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                TestObject testObject = restTemplate.getForObject(url, TestObject.class);
                return testObject;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(TestObject testObject) {
            testObjectReceived(testObject);
        }

    }

    private class GetFriendRequestTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                final String url = "https://ianofferdahl.com:9797/ShitChat/GetFriend";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                return restTemplate.postForObject(url, new DeviceRequest(Utils.getUUID()), String.class);
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e(this.getClass().getSimpleName(), s);
        }
    }

    private class GetMessagesRequestTask extends AsyncTask<Void, Void, Messages> {
        @Override
        protected Messages doInBackground(Void... params) {
            try {
                final String url = "https://ianofferdahl.com:9797/ShitChat/GetMessages";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                return restTemplate.postForObject(url, new DeviceRequest(Utils.getUUID()), Messages.class);
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return new Messages();
        }

        @Override
        protected void onPostExecute(Messages messages) {
            if (messages != null) {
                for (String message : messages.getMessageList()) {
                    Log.e(this.getClass().getSimpleName(), message + "");
                    addPartnerMessage(message);
                }
            }
        }
    }

    private class SendMessageRequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                final String url = "https://ianofferdahl.com:9797/ShitChat/SendMessage";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                return restTemplate.postForObject(url, new DeviceMessageRequest(Utils.getUUID(), params[0]), String.class);
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e(this.getClass().getSimpleName(), s);
        }
    }
}
