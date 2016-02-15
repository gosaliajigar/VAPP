package com.rpg.brg.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.rpg.brg.library.model.AllContactDetails;
import com.rpg.brg.library.utility.Utility;
import com.rpg.brg.vapp.R;

/**
 * Send Money activity to send money
 * 
 * @author "Jigar Gosalia"
 *
 */
public class SendMoneyActivity extends AbstractActivity {

	private static final String TAG = SendMoneyActivity.class.getCanonicalName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sendmoney);

		commandTextView = (EditText) findViewById(R.id.commandTextView);

		activity = getIntent().getStringExtra(Utility.ACTIVITY_STRING);

		name = getIntent().getStringExtra(Utility.NAME_STRING);

		allContactDetails = (AllContactDetails) getIntent().getSerializableExtra(Utility.ALL_CONTACT_DETAILS_STRING);
		if (allContactDetails != null) {
			Utility.toastShort(SendMoneyActivity.this, "Contacts Size is : " + allContactDetails.getAllContactDetails().size(), false);
		}

		receiver = getIntent().getStringExtra(Utility.TO_STRING);
		EditText emailPhoneEditText = (EditText) findViewById(R.id.emailPhoneEditText);
		handleNameCheck(SendMoneyActivity.this, allContactDetails, receiver, activity, emailPhoneEditText);

		amount = getIntent().getStringExtra(Utility.AMOUNT_STRING);
		EditText amountEditText = (EditText) findViewById(R.id.amountEditText);
		amountEditText.setText(amount);

		message = getIntent().getStringExtra(Utility.MESSAGE_STRING);
		EditText messageEditText = (EditText) findViewById(R.id.messageEditText);
		messageEditText.setText(message);

		paymentType = getIntent().getStringExtra(Utility.PAYMENT_TYPE_STRING);
		handlePaymentType(paymentType, false);


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

		speechRecognizer = SpeechRecognizer.createSpeechRecognizer(SendMoneyActivity.this);
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

		RadioButton radioButtonFamilyFriends = (RadioButton) findViewById(R.id.radioFamilyFriends);
		radioButtonFamilyFriends.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				TextView feesTextView = (TextView) findViewById(R.id.feesTextView);
				feesTextView.setText(Utility.FAMILY_AND_FRIENDS_MESSAGE);
				playMediaPlayer(R.raw.fees_family_friends);
			}
		});

		RadioButton radioButtonGoodsServices = (RadioButton) findViewById(R.id.radioGoodsServices);
		radioButtonGoodsServices.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				TextView feesTextView = (TextView) findViewById(R.id.feesTextView);
				feesTextView.setText(Utility.GOODS_AND_SERVICES_MESSAGE);
				playMediaPlayer(R.raw.fees_goods_services);
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
	 * @param inputString
	 */
	protected void handleSpeechRequest(final String inputString) {
		if (inputString != null
				&& (inputString.contains(Utility.COMMAND_HELP)
				|| inputString.contains(Utility.COMMAND_SEND)
				|| inputString.contains(Utility.COMMAND_BACK)
				|| inputString.contains(Utility.COMMAND_CANCEL)
				|| inputString.contains(Utility.COMMAND_CLEAR)
				|| inputString.contains(Utility.COMMAND_RESET)
				|| inputString.contains(Utility.COMMAND_MESSAGE)
				|| inputString.contains(Utility.COMMAND_NAME)
				|| inputString.contains(Utility.COMMAND_AMOUNT)
				|| inputString.contains(Utility.COMMAND_PAYMENT))) {
			if (inputString.contains(Utility.COMMAND_HELP)) {
				playMediaPlayer(R.raw.help_send_back_cancel_clear_reset_name_amount_message);
			} else if (inputString.contains(Utility.PATTERN_SEND_STRING)) {
				Utility.handleSendCommand(getIntent(), inputString, name, contactDetailsMap);
				String rawName = getIntent().getStringExtra(Utility.TO_STRING);
				EditText emailPhoneEditText = (EditText) findViewById(R.id.emailPhoneEditText);
				handleNameCheck(SendMoneyActivity.this, allContactDetails, rawName, activity, emailPhoneEditText);
				EditText amountEditText = (EditText) findViewById(R.id.amountEditText);
				amountEditText.setText(getIntent().getStringExtra(Utility.AMOUNT_STRING));
				EditText messageEditText = (EditText) findViewById(R.id.messageEditText);
				messageEditText.setText(getIntent().getStringExtra(Utility.MESSAGE_STRING));
				handlePaymentType(getIntent().getStringExtra(Utility.PAYMENT_TYPE_STRING), false);
			} else if (inputString.contains(Utility.COMMAND_SEND)) {
				EditText emailPhoneEditText = (EditText) findViewById(R.id.emailPhoneEditText);
				EditText amountEditText = (EditText) findViewById(R.id.amountEditText);
				if (Utility.checkMandatoryValues(emailPhoneEditText, amountEditText)
						&& isRadioButtonSelected()) {
					if (Utility.validateContact(emailPhoneEditText, allContactDetails)) {
						Intent intent = new Intent(SendMoneyActivity.this, ReviewActivity.class);
						intent.putExtra(Utility.ACTIVITY_STRING, Utility.COMMAND_SEND);
						String receiver = emailPhoneEditText.getText().toString();
						intent.putExtra(Utility.TO_STRING, receiver);
						String amount = amountEditText.getText().toString();
						intent.putExtra(Utility.AMOUNT_STRING, amount);
						EditText messageEditText = (EditText) findViewById(R.id.messageEditText);
						if (Utility.dataExists(messageEditText)) {
							String message = messageEditText.getText().toString();
							intent.putExtra(Utility.MESSAGE_STRING, message);
						}
						Utility.toastShort(SendMoneyActivity.this, "Sending " + amount + " to " + receiver, false);
						intent.putExtra(Utility.PAYMENT_TYPE_STRING, paymentType);
						intent.putExtra(Utility.ALL_CONTACT_DETAILS_STRING, allContactDetails);
						intent.putExtra(Utility.NAME_STRING, name);
						startActivity(intent);
					} else {
						playMediaPlayer(R.raw.contact_doesnt_exists);
					}
				} else {
					playMediaPlayer(R.raw.check_name_amount_payment);
				}
			} else if (inputString.contains(Utility.COMMAND_BACK) || inputString.contains(Utility.COMMAND_CANCEL)) {
				Intent intent = new Intent(SendMoneyActivity.this, LandingPageActivity.class);
				intent.putExtra(Utility.ALL_CONTACT_DETAILS_STRING, allContactDetails);
				intent.putExtra(Utility.NAME_STRING, name);
				startActivity(intent);
			} else if (inputString.contains(Utility.COMMAND_MESSAGE)) {
				EditText editText = (EditText) findViewById(R.id.messageEditText);
				editText.setText(inputString.replace(Utility.COMMAND_MESSAGE, Utility.EMPTY_STRING));
			} else if (inputString.contains(Utility.COMMAND_CLEAR) || inputString.contains(Utility.COMMAND_RESET)) {
				EditText emailPhoneEditText = (EditText) findViewById(R.id.emailPhoneEditText);
				emailPhoneEditText.setText(Utility.EMPTY_STRING);
				EditText amountEditText = (EditText) findViewById(R.id.amountEditText);
				amountEditText.setText(Utility.EMPTY_STRING);
				EditText messageEditText = (EditText) findViewById(R.id.messageEditText);
				messageEditText.setText(Utility.EMPTY_STRING);
			} else if (inputString.contains(Utility.COMMAND_NAME)) {
				String name = inputString.replace(Utility.PATTERN_NAME_STRING, Utility.EMPTY_STRING);
				EditText editText = (EditText) findViewById(R.id.emailPhoneEditText);
				editText.setText(name);
				EditText emailPhoneEditText = (EditText) findViewById(R.id.emailPhoneEditText);
				handleNameCheck(SendMoneyActivity.this, allContactDetails, name, activity, emailPhoneEditText);
			} else if (inputString.contains(Utility.COMMAND_AMOUNT)) {
				if (inputString != null
						&& inputString.length() > 0
						&& Utility.isNumber(inputString.replace(Utility.PATTERN_AMOUNT_STRING, Utility.EMPTY_STRING))) {
					EditText editText = (EditText) findViewById(R.id.amountEditText);
					editText.setText(inputString.replace(Utility.PATTERN_AMOUNT_STRING, Utility.EMPTY_STRING));
				} else {
					playMediaPlayer(R.raw.repeat_amount);
				}
			} else if (inputString.contains(Utility.COMMAND_PAYMENT)) {
				handlePaymentType(inputString, true);
			}
		} else {
			playMediaPlayer(R.raw.help_send_back_cancel_clear_reset_name_amount_message);
		}
		ImageButton speakImageButton = (ImageButton) findViewById(R.id.button);
		speakImageButton.setBackgroundResource(R.drawable.ic_microphone);
	}

	/**
	 * Handle different payment types
	 * 
	 * @param inputString
	 * @param speakFeesText
	 */
	private void handlePaymentType(String inputString, final boolean speakFeesText) {
		TextView feesTextView = (TextView) findViewById(R.id.feesTextView);
		if (inputString != null) {
			if (inputString.contains(Utility.COMMAND_FAMILY) || inputString.contains(Utility.COMMAND_FRIEND)) {
				RadioButton radioButtonFamilyFriends = (RadioButton) findViewById(R.id.radioFamilyFriends);
				radioButtonFamilyFriends.setChecked(true);
				feesTextView.setText(Utility.FAMILY_AND_FRIENDS_MESSAGE);
				// playMediaPlayer(R.raw.fees_family_friends);
			} else if (inputString.contains(Utility.COMMAND_GOOD) || inputString.contains(Utility.COMMAND_SERVICE)) {
				RadioButton radioButtonGoodsServices = (RadioButton) findViewById(R.id.radioGoodsServices);
				radioButtonGoodsServices.setChecked(true);
				feesTextView.setText(Utility.GOODS_AND_SERVICES_MESSAGE);
				// playMediaPlayer(R.raw.fees_goods_services);
			} else {
				playMediaPlayer(R.raw.payment_keywords);
			}
		}
	}

	/**
	 * Is either of Payment radio buttons selected
	 * 
	 * @return
	 */
	private boolean isRadioButtonSelected() {
		RadioButton radioButtonFamilyFriends = (RadioButton) findViewById(R.id.radioFamilyFriends);
		RadioButton radioButtonGoodsServices = (RadioButton) findViewById(R.id.radioGoodsServices);
		if (radioButtonFamilyFriends.isChecked() || radioButtonGoodsServices.isChecked()) {
			return true;
		}
		return false;
	}
}
