import { css } from '@emotion/react';
import styled from '@emotion/styled';

import Button from '../Button/Button';
import FormField from '../FormField/FormField';
import Input from '../Input/Input';

interface PhoneFieldsProps {
  phoneNumber: string;
  verificationCode: string;
  inputRef: React.RefObject<HTMLInputElement | null>;
  onPhoneNumberChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onVerificationCodeChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onAuthCodeVerifyClick: (phoneNumber: string) => void;
  onAuthCodeClick: (phoneNumber: string) => void;
  phoneNumberErrorMessage: string;
  verificationCodeErrorMessage: string;
  verificationButtonEnabled: boolean;
  verificationRequestButtonEnabled: boolean;
}

function PhoneFields({
  phoneNumber,
  verificationCode,
  inputRef,
  onPhoneNumberChange,
  onVerificationCodeChange,
  onAuthCodeClick,
  onAuthCodeVerifyClick,
  phoneNumberErrorMessage,
  verificationCodeErrorMessage,
  verificationButtonEnabled,
  verificationRequestButtonEnabled,
}: PhoneFieldsProps) {
  return (
    <>
      <FormField label="전화번호 *" errorMessage={phoneNumberErrorMessage}>
        <S_InputAndBtnWrapper>
          <div className="input-wrapper">
            <Input
              id="phone"
              name="phone"
              placeholder="010-1234-5678"
              type="tel"
              value={phoneNumber}
              ref={inputRef}
              onChange={onPhoneNumberChange}
              errored={phoneNumberErrorMessage !== ''}
            />
          </div>
          <Button
            type="button"
            customStyle={buttonCustomStyle}
            onClick={() => onAuthCodeClick(phoneNumber)}
            variant={verificationRequestButtonEnabled ? 'primary' : 'disabled'}
          >
            인증요청
          </Button>
        </S_InputAndBtnWrapper>
      </FormField>
      <FormField
        label="인증번호 확인 *"
        errorMessage={verificationCodeErrorMessage}
      >
        <S_InputAndBtnWrapper>
          <div className="input-wrapper">
            <Input
              id="verificationCode"
              name="verificationCode"
              placeholder="123456"
              type="tel"
              value={verificationCode}
              onChange={onVerificationCodeChange}
              errored={verificationCodeErrorMessage !== ''}
              maxLength={6}
            />
          </div>
          <Button
            type="button"
            customStyle={buttonCustomStyle}
            onClick={() => onAuthCodeVerifyClick(phoneNumber)}
            variant={verificationButtonEnabled ? 'primary' : 'disabled'}
          >
            인증하기
          </Button>
        </S_InputAndBtnWrapper>
      </FormField>
    </>
  );
}

export default PhoneFields;

const S_InputAndBtnWrapper = styled.div`
  display: flex;
  gap: 1.4rem;

  & > .input-wrapper {
    flex-grow: 1;
  }
`;

const buttonCustomStyle = css`
  height: 4rem;
  min-width: 6.44rem;
  padding: 1.1rem 0.8rem;

  font-size: 1.4rem;
`;
