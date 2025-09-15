import { convertHeicToJpeg } from './convertHeicToJpeg';
import { isHeicFile } from './isHeicFormat';

export const convertHeicToJpegIfNeeded = async (file: File) => {
  const correctHeicFile = await isHeicFile(file);

  if (correctHeicFile) {
    return await convertHeicToJpeg(file);
  }

  return file;
};
