package org.simpod.torch;

import java.io.IOException;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.CompoundButton;
import android.widget.ToggleButton;


public class MainActivity extends ActionBarActivity implements SurfaceHolder.Callback {
	
	ToggleButton mTglSwitch;
	private Camera mCamera;
	private SurfaceHolder mHolder;
	private boolean mIsFlashOn = false;
	private boolean hasFlash = false;		

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//toggle button
		mTglSwitch = (ToggleButton) findViewById(R.id.torchToggleButton);
		
		mTglSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if (isChecked) {
		            turnFlashOn();
		        } else {
		        	turnFlashOff();
		        }
		    }
		});
		
		//check if the device has flash
		hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
		
		if (!hasFlash) {
			//device does not support flash: message and close the application
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
			
			alertBuilder.setTitle(R.string.error);			
			alertBuilder.setMessage(R.string.no_flash_error);
			alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish(); //close the application					
				}
			});
			AlertDialog alert = alertBuilder.create();
			alert.show();
			return;
		}
		
		//open the camera module
		getCamera();
		
				
/*
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}*/
	}
	
	private void getCamera() {
		if (mCamera == null) {
			try {
				//camera = Camera.open();
				//params = camera.getParameters();
				
				SurfaceView preview = (SurfaceView) findViewById(R.id.PREVIEW);
				mHolder = preview.getHolder();
				mHolder.addCallback(this);
				mCamera = Camera.open();
				mCamera.setPreviewDisplay(mHolder);				
				
			} catch (RuntimeException | IOException e) {
				Log.e("Camera Error. Failed to Open. Error: ", e.getMessage());
			}
		}
	}
	
	//power on the flash
    private void turnFlashOn() {
    	if (mCamera == null) {
            return;
        }
    	
        if (mIsFlashOn)
        	return;

        playSound();
                       
        Parameters params = mCamera.getParameters();
        params.setFlashMode(Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(params);
        mCamera.startPreview();        
        mIsFlashOn = true;                        	
    }
    
    private void turnFlashOff() {
    	if (mCamera == null) {
            return;
        }
    	
        if (!mIsFlashOn)
        	return;

        playSound();
        
	    // Turn off LED
	    Parameters params = mCamera.getParameters();
	    params.setFlashMode(Parameters.FLASH_MODE_OFF);
	    mCamera.setParameters(params);
	    mCamera.stopPreview();
	    mIsFlashOn = false;	    	    
    }
    
    
    
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {}

    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
        try {
			mCamera.setPreviewDisplay(mHolder);
		} catch (IOException e) {
			Log.e("Camera Error. Failed to Open. Error: ", e.getMessage());
			e.printStackTrace();
		}
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mHolder = null;
    }
    
    private void playSound(){    	
    	MediaPlayer MP;
        if(mIsFlashOn){
        	MP = MediaPlayer.create(MainActivity.this, R.raw.torch_off);            
        }else{
        	MP = MediaPlayer.create(MainActivity.this, R.raw.torch_on);        	
        }
        MP.setOnCompletionListener(new OnCompletionListener() {
     
            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.release();
            }
        });
        MP.start();
    }
    
    @Override
    protected void onStart() {
        super.onStart();
         
        // on starting the app get the camera params
        getCamera();
        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        //turn on the flash
        if(hasFlash)
        {
        	if (!mIsFlashOn)
        		mTglSwitch.setChecked(true);
        }
            
    }
    
    
    @Override
    protected void onPause() {
        super.onPause();
         
        // on pause turn off the flash
        if (mIsFlashOn)
        	mTglSwitch.setChecked(false);
    }

 
    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    protected void onStop() {
        super.onStop();
        
        if (mIsFlashOn)
        	mTglSwitch.setChecked(false);
         
        // on stop release the camera
        if (mCamera != null) {
        	mCamera.release();
        	mCamera = null;
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	//A placeholder fragment containing a simple view.
	
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
*/
}
