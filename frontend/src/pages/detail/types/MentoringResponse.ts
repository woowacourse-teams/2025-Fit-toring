export interface MentoringResponse {
  id: number;
  mentorName: string;
  categories: string[];
  price: number;
  career: number;
  imageUrl: string | null;
  introduction: string;
  content: string;
}
