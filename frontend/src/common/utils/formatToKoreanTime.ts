export const formatToKoreanTime = (isoString: string) => {
  const date = new Date(isoString);

  let hours = date.getHours();
  const minutes = date.getMinutes();

  const period = hours >= 12 ? '오후' : '오전';
  hours = hours % 12;
  if (hours === 0) {
    hours = 12;
  }

  const paddedMinutes = String(minutes).padStart(2, '0');

  return `${period} ${hours}시 ${paddedMinutes}분`;
};
