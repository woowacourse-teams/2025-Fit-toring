import styled from '@emotion/styled';

const Emotion = styled.p`
  display: inline-block;
  font-size: 30px;
  padding: 10px;
  color: #a0cc39;
  background-color: #413939;
`;

function App() {
  return (
    <>
      <h1>Hello Fit-toring!</h1>
      <Emotion>스타일이 적용된 리액트 컴포넌트</Emotion>
    </>
  );
}

export default App;
