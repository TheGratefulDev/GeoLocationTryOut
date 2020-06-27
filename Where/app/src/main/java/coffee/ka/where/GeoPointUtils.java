package coffee.ka.where;

import android.location.Location;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class GeoPointUtils {

	static Map<String, Object> cloudLatLng (Location location ) {
		GeoPoint geoPoint = new GeoPoint( location.getLatitude() , location.getLongitude());
		return cloudLatLng(geoPoint);
	}

	static Map<String, Object> cloudLatLng (GeoPoint geoPoint ) {
		HashMap<String, Object> map = new HashMap<>();
		map.put( Constant.FIELDS_NAME, geoPoint );
		return map;
	}

	static List<GeoPoint> getMockGeoPoint(){

		List<GeoPoint> mockGeoPoints = new ArrayList<>();

		mockGeoPoints.add(new GeoPoint(41.521415, -88.217779));
		mockGeoPoints.add(new GeoPoint(41.521415, -88.221604));
		mockGeoPoints.add(new GeoPoint(41.521269, -88.225720));
		mockGeoPoints.add(new GeoPoint(41.521318, -88.230808));
		mockGeoPoints.add(new GeoPoint(41.521275, -88.231230));
		mockGeoPoints.add(new GeoPoint(41.521275, -88.231230));
		mockGeoPoints.add(new GeoPoint(41.518454, -88.231197));
		mockGeoPoints.add(new GeoPoint(41.515906, -88.231943));
		mockGeoPoints.add(new GeoPoint(41.513940, -88.231943));
		mockGeoPoints.add(new GeoPoint(41.511173, -88.231910));
		mockGeoPoints.add(new GeoPoint(41.511173, -88.231910));
		mockGeoPoints.add(new GeoPoint(41.509572, -88.228961));
		mockGeoPoints.add(new GeoPoint(41.508310, -88.225817));
		mockGeoPoints.add(new GeoPoint(41.508310, -88.225817));
		mockGeoPoints.add(new GeoPoint(41.507023, -88.225525));
		mockGeoPoints.add(new GeoPoint(41.507023, -88.225525));
		mockGeoPoints.add(new GeoPoint(41.506853, -88.229528));
		mockGeoPoints.add(new GeoPoint(41.506756, -88.234163));
		mockGeoPoints.add(new GeoPoint(41.506756, -88.234163));

		return mockGeoPoints;
	}
}
