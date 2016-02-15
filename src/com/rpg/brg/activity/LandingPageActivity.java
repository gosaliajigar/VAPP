package com.rpg.brg.activity;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.rpg.brg.library.model.AllContactDetails;
import com.rpg.brg.library.model.ContactDetails;
import com.rpg.brg.library.model.TransactionDetails;
import com.rpg.brg.library.provider.TransactionsProvider;
import com.rpg.brg.library.utility.Utility;
import com.rpg.brg.vapp.R;

/**
 * 
 * Account Overview or Landing Page Activity when user logs in.
 * 
 * @author "Jigar Gosalia"
 * 
 */
public class LandingPageActivity extends AbstractActivity {

	protected static final String TAG = LandingPageActivity.class.getCanonicalName();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_activate) {
			Utility.DEBUG_MODE = false;
			Utility.VAPP_ACTIVATED = true;
			handleActivation();
			return true;
		}

		if (item.getItemId() == R.id.action_debug) {
			Utility.DEBUG_MODE = true;
			Utility.VAPP_ACTIVATED = false;
			debugMode();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_landing_page);

		commandTextView = (EditText) findViewById(R.id.commandTextView);
		
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
		
		if (Utility.VAPP_ACTIVATED) {
			handleActivation();
		} else {
			speakImageButton.setVisibility(View.GONE);	
			commandTextView.setVisibility(View.GONE);
		}
		speechRecognizer = SpeechRecognizer.createSpeechRecognizer(LandingPageActivity.this);
		speechRecognizer.setRecognitionListener(new RecognitionListener() {
			
			@Override
			public void onRmsChanged(float rmsdB) {
				Log.d(TAG, "onRmsChanged");				
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
				Log.d(TAG, "onReadyForSpeech");
			}
			
			@Override
			public void onPartialResults(Bundle partialResults) {
				Log.d(TAG, "onPartialResults");
			}
			
			@Override
			public void onEvent(int eventType, Bundle params) {
				Log.d(TAG, "onEvent " + eventType);
			}
			
			@Override
			public void onError(int error) {
				Log.d(TAG, Utility.ERROR_CODE + error);
				commandTextView.setText(getSpeechRecognizerErrorMessage(error));
				ImageButton speakImageButton = (ImageButton) findViewById(R.id.button);
				speakImageButton.setBackgroundResource(R.drawable.ic_microphone);
				playMediaPlayer(R.raw.repeat_command);
			}

			@Override
			public void onEndOfSpeech() {
				Log.d(TAG, "onEndofSpeech");
			}
			
			@Override
			public void onBufferReceived(byte[] buffer) {
				Log.d(TAG, "onBufferReceived");
			}
			
			@Override
			public void onBeginningOfSpeech() {
				Log.d(TAG, "onBeginningOfSpeech");
			}
		});

		Button sendMoneyButton = (Button) findViewById(R.id.sendMoneyButton);
		sendMoneyButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Utility.toastShort(LandingPageActivity.this, Utility.USE_VAPP_FEATURE, true);
			}
		});

		Button requestMoneyButton = (Button) findViewById(R.id.requestMoneyButton);
		requestMoneyButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Utility.toastShort(LandingPageActivity.this, Utility.USE_VAPP_FEATURE, true);
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

		name = getIntent().getStringExtra(Utility.NAME_STRING);

		allContactDetails = (AllContactDetails) getIntent().getSerializableExtra(Utility.ALL_CONTACT_DETAILS_STRING); 
		if (allContactDetails != null
				&& allContactDetails.getAllContactDetails() != null
				&& allContactDetails.getAllContactDetails().size() > 0) {
			contactDetailsMap = allContactDetails.getAllContactDetails();
		} else {
			AsyncTask<String, Void, Map<String, ContactDetails>> contactRetrievalTask = new AsyncTask<String, Void, Map<String, ContactDetails>>() {
				// Aysnc Background Thread
				@Override
				protected Map<String, ContactDetails> doInBackground(String... params) {
					ContentResolver contentResolver = getContentResolver();
					contactDetailsMap = Utility.fetchContactsMap(contentResolver);
					if (contactDetailsMap != null
							&& contactDetailsMap.size() > 0) {
						Log.d(TAG, String.valueOf(contactDetailsMap.size()));	
					} else {
						Log.d(TAG, Utility.NO_CONTACTS_FOUND);
					}
					return contactDetailsMap;
				}

				// Main UI Thread before running
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					Utility.toastShort(LandingPageActivity.this, Utility.START_RETRIEVING_CONTACTS, false);
				}

				// UI Thread after running
				@Override
				protected void onPostExecute(final Map<String, ContactDetails> result) {
					super.onPostExecute(result);
					Utility.toastShort(LandingPageActivity.this, Utility.DONE_RETRIEVING_CONTACTS + result.size(), false);
				}
			};
			contactRetrievalTask.execute();
		}

		AsyncTask<String, Void, Map<String, TransactionDetails>> transactionsRetrievalTask = new AsyncTask<String, Void, Map<String,TransactionDetails>>() {
			// Aysnc Background Thread
			@Override
			protected Map<String, TransactionDetails> doInBackground(String... params) {
				transactionDetailsMap = TransactionsProvider.retrieveTransactions(getActivityInstance());
				return transactionDetailsMap;
			}

			// Main UI Thread before running
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				Utility.toastShort(LandingPageActivity.this, Utility.START_RETRIEVING_TRANSACTIONS, false);
			}

			// UI Thread after running
			@Override
			protected void onPostExecute(final Map<String, TransactionDetails> result) {
				super.onPostExecute(result);
				Utility.toastShort(LandingPageActivity.this, Utility.DONE_RETRIEVING_TRANSACTIONS + result.size(), false);
			}
		};
		transactionsRetrievalTask.execute();

		debugMode();
	}

	/**
	 * Handle Speech Request for different commands
	 * 
	 * @param inputString
	 */
	protected void handleSpeechRequest(final String inputString) {
		Utility.toastShort(LandingPageActivity.this, inputString, false);
		if (inputString.contains(Utility.COMMAND_HELP)
				|| inputString.contains(Utility.COMMAND_DONE)
				|| inputString.contains(Utility.COMMAND_CANCEL)
				|| inputString.contains(Utility.COMMAND_SEND)
				|| inputString.contains(Utility.COMMAND_REQUEST)) {
			if (inputString.contains(Utility.COMMAND_HELP)) {
				playMediaPlayer(R.raw.help_send_request);
			} else if (inputString.contains(Utility.COMMAND_SEND)) {
				Intent intent = new Intent(LandingPageActivity.this, SendMoneyActivity.class); 
				Utility.handleSendCommand(intent, inputString, name, contactDetailsMap);
				startActivity(intent);
			} else if (inputString.contains(Utility.COMMAND_REQUEST)) {
				Intent intent = new Intent(LandingPageActivity.this, RequestMoneyActivity.class);
				Utility.handleRequestCommand(intent, inputString, name, contactDetailsMap);
				startActivity(intent);
			} else if (inputString.contains(Utility.COMMAND_DONE) || inputString.contains(Utility.COMMAND_CANCEL)) {
				this.moveTaskToBack(true);
			}
		} else {
			playMediaPlayer(R.raw.help_send_request);
		}
		ImageButton speakImageButton = (ImageButton) findViewById(R.id.button);
		speakImageButton.setBackgroundResource(R.drawable.ic_microphone);
	}

	/**
	 * Handle VAPP Activation
	 */
	public void handleActivation() {
		debugMode();
		toSpeak = new StringBuilder();
		name = getIntent().getStringExtra(Utility.NAME_STRING);
		toSpeak.append("Hi " + name + ", what would you like to do today? Send Money or Request Money?");
		textToSpeech = new TextToSpeech(LandingPageActivity.this, new TextToSpeech.OnInitListener() {
		      @Override
		      public void onInit(int status) {
		    	  if(status == TextToSpeech.SUCCESS){
			            textToSpeech.setLanguage(Locale.US);
			            Utility.toastShort(LandingPageActivity.this, "SUCCESS IN SPEECH", false);
			            Utility.speakText(LandingPageActivity.this, textToSpeech, toSpeak.toString(), false);
		    	  } else {
		    		  Utility.toastShort(LandingPageActivity.this, "Status is " + String.valueOf(status), false);
			        }
			  }
		});
	}
}
