package com.rpg.brg.activity;

import java.util.ArrayList;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.TextView;

import com.rpg.brg.library.model.AllContactDetails;
import com.rpg.brg.library.utility.Utility;
import com.rpg.brg.vapp.R;

/**
 * Review Activity to review activities in an account
 * 
 * @author "Jigar Gosalia"
 *
 */
public class ReviewActivity extends AbstractActivity {

	private static final String TAG = ReviewActivity.class.getCanonicalName();

	private CharSequence financialInstrumentArray[] = financialInstrumentList.toArray(new CharSequence[financialInstrumentList.size()]);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_review);

		name = getIntent().getStringExtra(Utility.NAME_STRING);

		allContactDetails = (AllContactDetails) getIntent().getSerializableExtra(Utility.ALL_CONTACT_DETAILS_STRING);
		if (allContactDetails != null) {
			Utility.toastShort(ReviewActivity.this, "Contacts Size is : " + allContactDetails.getAllContactDetails().size(), false);
		}

		commandTextView = (EditText) findViewById(R.id.commandTextView);

		handleFinancialOptions();

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

		speechRecognizer = SpeechRecognizer.createSpeechRecognizer(ReviewActivity.this);
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

		TextView feesTextView = (TextView) findViewById(R.id.feesTextView);
		feesTextView.setText(Utility.FAMILY_AND_FRIENDS_MESSAGE);

		debugMode();
	}

	/**
	 * 
	 * Calculate amount from the given string
	 * 
	 * @param paymentOption
	 * @param rawAmount
	 */
	protected void calculateAmounts(final EditText paymentOption, final String rawAmount) {
		String amount = rawAmount.replace(Utility.DOLLAR_STRING, "");
		if (Utility.dataExists(paymentOption)
				&& Utility.isNumber(amount)) {
			String option = paymentOption.getText().toString();
			Double amountValue = Double.parseDouble(amount);
			Double fees = 0.0;
			Double totalAmount = 0.0;
			EditText actualAmountEditText = (EditText) findViewById(R.id.actualAmountEditText);
			actualAmountEditText.setText(Utility.DOLLAR_STRING + Utility.decimalFormat.format(amountValue) + Utility.BLANK_STRING);
			EditText actualFeesEditText = (EditText) findViewById(R.id.actualFeesEditText);
			EditText actualTotalEditText = (EditText) findViewById(R.id.actualTotalEditText);
			if (option.contains(Utility.BANK_STRING)) {
				fees = 0.0;
			} else if (option.contains(Utility.CARD_STRING)) {
				fees = (amountValue * 3.2)/100;
			}
			actualFeesEditText.setText(Utility.DOLLAR_STRING + Utility.decimalFormat.format(fees) + Utility.BLANK_STRING);
			totalAmount = amountValue + fees;
			actualTotalEditText.setText(Utility.DOLLAR_STRING + Utility.decimalFormat.format(totalAmount) + Utility.BLANK_STRING);

			toSpeak = new StringBuilder();

			String receiver = getIntent().getStringExtra(Utility.TO_STRING);
			String message = getIntent().getStringExtra(Utility.MESSAGE_STRING);
			if (message != null
					&& message.length() > 0) {
				toSpeak.append("Do you confirm that you would like to send " + Utility.DOLLAR_STRING + Utility.decimalFormat.format(totalAmount) + " to " + receiver + " with message " + message + "?");
			} else {
				toSpeak.append("Do you confirm that you would like to send " + Utility.DOLLAR_STRING + Utility.decimalFormat.format(totalAmount) + " to " + receiver + "?");
			}
			textToSpeech = new TextToSpeech(ReviewActivity.this, new TextToSpeech.OnInitListener() {
			      @Override
			      public void onInit(int status) {
			    	  if(status == TextToSpeech.SUCCESS){
				          textToSpeech.setLanguage(Locale.US);
				          Utility.toastShort(ReviewActivity.this, "SUCCESS IN SPEECH", false);
				          Utility.speakText(ReviewActivity.this, textToSpeech, toSpeak.toString(), false);
			    	  } else {
			    		  Utility.toastShort(ReviewActivity.this, "Status is " + String.valueOf(status), false);
				        }
				  }
			});
		}
	}

	/**
	 * Handle Financial Options
	 */
	protected void handleFinancialOptions() {
		toSpeak = new StringBuilder();

		String receiver = getIntent().getStringExtra(Utility.TO_STRING);
		String amount = getIntent().getStringExtra(Utility.AMOUNT_STRING);
		if (receiver != null
				&& receiver.length() > 0
				&& !receiver.toLowerCase(Locale.US).contains("john")) {
			toSpeak.append("Only one Payment Options found ... ").append(Utility.BLANK_STRING);
			toSpeak.append(financialInstrumentList.get(0));
			EditText paymentOptionsEditText = (EditText) findViewById(R.id.paymentOptionsEditText);
			paymentOptionsEditText.setText(financialInstrumentList.get(0));
			calculateAmounts(paymentOptionsEditText, amount);
		} else {
			toSpeak.append(financialInstrumentArray.length + " Payment Options found .... ").append(Utility.BLANK_STRING);
			for (CharSequence financialInstrument : financialInstrumentArray) {
				toSpeak.append(financialInstrument).append(Utility.BLANK_STRING);
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(financialInstrumentArray.length + " Payment Options found .... ");
			builder.setItems(financialInstrumentArray, new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
					EditText paymentOptionsEditText = (EditText) findViewById(R.id.paymentOptionsEditText);
			    	ListView listView = ((AlertDialog)dialog).getListView();
			    	String view = (String) listView.getAdapter().getItem(which);
			    	Utility.toastShort(ReviewActivity.this, "Single Selected instrument is : " + view, false);
					paymentOptionsEditText.setText(view);
					String amount = getIntent().getStringExtra(Utility.AMOUNT_STRING);
					calculateAmounts(paymentOptionsEditText, amount);
			    }
			});
			builder.show();
		}

		textToSpeech = new TextToSpeech(ReviewActivity.this, new TextToSpeech.OnInitListener() {
		      @Override
		      public void onInit(int status) {
		    	  if(status == TextToSpeech.SUCCESS){
			          textToSpeech.setLanguage(Locale.US);
			          Utility.toastShort(ReviewActivity.this, "SUCCESS IN SPEECH", false);
			          Utility.speakText(ReviewActivity.this, textToSpeech, toSpeak.toString(), false);
		    	  } else {
		    		  Utility.toastShort(ReviewActivity.this, "Status is " + String.valueOf(status), false);
			      }
			  }
		});
	}

	/**
	 * Handle Speech Request for different commands
	 * 
	 * @param inputString
	 */
	protected void handleSpeechRequest(final String inputString) {

		toSpeak = new StringBuilder();

		if (inputString != null
				&& (inputString.contains(Utility.COMMAND_HELP)
				|| inputString.contains(Utility.COMMAND_CONFIRM)
				|| inputString.contains(Utility.COMMAND_OPTIONS)
				|| inputString.contains(Utility.COMMAND_CANCEL)
				|| inputString.contains(Utility.COMMAND_BACK))) {
			if (inputString.contains(Utility.COMMAND_HELP)) {
				playMediaPlayer(R.raw.help_confirm_back_cancel_options);
			} else if (inputString.contains(Utility.COMMAND_CONFIRM)) {
				String activity = getIntent().getStringExtra(Utility.ACTIVITY_STRING);
				Intent intent = new Intent(ReviewActivity.this, ConfirmActivity.class);
				intent.putExtra(Utility.ACTIVITY_STRING, activity);
				String receiver = getIntent().getExtras().getString(Utility.TO_STRING);
				intent.putExtra(Utility.TO_STRING, receiver);
				EditText actualTotalEditText = (EditText) findViewById(R.id.actualTotalEditText);
				String amount = actualTotalEditText.getText().toString();
				intent.putExtra(Utility.AMOUNT_STRING, amount);
				EditText paymentOptionsEditText = (EditText) findViewById(R.id.paymentOptionsEditText);
				StringBuilder selectedFinancialInstrument = new StringBuilder();
				if (paymentOptionsEditText.getText().toString().contains(Utility.BANK_STRING)) {
					selectedFinancialInstrument.append("Bank account ending in ").append(financialInstrumentMap.get(paymentOptionsEditText.getText().toString()));
				} else if (paymentOptionsEditText.getText().toString().contains(Utility.CARD_STRING)) {
					selectedFinancialInstrument.append("Card ending in ").append(financialInstrumentMap.get(paymentOptionsEditText.getText().toString()));
				}
				String transactionDetails = "You have successfully send " + amount + " to " + receiver;
				String message = getIntent().getExtras().getString(Utility.MESSAGE_STRING);
				if (message != null
						&& message.length() > 0) {
					transactionDetails = "You have successfully send " + amount + " to " + receiver + " with message " + message;
					intent.putExtra(Utility.MESSAGE_STRING, message);
				}
				intent.putExtra(Utility.TRANSACTION_DETAILS_STRING, transactionDetails);
				Utility.toastShort(ReviewActivity.this, "Sending " + amount + " to " + receiver, false);
				intent.putExtra(Utility.ALL_CONTACT_DETAILS_STRING, allContactDetails);
				intent.putExtra(Utility.NAME_STRING, name);
				startActivity(intent);
			} else if (inputString.contains(Utility.COMMAND_CANCEL)) {
				Intent intent = new Intent(ReviewActivity.this, LandingPageActivity.class);
				intent.putExtra(Utility.ALL_CONTACT_DETAILS_STRING, allContactDetails);
				intent.putExtra(Utility.NAME_STRING, name);
				startActivity(intent);
			} else if (inputString.contains(Utility.COMMAND_BACK)) {
				Intent intent = new Intent(ReviewActivity.this, SendMoneyActivity.class);
				intent.putExtra(Utility.ALL_CONTACT_DETAILS_STRING, allContactDetails);
				intent.putExtra(Utility.NAME_STRING, name);
				startActivity(intent);
			} else if (inputString.contains(Utility.COMMAND_OPTIONS)) {
				handleFinancialOptions();
			}
		} else {
			playMediaPlayer(R.raw.help_confirm_back_cancel_options);
		}
		ImageButton speakImageButton = (ImageButton) findViewById(R.id.button);
		speakImageButton.setBackgroundResource(R.drawable.ic_microphone);
	}
}
