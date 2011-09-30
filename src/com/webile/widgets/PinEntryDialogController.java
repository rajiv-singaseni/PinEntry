package com.webile.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class PinEntryDialogController implements TextWatcher {

	public static final int MODE_CREATE = 0x1;
	public static final int MODE_CONFIRM = 0x10;
	public static final int MODE_VERIFY = 0X2;
	public static final String TAG = "PinEntryDialog";
	private int mMode;
	
	private AlertDialog mDialog;
	private PinEntryDelegate mDelegate;
	
    private EditText securePinEditText1;
    private EditText securePinEditText2;
    private EditText securePinEditText3;
    private EditText securePinEditText4;
    private TextView mHintTextView;
    
    private String enteredPin;
    private String pinToVerify;
    
    private PinEntryDialogController(Context context, PinEntryDelegate delegate) {
    	this.mDelegate = delegate;
    	View contentView = LayoutInflater.from(context).inflate(com.webile.pinentry.R.layout.pin_entry_content, null, false);
    	securePinEditText1 = (EditText) contentView.findViewById(android.R.id.text1);
    	securePinEditText2 = (EditText) contentView.findViewById(android.R.id.text2);
    	securePinEditText3 = (EditText) contentView.findViewById(android.R.id.button1);
    	securePinEditText4 = (EditText) contentView.findViewById(android.R.id.button2);
    	securePinEditText1.addTextChangedListener(this);
    	securePinEditText2.addTextChangedListener(this);
    	securePinEditText3.addTextChangedListener(this);
    	securePinEditText4.addTextChangedListener(this);
    	mHintTextView = (TextView) contentView.findViewById(android.R.id.hint);
    	mDialog = new AlertDialog.Builder(context).setView(contentView).setPositiveButton("OK", null).create();
    	delegate.setPinEntryDialogController(this);
        securePinEditText1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                	mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
    }
    
	public static AlertDialog getEntryDialog(Context context, PinEntryDelegate delegate) {
		PinEntryDialogController d = new PinEntryDialogController(context, delegate);
		return d.mDialog;
	}
    
    @Override
    public void afterTextChanged(Editable s) {
    	
    	if (securePinEditText1.getText().length() == 0)
    		securePinEditText1.requestFocus();
    	else if (securePinEditText2.getText().length() == 0)
    		securePinEditText2.requestFocus();
    	else if (securePinEditText3.getText().length() == 0)
    		securePinEditText3.requestFocus();
    	else if (securePinEditText4.getText().length() == 0)
    		securePinEditText4.requestFocus();
    	else {
    		String pinEntered = String.format("%c%c%c%c", 
    				securePinEditText1.getText().charAt(0),
    				securePinEditText2.getText().charAt(0),
    				securePinEditText3.getText().charAt(0),
    				securePinEditText4.getText().charAt(0));
			enteredAllDigits(pinEntered);
		}
    }
    
    private void enteredAllDigits(String pinEntered) {
    	Log.v("PE","enteredDigits mode:"+mMode);
    	if(mDelegate.getMode() == MODE_VERIFY) {
    		mMode = MODE_VERIFY;
    		pinToVerify = mDelegate.getVerificationPin();
    	}
    	if(mMode == MODE_CREATE) {
    		this.enteredPin = pinEntered;
    		reset(MODE_CONFIRM);
    		//reset the titles.
    	} else if (mMode == MODE_CONFIRM) {
    		if(this.enteredPin.equalsIgnoreCase(pinEntered)) {
    			//Successfully confirmed pin
    			mDelegate.didCreatePin(pinEntered);
    		} else {
    			//Entered incorrect pin while confirmation
    			mDelegate.didFailToCreatePin();
    		}
    		mMode = MODE_CREATE;
    	} else if (mMode == MODE_VERIFY) {
    		if(pinToVerify.equalsIgnoreCase(pinEntered)) {
    			//Successfully verified
    			mDelegate.didEnterCorrectPin();
    		} else {
    			//Entered incorrect 
    			mDelegate.didFailEnteringCorrectPin();
    		}
    	}
    	securePinEditText1.setText("");
    	securePinEditText2.setText("");
    	securePinEditText3.setText("");
    	securePinEditText4.setText("");
    }
    
    /**
     * Reset the dialog textfields and adjust the mode.
     * @param mode
     */
	private void reset(int mode) {
		mMode = mDelegate.getMode();
		
		if (mode == MODE_CONFIRM) {
			mMode = MODE_CONFIRM;
		} else {

			if (mMode == MODE_CREATE) {
				enteredPin = "";
			} else if (mMode == MODE_VERIFY) {
				pinToVerify = mDelegate.getVerificationPin();
			}
		}
		// set the title and message based on the mode.
		mHintTextView.setText(mDelegate.getTitleForMode(mMode));
	}
	
	public void reset() {
		//TODO: Throw an exception if the mode is not one of MODE_CREATE or MODE_VERIFY
		reset(mDelegate.getMode());
	}
    
	public interface PinEntryDelegate {
		
		//callbacks for creation
		public void didCreatePin(String createdPin);
		
		public void didFailToCreatePin();
		
		//callbacks for verification
		public void didEnterCorrectPin();
		
		public void didFailEnteringCorrectPin();
		
		//mode and pin details
		
		/**
		 * Should be one of {@link PinEntryDialogController#MODE_CREATE} or {@link PinEntryDialogController#MODE_VERIFY}
		 */
		public int getMode();
		
		/**
		 * The string user has to verify. This function is called when {@link PinEntryDialogController.PinEntryDelegate#getMode()} returns {@link PinEntryDialogController#MODE_VERIFY}
		 * @return
		 */
		public String getVerificationPin();
		
		//titles and styling
		public String getTitleForMode(int mode);
		
		//handling dialog controller changes.
		public void setPinEntryDialogController(PinEntryDialogController dialogController);
	
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
}