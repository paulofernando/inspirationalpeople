package br.net.paulofernando.pessoasinspiradoras.image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Environment;
import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;

public class ImageFromSentence {

	/**
	 * Margin in pixels
	 */
	private int margin = 25;
	
	private Context context;
	
	public ImageFromSentence(Context context) {
		this.context = context;		
	}
	
	public void getImageFromSentence(final String sentence, final long userId, final int imgWidth, final int imgHeight) {
		
		Thread processingImage = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Bitmap dest = Bitmap.createBitmap(imgWidth, imgHeight, Bitmap.Config.ARGB_8888);		
				File file = null;		
			    Canvas canvas = new Canvas(dest);	    
			    Paint paint = new Paint();
			    
			    Bitmap photo = new DatabaseHelper(context).getPhotoByUserId(userId);
			    
			    if(photo.getWidth() > 128) {
			    	photo = Bitmap.createScaledBitmap(photo, 96, 96, false);
			    }
			    
			    //------------------- background ---------------------
			    paint.setShader(new LinearGradient(0, 0, 0, imgHeight, Color.parseColor("#19a5f6"), Color.parseColor("#37b7fb"), Shader.TileMode.MIRROR));
			    canvas.drawPaint(paint);
			    //----------------------------------------------------
			    
			    //------------------ Text ----------------------------
			    Paint paintText = new Paint();
			    paintText.setTextSize(35);
			    paintText.setColor(Color.WHITE);
			    paintText.setStyle(Style.FILL);
			    paintText.setFlags(Paint.ANTI_ALIAS_FLAG);
			    	    
			    float lineHeight = paintText.getFontMetrics().descent - paintText.getFontMetrics().ascent + paintText.getFontMetrics().leading + (margin/2);
			    
			    ArrayList<String> lines = adjustText(sentence, imgWidth, paintText);
			    float yText = (imgHeight/2) - ((lines.size() * lineHeight)/2);
			    
			    
			    int count = 1;
			    for(String line: lines) {
			    	canvas.drawText(line, (imgWidth/2) - (paintText.measureText(line)/2), yText + (lineHeight * count), paintText);
			    	count++;
			    }
			    //--------------------------------------------------
			    
			    	    
			    
			    //------------------ person's photo ----------------
			    Paint paintFrame = new Paint();
			    paintFrame.setColor(Color.parseColor("#eeeeee"));
			    paintFrame.setStyle(Paint.Style.STROKE);
			    paintFrame.setFlags(Paint.ANTI_ALIAS_FLAG);
			    paintFrame.setStrokeWidth(3f);
			    
			    
			    Bitmap cropped = Utils.getCroppedBitmap(photo);
			    float photoX = (imgWidth/2) - (cropped.getWidth()/2);
			    canvas.drawBitmap(cropped, photoX, margin, paint);
			    
			    canvas.drawCircle(imgWidth/2, (cropped.getHeight()/2) + margin, cropped.getWidth()/2, paintFrame);
			    
			    Paint paintName = new Paint();
			    paintName.setFlags(Paint.ANTI_ALIAS_FLAG);
			    paintName.setColor(Color.parseColor("#eeeeee"));
			    paintName.setTextSize(28);
			    
			    String name = new DatabaseHelper(context).getPerson(userId).name;
			    canvas.drawText(name, (imgWidth/2) - (paintName.measureText(name)/2), 
			    		cropped.getHeight() + ((int)(margin * 2.5)), paintName);
			    
			    //--------------------------------------------------
			    
			    try {
			    	String root = Environment.getExternalStorageDirectory().toString();
			    	File myDir = new File(root + "/" + (context.getResources().getString(R.string.app_name)) + "/inspirations");
			    	myDir.mkdirs();	    	
			    	file = new File (myDir, "inspiration-" + userId + ".jpg");
			    	
			    	if (file.exists ()) { 
			        	file.delete (); 
			        }
			    	
			        dest.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
			    } catch (FileNotFoundException e) {
			        e.printStackTrace();
			    }
			    
			    Intent intent = new Intent();
		        intent.setAction(Intent.ACTION_VIEW);
		        intent.setDataAndType(Uri.fromFile(file), "image/*");
		        context.startActivity(intent);
			}
		});
		
		processingImage.start();
		
	}
	
	/**
	 * Breaks the text in lines to adjust in the image
	 */
	private ArrayList<String> adjustText(String sentence, int imgWidth, Paint paint) {
		float textWidth  = imgWidth - (2 * margin);
		
		String[] words = sentence.split(" ");		
		ArrayList<String> lines = new ArrayList<String>();		
		StringBuffer buffer = new StringBuffer("");
		
		if(words.length > 0) {
			
			buffer.append(words[0]);
			
			for(int i = 1; i < words.length; i++) {
				if(paint.measureText(buffer.toString() + " " + words[i]) > textWidth) {
					lines.add(buffer.toString());
					buffer.delete(0, buffer.length());				
				} 
				buffer.append(" " + words[i]);								
			}
						
			lines.add(buffer.toString());		
		}
		
		return lines;
		
	}
	
}
