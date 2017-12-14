package com.intelliReader.image;

/* Copyright 2016 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/


import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import com.intelliReader.jetty.HTMLUtil;
import org.apache.lucene.util.IOUtils;
import org.eclipse.jdt.internal.core.util.Util;
import org.tensorflow.DataType;
import org.tensorflow.Graph;
import org.tensorflow.Output;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;
import sun.nio.ch.IOUtil;


/** Sample use of the TensorFlow Java API to label images using a pre-trained model. */
public class LabelImage {
    // The inception model dir.
    private String modelDir;
    private InputStream is;
    // The top K image label to return.
    private int topK;

    private byte[] graphDef;
    List<String> labels;

    private static void printUsage(PrintStream s) {
        final String url =
                "https://storage.googleapis.com/download.tensorflow.org/models/inception5h.zip";
        s.println(
                "Java program that uses a pre-trained Inception model (http://arxiv.org/abs/1512.00567)");
        s.println("to label JPEG images.");
        s.println("TensorFlow version: " + TensorFlow.version());
        s.println();
        s.println("Usage: label_image <model dir> <image file>");
        s.println();
        s.println("Where:");
        s.println("<model dir> is a directory containing the unzipped contents of the inception model");
        s.println("            (from " + url + ")");
        s.println("<image file> is the path to a JPEG image file");
    }

    public static void main(String[] args) throws FileNotFoundException {
        String modelDir = "C:\\Users\\ting\\Documents\\GitHub\\iReader\\src\\main\\resources\\iReader\\inception5h";
        String imageUrl = "https://static01.nyt.com/images/2017/12/10/world/08Military1/merlin_130833051_9dfea6af-ab25-4b84-98cd-966f2bba6fcf-facebookJumbo.jpg";
        LabelImage li = new LabelImage(modelDir, 3);
        for (String s : li.getLabels(imageUrl)) {
            System.out.println("Label: " + s);
        }
    }

    public LabelImage(String modelDir, int topK) {
        this.modelDir = modelDir;
        this.topK = topK;
        try {
            this.graphDef = readAllBytesOrExit(new FileInputStream(
                    String.valueOf(Paths.get(modelDir, "tensorflow_inception_graph.pb").toAbsolutePath())));
            this.labels = readAllLinesOrExit(new FileInputStream(
                    String.valueOf(Paths.get(modelDir, "imagenet_comp_graph_label_strings.txt").toAbsolutePath())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public LabelImage(InputStream graphDefInputStream,
                      InputStream labelInputStream, int topK) {
        this.topK = topK;
        this.graphDef = readAllBytesOrExit(graphDefInputStream);
        this.labels = readAllLinesOrExit(labelInputStream);
    }

    public List<String> getLabels(String imageUrl) {
        if (this.graphDef == null || this.labels == null) return null;
        byte[] imageBytes;
        try {
            imageBytes = HTMLUtil.getPicFromUrl(imageUrl);
        } catch (IOException e) {
            return null;
        }

        List<String> foundLabels = new ArrayList<>();
        try (Tensor image = constructAndExecuteGraphToNormalizeImage(imageBytes)) {
            float[] labelProbabilities = executeInceptionGraph(graphDef, image);
            List<LabelProb> labelProbs = new ArrayList<>(labelProbabilities.length);
            for (int i = 0; i < labelProbabilities.length; i++) {
                labelProbs.add(new LabelProb(i, labelProbabilities[i]));
            }
            Collections.sort(labelProbs);
            for (int i = 0; i < Math.min(topK, labelProbabilities.length); i++) {
                foundLabels.add(labels.get(labelProbs.get(i).idx));
            }
        }
        return foundLabels;
    }

    private static Tensor constructAndExecuteGraphToNormalizeImage(byte[] imageBytes) {
        try (Graph g = new Graph()) {
            GraphBuilder b = new GraphBuilder(g);
            // Some constants specific to the pre-trained model at:
            // https://storage.googleapis.com/download.tensorflow.org/models/inception5h.zip
            //
            // - The model was trained with images scaled to 224x224 pixels.
            // - The colors, represented as R, G, B in 1-byte each were converted to
            //   float using (value - Mean)/Scale.
            final int H = 224;
            final int W = 224;
            final float mean = 117f;
            final float scale = 1f;

            // Since the graph is being constructed once per execution here, we can use a constant for the
            // input image. If the graph were to be re-used for multiple input images, a placeholder would
            // have been more appropriate.
            final Output input = b.constant("input", imageBytes);
            final Output output =
                    b.div(
                            b.sub(
                                    b.resizeBilinear(
                                            b.expandDims(
                                                    b.cast(b.decodeJpeg(input, 3), DataType.FLOAT),
                                                    b.constant("make_batch", 0)),
                                            b.constant("size", new int[] {H, W})),
                                    b.constant("mean", mean)),
                            b.constant("scale", scale));
            try (Session s = new Session(g)) {
                return s.runner().fetch(output.op().name()).run().get(0);
            }
        }
    }

    private static float[] executeInceptionGraph(byte[] graphDef, Tensor image) {
        try (Graph g = new Graph()) {
            g.importGraphDef(graphDef);
            try (Session s = new Session(g);
                 Tensor result = s.runner().feed("input", image).fetch("output").run().get(0)) {
                final long[] rshape = result.shape();
                if (result.numDimensions() != 2 || rshape[0] != 1) {
                    throw new RuntimeException(
                            String.format(
                                    "Expected model to produce a [1 N] shaped tensor where N is the number of labels, instead it produced one with shape %s",
                                    Arrays.toString(rshape)));
                }
                int nlabels = (int) rshape[1];
                return result.copyTo(new float[1][nlabels])[0];
            }
        }
    }

    private static byte[] readAllBytesOrExit(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            System.err.println("Failed to read [" + path + "]: " + e.getMessage());
            System.exit(1);
        }
        return null;
    }

    private static byte[] readAllBytesOrExit(InputStream is) {
        if (is == null) return null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<String> readAllLinesOrExit(InputStream is) {
        if (is == null) return null;
        List<String> lines =
                new BufferedReader(new InputStreamReader(is,
                        StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
        return lines;
    }

    private static List<String> readAllLinesOrExit(Path path) {
        try {
            return Files.readAllLines(path, Charset.forName("UTF-8"));
        } catch (IOException e) {
            System.err.println("Failed to read [" + path + "]: " + e.getMessage());
            System.exit(0);
        }
        return null;
    }

    // In the fullness of time, equivalents of the methods of this class should be auto-generated from
    // the OpDefs linked into libtensorflow_jni.so. That would match what is done in other languages
    // like Python, C++ and Go.
    static class GraphBuilder {
        GraphBuilder(Graph g) {
            this.g = g;
        }

        Output div(Output x, Output y) {
            return binaryOp("Div", x, y);
        }

        Output sub(Output x, Output y) {
            return binaryOp("Sub", x, y);
        }

        Output resizeBilinear(Output images, Output size) {
            return binaryOp("ResizeBilinear", images, size);
        }

        Output expandDims(Output input, Output dim) {
            return binaryOp("ExpandDims", input, dim);
        }

        Output cast(Output value, DataType dtype) {
            return g.opBuilder("Cast", "Cast").addInput(value).setAttr("DstT", dtype).build().output(0);
        }

        Output decodeJpeg(Output contents, long channels) {
            return g.opBuilder("DecodeJpeg", "DecodeJpeg")
                    .addInput(contents)
                    .setAttr("channels", channels)
                    .build()
                    .output(0);
        }

        Output constant(String name, Object value) {
            try (Tensor t = Tensor.create(value)) {
                return g.opBuilder("Const", name)
                        .setAttr("dtype", t.dataType())
                        .setAttr("value", t)
                        .build()
                        .output(0);
            }
        }

        private Output binaryOp(String type, Output in1, Output in2) {
            return g.opBuilder(type, type).addInput(in1).addInput(in2).build().output(0);
        }

        private Graph g;
    }
}

class LabelProb implements Comparable<LabelProb> {
    int idx;
    float prob;
    LabelProb(int i, float f) {
        idx = i;
        prob = f;
    }
    @Override
    public int compareTo(LabelProb o) {
        if (this.prob < o.prob) return 1;
        if (this.prob == o.prob) return 0;
        return -1;
    }
}