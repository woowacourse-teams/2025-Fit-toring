import styled from '@emotion/styled';
import FormField from '../FormField/FormField';
import Input from '../Input/Input';

interface UserInfoFields {
  name: string;
  nameErrorMessage: string;
  onNameChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  gender: string;
  onGenderChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
}

function UserInfoFields({
  name,
  nameErrorMessage,
  onNameChange,
  gender,
  onGenderChange,
}: UserInfoFields) {
  return (
    <S_Container>
      <FormField label="이름 *" errorMessage={nameErrorMessage}>
        <S_NameInputWrapper>
          <Input
            id="name"
            name="name"
            placeholder="홍길동"
            value={name}
            onChange={onNameChange}
            errored={nameErrorMessage !== ''}
          />
        </S_NameInputWrapper>
      </FormField>
      <fieldset>
        <S_Legend>성별 *</S_Legend>
        <S_Radios>
          <S_RadioWrapper>
            <S_Label>
              남
              <S_Radio
                onChange={onGenderChange}
                type="radio"
                name="gender"
                value="남"
                id="male"
                checked={gender === '남'}
              />
            </S_Label>
          </S_RadioWrapper>
          <S_RadioWrapper>
            <S_Label>
              여
              <S_Radio
                onChange={onGenderChange}
                type="radio"
                name="gender"
                value="여"
                id="female"
                checked={gender === '여'}
              />
            </S_Label>
          </S_RadioWrapper>
        </S_Radios>
      </fieldset>
    </S_Container>
  );
}

export default UserInfoFields;

const S_Container = styled.div`
  grid-template-columns: 1fr 1fr;

  display: grid;
  gap: 2rem;
`;

const S_NameInputWrapper = styled.div`
  height: 4rem;
`;

const S_Legend = styled.legend`
  color: ${({ theme }) => theme.FONT.B02};

  ${({ theme }) => theme.TYPOGRAPHY.B4_R};
`;

const S_RadioWrapper = styled.div`
  display: flex;
  align-items: center;
  gap: 0.5rem;
`;

const S_Radios = styled.div`
  display: flex;
  gap: 3rem;

  height: 4rem;
  margin-top: 0.7rem;
`;

const S_Radio = styled.input`
  flex-shrink: 0;

  width: 1.4rem;
  height: 1.4rem;
  margin: 0;
  outline: none;
  border: 1px solid #ccc;
  border-radius: 50%;
  appearance: none;
  cursor: pointer;

  &:checked {
    border: 3px solid #fff;
    box-shadow: 0 0 0 1px ${({ theme }) => theme.SYSTEM.MAIN600};

    background-color: ${({ theme }) => theme.SYSTEM.MAIN600};
  }
`;

const S_Label = styled.label`
  display: flex;
  align-items: center;
  gap: 1rem;

  color: ${({ theme }) => theme.FONT.B02};

  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
  cursor: pointer;
`;
