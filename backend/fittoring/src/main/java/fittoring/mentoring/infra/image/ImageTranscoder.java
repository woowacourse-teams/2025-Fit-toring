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
            boolean ok = ImageIO.write(image, formatName, baos);
            if (!ok) {
                throw new S3UploadException(InfraErrorMessage.IMAGE_TRANSCODE_ERROR.getMessage());
            }
            return baos.toByteArray();
        }
    }

    private static byte[] encodeWithAvifenc(
            BufferedImage src,
            int quality,
            int speed,
            boolean yuv444,
            Duration timeout
    ) {
        if (quality < 0 || quality > 63) {
            throw new S3UploadException("avifenc 품질(q)은 0~63 범위여야 합니다. 입력: " + quality);
        }
        if (speed < 0 || speed > 8) {
            throw new S3UploadException("avifenc 속도(s)는 0~8 범위여야 합니다. 입력: " + speed);
        }
        final String yuv = yuv444 ? "444" : "420";
        final int jobs = Math.min(Math.max(1, Runtime.getRuntime().availableProcessors()), 4);

        java.util.function.Function<String[], ProcessBuilder> pbFactory = (extraArgs) -> {
            java.util.List<String> args = new java.util.ArrayList<>();
            args.add("avifenc");
            args.add("--stdin");
            args.add("--stdout");
            args.add("--yuv");
            args.add(yuv);
            args.add("--speed");
            args.add(String.valueOf(speed));
            args.add("--jobs");
            args.add(String.valueOf(jobs));
            for (String s : extraArgs) {
                args.add(s);
            }
            return new ProcessBuilder(args);
        };
        String[][] attempts = new String[][]{
                new String[]{"--cq-level", String.valueOf(quality)},
                new String[]{"-q", String.valueOf(quality)}
        };
        IOException lastIo = null;
        for (String[] attemptArgs : attempts) {
            Process process = null;
            final ByteArrayOutputStream errBuf = new ByteArrayOutputStream();
            try {
                ProcessBuilder pb = pbFactory.apply(attemptArgs);
                pb.redirectErrorStream(false);
                process = pb.start();
                final InputStream stderrStream = process.getErrorStream();
                Thread stderrDrainer = new Thread(() -> {
                    try (InputStream stdErr = stderrStream) {
                        stdErr.transferTo(errBuf);
                    } catch (IOException ignored) {
                    }
                }, "avifenc-stderr-drain");
                stderrDrainer.setDaemon(true);
                stderrDrainer.start();
                try (OutputStream os = process.getOutputStream()) {
                    boolean ok = ImageIO.write(src, "png", os);
                    if (!ok) {
                        throw new S3UploadException("PNG 변환 실패");
                    }
                }
                byte[] avifBytes;
                try (InputStream stdOut = process.getInputStream();
                     ByteArrayOutputStream outBuf = new ByteArrayOutputStream()) {
                    stdOut.transferTo(outBuf);
                    avifBytes = outBuf.toByteArray();
                }
                boolean finished = process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
                if (!finished) {
                    process.destroyForcibly();
                    throw new S3UploadException("avifenc 타임아웃(" + timeout.toSeconds() + "s)");
                }
                try {
                    stderrDrainer.join(Math.min(timeout.toMillis(), 1000));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                int exit = process.exitValue();
                if (exit == 0 && avifBytes.length > 0) {
                    return avifBytes;
                }
                String err = errBuf.toString(StandardCharsets.UTF_8);
                lastIo = new IOException("avifenc 실패 (exit=" + exit + ") stderr=" + err);

            } catch (IOException e) {
                lastIo = e;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new S3UploadException("avifenc 대기 중 인터럽트");
            } finally {
                if (process != null) {
                    process.destroy();
                }
            }
        }
        String msg = (lastIo != null ? lastIo.getMessage() : "원인 불명");
        throw new S3UploadException("avifenc 실행 실패: " + msg);
    }
}
