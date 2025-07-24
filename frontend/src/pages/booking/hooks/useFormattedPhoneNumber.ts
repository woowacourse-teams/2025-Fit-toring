import type React from 'react';
import { useRef, useState } from 'react';

import { getFormattedPhoneNumber } from '../utils/getFormattedPhoneNumber ';

const useFormattedPhoneNumber = () => {
  const [phoneNumber, setPhoneNumber] = useState('');

  const inputRef = useRef<HTMLInputElement | null>(null);

  const handlePhoneNumberChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const rawValue = e.target.value;
    const prevValue = phoneNumber;
    const cursorPos = e.target.selectionStart ?? rawValue.length;

    // 포매팅
    const formatted = getFormattedPhoneNumber(rawValue);

    // 원본입력과 포매팅된 입력의 길이 차이
    const lengthDiff = formatted.length - prevValue.length;

    // 커서 보정용 gap
    let gap = lengthDiff > 0 ? -1 : 1;

    // 하이픈 직전 자리 숫자 추가 시 보정
    if (lengthDiff === 1) {
      if (cursorPos - 1 === 8 || cursorPos - 1 === 3) gap = 0;
    }

    // 하이픈 삭제될 경우 보정
    if (lengthDiff === -2) {
      gap = 2;
    }

    setPhoneNumber(formatted);

    // DOM 업데이트 후 커서 이동
    requestAnimationFrame(() => {
      const nextPos = cursorPos + lengthDiff + gap;
      inputRef.current?.setSelectionRange(nextPos, nextPos);
    });
  };

  return {
    phoneNumber,
    inputRef,
    handlePhoneNumberChange,
  };
};

export default useFormattedPhoneNumber;
