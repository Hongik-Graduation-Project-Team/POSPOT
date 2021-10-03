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
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

/**
 * Identify which of a known set of objects might be present and provide information about their positions within the given image or a video stream. */
public final class Model {
  @NonNull
  private final ImageProcessor imageProcessor;

  private int imageHeight;

  private int imageWidth;

  @NonNull
  private final TensorProcessor locationPostProcessor;

  @NonNull
  private final List<String> labelmap;

  @NonNull
  private final TensorProcessor categoryPostProcessor;

  @NonNull
  private final TensorProcessor scorePostProcessor;

  @NonNull
  private final TensorProcessor numberOfDetectionsPostProcessor;

  @NonNull
  private final org.tensorflow.lite.support.model.Model model;

  private Model(@NonNull Context context,
      @NonNull org.tensorflow.lite.support.model.Model.Options options) throws IOException {
    model = org.tensorflow.lite.support.model.Model.createModel(context, "model.tflite", options);
    MetadataExtractor extractor = new MetadataExtractor(model.getData());
    ImageProcessor.Builder imageProcessorBuilder = new ImageProcessor.Builder()
      .add(new ResizeOp(320, 320, ResizeMethod.NEAREST_NEIGHBOR))
      .add(new NormalizeOp(new float[] {127.5f}, new float[] {127.5f}))
      .add(new QuantizeOp(127f, 0.0078125f))
      .add(new CastOp(DataType.UINT8));
    imageProcessor = imageProcessorBuilder.build();
    TensorProcessor.Builder locationPostProcessorBuilder = new TensorProcessor.Builder()
      .add(new DequantizeOp((float)0, (float)0.0))
      .add(new NormalizeOp(new float[] {0.0f}, new float[] {1.0f}));
    locationPostProcessor = locationPostProcessorBuilder.build();
    TensorProcessor.Builder categoryPostProcessorBuilder = new TensorProcessor.Builder()
      .add(new DequantizeOp((float)0, (float)0.0))
      .add(new NormalizeOp(new float[] {0.0f}, new float[] {1.0f}));
    categoryPostProcessor = categoryPostProcessorBuilder.build();
    labelmap = FileUtil.loadLabels(extractor.getAssociatedFile("labelmap.txt"));
    TensorProcessor.Builder scorePostProcessorBuilder = new TensorProcessor.Builder()
      .add(new DequantizeOp((float)0, (float)0.0))
      .add(new NormalizeOp(new float[] {0.0f}, new float[] {1.0f}));
    scorePostProcessor = scorePostProcessorBuilder.build();
    TensorProcessor.Builder numberOfDetectionsPostProcessorBuilder = new TensorProcessor.Builder()
      .add(new DequantizeOp((float)0, (float)0.0))
      .add(new NormalizeOp(new float[] {0.0f}, new float[] {1.0f}));
    numberOfDetectionsPostProcessor = numberOfDetectionsPostProcessorBuilder.build();
  }

  @NonNull
  public static Model newInstance(@NonNull Context context) throws IOException {
    return new Model(context, (new org.tensorflow.lite.support.model.Model.Options.Builder()).build());
  }

  @NonNull
  public static Model newInstance(@NonNull Context context,
      @NonNull org.tensorflow.lite.support.model.Model.Options options) throws IOException {
    return new Model(context, options);
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
    private TensorBuffer location;

    private TensorBuffer category;

    private TensorBuffer score;

    private TensorBuffer numberOfDetections;

    private Outputs(org.tensorflow.lite.support.model.Model model) {
      this.location = TensorBuffer.createFixedSize(model.getOutputTensorShape(0), DataType.FLOAT32);
      this.category = TensorBuffer.createFixedSize(model.getOutputTensorShape(1), DataType.FLOAT32);
      this.score = TensorBuffer.createFixedSize(model.getOutputTensorShape(2), DataType.FLOAT32);
      this.numberOfDetections = TensorBuffer.createFixedSize(model.getOutputTensorShape(3), DataType.FLOAT32);
    }

    @NonNull
    private List<RectF> getLocationAsRectFList() {
      List<RectF> originalBoxes = BoundingBoxUtil.convert(location, new int[] {1,0,3,2}, 2, BoundingBoxUtil.Type.BOUNDARIES, BoundingBoxUtil.CoordinateType.RATIO, 320, 320);
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
    private List<String> getCategoryAsStringList() {
      return LabelUtil.mapValueToLabels(category, labelmap, 0);
    }

    @NonNull
    public TensorBuffer getCategoryAsTensorBuffer() {
      return categoryPostProcessor.process(category);
    }

    @NonNull
    public TensorBuffer getScoreAsTensorBuffer() {
      return scorePostProcessor.process(score);
    }

    @NonNull
    public TensorBuffer getNumberOfDetectionsAsTensorBuffer() {
      return numberOfDetectionsPostProcessor.process(numberOfDetections);
    }

    @NonNull
    public List<DetectionResult> getDetectionResultList() {
      List<RectF> location = getLocationAsRectFList();
      List<String> category = getCategoryAsStringList();
      float[] score = getScoreAsTensorBuffer().getFloatArray();
      List<DetectionResult> detectionResult = new ArrayList<>();
      for (int i = 0; i < location.size(); i++) {
        detectionResult.add(new DetectionResult(location.get(i), category.get(i), score[i] ));
      }
      return detectionResult;
    }

    @NonNull
    private Map<Integer, Object> getBuffer() {
      Map<Integer, Object> outputs = new HashMap<>();
      outputs.put(0, location.getBuffer());
      outputs.put(1, category.getBuffer());
      outputs.put(2, score.getBuffer());
      outputs.put(3, numberOfDetections.getBuffer());
      return outputs;
    }
  }

  public class DetectionResult {
    private final RectF location;

    private final String category;

    private final float score;

    private DetectionResult(RectF location, String category, float score) {
      this.location = location;
      this.category = category;
      this.score = score;
    }

    @NonNull
    public RectF getLocationAsRectF() {
      return location;
    }

    @NonNull
    public String getCategoryAsString() {
      return category;
    }

    @NonNull
    public float getScoreAsFloat() {
      return score;
    }
  }
}
