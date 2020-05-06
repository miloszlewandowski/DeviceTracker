package com.example.trackerv2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class AuthorizationActivity extends AppCompatActivity {

	private Button loginButton;
	private EditText editText1, editText2;
	private String userMasterLogin;
	private String userSlaveLogin;
	private String userMasterPassword;
	private String userSlavePassword;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		setContentView(R.layout.activity_authorization);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		loginButton = (Button)findViewById(R.id.button);
		editText1 = (EditText)findViewById(R.id.editText);
		editText2 = (EditText)findViewById(R.id.editText2);
		userMasterLogin = getString(R.string.master_login);
		userMasterPassword = getString(R.string.master_password);
		userSlaveLogin = getString(R.string.tracked_login);
		userSlavePassword = getString(R.string.tracked_password);

		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (editText1.getText().toString().equals(userMasterLogin) && editText2.getText().toString().equals(userMasterPassword)) {
					hideKeyboard(AuthorizationActivity.this);
					startReceiverActivity();
					Toast.makeText(getApplicationContext(), "Admin logged",Toast.LENGTH_SHORT).show();
				} else if(editText1.getText().toString().equals(userSlaveLogin) && editText2.getText().toString().equals(userSlavePassword)) {
					hideKeyboard(AuthorizationActivity.this);
					startTransmitterActivity();
					Toast.makeText(getApplicationContext(), "User logged",Toast.LENGTH_SHORT).show();

				} else {
					Toast.makeText(getApplicationContext(), "Wrong Credentials",Toast.LENGTH_SHORT).show();
				}
			}
		});

	}


	private void startReceiverActivity() {

		startActivity(new Intent(this, ReceiverActivity.class));

		Toast.makeText(this, "ReceiverActivity started", Toast.LENGTH_SHORT).show();

		//Close main activity
		finish();
	}

	private void startTransmitterActivity() {

		startActivity(new Intent(this, TransmitterActivity.class));

		Toast.makeText(this, "TransmitterActivity started", Toast.LENGTH_SHORT).show();

		//Close main activity
		finish();
	}

	public static void hideKeyboard(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		//Find the currently focused view, so we can grab the correct window token from it.
		View view = activity.getCurrentFocus();
		//If no view currently has focus, create a new one, just so we can grab a window token from it
		if (view == null) {
			view = new View(activity);
		}
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
