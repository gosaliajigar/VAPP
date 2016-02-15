package com.rpg.brg.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rpg.brg.library.utility.Utility;
import com.rpg.brg.vapp.R;

/**
 * Login Activity
 * 
 * @author "Jigar Gosalia"
 *
 */
public class LoginActivity extends AbstractActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
		emailEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Button loginButton = (Button) findViewById(R.id.loginButton);
				toggleButton(loginButton);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		passwordEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Button loginButton = (Button) findViewById(R.id.loginButton);
				toggleButton(loginButton);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		Button loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
				EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
				if (Utility.dataExists(emailEditText)
						&& Utility.dataExists(passwordEditText)) {
					if (Utility.validateEmailAddress(emailEditText.getText().toString())) {
						Intent intent = new Intent(LoginActivity.this, LandingPageActivity.class);
						intent.putExtra(Utility.NAME_STRING, Utility.extractName(emailEditText.getText().toString()));
						startActivity(intent);
					} else {
						Utility.toastLong(LoginActivity.this, Utility.ENTER_VALID_EMAIL_ADDRESS, true);
					}
				}
			}
		});

		TextView signUpEditText = (TextView) findViewById(R.id.signUpEditText);
		signUpEditText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Utility.toastLong(LoginActivity.this, Utility.FEATURE_NOT_AVAILABLE, true);
			}
		});
	}

	/**
	 * Toggle Buttons behavior depending on data present in email and password text boxes
	 * 
	 * @param button
	 */
	private void toggleButton(Button button) {
		EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
		EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		if (Utility.dataExists(emailEditText)
				&& Utility.dataExists(passwordEditText)) {
			button.setEnabled(true);
			button.setBackgroundColor(Color.parseColor(Utility.HEX_BLUE_COLOR));
		} else {
			button.setEnabled(false);
			button.setBackgroundColor(Color.parseColor(Utility.HEX_LIGHT_BLUE_COLOR));
		}
	}
}
