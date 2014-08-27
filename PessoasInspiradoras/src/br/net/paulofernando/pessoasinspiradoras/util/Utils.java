package br.net.paulofernando.pessoasinspiradoras.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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
import android.widget.Toast;
import br.net.paulofernando.pessoasinspiradoras.R;

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

    public static void saveBackupInFile(Context context, String text) {
    	String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/" + (context.getResources().getString(R.string.app_name)) + "/backup");    
        String fname = "backup.xml";
        
        myDir.mkdirs();        
        File file = new File (myDir, fname);
        
        if (file.exists ()) { 
        	file.delete (); 
        }
        
        try {
           FileOutputStream out = new FileOutputStream(file);
           out.write(text.getBytes());
           out.flush();
           out.close();
           
           Toast.makeText(context, context.getResources().getString(R.string.backup_completed), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
           e.printStackTrace();
        }
    }
    
    public static void restoreBackup(Context context) {
    	String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/" + (context.getResources().getString(R.string.app_name)) + "/backup");    
        String fname = "backup.xml";
        
        String read_data = null;

        try {
	        File myFile = new File(myDir + "/" + fname);
	        FileInputStream fis = new FileInputStream(myFile);
	
	        byte[] dataArray = new byte[fis.available()];
	        while (fis.read(dataArray) != -1) {
	        	read_data = new String(dataArray);
	        }
	        fis.close();
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
        
        InputStream is;
		try {
			is = new ByteArrayInputStream(read_data.getBytes("UTF-8"));
			 
			// Build XML document
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();	        
	        Document doc = db.parse(is);
	        
	        
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

       
    }
    
    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }
	
}
