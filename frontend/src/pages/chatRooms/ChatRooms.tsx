import styled from '@emotion/styled';

import ChatRoomList from './components/ChatRoomList/ChatRoomList';
import ChatRoomsHeader from './components/ChatRoomsHeader/ChatRoomsHeader';

export interface ChatRoomListItemType {
  chatRoomId: string;
  name: string;
  lastMessage: string;
  timeText: string;
  imageUrl?: string | null;
}

function ChatRooms() {
  const DUMMY: ChatRoomListItemType[] = [
    {
      chatRoomId: '1',
      name: '김멘토',
      lastMessage: '강남역 근처 스타벅스 어떤가요?',
      timeText: '2025-09-27T14:35:03',
    },
    {
      chatRoomId: '2',
      name: '김멘토',
      lastMessage: '강남역 근처 스타벅스 어떤가요?',
      timeText: '2025-09-27T14:35:03',
      imageUrl:
        'https://play-lh.googleusercontent.com/38AGKCqmbjZ9OuWx4YjssAz3Y0DTWbiM5HB0ove1pNBq_o9mtWfGszjZNxZdwt_vgHo',
    },
    {
      chatRoomId: '3',
      name: '김멘토',
      lastMessage: '강남역 근처 스타벅스 어떤가요?',
      timeText: '2025-09-27T14:35:03',
      imageUrl:
        'https://blog.kakaocdn.net/dna/bfZZQd/btrua3HciZ9/AAAAAAAAAAAAAAAAAAAAAFQMJkBr5W9Jsfmwj46M-CKXp7KDfMoYD6Bb7uc29T6x/%EC%B9%B4%ED%86%A1%20%EA%B8%B0%EB%B3%B8%ED%94%84%EB%A1%9C%ED%95%84%20%EC%82%AC%EC%A7%84(%EC%97%B0%EC%B4%88%EB%A1%9Dver).jpg?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1769871599&allow_ip=&allow_referer=&signature=UrugpHNkRMbhhlpX0UBO%2BqLMbMI%3D&attach=1&knm=img.jpg',
    },
    {
      chatRoomId: '4',
      name: '김멘토',
      lastMessage:
        '강남역 근처 스타벅스 어떤가요?강남역 근처 스타벅스 어떤가요?강남역 근처 스타벅스 어떤가요?',
      timeText: '2025-09-27T14:35:03',
    },
    {
      chatRoomId: '1',
      name: '김멘토',
      lastMessage: '강남역 근처 스타벅스 어떤가요?',
      timeText: '2025-09-27T14:35:03',
    },
    {
      chatRoomId: '2',
      name: '김멘토',
      lastMessage: '강남역 근처 스타벅스 어떤가요?',
      timeText: '2025-09-27T14:35:03',
      imageUrl:
        'https://play-lh.googleusercontent.com/38AGKCqmbjZ9OuWx4YjssAz3Y0DTWbiM5HB0ove1pNBq_o9mtWfGszjZNxZdwt_vgHo',
    },
    {
      chatRoomId: '3',
      name: '김멘토',
      lastMessage: '강남역 근처 스타벅스 어떤가요?',
      timeText: '2025-09-27T14:35:03',
      imageUrl:
        'https://blog.kakaocdn.net/dna/bfZZQd/btrua3HciZ9/AAAAAAAAAAAAAAAAAAAAAFQMJkBr5W9Jsfmwj46M-CKXp7KDfMoYD6Bb7uc29T6x/%EC%B9%B4%ED%86%A1%20%EA%B8%B0%EB%B3%B8%ED%94%84%EB%A1%9C%ED%95%84%20%EC%82%AC%EC%A7%84(%EC%97%B0%EC%B4%88%EB%A1%9Dver).jpg?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1769871599&allow_ip=&allow_referer=&signature=UrugpHNkRMbhhlpX0UBO%2BqLMbMI%3D&attach=1&knm=img.jpg',
    },
    {
      chatRoomId: '4',
      name: '김멘토',
      lastMessage:
        '강남역 근처 스타벅스 어떤가요?강남역 근처 스타벅스 어떤가요?강남역 근처 스타벅스 어떤가요?',
      timeText: '2025-09-27T14:35:03',
    },
    {
      chatRoomId: '1',
      name: '김멘토',
      lastMessage: '강남역 근처 스타벅스 어떤가요?',
      timeText: '2025-09-27T14:35:03',
    },
    {
      chatRoomId: '2',
      name: '김멘토',
      lastMessage: '강남역 근처 스타벅스 어떤가요?',
      timeText: '2025-09-27T14:35:03',
      imageUrl:
        'https://play-lh.googleusercontent.com/38AGKCqmbjZ9OuWx4YjssAz3Y0DTWbiM5HB0ove1pNBq_o9mtWfGszjZNxZdwt_vgHo',
    },
    {
      chatRoomId: '3',
      name: '김멘토',
      lastMessage: '강남역 근처 스타벅스 어떤가요?',
      timeText: '2025-09-27T14:35:03',
      imageUrl:
        'https://blog.kakaocdn.net/dna/bfZZQd/btrua3HciZ9/AAAAAAAAAAAAAAAAAAAAAFQMJkBr5W9Jsfmwj46M-CKXp7KDfMoYD6Bb7uc29T6x/%EC%B9%B4%ED%86%A1%20%EA%B8%B0%EB%B3%B8%ED%94%84%EB%A1%9C%ED%95%84%20%EC%82%AC%EC%A7%84(%EC%97%B0%EC%B4%88%EB%A1%9Dver).jpg?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1769871599&allow_ip=&allow_referer=&signature=UrugpHNkRMbhhlpX0UBO%2BqLMbMI%3D&attach=1&knm=img.jpg',
    },
    {
      chatRoomId: '4',
      name: '김멘토',
      lastMessage:
        '강남역 근처 스타벅스 어떤가요?강남역 근처 스타벅스 어떤가요?강남역 근처 스타벅스 어떤가요?',
      timeText: '2025-09-27T14:35:03',
    },
  ];

  return (
    <S_Container>
      <ChatRoomsHeader />
      <S_ChatRoomListSection>
        <ChatRoomList chatList={DUMMY} onChatListClick={() => {}} />
      </S_ChatRoomListSection>
    </S_Container>
  );
}

export default ChatRooms;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;

  height: 100svh;
`;

const S_ChatRoomListSection = styled.div`
  overflow-y: auto;

  flex-grow: 1;

  height: calc(100% - 5.7rem);
`;
