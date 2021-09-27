// Generated by view binder compiler. Do not edit!
package org.tensorflow.codelabs.objectdetection.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import org.tensorflow.codelabs.objectdetection.R;

public final class ActivityManualBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button btnPose;

  @NonNull
  public final Button btnSpot;

  @NonNull
  public final TextView text;

  private ActivityManualBinding(@NonNull ConstraintLayout rootView, @NonNull Button btnPose,
      @NonNull Button btnSpot, @NonNull TextView text) {
    this.rootView = rootView;
    this.btnPose = btnPose;
    this.btnSpot = btnSpot;
    this.text = text;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityManualBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityManualBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_manual, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityManualBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btn_pose;
      Button btnPose = ViewBindings.findChildViewById(rootView, id);
      if (btnPose == null) {
        break missingId;
      }

      id = R.id.btn_spot;
      Button btnSpot = ViewBindings.findChildViewById(rootView, id);
      if (btnSpot == null) {
        break missingId;
      }

      id = R.id.text;
      TextView text = ViewBindings.findChildViewById(rootView, id);
      if (text == null) {
        break missingId;
      }

      return new ActivityManualBinding((ConstraintLayout) rootView, btnPose, btnSpot, text);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
