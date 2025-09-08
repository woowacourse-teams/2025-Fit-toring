export const convertHeicToJpeg = async (heicFile: File) => {
  const heic2any = (await import('heic2any')).default;

  const jpegBlob = await heic2any({
    blob: heicFile,
    toType: 'image/jpeg',
    quality: 0.8,
  });

  if (Array.isArray(jpegBlob)) {
    throw new Error('HEIC 파일 변환 중 오류가 발생했습니다.');
  }

  const fileName = heicFile.name.replace(/\.(heic|heif)$/i, '.jpeg');

  return new File([jpegBlob], fileName, {
    type: 'image/jpeg',
  });
};
