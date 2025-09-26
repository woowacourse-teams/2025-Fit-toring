import type { MentorInformation } from './MentorInformation';

export interface MentoringByPage {
  mentoringSummaryResponses: MentorInformation[];
  nextCursorCode: string;
  hasNext: boolean;
}
