package com.example.weatherdemo;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText cityEditText;
    private Button queryButton;
    private TextView weatherTextView;
    private OkHttpClient client;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        cityEditText = findViewById(R.id.cityEditText);
        queryButton = findViewById(R.id.queryButton);
        weatherTextView = findViewById(R.id.weatherTextView);

        client = new OkHttpClient();
        gson = new Gson();

        queryButton.setOnClickListener(v -> {
            String city = cityEditText.getText().toString().trim();
            if (!city.isEmpty()) {
                getWeatherData(city);
            } else {
                Toast.makeText(MainActivity.this, "请输入城市名称",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 获取当前城市的天气数据
     * @param city 城市
     */
    private void getWeatherData(String city) {
        String apiUrl =
                "https://api.caiyunapp.com/v2.6/qgJtgRLBY9jWj99P/101.6656,39.2072/weather?dailysteps=3&hourlysteps=48"
                + city;

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            // 网络请求失败
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "网络请求失败",
                        Toast.LENGTH_SHORT).show());
            }
            // 网络请求成功，返回不同的信息
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response)
                    throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    final String jsonData = response.body().string();
                    runOnUiThread(() -> {
                        WeatherData weatherData = gson.fromJson(jsonData, WeatherData.class);
                        if (weatherData != null) {
                            String weatherInfo = "城市：" + weatherData.getCity()
                                     + "\n天气：" + weatherData.getWeather()
                                     + "\n温度：" + weatherData.getTemperature() + "℃";
                            weatherTextView.setText(weatherInfo);
                        } else {
                            Toast.makeText(MainActivity.this, "数据解析失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this,
                            "请求失败，状态码：" + response.code(),
                            Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private static class WeatherData {
        private String city;
        private String weather;
        private String temperature;

        public String getCity() {
            return city;
        }

        public String getWeather() {
            return weather;
        }

        public String getTemperature() {
            return temperature;
        }
    }
}