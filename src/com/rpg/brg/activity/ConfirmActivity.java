package com.rpg.brg.activity;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rpg.brg.library.model.AllContactDetails;
import com.rpg.brg.library.utility.Utility;
import com.rpg.brg.vapp.R;

/**
 * 
 * Confirmation Activity for Send and Money request. 
 * 
 * @author "Jigar Gosalia"
 *
 */
public class ConfirmActivity extends AbstractActivity {

	private static final String TAG = ConfirmActivity.class.getCanonicalName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm);
		// colorActionBar();

		// setActionBarTitle(getIntent().getStringExtra(ACTIVITY_STRING));

		playMediaPlayer(R.raw.cha_ching_register);

		name = getIntent().getStringExtra(Utility.NAME_STRING);

		allContactDetails = (AllContactDetails) getIntent().getSerializableExtra(Utility.ALL_CONTACT_DETAILS_STRING);
		if (allContactDetails != null) {
			Utility.toastShort(ConfirmActivity.this, "Contacts Size is : " + allContactDetails.getAllContactDetails().size(), false);
		}

		commandTextView = (EditText) findViewById(R.id.commandTextView);

		TextView textView = (TextView) findViewById(R.id.textView);
		String transactionDetails = getIntent().getExtras().getString(Utility.TRANSACTION_DETAILS_STRING);
		textView.setText(transactionDetails);
		
		ImageButton speakImageButton = (ImageButton) findViewById(R.id.button);
		speakImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				ImageButton speakImageButton = (ImageButton) findViewById(R.id.button);
				speakImageButton.setBackgroundResource(R.drawable.ic_microphone_pressed);
				commandTextView.setText(Utility.SPEAK_NOW_STRING);
				Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
				intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,	getPackageName());
				intent.putExtra(RecognizerIntent.EXTRA_PROMPT, Utility.SPEAK_NOW_STRING);
				intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
				speechRecognizer.startListening(intent);
			}
		});

		speechRecognizer = SpeechRecognizer.createSpeechRecognizer(ConfirmActivity.this);
		speechRecognizer.setRecognitionListener(new RecognitionListener() {
			
			@Override
			public void onRmsChanged(float rmsdB) {
			}
			
			@Override
			public void onResults(Bundle results) {
				Log.d(TAG, "onResults " + results);
				StringBuilder inputString = new StringBuilder();
				ArrayList<String> voiceResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
	            if (voiceResults != null) {
	                Log.d(TAG, "Printing matches: ");
	                inputString.append(voiceResults.get(0));
	            }
	            commandTextView.setText("You Said : " + inputString.toString());
	            handleSpeechRequest(inputString.toString());
				ImageButton speakImageButton = (ImageButton) findViewById(R.id.button);
				speakImageButton.setBackgroundResource(R.drawable.ic_microphone);
			}
			
			@Override
			public void onReadyForSpeech(Bundle params) {
			}
			
			@Override
			public void onPartialResults(Bundle partialResults) {
			}
			
			@Override
			public void onEvent(int eventType, Bundle params) {
			}
			
			@Override
			public void onError(int error) {
				commandTextView.setText(getSpeechRecognizerErrorMessage(error));
				ImageButton speakImageButton = (ImageButton) findViewById(R.id.button);
				speakImageButton.setBackgroundResource(R.drawable.ic_microphone);
				playMediaPlayer(R.raw.repeat_command);
			}
			
			@Override
			public void onEndOfSpeech() {
			}
			
			@Override
			public void onBufferReceived(byte[] buffer) {
			}
			
			@Override
			public void onBeginningOfSpeech() {
			}
		});
		
		textToSpeech = new TextToSpeech(ConfirmActivity.this, new TextToSpeech.OnInitListener() {
		      @Override
		      public void onInit(int status) {
		  		TextView textView = (TextView) findViewById(R.id.textView);

		    	  if(status == TextToSpeech.SUCCESS){
			            textToSpeech.setLanguage(Locale.US);
			            Utility.toastShort(ConfirmActivity.this, Utility.TTS_HANDLER_SUCCESS, false);
			    		if (textView != null
			    				&& textView.getText() != null
			    				&& textView.getText().toString() != null
			    				&& textView.getText().toString().length() > 0) {
			    			String toSpeak = textView.getText().toString();
			    			Utility.speakText(ConfirmActivity.this, textToSpeech, toSpeak, false);
			    		}
			        } else {
			        	Utility.toastShort(ConfirmActivity.this, Utility.STATUS_IS + String.valueOf(status), false);
			        }
			  }
		});

		Button commandButton = (Button) findViewById(R.id.commandButton);
		commandButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {

				EditText editText = (EditText) findViewById(R.id.enterCommandEditText);
				if (Utility.dataExists(editText)) {
					handleSpeechRequest(editText.getText().toString());
				}
			}
		});
		debugMode();
	}

	/**
	 * Handle Speech Request for different commands
	 * 
	 * 
	 * @param inputString
	 */
	protected void handleSpeechRequest(final String inputString) {
		if (inputString != null
				&& (inputString.contains(Utility.COMMAND_HELP)
				|| inputString.contains(Utility.COMMAND_ANOTHER)
				|| inputString.contains(Utility.COMMAND_DONE)
				|| inputString.contains(Utility.COMMAND_ACTIVITY))) {
			if (inputString.contains(Utility.COMMAND_HELP)) {
				playMediaPlayer(R.raw.help_activity_done_another);
			} else if (inputString.contains(Utility.COMMAND_ANOTHER)) {
				Intent intent = new Intent(ConfirmActivity.this, LandingPageActivity.class);
				intent.putExtra(Utility.ALL_CONTACT_DETAILS_STRING, allContactDetails);
				intent.putExtra(Utility.NAME_STRING, name);
				startActivity(intent);
			} else if (inputString.contains(Utility.COMMAND_DONE)) {
				this.moveTaskToBack(true);
			} else if (inputString.contains(Utility.COMMAND_ACTIVITY)) {
				String transactionDetails = getIntent().getExtras().getString(Utility.TRANSACTION_DETAILS_STRING);
				Intent intent = new Intent(ConfirmActivity.this, RecentActivity.class);
				intent.putExtra(Utility.ALL_CONTACT_DETAILS_STRING, allContactDetails);
				intent.putExtra(Utility.NAME_STRING, name);
				intent.putExtra(Utility.TRANSACTION_DETAILS_STRING, transactionDetails);
				startActivity(intent);
			}
		} else {
			playMediaPlayer(R.raw.help_activity_done_another);
		}
		ImageButton speakImageButton = (ImageButton) findViewById(R.id.button);
		speakImageButton.setBackgroundResource(R.drawable.ic_microphone);
	}
}
