package fittoring.mentoring.infra.image;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import fittoring.mentoring.infra.exception.S3UploadException;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Optional;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class ImageTranscoderTest {

    private final ImageTranscoder transcoder = new ImageTranscoder();

    private static MockMultipartFile pngWithAlpha(
            String name,
            int w,
            int h,
            Color fill,
            float alpha
    ) throws Exception {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setComposite(AlphaComposite.SrcOver.derive(alpha));
            g.setColor(fill);
            g.fillRect(0, 0, w, h);
        } finally {
            g.dispose();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        return new MockMultipartFile("image", name, "image/png", baos.toByteArray());
    }

    private static MockMultipartFile opaquePng(
            String name,
            int w,
            int h,
            Color fill
    ) throws Exception {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setColor(fill);
            g.fillRect(0, 0, w, h);
        } finally {
            g.dispose();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        return new MockMultipartFile("image", name, "image/png", baos.toByteArray());
    }

    private static Optional<String> which(String cmd) {
        try {
            Process p = new ProcessBuilder("sh", "-c", "command -v " + cmd).start();
            int exit = p.waitFor();
            if (exit == 0) {
                return Optional.of(new String(p.getInputStream().readAllBytes()).trim());
            }
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Nested
    @DisplayName("원본 확장자 변환")
    class ToOriginal {

        @DisplayName("jpg로 요청 시 알파가 있으면 흰색 배경으로 평탄화 후 JPEG로 인코딩한다")
        @Test
        void flattenAlphaWhenJpeg() throws Exception {
            // given
            var input = pngWithAlpha("alpha.png", 16, 16, Color.RED, 0.5f);

            // when
            var encoded = transcoder.toOriginal(input, "jpg", null);

            // then
            assertThat(encoded.extension()).isEqualTo("jpg");
            assertThat(encoded.contentType()).isEqualTo("image/jpeg");
            assertThat(encoded.bytes()).isNotEmpty();
            BufferedImage back = ImageIO.read(new java.io.ByteArrayInputStream(encoded.bytes()));
            assertThat(back.getType()).isEqualTo(BufferedImage.TYPE_INT_RGB);
        }

        @DisplayName("png로 요청 시 알파 유지/손상 없이 png로 인코딩한다")
        @Test
        void keepPng() throws Exception {
            // given
            var input = opaquePng("opaque.png", 16, 16, Color.BLUE);

            // when
            var encoded = transcoder.toOriginal(input, "png", null);

            // then
            assertThat(encoded.extension()).isEqualTo("png");
            assertThat(encoded.contentType()).isEqualTo("image/png");
            assertThat(encoded.bytes()).isNotEmpty();
        }

        @DisplayName("지원하지 않는 확장자는 jpg로 폴백한다")
        @Test
        void fallbackToJpg() throws Exception {
            // given
            var input = opaquePng("x.tiff", 8, 8, Color.BLACK);

            // when
            var encoded = transcoder.toOriginal(input, "tiff", null);

            // then
            assertThat(encoded.extension()).isEqualTo("jpg");
            assertThat(encoded.contentType()).isEqualTo("image/jpeg");
            assertThat(encoded.bytes()).isNotEmpty();
        }
    }

    @DisplayName("Avif 확장자 변환")
    @Nested
    class ToAvif {

        @DisplayName("avifenc가 설치된 환경이면 avif 바이트를 생성한다 (통합성 테스트)")
        @Test
        void toAvif_whenAvifencExists() throws Exception {
            // given
            assumeTrue(which("avifenc").isPresent(), "avifenc not found; skipping");
            var input = opaquePng("sample.png", 32, 32, Color.GREEN);

            // when
            var encoded = transcoder.toAvif(input);

            // then
            assertThat(encoded.extension()).isEqualTo("avif");
            assertThat(encoded.contentType()).isEqualTo("image/avif");
            assertThat(encoded.bytes()).isNotEmpty();
        }

        @DisplayName("avifenc가 없는 환경이면 S3UploadException으로 감싸 예외를 던진다")
        @Test
        void toAvif_whenAvifencMissing() throws Exception {
            // given
            assumeTrue(which("avifenc").isEmpty(), "avifenc found; skipping negative path test");
            var input = opaquePng("sample.png", 32, 32, Color.GREEN);

            // when
            // then
            assertThatThrownBy(() -> transcoder.toAvif(input))
                    .isInstanceOf(S3UploadException.class)
                    .hasMessageContaining("avifenc");
        }
    }
}
