// Generated by view binder compiler. Do not edit!
package org.tensorflow.codelabs.objectdetection.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import org.tensorflow.codelabs.objectdetection.R;

public final class ActivityCameraBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ImageView background;

  @NonNull
  public final Button btnTakePhoto;

  @NonNull
  public final RecyclerView cameraProfile;

  @NonNull
  public final Button reset;

  @NonNull
  public final PreviewView viewFinder;

  private ActivityCameraBinding(@NonNull ConstraintLayout rootView, @NonNull ImageView background,
      @NonNull Button btnTakePhoto, @NonNull RecyclerView cameraProfile, @NonNull Button reset,
      @NonNull PreviewView viewFinder) {
    this.rootView = rootView;
    this.background = background;
    this.btnTakePhoto = btnTakePhoto;
    this.cameraProfile = cameraProfile;
    this.reset = reset;
    this.viewFinder = viewFinder;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityCameraBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityCameraBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_camera, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityCameraBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.background;
      ImageView background = ViewBindings.findChildViewById(rootView, id);
      if (background == null) {
        break missingId;
      }

      id = R.id.btnTakePhoto;
      Button btnTakePhoto = ViewBindings.findChildViewById(rootView, id);
      if (btnTakePhoto == null) {
        break missingId;
      }

      id = R.id.camera_profile;
      RecyclerView cameraProfile = ViewBindings.findChildViewById(rootView, id);
      if (cameraProfile == null) {
        break missingId;
      }

      id = R.id.reset;
      Button reset = ViewBindings.findChildViewById(rootView, id);
      if (reset == null) {
        break missingId;
      }

      id = R.id.viewFinder;
      PreviewView viewFinder = ViewBindings.findChildViewById(rootView, id);
      if (viewFinder == null) {
        break missingId;
      }

      return new ActivityCameraBinding((ConstraintLayout) rootView, background, btnTakePhoto,
          cameraProfile, reset, viewFinder);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}