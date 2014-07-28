package com.tmstudio.goon;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import com.tmstudio.goon.database.GoonDB;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class CongratsDialog extends Activity {
	
	private ImageView frame;
	private ImageView addPhoto;
	private Intent pictureActionIntent = null;
	protected static final int CAMERA_REQUEST = 0;
	protected static final int GALLERY_PICTURE = 1;
	protected static final int PIC_CROP = 2;
	private Bitmap bitmap;
	private GoonDB goonDB;
	private String date;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.congrats_dialog);

		frame = (ImageView) findViewById(R.id.congratsframe);
		addPhoto = (ImageView) findViewById(R.id.takeaphotoforcongrats);

		frame.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startDialog();
			}
		});

	}

	public void startDialog() {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Upload Pictures Option");
		myAlertDialog.setMessage("How do you want to set your picture?");

		myAlertDialog.setPositiveButton("Gallery",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						pictureActionIntent = new Intent(
								Intent.ACTION_GET_CONTENT, null);
						pictureActionIntent.setType("image/*");
						pictureActionIntent.putExtra("crop", "true");
						pictureActionIntent.putExtra("aspectX", 0);
						pictureActionIntent.putExtra("aspectY", 0);
						pictureActionIntent.putExtra("return-data", true);
						startActivityForResult(pictureActionIntent,GALLERY_PICTURE);
					}
				});

		myAlertDialog.setNegativeButton("Camera",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						pictureActionIntent = new Intent(
								android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						pictureActionIntent.putExtra(MediaStore.EXTRA_OUTPUT,
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI
										.toString());
						pictureActionIntent.putExtra("crop", "true");
						pictureActionIntent.putExtra("aspectX", 0);
						pictureActionIntent.putExtra("aspectY", 0);
						pictureActionIntent.putExtra("outputX", 1000);
						pictureActionIntent.putExtra("outputY", 1000);
						startActivityForResult(pictureActionIntent,CAMERA_REQUEST);

					}
				});
		myAlertDialog.show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			HashMap<String, String> map = goonDB.getDiaryNoteByDate(date);
			if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
				if (data != null) {
					Uri selectedUri = data.getData();
					Bitmap bmp = (Bitmap) data.getExtras().get("data");
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
					performCrop(selectedUri);
					bitmap = bmp;
					// addPhoto.setImageBitmap(transform(bmp));
					addPhoto.setImageBitmap(transform(bmp));
					bmp.recycle();
					uploadImage("https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcR2AEQs9fH80uDPmJkF83KaWThzxMmH8FezV0DXvqyLs6Agw-gD");
				}
			} else if (resultCode == RESULT_OK&& requestCode == GALLERY_PICTURE) {
				if (data != null) {
					Uri selectedUri = data.getData();
					Bitmap photo = data.getExtras().getParcelable("data");
					// BitmapFactory.Options options = new
					// BitmapFactory.Options();
					// options.inTempStorage = new byte[16 * 1024];
					// float i;
					// Bitmap bmp = tempImage(selectedUri, 1000, 1000);
					// addPhoto.setImageBitmap(transform(bmp));
					bitmap = photo;
					addPhoto.setImageBitmap(transform(photo));
					// addPhoto.setImageBitmap(transform(photo));
					// rotate(getImageOrientation(selectedUri+""));
					// bmp.recycle();
					uploadImage("http://image.alienware.com/images/galleries/gallery-shot_laptops_14_05.jpg");
				}
			}
		} catch (RuntimeException e) {

			Log.d("Runtime Memory", e.getMessage() + " / "
					+ Runtime.getRuntime().totalMemory());
			System.out.println();
		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	private void performCrop(Uri picUri) {
		Intent cropIntent = new Intent("com.android.camera.action.CROP");
		// indicate image type and Uri
		cropIntent.setDataAndType(picUri, "image/*");
		// set crop properties
		cropIntent.putExtra("crop", "true");
		// indicate aspect of desired crop
		cropIntent.putExtra("aspectX", 1);
		cropIntent.putExtra("aspectY", 1);
		// indicate output X and Y
		cropIntent.putExtra("outputX", 256);
		cropIntent.putExtra("outputY", 256);
		// retrieve data on return
		cropIntent.putExtra("return-data", true);
		// start the activity - we handle returning in onActivityResult
		startActivityForResult(cropIntent, PIC_CROP);
	}

	private void uploadImage(String selectedUri) {
		// if (map != null) {
		// goonDB.updateDiaryNote(date, noteString, selectedUri, 1);
		// } else {
		// goonDB.insertDiaryNote(date, noteString, selectedUri, 0);
		// }
	}

	public static int getImageOrientation(String imagePath) {
		int rotate = 0;
		try {

			File imageFile = new File(imagePath);
			ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rotate;
	}

	protected Bitmap tempImage(Uri imageUri, int reqWidth, int reqHeight) {
		Bitmap bitmapImage = null;

		try {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(this.getContentResolver()
					.openInputStream(imageUri), null, options);

			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);

			options.inJustDecodeBounds = false;
			bitmapImage = BitmapFactory.decodeStream(this.getContentResolver()
					.openInputStream(imageUri), null, options);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return bitmapImage;
	}

	protected int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	public Bitmap transform(Bitmap source) {
		if (source != null) {
			int size = Math.min(source.getWidth(), source.getHeight());
			int x = (source.getWidth() - size) / 2;
			int y = (source.getHeight() - size) / 2;
			Bitmap result = Bitmap.createBitmap(source, x, y, size, size);
			if (result != source) {
				source.recycle();
			}
			return result;
		} else {
			Drawable myDrawable = getResources().getDrawable(
					R.drawable.goon_ipad_new_born_frame_con02);
			Bitmap result = ((BitmapDrawable) myDrawable).getBitmap();
			return result;
		}
	}
}
