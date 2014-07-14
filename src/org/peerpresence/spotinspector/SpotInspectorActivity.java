package org.peerpresence.spotinspector;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;


public class SpotInspectorActivity extends Activity implements CvCameraViewListener2, GestureDetector.OnGestureListener
{
	private static final String LOG_LABEL= "SpotInspectorActivity";

	private static final Scalar SPOT_COLOR= new Scalar(255, 255, 255, 255); // white
	private static final int SPOT_SQUARE_PIXELS= 4;

	private Mat mRgba;
	private Mat mGray;
	private Mat mTemp;
	private Mat mHsv;
	private Mat mResult;
	
	private Point mUpperLeftCorner;
	private Point mLowerRightCorner;

	private JView mOpenCvCameraView;

	private GestureDetector mGestureDetector;

	
	public SpotInspectorActivity()
	{
		Log.i(LOG_LABEL, "Instantiated new " + this.getClass());
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.i(LOG_LABEL, "called onCreate");
		super.onCreate(savedInstanceState);

		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		this.setContentView(R.layout.spotinspector_view);

		mGestureDetector= new GestureDetector(this, this);

		mOpenCvCameraView= (JView) findViewById(R.id.spotinspector_view);
		mOpenCvCameraView.setCvCameraViewListener(this);

		DisplayMetrics displayMetrics= getResources().getDisplayMetrics(); 
		int height= displayMetrics.heightPixels;
		int width= displayMetrics.widthPixels;

		mUpperLeftCorner= new Point((width/2) - (SPOT_SQUARE_PIXELS/2), (height/2 - (SPOT_SQUARE_PIXELS/2)));
		mLowerRightCorner= new Point((width/2) + (SPOT_SQUARE_PIXELS/2), (height/2 + (SPOT_SQUARE_PIXELS/2)));

	}

	@Override
	public void onPause()
	{
		super.onPause();

		if (mOpenCvCameraView != null)
		{
			mOpenCvCameraView.disableView();
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();

		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		if (mOpenCvCameraView != null)
		{
			mOpenCvCameraView.disableView();
		}
	}

	private BaseLoaderCallback mLoaderCallback= new BaseLoaderCallback(this)
	{
		@Override
		public void onManagerConnected(int status)
		{
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(LOG_LABEL, "OpenCV loaded successfully");

				mOpenCvCameraView.enableView();
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	public void onCameraViewStarted(int width, int height)
	{
		mGray= new Mat();
		mRgba= new Mat();
		mTemp= new Mat();
		mHsv= new Mat();
		mResult= new Mat();
	}

	public void onCameraViewStopped()
	{
		mGray.release();
		mRgba.release();
		mTemp.release();
		mHsv.release();
		mResult.release();
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame)
	{
		mRgba= inputFrame.rgba();
		mGray= inputFrame.gray();

		double[] values= mRgba.get((int)mUpperLeftCorner.x, (int)mUpperLeftCorner.y);
//		for (double value : values)
//		{
			Core.putText(mRgba, Double.toString(values[0]), new Point(400, 40), 0/* font */, 1, new Scalar(255, 0, 0, 255), 1);			
			Core.putText(mRgba, Double.toString(values[1]), new Point(400, 80), 0/* font */, 1, new Scalar(255, 0, 0, 255), 1);			
			Core.putText(mRgba, Double.toString(values[2]), new Point(400, 120), 0/* font */, 1, new Scalar(255, 0, 0, 255), 1);			
//		}
		
		Core.rectangle(mRgba, mUpperLeftCorner, mLowerRightCorner, SPOT_COLOR, 3);

		return mRgba;
	}

	
	// Gesture handlers
	//

	@Override
	public boolean onGenericMotionEvent(MotionEvent event)
	{
		if (mGestureDetector != null)
		{
			mGestureDetector.onTouchEvent(event);
		}

		return true;
	}

	@Override
	public boolean onDown(MotionEvent e)
	{
		finish();
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e)
	{
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e)
	{
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
	{
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e)
	{
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
	{
		return false;
	}

}