// Generated by view binder compiler. Do not edit!
package org.tensorflow.codelabs.objectdetection.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

public final class ActivitySpotResultDetailBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final TextView address;

  @NonNull
  public final Button btnLink;

  @NonNull
  public final ImageView imageview;

  @NonNull
  public final TextView name;

  private ActivitySpotResultDetailBinding(@NonNull ConstraintLayout rootView,
      @NonNull TextView address, @NonNull Button btnLink, @NonNull ImageView imageview,
      @NonNull TextView name) {
    this.rootView = rootView;
    this.address = address;
    this.btnLink = btnLink;
    this.imageview = imageview;
    this.name = name;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivitySpotResultDetailBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivitySpotResultDetailBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_spot_result_detail, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivitySpotResultDetailBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.address;
      TextView address = ViewBindings.findChildViewById(rootView, id);
      if (address == null) {
        break missingId;
      }

      id = R.id.btn_link;
      Button btnLink = ViewBindings.findChildViewById(rootView, id);
      if (btnLink == null) {
        break missingId;
      }

      id = R.id.imageview;
      ImageView imageview = ViewBindings.findChildViewById(rootView, id);
      if (imageview == null) {
        break missingId;
      }

      id = R.id.name;
      TextView name = ViewBindings.findChildViewById(rootView, id);
      if (name == null) {
        break missingId;
      }

      return new ActivitySpotResultDetailBinding((ConstraintLayout) rootView, address, btnLink,
          imageview, name);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
