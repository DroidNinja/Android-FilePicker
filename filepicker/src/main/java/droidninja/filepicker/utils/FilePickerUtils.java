package droidninja.filepicker.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;

import android.widget.Toast;
import com.android.internal.util.Predicate;
import droidninja.filepicker.FilePickerConst;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by droidNinja on 29/07/16.
 */
public class FilePickerUtils {

  public static <T> Collection<T> filter(Collection<T> target, Predicate<T> predicate) {
    Collection<T> result = new ArrayList<T>();
    for (T element : target) {
      if (predicate.apply(element)) {
        result.add(element);
      }
    }
    return result;
  }

  public static String getFileExtension(File file) {
    String name = file.getName();
    try {
      return name.substring(name.lastIndexOf(".") + 1);
    } catch (Exception e) {
      return "";
    }
  }

  public static boolean contains(String[] types, String path) {
    for (String string : types) {
      if (path.toLowerCase().endsWith(string)) return true;
    }
    return false;
  }

  public static <T> boolean contains2(final T[] array, final T v) {
    if (v == null) {
      for (final T e : array)
        if (e == null)
          return true;
    } else {
      for (final T e : array)
        if (e == v || v.equals(e))
          return true;
    }

    return false;
  }

  public static void notifyMediaStore(Context context, String path) {
    if (path != null && !TextUtils.isEmpty(path)) {
      Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
      File f = new File(path);
      Uri contentUri = Uri.fromFile(f);
      mediaScanIntent.setData(contentUri);
      context.sendBroadcast(mediaScanIntent);
    }
  }
}
