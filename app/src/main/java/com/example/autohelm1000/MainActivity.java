package com.example.autohelm1000;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Debug;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.view.InputDeviceCompat;
import androidx.core.view.MotionEventCompat;

import com.example.autohelm1000.databinding.ActivityMainBinding;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends Activity  {

    private TextView mTextView;
    private Button mApply;
    private TextView mLeftArrow;
    private TextView mRightArrow;
    private TextView mHeart;
    private ActivityMainBinding binding;
    private int relativeCorrection = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mTextView = binding.relativeBearing;
        mLeftArrow = binding.mLeftArrow;
        mRightArrow = binding.mRightArrow;
        mHeart = binding.mHeart;

        mApply = binding.applyCorrection;
        updateUI();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.100/")
                .build();

        mApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Autohelm1000Api api = retrofit.create(Autohelm1000Api.class);
                api.setRelativeBearing(relativeCorrection).enqueue(new Callback<ResponseBody>()
                {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        mHeart.setText("OK");
                        relativeCorrection = 0;
                        updateUI();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        mHeart.setText("Not OK");
                    }
                });
            }
        });

    }

    public void updateUI()
    {
        // Swap these axes to scroll horizontally instead
        mTextView.setText(String.format("%03d", Math.abs(relativeCorrection) ));
        if (relativeCorrection > 0)
        {
            mTextView.setTextColor(0xFF00FF00);
            mLeftArrow.setVisibility(View.INVISIBLE);
            mRightArrow.setVisibility(View.VISIBLE);
        }
        else if (relativeCorrection < 0)
        {
            mTextView.setTextColor(0xFFFF0000);
            mLeftArrow.setVisibility(View.VISIBLE);
            mRightArrow.setVisibility(View.INVISIBLE);
        }
        else
        {
            mTextView.setTextColor(0xFFFFFFFF);
            mLeftArrow.setVisibility(View.GONE);
            mRightArrow.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        mTextView.setText("0");
        if (event.getAction() == MotionEvent.ACTION_SCROLL &&
                event.isFromSource(InputDeviceCompat.SOURCE_ROTARY_ENCODER)
        ) {
            // Don't forget the negation here
            float delta = -event.getAxisValue(MotionEventCompat.AXIS_SCROLL) ;
            int multiplier = Math.abs(relativeCorrection) >= (Math.signum(delta) * Math.signum(relativeCorrection) > 0 ? 10 : 11) ? 5 : 1;
            relativeCorrection += Math.signum(delta) * multiplier;
            if (Math.abs(relativeCorrection) >= 180)
            {
                relativeCorrection += Math.signum(relativeCorrection)*-360;
            }
            updateUI();

            return true;
        }
        return false;
    }


}