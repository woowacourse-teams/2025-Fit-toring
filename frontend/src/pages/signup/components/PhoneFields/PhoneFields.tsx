import { css } from '@emotion/react';
import styled from '@emotion/styled';

import Button from '../../../../common/components/Button/Button';
import FormField from '../../../../common/components/FormField/FormField';
import Input from '../../../../common/components/Input/Input';

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
  isVerificationButtonEnabled: boolean;
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
  isVerificationButtonEnabled,
}: PhoneFieldsProps) {
  const isVerificationRequestButtonEnabled =
    phoneNumber !== '' && phoneNumberErrorMessage === '';
  return (
    <>
      <FormField label="전화번호 *" errorMessage={phoneNumberErrorMessage}>
        <StyledInputAndBtnWrapper>
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
            variant={
              isVerificationRequestButtonEnabled ? 'primary' : 'disabled'
            }
          >
            인증요청
          </Button>
        </StyledInputAndBtnWrapper>
      </FormField>
      <FormField
        label="인증번호 확인 *"
        errorMessage={verificationCodeErrorMessage}
      >
        <StyledInputAndBtnWrapper>
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
            variant={isVerificationButtonEnabled ? 'primary' : 'disabled'}
          >
            인증하기
          </Button>
        </StyledInputAndBtnWrapper>
      </FormField>
    </>
  );
}

export default PhoneFields;

const StyledInputAndBtnWrapper = styled.div`
  display: flex;
  gap: 1.4rem;

  & > .input-wrapper {
    flex-grow: 1;
  }
`;

const buttonCustomStyle = css`
  height: 4rem;
  padding: 1.1rem 0.8rem;

  font-size: 1.4rem;
`;
