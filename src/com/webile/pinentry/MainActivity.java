package com.webile.pinentry;

import com.webile.widgets.PinEntryDialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	TextView pinText;
	
	private String pinStored;
	
	private MyPinEntryDelegate delegate;
	
	private static final int PIN_ENTRY_DIALOG = 0x1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        pinText = (TextView) findViewById(android.R.id.text1);
        delegate = new MyPinEntryDelegate();
    }
    
    public void createPin(View view) {
//    	Toast.makeText(this, "Create a new pin", Toast.LENGTH_SHORT).show();
    	delegate.setPinDialogMode(PinEntryDialog.MODE_CREATE);
    	showDialog(PIN_ENTRY_DIALOG);
    }
    
    public void verifyPin(View view) {
    	Toast.makeText(this, "Verify pin", Toast.LENGTH_SHORT).show();
    	if(pinStored == null || pinStored.length() == 0) {
    		Toast.makeText(this, "Please create a pin first", Toast.LENGTH_SHORT).show();
    	}
    	delegate.setPinDialogMode(PinEntryDialog.MODE_CREATE);
    	showDialog(PIN_ENTRY_DIALOG);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	return PinEntryDialog.getEntryDialog(this, delegate);
    }
    
    public class MyPinEntryDelegate implements PinEntryDialog.PinEntryDelegate {

    	private int pinDialogMode;
    	
    	public void setPinDialogMode(int pinDialogMode) {
			this.pinDialogMode = pinDialogMode;
		}
    	
		@Override
		public void didCreatePin(String createdPin) {
			pinStored = createdPin;
			pinText.setText(String.format("New pin created: %s",pinStored));
			dismissDialog(PIN_ENTRY_DIALOG);
		}

		@Override
		public void didEnterCorrectPin() {
	    	Toast.makeText(MainActivity.this, "Correct pin entered", Toast.LENGTH_SHORT).show();
			dismissDialog(PIN_ENTRY_DIALOG);

		}

		@Override
		public void didFailEnteringCorrectPin() {
	    	Toast.makeText(MainActivity.this, "Incorrect pin!", Toast.LENGTH_SHORT).show();	
			dismissDialog(PIN_ENTRY_DIALOG);

		}

		@Override
		public String getTitleForMode(int mode) {
			return null;
		}

		@Override
		public int getMode() {
			return pinDialogMode;
		}

		@Override
		public String getVerificationPin() {
			return pinStored;
		}

		@Override
		public void didFailToCreatePin() {
	    	Toast.makeText(MainActivity.this, "No new pin created", Toast.LENGTH_SHORT).show();
			dismissDialog(PIN_ENTRY_DIALOG);

		}
    }
}