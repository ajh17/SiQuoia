/**
 * 
 */
package com.sjsu.siquoia;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.sjsu.siquoia.model.SiQuoiaJSONParser;
import com.sjsu.siquoia.model.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Parnit Sainion
 * @since 25 November 2013
 * Description: This app is the home landing screen for the app. Users can: continue a previous quiz, start a new quiz,
 * 				check the leader-board, submit a question to put into the same, and quit the app.
 *
 */
public class SiQuoiaHomeActivity extends Activity {
	
	//Variable Declaration
	private Button continueButton, newGameButton, leaderboardButton, submitQuestionButton, quitButton;
	private ProgressDialog progressBar;
	private SharedPreferences preferences;
	private String userInfoUrl ="http://ec2-54-201-65-140.us-west-2.compute.amazonaws.com/getUser.php";
	private TextView currentPointsTextView; 
	protected User user;
	private AlertDialog alertDialog;
	
	//preferences
	protected static final String SIQUOIA_PREF = "SiquoiaPref";
	protected static final String LOGGED_IN = "loggedIn";
	protected static final String NEW_USER = "newUser";
	protected static final String EMAIL = "email";
	protected static final String QUIZ = "currentQuiz";
	protected static final String ANSWERS = "currentAnswers";
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        
        //initialize buttons from view
        continueButton = (Button) findViewById(R.id.continueButton);
        newGameButton = (Button) findViewById(R.id.newGameButton);
        leaderboardButton = (Button) findViewById(R.id.leaderboardButton);
        submitQuestionButton = (Button) findViewById(R.id.submitQuesButton);
        quitButton = (Button) findViewById(R.id.quitButton);
        currentPointsTextView = (TextView) findViewById(R.id.currentPointsText);
        
       //get users info from app
        preferences = getSharedPreferences(SiQuoiaHomeActivity.SIQUOIA_PREF, 0);
        
        Intent intent = getIntent();
        boolean newUser = intent.getBooleanExtra(SiQuoiaHomeActivity.NEW_USER, true);
        
        if(newUser)
        {
        	String email = intent.getStringExtra(SiQuoiaHomeActivity.EMAIL);
        	user =  new User(email);
        }
        {
        	new SiQuoiaGetUserTask().execute(preferences.getString(EMAIL, EMAIL));
        }        
        
        //Set Listener for continue
        continueButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Log.i("homeScreenButtons", "continue clicked");		
				
				if(!preferences.getString(QUIZ, "").equalsIgnoreCase(""))
				{
					Intent intent = new Intent();
					intent.setClass(SiQuoiaHomeActivity.this, QuizActivity.class);
					startActivity(intent);
				}
				else
				{
					Toast toast = Toast.makeText(getApplicationContext(), "No Saved Quiz", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
					toast.show();
				}				
			}        	
        });
        
      //Set Listener for newGameButton
        newGameButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				showNewGameAlert();
			}        	
        });
        
      //Set Listener for leaderboardButton
        leaderboardButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Log.i("homeScreenButtons", "leaderboardButton clicked");				
			}        	
        });
        
        //Set Listener for submitQuestionButton
        submitQuestionButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Log.i("homeScreenButtons", "submitQuestionButton clicked");				
			}        	
        });
        
      //Set Listener for quitButton
        quitButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//closes the application
				finish();				
			}        	
        });
    }
	
	public void showNewGameAlert()
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		
		alertDialogBuilder.setTitle("Start New Game");
		
		alertDialogBuilder.setMessage("A new game cost 5 SiQuoia points and will override any uncompleted Quiz. Do you want to start a new Quiz?");
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {						
				if(user.buyPacket())
				{
					Intent intent = new Intent();
					intent.setClass(SiQuoiaHomeActivity.this, NewQuizActivity.class);
					startActivity(intent);
				}
				else
				{
					Toast toast = Toast.makeText(getApplicationContext(), "Not Enough SiQuoia Points", Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
		
		alertDialogBuilder.setNegativeButton("No",  new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();						
			}					
		});				
		
		alertDialog = alertDialogBuilder.create();
		alertDialog.show();	
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
		switch(item.getItemId())
		{
			//user wants to log out
			case R.id.action_logout:
				//update user info
				SharedPreferences preferences = getSharedPreferences(SiQuoiaHomeActivity.SIQUOIA_PREF, 0);
				SharedPreferences.Editor perferenceUpdater = preferences.edit();
				perferenceUpdater.putBoolean(SiQuoiaHomeActivity.LOGGED_IN, false);
				perferenceUpdater.putString(SiQuoiaHomeActivity.EMAIL, "");
				perferenceUpdater.putString(SiQuoiaHomeActivity.QUIZ, "");
				perferenceUpdater.putString(SiQuoiaHomeActivity.ANSWERS, "");
				
				
				//commit preference changes
				perferenceUpdater.commit();
				
				Intent intent = new Intent();
	        	intent.setClass(SiQuoiaHomeActivity.this, SiQuoiaLoginActivity.class);
	        	startActivity(intent);
	        	finish();
				break;
				
			//user is redeeming a code
			case R.id.action_redeem:
				Toast toast = Toast.makeText(getApplicationContext(), "To Be Implemented", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				toast.show();
				break;
				
			default:
				break;
		}
    	return false;    	
    }    
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	
    	//set Current points
    	if(currentPointsTextView != null && user != null)
        currentPointsTextView.setText("Current Points: " + user.getSiquoiaBucks());
    }
    
    /**
     * get user's information from the database
     * @param email user's email
     * @return user information or nothing
     */
    public String getUser(String email)
    {
    	//variables declared
    	String message ="";
    	HttpClient httpclient = new DefaultHttpClient();
    	HttpPost httppost = new HttpPost(userInfoUrl);
    	
    	try {
    		//add user information to post
        	List<NameValuePair> data = new ArrayList<NameValuePair>(1);    	
        	data.add(new BasicNameValuePair("email",email));
			httppost.setEntity(new UrlEncodedFormEntity(data));
			
			//HttpResponse response = httpclient.execute(httppost);
			
			ResponseHandler<String> handler = new BasicResponseHandler();
			message = httpclient.execute(httppost,handler);
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
    	return message;
    }
    
    /**
     * This is the background task that will get the user's current information from the database.
     * @author Parnit Sainion
     *
     */
    class SiQuoiaGetUserTask extends AsyncTask<String, String, String>
    {
    	@Override
		protected void onPreExecute() {
			//create the progress dialog and display it
    		progressBar = new ProgressDialog(SiQuoiaHomeActivity.this);
			progressBar.setIndeterminate(true);
			progressBar.setCancelable(false);
			progressBar.setMessage("Getting User Info");
			progressBar.show();			
		}
    	
    	@Override
		protected String doInBackground(String... input) {
    		//input[0] = username
			return getUser(input[0]);
		}
		
		protected void onPostExecute(String result) {
		
				//get preferences
				//SharedPreferences preferences = getSharedPreferences(SiQuoiaHomeActivity.SIQUOIA_PREF, 0);
				
				//update user info
				//SharedPreferences.Editor perferenceUpdater = preferences.edit();
				
				//commit preference changes
				//perferenceUpdater.commit();
				System.out.println(result);
				user = SiQuoiaJSONParser.parseUser(result);	
				
			
				//update user's quiz
				preferences = getSharedPreferences(SiQuoiaHomeActivity.SIQUOIA_PREF, 0);
				SharedPreferences.Editor perferenceUpdater = preferences.edit();
				perferenceUpdater.putString(SiQuoiaHomeActivity.QUIZ, user.getCurrentQuiz());
				perferenceUpdater.putString(SiQuoiaHomeActivity.ANSWERS, user.getAnswers());
				perferenceUpdater.commit();

		        //set Current points
		        currentPointsTextView.setText("Current Points: " + user.getSiquoiaBucks());
				
				//close progress dialog
				progressBar.dismiss();
		}    	
    }
}
