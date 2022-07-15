package com.example.metaucapstone;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CachedBitmap implements Serializable{

    private static final long serialVersionUID = -6298516694275121291L;

    transient Bitmap bitmap;

    public CachedBitmap(){};

    public CachedBitmap(Bitmap b){
        bitmap = b;
    }

    public void writeObject(ObjectOutputStream oos) throws IOException{
        if(bitmap!=null){
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            boolean success = bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
            if(success){
                oos.writeObject(byteStream.toByteArray());
            }
        }
    }

    public void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
        byte[] image = (byte[]) ois.readObject();
        if(image != null && image.length > 0){
            bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        }
    }

}
