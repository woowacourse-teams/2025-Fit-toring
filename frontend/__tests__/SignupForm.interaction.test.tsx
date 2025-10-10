import { ThemeProvider } from '@emotion/react';
import { screen, fireEvent, render } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { describe, expect, it, vi } from 'vitest';

import { THEME } from '../src/common/styles/theme';
import SignupForm from '../src/pages/signup/components/SignupForm/SignupForm';

const fillSignUpFormExceptPhone = async () => {
  const nameInput = screen.getByLabelText('이름 *');
  await userEvent.type(nameInput, '홍길동');

  const idInput = screen.getByLabelText('아이디 *');
  await userEvent.type(idInput, 'abc1234');

  await userEvent.click(screen.getByRole('button', { name: /중복확인/i }));

  const passwordInput = screen.getByLabelText('비밀번호 *');
  await userEvent.type(passwordInput, '12345');

  const passwordConfirmInput = screen.getByLabelText('비밀번호 확인 *');
  await userEvent.type(passwordConfirmInput, '12345');
};

const renderSignupForm = () => {
  render(
    <ThemeProvider theme={THEME}>
      <MemoryRouter>
        <SignupForm />
      </MemoryRouter>
    </ThemeProvider>,
  );
};

describe('SignUpForm', () => {
  describe('아이디 중복 검증', () => {
    it('사용가능한 아이디 입력시 "사용 가능한 아이디입니다" 라는 문구가 출력된다.', async () => {
      // given
      renderSignupForm();

      const idInput = screen.getByLabelText(/아이디/i);

      // when
      await userEvent.clear(idInput);
      await userEvent.type(idInput, 'test123');
      await userEvent.click(screen.getByRole('button', { name: /중복확인/i }));

      // then
      const successMessage =
        await screen.findByText(/사용 가능한 아이디입니다/i);
      expect(successMessage).toBeInTheDocument();
    });

    it('존재하는 아이디 입력시 "이미 사용중인 아이디입니다" 라는 문구가 나타나며 빨간테두리가 나타난다.', async () => {
      // given
      renderSignupForm();

      const idInput = screen.getByLabelText(/아이디/i);

      // when
      await userEvent.clear(idInput);
      await userEvent.type(idInput, 'test1234');
      await userEvent.click(screen.getByRole('button', { name: /중복확인/i }));

      // then
      const errorMessage =
        await screen.findByText(/이미 사용중인 아이디입니다/i);
      expect(errorMessage).toBeInTheDocument();
    });

    it('중복확인을 하지 않은 경우 회원가입 버튼 클릭시 에러 메시지 및 빨간 테두리가 뜬다', async () => {
      // given
      renderSignupForm();

      const nameInput = screen.getByLabelText('이름 *');
      await userEvent.type(nameInput, '홍길동');

      const idInput = screen.getByLabelText('아이디 *');
      await userEvent.type(idInput, 'abc1234');

      const passwordInput = screen.getByLabelText('비밀번호 *');
      await userEvent.type(passwordInput, '12345');

      const passwordConfirmInput = screen.getByLabelText('비밀번호 확인 *');
      await userEvent.type(passwordConfirmInput, '12345');

      const phoneInput = screen.getByLabelText('전화번호 *');
      await userEvent.type(phoneInput, '01012345678');

      const verificationCodeInput = screen.getByLabelText('인증번호 확인 *');
      await userEvent.type(verificationCodeInput, '123456');

      // when
      await userEvent.click(screen.getByRole('button', { name: /회원가입/i }));

      // then
      const errorMessage = await screen.findByText(/중복확인을 해주세요/i);
      expect(errorMessage).toBeInTheDocument();
    });
  });

  describe('비밀번호 확인 검증', async () => {
    it('비밀번호가 일치할때 에러가 나지 않는다.', async () => {
      // given
      renderSignupForm();

      const passwordInput = screen.getByLabelText('비밀번호 *');
      const passwordConfirmInput = screen.getByLabelText(/비밀번호 확인/i);
      const password = '12345';

      // when
      await userEvent.type(passwordInput, password);
      await userEvent.type(passwordConfirmInput, password);

      // then
      const errorMessage =
        await screen.queryByText(/비밀번호가 일치하지 않습니다/i);
      expect(errorMessage).not.toBeInTheDocument();
    });

    it('비밀번호가 일치하지 않으면 에러메시지가 출력된다.', async () => {
      // given
      renderSignupForm();

      const passwordInput = screen.getByLabelText('비밀번호 *');
      const passwordConfirmInput = screen.getByLabelText(/비밀번호 확인/i);

      // when
      await userEvent.type(passwordInput, '12345');
      await userEvent.type(passwordConfirmInput, '123455');

      // then
      const errorMessage =
        await screen.findByText(/비밀번호가 일치하지 않습니다/i);
      expect(errorMessage).toBeInTheDocument();
    });
  });

  describe('인증번호 입력', () => {
    it('인증번호 입력 검증을 통과하면 인증하기 버튼이 활성화된다.', async () => {
      // given
      renderSignupForm();

      const codeInput = screen.getByLabelText(/인증번호/i);
      const phoneNumberInput = screen.getByLabelText(/전화번호/i);

      // when
      await userEvent.type(phoneNumberInput, '123-4567-8901');
      fireEvent.click(screen.getByRole('button', { name: /인증요청/i }));
      await userEvent.type(codeInput, '123456');

      // then
      const button = screen.getByRole('button', { name: /인증하기/i });
      expect(button).toHaveStyle(`background-color: ${THEME.SYSTEM.MAIN600}`);
    });

    it('인증번호 입력을 6자 미만으로 하면 인증하기 버튼이 비활성화된다.', async () => {
      // given
      renderSignupForm();
      const codeInput = screen.getByLabelText(/인증번호/i);

      // when
      await userEvent.type(codeInput, '12345');

      // then
      const button = screen.getByRole('button', { name: /인증하기/i });
      expect(button).toHaveStyle(`background-color: ${THEME.BG.GRAY}`);
    });
  });

  describe('전화번호 인증요청 및 인증번호 확인 검증', () => {
    it('전화번호를 입력 및 인증요청 후 틀린 인증번호로 인증하기 버튼 클릭시 "인증 실패" 에러메시지 나타난다.', async () => {
      // given
      renderSignupForm();
      await fillSignUpFormExceptPhone();

      const phoneNumberInput = screen.getByLabelText(/전화번호/i);
      const confirmCodeInput = screen.getByLabelText(/인증번호 확인/i);

      // when
      await userEvent.type(phoneNumberInput, '123-4567-8901');
      fireEvent.click(screen.getByRole('button', { name: /인증요청/i }));

      await userEvent.type(confirmCodeInput, '123457');
      fireEvent.click(screen.getByRole('button', { name: /인증하기/i }));

      // then
      const errorMessage = await screen.findByText(/인증 실패/i);
      expect(errorMessage).toBeInTheDocument();
    });

    it('인증하기 완료후 다시 전화번호를 변경한후 회원가입 버튼을 누르면 "인증요청을 해주세요" 라는 에러메시지와 함께 빨간 테두리 나타면서 인증하기 버튼 비활성화', async () => {
      // given
      renderSignupForm();
      await fillSignUpFormExceptPhone();

      const phoneNumberInput = screen.getByLabelText(/전화번호/i);
      const confirmCodeInput = screen.getByLabelText(/인증번호 확인/i);

      // then
      await userEvent.type(phoneNumberInput, '123-4567-8901');
      fireEvent.click(screen.getByRole('button', { name: /인증요청/i }));

      await userEvent.type(confirmCodeInput, '123456');
      fireEvent.click(screen.getByRole('button', { name: /인증하기/i }));

      // 전화번호 재입력
      await userEvent.clear(phoneNumberInput);
      await userEvent.type(phoneNumberInput, '123-4567-8900');
      const button = await screen.findByRole('button', { name: /회원가입/i });
      await userEvent.click(button);

      // then
      const errorMessage = await screen.findByText(/인증요청을 해주세요/i);
      expect(errorMessage).toBeInTheDocument();
    });

    it('인증하기 완료후 다시 전화번호를 변경한후 인증요청 후 틀린 인증번호로 인증하기 버튼 클릭시 "인증 실패" 에러메시지 나타난다.', async () => {
      // given
      renderSignupForm();
      await fillSignUpFormExceptPhone();

      const phoneNumberInput = screen.getByLabelText(/전화번호/i);
      const confirmCodeInput = screen.getByLabelText(/인증번호 확인/i);

      // when
      await userEvent.type(phoneNumberInput, '123-4567-8901');
      await userEvent.click(screen.getByRole('button', { name: /인증요청/i }));
      await userEvent.type(confirmCodeInput, '123456');
      await userEvent.click(screen.getByRole('button', { name: /인증하기/i }));

      // 전화번호 재입력
      await userEvent.clear(phoneNumberInput);
      await userEvent.type(phoneNumberInput, '123-4567-8900');

      // 인증요청
      await userEvent.click(screen.getByRole('button', { name: /인증요청/i }));

      // 인증번호 입력
      await userEvent.type(confirmCodeInput, '123457');
      fireEvent.click(screen.getByRole('button', { name: /인증하기/i }));

      // then
      const errorMessage = await screen.findByText(/인증 실패/i);
      expect(errorMessage).toBeInTheDocument();
    });

    // TODO: 현재는 회원가입 버튼 누르면 걍 아무것도 안뜨는 문제 발생. 버그 수정 필요
    it('인증하기 완료후 다시 전화번호를 변경한후 인증요청을 하면 회원가입 버튼 및 인증하기 버튼이 비활성화 된다.', async () => {
      // given
      renderSignupForm();
      await fillSignUpFormExceptPhone();

      const phoneNumberInput = screen.getByLabelText(/전화번호/i);
      const confirmCodeInput = screen.getByLabelText(/인증번호 확인/i);

      // when
      await userEvent.type(phoneNumberInput, '123-4567-8901');
      await userEvent.click(screen.getByRole('button', { name: /인증요청/i }));
      await userEvent.type(confirmCodeInput, '123456');
      await userEvent.click(screen.getByRole('button', { name: /인증하기/i }));

      await userEvent.clear(phoneNumberInput);
      await userEvent.type(phoneNumberInput, '123-4567-8900');

      await userEvent.click(screen.getByRole('button', { name: /인증요청/i }));

      // then
      const submitButton = await screen.findByRole('button', {
        name: /회원가입/i,
      });
      const verificationButton = await screen.findByRole('button', {
        name: /인증하기/i,
      });

      expect(submitButton).toHaveStyle(`background-color: ${THEME.BG.GRAY}`);
      expect(verificationButton).toHaveStyle(
        `background-color: ${THEME.BG.GRAY}`,
      );
    });

    it('인증하기 완료후 다시 전화번호를 변경한후 인증요청 후 올바른 인증번호 입력하고 인증하기 누르면 회원가입이 성공한다.', async () => {
      // given
      renderSignupForm();
      const alertMock = vi.spyOn(window, 'alert').mockImplementation(() => {});
      await fillSignUpFormExceptPhone();

      const phoneNumberInput = screen.getByLabelText(/전화번호/i);
      const confirmCodeInput = screen.getByLabelText(/인증번호 확인/i);

      // when
      await userEvent.type(phoneNumberInput, '123-4567-8901');
      await userEvent.click(screen.getByRole('button', { name: /인증요청/i }));
      await userEvent.type(confirmCodeInput, '123456');
      await userEvent.click(screen.getByRole('button', { name: /인증하기/i }));

      await userEvent.clear(phoneNumberInput);
      await userEvent.type(phoneNumberInput, '123-4567-8900');

      await userEvent.click(screen.getByRole('button', { name: /인증요청/i }));
      await userEvent.click(screen.getByRole('button', { name: /인증하기/i }));

      const submitButton = await screen.findByRole('button', {
        name: /회원가입/i,
      });
      await userEvent.click(submitButton);

      // then
      expect(submitButton).toHaveStyle(
        `background-color: ${THEME.SYSTEM.MAIN600}`,
      );
      expect(alertMock).toHaveBeenCalledWith('가입에 성공했습니다.');
    });

    it('인증하기 완료후 다시 전화번호를 변경한후 인증요청 후 잘못된 인증번호 입력하면 에러 메시지가 뜬다.', async () => {
      // given
      renderSignupForm();
      await fillSignUpFormExceptPhone();

      const phoneNumberInput = screen.getByLabelText(/전화번호/i);
      const confirmCodeInput = screen.getByLabelText(/인증번호 확인/i);

      // when
      await userEvent.type(phoneNumberInput, '123-4567-8901');
      await userEvent.click(screen.getByRole('button', { name: /인증요청/i }));

      await userEvent.type(confirmCodeInput, '123456');
      await userEvent.click(screen.getByRole('button', { name: /인증하기/i }));

      await userEvent.clear(phoneNumberInput);
      await userEvent.type(phoneNumberInput, '123-4567-8900');
      await userEvent.click(screen.getByRole('button', { name: /인증요청/i }));

      await userEvent.type(confirmCodeInput, '123458');
      await userEvent.click(screen.getByRole('button', { name: /인증하기/i }));

      // then
      const errorMessage = await screen.findByText(/인증 실패/i);
      expect(errorMessage).toBeInTheDocument();

      const submitButton = await screen.findByRole('button', {
        name: /회원가입/i,
      });
      expect(submitButton).toHaveStyle(`background-color: ${THEME.BG.GRAY}`);
    });
  });
});

describe('SignupForm 컴포넌트 입력 비활성화 테스트', () => {
  it('모든 입력값이 유효하지 않으면 버튼이 클릭되지 않는다.', () => {
    // given
    renderSignupForm();

    // when
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

    // then
    const submitButton = screen.getByRole('button', { name: /회원가입/i });

    expect(submitButton).toHaveStyle('pointer-events: none');
  });

  it('하나의 인풋이라도 입력값이 유효하지 않으면 버튼이 클릭되지 않는다.', () => {
    // given
    renderSignupForm();

    // when
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

    // then
    const submitButton = screen.getByRole('button', { name: /회원가입/i });

    expect(submitButton).toHaveStyle('pointer-events: none');
  });

  it('모든 입력값이 유효하면 버튼이 클릭된다.', () => {
    // given
    renderSignupForm();

    // when
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

    // then
    const submitButton = screen.getByRole('button', { name: /회원가입/i });

    expect(submitButton).not.toHaveStyle('pointer-events: none');
  });
});
