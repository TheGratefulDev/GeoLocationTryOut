package coffee.ka.where;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

	Button coffeeDrinkerButton;
	Button driverButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		coffeeDrinkerButton = findViewById(R.id.coffee_drinker_btn);
		driverButton = findViewById(R.id.driver_btn);

		coffeeDrinkerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		driverButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});


	}
}
