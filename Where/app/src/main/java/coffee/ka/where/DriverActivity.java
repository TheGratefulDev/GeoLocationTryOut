package coffee.ka.where;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class DriverActivity extends AppCompatActivity {

	//to receive location updates
	private FusedLocationProviderClient fusedLocationProviderClient;

	//defines important parameters regarding location request
	private LocationRequest locationRequest;

	private TextView driverTextView;
	private ImageView steeringWheelImageView;

	private FirebaseFirestore db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver);

		db = FirebaseFirestore.getInstance();

		if(getSupportActionBar() != null){
			getSupportActionBar().setTitle(R.string.driver_action_bar_text);
		}

		driverTextView = findViewById(R.id.driver_tv);
		steeringWheelImageView = findViewById(R.id.steering_wheel_iv);

		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

		//https://developers.google.com/android/reference/com/google/android/gms/location/LocationRequest
		locationRequest = LocationRequest.create();
		locationRequest.setInterval(5000);
		locationRequest.setFastestInterval(5000);
		locationRequest.setSmallestDisplacement(10);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		sendUpdatedLocationToCloud();
		//Send Notification To Cloud every 5 seconds.
	}

	private void sendUpdatedLocationToCloud() {
		try {
			fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
				@Override
				public void onLocationResult(LocationResult locationResult) {
					super.onLocationResult(locationResult);

					Location location = locationResult.getLastLocation();
					driverTextView.setText(MessageFormat.format("{0},{1}", location.getLatitude(), location.getLongitude()));

					steeringAnimation();

				 	db.collection(Constant.COLLECTION_NAME)
						    .document(Constant.DOCUMENT_ID)
						    .update( GeoPointUtils.cloudLatLng(location) );

				}

			}, Looper.myLooper());

		}catch (Exception e){
			e.printStackTrace();
		}
	}


	private void steeringAnimation(){

		int degree = 360;
		if( Math.random() < 0.5){
			degree = -360;
		}

		RotateAnimation animation = new RotateAnimation(0, degree, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setRepeatCount(1);
		animation.setDuration(1500);
		steeringWheelImageView.startAnimation(animation);
	}
}
