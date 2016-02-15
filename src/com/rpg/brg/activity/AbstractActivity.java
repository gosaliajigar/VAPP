package com.rpg.brg.activity;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.rpg.brg.library.parent.AbstractParentActivity;
import com.rpg.brg.library.utility.Utility;
import com.rpg.brg.vapp.R;

/**
 * Abstract Activity to host common methods and members.
 * 
 * @author "Jigar Gosalia"
 *
 */
public class AbstractActivity extends AbstractParentActivity {

	/**
	 * Activate debugMode
	 */
	protected void debugMode() {
		Button commandButton = (Button) findViewById(R.id.commandButton);
		EditText enterCommandEditText = (EditText) findViewById(R.id.enterCommandEditText);
		EditText commandTextView = (EditText) findViewById(R.id.commandTextView);
		ImageButton micButton = (ImageButton) findViewById(R.id.button);
		Button sendMoneyButton = (Button) findViewById(R.id.sendMoneyButton);
		Button requestMoneyButton = (Button) findViewById(R.id.requestMoneyButton);

		EditText emailPhoneEditText = (EditText) findViewById(R.id.emailPhoneEditText);
		EditText amountEditText = (EditText) findViewById(R.id.amountEditText);
		EditText currencyEditText = (EditText) findViewById(R.id.currencyEditText);
		EditText messageEditText = (EditText) findViewById(R.id.messageEditText);

		Utility.debugMode(commandButton, enterCommandEditText, commandTextView, micButton, sendMoneyButton, requestMoneyButton, emailPhoneEditText, amountEditText, currencyEditText, messageEditText);
	}
}
