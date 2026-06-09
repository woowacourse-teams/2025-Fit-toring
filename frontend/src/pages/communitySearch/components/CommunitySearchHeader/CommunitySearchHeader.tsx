import {
  useEffect,
  useRef,
  useState,
  type ChangeEvent,
  type FormEvent,
} from 'react';

import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import backIcon from '../../../../common/assets/images/backIcon.svg';
import searchIcon from '../../../../common/assets/images/searchIcon.svg';
import Header from '../../../../common/components/Header/Header';
import { PAGE_URL } from '../../../../common/constants/url';

interface CommunitySearchHeaderProps {
  defaultKeyword?: string;
  autoFocus?: boolean;
}

function CommunitySearchHeader({
  defaultKeyword = '',
  autoFocus = false,
}: CommunitySearchHeaderProps) {
  const [keyword, setKeyword] = useState(defaultKeyword);
  const inputRef = useRef<HTMLInputElement>(null);
  const navigate = useNavigate();

  useEffect(() => {
    setKeyword(defaultKeyword);
  }, [defaultKeyword]);

  useEffect(() => {
    if (autoFocus) {
      inputRef.current?.focus();
    }
  }, [autoFocus]);

  const handleBackButtonClick = () => {
    navigate(-1);
  };

  const handleKeywordChange = (event: ChangeEvent<HTMLInputElement>) => {
    setKeyword(event.target.value);
  };

  const handleSearchSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const trimmedKeyword = keyword.trim();

    if (!trimmedKeyword) {
      return;
    }

    navigate(
      `${PAGE_URL.COMMUNITY_SEARCH_RESULT}?keyword=${encodeURIComponent(
        trimmedKeyword,
      )}`,
    );
  };

  return (
    <Header>
      <S_Wrapper>
        <S_BackButton type="button" onClick={handleBackButtonClick}>
          <S_BackIcon src={backIcon} alt="뒤로가기 아이콘" />
        </S_BackButton>
        <S_Form role="search" onSubmit={handleSearchSubmit}>
          <S_InputWrapper>
            <S_SearchIcon src={searchIcon} alt="" aria-hidden="true" />
            <S_Input
              ref={inputRef}
              type="search"
              inputMode="search"
              enterKeyHint="search"
              aria-label="커뮤니티 게시글 검색어"
              placeholder="글 제목, 내용, 해시태그"
              value={keyword}
              onChange={handleKeywordChange}
            />
          </S_InputWrapper>
        </S_Form>
      </S_Wrapper>
    </Header>
  );
}

export default CommunitySearchHeader;

const S_Wrapper = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;

  height: 100%;
  padding: 1rem 1.6rem 1rem 1.1rem;
`;

const S_BackButton = styled.button`
  display: flex;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;

  width: 3.4rem;
  height: 3.4rem;
  padding: 0;
  border: none;

  background-color: transparent;
  cursor: pointer;
`;

const S_BackIcon = styled.img`
  width: 3.4rem;
  height: 3.4rem;
`;

const S_Form = styled.form`
  flex: 1;

  min-width: 0;
`;

const S_InputWrapper = styled.div`
  display: flex;
  align-items: center;
  gap: 0.8rem;

  width: 100%;
  height: 4.4rem;
  padding: 0 1.4rem;
  border-radius: 2.2rem;

  background-color: ${({ theme }) => theme.SYSTEM.GRAY50};
`;

const S_SearchIcon = styled.img`
  flex-shrink: 0;

  width: 2.2rem;
  height: 2.2rem;

  opacity: 0.45;
`;

const S_Input = styled.input`
  width: 100%;
  height: 100%;
  min-width: 0;
  padding: 0;
  border: none;

  background-color: transparent;

  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R};

  :focus {
    outline: none;
  }

  ::placeholder {
    color: ${({ theme }) => theme.SYSTEM.GRAY400};
  }

  ::-webkit-search-cancel-button {
    appearance: none;
  }
`;
