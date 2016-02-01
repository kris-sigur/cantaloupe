package edu.illinois.library.cantaloupe.processor;

import edu.illinois.library.cantaloupe.image.Crop;
import edu.illinois.library.cantaloupe.image.OperationList;
import edu.illinois.library.cantaloupe.image.Rotate;
import edu.illinois.library.cantaloupe.image.Scale;
import edu.illinois.library.cantaloupe.image.SourceFormat;
import edu.illinois.library.cantaloupe.image.Transpose;
import edu.illinois.library.cantaloupe.test.TestUtil;
import org.junit.Test;

import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import java.awt.Dimension;
import java.awt.image.RenderedImage;

import static org.junit.Assert.*;

public class JaiUtilTest {

    private static final String IMAGE = "images/jpg-rgb-64x56x8-baseline.jpg";

    @Test
    public void testCropImage() throws Exception {
        RenderedOp image = getFixture(IMAGE);

        // test with no-op crop
        Crop crop = new Crop();
        crop.setFull(true);
        RenderedOp croppedImage = JaiUtil.cropImage(image, crop);
        assertSame(image, croppedImage);

        // test with non-no-op crop
        crop = new Crop();
        crop.setX(0f);
        crop.setY(0f);
        crop.setWidth(50f);
        crop.setHeight(50f);
        croppedImage = JaiUtil.cropImage(image, crop);
        assertEquals(50, croppedImage.getWidth());
        assertEquals(50, croppedImage.getHeight());
    }

    @Test
    public void testCropImageWithReductionFactor() {
        // TODO: write this
    }

    @Test
    public void testFilterImage() {
        // TODO: write this
    }

    @Test
    public void testReformatImage() throws Exception {
        final OperationList ops = new OperationList();
        final ReductionFactor reductionFactor = new ReductionFactor();
        ImageIoImageReader reader = new ImageIoImageReader();
        RenderedImage image = reader.read(
                TestUtil.getFixture(IMAGE), SourceFormat.JPG, ops,
                reductionFactor);
        PlanarImage planarImage = PlanarImage.wrapRenderedImage(image);
        RenderedOp renderedOp = JaiUtil.reformatImage(planarImage,
                new Dimension(512, 512));
        assertEquals(64, renderedOp.getWidth());
        assertEquals(56, renderedOp.getHeight());
    }

    @Test
    public void testRotateImage() throws Exception {
        RenderedOp inImage = getFixture(IMAGE);

        // test with no-op rotate
        Rotate rotate = new Rotate(0);
        RenderedOp rotatedImage = JaiUtil.rotateImage(inImage, rotate);
        assertSame(inImage, rotatedImage);

        // test with non-no-op crop
        rotate = new Rotate(45);
        rotatedImage = JaiUtil.rotateImage(inImage, rotate);

        final int sourceWidth = inImage.getWidth();
        final int sourceHeight = inImage.getHeight();
        final double radians = Math.toRadians(rotate.getDegrees());
        // note that JAI appears to use flooring instead of rounding
        final int expectedWidth = (int) Math.floor(Math.abs(sourceWidth *
                Math.cos(radians)) + Math.abs(sourceHeight *
                Math.sin(radians)));
        final int expectedHeight = (int) Math.floor(Math.abs(sourceHeight *
                Math.cos(radians)) + Math.abs(sourceWidth *
                Math.sin(radians)));

        assertEquals(expectedWidth, rotatedImage.getWidth());
        assertEquals(expectedHeight, rotatedImage.getHeight());
    }

    @Test
    public void testScaleImage() throws Exception {
        RenderedOp image = getFixture(IMAGE);

        // test with no-op scale
        Scale scale = new Scale();
        scale.setMode(Scale.Mode.FULL);
        RenderedOp scaledImage = JaiUtil.scaleImage(image, scale);
        assertSame(image, scaledImage);

        // test with non-no-op crop
        final float percent = 0.5f;
        final double fudge = 0.00000001f;
        scale = new Scale();
        scale.setPercent(percent);
        scaledImage = JaiUtil.scaleImage(image, scale);
        assertEquals(image.getWidth() * percent, scaledImage.getWidth(), fudge);
        assertEquals(image.getHeight() * percent, scaledImage.getHeight(), fudge);
    }

    @Test
    public void testScaleImageWithReductionFactor() {
        // TODO: write this
    }

    @Test
    public void testTransposeImage() throws Exception {
        // TODO: this test could be better
        RenderedOp image = getFixture(IMAGE);
        // horizontal
        Transpose transpose = Transpose.HORIZONTAL;
        RenderedOp result = JaiUtil.transposeImage(image, transpose);
        assertEquals(image.getWidth(), result.getWidth());
        assertEquals(image.getHeight(), result.getHeight());
        // vertical
        transpose = Transpose.VERTICAL;
        result = JaiUtil.transposeImage(image, transpose);
        assertEquals(image.getWidth(), result.getWidth());
        assertEquals(image.getHeight(), result.getHeight());
    }

    private RenderedOp getFixture(final String name) throws Exception {
        final OperationList ops = new OperationList();
        final ReductionFactor reductionFactor = new ReductionFactor();
        ImageIoImageReader reader = new ImageIoImageReader();
        RenderedImage image = reader.read(
                TestUtil.getFixture(name), SourceFormat.JPG, ops,
                reductionFactor);
        PlanarImage planarImage = PlanarImage.wrapRenderedImage(image);
        return JaiUtil.reformatImage(planarImage, new Dimension(512, 512));
    }

}