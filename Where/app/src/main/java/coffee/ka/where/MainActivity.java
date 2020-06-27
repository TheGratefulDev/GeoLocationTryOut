package coffee.ka.where;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.tbruyelle.rxpermissions2.RxPermissions;

public class MainActivity extends AppCompatActivity {

	Button coffeeDrinkerButton;
	Button driverButton;
	RxPermissions rxPermissions;
	FirebaseFirestore db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		db = FirebaseFirestore.getInstance();

		coffeeDrinkerButton = findViewById(R.id.coffee_drinker_btn);
		driverButton = findViewById(R.id.driver_btn);

		rxPermissions = new RxPermissions(this);

		coffeeDrinkerButton.setVisibility(View.GONE);
		driverButton.setVisibility(View.GONE);

		runTimePermissionCheck();

		coffeeDrinkerButton.setOnClickListener(v -> {
			Intent coffeeDrinkerIntent = new Intent(MainActivity.this, CoffeeDrinkerActivity.class);
			MainActivity.this.startActivity(coffeeDrinkerIntent);

		});

		driverButton.setOnClickListener(v -> {
			Intent driverIntent = new Intent(MainActivity.this, DriverActivity.class);
			MainActivity.this.startActivity(driverIntent);
		});
	}

	@SuppressLint("CheckResult")
	public void runTimePermissionCheck() {
		rxPermissions
				.requestEach(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
				.subscribe(permission -> { // will emit 2 Permission objects
					if (permission.granted) {

						coffeeDrinkerButton.setVisibility(View.VISIBLE);
						driverButton.setVisibility(View.VISIBLE);

					} else if (permission.shouldShowRequestPermissionRationale) {
						// Denied permission without ask never again
						Toast.makeText(MainActivity.this, "Denied permission without ask never again",
								Toast.LENGTH_SHORT).show();
					} else {
						// Denied permission with ask never again
						// Need to go to the settings
						Toast.makeText(MainActivity.this, "Permission denied, can't enable the Location",
								Toast.LENGTH_SHORT).show();
					}
				});
	}
}
