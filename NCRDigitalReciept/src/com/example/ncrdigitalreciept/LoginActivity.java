package com.example.ncrdigitalreciept;

import com.parse.LogInCallback;
import com.parse.SignUpCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

	}

	public void onShowSignUp(View view) {
		findViewById(R.id.login_activity_signup_body).setVisibility(
				View.VISIBLE);
		findViewById(R.id.login_activity_login_body).setVisibility(View.GONE);

	}

	public void onLogin(View view) {

		EditText editTextUserName = (EditText) findViewById(R.id.login_activity_login_body_user_name);
		EditText editTextPassword = (EditText) findViewById(R.id.login_activity_login_body_password);
		ParseUser.logInInBackground(editTextUserName.getText().toString(),
				editTextPassword.getText().toString(),

				new LogInCallback() {
					public void done(ParseUser user, ParseException e) {
						if (user != null) {
							// Hooray! The user is logged in.
						} else {
							// Signup failed. Look at the ParseException to see
							// what happened.
						}
					}

				});

	}

	public void onSignUp(View view) {
		EditText editTextUserName = (EditText) findViewById(R.id.login_activity_signup_body_user_name);
		EditText editTextPassword = (EditText) findViewById(R.id.login_activity_signup_body_password);
		ParseUser user = new ParseUser();
		user.setUsername(editTextUserName.getText().toString());
		user.setPassword(editTextPassword.getText().toString());
		user.setEmail(editTextUserName.getText().toString());
		 
		
	
		 
		user.signUpInBackground(new SignUpCallback() {
		  public void done(ParseException e) {
		    if (e == null) {
		      // Hooray! Let them use the app now.
		    } else {
		      // Sign up didn't succeed. Look at the ParseException
		      // to figure out what went wrong
		    }
		  }
		});
	}

	public void onShowLogin(View view) {
		findViewById(R.id.login_activity_login_body)
				.setVisibility(View.VISIBLE);
		findViewById(R.id.login_activity_signup_body).setVisibility(View.GONE);
	}

}
