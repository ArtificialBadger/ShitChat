package com.ianofferdahl.shitchat;

import android.app.ActionBar;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ianofferdahl.shitchat.DataModels.TestObject;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;


public class ChatActivity extends Activity {

    private double maxWidth;

    private RelativeLayout chatLayout;

    private View lastMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        maxWidth = dm.widthPixels/1.2;

        this.chatLayout = (RelativeLayout) findViewById(R.id.chat_layout);
        this.addPartnerMessage("Hello fellow Pooper");
        this.addMessage("Hello back to you");
        this.addPartnerMessage("What did you eat?");
        this.addMessage("Cheerios and Vodka");
        this.addMessage("and applesauce");
        this.addPartnerMessage("Hello fellow Pooper");
        this.addMessage("Hello back to you");
        this.addPartnerMessage("What did you eat?");
        this.addMessage("Cheerios and Vodka");
        this.addMessage("and applesauce");
        this.addPartnerMessage("Hello fellow Pooper");
        this.addMessage("Hello back to you");
        this.addPartnerMessage("What did you eat?");
        this.addMessage("Cheerios and Vodka");
        this.addMessage("and applesauce");
        this.addPartnerMessage("Hello fellow Pooper");
        this.addMessage("Hello back to you");
        this.addPartnerMessage("What did you eat?");
        this.addPartnerMessage("Cheerios and Vodka");
        this.addPartnerMessage("and applesauce as well as a plethora of other things, This is really long text. Is is tho?");
        this.addMessage("What did you eat?");
        this.addMessage("Cheerios and Vodka");
        this.addMessage("and applesauce as well as a plethora of other things, This is really long text. Is is tho?");
    }

    @Override
    protected void onStart() {
        super.onStart();
        new HttpRequestTask().execute();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addPartnerMessage(String message) {

        TextView partnerMessage = new TextView(this);
        partnerMessage.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        partnerMessage.setText(message);
        partnerMessage.setPadding(16, 16, 16, 16);
        partnerMessage.setTextColor(getResources().getColor(R.color.white));
        partnerMessage.setGravity(Gravity.CENTER);
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
        params.setMargins(0,4,0,4);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);

        if (lastMessage != null) {
            params.addRule(RelativeLayout.BELOW, lastMessage.getId());
        }

        lastMessage = sentMessage;
        chatLayout.addView(sentMessage, params);

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
}
