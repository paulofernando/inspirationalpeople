package br.net.paulofernando.pessoasinspiradoras.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.widget.EditText;
import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.parser.PersonParser;
import br.net.paulofernando.pessoasinspiradoras.parser.XMLPullParserHandler;

public class Utils {
	
	public static Uri getPhotoUri(Context ctx, long contactId) {
	    try {
	        Cursor cur = ctx.getContentResolver().query(
	                ContactsContract.Data.CONTENT_URI,
	                null,
	                ContactsContract.Data.CONTACT_ID + "=" + contactId + " AND "
	                        + ContactsContract.Data.MIMETYPE + "='"
	                        + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
	                null);
	        if (cur != null) {
	            if (!cur.moveToFirst()) {
	                return null; // no photo
	            }
	        } else {
	            return null; // error in cursor process
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	    Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
	    return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
	}
	
	public static byte[] getPhotoByResource(int resource, Context context) {
		Resources res = context.getResources();
		Drawable drawable = res.getDrawable(resource);
		Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		return stream.toByteArray();
	}
	
	public static void showConfirmDialog(Context context, String title, String message, DialogInterface.OnClickListener listenerConfirmacao) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(context.getString(R.string.yes), listenerConfirmacao).setNegativeButton(context.getString(R.string.no), null).show();
    }
	
	public static void showConfirmDialog(Context context, String title, String message, 
			DialogInterface.OnClickListener listenerConfirmacao, DialogInterface.OnClickListener listenerNegacao) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(context.getString(R.string.yes), listenerConfirmacao).setNegativeButton(context.getString(R.string.no), listenerNegacao).show();
    }

    public static void showAlertDialog(Context context, String title, String message) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setIcon(android.R.drawable.ic_dialog_alert)
                .setNeutralButton(android.R.string.ok, null).show();
    }
    
    public static void showInfoDialog(Context context, String title, String message) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setIcon(android.R.drawable.ic_dialog_info)
                .setNeutralButton(android.R.string.ok, null).show();
    }
    
    public static void showErrorDialog(Context context, String title, String message) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setIcon(android.R.drawable.ic_dialog_info)
                .setNeutralButton(android.R.string.ok, null).show();
    }
    
    public static void showTextDialog(Context context, String title, DialogInterface.OnClickListener listenerConfirmacao) {
    	final EditText input = new EditText(context);
    	input.setId(android.R.id.edit);
        new AlertDialog.Builder(context).setTitle(title).setView(input).setIcon(android.R.drawable.ic_dialog_alert)
                .setNeutralButton(android.R.string.ok, listenerConfirmacao).show();        
    }
    
    public static Bitmap scaleDownBitmap(Bitmap bmp, int newHeight, Context context) {
    	final float densityMultiplier = context.getResources().getDisplayMetrics().density;
		int h = (int)(newHeight * densityMultiplier);
		int w = (int)(h * bmp.getWidth()/((double) bmp.getHeight()));
		return Bitmap.createScaledBitmap(bmp, w, h, true);
    }
    
    public static byte[] getByteArrayFromBitmap(Bitmap bmp) {
    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
    	bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
    	return stream.toByteArray();
    }
    
    public static void showSharePopup(Context context, String text) {
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("text/plain");
		share.putExtra(Intent.EXTRA_TEXT, text);

		context.startActivity(Intent.createChooser(share, context.getString(R.string.send_backup)));
    }
    
    public static Bitmap getFacebookProfilePicture(String userID){
        URL imageURL;
		try {
			imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=large");
			Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
	        return bitmap;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return null;
    }
        
    public static Bitmap bitmapToMinimunSize(Bitmap bmp, int size) {    	        		
		if(bmp.getWidth() > size) {
			bmp = Bitmap.createScaledBitmap(bmp, size, size, true);        			
		}
		return bmp;
    }
	
}
