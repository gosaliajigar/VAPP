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
import android.widget.Toast;

import com.rpg.brg.library.model.AllContactDetails;
import com.rpg.brg.library.provider.TransactionsProvider;
import com.rpg.brg.library.utility.Utility;
import com.rpg.brg.vapp.R;

/**
 * Recent Activity shows Activity Page
 * 
 * @author "Jigar Gosalia"
 *
 */
public class RecentActivity extends AbstractActivity {

	private static final String TAG = RecentActivity.class.getCanonicalName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recent_activity);

		commandTextView = (EditText) findViewById(R.id.commandTextView);

		name = getIntent().getStringExtra(Utility.NAME_STRING);

		allContactDetails = (AllContactDetails) getIntent().getSerializableExtra(Utility.ALL_CONTACT_DETAILS_STRING);
		if (allContactDetails != null) {
			Utility.toastShort(RecentActivity.this, "Contacts Size is : " + allContactDetails.getAllContactDetails().size(), false);
		}

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

		speechRecognizer = SpeechRecognizer.createSpeechRecognizer(RecentActivity.this);
		speechRecognizer.setRecognitionListener(new RecognitionListener() {
			
			@Override
			public void onRmsChanged(float rmsdB) {
			}
			
			@Override
			public void onResults(Bundle results) {
				StringBuilder inputString = new StringBuilder();
				Log.d(TAG, "onResults " + results);
				ArrayList<String> voiceResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
	            if (voiceResults != null) {
	                Log.d(TAG, "Printing matches: ");
	                inputString.append(voiceResults.get(0));
	            }
	            commandTextView.setText(Utility.YOU_SAID + inputString.toString());
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

		textToSpeech = new TextToSpeech(RecentActivity.this, new TextToSpeech.OnInitListener() {
		      @Override
		      public void onInit(int status) {
		    	  if(status == TextToSpeech.SUCCESS){
			            textToSpeech.setLanguage(Locale.US);
			            Utility.toastShort(RecentActivity.this, Utility.TTS_HANDLER_SUCCESS, false);
			            String transactionDetails = getIntent().getExtras().getString(Utility.TRANSACTION_DETAILS_STRING);
			            if (transactionDetails != null
			    				&& transactionDetails.length() > 0) {
			    			String toSpeak = Utility.YOUR_MOST_RECENT_TRANSACTION + transactionDetails;
			    			Utility.speakText(RecentActivity.this, textToSpeech, toSpeak, false);
			    		}
			        } else {
			        	Utility.toastShort(RecentActivity.this, Utility.STATUS_IS + String.valueOf(status), false);
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

		Button addTransactionButton = (Button) findViewById(R.id.addTransaction);
		addTransactionButton.setVisibility(View.VISIBLE);
		addTransactionButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				TransactionsProvider.addTransaction(getBaseContext(), getContentResolver());
			}
		});

		class ClickListener implements View.OnClickListener {

			RecentActivity activity = (RecentActivity)getActivityInstance();

			@Override
			public void onClick(View view) {
				transactionDetailsMap = TransactionsProvider.retrieveTransactions(activity);
				Toast.makeText(getBaseContext(), "Transactions : " + transactionDetailsMap.size(), Toast.LENGTH_SHORT).show();
			}
		}

		Button retrieveTransactionButton = (Button) findViewById(R.id.retrieveTransaction);
		retrieveTransactionButton.setVisibility(View.VISIBLE);
		retrieveTransactionButton.setOnClickListener(new ClickListener());

		if (!Utility.DEBUG_MODE) {
			commandButton.setVisibility(View.GONE);
			EditText editText = (EditText) findViewById(R.id.enterCommandEditText);
			editText.setVisibility(View.GONE);
			addTransactionButton.setVisibility(View.GONE);
			retrieveTransactionButton.setVisibility(View.GONE);
		}
	}

	/**
	 * Handle Speech Request for different commands
	 * 
	 * @param inputString
	 */
	protected void handleSpeechRequest(final String inputString) {
		if (inputString != null
				&& (inputString.contains(Utility.COMMAND_HELP)
				|| inputString.contains(Utility.COMMAND_DONE)
				|| inputString.contains(Utility.COMMAND_CANCEL)
				|| inputString.contains(Utility.COMMAND_BACK))) {
			if (inputString.contains(Utility.COMMAND_HELP)) {
				playMediaPlayer(R.raw.help_done_back);
			} else if (inputString.contains(Utility.COMMAND_DONE) || inputString.contains(Utility.COMMAND_CANCEL)) {
				this.moveTaskToBack(true);
			} else if (inputString.contains(Utility.COMMAND_BACK)) {
				Intent intent = new Intent(RecentActivity.this, LandingPageActivity.class);
				intent.putExtra(Utility.ALL_CONTACT_DETAILS_STRING, allContactDetails);
				intent.putExtra(Utility.NAME_STRING, name);
				startActivity(intent);
			}
		} else {
			playMediaPlayer(R.raw.help_done_back);
		}
		ImageButton speakImageButton = (ImageButton) findViewById(R.id.button);
		speakImageButton.setBackgroundResource(R.drawable.ic_microphone);
	}
}
