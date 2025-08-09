import { ThemeProvider } from '@emotion/react';
import { fireEvent, render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { describe, expect, it } from 'vitest';

import { PAGE_URL } from '../src/common/constants/url';
import { THEME } from '../src/common/styles/theme';
import SignupForm from '../src/pages/signup/components/SignupForm/SignupForm';

describe('SignupForm 컴포넌트 입력 비활성화 테스트', () => {
  it('모든 입력값이 유효하지 않으면 버튼이 클릭되지 않는다.', () => {
    render(
      <ThemeProvider theme={THEME}>
        <MemoryRouter initialEntries={[PAGE_URL.SIGNUP]}>
          <SignupForm />
        </MemoryRouter>
      </ThemeProvider>,
    );

    const nameInput = screen.getByLabelText('이름 *');
    fireEvent.change(nameInput, { target: { value: '' } });

    const idInput = screen.getByLabelText('아이디 *');
    fireEvent.change(idInput, { target: { value: 'abc@' } });

    const passwordInput = screen.getByLabelText('비밀번호 *');
    fireEvent.change(passwordInput, { target: { value: '123' } });

    const passwordConfirmInput = screen.getByLabelText('비밀번호 확인 *');
    fireEvent.change(passwordConfirmInput, { target: { value: '321' } });

    const phoneInput = screen.getByLabelText('전화번호 *');
    fireEvent.change(phoneInput, { target: { value: '010123' } });

    const verificationCodeInput = screen.getByLabelText('인증번호 확인 *');
    fireEvent.change(verificationCodeInput, { target: { value: '123' } });

    const submitButton = screen.getByRole('button', { name: /회원가입/i });

    expect(submitButton).toHaveStyle('pointer-events: none');
  });

  it('하나의 인풋이라도 입력값이 유효하지 않으면 버튼이 클릭되지 않는다.', () => {
    render(
      <ThemeProvider theme={THEME}>
        <MemoryRouter initialEntries={[PAGE_URL.SIGNUP]}>
          <SignupForm />
        </MemoryRouter>
      </ThemeProvider>,
    );

    const nameInput = screen.getByLabelText('이름 *');
    fireEvent.change(nameInput, { target: { value: '홍길동' } });

    const idInput = screen.getByLabelText('아이디 *');
    fireEvent.change(idInput, { target: { value: 'abc@' } });

    const passwordInput = screen.getByLabelText('비밀번호 *');
    fireEvent.change(passwordInput, { target: { value: '123456' } });

    const passwordConfirmInput = screen.getByLabelText('비밀번호 확인 *');
    fireEvent.change(passwordConfirmInput, { target: { value: '123456' } });

    const phoneInput = screen.getByLabelText('전화번호 *');
    fireEvent.change(phoneInput, { target: { value: '01012345678' } });

    const verificationCodeInput = screen.getByLabelText('인증번호 확인 *');
    fireEvent.change(verificationCodeInput, { target: { value: '123456' } });

    const submitButton = screen.getByRole('button', { name: /회원가입/i });

    expect(submitButton).toHaveStyle('pointer-events: none');
  });

  it('모든 입력값이 유효하면 버튼이 클릭된다.', () => {
    render(
      <ThemeProvider theme={THEME}>
        <MemoryRouter initialEntries={[PAGE_URL.SIGNUP]}>
          <SignupForm />
        </MemoryRouter>
      </ThemeProvider>,
    );

    const nameInput = screen.getByLabelText('이름 *');
    fireEvent.change(nameInput, { target: { value: '홍길동' } });

    const idInput = screen.getByLabelText('아이디 *');
    fireEvent.change(idInput, { target: { value: 'abc1234' } });

    const passwordInput = screen.getByLabelText('비밀번호 *');
    fireEvent.change(passwordInput, { target: { value: '12345' } });

    const passwordConfirmInput = screen.getByLabelText('비밀번호 확인 *');
    fireEvent.change(passwordConfirmInput, { target: { value: '12345' } });

    const phoneInput = screen.getByLabelText('전화번호 *');
    fireEvent.change(phoneInput, { target: { value: '01012345678' } });

    const verificationCodeInput = screen.getByLabelText('인증번호 확인 *');
    fireEvent.change(verificationCodeInput, { target: { value: '123456' } });

    const submitButton = screen.getByRole('button', { name: /회원가입/i });

    expect(submitButton).not.toHaveStyle('pointer-events: none');
  });
});
