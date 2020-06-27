package coffee.ka.where;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.List;

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

	// create an action bar button
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
		// If you don't have res/menu, just create a directory named "menu" inside res
		getMenuInflater().inflate(R.menu.coffee_drinker_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	// handle button activities
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_demo) {

			List<GeoPoint> mockGeoPoints = GeoPointUtils.getMockGeoPoint();
			updateUI(mockGeoPoints.get(0));

			int i = 1;
			for (GeoPoint geoPoint : mockGeoPoints) {
				i++;
				Handler handler = new Handler();
				handler.postDelayed(() -> db.collection(Constant.COLLECTION_NAME)
						.document(Constant.DOCUMENT_ID)
						.update(GeoPointUtils.cloudLatLng(geoPoint)), 4000*i );
			}
		}

		return super.onOptionsItemSelected(item);
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
		if (driverMarker != null) {
			animateCar(newLocation);
			boolean contains = mGoogleMap.getProjection()
					.getVisibleRegion()
					.latLngBounds
					.contains(newLocation);
			if (!contains) {
				mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
			}
		} else {
			mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
					newLocation, 15.5f));
			driverMarker = mGoogleMap.addMarker(new MarkerOptions().position(newLocation).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.coffee_marker)));
		}
	}

	private void animateCar(final LatLng destination) {
		final LatLng startPosition = driverMarker.getPosition();
		final LatLng endPosition = new LatLng(destination.latitude, destination.longitude);
		final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();

		ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
		valueAnimator.setDuration(5000); // duration 5 seconds
		valueAnimator.setInterpolator(new LinearInterpolator());

		valueAnimator.addUpdateListener(animation -> {
			try {
				float v = animation.getAnimatedFraction();
				LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
				driverMarker.setPosition(newPosition);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});

		valueAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
			}
		});
		valueAnimator.start();
	}

	private interface LatLngInterpolator {
		LatLng interpolate(float fraction, LatLng a, LatLng b);

		class LinearFixed implements LatLngInterpolator {
			@Override
			public LatLng interpolate(float fraction, LatLng a, LatLng b) {
				double lat = (b.latitude - a.latitude) * fraction + a.latitude;
				double lngDelta = b.longitude - a.longitude;
				if (Math.abs(lngDelta) > 180) {
					lngDelta -= Math.signum(lngDelta) * 360;
				}
				double lng = lngDelta * fraction + a.longitude;
				return new LatLng(lat, lng);
			}
		}
	}


}
