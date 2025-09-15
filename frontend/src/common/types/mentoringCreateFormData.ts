export interface mentoringCreateFormData {
  price: number;
  category: string[];
  introduction: string;
  career: number;
  content: string;
  chatUrl: string;
  certificateInfos: {
    type: string | null;
    title: string | null;
  }[];
}
