/*
 * Copyright (C) 2014, 2016 Valerio Bozzolan & James Dearing (TheOpenSourceNinja)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package anupam.acrylic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ClickableViewAccessibility")
public class EasyPaint extends GraphicsActivity implements
		ColorPickerDialog.OnColorChangedListener {

	public static int DEFAULT_BRUSH_SIZE = 10;
	private static int MAX_POINTERS = 10;
	private static final float TOUCH_TOLERANCE = 4;

	private Paint mPaint;
	private MaskFilter mEmboss;
	private MaskFilter mBlur;
	private boolean doubleBackToExitPressedOnce = false;
	private static final int CHOOSE_IMAGE = 0;
	private MyView contentView;

	private boolean waitingForBackgroundColor = false; //If true and colorChanged() is called, fill the background, else mPaint.setColor()
	private boolean extractingColor = false; //If this is true, the next touch event should extract a color rather than drawing a line.

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// it removes the title from the actionbar(more space for icons?)
		// this.getActionBar().setDisplayShowTitleEnabled(false);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		contentView = new MyView( this );
		setContentView( contentView );

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(Color.GREEN);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(DEFAULT_BRUSH_SIZE);

		// Where did these magic numbers come from? What do they mean? Can I change them? ~TheOpenSourceNinja
		// Absolutely random numbers in order to see the emboss. asd! ~Valerio
		mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);

		mBlur = new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL);

		if (isFirstTime()) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle(R.string.app_name);
			alert.setMessage(R.string.app_description);
			alert.setNegativeButton(R.string.continue_fuck,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							Toast.makeText(getApplicationContext(),
									R.string.here_is_your_canvas,
									Toast.LENGTH_SHORT).show();
						}
					});

			alert.show();
		} else {
			Toast.makeText(getApplicationContext(),
					R.string.here_is_your_canvas, Toast.LENGTH_SHORT).show();
		}

		loadFromIntents();
	}

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}

		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, R.string.press_back_again, Toast.LENGTH_SHORT)
				.show();

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;
			}
		}, 3000);
	}

	public void colorChanged(int color) {
		if( waitingForBackgroundColor ) {
			waitingForBackgroundColor = false;
			contentView.mBitmapBackground.eraseColor( color );
			//int[] colors = new int[ 1 ];
			//colors[ 0 ] = color;
			//contentView.mBitmapBackground = Bitmap.createBitmap( colors, contentView.mBitmapBackground.getWidth(), contentView.mBitmapBackground.getHeight(), contentView.mBitmapBackground.getConfig() );
		} else {
			// Changes the color of the action bar when the pencil color is changed
			if(Build.VERSION.SDK_INT >= 11) {
				ActionBar actionBar = getActionBar();
				ColorDrawable colorDrawable = new ColorDrawable(color);
				actionBar.setBackgroundDrawable(colorDrawable);
			}
			mPaint.setColor( color );

		}
	}

	public class MyView extends View {

		public Bitmap mBitmap;
		private Bitmap mBitmapBackground;
		private Canvas mCanvas;
		private Paint mBitmapPaint;
		private MultiLinePathManager multiLinePathManager;

		private class LinePath extends Path {
			private Integer idPointer;
			private float lastX;
			private float lastY;

			LinePath() {
				this.idPointer = null;
			}

			public float getLastX() {
				return lastX;
			}

			public float getLastY() {
				return lastY;
			}

			public void touchStart(float x, float y) {
				this.reset();
				this.moveTo(x, y);
				this.lastX = x;
				this.lastY = y;
			}

			public void touchMove(float x, float y) {
				float dx = Math.abs(x - lastX);
				float dy = Math.abs(y - lastY);
				if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
					this.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2);
					lastX = x;
					lastY = y;
				}
			}

			public boolean isDisassociatedFromPointer() {
				return idPointer == null;
			}

			public boolean isAssociatedToPointer(int idPointer) {
				return this.idPointer != null
						&& (int) this.idPointer == idPointer;
			}

			public void disassociateFromPointer() {
				idPointer = null;
			}

			public void associateToPointer(int idPointer) {
				this.idPointer = idPointer;
			}
		}

		private class MultiLinePathManager {
			public LinePath[] superMultiPaths;

			MultiLinePathManager(int maxPointers) {
				superMultiPaths = new LinePath[maxPointers];
				for (int i = 0; i < maxPointers; i++) {
					superMultiPaths[i] = new LinePath();
				}
			}

			public LinePath findLinePathFromPointer(int idPointer) {
				for (LinePath superMultiPath : superMultiPaths) {
					if (superMultiPath.isAssociatedToPointer(idPointer)) {
						return superMultiPath;
					}
				}
				return null;
			}

			public LinePath addLinePathWithPointer(int idPointer) {
				for (LinePath superMultiPath : superMultiPaths) {
					if (superMultiPath.isDisassociatedFromPointer()) {
						superMultiPath.associateToPointer(idPointer);
						return superMultiPath;
					}
				}
				return null;
			}
		}

		public MyView(Context c) {
			super(c);

			setId(R.id.CanvasId);
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point(display.getWidth(), display.getHeight());
			mBitmapBackground = Bitmap.createBitmap(size.x, size.y,  Bitmap.Config.ARGB_8888);
			mBitmap = Bitmap.createBitmap(size.x, size.y,
					Bitmap.Config.ARGB_8888);
			mCanvas = new Canvas(mBitmap);
			mBitmapPaint = new Paint(Paint.DITHER_FLAG);
			multiLinePathManager = new MultiLinePathManager(MAX_POINTERS);
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawColor(0xFFFFFFFF);
			canvas.drawBitmap( mBitmapBackground, 0, 0, new Paint() );
			canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint );
			for (int i = 0; i < multiLinePathManager.superMultiPaths.length; i++) {
				canvas.drawPath(multiLinePathManager.superMultiPaths[i], mPaint);
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			LinePath linePath;
			int index;
			int id;
			int eventMasked = event.getActionMasked();
			switch (eventMasked) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN: {
					index = event.getActionIndex( );
					id = event.getPointerId( index );
					
					if( extractingColor ) { //If the user chose the 'extract color' menu option, the touch event indicates where they want to extract the color from.
						extractingColor = false;
						
						View v = findViewById(R.id.CanvasId);
						v.setDrawingCacheEnabled(true);
						Bitmap cachedBitmap = v.getDrawingCache();
						
						int newColor = cachedBitmap.getPixel( Math.round( event.getX( index ) ), Math.round( event.getY( index ) ) );
						
						v.destroyDrawingCache();
						colorChanged( newColor );
						
						Toast.makeText(getApplicationContext(),
									   R.string.color_extracted,
									   Toast.LENGTH_SHORT).show();
					} else {
						
						linePath = multiLinePathManager.addLinePathWithPointer( id );
						if( linePath != null ) {
							linePath.touchStart( event.getX( index ), event.getY( index ) );
						} else {
							Log.e( "anupam", "Too many fingers!" );
						}
					}
					
					break;
				}
				case MotionEvent.ACTION_MOVE:
					for (int i = 0; i < event.getPointerCount(); i++) {
						id = event.getPointerId(i);
						index = event.findPointerIndex(id);
						linePath = multiLinePathManager.findLinePathFromPointer(id);
						if (linePath != null) {
							linePath.touchMove(event.getX(index), event.getY(index));
						}
					}
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
				case MotionEvent.ACTION_CANCEL:
					index = event.getActionIndex();
					id = event.getPointerId(index);
					linePath = multiLinePathManager.findLinePathFromPointer(id);
					if (linePath != null) {
						linePath.lineTo(linePath.getLastX(), linePath.getLastY());
	
						// Commit the path to our offscreen
						mCanvas.drawPath(linePath, mPaint);
	
						// Kill this so we don't double draw
						linePath.reset();
	
						// Allow this LinePath to be associated to another idPointer
						linePath.disassociateFromPointer();
					}
					break;
			}
			invalidate();
			return true;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		mPaint.setXfermode(null);
		mPaint.setAlpha(0xFF);

		switch (item.getItemId()) {
			case R.id.extract_color_menu: {
				Toast.makeText(getApplicationContext(),
							   R.string.tap_to_extract_color,
							   Toast.LENGTH_LONG).show();
				extractingColor = true;
				return true;
			}
			case R.id.normal_brush_menu:
				mPaint.setShader( null );
				mPaint.setMaskFilter(null);
				return true;
			case R.id.color_menu:
				new ColorPickerDialog(this, this, mPaint.getColor()).show();
				return true;
			case R.id.emboss_menu:
				mPaint.setShader( null );
				mPaint.setMaskFilter(mEmboss);
				return true;
			case R.id.smudge_menu: {
				/* I considered making this what happens when the blur_menu item is selected, but
				 * that could surprise users who are used to blur_menu's previous functionality, so
				 * I made this new smudge_menu item instead. I don't like calling it "Smudge" because
				 * this isn't exactly the same as what Photoshop and GIMP refer to as "Smudge", but I
				 * couldn't think of a better name that isn't "Blur".
				 * ~TheOpenSourceNinja
				 */
				if( Build.VERSION.SDK_INT >= 17 ) {
					/* Basically what we're doing here is copying the entire foreground bitmap,
					 * blurring it, then telling mPaint to use that instead of a solid color.
					 */
					
					RenderScript rs = RenderScript.create( getApplicationContext( ) );
					ScriptIntrinsicBlur script;
					try {
						script = ScriptIntrinsicBlur.create(rs, Element.RGBA_8888(rs));
					} catch (android.renderscript.RSIllegalArgumentException e) {
						script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
					}
					script.setRadius( 20f ); //The radius must be between 0 and 25. Smaller radius means less blur. I just picked 20 randomly. ~TheOpenSourceNinja
					
					//copy the foreground: (n API level 18+, this will be really fast because it uses a shared memory model, thus not really copying everything)
					Allocation input = Allocation.createFromBitmap( rs, contentView.mBitmap );
					script.setInput( input );
					
					//allocate memory for the output:
					Allocation output = Allocation.createTyped( rs, input.getType( ) );
					
					//Blur the image:
					script.forEach( output );
					
					//Store the blurred image as a Bitmap object:
					Bitmap blurred = Bitmap.createBitmap( contentView.mBitmap );
					output.copyTo( blurred );
					
					//Tell mPaint to use the blurred image:
					Shader shader = new BitmapShader( blurred, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP );
					mPaint.setShader( shader );
					return true;
				} else {
					Toast.makeText( this.getApplicationContext( ),
									anupam.acrylic.R.string.ability_disabled_need_newer_api_level,
									Toast.LENGTH_LONG ).show( );
					return true;
				}
			}
			case R.id.blur_menu:
				mPaint.setShader( null );
				mPaint.setMaskFilter(mBlur);
				return true;
			case R.id.size_menu: {
				LayoutInflater inflater = ( LayoutInflater ) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
				View layout = inflater.inflate( R.layout.brush,
												( ViewGroup ) findViewById( R.id.root ) );
				AlertDialog.Builder builder = new AlertDialog.Builder( this )
						.setView( layout );
				builder.setTitle( R.string.choose_width );
				final AlertDialog alertDialog = builder.create( );
				alertDialog.show( );
				SeekBar sb = ( SeekBar ) layout.findViewById( R.id.brushSizeSeekBar );
				sb.setProgress( getStrokeSize( ) );
				final TextView txt = ( TextView ) layout
						.findViewById( R.id.sizeValueTextView );
				txt.setText( String.format(
						getResources( ).getString( R.string.your_selected_size_is ),
						getStrokeSize( ) + 1 ) );
				sb.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener( ) {
					public void onProgressChanged( SeekBar seekBar,
												   final int progress, boolean fromUser ) {
						// Do something here with new value
						mPaint.setStrokeWidth( progress );
						txt.setText( String.format(
								getResources( ).getString(
										R.string.your_selected_size_is ), progress + 1 ) );
					}
	
					@Override
					public void onStartTrackingTouch( SeekBar seekBar ) {
						// TODO Auto-generated method stub
					}
	
					@Override
					public void onStopTrackingTouch( SeekBar seekBar ) {
						// TODO Auto-generated method stub
					}
				} );
				return true;
			}
			case R.id.erase_menu: {
				LayoutInflater inflater_e = ( LayoutInflater ) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
				View layout_e = inflater_e.inflate( R.layout.brush,
													( ViewGroup ) findViewById( R.id.root ) );
				AlertDialog.Builder builder_e = new AlertDialog.Builder( this )
						.setView( layout_e );
				builder_e.setTitle( R.string.choose_width );
				final AlertDialog alertDialog_e = builder_e.create( );
				alertDialog_e.show( );
				SeekBar sb_e = ( SeekBar ) layout_e.findViewById( R.id.brushSizeSeekBar );
				sb_e.setProgress( getStrokeSize( ) );
				final TextView txt_e = ( TextView ) layout_e
						.findViewById( R.id.sizeValueTextView );
				txt_e.setText( String.format(
						getResources( ).getString( R.string.your_selected_size_is ),
						getStrokeSize( ) + 1 ) );
				sb_e.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener( ) {
					public void onProgressChanged( SeekBar seekBar,
												   final int progress, boolean fromUser ) {
						// Do something here with new value
						mPaint.setStrokeWidth( progress );
						txt_e.setText( String.format(
								getResources( ).getString(
										R.string.your_selected_size_is ), progress + 1 ) );
					}
	
					public void onStartTrackingTouch( SeekBar seekBar ) {
						// TODO Auto-generated method stub
					}
	
					public void onStopTrackingTouch( SeekBar seekBar ) {
						// TODO Auto-generated method stub
					}
				} );
				mPaint.setShader( null );
				mPaint.setXfermode( new PorterDuffXfermode( Mode.CLEAR ) );
				return true;
			}
			case R.id.clear_all_menu: {
				contentView.mBitmap.eraseColor( Color.TRANSPARENT );
				return true;
			}
			case R.id.save_menu:
				takeScreenshot(true);
				break;
			case R.id.share_menu: {
				File screenshotPath = takeScreenshot( false );
				Intent i = new Intent( );
				i.setAction( Intent.ACTION_SEND );
				i.setType( "image/png" );
				i.putExtra( Intent.EXTRA_SUBJECT,
							getString( anupam.acrylic.R.string.share_title_template ) );
				i.putExtra( Intent.EXTRA_TEXT,
							getString( anupam.acrylic.R.string.share_text_template ) );
				i.putExtra( Intent.EXTRA_STREAM, Uri.fromFile( screenshotPath ) );
				try {
					startActivity( Intent.createChooser( i,
														 getString( anupam.acrylic.R.string.toolbox_share_title ) ) );
				} catch( android.content.ActivityNotFoundException ex ) {
					Toast.makeText( this.getApplicationContext( ),
									anupam.acrylic.R.string.no_way_to_share,
									Toast.LENGTH_LONG ).show( );
				}
				break;
			}
			case R.id.open_image_menu: {
				Intent intent = new Intent( );
				intent.setType( "image/*" ); //The argument is an all-lower-case MIME type - in this case, any image format.
				intent.setAction( Intent.ACTION_GET_CONTENT );
				intent.putExtra( Intent.EXTRA_ALLOW_MULTIPLE, false ); //This is false by default, but I felt that for code clarity it was better to be explicit: we only want one image
				startActivityForResult( Intent.createChooser( intent, getResources().getString( R.string.select_image_to_open ) ), CHOOSE_IMAGE );
				break;
			}
			case R.id.fill_background_with_color: {
				waitingForBackgroundColor = true;
				new ColorPickerDialog( this, this, contentView.mBitmapBackground.getPixel( 0, 0 ) ).show();
				return true;
			}
			case R.id.about_menu:
				startActivity(new Intent(this, AboutActivity.class));
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * This takes the screenshot of the whole screen. Is this a good thing?
	 */
	private File takeScreenshot(boolean showToast) {
		View v = findViewById(R.id.CanvasId);
		v.setDrawingCacheEnabled(true);
		Bitmap cachedBitmap = v.getDrawingCache();
		Bitmap copyBitmap = cachedBitmap.copy(Bitmap.Config.RGB_565, true);
		v.destroyDrawingCache();
		FileOutputStream output = null;
		File file = null;
		try {
			File path = Places.getScreenshotFolder();
			Calendar cal = Calendar.getInstance();

			file = new File(path,

			cal.get(Calendar.YEAR) + "_" + (1 + cal.get(Calendar.MONTH)) + "_"
					+ cal.get(Calendar.DAY_OF_MONTH) + "_"
					+ cal.get(Calendar.HOUR_OF_DAY) + "_"
					+ cal.get(Calendar.MINUTE) + "_" + cal.get(Calendar.SECOND)
					+ ".png");
			output = new FileOutputStream(file);
			copyBitmap.compress(CompressFormat.PNG, 100, output);
		} catch (FileNotFoundException e) {
			file = null;
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		if (file != null) {
			if (showToast)
				Toast.makeText(
						getApplicationContext(),
						String.format(
								getResources().getString(
										R.string.saved_your_location_to),
								file.getAbsolutePath()), Toast.LENGTH_LONG)
						.show();
			// sending a broadcast to the media scanner so it will scan the new
			// screenshot.
			Intent requestScan = new Intent(
					Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			requestScan.setData(Uri.fromFile(file));
			sendBroadcast(requestScan);

			return file;
		} else {
			return null;
		}
	}

	private boolean isFirstTime() {
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		boolean ranBefore = preferences.getBoolean("RanBefore", false);
		if (!ranBefore) {
			// first time
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean("RanBefore", true);
			editor.commit();
		}
		return !ranBefore;
	}

	private int getStrokeSize() {
		return (int) mPaint.getStrokeWidth();
	}

	public void onActivityResult( int requestCode, int resultCode, Intent data ) {
		super.onActivityResult( requestCode, resultCode, data );

		if( resultCode != RESULT_CANCELED ) { //"The resultCode will be RESULT_CANCELED if the activity explicitly returned that, didn't return any result, or crashed during its operation." (quote from https://developer.android.com/reference/android/app/Activity.html#onActivityResult(int,%20int,%20android.content.Intent) )
			switch( requestCode ) {
				case CHOOSE_IMAGE: {
					setBackgroundUri( data.getData() );
				}
			}
		}
	}

	public void setBackgroundUri(Uri uri) {
		if (uri == null) {
			return;
		}

		try {
			//I don't like loading both full-sized and reduced-size copies of the image (the larger copy can use a lot of memory), but I couldn't find any other way to do this.
			Bitmap fullsize = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
			Bitmap resized = Bitmap.createScaledBitmap(fullsize, contentView.mBitmap.getWidth(), contentView.mBitmap.getHeight(), true);
			contentView.mBitmapBackground = resized;
			//contentView.mCanvas = new Canvas( contentView.mBitmapBackground );
		} catch (IOException exception) {
			//TODO: How should we handle this exception?
		}
	}

	public void loadFromIntents() {
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		System.out.println("Intentoso " + action + " type " + type);
		if(Intent.ACTION_SEND.equals(action) && type != null) {
			if( type.startsWith("image/") ) {
				setBackgroundUri( (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM) );
			}

		}
	}
}
