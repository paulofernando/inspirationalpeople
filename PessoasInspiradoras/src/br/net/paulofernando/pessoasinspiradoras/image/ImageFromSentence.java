package br.net.paulofernando.pessoasinspiradoras.image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;
import br.net.paulofernando.pessoasinspiradoras.R;

public class ImageFromSentence {

	/**
	 * Margin in pixels
	 */
	private int margin = 25;
	
	private Context context;
	
	public ImageFromSentence(Context context) {
		this.context = context;		
	}
	
	public Uri getImageFromSentence(String sentence, int imgWidth, int imgHeight) {
		Bitmap dest = Bitmap.createBitmap(imgWidth, imgHeight, Bitmap.Config.ARGB_8888);				
		File file = null;		
	    Canvas canvas = new Canvas(dest);	    
	    Paint paint = new Paint();
	    
	    paint.setShader(new LinearGradient(0, 0, 0, imgHeight, Color.parseColor("#19a5f6"), Color.parseColor("#6ac8fe"), Shader.TileMode.MIRROR));
	    canvas.drawPaint(paint);
	    
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
	    
	    try {
	    	String root = Environment.getExternalStorageDirectory().toString();
	    	File myDir = new File(root + "/" + (context.getResources().getString(R.string.app_name)) + "/inspirations");
	    	myDir.mkdirs();	    	
	    	file = new File (myDir, "inspiration.jpg");
	    	
	    	if (file.exists ()) { 
	        	file.delete (); 
	        }
	    	
	        dest.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
	    
	    if(file == null) {
	    	Toast.makeText(context, context.getResources().getString(R.string.image_not_created), Toast.LENGTH_SHORT);
	    	return null;
	    }
	    
	    return Uri.fromFile(file);
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
