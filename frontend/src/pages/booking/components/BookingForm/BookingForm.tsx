import { useRef, useState } from 'react';

import styled from '@emotion/styled';

import Input from '../../../../common/components/Input/Input';
import BookingSummarySection from '../BookingSummarySection/BookingSummarySection';
import FormField from '../FormField/FormField';

function BookingForm() {
  const [menteeName, setMenteeName] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [counselContent, setCounselContent] = useState('');

  const inputRef = useRef<HTMLInputElement | null>(null);

  const handleMenteeNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setMenteeName(e.target.value);
  };

  const formatPhoneNumber = (input: string) => {
    const onlyNums = input.replace(/\D/g, '');

    if (onlyNums.length < 4) return onlyNums;
    if (onlyNums.length < 8)
      return `${onlyNums.slice(0, 3)}-${onlyNums.slice(3)}`;
    return `${onlyNums.slice(0, 3)}-${onlyNums.slice(3, 7)}-${onlyNums.slice(7, 11)}`;
  };

  const handlePhoneNumberChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const rawValue = e.target.value;
    const prevValue = phoneNumber;
    const cursorPos = e.target.selectionStart ?? rawValue.length;

    // 포매팅
    const formatted = formatPhoneNumber(rawValue);

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

  const handleCounselContentChange = (
    e: React.ChangeEvent<HTMLTextAreaElement>,
  ) => {
    setCounselContent(e.target.value);
  };

  return (
    <StyledContainer>
      <StyledInfoText>
        아래 정보를 입력해주시면 멘토에게 상담 신청이 전송됩니다.
      </StyledInfoText>
      <StyledFieldWrapper>
        <FormField label="상담자명 *" htmlFor="name" errorMessage={''}>
          <Input
            placeholder="홍길동"
            id="name"
            value={menteeName}
            onChange={handleMenteeNameChange}
            errored={false}
          />
        </FormField>
        <FormField label="전화번호 *" htmlFor="phone" errorMessage={''}>
          <Input
            placeholder="010-1234-4986"
            id="phone"
            value={phoneNumber}
            onChange={handlePhoneNumberChange}
            errored={false}
            ref={inputRef}
            maxLength={13}
          />
        </FormField>
        <FormField
          label="상담 내용(선택사항)"
          htmlFor="details"
          errorMessage={''}
        >
          <StyledTextarea
            id="details"
            placeholder="구체적으로 궁금한 내용이나 현재 상황을 적어주시면 
더 정확한 조언을 받을 수 있습니다."
            onChange={handleCounselContentChange}
            errored={false}
            value={counselContent}
          />
        </FormField>
      </StyledFieldWrapper>
      <BookingSummarySection />
    </StyledContainer>
  );
}

export default BookingForm;

const StyledContainer = styled.form`
  padding: 2.2rem;
  border-radius: 1.3rem;
  border: 1px solid ${({ theme }) => theme.LINE.REGULAR};
  width: 100%;
  height: 100%;
  background-color: white;
`;

const StyledInfoText = styled.p`
  ${({ theme }) => theme.TYPOGRAPHY.B4_R};
  color: ${({ theme }) => theme.FONT.BLACK};
  margin-top: 1.7rem;
`;

const StyledFieldWrapper = styled.div`
  display: flex;
  flex-direction: column;
  margin-top: 3.3rem;
  margin-bottom: 3.2rem;
  gap: 2.1rem;
`;

const StyledTextarea = styled.textarea<{ errored: boolean }>`
  width: 100%;
  height: 5.8rem;
  padding: 0.7rem 1.1rem;
  border: ${({ theme, errored }) =>
      errored ? theme.FONT.ERROR : theme.BORDER.GRAY300}
    1px solid;
  border-radius: 0.7rem;

  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
  resize: none;

  :focus {
    outline: none;
  }
`;
