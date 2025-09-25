package fittoring.mentoring.infra.image;

import fittoring.mentoring.infra.exception.InfraErrorMessage;
import fittoring.mentoring.infra.exception.S3UploadException;
import jakarta.annotation.Nullable;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageTranscoder {

    public Encoded toOriginal(MultipartFile input, String extension, @Nullable String contentType) throws IOException {
        String normalizedExt = normalizeExtension(extension);
        String ct = (contentType == null || contentType.isBlank()) ? contentTypeOf(normalizedExt) : contentType;

        BufferedImage src = ImageIO.read(input.getInputStream());
        if (src == null) {
            throw new S3UploadException(InfraErrorMessage.IMAGE_TRANSCODE_ERROR.getMessage());
        }

        if ("jpg".equals(normalizedExt) || "jpeg".equals(normalizedExt)) {
            src = flattenIfHasAlpha(src, Color.WHITE);
            normalizedExt = "jpg";
            ct = "image/jpeg";
        }
        byte[] out = writeWithImageIO(src, normalizedExt);
        return new Encoded(out, normalizedExt, ct);
    }

    public Encoded toAvif(MultipartFile input) throws IOException {
        BufferedImage src = ImageIO.read(input.getInputStream());
        if (src == null) {
            throw new S3UploadException(InfraErrorMessage.IMAGE_TRANSCODE_ERROR.getMessage());
        }

        byte[] out = encodeWithAvifenc(src, 30, 4, false, Duration.ofSeconds(30));
        return new Encoded(out, "avif", "image/avif");
    }

    private static String normalizeExtension(String ext) {
        if (ext == null) {
            return "jpg";
        }
        String e = ext.toLowerCase().replace(".", "");
        if ("jpeg".equals(e)) {
            return "jpg";
        }
        return switch (e) {
            case "jpg", "png", "gif", "bmp", "webp" -> e;
            default -> "jpg";
        };
    }

    private static String contentTypeOf(String extension) {
        return switch (extension) {
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "webp" -> "image/webp";
            case "avif" -> "image/avif";
            default -> "image/jpeg";
        };
    }

    private static BufferedImage flattenIfHasAlpha(BufferedImage src, Color bg) {
        if (!src.getColorModel().hasAlpha()) {
            return src;
        }
        BufferedImage rgb = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgb.createGraphics();
        try {
            g.setComposite(AlphaComposite.SrcOver);
            g.setColor(bg);
            g.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
            g.drawImage(src, 0, 0, null);
        } finally {
            g.dispose();
        }
        return rgb;
    }

    private static byte[] writeWithImageIO(BufferedImage image, String formatName) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            if (!ImageIO.write(image, formatName, baos)) {
                throw new S3UploadException(InfraErrorMessage.IMAGE_TRANSCODE_ERROR.getMessage());
            }
            return baos.toByteArray();
        }
    }

    private static byte[] encodeWithAvifenc(
            BufferedImage src, int quality, int speed, boolean yuv444, Duration timeout
    ) {
        if (quality < 0 || quality > 63) {
            throw new S3UploadException("avifenc 품질(q)은 0~63 범위여야 합니다. 입력: " + quality);
        }
        if (speed < 0 || speed > 8) {
            throw new S3UploadException("avifenc 속도(s)는 0~8 범위여야 합니다. 입력: " + speed);
        }

        final String yuv = yuv444 ? "444" : "420";
        final int jobs = Math.min(Math.max(1, Runtime.getRuntime().availableProcessors()), 4);

        Path inPng = null;
        Path outAvif = null;
        Process process = null;

        try {
            inPng = Files.createTempFile("img-", ".png");
            outAvif = Files.createTempFile("img-", ".avif");

            try (OutputStream fos = Files.newOutputStream(inPng)) {
                if (!ImageIO.write(src, "png", fos)) {
                    throw new S3UploadException("PNG 저장 실패");
                }
            }
            String[][] attempts = {
                    new String[]{"--cq-level", String.valueOf(quality)},
                    new String[]{"-q", String.valueOf(quality)}
            };
            IOException last = null;

            for (String[] qopt : attempts) {
                ProcessBuilder pb = new ProcessBuilder(
                        "avifenc",
                        qopt[0], qopt[1],
                        "--yuv", yuv,
                        "--speed", String.valueOf(speed),
                        "--jobs", String.valueOf(jobs),
                        inPng.toString(),
                        outAvif.toString()
                );
                pb.redirectErrorStream(false);

                process = pb.start();
                final InputStream stderrStream = process.getErrorStream();
                final ByteArrayOutputStream errBuf = new ByteArrayOutputStream();

                Thread errT = new Thread(() -> {
                    try (stderrStream) {
                        stderrStream.transferTo(errBuf);
                    } catch (IOException ignored) {
                    }
                }, "avifenc-stderr");
                errT.setDaemon(true);
                errT.start();

                boolean finished = process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
                if (!finished) {
                    process.destroyForcibly();
                    throw new S3UploadException("avifenc 타임아웃(" + timeout.toSeconds() + "s)");
                }
                try {
                    errT.join(Math.min(timeout.toMillis(), 1000));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }

                int exit = process.exitValue();
                if (exit == 0) {
                    byte[] bytes = Files.readAllBytes(outAvif);
                    if (bytes.length > 0) {
                        return bytes;
                    }
                }
                last = new IOException("exit=" + exit + " stderr=" +
                        errBuf.toString(StandardCharsets.UTF_8));
            }

            throw new S3UploadException("avifenc 실행 실패: " + (last != null ? last.getMessage() : "원인 불명"));

        } catch (IOException e) {
            throw new S3UploadException("avifenc 실행 실패: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new S3UploadException("avifenc 대기 중 인터럽트");
        } finally {
            if (process != null) {
                process.destroy();
            }
            try {
                if (inPng != null) {
                    Files.deleteIfExists(inPng);
                }
            } catch (IOException ignored) {
            }
            try {
                if (outAvif != null) {
                    Files.deleteIfExists(outAvif);
                }
            } catch (IOException ignored) {
            }
        }
    }
}
