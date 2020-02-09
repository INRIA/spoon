package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.Views.DoEditorActions;
import me.ccrama.redditslide.R;
import java.io.FileOutputStream;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Views.CanvasView;
import me.ccrama.redditslide.Visuals.Palette;
import java.io.IOException;
import java.io.File;
import me.ccrama.redditslide.util.FileUtil;
/**
 * Created by ccrama on 5/27/2015.
 */
public class Draw extends me.ccrama.redditslide.Activities.BaseActivity implements com.afollestad.materialdialogs.color.ColorChooserDialog.ColorCallback {
    me.ccrama.redditslide.Views.CanvasView drawView;

    android.view.View color;

    android.graphics.Bitmap bitmap;

    public static android.net.Uri uri;

    public static me.ccrama.redditslide.Views.DoEditorActions editor;

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        overrideSwipeFromAnywhere();
        disableSwipeBackLayout();
        super.onCreate(savedInstance);
        applyColorTheme("");
        setContentView(me.ccrama.redditslide.R.layout.activity_draw);
        drawView = ((me.ccrama.redditslide.Views.CanvasView) (findViewById(me.ccrama.redditslide.R.id.paintView)));
        drawView.setBaseColor(android.graphics.Color.parseColor("#303030"));
        color = findViewById(me.ccrama.redditslide.R.id.color);
        com.theartofdev.edmodo.cropper.CropImage.activity(me.ccrama.redditslide.Activities.Draw.uri).setGuidelines(com.theartofdev.edmodo.cropper.CropImageView.Guidelines.ON).start(this);
        setSupportActionBar(((android.support.v7.widget.Toolbar) (findViewById(me.ccrama.redditslide.R.id.toolbar))));
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, "", true, android.graphics.Color.parseColor("#212121"), me.ccrama.redditslide.R.id.toolbar);
    }

    public int getLastColor() {
        return me.ccrama.redditslide.Reddit.colors.getInt("drawColor", me.ccrama.redditslide.Visuals.Palette.getDefaultAccent());
    }

    @java.lang.Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        if ((id == me.ccrama.redditslide.R.id.done) && enabled) {
            java.io.File image;// image to share

            // check to see if the cache/shared_images directory is present
            final java.io.File imagesDir = new java.io.File((this.getCacheDir().toString() + java.io.File.separator) + "shared_image");
            if (!imagesDir.exists()) {
                imagesDir.mkdir();// create the folder if it doesn't exist

            } else {
                me.ccrama.redditslide.util.FileUtil.deleteFilesInDir(imagesDir);
            }
            try {
                // creates a file in the cache; filename will be prefixed with "img" and end with ".png"
                image = java.io.File.createTempFile("img", ".png", imagesDir);
                java.io.FileOutputStream out = null;
                try {
                    // convert image to png
                    out = new java.io.FileOutputStream(image);
                    android.graphics.Bitmap.createBitmap(drawView.getBitmap(), 0, ((int) (drawView.height)), ((int) (drawView.right)), ((int) (drawView.bottom - drawView.height))).compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, out);
                } finally {
                    if (out != null) {
                        out.close();
                        final android.net.Uri contentUri = me.ccrama.redditslide.util.FileUtil.getFileUri(image, this);
                        if (contentUri != null) {
                            android.content.Intent intent = me.ccrama.redditslide.util.FileUtil.getFileIntent(image, new android.content.Intent(), this);
                            setResult(android.app.Activity.RESULT_OK, intent);
                        } else {
                            // todo error Toast.makeText(this, getString(R.string.err_share_image), Toast.LENGTH_LONG).show();
                        }
                        finish();
                    }
                }
            } catch (java.io.IOException | java.lang.NullPointerException e) {
                e.printStackTrace();
                // todo error Toast.makeText(this, getString(R.string.err_share_image), Toast.LENGTH_LONG).show();
            }
        }
        if (id == me.ccrama.redditslide.R.id.undo) {
            drawView.undo();
        }
        return super.onOptionsItemSelected(item);
    }

    @java.lang.Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        android.view.MenuInflater inflater = getMenuInflater();
        inflater.inflate(me.ccrama.redditslide.R.menu.draw_menu, menu);
        return true;
    }

    boolean enabled;

    @java.lang.Override
    protected void onActivityResult(int code, int resultC, android.content.Intent data) {
        super.onActivityResult(code, resultC, data);
        if ((code == 10001) && (data != null)) {
            android.net.Uri selectedImageUri = data.getData();
            com.theartofdev.edmodo.cropper.CropImage.activity(selectedImageUri).setGuidelines(com.theartofdev.edmodo.cropper.CropImageView.Guidelines.ON).start(this);
        } else if (data != null) {
            com.theartofdev.edmodo.cropper.CropImage.ActivityResult result = com.theartofdev.edmodo.cropper.CropImage.getActivityResult(data);
            android.net.Uri selectedImageUri = result.getUri();
            try {
                bitmap = android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri).copy(android.graphics.Bitmap.Config.RGB_565, true);
                color.getBackground().setColorFilter(getLastColor(), android.graphics.PorterDuff.Mode.MULTIPLY);
                color.setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        new com.afollestad.materialdialogs.color.ColorChooserDialog.Builder(me.ccrama.redditslide.Activities.Draw.this, me.ccrama.redditslide.R.string.choose_color_title).allowUserColorInput(true).show();
                    }
                });
                drawView.drawBitmap(bitmap);
                drawView.setPaintStrokeColor(getLastColor());
                drawView.setPaintStrokeWidth(20.0F);
                enabled = true;
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        } else {
            finish();
        }
    }

    @java.lang.Override
    public void onColorSelection(@android.support.annotation.NonNull
    com.afollestad.materialdialogs.color.ColorChooserDialog dialog, @android.support.annotation.ColorInt
    int selectedColor) {
        drawView.setPaintStrokeColor(selectedColor);
        color.getBackground().setColorFilter(selectedColor, android.graphics.PorterDuff.Mode.MULTIPLY);
        me.ccrama.redditslide.Reddit.colors.edit().putInt("drawColor", selectedColor).commit();
    }
}