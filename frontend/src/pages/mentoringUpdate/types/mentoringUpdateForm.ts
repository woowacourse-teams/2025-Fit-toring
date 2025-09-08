export interface MentoringUpdateFormData {
  price: number;
  category: string[];
  introduction: string;
  career: number;
  content: string;
  profileImageUrl: string | null;
  chatUrl: string;
  certificateInfos: {
    id: string;
    type: string | null;
    title: string | null;
  }[];
}
