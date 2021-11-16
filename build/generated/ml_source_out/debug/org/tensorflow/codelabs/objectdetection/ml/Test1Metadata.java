package org.tensorflow.codelabs.objectdetection.ml;

import android.content.Context;
import android.graphics.RectF;
import androidx.annotation.NonNull;
import java.io.IOException;
import java.lang.Integer;
import java.lang.Object;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.CastOp;
import org.tensorflow.lite.support.common.ops.DequantizeOp;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.common.ops.QuantizeOp;
import org.tensorflow.lite.support.image.BoundingBoxUtil;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeOp.ResizeMethod;
import org.tensorflow.lite.support.label.LabelUtil;
import org.tensorflow.lite.support.metadata.MetadataExtractor;
import org.tensorflow.lite.support.model.Model;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

/**
 * Identify which of a known set of objects might be present and provide information about their positions within the given image or a video stream. */
public final class Test1Metadata {
  @NonNull
  private final ImageProcessor imageProcessor;

  private int imageHeight;

  private int imageWidth;

  @NonNull
  private final List<String> custom;

  @NonNull
  private final TensorProcessor categoryPostProcessor;

  @NonNull
  private final TensorProcessor locationPostProcessor;

  @NonNull
  private final Model model;

  private Test1Metadata(@NonNull Context context, @NonNull Model.Options options) throws
      IOException {
    model = Model.createModel(context, "test1_metadata.tflite", options);
    MetadataExtractor extractor = new MetadataExtractor(model.getData());
    ImageProcessor.Builder imageProcessorBuilder = new ImageProcessor.Builder()
      .add(new ResizeOp(416, 416, ResizeMethod.NEAREST_NEIGHBOR))
      .add(new NormalizeOp(new float[] {127.5f}, new float[] {127.5f}))
      .add(new QuantizeOp(0f, 0.0f))
      .add(new CastOp(DataType.FLOAT32));
    imageProcessor = imageProcessorBuilder.build();
    TensorProcessor.Builder categoryPostProcessorBuilder = new TensorProcessor.Builder()
      .add(new DequantizeOp((float)0, (float)0.0))
      .add(new NormalizeOp(new float[] {0.0f}, new float[] {1.0f}));
    categoryPostProcessor = categoryPostProcessorBuilder.build();
    custom = FileUtil.loadLabels(extractor.getAssociatedFile("custom.txt"));
    TensorProcessor.Builder locationPostProcessorBuilder = new TensorProcessor.Builder()
      .add(new DequantizeOp((float)0, (float)0.0))
      .add(new NormalizeOp(new float[] {0.0f}, new float[] {1.0f}));
    locationPostProcessor = locationPostProcessorBuilder.build();
  }

  @NonNull
  public static Test1Metadata newInstance(@NonNull Context context) throws IOException {
    return new Test1Metadata(context, (new Model.Options.Builder()).build());
  }

  @NonNull
  public static Test1Metadata newInstance(@NonNull Context context, @NonNull Model.Options options)
      throws IOException {
    return new Test1Metadata(context, options);
  }

  @NonNull
  public Outputs process(@NonNull TensorImage image) {
    imageHeight = image.getHeight();
    imageWidth = image.getWidth();
    TensorImage processedimage = imageProcessor.process(image);
    Outputs outputs = new Outputs(model);
    model.run(new Object[] {processedimage.getBuffer()}, outputs.getBuffer());
    return outputs;
  }

  public void close() {
    model.close();
  }

  @NonNull
  public Outputs process(@NonNull TensorBuffer image) {
    TensorBuffer processedimage = image;
    Outputs outputs = new Outputs(model);
    model.run(new Object[] {processedimage.getBuffer()}, outputs.getBuffer());
    return outputs;
  }

  public class Outputs {
    private TensorBuffer category;

    private TensorBuffer location;

    private Outputs(Model model) {
      this.category = TensorBuffer.createFixedSize(model.getOutputTensorShape(0), DataType.FLOAT32);
      this.location = TensorBuffer.createFixedSize(model.getOutputTensorShape(1), DataType.FLOAT32);
    }

    @NonNull
    private List<String> getCategoryAsStringList() {
      return LabelUtil.mapValueToLabels(category, custom, 0);
    }

    @NonNull
    public TensorBuffer getCategoryAsTensorBuffer() {
      return categoryPostProcessor.process(category);
    }

    @NonNull
    private List<RectF> getLocationAsRectFList() {
      List<RectF> originalBoxes = BoundingBoxUtil.convert(location, new int[] {1,0,3,2}, 2, BoundingBoxUtil.Type.BOUNDARIES, BoundingBoxUtil.CoordinateType.RATIO, 416, 416);
      List<RectF> processedBoxes = new ArrayList<>();
      for (android.graphics.RectF box : originalBoxes) {
        processedBoxes.add(imageProcessor.inverseTransform(box, imageHeight, imageWidth));
      }
      return processedBoxes;
    }

    @NonNull
    public TensorBuffer getLocationAsTensorBuffer() {
      return locationPostProcessor.process(location);
    }

    @NonNull
    public List<DetectionResult> getDetectionResultList() {
      List<String> category = getCategoryAsStringList();
      List<RectF> location = getLocationAsRectFList();
      List<DetectionResult> detectionResult = new ArrayList<>();
      for (int i = 0; i < category.size(); i++) {
        detectionResult.add(new DetectionResult(category.get(i), location.get(i) ));
      }
      return detectionResult;
    }

    @NonNull
    private Map<Integer, Object> getBuffer() {
      Map<Integer, Object> outputs = new HashMap<>();
      outputs.put(0, category.getBuffer());
      outputs.put(1, location.getBuffer());
      return outputs;
    }
  }

  public class DetectionResult {
    private final String category;

    private final RectF location;

    private DetectionResult(String category, RectF location) {
      this.category = category;
      this.location = location;
    }

    @NonNull
    public String getCategoryAsString() {
      return category;
    }

    @NonNull
    public RectF getLocationAsRectF() {
      return location;
    }
  }
}
