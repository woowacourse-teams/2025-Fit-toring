const HH_MM_PATTERN = /^(\d{2,}):([0-5]\d)$/;
const ISO_TIME_DURATION_PATTERN = /^PT(?:(\d+)H)?(?:(\d+)M)?(?:(\d+(?:\.\d+)?)S)?$/;

export const isoDurationToHHMM = (isoDuration: string): string => {
  const match = ISO_TIME_DURATION_PATTERN.exec(isoDuration);
  if (!match) {
    throw new Error(`지원하지 않는 duration 형식입니다: ${isoDuration}`);
  }

  const hours = Number(match[1] ?? 0);
  const minutes = Number(match[2] ?? 0);
  const seconds = Number(match[3] ?? 0);
  const totalMinutes = hours * 60 + minutes + Math.floor(seconds / 60);

  return minutesToHHMM(totalMinutes);
};

export const hhmmToIsoDuration = (hhmm: string): string => {
  const match = HH_MM_PATTERN.exec(hhmm);
  if (!match) {
    throw new Error("duration은 HH:MM 형식이어야 합니다.");
  }

  const hours = Number(match[1]);
  const minutes = Number(match[2]);
  if (hours === 0 && minutes === 0) {
    return "PT0S";
  }

  const hourPart = hours > 0 ? `${hours}H` : "";
  const minutePart = minutes > 0 ? `${minutes}M` : "";
  return `PT${hourPart}${minutePart}`;
};

export const minutesToHHMM = (totalMinutes: number): string => {
  if (!Number.isInteger(totalMinutes) || totalMinutes < 0) {
    throw new Error(`분 단위 duration은 0 이상의 정수여야 합니다: ${totalMinutes}`);
  }

  const hours = Math.floor(totalMinutes / 60);
  const minutes = totalMinutes % 60;
  return `${String(hours).padStart(2, "0")}:${String(minutes).padStart(2, "0")}`;
};
