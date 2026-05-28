type DateInput = string | Date;

const KOREAN_WEEKDAYS = [
  '일요일',
  '월요일',
  '화요일',
  '수요일',
  '목요일',
  '금요일',
  '토요일',
];

const toDate = (dateInput: DateInput) => {
  return dateInput instanceof Date ? dateInput : new Date(dateInput);
};

export const isSameLocalDate = (
  firstDateInput: DateInput,
  secondDateInput: DateInput,
) => {
  const firstDate = toDate(firstDateInput);
  const secondDate = toDate(secondDateInput);

  return (
    firstDate.getFullYear() === secondDate.getFullYear() &&
    firstDate.getMonth() === secondDate.getMonth() &&
    firstDate.getDate() === secondDate.getDate()
  );
};

export const isSameLocalMinute = (
  firstDateInput: DateInput,
  secondDateInput: DateInput,
) => {
  const firstDate = toDate(firstDateInput);
  const secondDate = toDate(secondDateInput);

  return (
    isSameLocalDate(firstDate, secondDate) &&
    firstDate.getHours() === secondDate.getHours() &&
    firstDate.getMinutes() === secondDate.getMinutes()
  );
};

export const formatToKoreanTime = (isoString: string) => {
  const date = toDate(isoString);

  let hours = date.getHours();
  const minutes = date.getMinutes();

  const period = hours >= 12 ? '오후' : '오전';
  hours = hours % 12;
  if (hours === 0) {
    hours = 12;
  }

  const paddedMinutes = String(minutes).padStart(2, '0');

  return `${period} ${hours}:${paddedMinutes}`;
};

export const formatChatRoomListDate = (
  isoString: string,
  baseDate: Date = new Date(),
) => {
  const date = toDate(isoString);

  if (isSameLocalDate(date, baseDate)) {
    return formatToKoreanTime(isoString);
  }

  return `${date.getMonth() + 1}월 ${date.getDate()}일`;
};

export const formatChatDateDivider = (isoString: string) => {
  const date = toDate(isoString);

  return `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일 ${
    KOREAN_WEEKDAYS[date.getDay()]
  }`;
};
