export interface ChatRoomInfo {
  mentoringInfoDto: {
    mentorName: string;
    price: number;
    profileImageUrl: string;
  };
  chatRoomInfoDto: {
    mentoringId: number;
    opponentName: string;
    myRole: 'MENTEE' | 'MENTOR';
    status: 'ACTIVATE' | 'DEACTIVATE';
  };
}
