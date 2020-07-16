package coffee.ka.where;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.android.SphericalUtil;

public class CoffeeDrinkerActivity extends AppCompatActivity implements OnMapReadyCallback {

	private MapView mapView;
	private GoogleMap mGoogleMap;
	private Marker driverMarker; // Marker to display driver's location

	private FirebaseFirestore db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_coffee_drinker);
		db = FirebaseFirestore.getInstance();

		if(getSupportActionBar() != null){
			getSupportActionBar().setTitle(R.string.coffee_drinker_action_bar_text);
		}

		mapView = findViewById(R.id.mapView);
		mapView.onCreate(savedInstanceState);
		mapView.getMapAsync(this);


		//use google map fragment
		//load map
		//connect to cloud
		//subscribe and then on update return...

	}



	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.onLowMemory();
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		try {
			mGoogleMap = googleMap;
			mGoogleMap.setMyLocationEnabled(true);
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		db.collection(Constant.COLLECTION_NAME)
				.document(Constant.DOCUMENT_ID)
				.addSnapshotListener((documentSnapshot, e) -> {

			if(e != null){
				e.printStackTrace();
				return;
			}

			if (documentSnapshot != null) {
				GeoPoint geoPoint = documentSnapshot.getGeoPoint(Constant.FIELDS_NAME);
				if (geoPoint != null) {
					updateUI(geoPoint);
				}
			}
		});
	}

	private void updateUI(GeoPoint geoPoint) {
		LatLng newLocation = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
		mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom( newLocation, 15.5f));

		if(driverMarker == null ){
			driverMarker = mGoogleMap.addMarker(new MarkerOptions().position(newLocation). icon(BitmapDescriptorFactory.fromResource(R.drawable.coffee_marker)));
		}

		animateCar(newLocation);
		boolean contains = mGoogleMap.getProjection()
				.getVisibleRegion()
				.latLngBounds
				.contains(newLocation);
		if (!contains) {
			mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
		}

	}

	private void animateCar(final LatLng destination) {
		final LatLng startPosition = driverMarker.getPosition();
		final LatLng endPosition = new LatLng(destination.latitude, destination.longitude);

		ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
		valueAnimator.setDuration(5000); // duration 5 seconds
		valueAnimator.setInterpolator(new LinearInterpolator());

		valueAnimator.addUpdateListener(animation -> {
			try {
				float v = animation.getAnimatedFraction();
				LatLng newPosition =  SphericalUtil.interpolate( startPosition, endPosition, v);
				driverMarker.setPosition(newPosition);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});

		/*


		valueAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);

			}
		});

		 */
		valueAnimator.start();
	}
}
