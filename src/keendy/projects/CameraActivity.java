package keendy.projects;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import android.app.Activity;
import android.content.ContentValues;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CameraActivity extends Activity implements Callback { 

  private static final String TAG = "CAMERA ACTIVITY";
  
  private SurfaceView mSurfaceView;
  private SurfaceHolder mSurfaceHolder;
  
  private ImageButton mImageButton;
  
  private LinearLayout mLinearLayout;
  
  private Camera mCamera;
  
  private boolean mPreviewRunning = false;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	
    getWindow().setFormat(PixelFormat.TRANSLUCENT);
    
    setContentView(R.layout.camera);
    
    mLinearLayout = (LinearLayout) findViewById(R.id.camera_linear_layout);
    
    mSurfaceView = (SurfaceView) findViewById(R.id.camera_surface);
    mSurfaceHolder = mSurfaceView.getHolder();
    mSurfaceHolder.addCallback(this);
    mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
   
    mImageButton = (ImageButton) findViewById(R.id.camera_button);
    
    mImageButton.setOnClickListener(new OnClickListener() {

	  @Override
	  public void onClick(View view) {
		Log.i(TAG, "Class Name: " + view.getClass().getName());
		Log.i(TAG, "View ID: " + view.getId());
		if(view.getId() == mImageButton.getId())
		  mCamera.takePicture(null, null, jpegCallback);
	  }
    
    });
    
    Log.i(TAG, "onCreated!");
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
	
	if(mPreviewRunning)	{
	  mCamera.stopPreview();
	}
	
	Camera.Parameters p = mCamera.getParameters();
	p.setPreviewSize(w, h);
	mCamera.setParameters(p);

	try {
	  mCamera.setPreviewDisplay(holder);
	} catch (IOException e) {
	  Log.e(TAG, "IOException lol");
	}

	mCamera.startPreview();
	mPreviewRunning = true;

  }

  @Override
  public void surfaceCreated(SurfaceHolder holder) {
	mCamera = Camera.open();
	
    Log.i(TAG, "surfaceCreated!");
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
	mCamera.stopPreview();
	mPreviewRunning = false;
	mCamera.release();

    Log.i(TAG, "surfaceDestroyed!");
  }

  /** Picture Callback */
  Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
    public void onPictureTaken(byte[] imageData, Camera c) {
      
	  Uri imageFileUri =
	    getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, 
		    new ContentValues());
	  
	  try {
	    OutputStream imageFileOS = 
	      getContentResolver().openOutputStream(imageFileUri);
	    
	    imageFileOS.write(imageData);
	    imageFileOS.flush();
	    imageFileOS.close();
	    
	  } catch (FileNotFoundException e) {
	    Log.e(TAG, "File not found!");
	  } catch (IOException e) {
	    Log.e(TAG, "IOException!");
	  }
	    
	  mLinearLayout.setVisibility(View.INVISIBLE);
	  
	  Toast.makeText(CameraActivity.this, imageFileUri.getPath(), 
		  Toast.LENGTH_SHORT).show();
	  
      Log.i(TAG, imageFileUri.getPath());
      
   }  
  }; 
}
